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
 * Tests the VerifyBatchSignature class with valid data
 *
 * @author juca
 * 
 * 
 */
public class TestVerifyBatchSignature {

    public static void main(String[] args) {

        try {
            FileInputStream[] input = new FileInputStream[3];            
                        
            /*
            input[0] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\Test1.txt");
            input[1] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\Test2.txt");
            input[2] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\Test3.txt");
            */
            input[0] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\BCSS\\2015BSM994.pdf");
            input[1] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\BCSS\\BCSS 2015 Convention T�l�travail CHEVALIER Jacques.pdf");
            input[2] = new FileInputStream("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\BCSS\\KSZ 2015 Telewerkovereenkomst PUTTEMANS Tom.pdf");

            //String MasterDigest="bffea6aa2630b5870643ff09ab35a6e2ff7be129989f3eda25c0550c9f27be26025cb97865a254bf6b094824536de526ea35a667d6823b847e3a98563b068c824131d8d6accee32f668bec4d7c2e7311cef3b30be8c58e9fda1eabe72b4b11c9";

            String MasterDigest = "fa8eb5be512f638f694aa1643270ac9c81a1aaed263de3c6d5eeac2576d6705a9687bb02e808836a4190144f414912bc16876e32dabfda9d8922cd16954ebe848a5ca919bfc500fc16d0d68473f2ffee0c019bde5da7acf8cb30a601021ebd9a";
            /* Modulus and pubExp of the public key used to sign*/
            BigInteger modulus = new BigInteger("95099863606005976866430829628947691169181414536044822663514146236363189484868479038765230330544286836853478245670270453709068745482695225683666031954181177729573100887158579643353159710420254746103592570767926512143089393115103159904644719399002044705720254566168147379698895092832292865563385058248455055373", 10);
            BigInteger pubExp = new BigInteger("65537", 10);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
            RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

            File file = new File("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\BCSS\\Output.sig");

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
                System.out.println(MasterDigest);
                System.out.println(" ");

                System.out.print("Public Key : ");
                System.out.println(key);
                System.out.println(" ");

                System.out.println("Starting Batch Signature Verification on file 0...");

                String VBS = VerifyBatchSignature.main(input[0], signature, MasterDigest, key);
                System.out.println(VBS);
                System.out.println(" ");

                System.out.println("Starting Batch Signature Verification on file 1...");

                String VBS1 = VerifyBatchSignature.main(input[1], signature, MasterDigest, key);
                System.out.println(VBS1);
                System.out.println(" ");

                System.out.println("Starting Batch Signature Verification on file 2...");

                String VBS2 = VerifyBatchSignature.main(input[2], signature, MasterDigest, key);
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
