
package genj.gedcom;

import genj.util.Base64;
import genj.util.ByteArray;
import genj.util.swing.ImageIcon;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.logging.Level;


public class PropertyBlob extends Property implements MultiLineProperty, IconValueAvailable {
  
  private final static String TAG = "BLOB";

  
  private Object content = "";

  
  private SoftReference icon;

  
  private boolean noIconAvailable;

  
  public byte[] getBlobData() {

    
    if (content instanceof byte[])
      return (byte[])content;

    
    try {
      content = Base64.decode(content.toString());
    } catch (IllegalArgumentException e) {
      Gedcom.LOG.log(Level.WARNING, "Cannot convert blob base64 in "+getGedcom().getName()+"/"+getEntity()+"/"+getPath()+" into bytes ("+e.getMessage()+")");
      return new byte[0];
    }

    return (byte[])content;
  }
  
  
  public String getTitle() {
    Entity e = getEntity();
    return (e instanceof Media) ? ((Media)e).getTitle() : getTag();
  }

  
  public String getTag() {
    return TAG;
  }
  
  
   Property init(MetaProperty meta, String value) throws GedcomException {
    meta.assertTag(TAG);
    return super.init(meta,value);
  }

  
  public String getValue() {

    
    if (content instanceof byte[])
      return ((byte[])content).length+" Raw Bytes";

    
    return content.toString().length()+" Base64 Bytes";
  }

  
  public synchronized ImageIcon getValueAsIcon() {

    
    if (noIconAvailable)
      return null;

    
    if (icon!=null) {
      ImageIcon result = (ImageIcon)icon.get();
      if (result!=null)
        return result;
    }
    
    
    byte[] bs = getBlobData();
    if (bs==null) {
      noIconAvailable = true;
      return null;
    }

    
    try {
      ImageIcon result = new ImageIcon(getTitle(), bs);

      
      icon = new SoftReference(result);
      
      
      return result;
      
    } catch (Throwable t) {
    }

    
    noIconAvailable = true;
    return null;
  }
  
  
  public Collector getLineCollector() {
    return new BlobCollector();
  }

  
  public MultiLineProperty.Iterator getLineIterator() {
    
    
    if (content instanceof byte[])
      return new BlobIterator(Base64.encode((byte[])content));
      
    
    return new BlobIterator(content.toString());
  }
  
  
  public void setValue(String value) {
    
    String old = getValue();

    
    content = value;
    icon = null;
    noIconAvailable = false;

    
    propagatePropertyChanged(this, old);

    
  }
  
  
  public boolean addFile(File file) {
    return load(file.getAbsolutePath(), true);
  }
  
  
  public boolean load(String file, boolean updateMeta) {
    
    String old = getValue();

    
    noIconAvailable = false;
    icon = null;
    
    
    if (file.length()!=0) {
      
      try {
        InputStream in = getGedcom().getOrigin().open(file);
        byte[] newContent = new ByteArray(in, in.available(), false).getBytes();
        in.close();
        content = newContent;
      } catch (Throwable t) {
        return false;
      }
    }
    
    
    propagatePropertyChanged(this, old);
    
    
    Property media = getParent();
    if (!updateMeta||!(media instanceof PropertyMedia||media instanceof Media)) 
      return true;
      
    
    Property format = media.getProperty("FORM");
    if (format==null)
      format = media.addProperty(new PropertySimpleValue("FORM")); 
    format.setValue(PropertyFile.getSuffix(file));
    
    
    return true;
  }

  
  private class BlobCollector implements MultiLineProperty.Collector {
    
    
    private StringBuffer buffer;
    
     
    private BlobCollector() {
      buffer = new StringBuffer(1024);
      if (content instanceof String) buffer.append(content);
    }
    
    
    public boolean append(int indent, String tag, String value) {
      
      
      if (indent!=1)
        return false;
        
      
      if (!"CONT".equals(tag))
        return false;
        
      
      buffer.append(value.trim());
      
      
      return true;
    }
    
    
    public String getValue() {
      return buffer.toString();
    }

  } 

  
  private static class BlobIterator implements MultiLineProperty.Iterator {

    
    private String base64;

    
    private int offset;

    
    private final int LINE = 72;

    
    public BlobIterator(String base64) {
      this.base64 = base64;
      this.offset = 0;
    }
    
    
    public void setValue(String base64) {
      
      
    }
    
    
    public int getIndent() {
      return offset==0?0:1;
    }
    
    
    public String getTag() {
      return offset==0 ? TAG : "CONT";
    }

    
    public String getValue() {
      return base64.substring( offset, Math.min(offset+LINE,base64.length()) );
    }

    
    public boolean next() {
      if (offset+LINE>=base64.length()) 
        return false;
      offset += LINE;
      return true;
    }

  } 

} 
