
package net.sourceforge.pmd.util.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;


public class AntLogHandler extends Handler {
    private Task antTask;

    private static final Formatter FORMATTER = new PmdLogFormatter();

    public AntLogHandler(Task antTask) {
        this.antTask = antTask;
    }

    public void publish(LogRecord logRecord) {
        
        int antLevel;
        Level level = logRecord.getLevel();
        if (level == Level.FINEST)
            antLevel = Project.MSG_DEBUG;   
        else if (level == Level.FINE || level == Level.FINER || level == Level.CONFIG)
            antLevel = Project.MSG_VERBOSE; 
        else if (level == Level.INFO)
            antLevel = Project.MSG_INFO;    
        else if (level == Level.WARNING)
            antLevel = Project.MSG_WARN;    
        else if (level == Level.SEVERE)
            antLevel = Project.MSG_ERR;     
        else
            throw new IllegalStateException("Unknown logging level");   
        
        antTask.log(FORMATTER.format(logRecord), antLevel);
        if (logRecord.getThrown() != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter, true);
            logRecord.getThrown().printStackTrace(printWriter);
            antTask.log(stringWriter.toString(), antLevel);
        }
    }

    public void close() throws SecurityException {
        return;
    }

    public void flush() {
        return;
    }
}