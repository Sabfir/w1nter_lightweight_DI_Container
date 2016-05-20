package helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;

public class Log4j2Wrapper {
    public static final Marker MARKER_FLOW = new MarkerManager.Log4jMarker("FLOW");
    public static final Marker MARKER_EXCEPTION = new MarkerManager.Log4jMarker("EXCEPTION");
    private static LoggerContext context;
    static {
        context = (LoggerContext) LogManager.getContext(false);
        File file = new File("../../../log4j2.xml");
        context.setConfigLocation(file.toURI());
    }
    public static Logger getLogger(String loggerName) {
        return context.getLogger(loggerName);
    }
}
