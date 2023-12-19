package com.rankway.controller.webapi;

import com.rankway.controller.persistence.entity.PaymentRecordEntity;

import java.text.SimpleDateFormat;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/10/05
 *   desc  :
 *   version: 1.0
 * </pre>
 */
public class PaymentApplyEntity {
    int cno;
    String cposno;
    String cusercode;
    int cardno;
    String cdate;
    int typeid;
    float cmoney;

    int systemid;
    int qrtype;
    String userid;

    int payamount;

    public int getCno() {
        return cno;
    }

    public void setCno(int cno) {
        this.cno = cno;
    }

    public String getCposno() {
        return cposno;
    }

    public void setCposno(String cposno) {
        this.cposno = cposno;
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

    public float getCmoney() {
        return cmoney;
    }

    public void setCmoney(float cmoney) {
        this.cmoney = cmoney;
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

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public int getPayamount() {
        return payamount;
    }

    public void setPayamount(int payamount) {
        this.payamount = payamount;
    }

    @Override
    public String toString() {
        return "PaymentApplyEntity{" +
                "cno=" + cno +
                ", cposno='" + cposno + '\'' +
                ", cusercode='" + cusercode + '\'' +
                ", cardno=" + cardno +
                ", cdate='" + cdate + '\'' +
                ", typeid=" + typeid +
                ", cmoney=" + cmoney +
                ", systemid=" + systemid +
                ", qrtype=" + qrtype +
                ", userid='" + userid + '\'' +
                ", payamount=" + payamount +
                '}';
    }

    public PaymentApplyEntity(PaymentRecordEntity recordEntity){
        this.cno = recordEntity.getAuditNo();
        this.cposno = recordEntity.getPosNo();
        this.cusercode = recordEntity.getUserCode();
        this.cardno = recordEntity.getCardno();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        this.cdate = format.format(recordEntity.getTransTime());

        this.typeid = 100;

        this.cmoney = recordEntity.getAmount();
        this.systemid = recordEntity.getSystemId();
        this.qrtype = recordEntity.getQrType();
        this.userid = recordEntity.getUserId();

        this.payamount = recordEntity.getPostype();
    }
}
