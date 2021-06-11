package com.eleven.bot;

import java.sql.*;

public class Database {
    private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://localhost:3306/bot";

    private final String USER = "root";
    private final String PASS = "zhaoziqianQWE369";

    private Connection connection = null;
    private Statement statement = null;

    public Database() {
        try {
            Class.forName(JDBC_DRIVER);

            System.out.println("Connecting...");

            connection = DriverManager.getConnection(DB_URL, USER, PASS);

            statement = connection.createStatement();

            System.out.println("Connect Database Successfully.");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


}
