package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Trigger_Goodnight implements Trigger {

    ArrayList<String> responses = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();

    public Trigger_Goodnight() {
        responses.add("我要乔装成一颗小奶糖，夜深了提着星星灯，快快溜到你的梦里说晚安");
        responses.add("天黑请闭眼");
        responses.add("偷偷向银河要了一把碎星，等你闭上眼睛撒进梦里，晚安");
        responses.add("晚安，月亮，别忘记保护我");
        responses.add("说了晚安，就不可以和别的小朋友聊天了哦");
        responses.add("说不定睡着才是常态，醒着是为了收集做梦的素材");
        responses.add("晚安，全世界");
        responses.add("月亮今晚没营业，我代替它和你说晚安");
        responses.add("明天再喜欢你吧，我今天太困了");
        responses.add("我要带着可爱打烊了");
        responses.add("晚睡的人会被月亮抓起来罚站！");

        images.add("http://gchat.qpic.cn/gchatpic_new/740614810/3807109237-2735039276-37D99D536E7025C54349BBC693E790CF/0?term=2");
        images.add("http://c2cpicdw.qpic.cn/offpic_new/740614810//740614810-453292542-039AF9977548876AA6475D6902C22EB6/0?term=2");
        images.add("http://c2cpicdw.qpic.cn/offpic_new/740614810//740614810-3362964231-6307406962404F4094781F49203DCD30/0?term=2");
        images.add("http://c2cpicdw.qpic.cn/offpic_new/740614810//740614810-771844593-82B60C143D06AB2B55BB49670415375A/0?term=2");
        images.add("http://c2cpicdw.qpic.cn/offpic_new/740614810//740614810-3093400815-E355F2242FEC1E5DBBAAE297B10D7DA0/0?term=2");
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

        //add image
        JSONObject triggerImage = new JSONObject();
        triggerImage.put("type", "Image");
        triggerImage.put("url", randomImage());
        JSONArray imageArray = new JSONArray();
        imageArray.add(triggerImage);

        //messageChains
        triggerResponses.add(textArray);
        triggerResponses.add(imageArray);
        return triggerResponses;
    }

    private String randomRespond() {
        int randomIndex = (int) (Math.random() * responses.size());
        return responses.get(randomIndex);
    }

    private String randomImage() {
        int randomIndex = (int) (Math.random() * images.size());
        return images.get(randomIndex);
    }
}
