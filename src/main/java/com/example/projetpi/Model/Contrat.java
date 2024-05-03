package com.example.projetpi.Model;

import java.time.LocalDate;

public class Contrat {
    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String Signature;
    private String type;
    private LocalDate datedebut;
    private LocalDate datefin;
    private int numero;
    private String imageBase64; // Nouvelle propriété pour stocker l'image en base64



    public Contrat(int id, String signature, String type, LocalDate datedebut, LocalDate datefin, int numero) {
        this.id = id;
        this.Signature = signature;
        this.type = type;
        this.datedebut = datedebut;
        this.datefin = datefin;
        this.numero = numero;
    }


    public Contrat(String signature, String type, LocalDate datedebut, LocalDate datefin, int numero) {
    }

    public String getSignature() {
        return Signature;
    }

    public void setSignature(String Signature) {
        this.Signature = Signature;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDatedebut() {
        return datedebut;
    }

    public void setDatedebut(LocalDate datedebut) {
        this.datedebut = datedebut;
    }

    public LocalDate getDatefin() {
        return datefin;
    }

    public void setDatefin(LocalDate datefin) {
        this.datefin = datefin;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getId() {
        return id ;

    }
    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    @Override
    public String toString() {
        return "Contrat{" +
                "id=" + id +
                ", Signature='" + Signature + '\'' +
                ", type='" + type + '\'' +
                ", datedebut=" + datedebut +
                ", datefin=" + datefin +
                ", numero=" + numero +
                ", imageBase64='" + imageBase64 + '\'' +
                '}';
    }
}


