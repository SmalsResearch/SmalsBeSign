package be.smals.research.bulksign.desktopapp.controllers;

import be.fedict.commons.eid.client.BeIDCard;
import be.fedict.commons.eid.client.CancelledException;
import be.fedict.commons.eid.client.OutOfCardsException;
import be.fedict.commons.eid.client.spi.BeIDCardsUI;
import be.fedict.commons.eid.client.spi.UserCancelledException;
import be.smals.research.bulksign.desktopapp.exception.BulkSignException;
import be.smals.research.bulksign.desktopapp.services.DigestService;
import be.smals.research.bulksign.desktopapp.services.EIDService;
import be.smals.research.bulksign.desktopapp.services.SigningService;
import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
import be.smals.research.bulksign.desktopapp.ui.FileListItem;
import be.smals.research.bulksign.desktopapp.utilities.Settings;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import be.smals.research.bulksign.desktopapp.utilities.Utilities;
import be.smals.research.bulksign.desktopapp.utilities.VerifySigningOutput;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Sign screen controller
 *
 * Handles events from Sign view
 */
public class SignController extends Controller implements BeIDCardsUI{

    private SigningService signingService;
    private VerifySigningService verifySigningService;
    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;
    private List<File> filesToSign;
    private OpenViewerFX viewerFx;

    @FXML private Label fileCountLabel;
    @FXML private ListView filesListView;
    @FXML private Pane readerPane;
    @FXML private Label readerTitle;
    @FXML private StackPane masterSign;
    @FXML private JFXDialog infoDialog;
    @FXML private JFXDialog errorDialog;
    @FXML private JFXDialog successDialog;
    @FXML private JFXDialog waitingDialog;
    @FXML private JFXDialog signResultDialog;
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

        this.setupViewer();

