package com.eleven.bot;

import com.alibaba.fastjson.JSONObject;

import java.util.Set;

public class Command_Trigger implements Command {

    private final Set<String> set;

    public Command_Trigger(Set<String> set) {
        this.set = set;
    }

    @Override
    public JSONObject formCommandResponse() {
        StringBuilder sb = new StringBuilder();
        for (String s : set) {
            sb.append(s).append("\n");
        }
        JSONObject commandResponse = new JSONObject();
        sb.deleteCharAt(sb.length() - 1);
        commandResponse.put("type", "Plain");
        commandResponse.put("text", sb.toString());
        return commandResponse;
    }
}
