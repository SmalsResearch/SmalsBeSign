package be.smals.research.bulksign.desktopapp.test;

import be.smals.research.bulksign.desktopapp.services.SigningService;
import org.testng.Assert;
import org.testng.annotations.Test;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by cea on 03/08/2016.
 */
public class SigningServiceTest {

    private SigningService signingService;

    public SigningServiceTest () {

    }

    @Test public void saveSigningOutputTest () throws URISyntaxException {

    }
    @Test public void signFilesTest () {
        try {
            signingService = new SigningService();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PKCS11Exception e) {
            e.printStackTrace();
        }
        FileInputStream[] filesToSign = new FileInputStream[5];
        try {
            filesToSign[0] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            filesToSign[1] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            filesToSign[2] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            filesToSign[3] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            filesToSign[4] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));

            byte[] signature = this.signingService.signWithMock(filesToSign);

            Assert.assertNotNull(signature);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
