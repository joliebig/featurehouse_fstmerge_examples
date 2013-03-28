
package genj.report;

import genj.common.ContextListWidget;
import genj.fo.Format;
import genj.fo.FormatOptionsWidget;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.EditorHyperlinkSupport;
import genj.util.swing.ImageIcon;
import genj.util.swing.NestedBlockLayout;
import genj.view.SelectionSink;
import genj.view.ToolBar;
import genj.view.View;
import genj.view.ViewContext;

import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;

import spin.Spin;


public class ReportView extends View {

  static Logger LOG = Logger.getLogger("genj.report");

  
  private final static String
    WELCOME = "welcome",
    CONSOLE = "console",
    RESULT = "result";
  private String currentPage = WELCOME;

  
  private final static String EOL = System.getProperty("line.separator");

  
  private final static ImageIcon 
    imgStart = new ImageIcon(ReportView.class, "Start"), 
    imgStop = new ImageIcon(ReportView.class, "Stop"), 
    imgSave = new ImageIcon(ReportView.class, "Save"), 
    imgConsole = new ImageIcon(ReportView.class, "ReportShell"), 
    imgGui = new ImageIcon(ReportView.class, "ReportGui");

  
  private Gedcom gedcom;

  
  private Console output;
  private JScrollPane result;
  private ActionStart actionStart = new ActionStart();
  private ActionStop actionStop = new ActionStop();
  private ActionShow actionShow = new ActionShow();

  
  private final static Registry REGISTRY = Registry.get(ReportView.class);

  
  static final Resources RESOURCES = Resources.get(ReportView.class);

  
  private ReportPlugin plugin = null;

  
  public ReportView() {
    
    setLayout(new CardLayout());
    
    
    output = new Console();
    add(new JScrollPane(output), CONSOLE);
    
    
    result = new JScrollPane();
    add(result, RESULT);

    
    String msg = RESOURCES.getString("report.welcome");
    int i = msg.indexOf('*');
    String pre = i<0 ? "" : msg.substring(0, i);
    String post = i<0 ? "" : msg.substring(i+1);
    
    JButton b = new JButton(new ActionStart());
    b.setRequestFocusEnabled(false);
    b.setOpaque(false);
    
    JPanel welcome = new JPanel(new NestedBlockLayout("<col><row><a wx=\"1\" ax=\"1\" wy=\"1\"/><b/><c wx=\"1\"/></row></col>"));
    welcome.setBackground(output.getBackground());
    welcome.setOpaque(true);
    welcome.add(new JLabel(pre, SwingConstants.RIGHT));
    welcome.add(b);
    welcome.add(new JLabel(post));
    add(welcome, WELCOME);

    
  }

  
  public void removeNotify() {
    
    super.removeNotify();
    
    ReportLoader.getInstance().saveOptions();
  }

  void setPlugin(ReportPlugin plugin) {
    this.plugin = plugin;
  }

  
  public void startReport(final Report report, Object context) {

    if (!actionStart.isEnabled())
      return;

    if (report.getStartMethod(context) == null) {
      for (int i = 0; i < Gedcom.ENTITIES.length; i++) {
        String tag = Gedcom.ENTITIES[i];
        Entity sample = gedcom.getFirstEntity(tag);
        if (sample != null && report.accepts(sample) != null) {

          
          String txt = report.accepts(sample.getClass());
          if (txt == null)
            Gedcom.getName(tag);

          
          context = report.getEntityFromUser(txt, gedcom, tag);
          if (context == null)
            return;
          break;
        }
      }
    }

    
    if (context == null || report.accepts(context) == null) {
      DialogHelper.openDialog(report.getName(), DialogHelper.ERROR_MESSAGE, RESOURCES.getString("report.noaccept"), Action2.okOnly(), ReportView.this);
      return;
    }

    
    REGISTRY.put("lastreport", report.getClass().getName());
    
    
    report.setOwner(this);

    
    clear();
    show(CONSOLE);
    
    
    actionStart.setEnabled(false);
    actionStop.setEnabled(true);
    if (plugin != null)
      plugin.setEnabled(false);

    
    new Thread(new Runner(gedcom, context, report, (Runner.Callback) Spin.over(new RunnerCallback()))).start();

  }
  
  private void clear() {
    output.clear();
    output.setContentType("text/plain");
    result.setViewportView(null);
    actionShow.setSelected(false);
    actionShow.setEnabled(false);
    show(gedcom!=null ? WELCOME : CONSOLE);
  }

  
  private class RunnerCallback implements Runner.Callback {

    public void handleOutput(Report report, String s) {
      
      if (currentPage!=CONSOLE)
        show(CONSOLE);
      
      output.add(s);
    }

