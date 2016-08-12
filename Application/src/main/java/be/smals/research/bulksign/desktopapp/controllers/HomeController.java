package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.abstracts.Controller;
import com.jfoenix.controls.JFXDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Home screen controller
 *
 * Handles events from Home view
 */
public class HomeController extends Controller{

    @FXML private StackPane masterHome;
    @FXML private JFXDialog infoDialog;
    @FXML private JFXDialog errorDialog;
    @FXML private JFXDialog successDialog;

    /**
     * Constructor
     */
    public HomeController() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStage (Stage stage) {
        super.setStage(stage);
        // Setup dialogs
        this.infoDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.errorDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.successDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
    }
    @FXML public void handleSignButtonAction(ActionEvent event) {
    }
    @FXML public void handleVerifyButtonAction(ActionEvent event) {}
    @FXML public void handleExitButtonAction(ActionEvent event) {}
}
