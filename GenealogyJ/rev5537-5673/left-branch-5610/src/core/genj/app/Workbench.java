
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomMetaListener;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertySex;
import genj.gedcom.Submitter;
import genj.gedcom.UnitOfWork;
import genj.io.Filter;
import genj.io.GedcomEncodingException;
import genj.io.GedcomEncryptionException;
import genj.io.GedcomIOException;
import genj.io.GedcomReader;
import genj.io.GedcomReaderContext;
import genj.io.GedcomReaderFactory;
import genj.io.GedcomWriter;
import genj.option.OptionProvider;
import genj.option.OptionsWidget;
import genj.util.EnvironmentChecker;
import genj.util.Origin;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.SafeProxy;
import genj.util.ServiceLookup;
import genj.util.swing.Action2;
import genj.util.swing.FileChooser;
import genj.util.swing.HeapStatusWidget;
import genj.util.swing.MenuHelper;
import genj.util.swing.NestedBlockLayout;
import genj.view.ActionProvider;
import genj.view.SelectionSink;
import genj.view.View;
import genj.view.ViewFactory;
import genj.view.ActionProvider.Purpose;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import spin.Spin;
import swingx.docking.Dockable;
import swingx.docking.DockingPane;


public class Workbench extends JPanel implements SelectionSink {

