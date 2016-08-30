//package be.smals.research.bulksign.desktopapp.test;
//
//import be.smals.research.bulksign.desktopapp.exception.BulkSignException;
//import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
//import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
//import be.smals.research.bulksign.desktopapp.utilities.VerifySigningOutput;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.File;
//import java.io.IOException;
//import java.security.cert.CertificateException;
//import java.text.ParseException;
//
///**
// * Created by cea on 29/08/2016.
// */
//public class VerifyServiceTest {
//    private final VerifySigningService verifySigningService = new VerifySigningService();
//
//    public VerifyServiceTest () {
//        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//    }
//    /*
//    ---------- Normal cases : Everything is legit ----------------------------------------------------------------------
//     */
//    @Test public void verifyCertificatesNormalCaseTest () throws SAXException, ParseException, IOException, NoSuchProviderException, BulkSignException, ParserConfigurationException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, NoSuchAlgorithmException {
//        File signatureFile  = new File(getClass().getClassLoader().getResource("files/SignatureFile.sig").getPath());
//        SigningOutput signature = verifySigningService.getSigningOutput(signatureFile);
//        VerifySigningOutput verifySigningOutput = verifySigningService.verifyCertificates(signature.certificateChain, new VerifySigningOutput());
//
//        Assert.assertTrue(verifySigningOutput.getCertificateResult().equals(VerifySigningOutput.VerifyResult.OK)
//                || verifySigningOutput.getCertificateResult().equals(VerifySigningOutput.VerifyResult.WARNING));
//    }
//    @Test public void verifySigningNormalCaseTest () throws SAXException, ParseException, IOException, NoSuchProviderException, BulkSignException, ParserConfigurationException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
//        File signedPdfFile  = new File(getClass().getClassLoader().getResource("files/signedPdf.pdf").getPath());
//        File signatureFile  = new File(getClass().getClassLoader().getResource("files/SignatureFile.sig").getPath());
//
//        SigningOutput signature = verifySigningService.getSigningOutput(signatureFile);
//        VerifySigningOutput output = verifySigningService.verifySigning(signedPdfFile, signature);
//
//        Assert.assertTrue(output.getOutputResult().equals(VerifySigningOutput.VerifyResult.OK)
//                || output.getOutputResult().equals(VerifySigningOutput.VerifyResult.WARNING));
//    }
//    /*
//    ---------- SigningOutput -------------------------------------------------------------------------------------------
//     */
//    /**
//     * SigningOutput case 01 : Empty signature file
//     *
//     * Expect : BulkSignException with error message
//     */
//    @Test(expectedExceptions = BulkSignException.class)
//    public void signingOutputCase01 () throws SAXException, ParseException, IOException, NoSuchProviderException, BulkSignException, ParserConfigurationException, CertificateException {
//        File signatureFile  = new File(getClass().getClassLoader().getResource("files/SignatureFile-empty.sig").getPath());
//        verifySigningService.getSigningOutput(signatureFile);
//    }
//
//    /*
//    ---------- MasterDigest defect cases -------------------------------------------------------------------------------
//     */
//
//    /**
//     * MasterDigest case 01 : Missing node from signature file
//     *
//     * Expect : BulkSignException with error message
//     */
//    @Test(expectedExceptions = BulkSignException.class)
//    public void mdCase01 () throws SAXException, ParseException, IOException, NoSuchProviderException, BulkSignException, ParserConfigurationException, CertificateException {
//        File signatureFile =  new File(getClass().getClassLoader().getResource("files/SignatureFile-no-md-node.sig").getPath());
//        verifySigningService.getSigningOutput(signatureFile);
//    }
//    /**
//     * MasterDigest case 02 : Wrong Master Digest
//     *
//     * Expect : Digest validation - FAILED AND Result - FAILED
//     */
//    @Test(expectedExceptions = BulkSignException.class)
//    public void mdCase02 () throws SAXException, ParseException, IOException, NoSuchProviderException, BulkSignException, ParserConfigurationException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
//        File signedPdfFile  = new File(getClass().getClassLoader().getResource("files/signedPdf.pdf").getPath());
//        File signatureFile =  new File(getClass().getClassLoader().getResource("files/SignatureFile-wrong-md.sig").getPath());
//        SigningOutput signingOutput = verifySigningService.getSigningOutput(signatureFile);
//        VerifySigningOutput verifySigningOutput = verifySigningService.verifySigning(signedPdfFile, signingOutput);
//
//        Assert.assertTrue(!verifySigningOutput.digestValid
//                && verifySigningOutput.getOutputResult().equals(VerifySigningOutput.VerifyResult.FAILED));
//    }
//
//    /**
//     * MasterDigest case 03 : Shorter MD - Correct Master Digest but without last byte
//     *
//     * Expect : Digest validation - FAILED AND Result - FAILED
//     */
//    @Test(expectedExceptions = BulkSignException.class)
//    public void mdCase03 () throws SAXException, ParseException, IOException, NoSuchProviderException, BulkSignException, ParserConfigurationException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
//        File signedPdfFile  = new File(getClass().getClassLoader().getResource("files/signedPdf.pdf").getPath());
//        File signatureFile =  new File(getClass().getClassLoader().getResource("files/SignatureFile-shorter-md.sig").getPath());
//        SigningOutput signingOutput = verifySigningService.getSigningOutput(signatureFile);
//        VerifySigningOutput verifySigningOutput = verifySigningService.verifySigning(signedPdfFile, signingOutput);
//
//        Assert.assertTrue(!verifySigningOutput.digestValid
//                && verifySigningOutput.getOutputResult().equals(VerifySigningOutput.VerifyResult.FAILED));
//    }
//    /**
//     * MasterDigest case 03 : Longer MD - Correct Master Digest but with 1 more byte
//     *
//     * Expect :
//     * ----- Digest validation - OK
//     * ----- Signature validation - FAILED
//     * ----- Result - FAILED
//     */
//    @Test(expectedExceptions = BulkSignException.class)
//    public void mdCase04 () throws SAXException, ParseException, IOException, NoSuchProviderException, BulkSignException, ParserConfigurationException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
//        File signedPdfFile  = new File(getClass().getClassLoader().getResource("files/signedPdf.pdf").getPath());
//        File signatureFile =  new File(getClass().getClassLoader().getResource("files/SignatureFile-longer-md.sig").getPath());
//        SigningOutput signingOutput = verifySigningService.getSigningOutput(signatureFile);
//        VerifySigningOutput verifySigningOutput = verifySigningService.verifySigning(signedPdfFile, signingOutput);
//
//        Assert.assertTrue(verifySigningOutput.digestValid && !verifySigningOutput.signatureValid
//                && verifySigningOutput.getOutputResult().equals(VerifySigningOutput.VerifyResult.FAILED));
//    }
//
//
//
//    /*
//    ---------- Signature defect cases ----------------------------------------------------------------------------------
//     */
//    /**
//     * Signature case 01 : Missing Signature node from signature file
//     *
//     * Expect : BulkSignException with error message
//     */
//    @Test(expectedExceptions = BulkSignException.class)
//    public void signatureCase01 () throws SAXException, ParseException, IOException, NoSuchProviderException, BulkSignException, ParserConfigurationException, CertificateException {
//        File signatureFile  = new File(getClass().getClassLoader().getResource("files/SignatureFile-no-signature-node.sig").getPath());
//        verifySigningService.getSigningOutput(signatureFile);
//    }
//    /**
//     * Signature case 02 : Wrong signature
//     *
//     * Expect : Signature verification - FAILED AND Result - FAILED
//     */
//    @Test public void signatureCase02 () throws SAXException, ParseException, IOException, NoSuchProviderException, BulkSignException, ParserConfigurationException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
//        File signedPdfFile  = new File(getClass().getClassLoader().getResource("files/signedPdf.pdf").getPath());
//        File signatureFile  = new File(getClass().getClassLoader().getResource("files/SignatureFile-wrong-signature.sig").getPath());
//        SigningOutput signature = verifySigningService.getSigningOutput(signatureFile);
//        VerifySigningOutput output = verifySigningService.verifySigning(signedPdfFile, signature);
//
//        Assert.assertTrue(!output.signatureValid && output.getOutputResult().equals(VerifySigningOutput.VerifyResult.FAILED));
//    }
//}
