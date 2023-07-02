package com.rankway.controller.pushmessage;

public class PushMessageRequest {
    private int messageType;
    private String token;
    private String from;
    private long timeStamp;
    private String content;

    public PushMessageRequest(){
        this.timeStamp = System.currentTimeMillis();
    }
    public PushMessageRequest(int type,String token,String from,String content){
        this.messageType = type;
        this.token=token;
        this.from = from;
        this.content = content;
        this.timeStamp = System.currentTimeMillis();
    }
    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String context) {
        this.content = context;
    }

    @Override
    public String toString() {
        return "PushMessageRequest{" +
                "messageType=" + messageType +
                ", token='" + token + '\'' +
                ", from='" + from + '\'' +
                ", timeStamp=" + timeStamp +
                ", context='" + content + '\'' +
                '}';
    }
}
