
package genj.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public abstract class Origin {
  
  private static Logger LOG = Logger.getLogger( "genj.util");
  
  
  private final static char
    BSLASH = '\\',
    FSLASH = '/',
    COLON  = ':';

  
  protected URL url;
  
  
  protected Origin(URL url) {
    this.url = url;
  }

  
  public static Origin create(String s) throws MalformedURLException {
    
    return create(new URL(s));
  }
  
  
  public static Origin create(URL url) {

    
    if (url.getFile().endsWith(".zip")) {
      return new ZipOrigin(url);
    } else {
      return new DefaultOrigin(url);
    }

  }

  
  public abstract InputStream open() throws IOException;

  
  public final InputStream open(String name) throws IOException {

    
    name = back2forwardslash(name);

    
    if (ABSOLUTE.matcher(name).matches()) {
      
      LOG.fine("Trying to open "+name+" as absolute path (origin is "+this+")");

      URLConnection uc;
      try {
        uc = new URL(name).openConnection();
      } catch (MalformedURLException e1) {
        
        try {
          
          
          uc = new URL("file:"+name).openConnection();
        } catch (MalformedURLException e2) {
          return null;
        }
      }
      return new InputStreamImpl(uc.getInputStream(),uc.getContentLength());

    }

    
    LOG.fine("Trying to open "+name+" as relative path (origin is "+this+")");
    
    return openImpl(name);
  }
  
  
  protected abstract InputStream openImpl(String name) throws IOException;

  
  public String toString() {
    return url.toString();
  }

  
  private final static Pattern ABSOLUTE = Pattern.compile("([a-z]:).*|([A-Z]:).*|\\/.*|\\\\.*");
  
  public String calcRelativeLocation(String file) {

    
    
    
    
    String here = url.toString();
    
    if (here.startsWith("file://"))
      here = here.substring("file:/".length());
    
    else if (here.startsWith("file:"))
      here = here.substring("file:".length());
    
    
    if (!ABSOLUTE.matcher(file).matches())
      return null;
    
    
    try {
      here = back2forwardslash(new File(here.substring(0,here.lastIndexOf(FSLASH))).getCanonicalPath()) + "/";
      file = back2forwardslash(new File(file).getCanonicalPath()); 
      
      boolean startsWith = file.startsWith(here);
      LOG.fine("File "+file+" is "+(startsWith?"":"not ")+"relative to "+here);
      if (startsWith)
        return file.substring(here.length());
    } catch (Throwable t) {
    }
    

    
    return null;
  }
  
  
  public abstract String[] list() throws IOException ;
  
  
  public abstract File getFile();
  
  
  public abstract File getFile(String name);

  
  public String getFileName() {
    return getName();
  }

  
  public String getName() {
    String path = back2forwardslash(url.toString());
    if (path.endsWith(""+FSLASH))
      path = path.substring(0, path.length()-1);
    return path.substring(path.lastIndexOf(FSLASH)+1);
  }
  
  
  public boolean equals(Object other) {
    return other instanceof Origin && ((Origin)other).url.toString().equals(url.toString());
  }
  
  
  public int hashCode() {
    return url.toString().hashCode();
  }
  
  
  protected String back2forwardslash(String s) {
    return s.toString().replace(BSLASH, FSLASH);
  }
  
  
  private static class DefaultOrigin extends Origin {

    
    protected DefaultOrigin(URL url) {
      super(url);
    }
    
    
    public InputStream open() throws IOException {
      URLConnection uc = url.openConnection();
      return new InputStreamImpl(uc.getInputStream(),uc.getContentLength());
    }
    
    
    protected InputStream openImpl(String name) throws IOException {

      
      String path = back2forwardslash(url.toString());
      path = path.substring(0, path.lastIndexOf(FSLASH) +1) + name;

      
      try {

        URLConnection uc = new URL(path).openConnection();
        return new InputStreamImpl(uc.getInputStream(),uc.getContentLength());

      } catch (MalformedURLException e) {
        throw new IOException(e.getMessage());
      }

    }

    
    public String[] list() {
      File dir = getFile();
      if (dir==null) 
        throw new IllegalArgumentException("list() not supported by url protocol");
      if (!dir.isDirectory())
        dir = dir.getParentFile();
      return dir.list();
    }
    
    
    public File getFile() {
      
      if (!"file".equals(url.getProtocol()))
        return null;
      try {
        return new File(URLDecoder.decode(url.getFile(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        
        return new File(url.getFile());
      }
    }

    
    public File getFile(String file) {
      
      
      if (file.length()<1) return null;
      
      
      if (ABSOLUTE.matcher(file).matches()) 
        return new File(file);
      
      
      return new File(getFile().getParent(), file);
    }


  } 
 

  
  private static class ZipOrigin extends Origin {

    
    private byte[] cachedBits;

    
    protected ZipOrigin(URL url) {
      super(url);
    }

    
    public String[] list() throws IOException {
      ArrayList result = new ArrayList();
      ZipInputStream in  = openImpl();
      while (true) {
        ZipEntry entry = in.getNextEntry();
        if (entry==null) break;
        result.add(entry.getName());
      }
      in.close();
      return (String[]) result.toArray(new String[result.size()]);
    }
    
    
    public InputStream open() throws IOException {

      
      String anchor = url.getRef();
      if ((anchor==null)||(anchor.length()==0)) {
        throw new IOException("ZipOrigin needs anchor for open()");
      }

      
      return openImpl(anchor);
    }
    
    
    private ZipInputStream openImpl() throws IOException {
      
      
      if (cachedBits==null) try {
        cachedBits = new ByteArray(url.openConnection().getInputStream(), true).getBytes();
      } catch (InterruptedException e) {
        throw new IOException("interrupted while opening "+getName());
      }

      
      return new ZipInputStream(new ByteArrayInputStream(cachedBits));

    }
    
    
    protected InputStream openImpl(String file) throws IOException {

       ZipInputStream zin = openImpl();

      
      for (ZipEntry zentry = zin.getNextEntry();zentry!=null;zentry=zin.getNextEntry()) {
        if (zentry.getName().equals(file)) 
          return new InputStreamImpl(zin, (int)zentry.getSize());
      }

      
      throw new IOException("Couldn't find resource "+file+" in ZIP-file");
    }

    
    public File getFile() {
      return null;
    }

    
    public String getFileName() {
      return url.getRef();
    }

    
    public File getFile(String name) {
      return null;
    }

  } 
  
  
  private static class InputStreamImpl extends InputStream {

    
    private InputStream in;
    
    
    private int len;

    
    protected InputStreamImpl(InputStream in, int len) {
      this.in=in;
      this.len=len;
    }

    
    public int read() throws IOException {
      return in.read();
    }
    
    
    public int read(byte[] b, int off, int len) throws IOException {
      return in.read(b, off, len);
    }

    
    public int available() throws IOException {
      return len;
    }
    
    
    public void close() throws IOException {
      in.close();
    }

  } 

} 
