package be.smals.research.bulksign.desktopapp.services;

import be.fedict.commons.eid.client.BeIDCards;
import be.fedict.commons.eid.client.CancelledException;
import be.fedict.commons.eid.client.FileType;
import be.fedict.commons.eid.client.impl.BeIDDigest;
import be.fedict.commons.eid.client.spi.UserCancelledException;

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
    public byte[] signWithBeID (byte[] masterDigest) throws CancelledException, CardException, UserCancelledException, InterruptedException, IOException {
        return this.beID.getOneBeIDCard().sign(masterDigest, BeIDDigest.SHA_1, FileType.NonRepudiationCertificate, false);
    }

    // ----- PIN -------------------------------------------------------------------------------------------------------
}
