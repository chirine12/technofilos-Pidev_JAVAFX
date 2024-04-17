package tn.esprit.model;

import java.sql.Date;

public class Compteep {
    private int clientId;
    private int id;
    private Long rib;
    private double solde;
    private String type;
    private Date dateouv;
    private String description;
    private Boolean etat;
    private int typeTauxId; // Champ pour stocker l'ID de TypeTaux, utilisé pour lier avec la table TypeTaux

    // Constructeurs
    public Compteep(int id, Long rib, double solde, String type, Date dateouv, String description, Boolean etat, int typeTauxId) {
        this.id = id;
        this.rib = rib;
        this.solde = solde;
        this.type = type;
        this.dateouv = dateouv;
        this.description = description;
        this.etat = etat;
        this.typeTauxId = typeTauxId;
    }

    // Mise à jour pour inclure typeTauxId
    public Compteep(Long rib, double solde, String type, Date dateouv, String description, Boolean etat, int typeTauxId) {
        this.rib = rib;
        this.solde = solde;
        this.type = type;
        this.dateouv = dateouv;
        this.description = description;
        this.etat = etat;
        this.typeTauxId = typeTauxId; // Ajout du paramètre typeTauxId
    }

    // Constructeur par défaut avec initialisation de valeurs par défaut si nécessaire
    public Compteep() {
        this.rib = 0L;
        this.solde = 0.0;
        this.type = "";
        this.dateouv = new Date(System.currentTimeMillis()); // Date courante comme valeur par défaut
        this.description = "";
        this.etat = false;
        this.typeTauxId = 0; // Initialisation par défaut de typeTauxId
    }



    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getRib() {
        return rib;
    }

    public void setRib(Long rib) {
        this.rib = rib;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDateouv() {
        return dateouv;
    }

    public void setDateouv(Date dateouv) {
        this.dateouv = dateouv;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEtat() {
        return etat;
    }

    public void setEtat(Boolean etat) {
        this.etat = etat;
    }

    public int getTypeTauxId() {
        return typeTauxId;
    }

    public void setTypeTauxId(int typeTauxId) {
        this.typeTauxId = typeTauxId;
    }

    @Override
    public String toString() {
        return "Compteep{" +
                "id=" + id +
                ", rib=" + rib +
                ", solde=" + solde +
                ", type='" + type + '\'' +
                ", dateouv=" + dateouv +
                ", description='" + description + '\'' +
                ", etat=" + etat +
                ", typeTauxId=" + typeTauxId +
                '}';
    }
}
