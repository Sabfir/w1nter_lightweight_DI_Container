package container;

import container.annotation.Copied;
import container.annotation.Denied;
import container.annotation.Report;
import container.annotation.SnowFlake;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Winter {
    private String packageName;
    private List<ClassProperty> annotatedClassProperty = new ArrayList<>();
    private List<Object> objectList = new ArrayList<Object>();

    private List<ClassProperty> getAnnotatedClasses() {
        final String CLASS_SUFFIX = ".class";
        List<ClassProperty> classes = new ArrayList<>();
        String packageNameFormatted = "/" + packageName.replace(".", "/");
        URL packageLocation = Thread.currentThread().getContextClassLoader().getResource(packageNameFormatted);
        if (packageLocation == null) {
//            LOG.warn("Could not retrieve URL resource: " + packageNameSlashed);
            return classes;
        }

        String directoryString = packageLocation.getFile();
        if (directoryString == null) {
//            LOG.warn("Could not find directory for URL resource: " + packageNameSlashed);
            return classes;
        }

        File directory = new File(directoryString);
        if (directory.exists()) {
            String[] files = directory.list();
            for (String fileName : files) {
                if (fileName.endsWith(CLASS_SUFFIX)) {
                    fileName = fileName.substring(0, fileName.length() - 6);
                    try {
                        Class scannedClass = Class.forName(packageName + "." + fileName);
                        SnowFlake snowFlake = (SnowFlake)scannedClass.getAnnotation(SnowFlake.class);
                        if (snowFlake != null) {
                            classes.add(getClassProperty(scannedClass, snowFlake.value()));
                        }
                    } catch (ClassNotFoundException e) {
//                        LOG.warn(packageName + "." + fileName + " does not appear to be a valid class.", e);
                    }
                }
            }
        } else {
//            LOG.warn(packageName + " does not appear to exist as a valid package on the file system.");
        }
        return classes;
    }
    private ClassProperty getClassProperty(Class scannedClass) {
        ClassProperty classProperty = new ClassProperty(scannedClass);
        classProperty.setIsCopied(scannedClass.isAnnotationPresent(Copied.class));
        classProperty.setIsDenied(scannedClass.isAnnotationPresent(Denied.class));
        return classProperty;
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

    public <T>T getSnowflake(String beanName) {
        if (beanName.isEmpty()) {

        }
        return (T)null;
    }

    public Winter(String packageName) {
        this.packageName = packageName;
        annotatedClassProperty = getAnnotatedClasses();
        publishClassInfo();
    }

    public void addSnowflakes(String packageName) {
        this.packageName = packageName;
        annotatedClassProperty = getAnnotatedClasses();
        publishClassInfo();
    }

    private void publishClass(Class scannedClass){

    }
    private ClassProperty getClassProperty(Class scannedClass, String beanName) {
        ClassProperty classProperty = new ClassProperty(scannedClass);
        classProperty.setIsCopied(scannedClass.isAnnotationPresent(Copied.class));
        classProperty.setIsDenied(scannedClass.isAnnotationPresent(Denied.class));
        classProperty.setBeanName(beanName);
        return classProperty;
    }
    private class ClassProperty {
        private String beanName;
        private Class clazz;
        private boolean denied;
        private boolean copied;

        public ClassProperty(Class annotatedClass) {
            this.clazz = annotatedClass;
        }
        public Class getClazz() {
            return clazz;
        }
        public boolean isDenied() {
            return denied;
        }
        public void setIsDenied(boolean isDenied) {
            this.denied = isDenied;
        }
        public boolean isCopied() {
            return copied;
        }
        public void setIsCopied(boolean isCopied) {
            this.copied = isCopied;
        }
        public String getBeanName() {
            return beanName;
        }
        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }
    }
}
