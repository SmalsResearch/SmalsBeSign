package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.abstracts.Controller;
import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
import be.smals.research.bulksign.desktopapp.ui.FileListItem;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jpedal.examples.viewer.Commands;
import org.jpedal.examples.viewer.OpenViewerFX;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main screen controller
 *
 * Handles events from main screen
 */
public class VerifyController extends Controller {

    private VerifySigningService verifySigningService;
    private FileChooser fileChooser;
    private OpenViewerFX viewerFx;

    @FXML private Label filesToSignCount;
    @FXML private Label signatureFileLabel;
    @FXML private StackPane masterVerify;
    @FXML private ListView filesListView;
    @FXML private Pane readerPane;
    @FXML private JFXDialog infoDialog;
    @FXML private JFXDialog errorDialog;
    @FXML private JFXDialog verifyResultDialog;

    private Stage stage;

    private List<File> filesToVerify;
    private File signatureFile;

    /**
     * Constructor
     */
    public VerifyController() {
        this.verifySigningService   = new VerifySigningService();
        this.fileChooser            = new FileChooser();
        this.filesToVerify          = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStage (Stage stage) {
        this.stage = stage;
        this.viewerFx = new OpenViewerFX(readerPane, getClass().getClassLoader().getResource("lib/OpenViewerFx/preferences/custom.xml").getPath());
        this.viewerFx.getRoot().prefWidthProperty().bind(readerPane.widthProperty());
        this.viewerFx.getRoot().prefHeightProperty().bind(readerPane.heightProperty());
        this.viewerFx.setupViewer();
        // Update viewer look
        BorderPane viewerPane = (BorderPane) this.readerPane.getChildren().get(0);
        viewerPane.setTop(null);
        HBox bottomPane = (HBox) viewerPane.getBottom();
        bottomPane.getChildren().remove(0, 2);
        // Setup dialogs
        this.infoDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.errorDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.verifyResultDialog.setTransitionType(JFXDialog.DialogTransition.TOP);

    }
    /**
     * Returns selected files from the file list
     *
     * @return the list of files
     */
    private List<File> getSelectedFiles () {
        List<File> selectedFiles = new ArrayList<>();
        for ( Object item : this.filesListView.getItems() ){
            FileListItem fileListItem = (FileListItem) item;
            if (fileListItem.isFileSelected())
                selectedFiles.add(fileListItem.getFile());
        }
        return selectedFiles;
    }
    @FXML
    private void handleVerifyFilesButtonAction(ActionEvent event) {
        List<File> selectedFiles = this.getSelectedFiles ();
        if (selectedFiles.isEmpty()){
            infoDialog.show(masterVerify);
            Label title     = (Label) this.stage.getScene().lookup("#infoDialogTitle");
            Label body      = (Label) this.stage.getScene().lookup("#infoDialogBody");
            body.setText("Please, select the signature file and a least one signed file.");
            title.setText("No file selected");
        } else {
            SigningOutput signingOutput = null;
            List<String> pass = new ArrayList<>();
            List<String> fail = new ArrayList<>();
            try {
                signingOutput = this.verifySigningService.getSigningOutput(this.signatureFile);

                for (File signedFile : selectedFiles) {
                    FileInputStream file = new FileInputStream(signedFile);
                    boolean isValid = this.verifySigningService.verifySigning(file, signingOutput);
                    if (isValid) {
                        pass.add(signedFile.getName());
                    } else {
                        fail.add(signedFile.getName());
                    }
                    file.close();
                }

                // Display result
                verifyResultDialog.show(masterVerify);
                Label resultLabel       = (Label) this.stage.getScene().lookup("#verifyResultTitle");
                JFXListView resultList  = (JFXListView) this.stage.getScene().lookup("#verifyResultListView");
                resultList.getItems().clear();
                if (pass.isEmpty()) resultLabel.setText("All files failed verification!");
                else if (fail.isEmpty()) resultLabel.setText("All files pass!");
                else resultLabel.setText(pass.size() + " file out of "+(pass.size()+fail.size())+ " pass the verification");
                for (String passFile : pass) {
                    Label label = new Label(passFile);
                    label.getStyleClass().add("color-success");
                    resultList.getItems().add(label);
                }
                for (String failFile : fail) {
                    Label label = new Label(failFile);
                    label.getStyleClass().add("color-danger");
                    resultList.getItems().add(label);
                }

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
        List<File> files = this.fileChooser.showOpenMultipleDialog(this.stage);
        if (files != null) {
            files.stream().filter(file -> !this.filesToVerify.contains(file)).forEach(file -> this.filesToVerify.add(file));
            this.filesToSignCount.textProperty().set(this.filesToVerify.size() +" file(s) to verify");
            this.populateListView();
        }
    }
    /**
     * Populates the ListView with the files selected by the user
     */
    private void populateListView () {
        ObservableList<FileListItem> items = this.filesListView.getItems();
        List<File> files = this.getCurrentFiles(items);
        List<FileListItem> fileListItems = new ArrayList<>();
        this.filesToVerify.stream().filter(file -> !files.contains(file)).forEach(file -> {
            FileListItem listItem = new FileListItem(file);
            EventHandler event;
            if (listItem.getFileExtension().equalsIgnoreCase("pdf")) {
                event = event1 -> {
                    Object[] args = {file};
                    viewerFx.executeCommand(Commands.OPENFILE, args);
                };
            } else {
                event = event1 -> {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        errorDialog.show(masterVerify);
                        Label title     = (Label) this.stage.getScene().lookup("#errorDialogTitle");
                        Label body      = (Label) this.stage.getScene().lookup("#errorDialogBody");
                        body.setText("No application associated with the specified file.");
                        title.setText("Unable to open the file");
                    }
                };
            }
            listItem.setViewButtonAction(event);
            fileListItems.add(listItem);
        });
        this.filesListView.getItems().addAll(FXCollections.observableList(fileListItems));
    }
    private List<File> getCurrentFiles (ObservableList<FileListItem> items) {
        List<File> files = new ArrayList<>();
        for (FileListItem item : items) {
            files.add(item.getFile());
        }
        return files;
    }

}
