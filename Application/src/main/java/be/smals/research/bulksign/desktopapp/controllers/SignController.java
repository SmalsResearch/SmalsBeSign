package be.smals.research.bulksign.desktopapp.controllers;

import be.fedict.commons.eid.client.BeIDCard;
import be.fedict.commons.eid.client.CancelledException;
import be.fedict.commons.eid.client.OutOfCardsException;
import be.fedict.commons.eid.client.spi.BeIDCardsUI;
import be.smals.research.bulksign.desktopapp.eid.EID;
import be.smals.research.bulksign.desktopapp.eid.EIDObserver;
import be.smals.research.bulksign.desktopapp.eid.external.UserCancelledException;
import be.smals.research.bulksign.desktopapp.services.DigestService;
import be.smals.research.bulksign.desktopapp.services.EIDService;
import be.smals.research.bulksign.desktopapp.services.SigningService;
import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
import be.smals.research.bulksign.desktopapp.ui.FileListItem;
import be.smals.research.bulksign.desktopapp.utilities.Settings;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import be.smals.research.bulksign.desktopapp.utilities.Utilities;
import be.smals.research.bulksign.desktopapp.utilities.VerifySigningOutput;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPasswordField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.List;

/**
 * Sign screen controller
 *
 * Handles events from Sign view
 */
public class SignController extends Controller implements EIDObserver, BeIDCardsUI{

    private SigningService signingService;
    private VerifySigningService verifySigningService;
    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;
    private List<File> filesToSign;
    private OpenViewerFX viewerFx;

    @FXML private Label fileCountLabel;
    @FXML private ListView filesListView;
    @FXML private Pane readerPane;
    @FXML private StackPane masterSign;
    @FXML private JFXDialog infoDialog;
    @FXML private JFXDialog errorDialog;
    @FXML private JFXDialog successDialog;
    @FXML private JFXDialog waitingDialog;
    @FXML private JFXCheckBox selectAllCheckBox;

