package be.smals.research.bulksign.desktopapp.controllers;

import be.fedict.eid.applet.UserCancelledException;
import be.smals.research.bulksign.desktopapp.abstracts.Controller;
import be.smals.research.bulksign.desktopapp.services.EIDService;
import be.smals.research.bulksign.desktopapp.services.MockKeyService;
import be.smals.research.bulksign.desktopapp.services.SigningService;
import be.smals.research.bulksign.desktopapp.ui.FileListItem;
import be.smals.research.bulksign.desktopapp.utilities.Settings;
import be.smals.research.bulksign.desktopapp.utilities.Settings.Signer;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
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
import sun.security.pkcs11.wrapper.PKCS11Exception;

import javax.smartcardio.CardException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Main screen controller
 *
 * Handles events from main screen
 */
public class SignController extends Controller{

    private Stage stage;
    private SigningService signingService;
    private FileChooser fileChooser;
    private List<File> filesToSign;
    private OpenViewerFX viewerFx;

    @FXML private Label fileCountLabel;
    @FXML private ListView filesListView;
    @FXML private Pane readerPane;
    @FXML private StackPane masterSign;
    @FXML private JFXDialog noFileDialog;
    @FXML private JFXDialog noDefaultAppDialog;
    @FXML private JFXDialog saveOutputDialog;
    @FXML private JFXDialog noEIDDialog;
    @FXML private JFXDialog getPinDialog;
    @FXML private JFXPasswordField pinField;

    /**
     * Constructor
     */
    public SignController() {
        this.filesToSign = new ArrayList<>();
        try {
            this.signingService         = new SigningService();
        } catch (IOException | PKCS11Exception e) {
            e.printStackTrace();
        }

        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Select a file");
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
    }
    private void waitForReaderAndCard() throws CardException, InterruptedException {
        noEIDDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        noEIDDialog.show(masterSign);
        if (!EIDService.getInstance().isEIDReaderPresent()) {
            // MSG CONNECT READER
            EIDService.getInstance().waitForReader();
        }
        if (!EIDService.getInstance().isEIDPresent()) {
            // MSG INSERT CARD
            EIDService.getInstance().waitForCard();
        }
        noEIDDialog.close();
    }
    /**
     * Handles the output file saving process
     *
     * @param signature
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    private void saveSigningOutput(byte[] signature, List<X509Certificate> certificateChain) throws IOException, ParserConfigurationException, TransformerException {
        fileChooser.setTitle("Save the signature output");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Signature files (SIG)", "*.sig"));
        File fileToSave = fileChooser.showSaveDialog(this.stage);
        fileChooser.getExtensionFilters().clear();
        if (fileToSave != null) {
            try {
                SigningOutput signingOutput = new SigningOutput(null, signature, certificateChain);
                this.signingService.saveSigningOutput(signingOutput, fileToSave.getPath());
                Alert saveAlert = new Alert(Alert.AlertType.CONFIRMATION, "Signature successfully saved !", ButtonType.CLOSE);
                saveAlert.setTitle("Save Notification");
                saveAlert.setHeaderText("Saved !");
                saveAlert.showAndWait();
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Alert saveCanceledAlert = new Alert(Alert.AlertType.INFORMATION, "Save aborted", ButtonType.CLOSE);
            saveCanceledAlert.setTitle("Save canceled");
            saveCanceledAlert.setHeaderText(null);
            saveCanceledAlert.showAndWait();
        }
    }
    /**
     * Populates the ListView with the files selected by the user
     */
    private void populateListView () {
        ObservableList<FileListItem> items = this.filesListView.getItems();
        List<File> files = this.getCurrentFiles(items);
        List<FileListItem> fileListItems = new ArrayList<>();
        this.filesToSign.stream().filter(file -> !files.contains(file)).forEach(file -> {
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
                        this.noDefaultAppDialog.show(masterSign);
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

    // ---------- Actions ----------------------------------------------------------------------------------------------
    /**
     * Sign the selected file
     *
     * @param event click on signFile button
     */
    @FXML private void handleSignFilesButtonAction(ActionEvent event) {
        List<File> selectedFiles = this.getSelectedFiles ();
        if (selectedFiles.isEmpty()){
            noFileDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
            noFileDialog.show(masterSign);
        } else {
            // Sign Process
            FileInputStream[] inputFiles = new FileInputStream[selectedFiles.size()];

            try {
                if (Settings.getInstance().getSigner().equals(Signer.EID)) {
                    // Is card present ?
                    if (EIDService.getInstance().isEIDReaderPresent() || !EIDService.getInstance().isEIDStillPresent()) {
                        noEIDDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
                        noEIDDialog.show(masterSign);
                    } else {
                        // Prepare files
                        for (int i = 0; i < selectedFiles.size(); i++) {
                            inputFiles[i] = new FileInputStream(selectedFiles.get(i));
                        }
                        // Sign
                        this.signingService.prepareSigning();
                        this.isPinValid();
                        byte[] signature = this.signingService.signWithEID(inputFiles);
                        List<X509Certificate> certificateChain = EIDService.getInstance().getCertificateChain();
                        this.saveSigningOutput(signature, certificateChain);

                        for (FileInputStream file : inputFiles)
                            file.close();

                        EIDService.getInstance().close();
                    }
                } else {
                    // Prepare files
                    for (int i = 0; i < selectedFiles.size(); i++) {
                        inputFiles[i] = new FileInputStream(selectedFiles.get(i));
                    }
                    // Sign
                    byte[] signature = this.signingService.sign(inputFiles);

                    List<X509Certificate> certificateChain = MockKeyService.getInstance().getCertificateChain();
                    this.saveSigningOutput(signature, certificateChain);

                    for (FileInputStream file : inputFiles)
                        file.close();
                }

            } catch (IOException | ParserConfigurationException | TransformerException e) {
                e.printStackTrace();
            } catch (CardException|CertificateException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Defines the selected file
     *
     * @param event click on the selectFile button
     */
    @FXML private void handleSelectFilesToSignButtonAction(ActionEvent event) {
        List<File> files = fileChooser.showOpenMultipleDialog(this.stage);
        if (files != null) {
            files.stream().filter(file -> !this.filesToSign.contains(file)).forEach(file -> this.filesToSign.add(file));
            this.fileCountLabel.textProperty().set(this.filesToSign.size() + " file(s)");
            this.populateListView();
        }
    }

    // ---------- Dialog action ----------------------------------------------------------------------------------------
    @FXML private void handleCancelNoFileDialogAction (ActionEvent event) {
        noFileDialog.close();
    }
    @FXML private void handleCancelNoDefaultAppDialogAction (ActionEvent event) {
        noDefaultAppDialog.close();
    }
    @FXML private void handleCancelNoEIDDialogAction (ActionEvent event) {
        noEIDDialog.close();
    }

    // ----------
    private boolean isPinValid() {
        this.getPinDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.getPinDialog.show(masterSign);
        try {
            return EIDService.getInstance().isPinValid (pinField.getText().toCharArray());
        } catch (UserCancelledException|CardException e) {
            e.printStackTrace();
        }
        return false;
    }
}
