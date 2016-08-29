package be.smals.research.bulksign.desktopapp.test;

import be.smals.research.bulksign.desktopapp.services.VerifySigningService;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by cea on 29/08/2016.
 */
public class VerifyServiceTest {
    private final VerifySigningService verifySigningService = new VerifySigningService();

    @Test public void verifySigningTest () {
        File signedPdfFile  = new File(getClass().getClassLoader().getResource("files/signedPdf.pdf").getPath());
        File signatureFile  = new File(getClass().getClassLoader().getResource("files/SignatureFile.pdf").getPath());

//        SigningOutput signature = verifySigningService.getSigningOutput()
    }
}
