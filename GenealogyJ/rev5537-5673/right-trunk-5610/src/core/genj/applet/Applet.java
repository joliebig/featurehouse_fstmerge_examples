
package genj.applet;

import genj.Version;
import genj.gedcom.Gedcom;
import genj.io.GedcomReader;
import genj.option.OptionProvider;
import genj.util.EnvironmentChecker;
import genj.util.Origin;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.Trackable;
import genj.util.swing.Action2;
import genj.util.swing.ProgressWidget;
import genj.view.ViewManager;
import genj.window.DefaultWindowManager;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.UIManager;


public class Applet extends java.applet.Applet {
  
  private final static Resources RESOURCES = Resources.get(Applet.class);

  private final static Logger LOG = Logger.getLogger("genj");
  
  
  static final private String[] FACTORIES = new String[]{
    "genj.table.TableViewFactory",
    "genj.tree.TreeViewFactory",
    "genj.timeline.TimelineViewFactory",
    "genj.edit.EditViewFactory",
    "genj.nav.NavigatorViewFactory",
    "genj.entity.EntityViewFactory", 
    "genj.search.SearchViewFactory" 
  };
  
  private final static String[] OPTIONPROVIDERS = {
    "genj.gedcom.Options",
    "genj.renderer.Options"
  };

  
  private boolean isInitialized = false;

  
  public String getAppletInfo() {
    return "GenealogyJ "+Version.getInstance().getBuildString();
  }

  private final static String[] S404FIX = {
    "javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl",
    "javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl",
    "javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl",
    "com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager", "com.sun.org.apache.xalan.internal.xsltc.dom.XSLTCDTMManager"    
  };

  
  public void init() {
    
    
    if (isInitialized)
      return;
    isInitialized = true;
    
    
    
    if (getParameter("404FIX")!=null) try {
      for (int i=0;i<S404FIX.length;)  System.setProperty(S404FIX[i++], S404FIX[i++]);
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "Couldn't apply 404FIX", t);
    }
    
    
    LOG.info(getAppletInfo());

    EnvironmentChecker.log();

    
    setLayout(new BorderLayout());

    
    String gedcom = getParameter("gedcom");
    if (gedcom==null) {
      log(RESOURCES.getString("applet.missing"));
      return;
    }
    
    URL url;
    try {
      log("document base="+getDocumentBase());
      log("gedcom="+gedcom);
      url = new URL(getDocumentBase(), gedcom);
    } catch (MalformedURLException e) {
      log(RESOURCES.getString("applet.missing"));
      return;
    }

    
    log(RESOURCES.getString("applet.loading", url));

    
    new Init(url).trigger();

    
  }
  
  private void log(String msg) {
    showStatus(msg);
    LOG.info(msg);
  }
    
  
  private class Init extends Action2 implements Trackable {

    
    private URL url;

    
    private GedcomReader reader;

    
    private Gedcom gedcom;
    
    
    private Registry registry;
    
    
    private Throwable throwable;

    
    private Init(URL url) {

      
      this.url = url;
      
      
      setAsync(ASYNC_SAME_INSTANCE);

      
    }
    
    
    protected boolean preExecute() {

      
      throwable = null;

      
      ProgressWidget progress = new ProgressWidget(this, getThread());
      progress.setBackground(getBackground());
      
      removeAll();
      add(BorderLayout.NORTH , new JLabel(getAppletInfo()));
      add(BorderLayout.CENTER, progress);
      
      
      return true;
    }
    
    
    protected void execute() {

      
      try {
        
        
        Origin origin = Origin.create(url);
        
        
        try {
          registry = new Registry(origin.open("genj.properties"));
          
          
          
          OptionProvider.setOptionProviders(OPTIONPROVIDERS);
          OptionProvider.getAllOptions(registry);
          
        } catch (Throwable t) {
          LOG.log(Level.INFO, "Couldn't load genj.properties from "+origin+" ("+t.getMessage()+")");
          registry = new Registry();
        }
        
        
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
        }
        
        
        reader = new GedcomReader(origin);
        reader.setPassword(Gedcom.PASSWORD_UNKNOWN);
         
        gedcom = reader.read();
        Thread.sleep(100);
        
      } catch (Throwable t) {
        throwable = t;
        LOG.log(Level.SEVERE, "Encountered throwable", throwable);
      }

      
    }
    
    
    protected void postExecute(boolean preExecuteResult) {

      
      WindowManager winMgr = new DefaultWindowManager(registry, Gedcom.getImage());
      
      
      if (throwable!=null) {
        
        Action[] actions = { new Action2("Retry"),  Action2.cancel() };
        String msg =  RESOURCES.getString( throwable instanceof FileNotFoundException ? "applet.404" :"applet.ioerror", url);
        int rc = winMgr.openDialog(null, "Error", WindowManager.ERROR_MESSAGE, msg, actions, Applet.this);        
        
        if (rc==0) trigger();
        
      } else {
        
        log(RESOURCES.getString("applet.ready"));
        
        
        ViewManager vmanager = new ViewManager(winMgr, FACTORIES);

        
        removeAll();
        add(BorderLayout.CENTER, new ControlCenter(vmanager, gedcom));
        invalidate();
        validate();
        repaint();
    
      }

      
    }

    
    public void cancelTrackable() {
      if (reader!=null) reader.cancelTrackable();
    }
    
    
    public int getProgress() {
      return reader!=null ? reader.getProgress() : 0;
    }

    
    public String getState() {
      return reader!=null ? reader.getState() : RESOURCES.getString("applet.connecting");
    }

    
  } 
  
} 
