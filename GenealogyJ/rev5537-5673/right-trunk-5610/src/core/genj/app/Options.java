
package genj.app;

import genj.gedcom.PropertyFile;
import genj.io.FileAssociation;
import genj.lnf.LnF;
import genj.option.Option;
import genj.option.OptionProvider;
import genj.option.OptionUI;
import genj.option.OptionsWidget;
import genj.option.PropertyOption;
import genj.util.EnvironmentChecker;
import genj.util.GridBagHelper;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.FileChooserWidget;
import genj.util.swing.PopupWidget;
import genj.util.swing.TextFieldWidget;
import genj.window.WindowManager;

import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Options extends OptionProvider {

  
  private final static String SWING_RESOURCES_KEY_PREFIX = "swing.";

  
  private final static Options instance = new Options();

  
  private WindowManager windowManager;

  
  private Resources resources;

  
  private int maxLogSizeKB = 128;

  
  private int lookAndFeel = -1;

  
  private int language = -1;

  
  public boolean isRestoreViews = true;
  
  
  public boolean isWriteBOM = false;

  
  private static String[] languages;

  
  private final static String[] codes = findCodes();

  private static String[] findCodes() {

    
    
    TreeSet result = new TreeSet();
    result.add("en");

    
    File[] dirs = new File(EnvironmentChecker.getProperty(Options.class, "genj.language.dir", "./language", "Dev-time language directory switch")).listFiles();
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

  
  public void setWindowManager(WindowManager set) {
    windowManager = set;
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

    
    lnfs[set].apply(windowManager!=null?windowManager.getRootComponents():null);

    
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
    Iterator keys = resources.getKeys().iterator();
    while (keys.hasNext()) {
      String key = (String)keys.next();
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

      
      languages = new String[codes.length];
      for (int i=0;i<languages.length;i++) {
        String language = resources.getString("option.language."+codes[i], false);
        languages[i] = language!=null ? language : codes[i];
      }
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

  
  public List getOptions() {
    
    List result = PropertyOption.introspect(instance);
    
    result.add(new FileAssociationOption());
    
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

    public void persist(Registry registry) {
      
    }

    public void restore(Registry registry) {
      
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
      protected void execute() {
        File user_home_genj = new File(EnvironmentChecker.getProperty(UserHomeGenJOption.this, "user.home.genj", null, "trying to open user.home.genj")) ;
        FileAssociation asso = FileAssociation.get(user_home_genj, "", null);
        if (asso!=null) asso.execute(user_home_genj);
      }
    }
  }

  
  private static class FileAssociationOption extends Option implements OptionUI {

    
    private OptionsWidget widget;

    
    private PopupWidget popup;

    
    public String getName() {
      return getInstance().getResources().getString("option.fileassociations");
    }

    
    public String getToolTip() {
      return getInstance().getResources().getString("option.fileassociations.tip", false);
    }

    
    public void persist(Registry registry) {
      registry.put("associations", FileAssociation.getAll());
    }

    
    public void restore(Registry registry) {
      String[] associations = registry.get("associations", new String[0]);
      for (int i = 0; i < associations.length; i++)
        FileAssociation.add(new FileAssociation(associations[i]));
    }

    
    public OptionUI getUI(OptionsWidget widget) {
      this.widget = widget;
      return this;
    }

    
    public String getTextRepresentation() {
      return null;
    }

    
    public JComponent getComponentRepresentation() {
      
      popup = new PopupWidget("...");
      popup.setActions(getActions());
      
      return popup;
    }

    
    private List getActions() {
      
      List result = new ArrayList(10);
      Iterator it = FileAssociation.getAll().iterator();
      for (int i=1;it.hasNext();i++)
        result.add(new Edit(i, (FileAssociation)it.next()));
      result.add(new Edit(0, null));
      
      return result;
    }

    
    public void endRepresentation() {
      
    }

    
    private class Edit extends Action2 {
      
      private FileAssociation association;
      
      private Edit(int i, FileAssociation fa) {
        association = fa;
        setImage(PropertyFile.DEFAULT_IMAGE);
        setText(fa!=null ? i+" "+fa.getName()+" ("+fa.getSuffixes()+')' : localize("new"));
      }
      
      private String localize(String key) {
        return Options.getInstance().getResources().getString("option.filesssociations."+key);
      }
      
      protected void execute() {

        
        JPanel panel = new JPanel();
        final TextFieldWidget
          suffixes   = new TextFieldWidget(),
          name       = new TextFieldWidget();
        final FileChooserWidget
          executable = new FileChooserWidget(FileChooserWidget.EXECUTABLES);
        GridBagHelper gh = new GridBagHelper(panel);
        gh.add(new JLabel(localize("suffix"), JLabel.LEFT), 0,0,1,1,GridBagHelper.FILL_HORIZONTAL);
        gh.add(suffixes                                   , 1,0,1,1,GridBagHelper.GROWFILL_HORIZONTAL);
        gh.add(new JLabel(localize("name"  ), JLabel.LEFT), 0,1,1,1,GridBagHelper.FILL_HORIZONTAL);
        gh.add(name                                       , 1,1,1,1,GridBagHelper.GROWFILL_HORIZONTAL);
        gh.add(new JLabel(localize("exec"  ), JLabel.LEFT), 0,2,1,1,GridBagHelper.FILL_HORIZONTAL);
        gh.add(executable                                 , 1,2,1,1,GridBagHelper.GROWFILL_HORIZONTAL);

        
        if (association!=null) {
          suffixes  .setText(association.getSuffixes()  );
          name      .setText(association.getName()      );
          executable.setFile(association.getExecutable());
        }

        
        final Action
          ok = Action2.ok(),
          delete = new Action2(localize("delete"), association!=null),
          cancel = Action2.cancel();

        
        ChangeListener l = new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            ok.setEnabled( !suffixes.isEmpty() && !name.isEmpty() && !executable.isEmpty() );
          }
        };
        suffixes.addChangeListener(l);
        name.addChangeListener(l);
        executable.addChangeListener(l);
        l.stateChanged(null);

        
        WindowManager mgr = widget.getWindowManager();
        int rc = mgr.openDialog(null, getName(), WindowManager.QUESTION_MESSAGE, panel, new Action[]{ ok, delete, cancel }, widget);
        if (rc==-1||rc==2)
          return;

        
        if (rc==0) {
            
            if (association==null)
              association = FileAssociation.add(new FileAssociation());
            
            association.setSuffixes(suffixes.getText());
            association.setName(name.getText());
            association.setExecutable(executable.getFile().toString());
        } else { 
          FileAssociation.del(association);
        }

        
        popup.setActions(getActions());
      }
    } 

  } 

} 