    public void handleResult(Report report, Object result) {

      LOG.fine("Result of report " + report.getName() + " = " + result);

      
      actionStart.setEnabled(gedcom != null);
      actionStop.setEnabled(false);
      if (plugin != null)
        plugin.setEnabled(true);

      
      showResult(result);

    }

  }

  
  public void startReport() {
    
    
    if (gedcom==null)
      return;

    
    ReportSelector selector = new ReportSelector();
    try {
      selector.select(ReportLoader.getInstance().getReportByName(REGISTRY.get("lastreport", (String) null)));
    } catch (Throwable t) {
    }

    if (0 != DialogHelper.openDialog(RESOURCES.getString("report.reports"), DialogHelper.QUESTION_MESSAGE, selector, Action2.okCancel(), ReportView.this))
      return;

    Report report = selector.getReport();
    if (report == null)
      return;

    REGISTRY.put("lastreport", report.getClass().getName());

    startReport(report, gedcom);

  }

  
  public void stopReport() {
    
  }

  @Override
  public void setContext(Context context, boolean isActionPerformed) {

    
    
    
    if (getClientProperty(CheckGedcom.class)==null) 
      SwingUtilities.invokeLater(new CheckGedcom(gedcom));
    
    
    gedcom = context.getGedcom();
    
    
    actionStart.setEnabled(!actionStop.isEnabled() && gedcom != null);

  }
  
  private class CheckGedcom implements Runnable {
    private Gedcom old;
    public CheckGedcom(Gedcom current) {
      old = current;
      
      putClientProperty(CheckGedcom.class, this);
    }
    public void run() {
      
      putClientProperty(CheckGedcom.class, null);
      
      if (gedcom!=old) 
        clear();
    }
  }

  
  void show(String page) {
    if (currentPage!=page) {
      ((CardLayout) getLayout()).show(this, page);
      currentPage = page;
    }
  }
  
  @SuppressWarnings("unchecked")
  
