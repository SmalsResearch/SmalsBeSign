package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.abstracts.Controller;
import com.jfoenix.controls.JFXDialog;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Home screen controller
 *
 * Handles events from Home view
 */
public class HomeController extends Controller{

    private Stage stage;

    @FXML private Pane readerPane;
    @FXML private StackPane masterSign;
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
        this.stage = stage;
        // Setup dialogs
        this.infoDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.errorDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.successDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
    }

}
