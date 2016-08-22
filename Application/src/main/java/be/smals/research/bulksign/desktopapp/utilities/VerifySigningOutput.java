package be.smals.research.bulksign.desktopapp.utilities;

import java.io.File;
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
    public boolean  rootCertInCRL;
    public boolean  signatureValid;
    public boolean  intermCertChecked;
    public boolean  intermCertValid;
    public boolean  intermCertInCRL;

    public enum VerifyResult {
        OK {
            @Override public String toString() { return "OK"; }
        },
        FAILED {
            @Override public String toString() { return "FAILED"; }
        } ,
        WARNING {
            @Override public String toString() { return "WARNING"; }
        };

        public abstract String toString();
    }
    public static class FileWithAltName {
        public String name;
        public File file;
        public FileWithAltName (String name, File file) {
            this.name = name;
            this.file = file;
        }
    }
    public VerifySigningOutput () {
        digestValid         = false;
        certChainValid      = false;
        intermCertChecked   = false;
        intermCertInCRL     = false;
        intermCertValid     = false;
        rootCertChecked     = false;
        rootCertInCRL       = false;
        rootCertValid       = false;
        signatureValid      = false;
    }
    public VerifySigningOutput (String fileName, String signedBy, Date signedAt) {
        this.fileName       = fileName;
        this.signedBy       = signedBy;
        this.signedAt       = signedAt;

        digestValid         = false;
        certChainValid      = false;
        intermCertChecked   = false;
        intermCertInCRL     = false;
        intermCertValid     = false;
        rootCertChecked     = false;
        rootCertInCRL       = false;
        rootCertValid       = false;
        signatureValid      = false;
    }

    public VerifyResult getOutputResult () {
        // Digest
        if (!this.digestValid)
            return VerifyResult.FAILED;
        // Signature
        if (!this.signatureValid)
            return VerifyResult.FAILED;
        // Certificate
        if (!this.certChainValid)
            return VerifyResult.FAILED;
        else if (!this.rootCertChecked||!this.intermCertChecked)
            return VerifyResult.WARNING;
        else if (!this.intermCertValid || !this.rootCertValid)
            return VerifyResult.FAILED;
        // Digest:ok / Signature:ok / CertChain:ok // CertInternet:ok
        return VerifyResult.OK;
    }

    @Override
    public String toString() {
        String returnValue = "\n- Signed by "+this.signedBy + "\n- Signed on "+this.signedAt;
        if (!this.digestValid) {
            returnValue += "\n- Digest verification : FAILED"
                            +"\n---- The file digest is not part of the MasterDigest";
            return fileName + " - " + this.getOutputResult() + returnValue;
        }
        returnValue +=  "\n- Digest verification : OK";
        if (!this.certChainValid) {
            returnValue += "\n- Chain certificate verification : FAILED";
            return returnValue;
        } else if (this.certChainValid && !this.intermCertChecked) {
            returnValue += "\n- Chain certificate verification : WARNING"
                    + "\n--- Could not verify the Intermediate Certificate";
        } else if (this.certChainValid && this.intermCertChecked && !this.intermCertValid) {
            returnValue += "\n- Chain certificate verification : FAILED";
            return returnValue;
        }

        if (this.rootCertChecked && this.rootCertValid)
            returnValue += "\n- Chain certificate verification : OK";
        else if (!this.intermCertChecked && !this.rootCertChecked)
            returnValue += "\n--- Could not verify the Root Certificate on the internet.";
        else if (!this.rootCertChecked)
            returnValue += "\n- Chain certificate verification : WARNING"
                    + "\n--- Could not verify the Root Certificate on the internet.";
         else
            returnValue += "\n- Chain certificate verification : FAILED";
        returnValue += "\n- Signature verification : " + (this.signatureValid ? "OK" : "FAILED");

        return fileName + " - " + this.getOutputResult() +returnValue;
    }
}
