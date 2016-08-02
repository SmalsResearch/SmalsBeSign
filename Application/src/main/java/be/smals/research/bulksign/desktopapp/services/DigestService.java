package be.smals.research.bulksign.desktopapp.services;

import org.bouncycastle.util.encoders.Hex;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Service used to computeMasterDigest the digest of files to sign
 *
 */
public class DigestService {

    private static DigestService instance = new DigestService();

    private DigestService() {}
    public static DigestService getInstance() {
        if (instance == null)
            instance = new DigestService();
        return instance;
    }

    /**
     * Computes the master digest from files
     *
     * @param fileInputStreams array of files
     * @return the concatenation of the digest of all files ( the master digest)
     * @throws IOException when closing the streams
     * @throws NoSuchAlgorithmException when looking for the hash algorithm
     */
    public String computeMasterDigest(FileInputStream[] fileInputStreams) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {

        /*Create an array that will store the individual digest of each file
         * meaning that
		 *     IndividualDigest[i]=SHA256(File[i])
		 */
        String[] individualDigest = new String[fileInputStreams.length];

        for (int i = 0; i < fileInputStreams.length; i++) {
            FileInputStream fileInputStream = fileInputStreams[i];
            individualDigest[i] = computeIndividualDigest(fileInputStream);
            outputIndividualDigest(individualDigest[i], i);
            fileInputStream.close();
        }

        String masterDigest = "";
		/* Compute the Master Digest as a concatenation of the IndividualDigest strings.*/
        for (int j = 0; j < individualDigest.length; j++) {
            masterDigest += individualDigest[j];
        }

        return (masterDigest);
    }

    /**
     * Console output of an individual file digest
     *
     * @param individualDigest
     * @param position
     */
    private void outputIndividualDigest(String individualDigest, int position) {
        System.out.print("Digest number ");
        System.out.print(position);
        System.out.print(" = ");
        System.out.print(individualDigest);
        System.out.print(" of length ");
        System.out.println(individualDigest.length());
    }

    /**
     * Creates the digest of the given file
     *
     * @param individualFile a file
     * @return the file digest as String
     * @throws IOException when closing the streams
     * @throws NoSuchAlgorithmException when looking for the hash algorithm
     */
    public String computeIndividualDigest(FileInputStream individualFile) throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
        /* Compute a hash of File[i]*/
        int read;
        byte[] buffer = new byte[8192];

        MessageDigest digest = MessageDigest.getInstance("SHA-256", "BC");
        while ((read = individualFile.read(buffer)) > 0) {
            digest.update(buffer, 0, read);
        }
        byte[] hash = digest.digest();
        // Replacement of the 2 previous lines in order to accept the 0 at the beginning of an hex number
        return new String(Hex.encode(hash));
    }
}
