package com.eleven.bot;

import java.util.Calendar;

public class TimeMessage {
    private Long target;
    private String type;

    private String text;
    private String url;
    private int hour;
    private int minute;

    private Calendar curTime;

    public TimeMessage(Long target, String type, String text, String url, int hour, int minute) {
        this.target = target;
        this.type = type;
        this.text = text;
        this.url = url;
        this.hour = hour;
        this.minute = minute;
    }

    public void updateTime(Calendar curTime) {
        this.curTime = curTime;
    }

    public void sendMessage() {

    }
}
