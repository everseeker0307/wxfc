package com.everseeker.mapper;

import com.everseeker.entity.HouseStock;
import org.apache.ibatis.annotations.Insert;

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
}
