package com.everseeker.controller;

import com.everseeker.entity.HouseInfo;
import com.everseeker.service.HouseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by everseeker on 2017/3/2.
 */
@Controller
public class HouseInfoController {
//    @Autowired
//    private HouseInfoService houseInfoService;
//
//    @ResponseBody
//    @RequestMapping("/")
//    public String getIndex() {
//        return "index";
//    }
//
//    @RequestMapping("/houseinfo/{urlId}")
//    public String getHouseInfoById(@PathVariable("urlId") String urlId, Model model) {
//        HouseInfo houseInfo = houseInfoService.getHouseInfoByHouseUrlId(urlId);
//        model.addAttribute("houseInfo", houseInfo);
//        return "houseinfo";
//    }
}
