package be.smals.research.bulksign.desktopapp.exception;

/**
 * App exception class
 */
public class BulkSignException extends Exception{

    public BulkSignException () {
        super();
    }
    /**
     * Constructor
     *
     * @param message exception message
     */
    public BulkSignException(String message) {
        super(message);
    }
}
