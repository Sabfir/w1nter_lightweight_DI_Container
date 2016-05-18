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
    private List<ClassProperty> annotatedClassProperty = new ArrayList<>();
    private Map<Class, Object> objectPool = new HashMap<>();


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

    public <T>T getSnowflake(String beanName) {
        if (beanName.isEmpty()) {
            //TODO exception
        }
        try {
            ClassProperty classInfo = getClassByBeanName(beanName);
            Class clazz = classInfo.getClazz();
            if (classInfo.isDenied()) {
                throw new BeanCreationDeniedException("Creating instance of " + beanName + " forbidden. See info about " + Denied.class.toString());
            }
            if (!classInfo.isCopied()) {
                return (T)objectPool.get(clazz);
            } else {
                Constructor defaultConstructor = clazz.getDeclaredConstructor();
                defaultConstructor.setAccessible(true);
                return (T)defaultConstructor.newInstance();
            }
        } catch (BeanNotFound | BeanCreationDeniedException e){
            //TODO logging e.getMessage()
        } catch (NoSuchMethodException| InvocationTargetException | InstantiationException | IllegalAccessException e) {
            //TODO logging e.printStackTrace(); problem with constr
        }
        return (T)null;
    }

    public ClassProperty getClassByBeanName(String beanName) throws BeanNotFound {
        for (ClassProperty classInfo : annotatedClassProperty) {
            if (classInfo.getBeanName().equalsIgnoreCase(beanName)) {
                return classInfo;
            }
        }
        throw new BeanNotFound("Doesn\'t find class by alias: " + beanName);
    }

    public Winter(String packageName) {
        annotatedClassProperty = getAnnotatedClasses(packageName, SnowFlake.class);
        initializeContainer();
        publishClassInfo();
    }

    public void initializeContainer(){
        if (!objectPool.isEmpty()) return;
        for (ClassProperty classInfo : annotatedClassProperty) {
            if (!classInfo.isCopied()) continue;
            try {
                Class clazz = classInfo.getClazz();
                Constructor defaultConstructor = clazz.getDeclaredConstructor();
                defaultConstructor.setAccessible(true);
                Object newInstance = defaultConstructor.newInstance();
                objectPool.put(clazz, newInstance);
            } catch (NoSuchMethodException e) {
                //TODO logging e.printStackTrace(); not exist def. constr.
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                //TODO logging e.printStackTrace(); problem with constr
            }
        }
    }

    public void addSnowflakes(String packageName) {
        annotatedClassProperty = getAnnotatedClasses(packageName, SnowFlake.class);
        initializeContainer();
        publishClassInfo();
    }
}
