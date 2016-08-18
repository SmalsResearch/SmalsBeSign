package be.smals.research.bulksign.desktopapp.utilities;

import java.util.Date;

/**
 * Created by cea on 17/08/2016.
 */
public class VerifySigningOutput {
    public String   fileName;
    public String   signedBy;
    public Date     signedAt;
    public boolean  digestValid;
    public boolean  certChainValid;
    public boolean  rootCertChecked;
    public boolean  rootCertValid;
    public boolean signatureValid;

    public VerifySigningOutput () {}
    public VerifySigningOutput (String fileName, String signedBy, Date signedAt) {
        this.fileName       = fileName;
        this.signedBy       = signedBy;
        this.signedAt       = signedAt;

        digestValid         = false;
        certChainValid      = false;
        rootCertChecked     = false;
        rootCertValid       = false;
        signatureValid      = false;
    }

    @Override
    public String toString() {
        String returnValue = this.fileName
                + "\n- Signed by "+this.signedBy
                + "\n- Signed at "+this.signedAt
                + "\n- Digest verification : "+(this.digestValid ? "OK" : "FAILED");
        if (this.certChainValid && this.rootCertChecked && this.rootCertValid)
            returnValue += "\n- Chain certificate verification : OK";
        else if (this.certChainValid && this.rootCertChecked && !this.rootCertValid)
            returnValue += "\n- Chain certificate verification : FAILED";
        else if (this.certChainValid && !this.rootCertChecked)
            returnValue += "\n- Chain certificate verification : WARNING"
                    + "\n--- Could not verify the Root Certificate on the internet.";
         else
            returnValue += "\n- Chain certificate verification : FAILED";
        returnValue += "\n- Signature verification : " + (this.signatureValid ? "OK" : "FAILED");

        return returnValue;
    }
}
