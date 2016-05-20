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
    
    public ReflectionDecorator.ClassProperty getClassByBeanName(final String beanName) throws BeanNotFound {
        for (ReflectionDecorator.ClassProperty classInfo : annotatedClassProperty) {
            if (classInfo.getBeanName().equalsIgnoreCase(beanName)) {
                return classInfo;
            }
        }
        throw new BeanNotFound("Doesn\'t find class by alias: " + beanName);
    }

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
    private void publishClassInfo() {
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