        // Setup dialogs
        this.infoDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.errorDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.successDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.signResultDialog.setTransitionType(JFXDialog.DialogTransition.TOP);
        this.infoDialog.setOverlayClose(false);
        this.errorDialog.setOverlayClose(false);
        this.successDialog.setOverlayClose(false);
        this.waitingDialog.setOverlayClose(false);
        this.signResultDialog.setOverlayClose(false);
    }

    private void setupViewer() {
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

    /**
     * Handles the output file saving process
     *
     * @param signature
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    private void saveSigningOutput(List<File> files, byte[] signature, List<X509Certificate> certificateChain,
                                   VerifySigningOutput verifySigningOutput) throws IOException, ParserConfigurationException, TransformerException {
        this.directoryChooser.setTitle("Save the signing output");
        this.directoryChooser.setInitialDirectory(this.lastDirectory);
        File dir = this.directoryChooser.showDialog(this.stage);
        if (dir != null) {
            try {
                this.lastDirectory = dir;
                this.showWaitingDialog(waitingDialog, masterSign,"Saving...");
                SigningOutput signingOutput = new SigningOutput(null, signature, certificateChain);
                this.signingService.saveSigningOutput(files, signingOutput, dir.getAbsolutePath()+File.separator+"SignatureFile.sig");
                this.waitingDialog.close();
                Text line1 = new Text("Signature(s) successfully computed!\nSigned file(s) can be found at "+dir.getAbsolutePath());
                if (verifySigningOutput.getOutputResult().equals(VerifySigningOutput.VerifyResult.WARNING)) {
                    Text line2 = new Text(verifySigningOutput.outputCertificateResult());
                    line2.setFill(Color.ORANGE);
                    this.showSignResultDialog ("File(s) saved!", line1, line2);
                } else {
                    this.showSignResultDialog ("File(s) saved!", line1);
                }
            } catch (CertificateEncodingException e) {
                waitingDialog.close();
                this.showErrorDialog(errorDialog, masterSign, "Saving...", "Error while saving the output file...\n"+e.getMessage());
                e.printStackTrace();
            }
        } else {
            waitingDialog.close();
            this.showErrorDialog(errorDialog, masterSign, "Save aborted!", "Nothing is saved from your last signing request.");
        }
    }
    private void showSignResultDialog (String title, Object... textList) {
        signResultDialog.show(masterSign);
        Label titleLabel     = (Label) this.stage.getScene().lookup("#resultDialogTitle");
        TextFlow bodyText      = (TextFlow) this.stage.getScene().lookup("#resultDialogBody");
        JFXButton closeButton   = (JFXButton) this.stage.getScene().lookup("#closeResultDialogButton");
        JFXButton openButton   = (JFXButton) this.stage.getScene().lookup("#openResultFolderButton");

        closeButton.setOnAction(event -> signResultDialog.close());
        openButton.setOnAction(event -> {
            try {
                Desktop.getDesktop().open(this.lastDirectory);
            } catch (IOException e) {
                this.showErrorDialog(errorDialog, masterSign, "Open directory", "Unable to open the directory : "+this.lastDirectory
                        +"\nMake sure you have required permissions.");
            }
        });
        titleLabel.setText(title);
        bodyText.getChildren().clear();
        for (Object text:textList){
            bodyText.getChildren().add((Text) text);
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
                    viewerFx.getPdfDecoder().closePdfFile();
                    Object[] args = {file};
                    viewerFx.executeCommand(Commands.OPENFILE, args);
                    readerTitle.setText(file.getName());
                    listItem.setFileInViewer(true);
                    listItem.setFileViewed(true);
                    setFileInViewer(listItem);
                };
            } else {
                event = event1 -> {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        this.showErrorDialog(this.errorDialog, this.masterSign, "Unable to open the file...",
                                "No application associated with the specified file.");
                    }
                    listItem.setFileViewed(true);
                };
            }
            listItem.setViewButtonAction(event);
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
        this.fileChooser.setInitialDirectory(this.lastDirectory);
        List<File> files = fileChooser.showOpenMultipleDialog(this.stage);
        if (files != null) {
            this.lastDirectory = files.get(0).getParentFile();
            files.stream().filter(file -> !this.filesToSign.contains(file)).forEach(file -> this.filesToSign.add(file));
            this.fileCountLabel.textProperty().set(this.filesToSign.size() + " file(s)");
            this.populateListView();
        }
    }

    // ---------- ------------------------------------------------------------------------------------------------------
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
        if (!Settings.getInstance().eIDCardIsPresent) {
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
                    verifySigningOutput.digestValid = true;
                    verifySigningOutput.signatureValid = true;
                    this.verifySigningService.verifyCertificates(certificateChain, verifySigningOutput);
                    verifySigningOutput.consoleOutput();

                    if (!verifySigningOutput.getOutputResult().equals(VerifySigningOutput.VerifyResult.FAILED)) {
                        this.closeInputFiles(inputFiles);
                        // Sign
                        this.updateWaitingDialogMessage("Signing...");
                        this.signWithBeIDAndSave(selectedFiles, prepareTask, certificateChain, verifySigningOutput);
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
                } catch (BulkSignException e) {
                    this.waitingDialog.close();
                    this.showErrorDialog(errorDialog, masterSign, "Error while verifying...",
                            e.getMessage());
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
    private void signWithBeIDAndSave(List<File> selectedFiles, Task<String> prepareTask,
                                     List<X509Certificate> certificateChain, VerifySigningOutput verifySigningOutput) {
        byte[] signature;
        try {
            signature = this.signingService.signWithEID(prepareTask.getValue());
        } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            waitingDialog.close();
            return;
        } catch (CardException cardException) {
            this.showErrorDialog(errorDialog, masterSign, "Signing error", "Make sure all required drivers are installed!");
            waitingDialog.close();
            return;
        } catch (CancelledException | UserCancelledException e) {
            this.showErrorDialog(errorDialog, masterSign, "Signing canceled!", "Signing operation canceled!");
            waitingDialog.close();
            return;
        } catch (BulkSignException e) {
            this.showErrorDialog(errorDialog, masterSign, "Signing failed!", e.getMessage());
            waitingDialog.close();
            return;
        }
        if (signature != null && signature.length!=0) {
            try {
                waitingDialog.close();
                this.saveSigningOutput(selectedFiles, signature, certificateChain, verifySigningOutput);
            } catch (IOException | ParserConfigurationException | TransformerException e) {
                waitingDialog.close();
                showErrorDialog(errorDialog, masterSign, "Saving error",
                        e.getMessage());
                e.printStackTrace();
            }
        } else {
            waitingDialog.close();
            showErrorDialog(errorDialog, masterSign, "Signing failed",
                    "Error while signing with the eID card.\n- Did you typed in your correct PIN code ?\n- Do you have you reader's drivers installed ?");
        }
    }
    /**
     * Select / Deselect selected all checkbox action
     */
    @FXML public void handleSelectAllAction() {
        for (Object item : filesListView.getItems())
            ((FileListItem)item).setFileSelected(selectAllCheckBox.isSelected());
    }
    @FXML public void handleClearListAction() {
        this.filesToSign.clear();
        this.filesListView.getItems().clear();
        this.selectAllCheckBox.setSelected(false);
        this.fileCountLabel.setText("");

        this.readerTitle.setText("No file in viewer");
        this.viewerFx.getPdfDecoder().closePdfFile();
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
