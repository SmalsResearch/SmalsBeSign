package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.services.SigningService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main screen controller
 *
 * Handles events from main screen
 */
public class SignController {

    private Stage stage;
    private SigningService signingService;
    private FileChooser fileChooser;
    private List<File> filesToSign;

    @FXML private Label fileCountLabel;
    @FXML private ListView filesListView;

    /**
     * Constructor
     */
    public SignController() {
        this.filesToSign = new ArrayList<>();
        try {
            this.signingService         = new SigningService();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PKCS11Exception e) {
            e.printStackTrace();
        }

        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Select a file");
    }
    /**
     * Sign the selected file
     *
     * @param event click on signFile button
     */
    @FXML
    private void handleSignFileButtonAction(ActionEvent event) {

        if (this.filesToSign.isEmpty()){
            Alert noFileSelectedDialog = new Alert(Alert.AlertType.INFORMATION, "Please, select at least one file.", ButtonType.CLOSE);
            noFileSelectedDialog.setTitle("No file to sign");
            noFileSelectedDialog.setHeaderText(null);
            noFileSelectedDialog.showAndWait();
        } else {
            FileInputStream[] inputFiles = new FileInputStream[this.filesToSign.size()];

            try {
                for ( int i=0; i < this.filesToSign.size(); i++ ) {
                    inputFiles[i] = new FileInputStream(this.filesToSign.get(i));
                }

                byte[] signature = this.signingService.sign(inputFiles);
                fileChooser.setTitle("Save the signature output");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Signature files (SIG)", "*.sig"));
                File fileToSave = fileChooser.showSaveDialog(this.stage);
                if (fileToSave != null) {
                    this.signingService.saveSigningOutput(signature, fileToSave.getPath());
                    Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION, "Signature successfully saved !", ButtonType.CLOSE);
                    saveAlert.setTitle("Save Notification");
                    saveAlert.setHeaderText("Saved !");
                    saveAlert.showAndWait();
                } else {
                    Alert saveCanceledAlert = new Alert(Alert.AlertType.INFORMATION, "Save aborted", ButtonType.CLOSE);
                    saveCanceledAlert.setTitle("Save canceled");
                    saveCanceledAlert.setHeaderText(null);
                    saveCanceledAlert.showAndWait();
                }


                for (FileInputStream file : inputFiles)
                     file.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * Defines the selected file
     *
     * @param event click on the selectFile button
     */
    @FXML
    private void handleSelectSignFileButtonAction(ActionEvent event) {
        List<File> files = fileChooser.showOpenMultipleDialog(this.stage);
        if (files != null) {
            this.filesToSign.addAll(files);
            this.fileCountLabel.textProperty().set(this.filesToSign.size() + " file(s) to sign");
            this.populateListView();
        } else {
            System.out.println("INFO - No file selected");
        }
    }
    public void setStage (Stage stage) {
        this.stage = stage;
    }
    private void populateListView () {
        this.filesListView.getItems().clear();
        for ( File file : this.filesToSign) {
            Label fileLabel = new Label(file.getAbsolutePath());
            this.filesListView.getItems().addAll(fileLabel);
        }
    }
}
