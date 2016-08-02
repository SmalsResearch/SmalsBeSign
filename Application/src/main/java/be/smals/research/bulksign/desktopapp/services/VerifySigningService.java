package be.smals.research.bulksign.desktopapp.services;

import be.smals.research.bulksign.desktopapp.exception.BulkSignException;
import org.bouncycastle.util.encoders.Hex;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class VerifySigningService {

    public VerifySigningService () {}

    public boolean verifySigning (FileInputStream file, byte[] signature, String masterDigest, PublicKey key) throws NoSuchProviderException, NoSuchAlgorithmException, IOException, BulkSignException, InvalidKeyException, SignatureException {

        /* Compute Individual Digest of document */
        int read;
        byte[] buffer = new byte[8192];

        MessageDigest digest = MessageDigest.getInstance("SHA-256", "BC");

        while ((read = file.read(buffer)) > 0) {
            digest.update(buffer, 0, read);
        }
        file.close();
        byte[] hash = digest.digest();
        //BigInteger bigInt = new BigInteger(1, hash);
        //String IndividualDigest = bigInt.toString(16);
        // Replacement of the 2 previous lines in order to accept the 0 at the beginning of an hex number
        String IndividualDigest = new String(Hex.encode(hash));


        /* Verify that Individual Digest is part of Master Digest.

         To do this, read all the Digests that are concatenated in the Master Digest
         and check if at least one of them is equal to the Individual Digest
         */

        System.out.print("The size of MasterDigest is:  ");
        System.out.println(masterDigest.length());

        int numDigests = masterDigest.length() / 64;
        System.out.print("The number of files are: ");
        System.out.println(numDigests);

        int found = 0;

        for (int j = 0; j < numDigests; j++) {
            if (masterDigest.regionMatches(64 * j, IndividualDigest, 0, 64) == true) {
                System.out.print("The Individual Digest corresponds to position ");
                System.out.print(j);
                System.out.println(" in the Master Digest.");
                found = 1;
            }
        }

        if (found == 0)
            throw new BulkSignException("Invalid Batch Signature (cause : Individual Digest and Master Digest do not match)");

        /* Verify that Signature of Master Digest is Valid*/
        Signature signer = Signature.getInstance("SHA1withRSA", "BC");

        /* Convert the PublicKey, the Master Digest and the Batch Signature in objects */

        boolean sigvalid;

        /*X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
         KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
         PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
         */

        signer.initVerify(key);

        for (int j = 0; j < masterDigest.length(); j++) {
            signer.update(masterDigest.getBytes()[j]);
        }

        sigvalid = signer.verify(signature);

        return sigvalid;
    }

    public PublicKey getPublicKey (BigInteger modulus, BigInteger publicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

        return MockKeyService.getInstance().getPublicKey();
    }
}
