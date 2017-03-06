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
    private int totalPageCount = 0;
    private static final int pageSize = 15;  //每页显示数据

    @Scheduled(cron = "0 06 10 * * *")
    public void startSpider() {
        long start = System.currentTimeMillis();
        logger.info("spider start...");
        getTotalPageNum(indexPage);
        if (totalPageCount > 0) {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
//            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    int cpn;
                    while ((cpn = currentPageNo.getAndIncrement()) <= totalPageCount)
                        postPage(indexPage, cpn);
                }
            });
            while (threadPoolExecutor.getActiveCount() > 0) {
                logger.info("current work thread's num: " + threadPoolExecutor.getActiveCount());
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            threadPoolExecutor.shutdown();
        }
        logger.info("spider end! It costs time: " + (System.currentTimeMillis() - start)/ 1000 + "s");
    }

    public void getTotalPageNum(String url) {
        try {
            Document doc = Jsoup.connect(url)
                                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                                .get();
            Element pagination = doc.getElementById("pagination").parent();
            Elements inputs = pagination.getElementsByTag("input");
            for(Element input : inputs) {
                if (input.attr("id").equals("totalPageCount"))
                    totalPageCount = Integer.valueOf(input.attr("value"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void postPage(String url, int currentPage) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                    .data("page.pageSize", String.valueOf(pageSize))
                    .data("page.currentPageNo", String.valueOf(currentPage))
                    .post();
            analysisDocumentGainLinks(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDetailPageAndSave(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                    .get();
            Elements elements = doc.select("table.searchdiv");
            String urlId = url.substring(url.indexOf("=")+1);
            if (houseInfoService.getHouseInfoByHouseUrlId(urlId) == null) {
                houseInfoService.addHouseInfo(analysisDocumentGainHouseInfo(elements, urlId));
            }
            houseStockService.addHouseStock(analysisDocumentGainHouseStock(elements, urlId));
        } catch (IOException e) {
            e.printStackTrace();
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
