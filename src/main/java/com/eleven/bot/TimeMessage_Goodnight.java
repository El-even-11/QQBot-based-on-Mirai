package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.eleven.bot.PostMessage.postMessage;

public class TimeMessage_Goodnight implements TimeMessage {

    Calendar curTime;
    int triggerHour;
    int triggerMinute;
    Long target;
    String sessionKey;
    ArrayList<String> timeMessages = new ArrayList<>();

    public TimeMessage_Goodnight(int hour, int minute, Long target, String sessionKey) {
        this.curTime = Calendar.getInstance();
        this.triggerHour = hour;
        this.triggerMinute = minute;
        this.target = target;
        this.sessionKey = sessionKey;
        timeMessages.add("十二点啦，大家该睡觉觉啦！");
    }

    @Override
    public void handle() {
        if (isTriggerTime()) {
            List<JSONArray> messages = new ArrayList<>();

            JSONObject message = new JSONObject();
            message.put("type", "Plain");
            message.put("text", randomRespond());
            JSONArray textArray = new JSONArray();
            textArray.add(message);
            messages.add(textArray);

            List<JSONObject> messageChains = new ArrayList<>();
            for (JSONArray i : messages) {
                JSONObject messageChain = new JSONObject();
                messageChain.put("messageChain", i);
                messageChains.add(messageChain);
            }
            postMessage(messageChains, "Group", target, sessionKey);
        }
    }

    @Override
    public boolean isTriggerTime() {
        return triggerHour == curTime.get(Calendar.HOUR_OF_DAY) && triggerMinute == curTime.get(Calendar.MINUTE) && curTime.get(Calendar.SECOND) == 0;
    }

    @Override
    public void updateTime(Calendar curTime) {
        this.curTime = curTime;
    }

    private String randomRespond() {
        int randomIndex = (int) (Math.random() * timeMessages.size());
        return timeMessages.get(randomIndex);
    }
}
