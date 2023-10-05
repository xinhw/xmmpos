package com.rankway.controller.dto;

import java.util.Date;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/07/02
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class PosInfoBean {
    String cposno;
    String usercode;
    int auditNo;
    String serverIP;
    int portNo;

    String shiftNo;     //  班次号

    int status;         //  状态：0--已经结班；1--未结班
    public static final int STATUS_SETTLE_OUT = 0;      //  结班
    public static final int STATUS_SETTLE_IN = 1;       //  班上

    Date startTime;         //  开班时间
    Date settleTime;        //  结班时间

    public String getCposno() {
        return cposno;
    }

    public void setCposno(String cposno) {
        this.cposno = cposno;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public int getAuditNo() {
        return auditNo;
    }

    public void setAuditNo(int auditNo) {
        if(auditNo>this.auditNo){
            this.auditNo = auditNo;
        }else{
            this.auditNo++;
        }
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getPortNo() {
        return portNo;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }

    public String getShiftNo() {
        return shiftNo;
    }

    public void setShiftNo(String shiftNo) {
        this.shiftNo = shiftNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(Date settleTime) {
        this.settleTime = settleTime;
    }

    @Override
    public String toString() {
        return "PosInfoBean{" +
                "cposno='" + cposno + '\'' +
                ", usercode='" + usercode + '\'' +
                ", auditNo=" + auditNo +
                ", serverIP='" + serverIP + '\'' +
                ", portNo=" + portNo +
                ", shiftNo='" + shiftNo + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", settleTime=" + settleTime +
                '}';
    }
}
