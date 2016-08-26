package be.smals.research.bulksign.desktopapp.ui;

import be.smals.research.bulksign.desktopapp.utilities.Utilities;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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

    private boolean fileInViewer;
    private boolean fileViewed;

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
        this.nameLabel.paddingProperty().setValue(new Insets(0,0, 0, 8));
        this.selectCheckBox = new JFXCheckBox();
        this.selectCheckBox.getStyleClass().add("checkbox");

        this.fileExtension = Utilities.getInstance().getFileExtension(this.file.getName());
        this.viewButton = (this.fileExtension.equalsIgnoreCase("pdf")) ? new JFXButton("Preview") : new JFXButton("Open");
        this.viewButton.getStyleClass().add("button-s-list");
        this.nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.nameLabel, Priority.ALWAYS);

        this.getChildren().addAll(this.selectCheckBox, this.viewButton, this.nameLabel);
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
        try {
            this.selectCheckBox.setSelected(selected);
        } catch (Exception e) {}
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
    public void setFileInViewer (boolean fileInViewer) {
        this.fileInViewer = fileInViewer;
        if (this.fileInViewer) {
            this.getStyleClass().add("item-highlight");
        }
    }
    public void setFileViewed (boolean fileViewed) {
        this.fileViewed = fileViewed;
        if (this.fileViewed)
            this.getStyleClass().add("item-disabled");
    }
}
