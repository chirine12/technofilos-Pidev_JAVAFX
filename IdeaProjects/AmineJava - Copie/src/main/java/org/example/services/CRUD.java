package org.example.services;

import org.example.models.Credit;

import java.sql.SQLException;
import java.util.List;

public interface CRUD<T> {
    void create(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(int id) throws SQLException;
    List<T> read() throws SQLException;
    Credit findById(int id) throws SQLException;
    List<Credit> findByType(String type) throws SQLException;
    void generateCreditReport( String destPath);
}
