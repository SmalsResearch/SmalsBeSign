package be.smals.research.bulksign.desktopapp.signverify;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import sun.security.pkcs11.wrapper.PKCS11Exception;


/*
 * Copyright (c) Smals
 *
 * Tests the BatchSignature class
 * 
 * Takes 3 files from the hard drive
 * Returns the corresponding MasterDigest and BatchSignature
 * 
 *  
 * @author juca
 * 
 * 
 */

public class TestBatchSignature {

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

            byte[] signature = BatchSignature.main(input);
        
        /* Display the signature length and value */
            System.out.print("Length of generated signature (in bytes):");
            System.out.println(signature.length);
            System.out.println("");
            System.out.print("Value of generated signature: ");

            for (int i = 0; i < signature.length; i++) {
                System.out.print(signature[i]);
                System.out.print(" ");
            }
            System.out.println("");
        
        /*Write the signature into a file*/

            File file = null;
            file = new File("C:\\Users\\tm\\Documents\\2015 - Signature en batch\\Travail Julien Cathalo\\Tests\\BCSS\\Output.sig");
            FileOutputStream file_output = new FileOutputStream(file);
            DataOutputStream data_out = new DataOutputStream(file_output);
            data_out.write(signature);
            file_output.close();

            input[0].close();
            input[1].close();
            input[2].close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } catch (PKCS11Exception e) {
            e.printStackTrace(System.err);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }


}
