
package genj.report;

import genj.common.SelectEntityWidget;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.time.PointInTime;
import genj.option.Option;
import genj.option.OptionsWidget;
import genj.option.PropertyOption;
import genj.util.EnvironmentChecker;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ChoiceWidget;
import genj.util.swing.DialogHelper;

import java.awt.Component;
import java.awt.Graphics;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;



public abstract class Report implements Cloneable {
  
  private final static PrintWriter NUL = new PrintWriter(new OutputStream() { @Override public void write(int b) { }} );

  protected final static Logger LOG = Logger.getLogger("genj.report");

  protected final static Icon DEFAULT_ICON = new Icon() {
    public int getIconHeight() {
      return 16;
    }
    public int getIconWidth() {
      return 16;
    }
    public void paintIcon(Component c, Graphics g, int x, int y) {
    }
  };

  
  protected Options OPTIONS = Options.getInstance();

  
  protected final static int
    OPTION_YESNO    = 0,
    OPTION_OKCANCEL = 1,
    OPTION_OK       = 2;

  private final static String[][] OPTION_TEXTS = {
    new String[]{Action2.TXT_YES, Action2.TXT_NO     },
    new String[]{Action2.TXT_OK , Action2.TXT_CANCEL },
    new String[]{Action2.TXT_OK }
  };

  
  protected final static int
    ALIGN_LEFT   = 0,
    ALIGN_CENTER = 1,
    ALIGN_RIGHT  = 2;

  
  protected Registry registry;

  
  private final static String lang = Locale.getDefault().getLanguage();

  
  static final Resources COMMON_RESOURCES = Resources.get(Report.class);

  
  private Resources resources;

  
  private List<Option> options;

  
  private Icon icon;

  
  private File file;
  
  
  private PrintWriter out = NUL;
  private Component owner = null;
  

  
  protected Report() {
    registry = new Registry(Registry.get(Report.class), getClass().getName());
  }

  
   void log(String txt) {
    getOut().println(txt);
  }
  
  
  public PrintWriter getOut() {
    return out;
  }
  
  
   void setOut(PrintWriter set) {
    out = set;
  }

  
   void setOwner(Component set) {
    owner = set;
  }

  
   void saveOptions() {
    
    if (options==null)
      return;
    
    for (Option option : options)
      if (option instanceof PropertyOption)
        ((PropertyOption)option).persist(registry);
      else
        option.persist();
    
  }
  
  protected Registry getRegistry() {
    return registry;
  }

  
  public final List<? extends Option> getOptions() {

    
    if (options!=null)
      return options;

    options = new ArrayList<Option>();
    
    
    
    List<PropertyOption> props = PropertyOption.introspect(this, true);

    
    for (PropertyOption prop : props) {
      
      prop.restore(registry);
      
      
      
      
      String oname = translateOption(prop.getProperty());
      if (oname.length()>0) prop.setName(oname);
      String toolTipKey = prop.getProperty() + ".tip";
      String toolTip = translateOption(toolTipKey);
      if (toolTip.length() > 0 && !toolTip.equals(toolTipKey))
        prop.setToolTip(toolTip);
      
      if (prop.getCategory()==null)
        prop.setCategory(getName());
      else
        prop.setCategory(translateOption(prop.getCategory()));

      
      options.add(prop);
    }
    
    
    for (Option option : getCustomOptions()) {
      if (option.getCategory()==null)
        option.setCategory(getName());
      option.restore();
      options.add(option);
    }

    
    return options;
  }
  
