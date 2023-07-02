package com.rankway.controller.webapi;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/10
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class cardInfo {
    int cardno;
    String gsno;
    int statusid;
    float gremain;
    String gno;
    String name;

    int systemId;
    int qrType;
    String userId;
    int qrStatus;
    long timeStamp;
    String mac;

    public int getCardno() {
        return cardno;
    }

    public void setCardno(int cardno) {
        this.cardno = cardno;
    }

    public String getGsno() {
        return gsno;
    }

    public void setGsno(String gsno) {
        this.gsno = gsno;
    }

    public int getStatusid() {
        return statusid;
    }

    public void setStatusid(int statusid) {
        this.statusid = statusid;
    }

    public float getGremain() {
        return gremain;
    }

    public void setGremain(float gremain) {
        this.gremain = gremain;
    }

    public String getGno() {
        return gno;
    }

    public void setGno(String gno) {
        this.gno = gno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public int getQrType() {
        return qrType;
    }

    public void setQrType(int qrType) {
        this.qrType = qrType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getQrStatus() {
        return qrStatus;
    }

    public void setQrStatus(int qrStatus) {
        this.qrStatus = qrStatus;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "cardInfo{" +
                "cardno=" + cardno +
                ", gsno='" + gsno + '\'' +
                ", statusid=" + statusid +
                ", gremain=" + gremain +
                ", gno='" + gno + '\'' +
                ", name='" + name + '\'' +
                ", systemId=" + systemId +
                ", qrType=" + qrType +
                ", userId='" + userId + '\'' +
                ", qrStatus=" + qrStatus +
                ", timeStamp=" + timeStamp +
                ", mac='" + mac + '\'' +
                '}';
    }

    public cardInfo(){
        this.timeStamp = System.currentTimeMillis();
    }

    public cardInfo(int cardno, String gsno, int statusid, float gremain, String gno, String name, int systemId, int qrType, String userId, int qrStatus, long timeStamp, String mac) {
        this.cardno = cardno;
        this.gsno = gsno;
        this.statusid = statusid;
        this.gremain = gremain;
        this.gno = gno;
        this.name = name;
        this.systemId = systemId;
        this.qrType = qrType;
        this.userId = userId;
        this.qrStatus = qrStatus;
        this.timeStamp = System.currentTimeMillis();
        this.mac = mac;
    }

    public cardInfo(cardInfo obj){
        this.cardno = obj.getCardno();
        this.gsno = obj.getGsno();
        this.statusid = obj.getStatusid();
        this.gremain = obj.getGremain();
        this.gno = obj.getGno();
        this.name = obj.getName();
        this.systemId = obj.getSystemId();
        this.qrType = obj.getQrType();
        this.userId = obj.getUserId();
        this.qrStatus = obj.getQrStatus();
        this.timeStamp = obj.getTimeStamp();
        this.mac = obj.getMac();
    }
}
