package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageChain {

    private HashMap<String, MessageItem> requests;
    private boolean isCommand = false;
    private String command = null;
    private JSONArray data;
    private final Long senderID;

    public MessageChain(JSONArray data, Long senderID) {
        this.data = data;
        this.senderID = senderID;

        requests = new HashMap<>();

        for (Object i : data) {
            JSONObject cur = (JSONObject) i;
            String type = cur.getString("type");
            if (type.equals("Plain")) {
                String text = cur.getString("text");
                if (text.length() >= 4 && text.startsWith("cmd ")) {
                    isCommand = true;
                    command = text.substring(4);
                    return;
                }
            }
        }

        for (Object i : data) {
            JSONObject cur = (JSONObject) i;
            String type = cur.getString("type");
            if (type.equals("Plain") && !requests.containsKey("Plain")) {
                requests.put("Plain", new MessageItem_Plain(cur.getString("text")));
            } else if (type.equals("At") && !requests.containsKey("At")) {
                requests.put("At", new MessageItem_At());
            } else if (type.equals("Face") && !requests.containsKey("Face")) {
                requests.put("Face", new MessageItem_Face());
            }
        }
    }

    public List<JSONObject> formMessageChains() {

        if (isCommand) {
            Command cmd = new Command(command, data, senderID);
            return cmd.doCommand();
        }

        List<JSONObject> messageChains = new ArrayList<>();

        for (String key : requests.keySet()) {
            List<JSONArray> messageItems = requests.get(key).formMessageItems();
            if (messageItems != null && messageItems.size() > 0) {
                for (JSONArray i : messageItems) {
                    JSONObject messageChain = new JSONObject();
                    messageChain.put("messageChain", i);
                    messageChains.add(messageChain);
                }
            }
        }

        return messageChains;
    }
}
