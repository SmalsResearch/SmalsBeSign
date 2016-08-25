package be.smals.research.bulksign.desktopapp.ui;

import be.fedict.commons.eid.client.BeIDCard;
import be.fedict.commons.eid.client.event.BeIDCardEventsListener;
import be.fedict.commons.eid.client.event.CardTerminalEventsListener;
import be.smals.research.bulksign.desktopapp.services.EIDService;
import be.smals.research.bulksign.desktopapp.services.EIDServiceObserver;
import be.smals.research.bulksign.desktopapp.utilities.Message.MessageType;
import be.smals.research.bulksign.desktopapp.utilities.Settings;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

public class StatusBar extends HBox implements EIDServiceObserver, BeIDCardEventsListener, CardTerminalEventsListener{
    private Label messageLabel;
    private JFXSpinner spinner;
    public StatusBar () {
        this.messageLabel   = new Label("Ready.");
        this.spinner        = new JFXSpinner();
        this.messageLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.messageLabel, Priority.ALWAYS);
        this.spinner.setRadius(4);

        this.getChildren().addAll(messageLabel);
        this.getStyleClass().add("statusBar");
        this.setAlignment(Pos.CENTER);

//        this.createAndStartCheckCardService();
    }

    /**
     * Creates and starts the service that checks either the reader and the card are connected or not
     */
    private void createAndStartCheckCardService() {
        Service<String> checkCardService = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        try {
                            while (true) {
                                EIDService.getInstance().waitForReader();
                                EIDService.getInstance().waitForCard();
                                Platform.runLater(() -> {
                                    getChildren().remove(spinner);
                                    setMessage(MessageType.DEFAULT, "Ready to sign!");
                                });

                                while (EIDService.getInstance().isEIDStillPresent())
                                    ;
                                Thread.sleep(2000);
                            }
                        } catch (CardException|InterruptedException e) {
                            e.printStackTrace();
                        }
                        return "CheckCardTask Finished.";
                    }
                };
            }
        };
        checkCardService.start();
    }

    public void setMessage (MessageType messageType, String message) {
        this.messageLabel.setText(message);

        switch (messageType) {
            case ERROR:
                this.messageLabel.getStyleClass().clear();
                this.messageLabel.getStyleClass().add("color-danger");
                break;
            case SUCCESS:
                this.messageLabel.getStyleClass().clear();
                this.messageLabel.getStyleClass().add("color-success");
                break;
            case INFO:
                this.messageLabel.getStyleClass().clear();
                this.messageLabel.getStyleClass().add("color-info");
                break;
            default:
                this.messageLabel.getStyleClass().clear();
                this.messageLabel.getStyleClass().add("color-white");
        }
        this.messageLabel.setWrapText(true);
    }

    // ----- Implements ------------------------------------------------------------------------------------------------
    @Override
    public void getPinCode() {}

    @Override
    public void cardReaderNeeded() {
        if (!this.getChildren().contains(spinner))
            getChildren(). add(spinner);
        Platform.runLater(() -> setMessage(MessageType.DEFAULT, "Waiting for an eID card reader..."));
    }
    @Override
    public void cardNeeded() {
        if (!this.getChildren().contains(spinner))
            getChildren().add(spinner);
        Platform.runLater(() -> setMessage(MessageType.DEFAULT, "Waiting for an eID card..."));
    }

    // ----- EID Events
    @Override
    public void eIDCardEventsInitialized() {}

    @Override
    public void eIDCardInserted(CardTerminal cardTerminal, BeIDCard beIDCard) {
        Platform.runLater(() ->setMessage(MessageType.DEFAULT, "EID Card inserted"));
        Settings.getInstance().setEIDCardPresent(true);
    }

    @Override
    public void eIDCardRemoved(CardTerminal cardTerminal, BeIDCard beIDCard) {
        Platform.runLater(() -> setMessage(MessageType.DEFAULT, "EID Card removed"));
        Settings.getInstance().setEIDCardPresent(false);
    }
    // ----- Terminal Events
    @Override
    public void terminalEventsInitialized() {}

    @Override
    public void terminalAttached(CardTerminal cardTerminal) {
        Platform.runLater(() ->setMessage(MessageType.DEFAULT, "A new terminal has been attached"));
    }

    @Override
    public void terminalDetached(CardTerminal cardTerminal) {
        Platform.runLater(() ->setMessage(MessageType.DEFAULT, "A terminal has been detached"));
    }
}
