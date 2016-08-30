package be.smals.research.bulksign.desktopapp.services;

import be.fedict.commons.eid.client.*;
import be.fedict.commons.eid.client.impl.BeIDDigest;
import be.fedict.commons.eid.client.impl.CCID;
import be.fedict.commons.eid.client.spi.UserCancelledException;
import be.smals.research.bulksign.desktopapp.exception.BulkSignException;

import javax.smartcardio.CardException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Service to access eID card and reader operations
 */
public class EIDService {
    private static EIDService instance = new EIDService();
    private BeIDCards beID;

    private EIDService () {}
    public static EIDService getInstance(){
        return instance;
    }
    public void setBeID (BeIDCards beID) {
        this.beID = beID;
    }
    // ----- Status ----------------------------------------------------------------------------------------------------
    /**
     * Retrieves the certificate chain from the eID card
     *
     * @return the certificate chain as a list of certificates
     * @throws CertificateException
     * @throws IOException
     * @throws CardException
     */
    public List<X509Certificate> getBeIDCertificateChain () throws CancelledException, CardException, CertificateException, InterruptedException, IOException {
        return this.beID.getOneBeIDCard().getSigningCertificateChain();
    }

    // ----- Sign ------------------------------------------------------------------------------------------------------


    /**
     * Performs the signing operation
     *
     * @param masterDigest the digest to sign
     * @return the signature as byte array
     * @throws IOException
     * @throws CardException
     */
    public byte[] signWithBeID (byte[] masterDigest) throws CancelledException, CardException, UserCancelledException, InterruptedException, IOException, BulkSignException {
        BeIDCard card = this.beID.getOneBeIDCard();
        try {
            boolean isSecurePinPad = false;
            if (card.cardTerminalHasCCIDFeature(CCID.FEATURE.EID_PIN_PAD_READER))
                isSecurePinPad = true;
            return this.beID.getOneBeIDCard().sign(masterDigest, BeIDDigest.SHA_1, FileType.NonRepudiationCertificate, isSecurePinPad);
        } catch (ResponseAPDUException e) {
            if (e.getApdu().getSW() == 6400)
                throw new BulkSignException("PIN verification error\nMake sure to type your PIN code.");

            throw new BulkSignException("Wrong PIN code.");
        }
    }

    // ----- PIN -------------------------------------------------------------------------------------------------------
}
