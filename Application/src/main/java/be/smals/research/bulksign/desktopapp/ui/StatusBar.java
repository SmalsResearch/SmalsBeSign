package be.smals.research.bulksign.desktopapp.ui;

import be.smals.research.bulksign.desktopapp.services.EIDService;
import be.smals.research.bulksign.desktopapp.utilities.Message.MessageType;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import javax.smartcardio.CardException;

public class StatusBar extends HBox{
    private Label messageLabel;
    public StatusBar () {
        messageLabel = new Label("Ready.");
        this.getChildren().add(messageLabel);

//        new Thread(() -> {
//            try {
//                while (true) {
//                    if (EIDService.getInstance().isEIDPresent()) {
//                        Platform.runLater(() -> setMessage(MessageType.SUCCESS, "Ready to sign!"));
//                        while (EIDService.getInstance().isEIDStillPresent())
//                            ;
//                    } else {
//                        if (EIDService.getInstance().)
//                        Platform.runLater(() -> setMessage(MessageType.ERROR, "Please plug your eID card in..."));
//                    }
//                    Thread.sleep(3000);
//                }
//            } catch (CardException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
        Service<String> checkCardService = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        try {
                            while (true) {
                                setMessage(MessageType.INFO, "Waiting for an eID card reader...");
                                EIDService.getInstance().waitForReader();
                                setMessage(MessageType.INFO, "Waiting for an eID card...");
                                EIDService.getInstance().waitForCard();
                                setMessage(MessageType.SUCCESS, "Ready to sign!");
                                Thread.sleep(2000);
                            }
                        } catch (CardException e) {
                            e.printStackTrace();
                        }
                        return null;
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
                break;
            default:
        }
        this.messageLabel.setText(message);
    }
}
