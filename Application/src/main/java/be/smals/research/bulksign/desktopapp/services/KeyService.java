package be.smals.research.bulksign.desktopapp.services;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

public abstract class KeyService {

    public KeyService() {}

    public abstract  long getKey ();
    public abstract PrivateKey getPrivateKey ();
    public abstract PublicKey getPublicKey ();
    public abstract List<X509Certificate> getCertificateChain ();
}
