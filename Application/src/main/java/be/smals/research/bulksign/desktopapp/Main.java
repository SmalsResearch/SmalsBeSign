package be.smals.research.bulksign.desktopapp;

import be.smals.research.bulksign.desktopapp.controllers.MainController;
import be.smals.research.bulksign.desktopapp.services.EIDService;
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
        primaryStage.setTitle("BulkSign Desktop");

        MainController controller = loader.getController();
        controller.initController(controller, primaryStage);
        BorderPane root         = controller.getRoot();

        // ----- TOP ---------------------------------------------------------------------------------------------------
        createTop(controller, root);

        // ----- BOTTOM ------------------------------------------------------------------------------------------------
        StatusBar statusBar = new StatusBar();
        EIDService.getInstance().registerAsEIDServiceObserver(statusBar);
        root.setBottom(statusBar);

        // ----- CENTER ------------------------------------------------------------------------------------------------
        createCenter (controller);

        primaryStage.setScene(new Scene(masterPane, 800, 480));
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
        Menu signerMenu         = new Menu("Signer");
        Menu helpMenu           = new Menu("Help");
        menuBar.getMenus().addAll(fileMenu, taskMenu, signerMenu, helpMenu);
        MenuItem homeMenuItem   = new MenuItem("Home");
        MenuItem exitMenuItem   = new MenuItem("Exit...");
        MenuItem signMenuItem   = new MenuItem("Sign");
        MenuItem verifyMenuItem = new MenuItem("Verify");
        MenuItem aboutMenuItem  = new MenuItem("About");
        exitMenuItem.setId("exitMenuItem");
        final ToggleGroup signerGroup   = new ToggleGroup();
        RadioMenuItem eidMenuItem       = new RadioMenuItem("eID");
        RadioMenuItem mockMenuItem      = new RadioMenuItem("Mock");
        eidMenuItem.setUserData(Settings.Signer.EID);
        mockMenuItem.setUserData(Settings.Signer.MOCK);
        eidMenuItem.setToggleGroup(signerGroup);
        mockMenuItem.setToggleGroup(signerGroup);
        signerGroup.selectToggle(eidMenuItem);
        Settings.getInstance().setSigner(Settings.Signer.EID);
        fileMenu.getItems().addAll(homeMenuItem, exitMenuItem);
        taskMenu.getItems().addAll(signMenuItem, verifyMenuItem);
        signerMenu.getItems().addAll(mockMenuItem, eidMenuItem);
        helpMenu.getItems().addAll(aboutMenuItem);
        root.setTop(menuBar);

        aboutMenuItem.setOnAction( event -> controller.aboutMenuItemAction ());
        exitMenuItem.setOnAction( event -> controller.exitMenuItemAction ());
        signMenuItem.setOnAction( event -> controller.signMenuItemAction ());
        verifyMenuItem.setOnAction( event -> controller.verifyMenuItemAction ());
        homeMenuItem.setOnAction( event -> controller.homeMenuItemAction());
        signerGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (signerGroup.getSelectedToggle() != null) {
                Settings.Signer signer = (Settings.Signer) signerGroup.getSelectedToggle().getUserData();
                Settings.getInstance().setSigner(signer);
            }
        });
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
