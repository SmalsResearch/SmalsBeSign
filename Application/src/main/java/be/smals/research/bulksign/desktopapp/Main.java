package be.smals.research.bulksign.desktopapp;


import be.smals.research.bulksign.desktopapp.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.security.Security;

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

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu aboutMenu = new Menu("About");
        MenuItem exitMenuItem = new MenuItem("Exit...");
        fileMenu.getItems().addAll(exitMenuItem);
        menuBar.getMenus().addAll(fileMenu);
        root.setTop(menuBar);

        MainController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setScene(new Scene(root, 600, 320));
        primaryStage.show();
    }
}
