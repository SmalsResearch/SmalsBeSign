package be.smals.research.bulksign.desktopapp.services;

import be.smals.research.bulksign.desktopapp.utilities.Settings;
import be.smals.research.bulksign.desktopapp.utilities.Settings.Signer;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import sun.security.pkcs11.wrapper.*;

import javax.smartcardio.CardException;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;


public class SigningService {

    private PKCS11 pkcs11;
    private String masterDigest;

    /**
     * Constructor
     */
    public SigningService() throws IOException, PKCS11Exception {
        String osName = System.getProperty("os.name");

        if (osName.contains("Windows"))
            pkcs11 = PKCS11.getInstance("beidpkcs11.dll", "C_GetFunctionList", null, false);
        else
            pkcs11 = PKCS11.getInstance("libbeidpkcs11.so", "C_GetFunctionList", null, false);
    }

    public void prepareSigning () throws CardException {
        EIDService.getInstance().prepareSigning (DigestService.getInstance().getAlgorithm());
    }
    /**
     * Used to sign given files
     *
     * @param inputStreams files to sign
     * @return the signature
     */
    public byte[] sign (InputStream[] inputStreams) {
        byte[] signErrorOutput = new byte[0];

        try {
            //Open the P11 session
            long p11_session = pkcs11.C_OpenSession(0, PKCS11Constants.CKF_SERIAL_SESSION, null, null);
            try {
                this.masterDigest = DigestService.getInstance().computeMasterDigest(inputStreams);
                byte[] signature;
                if (Settings.getInstance().getSigner() == Signer.EID) {
                    System.out.println("EID SIGNER");
//                    EIDSigningService.getInstance().initSign(pkcs11, p11_session);
//                    signature = EIDSigningService.getInstance().sign(pkcs11, p11_session, this.masterDigest);
                    signature = EIDService.getInstance().sign(masterDigest);
                } else {
                    System.out.println("MOCK SIGNER");
                    MockSigningService.getInstance().initSign(this.masterDigest);
                    signature = MockSigningService.getInstance().sign();
                }

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

    public byte[] signAlt(FileInputStream[] inputFiles) {
        byte[] signErrorOutput = new byte[0];

        try {
            this.masterDigest = DigestService.getInstance().computeMasterDigest(inputFiles);
            byte[] signature;
            if (Settings.getInstance().getSigner() == Signer.EID) {
                System.out.println("EID SIGNER");
//                    EIDSigningService.getInstance().initSign(pkcs11, p11_session);
//                    signature = EIDSigningService.getInstance().sign(pkcs11, p11_session, this.masterDigest);
                signature = EIDService.getInstance().signAt(this.masterDigest.getBytes(), DigestService.getInstance().getAlgorithm());
            } else {
                System.out.println("MOCK SIGNER");
                MockSigningService.getInstance().initSign(this.masterDigest);
                signature = MockSigningService.getInstance().sign();
            }

            return (signature);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[Catch] Exception: " + e.getMessage());
            return (signErrorOutput);
        }
    }

    public void saveSigningOutput(SigningOutput signingOutput, String filePath) throws IOException, ParserConfigurationException, TransformerException, CertificateEncodingException {
        // XML - Create
        DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder         =  factory.newDocumentBuilder();

        // Root element
        Document document = builder.newDocument();
        document.setXmlVersion("1.1");
        Element rootElement = document.createElement("SigningOutput");
        document.appendChild(rootElement);
        // MasterDigest
        Element masterDigestElement = document.createElement("MasterDigest");
        masterDigestElement.appendChild(document.createTextNode(signingOutput.masterDigest == null ? this.masterDigest : signingOutput.masterDigest));
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

    private Element createCertificateXMLElement(List<X509Certificate> certificateChain, Document document) throws CertificateEncodingException {
        Element certificateElement              = document.createElement("Certificate");
        Element rootCertificateElement          = document.createElement("Root");
        Element intermediateCertificateElement  = document.createElement("Intermediate");
        Element userCertificateElement          = document.createElement("User");
        byte[] encodedCertificate               = certificateChain.get(0).getEncoded();
        Text rootCertificateElementContent      = document.createTextNode(DatatypeConverter.printHexBinary(encodedCertificate));
        rootCertificateElement.appendChild(rootCertificateElementContent);
        encodedCertificate                      = certificateChain.get(1).getEncoded();
        Text intermediateCertificateElementContent = document.createTextNode(DatatypeConverter.printHexBinary(encodedCertificate));
        intermediateCertificateElement.appendChild(intermediateCertificateElementContent);
        encodedCertificate                      = certificateChain.get(2).getEncoded();
        Text userCertificateElementContent      = document.createTextNode(DatatypeConverter.printHexBinary(encodedCertificate));
        userCertificateElement.appendChild(userCertificateElementContent);
        certificateElement.appendChild(rootCertificateElement);
        certificateElement.appendChild(intermediateCertificateElement);
        certificateElement.appendChild(userCertificateElement);
        return certificateElement;
    }

    private void writeXMLDocument(String filePath, Document document) throws TransformerException {
        TransformerFactory transformerFactory   = TransformerFactory.newInstance();
        Transformer transformer                 = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source                        = new DOMSource(document);
        StreamResult result                     = new StreamResult(new File(filePath));
        transformer.transform(source, result);
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
               