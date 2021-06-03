package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

public interface Trigger {
    List<JSONArray> formTriggerResponses();
}
