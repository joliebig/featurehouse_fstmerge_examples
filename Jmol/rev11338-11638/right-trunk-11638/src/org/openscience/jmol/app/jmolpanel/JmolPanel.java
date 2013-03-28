
package org.openscience.jmol.app.jmolpanel;

import org.jmol.api.*;
import org.jmol.export.dialog.Dialog;
import org.jmol.export.history.HistoryFile;
import org.jmol.export.image.ImageCreator;
import org.jmol.i18n.GT;
import org.jmol.util.*;
import org.jmol.viewer.JmolConstants;
import org.openscience.jmol.app.*;
import org.openscience.jmol.app.webexport.WebExport;

import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.awt.print.*;
import java.beans.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

public class JmolPanel extends JPanel implements SplashInterface {

  

  public JmolViewer viewer;
  JmolAdapter modelAdapter;
  JmolApp jmolApp;


  DisplayPanel display;
  StatusBar status;
  protected GaussianDialog gaussianDialog;
  private PreferencesDialog preferencesDialog;
  MeasurementTable measurementTable;
  RecentFilesDialog recentFiles;
  
  public AtomSetChooser atomSetChooser;
  private ExecuteScriptAction executeScriptAction;
  protected JFrame frame;

  

  GuiMap guimap = new GuiMap();

  private static int numWindows = 0;
  private static Dimension screenSize = null;
  int startupWidth, startupHeight;

  PropertyChangeSupport pcs = new PropertyChangeSupport(this);

  
  private final static String CONSOLE_WINDOW_NAME = "Console";
  private final static String EDITOR_WINDOW_NAME = "ScriptEditor";
  private final static String SCRIPT_WINDOW_NAME = "ScriptWindow";
  private final static String FILE_OPEN_WINDOW_NAME = "FileOpen";
  private final static String WEB_MAKER_WINDOW_NAME = "JmolWebPageMaker";


  
  
  protected SplashInterface splash;

  protected JFrame consoleframe;
  
  String appletContext;
  
  static HistoryFile historyFile;

