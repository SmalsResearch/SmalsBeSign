package be.smals.research.bulksign.desktopapp.utilities;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;
import java.util.Vector;

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
        final X509Principal principal;
        try {
            principal = PrincipalUtil.getSubjectX509Principal(certificate.get(0));
            final Vector<?> values = principal.getValues(X509Name.CN);
            final String cn = (String) values.get(0);
            this.author             = cn.substring(0, cn.indexOf("(")-1);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        this.createdAt          = new Date();
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
    public SigningOutput (String masterDigest, byte[] signature, List<X509Certificate> certificate, String author, Date createdAt) {
        this.masterDigest       = masterDigest;
        this.signature          = signature;
        this.certificateChain   = certificate;
        this.author             = author;
        this.createdAt          = createdAt;
    }
}
