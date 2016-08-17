package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.abstracts.Controller;
import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
import be.smals.research.bulksign.desktopapp.ui.FileListItem;
import be.smals.research.bulksign.desktopapp.ui.ResultListItem;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import be.smals.research.bulksign.desktopapp.utilities.VerifySigningOutput;
import com.jfoenix.controls.JFXCheckBox;
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
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @FXML private StackPane masterVerify;
    @FXML private ListView filesListView;
    @FXML private Pane readerPane;
    @FXML private JFXDialog infoDialog;
    @FXML private JFXDialog errorDialog;
    @FXML private JFXDialog verifyResultDialog;
    @FXML private JFXCheckBox selectAllCheckBox;

    private List<File> filesToVerify;

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
    private void displayVerifyResult(List<VerifySigningOutput> pass, List<VerifySigningOutput> fail) {
        verifyResultDialog.show(masterVerify);
        Label resultLabel       = (Label) this.stage.getScene().lookup("#verifyResultTitle");
        JFXListView resultList  = (JFXListView) this.stage.getScene().lookup("#verifyResultListView");
        resultList.getItems().clear();

        if (pass.isEmpty()) {
            resultLabel.getStyleClass().clear();
            resultLabel.getStyleClass().add("color-danger");
            resultLabel.setText("No file has passed the verification!");
        } else if (fail.isEmpty()) {
            resultLabel.getStyleClass().clear();
            resultLabel.getStyleClass().add("color-success");
            resultLabel.setText("All files are ok!");
        } else {
            resultLabel.getStyleClass().clear();
            resultLabel.getStyleClass().add("color-info");
            resultLabel.setText(pass.size() + " file(s) out of "+(pass.size()+fail.size())+ " passed");
        }

        for (VerifySigningOutput passOutput : pass) {
            resultList.getItems().add(new ResultListItem(passOutput.fileName, true, passOutput.signedBy, passOutput.signedAt));
        }
        for (VerifySigningOutput failOutput : fail) {
            resultList.getItems().add(new ResultListItem(failOutput.fileName, false, failOutput.signedBy, failOutput.signedAt));
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
            List<VerifySigningOutput> pass = new ArrayList<>();
            List<VerifySigningOutput> fail = new ArrayList<>();
            for (File signedFile : selectedFiles) {
                try {
                    Map<String, File> files = this.verifySigningService.getFiles(signedFile);
                    signingOutput = this.verifySigningService.getSigningOutput(files.get("SIGNATURE"));
                    boolean isValid = this.verifySigningService.verifySigning(new FileInputStream(files.get("FILE")), signingOutput);
                    if (isValid) {
                        pass.add(new VerifySigningOutput(signedFile.getName(), signingOutput.author, signingOutput.createdAt));
                    } else {
                        fail.add(new VerifySigningOutput(signedFile.getName(), signingOutput.author, signingOutput.createdAt));
                    }
                    for (File file : files.values())
                        Files.deleteIfExists(file.toPath());
                } catch (IOException|SAXException|ParserConfigurationException|CertificateException
                        |SignatureException|NoSuchAlgorithmException|InvalidKeyException|NoSuchProviderException e) {
                    fail.add(new VerifySigningOutput(signedFile.getName(), signingOutput.author, signingOutput.createdAt));
                }
            }

            // Display result
            this.displayVerifyResult(pass, fail);
        }
    }
    /**
     * Signed files selection action
     */
    @FXML private void handleSelectSignFileButtonAction () {
        this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Signed Files (SIGNED.ZIP)", "*.signed.zip"));
        List<File> files = this.fileChooser.showOpenMultipleDialog(this.stage);
        this.fileChooser.getExtensionFilters().clear();
        if (files != null) {
            files.stream().filter(file -> !this.filesToVerify.contains(file)).forEach(file -> this.filesToVerify.add(file));
            this.filesToSignCount.textProperty().set(this.filesToVerify.size() +" file(s)");
            this.populateListView();
        }
    }
    /**
     * Select / Deselect selected all checkbox action
     */
    @FXML private void handleSelectAllAction () {
        for (Object item : this.filesListView.getItems())
            ((FileListItem) item).setFileSelected(this.selectAllCheckBox.isSelected());
    }

    public void handleCloseVerifyDialog() {
        verifyResultDialog.close();
    }
}
