package be.smals.research.bulksign.desktopapp.controllers;

import be.fedict.commons.eid.client.BeIDCards;
import be.smals.research.bulksign.desktopapp.services.EIDService;
import be.smals.research.bulksign.desktopapp.services.LoggerService;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main screen controller
 *
 * Handles events from main screen
 */
public class MainController extends Controller{

    @FXML private StackPane masterPane;
    @FXML private BorderPane root;
    @FXML private JFXDialog exitDialog;
    @FXML private JFXDialog aboutDialog;
    @FXML private JFXDialog waitingDialog;

    /**
     * Constructor
     */
    public MainController() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initController(MainController mainController, Stage stage) {
        super.initController(this, stage);
        aboutDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
    }

    /**
     * Returns the root pane of Main Screen
     *
     * @return a BorderPane
     */
    public BorderPane getRoot () {
        return this.root;
    }

    // ----- MenuItems action ------------------------------------------------------------------------------------------

    /**
     * Exit Application MenuItem action
     */
    public void exitMenuItemAction () {
        exitDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        exitDialog.show(masterPane);
    }
    /**
     * Sign MenuItem action - Leads to Sign screen
     */
    public void signMenuItemAction () {
        FXMLLoader signViewLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/sign.fxml"));
        try {
            Parent signPane = signViewLoader.load();
            root.setCenter(signPane);

            SignController signController = signViewLoader.getController();
            signController.initController(this.mainController, this.stage);

            BeIDCards beIDCards = new BeIDCards(new LoggerService(), signController);
            EIDService.getInstance().setBeID(beIDCards);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * Verify MenuItem - Leads to Verify screen
     */
    public void verifyMenuItemAction () {
        FXMLLoader verifyViewLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/verify.fxml"));
        try {
            Parent verifyPane = verifyViewLoader.load();
            root.setCenter(verifyPane);

            VerifyController verifyController = verifyViewLoader.getController();
            verifyController.initController(this.mainController, this.stage);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * Home MenuItem - Leads back to home screen
     */
    public void homeMenuItemAction () {
        FXMLLoader homeViewLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/home.fxml"));
        try {
            Parent homePane = homeViewLoader.load();
            root.setCenter(homePane);

            HomeController homeController = homeViewLoader.getController();
            homeController.initController(this.mainController, this.stage);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * About MenuItem - Displays About dialog
     */
    public void aboutMenuItemAction () {
        this.aboutDialog.show(masterPane);
    }

    // ----- Dialog actions --------------------------------------------------------------------------------------------
    /**
     * Exit application dialog action - Cancels the exit request
     */
    @FXML private void handleCancelDialogButtonAction() {
        this.exitDialog.close();
    }
    /**
     * Exit application dialog action - Exit for real
     */
    @FXML private void handleExitAppButtonAction () {
        System.exit(0);
        Platform.exit();
    }
    /**
     * Closes About dialog
     */
    @FXML private void handleCloseAboutDialogButtonAction () {
        this.aboutDialog.close();
    }
}
