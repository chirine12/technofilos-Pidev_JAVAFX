package tn.esprit.model;
import java.util.Date;
public class Carte {
    private int id;
    private int num;
    private String nom;
    private Date dateexp;
    private int cvv;

    public Carte(){

    }

    public Carte(int num, String nom, Date dateexp, int cvv) {
        this.num = num;
        this.nom = nom;
        this.dateexp = dateexp;
        this.cvv = cvv;
    }

    public Carte(int id, int num, String nom, Date dateexp, int cvv) {
        this.id = id;
        this.num = num;
        this.nom = nom;
        this.dateexp = dateexp;
        this.cvv = cvv;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateexp() {
        return dateexp;
    }

    public void setDateexp(Date dateexp) {
        this.dateexp = dateexp;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }
}
