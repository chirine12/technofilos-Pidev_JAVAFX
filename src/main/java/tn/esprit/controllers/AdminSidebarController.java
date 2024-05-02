package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import java.io.IOException;

public class AdminSidebarController {





    private Maindashbord mainController;


    // Method to set the main controller
    public void setMainController(Maindashbord mainController) {
        this.mainController = mainController;
    }





    @FXML
    private void handleHome() throws IOException {

        FXMLLoader admindashbord = new FXMLLoader(getClass().getResource("/FXML/admindashbord.fxml"));
        Parent sidebar = admindashbord.load();
        compteepAdminController admindashbordController = admindashbord.getController();
        admindashbordController.setMainController(mainController);
        mainController.setContenu((sidebar));


    }

    @FXML
    private void typecredit() throws IOException {

        FXMLLoader typecreditdashbord = new FXMLLoader(getClass().getResource("/FXML/typeCredit.fxml"));
        Parent sidebar = typecreditdashbord.load();
        TypeCreditController typecreditdashbordController = typecreditdashbord.getController();
        typecreditdashbordController.setMainController(mainController);
        mainController.setContenu((sidebar));


    }




}

