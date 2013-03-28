
package genj.app;

import genj.common.ContextListWidget;
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
import genj.gedcom.GedcomDirectory;
import genj.io.Filter;
import genj.io.GedcomEncodingException;
import genj.io.GedcomEncryptionException;
import genj.io.GedcomIOException;
import genj.io.GedcomReader;
import genj.io.GedcomWriter;
import genj.option.OptionProvider;
import genj.option.OptionsWidget;
import genj.util.DirectAccessTokenizer;
import genj.util.EnvironmentChecker;
import genj.util.MnemonicAndText;
import genj.util.Origin;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.WordBuffer;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.ChoiceWidget;
import genj.util.swing.FileChooser;
import genj.util.swing.HeapStatusWidget;
import genj.util.swing.MenuHelper;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.ProgressWidget;
import genj.view.CommitRequestedEvent;
import genj.view.ViewContext;
import genj.view.ViewFactory;
import genj.view.ViewHandle;
import genj.view.ViewManager;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import spin.Spin;


public class ControlCenter extends JPanel {
  
  private final static String
    ACC_SAVE = "ctrl S",
    ACC_EXIT = "ctrl X",
    ACC_NEW = "ctrl N",
    ACC_OPEN = "ctrl O";

  
  private JMenuBar menuBar; 
  private GedcomTableWidget tGedcoms;
  private Registry registry;
  private Resources resources = Resources.get(this);
  private WindowManager windowManager;
  private ViewManager viewManager;
  private List gedcomActions = new ArrayList();
  private List toolbarActions = new ArrayList();
  private Stats stats = new Stats();
  private ActionExit exit = new ActionExit();

