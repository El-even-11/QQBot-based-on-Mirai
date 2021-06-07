package com.eleven.bot;

import com.alibaba.fastjson.JSONObject;

import static com.eleven.bot.Http.doPost;
import static java.lang.Thread.sleep;

public class Bot {
    private final Long botQQ = 2473537565L;
    private final String authKey = "INITKEYMKBdPDph";
    private String sessionKey;


    public Bot() {

        setUp();
    }

    public void run() {

        MessageListener listener = new MessageListener(botQQ, sessionKey);
        Timer timer = new Timer(botQQ, sessionKey);

        while (true) {
            listener.updateMessages();
            listener.handleMessages();
            timer.updateTime();
            timer.sendMessage();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUp() {

        //get sessionKey
        JSONObject JOGetSessionKey = new JSONObject();
        JOGetSessionKey.put("authKey", authKey);
        String sessionKeyResponse = doPost("http://0.0.0.0:8080/auth", JOGetSessionKey.toJSONString());
        JSONObject sessionKeyJsonResponse = JSONObject.parseObject(sessionKeyResponse);
        if (sessionKeyJsonResponse.getIntValue("code") != 0) {
            System.out.println("get session fail,the code is " + sessionKeyJsonResponse.getIntValue("code"));
            return;
        }
        System.out.println("get session successfully");
        sessionKey = sessionKeyJsonResponse.getString("session");

        //verify sessionKey
        JSONObject JOVerify = new JSONObject();
        JOVerify.put("sessionKey", sessionKey);
        JOVerify.put("qq", botQQ);
        String verifyResponse = doPost("http://0.0.0.0:8080/verify", JOVerify.toJSONString());
        JSONObject verifyJsonResponse = JSONObject.parseObject(verifyResponse);
        if (verifyJsonResponse.getIntValue("code") != 0) {
            System.out.println("verify fail,the code is " + verifyJsonResponse.getIntValue("code"));
            return;
        }
        System.out.println("verify successfully");
    }

}
