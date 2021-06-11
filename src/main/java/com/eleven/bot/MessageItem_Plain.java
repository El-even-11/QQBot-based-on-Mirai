package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;

import java.util.*;

public class MessageItem_Plain implements MessageItem {

    private final String text;
    private final HashMap<String, Trigger> triggers = new HashMap<>();
    private Database database = Bot.database;

    public MessageItem_Plain(String text) {
        this.text = text;

        triggers.put("晚安", new Trigger_Goodnight());
        triggers.put("晚上好", new Trigger_Goodnight());
        triggers.put("小可爱", new Trigger_Call());
        triggers.put("早", new Trigger_GoodMorning());
        triggers.put("早安", new Trigger_GoodMorning());
        triggers.put("早上好", new Trigger_GoodMorning());
    }

    @Override
    public List<JSONArray> formMessageItems() {

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

            //here

            if (triggers.containsKey(para) && !duplicate.contains(para)) {
                duplicate.add(para);
                triggerResponses.addAll(triggers.get(para).formTriggerResponses());
            }
        }

        return triggerResponses;
    }

}
