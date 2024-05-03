package com.example.test.test;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.test.HelloApplication;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("virementclient.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        System.setProperty("jdk.xml.allowInsecureParser", "true");

        launch();
    }
}
/*public class Main {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("CategorieInterface.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) throws SQLException {
        /*SQLConnector db1 = new SQLConnector().getInstance();*/

       /* SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            java.util.Date utilDate = dateFormat.parse("12/02/2024");
            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            Virement v1 = new Virement(15,123, 258, 500, "bonjour", sqlDate); // Use sqlDate
            VirementService vs1 = new VirementService();

            List<Virement> l1 = vs1.read();
            l1.stream()
                    .map(Virement::toString)
                    .forEach(System.out::println);

        } catch (ParseException | SQLException e) {
            e.printStackTrace(); // Handle parsing exception or SQL exception
        }
        Beneficiaire b1 =new Beneficiaire("chirine","ourariii",12345678910L);
        Beneficiaire b2 =new Beneficiaire(5,"chichi","ourariii",12345678910L);
       BeneficiaireService bs1 = new BeneficiaireService();
       /* bs1.create(b1)*/
        /*bs1.update(b1);*/
        /* bs1.delete(5);*/
       /* List<Beneficiaire> l2 = bs1.read();
        l2.stream()
                .map(Beneficiaire::toString)
                .forEach(System.out::println);

    }*/



