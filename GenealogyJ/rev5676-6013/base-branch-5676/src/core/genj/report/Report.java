
package genj.report;

import genj.chart.Chart;
import genj.common.SelectEntityWidget;
import genj.fo.Document;
import genj.fo.Format;
import genj.fo.FormatOptionsWidget;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.time.PointInTime;
import genj.io.FileAssociation;
import genj.option.Option;
import genj.option.OptionsWidget;
import genj.option.PropertyOption;
import genj.util.EnvironmentChecker;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ChoiceWidget;
import genj.window.WindowManager;

import java.awt.Component;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;



public abstract class Report implements Cloneable {

  protected final static Logger LOG = Logger.getLogger("genj.report");

  protected final static ImageIcon
    IMG_SHELL = new genj.util.swing.ImageIcon(ReportView.class,"ReportShell"),
    IMG_FO    = new genj.util.swing.ImageIcon(ReportView.class,"ReportFO"  ),
    IMG_GUI   = new genj.util.swing.ImageIcon(ReportView.class,"ReportGui"  );

  
  protected Options OPTIONS = Options.getInstance();

  
  protected final static int
    OPTION_YESNO    = 0,
    OPTION_OKCANCEL = 1,
    OPTION_OK       = 2;

  
  private static final Category DEFAULT_CATEGORY = new Category("Other", IMG_SHELL);
  private static final Categories categories = new Categories();
  static {
      
      categories.add(DEFAULT_CATEGORY);
  }

  private final static String[][] OPTION_TEXTS = {
    new String[]{Action2.TXT_YES, Action2.TXT_NO     },
    new String[]{Action2.TXT_OK , Action2.TXT_CANCEL },
    new String[]{Action2.TXT_OK }
  };

  
  protected final static int
    ALIGN_LEFT   = 0,
    ALIGN_CENTER = 1,
    ALIGN_RIGHT  = 2;

  
  protected final static Registry registry = new Registry("genj-reports");

  
  private final static String lang = Locale.getDefault().getLanguage();

  
  static final Resources COMMON_RESOURCES = Resources.get(Report.class);

  
  private Resources resources;

  
  protected PrintWriter out;

  
  private WindowManager windowManager;

  
  private Component owner;

  
  private List options;

  
  private ImageIcon image;

  
  private File file;

  
  protected Report() {

  }

  
   Report getInstance(Component owner, PrintWriter out) {

    try {

      
      getOptions();

      
      Report result = (Report)clone();

      
      result.windowManager = WindowManager.getInstance(owner);
      result.out = out;
      result.owner = owner;

      
      return result;

    } catch (CloneNotSupportedException e) {
      ReportView.LOG.log(Level.SEVERE, "couldn't clone report", e);
      throw new RuntimeException("getInstance() failed");
    }
  }

  
   void log(String txt) {
    if (out!=null)
      out.println(txt);
  }

  
  public void saveOptions() {
    
    if (options==null)
      return;
    
    Iterator it = options.iterator();
    while (it.hasNext())
      ((Option)it.next()).persist(registry);
    
  }

  
  public List getOptions() {

    
    if (options!=null)
      return options;

    
    options = PropertyOption.introspect(this);

    
    Iterator it = options.iterator();
    while (it.hasNext()) {
      PropertyOption option = (PropertyOption)it.next();
      
      option.restore(registry);
      
      
      
      
      String oname = translateOption(option.getProperty());
      if (oname.length()>0) option.setName(oname);
      String toolTipKey = option.getProperty() + ".tip";
      String toolTip = translateOption(toolTipKey);
      if (toolTip.length() > 0 && !toolTip.equals(toolTipKey))
          option.setToolTip(toolTip);
      
      option.setCategory(getName());
    }

    
    return options;
  }

  
  protected ImageIcon getImage() {

    
    if (image==null) try {
      String file = getTypeName()+".png";
      InputStream in = getClass().getResourceAsStream(file);
      if (in==null) {
        
        file = getTypeName()+".gif";
        in = getClass().getResourceAsStream(file);
      }
      image = new genj.util.swing.ImageIcon(file, in);
    } catch (Throwable t) {
      image = usesStandardOut() ? IMG_SHELL : IMG_GUI;
    }

    
    return image;
  }

  
  public Category getCategory() {
      String name = translate("category");
      if (name.equals("category"))
          return DEFAULT_CATEGORY;

      Category category = (Category)categories.get(name);
      if (category == null) {
          category = createCategory(name);
          categories.add(category);
      }
      return category;
  }

