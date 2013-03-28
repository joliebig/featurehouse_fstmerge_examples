
package genj.print;

import genj.option.Option;
import genj.option.PropertyOption;
import genj.renderer.DPI;
import genj.renderer.EmptyHintKey;
import genj.util.Dimension2d;
import genj.util.EnvironmentChecker;
import genj.util.Resources;
import genj.util.Trackable;
import genj.util.WordBuffer;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;


 class PrintTask implements Printable, Trackable {

  
   final static DocFlavor FLAVOR = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
   final static Resources RESOURCES = Resources.get(PrintTask.class);
   final static Logger LOG = Logger.getLogger("genj.print");
  
  
  private PrintService service;

  
  private PrintRenderer renderer;

  
  private int page = 0;

  
  private Throwable throwable;

  
  private String title;
  
  
  private PrintRegistry registry;

  
  private String progress;
  
  
  private PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
  
  
  private Dimension pages;
  private double zoomx = 1.0D, zoomy = 1.0D;
  private boolean printEmpties = false;

  
  public PrintTask(String title, PrintRenderer renderer) throws PrintException {
  
    
    this.renderer = renderer;
    this.title = title;
    this.registry = PrintRegistry.get(renderer);
    
    
    PrintService service = registry.get(getDefaultService());
    if (!service.isDocFlavorSupported(FLAVOR))
      service = getDefaultService();
    setService(service);

    
    attributes.add(new JobName("GenJ", null));
    
    
    registry.get(attributes);
    
    
    String file = EnvironmentChecker.getProperty("genj.print.file", null, "Print file output");
    if (file!=null)
      attributes.add(new Destination(new File(file).toURI()));

    
  }
  
   String getTitle() {
    return title;
  }

  
  protected PrintService getDefaultService() throws PrintException {
    
    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
    if (service!=null) {
      
      if (service.isDocFlavorSupported(FLAVOR))
        return service;
      LOG.info("Default print service not supported");
    } else {
      LOG.info("No default print service available (are you running CUPS?)");
    }
    
    
    PrintService[] suitables = PrintServiceLookup.lookupPrintServices(FLAVOR, null);
    if (suitables.length==0)
      throw new PrintException("Couldn't find any suitable printer");
    
    return suitables[0];
  }
  
  
  protected PrintService[] getServices() {
    return PrintServiceLookup.lookupPrintServices(FLAVOR, null);    
  }

  
   PrintRequestAttributeSet getAttributes() {
    return attributes;
  }

  
   void setService(PrintService set) {
    
    service = set;
    
    registry.put(service);
  }

  
   PrintService getService() {
    return service;
  }

  
   DPI getResolution() {
    
    return new DPI (72,72);





  }
  
  
   Rectangle2D getPrintable() {
    
    OrientationRequested orientation = (OrientationRequested)getAttribute(OrientationRequested.class);
    MediaPrintableArea printable = (MediaPrintableArea)getAttribute(MediaPrintableArea.class);
    Rectangle2D result = new Rectangle2D.Float();
    
    if (orientation==OrientationRequested.LANDSCAPE||orientation==OrientationRequested.REVERSE_LANDSCAPE) {
      
      Dimension2D size = getPageSize();
      result.setRect(
          size.getWidth()-printable.getHeight(MediaSize.INCH)-printable.getY(MediaSize.INCH), 
          printable.getX(MediaSize.INCH),
          printable.getHeight(MediaSize.INCH),
          printable.getWidth(MediaSize.INCH)
        );
    } else {
      
      result.setRect(
        printable.getX(MediaSize.INCH),
        printable.getY(MediaSize.INCH), 
        printable.getWidth(MediaSize.INCH),
        printable.getHeight(MediaSize.INCH)
      );
    }    
    return result;
  }

  
   Rectangle2D getPrintable(Rectangle2D page) {
    Rectangle2D printable = getPrintable();
    return new Rectangle2D.Double(
      page.getMinX() + printable.getX(), 
      page.getMinY() + printable.getY(), 
      printable.getWidth(), 
      printable.getHeight()
    );
  }
  
  
   Dimension2D getPageSize() {
    
    OrientationRequested orientation = (OrientationRequested)getAttribute(OrientationRequested.class);
    Media media = (Media)getAttribute(Media.class);
    
    
    MediaSize size = null;
    if (media instanceof MediaSizeName) 
      size = MediaSize.getMediaSizeForName((MediaSizeName)media);

    
    if (size==null) {
      try {
        size = MediaSize.getMediaSizeForName((MediaSizeName)media.getClass().getMethod("getStandardMedia").invoke(media));
        LOG.fine("Got MediaSize "+size+" from "+media+".getStandardMedia()");
      } catch (Throwable t) {
        
      }
    }

    
    if (size==null) {
      LOG.warning("Need MediaSize, got unknown MediaSizeName, MediaTray or MediaName '"+media+"' - using A4");
      attributes.add(MediaSizeName.ISO_A4);
      size = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A4);
    }
    
    Dimension2D result = new Dimension2d();

    double w,h;
    if (orientation==OrientationRequested.LANDSCAPE||orientation==OrientationRequested.REVERSE_LANDSCAPE) {
      result.setSize(size.getY(MediaSize.INCH), size.getX(MediaSize.INCH));
    } else {
      result.setSize(size.getX(MediaSize.INCH), size.getY(MediaSize.INCH));
    }    
    return result;
  }
  
  
   void setPages(Dimension pages, boolean fit) {
    
    if (pages.width==0 || pages.height==0)
      throw new IllegalArgumentException("0 not allowed");
    
    this.pages = pages;

    Rectangle2D printable = getPrintable();
    Dimension2D size = getSize();
    
    this.zoomx = pages.width*printable.getWidth()   / size.getWidth();
    this.zoomy = pages.height*printable.getHeight() / size.getHeight();
    
    if (!fit) {
      if (zoomx>zoomy) zoomx=zoomy;
      if (zoomy>zoomx) zoomy=zoomx;
    }
  }
  
   boolean isPrintEmpties() {
    return printEmpties;
  }
  
  
   void setPrintEmpties(boolean set) {
    this.printEmpties  = set;
  }
  
   void setZoom(double zoom) {
    this.zoomx = zoom;
    this.zoomy = zoom;
    this.pages = null;
  }
  
   Dimension2D getSize() {
    return renderer.getSize();
  }
  
   Dimension getPages() {
    if (pages!=null) 
      return pages;
    
    Rectangle2D printable = getPrintable();
    Dimension2D dim = renderer.getSize();
    return new Dimension(
      (int)Math.ceil(dim.getWidth ()*zoomx/printable.getWidth ()),
      (int)Math.ceil(dim.getHeight()*zoomy/printable.getHeight())
    );
  }
  
  
  private String toString(PrintRequestAttributeSet atts) {
    WordBuffer buf = new WordBuffer(",");
    Attribute[] array = attributes.toArray();
    for (int i = 0; i < array.length; i++) 
      buf.append(array[i].getClass().getName()+"="+array[i].toString());
    return buf.toString();
  }
  
  
  private PrintRequestAttribute getAttribute(Class<? extends PrintRequestAttribute> category) {
    
    Object result = attributes.get(category);
    if (result instanceof PrintRequestAttribute)
      return (PrintRequestAttribute)result;
    
    if (!Media.class.isAssignableFrom(category))
      getAttribute(Media.class);
    
    result = service.getDefaultAttributeValue(category);
    
    if (result==null) {
      LOG.finer( "Couldn't find default PrintRequestAttribute for category "+category);
      
      
      result = service.getSupportedAttributeValues(category, null, attributes);
      if (result==null)
        LOG.warning( "Couldn't find supported PrintRequestAttribute for category "+category+" with "+toString(attributes));
      else if (result.getClass().isArray()) {
	      LOG.fine( "Got PrintRequestAttribute values "+Arrays.toString((Object[])result)+" for category "+category);
	      
	      
	      
	      

	      Object[] os = (Object[])result;
	      result = null;
	      for (int i=0;result==null && i<os.length;i++) {
	        if (os[i]!=null && category.isAssignableFrom(os[i].getClass())) {
	          result = os[i];
	          break;
	        }
	      }
      }
    }
    
    if (result==null&&category==Media.class) {
      result = MediaSizeName.ISO_A4;
      LOG.warning("fallback media is "+result);
      attributes.add((Media)result);
    }    
    
    
    
    
    if (result==null&&category==MediaPrintableArea.class) {
      Dimension2D page = getPageSize();
      result = new MediaPrintableArea(1,1,(float)page.getWidth()-2,(float)page.getHeight()-2, MediaPrintableArea.INCH);
      LOG.warning( "Using fallback MediaPrintableArea "+result);
    }
    
    if (result!=null) {
      attributes.add((PrintRequestAttribute)result);
      LOG.fine( "PrintRequestAttribute for category "+category+" is "+result+" with "+toString(attributes));
    } else {
      LOG.warning( "Couldn't find any PrintRequestAttribute for category "+category+" with "+toString(attributes));
    }
    
    return (PrintRequestAttribute)result;
  }

   List<? extends Option> getOptions() {
    return PropertyOption.introspect(renderer);
  }

  
  public void cancelTrackable() {
  }

  
  public int getProgress() {
    return (int) (page / (float) (getPages().width * getPages().height) * 100);
  }

  
  public String getState() {
    return RESOURCES.getString("progress", (page + 1), (getPages().width * getPages().height) );
  }
  
   void print() {
    
    
    registry.put(attributes);

    
    try {
      service.createPrintJob().print(new SimpleDoc(this, FLAVOR, null), attributes);
    } catch (PrintException e) {
      LOG.log(Level.WARNING, "print failed", e);
    }
    
    
    String file = EnvironmentChecker.getProperty("genj.print.file", null, "Print file output");
    if (file!=null)
      try {
        Desktop.getDesktop().open(new File(file));
      } catch (Throwable t) {
        LOG.log(Level.FINE, "can't open "+file, t);
      }
    
  }
  
  
  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
    
    Dimension pages = getPages();
    int
      row = pageIndex/pages.width,
      col = pageIndex%pages.width;
    if (col>=pages.width||row>=pages.height) 
      return NO_SUCH_PAGE;

    page = pageIndex;
    
    
    Graphics2D g2d = (Graphics2D)graphics;
    g2d.setRenderingHint(DPI.KEY, getResolution());
    
    
    print((Graphics2D)graphics, row, col);
    
    
    return PAGE_EXISTS;
  }
  
  
   void print(Graphics2D graphics, int row, int col) {

    
    DPI dpi = DPI.get(graphics);
    
    Rectangle2D pixels = dpi.toPixel(getPrintable());
    
    graphics.translate(pixels.getX()-col*pixels.getWidth(), pixels.getY()-row*pixels.getHeight()); 
    
    Rectangle2D box = new Rectangle2D.Double(
        col*pixels.getWidth(),
        row*pixels.getHeight(),
        pixels.getWidth(),
        pixels.getHeight());
    graphics.clip(box);

    graphics.scale(zoomx, zoomy);

    graphics.setRenderingHint(EmptyHintKey.KEY, true);
    
    renderer.render(graphics);

    
  }

} 