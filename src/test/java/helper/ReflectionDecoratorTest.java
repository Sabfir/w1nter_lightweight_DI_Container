package helper;

import helper.ReflectionDecorator.ClassProperty;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import container.annotation.SnowFlake;
import static org.junit.Assert.*;

public class ReflectionDecoratorTest {
	String message;
	
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetAnnotatedClasses() throws Exception {
    	message = "When GetAnnotatedClasses is triggered, "
				+ "then it should return the list of all annotated classes";
		
    	List<ClassProperty> classProperties = ReflectionDecorator.getAnnotatedClasses("beans.scanned", SnowFlake.class);
    	
    	assertEquals(message, 3, classProperties.size());
    }

    @Test
    public void testGetClasses() throws Exception {
    	message = "When GetAnnotatedClasses is triggered, "
				+ "then it should return the list of all annotated classes";
		
    	List<Class> classes = ReflectionDecorator.getClasses("beans.scanned");
    	
    	assertEquals(message, 4, classes.size());
    }
}