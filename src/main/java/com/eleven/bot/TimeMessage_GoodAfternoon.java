package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.eleven.bot.PostMessage.postMessage;

public class TimeMessage_GoodAfternoon implements TimeMessage{
    Calendar curTime;
    int triggerHour;
    int triggerMinute;
    Long target;
    String sessionKey;
    ArrayList<String> timeMessages = new ArrayList<>();

    public TimeMessage_GoodAfternoon(int hour, int minute, Long target, String sessionKey) {
        this.curTime = Calendar.getInstance();
        this.triggerHour = hour;
        this.triggerMinute = minute;
        this.target = target;
        this.sessionKey = sessionKey;
        timeMessages.add("喂！三点钟了！喝杯茶吧！");
    }

    @Override
    public void handle() {
        this.curTime = Calendar.getInstance();
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

    private String randomRespond() {
        int randomIndex = (int) (Math.random() * timeMessages.size());
        return timeMessages.get(randomIndex);
    }
}
