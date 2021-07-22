package com.eleven.bot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.Queue;

import static com.eleven.bot.Http.doGet;

public class MessageListener {

    private final Long botQQ;
    private final String sessionKey;
    private final Queue<Message> unhandledMessage = new LinkedList<>();

    public MessageListener(Long botQQ, String sessionKey) {
        this.botQQ = botQQ;
        this.sessionKey = sessionKey;
    }

    private final int GET_MASSAGE_COUNT = 10;

    public void updateMessages() {
        String fetchResponse = doGet("http://0.0.0.0:8080/fetchLatestMessage?sessionKey=" + sessionKey + "&count=" + GET_MASSAGE_COUNT);
        JSONObject fetchJsonResponse = JSON.parseObject(fetchResponse);
        JSONArray messages = fetchJsonResponse.getJSONArray("data");
        if (messages != null && messages.size() > 0) {
            for (Object i : messages) {
                JSONObject cur = (JSONObject) i;
                System.out.println(cur.toJSONString());
                if (cur.getString("type").equals("GroupMessage")) {
                    unhandledMessage.offer(new Message_Group(cur, sessionKey));
                }
                if (cur.getString("type").equals("FriendMessage")) {
                    unhandledMessage.offer(new Message_Friend(cur, sessionKey));
                }
            }
        }
    }

    public void handleMessages() {
        while (!unhandledMessage.isEmpty()) {
            Message i = unhandledMessage.poll();
            i.handle();
        }
    }
}
