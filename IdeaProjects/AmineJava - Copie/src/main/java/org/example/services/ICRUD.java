package org.example.services;

import org.example.models.TypeCredit;

import java.sql.SQLException;
import java.util.List;
public interface ICRUD<T> {
    void create(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(int id) throws SQLException;
    List<T> read() throws SQLException;
    TypeCredit findById1(int id) throws SQLException;
}
