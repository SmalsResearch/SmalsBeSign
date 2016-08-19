package be.smals.research.bulksign.desktopapp.eid;

/**
 * EID observer interface
 */
public interface EIDObserver {

    /**
     * Called when the eID controller needs the user's pincode
     */
    void getPinCode();

}
