package be.smals.research.bulksign.desktopapp.utilities;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.selector.misc.BufferedProxySelector;
import com.github.markusbernhardt.proxy.util.PlatformUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by kova on 30/08/2016.
 */
public class ProxyFinder {

    private Proxy proxy = null;

    public static void main(String[] args) {
        //todo example:

        ProxyFinder finder = new ProxyFinder();
        Proxy p = finder.getProxy();
        Proxy.Type t = p.type();
        System.out.println(t);

        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection connection =(HttpURLConnection)(url.openConnection(p));
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-type", "text/xml");
            connection.setRequestProperty("Accept", "text/xml, application/xml");
            connection.setRequestMethod("GET");
            connection.connect();
            System.out.println(connection.usingProxy());
            String response = IOUtils.toString(connection.getInputStream(), "UTF-8");
            System.out.println(connection.usingProxy());
            System.out.println(response);
            //todo if the below is 200, all ok, if it is 407, proxy auth required
            System.out.println(connection.getResponseCode());
            System.out.println(connection.usingProxy());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProxyFinder() {
        try {
            System.setProperty("java.net.useSystemProxies","true");
            ProxySearch ps = ProxySearch.getDefaultProxySearch();
            if (PlatformUtil.getCurrentPlattform() == PlatformUtil.Platform.WIN) {
                System.out.println("OS: windows");
                ps.addStrategy(ProxySearch.Strategy.IE);
                ps.addStrategy(ProxySearch.Strategy.FIREFOX);
                ps.addStrategy(ProxySearch.Strategy.JAVA);
            } else if (PlatformUtil.getCurrentPlattform() == PlatformUtil.Platform.LINUX) {
                System.out.println("OS: linux");
                ps.addStrategy(ProxySearch.Strategy.GNOME);
                ps.addStrategy(ProxySearch.Strategy.KDE);
                ps.addStrategy(ProxySearch.Strategy.FIREFOX);
            } else {
                System.out.println("OS: other");
                ps.addStrategy(ProxySearch.Strategy.OS_DEFAULT);
            }
            System.out.println("Detecting Proxies");
            //ps.addStrategy(ProxySearch.Strategy.OS_DEFAULT);
            ps.setPacCacheSettings(32, 1000*60*5, BufferedProxySelector.CacheScope.CACHE_SCOPE_URL);
            ProxySelector selector = ps.getProxySelector();
            List l = selector.select(new URI("http://www.google.com/"));

            //... Now just do what the original did ...
            for (Iterator iter = l.iterator(); iter.hasNext(); ) {
                Proxy proxy = (Proxy) iter.next();

                System.out.println("proxy hostname : " + proxy.type());
                InetSocketAddress addr = (InetSocketAddress) proxy.address();

                if(addr == null) {
                    System.out.println("No Proxy");
                } else {
                    //todo show this to the user when asking proxy credentials somehow
                    System.out.println("proxy hostname : " + addr.getHostName());
                    System.out.println("proxy port : " + addr.getPort());
                    this.proxy = proxy;
                    //todo only do the following if, after a test, you get responsecode 407 (proxy auth required)
                    changeAuthenticator();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeAuthenticator() {
        Authenticator.setDefault(new PassWordAuthenticator());
    }

    private static final class PassWordAuthenticator extends Authenticator {
        private String proxyUserName = "";
        private String proxyPassword = "";

        //todo get this using an observer of some kind and ask the user's credentials before making connections
        //todo check if proxy is not null; if it isnt, ask the password and save it (allow the user to cancel
        //todo giving a password, the proxy may not need it sometimes. If the user cancels, DO NOT change the authenticator, but use the rpoxy without credentials
        public PassWordAuthenticator() {
            Scanner in = new Scanner(System.in);
            System.out.print("Username: ");
            proxyUserName = in.nextLine();
            System.out.print("Password: ");
            proxyPassword = in.nextLine();
            in.close();
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            if (getRequestorType() == RequestorType.PROXY) {
                return new PasswordAuthentication(proxyUserName,proxyPassword.toCharArray());
            } else {
                return super.getPasswordAuthentication();
            }
        }
    }

    public Proxy getProxy() {
        return proxy;
    }
}
