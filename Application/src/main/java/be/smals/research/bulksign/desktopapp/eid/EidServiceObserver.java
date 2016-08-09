package be.smals.research.bulksign.desktopapp.eid;

/**
 * Created by kova on 09/08/2016.
 */
public interface EidServiceObserver {

    /**
     * Called when the eID controller needs the user's incode
     * @return The pincode, make sure it is purely numerical
     */
    char[] getPinCode();

    /**
     * Alerts the Observer that an operation was called requiring a card reader, but no reader was detected.
     * Typically, the end-user will need to alerted, and the previously called operation on the controller will need to be repeated once the user has attached the card reader.
     * It can be checked if the user has attached a card reader by calling EidService's isCardReaderConnected method.
     */
    void cardReaderNeeded();

    void cardNeeded();

}
