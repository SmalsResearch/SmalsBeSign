package be.smals.research.bulksign.desktopapp.ui;

import be.fedict.commons.eid.client.BeIDCard;
import be.fedict.commons.eid.client.event.BeIDCardEventsListener;
import be.fedict.commons.eid.client.event.CardTerminalEventsListener;
import be.smals.research.bulksign.desktopapp.utilities.Message.MessageType;
import be.smals.research.bulksign.desktopapp.utilities.Settings;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javax.smartcardio.CardTerminal;

public class StatusBar extends HBox implements BeIDCardEventsListener, CardTerminalEventsListener{
    private Label messageLabel;
    private JFXSpinner spinner;
    public StatusBar () {
        this.messageLabel   = new Label();
        this.spinner        = new JFXSpinner();
        this.messageLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.messageLabel, Priority.ALWAYS);
        this.spinner.setRadius(4);

        this.getChildren().addAll(messageLabel);
        this.getStyleClass().add("statusBar");
        this.setAlignment(Pos.CENTER);

        this.setMessage(MessageType.DEFAULT, "Welcome!");
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
    // ----- EID Events
    @Override
    public void eIDCardEventsInitialized() {}
    @Override
    public void eIDCardInserted(CardTerminal cardTerminal, BeIDCard beIDCard) {
        Platform.runLater(() ->setMessage(MessageType.DEFAULT, "eID Card inserted"));
        Settings.getInstance().setEIDCardPresent(true);
    }
    @Override
    public void eIDCardRemoved(CardTerminal cardTerminal, BeIDCard beIDCard) {
        Platform.runLater(() -> setMessage(MessageType.DEFAULT, "eID Card removed"));
        Settings.getInstance().setEIDCardPresent(false);
    }
    // ----- Terminal Events
    @Override
    public void terminalEventsInitialized() {}
    @Override
    public void terminalAttached(CardTerminal cardTerminal) {
        System.out.println(cardTerminal.getName());
        Platform.runLater(() ->setMessage(MessageType.DEFAULT, "A new terminal has been attached"));
    }
    @Override
    public void terminalDetached(CardTerminal cardTerminal) {
        Platform.runLater(() ->setMessage(MessageType.DEFAULT, "A terminal has been detached"));
    }
}
