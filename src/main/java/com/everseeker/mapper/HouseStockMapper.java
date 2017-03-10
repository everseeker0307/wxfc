package com.everseeker.mapper;

import com.everseeker.entity.HouseStock;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    @Select("SELECT COUNT(*) FROM housestock WHERE recordDate = #{recordDate}")
    int getRecordNumByDate(String recordDate);

    @Select("select houseUrlId from houseinfo where createDate <= #{recordDate} and houseUrlId not in (select houseUrlId from housestock where recordDate = #{recordDate})")
    List<String> getMissedHouseUrlId(String recordDate);
}
