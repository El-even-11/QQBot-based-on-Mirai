package com.eleven.bot;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.eleven.bot.Bot.sessionKey;
import static com.eleven.bot.PostMessage.buildMessageChain;
import static com.eleven.bot.PostMessage.postMessage;

public class TimeMessage {
    private final Long target;
    private final String type;
    private final int hour;
    private final int minute;
    private final int second;

    private List<String> texts = new ArrayList<>();
    private List<String> urls = new ArrayList<>();

    private Calendar curTime;

    public TimeMessage(Long target, String type, int hour, int minute) {
        this.target = target;
        this.type = type;
        this.hour = hour;
        this.minute = minute;
        second = (int) (Math.random() * 60);
    }

    public TimeMessage(Long target, String type, int hour, int minute, List<String> texts, List<String> urls) {
        this.target = target;
        this.type = type;
        this.texts = texts;
        this.urls = urls;
        this.hour = hour;
        this.minute = minute;
        second = (int) (Math.random() * 60);
    }

    public void addText(String text) {
        texts.add(text);
    }

    public void addUrls(String url) {
        urls.add(url);
    }

    public void updateTime(Calendar curTime) {
        this.curTime = curTime;
    }


    public void sendMessage() {
        if (curTime.get(Calendar.HOUR_OF_DAY) == hour && curTime.get(Calendar.MINUTE) == minute && curTime.get(Calendar.SECOND) == second) {
            List<JSONObject> messageChains = new ArrayList<>();
            int randomIndex = (int) (Math.random() * texts.size());
            messageChains.add(buildMessageChain(texts.get(randomIndex), urls.get(randomIndex)));
            postMessage(messageChains, type, target, sessionKey);
        }
    }
}
