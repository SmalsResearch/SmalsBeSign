package be.smals.research.bulksign.desktopapp.services;

import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockKeyService extends KeyService {

    private static MockKeyService instance = new MockKeyService();
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private List<X509Certificate> certificateChain;

    private MockKeyService () {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        this.certificateChain = new ArrayList<>();
        this.generateKeys ();
        try {
            this.generateCertificate ();
            this.generateChainCertificate ();

        } catch (CertificateParsingException|CertificateEncodingException|NoSuchAlgorithmException|InvalidKeyException|SignatureException|NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    private void generateKeys () {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException|NoSuchProviderException e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }
    private KeyPair generateKeyPair () {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        } catch (NoSuchAlgorithmException|NoSuchProviderException e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(2048);

        return keyPairGenerator.generateKeyPair();
    }
    private void generateChainCertificate () throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        X500Principal  subjectName          = new X500Principal("CN=Mock V3 Certificate");
        PKCS10CertificationRequest pkCR     = new PKCS10CertificationRequest("SHA1withRSA", subjectName, this.getPublicKey(), null, this.getPrivateKey());
    }
    private void generateCertificate () throws CertificateParsingException, CertificateEncodingException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        KeyPair rootKeyPair     = this.generateKeyPair();
        Date startDate          = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date endDate            = new Date();
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal  subjectName         = new X500Principal("CN=Mock ROOT");

        certGen.setSerialNumber(serialNumber);
        certGen.setIssuerDN(subjectName); // Authority set to myself
        certGen.setNotBefore(startDate);
        certGen.setNotAfter(endDate);
        certGen.setSubjectDN(subjectName);
        certGen.setPublicKey(rootKeyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA1withRSA");

        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(rootKeyPair.getPublic()));
        X509Certificate rootCertificate = certGen.generate(rootKeyPair.getPrivate(), "BC");

        // Intermediate
        KeyPair intermediateKeyPair     = this.generateKeyPair();
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(rootCertificate.getSubjectX500Principal()); // Authority set to root
        certGen.setNotBefore(startDate);
        certGen.setNotAfter(endDate);
        certGen.setSubjectDN(new X500Principal("CN=Mock INTERMEDIATE"));
        certGen.setPublicKey(intermediateKeyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA1withRSA");
        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(rootCertificate));
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(intermediateKeyPair.getPublic()));
        X509Certificate intermediateCertificate = certGen.generate(rootKeyPair.getPrivate(), "BC");

        // User
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(intermediateCertificate.getSubjectX500Principal()); // Authority set to intermediate
        certGen.setNotBefore(startDate);
        certGen.setNotAfter(endDate);
        certGen.setSubjectDN(new X500Principal("CN=Mock USER"));
        certGen.setPublicKey(this.getPublicKey());
        certGen.setSignatureAlgorithm("SHA1withRSA");
        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(intermediateCertificate));
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(this.getPublicKey()));
        X509Certificate certificate = certGen.generate(intermediateKeyPair.getPrivate(), "BC");

        this.certificateChain.add(rootCertificate);
        this.certificateChain.add(intermediateCertificate);
        this.certificateChain.add(certificate);
    }

    public static MockKeyService getInstance() {
        return instance;
    }

    @Override
    public long getKey() {
        return ByteBuffer.wrap(this.privateKey.getEncoded()).getLong();
    }
    @Override
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
    @Override
    public PublicKey getPublicKey(BigInteger modulus, BigInteger publicExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return null;
    }
    @Override
    public List<X509Certificate> getCertificateChain () {
        return this.certificateChain;
    }
}
