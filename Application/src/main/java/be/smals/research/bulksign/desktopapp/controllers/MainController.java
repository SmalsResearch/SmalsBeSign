package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.services.SigningService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import java.io.*;

/**
 * Main screen controller
 *
 * Handles events from main screen
 */
public class MainController {

    private SigningService signingService;
    private FileChooser fileChooser;
    @FXML private Button signFileButton;
    @FXML private Label selectedFileLabel;

    private Stage stage;

    private File selectedFile;

    /**
     * Constructor
     */
    public MainController () {
        try {
            this.signingService = new SigningService();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PKCS11Exception e) {
            e.printStackTrace();
        }
        this.fileChooser    = new FileChooser();
        this.fileChooser.setTitle("Select a file");
    }

    @FXML
    private void handleVerifyFileButtonAction (ActionEvent event) {

    }

    /**
     * Sign the selected file
     *
     * @param event click on signFile button
     */
    @FXML
    private void handleSignFileButtonAction(ActionEvent event) {

        if (this.selectedFile == null){
            Alert noFileSelectedDialog = new Alert(Alert.AlertType.INFORMATION, "No file to sign", ButtonType.CLOSE);
            noFileSelectedDialog.showAndWait();
        } else {
            FileInputStream[] inputFiles = new FileInputStream[1];

            try {
                inputFiles[0] = new FileInputStream(this.selectedFile);

                byte[] signature = this.signingService.sign(inputFiles);
                this.outputSignature(signature);

                for (FileInputStream file : inputFiles)
                     file.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    /**
     * Defines the selected file
     *
     * @param event click on the selectFile button
     */
    @FXML
    private void handleSelectFileButtonAction(ActionEvent event) {
        File file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            this.selectedFile = file;
            this.selectedFileLabel.textProperty().set(file.getName());
        } else {
            System.out.println("ERROR - No file found.");
        }
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    private void outputSignature (byte[] signature) {

        /* Display the signature length and value */
        System.out.print("Length of generated signature (in bytes):");
        System.out.println(signature.length);
        System.out.println("");
        System.out.print("Value of generated signature: ");

        for (int i = 0; i < signature.length; i++) {
            System.out.print(signature[i]);
            System.out.print(" ");
        }
        System.out.println("");

        /*Write the signature into a file*/
        File file = null;
        file = new File("C:\\Users\\cea\\Desktop\\Output.sig");
        FileOutputStream file_output = null;
        try {
            file_output = new FileOutputStream(file);
            DataOutputStream data_out = new DataOutputStream(file_output);
            data_out.write(signature);
            file_output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
