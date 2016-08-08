package be.smals.research.bulksign.desktopapp.utilities;

import java.security.cert.X509Certificate;

public class SigningOutput {

    public String masterDigest;
    public byte[] signature;
    public X509Certificate certificate;

    public SigningOutput () {}
    public SigningOutput (String masterDigest, byte[] signature, X509Certificate certificate) {
        this.masterDigest   = masterDigest;
        this.signature      = signature;
        this.certificate    = certificate;
    }
}
