package tn.esprit.model;

import java.util.Date;

public class Cheque {
    private int id;
    private int num;
    private int numcompte;
    private float montant;
    private Date date;
public Cheque(){

}
    public Cheque(int id, int num, int numcompte, float montant, Date date) {
        this.id = id;
        this.num = num;
        this.numcompte = numcompte;
        this.montant = montant;
        this.date = date;
    }

    public Cheque(int num, int numcompte, float montant, Date date) {
        this.num = num;
        this.numcompte = numcompte;
        this.montant = montant;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNumcompte() {
        return numcompte;
    }

    public void setNumcompte(int numcompte) {
        this.numcompte = numcompte;
    }

    public float getMontant() {
        return montant;
    }

    public void setMontant(float montant) {
        this.montant = montant;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
