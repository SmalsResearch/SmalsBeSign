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
        ProxyFinder finder = new ProxyFinder();
        Proxy p = finder.getProxy();

        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection connection =(HttpURLConnection)(url.openConnection(p));
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-type", "text/xml");
            connection.setRequestProperty("Accept", "text/xml, application/xml");
            connection.setRequestMethod("GET");
            connection.connect();
            String response = IOUtils.toString(connection.getInputStream(), "");
            System.out.println(response);
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
                    System.out.println("proxy hostname : " + addr.getHostName());
                    System.out.println("proxy port : " + addr.getPort());
                    this.proxy = proxy;
                    changeAuthenticator();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeAuthenticator() {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                if (getRequestorType() == RequestorType.PROXY) {
                    //todo get this using an observer of some kind and ask the user's credentials if needed
                    Scanner in = new Scanner(System.in);
                    System.out.println("Username: ");
                    String name = in.nextLine();
                    System.out.println("Password: ");
                    String pass =in.nextLine();
                    in.close();
                    return new PasswordAuthentication(name,pass.toCharArray());
                } else {
                    return super.getPasswordAuthentication();
                }
            }
        });
    }


    public Proxy getProxy() {
        return proxy;
    }
}
