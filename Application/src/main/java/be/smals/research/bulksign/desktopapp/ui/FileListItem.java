package be.smals.research.bulksign.desktopapp.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.File;

/**
 * Created by cea on 04/08/2016.
 */
public class FileListItem extends HBox {
    private Label nameLabel;
    private JFXCheckBox selectCheckBox;
    private JFXButton viewButton;
    private File file;
    private String fileExtension;

    public FileListItem (File file) {
        super();
        this.file           = file;
        this.nameLabel = new Label(file.getName());
        this.selectCheckBox = new JFXCheckBox();
        this.selectCheckBox.getStyleClass().add("checkbox");
        this.selectCheckBox.toFront();

        this.fileExtension = this.retrieveFileExtension(this.file.getName());
        this.viewButton = (this.fileExtension.equalsIgnoreCase("pdf")) ? new JFXButton("Preview") : new JFXButton("Open");
        this.viewButton.getStyleClass().add("button-s");
        this.viewButton.toFront();
        this.nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.nameLabel, Priority.ALWAYS);

        this.getChildren().addAll(this.selectCheckBox, this.nameLabel, this.viewButton);
        this.setAlignment(Pos.CENTER);
    }

    private String retrieveFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        // files without extension
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i >= p) {
            extension = fileName.substring(i+1);
        }

        return extension;
    }

    public boolean isFileSelected() {
        return this.selectCheckBox.isSelected();
    }

    public void setViewButtonAction (EventHandler event) {
        this.viewButton.setOnAction(event);
    }
    public File getFile() {
        return this.file;
    }
    public String getFileExtension () {
        return this.fileExtension;
    }
}
