package be.smals.research.bulksign.desktopapp.test;

import be.smals.research.bulksign.desktopapp.services.SigningService;
import be.smals.research.bulksign.desktopapp.utilities.SigningOutput;
import org.testng.Assert;
import org.testng.annotations.Test;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by cea on 03/08/2016.
 */
public class SigningServiceTest {

    private SigningService signingService;

    public SigningServiceTest () {}

    @Test public void saveSigningOutputTest () {
        byte[] signature = "this is the signature".getBytes();
        String masterDigest = "this is the masterDigest";
        SigningOutput signingOutput = new SigningOutput(masterDigest, signature);
        String filePath = getClass().getClassLoader().getResource("testFiles/file.sig").getPath();

        try {
            Files.delete(new File(filePath).toPath());
            signingService = new SigningService();
            signingService.saveSigningOutput(signingOutput, filePath);

            Assert.assertTrue(new File(filePath).exists());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PKCS11Exception e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
