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
import java.io.*;
import java.security.*;
import java.security.cert.*;

public class VerifySigningService {

    public VerifySigningService () {}

    public boolean verifySigning (FileInputStream file, byte[] signature, String masterDigest, PublicKey key)
            throws NoSuchProviderException, NoSuchAlgorithmException, IOException, BulkSignException,
                    InvalidKeyException, SignatureException {
        String individualDigest = DigestService.getInstance().computeIndividualDigest(file);

        // Verify that Individual Digest is part of Master Digest.
        boolean found = this.isIndividualDigestPartOfMasterDigest(masterDigest, individualDigest);
        if (!found)
            return false;

        // Verify that Signature of Master Digest is Valid
        Signature signer = Signature.getInstance("SHA1withRSA", "BC");
        signer.initVerify(key);
        for (int j = 0; j < masterDigest.length(); j++) {
            signer.update(masterDigest.getBytes()[j]);
        }

        return signer.verify(signature);
    }

    public void verifyCertificate (X509Certificate certificate) throws CertificateNotYetValidException, CertificateExpiredException {
        certificate.checkValidity();
    }

    public SigningOutput getSigningOutput (File signingOutputFile) throws ParserConfigurationException, IOException, SAXException, CertificateException, NoSuchProviderException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(signingOutputFile);

        document.getDocumentElement().normalize();

        Element signingOutputElement    = (Element) document.getElementsByTagName("SigningOutput").item(0);
        String masterDigest             = signingOutputElement.getElementsByTagName("MasterDigest").item(0).getTextContent();
        byte[] signature                = DatatypeConverter.parseHexBinary(signingOutputElement.getElementsByTagName("Signature").item(0).getTextContent());
        byte[] encodedCertificate       = DatatypeConverter.parseHexBinary(signingOutputElement.getElementsByTagName("Certificate").item(0).getTextContent());

        CertificateFactory certFactory  = CertificateFactory.getInstance("X.509", "BC");
        InputStream encodedStream       = new ByteArrayInputStream(encodedCertificate);
        X509Certificate certificate     = (X509Certificate) certFactory.generateCertificate(encodedStream);

        return new SigningOutput(masterDigest, signature, certificate);
    }

    /**
     * Returns true if the individualDigest is a part of the masterDigest
     *
     * @param masterDigest
     * @param individualDigest
     * @return
     */
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
