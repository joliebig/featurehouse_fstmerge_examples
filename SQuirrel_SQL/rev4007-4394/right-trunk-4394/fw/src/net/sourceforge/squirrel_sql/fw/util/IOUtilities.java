
package net.sourceforge.squirrel_sql.fw.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class IOUtilities {

   
   private final static ILogger s_log = LoggerController.createLogger(IOUtilities.class);

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

   public static void closeOutputStream(OutputStream os) {
      if (os != null) {
         try {
            os.close();
         } catch (Exception e) {
            s_log.error("closeOutpuStream: Unable to close OutputStream - "
                  + e.getMessage(), e);
         }
      }
   }

   
   public static void closeReader(Reader reader) {
      if (reader != null) {
         try {
            reader.close();
         } catch (Exception e) {
            s_log.error("closeReader: Unable to close FileReader - "
                        + e.getMessage(), e);            
         }
      }
   }
   
   public static void closeWriter(Writer writer) {
      if (writer != null) {
         try {
            writer.close();
         } catch (Exception e) {
            s_log.error("closeReader: Unable to close FileWriter - "
                        + e.getMessage(), e);            
         }
      }
   }
   
   
   public static void copyBytes(InputStream is, OutputStream os) 
      throws IOException
   {
      byte[] buffer = new byte[8192];
      int length;
      while ((length = is.read(buffer)) > 0) {
         os.write(buffer, 0, length);
      }
   }
}
