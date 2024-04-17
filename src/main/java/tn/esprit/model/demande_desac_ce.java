package tn.esprit.model;

public class demande_desac_ce {

    private long clientId;
    private int id; // Identifiant unique de la demande
    private long compteepId; // Identifiant du compte associé à la demande
    private String raison;
    public demande_desac_ce () {
    }

    // Constructeur avec tous les paramètres
    public demande_desac_ce (int id, long clientId,long compteepId, String raison) {
        this.id = id;
        this.clientId = clientId;
        this.compteepId = compteepId;
        this.raison = raison;
    }
    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getCompteepId() {
        return compteepId;
    }

    public void setCompteepId(long compteepId) {
        this.compteepId = compteepId;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    // Méthode toString pour représenter l'objet sous forme de chaîne
    @Override
    public String toString() {
        return "DemandeDesactivation{" +
                "id=" + id +
                ", compteepId=" + compteepId +
                ", raison='" + raison + '\'' +
                '}';
    }
}
