package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rankway.controller.persistence.gen.DaoSession;
import com.rankway.controller.persistence.gen.DishEntityDao;
import com.rankway.controller.persistence.gen.DishSubTypeEntityDao;
import com.rankway.controller.webapi.menu.DishSubType;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/10/14
 *   desc  :
 *   version: 1.0
 * </pre>
 */
@Entity
public class DishSubTypeEntity {
    @Id(autoincrement = true)
    Long id;

    String dishSubTypeCode;
    String dishSubTypeName;
    int status;

    @JSONField(serialize = false)
    long timestamp;

    long typeId;

    @ToMany(referencedJoinProperty = "subTypeId")
    List<DishEntity> dishs;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 242517488)
    private transient DishSubTypeEntityDao myDao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDishSubTypeCode() {
        return dishSubTypeCode;
    }

    public void setDishSubTypeCode(String dishSubTypeCode) {
        this.dishSubTypeCode = dishSubTypeCode;
    }

    public String getDishSubTypeName() {
        return dishSubTypeName;
    }

    public void setDishSubTypeName(String dishSubTypeName) {
        this.dishSubTypeName = dishSubTypeName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    @Override
    public String toString() {
        return "DishSubTypeEntity{" +
                "id=" + id +
                ", dishSubTypeCode='" + dishSubTypeCode + '\'' +
                ", dishSubTypeName='" + dishSubTypeName + '\'' +
                ", status=" + status +
                ", timestamp=" + timestamp +
                ", typeId=" + typeId +
                ", dishs=" + dishs +
                '}';
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1193417146)
    public List<DishEntity> getDishs() {
        if (dishs == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DishEntityDao targetDao = daoSession.getDishEntityDao();
            List<DishEntity> dishsNew = targetDao._queryDishSubTypeEntity_Dishs(id);
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
    @Generated(hash = 741075963)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDishSubTypeEntityDao() : null;
    }

    public DishSubTypeEntity(DishTypeEntity dishType,DishSubType subtype){
        this.dishSubTypeCode = subtype.getDishSubTypeCode();
        this.dishSubTypeName = subtype.getDishSubTypeName();
        this.status = subtype.getStatus();
        this.typeId = dishType.getId();
    }

    @Generated(hash = 507562619)
    public DishSubTypeEntity(Long id, String dishSubTypeCode,
            String dishSubTypeName, int status, long timestamp, long typeId) {
        this.id = id;
        this.dishSubTypeCode = dishSubTypeCode;
        this.dishSubTypeName = dishSubTypeName;
        this.status = status;
        this.timestamp = timestamp;
        this.typeId = typeId;
    }

    @Generated(hash = 1802282142)
    public DishSubTypeEntity() {
    }

    public DishSubTypeEntity(String code,String name,DishTypeEntity dishTypeEntity) {
        this.dishSubTypeCode = code;
        this.dishSubTypeName = name;
        this.typeId = dishTypeEntity.getId();
        this.status = 0;
        this.timestamp = System.currentTimeMillis();
    }
}
