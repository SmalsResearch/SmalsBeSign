package be.smals.research.bulksign.desktopapp.utilities;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.selector.misc.BufferedProxySelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.PlatformUtil;

import java.net.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by kova on 30/08/2016.
 */
public class TestProxyFinder {

    private Proxy proxy = null;

    public static void main(String[] args) {
        //todo example:

//        //use this class to get proxy object:
//        TestProxyFinder finder = new TestProxyFinder();
//        Proxy p = finder.getProxy();
//
//        try {
//            URL url = new URL("http://www.google.com");
//            //use the proxy in a http connection if it is not null:
//            HttpURLConnection conn = (HttpURLConnection) (p==null?url.openConnection():url.openConnection(p));
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setRequestProperty("Content-type", "text/xml");
//            conn.setRequestProperty("Accept", "text/xml, application/xml");
//            conn.setRequestMethod("GET");
//            conn.connect();
//            System.out.println(conn.usingProxy());
//            String response = IOUtils.toString(conn.getInputStream(), "UTF-8");
//            System.out.println(conn.usingProxy());
//            System.out.println(response);
//            //todo if the below is 200, all ok, if it is 407, proxy auth required
//            System.out.println(conn.getResponseCode());
//
//            System.out.println(conn.usingProxy());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Makes a new Proxyfinder and prefetches the proxy settings from the system.
     */
    public TestProxyFinder() {
        try {
            System.setProperty("java.net.useSystemProxies","true");
            ProxySearch ps = ProxySearch.getDefaultProxySearch();
            Logger.setBackend(new Logger.LogBackEnd() {
                @Override
                public void log(Class<?> aClass, Logger.LogLevel logLevel, String s, Object... objects) {
                    System.out.println("**proxy-vole** "+s+ " "+ Arrays.deepToString(objects));
                }

                @Override
                public boolean isLogginEnabled(Logger.LogLevel logLevel) {
                    return true;
                }
            });
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
            if (selector==null) {
                System.out.println("No proxies found.");
                return;
            }
            List l = selector.select(new URI("http://www.google.com/"));

            //... Now just do what the original did ...
            for (Iterator iter = l.iterator(); iter.hasNext(); ) {
                Proxy proxy = (Proxy) iter.next();

                System.out.println("proxy type : " + proxy.type());
                InetSocketAddress addr = (InetSocketAddress) proxy.address();

                if(addr == null) {
                    System.out.println("No Proxy");
                } else {
                    //todo show this to the user when asking proxy credentials somehow
                    System.out.println("proxy hostname : " + addr.getHostName());
                    System.out.println("proxy port : " + addr.getPort());
                    this.proxy = proxy;
                    //todo only do the following if, after a test, you get responsecode 407 (proxy auth required)
                    //changeAuthenticator();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to be called if it appears the proxy needs authentication
     * //todo give it a callback to an observer to ask username and password, informing the user his proxy requires that
     */
    public void changeAuthenticator() {
        Authenticator.setDefault(new ProxyPassWordAuthenticator());
    }

    private static final class ProxyPassWordAuthenticator extends Authenticator {
        private String proxyUserName = "";
        private String proxyPassword = "";

        //todo get this using an observer of some kind and ask the user's credentials before making connections
        //todo check if proxy is not null; if it is'nt, test to see if it requires auth. If it does, ask the password and save it (allow the user to cancel)
        //todo If the user cancels, DO NOT change the authenticator, but use the proxy without credentials
        //todo this may give an error, but then it's his fault; inform him the proxy is not working, redemand password maybe?.
        ProxyPassWordAuthenticator() {
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
