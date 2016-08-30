package be.smals.research.bulksign.desktopapp.utilities;

public class Settings {
    private static Settings instance = new Settings();

    private boolean eIDCardIsPresent;

    public static final double WIDTH_MIN         = 600;
    public static final double HEIGHT_MIN        = 460;
    public static final double APP_VERSION       = 0.1;
    public static final String APP_VERSION_NAME  = "First release";
    public static final String APP_NAME          = "SmalsBeSign";

    private Settings () {}

    public static Settings getInstance () {
        return instance;
    }
    public void setEIDCardPresent (boolean connected) {
        this.eIDCardIsPresent = connected;
    }
    public boolean isEIDCardPresent () {
        return this.eIDCardIsPresent;
    }
}
