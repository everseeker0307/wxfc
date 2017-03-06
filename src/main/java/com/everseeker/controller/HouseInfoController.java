package com.everseeker.controller;

import com.everseeker.entity.HouseInfo;
import com.everseeker.service.HouseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by everseeker on 2017/3/2.
 */
@RestController
public class HouseInfoController {
    @Autowired
    private HouseInfoService houseInfoService;

    @RequestMapping("/hello")
    public HouseInfo getHouseInfoById() {
        return houseInfoService.getHouseInfoById(1);
    }
}
