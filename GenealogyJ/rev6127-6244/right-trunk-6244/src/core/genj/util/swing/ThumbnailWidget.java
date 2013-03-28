
package genj.util.swing;

import genj.io.InputSource;
import genj.io.InputSource.FileInput;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileSystemView;


public class ThumbnailWidget extends JComponent {
  
  private final static int MIN_THUMBNAIL = 32;
  
  public final static ImageIcon 
    IMG_THUMBNAIL = new ImageIcon(ThumbnailWidget.class, "File.png"),
    IMG_ZOOM_FIT = new ImageIcon(ThumbnailWidget.class, "ZoomFit.png"),
    IMG_ZOOM_ALL = new ImageIcon(ThumbnailWidget.class, "ZoomAll.png");
  
  private final static Logger LOG = Logger.getLogger("genj.util.swing");
  private final static BlockingQueue<Runnable> executorQueue = new LinkedBlockingDeque<Runnable>();
  private final static Executor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, executorQueue);

  private int thumbSize = 64, thumbPadding = 10;
  private Insets thumbBorder = new Insets(4,4,20,4);
  private List<Thumbnail> thumbs = new ArrayList<Thumbnail>();
  private Timer repaint = new Timer(100, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      repaint();
    }
  });
  private Thumbnail selection = null;
  private Action2 
    zoomFit = new Fit(), 
    zoomAll = new All(),
    zoomOne = new One();
  private Point topLeft = new Point(0,0);

  
  public ThumbnailWidget() {
    
    setRequestFocusEnabled(true);

    EventHandler handler = new EventHandler();
    addMouseWheelListener(handler);
    addMouseListener(handler);
    addMouseMotionListener(handler);
    new DropTarget(this, handler);
    
    ToolTipManager.sharedInstance().registerComponent(this);
    setBackground(Color.LIGHT_GRAY);
  }
  
  public Action2 getFitAction() {
    return zoomFit;
  }
  
  public Action2 getOneAction() {
    return zoomOne;
  }
  
  public Action2 getAllAction() {
    return zoomAll;
  }
  
  
  public void clear() {
    setSources(null);
  }

  
  public InputSource getSelection() {
    return selection!=null ? selection.source : null;
  }
  
  public void setSource(InputSource source) {
    if (source==null)
      clear();
    else
      setSources(Collections.singletonList(source));
  }
  
  
  public void setSources(List<InputSource> sources) {

    if (selection!=null)
      unselect(selection.source);

    
    int oldSize = thumbs.size();
    thumbs.clear();
    if (sources!=null) for (InputSource source : sources)
      thumbs.add(new Thumbnail(source));
    
    
    firePropertyChange("content", oldSize, thumbs.size());
    
    
    showAll();
    
  }

  
  public void addSource(InputSource source) {
    
    
    int oldSize = thumbs.size();
    Thumbnail thumb = new Thumbnail(source);
    thumbs.add(thumb);
    firePropertyChange("content", oldSize, thumbs.size());
    
    
    Thumbnail old = selection;
    selection = thumb;
    firePropertyChange("selection", old!=null ? old.getSource() : null, selection.getSource());
    
    
    showAll();
  }
  
  
  public void removeSource(InputSource source) {
    
    unselect(source);
    
    
    int oldSize = thumbs.size();
    for (Thumbnail thumb : new ArrayList<Thumbnail>(thumbs)) {
      if (thumb.source==source)
        thumbs.remove(thumb);
    }
    
    
    firePropertyChange("content", oldSize, thumbs.size());
    
    
    showAll();
    
  }
  
  @Override
  public String getToolTipText() {
    
    return super.getToolTipText();
  }
  
  @Override
  public String getToolTipText(MouseEvent event) {
    Thumbnail thumb = getThumb(event.getPoint());
    return thumb!=null ? getToolTipText(thumb.source) : null;
  }
  
  
  public String getToolTipText(InputSource source) {
    return source.getName();
  }
  
  private void unselect(InputSource source) {
    if (selection==null || selection.source!=source)
      return;
    
    Thumbnail oldSelection = selection;
    selection = null;
    firePropertyChange("selection", oldSelection.getSource(), null);
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    
    Dimension d = getDimension();
    topLeft.x = (width -d.width )/2;
    topLeft.y = (height-d.height)/2;
  }
  
  private void scrollTo(int x, int y) {
    Dimension d = getDimension();
    if (d.width<getWidth())
      topLeft.x = (getWidth()-d.width)/2;
    else
      topLeft.x = Math.max( -(d.width-getWidth()), Math.min( 0, x));

    if (d.height<getHeight())
      topLeft.y = (getHeight()-d.height)/2;
    else
      topLeft.y = Math.max( -(d.height-getHeight()), Math.min( 0, y));
    repaint();
  }

  
  protected void handleDrop(List<File> files) {
    
  }
  
  
  private class EventHandler extends MouseAdapter implements MouseWheelListener, DropTargetListener {
    
    private Point start = new Point();
    
    public void mouseWheelMoved(MouseWheelEvent e) {
      
      if (e.isControlDown()) {
        Point mouse = e.getPoint();
        Dimension dim = getDimension();
        double x = (mouse.x-topLeft.x)/(double)dim.width;
        double y = (mouse.y-topLeft.y)/(double)dim.height;
        thumbSize = Math.max(Math.min(getShowAllThumbSize(),getMinThumbSize()), thumbSize - e.getWheelRotation() * Math.max(MIN_THUMBNAIL, thumbSize/2) );
        dim = getDimension();
        scrollTo(-(int)(x*dim.width-mouse.x), -(int)(y*dim.height-mouse.y));
        return;
      }
      
      if (e.isShiftDown())
        scrollTo(topLeft.x-getWidth()*e.getWheelRotation(), topLeft.y);
      else
        scrollTo(topLeft.x, topLeft.y -= getHeight()*e.getWheelRotation());
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      scrollTo(
        topLeft.x + (e.getPoint().x - start.x),
        topLeft.y = topLeft.y + (e.getPoint().y - start.y)
      );
      start.setLocation(e.getPoint());
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
      setCursor(null);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
      
      ThumbnailWidget.this.requestFocusInWindow();
      
      start.setLocation(e.getPoint());
      
      Thumbnail thumb = getThumb(e.getPoint());
      if (thumb!=null) {
        if (selection==thumb && e.isControlDown())
          select(null);
        else
          select(thumb);
      }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
      
      if (e.getClickCount()!=2)
        return;
      
      Thumbnail thumb = getThumb(e.getPoint());
      if (thumb!=null&&thumb==selection)
        showSelection();
      
    }
    
    
    public void dragEnter(DropTargetDragEvent dtde) {
      if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        dtde.acceptDrag(dtde.getDropAction());
      else
        dtde.rejectDrag();
    }
     
    
    @SuppressWarnings("unchecked")
    public void drop(DropTargetDropEvent dtde) {
      try {
        dtde.acceptDrop(dtde.getDropAction());
        
        handleDrop((List<File>)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
        
        dtde.dropComplete(true);
        
      } catch (Throwable t) {
        dtde.dropComplete(false);
      }
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }
      
  } 
  
  
  public void showOne() {
    if (selection==null||selection.size.width==0||selection.size.height==0)
      return;
    
    Point center = new Point(
      getWidth()/2,
      getHeight()/2
    );
    
    Rectangle r = getRectangle(selection);
    double cx = 0.5, cy = 0.5;
    if (r.contains(center)) {
      cx = (center.x-r.x) / r.getWidth ();
      cy = (center.y-r.y) / r.getHeight();
    }
    
    thumbSize = Math.max(selection.size.width, selection.size.height);
    topLeft.setLocation(0,0);
    r = getRectangle(selection);
    
    scrollTo(-(int)(r.x+r.width*cx-getWidth()/2),-(int)(r.y+r.height*cy-getHeight()/2));
  }
  
  
  public void showSelection() {
    if (selection==null)
      return;
    
    if (selection.size.width==0||selection.size.height==0)
      return;
    Dimension size = fit(selection.size, getSize());
    thumbSize = Math.max(size.width, size.height);
    topLeft.setLocation(0,0);
    Rectangle r = getRectangle(selection);
    scrollTo( -(r.x+thumbBorder.left-(getWidth()-size.width)/2), -(r.y+thumbBorder.top+(getHeight()-size.height)/2) );
  }
  
  
  public void showAll() {
    
    if (getWidth()==0||getHeight()==0) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          showAllImpl();
        }
      });
      return;
    }
    showAllImpl();
  }
  
  private void showAllImpl() {
    if (thumbs.size()==1)
      thumbSize = Math.min(getShowAllThumbSize(), getMinThumbSize());
    else
      thumbSize = getShowAllThumbSize();
    scrollTo(0,0);
  }
  
  private int getMinThumbSize() {
    int min = Integer.MAX_VALUE;
    
    for (Thumbnail thumb :thumbs) {
      if (thumb.size.width>0&&thumb.size.height>0) 
        min = Math.min(min, Math.max(thumb.size.width, thumb.size.height));
    }
    return min;
  }
  
  private int getShowAllThumbSize() {
    Dimension rc = getRowsCols();
    if (rc.width==0||rc.height==0) 
      return MIN_THUMBNAIL;
    int sizex = Math.max(MIN_THUMBNAIL, getWidth ()/rc.width  - thumbBorder.left-thumbBorder.right-thumbPadding);
    int sizey = Math.max(MIN_THUMBNAIL, getHeight()/rc.height - thumbBorder.top-thumbBorder.bottom-thumbPadding);
    return Math.min(sizex, sizey);
  }
  
  private void select(Thumbnail thumb) {
    
    Thumbnail old = selection;
    if (old==thumb)
      return;
    
    
    selection = thumb;

    
    repaint();
    firePropertyChange("selection", old!=null ? old.getSource() : null, selection!=null ? selection.getSource() : null);
  }
  
  private Rectangle getRectangle(Thumbnail find) {
    
    Dimension rc = getRowsCols();
    
    int row = 0, col = 0;
    for (Thumbnail thumb : thumbs) {
      
      if (find==thumb)
        return new Rectangle(
          topLeft.x + col*(thumbBorder.left+thumbSize+thumbBorder.right +thumbPadding) + thumbPadding/2, 
          topLeft.y + row*(thumbBorder.top +thumbSize+thumbBorder.bottom+thumbPadding) + thumbPadding/2,
          thumbBorder.left+thumbSize+thumbBorder.right,
          thumbBorder.top +thumbSize+thumbBorder.bottom
        );    
      
      
      col = (++col) % rc.width;
      if (col == 0)
        row++;
    }
    
    throw new IllegalArgumentException("unknown thumbnail");
  }
  
  private Thumbnail getThumb(Point pos) {
    
    Dimension rc = getRowsCols();
    
    int row = 0, col = 0;
    for (Thumbnail thumb : thumbs) {

      Rectangle r = new Rectangle(
        topLeft.x + col*(thumbBorder.left+thumbSize+thumbBorder.right +thumbPadding) + thumbPadding/2, 
        topLeft.y + row*(thumbBorder.top +thumbSize+thumbBorder.bottom+thumbPadding) + thumbPadding/2,
        thumbBorder.left+thumbSize+thumbBorder.right,
        thumbBorder.top +thumbSize+thumbBorder.bottom
      );    
      
      if (r.contains(pos))
        return thumb;

      
      col = (++col) % rc.width;
      if (col == 0)
        row++;
    }
    
    return null;
  }

  private Dimension getDimension() {
    Dimension rowsCols = getRowsCols();
    return new Dimension(
      rowsCols.width*(thumbSize+thumbBorder.left+thumbBorder.right+thumbPadding), 
      rowsCols.height*(thumbSize+thumbBorder.top+thumbBorder.bottom+thumbPadding)
    );
  }
  
  private Dimension getRowsCols() {
    if (thumbs.isEmpty())
      return new Dimension();
    int cols = (int) Math.ceil(Math.sqrt(thumbs.size()));
    int rows = (int) Math.ceil(thumbs.size() / (float) cols);
    return new Dimension(cols,rows);
  }
  
  private Image getFallback(InputSource source) {
    
    ImageIcon result = IMG_THUMBNAIL;
    
    if (source instanceof FileInput) {
      File file = ((FileInput)source).getFile();
   	  if (file.isFile()) try {
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon( file );
        if (icon!=null)
          result = new ImageIcon(icon);
   	  } catch (Throwable t) {
        
   	  }
    }
    
    return result.getImage();
  }

  @Override
  public void paint(Graphics g) {

    Graphics2D g2d = (Graphics2D) g;

    Dimension d = getSize();
    g.setColor(getBackground());
    g.fillRect(0, 0, d.width, d.height);

    int cols = (int) Math.ceil(Math.sqrt(thumbs.size()));
    int rows = (int) Math.ceil(thumbs.size() / (float) cols);

    int row = 0, col = 0;
    for (Thumbnail thumb : thumbs) {
      
      Point p = new Point(
        topLeft.x + col*(thumbSize+thumbBorder.left+thumbBorder.right+thumbPadding) + thumbPadding/2, 
        topLeft.y + row*(thumbSize+thumbBorder.top+thumbBorder.bottom+thumbPadding) + thumbPadding/2
      );

      
      g.setColor(Color.DARK_GRAY);
      g2d.fill(new Rectangle(p.x+thumbBorder.left + thumbSize + thumbBorder.right, p.y+2, 2, thumbBorder.top  + thumbSize + thumbBorder.bottom));
      g2d.fill(new Rectangle(p.x+2, p.y+thumbBorder.top + thumbSize + thumbBorder.bottom, thumbBorder.left + thumbSize + thumbBorder.right, 2));
      g.setColor(Color.WHITE);
      g2d.fill(new Rectangle(p.x, p.y, thumbBorder.left + thumbSize + thumbBorder.right, thumbBorder.top  + thumbSize + thumbBorder.bottom));
      g.setColor(Color.BLACK);
      GraphicsHelper.render(g2d, thumb.getName(), new Rectangle(
          p.x+thumbBorder.left, p.y + thumbBorder.top+thumbSize, thumbSize, thumbBorder.bottom),
          0.5,0.5
      );
      
      
      if (thumb==selection) {
        g.setColor(Color.BLUE);
        g2d.draw(new Rectangle(p.x, p.y, thumbBorder.left + thumbSize + thumbBorder.right, thumbBorder.top  + thumbSize + thumbBorder.bottom));
      }

      
      Rectangle content = new Rectangle(p.x + thumbBorder.left, p.y + thumbBorder.top, thumbSize,thumbSize);
      if (!thumb.render(g2d, content)) {
        
        Validation v = new Validation(thumb);
        if (!executorQueue.contains(v))
          executor.execute(v);
      }

      
      col = (++col) % cols;
      if (col == 0)
        row++;
    }
    
  } 
  
  private void center(Point pos) {
    






    
  }
  
  class Validation implements Runnable {
    
    private Thumbnail thumb;

    Validation(Thumbnail thumb) {
      this.thumb = thumb;
    }
    
    @Override
    public void run() {
      thumb.validate();
    }
    
    @Override
    public boolean equals(Object obj) {
      return obj instanceof Validation ? ((Validation)obj).thumb == thumb : false;
    }
    
    @Override
    public int hashCode() {
      return thumb.hashCode();
    }
  }
  
  private static Rectangle grow(Rectangle r, int by) {
    return new Rectangle(r.x-by/2, r.y-by/2, r.width+by, r.height+by);
  }

  private static Rectangle center(Rectangle a, Rectangle b) {
    return new Rectangle(b.x + (b.width - a.width) / 2, b.y + (b.height - a.height) / 2, a.width, a.height);
  }

  private static Dimension fit(Dimension a, Dimension b) {
    float scale = Math.min(b.width / (float) a.width, b.height / (float) a.height);
    return new Dimension((int) (a.width * scale), (int) (a.height * scale));
  }

  
  private class Thumbnail implements IIOReadUpdateListener {

    private InputSource source;
    private SoftReference<Image> image = new SoftReference<Image>(null);
    private Dimension size = new Dimension();
    private Dimension imageSize = new Dimension();
    private Rectangle imageView = new Rectangle();
    private Rectangle renderDest = new Rectangle();
    private Rectangle renderSource = new Rectangle();

    public Thumbnail(InputSource source) {
      this.source = source;
    }
    
    String getName() {
      return source.getName();
    }
    
    InputSource getSource() {
      return source;
    }

    private synchronized boolean isValid() {
      
      if (image.get() == null)
        return false;
      
      if ( (imageSize.width<size.width&&imageSize.width<renderDest.width) 
          || (imageSize.height<size.height&&imageSize.height<renderDest.height))
        return false;
      
      
      return true;
    }

    synchronized boolean render(Graphics2D g, Rectangle bounds) {

      
      renderDest.setBounds(bounds);

      
      if (!g.getClipBounds().intersects(renderDest))
        return true;

      
      if (size.width <= 0 || size.height <= 0)
        return false;

      
      renderSource.setBounds(0, 0, size.width, size.width);

      
      Image img = image.get();
      if (img==null || imageSize.width == 0 || imageSize.height == 0)
        return false;

      
      bounds = center(new Rectangle(fit(size, bounds.getSize())), bounds);

      
      Shape clip = g.getClip();
      g.clip(bounds);
      draw(img, g, bounds.x, bounds.y, bounds.width, bounds.height, 0, 0, imageSize.width, imageSize.height);
      g.setClip(clip);
      
      
      return isValid();
    }
    
    void draw(Image img, Graphics2D g, float sx, float sy, float sw, float sh, float dx, float dy, float dw, float dh) {
      g.drawImage(img, (int) sx, (int) sy, (int) (sx + sw), (int) (sy + sh), (int) dx, (int) dy, (int) (dx + dw), (int) (dy + dh), null);
    }

    void validate() {

      if (isValid()) 
        return;

      LOG.finer("Loading " + source.getName());

      
      InputStream in = null;
      ImageReader reader = null;
      try {

        in = source.open();
        ImageInputStream iin = ImageIO.createImageInputStream(in);

        Iterator<ImageReader> iter = ImageIO.getImageReaders(iin);
        if (!iter.hasNext())
          throw new IOException("no suiteable image reader for " + source.getName());

        reader = (ImageReader) iter.next();
        reader.setInput(iin, false, false);
        reader.addIIOReadUpdateListener(this);

        ImageReadParam param = reader.getDefaultReadParam();
        synchronized (this) {
          size.setSize(reader.getWidth(0), reader.getHeight(0));

          
          if (renderSource.width == 0 || renderSource.height == 0)
            return;

          
          
          if (param.canSetSourceRenderSize()) {
            param.setSourceRenderSize(fit(size, renderSource.getSize()));
          } else {
            param.setSourceSubsampling(Math.max(1, (int) Math.floor(size.width / renderDest.width)), Math.max(1, (int) Math.floor(size.height / renderDest.height)), 0, 0);
          }
        }
        
        
        
        

        Image img = reader.read(0, param);
        
        synchronized (this) {
          image = new SoftReference<Image>(img);
          imageSize.setSize(img.getWidth(null), img.getHeight(null));
        }

      } catch (Throwable t) {
        if (LOG.isLoggable(Level.FINER))
          LOG.log(Level.FINER, "Loading " + source.getName() + " failed", t);
        else
          LOG.log(Level.FINE, "Loading " + source.getName() + " failed");

        
        synchronized (this) {
          Image i = getFallback(source);
          image = new SoftReference<Image>(i);
          size.setSize(i.getWidth(null), i.getHeight(null));
          imageSize.setSize(size);
          imageView.setBounds(0, 0, size.width, size.height);
        }

      } finally {
        repaint.stop();
        try {
          reader.dispose();
        } catch (Throwable t) {
        }
        try {
          in.close();
        } catch (Throwable t) {
        }

        repaint();
      }

      
    }

    public synchronized void imageUpdate(ImageReader source, BufferedImage img, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
      if (image.get()==null) synchronized (this) {
        image = new SoftReference<Image>(img);
        imageSize.setSize(img.getWidth(null), img.getHeight(null));
        repaint.start();
      }
    }

    public void passComplete(ImageReader source, BufferedImage theImage) {
    }

    public void passStarted(ImageReader source, BufferedImage theImage, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {
    }

    public void thumbnailPassComplete(ImageReader source, BufferedImage theThumbnail) {
    }

    public void thumbnailPassStarted(ImageReader source, BufferedImage theThumbnail, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {
    }

    public void thumbnailUpdate(ImageReader source, BufferedImage theThumbnail, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
    }

  } 

  
  private class Fit extends Action2 implements PropertyChangeListener {
    public Fit() {
      setImage(IMG_ZOOM_FIT);
      setEnabled(false);
      ThumbnailWidget.this.addPropertyChangeListener("selection", this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      showSelection();
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      setEnabled(selection!=null);
    }
  }

  
  private class One extends Action2 implements PropertyChangeListener {
    public One() {
      setText("1:1");
      setEnabled(false);
      ThumbnailWidget.this.addPropertyChangeListener("selection", this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      showOne();
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      setEnabled(selection!=null);
    }
  }

  
  private class All extends Action2 implements PropertyChangeListener {
    public All() {
      setImage(IMG_ZOOM_ALL);
      setEnabled(false);
      ThumbnailWidget.this.addPropertyChangeListener("content", this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      showAll();
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      setEnabled(!thumbs.isEmpty());
    }
  }
  
}
