package be.smals.research.bulksign.desktopapp.services;

import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import be.smals.research.bulksign.desktopapp.utilities.Utilities;
import be.smals.research.bulksign.desktopapp.utilities.VerifySigningOutput;
import org.bouncycastle.openssl.PEMReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Service used to verify a signed file
 */
public class VerifySigningService {

    public VerifySigningService () {}

    /**
     * Check if the signing is valid for the file
     *
     * @param file
     * @param signingOutput
     * @return true if signing is valid
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws CertificateException
     */
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

        file.close();
        return signer.verify(signingOutput.signature);
    }

    /**
     *
     * @param file
     * @param signingOutput
     * @return verify output
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws CertificateException
     */
    public VerifySigningOutput verifySigning (File file, SigningOutput signingOutput)
            throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeyException,
            SignatureException, CertificateException {
        VerifySigningOutput verifySigningOutput = new VerifySigningOutput(file.getName(), signingOutput.author, signingOutput.createdAt);

        String fileDigest = DigestService.getInstance().computeIndividualDigest(new FileInputStream(file));
        if (!this.isIndividualDigestPartOfMasterDigest(signingOutput.masterDigest, fileDigest))
            return verifySigningOutput;
        verifySigningOutput.digestValid = true;

        if (this.isCertificateChainValid(signingOutput.certificateChain))
            verifySigningOutput.certChainValid = true;

        if (Utilities.getInstance().isInternetReachable())
            verifySigningOutput.rootCertChecked = true;
        if (verifySigningOutput.rootCertChecked && this.isRootCertificateValid(signingOutput.certificateChain.get(0)))
            verifySigningOutput.rootCertValid = true;

        Signature signer = Signature.getInstance("SHA1withRSA", "BC");
        signer.initVerify(signingOutput.certificateChain.get(2).getPublicKey()); // [2] is the user certificate
        signer.update(signingOutput.masterDigest.getBytes());
        if (signer.verify(signingOutput.signature))
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
            throws NoSuchAlgorithmException, CertificateException, NoSuchProviderException, InvalidKeyException, SignatureException {
        X509Certificate rootCert    = certificateChain.get(0);
        X509Certificate intermCert  = certificateChain.get(1);
        X509Certificate userCert    = certificateChain.get(2);
        boolean userCertValid       = this.isCertificateValid(userCert, intermCert.getPublicKey());
        boolean intermCertValid     = this.isCertificateValid(intermCert, rootCert.getPublicKey());
        boolean rootCertValid       = this.isCertificateValid(rootCert, rootCert.getPublicKey());
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
    private boolean isRootCertificateValid (X509Certificate certificate) {
        try {
            URL beRootCA3CertificateURL = new URL ("http://certs.eid.belgium.be/belgiumrca3.crt");
            BufferedReader input        = new BufferedReader(new InputStreamReader(beRootCA3CertificateURL.openStream()));

            String fileAsString = new String();
            String inputLine;
            while ((inputLine = input.readLine()) != null) {
                fileAsString += inputLine;
            }
//            try {
//                PrintStream printStream = new PrintStream(this.getClass().getClassLoader().)
//            }

            input.close();
            PEMReader pemReader             = new PEMReader(input);
            X509Certificate beRootCA3Certificate = (X509Certificate) pemReader.readObject();
            System.out.println("CERT : " + beRootCA3Certificate);

            return certificate.equals(beRootCA3Certificate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
            throws ParserConfigurationException, IOException, SAXException, CertificateException, NoSuchProviderException, ParseException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(signingOutputFile);

        document.getDocumentElement().normalize();

        Element signingOutputElement    = (Element) document.getElementsByTagName("SigningOutput").item(0);
        String masterDigest             = signingOutputElement.getElementsByTagName("MasterDigest").item(0).getTextContent().toLowerCase();
        String signedBy                 = signingOutputElement.getElementsByTagName("SignedBy").item(0).getTextContent();
        Date signedAt                   = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss").parse(signingOutputElement.getElementsByTagName("SignedAt").item(0).getTextContent());
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

        return new SigningOutput(masterDigest, signature, certificateChain, signedBy, signedAt);
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
    /**
     * Returns individual files from the Signed file (.signed.zip)
     *
     * @param signedFile
     * @return a map matching files with they identity
     */
    public Map<String, File> getFiles(File signedFile) throws IOException {
        byte[] buffer = new byte[1024];
        Map<String, File> files = new HashMap<>();
        ZipInputStream zipInputStream   = new ZipInputStream(new FileInputStream(signedFile));
        ZipEntry zipEntry               = zipInputStream.getNextEntry();

        while (zipEntry != null) {
            String fileName         = zipEntry.getName();
            File newFile            = new File(signedFile.getParent()+File.separator+fileName);
            FileOutputStream fos    = new FileOutputStream(newFile);
            int len;
            while ((len = zipInputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            String fileExt = Utilities.getInstance().getFileExtension(fileName);
            if (fileExt.equalsIgnoreCase("sig")) {
                files.put("SIGNATURE", newFile);
            } else if (fileName.equals("README")) {
                files.put("README", newFile);
            } else {
                files.put("FILE", newFile);
            }
            fos.close();
            zipEntry = zipInputStream.getNextEntry();
        }

        zipInputStream.closeEntry();
        zipInputStream.close();

        return files;
    }
}
