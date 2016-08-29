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
    public boolean  userCertValid;
    public boolean  userCertChecked;
    public boolean  errorDuringVerification;
    public String   errorMessage;

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
        userCertValid       = false;
        intermCertChecked   = false;
        intermCertInCRL     = false;
        intermCertValid     = false;
        rootCertChecked     = false;
        rootCertInCRL       = false;
        rootCertValid       = false;
        signatureValid      = false;

        errorMessage        = "";
    }
    public VerifySigningOutput (String fileName, String signedBy, Date signedAt) {
        this.fileName       = fileName;
        this.signedBy       = signedBy;
        this.signedAt       = signedAt;

        digestValid         = false;
        certChainValid      = false;
        userCertValid       = false;
        intermCertChecked   = false;
        intermCertInCRL     = false;
        intermCertValid     = false;
        rootCertChecked     = false;
        rootCertInCRL       = false;
        rootCertValid       = false;
        signatureValid      = false;

        errorMessage        = "";
    }

    public VerifyResult getOutputResult () {
        if (errorDuringVerification)
            return VerifyResult.FAILED;
        // Digest
        if (!this.digestValid)
            return VerifyResult.FAILED;
        // Signature
        if (!this.signatureValid)
            return VerifyResult.FAILED;
        // Certificate
        if (!this.certChainValid)
            return VerifyResult.FAILED;
        else if (this.userCertChecked && !this.userCertValid)
            return VerifyResult.FAILED;
        else if (this.intermCertChecked && !this.intermCertValid && !this.intermCertInCRL)
            return VerifyResult.FAILED;
        else if (this.rootCertChecked && !this.rootCertValid && !this.rootCertInCRL)
            return VerifyResult.FAILED;
        else if (!this.userCertChecked || !this.intermCertChecked || !this.rootCertChecked
                || this.intermCertInCRL || this.rootCertInCRL)
            return VerifyResult.WARNING;

        // Digest:ok / Signature:ok / CertChain:ok // CertInternet:ok
        return VerifyResult.OK;
    }
    public VerifyResult getCertificateResult () {
        // Digest -- none
        // Signature -- none
        // Certificate
        if (!this.certChainValid)
            return VerifyResult.FAILED;
        else if (this.userCertChecked && !this.userCertValid)
            return VerifyResult.FAILED;
        else if (this.intermCertChecked && !this.intermCertValid && !this.intermCertInCRL)
            return VerifyResult.FAILED;
        else if (this.rootCertChecked && !this.rootCertValid && !this.rootCertInCRL)
            return VerifyResult.FAILED;
        else if (!this.userCertChecked || !this.intermCertChecked || !this.rootCertChecked
                || this.intermCertInCRL || this.rootCertInCRL)
            return VerifyResult.WARNING;

        // Digest:ok / Signature:ok / CertChain:ok // CertInternet:ok
        return VerifyResult.OK;
    }

    @Override public String toString() {
        if (this.errorDuringVerification) {
            return errorMessage;
        }
        String returnValue = "\n- Signed by "+this.signedBy + "\n- Signed on "+this.signedAt;
        if (!this.digestValid) {
            returnValue += "\n- Master digest verification : FAILED"
                            +"\n---- The file digest is not part of the MasterDigest";
            return fileName + " - " + this.getOutputResult() + returnValue;
        }
        // (1) Digest : OK

        returnValue +=  "\n- Master digest verification : OK";
        if (!this.certChainValid) {
            returnValue += "\n- Chain certificate verification : FAILED";
            return returnValue;
        }else if (!this.userCertChecked) {
            returnValue += "\n- Chain certificate verification : WARNING"
                    + "\n--- Could not verify the User certificate";
        } else if (!this.userCertValid) {
            returnValue += "\n- Chain certificate verification : FAILED"
                    +"\n--- User certificate verification failed!";
            return returnValue;
        }
        // (2) Chain certificate (offline) : OK
        // (2.1) User Cert: could not be verified(WARNING) || is verified and succeed(OK)

        if (!this.userCertChecked && !this.intermCertChecked) {
            returnValue += "\n--- Could not verify the Intermediate certificate";
        }else if (!this.intermCertChecked) {
            returnValue += "\n- Chain certificate verification : WARNING"
                    + "\n--- Could not verify the Intermediate certificate";
        } else if (!this.intermCertValid && this.intermCertInCRL) {
            returnValue += "\n- Chain certificate verification : WARNING"
                    + "\n--- Your Intermediate certificate is revoked!";
        } else if (!this.intermCertValid) {
            returnValue += "\n- Chain certificate verification : FAILED";
            return returnValue;
        }
        // (2.2) Intermediate Cert:  could not be verified(WARNING) || is verified and succeed(OK) || is verified and is in CRL(WARNING)

        if (this.rootCertChecked && this.rootCertValid)
            returnValue += "\n- Chain certificate verification : OK";
        else if ((!this.userCertChecked && !this.intermCertChecked && !this.rootCertChecked)
                || (!this.intermCertChecked && !this.rootCertChecked))
            returnValue += "\n--- Could not verify the Root certificate";
        else if (!this.rootCertChecked)
            returnValue += "\n- Chain certificate verification : WARNING"
                    + "\n--- Could not verify the Root on the Internet.";
        else if (this.rootCertInCRL)
            returnValue += "\n- Chain certificate verification : WARNING"
                    + "\n--- Your Root certificate is revoked!";
        else
            returnValue += "\n- Chain certificate verification : FAILED";
        // (2.3) Root cert: could not be verified(WARNING) || is verified and succeed(OK) || is verified and is in CRL(WARNING)

        returnValue += "\n- Signature verification : " + (this.signatureValid ? "OK" : "FAILED");
        // (3) Signature: OK/FAILED

        return fileName + " - " + this.getOutputResult() +returnValue;
    }

    public String outputCertificateResult() {
        String returnValue = "";
        // (1) Digest : no need to print digest result

        if (!this.certChainValid) {
            returnValue += "\nChain certificate verification : FAILED";
            return returnValue;
        }else if (!this.userCertChecked) {
            returnValue += "\nChain certificate verification : WARNING"
                    + "\n- Could not verify the User certificate";
        } else if (!this.userCertValid) {
            returnValue += "\nChain certificate verification : FAILED"
                    +"\n- User certificate verification failed!";
            return returnValue;
        }
        // (2) Chain certificate (offline) : OK
        // (2.1) User Cert: could not be verified(WARNING) || is verified and succeed(OK)

        if (!this.userCertChecked && !this.intermCertChecked) {
            returnValue += "\n- Could not verify the Intermediate certificate";
        }else if (!this.intermCertChecked) {
            returnValue += "\nChain certificate verification : WARNING"
                    + "\n- Could not verify the Intermediate certificate";
        } else if (!this.intermCertValid && this.intermCertInCRL) {
            returnValue += "\nChain certificate verification : WARNING"
                    + "\n- Your Intermediate certificate is revoked!";
        } else if (!this.intermCertValid) {
            returnValue += "\nChain certificate verification : FAILED";
            return returnValue;
        }
        // (2.2) Intermediate Cert:  could not be verified(WARNING) || is verified and succeed(OK) || is verified and is in CRL(WARNING)

        if (this.rootCertChecked && this.rootCertValid)
            returnValue += "\nChain certificate verification : OK";
        else if ((!this.userCertChecked && !this.intermCertChecked && !this.rootCertChecked)
                || (!this.intermCertChecked && !this.rootCertChecked))
            returnValue += "\n- Could not verify the Root certificate";
        else if (!this.rootCertChecked)
            returnValue += "\nChain certificate verification : WARNING"
                    + "\n- Could not verify the Root on the Internet.";
        else if (this.rootCertInCRL)
            returnValue += "\nChain certificate verification : WARNING"
                    + "\n- Your Root certificate is revoked!";
        else
            returnValue += "\nChain certificate verification : FAILED";
        // (2.3) Root cert: could not be verified(WARNING) || is verified and succeed(OK) || is verified and is in CRL(WARNING)

        // (3) Signature: no need to print signature result

        return returnValue;
    }

    public void consoleOutput() {
        System.out.println("\nDigest Valid: "+digestValid
        +"\nCertChainValid: "+certChainValid
        +"\nUserCertValid: "+userCertValid
        +"\nIntermCertChecked: "+intermCertChecked
        +"\nIntermCertInCRL: "+intermCertInCRL
        +"\nIntermValid: "+intermCertValid
        +"\nRootCertChecked: "+rootCertChecked
        +"\nRootCertInCRL: "+rootCertChecked
        +"\nRootCertValid: "+rootCertValid
        +"\nSignature: "+signatureValid);
    }
}
