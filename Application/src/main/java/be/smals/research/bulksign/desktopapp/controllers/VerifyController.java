package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.exception.BulkSignException;
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
import javafx.collections.ObservableList;
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
    @FXML private Label readerTitle;
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
        this.infoDialog.setOverlayClose(false);
        this.errorDialog.setOverlayClose(false);
        this.waitingDialog.setOverlayClose(false);
        this.verifyResultDialog.setOverlayClose(false);
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
            listItem.setViewButtonAction(event -> {
                try {
                    // Unzip .signed.zip file then get main file
                    final Map<String, FileWithAltName> signedZipFiles = Utilities.getInstance().getFilesFromSignedFile(file);
                    FileWithAltName signedFile = signedZipFiles.get("FILE");
                    // Check extension to display in viewer or not
                    if (Utilities.getInstance().getFileExtension(signedFile.name).equalsIgnoreCase("pdf")) {
                        viewerFx.getPdfDecoder().closePdfFile();
                        Object[] args = {signedFile.file};
                        viewerFx.executeCommand(Commands.OPENFILE, args);
                        readerTitle.setText(file.getName());
                        listItem.setFileViewed(true);
                        listItem.setFileInViewer(true);
                        setFileInViewer(listItem);
                    } else {
                        try {
                            Desktop.getDesktop().open(signedFile.file);
                            listItem.setFileViewed(true);
                        } catch (IOException e) {
                            this.showErrorDialog(errorDialog, masterVerify, "Unable to open the file...",
                                    "No application associated with the specified file.");
                        }
                    }
                } catch (IOException e) {
                    this.showErrorDialog(errorDialog, masterVerify, "Open File", "Unable to open the .signed.zip file.");
                } catch (BulkSignException e) {
                    this.showErrorDialog(errorDialog, masterVerify, "Open file", e.getMessage());
                }

            });
            listItem.setFileSelected(true);
            fileListItems.add(listItem);
        });
        this.filesListView.getItems().addAll(FXCollections.observableList(fileListItems));
    }
    private void setFileInViewer (FileListItem inViewerItem) {
        ObservableList<FileListItem> items = this.filesListView.getItems();
        items.stream().filter(fileListItem ->
                (!fileListItem.equals(inViewerItem) && fileListItem.isFileInViewer()))
                .forEach(fileListItem -> fileListItem.setFileInViewer(false));
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

        resultLabel.setText(failedCount+" - FAILED | "+warningCount+" - WARNINGS | "+okCount+ " - OK");
    }
    private List<VerifySigningOutput> verifyAndGetResults(List<File> selectedFiles) {
        List<VerifySigningOutput> results = new ArrayList<>();
        float ind = 1.0f;
        float nbFiles = selectedFiles.size();
        for (File signedFile : selectedFiles) {
            int percent = (int)((ind/nbFiles)*100);
            Platform.runLater(() -> updateWaitingDialogMessage(percent + "% done..."));
            VerifySigningOutput verifySigningOutput = new VerifySigningOutput();
            String filename = "";
            try {
                Map<String, FileWithAltName> files = Utilities.getInstance().getFilesFromSignedFile(signedFile);

                filename = files.get("FILE").name;
                SigningOutput signingOutput = verifySigningService.getSigningOutput(files.get("SIGNATURE").file);
                if (signingOutput != null) {
                    verifySigningOutput = verifySigningService.verifySigning(files.get("FILE").file, signingOutput);
                    results.add(verifySigningOutput);
                } else {
                    throw new BulkSignException("Error while parsing the signature file\n--- The signature file might be corrupted.");
                }
            } catch (SignatureException | NoSuchAlgorithmException | CertificateException |
                    NoSuchProviderException | ParserConfigurationException | IOException | SAXException | ParseException e) {
                e.printStackTrace();
                filename = (filename=="" || filename==null) ? signedFile.getName() : filename;
                verifySigningOutput.errorDuringVerification = true;
                verifySigningOutput.errorMessage = filename+" - FAILED\n- "+e.getMessage();
                results.add(verifySigningOutput);
            } catch (InvalidKeyException | BulkSignException e) {
                filename = (filename=="" || filename==null) ? signedFile.getName() : filename;
                verifySigningOutput.errorDuringVerification = true;
                verifySigningOutput.errorMessage = filename+" - FAILED\n- "+e.getMessage();
                results.add(verifySigningOutput);
            } catch (Exception e) {
                e.printStackTrace();
                filename = (filename.equals("") || filename==null) ? signedFile.getName() : filename;
                verifySigningOutput.errorDuringVerification = true;
                verifySigningOutput.errorMessage = filename+" - FAILED\n- "+e.getMessage();
                results.add(verifySigningOutput);
            }
            ind++;
        }
        this.waitingDialog.close();
        return results;
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
            this.showWaitingDialog(waitingDialog, masterVerify, "");
            Task<List> verifyTask = new Task<List>() {
                @Override
                protected List call() throws Exception {
                    return verifyAndGetResults(selectedFiles);
                }
            };
            verifyTask.setOnSucceeded( event -> {
                waitingDialog.close();
                this.displayVerifyResult(verifyTask.getValue());
            });
            new Thread(verifyTask).start();
        }
    }
    /**
     * Signed files selection action
     */
    @FXML private void handleSelectSignFileButtonAction () {
        this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Signed Files (ZIP)", "*.zip"));
        this.fileChooser.setInitialDirectory(this.lastDirectory);
        List<File> files = this.fileChooser.showOpenMultipleDialog(this.stage);
        this.fileChooser.getExtensionFilters().clear();
        if (files != null) {
            this.lastDirectory = files.get(0).getParentFile();
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
    @FXML public void handleClearListAction() {
        this.filesToVerify.clear();
        this.filesListView.getItems().clear();
        this.selectAllCheckBox.setSelected(false);
        this.filesToSignCount.setText("");

        this.readerTitle.setText("No file in viewer");
        this.viewerFx.getPdfDecoder().closePdfFile();
    }
}
