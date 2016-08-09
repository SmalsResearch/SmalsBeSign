package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

/**
 * Main screen controller
 *
 * Handles events from main screen
 */
public class VerifyController {

    private VerifySigningService verifySigningService;
    private FileChooser fileChooser;

    @FXML private Label signedFileLabel;
    @FXML private Label signatureFileLabel;

    private Stage stage;

    private File signedFile;
    private File signatureFile;

    /**
     * Constructor
     */
    public VerifyController() {
        this.verifySigningService   = new VerifySigningService();

        this.fileChooser = new FileChooser();
    }
    @FXML
    private void handleVerifyFileButtonAction (ActionEvent event) {
        if (this.signedFile == null || this.signatureFile == null){
            Alert noFileSelectedDialog = new Alert(Alert.AlertType.INFORMATION, "Please, select the signature file and the signed file.", ButtonType.CLOSE);
            noFileSelectedDialog.showAndWait();
        } else {
            SigningOutput signingOutput = null;
            try {
                signingOutput = this.verifySigningService.getSigningOutput(this.signatureFile);

                FileInputStream file = new FileInputStream(this.signedFile);
                boolean isValid = this.verifySigningService.verifySigning(file, signingOutput);
                if (isValid) {
                    Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION, "The Signature is valid !", ButtonType.CLOSE);
                    confirmationDialog.showAndWait();
                } else {
                    Alert errorDialog = new Alert(Alert.AlertType.ERROR, "Invalid Signature \n Cause : invalid Digital Signature of Master Digest", ButtonType.CLOSE);
                    errorDialog.showAndWait();
                }

                file.close();
            } catch (IOException|SAXException|ParserConfigurationException e) {
                Alert corruptedFileDialog = new Alert(Alert.AlertType.ERROR, "The signature file is corrupted !", ButtonType.CLOSE);
                corruptedFileDialog.setTitle("Corrupted file");
                corruptedFileDialog.showAndWait();
                e.printStackTrace();
            } catch (SignatureException e) {
                Alert errorDialog = new Alert(Alert.AlertType.ERROR, "Invalid Signature", ButtonType.CLOSE);
                errorDialog.showAndWait();
            } catch (NoSuchAlgorithmException|InvalidKeyException e) {
                Alert corruptedFileDialog = new Alert(Alert.AlertType.ERROR, "The signature is incorrect.", ButtonType.CLOSE);
                corruptedFileDialog.setTitle("Invalid Signature");
                corruptedFileDialog.showAndWait();
            } catch (CertificateException e) {
                Alert corruptedFileDialog = new Alert(Alert.AlertType.ERROR, "Invalid certificate", ButtonType.CLOSE);
                corruptedFileDialog.setTitle("Invalid Certificate");
                corruptedFileDialog.showAndWait();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }


        }
    }
    @FXML
    private void handleSelectVerifyFileButtonAction (ActionEvent event) {
        this.fileChooser.setTitle("Select the signature file");
        this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Signature files (SIG)", "*.sig"));
        File file = this.fileChooser.showOpenDialog(this.stage);
        this.fileChooser.getExtensionFilters().clear();
        if (file != null) {
            this.signatureFile = file;
            this.signatureFileLabel.textProperty().set(file.getName());
        } else {
            System.out.println("ERROR - No file found.");
        }
    }

    /**
     * Defines the selected file
     *
     * @param event click on the selectFile button
     */
    @FXML
    private void handleSelectSignFileButtonAction(ActionEvent event) {
        File file = this.fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            this.signedFile = file;
            this.signedFileLabel.textProperty().set(file.getName());
        } else {
            System.out.println("ERROR - No file found.");
        }
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }
}
