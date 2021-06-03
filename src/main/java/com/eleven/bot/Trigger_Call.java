package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Trigger_Call implements Trigger{
    ArrayList<String> images = new ArrayList<>();

    public Trigger_Call() {
        images.add("http://gchat.qpic.cn/gchatpic_new/740614810/3807109237-2508656275-F0F7B1AE168B0FDA40E9A27362C9462C/0?term=2");
        images.add("http://c2cpicdw.qpic.cn/offpic_new/740614810//740614810-1696602751-56BD87550600FE8A2D733349CD694990/0?term=2");
        images.add("http://c2cpicdw.qpic.cn/offpic_new/740614810//740614810-3744520516-847FEBBA6FE25C2AF2BD7CE010F2AFC0/0?term=2");
    }

    @Override
    public List<JSONArray> formTriggerResponses() {
        List<JSONArray> triggerResponses = new ArrayList<>();

        JSONObject triggerImage = new JSONObject();
        triggerImage.put("type", "Image");
        triggerImage.put("url", randomImage());
        JSONArray imageArray = new JSONArray();
        imageArray.add(triggerImage);

        triggerResponses.add(imageArray);
        return triggerResponses;
    }

    private String randomImage() {
        int randomIndex = (int) (Math.random() * images.size());
        return images.get(randomIndex);
    }
}
