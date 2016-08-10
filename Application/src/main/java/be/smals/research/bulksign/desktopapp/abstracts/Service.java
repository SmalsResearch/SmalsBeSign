package be.smals.research.bulksign.desktopapp.abstracts;

import be.smals.research.bulksign.desktopapp.EID;

import javax.smartcardio.CardException;

/**
 * Created by cea on 09/08/2016.
 */
public abstract class Service {
    private EID eID;

    public Service () {
        eID = new EID();
    }
    public boolean isEIDPresent() throws CardException {
        return this.eID.isEidPresent();
    }
    public boolean isEIDStillPresent () throws CardException {
        return this.eID.isCardStillPresent();
    }
    public boolean isEIDReaderPresent () throws CardException {
        return this.eID.hasCardReader();
    }

    // ----- Wait for
    public void waitForCard() throws CardException, InterruptedException {
        this.eID.waitForEidPresent();
    }
    public void waitForReader () {
        this.eID.waitForCardReader();
    }
}
