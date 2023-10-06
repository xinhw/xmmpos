package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rankway.controller.persistence.gen.DaoSession;
import com.rankway.controller.persistence.gen.DishEntityDao;
import com.rankway.controller.persistence.gen.DishTypeEntityDao;
import com.rankway.controller.webapi.menu.DishType;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

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
public class DishTypeEntity {
    @Id(autoincrement = true)
    Long id;

    String dishTypeCode;
    String dishTypeName;
    int status;

    @ToMany(referencedJoinProperty = "typeId")
    List<DishEntity> dishs;

    @JSONField(serialize = false)
    long timestamp;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 953140332)
    private transient DishTypeEntityDao myDao;

    public DishTypeEntity(){
        timestamp = System.currentTimeMillis();
    }

    @Keep
    public DishTypeEntity(Long id, String dishTypeCode, String dishTypeName, int satus,
                          long timestamp) {
        this.id = id;
        this.dishTypeCode = dishTypeCode;
        this.dishTypeName = dishTypeName;
        this.status = satus;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int satus) {
        this.status = satus;
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
                ", satus=" + status +
                ", timestamp=" + timestamp +
                ", dishs=" + dishs +
                '}';
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
    @Generated(hash = 1054791633)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDishTypeEntityDao() : null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1432046668)
    public List<DishEntity> getDishs() {
        if (dishs == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DishEntityDao targetDao = daoSession.getDishEntityDao();
            List<DishEntity> dishsNew = targetDao._queryDishTypeEntity_Dishs(id);
            synchronized (this) {
                if (dishs == null) {
                    dishs = dishsNew;
                }
            }
        }
        return dishs;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1915619626)
    public synchronized void resetDishs() {
        dishs = null;
    }

    public DishTypeEntity(DishTypeEntity obj){
        this.dishTypeCode = obj.getDishTypeCode().trim();
        this.dishTypeName = obj.getDishTypeName().trim();
        this.status = obj.getStatus();
        this.timestamp = System.currentTimeMillis();
    }

    public DishTypeEntity(DishType obj){
        this.dishTypeCode = obj.getDishTypeCode().trim();
        this.dishTypeName = obj.getDishTypeName().trim();
        this.status = obj.getStatus();
        this.timestamp = System.currentTimeMillis();
    }

    public DishTypeEntity(String dishTypeCode,String dishTypeName){
        this.dishTypeCode = dishTypeCode;
        this.dishTypeName = dishTypeName;

        this.status = 0;
        this.timestamp = System.currentTimeMillis();
    }
}
