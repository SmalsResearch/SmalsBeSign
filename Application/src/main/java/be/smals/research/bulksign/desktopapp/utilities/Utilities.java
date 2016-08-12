package be.smals.research.bulksign.desktopapp.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Random functions
 */
public class Utilities {
    private static Utilities ourInstance = new Utilities();

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
}
