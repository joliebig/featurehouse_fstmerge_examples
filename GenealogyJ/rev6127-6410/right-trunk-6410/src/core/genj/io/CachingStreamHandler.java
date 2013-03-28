
package genj.io;

import genj.util.EnvironmentChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Logger;


public class CachingStreamHandler extends URLStreamHandler {
  
  private final static long EXPIRE_MILLIS = 24*60*60*1000;
  
  private final static Logger LOG = Logger.getLogger("genj.io");
  private File cache;
  
  
  public CachingStreamHandler(String name) {
    
    cache = new File(EnvironmentChecker.getProperty("user.home.genj", null, "local cache home"), name);
    if (!cache.isDirectory()&&!cache.mkdir()) {
      LOG.warning("caching disable - can't write to "+cache);
      cache = null;
    }
    
  }
  
  
  @Override
  protected URLConnection openConnection(URL url) throws IOException {
    return new Connection(url);
  }
  
  protected File getCacheEntry(URL url) {
    String host = url.getHost();
    String file = url.getFile();
    String dir = ""; 
    int d = file.lastIndexOf('/');
    if (d>0) {
      dir = file.substring(0,d);
      file = file.substring(d+1);
    }
    String hash = "";
    int q = file.indexOf('?');
    if (q>0) {
      hash = '-'+Integer.toString(Math.abs(file.substring(q).hashCode()));
      file = file.substring(0,q);
    }
    if (file.startsWith("/"))
      file = file.substring(1);
    String ext = "";
    int e = file.lastIndexOf('.');
    if (e>=0) {
      ext = file.substring(e);
      file = file.substring(0, e);
    }
    return new File(cache, host+'/'+dir+'/'+file+hash+ext);
  }
  
  
  private class Connection extends URLConnection {

    public Connection(URL url) {
      super(url);
    }
    
    @Override
    public void connect() throws IOException {
      
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
      
      if (cache!=null) {
        URL url = getURL();
        File cached = getCacheEntry(url);
        if (cached.exists()) {
          if (cached.lastModified() > System.currentTimeMillis() - EXPIRE_MILLIS) {
            LOG.fine("Using cached copy of wiki file "+getURL());
            return new FileInputStream(cached);
          }
        }
        
        return new CachingInputStream(getURL(), cached);
      }
      
      return getURL().openStream();
    }
    
  }
  
  private class CachingInputStream extends InputStream {

    private URL url;
    private File tmp, cached;
    private int len;
    private URLConnection con;
    private OutputStream out;
    private InputStream in;
    
    private CachingInputStream(URL url, File cached) throws IOException {
      this.url = new URL(url.toString());
      this.cached = cached;
    }
    
    
    private void open() throws IOException {
      
      if (in!=null)
        return;
      
      try {
        con = url.openConnection();
        in = con.getInputStream();
      } catch (IOException e) {
        
        if (cached.exists()) {
          LOG.fine("Falling back to cached copy of wiki file "+url);
          in = new FileInputStream(cached);
          con = null;
          return;
        }
        throw e;
      }
      
      try {
        tmp = new File(cached.getAbsolutePath()+".tmp");
        tmp.getParentFile().mkdirs();
        out = new FileOutputStream(tmp);
      } catch (IOException e) {
        LOG.fine("can't write cached copy of wiki file "+url);
      }
      
    }
    
    @Override
    public int read() throws IOException {
      open();
      int i = in.read();
      if (out!=null)
        out.write(i);
      return i;
    }
    @Override
    public int read(byte[] b) throws IOException {
      open();
      int i = in.read(b);
      if (i>0&&out!=null)
        out.write(b, 0, i);
      return i;
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      open();
      int i = in.read(b,off,len);
      if (i>0&&out!=null)
        out.write(b, off, i);
      return i;
    }
    @Override
    public void close() throws IOException {
      if (out!=null) {
        out.close();
        out = null;
        if (con!=null) {
          if (tmp.length()==con.getContentLength()) {
            tmp.renameTo(cached);
          } else {
            LOG.fine("not caching "+tmp.length()+" copy of "+con.getContentLength()+" wiki file "+cache);
          }
        }
        tmp.delete();
      }
      if (in!=null)
        in.close();
    }
  }
  
}