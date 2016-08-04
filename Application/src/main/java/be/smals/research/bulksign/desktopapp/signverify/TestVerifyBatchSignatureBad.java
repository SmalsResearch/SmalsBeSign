package be.smals.research.bulksign.desktopapp.signverify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;


/*
 * Copyright (c) Smals
 *
 * Tests the VerifyBatchSignature class with bad data
 * 
 * 
 * @author juca
 * 
 * 
 */
public class TestVerifyBatchSignatureBad {

    public static void main(String[] args) {

        try {
            FileInputStream[] input = new FileInputStream[3];

            input[0] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\Test1.txt");
            input[1] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\Test2.txt");
            input[2] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\Test3.txt");

            String BadMasterDigest = "ff55ebc2f422fbdd6f5d0498e2fcff02f8bbc0fee30ab01269cb28481f71c3103bbfe515ced2bf9c181f63cf36ef83368e147784fa18dba3021c97409b3e14f823f02db43918e36e4e5014a8016d40a624b621891c9f2ff9cc15e04293591b51";

            BigInteger modulus = new BigInteger("90626713186417459989117341637879377567955124606122788333813970040764020833273994519930667951014702200178191014146232347780774737076295377652387838072465324396545023046282793697909937527926599931272442378100126314566183797240374885759080207359996360745029505169805200507599649403695784515407419106434219169127", 10);
            BigInteger pubExp = new BigInteger("65537", 10);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
            RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

            File file = new File("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\Output.sig");

            try {
                //create FileInputStream object
                FileInputStream fin = new FileInputStream(file);

                byte signature[] = new byte[(int) file.length()];

                fin.read(signature);
                fin.close();

                System.out.println("Signature : ");
                for (int i = 0; i < signature.length; i++) {
                    System.out.print(signature[i]);
                    System.out.print(" ");
                }

                System.out.println(" ");
                System.out.println(" ");

                System.out.print("Master Digest : ");
                System.out.println(BadMasterDigest);
                System.out.println(" ");

                System.out.print("Public Key : ");
                System.out.println(key);
                System.out.println(" ");

                System.out.println("Starting Batch Signature Verification on file 0...");

                String VBS = VerifyBatchSignature.main(input[0], signature, BadMasterDigest, key);
                System.out.println(VBS);
                System.out.println(" ");

                System.out.println("Starting Batch Signature Verification on file 1...");

                String VBS1 = VerifyBatchSignature.main(input[1], signature, BadMasterDigest, key);
                System.out.println(VBS1);
                System.out.println(" ");

                System.out.println("Starting Batch Signature Verification on file 2...");

                String VBS2 = VerifyBatchSignature.main(input[2], signature, BadMasterDigest, key);
                System.out.println(VBS2);
                System.out.println(" ");

                input[0].close();
                input[1].close();
                input[2].close();


            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
    }
}
