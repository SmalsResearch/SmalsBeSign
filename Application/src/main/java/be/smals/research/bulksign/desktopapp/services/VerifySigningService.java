package be.smals.research.bulksign.desktopapp.services;

import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import be.smals.research.bulksign.desktopapp.exception.BulkSignException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;

public class VerifySigningService {

    public VerifySigningService () {}

    public boolean verifySigning (FileInputStream file, byte[] signature, String masterDigest, PublicKey key)
            throws NoSuchProviderException, NoSuchAlgorithmException, IOException, BulkSignException,
                    InvalidKeyException, SignatureException {
        /* Compute Individual Digest of document */
        String individualDigest = DigestService.getInstance().computeIndividualDigest(file);

        /* Verify that Individual Digest is part of Master Digest.

         To do this, read all the Digests that are concatenated in the Master Digest
         and check if at least one of them is equal to the Individual Digest
         */
        boolean found = this.isIndividualDigestPartOfMasterDigest(masterDigest, individualDigest);

        if (!found)
            throw new BulkSignException("Invalid Batch Signature (cause : Individual Digest and Master Digest do not match)");

        /* Verify that Signature of Master Digest is Valid*/
        Signature signer = Signature.getInstance("SHA1withRSA", "BC");

        signer.initVerify(key);

        for (int j = 0; j < masterDigest.length(); j++) {
            signer.update(masterDigest.getBytes()[j]);
        }

        return signer.verify(signature);
    }

    public SigningOutput getSigningOutput (File signingOutputFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(signingOutputFile);

        document.getDocumentElement().normalize();

        Element signingOutputElement    = (Element) document.getElementsByTagName("SigningOutput").item(0);
        String masterDigest             = signingOutputElement.getElementsByTagName("MasterDigest").item(0).getTextContent();
        byte[] signature                = DatatypeConverter.parseBase64Binary(signingOutputElement.getElementsByTagName("Signature").item(0).getTextContent());
        return new SigningOutput(masterDigest, signature);
    }
    private boolean isIndividualDigestPartOfMasterDigest(String masterDigest, String individualDigest) {
        System.out.print("The size of MasterDigest is:  ");
        System.out.println(masterDigest.length());

        int numDigests = masterDigest.length() / 64;
        System.out.print("The number of files are: ");
        System.out.println(numDigests);

        boolean found = false;

        for (int j = 0; j < numDigests; j++) {
            if (masterDigest.regionMatches(64 * j, individualDigest, 0, 64) == true) {
                System.out.print("The Individual Digest corresponds to position ");
                System.out.print(j);
                System.out.println(" in the Master Digest.");
                found = true;
            }
        }
        return found;
    }

}
