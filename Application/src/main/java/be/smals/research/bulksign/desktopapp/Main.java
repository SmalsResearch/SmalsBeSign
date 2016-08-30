package be.smals.research.bulksign.desktopapp;

import be.fedict.commons.eid.client.BeIDCardManager;
import be.smals.research.bulksign.desktopapp.controllers.MainController;
import be.smals.research.bulksign.desktopapp.ui.StatusBar;
import be.smals.research.bulksign.desktopapp.utilities.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.security.Security;

/**
 * Application starter class
 */
public class Main extends Application {

    private static double WIDTH_MIN = 600;
    private static double HEIGHT_MIN = 460;
    private static String APP_NAME = "SmalsBeSign";

    /**
     * Application entry point
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    /**
     * {@inheritDoc}
     */
    @Override public void start(Stage primaryStage) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        FXMLLoader loader   = new FXMLLoader(getClass().getClassLoader().getResource("views/main.fxml"));
        StackPane masterPane       = loader.load();
        primaryStage.setTitle(APP_NAME);

        MainController controller = loader.getController();
        controller.initController(controller, primaryStage);
        BorderPane root         = controller.getRoot();

        // ----- BOTTOM ------------------------------------------------------------------------------------------------
        StatusBar statusBar = new StatusBar();
        BeIDCardManager beIDCardManager = new BeIDCardManager();
        beIDCardManager.addBeIDCardEventListener(statusBar);
        beIDCardManager.start();
        root.setBottom(statusBar);
        // ----- TOP ---------------------------------------------------------------------------------------------------
        createTop(controller, root);
        // ----- CENTER ------------------------------------------------------------------------------------------------
        createCenter (controller);

        primaryStage.setScene(new Scene(masterPane, 800, 480));
        primaryStage.setMinWidth(WIDTH_MIN);
        primaryStage.setMinHeight(HEIGHT_MIN);
        primaryStage.show();
    }
    /**
     * Creates TOP components : the menu bar and defines actions
     *
     * @param controller the main controller
     * @param root the root pane
     */
    private void createTop(MainController controller, BorderPane root) {
        MenuBar menuBar         = new MenuBar();
        Menu fileMenu           = new Menu("File");
        Menu taskMenu           = new Menu("Task");
        Menu helpMenu           = new Menu("Help");
        menuBar.getMenus().addAll(fileMenu, taskMenu, helpMenu);
        MenuItem homeMenuItem   = new MenuItem("Home");
        MenuItem exitMenuItem   = new MenuItem("Exit...");
        MenuItem signMenuItem   = new MenuItem("Sign");
        MenuItem verifyMenuItem = new MenuItem("Verify");
        MenuItem aboutMenuItem  = new MenuItem("About");
        exitMenuItem.setId("exitMenuItem");

        Settings.getInstance().setSigner(Settings.Signer.EID);
        fileMenu.getItems().addAll(homeMenuItem, exitMenuItem);
        taskMenu.getItems().addAll(signMenuItem, verifyMenuItem);
        helpMenu.getItems().addAll(aboutMenuItem);
        root.setTop(menuBar);

        aboutMenuItem.setOnAction( event -> controller.aboutMenuItemAction ());
        exitMenuItem.setOnAction( event -> controller.exitMenuItemAction ());
        signMenuItem.setOnAction( event -> controller.signMenuItemAction ());
        verifyMenuItem.setOnAction( event -> controller.verifyMenuItemAction ());
        homeMenuItem.setOnAction( event -> controller.homeMenuItemAction());
    }
    /**
     *  Creates home screen
     * @param controller
     */
    private void createCenter (MainController controller) {
        Platform.runLater( () -> controller.homeMenuItemAction ());
    }
    /**
     * {@inheritDoc}
     */
    @Override public void stop() throws Exception {
        super.stop();
        System.exit(0);
        Platform.exit();
    }
}