  void showResult(Object object) {

    
    if (object == null) {
      
      
      if (output.getDocument().getLength()==0)
        show(WELCOME);
      return;
    }

    
    if (object instanceof InterruptedException) {
      output.add("*** cancelled");
      return;
    }

    if (object instanceof Throwable) {
      CharArrayWriter buf = new CharArrayWriter(256);
      ((Throwable) object).printStackTrace(new PrintWriter(buf));
      output.add("*** exception caught" + '\n' + buf);
      
      LOG.log(Level.WARNING, "Exception caught ", (Throwable)object);
      return;
    }

    
    if (object instanceof File) {
      File file = (File) object;
      if (file.getName().endsWith(".htm") || file.getName().endsWith(".html")) {
        try {
          object = file.toURI().toURL();
        } catch (Throwable t) {
          
        }
      } else {
        try {
          Desktop.getDesktop().open(file);
        } catch (Throwable t) {
          Logger.getLogger("genj.report").log(Level.INFO, "can't open "+file, t);
          output.add("*** can't open file "+file);
        }
        return;
      }
    }

    
    if (object instanceof URL) {
      try {
        output.setPage((URL) object);
      } catch (IOException e) {
        output.add("*** can't open URL " + object + ": " + e.getMessage());
      }
      actionShow.setEnabled(false);
      actionShow.setSelected(false);
      show(CONSOLE);
      return;
    }

    
    if (object instanceof List<?>) {
      try {
        object = new ContextListWidget((List<Context>)object);
      } catch (Throwable t) {
      }
    }

    
    if (object instanceof JComponent) {
      JComponent c = (JComponent) object;
      c.setMinimumSize(new Dimension(0, 0));
      result.setViewportView(c);
      actionShow.setEnabled(true);
      actionShow.setSelected(true);
      show(RESULT);
      return;
    }
    
    
    if (object instanceof genj.fo.Document) {

      genj.fo.Document doc = (genj.fo.Document) object;
      String title = "Document " + doc.getTitle();

      Registry foRegistry = Registry.get(getClass());

      Action[] actions = Action2.okCancel();
      FormatOptionsWidget options = new FormatOptionsWidget(doc, foRegistry);
      options.connect(actions[0]);
      
      int rc = DialogHelper.openDialog(title, DialogHelper.QUESTION_MESSAGE, options, actions, this);
      Format formatter = options.getFormat();
      File file = options.getFile();
      if (rc!=0 || formatter.getFileExtension() == null || file == null) {
        showResult(null);
        return;
      }
      
      
      options.remember(foRegistry);

      
      try {
        file.getParentFile().mkdirs();
        formatter.format(doc, file);
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "formatting " + doc + " failed", t);
        output.add("*** formatting " + doc + " failed");
        return;
      }

      
      showResult(file);

      return;
    }

    
    output.add("*** report returned unknown result " + object);
  }

  
  public void populate(ToolBar toolbar) {

    toolbar.add(actionStart);
    
    
    
    
    toolbar.add(new JToggleButton(actionShow));
    toolbar.add(new ActionSave());

    
  }

  
  private class ActionStop extends Action2 {
    protected ActionStop() {
      setImage(imgStop);
      setTip(RESOURCES, "report.stop.tip");
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent event) {
      stopReport();
    }
  } 

  
  private class ActionStart extends Action2 {

    
    private Object context;

    
    private Report report;

    
    private PrintWriter out;

    
    protected ActionStart() {
      
      setImage(imgStart);
      setTip(RESOURCES, "report.start.tip");
    }

    
    public void actionPerformed(ActionEvent event) {
      startReport();
    }

  } 

  
  private class ActionShow extends Action2 {
    protected ActionShow() {
      setImage(imgConsole);
      setTip(RESOURCES, "report.output");
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent event) {
      setSelected(isSelected());
    }
    
    @Override
    public boolean setSelected(boolean selected) {
      setImage(selected ? imgGui : imgConsole);
      if (selected)
        show(RESULT);
      else
        show(CONSOLE);
      return super.setSelected(selected);
    }
      
  }

  
  private class ActionSave extends Action2 {
    protected ActionSave() {
      setImage(imgSave);
      setTip(RESOURCES, "report.save.tip");
    }

    public void actionPerformed(ActionEvent event) {
      
      
      if (result.isVisible() && result.getViewport().getView() instanceof ContextListWidget) {
        ContextListWidget list = (ContextListWidget)result.getViewport().getView();
        String title = REGISTRY.get("lastreport", "Report");
        genj.fo.Document doc = new genj.fo.Document(title);
        doc.startSection(title);
        for (Context c : list.getContexts()) {
          if (c instanceof ViewContext)
            doc.addText(c.getEntity()+":"+((ViewContext)c).getText());
          else
            doc.addText(c.toString());
          doc.nextParagraph();
        }
        showResult(doc);
        
        return;
      }

      
      JFileChooser chooser = new JFileChooser(".");
      chooser.setDialogTitle(getTip());
      chooser.setFileFilter(new FileFilter() {
        @Override
        public boolean accept(File f) {
          return f.isDirectory() || f.getName().endsWith(".txt");
        }

        @Override
        public String getDescription() {
          return "*.txt (Text)";
        }
      });

      if (JFileChooser.APPROVE_OPTION != chooser.showDialog(ReportView.this, "Save")) 
        return;
      File file = chooser.getSelectedFile();
      if (file == null) 
        return;
      if (!file.getName().endsWith("*.txt"))
        file = new File(file.getAbsolutePath()+".txt");

      
      if (file.exists()) {
        int rc = DialogHelper.openDialog(RESOURCES.getString("title"), DialogHelper.WARNING_MESSAGE, "File exists. Overwrite?", Action2.yesNo(), ReportView.this);
        if (rc != 0) 
          return;
      }

      
      final OutputStreamWriter out;
      try {
        out = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF8"));
      } catch (IOException ex) {
        DialogHelper.openDialog(RESOURCES.getString("title"), DialogHelper.ERROR_MESSAGE, "Error while saving to\n" + file.getAbsolutePath(), Action2.okOnly(), ReportView.this);
        return;
      }

      
      try {
        String newline = System.getProperty("line.separator");
        BufferedReader in = new BufferedReader(new StringReader(output.getText()));
        while (true) {
          String line = in.readLine();
          if (line == null)
            break;
          out.write(line);
          out.write(newline);
        }
        in.close();
        out.close();

      } catch (Exception ex) {
      }

      
    }

  } 

  
  private class Console extends JEditorPane implements MouseListener, MouseMotionListener {

    
    private String id = null;

    
    private Console() {
      setContentType("text/plain");
      setFont(new Font("Monospaced", Font.PLAIN, 12));
      setEditable(false);
      addHyperlinkListener(new EditorHyperlinkSupport(this));
      addMouseMotionListener(this);
      addMouseListener(this);
    }

    
    public void mouseMoved(MouseEvent e) {

      
      id = markIDat(e.getPoint());

      
    }

    
    public void mouseClicked(MouseEvent e) {
      if (id != null && gedcom != null) {
        Entity entity = gedcom.getEntity(id);
        if (entity != null)
          SelectionSink.Dispatcher.fireSelection(e, new Context(entity));
      }
    }

    
    private String markIDat(Point loc) {

      try {
        
        int pos = viewToModel(loc);
        if (pos < 0)
          return null;

        
        javax.swing.text.Document doc = getDocument();

        
        for (int i = 0;; i++) {
          
          if (i == 10)
            return null;
          
          if (pos == 0 || !Character.isLetterOrDigit(doc.getText(pos - 1, 1).charAt(0)))
            break;
          
          pos--;
        }

        
        int len = 0;
        while (true) {
          
          if (len == 10)
            return null;
          
          if (pos + len == doc.getLength())
            break;
          
          if (!Character.isLetterOrDigit(doc.getText(pos + len, 1).charAt(0)))
            break;
          
          len++;
        }

        
        if (len < 2)
          return null;
        String id = doc.getText(pos, len);
        if (gedcom == null || gedcom.getEntity(id) == null)
          return null;

        
        
        setCaretPosition(pos);
        moveCaretPosition(pos + len);

        
        return id;

        
      } catch (BadLocationException ble) {
      }

      
      return null;
    }

    
    public void mouseDragged(MouseEvent e) {
      
    }

    void clear() {
      setContentType("text/plain");
      setText("");
    }

    void add(String txt) {
      javax.swing.text.Document doc = getDocument();
      try {
        doc.insertString(doc.getLength(), txt, null);
      } catch (Throwable t) {
      }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

  } 

} 

