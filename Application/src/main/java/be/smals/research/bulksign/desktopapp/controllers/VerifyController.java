package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.abstracts.Controller;
import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
import be.smals.research.bulksign.desktopapp.ui.FileListItem;
import be.smals.research.bulksign.desktopapp.ui.ResultListItem;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @Override public void initController(MainController mainController, Stage stage) {
        super.initController(mainController, stage);
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

    /**
     * Used to display verification results
     *
     * @param pass correct results
     * @param fail incorrect results
     */
    private void displayVerifyResult(List<String> pass, List<String> fail) {
        verifyResultDialog.show(masterVerify);
        Label resultLabel       = (Label) this.stage.getScene().lookup("#verifyResultTitle");
        JFXListView resultList  = (JFXListView) this.stage.getScene().lookup("#verifyResultListView");
        resultList.getItems().clear();
        resultList.setMaxWidth(Double.MAX_VALUE);
        if (pass.isEmpty()) {
            resultLabel.getStyleClass().clear();
            resultLabel.getStyleClass().add("color-danger");
            resultLabel.setText("No file has passed the verification!");
        } else if (fail.isEmpty()) {
            resultLabel.getStyleClass().clear();
            resultLabel.getStyleClass().add("color-success");
            resultLabel.setText("All files passed!");
        } else {
            resultLabel.getStyleClass().clear();
            resultLabel.getStyleClass().add("color-info");
            resultLabel.setText(pass.size() + " file(s) out of "+(pass.size()+fail.size())+ " passed");
        }

        for (String passFile : pass) {
            resultList.getItems().addAll(new ResultListItem(passFile, true, "John Doe", new Date()));
        }
        for (String failFile : fail) {
            resultList.getItems().addAll(new ResultListItem(failFile, false, "John Doe", new Date()));
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
                        this.showErrorDialog(errorDialog, masterVerify, "Unable to open the file...",
                                "No application associated with the specified file.");
                    }
                };
            }
            listItem.setViewButtonAction(event);
            fileListItems.add(listItem);
        });
        this.filesListView.getItems().addAll(FXCollections.observableList(fileListItems));
    }
    private List<File> getCurrentFiles (ObservableList<FileListItem> items) {
        List<File> files = items.stream().map(FileListItem::getFile).collect(Collectors.toList());
        return files;
    }

    /**
     * Submits verification
     */
    @FXML private void handleVerifyFilesButtonAction() {
        List<File> selectedFiles = this.getSelectedFiles ();
        if (selectedFiles.isEmpty()){
            this.showInfoDialog(infoDialog, masterVerify, "No file selected",
                    "Please, select the signature file and a least one signed file.");
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
                this.displayVerifyResult(pass, fail);

            } catch (IOException|SAXException|ParserConfigurationException e) {
                this.showErrorDialog(errorDialog, masterVerify, "Invalid signature file!",
                        "Unable to parse that signature file.\nIt looks like the file is corrupted.");
            } catch (SignatureException e) {
                this.showErrorDialog(errorDialog, masterVerify, "Invalid signature!",
                        "Is it the wright signature file ?");
            } catch (NoSuchAlgorithmException|InvalidKeyException|NoSuchProviderException e) {
                this.showErrorDialog(errorDialog, masterVerify, "Invalid key...",
                        "Unable to validate the signature. Your file may be corrupted.");
            } catch (CertificateException e) {
                this.showErrorDialog(errorDialog, masterVerify, "Invalid certificate!",
                        "Unable to validate your certificate.\nIs your signature file corrupted ?");
            }
        }
    }
    /**
     * Signature file selection action
     */
    @FXML private void handleSelectVerifyFileButtonAction () {
        this.fileChooser.setTitle("Select the signature file");
        this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Signature files (SIG)", "*.sig"));
        File file = this.fileChooser.showOpenDialog(this.stage);
        this.fileChooser.getExtensionFilters().clear();
        if (file != null) {
            this.signatureFile = file;
            this.signatureFileLabel.textProperty().set(file.getName());
        }
    }
    /**
     * Signed files selection action
     */
    @FXML private void handleSelectSignFileButtonAction () {
        List<File> files = this.fileChooser.showOpenMultipleDialog(this.stage);
        if (files != null) {
            files.stream().filter(file -> !this.filesToVerify.contains(file)).forEach(file -> this.filesToVerify.add(file));
            this.filesToSignCount.textProperty().set(this.filesToVerify.size() +" file(s)");
            this.populateListView();
        }
    }
}
