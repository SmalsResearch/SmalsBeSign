package be.smals.research.bulksign.desktopapp.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
     * Return a file extension from file name (without the dot)
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
}
