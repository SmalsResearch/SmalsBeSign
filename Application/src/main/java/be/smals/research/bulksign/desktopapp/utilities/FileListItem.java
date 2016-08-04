package be.smals.research.bulksign.desktopapp.utilities;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.File;

/**
 * Created by cea on 04/08/2016.
 */
public class FileListItem extends HBox {
    private Label namelabel;
    private CheckBox selectCheckBox;
    private Button viewButton;
    private File file;

    public FileListItem (File file) {
        super();

        this.file           = file;
        this.namelabel      = new Label(file.getName());
        this.selectCheckBox = new CheckBox();
        this.viewButton     = new Button("Preview");
        this.namelabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.namelabel, Priority.ALWAYS);

        this.getChildren().addAll(this.selectCheckBox, this.namelabel, this.viewButton);
    }

    public boolean isSelected () {
        return this.selectCheckBox.isSelected();
    }

    public void setViewButtonAction (EventHandler event) {
        this.viewButton.setOnAction(event);
    }
    public File getFile() {
        return this.file;
    }
}
