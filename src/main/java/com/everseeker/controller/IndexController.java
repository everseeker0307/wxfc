package com.everseeker.controller;

import com.everseeker.service.HouseStockService;
import com.everseeker.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @RequestMapping("/getForsaleHouseNumSum")
    public Long getForsaleHouseNumSum(@RequestParam("givenday") String givenday) throws Exception {
        return houseStockService.getForsaleHouseNumSum(TimeUtil.jsdateTransferTomydate(givenday));
    }

    @RequestMapping(value = "/getDailySaledHouseNumSum", method = RequestMethod.POST)
    public Map<String, Long> getDailySaledHouseNumSum(String startDay, String endDay) throws Exception {
        if (startDay == "" || startDay == null)
            startDay = "20170307";
        return houseStockService.getDailySaledHouseNumSum(startDay, TimeUtil.jsdateTransferTomydate(endDay));
    }
}
