package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.signverify.BatchSignature;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Main screen controller
 */
public class MainController {

    private FileChooser fileChooser;
    @FXML
    private Button signFileButton;
    @FXML
    private Label selectedFileLabel;

    private Stage stage;

    private File selectedFile;

    public MainController () {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");
    }

    @FXML
    private void handleSignFileButtonAction(ActionEvent event) {

        if (this.selectedFile == null){
            Alert noFileSelectedDialog = new Alert(Alert.AlertType.INFORMATION, "No file to sign", ButtonType.CLOSE);
            noFileSelectedDialog.showAndWait();
        } else {
            FileInputStream[] input = new FileInputStream[1];

            try {
                input[0] = new FileInputStream(this.selectedFile);

                try {
                    byte[] signature = BatchSignature.main(input);
                    outputSignature(signature);

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (PKCS11Exception pkcs11Exception) {
                    pkcs11Exception.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }
    @FXML
    private void handleSelectFileButtonAction(ActionEvent event) {
        File file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            this.selectedFile = file;
            //this.selectedFileLabel.setText(file.getName());
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
    }
}
