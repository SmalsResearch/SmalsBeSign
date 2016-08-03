package be.smals.research.bulksign.desktopapp.services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sun.security.pkcs11.wrapper.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.PrivateKey;


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

public class SigningService {

    private PKCS11 pkcs11;
    private String masterDigest;

    /**
     * Constructor
     */
    public SigningService() throws IOException, PKCS11Exception {
        String osName = System.getProperty("os.name");

        if (-1 != osName.indexOf("Windows"))
            pkcs11 = PKCS11.getInstance("beidpkcs11.dll", "C_GetFunctionList", null, false);
        else
            pkcs11 = PKCS11.getInstance("libbeidpkcs11.so", "C_GetFunctionList", null, false);
    }

    /**
     * Used to sign given files
     *
     * @param fileInputStreams files to sign
     * @return the signature
     */
    public byte[] sign (FileInputStream[] fileInputStreams) {
        byte[] signErrorOutput = new byte[0];

        try {
            //Open the P11 session
            long p11_session = pkcs11.C_OpenSession(0, PKCS11Constants.CKF_SERIAL_SESSION, null, null);

            try {
                //Find the signature private key
//                long signatureKey = this.findeSignaturePrivateKey(p11_session);
                PrivateKey privateKey = MockKeyService.getInstance().getPrivateKey();

                //Compute the Master Digest (a String) using the ComputeMasterDigest method
                this.masterDigest = DigestService.getInstance().computeMasterDigest(fileInputStreams);

                //Initialize the signature
//                this.initializeSignature(p11_session, signatureKey);
                MockSigningService.getInstance().initSign (this.masterDigest);

                //Sign the data after converting the Master Digest string into a byte array
//                byte[] signature = pkcs11.C_Sign(p11_session, masterDigest.getBytes());
                byte[] signature = MockSigningService.getInstance().sign();

                System.out.println("Batch SigningService successful !");

                return (signature);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[Catch] Exception: " + e.getMessage());
                return (signErrorOutput);
            } finally {
                //Close the session
                pkcs11.C_CloseSession(p11_session);
            }
        } catch (Exception e) {
            System.out.println("[Catch] Exception: " + e.getMessage());
            return (signErrorOutput);
        }
    }

    public void saveSigningOutput(byte[] signature, String filePath) throws IOException, ParserConfigurationException, TransformerException {
        // XML - Create
        DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder         =  factory.newDocumentBuilder();

        // Root element
        Document document = builder.newDocument();
        Element rootElement = document.createElement("SigningOutput");
        document.appendChild(rootElement);
        // MasterDigest
        Element masterDigestElement = document.createElement("MasterDigest");
        masterDigestElement.appendChild(document.createTextNode(this.masterDigest));
        rootElement.appendChild(masterDigestElement);
        // Signature
        Element signatureElement = document.createElement("Signature");
        signatureElement.appendChild(document.createTextNode(new String(signature)));
        rootElement.appendChild(signatureElement);
        // XML - Write
        TransformerFactory transformerFactory   = TransformerFactory.newInstance();
        Transformer transformer                 = transformerFactory.newTransformer();
        DOMSource source                        = new DOMSource(document);
        StreamResult result               = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    /**
     * Outputs the master digest
     *
     * @param masterDigest master digest
     */
    private void outputDigest(String masterDigest) {
        System.out.print("Master Digest = ");
        System.out.println(masterDigest);
        System.out.println("");
        System.out.print("Length of MasterDigest = ");
        System.out.println(masterDigest.length());
        System.out.println("");
    }

    /**
     *
     * @param p11_session
     * @return
     * @throws PKCS11Exception
     */
    private long findSignaturePrivateKey(long p11_session) throws PKCS11Exception {
        CK_ATTRIBUTE[] attributes = new CK_ATTRIBUTE[2];
        attributes[0] = new CK_ATTRIBUTE();
        attributes[0].type = PKCS11Constants.CKA_CLASS;
        attributes[0].pValue = new Long(PKCS11Constants.CKO_PRIVATE_KEY);
        attributes[1] = new CK_ATTRIBUTE();
        attributes[1].type = PKCS11Constants.CKA_ID;
        attributes[1].pValue = 3;

        this.pkcs11.C_FindObjectsInit(p11_session, attributes);
        long[] keyHandles = pkcs11.C_FindObjects(p11_session, 1);
        pkcs11.C_FindObjectsFinal(p11_session);

        return keyHandles[0];
    }

    private void initializeSignature(long p11_session, long signatureKey) throws PKCS11Exception {
        CK_MECHANISM mechanism = new CK_MECHANISM();
        mechanism.mechanism = PKCS11Constants.CKM_SHA1_RSA_PKCS;
        mechanism.pParameter = null;
        pkcs11.C_SignInit(p11_session, mechanism, signatureKey);
    }
}
               