    /**
     * Constructor
     */
    public SignController() {
        this.filesToSign = new ArrayList<>();
        try {
            this.signingService         = new SigningService();
            this.verifySigningService   = new VerifySigningService();
        } catch (IOException | PKCS11Exception e) {
            e.printStackTrace();
        }

        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Select a file");
        this.directoryChooser = new DirectoryChooser();
        this.directoryChooser.setTitle("Select a directory");
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void initController(MainController mainController, Stage stage) {
        super.initController(mainController, stage);
        EIDService.getInstance().registerAsEIDObserver(this);

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
        this.successDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
    }
    /**
     * Handles the output file saving process
     *
     * @param signature
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    private void saveSigningOutput(List<File> files, byte[] signature, List<X509Certificate> certificateChain) throws IOException, ParserConfigurationException, TransformerException {
        this.directoryChooser.setTitle("Save the signing output");
        File dir = this.directoryChooser.showDialog(this.stage);
        if (dir != null) {
            try {
                SigningOutput signingOutput = new SigningOutput(null, signature, certificateChain);
                this.signingService.saveSigningOutput(files, signingOutput, dir.getPath()+File.separator+"SignatureFile.sig");
                this.showSuccessDialog(successDialog, masterSign, "File saved!",
                        "Signature successfully saved!\nSigned files can be found at "+dir.getPath());
            } catch (CertificateEncodingException e) {
                e.printStackTrace();
            }
        } else {
            this.showErrorDialog(errorDialog, masterSign, "Save aborted!", "Nothing is saved from your last signing request.");
        }
    }
    /**
     * Populates the ListView with the files selected by the user
     */
    private void populateListView () {
        List<File> files = Utilities.getInstance().getFileListFromFileListView(this.filesListView);
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
                        this.showErrorDialog(this.errorDialog, this.masterSign, "Unable to open the file...",
                                "No application associated with the specified file.");
                    }
                };
            }
            listItem.setViewButtonAction(event);
            fileListItems.add(listItem);
        });
        this.filesListView.getItems().addAll(FXCollections.observableList(fileListItems));
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
     */
    @FXML private void handleSignFilesButtonAction() {
        List<File> selectedFiles = this.getSelectedFiles ();
        if (selectedFiles.isEmpty()){
            this.showInfoDialog(infoDialog, masterSign, "No file to sign",
                    "Please, select at least one file before you proceed.");
        } else {
            // Sign Process
            try {
                this.signWithEID(selectedFiles);
            } catch (IOException | ParserConfigurationException | TransformerException | CardException | CertificateException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Defines the selected file
     */
    @FXML private void handleSelectFilesToSignButtonAction() {
        List<File> files = fileChooser.showOpenMultipleDialog(this.stage);
        if (files != null) {
            files.stream().filter(file -> !this.filesToSign.contains(file)).forEach(file -> this.filesToSign.add(file));
            this.fileCountLabel.textProperty().set(this.filesToSign.size() + " file(s)");
            this.populateListView();
        }
    }

    // ---------- ------------------------------------------------------------------------------------------------------
    /**
     * Validates the user pin code
     *
     * @return true if the PIN code is correct
     */
    private boolean askAndVerifyPin() {
        Dialog<String> pinDialog = new Dialog<>();
        pinDialog.setTitle("PIN Code");
        pinDialog.setHeaderText("Please, enter your Pin code");
        JFXPasswordField passwordField = new JFXPasswordField();
        passwordField.setFocusColor(Color.web("#52BBFE"));
        passwordField.setPrefWidth(200);
        ButtonType validateButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        pinDialog.getDialogPane().getButtonTypes().add(validateButtonType);
        pinDialog.getDialogPane().setContent(passwordField);
        Platform.runLater( () -> passwordField.requestFocus());

        Optional<String> result = pinDialog.showAndWait();
        if (result.isPresent()) {
            try {
                return EIDService.getInstance().isPinValid(passwordField.getText().trim().toCharArray());
            } catch (UserCancelledException|CardException e) {
                this.showErrorDialog(errorDialog, masterSign, "Verifying PIN code...",
                        "Unable to verify your PIN.\nError message : "+e.getMessage());
            }
        }

        return false;
    }
    /**
     * Performs signing with eID card process
     *
     * @param selectedFiles files to sign
     * @throws CardException
     * @throws CertificateException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    private void signWithEID(List<File> selectedFiles)
            throws CardException, CertificateException, IOException, ParserConfigurationException, TransformerException {

        FileInputStream[] inputFiles = new FileInputStream[selectedFiles.size()];
        if (!Settings.getInstance().isEIDCardPresent()) {
            this.showInfoDialog(infoDialog, masterSign, "Missing eID card...",
                    "You must insert your eID card inside the reader before you proceed.");
        } else {
            // Prepare files
            showWaitingDialog(waitingDialog, masterSign, "Computing\nDigests...");
            Task<String> prepareTask = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    // Compute Master Digest
                    for (int i = 0; i < selectedFiles.size(); i++) {
                        inputFiles[i] = new FileInputStream(selectedFiles.get(i));
                    }
                    String masterDigest = DigestService.getInstance().computeMasterDigest(inputFiles);

                    return masterDigest;
                }
            };
            prepareTask.setOnSucceeded(event -> {
                this.updateWaitingDialogMessage("Verifying\ncertificates...");
                List<X509Certificate> certificateChain = null;
                try {
                    certificateChain = EIDService.getInstance().getBeIDCertificateChain();
                    VerifySigningOutput verifySigningOutput = new VerifySigningOutput();
                    verifySigningOutput = this.verifySigningService.verifyChainCertificate(certificateChain);
                    verifySigningOutput.consoleOutput();

                    if (!verifySigningOutput.getOutputResult().equals(VerifySigningOutput.VerifyResult.FAILED)) {
                        this.closeInputFiles(inputFiles);
                        // Sign
                        System.out.println("certificate verification // DONE");
                        this.updateWaitingDialogMessage("Signing...");
                        this.signWithBeIDAndSave(selectedFiles, prepareTask, certificateChain);
                    } else {
                        waitingDialog.close();
                        this.showErrorDialog(errorDialog, masterSign, "Certificate Verification",
                                verifySigningOutput.outputCertificateResult());
                    }
                } catch (NoSuchAlgorithmException | CardException | InvalidKeyException | SignatureException | NoSuchProviderException | CertificateException | IOException e) {
                    this.waitingDialog.close();
                    this.showErrorDialog(errorDialog, masterSign, "Error while verifying...",
                            e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException | CancelledException e) {
                    e.printStackTrace();
                }
            });
            new Thread(prepareTask).start();
        }
    }

    private void closeInputFiles(FileInputStream[] inputFiles) {
        for (FileInputStream file : inputFiles) {
            try {
                file.close();
            } catch (IOException e) {
                this.waitingDialog.close();
                this.showErrorDialog(errorDialog, masterSign, "Error while signing",
                        e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private void signWithBeIDAndSave(List<File> selectedFiles, Task<String> prepareTask, List<X509Certificate> certificateChain) {
        byte[] signature = this.signingService.signWithEID(prepareTask.getValue(), "SHA-1", EID.NON_REP_KEY_ID);
        if (signature != null) {
            updateWaitingDialogMessage("Saving...");
            try {
                saveSigningOutput(selectedFiles, signature, certificateChain);
                waitingDialog.close();
            } catch (IOException | ParserConfigurationException | TransformerException e) {
                waitingDialog.close();
                showErrorDialog(errorDialog, masterSign, "Saving error",
                        e.getMessage());
                e.printStackTrace();
            }
        } else {
            waitingDialog.close();
            showErrorDialog(errorDialog, masterSign, "Signing FAILED",
                    "Error while signing with eID");
        }
    }

    /**
     * Select / Deselect selected all checkbox action
     */
    @FXML public void handleSelectAllAction() {
        for (Object item : filesListView.getItems())
            ((FileListItem)item).setFileSelected(selectAllCheckBox.isSelected());
    }

    // ---------- Observable notifications
    @Override
    public void getPinCode() {
        this.askAndVerifyPin();
    }
    // BeIDCards UI ----------------------------------------------------------------------------------------------------

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void adviseCardTerminalRequired() {
        System.out.println("Advise CardTerminal Required");
    }

    @Override
    public void adviseBeIDCardRequired() throws CancelledException {
        System.out.println("Advise BeIDCard Required");
    }

    @Override
    public void adviseBeIDCardRemovalRequired() {
        System.out.println("Advise BeIDCardRemovalCard Required");
    }

    @Override
    public void adviseEnd() {
        System.out.println("Advise End");
    }

    @Override
    public BeIDCard selectBeIDCard(Collection<BeIDCard> collection) throws CancelledException, OutOfCardsException {
        List<BeIDCard> cards = new ArrayList<>(collection);
        return cards.get(0);
    }
    @Override
    public void eIDCardInsertedDuringSelection(BeIDCard beIDCard) {
        System.out.println("eIDCardInserted");
    }

    @Override
    public void eIDCardRemovedDuringSelection(BeIDCard beIDCard) {
        System.out.println("eIDCard Removed");

    }
}
