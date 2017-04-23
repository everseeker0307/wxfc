package com.everseeker.service;

import com.everseeker.entity.HouseInfo;
import com.everseeker.mapper.HouseInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by everseeker on 2017/3/2.
 */
@Service
public class HouseInfoService {
    @Autowired
    private HouseInfoMapper houseinfoMapper;

    /**
     * 根据id查询houseinfo信息
     * @param id
     * @return
     */
    public HouseInfo getHouseInfoById(int id) {
        return houseinfoMapper.getHouseInfoById(id);
    }

    /**
     * 数据表中增加houseinfo记录
     * @param houseInfo
     */
    public void addHouseInfo(HouseInfo houseInfo) {
        houseinfoMapper.addHouseInfo(houseInfo);
    }

    /**
     * 通过houseurlid查询houseinfo信息
     * @param houseUrlId
     * @return
     */
    public HouseInfo getHouseInfoByHouseUrlId(String houseUrlId) {
        return houseinfoMapper.getHouseInfoByHouseUrlId(houseUrlId);
    }

    /**
     * 根据日期查询楼盘总数
     * @param recordDate
     * @return
     */
    public int getTotalHouseNum(String recordDate) {
        return houseinfoMapper.getTotalHouseNum(recordDate);
    }

    /**
     * 更新数据表中部分记录
     * @param houseInfo
     */
    public void updateHouseInfoTypeNums(HouseInfo houseInfo) {
        houseinfoMapper.updateHouseInfoTypeNums(houseInfo);
    }

    /**
     * 查询houseinfo表中所有数据
     * @return
     */
    public List<HouseInfo> getAllHouseInfo() {
        return houseinfoMapper.getAllHouseInfo();
    }
}
