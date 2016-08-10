package be.smals.research.bulksign.desktopapp.abstracts;

import be.smals.research.bulksign.desktopapp.EID;

import javax.smartcardio.CardException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created by cea on 09/08/2016.
 */
public abstract class Service {
    private EID eID;

    public Service () {
        eID = new EID();
    }

    // ----- Status ----------------------------------------------------------------------------------------------------
    public boolean isEIDPresent() throws CardException {
        return this.eID.isEidPresent();
    }
    public boolean isEIDStillPresent () throws CardException {
        return this.eID.isCardStillPresent();
    }
    public boolean isEIDReaderPresent () throws CardException {
        return this.eID.hasCardReader();
    }

    // ----- Wait for --------------------------------------------------------------------------------------------------
    public void waitForCard() throws CardException, InterruptedException {
        this.eID.waitForEidPresent();
    }
    public void waitForReader () {
        this.eID.waitForCardReader();
    }
    public List<X509Certificate> getCertificateChain () throws CertificateException, IOException, CardException {
        return this.eID.getSignCertificateChain();
    }

    // ----- Sign ------------------------------------------------------------------------------------------------------
    public void sign () {}
}
