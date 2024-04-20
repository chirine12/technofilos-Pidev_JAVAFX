package com.example.test.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Beneficiaire {
private int id;
private String nom , prenom;
private Long rib;


    public Beneficiaire(int id, String nom, String prenom, Long rib) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.rib = rib;
    }

    public Beneficiaire(String nom, String prenom, Long rib) {
        this.nom = nom;
        this.prenom = prenom;
        this.rib = rib;
    }

    public Beneficiaire() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Long getRib() {
        return rib;
    }

    public void setRib(Long rib) {
        this.rib = rib;
    }

    @Override
    public String toString() {
        return "Beneficiaire{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", rib=" + rib +
                '}';
    }
}
