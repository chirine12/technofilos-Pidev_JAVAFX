package com.example.projetpi.utils;

        import java.sql.*;

public class DBconnexion {
    private static final String URL = "jdbc:mysql://localhost:3306/gestiondonjava";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static DBconnexion instance;
    private Connection cnx;

    private DBconnexion() {
        try {
            this.cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/ebank", "root", "");
            System.out.println("Connected To DATABASE !");
        } catch (SQLException var2) {
            SQLException e = var2;
            System.err.println("Error: " + e.getMessage());
        }

    }

    public static DBconnexion getInstance() {
        if (instance == null) {
            instance = new DBconnexion();
        }

        return instance;
    }

    public static Connection getCon() {
        return getInstance().getCnx();
    }



    public Connection getCnx() {
        return this.cnx;
    }
}