package com.eleven.bot;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public class Command_Help implements Command {

    private final HashMap<String, String> cmdDesc;

    public Command_Help(HashMap<String, String> cmdDesc) {

        this.cmdDesc = cmdDesc;
    }

    @Override
    public JSONObject formCommandResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("命令格式为 cmd+空格+命令").append("\n");
        for (String key : cmdDesc.keySet()) {
            sb.append(key).append(" ").append(cmdDesc.get(key)).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        JSONObject commandResponse = new JSONObject();
        commandResponse.put("type", "Plain");
        commandResponse.put("text", sb.toString());
        return commandResponse;
    }
}
