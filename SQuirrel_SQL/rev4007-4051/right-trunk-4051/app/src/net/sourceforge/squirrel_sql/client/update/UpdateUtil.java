
package net.sourceforge.squirrel_sql.client.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class UpdateUtil {

    
    private final static ILogger s_log = 
        LoggerController.createLogger(UpdateUtil.class);
    
    
    public static final String HTTP_PROTOCOL_PREFIX = "http";
    
    
    public static final String LOCAL_UPDATE_DIR_NAME = "update";
        
    
    private final UpdateXmlSerializer serializer = new UpdateXmlSerializer();
    
    
    public ChannelXmlBean downloadCurrentRelease(String host, 
                                                 String path, 
                                                 String fileToGet) {
        ChannelXmlBean result = null;
        BufferedInputStream is = null;
        String pathToFile = path + fileToGet;
        try {
            URL url = new URL(HTTP_PROTOCOL_PREFIX, host, pathToFile);
            is = new BufferedInputStream(url.openStream());
            result = serializer.read(is);
        } catch (Exception e) {
            s_log.error(
                "downloadCurrentRelease: Unexpected exception while " +
                "attempting to open an HTTP connection to host ("+host+") " +
                "to download a file ("+pathToFile+")");
        } finally {
            IOUtilities.closeInputStream(is);
        }
        return result;
    }
    
    
    public boolean downloadHttpFile(String host, 
                                    String path, 
                                    String fileToGet, 
                                    String destDir) {
        boolean result = false;
        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        try {
            URL url = new URL(HTTP_PROTOCOL_PREFIX, host, path + fileToGet);
            is = new BufferedInputStream(url.openStream());
            File localFile = new File(destDir, fileToGet);
            os = new BufferedOutputStream(new FileOutputStream(localFile));
            byte[] buffer = new byte[8192];
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            result = true;
        } catch (Exception e) {
            s_log.error("Exception encountered while attempting to " +
            		"download file "+fileToGet+" from host "+host+" and path "+
            		path+" to destDir "+destDir+": "+e.getMessage(), e);
        } finally {
            IOUtilities.closeInputStream(is);
            IOUtilities.closeOutpuStream(os); 
        }
        return result;
    }  
    
    
    public ChannelXmlBean getLocalReleaseInfo(String localReleaseFile) {
        ChannelXmlBean result = null;
        if (s_log.isDebugEnabled()) {
            s_log.debug("Attempting to read local release file: "
                    + localReleaseFile);
        }
        try {
            result = serializer.read(localReleaseFile);
        } catch (IOException e) {
            s_log.error("Unable to read local release file: "+e.getMessage(), e);
        }
        return result;
    }
    
    
    public String getLocalReleaseFile() {
        String result = null;
        try {
            File f = new File(".");
            File[] files = f.listFiles();
            for (File file : files) {
                if ("update".equals(file.getName())) {
                    File[] updateFiles = file.listFiles();
                    for (File updateFile : updateFiles) {
                        if ("release.xml".equals(updateFile.getName())) {
                            result = updateFile.getAbsolutePath();
                        }
                    }
                }
            }
        } catch (Exception e) {
            s_log.error("getLocalReleaseFile: Exception encountered while " +
            		"attempting to find release.xml file");
        }
        return result;
    }
}
