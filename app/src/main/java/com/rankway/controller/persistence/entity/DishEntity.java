package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rankway.controller.webapi.menu.Dish;
import com.rankway.controller.webapi.menu.DishType;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

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

    String dishTypeCode;
    String dishTypeName;

    @Transient
    int count;

    @JSONField(serialize = false)
    long timestamp;
    
    public DishEntity(){
        this.count = 1;
        this.timestamp = System.currentTimeMillis();
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

    public String getDishTypeCode() {
        return dishTypeCode;
    }

    public void setDishTypeCode(String dishTypeCode) {
        this.dishTypeCode = dishTypeCode;
    }

    public String getDishTypeName() {
        return dishTypeName;
    }

    public void setDishTypeName(String dishTypeName) {
        this.dishTypeName = dishTypeName;
    }

    public DishEntity(DishEntity obj){
        this.dishCode = obj.getDishCode().trim();
        this.dishName = obj.getDishName().trim();
        this.price = obj.getPrice();
        this.status = obj.getStatus();
        this.count = 1;
        this.timestamp = System.currentTimeMillis();
        this.dishTypeCode = obj.getDishTypeCode();
        this.dishTypeName = obj.getDishTypeName();
    }

    public DishEntity(String dishCode,String dishName,int price,DishTypeEntity dishTypeEntity){
        this.dishCode = dishCode;
        this.dishName = dishName;
        this.price = price;
        this.typeId = dishTypeEntity.getId();
        this.dishTypeCode = dishTypeEntity.getDishTypeCode();
        this.dishTypeName = dishTypeEntity.getDishTypeName();
        this.status="2";
        this.count = 1;
        this.timestamp = System.currentTimeMillis();
    }

    public int getSubAmount(){
        return this.count*this.price;
    }


    public DishEntity(DishType dishType, Dish obj){
        this.dishCode = obj.getDishCode().trim();
        this.dishName = obj.getDishName().trim();
        this.price = obj.getPrice();
        this.status = obj.getStatus();
        this.count = 1;
        this.timestamp = System.currentTimeMillis();
        this.dishTypeCode = dishType.getDishTypeCode();
        this.dishTypeName = dishType.getDishTypeName();
    }

    @Generated(hash = 1646385327)
    public DishEntity(Long id, String dishCode, String dishName, int price, String status, long typeId,
            String dishTypeCode, String dishTypeName, long timestamp) {
        this.id = id;
        this.dishCode = dishCode;
        this.dishName = dishName;
        this.price = price;
        this.status = status;
        this.typeId = typeId;
        this.dishTypeCode = dishTypeCode;
        this.dishTypeName = dishTypeName;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DishEntity{" +
                "id=" + id +
                ", dishCode='" + dishCode + '\'' +
                ", dishName='" + dishName + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", typeId=" + typeId +
                ", count=" + count +
                ", timestamp=" + timestamp +
                ", dishTypeCode='" + dishTypeCode + '\'' +
                ", dishTypeName='" + dishTypeName + '\'' +
                '}';
    }
}
