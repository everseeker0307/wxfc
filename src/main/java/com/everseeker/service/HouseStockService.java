package com.everseeker.service;

import com.everseeker.entity.HouseStock;
import com.everseeker.mapper.HouseStockMapper;
import com.everseeker.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by everseeker on 2017/3/3.
 */
@Service
@CacheConfig(cacheNames = "wxfc")
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
    public List<?> getTodayDealDetails(String today, String region) {
        String yesterday = TimeUtil.beforeGivenday(today, 1);
        return getPeriodDealDetails(yesterday, today, region);
    }

    /**
     * 返回一段时间内的成交详情
     * @param givenday
     * @param daysInterval
     * @param region
     * @return
     */

    @Cacheable(key = "'getPeriodDetails:' + #givenday + ':' + #daysInterval + ':' + #region")
    public List<?> getPeriodDetails(String givenday, int daysInterval, String region) {
        if (daysInterval == 1)
            return getTodayDealDetails(givenday, region);
        else if (daysInterval == 7) {
            String endDay = TimeUtil.firstDayBeforeGivenday(givenday, -1);
            String startDay = TimeUtil.beforeGivenday(endDay, 7);
            return getPeriodDealDetails(startDay, endDay, region);
        } else if (daysInterval == 30) {
            String startDay = TimeUtil.monthTailBeforeGivenday(givenday, 1);
            String endDay = TimeUtil.monthTailBeforeGivenday(givenday, 0);
            return getPeriodDealDetails(startDay, endDay, region);
        } else
            return null;
    }

    /**
     * 返回一段时间内的住宅成交详情
     * @param givenday
     * @param daysInterval
     * @param region
     * @return
     */
    @Cacheable(key = "'getZhuzhaiPeriodDetails:' + #givenday + ':' + #daysInterval + ':' + #region")
    public List<?> getZhuzhaiPeriodDetails(String givenday, int daysInterval, String region) {
        if (daysInterval == 1) {
            String yesterday = TimeUtil.beforeGivenday(givenday, 1);
            return getZhuzhaiPeriodDealDetails(yesterday, givenday, region);
        }
        else if (daysInterval == 7) {
            String endDay = TimeUtil.firstDayBeforeGivenday(givenday, -1);
            String startDay = TimeUtil.beforeGivenday(endDay, 7);
            return getZhuzhaiPeriodDealDetails(startDay, endDay, region);
        } else if (daysInterval == 30) {
            String startDay = TimeUtil.monthTailBeforeGivenday(givenday, 1);
            String endDay = TimeUtil.monthTailBeforeGivenday(givenday, 0);
            return getZhuzhaiPeriodDealDetails(startDay, endDay, region);
        } else
            return null;
    }

    /**
     * 返回startDay(不含)到endDay(含)期间的成交详情
     * @param startDay
     * @param endDay
     * @param region
     * @return
     */
    public List<?> getPeriodDealDetails(String startDay, String endDay, String region) {
        // 原先的做法为先取出所有楼盘的成交量，之后剔除为0的数据，采用list.removeIf(el -> ((BigDecimal)el.get("dealNum")).compareTo(BigDecimal.ZERO) == 0);
        // return houseStockMapper.getPeriodDealDetails(startDay, endDay);
        List<Map<String, Object>> list = houseStockMapper.getSaledHouseNum(startDay, endDay, region);
        list.stream().filter(m -> m.get("startsaledNum") == null).forEach(s -> s.put("startsaledNum", 0));
        List<Map<String, Object>> newlist = list.stream().filter(m -> !m.get("startsaledNum").equals(m.get("endsaledNum"))).collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> grouplist = newlist.stream().collect(Collectors.groupingBy(m -> (String)m.get("houseName")));
        List<Map<String, Object>> retlist = new ArrayList<>();
        transferDetailList(grouplist, retlist);
        Collections.sort(retlist, (Comparator<Map<String, Object>>) (o1, o2) -> (Integer)o2.get("dealNum") - (Integer)o1.get("dealNum"));

        return retlist;
    }

    /**
     * 返回startDay(不含)到endDay(含)期间的住宅成交详情
     * @param startDay
     * @param endDay
     * @param region
     * @return
     */
    public List<?> getZhuzhaiPeriodDealDetails(String startDay, String endDay, String region) {
        List<Map<String, Object>> list = houseStockMapper.getSaledHouseNum(startDay, endDay, region);
        list.stream().filter(m -> m.get("startsaledNum") == null).forEach(s -> s.put("startsaledNum", 0));
        List<Map<String, Object>> newlist = list.stream().filter(m -> !m.get("startsaledNum").equals(m.get("endsaledNum"))).collect(Collectors.toList());
        newlist.stream().forEach(s -> {
            s.put("endsaledNum", (int)((Integer)s.get("endsaledNum") * ((BigDecimal)s.get("ratio")).doubleValue()));
            s.put("startsaledNum", (int)((Integer)s.get("startsaledNum") * ((BigDecimal)s.get("ratio")).doubleValue()));
        });
        List<Map<String, Object>> newlist2 = newlist.stream().filter(m -> !m.get("startsaledNum").equals(m.get("endsaledNum"))).collect(Collectors.toList());
        Map<String, List<Map<String, Object>>> grouplist = newlist2.stream().collect(Collectors.groupingBy(m -> (String)m.get("houseName")));
        List<Map<String, Object>> retlist = new ArrayList<>();
        transferDetailList(grouplist, retlist);
        Collections.sort(retlist, (Comparator<Map<String, Object>>) (o1, o2) -> (Integer)o2.get("dealNum") - (Integer)o1.get("dealNum"));

        return retlist;
    }

    private void transferDetailList(Map<String, List<Map<String, Object>>> grouplist, List<Map<String, Object>> retlist) {
        for(Map.Entry<String, List<Map<String, Object>>> entry : grouplist.entrySet()) {
            Map<String, Object> ret = new HashMap<>();
            ret.put("houseName", entry.getKey());
            int dealnum = 0;
            for (Map<String, Object> map : entry.getValue()) {
                dealnum += (Integer)map.get("endsaledNum") - (Integer)map.get("startsaledNum");
            }
            ret.put("dealNum", dealnum);
            retlist.add(ret);
        }
    }

    /**
     * 返回givenday当天的在售库存总量
     * @param givenday
     * @param region
     * @return
     */
    public Long getForsaleHouseNumSum(String givenday, String region) {
        return houseStockMapper.getForsaleHouseNumSum(givenday, region);
    }

    /**
     * 返回givenday当天，region区域的限售库存总量
     * @param givenday
     * @param region
     * @return
     */
    public Long getLimitedHouseNumSum(String givenday, String region) {
        return houseStockMapper.getlimitedHouseNumSum(givenday, region);
    }

    /**
     * 返回截止到givenday总售出数
     * @param givenday
     * @param region:区域
     * @return
     */
    private Long getSaledHouseNumSum(String givenday, String region) {
        return houseStockMapper.getSaledHouseNumSum(givenday, region);
    }

    /**
     * 返回截止到givenday的住宅售出数
     * @param givenday
     * @param region
     * @return
     */
    private Long getSaledZhuzhaiHouseNumSum(String givenday, String region) {
        return houseStockMapper.getSaledZhuzhaiHouseNumSum(givenday, region);
    }

    /**
     * 返回从startDay(不含)到endDay(含)期间的成交量
     * @param startDay
     * @param endDay
     * @Param interval: 表示时间间隔，1: 每日统计; 7: 每周统计; 30: 每月统计
     * @Param region: 区域，比如滨湖区等
     * @return
     */
    @Cacheable(key = "'getIntervalSaledHouseNumSum:' + #startDay + ':' + #endDay + ':' + #interval + ':' + #region")
    public List<?> getIntervalSaledHouseNumSum(String startDay, String endDay, int interval, String region) {
        // 处理日期时间
        startDay = startDay(startDay, interval);
        endDay = endDay(endDay, interval);
        if (interval == 1) {
            startDay = TimeUtil.beforeGivenday(endDay, 12);
            if (startDay.compareTo(originDay) < 0)
                startDay = originDay;
        }

        // 根据日期查询数据库并返回
        List<Map<String, Object>> list = new ArrayList<>();
        String iday = startDay;
        long yesNum = getSaledHouseNumSum(iday, region);
        long todayNum;
        while (iday.compareTo(endDay) < 0) {
            if (interval == HouseStockService.DAILY || interval == HouseStockService.WEEKLY)
                iday = TimeUtil.beforeGivenday(iday, (-1) * interval);
            else {
                iday = TimeUtil.monthTailBeforeGivenday(iday, -1);
            }
            todayNum = getSaledHouseNumSum(iday, region);
            Map<String, Object> map = new HashMap<>();
            map.put("date", TimeUtil.formatDate(iday));
            map.put("dealNumSum", todayNum - yesNum);
            list.add(map);
            yesNum = todayNum;
        }
        return list;
    }

    /**
     * 返回从startDay(不含)到endDay(含)期间的住宅成交量
     * @param startDay
     * @param endDay
     * @Param interval: 表示时间间隔，1: 每日统计; 7: 每周统计; 30: 每月统计
     * @Param region: 区域，比如滨湖区等
     * @return
     */
    @Cacheable(key = "'getIntervalSaledZhuzhaiHouseNumSum:' + #startDay + ':' + #endDay + ':' + #interval + ':' + #region")
    public List<?> getIntervalSaledZhuzhaiHouseNumSum(String startDay, String endDay, int interval, String region) {
        // 处理日期时间
        startDay = startDay(startDay, interval);
        endDay = endDay(endDay, interval);
        if (interval == 1) {
            startDay = TimeUtil.beforeGivenday(endDay, 12);
            if (startDay.compareTo(originDay) < 0)
                startDay = originDay;
        }

        // 根据日期查询数据库并返回
        List<Map<String, Object>> list = new ArrayList<>();
        String iday = startDay;
        long yesNum = getSaledZhuzhaiHouseNumSum(iday, region);
        long todayNum;
        while (iday.compareTo(endDay) < 0) {
            if (interval == HouseStockService.DAILY || interval == HouseStockService.WEEKLY)
                iday = TimeUtil.beforeGivenday(iday, (-1) * interval);
            else {
                iday = TimeUtil.monthTailBeforeGivenday(iday, -1);
            }
            todayNum = getSaledZhuzhaiHouseNumSum(iday, region);
            Map<String, Object> map = new HashMap<>();
            map.put("date", TimeUtil.formatDate(iday));
            map.put("dealNumSum", todayNum - yesNum);
            list.add(map);
            yesNum = todayNum;
        }
        return list;
    }

    private long getForsaleZhuzhaiNum(String givenday, String region) {
        return houseStockMapper.getForsaleZhuzhaiNum(givenday, region);
    }

    private long getLimitedZhuzhaiNum(String givenday, String region) {
        return houseStockMapper.getLimitedZhuzhaiNum(givenday, region);
    }

    /**
     * 返回从startDay(含)到endDay(含)期间的住宅楼盘在售库存量
     * @param startDay
     * @param endDay
     * @Param interval: 表示时间间隔，1: 每日统计; 7: 每周日统计; 30: 每月末统计
     * @Param region: 区域
     * @return
     */
    @Cacheable(key = "'getForsaleZhuzhaiNum:' + #startDay + ':' + #endDay + ':' + #interval + ':' + #region")
    public List<?> getForsaleZhuzhaiNum(String startDay, String endDay, int interval, String region) {
        // 处理日期时间
        startDay = startDay(startDay, interval);
        endDay = endDay(endDay, interval);
        List<Map<String, Object>> list = new ArrayList<>();
        String iday = startDay;
        long zznum;
        while (iday.compareTo(endDay) <= 0) {
            zznum = getForsaleZhuzhaiNum(iday, region);
            Map<String, Object> map = new HashMap<>();
            map.put("date", TimeUtil.formatDate(iday));
            map.put("zzNumSum", zznum);
            list.add(map);
            if (interval == HouseStockService.DAILY || interval == HouseStockService.WEEKLY)
                iday = TimeUtil.beforeGivenday(iday, (-1) * interval);
            else {
                iday = TimeUtil.monthTailBeforeGivenday(iday, -1);
            }
        }
        return list;
    }

    /**
     * 返回从startDay(含)到endDay(含)期间的住宅楼盘限售库存量
     * @param startDay
     * @param endDay
     * @param interval
     * @param region
     * @return
     */
    @Cacheable(key = "'getLimitedZhuzhaiNum:' + #startDay + ':' + #endDay + ':' + #interval + ':' + #region")
    public List<?> getLimitedZhuzhaiNum(String startDay, String endDay, int interval, String region) {
        // 处理日期时间
        startDay = startDay(startDay, interval);
        endDay = endDay(endDay, interval);
        List<Map<String, Object>> list = new ArrayList<>();
        String iday = startDay;
        long zznum;
        while (iday.compareTo(endDay) <= 0) {
            zznum = getLimitedZhuzhaiNum(iday, region);
            Map<String, Object> map = new HashMap<>();
            map.put("date", TimeUtil.formatDate(iday));
            map.put("zzNumSum", zznum);
            list.add(map);
            if (interval == HouseStockService.DAILY || interval == HouseStockService.WEEKLY)
                iday = TimeUtil.beforeGivenday(iday, (-1) * interval);
            else {
                iday = TimeUtil.monthTailBeforeGivenday(iday, -1);
            }
        }
        return list;
    }

    /**
     * 返回从startDay(含)到endDay(含)期间的商业+办公+公寓等楼盘库存量
     * @param startDay
     * @param endDay
     * @Param interval: 表示时间间隔，1: 每日统计; 7: 每周日统计; 30: 每月末统计
     * @Param region: 区域
     * @return
     */
    @Cacheable(key = "'getForsaleBOANum:' + #startDay + ':' + #endDay + ':' + #interval + ':' + #region")
    public List<?> getForsaleBOANum(String startDay, String endDay, int interval, String region) {
        // 处理日期时间
        startDay = startDay(startDay, interval);
        endDay = endDay(endDay, interval);
        List<Map<String, Object>> list = new ArrayList<>();
        String iday = startDay;
        long zznum;
        long totalnum;
        while (iday.compareTo(endDay) <= 0) {
            zznum = getForsaleZhuzhaiNum(iday, region);
            Map<String, Object> map = new HashMap<>();
            map.put("date", TimeUtil.formatDate(iday));
            totalnum = getForsaleHouseNumSum(iday, region);
            map.put("boaNumSum", totalnum - zznum);
            list.add(map);
            if (interval == HouseStockService.DAILY || interval == HouseStockService.WEEKLY)
                iday = TimeUtil.beforeGivenday(iday, (-1) * interval);
            else {
                iday = TimeUtil.monthTailBeforeGivenday(iday, -1);
            }
        }
        return list;
    }

    /**
     * 返回从startDay(含)到endDay(含)期间的商业+办公+公寓等楼盘限售库存量
     * @param startDay
     * @param endDay
     * @param interval
     * @param region
     * @return
     */
    @Cacheable(key = "'getLimitedBOANum:' + #startDay + ':' + #endDay + ':' + #interval + ':' + #region")
    public List<?> getLimitedBOANum(String startDay, String endDay, int interval, String region) {
        // 处理日期时间
        startDay = startDay(startDay, interval);
        endDay = endDay(endDay, interval);
        List<Map<String, Object>> list = new ArrayList<>();
        String iday = startDay;
        long zznum;
        long totalnum;
        while (iday.compareTo(endDay) <= 0) {
            zznum = getLimitedZhuzhaiNum(iday, region);
            Map<String, Object> map = new HashMap<>();
            map.put("date", TimeUtil.formatDate(iday));
            totalnum = getLimitedHouseNumSum(iday, region);
            map.put("boaNumSum", totalnum - zznum);
            list.add(map);
            if (interval == HouseStockService.DAILY || interval == HouseStockService.WEEKLY)
                iday = TimeUtil.beforeGivenday(iday, (-1) * interval);
            else {
                iday = TimeUtil.monthTailBeforeGivenday(iday, -1);
            }
        }
        return list;
    }

    private String startDay(String startDay, int interval) {
        if (startDay.compareTo(originDay) < 0)
            startDay = originDay;
        if (interval == HouseStockService.WEEKLY)
            startDay = TimeUtil.firstDayBeforeGivenday(startDay, -1);
        else if (interval == HouseStockService.MONTHLY)
            startDay = TimeUtil.monthTailBeforeGivenday(startDay, 0);
        return startDay;
    }

    private String endDay(String endDay, int interval) {
        if (endDay.compareTo(TimeUtil.getBeijingDate(new Date())) > 0)
            endDay = TimeUtil.getBeijingDate(new Date());
        if (interval == HouseStockService.WEEKLY)
            endDay = TimeUtil.firstDayBeforeGivenday(endDay, 1);
        else if (interval == HouseStockService.MONTHLY)
            endDay = TimeUtil.monthTailBeforeGivenday(endDay, 1);
        return endDay;
    }

}
