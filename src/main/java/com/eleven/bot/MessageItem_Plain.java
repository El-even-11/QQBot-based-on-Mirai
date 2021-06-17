package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.eleven.bot.PostMessage.buildImageMessageChains;
import static com.eleven.bot.PostMessage.buildTextMessageChains;

public class MessageItem_Plain implements MessageItem {

    private final String text;
    private Database database = Bot.database;

    public MessageItem_Plain(String text) {
        this.text = text;
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
        final int MAX_ALLOWED_WORD_LENGTH = 10;
        for (int i = 2; i <= MAX_ALLOWED_WORD_LENGTH; i++) {
            ALLOWED_WORD_LENGTH.add(i);
        }

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
        Set<String> duplicate = new HashSet<>(paras);

        for (String para : duplicate) {

            triggerResponses.addAll(Objects.requireNonNull(getResponses(para)));
        }

        return triggerResponses;
    }

    private List<JSONArray> getResponses(String trigger) {
        List<JSONArray> responses = new ArrayList<>();

        try {
            //text response
            String sql = "SELECT response FROM text_triggers WHERE text_trigger='" + trigger + "';";
            ResultSet rs = database.executeQuery(sql);

            //get rs size
            rs.last();
            int size = rs.getRow();

            //random response
            int pos = (int) (Math.random() * size);

            String textResponse = null;
            if (rs.first()) {

                for (int i = 0; i < pos; i++) {
                    rs.next();
                }
                textResponse = rs.getString("response");
            }

            if (textResponse != null) {
                responses.addAll(buildTextMessageChains(textResponse));
            }


            //image response
            sql = "SELECT url FROM image_triggers WHERE image_trigger='" + trigger + "';";

            rs = database.executeQuery(sql);

            //get rs size
            rs.last();
            size = rs.getRow();

            //random response
            pos = (int) (Math.random() * size);

            String url = null;
            if (rs.first()) {

                for (int i = 0; i < pos; i++) {
                    rs.next();
                }
                url = rs.getString("url");
            }

            if (url != null) {
                responses.addAll(buildImageMessageChains(url));
            }

            return responses;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }


}
