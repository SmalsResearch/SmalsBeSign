package be.smals.research.bulksign.desktopapp.utilities;

import java.util.Date;

/**
 * Created by cea on 17/08/2016.
 */
public class VerifySigningOutput {
    public String fileName;
    public String signedBy;
    public Date signedAt;

    public VerifySigningOutput () {}
    public VerifySigningOutput (String fileName, String signedBy, Date signedAt) {
        this.fileName       = fileName;
        this.signedBy       = signedBy;
        this.signedAt       = signedAt;
    }
}
