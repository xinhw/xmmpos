package com.rankway.controller.common;

import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.rankway.controller.activity.project.comment.AppSpSaveConstant;
import com.rankway.controller.activity.project.manager.SpManager;
import com.rankway.controller.model.User;

public class HandesetInfo {
    private String ControllerNo;
    private String LoginID;
    private String UserName;
    @JSONField(name="idNo")
    private String idNo;
    private String UnitCode;
    private String UnitName;
    private String appVersion;
    private String mbVersion;
    private String manufacturer;
    private String serialNo;

    private String appName;

    private String aliDeviceId;        //  阿里移动推送DeviceId

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getMbVersion() {
        return mbVersion;
    }

    public void setMbVersion(String mbVersion) {
        this.mbVersion = mbVersion;
    }

    public String getAliDeviceId() {
        return aliDeviceId;
    }

    public void setAliDeviceId(String aliDeviceId) {
        this.aliDeviceId = aliDeviceId;
    }

    public String getControllerNo() {
        return ControllerNo;
    }

    public void setControllerNo(String controllerNo) {
        ControllerNo = controllerNo;
    }

    public String getLoginID() {
        return LoginID;
    }

    public void setLoginID(String loginID) {
        LoginID = loginID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUnitCode() {
        return UnitCode;
    }

    public void setUnitCode(String unitCode) {
        UnitCode = unitCode;
    }

    public String getUnitName() {
        return UnitName;
    }

    public void setUnitName(String unitName) {
        UnitName = unitName;
    }

    @Override
    public String toString() {
        return "HandesetInfo{" +
                "ControllerNo='" + ControllerNo + '\'' +
                ", LoginID='" + LoginID + '\'' +
                ", UserName='" + UserName + '\'' +
                ", idNo='" + idNo + '\'' +
                ", UnitCode='" + UnitCode + '\'' +
                ", UnitName='" + UnitName + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", mbVersion='" + mbVersion + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", appName='" + appName + '\'' +
                ", aliDeviceId='" + aliDeviceId + '\'' +
                '}';
    }

    public void initData(String strSNO){
        //  缓存的登录名和密码
        setLoginID(SpManager.getIntance().getSpString(AppSpSaveConstant.USER_NAME));

        String userinfo = SpManager.getIntance().getSpString(AppSpSaveConstant.USER_INFO);
        if (!TextUtils.isEmpty(userinfo)) {
            User user = JSON.parseObject(userinfo, User.class);
            setUserName(user.getName());
            setIdNo(user.getIdCode());
            setUnitCode(user.getCompanyCode());
            setUnitName(user.getCompanyName());
        }
        setControllerNo(strSNO);

        String strModel =String.format("%s(%s)",
                Build.MODEL.toUpperCase(),
                Build.DISPLAY);
        //  百富的设备型号为： X3s
        if(strModel.contains("X3")||strModel.contains("X5")){
            manufacturer = "PAX " + strModel;
        }else{
            manufacturer ="iData " + strModel;
        }

        setSerialNo(Build.SERIAL);

        this.aliDeviceId = PushServiceFactory.getCloudPushService().getDeviceId();
    }
}