  private final static Logger LOG = Logger.getLogger("genj.app");
  private final static String ACC_SAVE = "ctrl S", ACC_EXIT = "ctrl X", ACC_NEW = "ctrl N", ACC_OPEN = "ctrl O";
  private final static Resources RES = Resources.get(Workbench.class);
  private final static Registry REGISTRY = Registry.get(Workbench.class);

  
  private List<WorkbenchListener> listeners = new CopyOnWriteArrayList<WorkbenchListener>();
  private List<Object> plugins = new ArrayList<Object>();
  private List<ViewFactory> viewFactories = ServiceLookup.lookup(ViewFactory.class);
  private WindowManager windowManager;
  private Menu menu = new Menu();
  private Toolbar toolbar = new Toolbar();
  private DockingPane dockingPane = new DockingPane();
  private Context context = new Context();
  private Runnable runOnExit;

  
  public Workbench(Runnable onExit) {

    
    windowManager = WindowManager.getInstance();
    runOnExit = onExit;
    
    
    for (PluginFactory pf : ServiceLookup.lookup(PluginFactory.class)) {
      LOG.info("Loading plugin "+pf.getClass());
      Object plugin = SafeProxy.harden(pf.createPlugin(this), LOG);
      plugins.add(plugin);
    }

    
    setLayout(new BorderLayout());
    add(toolbar, BorderLayout.NORTH);
    add(dockingPane, BorderLayout.CENTER);
    add(new StatusBar(), BorderLayout.SOUTH);

    
    new ActionSave(false).install(this, ACC_SAVE, JComponent.WHEN_IN_FOCUSED_WINDOW);
    new ActionExit().install(this, ACC_EXIT, JComponent.WHEN_IN_FOCUSED_WINDOW);
    new ActionOpen().install(this, ACC_OPEN, JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    
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
      int rc = windowManager.openDialog(null, RES.getString("cc.create.title"), WindowManager.WARNING_MESSAGE, RES.getString("cc.open.file_exists", file.getName()), Action2.yesNo(), Workbench.this);
      if (rc != 0)
        return;
    }
    
    
    if (!closeGedcom())
      return;
    
    
    Gedcom gedcom;
    try {
      gedcom = new Gedcom(Origin.create(new URL("file", "", file.getAbsolutePath())));
    } catch (MalformedURLException e) {
      LOG.log(Level.WARNING, "unexpected exception creating new gedcom", e);
      return;
    }
    
    try {
      Indi adam = (Indi) gedcom.createEntity(Gedcom.INDI);
      adam.addDefaultProperties();
      adam.setName("Adam", "");
      adam.setSex(PropertySex.MALE);
      Submitter submitter = (Submitter) gedcom.createEntity(Gedcom.SUBM);
      submitter.setName(EnvironmentChecker.getProperty(this, "user.name", "?", "user name used as submitter in new gedcom"));
    } catch (GedcomException e) {
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
      return openGedcom(new URL("file", "", file.getAbsolutePath()));
    } catch (MalformedURLException e) {
      
      return false;
    }
    
  }
  
  
  public boolean openGedcom(URL url) {

    
    if (!closeGedcom())
      return false;
    
    
    final Origin origin = Origin.create(url);

    
    GedcomReader reader;
    try {

      
      reader = GedcomReaderFactory.createReader(origin, new GedcomReaderContext() {
        public String getPassword() {
          return windowManager.openDialog(null, origin.getName(), WindowManager.QUESTION_MESSAGE, RES.getString("cc.provide_password"), "", Workbench.this);
        }
        public void handleWarning(int line, String warning, Context context) {
          
        }
      });

    } catch (IOException ex) {
      String txt = RES.getString("cc.open.no_connect_to", origin) + "\n[" + ex.getMessage() + "]";
      windowManager.openDialog(null, origin.getName(), WindowManager.ERROR_MESSAGE, txt, Action2.okOnly(), Workbench.this);
      return false;
    }

    
    
    
    try {
      setGedcom(reader.read());
    } catch (GedcomIOException ex) {
      
      windowManager.openDialog(null, origin.getName(), WindowManager.ERROR_MESSAGE, RES.getString("cc.open.read_error", "" + ex.getLine()) + ":\n" + ex.getMessage(), Action2.okOnly(), Workbench.this);
      
      return false;
    } finally {
      
      
    }
    
    
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
    
    
    fireSelection(context, true);
    
    for (WorkbenchListener listener: listeners)
      listener.gedcomOpened(gedcom);
  
    
    installTools();
    
    
  }
  
  
  public boolean saveAsGedcom() {
    
    if (context.getGedcom() == null)
      return false;
    
    
    fireCommit();
    
    
    
    SaveOptionsWidget options = new SaveOptionsWidget(context.getGedcom(), new Filter[] {});
    
    File file = chooseFile(RES.getString("cc.save.title"), RES.getString("cc.save.action"), options);
    if (file == null)
      return false;
  
    
    if (file.exists()) {
      int rc = windowManager.openDialog(null, RES.getString("cc.save.title"), WindowManager.WARNING_MESSAGE, RES.getString("cc.open.file_exists", file.getName()), Action2.yesNo(), Workbench.this);
      if (rc != 0) 
        return false;
    }
    
    
    if (!file.getName().endsWith(".ged"))
      file = new File(file.getAbsolutePath() + ".ged");
    
    Filter[] filters = options.getFilters();
    Gedcom gedcom = context.getGedcom();
    gedcom.setPassword(context.getGedcom().getPassword());
    gedcom.setEncoding(options.getEncoding());
    
    
    try {
      gedcom.setOrigin(Origin.create(new URL("file", "", file.getAbsolutePath())));
    } catch (Throwable t) {
      LOG.log(Level.FINER, "Failed to create origin for file "+file, t);
      return false;
    }
  
    return saveGedcomImpl(gedcom, filters);
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
        windowManager.openDialog(null, gedcom.getName(), WindowManager.ERROR_MESSAGE, RES.getString("cc.save.write_encoding_error", gee.getMessage()), Action2.okOnly(), Workbench.this);
        return false;
      } catch (IOException ex) {
        windowManager.openDialog(null, gedcom.getName(), WindowManager.ERROR_MESSAGE, RES.getString("cc.save.open_error", gedcom.getOrigin().getFile().getAbsolutePath()), Action2.okOnly(), Workbench.this);
        return false;
      }
      writer.setFilters(filters);

      
      writer.write();
      
      
      if (file.exists()) {
        File bak = new File(file.getAbsolutePath() + "~");
        if (bak.exists())
          bak.delete();
        file.renameTo(bak);
      }

      
      if (!temp.renameTo(file))
        throw new GedcomIOException("Couldn't move temporary " + temp.getName() + " to " + file.getName(), -1);

    } catch (GedcomIOException gioex) {
      windowManager.openDialog(null, gedcom.getName(), WindowManager.ERROR_MESSAGE, RES.getString("cc.save.write_error", "" + gioex.getLine()) + ":\n" + gioex.getMessage(), Action2.okOnly(), Workbench.this);
      return false;
    }



    
    
    if (gedcom.hasChanged())
      gedcom.doMuteUnitOfWork(new UnitOfWork() {
        public void perform(Gedcom gedcom) throws GedcomException {
          gedcom.setUnchanged();
        }
      });

    
    return false;
  }
  
  
  public void exit() {
    
    
    if (context.getGedcom()!=null)
      REGISTRY.put("restore.url", context.getGedcom().getOrigin().toString());
    
    
    if (!closeGedcom())
      return;
    
    
    runOnExit.run();
  }
  
  
  public boolean closeGedcom() {

    
    if (context.getGedcom()==null)
      return true;
    
    
    fireCommit();
    
    
    if (context.getGedcom().hasChanged()) {
      
      
      int rc = windowManager.openDialog("confirm-exit", null, WindowManager.WARNING_MESSAGE, RES.getString("cc.savechanges?", context.getGedcom().getName()), Action2.yesNoCancel(), Workbench.this);
      
      if (rc == 2)
        return false;
      
      if (rc == 0) 
        if (!saveGedcom())
          return false;

    }
    
    
    for (WorkbenchListener listener: listeners)
      listener.gedcomClosed(context.getGedcom());
    
    
    REGISTRY.put(context.getGedcom().getName(), context.toString());

    
    uninstallTools();    
    
    
    context = new Context();
    for (WorkbenchListener listener : listeners) 
      listener.selectionChanged(context, true);
    
    
    return true;
  }
  
  
  public void restoreGedcom() {

    String restore = REGISTRY.get("restore.url", (String)null);
    try {
      
      if (restore==null)
        restore = new File("gedcom/example.ged").toURI().toURL().toString();
      
      if (restore.length()>0)
        openGedcom(new URL(restore));
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "unexpected error", t);
    }
  }
  
  
  JMenuBar getMenuBar() {
    return menu;
  }

  
   List<ActionProvider> getActionProviders() {
    
    List<ActionProvider> result = new ArrayList<ActionProvider>();
    
    
    for (Object key : dockingPane.getDockableKeys()) {
      Dockable dockable = dockingPane.getDockable(key);
      if (dockable instanceof ViewDockable) {
        ViewDockable vd = (ViewDockable)dockable;
        if (vd.getContent() instanceof ActionProvider)
          result.add(SafeProxy.harden((ActionProvider)vd.getContent(), LOG));
      }
    }
    
    
    for (Object plugin : plugins) {
      if (plugin instanceof ActionProvider)
        result.add(SafeProxy.harden((ActionProvider)plugin, LOG));
    }
    
    
    Collections.sort(result, new Comparator<ActionProvider>() {
      public int compare(ActionProvider a1, ActionProvider a2) {
        return a2.getPriority() - a1.getPriority();
      }
    });
    
    return result;
  }
  
  public void fireCommit() {
    for (WorkbenchListener listener : listeners)
      listener.commitRequested();
  }
  
  public void fireSelection(Context context, boolean isActionPerformed) {
    
    
    if (context.getGedcom()!= this.context.getGedcom())
      throw new IllegalArgumentException("context selection on unknown gedcom");

    
    if (!isActionPerformed && this.context.equals(context))
      return;
    
    LOG.fine("fireSelection("+context+","+isActionPerformed+")");
    
    
    uninstallTools();    
    
    
    this.context = context;
    
    if (context.getGedcom()!=null) 
      REGISTRY.put(context.getGedcom().getName()+".context", context.toString());
    
    
    for (WorkbenchListener listener : listeners) 
      listener.selectionChanged(context, isActionPerformed);
    
    
    installTools();
  }
  
  private void uninstallTools() {
    
    if (context==null)
      return;
    
    
    removeGedcomListeners(menu.getTools());
    removeGedcomListeners(toolbar.getTools());
    
    
    menu.reset();
    toolbar.reset();
  }
  
  private void installTools() {
    
    if (context==null)
      return;
    
    
    uninstallTools();

    
    List<Action2> tools = new ArrayList<Action2>();
    
    for (ActionProvider provider : getActionProviders()) {
      for (Action2 action : provider.createActions(context, Purpose.TOOLBAR)) {
        if (action instanceof Action2.Group)
          LOG.warning("ActionProvider "+provider+" returned a group for toolbar");
        else {
          if (action instanceof ActionProvider.SeparatorAction)
            toolbar.addSeparator();
          else {
            toolbar.addTool(action);
          }
        }
      }
      for (Action2 action : provider.createActions(context, Purpose.MENU)) {
        if (action instanceof Action2.Group) {
          menu.addTool((Action2.Group)action);
        } else {
          LOG.warning("ActionProvider "+provider+" returned a non-group for menu");
        }
      }
    }
    
    
    addGedcomListeners(menu.getTools());
    addGedcomListeners(toolbar.getTools());
    
    
  }
  
  private void addGedcomListeners(List<Action2> actions) {
    if (context.getGedcom()==null)
      throw new IllegalArgumentException("context.gedcom==null");
    for (Action action : actions)
      if (action instanceof GedcomListener)
        context.getGedcom().addGedcomListener((GedcomListener)action);
  }
  
  private void removeGedcomListeners(List<Action2> actions) {
    if (context.getGedcom()==null)
      throw new IllegalArgumentException("context.gedcom==null");
    for (Action action : actions)
      if (action instanceof GedcomListener)
        context.getGedcom().removeGedcomListener((GedcomListener)action);
  }
  
  private void fireViewOpened(View view) {
    
    for (WorkbenchListener listener : listeners)
      listener.viewOpened(view);
  }

  private void fireViewClosed(View view) {
    
    for (WorkbenchListener listener : listeners)
      listener.viewClosed(view);
  }
  
  public void addWorkbenchListener(WorkbenchListener listener) {
    listeners.add(SafeProxy.harden(listener));
  }

  public void removeWorkbenchListener(WorkbenchListener listener) {
    listeners.remove(SafeProxy.harden(listener));
  }

  
  public View getView(Class<? extends ViewFactory> factoryClass) {
    ViewDockable dockable = (ViewDockable)dockingPane.getDockable(factoryClass);
    return dockable!=null ? dockable.getView() : null;
  }
  
  
  public void closeView(Class<? extends ViewFactory> factory) {
    
    View view = getView(factory);
    if (view==null)
      return;
    
    dockingPane.removeDockable(factory);

    
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
      dockable = new ViewDockable(Workbench.this, factory, REGISTRY);
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "cannot open view for "+factory.getClass().getName(), t);
      return null;
    }
    dockingPane.putDockable(factory.getClass(), dockable);

    
    fireViewOpened(dockable.getView());

    return dockable.getView();
  }

  
  private File chooseFile(String title, String action, JComponent accessory) {
    FileChooser chooser = new FileChooser(Workbench.this, title, action, "ged", EnvironmentChecker.getProperty(Workbench.this, new String[] { "genj.gedcom.dir", "user.home" }, ".", "choose gedcom file"));
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
  
  
  private class GedcomAction extends Action2 implements WorkbenchListener {
    
    public GedcomAction() {
      addWorkbenchListener(this);
    }

    public void commitRequested() {
    }

    public void gedcomClosed(Gedcom gedcom) {
      setEnabled(false);
    }

    public void gedcomOpened(Gedcom gedcom) {
      setEnabled(true);
    }

    public void selectionChanged(Context context, boolean isActionPerformed) {
    }
    
    public void viewClosed(View view) {
    }

    public void viewOpened(View view) {
    }

    public boolean workbenchClosing() {
      return true;
    }
  }

  
  private class ActionAbout extends Action2 {
    
    protected ActionAbout() {
      setText(RES, "cc.menu.about");
      setImage(Images.imgAbout);
    }

    
    public void actionPerformed(ActionEvent event) {
      windowManager.openDialog("about", RES.getString("cc.menu.about"), WindowManager.INFORMATION_MESSAGE, new AboutWidget(), Action2.okOnly(), Workbench.this);
      
    }
  } 

  
  private class ActionExit extends Action2 {
    
    
    protected ActionExit() {
      setText(RES, "cc.menu.exit");
      setImage(Images.imgExit);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      exit();
    }
  }
  
  
  private class ActionClose extends GedcomAction {
    
    
    protected ActionClose() {
      setText(RES, "cc.menu.close");
      setImage(Images.imgClose);
    }
    
    
    public void actionPerformed(ActionEvent event) {
      closeGedcom();
    }
  } 

  
  private class ActionNew extends Action2 {

    
    ActionNew() {
      setText(RES, "cc.menu.new");
      setTip(RES, "cc.tip.create_file");
      setImage(Images.imgNew);
    }

    
    public void actionPerformed(ActionEvent event) {
      newGedcom();
    }

  } 

  
  private class ActionOpen extends Action2 {

    
    protected ActionOpen() {
      setTip(RES, "cc.tip.open_file");
      setText(RES, "cc.menu.open");
      setImage(Images.imgOpen);
    }

    
    public void actionPerformed(ActionEvent event) {
      openGedcom();
    }
  } 

  
  private class ActionSave extends GedcomAction {
    
    private boolean saveAs;
    
    protected Gedcom gedcomBeingSaved;
    
    private GedcomWriter gedWriter;
    
    private Origin newOrigin;
    
    private Filter[] filters;
    
    private String progress;
    
    private GedcomIOException ioex = null;
    
    private File temp, file;
    
    private String password;

    
    protected ActionSave(boolean saveAs) {
      
      this.saveAs = saveAs;
      
      if (saveAs)
        setText(RES.getString("cc.menu.saveas"));
      else
        setText(RES.getString("cc.menu.save"));
      setTip(RES, "cc.tip.save_file");
      
      setImage(Images.imgSave);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (saveAs)
        saveAsGedcom();
      else
        saveGedcom();
    }

  } 

  
  private class ActionOpenView extends Action2 {
    
    private ViewFactory factory;

    
    protected ActionOpenView(ViewFactory vw) {
      factory = vw;
      setText(factory.getTitle());
      setTip(RES.getString("cc.tip.open_view", factory.getTitle()));
      setImage(factory.getImage());
    }

    
    public void actionPerformed(ActionEvent event) {
      openViewImpl(factory, context);
    }
  } 

  
  private class ActionOptions extends Action2 {
    
    protected ActionOptions() {
      setText(RES.getString("cc.menu.options"));
      setImage(OptionsWidget.IMAGE);
    }

    
    public void actionPerformed(ActionEvent event) {
      
      OptionsWidget widget = new OptionsWidget(getText());
      widget.setOptions(OptionProvider.getAllOptions());
      
      windowManager.openDialog("options", getText(), WindowManager.INFORMATION_MESSAGE, widget, Action2.okOnly(), Workbench.this);
      
    }
  } 

  
  private class StatusBar extends JPanel implements GedcomMetaListener, WorkbenchListener {

    private int commits;

    private JLabel[] ents = new JLabel[Gedcom.ENTITIES.length];
    private JLabel changes;

    StatusBar() {

      super(new NestedBlockLayout("<row><i/><f/><m/><n/><s/><b/><r/><cs wx=\"1\" gx=\"1\"/><mem/></row>"));

      for (int i = 0; i < Gedcom.ENTITIES.length; i++) {
        ents[i] = new JLabel("0", Gedcom.getEntityImage(Gedcom.ENTITIES[i]), SwingConstants.LEFT);
        add(ents[i]);
      }
      changes = new JLabel("", SwingConstants.RIGHT);
      
      add(changes);
      add(new HeapStatusWidget());

      addWorkbenchListener(this);
    }
    
    private void update(Gedcom gedcom) {
      for (int i=0;i<Gedcom.ENTITIES.length;i++) 
        ents[i].setText(count(gedcom, i));
      
      changes.setText(commits>0?RES.getString("stat.commits", new Integer(commits)):"");
    }
    
    public void gedcomWriteLockReleased(Gedcom gedcom) {
      commits++;
      update(gedcom);
    }

    private String count(Gedcom gedcom, int type) {
      return ""+gedcom.getEntities(Gedcom.ENTITIES[type]).size();
    }

    public void gedcomHeaderChanged(Gedcom gedcom) {
    }

    public void gedcomBeforeUnitOfWork(Gedcom gedcom) {
    }

    public void gedcomAfterUnitOfWork(Gedcom gedcom) {
    }

    public void gedcomWriteLockAcquired(Gedcom gedcom) {
    }

    public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
    }

    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
    }

    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
    }

    public void gedcomPropertyChanged(Gedcom gedcom, Property prop) {
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
    }

    public void commitRequested() {
    }

    public void gedcomClosed(Gedcom gedcom) {
      gedcom.removeGedcomListener((GedcomListener)Spin.over(this));
      commits = 0;
      for (int i=0;i<Gedcom.ENTITIES.length;i++) 
        ents[i].setText("-");
      changes.setText("");
    }


    public void gedcomOpened(Gedcom gedcom) {
      gedcom.addGedcomListener((GedcomListener)Spin.over(this));
      update(gedcom);
    }

    public void selectionChanged(Context context, boolean isActionPerformed) {
    }

    public void viewClosed(View view) {
    }

    public void viewOpened(View view) {
    }

    public boolean workbenchClosing() {
      return true;
    }

  } 

  
  private class Menu extends JMenuBar implements SelectionSink {
    
    private int toolStart, toolEnd;
    private List<Action2> tools = new ArrayList<Action2>();
    
    
    
    
    public void fireSelection(Context context, boolean isActionPerformed) {
      Workbench.this.fireSelection(context, isActionPerformed);
    }
    
    private Menu() {
      
      MenuHelper mh = new MenuHelper().pushMenu(this);

      
      mh.createMenu(RES.getString("cc.menu.file"));
      mh.createItem(new ActionNew());
      mh.createItem(new ActionOpen());
  
      Action2 save = new ActionSave(false);
      Action2 saveAs = new ActionSave(true);
      mh.createItem(save);
      mh.createItem(saveAs);
  
      mh.createSeparator();
      mh.createItem(new ActionClose());
  
      if (!EnvironmentChecker.isMac()) { 
                                         
        mh.createItem(new ActionExit());
      }

      mh.createSeparator();
      mh.createItem(new ActionAbout());

      mh.popMenu();
      
      
      mh.createMenu(RES.getString("cc.menu.view"));
  
      for (ViewFactory factory : viewFactories) {
        ActionOpenView action = new ActionOpenView(factory);
        mh.createItem(action);
      }
      mh.createSeparator();
      mh.createItem(new ActionOptions());
      mh.popMenu();
  
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      

      
      toolStart = getMenuCount();
      toolEnd = toolStart;

      
    }
    
    private void addTool(Action2.Group group) {
      if (group.size()==0)
        return;
      MenuHelper mh = new MenuHelper();
      add(mh.createMenu(group), toolEnd);
      tools.addAll(mh.getActions());
      toolEnd++;
    }
    
    private List<Action2> getTools() {
      return tools;
    }
    
    private void reset() {
      
      while (toolEnd>toolStart) {
        remove(toolEnd-1);
        toolEnd--;
      }
      tools.clear();
    }
    
  } 

  
  private class Toolbar extends JToolBar {

    private int toolIndex;
    private List<Action2> tools = new ArrayList<Action2>();
    
    
    private Toolbar() {

      setFloatable(false);

      
      add(new ActionNew());
      add(new ActionOpen());
      add(new ActionSave(false));
      
      addSeparator();
      
      
      toolIndex = getComponentCount();

      
    }
    
    private List<Action2> getTools() {
      return tools;
    }
    
    private void reset() {
      tools.clear();
      while (toolIndex<getComponentCount()) {
        remove(toolIndex);
      }
    }
    
    public void addTool(Action2 action) {
      tools.add(action);
      
      super.add(action);
    }
  }

} 
