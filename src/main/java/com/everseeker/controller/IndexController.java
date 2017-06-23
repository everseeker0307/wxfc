package com.everseeker.controller;

import com.everseeker.service.HouseStockService;
import com.everseeker.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Created by everseeker on 2017/4/4.
 */
@RestController
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private HouseStockService houseStockService;

    @RequestMapping(value = "/getPeriodDetails", method = RequestMethod.POST)
    public List<?> getPeriodDetails(String givenday, int daysInterval, String region) throws Exception {
        logger.info("invoke /getPeriodDetails, givenday=" + givenday + ", daysInterval=" + daysInterval + ", region=" + region);
        return houseStockService.getPeriodDetails(TimeUtil.jsdateTransferTomydate(givenday), daysInterval, region);
    }

    @RequestMapping(value = "/getZhuzhaiPeriodDetails", method = RequestMethod.POST)
    public List<?> getZhuzhaiPeriodDealDetails(String givenday, int daysInterval, String region) throws Exception {
        logger.info("invoke /getZhuzhaiPeriodDetails, givenday=" + givenday + ", daysInterval=" + daysInterval + ", region=" + region);
        return houseStockService.getZhuzhaiPeriodDetails(TimeUtil.jsdateTransferTomydate(givenday), daysInterval, region);
    }

    @RequestMapping(value = "/getIntervalSaledHouseNumSum", method = RequestMethod.POST)
    public List<?> getIntervalSaledHouseNumSum(String startDay, String endDay, int interval, String region) throws Exception {
        logger.info("invoke /getIntervalSaledHouseNumSum, startDay=" + startDay + ", endDay=" + endDay + ", interval=" + interval + ", region=" + region);
        if (startDay == "" || startDay == null)
            startDay = "2017/03/08";
        return houseStockService.getIntervalSaledHouseNumSum(TimeUtil.jsdateTransferTomydate(startDay), TimeUtil.jsdateTransferTomydate(endDay), interval, region);
    }

    @RequestMapping(value = "/getIntervalSaledZhuzhaiHouseNumSum", method = RequestMethod.POST)
    public List<?> getIntervalSaledZhuzhaiHouseNumSum(String startDay, String endDay, int interval, String region) throws Exception {
        logger.info("invoke /getIntervalSaledZhuzhaiHouseNumSum, startDay=" + startDay + ", endDay=" + endDay + ", interval=" + interval + ", region=" + region);
        if (startDay == "" || startDay == null)
            startDay = "2017/03/08";
        return houseStockService.getIntervalSaledZhuzhaiHouseNumSum(TimeUtil.jsdateTransferTomydate(startDay), TimeUtil.jsdateTransferTomydate(endDay), interval, region);
    }

    @RequestMapping(value = "/getForsaleZhuzhaiNumSum", method = RequestMethod.POST)
    public List<?> getForsaleZhuzhaiNumSum(String startDay, String endDay, int interval, String region) throws Exception {
        logger.info("invoke /getForsaleZhuzhaiNumSum, startDay=" + startDay + ", endDay=" + endDay + ", interval=" + interval + ", region=" + region);
        if (startDay == "" || startDay == null)
            startDay = "2017/03/08";
        return houseStockService.getForsaleZhuzhaiNum(TimeUtil.jsdateTransferTomydate(startDay), TimeUtil.jsdateTransferTomydate(endDay), interval, region);
    }

    @RequestMapping(value = "/getLimitedZhuzhaiNumSum", method = RequestMethod.POST)
    public List<?> getLimitedZhuzhaiNumSum(String startDay, String endDay, int interval, String region) throws Exception {
        logger.info("invoke /getLimitedZhuzhaiNumSum, startDay=" + startDay + ", endDay=" + endDay + ", interval=" + interval + ", region=" + region);
        if (startDay == "" || startDay == null)
            startDay = "2017/03/08";
        return houseStockService.getLimitedZhuzhaiNum(TimeUtil.jsdateTransferTomydate(startDay), TimeUtil.jsdateTransferTomydate(endDay), interval, region);
    }

    @RequestMapping(value = "/getForsaleBOANumSum", method = RequestMethod.POST)
    public List<?> getForsaleBOANumSum(String startDay, String endDay, int interval, String region) throws Exception {
        logger.info("invoke /getForsaleBOANumSum, startDay=" + startDay + ", endDay=" + endDay + ", interval=" + interval + ", region=" + region);
        if (startDay == "" || startDay == null)
            startDay = "2017/03/08";
        return houseStockService.getForsaleBOANum(TimeUtil.jsdateTransferTomydate(startDay), TimeUtil.jsdateTransferTomydate(endDay), interval, region);
    }

    @RequestMapping(value = "/getLimitedBOANumSum", method = RequestMethod.POST)
    public List<?> getLimitedBOANumSum(String startDay, String endDay, int interval, String region) throws Exception {
        logger.info("invoke /getLimitedBOANumSum, startDay=" + startDay + ", endDay=" + endDay + ", interval=" + interval + ", region=" + region);
        if (startDay == "" || startDay == null)
            startDay = "2017/03/08";
        return houseStockService.getLimitedBOANum(TimeUtil.jsdateTransferTomydate(startDay), TimeUtil.jsdateTransferTomydate(endDay), interval, region);
    }

}
