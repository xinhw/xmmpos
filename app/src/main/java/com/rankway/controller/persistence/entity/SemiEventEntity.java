package com.rankway.controller.persistence.entity;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SemiEventEntity {
    @Id(autoincrement = true)
    Long id;
    long eventTime;
    int eventLevel;
    String process;
    long projectInfoId;
    String description;
    @JSONField(serialize = false)
    int status;
    
    @Generated(hash = 1537099763)
    public SemiEventEntity(Long id, long eventTime, int eventLevel, String process,
            long projectInfoId, String description, int status) {
        this.id = id;
        this.eventTime = eventTime;
        this.eventLevel = eventLevel;
        this.process = process;
        this.projectInfoId = projectInfoId;
        this.description = description;
        this.status = status;
    }
    @Generated(hash = 648187116)
    public SemiEventEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getEventTime() {
        return this.eventTime;
    }
    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
    public int getEventLevel() {
        return this.eventLevel;
    }
    public void setEventLevel(int eventLevel) {
        this.eventLevel = eventLevel;
    }
    public String getProcess() {
        return this.process;
    }
    public void setProcess(String process) {
        this.process = process;
    }
    public long getProjectInfoId() {
        return this.projectInfoId;
    }
    public void setProjectInfoId(long projectInfoId) {
        this.projectInfoId = projectInfoId;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SemiEventEntity{" +
                "id=" + id +
                ", eventTime=" + eventTime +
                ", eventLevel=" + eventLevel +
                ", process='" + process + '\'' +
                ", projectInfoId=" + projectInfoId +
                ", description='" + description + '\'' +
                '}';
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public SemiEventEntity(long proid,String process,int level,String desc) {
        this.eventTime = System.currentTimeMillis();
        this.projectInfoId = proid;
        this.eventLevel = level;
        this.process = process;
        this.description = desc;
        this.status = 0;
    }
}
