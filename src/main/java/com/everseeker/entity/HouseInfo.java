package com.everseeker.entity;

import java.io.Serializable;

/**
 * Created by everseeker on 2017/3/2.
 */
public class HouseInfo implements Serializable {
    private int id;
    private String houseUrlId;         //楼盘页面网址
    private String houseName;          //项目现定名
    private String tempName;           //项目暂定名
    private String presellLicence;     //预销售许可证号
    private String presellApproval;    //预销售批准机关
    private String houseDeveloper;     //开发商
    private String partner;            //合作方
    private String location;           //坐落
    private String adminRegion;        //行政区
    private String approval;           //立项批文
    private String planLicence;        //规划许可证号
    private String landLicence;        //土地证号
    private String constructLicence;   //施工许可证号
    private String holdLandLicence;    //用地许可证
    private double presellArea;        //预售总面积
    private String sellCompany;        //代销公司
    private String sellTel;            //电话
    private String sellAddress;        //项目销售地点
    private String telephone;          //销售电话
    private String tenement;           //物业公司
    private int totalHouseNum;         //总套数
    private String createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHouseUrlId() {
        return houseUrlId;
    }

    public void setHouseUrl(String houseUrlId) {
        this.houseUrlId = houseUrlId;
    }

    public int getTotalHouseNum() {
        return totalHouseNum;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getAdminRegion() {
        return adminRegion;
    }

    public String getApproval() {
        return approval;
    }

    public String getConstructLicence() {
        return constructLicence;
    }

    public String getHouseDeveloper() {
        return houseDeveloper;
    }

    public String getHouseName() {
        return houseName;
    }

    public String getLandLicence() {
        return landLicence;
    }

    public String getLocation() {
        return location;
    }

    public String getPartner() {
        return partner;
    }

    public String getPlanLicence() {
        return planLicence;
    }

    public String getPresellApproval() {
        return presellApproval;
    }

    public double getPresellArea() {
        return presellArea;
    }

    public String getPresellLicence() {
        return presellLicence;
    }

    public String getSellAddress() {
        return sellAddress;
    }

    public String getSellCompany() {
        return sellCompany;
    }

    public String getSellTel() {
        return sellTel;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getTempName() {
        return tempName;
    }

    public String getTenement() {
        return tenement;
    }

    public void setAdminRegion(String adminRegion) {
        this.adminRegion = adminRegion;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public void setConstructLicence(String constructLicence) {
        this.constructLicence = constructLicence;
    }

    public void setHouseDeveloper(String houseDeveloper) {
        this.houseDeveloper = houseDeveloper;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public void setLandLicence(String landLicence) {
        this.landLicence = landLicence;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public void setPlanLicence(String planLicence) {
        this.planLicence = planLicence;
    }

    public void setPresellApproval(String presellApproval) {
        this.presellApproval = presellApproval;
    }

    public void setPresellArea(double presellArea) {
        this.presellArea = presellArea;
    }

    public void setPresellLicence(String presellLicence) {
        this.presellLicence = presellLicence;
    }

    public void setSellAddress(String sellAddress) {
        this.sellAddress = sellAddress;
    }

    public void setSellCompany(String sellCompany) {
        this.sellCompany = sellCompany;
    }

    public void setSellTel(String sellTel) {
        this.sellTel = sellTel;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public void setTenement(String tenement) {
        this.tenement = tenement;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setTotalHouseNum(int totalHouseNum) {
        this.totalHouseNum = totalHouseNum;
    }

    public String getHoldLandLicence() {
        return holdLandLicence;
    }

    public void setHoldLandLicence(String holdLandLicence) {
        this.holdLandLicence = holdLandLicence;
    }

    @Override
    public String toString() {
        return "[id=" + id + ", houseUrlId=" + houseUrlId + ", houseName=" + houseName + ", tempName=" + tempName +
                ", presellLicence=" + presellLicence + ", presellApproval=" + presellApproval + ", houseDeveloper=" +
                houseDeveloper + ", partner=" + partner + ", location=" + location + ", adminRegion=" + adminRegion +
                ", approval=" + approval + ", planLicence=" + planLicence + ", landLicence=" + landLicence +
                ", constructLicence=" + constructLicence + ", holdLandLicence=" + holdLandLicence + ", presellArea=" +
                presellArea + ", sellCompany=" + sellCompany + ", sellTel=" + sellTel + ", sellAddress=" + sellAddress +
                ", telephone=" + telephone + ", tenement=" + tenement + ", totalHouseNum=" + totalHouseNum + ", createDate=" +
                createDate + "]";
    }
}
