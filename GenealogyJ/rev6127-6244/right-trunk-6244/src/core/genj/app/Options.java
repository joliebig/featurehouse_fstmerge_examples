
package genj.app;

import genj.lnf.LnF;
import genj.option.Option;
import genj.option.OptionProvider;
import genj.option.OptionUI;
import genj.option.OptionsWidget;
import genj.option.PropertyOption;
import genj.util.EnvironmentChecker;
import genj.util.Resources;
import genj.util.swing.Action2;

import java.awt.Desktop;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;


public class Options extends OptionProvider {

  
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

    
    File[] dirs = new File(EnvironmentChecker.getProperty("genj.language.dir", "./language", "Dev-time language directory switch")).listFiles();
    if (dirs!=null) {
      for (int i = 0; i < dirs.length; i++) {
        String dir = dirs[i].getName();
        if (!"CVS".equals(dir))
          result.add(dir);
      }
    }

    
    File[] libs = new File("./lib").listFiles();
    if (libs!=null)
      for (int l=0;l<libs.length;l++) {
        File lib = libs[l];
        if (!lib.isFile()) continue;
        String name = lib.getName();
        if (!name.startsWith("genj_")) continue;
        if (!name.endsWith  (".jar" )) continue;
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
    
    result.add(new UserHomeGenJOption());
    
    return result;
  }

  private static class UserHomeGenJOption extends Option implements OptionUI {

    public String getName() {
      return getInstance().getResources().getString("option.userhomegenj.name");
    }

    public String getToolTip() {
      return getInstance().getResources().getString("option.userhomegenj.name.tip", false);
    }

    public OptionUI getUI(OptionsWidget widget) {
      return this;
    }

    public void persist() {
      
    }

    public void restore() {
      
    }

    public void endRepresentation() {
    }

    public JComponent getComponentRepresentation() {
      JButton button = new JButton(new Open());
      
      button.setMargin(new Insets(2,2,2,2));
      
      return button;
    }

    public String getTextRepresentation() {
      
      return null;
    }

    
    private class Open extends Action2 {
      Open() {
        setText("...");
      }
      public void actionPerformed(ActionEvent event) {
        File user_home_genj = new File(EnvironmentChecker.getProperty("user.home.genj", null, "trying to open user.home.genj")) ;
        try {
          Desktop.getDesktop().open(user_home_genj);
        } catch (Throwable t) {
          Logger.getLogger("genj.io").log(Level.INFO, "can't open user.home.genj", t);
        }
      }
    }
  }

} 
