package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.SigningOutput;
import be.smals.research.bulksign.desktopapp.services.MockKeyService;
import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.PublicKey;

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
                signingOutput = verifySigningService.getSigningOutput(this.signatureFile);
            } catch (IOException|SAXException|ParserConfigurationException e) {
                Alert corruptedFileDialog = new Alert(Alert.AlertType.ERROR, "Le signature file is corrupted !", ButtonType.CLOSE);
                corruptedFileDialog.setTitle("Corrupted file");
                corruptedFileDialog.showAndWait();
                System.exit(0);
            }
            String MasterDigest = signingOutput.masterDigest;

//            PublicKey key = EIDKeyService.getInstance().getPublicKey(modulus, publicExponent);
            PublicKey key = MockKeyService.getInstance().getPublicKey();
            try {
                FileInputStream signatureFile = new FileInputStream(this.signatureFile);
                byte signature[] = new byte[(int) this.signatureFile.length()];
                signatureFile.read(signature);
                signatureFile.close();

                FileInputStream file = new FileInputStream(this.signedFile);
                boolean isValid = verifySigningService.verifySigning( file, signature, MasterDigest, key);
                if (isValid) {
                    Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION, "The Signature is valid !", ButtonType.CLOSE);
                    confirmationDialog.showAndWait();
                } else {
                    Alert errorDialog = new Alert(Alert.AlertType.ERROR, "Invalid Signature \n Cause : invalid Digital Signature of Master Digest", ButtonType.CLOSE);
                    errorDialog.showAndWait();
                }

                file.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }

        }
    }

    @FXML
    private void handleSelectVerifyFileButtonAction (ActionEvent event) {
        this.fileChooser.setTitle("Select the signature file");
        this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Signature files (SIG)", "*.sig"));
        File file = this.fileChooser.showOpenDialog(this.stage);
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
        File file = fileChooser.showOpenDialog(this.stage);
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
