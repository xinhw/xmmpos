package com.rankway.controller.pushmessage;

public class PushMessageResponse {
    private int messageType;
    private String token;
    private long timeStamp;
    private String response;

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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "PushMessageResponse{" +
                "messageType=" + messageType +
                ", token='" + token + '\'' +
                ", timeStamp=" + timeStamp +
                ", response='" + response + '\'' +
                '}';
    }

    public PushMessageResponse(){
        setTimeStamp(System.currentTimeMillis());
    }

    public PushMessageResponse(PushMessageRequest req){
        setMessageType(req.getMessageType());
        setToken(req.getToken());
        setTimeStamp(System.currentTimeMillis());
    }
}
