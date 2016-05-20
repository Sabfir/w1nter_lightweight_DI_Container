package helper;

import beans.scanned.Bear;
import beans.scanned.Fox;
import beans.scanned.NotAnnotated;
import beans.scanned.Rabbit;
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
        Class[] selectedClasses = {Bear.class, Fox.class, Rabbit.class};
    	message = "When GetAnnotatedClasses is triggered, "
				+ "then it should return the list of all annotated classes";
		
    	List<ClassProperty> classProperties = ReflectionDecorator.getAnnotatedClasses("beans.scanned", SnowFlake.class);
    	
    	assertEquals(message, selectedClasses.length, classProperties.size());
    }

    @Test
    public void testGetClasses() throws Exception {
        Class[] selectedClasses = {Bear.class, Fox.class, Rabbit.class, NotAnnotated.class};
        message = "When GetAnnotatedClasses is triggered, "
				+ "then it should return the list of all annotated classes";
		
    	List<Class> classes = ReflectionDecorator.getClasses("beans.scanned");
    	
    	assertEquals(message, selectedClasses.length, classes.size());
    }
}