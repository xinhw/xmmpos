package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

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
public class PaymentItem implements Comparable<PaymentItem>{
    @Id(autoincrement = true)
    Long id;

    int posSerialChild;
    String dishCode;
    String dishName;
    int price;
    int quantity;
    int transMoney;
    long timestamp;

    @JSONField(serialize = false)
    long paymentTotalId;

    public PaymentItem(){
        timestamp = System.currentTimeMillis();
    }

    @Generated(hash = 2020225571)
    public PaymentItem(Long id, int posSerialChild, String dishCode,
            String dishName, int price, int quantity, int transMoney,
            long timestamp, long paymentTotalId) {
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

    public int getPosSerialChild() {
        return posSerialChild;
    }

    public void setPosSerialChild(int posSerialChild) {
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTransMoney() {
        return transMoney;
    }

    public void setTransMoney(int transMoney) {
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
    public int compareTo(PaymentItem o) {
        int ret = (int)(this.getPaymentTotalId()-o.getPaymentTotalId());
        if(ret!=0) return ret;

        ret = this.getPosSerialChild() - o.getPosSerialChild();
        return ret;
    }
}
