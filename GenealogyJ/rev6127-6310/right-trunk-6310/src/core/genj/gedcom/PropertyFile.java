
package genj.gedcom;

import genj.util.swing.ImageIcon;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class PropertyFile extends Property {

  
  public final static ImageIcon DEFAULT_IMAGE = Grammar.V55.getMeta(new TagPath("INDI:OBJE:FILE")).getImage();

  
  private String  file;

  
  private boolean isRelativeChecked = false;
  
  
  public PropertyFile(String tag) {
    super(tag);
  }

  
  public boolean addFile(File file) {
    setValue(file.getAbsolutePath(), true);
    return true;
  }

  
  public String getValue() {

    if (file==null)
      return "";

    
    
    
    if (!isRelativeChecked) {
      Gedcom gedcom = getGedcom();
      if (gedcom!=null) {
        String relative = gedcom.getOrigin().calcRelativeLocation(file);
        if (relative !=null)
          file = relative;
        isRelativeChecked = true;
      }
    }
    return file;
  }

  
  private synchronized ImageIcon loadValueAsIcon() {

    ImageIcon result = null;

    
    if (file!=null&&file.trim().length()>0) {

      
      InputStream in = null;
      try {
        
        
        in = getGedcom().getOrigin().open(file);
        long size = in.available();
        result = new ImageIcon(file, in);
        
        
        int w = result.getIconWidth();
        int h = result.getIconHeight();
        if (w<=0||h<=0)
          throw new IllegalArgumentException();
          
        
        int max = getMaxValueAsIconSize(false);
        if (max>0 && size>max) {
          
          double ratio = w / (double)h;
          int maxarea = Math.max(32*32, max/4);
          
          w = (int)(Math.sqrt(maxarea * ratio   ));
          h = (int)(Math.sqrt(maxarea / ratio ));
            
          BufferedImage thumb = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
          Graphics2D g = (Graphics2D)thumb.getGraphics();
          g.drawImage(result.getImage(), 0, 0, w, h, null);
          result = new ImageIcon(thumb);
        }
        
      } catch (Throwable t) {
        result = null;
      } finally {
        
        if (in!=null) try { in.close(); } catch (IOException ioe) {};
      }
      
      
    }

    
    return result;
  }
  
  
  public synchronized void setValue(String value) {

    String old = getValue();

    
    file = value.replace('\\','/');
    isRelativeChecked = false;
    
    
    
    
    
    propagatePropertyChanged(this, old);
    
    
  }
  
  
  public void setValue(String value, boolean updateMeta) {
    
    
    setValue(value);
    
    
    Property media = getParent();
    if (!updateMeta||!media.getTag().equals("OBJE")) 
      return;
      
    
    Property parent = this;
    if (!getMetaProperty().allows("FORM")) {
      if (!media.getMetaProperty().allows("FORM"))
        return;
      parent = media;
    }

    Property form = parent.getProperty("FORM");
    if (form==null) parent.addProperty("FORM", PropertyFile.getSuffix(file));
    else form.setValue(PropertyFile.getSuffix(file));
    
    
  }

  
  public InputStream getInputStream() throws IOException {
    return getGedcom().getOrigin().open(file);
  }
  
    public File getFile() {
    Gedcom gedcom = getGedcom();
    return gedcom!=null ? gedcom.getOrigin().getFile(file) : null;
  }

    public static int getMaxValueAsIconSize(boolean kb) {
    return (kb ? 1 : 1024) * Options.getInstance().getMaxImageFileSizeKB();
  }

  
  public String getSuffix() {
    return getSuffix(file);
  }

  
  public static String getSuffix(String value) {
    
    String result = "";
    if (value!=null) {
      int i = value.lastIndexOf('.');
      if (i>=0) result = value.substring(i+1);
    }
    
    return result;
  }
  
} 
