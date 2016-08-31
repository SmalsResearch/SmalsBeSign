package be.smals.research.bulksign.desktopapp;

import be.fedict.commons.eid.client.BeIDCardManager;
import be.fedict.commons.eid.client.CardAndTerminalManager;
import be.smals.research.bulksign.desktopapp.controllers.MainController;
import be.smals.research.bulksign.desktopapp.ui.StatusBar;
import be.smals.research.bulksign.desktopapp.utilities.TestProxyFinder;
import be.smals.research.bulksign.desktopapp.utilities.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.security.Security;

/**
 * Application starter class
 */
public class Main extends Application {

    /**
     * Application entry point
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        /*
         * //todo fait-ça qq part comme parti du config proxy settings:
         * // if you work behind proxy, configure the proxy.
        System.setProperty("http.proxyHost", "proxyhost, eg proxy.smals-mvm.be");
        System.setProperty("http.proxyPort", "proxyport, eg 8080");
         *
         *
         */

        //todo remove this when you no longer need the test:
        TestProxyFinder.main(args);


        launch(args);
    }
    /**
     * {@inheritDoc}
     */
    @Override public void start(Stage primaryStage) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        FXMLLoader loader       = new FXMLLoader(getClass().getClassLoader().getResource("views/main.fxml"));
        StackPane masterPane    = loader.load();
        primaryStage.setTitle(Settings.APP_NAME + " - Version " + Settings.APP_VERSION);

        MainController controller = loader.getController();
        controller.initController(controller, primaryStage);
        BorderPane root         = controller.getRoot();

        // ----- BOTTOM ------------------------------------------------------------------------------------------------
        StatusBar statusBar = new StatusBar();
        BeIDCardManager beIDCardManager = new BeIDCardManager();
        CardAndTerminalManager cardAndTerminalManager = new CardAndTerminalManager();

        cardAndTerminalManager.addCardTerminalListener(statusBar);
        beIDCardManager.addBeIDCardEventListener(statusBar);
        beIDCardManager.start();
        cardAndTerminalManager.start();
        root.setBottom(statusBar);
        // ----- TOP ---------------------------------------------------------------------------------------------------
        createTop(controller, root);
        // ----- CENTER ------------------------------------------------------------------------------------------------
        createCenter (controller);


        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("images/icon.png")));
        primaryStage.setScene(new Scene(masterPane, 800, 480));
        primaryStage.setMinWidth(Settings.WIDTH_MIN);
        primaryStage.setMinHeight(Settings.HEIGHT_MIN);
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
        MenuItem settingsMenuItem   = new MenuItem("Settings");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem exitMenuItem   = new MenuItem("Exit...");
        MenuItem signMenuItem   = new MenuItem("Sign");
        MenuItem verifyMenuItem = new MenuItem("Verify");
        MenuItem aboutMenuItem  = new MenuItem("About");
        exitMenuItem.setId("exitMenuItem");

        fileMenu.getItems().addAll(homeMenuItem, settingsMenuItem, separator, exitMenuItem);
        taskMenu.getItems().addAll(signMenuItem, verifyMenuItem);
        helpMenu.getItems().addAll(aboutMenuItem);
        root.setTop(menuBar);

        aboutMenuItem.setOnAction( event -> controller.aboutMenuItemAction ());
        exitMenuItem.setOnAction( event -> controller.exitMenuItemAction ());
        signMenuItem.setOnAction( event -> controller.signMenuItemAction ());
        verifyMenuItem.setOnAction( event -> controller.verifyMenuItemAction ());
        homeMenuItem.setOnAction( event -> controller.homeMenuItemAction ());
        settingsMenuItem.setOnAction( event -> controller.settingsMenuItemAction ());
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
