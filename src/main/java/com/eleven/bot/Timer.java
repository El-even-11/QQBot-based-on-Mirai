package com.eleven.bot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Timer {

    Long botQQ;
    String sessionKey;

    Database database;

    HashMap<String, TimeMessage> timeMessageList = new HashMap<>();

    Calendar curTime;

    public Timer(Long botQQ, String sessionKey) {
        this.botQQ = botQQ;
        this.sessionKey = sessionKey;
        this.curTime = Calendar.getInstance();
        this.database = Bot.database;

        updateList();
    }

    public void updateList() {
        timeMessageList.clear();

        String sql = "SELECT * FROM timers";
        try {
            ResultSet rs = database.executeQuery(sql);
            List<Long> targets = new ArrayList<>();
            List<String> types = new ArrayList<>();
            List<String> texts = new ArrayList<>();
            List<String> urls = new ArrayList<>();
            List<Integer> times = new ArrayList<>();
            List<String> keys = new ArrayList<>();
            while (rs.next()) {
                targets.add(rs.getLong("target"));
                types.add(rs.getString("type"));
                texts.add(rs.getString("text"));
                urls.add(rs.getString("url"));
                times.add(rs.getInt("hour") * 100 + rs.getInt("minute"));

                keys.add("" + rs.getLong("target") + rs.getInt("hour") * 100 + rs.getInt("minute"));
            }

            int size = targets.size();

            for (int i = 0; i < size; i++) {
                if (!timeMessageList.containsKey(keys.get(i))) {
                    timeMessageList.put(keys.get(i), new TimeMessage(targets.get(i), types.get(i), times.get(i) / 100, times.get(i) % 100));
                }
                timeMessageList.get(keys.get(i)).addText(texts.get(i));
                timeMessageList.get(keys.get(i)).addUrls(urls.get(i));
            }
            timeMessageList.put("0", new TimeMessageQ());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateTime() {
        this.curTime = Calendar.getInstance();
        for (String key : timeMessageList.keySet()) {
            timeMessageList.get(key).updateTime(this.curTime);
        }
    }

    public void sendMessage() {
        for (String key : timeMessageList.keySet()) {
            timeMessageList.get(key).sendMessage();
        }
    }
}
