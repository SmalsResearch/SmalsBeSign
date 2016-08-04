package be.smals.research.bulksign.desktopapp.services;

import sun.security.pkcs11.wrapper.*;

import java.security.*;


class EIDSigningService {

    private static EIDSigningService instance = new EIDSigningService();
    private Signature signatureService;

    private EIDSigningService() {}
    public static EIDSigningService getInstance () {
        return instance;
    }

    /**
     * Retrieves the private key and initialize the signing process
     *
     * @param pkcs11
     * @param p11_session
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws PKCS11Exception
     */
    void initSign(PKCS11 pkcs11, long p11_session) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, PKCS11Exception {
        // Find the private key
        CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[2];
        attributes[0] = new CK_ATTRIBUTE();
        attributes[0].type = PKCS11Constants.CKA_CLASS;
        attributes[0].pValue = new Long(PKCS11Constants.CKO_PRIVATE_KEY);
        attributes[1] = new CK_ATTRIBUTE();
        attributes[1].type = PKCS11Constants.CKA_ID;
        attributes[1].pValue = 3;

        pkcs11.C_FindObjectsInit(p11_session, attributes);
        long[] keyHandles = pkcs11.C_FindObjects(p11_session, 1);
        pkcs11.C_FindObjectsFinal(p11_session);
        long signatureKey = keyHandles[0];

        // Init sign
        CK_MECHANISM mechanism = new CK_MECHANISM();
        mechanism.mechanism = PKCS11Constants.CKM_SHA1_RSA_PKCS;
        mechanism.pParameter = null;
        pkcs11.C_SignInit(p11_session, mechanism, signatureKey);
    }

    byte[] sign(PKCS11 pkcs11, long p11_session, String masterDigest) throws SignatureException, PKCS11Exception {
        return pkcs11.C_Sign(p11_session, masterDigest.getBytes());
    }
}
