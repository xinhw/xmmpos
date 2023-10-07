package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/09/14
 *   desc  :
 *   version: 1.0
 * </pre>
 */
@Entity
public class PaymentItemEntity implements Comparable<PaymentItemEntity>{
    @JSONField(serialize = false)
    @Id(autoincrement = true)
    Long id;

    String posSerialChild;
    String dishCode;
    String dishName;
    float price;
    int quantity;
    float transMoney;

    @JSONField(serialize = false)
    long timestamp;

    @JSONField(serialize = false)
    long paymentTotalId;
    
    @Generated(hash = 1343734876)
    public PaymentItemEntity(Long id, String posSerialChild, String dishCode, String dishName,
            float price, int quantity, float transMoney, long timestamp, long paymentTotalId) {
        this.id = id;
        this.posSerialChild = posSerialChild;
        this.dishCode = dishCode;
        this.dishName = dishName;
        this.price = price;
        this.quantity = quantity;
        this.transMoney = transMoney;
        this.timestamp = timestamp;
        this.paymentTotalId = paymentTotalId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPosSerialChild() {
        return posSerialChild;
    }

    public void setPosSerialChild(String posSerialChild) {
        this.posSerialChild = posSerialChild;
    }

    public String getDishCode() {
        return dishCode;
    }

    public void setDishCode(String dishCode) {
        this.dishCode = dishCode;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getTransMoney() {
        return transMoney;
    }

    public void setTransMoney(float transMoney) {
        this.transMoney = transMoney;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getPaymentTotalId() {
        return paymentTotalId;
    }

    public void setPaymentTotalId(long paymentTotalId) {
        this.paymentTotalId = paymentTotalId;
    }


    @Override
    public String toString() {
        return "PaymentItem{" +
                "id=" + id +
                ", posSerialChild=" + posSerialChild +
                ", dishCode='" + dishCode + '\'' +
                ", dishName='" + dishName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", transMoney=" + transMoney +
                ", timestamp=" + timestamp +
                ", paymentTotalId=" + paymentTotalId +
                '}';
    }

    @Override
    public int compareTo(PaymentItemEntity o) {
        int ret = (int)(this.getPaymentTotalId()-o.getPaymentTotalId());
        if(ret!=0) return ret;

        ret = (int)(this.getId() - o.getId());
        return ret;
    }

    public PaymentItemEntity(int seqNo, long paymentTotalId, DishEntity dishEntity){
        this.posSerialChild = seqNo+"";
        this.dishCode = dishEntity.getDishCode().trim();
        this.dishName = dishEntity.getDishName().trim();
        this.price = dishEntity.getPrice();
        this.quantity = dishEntity.getCount();
        this.transMoney = dishEntity.getCount()* dishEntity.getPrice();
        this.timestamp = System.currentTimeMillis();
        this.paymentTotalId = paymentTotalId;
    }

    @Generated(hash = 558064288)
    public PaymentItemEntity() {
    }

    public PaymentItemEntity(int subSeqNo,String dishCode,String dishName,int quantity,float price) {
        this.posSerialChild = subSeqNo+"";
        this.dishCode = dishCode;
        this.dishName = dishName;
        this.price = price;
        this.quantity = quantity;
        this.transMoney = price*quantity;
        this.timestamp = System.currentTimeMillis();
    }

}
