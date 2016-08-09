package be.smals.research.bulksign.desktopapp.utilities;

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Data saved to file after a signing
 *
 * Contains :
 * a master digest
 * the signature
 * the chain certificate - the first item is the root, the second is the intermediate and the last one the user certificate
 *
 */
public class SigningOutput {
    public String masterDigest;
    public byte[] signature;
    public List<X509Certificate> certificateChain;

    /**
     * Constructor
     */
    public SigningOutput () {}

    /**
     * Constructor
     *
     * @param masterDigest the digest of all files
     * @param signature
     * @param certificate the certificate chain
     */
    public SigningOutput (String masterDigest, byte[] signature, List<X509Certificate> certificate) {
        this.masterDigest       = masterDigest;
        this.signature          = signature;
        this.certificateChain   = certificate;
    }
}
