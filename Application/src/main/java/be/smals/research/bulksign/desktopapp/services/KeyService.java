package be.smals.research.bulksign.desktopapp.services;

/**
 * Created by cea on 02/08/2016.
 */
public class KeyService {
    private static KeyService ourInstance = new KeyService();

    public static KeyService getInstance() {
        return ourInstance;
    }

    private KeyService() {
    }
}
