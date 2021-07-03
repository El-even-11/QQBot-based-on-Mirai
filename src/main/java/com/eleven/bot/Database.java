package com.eleven.bot;

import java.sql.*;

public class Database {
    private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://localhost:3306/bot";

    private final String USER = "root";
    private final String PASS = "";

    private Connection connection = null;
    private Statement statement = null;

    public Database() {
        try {
            Class.forName(JDBC_DRIVER);

            System.out.println("Connecting...");

            connection = DriverManager.getConnection(DB_URL, USER, PASS);

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            System.out.println("Connect Database Successfully");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean execute(String sql) throws SQLException {
        return statement.execute(sql);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        return statement.executeQuery(sql);
    }

    public String regularize(String s) {
        StringBuilder escaped = new StringBuilder(s);

        for (int i = 0; i < escaped.length(); i++) {
            if (escaped.charAt(i) == '\\' || escaped.charAt(i) == '\'' || escaped.charAt(i) == '\"') {
                escaped.insert(i, '\\');
                i++;
            }
        }

        return escaped.toString();
    }
}
