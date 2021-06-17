package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import static com.eleven.bot.PostMessage.buildTextMessageChainsList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Command {

    private String command;
    private JSONArray data;
    private String[] paras;
    private final Database database;
    private final Long senderID;

    public Command(String command, JSONArray data, Long senderID) {
        this.command = command;
        this.data = data;
        this.paras = command.split(" ");
        this.database = Bot.database;
        this.senderID = senderID;
    }

    public List<JSONObject> doCommand() {

        String cmd = paras[0];

        if (cmd.equalsIgnoreCase("setTriggerText")) {
            return setTriggerText();
        } else if (cmd.equalsIgnoreCase("help")) {
            return help();
        }else if (cmd.equalsIgnoreCase("setTriggerImage")){
            return setTriggerImage();
        }

        return null;
    }

    private List<JSONObject> setTriggerText() {

        final int SET_TRIGGER_TEXT_PERMISSION = 1;

        if (getPermission() >= SET_TRIGGER_TEXT_PERMISSION) {
            if (paras.length != 3) {
                return buildTextMessageChainsList("命令错误");
            }

            if (paras[1].length() > 3) {
                return buildTextMessageChainsList("添加失败，触发词过长");
            }

            String trigger = paras[1];
            String response = paras[2];

            String sql = "INSERT INTO text_triggers" +
                    "(text_trigger,response)" +
                    "VALUES" +
                    "(\"" + trigger + "\",\"" + response + "\");";

            try {
                database.execute(sql);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            System.out.println("Set successfully");
            return buildTextMessageChainsList("添加成功");
        }

        return buildTextMessageChainsList("权限不足");
    }

    private List<JSONObject> help() {
        StringBuilder response = new StringBuilder("命令以cmd+空格开头，所有命令如下\n\n");

        String sql = "SELECT * FROM commands;";

        try {
            ResultSet rs = database.executeQuery(sql);
            while (rs.next()) {
                response.append(rs.getString("command")).append("\n").append(rs.getString("description")).append("\n").append("权限 ").append(rs.getInt("permission")).append("\n\n");
            }

            response.delete(response.length() - 2, response.length());
            return buildTextMessageChainsList(response.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    private List<JSONObject> setTriggerImage() {

        final int SET_TRIGGER_IMAGE_PERMISSION = 1;

        if (getPermission() >= SET_TRIGGER_IMAGE_PERMISSION) {
            if (paras.length != 2) {
                return buildTextMessageChainsList("命令错误");
            }

            if (paras[1].length() > 3) {
                return buildTextMessageChainsList("添加失败，触发词过长");
            }

            String trigger = paras[1];
            String url = null;

            for (Object i : data) {
                JSONObject cur = (JSONObject) i;
                if (cur.getString("type").equals("Image")) {
                    url = cur.getString("url");
                }
            }

            if (url != null) {
                String sql = "INSERT INTO image_triggers" +
                        "(image_trigger,url)" +
                        "VALUES" +
                        "(\"" + trigger + "\",\"" + url + "\");";

                try {
                    database.execute(sql);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                System.out.println("Set successfully");
                return buildTextMessageChainsList("添加成功");
            }

            return buildTextMessageChainsList("图片呢！");
        }

        return buildTextMessageChainsList("权限不足");
    }

    private int getPermission() {
        String sql = "SELECT permission FROM permissions WHERE QQ=" + senderID + ";";

        try {
            ResultSet rs = database.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("permission");
            }

            return 0;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return -1;
    }
}
