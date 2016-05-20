/**
* Domain classes used to create and manage container
* <p>
* These classes contain the Winter functionality
* </p>
*
* @since 1.0
* @author Alex Pinta, Oleh Pinta
* @version 1.0
*/
package container;

import container.annotation.Denied;
import container.annotation.Report;
import container.annotation.SnowFlake;
import exception.BeanCreationDeniedException;
import exception.BeanNotFound;
import helper.FileHelper;

import org.apache.log4j.Logger;
import helper.ReflectionDecorator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* The Winter class implements DI Container like lightweight DI in Spring Framework.
* You can initialize Winter container by creating new instance of this class
* and the use it creating new instances of the classes in the container
*
* @author  Alex Pinta, Oleh Pinta
*/
public class Winter {
    private String packageName;
    private List<ReflectionDecorator.ClassProperty> annotatedClassProperty = new ArrayList<>();
    private Map<Class, Object> objectPool = new HashMap<>();
    private static Logger logger = Logger.getRootLogger();
    
    public Winter() {
    }

    public Winter(String packageName) {
    	this.packageName = packageName;
    	
        initializeContainer();
        publishClassInfo();
    }
    
    public void addSnowflakes(String packageName) {
    	this.packageName = packageName;
    	
        initializeContainer();
        publishClassInfo();
    }

    /**
     * This method is used to create in instance of the annotated class by bean name.
     * @param beanName. Using this string method find the class in annotatedClassProperty.
     * If it exists, then it create an instance of this class
     */
    public <T>T getSnowflake(final String beanName) {
        if (beanName == null || beanName.isEmpty()) {
            logger.info("Empty name of bean not allowed");
        }
        
        T snowFlake = null;
        
        try {
            ReflectionDecorator.ClassProperty classInfo = getClassByBeanName(beanName);
            Class clazz = classInfo.getClazz();
            
            if (classInfo.isDenied()) {
                throw new BeanCreationDeniedException("Creating instance of " + beanName + " forbidden. See info about "
                        + Denied.class.toString());
            }
            
            if (!classInfo.isCopied()) {
            	snowFlake = (T)objectPool.get(clazz);
            	
            	if (null == snowFlake) {
            		snowFlake = createInstanceByClass(clazz);
            		objectPool.put(clazz, snowFlake);
            	}
            } else {
            	snowFlake = createInstanceByClass(clazz);
            }
        } catch (BeanNotFound e) {
            logger.info(e.getMessage(), e);
        } catch (BeanCreationDeniedException e) {
            logger.info(e.getMessage(), e);
        }
        
        return snowFlake;
    }
    
	/**
	 * This method is used to find class property in annotatedClassProperty list.
	 * @param beanName. Using this parameter method find the class in annotatedClassProperty and return it
	 */
    public ReflectionDecorator.ClassProperty getClassByBeanName(final String beanName) throws BeanNotFound {
        for (ReflectionDecorator.ClassProperty classInfo : annotatedClassProperty) {
            if (classInfo.getBeanName().equalsIgnoreCase(beanName)) {
                return classInfo;
            }
        }
        throw new BeanNotFound("Doesn\'t find class by alias: " + beanName);
    }

    /**
	 * This method is used to create object of given class.
	 * @param clazz. This is the class which instance we want to create
	 */
    private <T> T createInstanceByClass(Class clazz) {
        Constructor defaultConstructor = null;
        T newInstance = null;
        try {
            defaultConstructor = clazz.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            newInstance = (T)defaultConstructor.newInstance();
        } catch (Exception e) {
            logger.info("Instance of class: " + clazz.toString() + " didn\'t create due to problem with constructor", e);
        }
        return newInstance;
    }

    /**
	 * This method is used to create a Report and save it on your local machine.
	 */
    private void publishClassInfo(){
        for (ReflectionDecorator.ClassProperty classInfo : annotatedClassProperty) {
            Class scannedClass = classInfo.getClazz();
            Report report = (Report)scannedClass.getAnnotation(Report.class);
            if (report!= null) {
            	String filepath = report.path();
            	String fullpath = filepath + "/" + classInfo.getBeanName() + ".txt";
            	
            	if (FileHelper.createFileByFullpath(fullpath)) {
            		FileHelper.addLineToFile(fullpath, "Class: " + classInfo.getClazz());
            		FileHelper.addLineToFile(fullpath, "Copied: " + classInfo.isCopied());
            		FileHelper.addLineToFile(fullpath, "Danied: " + classInfo.isDenied());
            		
            		FileHelper.addLineToFile(fullpath, "Fields:");
            		Field [] fields = scannedClass.getDeclaredFields();
            		for (Field field : fields) {
            			FileHelper.addLineToFile(fullpath, "\t" + field.getName() + " (" + field.getType() + ")");
					}
            		FileHelper.addLineToFile(fullpath, "Methods:");
            		Method[] methods = scannedClass.getDeclaredMethods();
            		for (Method method : methods) {
            			FileHelper.addLineToFile(fullpath, "\t" + method.getName() + " (" + method.getReturnType() + ")");
					}
                    FileHelper.addLineToFile(fullpath, "Annotations:");
                    Annotation[] annotations = scannedClass.getAnnotations();
                    for (Annotation annotation : annotations) {
                        FileHelper.addLineToFile(fullpath, "\t" + annotation.toString() + " (" + annotation.annotationType() + ")");
                    }
            	}
            }
        }
    }

    /**
     * This method is used to initialize Winter container instance.
     * It fills all annotated class in the package packageName
     */
    private void initializeContainer(){
        if (!objectPool.isEmpty()) {
        	objectPool.clear();
        }
        
        annotatedClassProperty = ReflectionDecorator.getAnnotatedClasses(this.packageName, SnowFlake.class);
    }

    public String getPackageName() {
        return packageName;
    }
}
