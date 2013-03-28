
package genj.util.swing;

import genj.renderer.DPI;
import genj.util.ByteArray;
import genj.util.Dimension2d;
import genj.util.ImageSniffer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;


public class ImageIcon extends javax.swing.ImageIcon {

  private final static ImageFilter GRAYSCALE_FILTER = new BufferedImageFilter(new GrayscaleFilter());

  
  private DPI dpi = null;

  
  private Map<Object, ImageIcon> overlays = new WeakHashMap<Object, ImageIcon>();

  
  public ImageIcon(Image copy) {
    super(copy);
  }

  
  public ImageIcon(String naMe, byte[] data) {
    super(data);

    if (getImageLoadStatus() != MediaTracker.COMPLETE)
      throw new RuntimeException("load status incomplete");

    
    
    
    if (getIconHeight() < 0 || getIconWidth() < 0)
      throw new RuntimeException("image with invalid width/height");

    
    setDescription(naMe);

    
    String msg;

    dpi = new ImageSniffer(new ByteArrayInputStream(data)).getDPI();

    
  }

  
  public ImageIcon(Object from, String resource) {
    this(from.getClass(), resource);
  }

  private static String patchPNG(String resource) {
    return resource.indexOf('.') < 0 ? resource + ".png" : resource;
  }

  
  public ImageIcon(Class<?> from, String resource) {
    this(from.getName() + '#' + resource, from.getResourceAsStream(patchPNG(resource)));
  }

  
  public ImageIcon(String name, InputStream in) {
    this(name, read(name, in));
  }

  
  public DPI getResolution() {
    return dpi;
  }

  
  public Dimension2D getSizeInInches() {
    
    if (dpi == null)
      return null;
    return new Dimension2d((double) getIconWidth() / dpi.horizontal(), (double) getIconHeight() / dpi.vertical());
  }

  
  public Dimension getSizeInPoints(DPI dpiTarget) {
    Dimension2D sizeInInches = getSizeInInches();
    if (sizeInInches == null)
      return new Dimension(getIconWidth(), getIconHeight());
    return new Dimension((int) (sizeInInches.getWidth() * dpiTarget.horizontal()), (int) (sizeInInches.getHeight() * dpiTarget.vertical()));
  }

  
  public ImageIcon paintIcon(Graphics g, int x, int y) {
    super.paintIcon(null, g, x, y);
    return this;
  }

  
  private static byte[] read(String name, InputStream in) {
    
    if (in == null)
      throw new IllegalArgumentException("no stream for " + name);
    
    try {
      return new ByteArray(in).getBytes();
    } catch (IOException ex) {
      throw new IllegalArgumentException("can't read " + name + ": " + ex.getMessage());
    } catch (InterruptedException e) {
      throw new IllegalStateException("interrupted while reading " + name);
    }
  }

  
  public ImageIcon getGrayedOut() {

    
    ImageIcon result = overlays.get("grayedout");
    if (result != null) 
      return result;
    
    GrayscaleFilter filter = new GrayscaleFilter();
    ImageProducer prod = new FilteredImageSource(getImage().getSource(), GRAYSCALE_FILTER);
    Image grayImage = Toolkit.getDefaultToolkit().createImage(prod);
    result = new ImageIcon(grayImage);
    result.dpi = dpi;
    result.setDescription(getDescription());
    
    overlays.put("grayedout", result);
    
    return result;
  }

  
  public ImageIcon getOverLayed(ImageIcon overlay) {

    
    ImageIcon result = overlays.get(overlay);
    if (result != null) 
      return result;

    
    int height = Math.max(getIconHeight(), overlay.getIconHeight());
    int width = Math.max(getIconWidth(), overlay.getIconWidth());

    Image image1 = getImage();
    Image image2 = overlay.getImage();
    BufferedImage composite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    Graphics g = composite.createGraphics();
    g.setClip(0, 0, width, height);
    g.drawImage(image1, 0, 0, null);
    g.drawImage(image2, 0, 0, null);
    g.dispose();

    result = new ImageIcon(composite);
    result.dpi = dpi;
    result.setDescription(getDescription());

    
    overlays.put(overlay, result);

    
    return result;
  }

  
  private static class GrayscaleFilter implements BufferedImageOp {

    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
      if (dstCM == null)
        dstCM = src.getColorModel();
      return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
    }

    public Rectangle2D getBounds2D(BufferedImage src) {
      return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
      if (dstPt == null)
        dstPt = new Point2D.Double();
      dstPt.setLocation(srcPt.getX(), srcPt.getY());
      return dstPt;
    }

    public RenderingHints getRenderingHints() {
      return null;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
      int width = src.getWidth();
      int height = src.getHeight();
      int type = src.getType();
      WritableRaster srcRaster = src.getRaster();

      if (dst == null)
        dst = createCompatibleDestImage(src, null);
      WritableRaster dstRaster = dst.getRaster();

      int[] inPixels = new int[width];
      for (int y = 0; y < height; y++) {
        
        
        if (type == BufferedImage.TYPE_INT_ARGB) {
          srcRaster.getDataElements(0, y, width, 1, inPixels);
          for (int x = 0; x < width; x++)
            inPixels[x] = filterRGB(x, y, inPixels[x]);
          dstRaster.setDataElements(0, y, width, 1, inPixels);
        } else {
          src.getRGB(0, y, width, 1, inPixels, 0, width);
          for (int x = 0; x < width; x++)
            inPixels[x] = filterRGB(x, y, inPixels[x]);
          dst.setRGB(0, y, width, 1, inPixels, 0, width);
        }
      }

      return dst;
    }

    private int filterRGB(int x, int y, int rgb) {
      int a = rgb & 0xff000000;
      int r = (rgb >> 16) & 0xff;
      int g = (rgb >> 8) & 0xff;
      int b = rgb & 0xff;
      rgb = (r * 77 + g * 151 + b * 28) >> 8; 
      return a | (rgb << 16) | (rgb << 8) | rgb;
    }

  }

} 
