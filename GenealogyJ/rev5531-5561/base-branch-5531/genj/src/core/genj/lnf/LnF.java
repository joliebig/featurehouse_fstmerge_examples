
package genj.lnf;

import genj.util.EnvironmentChecker;
import genj.util.Registry;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class LnF {
  
  private static Logger LOG = Logger.getLogger("genj.lnf");
  
  
  static private final String 
    LNF_PROPERTIES = "lnf.properties",
    LNF_DIR        = "./lnf";

  
  private static LnF[] instances;
  
  
  private String name,type,archive,url,version,theme;
  private ClassLoader cl;
  private LookAndFeel instance;
  
  
  public static LnF[] getLnFs() {
    
    
    if (instances!=null)
      return instances;

    
    ArrayList result = new ArrayList();    
    
    
    result.add(new LnF("System Default", UIManager.getSystemLookAndFeelClassName(), "", "", null, null));
    
    
    try {
      
      
      Registry r = new Registry(new FileInputStream(new File(getLnFDir(), LNF_PROPERTIES)));
      int num = r.get("lnf.count",0);
      
      
      for (int d=0; d<num; d++) {

        String prefix = "lnf."+(d+1);

        
        String
          name    = r.get(prefix+".name",""),
          type    = r.get(prefix+".type",""),
          url     = r.get(prefix+".url",""),
          version = r.get(prefix+".version",""),
          archive = r.get(prefix+".jar",(String)null);
        
        if (name.length()==0)
          continue;
      
        
        int i = name.indexOf('(');
        if (i>0)
          name = name.substring(0,i);

        
        String[] ts = r.get(prefix+".themes",new String[0]);
        if (ts.length>0) {
          for (int t=0;t<ts.length;t++) {
            result.add(new LnF(name,type,url,version,archive, ts[t]));
          }
        } else {
          result.add(new LnF(name,type,url,version,archive, null));
        }
      }   

    } catch (IOException ioe) {
    }

    
    result.add(new LnF("Java Default", UIManager.getLookAndFeel().getClass().getName(), "", "", null, null));

    
    instances = (LnF[])result.toArray(new LnF[result.size()]);   
    
    
    return instances;
  }
  
  
  private static String getLnFDir() {
    return EnvironmentChecker.getProperty(
      LnF.class,
      new String[]{ "genj.lnf.dir"},
      LNF_DIR,
      "read lnf.properties"
    );
  }
  
  
  private LnF(String name, String type, String url, String version, String archive, String theme) {
    
    
    this.name = name;
    this.type = type;
    this.url  = url ;
    this.version = version;
    this.archive = archive;
    this.theme = theme;      
    
    
    LOG.info("Found Look&Feel "+name+" type="+type+" version="+version+" url="+url+" archive="+archive+" theme="+theme);
    
    
  }
  
  
  private LookAndFeel getInstance() throws Exception {
    
    
    if (instance==null) 
      instance = (LookAndFeel)cl.loadClass(type).newInstance();      
    
    
    if (instance.getClass()==javax.swing.plaf.metal.MetalLookAndFeel.class) {
      javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(
        new javax.swing.plaf.metal.DefaultMetalTheme()
      );
    }

    
    if (theme!=null) {
      
      
      String themejar =  new File(getLnFDir(), getTheme()).getAbsolutePath();
      
      
      System.setProperty("skinlf.themepack", themejar);
    
      
    }
    
    
    return instance;
  }
  
  
  private ClassLoader getCL() throws MalformedURLException {
    if (cl!=null) return cl;
    if (archive==null) {
      cl = getClass().getClassLoader();
    } else {
      URL urlArchive = new URL("file", "", new File(getLnFDir(), archive).getAbsolutePath());
      cl = new URLClassLoader(new URL[]{urlArchive}, getClass().getClassLoader());
    }
    return cl;
  }
  
  
  public String toString() {
    
    if (theme==null)
      return name;
    
    String s = theme;
    int i = s.lastIndexOf('/');
    if (i>0)
      s = s.substring(i+1);
    return name + '(' + s + ')';
  }
  
  
  public String getName() {
    return name;
  }
  
  
  public String getType() {
    return type;
  }
  
  
  public String getArchive() {
    return archive;
  }
  
  
  public String getTheme() {
    return theme;
  }

  
  public boolean apply(final List rootComponents) {
    
    
    String prefix = "Look and feel #"+this+" of type "+type;
    
    
    try {
      
      UIManager.getLookAndFeelDefaults().put("ClassLoader",getCL());
      UIManager.getDefaults().put("ClassLoader",getCL());
      
      UIManager.setLookAndFeel(getInstance());
      
    } catch (ClassNotFoundException cnfe) {
      LOG.warning(prefix+" is not accessible (ClassNotFoundException)");
      return false;
    } catch (ClassCastException cce) {
      LOG.warning(prefix+" is not a valid LookAndFeel (ClassCastException)");
      return false;
    } catch (MalformedURLException mue) {
      LOG.warning(prefix+" doesn't point to a valid archive (MalformedURLException)");
      return false;
    } catch (UnsupportedLookAndFeelException e) {
      LOG.warning(prefix+" is not supported on this platform (UnsupportedLookAndFeelException)");
      return false;
    } catch (Throwable t) {
      LOG.warning(prefix+" couldn't be set ("+t.getClass()+")");
      return false;
    }
    
    
    if (rootComponents!=null) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          Iterator e = rootComponents.iterator();
          while (e.hasNext()) try {
            SwingUtilities.updateComponentTreeUI((Component)e.next());
          } catch (Throwable t) {
          }
        }
      });
    }
    
    
    return true;
  }

} 
