package be.smals.research.bulksign.desktopapp.services;

import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class VerifySigningService {

    public VerifySigningService () {}

    public boolean verifySigning (FileInputStream file, SigningOutput signingOutput)
            throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeyException,
                    SignatureException, CertificateException {
        String fileDigest = DigestService.getInstance().computeIndividualDigest(file);
        if (!this.isIndividualDigestPartOfMasterDigest(signingOutput.masterDigest, fileDigest))
            return false;
        if (!this.isCertificateChainValid(signingOutput.certificateChain))
            return false;

        Signature signer = Signature.getInstance("SHA1withRSA", "BC");
        signer.initVerify(signingOutput.certificateChain.get(2).getPublicKey()); // [2] is the user certificate
        signer.update(signingOutput.masterDigest.getBytes());

        return signer.verify(signingOutput.signature);
    }

    public boolean isCertificateChainValid(List<X509Certificate> certificateChain)
            throws NoSuchAlgorithmException, CertificateException, NoSuchProviderException, InvalidKeyException, SignatureException {
        X509Certificate rootCert    = certificateChain.get(0);
        X509Certificate intermCert  = certificateChain.get(1);
        X509Certificate userCert    = certificateChain.get(2);
        boolean userCertValid       = this.isCertificateValid(userCert, intermCert.getPublicKey());
        boolean intermCertValid     = this.isCertificateValid(intermCert, rootCert.getPublicKey());
        boolean rootCertValid       = this.isCertificateValid(rootCert, rootCert.getPublicKey());
        return userCertValid && intermCertValid && rootCertValid;
    }
    private boolean isCertificateValid (X509Certificate certificate, PublicKey authorityPubKey)
            throws CertificateException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        certificate.checkValidity();
        certificate.verify(authorityPubKey);
        return true;
    }
    public SigningOutput getSigningOutput (File signingOutputFile) throws ParserConfigurationException, IOException, SAXException, CertificateException, NoSuchProviderException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(signingOutputFile);

        document.getDocumentElement().normalize();

        Element signingOutputElement    = (Element) document.getElementsByTagName("SigningOutput").item(0);
        String masterDigest             = signingOutputElement.getElementsByTagName("MasterDigest").item(0).getTextContent().toLowerCase();
        byte[] signature                = DatatypeConverter.parseHexBinary(signingOutputElement.getElementsByTagName("Signature").item(0).getTextContent());
        Element certificateElement      = (Element) signingOutputElement.getElementsByTagName("Certificate").item(0);
        byte[] rootEncodedCertificate   = DatatypeConverter.parseHexBinary(certificateElement.getElementsByTagName("Root").item(0).getTextContent());
        byte[] intermEncodedCertificate = DatatypeConverter.parseHexBinary(certificateElement.getElementsByTagName("Intermediate").item(0).getTextContent());
        byte[] userEncodedCertificate   = DatatypeConverter.parseHexBinary(certificateElement.getElementsByTagName("User").item(0).getTextContent());

        CertificateFactory certFactory  = CertificateFactory.getInstance("X.509", "BC");
        InputStream encodedStream       = new ByteArrayInputStream(rootEncodedCertificate);
        X509Certificate rootCertificate = (X509Certificate) certFactory.generateCertificate(encodedStream);
        encodedStream                   = new ByteArrayInputStream(intermEncodedCertificate);
        X509Certificate intermCertificate = (X509Certificate) certFactory.generateCertificate(encodedStream);
        encodedStream                   = new ByteArrayInputStream(userEncodedCertificate);
        X509Certificate userCertificate = (X509Certificate) certFactory.generateCertificate(encodedStream);
        List<X509Certificate> certificateChain = new ArrayList<>();
        certificateChain.add(rootCertificate);
        certificateChain.add(intermCertificate);
        certificateChain.add(userCertificate);

        return new SigningOutput(masterDigest, signature, certificateChain);
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
            if (masterDigest.regionMatches(64 * j, individualDigest, 0, 64)) {
                System.out.print("The Individual Digest corresponds to position ");
                System.out.print(j);
                System.out.println(" in the Master Digest.");
                found = true;
            }
        }
        return found;
    }

}
