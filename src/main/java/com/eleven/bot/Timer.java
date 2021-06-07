package com.eleven.bot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Timer {

    Long botQQ;
    String sessionKey;
    List<TimeMessage> timeMessageList;

    Calendar curTime;

    public Timer(Long botQQ, String sessionKey) {
        this.botQQ = botQQ;
        this.sessionKey = sessionKey;
        this.curTime = Calendar.getInstance();

        timeMessageList = new ArrayList<>();
        timeMessageList.add(new TimeMessage_Goodnight(0, 0, 317109237L, sessionKey));
        timeMessageList.add(new TimeMessage_GoodAfternoon(15, 0, 317109237L, sessionKey));
        timeMessageList.add(new TimeMessage_GoodAfternoon(15, 0, 705091577L, sessionKey));
    }

    public void updateTime() {
        this.curTime = Calendar.getInstance();
        for (TimeMessage tm : timeMessageList) {
            tm.updateTime(this.curTime);
        }
    }

    public void sendMessage() {
        for (TimeMessage tm : timeMessageList) {
            tm.handle();
        }
    }
}
