package com.eleven.bot;

import java.util.Calendar;

public interface TimeMessage {
    void handle();
    boolean isTriggerTime();
    void updateTime(Calendar curTime);
}
