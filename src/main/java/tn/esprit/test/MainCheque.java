package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class MainCheque extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Cheque.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent, 720, 480);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style1.css")).toExternalForm());

        // Set the resolution
        stage.setMinWidth(1200);
        stage.setMinHeight(700);
        stage.setMaxWidth(1200);
        stage.setMaxHeight(700);

        stage.setTitle("Cheque");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
