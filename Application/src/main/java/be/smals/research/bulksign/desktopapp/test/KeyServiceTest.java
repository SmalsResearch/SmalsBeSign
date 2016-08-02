package be.smals.research.bulksign.desktopapp.test;

import be.smals.research.bulksign.desktopapp.services.KeyService;
import be.smals.research.bulksign.desktopapp.services.MockKeyService;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by cea on 01/08/2016.
 */
public class KeyServiceTest {

    @Test
    public void inheritanceTest () {
        Assert.assertTrue(MockKeyService.getInstance() instanceof KeyService);
    }
    @Test
    public void getKeyTest () {
        Assert.assertNotNull(MockKeyService.getInstance().getKey());
    }
}
