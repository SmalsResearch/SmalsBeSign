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
    public void initController(MainController mainController, Stage stage) {
        super.initController(mainController, stage);
        // Setup dialogs
        this.infoDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.errorDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.successDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
    }
    @FXML public void handleVerifyButtonAction(ActionEvent event) {
        this.mainController.verifyMenuItemAction();
    }
    @FXML public void handleExitButtonAction(ActionEvent event) {
        this.mainController.exitMenuItemAction();
    }
    @FXML public void handleSignButtonAction(ActionEvent event) {
        this.mainController.signMenuItemAction();
    }
}
