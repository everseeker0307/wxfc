package com.everseeker.spider;

import com.everseeker.entity.HouseInfo;
import com.everseeker.entity.HouseStock;
import com.everseeker.mq.MessageProducer;
import com.everseeker.service.HouseInfoService;
import com.everseeker.service.HouseStockService;
import com.everseeker.utils.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by everseeker on 2017/3/2.
 */
@Component
@EnableScheduling
public class Spider {
    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private HouseInfoService houseInfoService;

    @Autowired
    private HouseStockService houseStockService;

    private static final Logger logger = LoggerFactory.getLogger(Spider.class);

    private static String indexPage = "http://www.wxhouse.com:9097/wwzs/getzxlpxx.action";
    private AtomicInteger currentPageNo = new AtomicInteger(1);
    private int totalPageCount = 0;          //总页数
    private static final int pageSize = 15;  //每页显示数据
    private static final int spiderThreadNum = 3;

    @Scheduled(cron = "23 12 01,22 * * *")
    public void startSpider() {
        long start = System.currentTimeMillis();
        logger.info("spider start...");
        //获得总页数
        totalPageCount = getTotalPageNum(indexPage);
        //初始化当前页面为1
        currentPageNo.set(1);

        //设置爬取页面数据的线程池
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(spiderThreadNum);
        //设置countDownLatch，主要用于判断线程池中的线程是否都已执行完毕
        final CountDownLatch countDownLatch = new CountDownLatch(spiderThreadNum);
        for (int i = 0; i < spiderThreadNum; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    int cpn;
                    while ((cpn = currentPageNo.getAndIncrement()) <= totalPageCount) {
                        postPage(indexPage, cpn);
                        TimeUtil.sleep(500);
                    }
                    countDownLatch.countDown();
                }
            });
        }
        //等待线程池中所有线程都执行完毕后，关闭所有线程
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            threadPoolExecutor.shutdown();
        }
        TimeUtil.sleep(10000);
        //检查是否有遗漏
        checkSpiderResult(TimeUtil.getBeijingDate(new Date()));
        logger.info("spider end! It costs time: " + (System.currentTimeMillis() - start)/ 1000 + "s");
    }

    @Scheduled(cron = "04 20 00 * * *")
    public void findMissedHouseStock() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        String yesterdayStr = TimeUtil.getBeijingDate(yesterday.getTime());
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        String beforeYesterdayStr = TimeUtil.getBeijingDate(yesterday.getTime());

        int houseStockNumYes = houseStockService.getRecordNumByDate(yesterdayStr);
        logger.info(yesterdayStr + " spider num: " + houseStockNumYes);
        int houseStockNumBeforeYes = houseStockService.getRecordNumByDate(beforeYesterdayStr);
        logger.info(beforeYesterdayStr + " spider num: " + houseStockNumBeforeYes);
        if (houseStockNumYes < houseStockNumBeforeYes) {
            //说明数据有遗漏
            List<HouseStock> missedHouseStocks = houseStockService.getMissedHouseStockInDate(beforeYesterdayStr, yesterdayStr);
            logger.warn("missed num: " + missedHouseStocks.size());
            houseStockService.addMissedHouseStock(missedHouseStocks, yesterdayStr);
        }
    }

    @Scheduled(cron = "0 45 01 * * *")
    public void checkHouseTypeNum() {
        List<HouseInfo> allHouseInfo = houseInfoService.getAllHouseInfo();
        if (allHouseInfo != null) {
            allHouseInfo.forEach(this::checkSingleHouseTypeNum);
        }
    }

    /**
     * 检查是否需要更新houseinfo中关于houseNum等字段的信息
     * @param houseInfo
     */
    public void checkSingleHouseTypeNum(HouseInfo houseInfo) {
        if (houseInfo != null && houseInfo.getHouseNum() == 0 && houseInfo.getBusiNum() == 0 &&
                houseInfo.getOfficeNum() == 0 && houseInfo.getCarportNum() == 0 &&
                houseInfo.getPlantNum() == 0 && houseInfo.getOtherNum() == 0 &&
                houseInfo.getApartNum() == 0) {
            String urlId = houseInfo.getHouseUrlId();
            messageProducer.send("http://www.wxhouse.com:9097/wwzs/queryFwmxInfo.action?tplLpxx.id=" + urlId);
        }
    }

    public void updateSingleHouseTypeNum(String url) {
        List<Integer> list = getHousetypeNums(url);
        String urlId = url.substring(url.indexOf("=") + 1);
        HouseInfo houseInfo = houseInfoService.getHouseInfoByHouseUrlId(urlId);
        if (list != null) {
            houseInfo.setHouseNum(list.get(0));
            houseInfo.setBusiNum(list.get(1));
            houseInfo.setOfficeNum(list.get(2));
            houseInfo.setCarportNum(list.get(3));
            houseInfo.setPlantNum(list.get(4));
            houseInfo.setOtherNum(list.get(5));
            houseInfo.setApartNum(list.get(6));
            houseInfo.setLowHouseNum(list.get(7));
            houseInfo.setMultiHouseNum(list.get(8));
            houseInfo.setSmallhighHouseNum(list.get(9));
            houseInfo.setHighHouseNum(list.get(10));
            houseInfo.setVillaNum(list.get(11));
            houseInfoService.updateHouseInfoTypeNums(houseInfo);
        }
    }

    /**
     * 获得总楼盘数
     * @param url
     * @return
     */
    public int getTotalPageNum(String url) {
        int reconnection = 0;
        while (true) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                        .timeout(30000)
                        .get();
                Element pagination = doc.getElementById("pagination").parent();
                Elements inputs = pagination.getElementsByTag("input");
                for (Element input : inputs) {
                    if (input.attr("id").equals("totalPageCount"))
                        return Integer.valueOf(input.attr("value"));
                }
            } catch (SocketTimeoutException socketTimeoutException) {
                logger.warn(socketTimeoutException.toString() + ".    Invoke method getTotalPageNum error, url is: " + url + ", recon = " + (++reconnection));
                TimeUtil.sleep((reconnection < 100) ? 6000 : 600000);
            } catch (IOException e) {
                logger.error(e.toString() + ",  url=" + url);
                TimeUtil.sleep(600000);
            }
        }
    }

    /**
     * 根据页面发送post请求，获得每页的15个楼盘链接
     * @param url
     * @param currentPage
     */
    public void postPage(String url, int currentPage) {
        int reconnection = 0;
        while (true) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                        .data("page.pageSize", String.valueOf(pageSize))
                        .data("page.currentPageNo", String.valueOf(currentPage))
                        .timeout(30000)
                        .post();
                analysisDocumentGainLinks(doc);
                return;
            } catch (SocketTimeoutException socketTimeoutException) {
                logger.warn(socketTimeoutException.toString() + ".    invoke method postPage error, url is: " + url + ", currentPage is: " + currentPage + ", recon = " + (++reconnection));
                if (reconnection < 5)
                    TimeUtil.sleep(6000);
                else
                    return;
            } catch (IOException e) {
                logger.error(e.toString() + ",  page=" + currentPage);
                TimeUtil.sleep(60000);
            }
        }
    }

    /**
     * 分析每个具体的楼盘页面，提取出有用信息并加入数据库
     * @param url
     */
    public void getDetailPageAndSave(String url) {
        int reconnection = 0;
        while (true) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                        .timeout(30000)
                        .get();
                Elements elements = doc.select("table.searchdiv");
                String urlId = url.substring(url.indexOf("=") + 1);
                HouseInfo houseInfo = houseInfoService.getHouseInfoByHouseUrlId(urlId);
                // 1. 新增楼盘记录
                if (houseInfo == null) {
                    houseInfoService.addHouseInfo(analysisDocumentGainHouseInfo(elements, urlId));
                }
                // 2. 更新住宅性质
//                else
//                    checkSingleHouseTypeNum(houseInfo);
                // 3. 更新每日库存信息
                houseStockService.addHouseStock(analysisDocumentGainHouseStock(elements, urlId));
                return;
            } catch (SocketTimeoutException socketTimeoutException) {
                logger.warn(socketTimeoutException.toString() + ".    invoke method getDetailPageAndSave error, url is: " + url + ", recon = " + (++reconnection));
                if (reconnection < 3)
                    TimeUtil.sleep(3000);
                else
                    return;
            } catch (IOException e) {
                logger.warn(e.toString() + ",  url=" + url);
                TimeUtil.sleep(60000);
            }
        }
    }

    public void analysisDocumentGainLinks(Document doc) {
        Elements links = doc.select("table.Page_Table_Common").last().select("a[href*=tplLpxx.id=");
        for (Element link : links) {
            messageProducer.send("http://www.wxhouse.com:9097" + link.attr("href"));
        }
    }

    /**
     * 提取页面中的楼盘信息
     * @param elements
     * @param urlId
     * @return
     */
    public HouseInfo analysisDocumentGainHouseInfo(Elements elements, String urlId) {
        //提取楼盘信息
        Elements trsHouseInfo = elements.get(1).select("tr");
        HouseInfo houseInfo = new HouseInfo();
        houseInfo.setHouseUrl(urlId);
        houseInfo.setHouseName(getTdText(trsHouseInfo.get(0)));
        houseInfo.setTempName(getTdText(trsHouseInfo.get(1)));
        houseInfo.setPresellLicence(getTdText(trsHouseInfo.get(2)));
        houseInfo.setPresellApproval(getTdText(trsHouseInfo.get(3)));
        houseInfo.setHouseDeveloper(trsHouseInfo.get(4).select("a").first().html().replace("&nbsp;", "").trim());
        houseInfo.setPartner(getTdText(trsHouseInfo.get(5)));
        houseInfo.setLocation(getTdText(trsHouseInfo.get(6)));
        houseInfo.setAdminRegion(getTdText(trsHouseInfo.get(7)));
        houseInfo.setApproval(getTdText(trsHouseInfo.get(8)));
        houseInfo.setPlanLicence(getTdText(trsHouseInfo.get(9)));
        houseInfo.setLandLicence(getTdText(trsHouseInfo.get(10)));
        houseInfo.setConstructLicence(getTdText(trsHouseInfo.get(11)));
        houseInfo.setHoldLandLicence(getTdText(trsHouseInfo.get(12)));
        String presellArea = getTdText(trsHouseInfo.get(13));
        houseInfo.setPresellArea(Double.valueOf(presellArea.substring(0, presellArea.indexOf("平方米"))));
        houseInfo.setSellCompany(getTdText(trsHouseInfo.get(14)));
        houseInfo.setSellTel(getTdText(trsHouseInfo.get(15)));
        houseInfo.setSellAddress(getTdText(trsHouseInfo.get(16)));
        houseInfo.setTelephone(getTdText(trsHouseInfo.get(17)));
        houseInfo.setTenement(getTdText(trsHouseInfo.get(18)));
        houseInfo.setCreateDate(TimeUtil.getBeijingDate(new Date()));
        houseInfo.setTotalHouseNum(Integer.valueOf(getTdText(elements.last().select("tr").get(0)).replace("套", "")));
        return houseInfo;
    }

    /**
     * 提取页面中的库存信息
     * @param elements
     * @param urlId
     * @return
     */
    public HouseStock analysisDocumentGainHouseStock(Elements elements, String urlId) {
        //提取每日库存信息
        HouseStock houseStock = new HouseStock();
        houseStock.setHouseUrlId(urlId);
        Elements trsHouseStock = elements.last().select("tr");
        int total = Integer.valueOf(getTdText(trsHouseStock.get(0)).replace("套", ""));
        houseStock.setTotalHouseNum(total);
        houseStock.setForsaleHouseNum(Integer.valueOf((getTdText(trsHouseStock.get(1))).replace("套", "")));
        houseStock.setSaledHouseNum(Integer.valueOf((getTdText(trsHouseStock.get(2))).replace("套", "")));
        houseStock.setLimitedHouseNum(Integer.valueOf((getTdText(trsHouseStock.get(3))).replace("套", "")));
        houseStock.setRecordDate(TimeUtil.getBeijingDate(new Date()));
        return houseStock;
    }

    /**
     * 返回住宅、商业等具体数量, 具体数组为[houseNum, busiNum, officeNum, carportNum, plantNum, otherNum, apartNum, lowHouseNum, multiHouseNum, smallhighHouseNum, highHouseNum, villaNum]
     * @param url
     * @return
     */
    public List<Integer> getHousetypeNums(String url) {
        String urlId = url.substring(url.indexOf("=") + 1);
        HouseInfo houseInfo = houseInfoService.getHouseInfoByHouseUrlId(urlId);
        if (houseInfo == null)
            return null;
        int totalHouseNum = houseInfo.getTotalHouseNum();
        List<Integer> list = new ArrayList<>();
        int totalType = 0;
        for (int fwyt = 1; fwyt < 8; fwyt++) {
            if (totalType == totalHouseNum) {
                list.add(0);
            } else {
                int nm = getHousetypeNum(url, fwyt);
                if (nm == -1)
                    return null;
                list.add(nm);
                totalType += nm;
            }
        }
        int zztotalType = 0;
        for (int zzfwyt = 101; zzfwyt < 106; zzfwyt++) {
            if (zztotalType == list.get(0)) {
                list.add(0);
            } else {
                int nn = getHousetypeNum(url, zzfwyt);
                if (nn == -1)
                    return null;
                list.add(nn);
                zztotalType += nn;
            }
        }
        return list;
    }

    public int getHousetypeNum(String url, int fwyt) {
        int reconnection = 0;
        while (true) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                        .data("wwzsYsfw.fwyt", String.valueOf(fwyt))
                        .timeout(60000)
                        .post();
                Element element = doc.select("input#totalSize").first();
                return Integer.valueOf(element.attr("value"));
            } catch (SocketTimeoutException socketTimeoutException) {
                logger.warn(socketTimeoutException.toString() + ".    invoke method getHousetypeNum error, url is: " + url + ", fwyt = " + fwyt + ", recon = " + (++reconnection));
                if (reconnection < 3)
                    TimeUtil.sleep(3000);
                else
                    return -1;
            } catch (IOException e) {
                logger.warn(e.toString() + ",  url=" + url);
                TimeUtil.sleep(60000);
            }
        }
    }

    private static String getTdText(Element element) {
        return element.select("td").last().html().replace("&nbsp;", "").trim();
    }

    public void checkSpiderResult(String recordDate) {
        String today = TimeUtil.getBeijingDate(new Date());
        int totalHouseNum = houseInfoService.getTotalHouseNum(today);
        int recordHouseNum = houseStockService.getRecordNumByDate(today);
        if (recordHouseNum < totalHouseNum) {
            List<String> list = houseStockService.getMissedHouseUrlId(recordDate);
            StringBuffer urlIds = new StringBuffer();
            for (String urlId : list) {
                urlIds.append(urlId + ", ");
                messageProducer.send("http://www.wxhouse.com:9097/wwzs/queryLpxxInfo.action?tplLpxx.id=" + urlId);
            }
            logger.info("the missed urls are: " + urlIds.toString() + " already save to table housestock.");
        }
    }
}
