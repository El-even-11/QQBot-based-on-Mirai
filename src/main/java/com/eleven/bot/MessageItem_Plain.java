package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class MessageItem_Plain implements MessageItem {

    private final String text;
    private final HashMap<String, Trigger> triggers = new HashMap<>();
    private boolean isCommand = false;
    private String cmd = null;
    private final HashMap<String, String> cmdDesc = new HashMap<>();
    private final HashMap<String, Command> cmds = new HashMap<>();

    public MessageItem_Plain(String text) {
        this.text = text;

        triggers.put("晚安", new Trigger_Goodnight());
        triggers.put("晚上好", new Trigger_Goodnight());
        triggers.put("小可爱", new Trigger_Call());
        triggers.put("早", new Trigger_GoodMorning());
        triggers.put("早安", new Trigger_GoodMorning());
        triggers.put("早上好", new Trigger_GoodMorning());

        if (text.length() > 4 && text.startsWith("cmd ")) {
            isCommand = true;
            cmdDesc.put("help", "列出所有命令");
            cmds.put("help", new Command_Help(cmdDesc));
            cmdDesc.put("trigger", "列出触发词");
            cmds.put("trigger", new Command_Trigger(triggers.keySet()));
            cmd = text.substring(4);
        }
    }

    @Override
    public List<JSONArray> formMessageItems() {
        List<JSONArray> messageItems = new ArrayList<>();
        if (isCommand) {
            if (cmds.containsKey(cmd)) {
                JSONObject commandResponse = cmds.get(cmd).formCommandResponse();
                JSONArray array = new JSONArray();
                array.add(commandResponse);
                messageItems.add(array);
                return messageItems;
            } else {
                JSONObject messageItem = new JSONObject();
                messageItem.put("type", "Plain");
                messageItem.put("text", "格式错误");
                JSONArray array = new JSONArray();
                array.add(messageItem);
                messageItems.add(array);
                return messageItems;
            }
        } else {
            StringBuffer trigger = new StringBuffer(text);

            //remove blank
            while (trigger.length() > 0 && trigger.charAt(0) == ' ') {
                trigger.delete(0, 1);
            }

            List<String> paras = new ArrayList<>();

            final int MAX_ALLOWED_TEXT_LENGTH = 30;

            //split words length
            final ArrayList<Integer> ALLOWED_WORD_LENGTH = new ArrayList<>();
            ALLOWED_WORD_LENGTH.add(2);
            ALLOWED_WORD_LENGTH.add(3);

            //only one char
            if (trigger.length() == 1) {
                paras.add(trigger.toString());
            }

            //split
            for (int len : ALLOWED_WORD_LENGTH) {
                for (int i = 0; i <= trigger.length() - len && i <= MAX_ALLOWED_TEXT_LENGTH; i++) {
                    paras.add(trigger.substring(i, i + len));
                }
            }

            List<JSONArray> triggerResponses = new ArrayList<>();

            //remove duplicate triggers,
            Set<String> duplicate = new HashSet<>();

            for (String para : paras) {
                if (triggers.containsKey(para) && !duplicate.contains(para)) {
                    duplicate.add(para);
                    triggerResponses.addAll(triggers.get(para).formTriggerResponses());
                }
            }

            return triggerResponses;
        }
    }
}
