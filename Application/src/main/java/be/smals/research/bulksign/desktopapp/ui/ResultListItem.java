package be.smals.research.bulksign.desktopapp.ui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import java.util.Date;

public class ResultListItem extends Label {

    public ResultListItem(String fileName, boolean passed, String author, Date date) {
        super();
        this.setAlignment(Pos.CENTER_LEFT);

        String value = "";
        if (passed) {
            value += fileName + " - OK";
            this.getStyleClass().add("color-success");
            GlyphsDude.setIcon(this, FontAwesomeIcon.CHECK_CIRCLE, "3em");
        } else {
            value += fileName + " - FAILED";
            this.getStyleClass().add("color-danger");
            GlyphsDude.setIcon(this, FontAwesomeIcon.TIMES_CIRCLE, "3em");
        }
        value +=  "\n Signed by "+author+"\n Signed at "+date;

        this.setWrapText(true);
        this.setText(value);
    }
}