  protected List<? extends Option> getCustomOptions() {
    return Collections.emptyList();
  }

  
  public Icon getIcon() {
    
    
    if (icon!=null)
      return icon;

    
    String cat = translate("category");
    if (cat.equals("category")||cat.length()==0) {
      icon = DEFAULT_ICON;
    } else {
      
      String file = "Category"+Character.toUpperCase(cat.charAt(0))+cat.substring(1)+".png";
      try {
        InputStream in = Report.class.getResourceAsStream(file);
        icon = new genj.util.swing.ImageIcon(file, in);
      } catch (Throwable t) {
        icon = DEFAULT_ICON;
      }
    }
    
    
    return icon;
  }

  
  public final String getCategory() {
    
    String cat = translate("category");
    if (cat.equals("category")||cat.length()==0)
      return "";
    
    
    String result = COMMON_RESOURCES.getString("category."+cat, false);
    if (result==null) {
      LOG.fine("report's category "+cat+" doesn't exist");
      return COMMON_RESOURCES.getString("category.utility");
    }    
    
    return result;
  }

  
  public final void flush() {
    getOut().flush();
  }

  
  public final void println() {
    println("");
  }

  
  public final void println(Object o) {
    
    if (o==null)
      return;
    
    if (Thread.interrupted())
      throw new RuntimeException(new InterruptedException());
    
    log(o.toString());
    
  }

  
  public final void println(Throwable t) {
    CharArrayWriter awriter = new CharArrayWriter(256);
    t.printStackTrace(new PrintWriter(awriter));
    log(awriter.toString());
  }

  
  public File getFileFromUser(String title, String button) {
    return getFileFromUser(title, button, false);
  }

  
  public File getFileFromUser(String title, String button, boolean askForOverwrite) {
      return getFileFromUser(title, button, askForOverwrite, null);
  }

  
  public File getFileFromUser(String title, String button, boolean askForOverwrite, String extension) {

    
    String dir = registry.get("file", EnvironmentChecker.getProperty("user.home", ".", "looking for report dir to let the user choose from"));
    JFileChooser chooser = new JFileChooser(dir);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setDialogTitle(title);
    if (extension != null)
        chooser.setFileFilter(new FileExtensionFilter(extension));

    int rc = chooser.showDialog(owner,button);

    
    File result = chooser.getSelectedFile();
    if (rc!=JFileChooser.APPROVE_OPTION||result==null)
      return null;

    
    if (result.exists()&&askForOverwrite) {
      rc = DialogHelper.openDialog(title, DialogHelper.WARNING_MESSAGE, ReportView.RESOURCES.getString("report.file.overwrite"), Action2.yesNo(), owner);
      if (rc!=0)
        return null;
    }

    
    registry.put("file", result.getParent().toString());
    return result;
  }

  
  public File getDirectoryFromUser(String title, String button) {

    
    String dir = registry.get("dir", EnvironmentChecker.getProperty("user.home", ".", "looking for report dir to let the user choose from"));
    JFileChooser chooser = new JFileChooser(dir);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setDialogTitle(title);
    int rc = chooser.showDialog(owner,button);

    
    File result = chooser.getSelectedFile();
    if (rc!=JFileChooser.APPROVE_OPTION||result==null)
      return null;

    
    registry.put(dir, result.toString());
    return result;
  }

  
  public final Entity getEntityFromUser(String msg, Gedcom gedcom, String tag) {

    SelectEntityWidget select = new SelectEntityWidget(gedcom, tag, null);

    
    Entity entity = gedcom.getEntity(registry.get("select."+tag, (String)null));
    if (entity!=null)
      select.setSelection(entity);

    
    int rc = DialogHelper.openDialog(getName(),DialogHelper.QUESTION_MESSAGE,new JComponent[]{new JLabel(msg),select},Action2.okCancel(),owner);
    if (rc!=0)
      return null;

    
    Entity result = select.getSelection();
    if (result==null)
      return null;
    registry.put("select."+result.getTag(), result.getId());

    
    return result;
  }











  
  public final Object getValueFromUser(String msg, Object[] choices, Object selected) {

    ChoiceWidget choice = new ChoiceWidget(choices, selected);
    choice.setEditable(false);

    int rc = DialogHelper.openDialog(getName(),DialogHelper.QUESTION_MESSAGE,new JComponent[]{new JLabel(msg),choice},Action2.okCancel(),owner);

    return rc==0 ? choice.getSelectedItem() : null;
  }

  
  public final String getValueFromUser(String key, String msg) {
    return getValueFromUser(key, msg, new String[0]);
  }

  
  public final String getValueFromUser(String key, String msg, String[] defaultChoices) {

    
    if (key!=null) {
      String[] presets = registry.get(key, (String[])null);
      if (presets != null)
        defaultChoices = presets;
    }

    
    ChoiceWidget choice = new ChoiceWidget(defaultChoices, defaultChoices.length>0 ? defaultChoices[0] : "");
    int rc = DialogHelper.openDialog(getName(),DialogHelper.QUESTION_MESSAGE,new JComponent[]{new JLabel(msg),choice},Action2.okCancel(),owner);
    String result = rc==0 ? choice.getText() : null;

    
    if (key!=null&&result!=null&&result.length()>0) {
      List<String> values = new ArrayList<String>(defaultChoices.length+1);
      values.add(result);
      for (int d=0;d<defaultChoices.length&&d<20;d++)
        if (!result.equalsIgnoreCase(defaultChoices[d]))
          values.add(defaultChoices[d]);
      registry.put(key, values);
    }

    
    return result;
  }

  
  public final boolean getOptionsFromUser(String title, Object options) {

    
    List<PropertyOption> os = PropertyOption.introspect(options);

    
    String prefix = options.getClass().getName();

    int i = prefix.lastIndexOf('.');
    if (i>0) prefix = prefix.substring(i+1);

    i = prefix.lastIndexOf('$');
    if (i>0) prefix = prefix.substring(i+1);

    
    for (PropertyOption option : os) {
      
      option.restore(registry);

      
      
      
      
      String oname = translate(prefix+"."+option.getName());
      if (oname.length()>0) option.setName(oname);
    }

    
    OptionsWidget widget = new OptionsWidget(title, os);
    int rc = DialogHelper.openDialog(getName(), DialogHelper.QUESTION_MESSAGE, widget, Action2.okCancel(), owner);
    if (rc!=0)
      return false;

    
    widget.stopEditing();
    for (PropertyOption option : os)
      option.persist(registry);

    
    return true;
  }

  
  public final boolean getOptionFromUser(String msg, int option) {
    return 0==getOptionFromUser(msg, OPTION_TEXTS[option]);
  }

  
  private int getOptionFromUser(String msg, String[] actions) {

    Action[] as  = new Action[actions.length];
    for (int i=0;i<as.length;i++)
      as[i]  = new Action2(actions[i]);

    return DialogHelper.openDialog(getName(), DialogHelper.QUESTION_MESSAGE, msg, as, owner);

  }

  
  public String translateOption(String key)
  {
	  String result = translate(key);
	  if (result.equals(key))
	  {
		  String optionKey = "option." + key;
		  String optionName = COMMON_RESOURCES.getString(optionKey);
		  if (!optionName.equals(optionKey))
			  result = optionName;
	  }
	  return result;
  }

  
  public final String translate(String key) {
    return translate(key, (Object[])null);
  }

  
  public final String translate(String key, Object... values) {

    Resources resources = getResources();
    if (resources==null)
      return key;

    
    String result = null;
    if (lang!=null) {
      String locKey = key+'.'+lang;
      result = resources.getString(locKey, values);
      if (result!=locKey)
        return result;
    }

    
    result = resources.getString(key, values);

    
    return result;
  }

  
  public void putFile(File setFile) {
    
    file = setFile;
  }

