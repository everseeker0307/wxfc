package com.everseeker.controller;

import com.everseeker.service.HouseStockService;
import com.everseeker.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Created by everseeker on 2017/4/4.
 */
@RestController
public class IndexController {

    @Autowired
    private HouseStockService houseStockService;

    @RequestMapping(value = "/getTodayDealDetails", method = RequestMethod.POST)
    public List<?> getTodayDealDetails(String today) throws Exception {
        return houseStockService.getTodayDealDetails(TimeUtil.jsdateTransferTomydate(today));
    }

    @RequestMapping(value = "/getPeriodDetails", method = RequestMethod.POST)
    public List<?> getPeriodDetails(String givenday, int daysInterval) throws Exception {
        return houseStockService.getPeriodDetails(TimeUtil.jsdateTransferTomydate(givenday), daysInterval);
    }

    @RequestMapping("/getForsaleHouseNumSum")
    public Long getForsaleHouseNumSum(@RequestParam("givenday") String givenday) throws Exception {
        return houseStockService.getForsaleHouseNumSum(TimeUtil.jsdateTransferTomydate(givenday));
    }

    @RequestMapping(value = "/getIntervalSaledHouseNumSum", method = RequestMethod.POST)
    public List<?> getIntervalSaledHouseNumSum(String startDay, String endDay, int interval) throws Exception {
        if (startDay == "" || startDay == null)
            startDay = "2017/03/08";
        return houseStockService.getIntervalSaledHouseNumSum(TimeUtil.jsdateTransferTomydate(startDay), TimeUtil.jsdateTransferTomydate(endDay), interval);
    }

    @RequestMapping(value = "/getForsaleZhuzhaiNumSum", method = RequestMethod.POST)
    public List<?> getForsaleZhuzhaiNumSum(String startDay, String endDay, int interval) throws Exception {
        if (startDay == "" || startDay == null)
            startDay = "2017/03/08";
        return houseStockService.getForsaleZhuzhaiNum(TimeUtil.jsdateTransferTomydate(startDay), TimeUtil.jsdateTransferTomydate(endDay), interval);
    }

    @RequestMapping(value = "/getForsaleBOANumSum", method = RequestMethod.POST)
    public List<?> getForsaleBOANumSum(String startDay, String endDay, int interval) throws Exception {
        if (startDay == "" || startDay == null)
            startDay = "2017/03/08";
        return houseStockService.getForsaleBOANum(TimeUtil.jsdateTransferTomydate(startDay), TimeUtil.jsdateTransferTomydate(endDay), interval);
    }
}
