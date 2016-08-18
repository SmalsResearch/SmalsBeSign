package be.smals.research.bulksign.desktopapp.ui;

import be.smals.research.bulksign.desktopapp.utilities.Utilities;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.File;

/**
 * Custom listItem made for file listViews
 */
public class FileListItem extends HBox {
    private Label nameLabel;
    private JFXCheckBox selectCheckBox;
    private JFXButton viewButton;
    private File file;
    private String fileExtension;

    /**
     * Constructor
     * Initializes components with the file information received
     *
     * @param file file to display
     */
    public FileListItem (File file) {
        super();
        this.file           = file;
        this.nameLabel = new Label(file.getName());
        this.selectCheckBox = new JFXCheckBox();
        this.selectCheckBox.getStyleClass().add("checkbox");
        this.selectCheckBox.toFront();

        this.fileExtension = Utilities.getInstance().getFileExtension(this.file.getName());
        this.viewButton = (this.fileExtension.equalsIgnoreCase("pdf")) ? new JFXButton("Preview") : new JFXButton("Open");
        this.viewButton.getStyleClass().add("button-s");
        this.nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.nameLabel, Priority.ALWAYS);

        this.getChildren().addAll(this.selectCheckBox, this.nameLabel, this.viewButton);
        this.setAlignment(Pos.CENTER);
    }

    /**
     * Returns the status of the checkbox
     *
     * @return true if the checkbox is checked
     */
    public boolean isFileSelected() {
        return this.selectCheckBox.isSelected();
    }

    /**
     * Defines the status of the checkbox
     *
     * @param selected the new status, checked (true) or not
     */
    public void setFileSelected (boolean selected) {
        this.selectCheckBox.setSelected(selected);
    }

    /**
     * Defines the action of the button used to open the file
     * @param event
     */
    public void setViewButtonAction (EventHandler event) {
        this.viewButton.setOnAction(event);
    }

    /**
     * Returns the file
     *
     * @return
     */
    public File getFile() {
        return this.file;
    }
    public String getFileExtension () {
        return this.fileExtension;
    }
}
