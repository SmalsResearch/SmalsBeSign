package be.smals.research.bulksign.desktopapp.ui;

import be.smals.research.bulksign.desktopapp.utilities.VerifySigningOutput;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class ResultListItem extends Label {

    public ResultListItem(VerifySigningOutput verifySigningOutput) {
        super();
        this.setAlignment(Pos.CENTER_LEFT);
        this.setText(verifySigningOutput.toString());

        switch (verifySigningOutput.getOutputResult()) {
            case OK:
                this.getStyleClass().add("color-success");
                GlyphsDude.setIcon(this, FontAwesomeIcon.CHECK_CIRCLE, "3em");
                break;
            case FAILED:
                this.getStyleClass().add("color-danger");
                GlyphsDude.setIcon(this, FontAwesomeIcon.TIMES_CIRCLE, "3em");
                break;
            case WARNING:
                this.getStyleClass().add("color-warning");
                GlyphsDude.setIcon(this, FontAwesomeIcon.EXCLAMATION_CIRCLE, "3em");
                break;
        }

        this.setWrapText(true);
    }
}
