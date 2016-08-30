package be.smals.research.bulksign.desktopapp.utilities;

import java.security.cert.X509Certificate;
import java.util.Date;
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
    public String author;
    public Date createdAt;
    public String masterDigest;
    public byte[] signature;
    public List<X509Certificate> certificateChain;
    public String softwareVersion;

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
        String username = certificate.get(0).getSubjectDN().getName().split("CN=")[1].split(" \\(")[0];
        System.out.println();
        this.author             = username;
        this.createdAt          = new Date();
        this.softwareVersion    = Settings.APP_VERSION+"";
    }

    /**
     * Constructor
     *
     * @param masterDigest disgest of all files
     * @param signature signature
     * @param certificate the certificate chain
     * @param author eID owner first and last name
     * @param createdAt sysDate
     */
    public SigningOutput (String masterDigest, byte[] signature, List<X509Certificate> certificate, String author, Date createdAt, String softwareVersion) {
        this.masterDigest       = masterDigest;
        this.signature          = signature;
        this.certificateChain   = certificate;
        this.author             = author;
        this.createdAt          = createdAt;
        this.softwareVersion    = softwareVersion;
    }
}
