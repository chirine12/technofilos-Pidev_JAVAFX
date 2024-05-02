package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.controllers.AdminSidebarController;
import tn.esprit.controllers.Maindashbord;
import tn.esprit.controllers.SidebarController;
import tn.esprit.service.CompteepService;
import tn.esprit.service.TypetauxService;
import tn.esprit.service.calculinteretService;
// Supposons une implémentation concrète de TypetauxService

import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
      FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/FXML/maindashbord.fxml"));
        AnchorPane root = mainLoader.load();
        Maindashbord mainController = mainLoader.getController();

        // Après le chargement du fichier FXML principal, la barre latérale est déjà incluse,
        // Nous devons récupérer son contrôleur et définir le contrôleur principal
        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/FXML/SidebarClient.fxml"));
        Parent sidebar = sidebarLoader.load();

        SidebarController sidebarController = sidebarLoader.getController();
        sidebarController.setMainController(mainController);

        // Obtenez une référence à l'élément racine de l'interface utilisateur principale
        AnchorPane mainLayout = root;

        // Ajouter la barre latérale à l'interface utilisateur principale
        mainLayout.getChildren().add(sidebar);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
/*
         // nouveau start de ladmin
         FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/FXML/adminmaindashbord.fxml"));
        AnchorPane root = mainLoader.load();
        Maindashbord mainController = mainLoader.getController();

        // Après le chargement du fichier FXML principal, la barre latérale est déjà incluse,
        // Nous devons récupérer son contrôleur et définir le contrôleur principal
        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/FXML/AdminSidebar.fxml"));
        Parent sidebar = sidebarLoader.load();

        AdminSidebarController AdminsidebarController = sidebarLoader.getController();
        AdminsidebarController.setMainController(mainController);

        // Obtenez une référence à l'élément racine de l'interface utilisateur principale
        AnchorPane mainLayout = root;

        // Ajouter la barre latérale à l'interface utilisateur principale
        mainLayout.getChildren().add(sidebar);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();

/*
  //ancienstart admin
       //run pour admin
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/admindashbord.fxml"));
        Parent root = loader.load();

        // Configurer la scène et le stage
        Scene scene = new Scene(root);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
*/
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
