
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomDirectory;
import genj.gedcom.GedcomException;
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
import genj.io.GedcomWriter;
import genj.option.OptionProvider;
import genj.option.OptionsWidget;
import genj.util.EnvironmentChecker;
import genj.util.Origin;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.SafeProxy;
import genj.util.ServiceLookup;
import genj.util.WordBuffer;
import genj.util.swing.Action2;
import genj.util.swing.FileChooser;
import genj.util.swing.HeapStatusWidget;
import genj.util.swing.MenuHelper;
import genj.util.swing.NestedBlockLayout;
import genj.view.ActionProvider;
import genj.view.View;
import genj.view.ViewFactory;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import swingx.docking.Dockable;
import swingx.docking.DockingPane;


public class Workbench extends JPanel {

  private final static Logger LOG = Logger.getLogger("genj.app");
  
  private final static String ACC_SAVE = "ctrl S", ACC_EXIT = "ctrl X", ACC_NEW = "ctrl N", ACC_OPEN = "ctrl O";

  private final static Resources RES = Resources.get(Workbench.class);

  
  private Registry registry;
  private WindowManager windowManager;
  private List<Action> gedcomActions = new ArrayList<Action>();
  private Menu menu = new Menu();
  private Toolbar toolbar = new Toolbar();
  private StatusBar stats = new StatusBar();
  private DockingPane dockingPane = new DockingPane();
  private Context context= null;
  private Runnable runOnExit;
  private List<WorkbenchListener> listeners = new CopyOnWriteArrayList<WorkbenchListener>();
  private List<Object> plugins = new ArrayList<Object>();

  
  public Workbench(Registry registry, Runnable onExit) {

    
    this.registry = new Registry(registry, "cc");
    windowManager = WindowManager.getInstance();
    runOnExit = onExit;
    
    
    for (PluginFactory pf : ServiceLookup.lookup(PluginFactory.class)) {
      LOG.info("Loading plugin "+pf.getClass());
      plugins.add(SafeProxy.harden(pf.createPlugin(this), LOG));
    }

    
    setLayout(new BorderLayout());
    add(toolbar, BorderLayout.NORTH);
    add(dockingPane, BorderLayout.CENTER);
    add(stats, BorderLayout.SOUTH);

    
    for (Action a : gedcomActions) 
      a.setEnabled(false);

    
    new ActionSave(false).install(this, ACC_SAVE, JComponent.WHEN_IN_FOCUSED_WINDOW);
    new ActionExit().install(this, ACC_EXIT, JComponent.WHEN_IN_FOCUSED_WINDOW);
    new ActionOpen().install(this, ACC_OPEN, JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    
  }
  
  
  public Context getContext() {
    return context!=null ? new Context(context) : null;
  }
  
  
  public boolean openGedcom() {

    
    File file = chooseFile(RES.getString("cc.open.title"), RES.getString("cc.open.action"), null);
    if (file == null)
      return false;
    registry.put("last.dir", file.getParentFile().getAbsolutePath());
    
    
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
    
    
    Origin origin = Origin.create(url);







    
    if (GedcomDirectory.getInstance().getGedcom(origin.getName()) != null) {
      windowManager.openDialog(null, origin.getName(), WindowManager.ERROR_MESSAGE, RES.getString("cc.open.already_open", origin.getName()), Action2.okOnly(), Workbench.this);
      return false;
    }
    
    
    
    Gedcom gedcom = null;
    String password = Gedcom.PASSWORD_UNKNOWN;
    while (gedcom==null) {
      
      
      GedcomReader reader;
      try {
  
        
        reader = new GedcomReader(origin);
  
        
        reader.setPassword(password);
  
      } catch (IOException ex) {
        String txt = RES.getString("cc.open.no_connect_to", origin) + "\n[" + ex.getMessage() + "]";
        windowManager.openDialog(null, origin.getName(), WindowManager.ERROR_MESSAGE, txt, Action2.okOnly(), Workbench.this);
        return false;
      }

      
      
    
      try {
        gedcom = reader.read();
        
        
        




        
      } catch (GedcomEncryptionException e) {
        
        password = windowManager.openDialog(null, origin.getName(), WindowManager.QUESTION_MESSAGE, RES.getString("cc.provide_password"), "", Workbench.this);
      } catch (GedcomIOException ex) {
        
        windowManager.openDialog(null, origin.getName(), WindowManager.ERROR_MESSAGE, RES.getString("cc.open.read_error", "" + ex.getLine()) + ":\n" + ex.getMessage(), Action2.okOnly(), Workbench.this);
        
        return false;
      } finally {
        stats.handleRead(reader.getLines());
        
        
        
      }
    }

    
    context = new Context(gedcom);

    GedcomDirectory.getInstance().registerGedcom(gedcom);

    
    for (Action a : gedcomActions) 
      a.setEnabled(true);

    
    
    
    
    
    
    
    
    
    
    

    stats.setGedcom(gedcom);

    
    for (Object plugin : plugins) if (plugin instanceof WorkbenchListener)
        SafeProxy.harden((WorkbenchListener)plugin).gedcomOpened(gedcom);
    
    
    return true;
  }
  
  
  public boolean saveAsGedcom() {
    
    if (context == null)
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
    String password = Gedcom.PASSWORD_NOT_SET;
    if (context.getGedcom().hasPassword())
      password = options.getPassword();
    String encoding = options.getEncoding();
    
    
    Gedcom gedcom = context.getGedcom();
    gedcom.setPassword(password);
    gedcom.setEncoding(encoding);
    
    try {
      gedcom.setOrigin(Origin.create(new URL("file", "", file.getAbsolutePath())));
    } catch (Throwable t) {
      LOG.log(Level.FINER, "Failed to create origin for file "+file, t);
      return false;
    }
  
    return saveGedcomImpl(gedcom, filters);
  }
  
  
  public boolean saveGedcom() {

    if (context == null)
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
      
      
      stats.handleWrite(writer.getLines());
      
      
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
    
    
    registry.put("restore.url", context!=null ? context.getGedcom().getOrigin().toString() : "");
    
    
    windowManager.closeAll();

    
    runOnExit.run();
  }
  
  
  public boolean closeGedcom() {
    
    if (context==null)
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
    
    
    for (Object key : dockingPane.getDockableKeys())
      dockingPane.removeDockable(key);
    
    
    for (Action a : gedcomActions) 
      a.setEnabled(false);
    
    
    stats.setGedcom(null);
    
    
    for (Object plugin : plugins) if (plugin instanceof WorkbenchListener)
        SafeProxy.harden((WorkbenchListener)plugin).gedcomClosed(context.getGedcom());

    
    GedcomDirectory.getInstance().unregisterGedcom(context.getGedcom());
    context = null;

    
    return true;
  }
  
  
  public void restoreGedcom() {

    String restore = registry.get("restore.url", (String)null);
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
  
  public enum ToolLocation {
    TOOLBAR,
    MAINMENU,
    TOOLSMENU
  }

  
  public void installTool(Action2 tool, ToolLocation location) {
    switch (location) {
    case TOOLBAR:
      toolbar.addTool(tool);
    case MAINMENU:
      menu.addTool(tool);
    case TOOLSMENU:
      throw new IllegalArgumentException("not supported yet");
    }
  }

  
  public void uninstallTool(Action2 tool) {
    menu.delTool(tool);
    toolbar.delTool(tool);
  }

  public void fireCommit() {
    for (WorkbenchListener listener : listeners)
      listener.commitRequested();
  }
  
  public void fireSelection(Context context, boolean isActionPerformed) {
    
    this.context = new Context(context);
    
    for (WorkbenchListener listener : listeners) 
      listener.selectionChanged(context, isActionPerformed);
  }

  public void addWorkbenchListener(WorkbenchListener listener) {
    listeners.add(listener);
  }

  public void removeWorkbenchListener(WorkbenchListener listener) {
    listeners.remove(listener);
  }

  
  public View getView(ViewFactory factory) {
    ViewDockable dockable = (ViewDockable)dockingPane.getDockable(factory.getClass());
    return dockable!=null ? dockable.getView() : null;
  }
  
  
  public void closeView(ViewFactory factory) {
    dockingPane.removeDockable(factory.getClass());
  }
  
  
  public View openView(ViewFactory factory, Context context) {
    
    
    if (context == null)
      throw new IllegalArgumentException("Cannot open view without context");

    
    ViewDockable dockable = (ViewDockable)dockingPane.getDockable(factory.getClass());
    if (dockable != null) {
      
      dockingPane.putDockable(factory.getClass(), dockable);
      
      return dockable.getView();
    }
    dockable = new ViewDockable(Workbench.this, factory, context);

    dockingPane.putDockable(factory.getClass(), dockable);

    return dockable.getView();
  }

  
  private File chooseFile(String title, String action, JComponent accessory) {
    FileChooser chooser = new FileChooser(Workbench.this, title, action, "ged", EnvironmentChecker.getProperty(Workbench.this, new String[] { "genj.gedcom.dir", "user.home" }, ".", "choose gedcom file"));
    chooser.setCurrentDirectory(new File(registry.get("last.dir", "user.home")));
    if (accessory != null)
      chooser.setAccessory(accessory);
    if (JFileChooser.APPROVE_OPTION != chooser.showDialog())
      return null;
    
    File file = chooser.getSelectedFile();
    if (file == null)
      return null;
    
    registry.put("last.dir", file.getParentFile().getAbsolutePath());
    
    return file;
  }

  
  private class ActionAbout extends Action2 {
    
    protected ActionAbout() {
      setText(RES, "cc.menu.about");
      setImage(Images.imgAbout);
    }

    
    public void actionPerformed(ActionEvent event) {
      if (windowManager.show("about"))
        return;
      windowManager.openDialog("about", RES.getString("cc.menu.about"), WindowManager.INFORMATION_MESSAGE, new AboutWidget(), Action2.okOnly(), Workbench.this);
      
    }
  } 

  
  private class ActionHelp extends Action2 {
    
    protected ActionHelp() {
      setText(RES, "cc.menu.contents");
      setImage(Images.imgHelp);
    }

    
    public void actionPerformed(ActionEvent event) {
      if (windowManager.show("help"))
        return;
      windowManager.openWindow("help", RES.getString("cc.menu.help"), Images.imgHelp, new HelpWidget(), null, null);
      
    }
  } 

  
  private class ActionExit extends Action2 {
    
    
    protected ActionExit() {
      setText(RES, "cc.menu.exit");
      setImage(Images.imgExit);
      setTarget(Workbench.this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      exit();
    }
  }
  
  
  private class ActionClose extends Action2 {
    
    
    protected ActionClose() {
      setText(RES, "cc.menu.close");
      setImage(Images.imgClose);
      setTarget(Workbench.this);
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
      
      try {
        Gedcom newGedcom = new Gedcom(Origin.create(new URL("file", "", file.getAbsolutePath())));
        
        try {
          Indi adam = (Indi) newGedcom.createEntity(Gedcom.INDI);
          adam.addDefaultProperties();
          adam.setName("Adam", "");
          adam.setSex(PropertySex.MALE);
          Submitter submitter = (Submitter) newGedcom.createEntity(Gedcom.SUBM);
          submitter.setName(EnvironmentChecker.getProperty(this, "user.name", "?", "user name used as submitter in new gedcom"));
        } catch (GedcomException e) {
        }
        
        GedcomDirectory.getInstance().registerGedcom(newGedcom);
      } catch (MalformedURLException e) {
      }

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

  
  private class ActionSave extends Action2 {
    
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
      
      setTarget(Workbench.this);
      
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
      setEnabled(false);
    }

    
    public void actionPerformed(ActionEvent event) {
      if (context==null)
        return;
      if (context.getEntity()==null) {
        Entity adam = context.getGedcom().getFirstEntity(Gedcom.INDI);
        if (adam!=null)
          context = new Context(adam);
      }
      openView(factory, context);
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

  
  private class StatusBar extends JPanel implements GedcomMetaListener {

    private Gedcom gedcom;
    private int commits;
    private int read, written;

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

    }
    
    void setGedcom(Gedcom gedcom) {
      if (this.gedcom!=null)
        this.gedcom.removeGedcomListener(this);
      this.gedcom = gedcom;
      if (this.gedcom!=null)
        this.gedcom.addGedcomListener(this);
      commits = 0;
      read = 0;
      written = 0;
      update();
    }

    public void gedcomWriteLockReleased(Gedcom gedcom) {
      commits++;
      update();
    }

    public synchronized void handleRead(int lines) {
      read += lines;
      update();
    }

    public synchronized void handleWrite(int lines) {
      written += lines;
      update();
    }
    
    private String count(int type) {
      return gedcom==null ? "-" : ""+gedcom.getEntities(Gedcom.ENTITIES[type]).size();
    }

    private void update() {

      for (int i=0;i<Gedcom.ENTITIES.length;i++) {
        ents[i].setText(count(i));
      }

      WordBuffer buf = new WordBuffer(", ");
      if (commits > 0)
        buf.append(RES.getString("stat.commits", new Integer(commits)));
      if (read > 0)
        buf.append(RES.getString("stat.lines.read", new Integer(read)));
      if (written > 0)
        buf.append(RES.getString("stat.lines.written", new Integer(written)));
      changes.setText(buf.toString());
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

  } 

  
  private class Menu extends JMenuBar {
    
    private int toolIndex;
    
    private Menu() {
      
      MenuHelper mh = new MenuHelper().pushMenu(this);

      
      mh.createMenu(RES.getString("cc.menu.file"));
      mh.createItem(new ActionNew());
      mh.createItem(new ActionOpen());
  
      Action2 save = new ActionSave(false);
      Action2 saveAs = new ActionSave(true);
      gedcomActions.add(save);
      gedcomActions.add(saveAs);
      mh.createItem(save);
      mh.createItem(saveAs);
  
      mh.createSeparator();
      mh.createItem(new ActionClose());
  
      if (!EnvironmentChecker.isMac()) { 
                                         
        mh.createItem(new ActionExit());
      }
  
      mh.popMenu();
      
      
      mh.createMenu(RES.getString("cc.menu.view"));
  
      for (ViewFactory factory : ServiceLookup.lookup(ViewFactory.class)) {
        ActionOpenView action = new ActionOpenView(factory);
        gedcomActions.add(action);
        mh.createItem(action);
      }
      mh.createSeparator();
      mh.createItem(new ActionOptions());
      mh.popMenu();
  
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      

      
      toolIndex = getMenuCount();

      
      add(Box.createGlue());
      mh.createMenu(RES.getString("cc.menu.help"));
      mh.createItem(new ActionHelp());
      mh.createItem(new ActionAbout());
  
      
    }
    
    private void delTool(Action2 tool) {
      for (int i=0; i<getMenuCount(); i++) {
        Component c = getComponent(i);
        if (!(c instanceof JMenu)) continue;
        JMenu menu = (JMenu)c;
        if (tool.equals(menu.getAction())) 
          remove(i--);
        else 
          delToolRecursive(menu, tool);
      }
    }
    
    private void delToolRecursive(JMenu menu, Action2 tool) {
      for (int i=0;i<menu.getMenuComponentCount();i++) {
        Component c = menu.getMenuComponent(i);
        if (!(c instanceof JMenuItem)) continue;
        JMenuItem item = (JMenuItem)c;
        if (tool.equals(item.getAction()))
          remove(i--);
        else if (item instanceof JMenu)
          delToolRecursive((JMenu)item, tool);
      }
    }
    
    private void addTool(Action2 tool) {
      
      
      add(new MenuHelper().createItem(tool), toolIndex);
      
    }
    
  } 

  
  private class Toolbar extends JToolBar {

    private int toolIndex;
    
    
    private Toolbar() {

      setFloatable(false);

      
      add(new ActionNew());
      add(new ActionOpen());
      ActionSave save = new ActionSave(false);
      add(save);
      gedcomActions.add(save);
      
      addSeparator();
      
      
      toolIndex = getComponentCount();

      
    }
    
    private void addTool(Action2 action) {
      add(action);
    }
    
    private void delTool(Action2 action) {
      for (int i=0; i<getComponentCount(); i++) {
        Component c = getComponent(i);
        if (c instanceof JButton && action.equals(((JButton)c).getAction())) {
          remove(i);
          return;
        }
      }
    }

  }

} 
