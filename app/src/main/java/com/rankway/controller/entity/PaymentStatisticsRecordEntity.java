package com.rankway.controller.entity;

import com.rankway.controller.persistence.entity.PaymentRecordEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/18
 *   desc  : 支付统计信息（逐日）
 *   version: 1.0
 * </pre>
 */
public class PaymentStatisticsRecordEntity {
    int seqNo;
    String cdate;
    int subCount;
    float subAmount;

    List<PaymentRecordEntity> recordList;

    public PaymentStatisticsRecordEntity(){
        recordList = new ArrayList<>();
    }
    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public int getSubCount() {
        return subCount;
    }

    public void setSubCount(int subCount) {
        this.subCount = subCount;
    }

    public float getSubAmount() {
        return subAmount;
    }

    public void setSubAmount(float subAmount) {
        this.subAmount = subAmount;
    }

    public List<PaymentRecordEntity> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<PaymentRecordEntity> recordList) {
        this.recordList = recordList;
    }

    @Override
    public String toString() {
        return "PaymentStatisticsRecordEntity{" +
                "seqNo=" + seqNo +
                ", cdate='" + cdate + '\'' +
                ", subCount=" + subCount +
                ", subAmount=" + subAmount +
                '}';
    }
}
