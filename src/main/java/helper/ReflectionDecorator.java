package helper;

import container.annotation.Copied;
import container.annotation.Denied;
import container.annotation.SnowFlake;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class ReflectionDecorator {

    public static List<ClassProperty> getAnnotatedClasses(String packageName) {
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
                        } else {
                            //TODO loging not found annotated class
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

    public static ClassProperty getClassProperty(Class scannedClass, String beanName) {
        ClassProperty classProperty = new ClassProperty(scannedClass);
        classProperty.setIsCopied(scannedClass.isAnnotationPresent(Copied.class));
        classProperty.setIsDenied(scannedClass.isAnnotationPresent(Denied.class));
        classProperty.setBeanName(beanName);
        return classProperty;
    }

    public static class ClassProperty {
        private String beanName;
        private Class clazz;
        private boolean denied;
        private boolean copied;

        private ClassProperty(Class annotatedClass) {
            this.clazz = annotatedClass;
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
