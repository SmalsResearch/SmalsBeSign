package be.smals.research.bulksign.desktopapp.services;

import be.smals.research.bulksign.desktopapp.exception.BulkSignException;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import be.smals.research.bulksign.desktopapp.utilities.Utilities;
import be.smals.research.bulksign.desktopapp.utilities.VerifySigningOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import sun.security.provider.certpath.OCSP;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.security.cert.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service used to verify a signed file
 */
public class VerifySigningService {

    private static final String CERT_VERIFICATION_URL = "http://certs.eid.belgium.be/";
    private static final String CRL_VERIFICATION_URL = "http://crl.eid.belgium.be/";
    private static final String CERT_ROOT_URL = CERT_VERIFICATION_URL +"belgiumrca";
    private static final String CRL_ROOT_URL = CRL_VERIFICATION_URL +"belgium";
    public VerifySigningService () {}

    /**
     *
     * @param file
     * @param sOut
     * @return verify output
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws CertificateException
     */
    public VerifySigningOutput verifySigning (File file, SigningOutput sOut)
            throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeyException,
            SignatureException, CertificateException, BulkSignException {
        VerifySigningOutput verifySigningOutput = new VerifySigningOutput(file.getName(), sOut.author, sOut.createdAt);

        String fileDigest = DigestService.getInstance().computeIndividualDigest(new FileInputStream(file));
        if (!this.isIndividualDigestPartOfMasterDigest(sOut.masterDigest, fileDigest))
            return verifySigningOutput;
        verifySigningOutput.digestValid = true;

        this.verifyCertificates(sOut.certificateChain, verifySigningOutput);

        Signature signer = Signature.getInstance("SHA1withRSA", "BC");
        signer.initVerify(sOut.certificateChain.get(0).getPublicKey()); // [0] is the user certificate
        signer.update(sOut.masterDigest.getBytes());
        if (signer.verify(sOut.signature))
            verifySigningOutput.signatureValid = true;

        return verifySigningOutput;
    }
    /**
     * Used to check if the certificate chain is valid
     *
     * @param certificateChain the collection of certificate
     * @return true if the chain is valid
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    private boolean isCertificateChainValid(List<X509Certificate> certificateChain)
            throws NoSuchAlgorithmException, CertificateException, NoSuchProviderException, InvalidKeyException, SignatureException, BulkSignException {
        X509Certificate rootCert    = certificateChain.get(2);
        X509Certificate intermCert  = certificateChain.get(1);
        X509Certificate userCert    = certificateChain.get(0);
        boolean userCertValid, intermCertValid, rootCertValid;
        try {
            userCertValid = this.isCertificateValid(userCert, intermCert.getPublicKey());
        } catch (Exception e) {
            throw new BulkSignException("Unable to validate the user certificate");
        }
        try {
            intermCertValid = this.isCertificateValid(intermCert, rootCert.getPublicKey());
        } catch (Exception e) {
            throw new BulkSignException("Unable to validate the intermediate certificate");
        }
        try {
            rootCertValid = this.isCertificateValid(rootCert, rootCert.getPublicKey());
        } catch (Exception e) {
            throw new BulkSignException("Unable to validate the root certificate");
        }
        return userCertValid && intermCertValid && rootCertValid;
    }
    /**
     * Checks an individual certificate validity
     *
     * @param certificate the certificate to check
     * @param authorityPubKey the authority who signed the certificate's Public Key
     * @return true if the certificate is valid
     * @throws CertificateException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    private boolean isCertificateValid (X509Certificate certificate, PublicKey authorityPubKey)
            throws CertificateException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        certificate.checkValidity();
        certificate.verify(authorityPubKey);
        return true;
    }
    /**
     * Checks root certificate validity on the internet
     *
     * @param certificate
     * @return
     */
    private boolean isRootCertificateValid(X509Certificate certificate) {
        // Prepare URL -
        String caType = certificate.getSubjectDN().getName().split("CA")[1].split(",")[0];
        String fullUrl = CERT_ROOT_URL+caType+".crt";
        System.out.println("Root CERT : "+fullUrl);

        try {
            X509Certificate beRootCA3Certificate = this.getX509CertificateFromUrl(fullUrl);

            return certificate.equals(beRootCA3Certificate);
        } catch (IOException | CertificateException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean isRootCertificateInCRL (X509Certificate certificate) {
        // Prepare URL - C=BE,CN=Belgium Root CA3, .....
        String caType = certificate.getSubjectDN().getName().split("CA")[1].split(",")[0];
        String fullUrl = CRL_ROOT_URL+caType+".crl";
        System.out.println("Root CRL : "+fullUrl);
        try {
            X509CRL intermCRL       = this.getX509CRLFromUrl(fullUrl);
            return intermCRL.isRevoked(certificate);
        } catch (IOException | CertificateException | CRLException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Checks intermediate certificate validity on the internet
     *
     * @param certificate
     * @return
     */
    private boolean isIntermediateCertificateValid(X509Certificate certificate) {
        // Prepare URL - Issuer format : C=BE,CN={Foreigner, Citizen} CA,SERIALNUMBER=YYYYMM
        String subjectName      = (((certificate.getSubjectDN().getName().split("CN="))[1]).split(" "))[0];
        String subjectSerial    = ((certificate.getSubjectDN().getName().split("SERIALNUMBER="))[1]).split(",")[0].trim();
        String fullUrl = CERT_VERIFICATION_URL +subjectName.toLowerCase()+subjectSerial+".crt";
        System.out.println("Intermediate CERT : "+fullUrl);
        try {
            X509Certificate beIntermCA3Certificate = getX509CertificateFromUrl(fullUrl);
            return certificate.equals(beIntermCA3Certificate);
        } catch (IOException | CertificateException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     *
     * @param certificate
     * @return true is the certificate serial is in the Revocation list
     */
    private boolean isIntermediateCertificateInCRL (X509Certificate certificate){
        // Prepare URL - Issuer format : C=BE,CN={Foreigner, Citizen} CA,SERIALNUMBER=YYYYMM
        String subjectName      = (((certificate.getSubjectDN().getName().split("CN="))[1]).split(" "))[0];
        String subjectSerial    = ((certificate.getSubjectDN().getName().split("SERIALNUMBER="))[1]).split(",")[0].trim();
        String fullUrl = CRL_VERIFICATION_URL +"eid"+(subjectName.toLowerCase()).charAt(0)+subjectSerial+".crl";
        System.out.println("Intermediate CRL : "+fullUrl);
        try {
            X509CRL intermCRL       = this.getX509CRLFromUrl(fullUrl);
            return intermCRL.isRevoked(certificate);
        } catch (IOException | CertificateException | CRLException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Retrieve a X509 certificate from the url passed in param
     *
     * @param fileURL internet url
     * @return X509Certificate
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchProviderException
     */
    private X509Certificate getX509CertificateFromUrl(String fileURL)
            throws IOException, CertificateException, NoSuchProviderException {
        // Download
        URL beCertURL = new URL (fileURL);
        URLConnection connection;
        connection    = beCertURL.openConnection();
        InputStream in              = connection.getInputStream();
        byte[] buffer   = new byte[1024];
        int len;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while ((len = in.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        in.close();
        // Write to file
        File file = File.createTempFile("belgiumCert", ".crt");
        FileOutputStream fos = new FileOutputStream(file);
        if (!file.exists()) file.createNewFile();
        fos.write(outputStream.toByteArray());
        fos.flush();
        fos.close();
        file.deleteOnExit();
        // Convert to X509Certificate
        CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        return (X509Certificate) cf.generateCertificate(bis);
    }
    /**
     * Retrieve a X509 certificate revocation list from the url passed in param
     *
     * @param fileURL internet url
     * @return X509CRl
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchProviderException
     */
    private X509CRL getX509CRLFromUrl(String fileURL)
            throws IOException, CertificateException, NoSuchProviderException, CRLException {
        // Download
        URL beCertURL = new URL (fileURL);
        URLConnection connection    = beCertURL.openConnection();
        InputStream in              = connection.getInputStream();
        byte[] buffer   = new byte[1024];
        int len;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while ((len = in.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }
        in.close();
        // Write to file
        File file = File.createTempFile("belgiumCert", ".crt");
        FileOutputStream fos = new FileOutputStream(file);
        if (!file.exists()) file.createNewFile();
        fos.write(outputStream.toByteArray());
        fos.flush();
        fos.close();
        file.deleteOnExit();
        // Convert to X509CRL
        CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        return (X509CRL) cf.generateCRL(bis);
    }
    /**
     * Extracts and returns a SigningOutput from a signature file
     *
     * @param signingOutputFile signature file
     * @return a SigningOutput object
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws CertificateException
     * @throws NoSuchProviderException
     */
    public SigningOutput getSigningOutput (File signingOutputFile)
            throws ParserConfigurationException, IOException, SAXException, CertificateException, NoSuchProviderException, ParseException, BulkSignException {
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(signingOutputFile);
            document.getDocumentElement().normalize();
        } catch (Exception e) {
            throw new BulkSignException("Unable to parse the signature file.\n--- The signature file might be corrupted.");
        }

        Element signingOutputElement, certificateElement;
        List<X509Certificate> certificateChain = new ArrayList<>();
        byte[] signature, userEncodedCertificate,intermEncodedCertificate,rootEncodedCertificate;
        String masterDigest, signedBy, softwareVersion;
        Date signedAt;
        InputStream encodedStream;

        try {
            signingOutputElement = (Element) document.getElementsByTagName("SigningOutput").item(0);
            if (signingOutputElement == null || !signingOutputElement.hasChildNodes())
                throw new BulkSignException();
        } catch (Exception e) {
            throw new BulkSignException("Missing signing output.\n--- The signature file might be corrupted.");
        }
        try {
            masterDigest = signingOutputElement.getElementsByTagName("MasterDigest").item(0).getTextContent().toLowerCase();
        } catch (Exception e) {
            throw new BulkSignException("Unable to retrieve the MasterDigest.\n--- The signature file might be corrupted.");
        }

        try {
            softwareVersion = signingOutputElement.getElementsByTagName("SoftwareVersion").item(0).getTextContent();
        } catch (Exception e) {
            throw new BulkSignException("Unable to retrieve the software version.\n--- The signature file might be corrupted.");
        }try {
            signedBy = signingOutputElement.getElementsByTagName("SignedBy").item(0).getTextContent();
        } catch (Exception e) {
            throw new BulkSignException("Unable to retrieve the signer name.\n--- The signature file might be corrupted.");
        }try {
            signedAt = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss").parse(signingOutputElement.getElementsByTagName("SignedAt").item(0).getTextContent());
        } catch (Exception e) {
            throw new BulkSignException("Unable to retrieve the signing date.\n--- The signature file might be corrupted.");
        }
        try {
            signature = DatatypeConverter.parseHexBinary(signingOutputElement.getElementsByTagName("Signature").item(0).getTextContent());
        } catch (Exception e) {
            throw new BulkSignException("Unable to retrieve the signature.\n--- The signature file might be corrupted.");
        }
        try {
            certificateElement = (Element) signingOutputElement.getElementsByTagName("Certificate").item(0);
            if (certificateElement == null || !certificateElement.hasChildNodes())
                throw new BulkSignException("Unable to retrieve certificates.\n--- The signature file might be corrupted.");
        } catch (Exception e) {
            throw new BulkSignException("Missing certificates.\n--- The signature file might be corrupted.");
        }
        try {
            userEncodedCertificate = DatatypeConverter.parseHexBinary(certificateElement.getElementsByTagName("User").item(0).getTextContent());
        } catch (Exception e) {
            throw new BulkSignException("Error while parsing the user certificate.\n--- The signature file might be corrupted.");
        }
        try {
            intermEncodedCertificate = DatatypeConverter.parseHexBinary(certificateElement.getElementsByTagName("Intermediate").item(0).getTextContent());
        } catch (Exception e) {
            throw new BulkSignException("Error while parsing the intermediate certificate.\n--- The signature file might be corrupted.");
        }
        try {
            rootEncodedCertificate = DatatypeConverter.parseHexBinary(certificateElement.getElementsByTagName("Root").item(0).getTextContent());
        } catch (Exception e) {
            throw new BulkSignException("Error while parsing the root certificate.\n--- The signature file might be corrupted.");
        }
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509", "BC");
        try {
            encodedStream = new ByteArrayInputStream(userEncodedCertificate);
            X509Certificate userCertificate = (X509Certificate) certFactory.generateCertificate(encodedStream);
            certificateChain.add(userCertificate);
        } catch (Exception e) {
            throw new BulkSignException("Error while re-building user certificate.\n--- The signature file might be corrupted.");
        }
        try {
            encodedStream = new ByteArrayInputStream(intermEncodedCertificate);
            X509Certificate intermCertificate = (X509Certificate) certFactory.generateCertificate(encodedStream);
            certificateChain.add(intermCertificate);
        } catch (Exception e) {
            throw new BulkSignException("Error while re-building intermediate certificate.\n--- The signature file might be corrupted.");
        }
        try {
            encodedStream = new ByteArrayInputStream(rootEncodedCertificate);
            X509Certificate rootCertificate = (X509Certificate) certFactory.generateCertificate(encodedStream);
            certificateChain.add(rootCertificate);
        } catch (Exception e){
            throw new BulkSignException("Error while re-building root certificate.\n--- The signature file might be corrupted.");
        }

        return new SigningOutput(masterDigest, signature, certificateChain, signedBy, signedAt, softwareVersion);
    }
    /**
     * Returns true if the individualDigest is a part of the masterDigest
     *
     * @param masterDigest
     * @param individualDigest
     * @return
     */
    private boolean isIndividualDigestPartOfMasterDigest(String masterDigest, String individualDigest) {
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
    public VerifySigningOutput verifyCertificates(List<X509Certificate> certificateChain, VerifySigningOutput verifySigningOutput)
            throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException, IOException, BulkSignException {
        if (this.isCertificateChainValid(certificateChain))
            verifySigningOutput.certChainValid = true;
        else
            return verifySigningOutput;

        if (Utilities.getInstance().isInternetReachable()) {
            verifySigningOutput.userCertChecked = true;
            try {
                OCSP.RevocationStatus response = OCSP.check(certificateChain.get(0), certificateChain.get(1));
                if (response.getCertStatus().equals(OCSP.RevocationStatus.CertStatus.GOOD))
                    verifySigningOutput.userCertValid = true;
                else if (response.getCertStatus().equals(OCSP.RevocationStatus.CertStatus.UNKNOWN))
                    verifySigningOutput.userCertChecked = false;
                else
                    verifySigningOutput.userCertValid = false;
            } catch (CertPathValidatorException e) {
                e.printStackTrace();
                verifySigningOutput.userCertChecked = false;
            }
        }
        if (Utilities.getInstance().isInternetReachable()) {
            verifySigningOutput.intermCertChecked = true;
            if (this.isIntermediateCertificateValid(certificateChain.get(1)))
                verifySigningOutput.intermCertValid = true;
            if (!verifySigningOutput.intermCertValid)
                verifySigningOutput.intermCertInCRL = this.isIntermediateCertificateInCRL(certificateChain.get(1));
        }
        if (Utilities.getInstance().isInternetReachable()) {
            verifySigningOutput.rootCertChecked = true;
            if (this.isRootCertificateValid(certificateChain.get(2)))
                verifySigningOutput.rootCertValid = true;
            if (!verifySigningOutput.rootCertValid)
                verifySigningOutput.rootCertInCRL = this.isRootCertificateInCRL(certificateChain.get(2));
        }

        return verifySigningOutput;
    }
}
