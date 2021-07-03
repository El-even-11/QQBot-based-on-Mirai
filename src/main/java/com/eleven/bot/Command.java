package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import static com.eleven.bot.PostMessage.buildMessageChain;
import static com.eleven.bot.PostMessage.buildTextMessageChainsList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Command {

    private String command;
    private JSONArray data;
    private String[] paras;
    private final Database database;
    private final Long senderID;
    private final int MAX_ALLOWED_WORD_LENGTH = 10;

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
        } else if (cmd.equalsIgnoreCase("setTriggerImage")) {
            return setTriggerImage();
        } else if (cmd.equalsIgnoreCase("getTriggerText")) {
            return getTriggerText();
        } else if (cmd.equalsIgnoreCase("getTriggerImage")) {
            return getTriggerImage();
        } else if (cmd.equalsIgnoreCase("delTriggerText")) {
            return delTriggerText();
        } else if (cmd.equalsIgnoreCase("delTriggerImage")) {
            return delTriggerImage();
        } else if (cmd.equalsIgnoreCase("setGroupTimer")) {
            return setGroupTimer();
        }

        return null;
    }

    private List<JSONObject> setTriggerText() {

        final int SET_TRIGGER_TEXT_PERMISSION = 1;

        if (getPermission() >= SET_TRIGGER_TEXT_PERMISSION) {
            if (paras.length != 3) {
                return buildTextMessageChainsList("命令错误");
            }


            if (paras[1].length() > MAX_ALLOWED_WORD_LENGTH) {
                return buildTextMessageChainsList("添加失败，触发词过长");
            }

            String trigger = paras[1];
            String response = paras[2];

            String sql = "INSERT INTO text_triggers" +
                    "(text_trigger,response)" +
                    "VALUES" +
                    "(\"" + database.regularize(trigger) + "\",\"" + database.regularize(response) + "\")";

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

        String sql = "SELECT * FROM commands";

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

            if (paras[1].length() > MAX_ALLOWED_WORD_LENGTH) {
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
                        "(\"" + database.regularize(trigger) + "\",\"" + url + "\")";

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

    private List<JSONObject> getTriggerText() {
        final int GET_TRIGGER_TEXT_PERMISSION = 2;
        final int GET_TRIGGER_TEXT_PERMISSION_ALL = 3;

        if (paras.length == 1) {
            if (getPermission() >= GET_TRIGGER_TEXT_PERMISSION_ALL) {
                String sql = "SELECT * FROM text_triggers";

                try {
                    ResultSet rs = database.executeQuery(sql);
                    ArrayList<Integer> id = new ArrayList<>();
                    ArrayList<String> text_triggers = new ArrayList<>();
                    ArrayList<String> responses = new ArrayList<>();

                    while (rs.next()) {
                        id.add(rs.getInt("id"));
                        text_triggers.add(rs.getString("text_trigger"));
                        responses.add(rs.getString("response"));
                    }

                    List<JSONObject> messageChains = new ArrayList<>();

                    for (int i = 0; i < id.size(); i++) {
                        messageChains.add(buildMessageChain("id:" + id.get(i) + "\n触发词:" + text_triggers.get(i) + "\n回复:" + responses.get(i), null));
                    }

                    return messageChains;

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            return buildTextMessageChainsList("权限不足");
        } else if (paras.length == 2) {
            if (getPermission() >= GET_TRIGGER_TEXT_PERMISSION) {
                String sql = "SELECT * FROM text_triggers WHERE text_trigger=\"" + database.regularize(paras[1]) + "\"";

                try {
                    ResultSet rs = database.executeQuery(sql);
                    ArrayList<Integer> id = new ArrayList<>();
                    ArrayList<String> responses = new ArrayList<>();

                    while (rs.next()) {
                        id.add(rs.getInt("id"));
                        responses.add(rs.getString("response"));
                    }

                    List<JSONObject> messageChains = new ArrayList<>();

                    for (int i = 0; i < id.size(); i++) {
                        messageChains.add(buildMessageChain("id:" + id.get(i) + "\n回复:" + responses.get(i), null));
                    }

                    if (messageChains.size() == 0) {
                        return buildTextMessageChainsList("不存在触发词 " + paras[1]);
                    }

                    return messageChains;

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            return buildTextMessageChainsList("权限不足");
        }

        return buildTextMessageChainsList("命令错误");
    }


    private List<JSONObject> getTriggerImage() {
        final int GET_TRIGGER_IMAGE_PERMISSION = 2;
        final int GET_TRIGGER_IMAGE_PERMISSION_ALL = 3;

        if (paras.length == 1) {
            if (getPermission() >= GET_TRIGGER_IMAGE_PERMISSION_ALL) {
                String sql = "SELECT * FROM image_triggers";

                try {
                    ResultSet rs = database.executeQuery(sql);
                    ArrayList<Integer> id = new ArrayList<>();
                    ArrayList<String> image_triggers = new ArrayList<>();
                    ArrayList<String> urls = new ArrayList<>();

                    while (rs.next()) {
                        id.add(rs.getInt("id"));
                        image_triggers.add(rs.getString("image_trigger"));
                        urls.add(rs.getString("url"));
                    }

                    List<JSONObject> messageChains = new ArrayList<>();

                    for (int i = 0; i < id.size(); i++) {
                        messageChains.add(buildMessageChain("id:" + id.get(i) + "\n触发词:" + image_triggers.get(i), urls.get(i)));
                    }

                    return messageChains;

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            return buildTextMessageChainsList("权限不足");
        } else if (paras.length == 2) {
            if (getPermission() >= GET_TRIGGER_IMAGE_PERMISSION) {
                String sql = "SELECT * FROM image_triggers WHERE image_trigger=\"" + database.regularize(paras[1]) + "\"";

                try {
                    ResultSet rs = database.executeQuery(sql);
                    ArrayList<Integer> id = new ArrayList<>();
                    ArrayList<String> urls = new ArrayList<>();

                    while (rs.next()) {
                        id.add(rs.getInt("id"));
                        urls.add(rs.getString("url"));
                    }

                    List<JSONObject> messageChains = new ArrayList<>();

                    for (int i = 0; i < id.size(); i++) {
                        messageChains.add(buildMessageChain("id:" + id.get(i), urls.get(i)));
                    }

                    if (messageChains.size() == 0) {
                        return buildTextMessageChainsList("不存在触发词 " + paras[1]);
                    }

                    return messageChains;

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            return buildTextMessageChainsList("权限不足");
        }

        return buildTextMessageChainsList("命令错误");
    }

    private List<JSONObject> delTriggerText() {
        final int DEL_TRIGGER_TEXT_PERMISSION = 2;

        if (getPermission() >= DEL_TRIGGER_TEXT_PERMISSION) {
            if (paras.length == 2) {
                String sql = "SELECT * FROM text_triggers WHERE id=" + paras[1];

                try {
                    ResultSet rs = database.executeQuery(sql);
                    if (rs.next()) {
                        sql = "DELETE FROM text_triggers WHERE id=" + paras[1];
                        database.execute(sql);
                        return buildTextMessageChainsList("删除成功");
                    }

                    return buildTextMessageChainsList("删除失败，不存在id=" + paras[1] + "的回复");

                } catch (SQLException throwables) {
                    throwables.printStackTrace();

                }
            }

            return buildTextMessageChainsList("命令错误");
        }

        return buildTextMessageChainsList("权限不足");
    }

    private List<JSONObject> delTriggerImage() {
        final int DEL_TRIGGER_IMAGE_PERMISSION = 2;

        if (getPermission() >= DEL_TRIGGER_IMAGE_PERMISSION) {
            if (paras.length == 2) {
                String sql = "SELECT * FROM image_triggers WHERE id=" + paras[1];

                try {
                    ResultSet rs = database.executeQuery(sql);
                    if (rs.next()) {
                        sql = "DELETE FROM image_triggers WHERE id=" + paras[1];
                        database.execute(sql);
                        return buildTextMessageChainsList("删除成功");
                    }

                    return buildTextMessageChainsList("删除失败，不存在id=" + paras[1] + "的回复");

                } catch (SQLException throwables) {
                    throwables.printStackTrace();

                }
            }

            return buildTextMessageChainsList("命令错误");
        }

        return buildTextMessageChainsList("权限不足");
    }

    private List<JSONObject> setGroupTimer() {
        final int SET_GROUP_TIMER_PERMISSION = 2;

        if (getPermission() >= SET_GROUP_TIMER_PERMISSION) {

            //cmd : setGroupTimer target hour minute [text] [url]
            if (paras.length == 4) {
                //text : null
                String url = null;

                for (Object i : data) {
                    JSONObject cur = (JSONObject) i;
                    if (cur.getString("type").equals("Image")) {
                        url = cur.getString("url");
                    }
                }

                if (url != null) {
                    String target = paras[1];
                    String hour = paras[2];
                    String minute = paras[3];

                    String sql = "INSERT INTO timers" +
                            "(target,hour,minute,url)" +
                            "VALUES" +
                            "(" + target + "," + hour + "," + minute + ",\"" + url + "\")";

                    try {
                        database.execute(sql);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    System.out.println("Set successfully");
                    return buildTextMessageChainsList("添加成功");
                }

                return buildTextMessageChainsList("命令错误");

            } else if (paras.length == 5) {
                //text != null

                String url = null;
                for (Object i : data) {
                    JSONObject cur = (JSONObject) i;
                    if (cur.getString("type").equals("Image")) {
                        url = cur.getString("url");
                    }
                }

                String target = paras[1];
                String hour = paras[2];
                String minute = paras[3];
                String text = paras[4];
                String sql;

                if (url != null) {
                    sql = "INSERT INTO timers" +
                            "(target,hour,minute,text,url)" +
                            "VALUES" +
                            "(" + target + "," + hour + "," + minute + ",\"" + text + "\",\"" + url + "\")";
                } else {
                    sql = "INSERT INTO timers" +
                            "(target,hour,minute,text,url)" +
                            "VALUES" +
                            "(" + target + "," + hour + "," + minute + ",\"" + text + "\",\"null\")";
                }


                try {
                    database.execute(sql);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                System.out.println("Set successfully");


                return buildTextMessageChainsList("添加成功");
            }

            return buildTextMessageChainsList("命令错误");

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
