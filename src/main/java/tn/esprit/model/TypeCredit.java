package tn.esprit.model;
public class TypeCredit {
    private int id;
    private String nom;
    private Float taux;

    public TypeCredit() {
    }

    public TypeCredit(int id, String nom, Float taux) {

        this.id = id;
        this.nom = nom;
        this.taux = taux;
    }

    public TypeCredit(String nom, Float taux) {
        this.nom = nom;
        this.taux = taux;
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

    public Float getTaux() {
        return taux;
    }

    public void setTaux(Float taux) {
        this.taux = taux;
    }
}