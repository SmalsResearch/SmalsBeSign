package be.smals.research.bulksign.desktopapp.test;

import be.smals.research.bulksign.desktopapp.services.KeyService;
import be.smals.research.bulksign.desktopapp.services.MockKeyService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class KeyServiceTest {

    @Test public void inheritanceTest () {
        Assert.assertTrue(MockKeyService.getInstance() instanceof KeyService);
    }
    @Test public void getKeyTest () {
        Assert.assertNotNull(MockKeyService.getInstance().getKey());
    }
    @Test public void getPrivateKeyTest () {
        Assert.assertNotNull(MockKeyService.getInstance().getPrivateKey());
    }
    @Test public void getPublicKeyTest () {
        Assert.assertNotNull(MockKeyService.getInstance().getPublicKey());
    }
}
