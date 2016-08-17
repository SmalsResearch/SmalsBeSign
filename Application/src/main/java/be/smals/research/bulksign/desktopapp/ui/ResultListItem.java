package be.smals.research.bulksign.desktopapp.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

import java.util.Date;

public class ResultListItem extends Label {

    public ResultListItem(String fileName, boolean passed, String author, Date date) {
        super();
        this.setAlignment(Pos.CENTER_LEFT);

        String value = "";
        if (passed) {
            value += fileName + " - PASSED";
            this.getStyleClass().add("color-success");
        } else {
            value += fileName + " - FAILED";
            this.getStyleClass().add("color-danger");
        }
        value +=  "\n- Signed by "+author+"\n- Signed on "+date;

        this.setWrapText(true);
        this.setText(value);
    }
}
