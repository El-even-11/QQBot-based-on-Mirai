package com.eleven.bot;

import com.alibaba.fastjson.JSONObject;

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
                    System.out.println("post " + type + " fail");
                }
            }
        }
    }
}
