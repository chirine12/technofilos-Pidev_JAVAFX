package tn.esprit.service;

import tn.esprit.model.Credit;
import tn.esprit.model.TypeCredit;

import java.sql.SQLException;
import java.util.List;
public interface ICRUDCredit<T> {
    void create(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(int id) throws SQLException;
    List<T> read() throws SQLException;
    TypeCredit findById1(int id) throws SQLException;
    int findTypeCreditByName(String typeName)throws SQLException;
    float findTauxById(int typeCreditId) throws SQLException;
    List<TypeCredit> findByType(String type) throws SQLException;
}