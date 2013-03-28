
package genj.renderer;

import genj.gedcom.Entity;
import genj.gedcom.Media;
import genj.gedcom.Property;
import genj.gedcom.PropertyBlob;
import genj.gedcom.PropertyFile;
import genj.gedcom.PropertyXRef;
import genj.io.InputSource;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


public class MediaRenderer {

  private final static Logger LOG = Logger.getLogger("genj.renderer");
  
  private final static Map<Property, CacheEntry> CACHE = new WeakHashMap<Property, CacheEntry>();
  
  
  public static Dimension getSize(Property root, Graphics graphics) {
    
    
    InputSource source = getSource(root);
    if (source==null)
      return new Dimension();
    
    CacheEntry cached = CACHE.get(root);
    if (cached!=null&&cached.source.equals(source))
      return cached.size;

    cached = new CacheEntry();
    cached.source = source;
    cached.size = new Dimension();
    CACHE.put(root, cached);
    
    
    InputStream in = null;
    try {
      in = source.open();
      if (in!=null) {
        LOG.finer("Reading size from "+source);
        ImageInputStream iin = ImageIO.createImageInputStream(in);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iin);
        if (iter.hasNext()) {
          ImageReader reader = iter.next();
          try {
            reader.setInput(iin, false, false);
            cached.size.setSize(reader.getWidth(0), reader.getHeight(0));
          } finally {
            reader.dispose();
          }
        }
      }
    } catch (IOException ioe) {
      LOG.log(Level.FINER, "Can't get image dimension for "+root+"/"+source, ioe);
    } finally {
      try { in.close(); } catch (Throwable t) {}
    }
    
    return cached.size;
  }
  
  public static InputSource getSource(Property prop) {
    
    
    if (prop instanceof PropertyFile) {
      File file = ((PropertyFile)prop).getFile();
      if (file!=null && file.exists())
        return InputSource.get(file);
      return null;
    }

    
    if (prop instanceof PropertyBlob)
      return InputSource.get(prop.toString(), ((PropertyBlob)prop).getBlobData());
    
    
    for (int i=0;i<prop.getNoOfProperties(); i++) {
      Property child = prop.getProperty(i);
      
      
      if (child instanceof PropertyXRef) {
        Entity e = ((PropertyXRef)child).getTargetEntity();
        if (e instanceof Media) {
          Media m = (Media)e;
          PropertyBlob BLOB = m.getBlob();
          if (BLOB!=null)
            return InputSource.get(prop.toString(), BLOB.getBlobData());
          File file = m.getFile();
          if (file!=null)
            return InputSource.get(file);
        }
      }
      
      
      if ("OBJE".equals(child.getTag())) {
        Property file = child.getProperty("FILE");
        if (file instanceof PropertyFile) {
          PropertyFile FILE = ((PropertyFile)file);
          if (FILE!=null&&FILE.getFile()!=null&&FILE.getFile().exists())
            return InputSource.get(FILE.getFile());
        }
      }
      
    }
    
    
    return null;
  }
  
  
  public static void render(Graphics g, Rectangle bounds, Property root) {

    Image image = null;
    
    
    InputSource source = getSource(root);
    if (source==null)
      return;
    
    CacheEntry cached = CACHE.get(root);
    if (cached!=null) {
      if (cached.source.equals(source)) {
        
        
        if (cached.size.width==0||cached.size.height==0)
          return;
        
        
        image = cached.image.get();
        if (image!=null) {
          if ( (cached.size.width>=bounds.width&&image.getWidth(null)<bounds.width) 
             ||(cached.size.height>=bounds.height&&image.getHeight(null)<bounds.height))
          image = null;
        }
      }
    } else {
      cached = new CacheEntry();
      cached.source = source;
    }
    
    
    if (image==null) {
      
      InputStream in = null;
      try {
        in = source.open();
        if (in!=null) {
          LOG.finer("Reading image from "+source+" for "+bounds.getSize());
          
          ImageInputStream iin = ImageIO.createImageInputStream(in);
          Iterator<ImageReader> iter = ImageIO.getImageReaders(iin);
          if (iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
              reader.setInput(iin, false, false);
              cached.size.setSize(reader.getWidth(0), reader.getHeight(0));
              ImageReadParam param = reader.getDefaultReadParam();
              param.setSourceSubsampling(
                Math.max(1, (int) Math.floor(cached.size.width/bounds.width)), 
                Math.max(1, (int) Math.floor(cached.size.height/bounds.height))
                , 0, 0);
              image = reader.read(0, param);
              
              cached.image = new SoftReference<Image>(image);
            } finally {
              reader.dispose();
            }
          }
        }
      } catch (IOException ioe) {
        LOG.log(Level.FINER, "Can't get image for "+root+"/"+source, ioe);
        cached.size.setSize(0, 0);
      } finally {
        if (in!=null) try { in.close(); } catch (IOException e) {}
      }
    }
    
    
    if (image!=null)
      g.drawImage(image,
        bounds.x,bounds.y,bounds.x+bounds.width,bounds.y+bounds.height,
        0,0,image.getWidth(null),image.getHeight(null),
        null
      );
    
    
  }
  
  private static class CacheEntry {
    
    InputSource source;
    Dimension size;
    SoftReference<Image> image = new SoftReference<Image>(null);
    
  }

}