  private Category createCategory(String name) {
      String file = "Category" + name + ".png";

      InputStream in = Report.class.getResourceAsStream(file);
      if (in == null)
          in = getClass().getResourceAsStream(file);

      ImageIcon image;
      if (in != null)
          image = new genj.util.swing.ImageIcon(file, in);
      else
          image = IMG_SHELL;
      return new Category(name, image);
  }

  
  public final void flush() {
    if (out!=null)
      out.flush();
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

    String key = getClass().getName()+".file";

    
    String dir = registry.get(key, EnvironmentChecker.getProperty(this, "user.home", ".", "looking for report dir to let the user choose from"));
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
      rc = windowManager.openDialog(null, title, WindowManager.WARNING_MESSAGE, ReportView.RESOURCES.getString("report.file.overwrite"), Action2.yesNo(), owner);
      if (rc!=0)
        return null;
    }

    
    registry.put(key, result.getParent().toString());
    return result;
  }

  
  public File getDirectoryFromUser(String title, String button) {

    String key = getClass().getName()+".dir";

    
    String dir = registry.get(key, EnvironmentChecker.getProperty(this, "user.home", ".", "looking for report dir to let the user choose from"));
    JFileChooser chooser = new JFileChooser(dir);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setDialogTitle(title);
    int rc = chooser.showDialog(owner,button);

    
    File result = chooser.getSelectedFile();
    if (rc!=JFileChooser.APPROVE_OPTION||result==null)
      return null;

    
    registry.put(key, result.toString());
    return result;
  }

  
  public void showDocumentToUser(Document doc) {

    String title = "Document "+doc.getTitle();

    Registry foRegistry = new Registry(registry, getClass().getName()+".fo");

    Action[] actions = Action2.okCancel();
    FormatOptionsWidget output = new FormatOptionsWidget(doc, foRegistry);
    output.connect(actions[0]);
    int rc = windowManager.openDialog("reportdoc", title, WindowManager.QUESTION_MESSAGE, output, actions, owner);

    
    if (rc!=0)
      return;

    
    Format formatter = output.getFormat();

    File file = null;
    String progress = null;
    if (formatter.getFileExtension()!=null) {

      file = output.getFile();
      if (file==null)
        return;
      file.getParentFile().mkdirs();

      
      progress = windowManager.openNonModalDialog(
          null, title, WindowManager.INFORMATION_MESSAGE, new JLabel("Writing Document to file "+file+" ..."), Action2.okOnly(), owner);

    }

    
    output.remember(foRegistry);

    
    try {
      formatter.format(doc, file);
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "formatting "+doc+" failed", t);
      windowManager.openDialog(null, "Formatting "+doc+" failed", WindowManager.ERROR_MESSAGE, t.getMessage(), Action2.okOnly(), owner);
      file = null;
    }

    
    if (progress!=null)
      windowManager.close(progress);

    
    if (file!=null) {
    	showFileToUser(file, formatter.getFileExtension());
    }

    
  }

  
	public void showFileToUser(File file)
	{
		showFileToUser(file, null);
	}

	  
	public void showFileToUser(File file, String extension)
	{
		
		if (owner instanceof ReportView && ("html".equals(extension) || file.getName().endsWith(".html"))) {
			try {
				log("" + file.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			FileAssociation association = null;
			if (extension != null)
				association = FileAssociation.get(extension, extension, "Open", owner);
			else
				association = FileAssociation.get(file, "Open", owner);
			if (association != null)
				association.execute(file);
		}
	}

  
  public final void showChartToUser(Chart chart) {
    showComponentToUser(chart);
  }

  
  public void showComponentToUser(JComponent component) {

    
    windowManager.openNonModalDialog(getClass().getName()+"#component",getName(), WindowManager.INFORMATION_MESSAGE,component,Action2.okOnly(),owner);

    
  }

  
  public final void showBrowserToUser(URL url) {
    FileAssociation.open(url, owner);
  }

  
  public final Entity getEntityFromUser(String msg, Gedcom gedcom, String tag) {

    SelectEntityWidget select = new SelectEntityWidget(gedcom, tag, null);

    
    Entity entity = gedcom.getEntity(registry.get("select."+tag, (String)null));
    if (entity!=null)
      select.setSelection(entity);

    
    int rc = windowManager.openDialog("select."+tag,getName(),WindowManager.QUESTION_MESSAGE,new JComponent[]{new JLabel(msg),select},Action2.okCancel(),owner);
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

    int rc = windowManager.openDialog(null,getName(),WindowManager.QUESTION_MESSAGE,new JComponent[]{new JLabel(msg),choice},Action2.okCancel(),owner);

    return rc==0 ? choice.getSelectedItem() : null;
  }

  
  public final String getValueFromUser(String key, String msg) {
    return getValueFromUser(key, msg, new String[0]);
  }

  
  public final String getValueFromUser(String key, String msg, String[] defaultChoices) {

    
    if (key!=null) {
      key = getClass().getName()+"."+key;
      String[] presets = registry.get(key, (String[])null);
      if (presets != null)
        defaultChoices = presets;
    }

    
    ChoiceWidget choice = new ChoiceWidget(defaultChoices, defaultChoices.length>0 ? defaultChoices[0] : "");
    int rc = windowManager.openDialog(null,getName(),WindowManager.QUESTION_MESSAGE,new JComponent[]{new JLabel(msg),choice},Action2.okCancel(),owner);
    String result = rc==0 ? choice.getText() : null;

    
    if (key!=null&&result!=null&&result.length()>0) {
      List values = new ArrayList(defaultChoices.length+1);
      values.add(result);
      for (int d=0;d<defaultChoices.length&&d<20;d++)
        if (!result.equalsIgnoreCase(defaultChoices[d]))
          values.add(defaultChoices[d]);
      registry.put(key, values);
    }

    
    return result;
  }

  
  public final boolean getOptionsFromUser(String title, Object options) {

    
    List os = PropertyOption.introspect(options);

    
    String prefix = options.getClass().getName();

    int i = prefix.lastIndexOf('.');
    if (i>0) prefix = prefix.substring(i+1);

    i = prefix.lastIndexOf('$');
    if (i>0) prefix = prefix.substring(i+1);

    
    Registry r = new Registry(registry, prefix);
    Iterator it = os.iterator();
    while (it.hasNext()) {
      PropertyOption option  = (PropertyOption)it.next();
      option.restore(r);

      
      
      
      
      String oname = translate(prefix+"."+option.getName());
      if (oname.length()>0) option.setName(oname);

    }

    
    OptionsWidget widget = new OptionsWidget(title, os);
    int rc = windowManager.openDialog(null, getName(), WindowManager.QUESTION_MESSAGE, widget, Action2.okCancel(), owner);
    if (rc!=0)
      return false;

    
    widget.stopEditing();
    it = os.iterator();
    while (it.hasNext())
      ((Option)it.next()).persist(r);

    
    return true;
  }

  
  public final boolean getOptionFromUser(String msg, int option) {
    return 0==getOptionFromUser(msg, OPTION_TEXTS[option]);
  }

  
  private int getOptionFromUser(String msg, String[] actions) {

    Action[] as  = new Action[actions.length];
    for (int i=0;i<as.length;i++)
      as[i]  = new Action2(actions[i]);

    return windowManager.openDialog(null, getName(), WindowManager.QUESTION_MESSAGE, msg, as, owner);

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

  
  public final String translate(String key, int value) {
    return translate(key, new Integer(value));
  }

  
  public final String translate(String key, Object value) {
    return translate(key, new Object[]{value});
  }

  
  public String translate(String key, Object[] values) {

    Resources resources = getResources();
    if (resources==null)
      return key;

    
    String result = null;
    if (lang!=null)
      result = resources.getString(key+'.'+lang, values, false);

    
    if (result==null)
      result = resources.getString(key, values, true);

    
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
        resources.load(in);
      } catch (IOException e) {
        
      }
    }
    return resources;
  }

  
    public static String getIndent(int level, int spacesPerLevel, String prefix) {
        String oneLevel = "";
        while(oneLevel.length() != spacesPerLevel)
            oneLevel=oneLevel+" ";
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

  
  public void start(Object context) throws Throwable {
    try {
      getStartMethod(context).invoke(this, new Object[]{ context });
    } catch (Throwable t) {
      String msg = "can't run report on input";
      if (t instanceof InvocationTargetException)
        throw ((InvocationTargetException)t).getTargetException();
      throw t;
    }
  }

  
  public boolean isReadOnly() {
    return true;
  }

  
  public boolean usesStandardOut() {
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
          
          Class[] params = methods[m].getParameterTypes();
          if (params.length!=1) continue;
          
          Class param = params[0];
          if (param.isAssignableFrom(context.getClass()))
            return methods[m];
          
        }
      } catch (Throwable t) {
      }
      
      return null;
    }

    
    public static class Category
    {
        private String name;
        private ImageIcon image;

        public Category(String name, ImageIcon image) {
            this.name = name;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public ImageIcon getImage() {
            return image;
        }
    }

    private static class Categories extends TreeMap {
        void add(Category category) {
            put(category.getName(), category);
        }
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
