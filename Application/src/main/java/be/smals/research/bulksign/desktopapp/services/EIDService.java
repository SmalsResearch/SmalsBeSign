package be.smals.research.bulksign.desktopapp.services;

import be.fedict.commons.eid.client.BeIDCards;
import be.fedict.commons.eid.client.CancelledException;
import be.fedict.commons.eid.client.FileType;
import be.fedict.commons.eid.client.impl.BeIDDigest;
import be.fedict.commons.eid.client.spi.UserCancelledException;
import be.smals.research.bulksign.desktopapp.eid.EID;
import be.smals.research.bulksign.desktopapp.eid.EIDObserver;

import javax.smartcardio.CardException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to access eID card and reader operations
 */
public class EIDService {
    private static EIDService instance = new EIDService();
    private EID eID;
    private BeIDCards beID;
    private List<EIDServiceObserver> observers;

    private enum Services {
        GET_PINCODE,
        WAITINGFOR_CARDREADER, WAITINGFOR_CARD,
        GET_CERTIFICATES,
        MESSAGE {
            String message;
            @Override
            public String getMessage () {
                return this.message;
            }
            @Override public void setMessage (String message) {
                this.message = message;
            }
        };
        public String getMessage () {
            return "Hello";
        }
        public void setMessage (String message) {}
    }

    private EIDService () {
        this.eID = new EID();
        this.observers = new ArrayList<>();
    }
    public static EIDService getInstance(){
        return instance;
    }
    public void setBeID (BeIDCards beID) {
        this.beID = beID;
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
        this.notifyObservers(Services.WAITINGFOR_CARD);
        this.eID.waitForEidPresent();
    }
    /**
     * Loop until an eID card reader is connected to the computer
     */
    public void waitForReader () {
        this.notifyObservers(Services.WAITINGFOR_CARDREADER);
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
    public List<X509Certificate> getBeIDCertificateChain () throws CancelledException, CardException, CertificateException, InterruptedException, IOException {
        return this.beID.getOneBeIDCard().getSigningCertificateChain();
    }
    public byte[] signWithBeID (byte[] masterDigest) throws CancelledException, CardException, UserCancelledException, InterruptedException, IOException {
        return this.beID.getOneBeIDCard().sign(masterDigest, BeIDDigest.SHA_1, FileType.NonRepudiationCertificate, false);
    }
    /**
     * Performs the signing operation
     *
     * @param masterDigest the digest to sign
     * @param algorithm the algorithm to use on digest before signing
     * @param keyID
     * @return the signature as byte array
     * @throws IOException
     * @throws CardException
     */
    public byte[] sign(byte[] masterDigest, String algorithm, byte keyID)
            throws IOException, CardException {
        return this.eID.sign(masterDigest, algorithm, keyID);
    }

    // ----- PIN -------------------------------------------------------------------------------------------------------

    // ----- OTHERS ----------------------------------------------------------------------------------------------------
    /**
     * Concludes operations on the eID card
     *
     * @throws CardException
     */
    public void close () throws CardException {
        this.eID.close();
    }

    /**
     * Used to inform observers that an eID service has been used
     *
     * @param service the service
     */
    private void notifyObservers (Services service) {
        switch (service) {
            case GET_PINCODE:
                observers.forEach(EIDServiceObserver::getPinCode);
                break;
            // -- Wait for...
            case WAITINGFOR_CARDREADER:
                observers.forEach(EIDServiceObserver::cardReaderNeeded);
                break;
            case WAITINGFOR_CARD:
                observers.forEach(EIDServiceObserver::cardNeeded);
                break;
        }

    }

    /**
     * Used register observers that will be notified on service usage
     *
     * @param observer
     */
    public void registerAsEIDServiceObserver(EIDServiceObserver observer){
        if (!this.observers.contains(observer))
            this.observers.add(observer);
    }

    public void registerAsEIDObserver (EIDObserver observer) {
        this.eID.registerAsObserver(observer);
    }
}
