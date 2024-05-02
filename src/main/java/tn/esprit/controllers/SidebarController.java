package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SidebarController {
    private Maindashbord mainController;


    // Method to set the main controller
    public void setMainController(Maindashbord mainController) {
        this.mainController = mainController;
    }


    @FXML
    private void handleHome() throws IOException {

        FXMLLoader clientdashbord = new FXMLLoader(getClass().getResource("/FXML/clientdashbord.fxml"));
        Parent sidebar = clientdashbord.load();
        ComptepClientController clientdashbordController = clientdashbord.getController();
        clientdashbordController.setMainController(mainController);
        mainController.setContenu((sidebar));


    }

    @FXML
    private void credit() throws IOException {

        FXMLLoader credit= new FXMLLoader(getClass().getResource("/FXML/Credit.fxml"));
        Parent sidebar = credit.load();
        CreditController creditController = credit.getController();
        creditController .setMainController(mainController);
        mainController.setContenu((sidebar));


    }


    @FXML
    private void handleSettings() throws IOException {

        mainController.changeContent("/FXML/admindashbord.fxml");
    }


}

