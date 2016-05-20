package container;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import beans.notscanned.Wolf;
import beans.scanned.Bear;
import beans.scanned.Fox;
import beans.scanned.Rabbit;

public class WinterTest {
	String message;
	Winter winter;
	
	@Before
	public void setUp() {
		winter = new Winter("beans.scanned");
	}
	
	@Test
	public void testSingletonBeanCreating() {
		message = "When getSnowflake is triggered for not copied bean more then once, "
				+ "then it should return the same object";
		
		Bear beanUnderTest = winter.getSnowflake("bearFlake");
		
		assertNotNull(message, beanUnderTest);
		assertSame(message, beanUnderTest, winter.getSnowflake("bearFlake"));
	}
	
	@Test
	public void testCopiedBeanCreating() {
		message = "When getSnowflake is triggered for copied bean, "
				+ "then it should return new instance of the object each time";
		
		Fox beanUnderTest = winter.getSnowflake("foxFlake");
		
		assertNotNull(message, beanUnderTest);
		assertNotSame(message, beanUnderTest, winter.getSnowflake("foxFlake"));
	}
	
	@Test
	public void testDeinedBeanCreating() {
		message = "When getSnowflake is triggered for denied bean, "
				+ "then it should return null";
		
		Rabbit beanUnderTest = winter.getSnowflake("rabbitFlake");
		
		assertNull(message, beanUnderTest);
	}
	
	@Test
	public void testOtherPackageBeanCreating() {
		message = "When getSnowflake is triggered for bean from the other package, "
				+ "then it should return null";
		
		Wolf beanUnderTest = winter.getSnowflake("wolfFlake");
		
		assertNull(message, beanUnderTest);
	}
	
	@Test
	public void testAddSnowflakes() {
		message = "When addSnowflakes is triggered with existing package, "
				+ "then it should work with new package";
		
		winter.addSnowflakes("beans.notscanned");
		
		assertNotNull(message, winter.getSnowflake("wolfFlake"));
		assertNull(message, winter.getSnowflake("foxFlake"));
	}
}
