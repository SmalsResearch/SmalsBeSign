package be.smals.research.bulksign.desktopapp.utilities;

import java.security.cert.X509Certificate;
import java.util.List;

public class SigningOutput {

    public String masterDigest;
    public byte[] signature;
    public List<X509Certificate> certificateChain;

    public SigningOutput () {}
    public SigningOutput (String masterDigest, byte[] signature, List<X509Certificate> certificate) {
        this.masterDigest       = masterDigest;
        this.signature          = signature;
        this.certificateChain   = certificate;
    }
}
