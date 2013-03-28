
package genj.util.swing;

import genj.util.Origin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class ImageWidget extends JPanel {
  
  private final static Logger LOG = Logger.getLogger("genj.util");
  
  
  private final static Worker WORKER = new Worker();
  
  
  private final static Map source2imgsoftref = new HashMap();
  
  
  private JScrollPane scroll = new JScrollPane(); 
  private Content content = new Content();
  
  
  private double zoom;
  
  
  public ImageWidget() {
    super(new BorderLayout());
    scroll = new JScrollPane(new ViewPortAdapter(content));
    add(scroll, BorderLayout.CENTER);
  }
  
  
  public void setSource(Source source) {
    content.setSource(source);
  }
  
  
  public Source getSource() {
    return content.source;
  }
  
  
  public void setZoom(double zoom) {
    if (zoom<0)
      return;
    this.zoom = zoom;
    content.revalidate();
    content.setToolTipText(zoom==0 ? null : (int)(zoom*100D)+"%");

  }
  
  
  public double getZoom() {
    return zoom;
  }
  
  
  private static Image lookupCachedImage(Source source) {
    Image result = null;
    SoftReference ref = (SoftReference)source2imgsoftref.get(source);
    if (ref!=null) 
      result = (Image)ref.get();
    return result;
  }
  
  
  private static void keepCachedImage(Source source, Image img) {
    source2imgsoftref.put(source, new SoftReference(img));
  }
  
   
  private class Content extends JComponent implements Runnable, IIOReadUpdateListener {
    
    
    private Source source = null;

    
    private Dimension cachedDimension;
    
    
    public void run() {
      
      
      if (lookupCachedImage(source)!=null)
        return;
      
      
      InputStream in = null;
      ImageReader reader = null;
      try {
        
        in = source.open();
        
        ImageInputStream iin = ImageIO.createImageInputStream(in);
          
        Iterator iter = ImageIO.getImageReaders(iin);
        if (!iter.hasNext()) 
          throw new IOException("no suitable image reader for "+source);
    
        reader = (ImageReader)iter.next();
        reader.setInput(iin, false, false);
        reader.addIIOReadUpdateListener(this);
          
        reader.read(0, reader.getDefaultReadParam());
        
      } catch (Throwable t) {
        LOG.fine("Loading "+source+" failed with "+t.getMessage());
        
        
        keepCachedImage(source, null);
        
      } finally {
        try { in.close(); } catch (Throwable t) {} 
        try { reader.dispose(); } catch (Throwable t) {} 
      }
        
        
    }
    
    
    private void setSource(Source source) {
      this.source = source;
      cachedDimension = null;
      revalidate();
    }
    
    
    private Image getCachedImage() {
      return lookupCachedImage(source);
    }
    
    
    private Dimension getCachedImageSize() {
      
      
      if (cachedDimension==null) {
      
        
        int w = 0, h = 0;
        Image img = getCachedImage();
        if (img==null) {
          WORKER.add(this);
          return new Dimension(0,0);
        }

        
        cachedDimension = new Dimension(img.getWidth(null), h = img.getHeight(null));
        
      }
      
      
      return new Dimension(cachedDimension);
    }

    
    protected void paintComponent(Graphics g) {
      
      
      if (source==null)
        return;
      
      
      Image img = getCachedImage();
      if (img==null) {        
        WORKER.add(this);
        return;
      }

      
      Graphics2D g2d = (Graphics2D)g;
      
      
      
      double scale;
      if (zoom==0) {
        Dimension avail = getSize();
        scale = avail.width/(double)getCachedImageSize().width;
      } else {
        scale = zoom;
      }
      g2d.scale(scale, scale);
      
      
      g2d.drawImage(img, 0, 0, null);
    }

    
    public Dimension getPreferredSize() {
      
      
      if (source==null)
        return new Dimension(0,0);
      Dimension dim = getCachedImageSize();
      double scale;
      
      
      
      if (zoom==0)  {
        Dimension avail = scroll.getSize();
        double zx = avail.width/(double)dim.width;
        double zy = avail.height/(double)dim.height;
        scale = Math.min(1, Math.min(zx,zy));
      } else {
        scale = zoom;
      }
      
      
      dim.width *= scale;
      dim.height *= scale;
      return dim;
      






    }

    
    public void passComplete(ImageReader reader, BufferedImage img) {
    }

    
    public void thumbnailPassComplete(ImageReader reader, BufferedImage thumb) {
    }

    
    public void passStarted(ImageReader reader, BufferedImage img, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {
    }

    
    public void thumbnailPassStarted(ImageReader reader, BufferedImage thumb, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {
    }

    
    public void imageUpdate(ImageReader reader, BufferedImage img, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
      
      
      keepCachedImage(source, img);
      
      
      int w = img.getWidth();
      int h = img.getHeight();
      if (cachedDimension==null||cachedDimension.width!=w||cachedDimension.height!=h) {
        cachedDimension = new Dimension(w,h);
        revalidate();
        return;
      }
      
      
      if (getWidth()==0||getHeight()==0||!isVisible())
        return;
      
      
      width = width*periodX;
      height = height*periodY;
      
      
      Graphics2D g2d = (Graphics2D)getGraphics();
      
      
      
      
      double scale;
      if (zoom==0) {
        Dimension avail = getSize();
        scale = avail.width/(double)getCachedImageSize().width;
      } else {
        scale = zoom;
      }
      g2d.scale(scale, scale);
      
      try {
        g2d.drawImage(img, 
            minX, minY,
            minX+width, minY+height,
            minX, minY,
            minX+width, minY+height,
            null);
      } catch (Throwable t) {
        
      }
      
      
      
    }

    
    public void thumbnailUpdate(ImageReader source, BufferedImage thumb, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
    }
  } 
  
  
  public abstract static class Source {
    
    
    protected String name;
    
    
    private Source() {
    }
    
    
    public Source(String name) {
      this.name = name;
    }
    
    
    protected abstract InputStream open() throws IOException;
    
    
    public String toString() {
      return name!=null ? name : super.toString();
    }
    
  } 
  
  
  public static class RelativeSource extends Source {
    
    private Origin origin;
    
    
    public RelativeSource(Origin origin, String name) {
      super(name);
      this.origin = origin;
    }
    
    
    protected InputStream open() throws IOException {
      return origin.open(name);
    }
    
    
    public boolean equals(Object other) {
      if (!(other instanceof RelativeSource))
        return false;
      RelativeSource that = (RelativeSource)other;
      return this.origin.equals(that.origin) && this.name.equals(that.name);
    }
    
    
    public int hashCode() {
      return origin.hashCode() + name.hashCode();
    }
    
  } 
  
  
  public static class FileSource extends Source {
    
    private File file;
    
    
    public FileSource(File file) {
      super(file.getAbsolutePath());
      this.file = file;
    }
    
    
    protected InputStream open() throws IOException {
      return new FileInputStream(file);
    }
    
    
    public boolean equals(Object other) {
      if (!(other instanceof FileSource))
        return false;
      FileSource that = (FileSource)other;
      return this.file.equals(that.file);
      
    }
    
    
    public int hashCode() {
      return file.hashCode();
    }
    
  } 
  
  
  public static class ByteArraySource extends Source {
    
    private byte[] data;
    
    
    public ByteArraySource(byte[] data) {
      super(data.length+" bytes");
      this.data = data;
    }
    
    protected InputStream open() throws IOException {
      return new ByteArrayInputStream(data);
    }
    
    public int hashCode() {
      return data.hashCode();
    }
    
    public boolean equals(Object obj) {
      return data==obj;
    }
    
  } 
  
  
  private static class Worker implements Runnable {
    
    
    private Stack stack = new Stack();
    
    
    private Runnable current = null;
    
    
    Worker() {
      Thread t = new Thread(this);
      
      t.setPriority(Thread.NORM_PRIORITY);
      t.setDaemon(true);
      t.start();
    }
    
    
    synchronized void add(Runnable r) {
      if (current!=r && !stack.contains(r))
        stack.push(r);
      notify();
    }
    
    
    public void run() {
      
      while (true) try {
        
        next();
        current.run();
        current = null; 
        
      } catch (Throwable t) {}
      
    }
    
    
    private synchronized void next() throws InterruptedException {
      while (true) {
        
        if (!stack.isEmpty()) {
          current = (Runnable)stack.pop();
          return;
        }
        wait();
        
      }
    }
  
  } 
  
} 