  public JmolPanel(JmolApp jmolApp, Splash splash, JFrame frame, JmolPanel parent,
      int startupWidth, int startupHeight, String commandOptions, Point loc) {
    super(true);
    this.jmolApp = jmolApp;
    this.frame = frame;
    this.startupWidth = startupWidth;
    this.startupHeight = startupHeight;
    historyFile = jmolApp.historyFile;

    numWindows++;

    try {
      say("history file is " + historyFile.getFile().getAbsolutePath());
    } catch (Exception e) {
    }

    frame.setTitle("Jmol");
    frame.getContentPane().setBackground(Color.lightGray);
    frame.getContentPane().setLayout(new BorderLayout());

    this.splash = splash;

    setBorder(BorderFactory.createEtchedBorder());
    setLayout(new BorderLayout());

    status = (StatusBar) createStatusBar();
    say(GT._("Initializing 3D display..."));

    

    
    
    
    display = new DisplayPanel(this);
    StatusListener myStatusListener = new StatusListener(this, display);
    viewer = JmolViewer.allocateViewer(display, modelAdapter, null, null, null,
        appletContext = commandOptions, myStatusListener);
    display.setViewer(viewer);
    myStatusListener.setViewer(viewer);
    
    if (!jmolApp.haveDisplay)
      return;
    say(GT._("Initializing Preferences..."));
    preferencesDialog = new PreferencesDialog(this, frame, guimap, viewer);
    say(GT._("Initializing Recent Files..."));
    recentFiles = new RecentFilesDialog(frame);
    say(GT._("Initializing Script Window..."));
    viewer.getProperty("DATA_API", "getAppConsole", Boolean.TRUE);


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    


    
    say(GT._("Building Command Hooks..."));
    commands = new Hashtable();
    if (display != null) {
      Action[] actions = getActions();
      for (int i = 0; i < actions.length; i++) {
        Action a = actions[i];
        commands.put(a.getValue(Action.NAME), a);
      }
    }

    menuItems = new Hashtable();
    say(GT._("Building Menubar..."));
    executeScriptAction = new ExecuteScriptAction();
    menubar = createMenubar();
    add("North", menubar);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add("North", createToolbar());

    JPanel ip = new JPanel();
    ip.setLayout(new BorderLayout());
    ip.add("Center", display);
    panel.add("Center", ip);
    add("Center", panel);
    add("South", status);

    say(GT._("Starting display..."));
    display.start();

    if (jmolApp.menuFile != null) {
      viewer.getProperty("DATA_API", "setMenu", viewer.getFileAsString(jmolApp.menuFile));
    }

    
    if (loc != null) {
      frame.setLocation(loc);
    } else if (parent != null) {
      Point location = parent.frame.getLocationOnScreen();
      int maxX = screenSize.width - 50;
      int maxY = screenSize.height - 50;

      location.x += 40;
      location.y += 40;
      if ((location.x > maxX) || (location.y > maxY)) {
        location.setLocation(0, 0);
      }
      frame.setLocation(location);
    }
    frame.getContentPane().add("Center", this);

    frame.addWindowListener(new JmolPanel.AppCloser());
    frame.pack();
    frame.setSize(startupWidth, startupHeight);
    ImageIcon jmolIcon = JmolResourceHandler.getIconX("icon");
    Image iconImage = jmolIcon.getImage();
    frame.setIconImage(iconImage);

    
    Component c = (Component) viewer.getProperty("DATA_API","getAppConsole", null);
    if (c != null)
      historyFile.repositionWindow(SCRIPT_WINDOW_NAME, c, 200, 100);
    c = (Component) viewer.getProperty("DATA_API","getScriptEditor", null);
    if (c != null)
      historyFile.repositionWindow(EDITOR_WINDOW_NAME, c, 150, 50);

    say(GT._("Setting up Drag-and-Drop..."));
    FileDropper dropper = new FileDropper();
    final JFrame f = frame;
    dropper.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        
        f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (evt.getPropertyName().equals(FileDropper.FD_PROPERTY_FILENAME)) {
          final String filename = evt.getNewValue().toString();
          viewer.openFileAsynchronously(filename);
        } else if (evt.getPropertyName().equals(FileDropper.FD_PROPERTY_INLINE)) {
          final String inline = evt.getNewValue().toString();
          viewer.openStringInline(inline);
        }
        f.setCursor(Cursor.getDefaultCursor());
      }
    });

    this.setDropTarget(new DropTarget(this, dropper));
    this.setEnabled(true);

    say(GT._("Launching main frame..."));
  }

  protected static void startJmol(JmolApp jmolApp) {
    
    Dialog.setupUIManager();
    
    JFrame jmolFrame = new JFrame();
    
    
    
    Jmol jmol = null;
    
    try {
      if (jmolApp.jmolPosition != null) {
        jmolFrame.setLocation(jmolApp.jmolPosition);
      }
      
      jmol = getJmol(jmolApp, jmolFrame);

      jmolApp.startViewer(jmol.viewer, jmol.splash);
    
    } catch (Throwable t) {
      System.out.println("uncaught exception: " + t);
      t.printStackTrace();
    }

    if (jmolApp.haveConsole) {
      
      jmol.consoleframe = new JFrame(GT._("Jmol Console"));
      jmol.consoleframe.setIconImage(jmol.frame.getIconImage());
      try {
        final ConsoleTextArea consoleTextArea = new ConsoleTextArea(true);
        consoleTextArea.setFont(java.awt.Font.decode("monospaced"));
        jmol.consoleframe.getContentPane().add(new JScrollPane(consoleTextArea),
            java.awt.BorderLayout.CENTER);
        if (Boolean.getBoolean("clearConsoleButton")) {
          JButton buttonClear = new JButton(GT._("Clear"));
          buttonClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              consoleTextArea.setText("");
            }
          });
          jmol.consoleframe.getContentPane().add(buttonClear,
              java.awt.BorderLayout.SOUTH);
        }
      } catch (IOException e) {
        JTextArea errorTextArea = new JTextArea();
        errorTextArea.setFont(java.awt.Font.decode("monospaced"));
        jmol.consoleframe.getContentPane().add(new JScrollPane(errorTextArea),
            java.awt.BorderLayout.CENTER);
        errorTextArea.append(GT._("Could not create ConsoleTextArea: ") + e);
      }
      setWindow(CONSOLE_WINDOW_NAME, jmol.consoleframe, jmol);     
    }
  }

  public static Jmol getJmol(JmolApp jmolApp, JFrame frame) {

    String commandOptions = jmolApp.commandOptions;
    Splash splash = null;
    if (jmolApp.haveDisplay && jmolApp.splashEnabled) {
      ImageIcon splash_image = JmolResourceHandler.getIconX("splash");
      if (!jmolApp.isSilent)
        Logger.info("splash_image=" + splash_image);
      splash = new Splash((commandOptions != null
          && commandOptions.indexOf("-L") >= 0 ? null : frame), splash_image);
      splash.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      splash.showStatus(GT._("Creating main window..."));
      splash.showStatus(GT._("Initializing Swing..."));
    }
    try {
      UIManager
          .setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception exc) {
      System.err.println("Error loading L&F: " + exc);
    }

    screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    if (splash != null)
      splash.showStatus(GT._("Initializing Jmol..."));

    Jmol window = new Jmol(jmolApp, splash, frame, null, jmolApp.startupWidth,
        jmolApp.startupHeight, commandOptions, null);
    if (jmolApp.haveDisplay)
      frame.setVisible(true);
    return window;
  }

  

  private static void setWindow(String name,
                                          JFrame frame, JmolPanel jmol) {
    Point location = jmol.frame.getLocation();
    Dimension size = jmol.frame.getSize();
    Dimension consoleSize = historyFile.getWindowSize(name);
    Point consolePosition = historyFile.getWindowPosition(name);
    if ((consoleSize != null) && (consolePosition != null)) {
      frame.setBounds(consolePosition.x, consolePosition.y,
          consoleSize.width, consoleSize.height);
    } else {
      frame.setBounds(location.x, location.y + size.height,
          size.width, 200);
    }

    Boolean consoleVisible = historyFile.getWindowVisibility(name);
    if ((consoleVisible != null) && (consoleVisible.equals(Boolean.TRUE))) {
      frame.setVisible(true);
    }
  }

  public void showStatus(String message) {
    splash.showStatus(message);    
  }

  void report(String str) {
    if (jmolApp.isSilent)
      return;
    Logger.info(str);
  }

  private void say(String message) {
    if (jmolApp.haveDisplay)
      if (splash == null) {
        report(message);
      } else {
        splash.showStatus(message);
      }
  }

  
  public Action[] getActions() {

    ArrayList actions = new ArrayList();
    actions.addAll(Arrays.asList(defaultActions));
    actions.addAll(Arrays.asList(display.getActions()));
    actions.addAll(Arrays.asList(preferencesDialog.getActions()));
    return (Action[]) actions.toArray(new Action[0]);
  }

  
  protected final class AppCloser extends WindowAdapter {

    public void windowClosing(WindowEvent e) {
      JmolPanel.this.doClose();
    }
  }

  void doClose() {
    
    if (historyFile != null) {
      if (display != null) {
        jmolApp.border.x = this.getFrame().getWidth() - display.dimSize.width;
        jmolApp.border.y = this.getFrame().getHeight() - display.dimSize.height;
        historyFile.addWindowInfo("Jmol", this.frame, jmolApp.border);
      }
      
      
    }
    dispose(this.frame);
  }

  private void dispose(JFrame f) {
    Component c = (Component) viewer.getProperty("DATA_API","getAppConsole", null);
    if (c != null)
      historyFile.addWindowInfo(SCRIPT_WINDOW_NAME, c, null);
    c = (Component) viewer.getProperty("DATA_API","getScriptEditor", null);
    if (c != null)
      historyFile.addWindowInfo(EDITOR_WINDOW_NAME, c, null);
    if (historyFile != null && webExport != null) {
      WebExport.saveHistory();
      WebExport.cleanUp();
    }
    if (numWindows <= 1) {
      
      report(GT._("Closing Jmol..."));
      
      System.exit(0);
    } else {
      numWindows--;
      viewer.setModeMouse(JmolConstants.MOUSE_NONE);
      try {
        f.dispose();
      } catch (Exception e) {
        System.out.println("frame disposal exception");
        
      }
    }
  }

  protected void setupNewFrame(String state) {
    JFrame newFrame = new JFrame();
    JFrame f = this.frame;
    Jmol j = new Jmol(jmolApp, null, newFrame, (Jmol) this, startupWidth, startupHeight,
        "", (state == null ? null : f.getLocationOnScreen()));
    newFrame.setVisible(true);
    if (state != null) {
      dispose(f);
      j.viewer.evalStringQuiet(state);
    }
  }

  
  protected Frame getFrame() {

    for (Container p = getParent(); p != null; p = p.getParent()) {
      if (p instanceof Frame) {
        return (Frame) p;
      }
    }
    return null;
  }

  
  protected JMenuItem createMenuItem(String cmd) {

    JMenuItem mi;
    if (cmd.endsWith("Check")) {
      mi = guimap.newJCheckBoxMenuItem(cmd, false);
    } else {
      mi = guimap.newJMenuItem(cmd);
    }

    ImageIcon f = JmolResourceHandler.getIconX(cmd + "Image");
    if (f != null) {
      mi.setHorizontalTextPosition(SwingConstants.RIGHT);
      mi.setIcon(f);
    }

    if (cmd.endsWith("Script")) {
      mi.setActionCommand(JmolResourceHandler.getStringX(cmd));
      mi.addActionListener(executeScriptAction);
    } else {
      mi.setActionCommand(cmd);
      Action a = getAction(cmd);
      if (a != null) {
        mi.addActionListener(a);
        a.addPropertyChangeListener(new ActionChangedListener(mi));
        mi.setEnabled(a.isEnabled());
      } else {
        mi.setEnabled(false);
      }
    }
    menuItems.put(cmd, mi);
    return mi;
  }

  
  protected JMenuItem getMenuItem(String cmd) {
    return (JMenuItem) menuItems.get(cmd);
  }

  
  protected Action getAction(String cmd) {
    return (Action) commands.get(cmd);
  }

  
  private Component createToolbar() {

    toolbar = new JToolBar();
    String[] tool1Keys = tokenize(JmolResourceHandler.getStringX("toolbar"));
    for (int i = 0; i < tool1Keys.length; i++) {
      if (tool1Keys[i].equals("-")) {
        toolbar.addSeparator();
      } else {
        toolbar.add(createTool(tool1Keys[i]));
      }
    }

    
    toolbar.add(Box.createHorizontalGlue());

    return toolbar;
  }

  
  protected Component createTool(String key) {
    return createToolbarButton(key);
  }

  
  protected AbstractButton createToolbarButton(String key) {

    ImageIcon ii = JmolResourceHandler.getIconX(key + "Image");
    AbstractButton b = new JButton(ii);
    String isToggleString = JmolResourceHandler.getStringX(key + "Toggle");
    if (isToggleString != null) {
      boolean isToggle = Boolean.valueOf(isToggleString).booleanValue();
      if (isToggle) {
        b = new JToggleButton(ii);
        if (key.equals("rotate"))
          display.buttonRotate = (JToggleButton) b;
        display.toolbarButtonGroup.add(b);
        String isSelectedString = JmolResourceHandler.getStringX(key
            + "ToggleSelected");
        if (isSelectedString != null) {
          boolean isSelected = Boolean.valueOf(isSelectedString).booleanValue();
          b.setSelected(isSelected);
        }
      }
    }
    b.setRequestFocusEnabled(false);
    b.setMargin(new Insets(1, 1, 1, 1));

    Action a = null;
    String actionCommand = null;
    if (key.endsWith("Script")) {
      actionCommand = JmolResourceHandler.getStringX(key);
      a = executeScriptAction;
    } else {
      actionCommand = key;
      a = getAction(key);
    }
    if (a != null) {
      b.setActionCommand(actionCommand);
      b.addActionListener(a);
      a.addPropertyChangeListener(new ActionChangedListener(b));
      b.setEnabled(a.isEnabled());
    } else {
      b.setEnabled(false);
    }

    String tip = guimap.getLabel(key + "Tip");
    if (tip != null) {
      b.setToolTipText(tip);
    }

    return b;
  }

  
  protected String[] tokenize(String input) {

    Vector v = new Vector();
    StringTokenizer t = new StringTokenizer(input);
    String cmd[];

    while (t.hasMoreTokens()) {
      v.addElement(t.nextToken());
    }
    cmd = new String[v.size()];
    for (int i = 0; i < cmd.length; i++) {
      cmd[i] = (String) v.elementAt(i);
    }

    return cmd;
  }

  protected Component createStatusBar() {
    return new StatusBar();
  }

  
  protected JMenuBar createMenubar() {
    JMenuBar mb = new JMenuBar();
    addNormalMenuBar(mb);
    
    addMacrosMenuBar(mb);
    
    
    
    
    
    mb.add(Box.createHorizontalGlue());
    addHelpMenuBar(mb);
    return mb;
  }

  protected void addMacrosMenuBar(JMenuBar menuBar) {
    
    JMenu macroMenu = guimap.newJMenu("macros");
    File macroDir = new File(System.getProperty("user.home")
        + System.getProperty("file.separator") + ".jmol"
        + System.getProperty("file.separator") + "macros");
    report("User macros dir: " + macroDir);
    report("       exists: " + macroDir.exists());
    report("  isDirectory: " + macroDir.isDirectory());
    if (macroDir.exists() && macroDir.isDirectory()) {
      File[] macros = macroDir.listFiles();
      for (int i = 0; i < macros.length; i++) {
        
        String macroName = macros[i].getName();
        if (macroName.endsWith(".macro")) {
          if (Logger.debugging) {
            Logger.debug("Possible macro found: " + macroName);
          }
          FileInputStream macro = null;
          try {
            macro = new FileInputStream(macros[i]);
            Properties macroProps = new Properties();
            macroProps.load(macro);
            String macroTitle = macroProps.getProperty("Title");
            String macroScript = macroProps.getProperty("Script");
            JMenuItem mi = new JMenuItem(macroTitle);
            mi.setActionCommand(macroScript);
            mi.addActionListener(executeScriptAction);
            macroMenu.add(mi);
          } catch (IOException exception) {
            System.err.println("Could not load macro file: ");
            System.err.println(exception);
          } finally {
            if (macro != null) {
              try {
                macro.close();
              } catch (IOException e) {
                
              }
              macro = null;
            }
          }
        }
      }
    }
    menuBar.add(macroMenu);
  }

  protected void addNormalMenuBar(JMenuBar menuBar) {
    String[] menuKeys = tokenize(JmolResourceHandler.getStringX("menubar"));
    for (int i = 0; i < menuKeys.length; i++) {
      if (menuKeys[i].equals("-")) {
        menuBar.add(Box.createHorizontalGlue());
      } else {
        JMenu m = createMenu(menuKeys[i]);
        if (m != null)
          menuBar.add(m);
      }
    }
  }

  protected void addHelpMenuBar(JMenuBar menuBar) {
    String menuKey = "help";
    JMenu m = createMenu(menuKey);
    if (m != null) {
      menuBar.add(m);
    }
  }

  
  protected JMenu createMenu(String key) {

    
    String[] itemKeys = tokenize(JmolResourceHandler.getStringX(key));

    
    JMenu menu = guimap.newJMenu(key);
    ImageIcon f = JmolResourceHandler.getIconX(key + "Image");
    if (f != null) {
      menu.setHorizontalTextPosition(SwingConstants.RIGHT);
      menu.setIcon(f);
    }

    
    for (int i = 0; i < itemKeys.length; i++) {

      String item = itemKeys[i];
      if (item.equals("-")) {
        menu.addSeparator();
        continue;
      }
      if (item.endsWith("Menu")) {
        JMenu pm;
        if ("recentFilesMenu".equals(item)) {
          pm = createMenu(item);
        } else {
          pm = createMenu(item);
        }
        menu.add(pm);
        continue;
      }
      JMenuItem mi = createMenuItem(item);
      menu.add(mi);
    }
    menu.addMenuListener(display.getMenuListener());
    return menu;
  }

  private static class ActionChangedListener implements PropertyChangeListener {

    AbstractButton button;

    ActionChangedListener(AbstractButton button) {
      super();
      this.button = button;
    }

    public void propertyChange(PropertyChangeEvent e) {

      String propertyName = e.getPropertyName();
      if (e.getPropertyName().equals(Action.NAME)) {
        String text = (String) e.getNewValue();
        if (button.getText() != null) {
          button.setText(text);
        }
      } else if (propertyName.equals("enabled")) {
        Boolean enabledState = (Boolean) e.getNewValue();
        button.setEnabled(enabledState.booleanValue());
      }
    }
  }

  private Hashtable commands;
  private Hashtable menuItems;
  private JMenuBar menubar;
  private JToolBar toolbar;

  
  
  

  private static final String newwinAction = "newwin";
  private static final String openAction = "open";
  private static final String openurlAction = "openurl";
  private static final String newAction = "new";
  
  private static final String exportActionProperty = "export";
  private static final String closeAction = "close";
  private static final String exitAction = "exit";
  private static final String aboutAction = "about";
  
  private static final String whatsnewAction = "whatsnew";
  private static final String uguideAction = "uguide";
  private static final String printActionProperty = "print";
  private static final String recentFilesAction = "recentFiles";
  private static final String povrayActionProperty = "povray";
  private static final String writeActionProperty = "write";
  private static final String editorAction = "editor";
  private static final String consoleAction = "console";
  private static final String toWebActionProperty = "toweb";
  private static final String atomsetchooserAction = "atomsetchooser";
  private static final String copyImageActionProperty = "copyImage";
  private static final String copyScriptActionProperty = "copyScript";
  private static final String pasteClipboardActionProperty = "pasteClipboard";
  private static final String gaussianAction = "gauss";

  

  private ExportAction exportAction = new ExportAction();
  private PovrayAction povrayAction = new PovrayAction();
  private ToWebAction toWebAction = new ToWebAction();
  private WriteAction writeAction = new WriteAction();
  private PrintAction printAction = new PrintAction();
  private CopyImageAction copyImageAction = new CopyImageAction();
  private CopyScriptAction copyScriptAction = new CopyScriptAction();
  private PasteClipboardAction pasteClipboardAction = new PasteClipboardAction();
  private ViewMeasurementTableAction viewMeasurementTableAction = new ViewMeasurementTableAction();

  int qualityJPG = -1;
  int qualityPNG = -1;
  String imageType;

  
  private Action[] defaultActions = { new NewAction(), new NewwinAction(),
      new OpenAction(), new OpenUrlAction(), printAction, exportAction,
      new CloseAction(), new ExitAction(), copyImageAction, copyScriptAction,
      pasteClipboardAction, new AboutAction(), new WhatsNewAction(),
      new UguideAction(), new ConsoleAction(),  
      new RecentFilesAction(), povrayAction, writeAction, toWebAction, 
      new ScriptWindowAction(), new ScriptEditorAction(),
      new AtomSetChooserAction(), viewMeasurementTableAction, 
      new GaussianAction() }
  ;

  class CloseAction extends AbstractAction {
    CloseAction() {
      super(closeAction);
    }

    public void actionPerformed(ActionEvent e) {
      JmolPanel.this.frame.setVisible(false);
      JmolPanel.this.doClose();
    }
  }

  class ConsoleAction extends AbstractAction {

    public ConsoleAction() {
      super("jconsole");
    }

    public void actionPerformed(ActionEvent e) {
      if (consoleframe != null)
        consoleframe.setVisible(true);
    }

  }

  class AboutAction extends AbstractAction {

    public AboutAction() {
      super(aboutAction);
    }

    public void actionPerformed(ActionEvent e) {
      AboutDialog ad = new AboutDialog(frame);
      ad.setVisible(true);
    }

  }

  class WhatsNewAction extends AbstractAction {

    public WhatsNewAction() {
      super(whatsnewAction);
    }

    public void actionPerformed(ActionEvent e) {
      WhatsNewDialog wnd = new WhatsNewDialog(frame);
      wnd.setVisible(true);
    }
  }

  class GaussianAction extends AbstractAction {
    public GaussianAction() {
      super(gaussianAction);
    }
    
    public void actionPerformed(ActionEvent e) {
      if (gaussianDialog == null)
        gaussianDialog = new GaussianDialog(frame, viewer);
      gaussianDialog.setVisible(true);
    }
  }
    
  class NewwinAction extends AbstractAction {

    NewwinAction() {
      super(newwinAction);
    }

    public void actionPerformed(ActionEvent e) {
      JFrame newFrame = new JFrame();
      new Jmol(jmolApp, null, newFrame, (Jmol) JmolPanel.this, startupWidth, startupHeight, "", null);
      newFrame.setVisible(true);
    }

  }

  class UguideAction extends AbstractAction {

    public UguideAction() {
      super(uguideAction);
    }

    public void actionPerformed(ActionEvent e) {
      (new HelpDialog(frame)).setVisible(true);
    }
  }

  class PasteClipboardAction extends AbstractAction {

    public PasteClipboardAction() {
      super(pasteClipboardActionProperty);
    }

    public void actionPerformed(ActionEvent e) {
      String str = ImageCreator.getClipboardTextStatic();
      if (str != null && str.length() > 0)
        viewer.loadInline(str, false);
    }
  }

  
  class CopyImageAction extends AbstractAction {

    public CopyImageAction() {
      super(copyImageActionProperty);
    }

    public void actionPerformed(ActionEvent e) {
      (new ImageCreator(viewer)).clipImage(null);
    }
  }

  class CopyScriptAction extends AbstractAction {

    public CopyScriptAction() {
      super(copyScriptActionProperty);
    }

    public void actionPerformed(ActionEvent e) {
      (new ImageCreator(viewer)).clipImage((String) viewer.getProperty(
          "string", "stateInfo", null));
    }
  }

  class PrintAction extends AbstractAction {

    public PrintAction() {
      super(printActionProperty);
    }

    public void actionPerformed(ActionEvent e) {
      print();
    }

  }

  
  public void print() {

    PrinterJob job = PrinterJob.getPrinterJob();
    job.setPrintable(display);
    if (job.printDialog()) {
      try {
        job.print();
      } catch (PrinterException e) {
        Logger.error("Error while printing", e);
      }
    }
  }

  class OpenAction extends NewAction {

    OpenAction() {
      super(openAction);
    }

    public void actionPerformed(ActionEvent e) {
      String fileName = getOpenFileNameFromDialog(null);
      if (fileName == null)
        return;
      if (fileName.startsWith("load append"))
        viewer.scriptWait(fileName);
      else
        viewer.openFileAsynchronously(fileName);
    }
  }

  class OpenUrlAction extends NewAction {

    String title;
    String prompt;

    OpenUrlAction() {
      super(openurlAction);
      title = GT._("Open URL");
      prompt = GT._("Enter URL of molecular model");
    }

    public void actionPerformed(ActionEvent e) {
      String url = JOptionPane.showInputDialog(frame, prompt, title,
          JOptionPane.PLAIN_MESSAGE);
      if (url != null) {
        if (url.indexOf("://") == -1)
          url = "http://" + url;
        viewer.openFileAsynchronously(url);
      }
      return;
    }
  }

  class NewAction extends AbstractAction {

    NewAction() {
      super(newAction);
    }

    NewAction(String nm) {
      super(nm);
    }

    public void actionPerformed(ActionEvent e) {
      revalidate();
    }
  }

  
  class ExitAction extends AbstractAction {

    ExitAction() {
      super(exitAction);
    }

    public void actionPerformed(ActionEvent e) {
      JmolPanel.this.doClose();
    }
  }

  final static String[] imageChoices = { "JPEG", "PNG", "GIF", "PPM", "PDF" };
  final static String[] imageExtensions = { "jpg", "png", "gif", "ppm", "pdf" };

  class ExportAction extends AbstractAction {

    ExportAction() {
      super(exportActionProperty);
    }

    public void actionPerformed(ActionEvent e) {

      Dialog sd = new Dialog();
      String fileName = sd.getImageFileNameFromDialog(viewer, null, imageType,
          imageChoices, imageExtensions, qualityJPG, qualityPNG);
      if (fileName == null)
        return;
      qualityJPG = sd.getQuality("JPG");
      qualityPNG = sd.getQuality("PNG");
      String sType = imageType = sd.getType();
      if (sType == null) {
        
        sType = fileName;
        int i = sType.lastIndexOf(".");
        if (i < 0)
          return; 
        sType = sType.substring(i + 1).toUpperCase();
      }
      Logger.info(viewer.createImage(fileName, sType, (String) null, sd.getQuality(sType), 0, 0));
    }

  }

  class RecentFilesAction extends AbstractAction {

    public RecentFilesAction() {
      super(recentFilesAction);
    }

    public void actionPerformed(ActionEvent e) {

      recentFiles.setVisible(true);
      String selection = recentFiles.getFile();
      if (selection != null)
        viewer.openFileAsynchronously(selection);
    }
  }

  class ScriptWindowAction extends AbstractAction {

    public ScriptWindowAction() {
      super(consoleAction);
    }

    public void actionPerformed(ActionEvent e) {
      Component c = (Component) viewer.getProperty("DATA_API","getAppConsole", null);
      if (c != null)
        c.setVisible(true);
    }
  }

  class ScriptEditorAction extends AbstractAction {

    public ScriptEditorAction() {
      super(editorAction);
    }

    public void actionPerformed(ActionEvent e) {
      Component c = (Component) viewer.getProperty("DATA_API","getScriptEditor", null);
      if (c != null)
        c.setVisible(true);
    }
  }

  class AtomSetChooserAction extends AbstractAction {
    public AtomSetChooserAction() {
      super(atomsetchooserAction);
    }

    public void actionPerformed(ActionEvent e) {
      atomSetChooser.setVisible(true);
    }
  }

  class PovrayAction extends AbstractAction {

    public PovrayAction() {
      super(povrayActionProperty);
    }

    public void actionPerformed(ActionEvent e) {
      new PovrayDialog(frame, viewer);
    }

  }

  class WriteAction extends AbstractAction {

    public WriteAction() {
      super(writeActionProperty);
    }

    public void actionPerformed(ActionEvent e) {
      String fileName = (new Dialog()).getSaveFileNameFromDialog(viewer,
          null, "SPT");
      if (fileName != null)
        Logger.info(viewer.createImage(fileName, "SPT", viewer.getStateInfo(),
            Integer.MIN_VALUE, 0, 0));
    }
  }

  
  String createImageStatus(String fileName, String type, Object text_or_bytes,
                           int quality) {
    if (fileName != null && text_or_bytes != null)
      return null; 
    String msg = fileName;
    if (msg != null && !msg.startsWith("OK") && status != null) {
      status.setStatus(1, GT._("IO Exception:"));
      status.setStatus(2, msg);
    }
    return msg;
  }

  WebExport webExport;
  void createWebExport() {
    webExport = WebExport.createAndShowGUI(viewer, historyFile, WEB_MAKER_WINDOW_NAME);
  }


  class ToWebAction extends AbstractAction {

    public ToWebAction() {
      super(toWebActionProperty);
    }

    public void actionPerformed(ActionEvent e) {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          createWebExport();
        }
      });
    }
  }

  class ViewMeasurementTableAction extends AbstractAction {

    public ViewMeasurementTableAction() {
      super("viewMeasurementTable");
    }

    public void actionPerformed(ActionEvent e) {
      measurementTable.activate();
    }
  }

  
  public static File getUserDirectory() {
    String dir = System.getProperty("user.dir");
    return dir == null ? null : new File(System.getProperty("user.dir"));
  }

  String getOpenFileNameFromDialog(String fileName) {
    return (new Dialog()).getOpenFileNameFromDialog(appletContext,
        viewer, fileName, historyFile, FILE_OPEN_WINDOW_NAME, (fileName == null));
  }

  static final String chemFileProperty = "chemFile";

  void notifyFileOpen(String fullPathName, String title) {
    recentFiles.notifyFileOpen(fullPathName);
    frame.setTitle(title);
    if (atomSetChooser == null) {
      atomSetChooser = new AtomSetChooser(viewer, frame);
      pcs.addPropertyChangeListener(chemFileProperty, atomSetChooser);
    }
    pcs.firePropertyChange(chemFileProperty, null, null);
  }

  class ExecuteScriptAction extends AbstractAction {
    public ExecuteScriptAction() {
      super("executeScriptAction");
    }

    public void actionPerformed(ActionEvent e) {
      String script = e.getActionCommand();
      if (script.indexOf("#showMeasurementTable") >= 0)
        measurementTable.activate();
      
      viewer.evalStringQuiet(script);
    }
  }
}
