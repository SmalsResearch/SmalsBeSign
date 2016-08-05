package be.smals.research.bulksign.desktopapp;


import be.smals.research.bulksign.desktopapp.controllers.MainController;
import be.smals.research.bulksign.desktopapp.controllers.SignController;
import be.smals.research.bulksign.desktopapp.controllers.VerifyController;
import be.smals.research.bulksign.desktopapp.utilities.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.Security;
import java.util.Optional;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        FXMLLoader loader   = new FXMLLoader(getClass().getClassLoader().getResource("views/main.fxml"));
        BorderPane root       = loader.load();
        primaryStage.setTitle("BulkSign Desktop");

        MenuBar menuBar         = new MenuBar();
        Menu fileMenu           = new Menu("File");
        Menu taskMenu           = new Menu("Task");
        Menu signerMenu         = new Menu("Signer");
        menuBar.getMenus().addAll(fileMenu, taskMenu, signerMenu);
        MenuItem exitMenuItem   = new MenuItem("Exit...");
        MenuItem signMenuItem   = new MenuItem("Sign");
        MenuItem verifyMenuItem = new MenuItem("Verify");
        final ToggleGroup signerGroup   = new ToggleGroup();
        RadioMenuItem eidMenuItem       = new RadioMenuItem("eID");
        RadioMenuItem mockMenuItem      = new RadioMenuItem("Mock");
        eidMenuItem.setUserData(Settings.Signer.EID);
        mockMenuItem.setUserData(Settings.Signer.MOCK);
        eidMenuItem.setToggleGroup(signerGroup);
        mockMenuItem.setToggleGroup(signerGroup);
        signerGroup.selectToggle(mockMenuItem);
        fileMenu.getItems().addAll(exitMenuItem);
        taskMenu.getItems().addAll(signMenuItem, verifyMenuItem);
        signerMenu.getItems().addAll(mockMenuItem, eidMenuItem);

        root.setTop(menuBar);

        exitMenuItem.setOnAction(event -> {
            Alert exitAlert = new Alert(Alert.AlertType.WARNING, "You are about to leave...", ButtonType.YES, ButtonType.CANCEL);
            exitAlert.setTitle("Exit the application");
            exitAlert.setHeaderText("Are you sure ?");
            Optional<ButtonType> choice = exitAlert.showAndWait();
            if (choice.isPresent() && choice.get() == ButtonType.YES)
                Platform.exit();
            else
                exitAlert.close();
        });
        signMenuItem.setOnAction( event -> {
            FXMLLoader signViewLoader   = new FXMLLoader(getClass().getClassLoader().getResource("views/sign.fxml"));
            try {
                Parent signPane       = signViewLoader.load();
                root.setCenter(signPane);

                SignController controller = signViewLoader.getController();
                controller.setStage(primaryStage);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        verifyMenuItem.setOnAction( event -> {
            FXMLLoader verifyViewLoader   = new FXMLLoader(getClass().getClassLoader().getResource("views/verify.fxml"));
            try {
                Parent verifyPane       = verifyViewLoader.load();
                root.setCenter(verifyPane);

                VerifyController controller = verifyViewLoader.getController();
                controller.setStage(primaryStage);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        signerGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (signerGroup.getSelectedToggle() != null) {
                Settings.Signer signer = (Settings.Signer) signerGroup.getSelectedToggle().getUserData();
                Settings.getInstance().setSigner(signer);
            }
        });

        MainController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setScene(new Scene(root, 800, 480));
        primaryStage.show();
    }
}
