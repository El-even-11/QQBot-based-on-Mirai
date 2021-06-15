package com.eleven.bot;

import com.alibaba.fastjson.JSONObject;

import static com.eleven.bot.PostMessage.postMessage;

public class Message_Friend implements Message {

    private final String sessionKey;
    private final Long senderID;
    private final String senderName;
    private final MessageChain messageChain;

    public Message_Friend(JSONObject data, String sessionKey) {
        this.sessionKey = sessionKey;
        this.senderID = data.getJSONObject("sender").getLong("id");
        this.senderName = data.getJSONObject("sender").getString("nickname");
        this.messageChain = new MessageChain(data.getJSONArray("messageChain"), senderID);
    }

    @Override
    public void handle() {
        postMessage(messageChain.formMessageChains(), "Friend", senderID, sessionKey);
    }
}
