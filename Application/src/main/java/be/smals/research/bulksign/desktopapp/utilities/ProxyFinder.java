package be.smals.research.bulksign.desktopapp.utilities;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.selector.misc.BufferedProxySelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.PlatformUtil;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ProxyFinder {

    private static ProxyFinder instance = new ProxyFinder();
    private static final class ProxyPasswordAuthenticator extends Authenticator {
        private String proxyUserName = "";
        private String proxyPassword = "";

        //todo get this using an observer of some kind and ask the user's credentials before making connections
        //todo check if proxy is not null; if it is'nt, test to see if it requires auth. If it does, ask the password and save it (allow the user to cancel)
        //todo If the user cancels, DO NOT change the authenticator, but use the proxy without credentials
        //todo this may give an error, but then it's his fault; inform him the proxy is not working, redemand password maybe?.
        ProxyPasswordAuthenticator() {
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

    public ProxyFinder () {}
    public static ProxyFinder getInstance () {
        return instance;
    }
    public Proxy find () {
        Proxy proxy = null;
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
                ps.addStrategy(ProxySearch.Strategy.WIN);
                ps.addStrategy(ProxySearch.Strategy.OS_DEFAULT);
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
            ps.setPacCacheSettings(32, 1000*60*5, BufferedProxySelector.CacheScope.CACHE_SCOPE_URL);
            ProxySelector selector = ps.getProxySelector();
            if (selector==null) {
                System.out.println("No proxies found.");
                return null;
            }
            List l = selector.select(new URI("http://www.google.com/"));

            //... Now just do what the original did ...
            for (Object aL : l) {
                Proxy tmpProxy = (Proxy) aL;

                System.out.println("proxy type : " + tmpProxy.type());
                InetSocketAddress addr = (InetSocketAddress) tmpProxy.address();

                if (addr == null)
                    return null;

                proxy = tmpProxy;
                //todo only do the following if, after a test, you get responsecode 407 (proxy auth required)
                if (this.testConnectionTo(proxy, new URL("http://www.google.com/")))
                    return proxy;

                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return proxy;
    }
    public Proxy getProxy (String address, int port) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address, port));

        return proxy;
    }
    public boolean testConnectionTo(Proxy proxy, URL url) {
        try {
            //use the proxy in a http connection if it is not null:
            HttpURLConnection conn = (HttpURLConnection) (proxy==null?url.openConnection():url.openConnection(proxy));
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-type", "text/xml");
            conn.setRequestProperty("Accept", "text/xml, application/xml");
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200)
                return true;
            else if (responseCode == 407) {
//                this.changeAuthenticator();
            } else
                return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void changeAuthenticator() {
        Authenticator.setDefault(new ProxyPasswordAuthenticator());
    }



}