  private Runnable runOnExit;
    
  
  public ControlCenter(Registry setRegistry, WindowManager winManager, Runnable onExit) {

    
    registry = new Registry(setRegistry, "cc");
    windowManager = winManager;
    viewManager = new ViewManager(windowManager);
    runOnExit = onExit;
    
    
    tGedcoms = new GedcomTableWidget(viewManager, registry) {
      public ViewContext getContext() {
        ViewContext result = super.getContext();
        if (result!=null) {
          result.addAction(new ActionSave(false, true));
          result.addAction(new ActionClose(true));
        }
        return result;
      };
    };
    
    
    tGedcoms.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        for (int i=0;i<gedcomActions.size();i++)
          ((Action2)gedcomActions.get(i)).setEnabled(tGedcoms.getSelectedGedcom() != null);
      }
    });
    
    
    setLayout(new BorderLayout());
    add(createToolBar(), BorderLayout.NORTH);
    add(new JScrollPane(tGedcoms), BorderLayout.CENTER);
    add(createStatusBar(), BorderLayout.SOUTH);

    
    menuBar = createMenuBar();

    
  }
  
  
  public void load(String[] files) {
    
    SwingUtilities.invokeLater(new ActionAutoOpen(files));
  }
  
  
   Action2 getExitAction() {
    return exit;
  }
  
  
   JMenuBar getMenuBar() {
    return menuBar;
  }
  
  
  private JPanel createStatusBar() {
    
    HeapStatusWidget mem = new HeapStatusWidget();
    mem.setToolTipText(resources.getString("cc.heap"));
    
    JPanel result = new JPanel(new NestedBlockLayout("<row><info wx=\"1\" gx=\"1\"/><mem/></row>"));
    result.add(stats);
    result.add(mem);
    
    return result;
  }
  
  
  private JToolBar createToolBar() {
    
    
    JToolBar result = new JToolBar();
    result.setFloatable(false);
    ButtonHelper bh =
      new ButtonHelper()
        .setInsets(4)
        .setContainer(result)
        .setFontSize(10);

    
    Action2 
      actionNew = new ActionNew(),
      actionOpen = new ActionOpen(),
      actionSave = new ActionSave(false, false);
    actionNew.setText(null);
    actionOpen.setText(null);
    actionSave.setText(null);
    gedcomActions.add(actionSave);
    
    toolbarActions.add(actionNew);
    toolbarActions.add(actionOpen);
    toolbarActions.add(actionSave);
    
    bh.create(actionNew);
    bh.create(actionOpen);
    bh.create(actionSave);
    
    result.addSeparator();

    ViewFactory[] factories = viewManager.getFactories();
    for (int i = 0; i < factories.length; i++) {
      ActionView action = new ActionView(-1, factories[i]);
      action.setText(null);
      bh.create(action);
      toolbarActions.add(action);
      gedcomActions.add(action);
    }
    
    
    result.add(Box.createGlue());

    



    
    
    return result;
  }
  
  
  private JMenuBar createMenuBar() {

    MenuHelper mh = new MenuHelper();
    JMenuBar result = mh.createBar();
    
    
    mh.createMenu(resources.getString("cc.menu.file"));
    mh.createItem(new ActionNew());
    mh.createItem(new ActionOpen());
    mh.createSeparator();
    
    Action2
      save = new ActionSave(false, false),
      saveAs = new ActionSave(true, false),
      close = new ActionClose(false);
    
    gedcomActions.add(save);
    gedcomActions.add(saveAs);
    gedcomActions.add(close);
    
    mh.createItem(save);
    mh.createItem(saveAs);
    mh.createItem(close);
    
    if (!EnvironmentChecker.isMac()) { 
      mh.createSeparator();
      mh.createItem(exit);
    }

    mh.popMenu().createMenu(resources.getString("cc.menu.view"));

    ViewFactory[] factories = viewManager.getFactories();
    for (int i = 0; i < factories.length; i++) {
      ActionView action = new ActionView(i+1, factories[i]);
      gedcomActions.add(action);
      mh.createItem(action);
    }
    mh.createSeparator();
    mh.createItem(new ActionOptions());

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    mh.popMenu().createMenu(resources.getString("cc.menu.help"));

    mh.createItem(new ActionHelp());
    mh.createItem(new ActionAbout());

    
    return result;
  }
  
  
  private File chooseFile(String title, String action, JComponent accessory) {
    FileChooser chooser = new FileChooser(
      ControlCenter.this, title, action, "ged",
      EnvironmentChecker.getProperty(ControlCenter.this, new String[] { "genj.gedcom.dir", "user.home" } , ".", "choose gedcom file")
    );
    chooser.setCurrentDirectory(new File(registry.get("last.dir", "user.home")));
    if (accessory!=null) chooser.setAccessory(accessory);
    if (JFileChooser.APPROVE_OPTION!=chooser.showDialog())
      return null;
    
    File file = chooser.getSelectedFile();
    if (file == null)
      return null;
    
    registry.put("last.dir", file.getParentFile().getAbsolutePath());
    
    return file;
  }

  
  private class ActionAbout extends Action2 {
    
    protected ActionAbout() {
      setText(resources, "cc.menu.about");
      setImage(Images.imgAbout);
    }
    
    protected void execute() {
      if (windowManager.show("about"))
        return;
      windowManager.openDialog("about",resources.getString("cc.menu.about"),WindowManager.INFORMATION_MESSAGE,new AboutWidget(viewManager),Action2.okOnly(),ControlCenter.this);
      
    }
  } 

  
  private class ActionHelp extends Action2 {
    
    protected ActionHelp() {
      setText(resources, "cc.menu.contents");
      setImage(Images.imgHelp);
    }
    
    protected void execute() {
      if (windowManager.show("help"))
        return;
      windowManager.openWindow("help",resources.getString("cc.menu.help"),Images.imgHelp,new HelpWidget(),null,null);
      
    }
  } 

  
  private class ActionExit extends Action2 {
    
    protected ActionExit() {
      setAccelerator(ACC_EXIT);
      setText(resources, "cc.menu.exit");
      setImage(Images.imgExit);
      setTarget(ControlCenter.this);
    }
    
    protected void execute() {
      
      for (Gedcom gedcom : GedcomDirectory.getInstance().getGedcoms()) {
        WindowManager.broadcast(new CommitRequestedEvent(gedcom, ControlCenter.this));
      }
      
      Collection save = new ArrayList();
      for (Iterator gedcoms=GedcomDirectory.getInstance().getGedcoms().iterator(); gedcoms.hasNext(); ) {
        
        Gedcom gedcom = (Gedcom) gedcoms.next();
        
        if (gedcom.hasChanged()) {
          
          int rc = windowManager.openDialog(
              "confirm-exit", null, WindowManager.WARNING_MESSAGE, 
              resources.getString("cc.savechanges?", gedcom.getName()), 
              Action2.yesNoCancel(), ControlCenter.this
            );
          
          if (rc==2) return;
          
          if (rc==0) {
            
            ActionExit.this.setEnabled(false);
            
            new ActionSave(gedcom) {
              
              protected void postExecute(boolean preExecuteResult) {
                try {
                  
                  super.postExecute(preExecuteResult);
                  
                  if (gedcomBeingSaved.hasChanged()) 
                    return;
                } finally {
                  
                  ActionExit.this.setEnabled(true);
                }
                
                ActionExit.this.trigger();
              }
            }.trigger();
            return;
          }
          
        }
        
        File file =gedcom.getOrigin().getFile(); 
        if (file==null||file.exists()) { 
          StringBuffer restore = new StringBuffer();
          restore.append(gedcom.getOrigin());
          restore.append(",");
          if (gedcom.hasPassword())
            restore.append(gedcom.getPassword());
          restore.append(",");
          ViewHandle[] views = viewManager.getViews(gedcom);
          for (int i=0, j=0;i<views.length;i++) {
            if (j++>0) restore.append(",");
            restore.append(views[i].persist());
          }
          save.add(restore);
        }
        
      }
      registry.put("open", save);
      
      
      windowManager.closeAll();
      
      
      runOnExit.run();

      
    }
  } 

  
  private class ActionNew extends Action2 {
    
    
    ActionNew() {
      setAccelerator(ACC_NEW);
      setText(resources, "cc.menu.new" );
      setTip(resources, "cc.tip.create_file");
      setImage(Images.imgNew);
    }

    
    protected void execute() {
      
        
        File file = chooseFile(resources.getString("cc.create.title"), resources.getString("cc.create.action"), null);
        if (file == null)
          return;
        if (!file.getName().endsWith(".ged"))
          file = new File(file.getAbsolutePath()+".ged");
        if (file.exists()) {
          int rc = windowManager.openDialog(
            null,
            resources.getString("cc.create.title"),
            WindowManager.WARNING_MESSAGE,
            resources.getString("cc.open.file_exists", file.getName()),
            Action2.yesNo(),
            ControlCenter.this
          );
          if (rc!=0)
            return;
        }
        
        try {
          Gedcom gedcom  = new Gedcom(Origin.create(new URL("file", "", file.getAbsolutePath())));
          
          try {
            Indi adam = (Indi)gedcom.createEntity(Gedcom.INDI);
            adam.addDefaultProperties();
            adam.setName("Adam","");
            adam.setSex(PropertySex.MALE);
            Submitter submitter = (Submitter)gedcom.createEntity(Gedcom.SUBM);
            submitter.setName(EnvironmentChecker.getProperty(this, "user.name", "?", "user name used as submitter in new gedcom"));
          } catch (GedcomException e) {
          }
          
          GedcomDirectory.getInstance().registerGedcom(gedcom);
        } catch (MalformedURLException e) {
        }

    }
    
  } 
  
  
  private class ActionOpen extends Action2 {

    
    private Origin origin;

    
    private GedcomReader reader;

    
    private GedcomIOException exception;

    
    protected Gedcom gedcomBeingLoaded;
    
    
    private String progress;
    
    
    private String password = Gedcom.PASSWORD_NOT_SET;
    
    
    private List views2restore = new ArrayList();
    
    
    protected ActionOpen(String restore) throws MalformedURLException {
      
      setAsync(ASYNC_SAME_INSTANCE);
      
      
      DirectAccessTokenizer tokens = new DirectAccessTokenizer(restore, ",", false);
      String url = tokens.get(0);
      String pwd = tokens.get(1);
      if (url==null)
        throw new IllegalArgumentException("can't restore "+restore);

      origin = Origin.create(url);
      if (pwd!=null&&pwd.length()>0) password = pwd;
      
      
      for (int i=2; ; i++) {
        String token = tokens.get(i);
        if (token==null) break;
        if (token.length()>0) views2restore.add(tokens.get(i));
      }
      
      
    }

    
    protected ActionOpen() {
      setAccelerator(ACC_OPEN); 
      setTip(resources, "cc.tip.open_file");
      setText(resources, "cc.menu.open");
      setImage(Images.imgOpen);
      setAsync(ASYNC_NEW_INSTANCE);
    }

    
    protected ActionOpen(Origin setOrigin) {
      setAsync(ASYNC_SAME_INSTANCE);
      origin = setOrigin;
    }

    
    protected boolean preExecute() {
      
      
      if (origin==null) {
        Action actions[] = {
          new Action2(resources, "cc.open.choice.local"),
          new Action2(resources, "cc.open.choice.inet" ),
          Action2.cancel(),
        };
        int rc = windowManager.openDialog(
          null,
          resources.getString("cc.open.title"),
          WindowManager.QUESTION_MESSAGE,
          resources.getString("cc.open.choice"),
          actions,
          ControlCenter.this
        );
        switch (rc) {
          case 0 :
            origin = chooseExisting();
            break;
          case 1 :
            origin = chooseURL();
            break;
        }
      }      
      
      return origin==null ? false : open(origin);
    }

    
    protected void execute() {
      try {
        gedcomBeingLoaded = reader.read();
      } catch (GedcomIOException ex) {
        exception = ex;
      }
    }

    
    protected void postExecute(boolean preExecuteResult) {
      
      
      windowManager.close(progress);
      
      
      if (exception != null) {
        
        
        if (exception instanceof GedcomEncryptionException) {
          
          password = windowManager.openDialog(
            null, 
            origin.getName(), 
            WindowManager.QUESTION_MESSAGE, 
            resources.getString("cc.provide_password"),
            "", 
            ControlCenter.this
          );
          
          if (password==null)
            password = Gedcom.PASSWORD_UNKNOWN;
          
          
          exception = null;
          trigger();
          
          return;
        }

        
        windowManager.openDialog(
          null, 
          origin.getName(), 
          WindowManager.ERROR_MESSAGE, 
          resources.getString("cc.open.read_error", "" + exception.getLine()) + ":\n" + exception.getMessage(),
          Action2.okOnly(), 
          ControlCenter.this
        );
        
        return;

      } 
        
      
      if (gedcomBeingLoaded != null) {
        
        GedcomDirectory.getInstance().registerGedcom(gedcomBeingLoaded);
      
        
        if (Options.getInstance().isRestoreViews) {
          for (int i=0;i<views2restore.size();i++) {
            ViewHandle handle = ViewHandle.restore(viewManager, gedcomBeingLoaded, (String)views2restore.get(i));
            if (handle!=null)
              new ActionSave(gedcomBeingLoaded).setTarget(handle.getView()).install(handle.getView(), JComponent.WHEN_IN_FOCUSED_WINDOW);
          }
        }          
        
      }
      
      
      if (reader!=null) {
        
        stats.handleRead(reader.getLines());
        
        
        List warnings = reader.getWarnings();
        if (!warnings.isEmpty()) {
          windowManager.openNonModalDialog(
            null,
            resources.getString("cc.open.warnings", gedcomBeingLoaded.getName()),
            WindowManager.WARNING_MESSAGE,
            new JScrollPane(new ContextListWidget(gedcomBeingLoaded, warnings)),
            Action2.okOnly(),
            ControlCenter.this
          );
        }
      }
        
      
    }

    
    private Origin chooseExisting() {
      
      File file = chooseFile(resources.getString("cc.open.title"), resources.getString("cc.open.action"), null);
      if (file == null)
        return null;
      
      registry.put("last.dir", file.getParentFile().getAbsolutePath());
      
      try {
        return Origin.create(new URL("file", "", file.getAbsolutePath()));
      } catch (MalformedURLException e) {
        return null;
      }
      
    }

    
    private Origin chooseURL() {

      
      String[] choices = (String[])registry.get("urls", new String[0]);
      ChoiceWidget choice = new ChoiceWidget(choices, "");
      JLabel label = new JLabel(resources.getString("cc.open.enter_url"));
      
      int rc = windowManager.openDialog(null, resources.getString("cc.open.title"), WindowManager.QUESTION_MESSAGE, new JComponent[]{label,choice}, Action2.okCancel(), ControlCenter.this);
    
      
      String item = choice.getText();
      if (rc!=0||item.length()==0) return null;

      
      Origin origin;
      try {
        origin = Origin.create(item);
      } catch (MalformedURLException ex) {
        windowManager.openDialog(null, item, WindowManager.ERROR_MESSAGE, resources.getString("cc.open.invalid_url"), Action2.okCancel(), ControlCenter.this);
        return null;
      }

      
      Set remember = new HashSet();
      remember.add(item);
      for (int c=0; c<choices.length&&c<9; c++) {
        remember.add(choices[c]);
      }
      registry.put("urls", remember);

      
      return origin;
    }

    
    private boolean open(Origin origin) {

      
      if (GedcomDirectory.getInstance().getGedcom(origin.getName())!=null) {
        windowManager.openDialog(null,origin.getName(),WindowManager.ERROR_MESSAGE,resources.getString("cc.open.already_open", origin.getName()),Action2.okOnly(),ControlCenter.this);
        return false;
      }

      
      try {
        
        
        reader = new GedcomReader(origin);
        
        
        reader.setPassword(password);

      } catch (IOException ex) {
        String txt = 
          resources.getString("cc.open.no_connect_to", origin)
            + "\n["
            + ex.getMessage()
            + "]";
        windowManager.openDialog(null, origin.getName(), WindowManager.ERROR_MESSAGE, txt, Action2.okOnly(), ControlCenter.this);
        return false;
      }

      
      progress = windowManager.openNonModalDialog(
        null,
        resources.getString("cc.open.loading", origin.getName()),
        WindowManager.INFORMATION_MESSAGE,
        new ProgressWidget(reader, getThread()),
        Action2.cancelOnly(),
        ControlCenter.this
      );

      
      return true;
    }
    
  } 

  
  private class ActionAutoOpen extends Action2 {
    
    private Collection files;
    
    private ActionAutoOpen(String[] args) {
      
      
      if (args.length>0) {
        files = Arrays.asList(args);
        return;
      }
      
      
      HashSet deflt = new HashSet();
      if (args.length==0) try {
        deflt.add(new File("gedcom/example.ged").toURI().toURL());
      } catch (Throwable t) {
        
      }

      
      files = (Set)registry.get("open", deflt);
      
    }
    
    
    public void execute() {

      
      for (Iterator it = files.iterator(); it.hasNext(); ) {
        String restore = it.next().toString();
        try {
          
          
          File local  = new File(restore);
          if (local.exists())
            restore = local.toURI().toURL().toString();
          
          ActionOpen open = new ActionOpen(restore);
          open.trigger();
        } catch (Throwable t) {
          App.LOG.log(Level.WARNING, "cannot restore "+restore, t);
        }
        
        
      }

      
    }
  } 

  
  private class ActionSave extends Action2 {
    
    private boolean ask;
    
    protected Gedcom gedcomBeingSaved;
    
    private GedcomWriter gedWriter;
    
    private Origin newOrigin;
    
    private Filter[] filters;
    
    private String progress;
    
    private GedcomIOException ioex = null;
    
    private File temp, file;
    
    private String password;
    
    
    protected ActionSave(Gedcom gedcom) {
      this(false, true);
      
      
      this.gedcomBeingSaved = gedcom;
    }
    
    protected ActionSave(boolean ask, boolean enabled) {
      
      setTarget(ControlCenter.this);
      
      if (!ask) setAccelerator(ACC_SAVE);
      
      this.ask = ask;
      
      if (ask)
        setText(resources.getString("cc.menu.saveas"));
      else
        setText(resources.getString("cc.menu.save"));
      setTip(resources, "cc.tip.save_file");
      
      setImage(Images.imgSave);
      setAsync(ASYNC_NEW_INSTANCE);
      setEnabled(enabled);
    }
    
    protected boolean preExecute() {

      
      if (gedcomBeingSaved==null) {
	      gedcomBeingSaved = tGedcoms.getSelectedGedcom();
	      if (gedcomBeingSaved == null)
	        return false;
      }
      
      
      Origin origin = gedcomBeingSaved.getOrigin();
      String encoding = gedcomBeingSaved.getEncoding();
      password = gedcomBeingSaved.getPassword();
      
      if (ask || origin==null || origin.getFile()==null) {

        
        SaveOptionsWidget options = new SaveOptionsWidget(gedcomBeingSaved, (Filter[])viewManager.getViews(Filter.class, gedcomBeingSaved));
        file = chooseFile(resources.getString("cc.save.title"), resources.getString("cc.save.action"), options);
        if (file==null)
          return false;

        
        if (!file.getName().endsWith(".ged"))
          file = new File(file.getAbsolutePath()+".ged");
        filters = options.getFilters();
        if (gedcomBeingSaved.hasPassword())
          password = options.getPassword();
        encoding = options.getEncoding();

        
        try {
          newOrigin = Origin.create(new URL("file", "", file.getAbsolutePath()));
        } catch (Throwable t) {
        }
        

      } else {

        
        file = origin.getFile();

      }

      
      if (file.exists()&&ask) {

        int rc = windowManager.openDialog(null,resources.getString("cc.save.title"),WindowManager.WARNING_MESSAGE,resources.getString("cc.open.file_exists", file.getName()),Action2.yesNo(),ControlCenter.this);
        if (rc!=0) {
          newOrigin = null;
          
          return false;
        }
        
      }
      
      
      WindowManager.broadcast(new CommitRequestedEvent(gedcomBeingSaved, ControlCenter.this));
      
      
      try {
        
        
        file = file.getCanonicalFile();

        
        temp = File.createTempFile("genj", ".ged", file.getParentFile());

        
        gedWriter =
          new GedcomWriter(gedcomBeingSaved, file.getName(), encoding, new FileOutputStream(temp));
          
        
        gedWriter.setFilters(filters);
        gedWriter.setPassword(password);
        
      } catch (GedcomEncodingException ex) {
        windowManager.openDialog(null,gedcomBeingSaved.getName(),
            WindowManager.ERROR_MESSAGE,
            resources.getString("cc.save.write_encoding_error", ex.getMessage()), 
            Action2.okOnly(),
            ControlCenter.this);
        return false;
        
      } catch (IOException ex) {
          
          windowManager.openDialog(null,gedcomBeingSaved.getName(),
                      WindowManager.ERROR_MESSAGE,
                      resources.getString("cc.save.open_error", file.getAbsolutePath()),
                      Action2.okOnly(),
                      ControlCenter.this);
        return false;
      }

      
      progress = windowManager.openNonModalDialog(
        null,
        resources.getString("cc.save.saving", file.getName()),
        WindowManager.INFORMATION_MESSAGE,
        new ProgressWidget(gedWriter, getThread()),
        Action2.cancelOnly(),
        getTarget()
      );

      
      return true;

    }

    
    protected void execute() {

      
      try {

        
        gedWriter.write();

        
        if (file.exists()) {
          File bak = new File(file.getAbsolutePath()+"~");
          if (bak.exists()) 
            bak.delete();
          file.renameTo(bak);
        }
        
        
        if (!temp.renameTo(file))
          throw new GedcomIOException("Couldn't move temporary "+temp.getName()+" to "+file.getName(), -1);
       
        
        if (newOrigin == null) gedcomBeingSaved.doMuteUnitOfWork(new UnitOfWork() {
          public void perform(Gedcom gedcom) throws GedcomException {
            gedcomBeingSaved.setUnchanged();
          }
        });

      } catch (GedcomIOException ex) {
        ioex = ex;
      }

      
    }

    
    protected void postExecute(boolean preExecuteResult) {

      
      windowManager.close(progress);
      
      
      if (ioex!=null) {
          if( ioex instanceof GedcomEncodingException)  {
              windowManager.openDialog(null,
                      gedcomBeingSaved.getName(),
                      WindowManager.ERROR_MESSAGE,
                      resources.getString("cc.save.write_encoding_error", ioex.getMessage() ), 
                      Action2.okOnly(),ControlCenter.this);
          }
          else {
              windowManager.openDialog(null,
                      gedcomBeingSaved.getName(),
                      WindowManager.ERROR_MESSAGE,
                      resources.getString("cc.save.write_error", "" + ioex.getLine()) + ":\n" + ioex.getMessage(),
                      Action2.okOnly(),ControlCenter.this);
              
          }
      } else {
        
        if (newOrigin != null) {
          
          
          Gedcom alreadyOpen  = GedcomDirectory.getInstance().getGedcom(newOrigin.getName());
          if (alreadyOpen!=null)
            GedcomDirectory.getInstance().unregisterGedcom(alreadyOpen);
          
          
          ActionOpen open = new ActionOpen(newOrigin) {
            protected void postExecute(boolean preExecuteResult) {
              super.postExecute(preExecuteResult);
              
              if (gedcomBeingLoaded!=null) {
                ViewManager.getRegistry(gedcomBeingLoaded).set(ViewManager.getRegistry(gedcomBeingSaved));
              }
            }
          };
          open.password = password;
          open.trigger();
          
        }
      }
      
      
      if (gedWriter!=null)
        stats.handleWrite(gedWriter.getLines());

      
    }
    
  } 

  
  private class ActionClose extends Action2 {
    
    protected ActionClose(boolean enabled) {
      setText(resources.getString("cc.menu.close"));
      setImage(Images.imgClose);
      setEnabled(enabled);
    }
    
    protected void execute() {
  
      
      final Gedcom gedcom = tGedcoms.getSelectedGedcom();
      if (gedcom == null)
        return;
  
      
      if (gedcom.hasChanged()) {
        
        int rc = windowManager.openDialog(null,null,WindowManager.WARNING_MESSAGE,
            resources.getString("cc.savechanges?", gedcom.getName()),
            Action2.yesNoCancel(),ControlCenter.this);
        
        if (rc==2)
          return;
        
        if (rc==0) {
          
          GedcomDirectory.getInstance().unregisterGedcom(gedcom);
          
          new ActionSave(gedcom) {
            protected void postExecute(boolean preExecuteResult) {
              
              super.postExecute(preExecuteResult);
              
              if (gedcomBeingSaved.hasChanged())
                GedcomDirectory.getInstance().registerGedcom(gedcomBeingSaved);
            }
          }.trigger();
          return;
        }
      }
  
      
      GedcomDirectory.getInstance().unregisterGedcom(gedcom);
  
      
    }
  } 

  
  private class ActionView extends Action2 {
    
    private ViewFactory factory;
    
    protected ActionView(int i, ViewFactory vw) {
      factory = vw;
      if (i>0) 
        setText(Integer.toString(i) +" "+ new MnemonicAndText(factory.getTitle(false)).getText());
      else
        setText(factory.getTitle(true));
      setTip(resources.getString("cc.tip.open_view", factory.getTitle(false)));
      setImage(factory.getImage());
      setEnabled(false);
    }
    
    protected void execute() {
      
      final Gedcom gedcom = tGedcoms.getSelectedGedcom();
      if (gedcom == null)
        return;
      
      ViewHandle handle = viewManager.openView(gedcom, factory);
      
      new ActionSave(gedcom).setTarget(handle.getView()).install(handle.getView(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
  } 

  
  private class ActionOptions extends Action2 {
    
    protected ActionOptions() {
      setText(resources.getString("cc.menu.options"));
      setImage(OptionsWidget.IMAGE);
    }
    
    protected void execute() {
      
      Options.getInstance().setWindowManager(windowManager);
      
      OptionsWidget widget = new OptionsWidget(getText());
      widget.setOptions(OptionProvider.getAllOptions());
      
      windowManager.openDialog("options", getText(), WindowManager.INFORMATION_MESSAGE, widget, Action2.okOnly(), ControlCenter.this);
      
    }
  } 

  
  private class Stats extends JLabel implements GedcomMetaListener, GedcomDirectory.Listener {
    
    private int commits;
    private int read,written;
    
    private Stats() {
      setHorizontalAlignment(SwingConstants.LEFT);
      GedcomDirectory.getInstance().addListener(this);
    }

    public void gedcomWriteLockReleased(Gedcom gedcom) {
      commits++;
      update();
    }
    
    public synchronized void handleRead(int lines) {
      read+=lines;
      update();
    }
    
    public synchronized void handleWrite(int lines) {
      written+=lines;
      update();
    }
    
    private void update() {
      WordBuffer buf = new WordBuffer(", ");
      if (commits>0)
        buf.append(resources.getString("stat.commits", new Integer(commits)));
      if (read>0)
        buf.append(resources.getString("stat.lines.read", new Integer(read)));
      if (written>0)
        buf.append(resources.getString("stat.lines.written", new Integer(written)));
      setText(buf.toString());
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

    public void gedcomRegistered(int num, Gedcom gedcom) {
      gedcom.addGedcomListener(this);
   }

    public void gedcomUnregistered(int num, Gedcom gedcom) {
      gedcom.removeGedcomListener(this);
    }
    
  } 
  
} 
