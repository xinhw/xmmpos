package com.rankway.controller.activity.project.eventbus;

public class MessageEvent {
    private String title;
    private String message;
    private int type;

    public final static int TYPE_MESSAGE = 0;
    public final static int TYPE_NOTIFICATION = 1;

    public MessageEvent(int type,String title,String message) {
        this.type = type;
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
