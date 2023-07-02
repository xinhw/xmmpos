package com.rankway.controller.persistence.entity;

import com.rankway.controller.pushmessage.PushMessageRequest;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class MessageDetail implements Comparable<MessageDetail>{
    @Id(autoincrement = true)
    Long id;

    private String title;
    private String content;
    private String from;
    private long time;
    private boolean breaded;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isBreaded() {
        return breaded;
    }

    public void setBreaded(boolean breaded) {
        this.breaded = breaded;
    }

    public MessageDetail(PushMessageRequest req){
        this.from = req.getFrom();
        this.content = req.getContent();
        this.title =  "通知";
        this.time = System.currentTimeMillis();     //req.getTimeStamp();
        this.breaded = false;
    }

    public MessageDetail(){
        this.time = System.currentTimeMillis();
    }

    @Generated(hash = 1372921175)
    public MessageDetail(Long id, String title, String content, String from,
                         long time, boolean breaded) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.from = from;
        this.time = time;
        this.breaded = breaded;
    }

    @Override
    public int compareTo(MessageDetail o) {
        return (int)(o.time - this.time);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getBreaded() {
        return this.breaded;
    }

    @Override
    public String toString() {
        return "MessageDetail{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", from='" + from + '\'' +
                ", time=" + time +
                ", breaded=" + breaded +
                '}';
    }
}