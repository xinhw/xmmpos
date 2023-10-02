package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Transient;
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
public class DishEntity {
    @Id(autoincrement = true)
    Long id;

    String dishCode;
    String dishName;
    int price;
    String status;

    long typeId;

    @Transient
    int count;

    @JSONField(serialize = false)
    long timestamp;

    public DishEntity(){
        this.count = 1;
        this.timestamp = System.currentTimeMillis();
    }

    @Keep
    public DishEntity(Long id, String dishCode, String dishName, int price, String status,
                      long typeId, long timestamp) {
        this.id = id;
        this.dishCode = dishCode;
        this.dishName = dishName;
        this.price = price;
        this.status = status;
        this.typeId = typeId;
        this.timestamp = timestamp;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", dishCode='" + dishCode + '\'' +
                ", dishName='" + dishName + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", typeId=" + typeId +
                ", count=" + count +
                ", timestamp=" + timestamp +
                '}';
    }
}
