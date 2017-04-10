package com.everseeker.service;

import com.everseeker.entity.HouseStock;
import com.everseeker.mapper.HouseStockMapper;
import com.everseeker.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by everseeker on 2017/3/3.
 */
@Service
public class HouseStockService {
    private static final String originDay = "20170307";

    @Autowired
    private HouseStockMapper houseStockMapper;

    public void addHouseStock(HouseStock houseStock) {
        houseStockMapper.addHouseStock(houseStock);
    }

    public int getRecordNumByDate(String recordDate) {
        return houseStockMapper.getRecordNumByDate(recordDate);
    }

    public List<String> getMissedHouseUrlId(String recordDate) {
        return houseStockMapper.getMissedHouseUrlId(recordDate);
    }

    public void addMissedHouseStock(List<HouseStock> list, String yes) {
        houseStockMapper.addMissedHouseStock(list, yes);
    }

    public List<HouseStock> getMissedHouseStockInDate(String oldDate, String newDate) {
        return houseStockMapper.getMissedHouseStockInDate(oldDate, newDate);
    }

    /**
     * 返回today当天的成交详情
     * @param today
     * @return
     */
    public List<?> getTodayDealDetails(String today) {
        System.out.println("service: getTodayDealDetails");
        System.out.println("today = " + today);
        String yesterday = TimeUtil.beforeGivenDay(today, 1);
        return getPeriodDealDetails(yesterday, today);
    }

    /**
     * 返回startDay(不含)到endDay(含)期间的成交详情
     * @param startDay
     * @param endDay
     * @return
     */
    public List<?> getPeriodDealDetails(String startDay, String endDay) {
        // 原先的做法为先取出所有楼盘的成交量，之后剔除为0的数据，采用list.removeIf(el -> ((BigDecimal)el.get("dealNum")).compareTo(BigDecimal.ZERO) == 0);
        return houseStockMapper.getPeriodDealDetails(startDay, endDay);
    }

    /**
     * 返回givenday当天的库存总量
     * @param givenday
     * @return
     */
    public Long getForsaleHouseNumSum(String givenday) {
        return houseStockMapper.getForsaleHouseNumSum(givenday);
    }

    /**
     * 返回截止到givenday总售出数
     * @param givenday
     * @return
     */
    public Long getSaledHouseNumSum(String givenday) {
        return houseStockMapper.getSaledHouseNumSum(givenday);
    }

    /**
     * 返回从startDay(不含)到endDay(含)期间的每日成交量
     * @param startDay
     * @param endDay
     * @return
     */
    public Map<String, Long> getDailySaledHouseNumSum(String startDay, String endDay) {
        if (startDay.compareTo(originDay) < 0)
            startDay = originDay;
        if (endDay.compareTo(TimeUtil.getBeijingDate(new Date())) > 0)
            endDay = TimeUtil.getBeijingDate(new Date());
        Map<String, Long> map = new HashMap<>();
        String iday = startDay;
        long yesNum = getSaledHouseNumSum(iday);
        long todayNum;
        while (iday != endDay) {
            iday = TimeUtil.beforeGivenDay(iday, -1);   //iday设置为之后一天
            todayNum = getSaledHouseNumSum(iday);
            map.put(iday, todayNum - yesNum);
            yesNum = todayNum;
        }
        return map;
    }
}
