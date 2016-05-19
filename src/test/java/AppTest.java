import static org.junit.Assert.*;
import beans.scanned.Fox;
import container.Winter;

import org.junit.Before;
import org.junit.Test;

public class AppTest {
    Winter winter;
    @Before public void setUp(){
        winter = new Winter("bean");
    }
    @Test public void getInstanceByBeanName(){
        Fox foxFlake = winter.getSnowflake("foxFlake");
    }
}
