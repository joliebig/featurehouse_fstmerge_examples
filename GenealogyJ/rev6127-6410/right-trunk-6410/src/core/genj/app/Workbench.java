
package genj.app;

import genj.common.ContextListWidget;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import genj.gedcom.UnitOfWork;
import genj.io.Filter;
import genj.io.GedcomEncodingException;
import genj.io.GedcomIOException;
import genj.io.GedcomReader;
import genj.io.GedcomReaderContext;
import genj.io.GedcomReaderFactory;
import genj.io.GedcomWriter;
import genj.util.EnvironmentChecker;
import genj.util.Origin;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.SafeProxy;
import genj.util.ServiceLookup;
import genj.util.Trackable;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.FileChooser;
import genj.util.swing.ImageIcon;
import genj.util.swing.MacAdapter;
import genj.view.SelectionSink;
import genj.view.View;
import genj.view.ViewContext;
import genj.view.ViewFactory;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import spin.Spin;
import swingx.docking.DefaultDockable;
import swingx.docking.Dockable;
import swingx.docking.Docked;
import swingx.docking.DockingPane;
import swingx.docking.dock.TabbedDock;
import swingx.docking.persistence.XMLPersister;


public class Workbench extends JPanel implements SelectionSink {
  
  private final static ImageIcon
    IMG_CLOSE     = new ImageIcon(Workbench.class,"images/Close.png"),
    IMG_NEW       = new ImageIcon(Workbench.class,"images/New.png"),
    IMG_OPEN      = new ImageIcon(Workbench.class,"images/Open.png"),
    IMG_EXIT      = new ImageIcon(Workbench.class,"images/Exit.png"),
    IMG_SAVE      = new ImageIcon(Workbench.class,"images/Save.png");

  private final static String 
    ACC_SAVE = "ctrl S", 
    ACC_NEW = "ctrl N", 
    ACC_OPEN = "ctrl O",
    ACC_CLOSE = "ctrl W";

  
   final static Logger LOG = Logger.getLogger("genj.app");
   final static Resources RES = Resources.get(Workbench.class);
   final static Registry REGISTRY = Registry.get(Workbench.class);

  
  private List<WorkbenchListener> listeners = new CopyOnWriteArrayList<WorkbenchListener>();
  private List<Object> plugins = new ArrayList<Object>();
  private List<ViewFactory> viewFactories = ServiceLookup.lookup(ViewFactory.class);
  private Context context = new Context();
  private DockingPane dockingPane = new WorkbenchPane();
  private Menu menu = new Menu(this);
  private Toolbar toolbar = new Toolbar(this);
  private Runnable runOnExit;
  private StatusBar statusBar = new StatusBar(this);
  
  
  public Workbench(Runnable onExit) {

    
    runOnExit = onExit;
    
    
    LOG.info("loading plugins");
    for (PluginFactory pf : ServiceLookup.lookup(PluginFactory.class)) {
      LOG.info("Loading plugin "+pf.getClass());
      Object plugin = pf.createPlugin(this);
      plugins.add(plugin);
    }
    LOG.info("/loading plugins");

    
    setLayout(new BorderLayout());
    add(toolbar, BorderLayout.NORTH);
    add(dockingPane, BorderLayout.CENTER);
    add(statusBar, BorderLayout.SOUTH);

    
    new ActionCloseView();
    
    
    if (MacAdapter.isMac()) {
      MacAdapter.getInstance().setAboutListener(new ActionAbout());
      MacAdapter.getInstance().setQuitListener(new ActionExit());
      MacAdapter.getInstance().setPreferencesListener(new ActionOptions());
    }
    
    
    
  }
  
