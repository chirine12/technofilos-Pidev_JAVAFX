package com.example.test.service;

import java.sql.SQLException;
import java.util.List;

public interface ICRUD<T>{
    void create(T t,int id) throws SQLException;
    void update(T t) throws SQLException;
    void delete(int id) throws SQLException;
    List<T> read(int id) throws SQLException;


}
