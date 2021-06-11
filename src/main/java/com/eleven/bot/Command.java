package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import static com.eleven.bot.PostMessage.buildTextToPost;

import java.sql.SQLException;
import java.util.List;

public class Command {

    private String command;
    private JSONArray data;
    private String[] paras;
    private final Database database;

    public Command(String command, JSONArray data) {
        this.command = command;
        this.data = data;
        this.paras = command.split(" ");
        this.database = Bot.database;
    }

    public List<JSONObject> doCommand() {
        if (paras[0].equalsIgnoreCase("setTriggerText")) {
            return setTriggerText();
        }

        return null;
    }

    private List<JSONObject> setTriggerText() {
        if (paras.length != 3) {
            return buildTextToPost("命令错误");
        }

        String trigger = paras[1];
        String response = paras[2];

        String sql = "INSERT INTO table_name" +
                "(trigger,response)" +
                "VALUES" +
                "(" + trigger + "," + response + ");";

        try {
            database.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return buildTextToPost("添加成功");
    }
}
