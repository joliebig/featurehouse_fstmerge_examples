

package net.sf.freecol.common.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.networking.Message;


public final class DefaultHandler extends Handler {



    private FileWriter fileWriter;

    private final boolean consoleLogging;


    
    public DefaultHandler(boolean consoleLogging, String fileName) throws FreeColException {
        this.consoleLogging = consoleLogging;
        File file = new File(fileName);

        if (file.exists()) {
            if (file.isDirectory()) {
                throw new FreeColException("Log file \"" + fileName + "\" could not be created.");
            } else if (file.isFile()) {
                file.delete();
            }
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new FreeColException("Log file \"" + fileName + "\" could not be created: " + e.getMessage());
        }

        if (!file.canWrite()) {
            throw new FreeColException("Can not write in log file \"" + fileName + "\".");
        }

        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            throw new FreeColException("Can not write in log file \"" + fileName + "\".");
        }

        
        
        setFormatter(new TextFormatter());

        try {
            String str = "FreeCol game version: " + FreeCol.getRevision() + "\n" + "FreeCol protocol version: "
                    + Message.getFreeColProtocolVersion() + "\n\n" + "Java vendor: "
                    + System.getProperty("java.vendor") + "\n" + "Java version: " + System.getProperty("java.version")
                    + "\n" + "Java WM name: " + System.getProperty("java.vm.name") + "\n" + "Java WM vendor: "
                    + System.getProperty("java.vm.vendor") + "\n" + "Java WM version: "
                    + System.getProperty("java.vm.version") + "\n\n" + "OS name: " + System.getProperty("os.name")
                    + "\n" + "OS architecture: " + System.getProperty("os.arch") + "\n" + "OS version: "
                    + System.getProperty("os.version") + "\n\n";
            fileWriter.write(str, 0, str.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void flush() {
        try {
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void publish(LogRecord record) {
        if (record.getLevel().intValue() < getLevel().intValue()) {
            return;
        }

        String str = getFormatter().format(record);
        if (consoleLogging && record.getLevel().intValue() >= Level.WARNING.intValue()) {
            System.err.println(str);
        }

        try {
            fileWriter.write(str, 0, str.length());
        } catch (IOException e) {
            System.err.println("Failed to write log record!");
            e.printStackTrace(System.err);
        }

        
        flush();
    }
}
