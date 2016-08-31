package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.utilities.ProxyFinder;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

/**
 * Home screen controller
 *
 * Handles events from Home view
 */
public class SettingsController extends Controller{

    @FXML private StackPane masterSettings;
    @FXML private JFXDialog infoDialog;
    @FXML private JFXDialog errorDialog;
    @FXML private JFXDialog successDialog;
    @FXML private JFXDialog waitingDialog;
    // Proxy info
    @FXML private JFXTextField proxyAddress;
    @FXML private JFXTextField proxyPort;
    @FXML private JFXTextField proxyUsername;
    @FXML private JFXPasswordField proxyPassword;

    /**
     * Constructor
     */
    public SettingsController() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void initController(MainController mainController, Stage stage) {
        super.initController(mainController, stage);

        // Validate fields
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();
        requiredFieldValidator.setMessage("Input Required");
        // ...

        // Setup dialogs
        this.infoDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.errorDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.successDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.infoDialog.setOverlayClose(false);
        this.errorDialog.setOverlayClose(false);
        this.successDialog.setOverlayClose(false);
    }

    @FXML public void handleTestProxyButtonAction () {
//        this.showWaitingDialog(waitingDialog, masterSettings, "Testing\nProxy");
        String address = this.proxyAddress.getText().trim();
        String portStr = this.proxyPort.getText().trim();
        int port = Integer.parseInt(portStr);
        if (address == "" || portStr == "") {
            this.showErrorDialog(infoDialog, masterSettings, "Proxy testing - Succeed", "Address and port fields are required!");
        }

        String username = this.proxyUsername.getText().trim();
        String password = this.proxyPassword.getText().trim();
        Proxy proxy = ProxyFinder.getInstance().getProxy (address, port);
        try {
            boolean result = ProxyFinder.getInstance().testConnexionTo(proxy, new URL("http://www.google.com"));
            if (result) {
                this.showInfoDialog(infoDialog, masterSettings, "Proxy testing - Succeed", "Internet connection succeed with the proxy :\n"+proxy.address().toString());

                Label bodyLabel         = (Label) this.stage.getScene().lookup("#infoDialogBody");
                GlyphsDude.setIcon(bodyLabel, FontAwesomeIcon.CHECK_CIRCLE, "2.5em");
                bodyLabel.getStyleClass().clear();
                bodyLabel.getStyleClass().add("color-success");
            } else {
                this.showInfoDialog(infoDialog, masterSettings, "Proxy testing - Failed", "Internet connection failed with the proxy :\n"+proxy.address().toString());

                Label bodyLabel         = (Label) this.stage.getScene().lookup("#infoDialogBody");
                GlyphsDude.setIcon(bodyLabel, FontAwesomeIcon.TIMES_CIRCLE, "2.5em");
                bodyLabel.getStyleClass().clear();
                bodyLabel.getStyleClass().add("color-danger");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            this.showInfoDialog(infoDialog, masterSettings, "Proxy testing - Failed", "Internet connection failed with the proxy :\n"+proxy.address().toString());

            Label bodyLabel         = (Label) this.stage.getScene().lookup("#infoDialogBody");
            GlyphsDude.setIcon(bodyLabel, FontAwesomeIcon.TIMES_CIRCLE, "2.5em");
            bodyLabel.getStyleClass().clear();
            bodyLabel.getStyleClass().add("color-danger");
        }
//        this.waitingDialog.close();
    }
    @FXML public void handleProxyLookupButtonAction () {
        Proxy proxy = ProxyFinder.getInstance().find();
        if (proxy == null) {
            System.out.println("Proxy not found // ");
            this.showErrorDialog(errorDialog, masterSettings, "Proxy lookup", "Lookup failed !");
        } else {
            System.out.println("Proxy FOUND ! @ "+proxy.address().toString());
            this.showInfoDialog(infoDialog, masterSettings, "Proxy lookup", "Lookup succeed !\n"+proxy.address());
        }
    }
}