  @Override
  public void addNotify() {
    super.addNotify();
    
    DialogHelper.visitContainers(this, new DialogHelper.ComponentVisitor() {
      public Component visit(Component parent, Component child) {
        if (parent instanceof JFrame)
          ((JFrame)parent).setJMenuBar(menu);
        return null;
      }
    });
  }
  
  
  public void saveLayout(Writer writer) {
    new LayoutPersister(dockingPane, writer).save();
  }
  
  
  public void loadLayout(Reader reader) {
    new LayoutPersister(dockingPane, reader).load();
  }
  
  
  public Context getContext() {
    return context;
  }
  
  
  public void newGedcom() {
    
    
    File file = chooseFile(RES.getString("cc.create.title"), RES.getString("cc.create.action"), null);
    if (file == null)
      return;
    if (!file.getName().endsWith(".ged"))
      file = new File(file.getAbsolutePath() + ".ged");
    if (file.exists()) {
      int rc = DialogHelper.openDialog(RES.getString("cc.create.title"), DialogHelper.WARNING_MESSAGE, RES.getString("cc.open.file_exists", file.getName()), Action2.yesNo(), Workbench.this);
      if (rc != 0)
        return;
    }
    
    
    if (!closeGedcom())
      return;
    
    
    Gedcom gedcom;
    try {
      gedcom = new Gedcom(Origin.create(new URL("file:"+file.getAbsolutePath())));
    } catch (MalformedURLException e) {
      LOG.log(Level.WARNING, "unexpected exception creating new gedcom", e);
      return;
    }
    
    
    setGedcom(gedcom);
  }
  
  
  public boolean openGedcom() {

    
    File file = chooseFile(RES.getString("cc.open.title"), RES.getString("cc.open.action"), null);
    if (file == null)
      return false;
    REGISTRY.put("last.dir", file.getParentFile().getAbsolutePath());
    
    
    if (!closeGedcom())
      return false;
    
    
    try {
      return openGedcom(new URL("file:"+file.getAbsolutePath()));
    } catch (Throwable t) {
      
      return false;
    }
    
  }
  
  
  public boolean openGedcom(URL url) {

    
    if (!closeGedcom())
      return false;
    
    
    final Origin origin = Origin.create(url);

    
    final List<ViewContext> warnings = new ArrayList<ViewContext>();
    GedcomReader reader;
    try {

      
      reader = (GedcomReader)Spin.off(GedcomReaderFactory.createReader(origin, (GedcomReaderContext)Spin.over(new GedcomReaderContext() {
        public String getPassword() {
          return DialogHelper.openDialog(origin.getName(), DialogHelper.QUESTION_MESSAGE, RES.getString("cc.provide_password"), "", Workbench.this);
        }
        public void handleWarning(int line, String warning, Context context) {
          warnings.add(new ViewContext(RES.getString("cc.open.warning", new Object[] { new Integer(line), warning}), context));
        }
      })));

    } catch (IOException ex) {
      String txt = RES.getString("cc.open.no_connect_to", origin) + "\n[" + ex.getMessage() + "]";
      DialogHelper.openDialog(origin.getName(), DialogHelper.ERROR_MESSAGE, txt, Action2.okOnly(), Workbench.this);
      return false;
    }
    
    try {
      for (WorkbenchListener l : listeners) l.processStarted(this, reader);
      setGedcom(reader.read());
      if (!warnings.isEmpty()) {
        dockingPane.putDockable("warnings", new GedcomDockable(
            RES.getString("cc.open.warnings", context.getGedcom().getName()), 
            IMG_OPEN,
            new ContextListWidget(warnings))
        );
      }
    } catch (GedcomIOException ex) {
      
      DialogHelper.openDialog(origin.getName(), DialogHelper.ERROR_MESSAGE, RES.getString("cc.open.read_error", "" + ex.getLine()) + ":\n" + ex.getMessage(), Action2.okOnly(), Workbench.this);
      
      return false;
    } finally {
      for (WorkbenchListener l : listeners) l.processStopped(this, reader);
    }
    
    
    List<String> history = REGISTRY.get("history", new ArrayList<String>());
    history.remove(origin.toString());
    history.add(0, origin.toString());
    if (history.size()>5)
      history.remove(history.size()-1);
    REGISTRY.put("history", history);
    
    
    return true;
  }
  
