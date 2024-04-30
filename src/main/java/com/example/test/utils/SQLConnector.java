package com.example.test.utils;

import java.sql.*;

public class      SQLConnector {

    private final String URL = "jdbc:mysql://localhost:3306/ebank";
    private final String USER = "root";
    private final String PASS = "";
    private Connection connection;

    private static SQLConnector instance;

    public SQLConnector(){
        try {
            connection = DriverManager.getConnection(URL,USER,PASS);
            System.out.println("Connection established");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static SQLConnector getInstance() {
        if(instance == null)
            instance = new SQLConnector();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException ex) {
            System.out.println("Error while closing result set: " + ex.getMessage());
        }

        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException ex) {
            System.out.println("Error while closing statement: " + ex.getMessage());
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println("Error while closing database connection: " + ex.getMessage());
        }
    }
}