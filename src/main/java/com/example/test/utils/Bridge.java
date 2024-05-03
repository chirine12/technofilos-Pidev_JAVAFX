package com.example.test.utils;

public class Bridge {
    public void log(String message) {
        System.out.println("JS log: " + message);
    }

    public void error(String message) {
        System.err.println("JS error: " + message);
    }
}
