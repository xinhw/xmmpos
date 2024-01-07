package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rankway.controller.persistence.gen.DaoSession;
import com.rankway.controller.persistence.gen.PaymentRecordDao;
import com.rankway.controller.persistence.gen.PaymentShiftEntityDao;

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
 *   time  : 2024/01/07
 *   desc  :
 *   version: 1.0
 * </pre>
 */
@Entity
public class PaymentShiftEntity
        implements Comparable<PaymentShiftEntity>{
    @JSONField(serialize = false)
    @Id(autoincrement = true)
    Long id;

    String posNo;           //  POS机号
    String operatorNo;      //  操作员号

    long shiftOnAuditNo;    //  开班流水号
    long shiftOnTime;       //  开班时间

    long shiftOffAuditNo;   //  结班流水号
    long shiftOffTime;      //  结班时间

    long reportTime;        //  报告时间

    int subCardCount;       //  IC卡消费次数
    long subCardAmount;     //  IC卡消费合计金额

    int subQrCount;         //  二维码消费次数
    long subQrAmount;       //  二维吗消费金额

    int status;             //  状态

    String shiftNo;         //  班次号

    @JSONField(serialize = false)
    @ToMany(referencedJoinProperty= "shiftId")
    List<PaymentRecord> recordList;

    public static final int SHIFT_STATUS_ON = 1;                //  开班未结班
    public static final int SHIFT_STATUS_OFF = 2;                //  结班未上传
    public static final int SHIFT_STATUS_OFF_UPLOADED = 0;       //  结班已上传

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1607554536)
    private transient PaymentShiftEntityDao myDao;

    @Generated(hash = 1106537593)
    public PaymentShiftEntity(Long id, String posNo, String operatorNo,
            long shiftOnAuditNo, long shiftOnTime, long shiftOffAuditNo,
            long shiftOffTime, long reportTime, int subCardCount,
            long subCardAmount, int subQrCount, long subQrAmount, int status,
            String shiftNo) {
        this.id = id;
        this.posNo = posNo;
        this.operatorNo = operatorNo;
        this.shiftOnAuditNo = shiftOnAuditNo;
        this.shiftOnTime = shiftOnTime;
        this.shiftOffAuditNo = shiftOffAuditNo;
        this.shiftOffTime = shiftOffTime;
        this.reportTime = reportTime;
        this.subCardCount = subCardCount;
        this.subCardAmount = subCardAmount;
        this.subQrCount = subQrCount;
        this.subQrAmount = subQrAmount;
        this.status = status;
        this.shiftNo = shiftNo;
    }

    @Generated(hash = 161782890)
    public PaymentShiftEntity() {
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

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public long getShiftOnAuditNo() {
        return shiftOnAuditNo;
    }

    public void setShiftOnAuditNo(long shiftOnAuditNo) {
        this.shiftOnAuditNo = shiftOnAuditNo;
    }

    public long getShiftOnTime() {
        return shiftOnTime;
    }

    public void setShiftOnTime(long shiftOnTime) {
        this.shiftOnTime = shiftOnTime;
    }

    public long getShiftOffAuditNo() {
        return shiftOffAuditNo;
    }

    public void setShiftOffAuditNo(long shiftOffAuditNo) {
        this.shiftOffAuditNo = shiftOffAuditNo;
    }

    public long getShiftOffTime() {
        return shiftOffTime;
    }

    public void setShiftOffTime(long shiftOffTime) {
        this.shiftOffTime = shiftOffTime;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public int getSubCardCount() {
        return subCardCount;
    }

    public void setSubCardCount(int subCardCount) {
        this.subCardCount = subCardCount;
    }

    public long getSubCardAmount() {
        return subCardAmount;
    }

    public void setSubCardAmount(long subCardAmount) {
        this.subCardAmount = subCardAmount;
    }

    public int getSubQrCount() {
        return subQrCount;
    }

    public void setSubQrCount(int subQrCount) {
        this.subQrCount = subQrCount;
    }

    public long getSubQrAmount() {
        return subQrAmount;
    }

    public void setSubQrAmount(long subQrAmount) {
        this.subQrAmount = subQrAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getShiftNo() {
        return shiftNo;
    }

    public void setShiftNo(String shiftNo) {
        this.shiftNo = shiftNo;
    }

    @Override
    public int compareTo(PaymentShiftEntity o) {
        return (int)(o.getId()-this.getId());
    }

    public int getTotalCount(){
        return subCardCount+subQrCount;
    }

    public long getTotalAmount(){
        return subCardAmount+subQrAmount;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 2086203964)
    public List<PaymentRecord> getRecordList() {
        if (recordList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PaymentRecordDao targetDao = daoSession.getPaymentRecordDao();
            List<PaymentRecord> recordListNew = targetDao
                    ._queryPaymentShiftEntity_RecordList(id);
            synchronized (this) {
                if (recordList == null) {
                    recordList = recordListNew;
                }
            }
        }
        return recordList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1700181837)
    public synchronized void resetRecordList() {
        recordList = null;
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
    @Generated(hash = 1350655928)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPaymentShiftEntityDao() : null;
    }
}
