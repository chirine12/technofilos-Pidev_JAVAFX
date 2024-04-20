package tn.esprit.service;

import tn.esprit.model.Carte;
import tn.esprit.model.Cheque;

import java.sql.SQLException;
import java.util.List;

public interface IICRUD<T> {
    void create(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(int id) throws SQLException;
    List<T> read() throws SQLException;
    Cheque findById(int id) throws SQLException;
}
