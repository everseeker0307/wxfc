package com.everseeker.service;

import com.everseeker.entity.HouseInfo;
import com.everseeker.mapper.HouseInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by everseeker on 2017/3/2.
 */
@Service
public class HouseInfoService {
    @Autowired
    private HouseInfoMapper houseinfoMapper;

    public HouseInfo getHouseInfoById(int id) {
        return houseinfoMapper.getHouseInfoById(id);
    }

    public void addHouseInfo(HouseInfo houseInfo) {
        houseinfoMapper.addHouseInfo(houseInfo);
    }

    public HouseInfo getHouseInfoByHouseUrlId(String houseUrlId) {
        return houseinfoMapper.getHouseInfoByHouseUrlId(houseUrlId);
    }

    public int getTotalHouseNum(String recordDate) {
        return houseinfoMapper.getTotalHouseNum(recordDate);
    }
}
