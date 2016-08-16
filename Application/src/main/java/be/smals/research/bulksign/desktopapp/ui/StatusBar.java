package be.smals.research.bulksign.desktopapp.ui;

import be.smals.research.bulksign.desktopapp.abstracts.EIDServiceObserver;
import be.smals.research.bulksign.desktopapp.services.EIDService;
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

public class StatusBar extends HBox implements EIDServiceObserver{
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

        this.createAndStartCheckCardService();
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
                                Settings.getInstance().setEIDCardPresent(false);
                                EIDService.getInstance().waitForReader();
                                EIDService.getInstance().waitForCard();
                                Settings.getInstance().setEIDCardPresent(true);
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
        messageLabel.setText(message);
    }

    // ----- Implements ------------------------------------------------------------------------------------------------
    @Override
    public void getPinCode() {}
    @Override
    public void cardReaderNeeded() {
        Platform.runLater(() -> {
            if (!this.getChildren().contains(spinner))
                getChildren(). add(spinner);
            setMessage(MessageType.DEFAULT, "Waiting for an eID card reader...");
        });
    }
    @Override
    public void cardNeeded() {
        if (!this.getChildren().contains(spinner))
            getChildren(). add(spinner);
        Platform.runLater(() -> setMessage(MessageType.DEFAULT, "Waiting for an eID card..."));
    }
}
