package com.everseeker.entity;

import java.io.Serializable;

/**
 * Created by everseeker on 2017/3/2.
 */
public class HouseStock implements Serializable {
    private int id;
    private String houseUrlId;
    private int totalHouseNum;
    private int forsaleHouseNum;
    private int saledHouseNum;
    private int limitedHouseNum;
    private String recordDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHouseUrlId() {
        return houseUrlId;
    }

    public void setHouseUrlId(String houseUrlId) {
        this.houseUrlId = houseUrlId;
    }

    public int getTotalHouseNum() {
        return totalHouseNum;
    }

    public void setTotalHouseNum(int totalHouseNum) {
        this.totalHouseNum = totalHouseNum;
    }

    public int getForsaleHouseNum() {
        return forsaleHouseNum;
    }

    public void setForsaleHouseNum(int forsaleHouseNum) {
        this.forsaleHouseNum = forsaleHouseNum;
    }

    public int getSaledHouseNum() {
        return saledHouseNum;
    }

    public void setSaledHouseNum(int saledHouseNum) {
        this.saledHouseNum = saledHouseNum;
    }

    public int getLimitedHouseNum() {
        return limitedHouseNum;
    }

    public void setLimitedHouseNum(int limitedHouseNum) {
        this.limitedHouseNum = limitedHouseNum;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    @Override
    public String toString() {
        return "[id=" + id + ", houseUrlId=" + houseUrlId + ",totalHouseNum=" + totalHouseNum + ", forsaleHouseNum=" + forsaleHouseNum
                + ", saledHouseNum=" + saledHouseNum + ", limitedHouseNum=" + limitedHouseNum + ", recordDate=" + recordDate + "]";
    }
}
