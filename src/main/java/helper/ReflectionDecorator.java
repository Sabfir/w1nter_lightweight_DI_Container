package helper;

import container.annotation.Copied;
import container.annotation.Denied;
import container.annotation.SnowFlake;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class ReflectionDecorator {
    private static List<ClassProperty> listAnnotatedClasses = new ArrayList<>();

    public static List<ClassProperty> getAnnotatedClasses(String packageName, Class annotationType) {
    	listAnnotatedClasses.clear();
        
    	for (Class clazz : getClasses(packageName)) {
            Annotation annotation = clazz.getAnnotation(annotationType);
            if (annotation != null) {
                listAnnotatedClasses.add(getClassProperty(clazz, annotationType));
            }
        }
        
        return listAnnotatedClasses;
    }
    public static List<Class> getClasses(String packageName){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration resources = null;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            //TODO e.printStackTrace(); can't find package
        }
        List<File> dirs = new ArrayList();
        while (resources.hasMoreElements()) {
            URL resource = (URL)resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class> classes = new ArrayList();
        for (File directory : dirs) {
            try {
                classes.addAll(findClasses(directory, packageName));
            } catch (ClassNotFoundException e) {
                //TODO e.printStackTrace();
            }
        }
        
        return classes;
    }
    
    private static ClassProperty getClassProperty(Class scannedClass, Class annotationType) {
        ClassProperty classProperty = new ClassProperty(scannedClass);
        classProperty.setIsCopied(scannedClass.isAnnotationPresent(Copied.class));
        classProperty.setIsDenied(scannedClass.isAnnotationPresent(Denied.class));
        Annotation annotation = scannedClass.getAnnotation(annotationType);
        classProperty.setBeanName(((SnowFlake)annotation).value());
        
        return classProperty;
    }

    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        
        return classes;
    }

    public static class ClassProperty {
        private String beanName;
        private Class clazz;
        private boolean denied;
        private boolean copied;

        private ClassProperty(Class clazz) {
            this.clazz = clazz;
        }
        public Class getClazz() {
            return clazz;
        }
        public boolean isDenied() {
            return denied;
        }
        private void setIsDenied(boolean isDenied) {
            this.denied = isDenied;
        }
        public boolean isCopied() {
            return copied;
        }
        private void setIsCopied(boolean isCopied) {
            this.copied = isCopied;
        }
        public String getBeanName() {
            return beanName;
        }
        private void setBeanName(String beanName) {
            this.beanName = beanName;
        }
    }
}
