
package genj.print;

import genj.util.Dimension2d;
import genj.util.EnvironmentChecker;
import genj.util.Resources;
import genj.util.Trackable;
import genj.util.WordBuffer;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.util.swing.ProgressWidget;
import genj.util.swing.UnitGraphics;
import genj.window.WindowManager;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.util.Arrays;
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
import javax.swing.Action;
import javax.swing.JComponent;


public class PrintTask extends Action2 implements Printable, Trackable {

  
   final static DocFlavor FLAVOR = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
  
   final static Resources RESOURCES = Resources.get(PrintTask.class);
   final static Logger LOG = Logger.getLogger("genj.print");
  
  
  private JComponent owner;

  
  private PrintService service;

  
  private Printer renderer;

  
  private int page = 0;

  
  private Throwable throwable;

  
  private String title;
  
  
  private PrintRegistry registry;

  
  private String progress;
  
  
  private PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
  
  
  private Dimension cachedPages;

  
  public PrintTask(Printer setRenderer, String setTitle, JComponent setOwner, PrintRegistry setRegistry) throws PrintException {
    
    
    setImage(new ImageIcon(this,"images/Print"));

    
    renderer = setRenderer;
    owner = setOwner;
    title = RESOURCES.getString("title", setTitle);
    registry = setRegistry;

    
    setAsync(Action2.ASYNC_SAME_INSTANCE);
    
    
    PrintService service = registry.get(getDefaultService());
    if (!service.isDocFlavorSupported(FLAVOR))
      service = getDefaultService();
    setService(service);

    
    attributes.add(new JobName(title, null));
    
    
    registry.get(attributes);

    
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

  
   JComponent getOwner() {
    return owner;
  }

  
   void invalidate() {
    
    cachedPages = null;
  }
  
  
   void setService(PrintService set) {
    
    if (service==set)
      return;
    
    service = set;
    
    registry.put(service);
    
    invalidate();
  }

  
   PrintService getService() {
    return service;
  }

  
   Point getResolution() {
    
    return new Point(72,72);





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
  
  
  
   Rectangle2D getPage(int x, int y, float pad) {
    
    Dimension2D size = getPageSize();

    return new Rectangle2D.Double(
       pad + x*(size.getWidth ()+pad), 
       pad + y*(size.getHeight()+pad), 
       size.getWidth(), 
       size.getHeight()
    );
  }
  
  
   Dimension2D getPageSize() {
    
    OrientationRequested orientation = (OrientationRequested)getAttribute(OrientationRequested.class);
    MediaSize media = MediaSize.getMediaSizeForName((MediaSizeName)getAttribute(Media.class));
    
    Dimension2D result = new Dimension2d();
    
    if (orientation==OrientationRequested.LANDSCAPE||orientation==OrientationRequested.REVERSE_LANDSCAPE)
      result.setSize(media.getY(MediaSize.INCH), media.getX(MediaSize.INCH));
    else
      result.setSize(media.getX(MediaSize.INCH), media.getY(MediaSize.INCH));
    
    return result;
  }
  
  
   Dimension getPages() {
    if (cachedPages==null)
      cachedPages = renderer.calcSize(new Dimension2d(getPrintable()), getResolution());
    return cachedPages;
  }
  
  
   Printer getRenderer() {
    return renderer;
  }
  
  
  private String toString(PrintRequestAttributeSet atts) {
    WordBuffer buf = new WordBuffer(",");
    Attribute[] array = attributes.toArray();
    for (int i = 0; i < array.length; i++) 
      buf.append(array[i].getClass().getName()+"="+array[i].toString());
    return buf.toString();
  }
  
  
  private PrintRequestAttribute getAttribute(Class category) {
    
    if (!PrintRequestAttribute.class.isAssignableFrom(category))
      throw new IllegalArgumentException("only PrintRequestAttributes allowed");
    
    Object result = (PrintRequestAttribute)attributes.get(category);
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
      else if (result.getClass().isArray()&&result.getClass().getComponentType()==category) {
	      LOG.finer( "Got PrintRequestAttribute values "+Arrays.toString((Object[])result)+" for category "+category);
	      
	      
	      
	      

	      Object[] os = (Object[])result;
	      result = null;
	      for (int i=0;result==null && i<os.length;i++) 
	        result = os[i];
	    } else {
	      
        LOG.finer( "Got PrintRequestAttribute value "+result+" for category "+category);
	    }
    }
    
    if (result!=null)
      attributes.add((PrintRequestAttribute)result);
    
    return (PrintRequestAttribute)result;
  }
  
  
  protected boolean preExecute() {

    
    PrintWidget widget = new PrintWidget(this);

    
    Action[] actions = { 
        new Action2(RESOURCES, "print"),
        Action2.cancel() 
    };

    
    int choice = WindowManager.getInstance(owner).openDialog("print", title, WindowManager.QUESTION_MESSAGE, widget, actions, owner);

    
    registry.put(attributes);

    
    if (choice != 0 || getPages().width == 0 || getPages().height == 0)
      return false;

    
    String file = EnvironmentChecker.getProperty(this, "genj.print.file", null, "Print file output");
    if (file!=null)
      attributes.add(new Destination(new File(file).toURI()));
    
    
    progress = WindowManager.getInstance(owner).openNonModalDialog(null, title, WindowManager.INFORMATION_MESSAGE, new ProgressWidget(this, getThread()), Action2.cancelOnly(), owner);

    
    return true;
  }

  
  public void actionPerformed(ActionEvent event) {
    try {
      service.createPrintJob().print(new SimpleDoc(this, FLAVOR, null), attributes);
    } catch (PrintException e) {
      throwable = e;
    }
  }

  
  protected boolean postExecute(boolean preExecuteResult) {
    
    WindowManager.getInstance(owner).close(progress);
    
    if (throwable != null) {
      LOG.log(Level.WARNING, "print() threw error", throwable);
      return false;
    }
    
    return true;
  }

  
  public void cancelTrackable() {
    cancel(true);
  }

  
  public int getProgress() {
    return (int) (page / (float) (getPages().width * getPages().height) * 100);
  }

  
  public String getState() {
    return RESOURCES.getString("progress", new String[] { "" + (page + 1), "" + (getPages().width * getPages().height) });
  }

  
  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
    
    Dimension pages = getPages();
    int
      row = pageIndex/pages.width,
      col = pageIndex%pages.width;
    if (col>=pages.width||row>=pages.height) 
      return NO_SUCH_PAGE;

    page = pageIndex;

    
    Point dpi = getResolution();
    
    Rectangle2D printable = getPrintable();
    UnitGraphics ug = new UnitGraphics(graphics, dpi.x, dpi.y);
    ug.pushClip(0,0, printable);

    
    ug.translate(printable.getX(), printable.getY()); 

    
    renderer.renderPage((Graphics2D)graphics, new Point(col, row), new Dimension2d(printable), dpi, false);
    
    
    return PAGE_EXISTS;
  }
  
} 