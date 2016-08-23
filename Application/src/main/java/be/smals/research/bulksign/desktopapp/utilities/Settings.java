package be.smals.research.bulksign.desktopapp.utilities;

public class Settings {
    private static Settings instance = new Settings();

    private Signer signer;
    private boolean eIDCardIsPresent;

    public enum Signer {
        EID, MOCK
    }

    private Settings () {
        this.signer = Signer.EID;
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
    public void setEIDCardPresent (boolean connected) {
        this.eIDCardIsPresent = connected;
    }
    public boolean isEIDCardPresent () {
        return this.eIDCardIsPresent;
    }
}
