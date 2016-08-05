package be.smals.research.bulksign.desktopapp.utilities;

/**
 * Created by cea on 05/08/2016.
 */
public class Settings {
    private static Settings instance = new Settings();

    private Signer signer;

    public enum Signer {
        EID, MOCK
    }

    private Settings () {
        this.signer = Signer.MOCK;
    }


    public static Settings getInstance () {
        return instance;
    }
    public void setSigner (Signer signer) {
        this.signer = signer;
    }
    public Signer getSigner () {
        return this.signer;
    }
}
