package com.rankway.controller.persistence.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * <pre>
 *   author : Xin Hongwei
 *   e-mail : xinhw@wxsemicon.com
 *   time  : 2023/10/03
 *   desc  :
 *   version: 1.0
 * </pre>
 */
@Entity
public class PersonInfoEntity {
    @Id(autoincrement = true)
    Long id;

    int cardno;
    String gsno;
    String gname;
    String gsex;
    String gdeptname;
    String deptId;
    int StatusId;
    String gno;

    long timestamp;

    @Generated(hash = 1367480891)
    public PersonInfoEntity(Long id, int cardno, String gsno, String gname,
            String gsex, String gdeptname, String deptId, int StatusId, String gno,
            long timestamp) {
        this.id = id;
        this.cardno = cardno;
        this.gsno = gsno;
        this.gname = gname;
        this.gsex = gsex;
        this.gdeptname = gdeptname;
        this.deptId = deptId;
        this.StatusId = StatusId;
        this.gno = gno;
        this.timestamp = timestamp;
    }

    @Generated(hash = 958033728)
    public PersonInfoEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCardno() {
        return cardno;
    }

    public void setCardno(int cardno) {
        this.cardno = cardno;
    }

    public String getGsno() {
        return gsno;
    }

    public void setGsno(String gsno) {
        this.gsno = gsno;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getGsex() {
        return gsex;
    }

    public void setGsex(String gsex) {
        this.gsex = gsex;
    }

    public String getGdeptname() {
        return gdeptname;
    }

    public void setGdeptname(String gdeptname) {
        this.gdeptname = gdeptname;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public int getStatusId() {
        return StatusId;
    }

    public void setStatusId(int statusId) {
        StatusId = statusId;
    }

    public String getGno() {
        return gno;
    }

    public void setGno(String gno) {
        this.gno = gno;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return "PersonInfoEntity{" +
                "id=" + id +
                ", cardno=" + cardno +
                ", gsno='" + gsno + '\'' +
                ", gname='" + gname + '\'' +
                ", gsex='" + gsex + '\'' +
                ", gdeptname='" + gdeptname + '\'' +
                ", deptId='" + deptId + '\'' +
                ", StatusId=" + StatusId +
                ", gno='" + gno + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
