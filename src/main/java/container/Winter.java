package container;

import container.annotation.Denied;
import container.annotation.Report;
import container.annotation.SnowFlake;
import exception.BeanCreationDeniedException;
import exception.BeanNotFound;
import static helper.ReflectionDecorator.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Winter {
	private String packageName;
    private List<ClassProperty> annotatedClassProperty = new ArrayList<>();
    private Map<Class, Object> objectPool = new HashMap<>();

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
            //TODO exception
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
        } catch (BeanNotFound | BeanCreationDeniedException e){
            //TODO logging e.getMessage()
        } catch (NoSuchMethodException| InvocationTargetException | InstantiationException | IllegalAccessException e) {
            //TODO logging e.printStackTrace(); problem with constr
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

    private <T> T createInstanceByClass(Class clazz) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	Constructor defaultConstructor = clazz.getDeclaredConstructor();
        defaultConstructor.setAccessible(true);
        
        return (T)defaultConstructor.newInstance();
	}

    private void publishClassInfo(){
        for (ClassProperty classInfo : annotatedClassProperty) {
            //TODO
            Class scannedClass = classInfo.getClazz();
            Report report = (Report)scannedClass.getAnnotation(Report.class);
            if (report!= null) {
                //TODO report.path()
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
