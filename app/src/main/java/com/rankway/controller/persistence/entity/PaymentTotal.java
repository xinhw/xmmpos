package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rankway.controller.persistence.gen.DaoSession;
import com.rankway.controller.persistence.gen.PaymentItemDao;
import com.rankway.controller.persistence.gen.PaymentTotalDao;

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
 *   time  : 2023/09/14
 *   desc  :
 *   version: 1.0
 * </pre>
 */
@Entity
public class PaymentTotal implements Comparable<PaymentTotal>{
    @Id(autoincrement = true)
    Long id;

    String posNo;
    long posSerial;
    String transLocalTime;
    String siteVersion;

    @JSONField(serialize = false)
    long recordId;

    @ToMany(referencedJoinProperty = "paymentTotalId")
    List<PaymentItem> dishTransRecordDatas;

    @JSONField(serialize = false)
    int uploadFlag;

    @JSONField(serialize = false)
    long timestamp;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1699123541)
    private transient PaymentTotalDao myDao;

    public PaymentTotal(){
        timestamp = System.currentTimeMillis();
        uploadFlag = 0;
    }

    @Generated(hash = 1357667251)
    public PaymentTotal(Long id, String posNo, long posSerial,
            String transLocalTime, String siteVersion, long recordId,
            int uploadFlag, long timestamp) {
        this.id = id;
        this.posNo = posNo;
        this.posSerial = posSerial;
        this.transLocalTime = transLocalTime;
        this.siteVersion = siteVersion;
        this.recordId = recordId;
        this.uploadFlag = uploadFlag;
        this.timestamp = timestamp;
    }

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

    public long getPosSerial() {
        return posSerial;
    }

    public void setPosSerial(long posSerial) {
        this.posSerial = posSerial;
    }

    public String getTransLocalTime() {
        return transLocalTime;
    }

    public void setTransLocalTime(String transLocalTime) {
        this.transLocalTime = transLocalTime;
    }

    public String getSiteVersion() {
        return siteVersion;
    }

    public void setSiteVersion(String siteVersion) {
        this.siteVersion = siteVersion;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }
    

    public int getUploadFlag() {
        return uploadFlag;
    }

    public void setUploadFlag(int uploadFlag) {
        this.uploadFlag = uploadFlag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PaymentTotal{" +
                "id=" + id +
                ", posNo='" + posNo + '\'' +
                ", posSerial=" + posSerial +
                ", transLocalTime='" + transLocalTime + '\'' +
                ", siteVersion='" + siteVersion + '\'' +
                ", recordId=" + recordId +
                ", dishTransRecordDatas=" + dishTransRecordDatas +
                ", uploadFlag=" + uploadFlag +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public int compareTo(PaymentTotal o) {
        return (int)(this.getId()-o.getId());
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1209227958)
    public List<PaymentItem> getDishTransRecordDatas() {
        if (dishTransRecordDatas == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PaymentItemDao targetDao = daoSession.getPaymentItemDao();
            List<PaymentItem> dishTransRecordDatasNew = targetDao
                    ._queryPaymentTotal_DishTransRecordDatas(id);
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
    @Generated(hash = 335135841)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPaymentTotalDao() : null;
    }
}