  public File getFile() {
    return file;
  }

  
  private String getTypeName() {
    String rtype = getClass().getName();
    while (rtype.indexOf('.') >= 0)
      rtype = rtype.substring(rtype.indexOf('.')+1);
    return rtype;
  }

  
  protected Resources getResources() {
    if (resources==null) {
      
      resources = new Resources(getClass().getResourceAsStream(getTypeName()+".properties"));
      
      try {
        
        File reports = new File("./src/report");
        String src = getClass().getName().replace('.', '/')+".java";
        InputStream in = (reports.exists()&&reports.isDirectory()) ?
            new FileInputStream(new File(reports, src)) :
            getClass().getResourceAsStream(src);
        resources.load(in, true);
      } catch (IOException e) {
        
      }
    }
    return resources;
  }

  
    public static String getIndent(int level, int spacesPerLevel, String prefix) {
        StringBuffer oneLevel = new StringBuffer();
        while(oneLevel.length() != spacesPerLevel)
            oneLevel.append(" ");
        StringBuffer buffer = new StringBuffer(256);
        while (--level>0) {
            buffer.append(oneLevel);
        }
        if (prefix!=null)
          buffer.append(prefix);
        return buffer.toString();
    }

    
    public final String getIndent(int level) {
      return getIndent(level, OPTIONS.getIndentPerLevel(), null);
    }


  
  public static String align(String txt, int length, int alignment) {

    
    int n = txt.length();
    if (n>length)
      return txt.substring(0, length);
    n = length-n;

    
    StringBuffer buffer = new StringBuffer(length);

    int before,after;
    switch (alignment) {
      default:
      case ALIGN_LEFT:
        before = 0;
        break;
      case ALIGN_CENTER:
        before = (int)(n*0.5F);
        break;
      case ALIGN_RIGHT:
        before = n;
        break;
    }
    after = n-before;

    
    for (int i=0; i<before; i++)
      buffer.append(' ');

    
    buffer.append(txt);

    
    for (int i=0; i<after; i++)
      buffer.append(' ');

    
    return buffer.toString();
  }

  
  public String getName() {
    String name =  translate("name");
    if (name.length()==0||name.equals("name")) name = getTypeName();
    return name;
  }

  
  public String getAuthor() {
    return translate("author");
  }

  
  public String getVersion() {
    return translate("version");
  }

