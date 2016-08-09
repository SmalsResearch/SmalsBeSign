package be.smals.research.bulksign.desktopapp;

import be.fedict.eid.applet.shared.SignatureDataMessage;
import be.smals.research.bulksign.desktopapp.controllers.MainController;
import be.smals.research.bulksign.desktopapp.eid.EidService;
import be.smals.research.bulksign.desktopapp.utilities.Settings;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.security.Security;
import java.util.Arrays;

public class Main extends Application {

    public static void main(String[] args) {
        //launch(args);

        byte[] input = EidService.getMockInput(160);
        byte[] digest = EidService.getSha1(input);
        SignatureDataMessage signature = EidService.doSignature(digest);
        System.out.println("OUTPUT: " + Arrays.toString(signature.signatureValue));
        System.out.println("LENGTH: " + signature.signatureValue.length);
        //System.out.println("CERTIFICATE CHAIN (3 certs): "+ signature.certificateChain);
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/main.fxml"));
        BorderPane root = loader.load();
        primaryStage.setTitle("BulkSign Desktop");

        MainController controller = loader.getController();
        controller.setStage(primaryStage);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu taskMenu = new Menu("Task");
        Menu signerMenu = new Menu("Signer");
        menuBar.getMenus().addAll(fileMenu, taskMenu, signerMenu);
        MenuItem testMenuItem   = new MenuItem("Test");
        MenuItem exitMenuItem = new MenuItem("Exit...");
        MenuItem signMenuItem = new MenuItem("Sign");
        MenuItem verifyMenuItem = new MenuItem("Verify");
        final ToggleGroup signerGroup = new ToggleGroup();
        RadioMenuItem eidMenuItem = new RadioMenuItem("eID");
        RadioMenuItem mockMenuItem = new RadioMenuItem("Mock");
        eidMenuItem.setUserData(Settings.Signer.EID);
        mockMenuItem.setUserData(Settings.Signer.MOCK);
        eidMenuItem.setToggleGroup(signerGroup);
        mockMenuItem.setToggleGroup(signerGroup);
        signerGroup.selectToggle(mockMenuItem);
        fileMenu.getItems().addAll(testMenuItem, exitMenuItem);
        taskMenu.getItems().addAll(signMenuItem, verifyMenuItem);
        signerMenu.getItems().addAll(mockMenuItem, eidMenuItem);
        root.setTop(menuBar);

        testMenuItem.setOnAction(event -> {
            controller.testMenuItemAction();
        });
        exitMenuItem.setOnAction(event -> {
            controller.exitMenuItemAction();
        });
        signMenuItem.setOnAction(event -> {
            controller.signMenuItemAction();
        });
        verifyMenuItem.setOnAction(event -> {
            controller.verifyMenuItemAction();
        });
        signerGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (signerGroup.getSelectedToggle() != null) {
                Settings.Signer signer = (Settings.Signer) signerGroup.getSelectedToggle().getUserData();
                Settings.getInstance().setSigner(signer);
            }
        });

        primaryStage.setScene(new Scene(root, 800, 480));
        primaryStage.show();
    }
}
