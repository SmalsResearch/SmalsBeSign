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

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void exitMenuItemAction() {
        Alert exitAlert = new Alert(Alert.AlertType.WARNING, "You are about to leave...", ButtonType.YES, ButtonType.CANCEL);
        exitAlert.setTitle("Exit the application");
        exitAlert.setHeaderText("Are you sure ?");
        Optional<ButtonType> choice = exitAlert.showAndWait();
        if (choice.isPresent() && choice.get() == ButtonType.YES)
            Platform.exit();
        else
            exitAlert.close();

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

    public void testMenuItemAction() {
        FXMLLoader testViewLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/test.fxml"));
        try {
            Parent testPane = testViewLoader.load();
            root.setCenter(testPane);

            //TestController testController = testViewLoader.getController();
            //testController.setStage(this.stage);
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
}
