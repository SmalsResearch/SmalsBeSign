package be.smals.research.bulksign.desktopapp.abstracts;

import be.smals.research.bulksign.desktopapp.controllers.MainController;
import com.jfoenix.controls.JFXDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Controllers superclass
 */
public abstract class Controller {
    protected Stage stage;
    protected MainController mainController;

    public void initController(MainController mainController, Stage stage) {
        this.mainController     = mainController;
        this.stage              = stage;
    }
    public void showInfoDialog (JFXDialog dialog, StackPane masterPane, String title, String message) {
        dialog.show(masterPane);
        Label titleLabel     = (Label) this.stage.getScene().lookup("#infoDialogTitle");
        Label bodyLabel      = (Label) this.stage.getScene().lookup("#infoDialogBody");
        titleLabel.setText(title);
        bodyLabel.setText(message);
    }
    public void showSuccessDialog (JFXDialog dialog, StackPane masterPane, String title, String message) {
        dialog.show(masterPane);
        Label titleLabel     = (Label) this.stage.getScene().lookup("#successDialogTitle");
        Label bodyLabel      = (Label) this.stage.getScene().lookup("#successDialogBody");
        titleLabel.setText(title);
        bodyLabel.setText(message);
    }
    public void showErrorDialog (JFXDialog dialog, StackPane masterPane, String title, String message) {
        dialog.show(masterPane);
        Label titleLabel     = (Label) this.stage.getScene().lookup("#errorDialogTitle");
        Label bodyLabel      = (Label) this.stage.getScene().lookup("#errorDialogBody");
        titleLabel.setText(title);
        bodyLabel.setText(message);
    }
}
