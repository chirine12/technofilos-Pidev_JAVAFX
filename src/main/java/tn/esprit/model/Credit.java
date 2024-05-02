
package tn.esprit.model;
import java.sql.Date;
public class Credit {
    private int id;
    private String type;
    private int montant;
    private float payement;
    private int duree;
    private Date datedeb;
    private Date datefin;
    private int typecreditId;

    public Credit() {
    }

    public Credit(int id, String type, int montant, float payement, int duree, Date datedeb, Date datefin, int typecreditId) {

        this.id = id;
        this.type = type;
        this.montant = montant;
        this.payement = payement;
        this.duree = duree;
        this.datedeb = datedeb;
        this.datefin = datefin;
        this.typecreditId = typecreditId;
    }

    public Credit(String type, int montant, float payement, int duree, Date datedeb, Date datefin, int typecreditId) {

        this.type = type;
        this.montant = montant;
        this.payement = payement;
        this.duree = duree;
        this.datedeb = datedeb;
        this.datefin = datefin;
        this.typecreditId = typecreditId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public float getPayement() {
        return payement;
    }

    public void setPayement(float payement) {
        this.payement = payement;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public Date getDatedeb() {
        return datedeb;
    }

    public void setDatedeb(Date datedeb) {
        this.datedeb = datedeb;
    }

    public Date getDatefin() {
        return datefin;
    }

    public void setDatefin(Date datefin) {
        this.datefin = datefin;
    }

    public int getTypecreditId() {
        return typecreditId;
    }

    public void setTypecreditId(int typecreditId) {
        this.typecreditId = typecreditId;
    }

    @Override
    public String toString() {
        return "Credit{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", montant=" + montant +
                ", payement=" + payement +
                ", duree=" + duree +
                ", datedeb=" + datedeb +
                ", datefin=" + datefin +
                ", typecreditId=" + typecreditId +
                '}';
    }
}
