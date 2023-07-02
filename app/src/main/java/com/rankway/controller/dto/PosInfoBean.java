package com.rankway.controller.dto;

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
        this.auditNo = auditNo;
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

    @Override
    public String toString() {
        return "PosInfoBean{" +
                "cposno='" + cposno + '\'' +
                ", usercode='" + usercode + '\'' +
                ", auditNo=" + auditNo +
                ", serverIP='" + serverIP + '\'' +
                ", portNo=" + portNo +
                '}';
    }
}
