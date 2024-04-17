package tn.esprit.service;

import tn.esprit.model.Compteep;
import tn.esprit.model.TypeTaux;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class calculinteretService {
    private CompteepService compteepService;
    private TypetauxService typeTauxService;

    public calculinteretService(CompteepService compteepService, TypetauxService typeTauxService) {
        this.compteepService = compteepService;
        this.typeTauxService = typeTauxService;
    }

    public void calculateInterest() throws SQLException {
        List<Compteep> comptes = compteepService.findAllActive();
        for (Compteep compte : comptes) {
            System.out.println("Traitement du compte : " + compte.getId());

            // Récupérer l'identifiant du type de taux associé au compte
            int typeTauxId = compte.getTypeTauxId();

            // Utiliser l'identifiant pour récupérer le type de taux correspondant
            TypeTaux typeTaux = typeTauxService.findById(typeTauxId);

            if (typeTaux == null) {
                System.out.println("Type de taux non trouvé pour le compte : " + compte.getId());
                continue; // Passe au compte suivant s'il n'y a pas de type de taux trouvé
            }

            double soldeActuel = compte.getSolde();
            System.out.println("Solde actuel du compte : " + soldeActuel);

            double tauxInteret = typeTaux.getTaux();
            System.out.println("Taux d'intérêt associé au compte : " + tauxInteret);

            double interetMensuel = soldeActuel * (tauxInteret / 100);
            System.out.println("Intérêt mensuel calculé : " + interetMensuel);

            double nouveauSolde = soldeActuel + interetMensuel;
            System.out.println("Nouveau solde calculé : " + nouveauSolde);

            compte.setSolde(nouveauSolde);
            System.out.println("Mise à jour du solde du compte...");

            compteepService.update(compte);
            System.out.println("Compte mis à jour avec le nouveau solde.");
        }
    }



    public void startMonthlyInterestCalculation() {
        // Créer un Timer
        Timer timer = new Timer();

        // Obtenir la date actuelle
        Calendar now = Calendar.getInstance();

        // Fixer la date d'exécution au début du prochain mois
        Calendar nextMonth = Calendar.getInstance();
        nextMonth.add(Calendar.MONTH, 1);
        nextMonth.set(Calendar.DAY_OF_MONTH, 1);
        nextMonth.set(Calendar.HOUR_OF_DAY, 0);
        nextMonth.set(Calendar.MINUTE, 0);
        nextMonth.set(Calendar.SECOND, 0);
        nextMonth.set(Calendar.MILLISECOND, 0);

        // Calculer le délai avant la première exécution
        long delay = nextMonth.getTimeInMillis() - now.getTimeInMillis();

        // Planifier la tâche pour être exécutée au début de chaque mois
        timer.schedule(new MonthlyInterestTask(), delay, 30 * 24 * 60 * 60 * 1000); // Répéter chaque 30 jours
    }

    // Classe interne représentant la tâche de calcul des intérêts
    private class MonthlyInterestTask extends TimerTask {
        @Override
        public void run() {
            try {
                calculateInterest(); // Appel de la méthode de calcul des intérêts
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
