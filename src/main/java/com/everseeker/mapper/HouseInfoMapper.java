package com.everseeker.mapper;

import com.everseeker.entity.HouseInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
