package be.smals.research.bulksign.desktopapp.services;

import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

public class MockKeyService extends KeyService {

    private static MockKeyService instance = new MockKeyService();
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private X509Certificate certificate;

    private MockKeyService () {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        this.generateKeys ();
        try {
            this.generateCertificate ();
        } catch (CertificateParsingException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
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
    private void generateCertificate () throws CertificateParsingException, CertificateEncodingException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
        Date startDate          = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date endDate            = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal  subjectName         = new X500Principal("CN=Mock V3 Certificate");

        certGen.setSerialNumber(serialNumber);
        certGen.setIssuerDN(subjectName); // Authority set to myself
        certGen.setNotBefore(startDate);
        certGen.setNotAfter(endDate);
        certGen.setSubjectDN(subjectName);
        certGen.setPublicKey(this.publicKey);
        certGen.setSignatureAlgorithm("SHA1withRSA");

        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new SubjectKeyIdentifierStructure(this.getPublicKey()));
        X509Certificate certificate = certGen.generate(this.getPrivateKey(), "BC");
        this.certificate = certificate;
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
    public X509Certificate getCertificate () {
        return this.certificate;
    }
}
