
package genj.util.swing;

import genj.util.ByteArray;
import genj.util.Dimension2d;
import genj.util.ImageSniffer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.GrayFilter;

public class ImageIcon extends javax.swing.ImageIcon {
  
  
  private Point dpi = null;
  
  
  private Map overlays = new WeakHashMap();
  
  
  public ImageIcon(Image copy) {
    super(copy);
  }
  
  
  public ImageIcon(String naMe, byte[] data) {
    super(data);

    if (getImageLoadStatus()!=MediaTracker.COMPLETE)
      throw new RuntimeException("load status incomplete");

    
    
    
    if (getIconHeight()<0||getIconWidth()<0)
      throw new RuntimeException("image with invalid width/height");

    
    setDescription(naMe);

    
    String msg;
    
    dpi = new ImageSniffer(new ByteArrayInputStream(data)).getDPI();
    
    
  }
  
  
  public ImageIcon(Object from, String resource) {
    this(from.getClass(), resource);
  }
  
  private static String patchPNG(String resource) {
    return resource.indexOf('.')<0 ? resource+".png" : resource;
  }
  
  
  public ImageIcon(Class from, String resource) {
    this(from.getName()+'#'+resource, from.getResourceAsStream(patchPNG(resource)));
  }
  
  
  public ImageIcon(String name, InputStream in) {
    this(name, read(name, in));
  }
  
  
  public Point getResolution() {
    return dpi;
  }

  
  public Dimension2D getSizeInInches() {
    
    if (dpi==null) 
      return null;
    return new Dimension2d(
      (double)getIconWidth ()/dpi.x, 
      (double)getIconHeight()/dpi.y
    );
  }
  
  
  public Dimension getSizeInPoints(Point dpiTarget) {
    Dimension2D sizeInInches = getSizeInInches();
    if (sizeInInches==null) 
      return new Dimension(getIconWidth(), getIconHeight());
    return new Dimension(
      (int)(sizeInInches.getWidth()*dpiTarget.x), 
      (int)(sizeInInches.getHeight()*dpiTarget.y)
    );
  }
  
  
  public ImageIcon paintIcon(Graphics g, int x, int y) {
    super.paintIcon(null, g, x, y);
    return this;
  }
  
  
  
  
  private static byte[] read(String name, InputStream in) {
    
    if (in==null) 
      throw new IllegalArgumentException("no stream for "+name);
    
    try {
      return new ByteArray(in).getBytes();
    } catch (IOException ex) {
      throw new IllegalArgumentException("can't read "+name+": "+ex.getMessage());
    } catch (InterruptedException e) {
      throw new IllegalStateException("interrupted while reading "+name);
    }
  }

  
  public ImageIcon getDisabled(int percentage) {

    GrayFilter filter = new GrayFilter(true, percentage);
    ImageProducer prod = new FilteredImageSource(getImage().getSource(), filter);
    Image grayImage = Toolkit.getDefaultToolkit().createImage(prod);
    
    ImageIcon result = new ImageIcon(grayImage);
    result.dpi = dpi;
    result.setDescription(getDescription());
    return result;
  }
  
   
  public ImageIcon getOverLayed(ImageIcon overlay) {

    
    ImageIcon result = (ImageIcon)overlays.get(overlay);
    if (result!=null) {
      return result;
    }

    
    int height = Math.max(getIconHeight(), overlay.getIconHeight());
    int width = Math.max(getIconWidth(), overlay.getIconWidth());

    Image image1 = getImage();
    Image image2 = overlay.getImage();
    BufferedImage composite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    Graphics g = composite.createGraphics();
    g.setClip(0,0,width,height);
    g.drawImage(image1, 0, 0, null);
    g.drawImage(image2, 0, 0, null);
    g.dispose();

    result = new ImageIcon(composite);
    result.dpi = dpi;
    result.setDescription(getDescription());

    
    overlays.put(overlay, result);
        
    
    return result;
  }
  
} 
