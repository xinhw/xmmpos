package com.rankway.controller.persistence.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

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
public class UserInfoEntity {
    @Id(autoincrement = true)
    Long id;

    String userCode;
    String userName;
    String userPassword;
    String userNote;
    int optType;
    String sysScope;

    long timestamp;

    @Generated(hash = 993135169)
    public UserInfoEntity(Long id, String userCode, String userName,
            String userPassword, String userNote, int optType, String sysScope,
            long timestamp) {
        this.id = id;
        this.userCode = userCode;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userNote = userNote;
        this.optType = optType;
        this.sysScope = sysScope;
        this.timestamp = timestamp;
    }

    @Generated(hash = 2042969639)
    public UserInfoEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserNote() {
        return this.userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }

    public int getOptType() {
        return this.optType;
    }

    public void setOptType(int optType) {
        this.optType = optType;
    }

    public String getSysScope() {
        return this.sysScope;
    }

    public void setSysScope(String sysScope) {
        this.sysScope = sysScope;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
