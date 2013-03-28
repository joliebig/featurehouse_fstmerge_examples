
package net.sourceforge.squirrel_sql.fw.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.CRC32;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class IOUtilitiesImpl implements IOUtilities {

   
   private final ILogger s_log = LoggerController.createLogger(IOUtilitiesImpl.class);

   
   public void closeInputStream(InputStream is) {
      if (is != null) {
         try {
            is.close();
         } catch (Exception e) {
            s_log.error("closeInputStream: Unable to close InputStream - "
                  + e.getMessage(), e);
         }
      }
   }

   
   public void closeOutputStream(OutputStream os) {
      if (os != null) {
         try {
            os.close();
         } catch (Exception e) {
            s_log.error("closeOutpuStream: Unable to close OutputStream - "
                  + e.getMessage(), e);
         }
      }
   }

   
   public void closeReader(Reader reader) {
      if (reader != null) {
         try {
            reader.close();
         } catch (Exception e) {
            s_log.error("closeReader: Unable to close FileReader - "
                        + e.getMessage(), e);            
         }
      }
   }
   
   
   public void closeWriter(Writer writer) {
      if (writer != null) {
         try {
            writer.close();
         } catch (Exception e) {
            s_log.error("closeReader: Unable to close FileWriter - "
                        + e.getMessage(), e);            
         }
      }
   }
   
   
   public void copyBytes(InputStream is, OutputStream os) 
      throws IOException
   {
      byte[] buffer = new byte[8192];
      int length;
      while ((length = is.read(buffer)) > 0) {
         os.write(buffer, 0, length);
      }
   }
   
   
   public int copyBytesToFile(InputStream is, FileWrapper outputFile) throws IOException {
   	BufferedOutputStream outputFileStream = null;
   	int totalLength = 0;
   	try {
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
	   	outputFileStream = new BufferedOutputStream(new FileOutputStream(outputFile.getAbsolutePath()));
			byte[] buffer = new byte[8192];
			int length = 0;
			while ((length = is.read(buffer)) != -1)
			{
				totalLength += length;
				outputFileStream.write(buffer, 0, length);
			}
			
   	} finally {
   		closeOutputStream(outputFileStream);
   	}
   	return totalLength;
   }
   
   
   public long getCheckSum(File f) throws IOException {
       CRC32 result = new CRC32();  
       FileInputStream fis = null;
       try {
           fis = new FileInputStream(f);
           int b = 0;
           while ((b = fis.read()) != -1) {
               result.update(b);
           }
       } finally {
           closeInputStream(fis);
       }
       return result.getValue();
   }
 
   
   public long getCheckSum(FileWrapper f) throws IOException {
   	return getCheckSum(new File(f.getAbsolutePath()));
   }

	
	public void copyFile(FileWrapper from, FileWrapper to) throws IOException {
      FileInputStream in = null;
      FileOutputStream out = null;
      try {
         in = new FileInputStream(from.getAbsolutePath());
         out = new FileOutputStream(to.getAbsolutePath());
         byte[] buffer = new byte[8192];
         int len;
         while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);         
         }
      } finally {
      	closeInputStream(in);
      	closeOutputStream(out);
      }
   }
	
	
	public URL constructHttpUrl(final String host, final int port, final String fileToGet) 
		throws MalformedURLException {
		URL url = null;
		String server = host;
		if (server.startsWith(HTTP_PROTOCOL_PREFIX)) {
			int beginIdx = server.indexOf("://") + 3;
			server = server.substring(beginIdx, host.length());
		}
		if (port == 80) {
			url = new URL(HTTP_PROTOCOL_PREFIX, server, fileToGet);
		} else {
			url = new URL(HTTP_PROTOCOL_PREFIX, server, port, fileToGet);
		}
		return url;
	}
	
	
   public int downloadHttpFile(URL url, FileWrapper destFile)
	      throws Exception {
		BufferedInputStream is = null;
		HttpMethod method = null;
		int resultCode = -1;
		int result = -1;
		try {
			if (s_log.isDebugEnabled()) {
				s_log.debug("downloadHttpFile: downloading file (" + destFile.getName() + ") from url: " + url);
			}
			HttpClient client = new HttpClient();
			method = new GetMethod(url.toString());
			method.setFollowRedirects(true);

			resultCode = client.executeMethod(method);
			if (resultCode != 200) {
				throw new FileNotFoundException("Failed to download file from url (" + url
				      + "): HTTP Response Code=" + resultCode);
			}
			InputStream mis = method.getResponseBodyAsStream();

			if (s_log.isDebugEnabled()) {
				s_log.debug("downloadHttpFile: response code was: " + resultCode);
			}
			is = new BufferedInputStream(mis);

			if (s_log.isDebugEnabled()) {
				s_log.debug("downloadHttpFile: writing http response body to file: " + destFile.getAbsolutePath());
			}

			result = copyBytesToFile(mis, destFile);
		} catch (Exception e) {
			s_log.error("downloadHttpFile: Unexpected exception while "
			      + "attempting to open an HTTP connection to url (" + url 
			      + ") to download a file (" + destFile.getAbsolutePath() + "): " + e.getMessage(), e);
			s_log.error("response code was: " + resultCode);
			throw e;
		} finally {
			closeInputStream(is);
			method.releaseConnection();
		}
		return result;
	}
	
}