  private final static Pattern PATTERN_CVS_DATE  = Pattern.compile("\\$"+"Date: (\\d\\d\\d\\d)/(\\d\\d)/(\\d\\d)( \\d\\d:\\d\\d:\\d\\d) *\\$"); 

  
  public String getLastUpdate() {
    
    String result = translate("updated");
      if ("updated".equals(result))
      return null;
    
    Matcher cvsdata = PATTERN_CVS_DATE.matcher(result);
    if (cvsdata.matches()) try {
      
      result = new PointInTime(cvsdata.group(1)+cvsdata.group(2)+cvsdata.group(3)) + cvsdata.group(4);
    } catch (GedcomException e) {
    }
    
    return result;
  }

  
  public String getInfo() {
    return translate("info");
  }

  
  public Object start(Object context) throws Throwable {
    try {
      return getStartMethod(context).invoke(this, new Object[]{ context });
    } catch (InvocationTargetException t) {
      throw ((InvocationTargetException)t).getTargetException();
    }
  }

  
  public boolean isReadOnly() {
    return true;
  }

    
    public String accepts(Object context) {
      return getStartMethod(context)!=null ? getName() : null;
    }

    
     Method getStartMethod(Object context) {

      
      try {
        Method[] methods = getClass().getDeclaredMethods();
        for (int m = 0; m < methods.length; m++) {
          
          if (!methods[m].getName().equals("start")) continue;
          
          Class<?>[] params = methods[m].getParameterTypes();
          if (params.length!=1) continue;
          
          Class<?> param = params[0];
          if (param.isAssignableFrom(context.getClass()))
            return methods[m];
          
        }
      } catch (Throwable t) {
      }
      
      return null;
    }

    
    private class FileExtensionFilter extends FileFilter {

        private String extension;

        public FileExtensionFilter(String extension) {
            this.extension = extension.toLowerCase();
        }

        
        public boolean accept(File f) {
            if (f == null)
                return false;
            if (f.isDirectory())
                return true;
            return f.getName().toLowerCase().endsWith("." + extension);
        }

        public String getDescription() {
            return extension.toUpperCase() + " files";
        }
    }

} 
