package be.smals.research.bulksign.desktopapp.signverify;

import java.io.IOException;
import java.io.FileInputStream;

import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Constants;
import sun.security.pkcs11.wrapper.PKCS11Exception; 


/*
* Copyright (c) Smals
*
*  Input : 1 set of document (as an array of file input streams),
*  
*  Output : 1 batch signature (as byte array), 
*  
* @author juca
* 
* 
*/

public class BatchSignature {

    public static byte[] main(FileInputStream[] FISArray) throws IOException, PKCS11Exception {
        PKCS11 pkcs11;
        String osName = System.getProperty("os.name");
        byte[] a = new byte[0];

        try {
            if (-1 != osName.indexOf("Windows"))
                pkcs11 = PKCS11.getInstance("beidpkcs11.dll", "C_GetFunctionList", null, false);
            else
                pkcs11 = PKCS11.getInstance("libbeidpkcs11.so", "C_GetFunctionList", null, false);


            //Open the P11 session 
            long p11_session = pkcs11.C_OpenSession(0, PKCS11Constants.CKF_SERIAL_SESSION, null, null);

            try {
                //Find the signature private key 
                CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[2];
                attributes[0] = new CK_ATTRIBUTE();
                attributes[0].type = PKCS11Constants.CKA_CLASS;
                attributes[0].pValue = PKCS11Constants.CKO_PRIVATE_KEY;
                attributes[1] = new CK_ATTRIBUTE();
                attributes[1].type = PKCS11Constants.CKA_ID;
                attributes[1].pValue = 3;
                System.out.println("Debug (1)");
                pkcs11.C_FindObjectsInit(p11_session, attributes);
                System.out.println("Debug (2)");

                long[] keyHandles = pkcs11.C_FindObjects(p11_session, 1);
                long signatureKey = keyHandles[0];
                System.out.println("Debug (3)");

                pkcs11.C_FindObjectsFinal(p11_session);

                System.out.println("BEFORE COMPUTEMASTERDIGEST - DONE");

                //Compute the Master Digest (a String) using the ComputeMasterDigest method
                String MasterDigest = ComputeMasterDigest.main(FISArray);

                System.out.println("AFTER COMPUTEMASTERDIGEST - DONE");

                System.out.print("Master Digest = ");
                System.out.println(MasterDigest);
                System.out.println("");
                System.out.print("Length of MasterDigest = ");
                System.out.println(MasterDigest.length());
                System.out.println("");

                //Initialize the signature 
                CK_MECHANISM mechanism = new CK_MECHANISM();
                mechanism.mechanism = PKCS11Constants.CKM_SHA1_RSA_PKCS;
                mechanism.pParameter = null;
                pkcs11.C_SignInit(p11_session, mechanism, signatureKey);

                //Sign the data after converting the Master Digest string into a byte array
                byte[] signature = pkcs11.C_Sign(p11_session, MasterDigest.getBytes());

                System.out.println("Batch Signing succesfull !");

                return (signature);

            } catch (Exception e) {
                System.out.println("[Catch] Exception: " + e.getMessage());
                return (a);
            } finally {
                //Close the session
                pkcs11.C_CloseSession(p11_session);
            }
        } catch (Exception e) {
            System.out.println("[Catch] Exception: " + e.getMessage());
            return (a);
        }

    }

}
               