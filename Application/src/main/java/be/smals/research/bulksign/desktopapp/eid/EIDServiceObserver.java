package be.smals.research.bulksign.desktopapp.eid;

/**
 * Created by kova on 09/08/2016.
 */
public interface EIDServiceObserver {

    /**
     * Called when the eID controller needs the user's pincode
     */
    void getPinCode();
    /**
     * Alerts the Observer that an operation was called requiring a card reader, but no reader was detected.
     */
    void cardReaderNeeded();
    void cardNeeded();
}
