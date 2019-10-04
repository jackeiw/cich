package cnki.cord.zgj.cord.portal;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class HomeControllerTest {

    @Test
    public void getConfig() {
        Assert.assertEquals("test", new HomeController().getConfig());
    }
}