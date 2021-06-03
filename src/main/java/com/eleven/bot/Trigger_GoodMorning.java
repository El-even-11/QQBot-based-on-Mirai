package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Trigger_GoodMorning implements Trigger {
    ArrayList<String> responses = new ArrayList<>();

    public Trigger_GoodMorning() {
        responses.add("早上好，早起的又是我们这几个可爱的人");
        responses.add("早上好，我把清晨的第一缕阳光送给你");
        responses.add("生活不会刁难可爱的人，早上好呀");
        responses.add("早晨愉快，一些小美好正在井然有序地发生");
        responses.add("今日份的快乐也正常营业啦，早上好呀");
        responses.add("今天也要像云朵一样自由");
    }

    @Override
    public List<JSONArray> formTriggerResponses() {
        List<JSONArray> triggerResponses = new ArrayList<>();

        //add text
        JSONObject triggerResponse = new JSONObject();
        triggerResponse.put("type", "Plain");
        triggerResponse.put("text", randomRespond());
        JSONArray textArray = new JSONArray();
        textArray.add(triggerResponse);

        //messageChains
        triggerResponses.add(textArray);
        return triggerResponses;
    }

    private String randomRespond() {
        int randomIndex = (int) (Math.random() * responses.size());
        return responses.get(randomIndex);
    }
}
