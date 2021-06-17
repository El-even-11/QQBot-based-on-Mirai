package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.eleven.bot.Http.doPost;

public class PostMessage {
    public static void postMessage(List<JSONObject> messageChains, String type, Long target, String sessionKey) {

        if (messageChains != null && messageChains.size() > 0) {
            for (JSONObject response : messageChains) {
                response.put("sessionKey", sessionKey);
                response.put("target", target);

                String postResponse = doPost("http://0.0.0.0:8080/send" + type + "Message", response.toJSONString());
                JSONObject jsonResponse = JSONObject.parseObject(postResponse);

                if (jsonResponse != null && jsonResponse.getIntValue("code") == 0) {
                    System.out.println("post " + type + " message successfully");
                } else {
                    System.out.println("post " + type + " fail,code = " + jsonResponse.getIntValue("code"));
                }
            }
        }
    }

    public static List<JSONObject> buildTextMessageChainsList(String text) {
        List<JSONObject> messageChains = new ArrayList<>();
        JSONObject messageChain = new JSONObject();
        JSONArray responses = new JSONArray();
        JSONObject response = new JSONObject();
        response.put("type", "Plain");
        response.put("text", text);
        responses.add(response);
        messageChain.put("messageChain", responses);
        messageChains.add(messageChain);
        return messageChains;
    }

    public static List<JSONArray> buildTextMessageChains(String text) {
        List<JSONArray> messageChains = new ArrayList<>();
        JSONArray responses = new JSONArray();
        JSONObject response = new JSONObject();
        response.put("type", "Plain");
        response.put("text", text);
        responses.add(response);
        messageChains.add(responses);
        return messageChains;
    }

    public static List<JSONArray> buildImageMessageChains(String url) {
        List<JSONArray> messageChains = new ArrayList<>();
        JSONArray responses = new JSONArray();
        JSONObject response = new JSONObject();
        response.put("type", "Image");
        response.put("url", url);
        responses.add(response);
        messageChains.add(responses);
        return messageChains;
    }
}
