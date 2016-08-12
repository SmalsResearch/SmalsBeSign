package be.smals.research.bulksign.desktopapp.services;

import be.smals.research.bulksign.desktopapp.EID;
import be.smals.research.bulksign.desktopapp.eid.EIDServiceObserver;
import be.smals.research.bulksign.desktopapp.eid.external.UserCancelledException;

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
    private EID eID;
    private List<EIDServiceObserver> observers;

    private enum Services {
        GET_PINCODE,
        WAITINGFOR_CARDREADER, WAITINGFOR_CARD
    }

    private EIDService () {
        this.eID = new EID();
    }
    public static EIDService getInstance(){
        return instance;
    }

    // ----- Status ----------------------------------------------------------------------------------------------------
    /**
     * Returns true if the eID card is present
     * @return true or false
     * @throws CardException
     */
    public boolean isEIDPresent() throws CardException {
        return this.eID.isEidPresent();
    }
    /**
     * Returns true if the eID card is still inside the eID card reader
     *
     * @return true or false
     * @throws CardException
     */
    public boolean isEIDStillPresent () throws CardException {
        return this.eID.isCardStillPresent();
    }
    /**
     * Returns true if the reader is connected to the computer
     *
     * @return true or false
     * @throws CardException
     */
    public boolean isEIDReaderPresent () throws CardException {
        return this.eID.hasCardReader();
    }

    // ----- Wait for --------------------------------------------------------------------------------------------------
    /**
     * Loop until an eID card is detected by the eId card reader
     *
     * @throws CardException
     * @throws InterruptedException
     */
    public void waitForCard() throws CardException, InterruptedException {
        this.eID.waitForEidPresent();
    }
    /**
     * Loop until an eID card reader is connected to the computer
     */
    public void waitForReader () {
        this.eID.waitForCardReader();
    }
    /**
     * Retrieves the certificate chain from the eID card
     *
     * @return the certificate chain as a list of certificates
     * @throws CertificateException
     * @throws IOException
     * @throws CardException
     */
    public List<X509Certificate> getCertificateChain () throws CertificateException, IOException, CardException {
        return this.eID.getSignCertificateChain();
    }

    // ----- Sign ------------------------------------------------------------------------------------------------------
    /**
     * Prepares the eID card before a signing operation
     *
     * @param algotithm the algorithm to be used on the
     * @throws CardException
     */
    public void prepareSigning (String algotithm) throws CardException {
        this.eID.prepareSigning(algotithm,  EID.NON_REP_KEY_ID);
    }

    /**
     * Performs the signing operation
     *
     * @param masterDigest the digest to sign
     * @param algorithm the algorithm to use on digest before signing
     * @return the signature as byte array
     * @throws IOException
     * @throws CardException
     */
    public byte[] sign(byte[] masterDigest, String algorithm) throws IOException, CardException {
        return this.eID.signAlt(masterDigest, algorithm);
    }

    // ----- PIN -------------------------------------------------------------------------------------------------------
    /**
     * Checks either the pin passed to him is valid for the connected eID card or not
     *
     * @param pin the pin code
     * @return true or false
     * @throws UserCancelledException
     * @throws CardException
     */
    public boolean isPinValid (char[] pin) throws UserCancelledException, CardException {
        return this.eID.isPinValid(pin);
    }

    // ----- OTHERS ----------------------------------------------------------------------------------------------------
    /**
     * Concludes operations on the eID card
     *
     * @throws CardException
     */
    public void close () throws CardException {
        this.eID.close();
    }

    private void notifyObservers (Services service) {
        switch (service) {
            case GET_PINCODE:
                observers.forEach(EIDServiceObserver::getPinCode);
                break;
            case WAITINGFOR_CARDREADER:
                observers.forEach(EIDServiceObserver::cardReaderNeeded);
                break;
            case WAITINGFOR_CARD:
                observers.forEach(EIDServiceObserver::cardNeeded);
                break;
        }

    }
}
