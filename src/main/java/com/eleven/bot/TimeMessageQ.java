package com.eleven.bot;

import com.alibaba.fastjson.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.eleven.bot.Bot.sessionKey;
import static com.eleven.bot.PostMessage.buildMessageChain;
import static com.eleven.bot.PostMessage.postMessage;

public class TimeMessageQ extends TimeMessage {

    private final Long target = 2636948597L;
    //    private final Long target = 740614810L;
    private final String type = "Friend";
    private final int hour = 8;
    private final int minute = 0;
    private final int second = 0;

    private Calendar curTime;

    private Database database = Bot.database;

    public TimeMessageQ() {
        super();
    }

    @Override
    public void sendMessage() {
        if (curTime.get(Calendar.HOUR_OF_DAY) == hour && curTime.get(Calendar.MINUTE) == minute && curTime.get(Calendar.SECOND) == second) {
            List<JSONObject> messageChains = new ArrayList<>();

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse("2018-10-10");
                Date cur = format.parse(curTime.get(Calendar.YEAR) + "-" + (curTime.get(Calendar.MONTH) + 1) + "-" + curTime.get(Calendar.DATE));

                long diff = cur.getTime() - date.getTime();
                long days = diff / (24 * 60 * 60 * 1000);

                messageChains.add(buildMessageChain("今天是十一哥哥和泉泉姐姐在一起的第" + days + "天！", null));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            String sql = "SELECT * FROM messages_for_q";

            try {
                ResultSet rs = database.executeQuery(sql);
                rs.last();
                int size = rs.getRow();
                int pos = (int) (Math.random() * size);
                String text = null;
                if (rs.first()) {

                    for (int i = 0; i < pos; i++) {
                        rs.next();
                    }
                    text = rs.getString("text");
                }

                if (text != null) {
                    messageChains.add(buildMessageChain(text, null));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            postMessage(messageChains, type, target, sessionKey);
        }
    }

    @Override
    public void updateTime(Calendar curTime) {
        this.curTime = curTime;
    }
}
