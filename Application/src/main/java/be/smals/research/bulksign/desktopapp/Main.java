package be.smals.research.bulksign.desktopapp;


import be.smals.research.bulksign.desktopapp.controllers.MainController;
import be.smals.research.bulksign.desktopapp.controllers.SignController;
import be.smals.research.bulksign.desktopapp.controllers.VerifyController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.Security;
import java.util.Optional;

/**
 * Created by kova on 26/07/2016.
 */
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
        Menu aboutMenu          = new Menu("About");
        Menu taskMenu           = new Menu("Task");
        MenuItem exitMenuItem   = new MenuItem("Exit...");
        MenuItem signMenuItem   = new MenuItem("Sign");
        MenuItem verifyMenuItem = new MenuItem("Verify");
        fileMenu.getItems().addAll(exitMenuItem);
        taskMenu.getItems().addAll(signMenuItem, verifyMenuItem);
        menuBar.getMenus().addAll(fileMenu, taskMenu, aboutMenu);

        root.setTop(menuBar);

        exitMenuItem.setOnAction(event -> {
            Alert exitAlert = new Alert(Alert.AlertType.WARNING, "You are about to leave...", ButtonType.YES, ButtonType.CANCEL);
            exitAlert.setTitle("Exit the application");
            exitAlert.setHeaderText("Are you sure ?");
            Optional<ButtonType> choice = exitAlert.showAndWait();
            if (choice.get() == ButtonType.YES)
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

        MainController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setScene(new Scene(root, 600, 320));
        primaryStage.show();
    }
}
