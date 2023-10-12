package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rankway.controller.persistence.gen.DaoSession;
import com.rankway.controller.persistence.gen.DishEntityDao;
import com.rankway.controller.persistence.gen.DishTypeEntityDao;
import com.rankway.controller.webapi.menu.Dish;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
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

    @JSONField(serialize = false)
    @ToOne(joinProperty = "typeId")
    DishTypeEntity dishType;

    @Transient
    int count;

    @JSONField(serialize = false)
    long timestamp;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 294918680)
    private transient DishEntityDao myDao;

    @Generated(hash = 202536655)
    private transient Long dishType__resolvedKey;

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

    public DishEntity(DishEntity obj){
        this.dishCode = obj.getDishCode().trim();
        this.dishName = obj.getDishName().trim();
        this.price = obj.getPrice();
        this.status = obj.getStatus();
        this.count = 1;
        this.timestamp = System.currentTimeMillis();
    }

    public DishEntity(String dishCode,String dishName,int price,long typeId){
        this.dishCode = dishCode;
        this.dishName = dishName;
        this.price = price;
        this.typeId = typeId;

        this.status="2";
        this.count = 1;
        this.timestamp = System.currentTimeMillis();
    }

    public int getSubAmount(){
        return this.count*this.price;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 922423377)
    public DishTypeEntity getDishType() {
        long __key = this.typeId;
        if (dishType__resolvedKey == null || !dishType__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DishTypeEntityDao targetDao = daoSession.getDishTypeEntityDao();
            DishTypeEntity dishTypeNew = targetDao.load(__key);
            synchronized (this) {
                dishType = dishTypeNew;
                dishType__resolvedKey = __key;
            }
        }
        return dishType;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1103436735)
    public void setDishType(@NotNull DishTypeEntity dishType) {
        if (dishType == null) {
            throw new DaoException(
                    "To-one property 'typeId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.dishType = dishType;
            typeId = dishType.getId();
            dishType__resolvedKey = typeId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 802256535)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDishEntityDao() : null;
    }


    public DishEntity(Dish obj){
        this.dishCode = obj.getDishCode().trim();
        this.dishName = obj.getDishName().trim();
        this.price = obj.getPrice();
        this.status = obj.getStatus();
        this.count = 1;
        this.timestamp = System.currentTimeMillis();
    }
}
