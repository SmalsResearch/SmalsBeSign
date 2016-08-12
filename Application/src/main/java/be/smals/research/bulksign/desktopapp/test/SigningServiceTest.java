package be.smals.research.bulksign.desktopapp.test;

import be.smals.research.bulksign.desktopapp.services.SigningService;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import org.testng.Assert;
import org.testng.annotations.Test;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateEncodingException;

/**
 * Created by cea on 03/08/2016.
 */
public class SigningServiceTest {

    private SigningService signingService;

    public SigningServiceTest () {}

    @Test public void saveSigningOutputTest () throws URISyntaxException {
        byte[] signature    = "this is the signature".getBytes();
        String masterDigest = "this is the masterDigest";
        SigningOutput signingOutput = new SigningOutput(masterDigest, signature, null);
        URL fileURL         = getClass().getClassLoader().getResource("testFiles/file.sig");
        String filePath     = fileURL.getPath();
        URI fileURI         = fileURL.toURI();
        Path path           = Paths.get(fileURI);
        try {
            Files.deleteIfExists(path);
            signingService = new SigningService();
            signingService.saveSigningOutput(signingOutput, filePath);

            Assert.assertTrue(Files.exists(path));

        } catch (IOException|PKCS11Exception|ParserConfigurationException|TransformerException|CertificateEncodingException e) {
            e.printStackTrace();
        }
    }
    @Test public void signFilesTest () {
        FileInputStream[] filesToSign = new FileInputStream[5];
        try {
            filesToSign[0] = new FileInputStream(new File(getClass().getClassLoader().getResource("testFiles/file.txt").getPath()));
            filesToSign[1] = new FileInputStream(new File(getClass().getClassLoader().getResource("testFiles/file.txt").getPath()));
            filesToSign[2] = new FileInputStream(new File(getClass().getClassLoader().getResource("testFiles/file.txt").getPath()));
            filesToSign[3] = new FileInputStream(new File(getClass().getClassLoader().getResource("testFiles/file.txt").getPath()));
            filesToSign[4] = new FileInputStream(new File(getClass().getClassLoader().getResource("testFiles/file.txt").getPath()));

            byte[] signature = this.signingService.signWithMock(filesToSign);

            Assert.assertNotNull(signature);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
