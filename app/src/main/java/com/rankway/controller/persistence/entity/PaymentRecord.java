package com.rankway.controller.persistence.entity;

import com.rankway.controller.entity.PosInfoBean;
import com.rankway.controller.webapi.cardInfo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

import java.util.Date;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/04
 *   desc  : 支付记录（对应支付商品统计PaymentTotal记录）
 *   version: 1.0
 * </pre>
 */
@Entity
public class PaymentRecord implements Comparable<PaymentRecord>{
    @Id(autoincrement = true)
    Long id;

    int auditNo;        //  本地流水号
    String posNo;       //  POS号
    int postype;        //  POS类型，0     借用为金额（分）
    int payway;         //  支付方式，2
    String userCode;    //  操作员
    int cardno;         //  卡号
    float remain;       //  交易前余额
    float amount;       //  交易金额
    float balance;      //  交易后余额
    int typeid;         //  交易类型 100
    Date transTime;     //  本地时间

    String cardSNO;     //  卡唯一号
    String workNo;      //  工号
    String workName;    //  姓名

    String userId;      //  二维码UserId
    int qrType;         //  二维码类型
    int systemId;       //  二维码系统Id

    int uploadFlag;     //  上传标志
    Date uploadTime;    //  上传时间

    long shiftId;       //  班次ID

    @Generated(hash = 1731246785)
    public PaymentRecord(Long id, int auditNo, String posNo, int postype, int payway,
            String userCode, int cardno, float remain, float amount, float balance,
            int typeid, Date transTime, String cardSNO, String workNo, String workName,
            String userId, int qrType, int systemId, int uploadFlag, Date uploadTime,
            long shiftId) {
        this.id = id;
        this.auditNo = auditNo;
        this.posNo = posNo;
        this.postype = postype;
        this.payway = payway;
        this.userCode = userCode;
        this.cardno = cardno;
        this.remain = remain;
        this.amount = amount;
        this.balance = balance;
        this.typeid = typeid;
        this.transTime = transTime;
        this.cardSNO = cardSNO;
        this.workNo = workNo;
        this.workName = workName;
        this.userId = userId;
        this.qrType = qrType;
        this.systemId = systemId;
        this.uploadFlag = uploadFlag;
        this.uploadTime = uploadTime;
        this.shiftId = shiftId;
    }

    @Keep
    public PaymentRecord() {
        this.typeid = 100;
        this.payway = 2;
        this.postype = 0;
        this.transTime = new Date();
    }

    public PaymentRecord(cardInfo card, int amount,PosInfoBean pos){
        this.auditNo = pos.getAuditNo();
        this.posNo = pos.getCposno();
        this.postype = amount;
        this.payway = 2;
        this.userCode = pos.getUsercode();

        this.cardno = card.getCardno();
        this.remain = card.getGremain();
        this.amount = (float)(amount*0.01);
        this.balance =(float)(card.getGremain()-amount*0.01);
        this.typeid = 100;
        this.transTime = new Date();
        this.workNo = card.getGno();
        this.workName = card.getName();

        this.userCode = card.getUserId();
        this.qrType = card.getQrType();
        this.systemId = card.getSystemId();

        this.uploadFlag = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAuditNo() {
        return auditNo;
    }

    public void setAuditNo(int auditNo) {
        this.auditNo = auditNo;
    }

    public String getPosNo() {
        return posNo;
    }

    public void setPosNo(String posNo) {
        this.posNo = posNo;
    }

    public int getPostype() {
        return postype;
    }

    public void setPostype(int postype) {
        this.postype = postype;
    }

    public int getPayway() {
        return payway;
    }

    public void setPayway(int payway) {
        this.payway = payway;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public int getCardno() {
        return cardno;
    }

    public void setCardno(int cardno) {
        this.cardno = cardno;
    }

    public float getRemain() {
        return remain;
    }

    public void setRemain(float remain) {
        this.remain = remain;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public Date getTransTime() {
        return transTime;
    }

    public void setTransTime(Date transTime) {
        this.transTime = transTime;
    }

    public String getCardSNO() {
        return cardSNO;
    }

    public void setCardSNO(String cardSNO) {
        this.cardSNO = cardSNO;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getQrType() {
        return qrType;
    }

    public void setQrType(int qrType) {
        this.qrType = qrType;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public int getUploadFlag() {
        return uploadFlag;
    }

    public void setUploadFlag(int uploadFlag) {
        this.uploadFlag = uploadFlag;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    @Override
    public String toString() {
        return "PaymentRecord{" +
                "id=" + id +
                ", auditNo=" + auditNo +
                ", posNo='" + posNo + '\'' +
                ", postype=" + postype +
                ", payway=" + payway +
                ", userCode='" + userCode + '\'' +
                ", cardno=" + cardno +
                ", remain=" + remain +
                ", amount=" + amount +
                ", balance=" + balance +
                ", typeid=" + typeid +
                ", transTime=" + transTime +
                ", cardSNO='" + cardSNO + '\'' +
                ", workNo='" + workNo + '\'' +
                ", workName='" + workName + '\'' +
                ", userId='" + userId + '\'' +
                ", qrType=" + qrType +
                ", systemId=" + systemId +
                ", uploadFlag=" + uploadFlag +
                ", uploadTime=" + uploadTime +
                '}';
    }

    @Override
    public int compareTo(PaymentRecord o) {
        return (int)(this.id-o.getId());
    }

    public long getShiftId() {
        return shiftId;
    }

    public void setShiftId(long shiftId) {
        this.shiftId = shiftId;
    }
}
