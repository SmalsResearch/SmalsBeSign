package be.smals.research.bulksign.desktopapp.utilities;

import java.net.Proxy;

public class Settings {
    private static Settings instance = new Settings();


    public static final double WIDTH_MIN         = 600;
    public static final double HEIGHT_MIN        = 460;
    public static final double APP_VERSION       = 0.1;
    public static final String APP_VERSION_NAME  = "First release";
    public static final String APP_NAME          = "SmalsBeSign";

    public boolean eIDCardIsPresent;
    public boolean useProxy;
    public Proxy proxy;

    private Settings () {}

    public static Settings getInstance () {
        return instance;
    }

}
