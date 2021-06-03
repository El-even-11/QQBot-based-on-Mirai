package com.eleven.bot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Timer {

    Long botQQ;
    String sessionKey;
    List<TimeMessage> timeMessageList;

    public Timer(Long botQQ, String sessionKey) {
        this.botQQ = botQQ;
        this.sessionKey = sessionKey;
        timeMessageList = new ArrayList<>();
        timeMessageList.add(new TimeMessage_Goodnight(0, 0, 317109237L, sessionKey));
        timeMessageList.add(new TimeMessage_GoodAfternoon(15, 0, 317109237L, sessionKey));
    }

    public void sendMessage() {
        for (TimeMessage tm : timeMessageList) {
            tm.handle();
        }
    }
}
