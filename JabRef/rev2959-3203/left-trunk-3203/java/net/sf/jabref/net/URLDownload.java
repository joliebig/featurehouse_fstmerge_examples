
package net.sf.jabref.net;

import java.awt.Component;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.CookieHandler;

import javax.swing.ProgressMonitorInputStream;



public class URLDownload {  
    
    private URL source;
    private URLConnection con = null;
    private File dest;
    private Component parent;
    private String mimeType = null;
    private String content = null;

    private CookieHandler cm;

    
    public URLDownload(URL _source) {
        this.source = _source;
        this.dest = null;
        this.parent = null;

        setCookieHandler();
    }

    
    public URLDownload(Component _parent, URL _source, File _dest) {
        source = _source;
        dest = _dest;
        parent = _parent;

        setCookieHandler();
    }

    private void setCookieHandler() {
        try {
            
            if ((cm = CookieHandler.getDefault()) == null) {
                cm = new CookieHandlerImpl();
                CookieHandler.setDefault(cm);
            }
        } catch (SecurityException e) {
            
            
        }
    }

    public String getMimeType() {
        return mimeType;
    }

    public URLConnection getURLConnection() {
        return con;
    }

    
    public String getStringContent() {
        return content;
    }

    public void openConnectionOnly() throws IOException {
        con = source.openConnection();
        con.setRequestProperty("User-Agent", "Jabref");
        mimeType = con.getContentType();
    }

    public void download() throws IOException {

        if (con == null) {
            con = source.openConnection();
            con.setRequestProperty("User-Agent", "Jabref");
            mimeType = con.getContentType();
        }
        if (dest != null)
            downloadToFile();
        else
            downloadToString();
    }

    protected void downloadToString() throws IOException {

    	InputStream input = new BufferedInputStream(con.getInputStream());
        Writer output = new StringWriter();

        try
          {
            copy(input, output);
          }
        catch (IOException e)
          {
            e.printStackTrace();
          }
        finally
          {
            try
              {
                input.close();
                output.close();
              }
            catch (Exception e)
              {
              }
          }

        content = output.toString();
    }


    protected void downloadToFile() throws IOException {

    	InputStream input = new BufferedInputStream(con.getInputStream());
        OutputStream output =  new BufferedOutputStream(new FileOutputStream(dest));

        try
          {
            copy(input, output);
          }
        catch (IOException e)
          {
            e.printStackTrace();
          }
        finally
          {
            try
              {
                input.close();
                output.close();
              }
            catch (Exception e)
              {
              }
          }        
    }

    public void copy(InputStream in, OutputStream out) throws IOException
      {
        InputStream _in = new ProgressMonitorInputStream(parent, "Downloading " + source.toString(), in);
        byte[] buffer = new byte[512];
        while(true)
        {
            int bytesRead = _in.read(buffer);
            if(bytesRead == -1) break;
            out.write(buffer, 0, bytesRead);
        }        
      }

    public void copy(InputStream in, Writer out) throws IOException
      {
        InputStream _in = new ProgressMonitorInputStream(parent, "Downloading " + source.toString(), in);
        BufferedReader read = new BufferedReader(new InputStreamReader(_in));
        byte[] buffer = new byte[512];
        String line;
        while ((line = read.readLine()) != null) {
            out.write(line);
            out.write("\n");
        }
      }
}
