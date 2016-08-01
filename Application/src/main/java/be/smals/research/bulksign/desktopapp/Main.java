package be.smals.research.bulksign.desktopapp;


import be.smals.research.bulksign.desktopapp.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created by kova on 26/07/2016.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader   = new FXMLLoader(getClass().getClassLoader().getResource("views/main.fxml"));
        GridPane root       = loader.load();
        primaryStage.setTitle("BulkSign Desktop");

        MainController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setScene(new Scene(root, 800, 480));
        primaryStage.show();
    }
}