  private void setGedcom(Gedcom gedcom) {
    
    
    if (context.getGedcom()!=null)
      throw new IllegalArgumentException("context.gedcom!=null");

    
    try {
      context = Context.fromString(gedcom, REGISTRY.get(gedcom.getName()+".context", gedcom.getName()));
    } catch (GedcomException ge) {
    } finally {
      
      Entity adam = gedcom.getFirstEntity(Gedcom.INDI);
      if (context.getEntities().isEmpty())
        context = new Context(gedcom, adam!=null ? Collections.singletonList(adam) : null, null);
    }
    
    
    for (WorkbenchListener listener: listeners)
      listener.gedcomOpened(this, gedcom);
  
    fireSelection(context, true);
    
    
  }
  
  
  public boolean saveAsGedcom() {
    
    if (context.getGedcom() == null)
      return false;
    
    
    fireCommit();
    
    
    SaveOptionsWidget options = new SaveOptionsWidget(context.getGedcom());
    File file = chooseFile(RES.getString("cc.save.title"), RES.getString("cc.save.action"), options);
    if (file == null)
      return false;
  
    
    if (file.exists()) {
      int rc = DialogHelper.openDialog(RES.getString("cc.save.title"), DialogHelper.WARNING_MESSAGE, RES.getString("cc.open.file_exists", file.getName()), Action2.yesNo(), Workbench.this);
      if (rc != 0) 
        return false;
    }
    
    
    if (!file.getName().endsWith(".ged"))
      file = new File(file.getAbsolutePath() + ".ged");
    
    Filter[] filters = options.getFilters();
    Gedcom gedcom = context.getGedcom();
    gedcom.setPassword(options.getPassword());
    gedcom.setEncoding(options.getEncoding());
    
    
    try {
      gedcom.setOrigin(Origin.create(new URL("file", "", file.getAbsolutePath())));
    } catch (Throwable t) {
      LOG.log(Level.FINER, "Failed to create origin for file "+file, t);
      return false;
    }
  
    
    if (!saveGedcomImpl(gedcom, filters))
    	return false;
    
    
    if (!closeGedcom())
    	return false;

    
    setGedcom(gedcom);
    
    return true;
  }
  
  
  public boolean saveGedcom() {

    if (context.getGedcom() == null)
      return false;
    
    
    fireCommit();
    
    
    return saveGedcomImpl(context.getGedcom(), new Filter[0]);
    
  }
  
  
  private boolean saveGedcomImpl(Gedcom gedcom, Filter[] filters) {
  



    try {
      
      
      GedcomWriter writer = null;
      File file = null, temp = null;
      try {
        
        
        file = gedcom.getOrigin().getFile().getCanonicalFile();

        
        temp = File.createTempFile("genj", ".ged", file.getParentFile());

        
        writer = new GedcomWriter(gedcom, new FileOutputStream(temp));
      } catch (GedcomEncodingException gee) {
        DialogHelper.openDialog(gedcom.getName(), DialogHelper.ERROR_MESSAGE, RES.getString("cc.save.write_encoding_error", gee.getMessage()), Action2.okOnly(), Workbench.this);
        return false;
      } catch (IOException ex) {
        DialogHelper.openDialog(gedcom.getName(), DialogHelper.ERROR_MESSAGE, RES.getString("cc.save.open_error", gedcom.getOrigin().getFile().getAbsolutePath()), Action2.okOnly(), Workbench.this);
        return false;
      }
      writer.setFilters(filters);

      
      writer.write();
      
      
      if (file.exists()) {
        File bak = new File(file.getAbsolutePath() + "~");
        if (bak.exists()&&!bak.delete())
          throw new GedcomIOException("Couldn't delete backup file " + bak.getName(), -1);
        if (!file.renameTo(bak))
          throw new GedcomIOException("Couldn't create backup for " + file.getName(), -1);
      }

      
      if (!temp.renameTo(file))
        throw new GedcomIOException("Couldn't move temporary " + temp.getName() + " to " + file.getName(), -1);

    } catch (GedcomIOException gioex) {
      DialogHelper.openDialog(gedcom.getName(), DialogHelper.ERROR_MESSAGE, RES.getString("cc.save.write_error", "" + gioex.getLine()) + ":\n" + gioex.getMessage(), Action2.okOnly(), Workbench.this);
      return false;
    }



    
    
    if (gedcom.hasChanged())
      gedcom.doMuteUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) throws GedcomException {
          gedcom.setUnchanged();
        }
      });

    
    return true;
  }
  
  
  public void exit() {
    
    
    if (context.getGedcom()!=null)
      REGISTRY.put("restore.url", context.getGedcom().getOrigin().toString());
    
    
    if (!closeGedcom())
      return;
    
    
    for (WorkbenchListener l : listeners)
      l.workbenchClosing(this);
    
    
    for (Object key : dockingPane.getDockableKeys()) 
      dockingPane.removeDockable(key);
    
    
    runOnExit.run();
  }
  
  
  public boolean closeGedcom() {

    
    if (context.getGedcom()==null)
      return true;
    
    
    fireCommit();
    
    
    if (context.getGedcom().hasChanged()) {
      
      
      int rc = DialogHelper.openDialog(null, DialogHelper.WARNING_MESSAGE, RES.getString("cc.savechanges?", context.getGedcom().getName()), Action2.yesNoCancel(), Workbench.this);
      
      if (rc == 2)
        return false;
      
      if (rc == 0) 
        if (!saveGedcom())
          return false;

    }
    
    
    for (WorkbenchListener listener: listeners)
      listener.gedcomClosed(this, context.getGedcom());
    
    
    REGISTRY.put(context.getGedcom().getName(), context.toString());

    
    context = new Context();
    for (WorkbenchListener listener : listeners) 
      listener.selectionChanged(this, context, true);
    
    
    return true;
  }
  
  
  @SuppressWarnings("deprecation")
  public void restoreGedcom() {

    String restore = REGISTRY.get("restore.url", (String)null);
    try {
      
      if (restore==null)
      	
      	
      	
        restore = new File("gedcom/example.ged").toURL().toString();
      
      if (restore.length()>0)
        openGedcom(new URL(restore));
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "unexpected error", t);
    }
  }
  
  
  @SuppressWarnings("unchecked")
  public <T> List<T> getProviders(Class<T> type) {
    
    List<T> result = new ArrayList<T>();
    
    
    for (Object key : dockingPane.getDockableKeys()) {
      Dockable dockable = dockingPane.getDockable(key);
      if (dockable instanceof DefaultDockable) {
        DefaultDockable vd = (DefaultDockable)dockable;
        if (type.isAssignableFrom(vd.getContent().getClass()) && !result.contains(vd.getContent()))
          result.add((T)vd.getContent());
      }
    }
    
    
    for (Object plugin : plugins) {
      if (type.isAssignableFrom(plugin.getClass())&&!result.contains(plugin))
        result.add((T)plugin);
    }
    
    
    for (WorkbenchListener l : listeners) {
      l = SafeProxy.unwrap(l);
      if (type.isAssignableFrom(l.getClass())&&!result.contains(l))
        result.add((T)l);
    }
    
    
    Collections.sort(result, new Comparator<T>() {
      public int compare(T a1, T a2) {
        Priority P1 = a1.getClass().getAnnotation(Priority.class);
        Priority P2 = a2.getClass().getAnnotation(Priority.class);
        int p1 = P1!=null ? P1.priority() : Priority.NORMAL;
        int p2 = P2!=null ? P2.priority() : Priority.NORMAL;
        return p2 - p1;
      }
    });

    
    return SafeProxy.harden(result, LOG);
  }
  
  public void fireCommit() {
    for (WorkbenchListener listener : listeners)
      listener.commitRequested(this);
  }
  
  public void fireSelection(Context context, boolean isActionPerformed) {
    
    
    if (context.getGedcom()!= this.context.getGedcom()) {
      LOG.log(Level.FINER, "context selection on unknown gedcom", new Throwable());
      return;
    }
    
    
    if (isActionPerformed && context.getProperties().size()==1) {
      Property p = context.getProperty();
      if (p instanceof PropertyXRef)
        context = new Context(((PropertyXRef) p).getTarget());
    }
    
    
    if (!isActionPerformed && this.context.equals(context))
      return;
    
    LOG.finer("fireSelection("+context+","+isActionPerformed+")");
    
    
    this.context = context;
    
    if (context.getGedcom()!=null) 
      REGISTRY.put(context.getGedcom().getName()+".context", context.toString());
    
    
    for (WorkbenchListener listener : listeners) 
      listener.selectionChanged(this, context, isActionPerformed);
    
  } 
  
  private void fireViewOpened(View view) {
    
    for (WorkbenchListener listener : listeners)
      listener.viewOpened(this, view);
  }

  private void fireViewRestored(View view) {
    
    for (WorkbenchListener listener : listeners)
      listener.viewRestored(this, view);
  }
  
  private void fireViewClosed(View view) {
    
    for (WorkbenchListener listener : listeners)
      listener.viewClosed(this, view);
  }
  
  public void addWorkbenchListener(WorkbenchListener listener) {
    listeners.add(0, SafeProxy.harden(listener));
  }

  public void removeWorkbenchListener(WorkbenchListener listener) {
    listeners.remove(SafeProxy.harden(listener));
  }

  
  public List<? extends ViewFactory> getViewFactories() {
    return viewFactories;
  }

  
  public View getView(Class<? extends ViewFactory> factoryClass) {
    ViewDockable dockable = (ViewDockable)dockingPane.getDockable(factoryClass);
    return dockable!=null ? dockable.getView() : null;
  }
  
  
  public void closeView(Class<? extends ViewFactory> factory) {
    
    View view = getView(factory);
    if (view==null)
      return;
    
    dockingPane.putDockable(factory, null);

    
    fireViewClosed(view);

  }
  
  
  public View openView(Class<? extends ViewFactory> factory) {
    return openView(factory, context);
  }
  
  
  public View openView(Class<? extends ViewFactory> factory, Context context) {
    for (ViewFactory vf : viewFactories) {
      if (vf.getClass().equals(factory))
        return openViewImpl(vf, context);
    }
    throw new IllegalArgumentException("unknown factory");
  }
  
  private View openViewImpl(ViewFactory factory, Context context) {
    
    
    ViewDockable dockable = (ViewDockable)dockingPane.getDockable(factory.getClass());
    if (dockable != null) {
      
      dockingPane.putDockable(factory.getClass(), dockable);
      
      return dockable.getView();
    }
    
    
    try {
      dockable = new ViewDockable(Workbench.this, factory);
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "cannot open view for "+factory.getClass().getName(), t);
      return null;
    }
    dockingPane.putDockable(factory.getClass(), dockable);

    
    fireViewOpened(dockable.getView());

    return dockable.getView();
  }

  
  private File chooseFile(String title, String action, JComponent accessory) {
    FileChooser chooser = new FileChooser(Workbench.this, title, action, "ged", EnvironmentChecker.getProperty(new String[] { "genj.gedcom.dir", "user.home" }, ".", "choose gedcom file"));
    chooser.setCurrentDirectory(new File(REGISTRY.get("last.dir", "user.home")));
    if (accessory != null)
      chooser.setAccessory(accessory);
    if (JFileChooser.APPROVE_OPTION != chooser.showDialog())
      return null;
    
    File file = chooser.getSelectedFile();
    if (file == null)
      return null;
    
    REGISTRY.put("last.dir", file.getParentFile().getAbsolutePath());
    
    return file;
  }
  
  
  private class WorkbenchAction extends Action2 implements WorkbenchListener {
    
    public void commitRequested(Workbench workbench) {
    }

    public void gedcomClosed(Workbench workbench, Gedcom gedcom) {
    }

    public void gedcomOpened(Workbench workbench, Gedcom gedcom) {
    }

    public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed) {
    }
    
    public void viewClosed(Workbench workbench, View view) {
    }
    
    public void viewRestored(Workbench workbench, View view) {
    }

    public void viewOpened(Workbench workbench, View view) {
    }

    public void workbenchClosing(Workbench workbench) {
    }
    
    public void processStarted(Workbench workbench, Trackable process) {
      setEnabled(false);
    }

    public void processStopped(Workbench workbench, Trackable process) {
      setEnabled(true);
    }
  }
  
  
   class ActionExit extends WorkbenchAction {
    
    
     ActionExit() {
      setText(RES, "cc.menu.exit");
      setImage(IMG_EXIT);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      exit();
    }
  }
  
  
   class ActionClose extends WorkbenchAction {
    
    
     ActionClose() {
      setText(RES, "cc.menu.close");
      setImage(IMG_CLOSE);
    }
    
    
    public void actionPerformed(ActionEvent event) {
      closeGedcom();
    }
  } 

  
   class ActionNew extends WorkbenchAction {

    
     ActionNew() {
      setText(RES, "cc.menu.new");
      setTip(RES, "cc.tip.create_file");
      setImage(IMG_NEW);
      install(Workbench.this, ACC_NEW, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    
    public void actionPerformed(ActionEvent event) {
      newGedcom();
    }

  } 

  
   class ActionOpen extends WorkbenchAction {
    
    private URL url;

    
     ActionOpen() {
      setTip(RES, "cc.tip.open_file");
      setText(RES, "cc.menu.open");
      setImage(IMG_OPEN);
      install(Workbench.this, ACC_OPEN, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    protected ActionOpen(int m, URL url) {
      this.url = url;
      String txt = ((char)('1'+m)) + " " + url.getFile();
      
      int i = txt.indexOf('/');
      int j = txt.lastIndexOf('/');
      if (i!=j) 
        txt = txt.substring(0, i + 1) + "..." + txt.substring(j);
      setMnemonic(txt.charAt(0));
      setText(txt);
    }

    public void actionPerformed(ActionEvent event) {
      if (url!=null)
        openGedcom(url);
      else
        openGedcom();
    }
  } 

  
   class ActionSave extends WorkbenchAction {
    
    private boolean saveAs;
    
    protected Gedcom gedcomBeingSaved;
    
    private GedcomWriter gedWriter;
    
    private Origin newOrigin;
    
    private Filter[] filters;
    
    private String progress;
    
    private GedcomIOException ioex = null;
    
    private File temp, file;
    
    private String password;

    
     ActionSave(boolean saveAs) {
      
      this.saveAs = saveAs;
      
      if (saveAs)
        setText(RES.getString("cc.menu.saveas"));
      else {
        setText(RES.getString("cc.menu.save"));
        
        install(Workbench.this, ACC_SAVE, JComponent.WHEN_IN_FOCUSED_WINDOW);
      }
      setTip(RES, "cc.tip.save_file");
      
      setImage(IMG_SAVE);
      setEnabled(context.getGedcom()!=null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (saveAs)
        saveAsGedcom();
      else
        saveGedcom();
    }

  } 

  
  private class ActionCloseView extends Action2 {
    public ActionCloseView() {
      install(Workbench.this, ACC_CLOSE, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      DialogHelper.visitContainers(KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(), new DialogHelper.ComponentVisitor() {
        public Component visit(Component parent, Component child) {
          
          if (child instanceof TabbedDock) {
            ((ViewDockable)((TabbedDock)child).getSelectedDockable()).close();
            return child;
          }
          
          if (child instanceof View) {
            ViewDockable.getDockable((View)child).close();
            return child;
          }
          return null;
        }
      });

    }
  }
  
  
   class ActionOpenView extends Action2 {
    
    private ViewFactory factory;

    
     ActionOpenView(ViewFactory vw) {
      factory = vw;
      setText(factory.getTitle());
      setTip(RES.getString("cc.tip.open_view", factory.getTitle()));
      setImage(factory.getImage());
    }

    
    public void actionPerformed(ActionEvent event) {
      openViewImpl(factory, context);
    }
  } 

  
  private class LayoutPersister extends XMLPersister {
    
    private List<ViewDockable> dockables = new ArrayList<ViewDockable>();
    
    LayoutPersister(DockingPane dockingPane, Reader layout) {
      super(dockingPane, layout, "1");
    }
    
    LayoutPersister(DockingPane dockingPane, Writer layout) {
      super(dockingPane, layout, "1");
    }
    
    @Override
    protected Object parseKey(String key) throws SAXParseException {
      
      try {
        return Class.forName(key);
      } catch (ClassNotFoundException e) {
      }
      
      return key;
    }
    
    @Override
    protected Dockable resolveDockable(Object key) {
      for (ViewFactory vf : viewFactories) {
        if (vf.getClass().equals(key)) {
          ViewDockable vd = new ViewDockable(Workbench.this, vf);
          dockables.add(vd);
          return vd;
        }
      }
      LOG.finer("can't find view factory for docking key"+key);
      return null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected String formatKey(Object key) throws SAXException {
      if (key instanceof String)
        return (String)key;
      return ((Class<? extends ViewFactory>)key).getName();
    }
    
    @Override
    public void load() {
      try {
        super.load();
      } catch (Exception ex) {
        LOG.log(Level.WARNING, "unable to load layout", ex);
      }
      
      
      for (ViewDockable vd : dockables)
        fireViewRestored(vd.getView());
  
    }
  
    @Override
    public void save() {
      try {
        super.save();
      } catch (Exception ex) {
        LOG.log(Level.WARNING, "unable to save layout", ex);
      }
    }
  }
  
  
  private class GedcomDockable extends DefaultDockable implements WorkbenchListener {
    
    private GedcomDockable(String title, ImageIcon img, JComponent content) {
      setContent(content);
      setTitle(title);
      setIcon(img);
    }
    
    @Override
    public void docked(Docked docked) {
      super.docked(docked);
      addWorkbenchListener(this);
    }
    
    @Override
    public void undocked() {
      super.undocked();
      removeWorkbenchListener(this);
    }

    public void commitRequested(Workbench workbench) {
    }

    public void gedcomClosed(Workbench workbench, Gedcom gedcom) {
      for (Object key : dockingPane.getDockableKeys())
        if (dockingPane.getDockable(key)==this)
          dockingPane.putDockable(key, null);
      return;
    }

    public void gedcomOpened(Workbench workbench, Gedcom gedcom) {
    }

    public void processStarted(Workbench workbench, Trackable process) {
    }

    public void processStopped(Workbench workbench, Trackable process) {
    }

    public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed) {
    }

    public void viewClosed(Workbench workbench, View view) {
    }
    
    public void viewRestored(Workbench workbench, View view) {
    }

    public void viewOpened(Workbench workbench, View view) {
    }

    public void workbenchClosing(Workbench workbench) {
    }
  }
  
  private class WorkbenchPane extends DockingPane implements WorkbenchListener {
    
    private List<JDialog> dialogs = new ArrayList<JDialog>();
    
    public WorkbenchPane() {
      addWorkbenchListener(this);
    }
    
    private void updateTitle(JDialog dlg, String title) {
      dlg.setTitle(title);
    }
    
    private void updateTitles(String title) {
      for (JDialog dlg : dialogs) 
        updateTitle(dlg, title);
    }
    
    @Override
    protected JDialog createDialog() {
      JDialog dialog = super.createDialog();
      if (context.getGedcom()!=null)
        updateTitle(dialog, context.getGedcom()!=null ? context.getGedcom().getName() : "");
      return dialog;
    }
    
    @Override
    protected void dismissDialog(JDialog dialog) {
      super.dismissDialog(dialog);
      dialogs.remove(dialog);
    }

    public void commitRequested(Workbench workbench) {
    }

    public void gedcomClosed(Workbench workbench, Gedcom gedcom) {
      updateTitles("");
    }

    public void gedcomOpened(Workbench workbench, Gedcom gedcom) {
      updateTitles(gedcom.getName());
    }

    public void processStarted(Workbench workbench, Trackable process) {
    }

    public void processStopped(Workbench workbench, Trackable process) {
    }

    public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed) {
    }

    public void viewClosed(Workbench workbench, View view) {
    }

    public void viewOpened(Workbench workbench, View view) {
    }

    public void viewRestored(Workbench workbench, View view) {
    }

    public void workbenchClosing(Workbench workbench) {
    }
  }

} 
