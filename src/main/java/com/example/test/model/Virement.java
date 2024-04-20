package com.example.test.model;
import  java.sql.Date;
import java.time.LocalDate;

public class Virement {
   private  int id ;
    private long source , destinataire ;

    private float montant ;
    private String motif;
    private LocalDate date ;

    public Virement() {
    }

    public Virement(int id, long source, long destinataire, float montant, String motif, LocalDate date) {
        this.id = id;
        this.source = source;
        this.destinataire = destinataire;
        this.montant = montant;
        this.motif = motif;
        this.date = date;
    }

    public Virement(int id, long source, long destinataire, float montant, String motif) {
        this.id = id;
        this.source = source;
        this.destinataire = destinataire;
        this.montant = montant;
        this.motif = motif;
    }

    public Virement(long source, long destinataire, float montant, String motif) {
        this.source = source;
        this.destinataire = destinataire;
        this.montant = montant;
        this.motif = motif;
    }

    public Virement(long source, long destinataire, float montant, String motif, LocalDate date) {

        this.source = source;
        this.destinataire = destinataire;
        this.montant = montant;
        this.motif = motif;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSource() {
        return source;
    }

    public void setSource(long source) {
        this.source = source;
    }

    public long getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(long destinataire) {
        this.destinataire = destinataire;
    }

    public float getMontant() {
        return montant;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Virement{" +
                "id=" + id +
                ", source=" + source +
                ", destinataire=" + destinataire +
                ", montant=" + montant +
                ", motif='" + motif + '\'' +
                ", date=" + date +
                '}';
    }

}
