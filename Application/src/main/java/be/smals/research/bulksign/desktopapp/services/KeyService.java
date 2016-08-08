package be.smals.research.bulksign.desktopapp.services;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;

public abstract class KeyService {

    public KeyService() {}

    public abstract  long getKey ();
    public abstract PrivateKey getPrivateKey ();
    public abstract PublicKey getPublicKey ();
    public abstract PublicKey getPublicKey (BigInteger modulus, BigInteger publicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException;
    public abstract X509Certificate getCertificate ();
}
