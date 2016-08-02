package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.services.MockKeyService;
import be.smals.research.bulksign.desktopapp.services.SigningService;
import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * Main screen controller
 *
 * Handles events from main screen
 */
public class MainController {

    private SigningService signingService;
    private VerifySigningService verifySigningService;
    private FileChooser signFileChooser;
    private FileChooser verifyFileChooser;

    @FXML private Label selectedSignFileLabel;
    @FXML private Label selectedVerifyFileLabel;

    private Stage stage;

    private File selectedSignFile;
    private File selectedVerifyFile;

    /**
     * Constructor
     */
    public MainController () {
        try {
            this.signingService         = new SigningService();
            this.verifySigningService   = new VerifySigningService();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PKCS11Exception e) {
            e.printStackTrace();
        }

        this.signFileChooser = new FileChooser();
        this.verifyFileChooser = new FileChooser();
        this.signFileChooser.setTitle("Select a file");
        this.verifyFileChooser.setTitle("Select the signature file");
    }
    @FXML
    private void handleVerifyFileButtonAction (ActionEvent event) {
        if (this.selectedSignFile == null || this.selectedVerifyFile == null){
            Alert noFileSelectedDialog = new Alert(Alert.AlertType.INFORMATION, "Please, select the signature file and the signed file.", ButtonType.CLOSE);
            noFileSelectedDialog.showAndWait();
        } else {
            String MasterDigest = "c3f7c5a873a3f763fec69add38bc48835a475639a263042d7269d20ffb7fafee";

//            try {
//                BigInteger modulus = new BigInteger("95099863606005976866430829628947691169181414536044822663514146236363189484868479038765230330544286836853478245670270453709068745482695225683666031954181177729573100887158579643353159710420254746103592570767926512143089393115103159904644719399002044705720254566168147379698895092832292865563385058248455055373", 10);
//                BigInteger pubExp = new BigInteger("65537", 10);
//                PublicKey key = verifySigningService.getPublicKey(modulus, pubExp);
//            } catch (NoSuchAlgorithmException |InvalidKeySpecException e) {
//                Alert errorDialog = new Alert(Alert.AlertType.ERROR, "Unable to retrieve the public key.", ButtonType.CLOSE);
//                errorDialog.showAndWait();
//            }
            PublicKey key = MockKeyService.getInstance().getPublicKey();
            try {
                //create FileInputStream object
                FileInputStream signatureFile = new FileInputStream(this.selectedVerifyFile);
                byte signature[] = new byte[(int) this.selectedVerifyFile.length()];
                signatureFile.read(signature);
                signatureFile.close();

                this.outputInformation(MasterDigest, key, signature);

                FileInputStream file = new FileInputStream(this.selectedSignFile);
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
    /**
     * Pint to console masterDigest, key and signature
     *
     * @param masterDigest
     * @param key
     * @param signature
     */
    private void outputInformation(String masterDigest, PublicKey key, byte[] signature) {
        System.out.println("Signature : ");
        for (int i = 0; i < signature.length; i++) {
            System.out.print(signature[i]);
            System.out.print(" ");
        }

        System.out.println(" ");
        System.out.println(" ");

        System.out.print("Master Digest : ");
        System.out.println(masterDigest);
        System.out.println(" ");

        System.out.print("Public Key : ");
        System.out.println(key);
        System.out.println(" ");

        System.out.println("Starting Batch Signature Verification on file 0...");
    }
    @FXML
    private void handleSelectVerifyFileButtonAction (ActionEvent event) {
        File file = this.verifyFileChooser.showOpenDialog(this.stage);
        if (file != null) {
            this.selectedVerifyFile = file;
            this.selectedVerifyFileLabel.textProperty().set(file.getName());
        } else {
            System.out.println("ERROR - No file found.");
        }
    }
    /**
     * Sign the selected file
     *
     * @param event click on signFile button
     */
    @FXML
    private void handleSignFileButtonAction(ActionEvent event) {

        if (this.selectedSignFile == null){
            Alert noFileSelectedDialog = new Alert(Alert.AlertType.INFORMATION, "No file to sign", ButtonType.CLOSE);
            noFileSelectedDialog.showAndWait();
        } else {
            FileInputStream[] inputFiles = new FileInputStream[1];

            try {
                inputFiles[0] = new FileInputStream(this.selectedSignFile);

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
    private void handleSelectSignFileButtonAction(ActionEvent event) {
        File file = signFileChooser.showOpenDialog(this.stage);
        if (file != null) {
            this.selectedSignFile = file;
            this.selectedSignFileLabel.textProperty().set(file.getName());
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
        file = new File("C:\\Users\\cea\\Documents\\Output.sig");
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
