package be.smals.research.bulksign.desktopapp.signverify;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;

import org.bouncycastle.util.encoders.Hex;

/*
 * Copyright (c) Smals
 *
 *  Input : 1 document (as file input stream), 1 batch signature (as byte array), 
 * 1 master digest (string) , 1 public key (java object)
 * - Output : String : "valid" if the signature is valid, else a description of the problem.
 * 
 * 
 * @author juca
 * 
 * 
 */


public class VerifyBatchSignature {

    public static String main(FileInputStream FIS, byte[] BatchSignature, String MasterDigest, PublicKey pk) throws Exception {

		/* Compute Individual Digest of document */
        int read;
        byte[] buffer = new byte[8192];

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        while ((read = FIS.read(buffer)) > 0) {
            digest.update(buffer, 0, read);
        }

        FIS.close();
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
        System.out.println(MasterDigest.length());

        int numDigests = MasterDigest.length() / 64;
        System.out.print("The number of files are: ");
        System.out.println(numDigests);

        int found = 0;

        for (int j = 0; j < numDigests; j++) {
            if (MasterDigest.regionMatches(64 * j, IndividualDigest, 0, 64) == true) {
                System.out.print("The Individual Digest corresponds to position ");
                System.out.print(j);
                System.out.println(" in the Master Digest.");
                found = 1;
            }
        }

        if (found == 0)
            return ("Invalid Batch Signature (cause : Individual Digest and Master Digest do not match)");

        /* Verify that Signature of Master Digest is Valid*/

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Signature signer = Signature.getInstance("SHA1withRSA");

        /* Convert the PublicKey, the Master Digest and the Batch Signature in objects */

        boolean sigvalid;

        /*X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
         KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
         PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
         */

        signer.initVerify(pk);

        for (int j = 0; j < MasterDigest.length(); j++) {
            signer.update(MasterDigest.getBytes()[j]);
        }

        sigvalid = signer.verify(BatchSignature);

        if (sigvalid == true)
            return ("The Batch Signature is valid.");
        else
            return ("Invalid Batch Signature (cause : invalid Digital Signature of Master Digest)");
    }
}
