package container;

import container.annotation.Denied;
import container.annotation.Report;
import container.annotation.SnowFlake;
import exception.BeanCreationDeniedException;
import exception.BeanNotFound;
import helper.FileHelper;
import helper.Log4j2Wrapper;

import org.apache.logging.log4j.Logger;

import static helper.ReflectionDecorator.*;

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
    private List<ClassProperty> annotatedClassProperty = new ArrayList<>();
    private Map<Class, Object> objectPool = new HashMap<>();
    private static final Logger LOGGER = Log4j2Wrapper.getLogger(Winter.class.toString());
    
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

    public <T>T getSnowflake(String beanName) {
        if (beanName == null || beanName.isEmpty()) {
            LOGGER.info(Log4j2Wrapper.MARKER_FLOW, "Empty name of bean not allowed");
        }
        
        T snowFlake = null;
        
        try {
            ClassProperty classInfo = getClassByBeanName(beanName);
            Class clazz = classInfo.getClazz();
            
            if (classInfo.isDenied()) {
                throw new BeanCreationDeniedException("Creating instance of " + beanName + " forbidden. See info about " + Denied.class.toString());
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
        } catch (BeanNotFound e){
            LOGGER.info(Log4j2Wrapper.MARKER_FLOW, e.getMessage(), e);
        } catch (BeanCreationDeniedException e) {
            LOGGER.info(Log4j2Wrapper.MARKER_FLOW, e.getMessage(), e);
        }
        
        return snowFlake;
    }
    
    public ClassProperty getClassByBeanName(String beanName) throws BeanNotFound {
        for (ClassProperty classInfo : annotatedClassProperty) {
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
            LOGGER.info(Log4j2Wrapper.MARKER_EXCEPTION, "Instance of class: " + clazz.toString() + " didn\'t create due to problem with constructor", e);
        }
        return newInstance;
    }

    private void publishClassInfo(){
        for (ClassProperty classInfo : annotatedClassProperty) {
            Class scannedClass = classInfo.getClazz();
            Annotation report = scannedClass.getAnnotation(Report.class);
            if (report!= null) {
            	String filepath = ((Report)report).path();
            	String fullpath = filepath + "/" + classInfo.getBeanName() + ".txt";
            	
            	if (FileHelper.createFileByFullpath(fullpath)) {
            		FileHelper.addLineToFile(fullpath, "Class: " + classInfo.getClazz());
            		FileHelper.addLineToFile(fullpath, "Copied: " + classInfo.isCopied());
            		FileHelper.addLineToFile(fullpath, "Danied: " + classInfo.isDenied());
            		
            		FileHelper.addLineToFile(fullpath, "Fields:");
            		Field [] fields = scannedClass.getDeclaredFields();
            		for (Field field : fields) {
            			FileHelper.addLineToFile(fullpath, "  --- " + field.getName() + " (" + field.getType() + ")");
					}
            		FileHelper.addLineToFile(fullpath, "Methods:");
            		Method[] methods = scannedClass.getDeclaredMethods();
            		for (Method method : methods) {
            			FileHelper.addLineToFile(fullpath, "  --- " + method.getName() + " (" + method.getReturnType() + ")");
					}
            	}
            }
        }
    }

    private void initializeContainer(){
        if (!objectPool.isEmpty()) {
        	objectPool.clear();
        }
        
        annotatedClassProperty = getAnnotatedClasses(packageName, SnowFlake.class);
    }
}
