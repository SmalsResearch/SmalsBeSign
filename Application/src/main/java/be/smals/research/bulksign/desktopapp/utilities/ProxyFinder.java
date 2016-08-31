package be.smals.research.bulksign.desktopapp.utilities;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.selector.misc.BufferedProxySelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.PlatformUtil;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;

public class ProxyFinder {

    private static ProxyFinder instance = new ProxyFinder();
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

                //todo show this to the user when asking proxy credentials somehow
                System.out.println("proxy hostname : " + addr.getHostName());
                System.out.println("proxy port : " + addr.getPort());
                proxy = tmpProxy;
                //todo only do the following if, after a test, you get responsecode 407 (proxy auth required)
                //changeAuthenticator();
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
    public boolean testConnexionTo (Proxy proxy, URL url) {
        try {
            URLConnection connection = url.openConnection(proxy);
            try {
                if (connection.getInputStream() == null)
                    return false;
            } catch (UnknownHostException e) {
                System.out.println("UNKNOW HOST // "+e.getMessage());
                return false;
            } catch (Exception e) {
                // --- Timeout and others
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
