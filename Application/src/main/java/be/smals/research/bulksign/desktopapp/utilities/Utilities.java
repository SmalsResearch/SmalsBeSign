package be.smals.research.bulksign.desktopapp.utilities;

import be.smals.research.bulksign.desktopapp.exception.BulkSignException;
import be.smals.research.bulksign.desktopapp.ui.FileListItem;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
        return testInet("google.com") && testInet("certs.eid.belgium.be");

        } catch (java.io.IOException e) {
            System.out.println("Unknown host - No internet connection!");
        }

        return false;
    }
    private boolean testInet (String website) throws MalformedURLException {
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress(website,80);
        try {
            sock.connect(addr,3000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {sock.close();}
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Returns individual files from the Signed file (.signed.zip)
     *
     * @param signedFile
     * @return a map matching files with they identity
     */
    public Map<String, VerifySigningOutput.FileWithAltName> getFilesFromSignedFile(File signedFile) throws IOException, BulkSignException {
        byte[] buffer = new byte[1024];
        Map<String, VerifySigningOutput.FileWithAltName> files = new HashMap<>();
        ZipInputStream zipInputStream   = new ZipInputStream(new FileInputStream(signedFile));
        ZipEntry zipEntry               = zipInputStream.getNextEntry();
        int fileCount = 0;
        while (zipEntry != null && fileCount<3) {
            String fileName         = zipEntry.getName();
            File newFile            = File.createTempFile(signedFile.getParent()+File.separator+fileName, "."+getFileExtension(fileName));
            FileOutputStream fos    = new FileOutputStream(newFile);
            int len;
            while ((len = zipInputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            String fileExt = Utilities.getInstance().getFileExtension(fileName);
            VerifySigningOutput.FileWithAltName fileWithAltName = new VerifySigningOutput.FileWithAltName(fileName, newFile);
            if (fileExt.equalsIgnoreCase("sig")) {
                files.put("SIGNATURE", fileWithAltName);
            } else if (fileName.equals("README.txt")) {
                files.put("README", fileWithAltName);
            } else {
                files.put("FILE", fileWithAltName);
            }
            fileCount++;
            zipEntry = zipInputStream.getNextEntry();
        }
        if (fileCount != 3) {
            String message = "- Unable to retrieve necessary files";
            if (!files.containsKey("FILE") || files.get("FILE") == null)
                message += "\n--- The original file is missing";
            if (!files.containsKey("SIGNATURE") || files.get("SIGNATURE") == null)
                message += "\n--- The signature file is missing";
            if (!files.containsKey("README") || files.get("README") == null)
                message += "\n--- The readme file is missing";

            throw new BulkSignException(message);
        }

        zipInputStream.closeEntry();
        zipInputStream.close();

        return files;
    }
}
