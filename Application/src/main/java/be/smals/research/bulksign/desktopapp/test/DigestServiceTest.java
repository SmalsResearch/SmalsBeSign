package be.smals.research.bulksign.desktopapp.test;

import be.smals.research.bulksign.desktopapp.services.DigestService;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Created by cea on 04/08/2016.
 */
public class DigestServiceTest {

    @Test public void getInstanceNotNullTest () {
        Assert.assertNotNull(DigestService.getInstance());
    }
    @Test public void computeIndividualDigestNotNullTest () {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            String digest = DigestService.getInstance().computeIndividualDigest(fileInputStream);

            Assert.assertNotNull(digest);
        } catch (NoSuchAlgorithmException|NoSuchProviderException|IOException e) {
            e.printStackTrace();
        }
    }
    @Test public void computeIndividualDigestLengthTest () {
        final int digestLength = 64;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            String digest = DigestService.getInstance().computeIndividualDigest(fileInputStream);

            Assert.assertEquals(digest.length(), digestLength);
        } catch (NoSuchAlgorithmException|NoSuchProviderException|IOException e) {
            e.printStackTrace();
        }
    }
    @Test public void computeMasterDigestNotNullTest () {
        try {
            FileInputStream[] files = new FileInputStream[5];
            files[0] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            files[1] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            files[2] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            files[3] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            files[4] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));

            String masterDigest = DigestService.getInstance().computeMasterDigest (files);

            Assert.assertNotNull(masterDigest);
        } catch (NoSuchAlgorithmException|NoSuchProviderException|IOException e) {
            e.printStackTrace();
        }
    }
    @Test public void computeMasterDigestLengthTest () {
        final int digestLength = 64;
        try {
            FileInputStream[] files = new FileInputStream[5];
            files[0] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            files[1] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            files[2] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            files[3] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));
            files[4] = new FileInputStream(new File(getClass().getClassLoader().getResource("files/file.txt").getPath()));

            String masterDigest = DigestService.getInstance().computeMasterDigest (files);

            Assert.assertEquals(masterDigest.length(), digestLength*files.length);
        } catch (NoSuchAlgorithmException|NoSuchProviderException|IOException e) {
            e.printStackTrace();
        }
    }
}
