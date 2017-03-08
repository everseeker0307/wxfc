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
import java.util.Date;
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

//    @Scheduled(cron = "0 18 2/10 * * *")
    @Scheduled(cron = "0 48 13 * * *")
    public void startSpider() {
        long start = System.currentTimeMillis();
        logger.info("spider start...");
        //获得总页数
        totalPageCount = getTotalPageNum(indexPage);
        //设置爬取页面数据的线程池
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(spiderThreadNum);
        //设置countDownLatch，主要用于判断线程池中的线程是否都已执行完毕
        final CountDownLatch countDownLatch = new CountDownLatch(spiderThreadNum);
        for (int i = 0; i < spiderThreadNum; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    int cpn;
                    while ((cpn = currentPageNo.get()) <= totalPageCount) {
                        postPage(indexPage, cpn);
                        currentPageNo.incrementAndGet();
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
        logger.info("spider end! It costs time: " + (System.currentTimeMillis() - start)/ 1000 + "s");
    }

    public int getTotalPageNum(String url) {
        int reconnection = 0;
        while (true) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                        .get();
                Element pagination = doc.getElementById("pagination").parent();
                Elements inputs = pagination.getElementsByTag("input");
                for (Element input : inputs) {
                    if (input.attr("id").equals("totalPageCount"))
                        return Integer.valueOf(input.attr("value"));
                }
            } catch (IOException e) {
                reconnection++;
                logger.warn("invoke method getTotalPageNum error, url is: " + url);
                logger.warn(e.toString());
                TimeUtil.sleep((reconnection < 100) ? 6000 : 600000);
            }
        }
    }

    public void postPage(String url, int currentPage) {
        int reconnection = 0;
        while (true) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                        .data("page.pageSize", String.valueOf(pageSize))
                        .data("page.currentPageNo", String.valueOf(currentPage))
                        .post();
                analysisDocumentGainLinks(doc);
                return;
            } catch (IOException e) {
                reconnection++;
                logger.warn("invoke method postPage error, url is: " + url + ", currentPage is: " + currentPage);
                logger.warn(e.toString());
                TimeUtil.sleep((reconnection < 100) ? 6000 : 600000);
            }
        }
    }

    public void getDetailPageAndSave(String url) {
        int reconnection = 0;
        while (true) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                        .get();
                Elements elements = doc.select("table.searchdiv");
                String urlId = url.substring(url.indexOf("=") + 1);
                if (houseInfoService.getHouseInfoByHouseUrlId(urlId) == null) {
                    houseInfoService.addHouseInfo(analysisDocumentGainHouseInfo(elements, urlId));
                }
                houseStockService.addHouseStock(analysisDocumentGainHouseStock(elements, urlId));
                return;
            } catch (IOException e) {
                reconnection++;
                logger.warn("invoke method getDetailPageAndSave error, url is: " + url);
                logger.warn(e.toString());
                TimeUtil.sleep((reconnection < 10) ? 6000 : 60000);
            }
        }
    }

    public void analysisDocumentGainLinks(Document doc) {
        Elements links = doc.select("table.Page_Table_Common").last().select("a[href*=tplLpxx.id=");
        for (Element link : links) {
            messageProducer.send("http://www.wxhouse.com:9097" + link.attr("href"));
        }
    }

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

    private static String getTdText(Element element) {
        return element.select("td").last().html().replace("&nbsp;", "").trim();
    }
}
