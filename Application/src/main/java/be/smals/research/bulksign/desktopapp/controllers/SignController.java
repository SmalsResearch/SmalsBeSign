package be.smals.research.bulksign.desktopapp.controllers;

import be.smals.research.bulksign.desktopapp.services.SigningService;
import be.smals.research.bulksign.desktopapp.utilities.FileListItem;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jpedal.examples.viewer.Commands;
import org.jpedal.examples.viewer.OpenViewerFX;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    private OpenViewerFX viewerFx;

    @FXML private Label fileCountLabel;
    @FXML private ListView filesListView;
    @FXML private Pane readerPane;

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
     * Sign the selected file
     *
     * @param event click on signFile button
     */
    @FXML
    private void handleSignFilesButtonAction(ActionEvent event) {
        List<File> selectedFiles = this.getSelectedFiles ();
        if (selectedFiles.isEmpty()){
            Alert noFileSelectedDialog = new Alert(Alert.AlertType.INFORMATION, "Please, select at least one file.", ButtonType.CLOSE);
            noFileSelectedDialog.setTitle("No file to sign");
            noFileSelectedDialog.setHeaderText(null);
            noFileSelectedDialog.showAndWait();
        } else {
            FileInputStream[] inputFiles = new FileInputStream[selectedFiles.size()];

            try {
                for ( int i=0; i < selectedFiles.size(); i++ ) {
                    inputFiles[i] = new FileInputStream(selectedFiles.get(i));
                }

                byte[] signature = this.signingService.sign(inputFiles);
                this.saveSigningOutput(signature);

                for (FileInputStream file : inputFiles)
                     file.close();

            } catch (IOException | ParserConfigurationException | TransformerException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    /**
     * Defines the selected file
     *
     * @param event click on the selectFile button
     */
    private void handleSelectFilesToSignButtonAction(ActionEvent event) {
        List<File> files = fileChooser.showOpenMultipleDialog(this.stage);
        if (files != null) {
            files.stream().filter(file -> !this.filesToSign.contains(file)).forEach(file -> this.filesToSign.add(file));
            this.fileCountLabel.textProperty().set(this.filesToSign.size() + " file(s) to sign");
            this.populateListView();
        }
    }
    /**
     * Handles the output file saving process
     *
     * @param signature
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    private void saveSigningOutput(byte[] signature) throws IOException, ParserConfigurationException, TransformerException {
        fileChooser.setTitle("Save the signature output");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Signature files (SIG)", "*.sig"));
        File fileToSave = fileChooser.showSaveDialog(this.stage);
        fileChooser.getExtensionFilters().clear();
        if (fileToSave != null) {
            SigningOutput signingOutput = new SigningOutput(null, signature);
            this.signingService.saveSigningOutput(signingOutput, fileToSave.getPath());
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
    }
    public void setStage (Stage stage) {
        this.stage = stage;
        this.viewerFx = new OpenViewerFX(readerPane, null);

        this.viewerFx.setupViewer();
    }
    /**
     * Populates the ListView with the files selected by the user
     */
    private void populateListView () {
        this.filesListView.getItems().clear();
        List<FileListItem> fileListItems = new ArrayList<>();
        for ( File file : this.filesToSign) {
            FileListItem listItem = new FileListItem(file);
            listItem.setViewButtonAction(new EventHandler() {
                @Override
                public void handle(Event event) {
                    Object[] args = {file};
                    viewerFx.executeCommand(Commands.OPENFILE, args);
                }
            });

            fileListItems.add(listItem);
        }
        this.filesListView.setItems(FXCollections.observableList(fileListItems));
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
            if (fileListItem.isSelected())
                selectedFiles.add(fileListItem.getFile());
        }
        return selectedFiles;
    }
}
