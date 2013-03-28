
package genj.app;

import genj.lnf.LnF;
import genj.option.Option;
import genj.option.OptionProvider;
import genj.option.PropertyOption;
import genj.util.EnvironmentChecker;
import genj.util.Resources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.UIManager;


public class Options extends OptionProvider {
  
  private final static Logger LOG = Logger.getLogger("genj.app");

  
  private final static String SWING_RESOURCES_KEY_PREFIX = "swing.";

  
  private final static Options instance = new Options();

  
  private Resources resources;

  
  private int maxLogSizeKB = 128;

  
  private int lookAndFeel = -1;

  
  private int language = -1;

  
  public boolean isWriteBOM = false;

  
  private static String[] languages;

  
  private final static String[] codes = findCodes();

  private static String[] findCodes() {

    
    
    TreeSet<String> result = new TreeSet<String>();
    result.add("en");

    
    File[] dirs = new File(EnvironmentChecker.getProperty("genj.language.dir", "language", "Dev-time language directory switch")).listFiles();
    if (dirs!=null) {
      for (int i = 0; i < dirs.length; i++) {
        String dir = dirs[i].getName();
        LOG.fine("Found language directory "+dirs[i].getAbsolutePath());
        result.add(dir);
      }
    }

    
    File dir = new File("lib");
    LOG.fine("Looking for language archives in "+dir.getAbsolutePath());
    File[] libs = dir.listFiles();
    if (libs!=null)
      for (File lib : libs) {
        String name = lib.getName();
        if (!name.startsWith("genj_")) continue;
        if (!name.endsWith  (".jar" )) continue;
        LOG.fine("Found language archive "+lib.getAbsolutePath());
        
        result.add(name.substring(5, name.length()-4));
      }

    
    return (String[])result.toArray(new String[result.size()]);
  }

  
  public static Options getInstance() {
    return instance;
  }

  
  public int getLookAndFeel() {
    
    if (lookAndFeel<0)
      setLookAndFeel(0);
    return lookAndFeel;
  }

  
  public void setLookAndFeel(int set) {

    
    LnF[] lnfs = LnF.getLnFs();
    if (set<0||set>lnfs.length-1)
      set = 0;

    
    lnfs[set].apply(null);

    
    lookAndFeel = set;

    
  }

  
  public LnF[] getLookAndFeels() {
    return LnF.getLnFs();
  }

  
  public void setLanguage(int language) {

    
    if (language>=0&&language<codes.length) {
      String lang = codes[language];
      if (lang.length()>0) {
        App.LOG.info("Switching language to "+lang);
        String country = Locale.getDefault().getCountry();
        int i = lang.indexOf('_');
        if (i>0) {
          country = lang.substring(i+1);
          lang = lang.substring(0, i);
        }
        try {
          Locale.setDefault(new Locale(lang,country));
        } catch (Throwable t) {}
      }
    }

    
    this.language = language;

    
    Resources resources = Resources.get(this);
    for (String key : resources.getKeys()) {
      if (key.indexOf(SWING_RESOURCES_KEY_PREFIX)==0) {
        UIManager.put(
          key.substring(SWING_RESOURCES_KEY_PREFIX.length()),
          resources.getString(key)
        );
      }
    }

    
  }

  
  public int getLanguage() {
    return language;
  }

  
  public String[] getLanguages() {
    
    if (languages==null) {

      Resources resources = getResources();

      
      String[] ss = new String[codes.length];
      for (int i=0;i<ss.length;i++) {
        String language = resources.getString("option.language."+codes[i], false);
        ss[i] = language!=null ? language : codes[i];
      }
      
      languages = ss;
    }
    
    return languages;
  }

  
  public int getMaxLogSizeKB() {
    return maxLogSizeKB;
  }

  
  public void setMaxLogSizeKB(int set) {
    maxLogSizeKB = Math.max(128, set);
  }

  
  public String getHttpProxy() {
    String host = System.getProperty("http.proxyHost");
    String port = System.getProperty("http.proxyPort");
    if (host==null)
      return "";
    return port!=null&&port.length()>0 ? host+":"+port : host;
  }

  
  public void setHttpProxy(String set) {
    int colon = set.indexOf(":");
    String port = colon>=0 ? set.substring(colon+1) : "";
    String host = colon>=0 ? set.substring(0,colon) : set;
    if (host.length()==0) port = "";
    System.setProperty("http.proxyHost", host);
    System.setProperty("http.proxyPort", port);
  }

  
  private Resources getResources() {
    if (resources==null)
      resources = Resources.get(this);
    return resources;
  }

  

  public List<? extends Option> getOptions() {
    
    List<Option> result = new ArrayList<Option>(PropertyOption.introspect(instance));
    
    
    
    return result;
  }
  
} 
