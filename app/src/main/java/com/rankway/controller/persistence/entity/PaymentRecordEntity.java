package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rankway.controller.dto.PosInfoBean;
import com.rankway.controller.persistence.gen.DaoSession;
import com.rankway.controller.persistence.gen.PaymentRecordEntityDao;
import com.rankway.controller.persistence.gen.PaymentTotalDao;
import com.rankway.controller.webapi.cardInfo;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2022/12/04
 *   desc  : 支付记录（对应支付商品统计PaymentTotal记录）
 *   version: 1.0
 * </pre>
 */
@Entity
public class PaymentRecordEntity implements Comparable<PaymentRecordEntity>{
    @Id(autoincrement = true)
    Long id;

    int auditNo;        //  本地流水号
    String posNo;       //  POS号
    int postype;        //  POS类型，0
    int payway;         //  支付方式，2
    String userCode;    //  操作员
    int cardno;         //  卡号
    float remain;       //  交易前余额
    float amount;       //  交易金额
    float balance;      //  交易后余额
    int typeid;         //  交易类型 100
    Date transTime;     //  本地时间

    String cardSNO;     //  卡唯一号
    String workNo;      //  工号
    String workName;    //  姓名

    String userId;      //  二维码UserId
    int qrType;         //  二维码类型
    int systemId;       //  二维码系统Id

    int uploadFlag;     //  上传标志
    Date uploadTime;    //  上传时间

    long paymentTotalId;
    @JSONField(serialize = false)
    @ToOne(joinProperty = "paymentTotalId")
    PaymentTotal total;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1700074390)
    private transient PaymentRecordEntityDao myDao;

    @Generated(hash = 1707628587)
    private transient Long total__resolvedKey;

    @Generated(hash = 2058187595)
    public PaymentRecordEntity(Long id, int auditNo, String posNo, int postype, int payway, String userCode,
            int cardno, float remain, float amount, float balance, int typeid, Date transTime, String cardSNO,
            String workNo, String workName, String userId, int qrType, int systemId, int uploadFlag,
            Date uploadTime, long paymentTotalId) {
        this.id = id;
        this.auditNo = auditNo;
        this.posNo = posNo;
        this.postype = postype;
        this.payway = payway;
        this.userCode = userCode;
        this.cardno = cardno;
        this.remain = remain;
        this.amount = amount;
        this.balance = balance;
        this.typeid = typeid;
        this.transTime = transTime;
        this.cardSNO = cardSNO;
        this.workNo = workNo;
        this.workName = workName;
        this.userId = userId;
        this.qrType = qrType;
        this.systemId = systemId;
        this.uploadFlag = uploadFlag;
        this.uploadTime = uploadTime;
        this.paymentTotalId = paymentTotalId;
    }

    @Keep
    public PaymentRecordEntity() {
        this.typeid = 100;
        this.payway = 2;
        this.postype = 0;
        this.transTime = new Date();
    }

    public PaymentRecordEntity(cardInfo card, float amount, PosInfoBean pos){
        this.auditNo = pos.getAuditNo();
        this.posNo = pos.getCposno();
        this.postype = 0;
        this.payway = 2;
        this.userCode = pos.getUsercode();

        this.cardno = card.getCardno();
        this.remain = card.getGremain();
        this.amount = amount;
        this.balance = card.getGremain()-amount;
        this.typeid = 100;
        this.transTime = new Date();
        this.workNo = card.getGno();
        this.workName = card.getName();

        this.userId = card.getUserId();
        this.qrType = card.getQrType();
        this.systemId = card.getSystemId();

        this.uploadFlag = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAuditNo() {
        return auditNo;
    }

    public void setAuditNo(int auditNo) {
        this.auditNo = auditNo;
    }

    public String getPosNo() {
        return posNo;
    }

    public void setPosNo(String posNo) {
        this.posNo = posNo;
    }

    public int getPostype() {
        return postype;
    }

    public void setPostype(int postype) {
        this.postype = postype;
    }

    public int getPayway() {
        return payway;
    }

    public void setPayway(int payway) {
        this.payway = payway;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public int getCardno() {
        return cardno;
    }

    public void setCardno(int cardno) {
        this.cardno = cardno;
    }

    public float getRemain() {
        return remain;
    }

    public void setRemain(float remain) {
        this.remain = remain;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public Date getTransTime() {
        return transTime;
    }

    public void setTransTime(Date transTime) {
        this.transTime = transTime;
    }

    public String getCardSNO() {
        return cardSNO;
    }

    public void setCardSNO(String cardSNO) {
        this.cardSNO = cardSNO;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getQrType() {
        return qrType;
    }

    public void setQrType(int qrType) {
        this.qrType = qrType;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public int getUploadFlag() {
        return uploadFlag;
    }

    public void setUploadFlag(int uploadFlag) {
        this.uploadFlag = uploadFlag;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    @Override
    public String toString() {
        return "PaymentRecord{" +
                "id=" + id +
                ", auditNo=" + auditNo +
                ", posNo='" + posNo + '\'' +
                ", postype=" + postype +
                ", payway=" + payway +
                ", userCode='" + userCode + '\'' +
                ", cardno=" + cardno +
                ", remain=" + remain +
                ", amount=" + amount +
                ", balance=" + balance +
                ", typeid=" + typeid +
                ", transTime=" + transTime +
                ", cardSNO='" + cardSNO + '\'' +
                ", workNo='" + workNo + '\'' +
                ", workName='" + workName + '\'' +
                ", userId='" + userId + '\'' +
                ", qrType=" + qrType +
                ", systemId=" + systemId +
                ", uploadFlag=" + uploadFlag +
                ", uploadTime=" + uploadTime +
                '}';
    }

    @Override
    public int compareTo(PaymentRecordEntity o) {
        return (int)(this.id-o.getId());
    }

    public long getPaymentTotalId() {
        return this.paymentTotalId;
    }

    public void setPaymentTotalId(long paymentTotalId) {
        this.paymentTotalId = paymentTotalId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1555494400)
    public PaymentTotal getTotal() {
        long __key = this.paymentTotalId;
        if (total__resolvedKey == null || !total__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PaymentTotalDao targetDao = daoSession.getPaymentTotalDao();
            PaymentTotal totalNew = targetDao.load(__key);
            synchronized (this) {
                total = totalNew;
                total__resolvedKey = __key;
            }
        }
        return total;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2114216022)
    public void setTotal(@NotNull PaymentTotal total) {
        if (total == null) {
            throw new DaoException(
                    "To-one property 'paymentTotalId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.total = total;
            paymentTotalId = total.getId();
            total__resolvedKey = paymentTotalId;
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
    @Generated(hash = 1205506751)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPaymentRecordEntityDao() : null;
    }

    public PaymentRecordEntity(PaymentRecordEntity record){
        this.id = record.getId();
        this.auditNo = record.getAuditNo();
        this.posNo = record.getPosNo();
        this.postype = record.getPostype();
        this.payway = record.getPayway();
        this.userCode = record.getUserCode();
        this.cardno = record.getCardno();
        this.remain = record.getRemain();
        this.amount = record.getAmount();
        this.balance = record.getBalance();
        this.typeid = record.getTypeid();
        this.transTime = record.getTransTime();
        this.cardSNO = record.getCardSNO();
        this.workNo = record.getWorkNo();
        this.workName = record.getWorkName();
        this.userId = record.getUserId();
        this.qrType = record.getQrType();
        this.systemId = record.getSystemId();
        this.uploadFlag = record.getUploadFlag();
        this.uploadTime = record.getUploadTime();
        this.paymentTotalId = record.getPaymentTotalId();
    }
}
