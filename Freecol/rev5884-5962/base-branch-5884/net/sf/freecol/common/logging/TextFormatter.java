

package net.sf.freecol.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


final class TextFormatter extends Formatter {





    
    public TextFormatter() {
    }

    
    @Override
    public String format(LogRecord record) {
        StringBuilder result = new StringBuilder();
        result.append(record.getSourceClassName()).append(' ').append(record.getSourceMethodName());
        result.append("\n\t").append(record.getLevel().getName()).append(": ").append(
                record.getMessage().replaceAll("\n", "\n\t"));
        result.append("\n\t").append(new Date(record.getMillis()).toString());
        result.append("\n\tThread ID: ").append(record.getThreadID()).append('\n');
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println("\tStack trace:");
            record.getThrown().printStackTrace(pw);
            pw.println("----------------------------");
            pw.flush();
            result.append(sw.toString());
        }

        return result.toString();
    }
}
