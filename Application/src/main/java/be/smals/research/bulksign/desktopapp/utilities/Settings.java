package be.smals.research.bulksign.desktopapp.utilities;

import java.net.InetSocketAddress;
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
    private Proxy proxy;

    private Settings () {}

    public static Settings getInstance () {
        return instance;
    }
    public void setProxy (Proxy proxy) {
        this.proxy = proxy;
        System.out.println(proxy.type().name()+"//"+proxy.address().toString());
        System.setProperty("http.proxyHost", ((InetSocketAddress)proxy.address()).getHostName());
        System.setProperty("http.proxyPort", ((InetSocketAddress)proxy.address()).getPort()+"");
    }
    public Proxy getProxy () {
        return this.proxy;
    }

}
