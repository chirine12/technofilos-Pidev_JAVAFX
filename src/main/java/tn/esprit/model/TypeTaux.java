package tn.esprit.model;

public class TypeTaux {

    private int id;

    private String type;
    private double taux;

    public TypeTaux(int id, String type, double taux) {
        this.id = id;
        this.type = type;
        this.taux = taux;
    }


    public int getId() {
        return id;
    }

    public TypeTaux(String type, double taux) {
        this.type = type;
        this.taux = taux;
    }

    public String getType() {
        return type;
    }

    public double getTaux() {
        return taux;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeTaux() {
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTaux(double taux) {
        this.taux = taux;
    }
}
