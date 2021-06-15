package com.eleven.bot;

import com.alibaba.fastjson.JSONObject;

import static com.eleven.bot.PostMessage.postMessage;

public class Message_Group implements Message {

    private final String sessionKey;
    private final Long senderID;
    private final String senderName;
    private final Long groupID;
    private final String groupName;
    private final MessageChain messageChain;

    public Message_Group(JSONObject data, String sessionKey) {
        this.sessionKey = sessionKey;
        this.senderID = data.getJSONObject("sender").getLong("id");
        this.senderName = data.getJSONObject("sender").getString("memberName");
        this.groupID = data.getJSONObject("sender").getJSONObject("group").getLong("id");
        this.groupName = data.getJSONObject("sender").getJSONObject("group").getString("name");
        this.messageChain = new MessageChain(data.getJSONArray("messageChain"), senderID);
    }

    @Override
    public void handle() {
        postMessage(messageChain.formMessageChains(), "Group", groupID, sessionKey);
    }
}
