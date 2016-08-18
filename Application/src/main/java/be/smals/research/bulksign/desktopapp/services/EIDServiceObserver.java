package be.smals.research.bulksign.desktopapp.services;

/**
 * EIDService observer interface
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

    /**
     * Alerts that eID card is needed
     */
    void cardNeeded();
}
