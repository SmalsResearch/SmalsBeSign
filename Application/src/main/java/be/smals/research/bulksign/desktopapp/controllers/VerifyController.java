package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
import be.smals.research.bulksign.desktopapp.ui.FileListItem;
import be.smals.research.bulksign.desktopapp.ui.ResultListItem;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import be.smals.research.bulksign.desktopapp.utilities.Utilities;
import be.smals.research.bulksign.desktopapp.utilities.VerifySigningOutput;
import be.smals.research.bulksign.desktopapp.utilities.VerifySigningOutput.FileWithAltName;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @FXML private JFXDialog waitingDialog;
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
        this.waitingDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
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
     * Populates the ListView with the files selected by the user
     */
    private void populateListView () {
        List<File> files = Utilities.getInstance().getFileListFromFileListView(this.filesListView);
        List<FileListItem> fileListItems = new ArrayList<>();
        this.filesToVerify.stream().filter(file -> !files.contains(file)).forEach(file -> {
            FileListItem listItem = new FileListItem(file);
            listItem.setViewButtonAction(event1 -> {
                try {
                    // Unzip .signed.zip file then get main file
                    final Map<String, FileWithAltName> signedZipFiles = Utilities.getInstance().getFilesFromSignedFile(file);
                    FileWithAltName signedFile = signedZipFiles.get("FILE");
                    // Check extension to display in viewer or not
                    if (Utilities.getInstance().getFileExtension(signedFile.name).equalsIgnoreCase("pdf")) {
                        Object[] args = {signedFile.file};
                        viewerFx.executeCommand(Commands.OPENFILE, args);
                    } else {
                        try {
                            Desktop.getDesktop().open(file);
                        } catch (IOException e) {
                            this.showErrorDialog(errorDialog, masterVerify, "Unable to open the file...",
                                    "No application associated with the specified file.");
                        }
                    }
                } catch (IOException e) {
                    this.showErrorDialog(errorDialog, masterVerify, "Open SignedFile", "Unable to open the .signed.zip file.");
                }

            });
            fileListItems.add(listItem);
        });
        this.filesListView.getItems().addAll(FXCollections.observableList(fileListItems));
    }
    /**
     * Used to display verification results
     *
     * @param results List of Verify outputs
     */
    private void displayVerifyResult(List<VerifySigningOutput> results) {
        verifyResultDialog.show(masterVerify);
        Label resultLabel       = (Label) this.stage.getScene().lookup("#verifyResultTitle");
        JFXListView resultList  = (JFXListView) this.stage.getScene().lookup("#verifyResultListView");
        resultList.getItems().clear();
        int okCount = 0, warningCount = 0, failedCount = 0;
        for (VerifySigningOutput result : results) {
            System.out.println(result);
            switch (result.getOutputResult()) {
                case OK: okCount++;
                    break;
                case WARNING: warningCount++;
                    break;
                case FAILED: failedCount++;
                    break;
            }

            resultList.getItems().add(new ResultListItem(result));
        }

        resultLabel.setText(failedCount+" - FAILED, "+warningCount+" - WARNINGS, "+okCount+ " - OK");
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
            showWaitingDialog(waitingDialog, masterVerify, "");
            // >> Waiting screen

            List<VerifySigningOutput> results = new ArrayList<>();
            Task<Void> verifyTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    float ind = 1.0f;
                    float nbFiles = selectedFiles.size();
                    for (File signedFile : selectedFiles) {
                        int percent = (int)((ind/nbFiles)*100);
                        Platform.runLater(() -> updateDialogMessage(percent + "% done..."));
                        try {
                            Map<String, FileWithAltName> files = Utilities.getInstance().getFilesFromSignedFile(signedFile);
                            SigningOutput signingOutput = verifySigningService.getSigningOutput(files.get("SIGNATURE").file);
                            VerifySigningOutput verifySigningOutput = verifySigningService.verifySigning(files.get("FILE").file, signingOutput);
                            verifySigningOutput.fileName = files.get("FILE").name;
                            results.add(verifySigningOutput);
                        } catch (SignatureException | SAXException | NoSuchAlgorithmException | ParseException | InvalidKeyException | CertificateException | IOException | ParserConfigurationException | NoSuchProviderException e) {
                            e.printStackTrace();
                        }
                        ind++;
                    }
                    return null;
                }
            };
            verifyTask.setOnSucceeded(event -> {
                waitingDialog.close();
                displayVerifyResult(results);
            });
            new Thread(verifyTask).start();
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
    /**
     * Used to close verifyResult dialog
     */
    @FXML public void handleCloseVerifyDialog() {
        verifyResultDialog.close();
    }
}
