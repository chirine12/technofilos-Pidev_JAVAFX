package com.example.projetpi.Model;

@Entity
public class Assurance {

    @Id
    private int id;

    private int montant;
    private String delais;
    private String type;

    @OneToOne(mappedBy = "assurance")
    @JoinColumn(name = "id") // Spécifie la colonne de jointure
    private Contrat contrat;

    // Constructeur par défaut nécessaire pour JPA
    public Assurance() {
    }

    // Constructeur avec arguments
    public Assurance(int montant, String delais, String type) {
        this.montant = montant;
        this.delais = delais;
        this.type = type;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public String getDelais() {
        return delais;
    }

    public void setDelais(String delais) {
        this.delais = delais;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Contrat getContrat() {
        return contrat;
    }

    public void setContrat(Contrat contrat) {
        this.contrat = contrat;
    }
}
