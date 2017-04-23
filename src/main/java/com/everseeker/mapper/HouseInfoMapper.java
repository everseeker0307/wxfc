package com.everseeker.mapper;

import com.everseeker.entity.HouseInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by everseeker on 2017/3/2.
 */
public interface HouseInfoMapper {
    @Select("SELECT * FROM houseinfo WHERE id = #{id}")
    HouseInfo getHouseInfoById(@Param("id") int id);

    @Insert("INSERT INTO houseinfo(houseUrlId, houseName, tempName, presellLicence, presellApproval, houseDeveloper, partner, location, adminRegion, approval, planLicence, landLicence, constructLicence, holdLandLicence, presellArea, sellCompany, sellTel, sellAddress, telephone, tenement, totalHouseNum, createDate) " +
            "VALUES(#{houseUrlId}, #{houseName}, #{tempName}, #{presellLicence}, #{presellApproval}, #{houseDeveloper}, #{partner}, #{location}, #{adminRegion}, #{approval}, #{planLicence}, #{landLicence}, #{constructLicence}, #{holdLandLicence}, #{presellArea}, #{sellCompany}, #{sellTel}, #{sellAddress}, #{telephone}, #{tenement}, #{totalHouseNum}, #{createDate})")
    void addHouseInfo(HouseInfo houseInfo);

    @Select("SELECT * from houseinfo WHERE houseUrlId = #{houseUrlId}")
    HouseInfo getHouseInfoByHouseUrlId(@Param("houseUrlId") String houseUrlId);

    @Select("SELECT COUNT(*) FROM houseinfo where createDate <= #{recordDate}")
    int getTotalHouseNum(String recordDate);

    @Update("UPDATE houseinfo SET houseNum=#{houseNum}, busiNum=#{busiNum}, officeNum=#{officeNum}, carportNum=#{carportNum}, plantNum=#{plantNum}, otherNum=#{otherNum}, apartNum=#{apartNum}, " +
            "lowHouseNum=#{lowHouseNum}, multiHouseNum=#{multiHouseNum}, smallhighHouseNum=#{smallhighHouseNum}, highHouseNum=#{highHouseNum}, villaNum=#{villaNum} WHERE houseUrlId=#{houseUrlId}")
    void updateHouseInfoTypeNums(HouseInfo houseInfo);

    @Select("SELECT * FROM houseinfo")
    List<HouseInfo> getAllHouseInfo();
}