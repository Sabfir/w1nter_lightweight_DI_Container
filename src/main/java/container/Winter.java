package container;

import container.annotation.SnowFlake;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Winter {
    private String packageName;

    private Map<String, Class> getAnnotatedClasses() {
        final String CLASS_SUFFIX = ".class";
        Map<String, Class> classes = new HashMap<String, Class>();
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
                            classes.put(snowFlake.value(), scannedClass);
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

    public Winter(String packageName) {
        this.packageName = packageName;
        getAnnotatedClasses();
    }

    public void addSnowflakes(String packageName) {
        this.packageName = packageName;
        getAnnotatedClasses();
    }
}
