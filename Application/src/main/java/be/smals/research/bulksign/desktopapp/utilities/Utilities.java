package be.smals.research.bulksign.desktopapp.utilities;

import be.smals.research.bulksign.desktopapp.ui.FileListItem;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Random functions
 */
public class Utilities {
    private static Utilities ourInstance = new Utilities();

    /**
     * Constructor
     */
    private Utilities() {}
    public static Utilities getInstance() {
        return ourInstance;
    }

    /**
     * Computes the SHA-1 and returns it as bytes array
     *
     * @param input input to be hashed
     * @return the hash
     * @throws NoSuchAlgorithmException if SHA-1 is not found
     */
    public byte[] getSha1(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        return digest.digest(input);
    }

    /**
     * Returns a file extension from file name (without the dot)
     *
     * @param fileName
     * @return
     */
    public String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        // files without extension
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i >= p) {
            extension = fileName.substring(i+1);
        }

        return extension;
    }

    /**
     * Returns a File list from listView
     * @param listView
     * @return
     */
    public List<File> getFileListFromFileListView (ListView listView) {
        ObservableList<FileListItem> items = listView.getItems();
        List<File> files = items.stream().map(FileListItem::getFile).collect(Collectors.toList());
        return files;
    }

    /**
     * Pings Google and certs.eid.belgium.be
     *
     * @return true if both of them are reachable
     * @throws IOException
     */
    public boolean isInternetReachable () throws IOException {
        try {
            URL url = new URL("http://certs.eid.belgium.be");
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
            urlConnect.setConnectTimeout(1000);

            return InetAddress.getByName("www.google.com").isReachable(1000)
                    && urlConnect.getContent()!=null;

        } catch (java.io.IOException e) {
            System.out.println("Unknown host - No internet connection!");
        }

        return false;
    }
}
