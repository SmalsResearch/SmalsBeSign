package be.smals.research.bulksign.desktopapp.services;

import java.security.*;


class MockSigningService {

    private static MockSigningService instance = new MockSigningService ();
    private Signature signatureService;

    private MockSigningService () {}
    public static MockSigningService getInstance () {
        return instance;
    }
    void initSign(String masterDigest) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        signatureService = Signature.getInstance("SHA1withRSA", "BC");
        signatureService.initSign(MockKeyService.getInstance().getPrivateKey(), new SecureRandom());
        signatureService.update(masterDigest.getBytes());
    }
    byte[] sign() throws SignatureException {
        return signatureService.sign();
    }
}
