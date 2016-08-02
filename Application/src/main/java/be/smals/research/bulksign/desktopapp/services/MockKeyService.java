package be.smals.research.bulksign.desktopapp.services;

import java.nio.ByteBuffer;
import java.security.*;

public class MockKeyService extends KeyService {

    private static MockKeyService instance = new MockKeyService();
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private MockKeyService () {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException|NoSuchProviderException e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public static MockKeyService getInstance() {
        return instance;
    }

    @Override
    public long getKey() {
        return ByteBuffer.wrap(this.privateKey.getEncoded()).getLong();
    }
    @Override
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
}
