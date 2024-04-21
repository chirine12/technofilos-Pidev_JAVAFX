package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tn.esprit.service.CompteepService;
import tn.esprit.service.TypetauxService;
import tn.esprit.service.calculinteretService;
 // Supposons une implémentation concrète de TypetauxService

import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/admindashbord.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Comptes Epargnes");
        stage.setScene(scene);
        stage.show();

        // Instancier les services nécessaires
        CompteepService compteepService = new CompteepService(/* arguments du constructeur */);
        TypetauxService TypetauxService = new TypetauxService(/* arguments du constructeur */);

        // Instancier le service de calcul d'intérêts
        calculinteretService service = new calculinteretService(compteepService, TypetauxService);

        try {
            // Appeler la méthode pour calculer les intérêts
            service.calculateInterest();
            System.out.println("Calcul des intérêts terminé avec succès.");
        } catch (SQLException e) {
            System.err.println("Une erreur s'est produite lors du calcul des intérêts : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
