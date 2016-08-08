package be.smals.research.bulksign.desktopapp.services;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class EIDKeyService extends KeyService {

    public static EIDKeyService instance = new EIDKeyService();

    private EIDKeyService () {}
    public static EIDKeyService getInstance () {
        return instance;
    }
    @Override
    public long getKey() {
        return 0;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return null;
    }

    @Override
    public PublicKey getPublicKey() {
        return null;
    }

    @Override
    public PublicKey getPublicKey(BigInteger modulus, BigInteger publicExponent) throws InvalidKeySpecException, NoSuchAlgorithmException {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

        return key;
    }

    @Override
    public X509Certificate getCertificate() {
        return null;
    }
}
