package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.rankway.controller.persistence.gen.DaoSession;
import com.rankway.controller.persistence.gen.DishDao;
import com.rankway.controller.persistence.gen.DishTypeDao;

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
public class DishType {
    @Id(autoincrement = true)
    Long id;

    String dishTypeCode;
    String dishTypeName;
    int satus;

    @ToMany(referencedJoinProperty = "typeId")
    List<Dish> dishTransRecordDatas;

    @JSONField(serialize = false)
    long timestamp;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1586670420)
    private transient DishTypeDao myDao;

    public DishType(){
        timestamp = System.currentTimeMillis();
    }

    @Generated(hash = 1991250913)
    public DishType(Long id, String dishTypeCode, String dishTypeName, int satus,
            long timestamp) {
        this.id = id;
        this.dishTypeCode = dishTypeCode;
        this.dishTypeName = dishTypeName;
        this.satus = satus;
        this.timestamp = timestamp;
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

    public int getSatus() {
        return satus;
    }

    public void setSatus(int satus) {
        this.satus = satus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "DishType{" +
                "id=" + id +
                ", dishTypeCode='" + dishTypeCode + '\'' +
                ", dishTypeName='" + dishTypeName + '\'' +
                ", satus=" + satus +
                ", timestamp=" + timestamp +
                ", dishTransRecordDatas=" + dishTransRecordDatas +
                '}';
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1935461277)
    public List<Dish> getDishTransRecordDatas() {
        if (dishTransRecordDatas == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DishDao targetDao = daoSession.getDishDao();
            List<Dish> dishTransRecordDatasNew = targetDao
                    ._queryDishType_DishTransRecordDatas(id);
            synchronized (this) {
                if (dishTransRecordDatas == null) {
                    dishTransRecordDatas = dishTransRecordDatasNew;
                }
            }
        }
        return dishTransRecordDatas;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1988031716)
    public synchronized void resetDishTransRecordDatas() {
        dishTransRecordDatas = null;
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
    @Generated(hash = 805740506)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDishTypeDao() : null;
    }
}
