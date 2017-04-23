package com.everseeker.mapper;

import com.everseeker.entity.HouseStock;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Array;
import java.util.List;
import java.util.Map;

/**
 * Created by everseeker on 2017/3/3.
 */
public interface HouseStockMapper {
    /**
     * 判断(houseUrlId, recordDate)是否已经存在，如果不存在则新增记录，如果已存在则更新
     * @param houseStock
     */
    @Insert("INSERT INTO housestock(houseUrlId, totalHouseNum, forsaleHouseNum, saledHouseNum, limitedHouseNum, recordDate)" +
            "VALUES(#{houseUrlId}, #{totalHouseNum}, #{forsaleHouseNum}, #{saledHouseNum}, #{limitedHouseNum}, #{recordDate})" +
            "ON DUPLICATE KEY UPDATE totalHouseNum = #{totalHouseNum}, forsaleHouseNum = #{forsaleHouseNum}, saledHouseNum = " +
            "#{saledHouseNum}, limitedHouseNum = #{limitedHouseNum}")
    void addHouseStock(HouseStock houseStock);

    /**
     * 查找recordDate当天的数据条目
     * @param recordDate
     * @return
     */
    @Select("SELECT COUNT(*) FROM housestock WHERE recordDate = #{recordDate}")
    int getRecordNumByDate(String recordDate);

    /**
     * 查找recordDate当天爬取的数据
     * @param recordDate
     * @return
     */
    @Select("select * from housestock where recordDate = #{recordDate}")
    List<HouseStock> getHouseStocksByDate(String recordDate);

    /**
     * 查找recordDate遗漏爬取的houseUrlId
     * @param recordDate
     * @return
     */
    @Select("select houseUrlId from houseinfo where createDate <= #{recordDate} and houseUrlId not in (select houseUrlId from housestock where recordDate = #{recordDate})")
    List<String> getMissedHouseUrlId(String recordDate);

    /**
     * 查找newDate和oldDate比较后缺少的数据
     * @param oldDate
     * @param newDate
     * @return
     */
    @Select("select * from housestock where recordDate = #{oldDate} and houseUrlId not in (select houseUrlId from housestock where recordDate = #{newDate})")
    List<HouseStock> getMissedHouseStockInDate(@Param("oldDate") String oldDate, @Param("newDate") String newDate);

    /**
     * 将List<HouseStock>的数据更改recordDate为yes后，保存到数据库
     * @param houseStocks
     * @param yes
     */
    @Insert({"<script>" +
            "insert into housestock(houseUrlId, totalHouseNum, forsaleHouseNum, saledHouseNum, limitedHouseNum, recordDate) values" +
            "<foreach item='item' index='index' collection='houseStocks' separator=','>" +
                "(#{item.houseUrlId}, #{item.totalHouseNum}, #{item.forsaleHouseNum}, #{item.saledHouseNum}, #{item.limitedHouseNum}, #{yes})" +
            "</foreach>" +
            "</script>"})
    void addMissedHouseStock(@Param("houseStocks") List<HouseStock> houseStocks, @Param("yes") String yes);

    /**
     * 返回从startDay(不含)到endDay(含)期间的成交详情. 包括楼盘名和成交量，按成交量高->低排序.
     * @param startDay
     * @param endDay
     * @return
     */
    @Select("select h.houseName, sum(s.r) as dealNum from houseinfo h join (select (r1 - r0) as r, t.houseUrlId from (select t1.saledHouseNum r1, t0.saledHouseNum r0, t1.houseUrlId from housestock t1 join housestock t0 on t1.houseUrlId = t0.houseUrlId and t1.recordDate=#{endDay} and t0.recordDate=#{startDay}) as t having r != 0) as s on s.houseUrlId=h.houseUrlId group by h.houseName order by dealNum desc")
    List<Map<String, Object>> getPeriodDealDetails(@Param("startDay") String startDay, @Param("endDay") String endDay);

    /**
     * 返回givenday当天的库存总量
     * @param givenday
     * @return
     */
    @Select("select sum(forsaleHouseNum) from housestock where recordDate=#{givenday}")
    Long getForsaleHouseNumSum(String givenday);

    /**
     * 返回givenday那天已经售出的总量
     * @param givenday
     * @return
     */
    @Select("select sum(saledHouseNum) from housestock where recordDate=#{givenday}")
    Long getSaledHouseNumSum(String givenday);

    @Select("select sum((h.houseNum/h.totalHouseNum) * s.forsaleHouseNum) as forSaleHouseNum from houseinfo h join housestock s on s.recordDate=#{givenday} and h.houseUrlId=s.houseUrlId")
    Long getForsaleZhuzhaiNum(String givenday);
}
