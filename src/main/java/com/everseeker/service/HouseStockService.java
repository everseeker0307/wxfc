package com.everseeker.service;

import com.everseeker.entity.HouseStock;
import com.everseeker.mapper.HouseStockMapper;
import com.everseeker.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;

/**
 * Created by everseeker on 2017/3/3.
 */
@Service
public class HouseStockService {
    private static final String originDay = "20170308";

    private static final int DAILY = 1;
    private static final int WEEKLY = 7;
    private static final int MONTHLY = 30;

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
        String yesterday = TimeUtil.beforeGivenday(today, 1);
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
     * 返回从startDay(不含)到endDay(含)期间的成交量
     * @param startDay
     * @param endDay
     * @Param interval: 表示时间间隔，1: 每日统计; 7: 每周统计; 30: 每月统计
     * @return
     */
    public List<?> getIntervalSaledHouseNumSum(String startDay, String endDay, int interval) {
        // 处理日期时间
        if (startDay.compareTo(originDay) < 0)
            startDay = originDay;
        if (endDay.compareTo(TimeUtil.getBeijingDate(new Date())) > 0)
            endDay = TimeUtil.getBeijingDate(new Date());
        if (interval == HouseStockService.WEEKLY) {
            startDay = TimeUtil.firstDayBeforeGivenday(startDay, -1);
            endDay = TimeUtil.firstDayBeforeGivenday(endDay, 1);
        } else if (interval == HouseStockService.MONTHLY) {
            startDay = TimeUtil.monthTailBeforeGivenday(startDay, 0);
            endDay = TimeUtil.monthTailBeforeGivenday(endDay, 1);
        }

        // 根据日期查询数据库并返回
        List<Map<String, Object>> list = new ArrayList<>();
        String iday = startDay;
        long yesNum = getSaledHouseNumSum(iday);
        long todayNum;
        while (iday.compareTo(endDay) < 0) {
            if (interval == HouseStockService.DAILY || interval == HouseStockService.WEEKLY)
                iday = TimeUtil.beforeGivenday(iday, (-1) * interval);
            else {
                iday = TimeUtil.monthTailBeforeGivenday(iday, -1);
            }
            todayNum = getSaledHouseNumSum(iday);
            Map<String, Object> map = new HashMap<>();
            map.put("date", TimeUtil.formatDate(iday));
            map.put("dealNumSum", todayNum - yesNum);
            list.add(map);
            yesNum = todayNum;
        }
        return list;
    }
}
