
package net.sourceforge.squirrel_sql.client.util;

import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class IOUtilities {
        
    
    private final static ILogger s_log = 
        LoggerController.createLogger(IOUtilities.class);
                
    public static void closeInputStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception e) {
                s_log.error("closeInputStream: Unable to close InputStream - "
                        + e.getMessage(), e);
            }
        }
    }    
    
    public static void closeOutpuStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (Exception e) {
                s_log.error("closeOutpuStream: Unable to close OutputStream - "
                        + e.getMessage(), e);
            }
        }
    }
    
   
}
