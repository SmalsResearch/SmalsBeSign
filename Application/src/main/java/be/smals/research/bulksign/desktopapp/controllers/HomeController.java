package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.abstracts.Controller;
import com.jfoenix.controls.JFXDialog;
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

    /**
     * Verify signed files button action - Used to change screen to Verify screen
     */
    @FXML public void handleVerifyButtonAction() {
        this.mainController.verifyMenuItemAction();
    }

    /**
     * Exit button action - Used to perform exit application
     */
    @FXML public void handleExitButtonAction() {
        this.mainController.exitMenuItemAction();
    }

    /**
     * Sign files button action - Used to change screen to Sign screen
     */
    @FXML public void handleSignButtonAction() {
        this.mainController.signMenuItemAction();
    }
}
