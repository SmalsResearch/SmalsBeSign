package be.smals.research.bulksign.desktopapp.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Main screen controller
 *
 * Handles events from main screen
 */
public class MainController {

    private Stage stage;
    @FXML
    private BorderPane root;

    /**
     * Constructor
     */
    public MainController() {
    }
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void exitMenuItemAction() {
        exitDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        exitDialog.show(masterPane);
    }

    public void signMenuItemAction() {
        FXMLLoader signViewLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/sign.fxml"));
        try {
            Parent signPane = signViewLoader.load();
            root.setCenter(signPane);

            SignController signController = signViewLoader.getController();
            signController.setStage(this.stage);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public void verifyMenuItemAction() {
        FXMLLoader verifyViewLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/verify.fxml"));
        try {
            Parent verifyPane = verifyViewLoader.load();
            root.setCenter(verifyPane);

            VerifyController verifyController = verifyViewLoader.getController();
            verifyController.setStage(this.stage);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public BorderPane getRoot () {
        return this.root;
    }
    // ===== Dialog actions ============================================================================================
    @FXML private void handleCancelDialogButtonAction(ActionEvent event) {
        this.exitDialog.close();
    }
    @FXML private void handleExitAppButtonAction (ActionEvent event) {
        System.exit(0);
        Platform.exit();
    }
}
