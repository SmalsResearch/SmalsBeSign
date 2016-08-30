package be.smals.research.bulksign.desktopapp.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;

/**
 * Controllers superclass
 */
public abstract class Controller {
    protected Stage stage;
    protected MainController mainController;
    protected File lastDirectory;

    /**
     * Gives to controllers access to the main stage and controller
     *
     * @param mainController
     * @param stage
     */
    public void initController(MainController mainController, Stage stage) {
        this.mainController     = mainController;
        this.stage              = stage;
    }

    /**
     * Displays a classic InfoDialog
     *
     * @param dialog
     * @param masterPane
     * @param title
     * @param message
     */
    public void showInfoDialog (JFXDialog dialog, StackPane masterPane, String title, String message) {
        dialog.show(masterPane);
        Label titleLabel        = (Label) this.stage.getScene().lookup("#infoDialogTitle");
        Label bodyLabel         = (Label) this.stage.getScene().lookup("#infoDialogBody");
        JFXButton closeButton   = (JFXButton) this.stage.getScene().lookup("#closeInfoDialogButton");
        closeButton.setOnAction(event -> dialog.close());

        titleLabel.setText(title);
        bodyLabel.setText(message);
    }
    /**
     * Displays a classic SuccessDialog
     *
     * @param dialog
     * @param masterPane
     * @param title
     * @param message
     */
    public void showSuccessDialog (JFXDialog dialog, StackPane masterPane, String title, String message) {
        dialog.show(masterPane);
        Label titleLabel     = (Label) this.stage.getScene().lookup("#successDialogTitle");
        Label bodyLabel      = (Label) this.stage.getScene().lookup("#successDialogBody");
        JFXButton closeButton   = (JFXButton) this.stage.getScene().lookup("#closeSuccessDialogButton");
        closeButton.setOnAction(event -> dialog.close());

        titleLabel.setText(title);
        bodyLabel.setText(message);
    }
    /**
     * Displays a classic ErrorDialog
     *
     * @param dialog
     * @param masterPane
     * @param title
     * @param message
     */
    public void showErrorDialog (JFXDialog dialog, StackPane masterPane, String title, String message) {
        dialog.show(masterPane);
        Label titleLabel     = (Label) this.stage.getScene().lookup("#errorDialogTitle");
        Label bodyLabel      = (Label) this.stage.getScene().lookup("#errorDialogBody");
        JFXButton closeButton   = (JFXButton) this.stage.getScene().lookup("#closeErrorDialogButton");
        closeButton.setOnAction(event -> dialog.close());

        titleLabel.setText(title);
        bodyLabel.setText(message);
    }

    /**
     * Displays a waiting dialog
     *
     * @param dialog
     * @param masterPane
     * @param message
     */
    public void showWaitingDialog (JFXDialog dialog, StackPane masterPane, String message) {
        dialog.show(masterPane);
        Label messageLabel         = (Label) this.stage.getScene().lookup("#waitingDialogMessage");

        JFXButton closeButton   = (JFXButton) this.stage.getScene().lookup("#closeWaitingDialogButton");
        if (closeButton != null)
            closeButton.setOnAction(event -> dialog.close());

        messageLabel.setText(message);
    }
    public void updateWaitingDialogMessage(String message) {
        Label messageLabel         = (Label) this.stage.getScene().lookup("#waitingDialogMessage");
        messageLabel.setText(message);
    }
}
