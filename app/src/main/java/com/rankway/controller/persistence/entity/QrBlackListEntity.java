package com.rankway.controller.persistence.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/10/02
 *   desc  : 二维码黑名单
 *   version: 1.0
 * </pre>
 */
@Entity
public class QrBlackListEntity {
    @Id(autoincrement = true)
    Long id;

    int systemId;
    int qrType;
    String userId;
    String department;
    String gno;
    int statusId;

    long timestamp;

    @Generated(hash = 1178520065)
    public QrBlackListEntity(Long id, int systemId, int qrType, String userId,
            String department, String gno, int statusId, long timestamp) {
        this.id = id;
        this.systemId = systemId;
        this.qrType = qrType;
        this.userId = userId;
        this.department = department;
        this.gno = gno;
        this.statusId = statusId;
        this.timestamp = timestamp;
    }

    @Generated(hash = 1381681919)
    public QrBlackListEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSystemId() {
        return systemId;
    }

    public void setSystemId(int systemId) {
        this.systemId = systemId;
    }

    public int getQrType() {
        return qrType;
    }

    public void setQrType(int qrType) {
        this.qrType = qrType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getGno() {
        return gno;
    }

    public void setGno(String gno) {
        this.gno = gno;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "QrUserEntity{" +
                "id=" + id +
                ", systemId=" + systemId +
                ", qrType=" + qrType +
                ", userId='" + userId + '\'' +
                ", department='" + department + '\'' +
                ", gno='" + gno + '\'' +
                ", statusId=" + statusId +
                ", timestamp=" + timestamp +
                '}';
    }
}
