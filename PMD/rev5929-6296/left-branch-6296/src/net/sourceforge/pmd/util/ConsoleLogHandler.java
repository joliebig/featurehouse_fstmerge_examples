
package net.sourceforge.pmd.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class ConsoleLogHandler extends Handler {

    private static final Formatter FORMATTER = new PmdLogFormatter();

    public void publish(LogRecord logRecord) {
        System.out.println(FORMATTER.format(logRecord));
        if (logRecord.getThrown() != null) {
            
            
            
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter, true);
            logRecord.getThrown().printStackTrace(printWriter);
            System.out.println(stringWriter.toString());
        }
    }
    
    public void close() throws SecurityException {
        return;
    }

    public void flush() {
        return;
    }
}