package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/10/26
 *   desc  :
 *   version: 1.0
 * </pre>
 */
@Entity
public class PaymentShiftEntity
        implements Comparable<PaymentShiftEntity>{
    @JSONField(serialize = false)
    @Id(autoincrement = true)
    Long id;

    String posNo;           //  POS机号
    String operatorNo;      //  操作员号

    long shiftOnAuditNo;    //  开班流水号
    long shiftOnTime;       //  开班时间

    long shiftOffAuditNo;   //  结班流水号
    long shiftOffTime;      //  结班时间

    long reportTime;        //  报告时间

    int subCardCount;       //  IC卡消费次数
    long subCardAmount;     //  IC卡消费合计金额

    int subQrCount;         //  二维码消费次数
    long subQrAmount;       //  二维吗消费金额

    int status;             //  状态

    @Transient
    String shiftNo;         //  班次号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosNo() {
        return posNo;
    }

    public void setPosNo(String posNo) {
        this.posNo = posNo;
    }

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public long getShiftOnAuditNo() {
        return shiftOnAuditNo;
    }

    public void setShiftOnAuditNo(long shiftOnAuditNo) {
        this.shiftOnAuditNo = shiftOnAuditNo;
    }

    public long getShiftOnTime() {
        return shiftOnTime;
    }

    public void setShiftOnTime(long shiftOnTime) {
        this.shiftOnTime = shiftOnTime;
    }

    public long getShiftOffAuditNo() {
        return shiftOffAuditNo;
    }

    public void setShiftOffAuditNo(long shiftOffAuditNo) {
        this.shiftOffAuditNo = shiftOffAuditNo;
    }

    public long getShiftOffTime() {
        return shiftOffTime;
    }

    public void setShiftOffTime(long shiftOffTime) {
        this.shiftOffTime = shiftOffTime;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public int getSubCardCount() {
        return subCardCount;
    }

    public void setSubCardCount(int subCardCount) {
        this.subCardCount = subCardCount;
    }

    public long getSubCardAmount() {
        return subCardAmount;
    }

    public void setSubCardAmount(long subCardAmount) {
        this.subCardAmount = subCardAmount;
    }

    public int getSubQrCount() {
        return subQrCount;
    }

    public void setSubQrCount(int subQrCount) {
        this.subQrCount = subQrCount;
    }

    public long getSubQrAmount() {
        return subQrAmount;
    }

    public void setSubQrAmount(long subQrAmount) {
        this.subQrAmount = subQrAmount;
    }

    @Override
    public int compareTo(PaymentShiftEntity o) {
        return (int)(o.getId()-this.getId());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static final int SHIFT_STATUS_ON = 1;                //  开班未结班
    public static final int SHIFT_STATUS_OFF = 2;                //  结班未上传
    public static final int SHIFT_STATUS_OFF_UPLOADED = 0;       //  结班已上传


    @Generated(hash = 1861760238)
    public PaymentShiftEntity(Long id, String posNo, String operatorNo,
            long shiftOnAuditNo, long shiftOnTime, long shiftOffAuditNo,
            long shiftOffTime, long reportTime, int subCardCount,
            long subCardAmount, int subQrCount, long subQrAmount, int status) {
        this.id = id;
        this.posNo = posNo;
        this.operatorNo = operatorNo;
        this.shiftOnAuditNo = shiftOnAuditNo;
        this.shiftOnTime = shiftOnTime;
        this.shiftOffAuditNo = shiftOffAuditNo;
        this.shiftOffTime = shiftOffTime;
        this.reportTime = reportTime;
        this.subCardCount = subCardCount;
        this.subCardAmount = subCardAmount;
        this.subQrCount = subQrCount;
        this.subQrAmount = subQrAmount;
        this.status = status;
    }

    @Generated(hash = 161782890)
    public PaymentShiftEntity() {
    }

    public int getTotalCount(){
        return this.subCardCount+this.subQrCount;
    }

    public long getTotalAmount(){
        return this.subCardAmount+this.subQrAmount;
    }

    public void subCardCountInc(int n){
        this.subCardCount = this.subCardCount+n;
    }
    public void subQrCountInc(int n){
        this.subQrCount = this.subQrCount + n;
    }
    public void subCardAmountInc(int amount){
        this.subCardAmount = this.subCardAmount+amount;
    }
    public void subQrAmountInc(int amount){
        this.subQrAmount = this.subQrAmount + amount;
    }

    @Override
    public String toString() {
        return "PaymentShiftEntity{" +
                "id=" + id +
                ", posNo='" + posNo + '\'' +
                ", operatorNo='" + operatorNo + '\'' +
                ", shiftOnAuditNo=" + shiftOnAuditNo +
                ", shiftOnTime=" + shiftOnTime +
                ", shiftOffAuditNo=" + shiftOffAuditNo +
                ", shiftOffTime=" + shiftOffTime +
                ", reportTime=" + reportTime +
                ", subCardCount=" + subCardCount +
                ", subCardAmount=" + subCardAmount +
                ", subQrCount=" + subQrCount +
                ", subQrAmount=" + subQrAmount +
                ", status=" + status +
                ", shiftNo='" + shiftNo + '\'' +
                '}';
    }

    public String getShiftNo() {
        return shiftNo;
    }

    public void setShiftNo(String shiftNo) {
        this.shiftNo = shiftNo;
    }
}
