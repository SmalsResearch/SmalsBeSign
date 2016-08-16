package be.smals.research.bulksign.desktopapp.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Date;

public class ResultListItem extends HBox {
    private Label label;

    public ResultListItem(String fileName, boolean passed, String author, Date date) {
        super();

        this.label = new Label(fileName);
        if (passed) {
            this.label.setText(this.label.getText()+" - PASSED" );
            this.label.getStyleClass().add("color-success");
        } else {
            this.label.setText(this.label.getText()+" - FAILED" );
            this.label.getStyleClass().add("color-danger");
        }
        this.label.setText(this.label.getText()+ " - Signed by "+author+" at "+date);

        this.getChildren().addAll(this.label);
        this.setAlignment(Pos.CENTER);
    }
}
