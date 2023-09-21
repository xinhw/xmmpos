package com.rankway.controller.webapi;

import com.rankway.controller.persistence.entity.PaymentRecord;

import java.text.SimpleDateFormat;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/10
 *   desc  : 通过WebAPI向上批量传消费记录
 *   version: 1.0
 * </pre>
 */
public class payrecord {
    String cno;
    String cposno;
    String cpostype;
    String cpayway;
    String cusercode;
    int cardno;
    String cdate;
    double cmoney;
    double cremain;
    String cnote;
    int typeid;
    String localtime;

    int systemid;
    int qrtype;
    String userid;
    int transtype;

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    public String getCposno() {
        return cposno;
    }

    public void setCposno(String cposno) {
        this.cposno = cposno;
    }

    public String getCpostype() {
        return cpostype;
    }

    public void setCpostype(String cpostype) {
        this.cpostype = cpostype;
    }

    public String getCpayway() {
        return cpayway;
    }

    public void setCpayway(String cpayway) {
        this.cpayway = cpayway;
    }

    public String getCusercode() {
        return cusercode;
    }

    public void setCusercode(String cusercode) {
        this.cusercode = cusercode;
    }

    public int getCardno() {
        return cardno;
    }

    public void setCardno(int cardno) {
        this.cardno = cardno;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public double getCmoney() {
        return cmoney;
    }

    public void setCmoney(double cmoney) {
        this.cmoney = cmoney;
    }

    public double getCremain() {
        return cremain;
    }

    public void setCremain(double cremain) {
        this.cremain = cremain;
    }

    public String getCnote() {
        return cnote;
    }

    public void setCnote(String cnote) {
        this.cnote = cnote;
    }

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public String getLocaltime() {
        return localtime;
    }

    public void setLocaltime(String localtime) {
        this.localtime = localtime;
    }

    public int getSystemid() {
        return systemid;
    }

    public void setSystemid(int systemid) {
        this.systemid = systemid;
    }

    public int getQrtype() {
        return qrtype;
    }

    public void setQrtype(int qrtype) {
        this.qrtype = qrtype;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getTranstype() {
        return transtype;
    }

    public void setTranstype(int transtype) {
        this.transtype = transtype;
    }

    @Override
    public String toString() {
        return "payrecord{" +
                "cno=" + cno +
                ", cposno='" + cposno + '\'' +
                ", cpostype='" + cpostype + '\'' +
                ", cpayway='" + cpayway + '\'' +
                ", cusercode='" + cusercode + '\'' +
                ", cardno=" + cardno +
                ", cdate='" + cdate + '\'' +
                ", cmoney=" + cmoney +
                ", cremain=" + cremain +
                ", cnote='" + cnote + '\'' +
                ", typeid=" + typeid +
                ", localtime='" + localtime + '\'' +
                ", systemid=" + systemid +
                ", qrtype=" + qrtype +
                ", userid='" + userid + '\'' +
                ", transtype=" + transtype +
                '}';
    }

    public payrecord(PaymentRecord record){
        this.cno = record.getAuditNo()+"";
        this.cposno = record.getPosNo();
        this.cpostype = record.getPostype()+"";
        this.cpayway = record.getPayway()+"";
        this.cusercode = record.getUserCode();
        this.cardno = record.getCardno();
        this.cremain = record.getRemain();
        this.cmoney = record.getAmount();
        this.cnote = "";
        this.typeid = record.getTypeid();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.SSS");
        this.localtime = simpleDateFormat.format(record.getTransTime());

        this.systemid = record.getSystemId();
        this.qrtype = record.getQrType();
        this.userid = record.getUserId();
    }
}
