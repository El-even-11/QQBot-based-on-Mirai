package com.eleven.bot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import static com.eleven.bot.Bot.timer;
import static com.eleven.bot.PostMessage.buildMessageChain;
import static com.eleven.bot.PostMessage.buildTextMessageChainsList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Command {

    private final JSONArray data;
    private final String[] paras;
    private final Database database;
    private final Long senderID;
    private final int MAX_ALLOWED_WORD_LENGTH = 10;

    public Command(String command, JSONArray data, Long senderID) {
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
        } else if (cmd.equalsIgnoreCase("setTimer")) {
            return setTimer();
        } else if (cmd.equalsIgnoreCase("getTimer")) {
            return getTimer();
        } else if (cmd.equalsIgnoreCase("delTimer")) {
            return delTimer();
        } else if (cmd.equalsIgnoreCase("addMessageForQ")) {
            return addMessageForQ();
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
            if (paras.length > 2) {
                StringBuilder sSQL = new StringBuilder("SELECT * FROM text_triggers WHERE id=" + paras[1]);
                StringBuilder dSQL = new StringBuilder("DELETE FROM text_triggers WHERE id=" + paras[1]);

                for (int i = 2; i < paras.length; i++) {
                    sSQL.append(" OR id=").append(paras[i]);
                    dSQL.append(" OR id=").append(paras[i]);
                }
                try {
                    ResultSet rs = database.executeQuery(sSQL.toString());
                    StringBuilder returnMessage = new StringBuilder("删除成功\n");
                    while (rs.next()) {
                        String textTrigger = rs.getString("text_trigger");
                        String response = rs.getString("response");

                        returnMessage.append("text:").append(textTrigger).append("\nresp:").append(response).append("\n");
                    }

                    database.execute(dSQL.toString());

                    return buildTextMessageChainsList(returnMessage.toString());

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
            if (paras.length > 1) {
                StringBuilder sSQL = new StringBuilder("SELECT * FROM image_triggers WHERE id=" + paras[1]);
                StringBuilder dSQL = new StringBuilder("DELETE FROM image_triggers WHERE id=" + paras[1]);

                for (int i = 2; i < paras.length; i++) {
                    sSQL.append(" OR id=").append(paras[i]);
                    dSQL.append(" OR id=").append(paras[i]);
                }

                try {
                    ResultSet rs = database.executeQuery(sSQL.toString());
                    List<JSONObject> returnMessage=new ArrayList<>();
                    while (rs.next()) {
                        String text = rs.getString("image_trigger");
                        String url = rs.getString("url");
                        returnMessage.add(buildMessageChain("删除成功\ntext:" + text + "\nurl:", url));
                    }

                    database.execute(dSQL.toString());

                    return returnMessage;

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            return buildTextMessageChainsList("命令错误");
        }

        return buildTextMessageChainsList("权限不足");
    }

    private List<JSONObject> setTimer() {
        final int SET_TIMER_PERMISSION = 2;

        if (getPermission() >= SET_TIMER_PERMISSION) {


            if (paras.length == 5) {
                //cmd : setTimer [e] target type hour minute [text] [url]

                if (paras[1].equals("e")) {
                    // cmd : setTimer e target type text [url]

                    String url = null;

                    for (Object i : data) {
                        JSONObject cur = (JSONObject) i;
                        if (cur.getString("type").equals("Image")) {
                            url = cur.getString("url");
                        }

                        String target = paras[2];
                        String type = paras[3];
                        String text = paras[4];
                        if (!type.equals("Friend") && !type.equals("Group")) {
                            return buildTextMessageChainsList("命令错误");
                        }

                        StringBuilder sql = new StringBuilder("INSERT INTO timers");

                        if (url != null) {
                            sql.append("(target,type,hour,minute,text,url)VALUES");
                            for (int hour = 0; hour < 24; hour++) {
                                for (int minute = 0; minute < 60; minute++) {
                                    sql.append("(").append(target).append(",\"").append(type).append("\",").append(hour).append(",").append(minute).append(",\"").append(text).append("\",\"").append(url).append("\"),");
                                }
                            }
                        } else {
                            sql.append("(target,type,hour,minute,text)VALUES");
                            for (int hour = 0; hour < 24; hour++) {
                                for (int minute = 0; minute < 60; minute++) {
                                    sql.append("(").append(target).append(",\"").append(type).append("\",").append(hour).append(",").append(minute).append(",\"").append(text).append("\"),");
                                }
                            }
                        }
                        //remove ','
                        sql.deleteCharAt(sql.length() - 1);

                        try {

                            database.execute(sql.toString());
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        timer.updateList();

                        System.out.println("Set successfully");
                        return buildTextMessageChainsList("添加成功");
                    }

                } else {
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
                        String type = paras[2];
                        if (!type.equals("Friend") && !type.equals("Group") || !(Integer.parseInt(paras[3]) >= 0 && Integer.parseInt(paras[3]) <= 23 && Integer.parseInt(paras[4]) >= 0 && Integer.parseInt(paras[4]) <= 59)) {
                            return buildTextMessageChainsList("命令错误");
                        }

                        String hour = paras[3];
                        String minute = paras[4];

                        String sql = "INSERT INTO timers" +
                                "(target,type,hour,minute,url)" +
                                "VALUES" +
                                "(" + target + ",\"" + type + "\"," + hour + "," + minute + ",\"" + url + "\")";

                        try {
                            database.execute(sql);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        timer.updateList();

                        System.out.println("Set successfully");
                        return buildTextMessageChainsList("添加成功");
                    }

                    return buildTextMessageChainsList("命令错误");
                }


            } else if (paras.length == 6) {
                //text != null

                String url = null;
                for (Object i : data) {
                    JSONObject cur = (JSONObject) i;
                    if (cur.getString("type").equals("Image")) {
                        url = cur.getString("url");
                    }
                }

                String target = paras[1];
                String type = paras[2];
                if (!type.equals("Friend") && !type.equals("Group")) {
                    return buildTextMessageChainsList("命令错误");
                }

                String hour = paras[3];
                String minute = paras[4];
                String text = paras[5];
                String sql;

                if (url != null) {
                    sql = "INSERT INTO timers" +
                            "(target,type,hour,minute,text,url)" +
                            "VALUES" +
                            "(" + target + ",\"" + type + "\"," + hour + "," + minute + ",\"" + text + "\",\"" + url + "\")";
                } else {
                    sql = "INSERT INTO timers" +
                            "(target,type,hour,minute,text)" +
                            "VALUES" +
                            "(" + target + ",\"" + type + "\"," + hour + "," + minute + ",\"" + text + "\")";
                }


                try {
                    database.execute(sql);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    return buildTextMessageChainsList("发生错误");
                }

                timer.updateList();

                System.out.println("Set successfully");


                return buildTextMessageChainsList("添加成功");
            }

            return buildTextMessageChainsList("命令错误");

        }
        return buildTextMessageChainsList("权限不足");
    }

    private List<JSONObject> getTimer() {
        final int GET_TIMER_PERMISSION = 2;
        final int GET_TIMER_PERMISSION_ALL = 3;

        if (paras.length == 1) {
            //get all
            if (getPermission() >= GET_TIMER_PERMISSION_ALL) {
                try {
                    String sql = "SELECT * FROM timers";
                    ResultSet rs = database.executeQuery(sql);

                    ArrayList<Integer> id = new ArrayList<>();
                    ArrayList<String> text = new ArrayList<>();
                    ArrayList<String> url = new ArrayList<>();
                    ArrayList<Long> target = new ArrayList<>();
                    ArrayList<String> type = new ArrayList<>();
                    ArrayList<Integer> hour = new ArrayList<>();
                    ArrayList<Integer> minute = new ArrayList<>();

                    while (rs.next()) {
                        id.add(rs.getInt("id"));
                        text.add(rs.getString("text"));
                        url.add((rs.getString("url")));
                        target.add(rs.getLong("target"));
                        type.add(rs.getString("type"));
                        hour.add(rs.getInt("hour"));
                        minute.add(rs.getInt("minute"));
                    }

                    List<JSONObject> messageChains = new ArrayList<>();
                    for (int i = 0; i < id.size(); i++) {
                        messageChains.add(buildMessageChain("id:" + id.get(i) + "\n" + "text:" + text.get(i) + "\n" + "target:" + target.get(i) + "\n" + "type:" + type.get(i) + "\n" + "time:" + (hour.get(i) < 10 ? ("0" + hour.get(i)) : hour.get(i)) + ":" + (minute.get(i) < 10 ? ("0" + minute.get(i)) : minute.get(i)) + "\n", url.get(i)));
                    }

                    if (messageChains.size() == 0) {
                        return buildTextMessageChainsList("无对应定时消息");
                    }

                    return messageChains;

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            return buildTextMessageChainsList("权限不足");

        } else if (paras.length == 2) {
            //get one
            if (getPermission() >= GET_TIMER_PERMISSION) {
                try {
                    String sql = "SELECT * FROM timers WHERE target=" + paras[1];
                    ResultSet rs = database.executeQuery(sql);

                    ArrayList<Integer> id = new ArrayList<>();
                    ArrayList<String> text = new ArrayList<>();
                    ArrayList<String> url = new ArrayList<>();
                    ArrayList<Integer> hour = new ArrayList<>();
                    ArrayList<Integer> minute = new ArrayList<>();

                    while (rs.next()) {
                        id.add(rs.getInt("id"));
                        text.add(rs.getString("text"));
                        url.add((rs.getString("url")));
                        hour.add(rs.getInt("hour"));
                        minute.add(rs.getInt("minute"));
                    }

                    List<JSONObject> messageChains = new ArrayList<>();
                    for (int i = 0; i < id.size(); i++) {
                        messageChains.add(buildMessageChain("id:" + id.get(i) + "\n" + "text:" + text.get(i) + "\n" + "time:" + (hour.get(i) < 10 ? ("0" + hour.get(i)) : hour.get(i)) + ":" + (minute.get(i) < 10 ? ("0" + minute.get(i)) : minute.get(i)) + "\n", url.get(i)));
                    }

                    if (messageChains.size() == 0) {
                        return buildTextMessageChainsList("无对应定时消息");
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

    private List<JSONObject> delTimer() {
        final int DEL_GROUP_TIMER_PERMISSION = 2;

        if (getPermission() >= DEL_GROUP_TIMER_PERMISSION) {
            if (paras.length == 2) {
                String sql = "SELECT * FROM timers WHERE id=" + paras[1];

                try {
                    ResultSet rs = database.executeQuery(sql);
                    if (rs.next()) {

                        String type = rs.getString("type");
                        String text = rs.getString("text");
                        Long target = rs.getLong("target");
                        String time = (rs.getInt("hour") < 10 ? "0" + rs.getInt("hour") : rs.getInt("hour")) + ":" + (rs.getInt("minute") < 10 ? "0" + rs.getInt("minute") : rs.getInt("minute"));
                        String url = rs.getString("url");

                        sql = "DELETE FROM timers WHERE id=" + paras[1];
                        database.execute(sql);
                        List<JSONObject> messageChains = new ArrayList<>();
                        messageChains.add(buildMessageChain("删除成功\ntype:" + type + "\ntext:" + text + "\ntarget:" + target + "\ntime:" + time + "\nimage:", url));

                        timer.updateList();

                        return messageChains;
                    }

                    return buildTextMessageChainsList("删除失败，不存在id=" + paras[1] + "的定时信息");

                } catch (SQLException throwables) {
                    throwables.printStackTrace();

                }
            } else if (paras.length == 3 && paras[1].equals("q")) {
                String sql = "SELECT * FROM timers WHERE target=" + paras[2];
                boolean isEmpty = true;

                try {
                    ResultSet rs = database.executeQuery(sql);

                    isEmpty = !rs.next();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                sql = "DELETE FROM timers WHERE target=" + paras[2];

                try {
                    database.execute(sql);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                if (isEmpty) {
                    return buildTextMessageChainsList("删除失败，无对应定时消息");
                } else {
                    timer.updateList();
                    return buildTextMessageChainsList("删除成功，已删除target:" + paras[2] + "的全部定时消息");
                }

            }

            return buildTextMessageChainsList("命令错误");
        }

        return buildTextMessageChainsList("权限不足");
    }

    private List<JSONObject> addMessageForQ() {
        final int ADD_MESSAGE_FOR_Q = 10;

        if (getPermission() >= ADD_MESSAGE_FOR_Q) {
            if (paras.length == 2) {
                String sql = "INSERT INTO messages_for_q" +
                        "(text)" +
                        "VALUES" +
                        "(\"" + paras[1] + "\")";

                try {
                    database.execute(sql);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    return buildTextMessageChainsList("发生错误");
                }
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
