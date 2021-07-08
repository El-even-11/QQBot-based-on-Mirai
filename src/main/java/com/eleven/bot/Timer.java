package com.eleven.bot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class Timer {

    Long botQQ;
    String sessionKey;

    Database database;

    ArrayList<TimeMessage> timeMessageList = new ArrayList<>();

    Calendar curTime;

    public Timer(Long botQQ, String sessionKey) {
        this.botQQ = botQQ;
        this.sessionKey = sessionKey;
        this.curTime = Calendar.getInstance();
        this.database = Bot.database;

        String sql = "SELECT * FROM timers";
        try {
            ResultSet rs = database.executeQuery(sql);
            while (rs.next()) {
                timeMessageList.add(new TimeMessage(rs.getLong("target"), rs.getString("type"), rs.getString("text"), rs.getString("url"), rs.getInt("hour"), rs.getInt("minute")));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateTime() {
        this.curTime = Calendar.getInstance();
        for (TimeMessage tm : timeMessageList) {
            tm.updateTime(this.curTime);
        }
    }

    public void sendMessage() {
        for (TimeMessage tm : timeMessageList) {
            tm.sendMessage();
        }
    }
}
