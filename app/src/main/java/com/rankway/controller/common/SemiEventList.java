package com.rankway.controller.common;

import com.rankway.controller.persistence.entity.SemiEventEntity;

import java.util.ArrayList;
import java.util.List;

public class SemiEventList {
    String controllerNo;
    String loginID;
    String appName;
    String appVersion;
    String mbVersion;
    long timeStamp;
    List<SemiEventEntity> eventList;


    public String getControllerNo() {
        return controllerNo;
    }

    public void setControllerNo(String controllerNo) {
        controllerNo = controllerNo;
    }

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        loginID = loginID;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<SemiEventEntity> getEventList() {
        return eventList;
    }

    public void setEventList(List<SemiEventEntity> eventList) {
        this.eventList = eventList;
    }

    public SemiEventList() {
        controllerNo ="";
        loginID = "";
        appName="";
        mbVersion="";
        timeStamp = System.currentTimeMillis();
        eventList = new ArrayList<>();
    }

    public SemiEventList(String controllerNo, String loginID, String appName, String appVersion, String mbVersion) {
        this.controllerNo = controllerNo;
        this.loginID = loginID;
        this.appName = appName;
        this.appVersion = appVersion;
        this.mbVersion = mbVersion;
        timeStamp = System.currentTimeMillis();
        eventList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "SemiEventList{" +
                "controllerNo='" + controllerNo + '\'' +
                ", loginID='" + loginID + '\'' +
                ", appName='" + appName + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", mbVersion='" + mbVersion + '\'' +
                ", timeStamp=" + timeStamp +
                ", eventList=" + eventList +
                '}';
    }
}
