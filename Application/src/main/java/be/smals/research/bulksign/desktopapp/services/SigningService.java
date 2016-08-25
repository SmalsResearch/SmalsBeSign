package be.smals.research.bulksign.desktopapp.services;

import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import be.smals.research.bulksign.desktopapp.utilities.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Collection of methods used to sign and save the output
 */
public class SigningService {

    private String masterDigest;

    /**
     * Constructor
     */
    public SigningService() throws IOException, PKCS11Exception {}

    /**
     * Computes the digest of given files and sign them
     *
     * @param inputStreams files to sign
     * @return the signature
     */
    public byte[] signWithMock (InputStream[] inputStreams) {
        byte[] signErrorOutput = new byte[0];

        try {
            try {
                this.masterDigest = DigestService.getInstance().computeMasterDigest(inputStreams);
                byte[] signature;

                MockSigningService.getInstance().initSign(this.masterDigest);
                signature = MockSigningService.getInstance().sign();

                return (signature);
            } catch (Exception e) {
                System.out.println("[Catch] Exception: " + e.getMessage());
                return (signErrorOutput);
            }
        } catch (Exception e) {
            System.out.println("[Catch] Exception: " + e.getMessage());
            return (signErrorOutput);
        }
    }
    /**
     * Sign the MasterDigest with an eID
     *
     * @return the signature
     */
    public byte[] signWithEID(String masterDigest) {
        try {
            this.masterDigest = masterDigest;
            return EIDService.getInstance().signWithBeID(Utilities.getInstance().getSha1(this.masterDigest.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
    /**
     * Turns a signing output into a signed zip (.signed.zip)
     *
     * @param signingOutput the result of a signing process
     * @param filePath saving path
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws CertificateEncodingException
     */
    public void saveSigningOutput(List<File> files, SigningOutput signingOutput, String filePath)
            throws IOException, ParserConfigurationException, TransformerException, CertificateEncodingException {
        this.createSignatureFile(signingOutput, filePath);
        String destinationDir = (new File(filePath)).getParent();
        for (File file: files) {
            this.createIndividualZipOutput(file, filePath, destinationDir);
        }
        Files.deleteIfExists(new File(filePath).toPath());
    }
    /**
     * Creates the signature file (.sig) from signing output
     *
     * @param signingOutput data to be saved
     * @param filePath .sig file path
     * @throws ParserConfigurationException
     * @throws CertificateEncodingException
     * @throws TransformerException
     */
    private void createSignatureFile(SigningOutput signingOutput, String filePath)
            throws ParserConfigurationException, CertificateEncodingException, TransformerException {
        // XML - Create
        DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder         =  factory.newDocumentBuilder();

        // Root element
        Document document = builder.newDocument();
        document.setXmlVersion("1.1");
        Element rootElement = document.createElement("SigningOutput");
        document.appendChild(rootElement);
        // Signed by
        Element signedByDigestElement = document.createElement("SignedBy");
        signedByDigestElement.appendChild(document.createTextNode(signingOutput.author));
        rootElement.appendChild(signedByDigestElement);
        // Signed at
        Element signedAtDigestElement = document.createElement("SignedAt");
        signedAtDigestElement.appendChild(document.createTextNode(new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss").format(signingOutput.createdAt)));
        rootElement.appendChild(signedAtDigestElement);
        // MasterDigest
        Element masterDigestElement = document.createElement("MasterDigest");
        masterDigestElement.appendChild(document.createTextNode(signingOutput.masterDigest == null ? this.masterDigest.toUpperCase() : signingOutput.masterDigest.toUpperCase()));
        rootElement.appendChild(masterDigestElement);
        // Signature
        Element signatureElement = document.createElement("Signature");
        signatureElement.appendChild(document.createTextNode(DatatypeConverter.printHexBinary(signingOutput.signature)));
        rootElement.appendChild(signatureElement);
        // Certificate
        if (signingOutput.certificateChain != null) {
            Element certificateChainElement = this.createCertificateXMLElement(signingOutput.certificateChain, document);
            rootElement.appendChild(certificateChainElement);
        }
        // XML - Write
        this.writeXMLDocument(filePath, document);
    }
    /**
     * Creates a signed file (.signed.zip) containing the original file, the signature file and a readme file
     *
     * @param originalFile original file
     * @param sigFilePath signature file path
     * @param dirPath where to save the signed file
     * @throws IOException
     */
    private void createIndividualZipOutput (File originalFile, String sigFilePath, String dirPath) throws IOException {
        FileOutputStream zipFOS = new FileOutputStream(dirPath +File.separator+ originalFile.getName() + ".signed.zip");
        ZipOutputStream outputStream = new ZipOutputStream(zipFOS);

        // Entry 1 - Original file
        this.addFileToZIP(originalFile, outputStream);
        // Entry 2 - SignatureFile
        this.addFileToZIP(new File(sigFilePath), outputStream);
        // Entry 3 - README
        this.addResourceFileToZIP("README.txt",this.getClass().getClassLoader().getResourceAsStream("files/README.txt"), outputStream);
        outputStream.closeEntry();
        outputStream.close();
    }
    /**
     * Adds a file to a ZIP archive
     *
     * @param file file to add
     * @param zipOutputStream
     * @throws IOException
     */
    private void addFileToZIP(File file, ZipOutputStream zipOutputStream) throws IOException {
        byte[] buffer = new byte[1024];

        ZipEntry entry = new ZipEntry(file.getName());
        zipOutputStream.putNextEntry(entry);

        FileInputStream in  = new FileInputStream(file);
        int len;
        while ((len = in.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, len);
        }
        in.close();
    }
    /**
     * Adds a resource file to a ZIP archive
     *
     * @param filename the file name
     * @param fileStream file to add
     * @param zipOutputStream
     * @throws IOException
     */
    private void addResourceFileToZIP(String filename, InputStream fileStream, ZipOutputStream zipOutputStream) throws IOException {
        byte[] buffer = new byte[1024];

        ZipEntry entry = new ZipEntry(filename);
        zipOutputStream.putNextEntry(entry);

        File tempFile = File.createTempFile(filename, ".file");
        Files.copy(fileStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        FileInputStream in  = new FileInputStream(tempFile);
        int len;
        while ((len = in.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, len);
        }
        in.close();
    }
    /**
     * Creates the "Certificate" and his children nodes
     *
     * @param certificateChain the object to turn into xml node
     * @param document the xml document
     * @return the created element
     * @throws CertificateEncodingException when it's unable to get the encoded version of an X509Certificate
     */
    private Element createCertificateXMLElement(List<X509Certificate> certificateChain, Document document)
            throws CertificateEncodingException {
        Element certificateElement              = document.createElement("Certificate");
        Element rootCertificateElement          = document.createElement("Root");
        Element intermediateCertificateElement  = document.createElement("Intermediate");
        Element userCertificateElement          = document.createElement("User");
        byte[] encodedCertificate               = certificateChain.get(0).getEncoded();
        Text userCertificateElementContent      = document.createTextNode(DatatypeConverter.printHexBinary(encodedCertificate));
        userCertificateElement.appendChild(userCertificateElementContent);
        encodedCertificate                      = certificateChain.get(1).getEncoded();
        Text intermediateCertificateElementContent = document.createTextNode(DatatypeConverter.printHexBinary(encodedCertificate));
        intermediateCertificateElement.appendChild(intermediateCertificateElementContent);
        encodedCertificate                      = certificateChain.get(2).getEncoded();
        Text rootCertificateElementContent      = document.createTextNode(DatatypeConverter.printHexBinary(encodedCertificate));
        rootCertificateElement.appendChild(rootCertificateElementContent);

        certificateElement.appendChild(userCertificateElement);
        certificateElement.appendChild(intermediateCertificateElement);
        certificateElement.appendChild(rootCertificateElement);
        return certificateElement;
    }
    /**
     * Writes the XML Document to file
     *
     * @param filePath file path
     * @param document the document to write
     * @throws TransformerException mainly when the save failed
     */
    private void writeXMLDocument(String filePath, Document document) throws TransformerException {
        TransformerFactory transformerFactory   = TransformerFactory.newInstance();
        Transformer transformer                 = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source                        = new DOMSource(document);
        StreamResult result                     = new StreamResult(new File(filePath));
        transformer.transform (source, result);
    }

}
               