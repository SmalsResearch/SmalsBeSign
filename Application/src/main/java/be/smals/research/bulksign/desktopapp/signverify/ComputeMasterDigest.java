package be.smals.research.bulksign.desktopapp.signverify;

import org.bouncycastle.util.encoders.Hex;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/*
 * Copyright (c) Smals
 */

/**
 * Takes as input the list of documents (as an array of file input streams)
 * Outputs the master digest (as a string)
 *
 * @author juca
 */


public class ComputeMasterDigest {

    public static String main(FileInputStream[] FISArray) {

		/*Create an array that will store the individual digest of each file
         * meaning that
		 *     IndividualDigest[i]=SHA256(File[i])
		 */
        String[] IndividualDigest = new String[FISArray.length];

        for (int i = 0; i < FISArray.length; i++) {
            FileInputStream is = FISArray[i];

            try {

				/* Compute a hash of File[i]*/

                int read;
                byte[] buffer = new byte[8192];

                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    while ((read = is.read(buffer)) > 0) {
                        digest.update(buffer, 0, read);
                    }
                    byte[] hash = digest.digest();
                    //BigInteger bigInt = new BigInteger(1, hash);
                    //IndividualDigest[i] = bigInt.toString(16);
                    // Replacement of the 2 previous lines in order to accept the 0 at the beginning of an hex number
                    IndividualDigest[i] = new String(Hex.encode(hash));

                    System.out.print("Digest number ");
                    System.out.print(i);
                    System.out.print(" = ");
                    System.out.print(IndividualDigest[i]);
                    System.out.print(" of length ");
                    System.out.println(IndividualDigest[i].length());

                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    return null;
                }

                is.close();

            } catch (IOException e) {
                System.out.print("Exception");

            }

        }

        String MasterDigest = "";

		/* Compute the Master Digest as a concatenation of the IndividualDigest strings.*/


        for (int j = 0; j < IndividualDigest.length; j++) {
            MasterDigest += IndividualDigest[j];
        }


        return (MasterDigest);

    }

}





