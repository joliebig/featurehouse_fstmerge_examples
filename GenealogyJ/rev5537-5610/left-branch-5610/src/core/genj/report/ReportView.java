
package genj.report;

import genj.fo.Format;
import genj.fo.FormatOptionsWidget;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.io.FileAssociation;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.EditorHyperlinkSupport;
import genj.util.swing.ImageIcon;
import genj.view.SelectionSink;
import genj.view.ToolBar;
import genj.view.View;
import genj.window.WindowManager;

import java.awt.CardLayout;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import spin.Spin;


public class ReportView extends View {

  static Logger LOG = Logger.getLogger("genj.report");

  
  private final static String EOL = System.getProperty("line.separator");

  
  private final static ImageIcon imgStart = new ImageIcon(ReportView.class, "Start"), imgStop = new ImageIcon(ReportView.class, "Stop"), imgSave = new ImageIcon(ReportView.class, "Save"), imgConsole = new ImageIcon(ReportView.class, "ReportShell"), imgGui = new ImageIcon(ReportView.class, "ReportGui");

  
  private Gedcom gedcom;

  
  private Output output;
  private ActionStart actionStart = new ActionStart();
  private ActionStop actionStop = new ActionStop(actionStart);
  private ActionConsole actionConsole = new ActionConsole();

  
  private final static Registry REGISTRY = Registry.get(ReportView.class);

  
  static final Resources RESOURCES = Resources.get(ReportView.class);

  
  private ReportPlugin plugin = null;

  
  public ReportView() {

    
    output = new Output();

    
    setLayout(new CardLayout());
    add(new JScrollPane(output), "output");

    
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

    if (report.getStartMethod(gedcom) == null) {
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
      WindowManager.getInstance().openDialog(null, report.getName(), WindowManager.ERROR_MESSAGE, RESOURCES.getString("report.noaccept"), Action2.okOnly(), ReportView.this);
      return;
    }

    
    output.clear();
    while (getComponentCount() > 1)
      remove(1);
    showConsole(true);

    
    actionStart.setEnabled(false);
    actionStop.setEnabled(true);
    if (plugin != null)
      plugin.setEnabled(false);

    
    new Thread(new Runner(gedcom, context, report, (Runner.Callback) Spin.over(new RunnerCallback()))).start();

  }

  
  private class RunnerCallback implements Runner.Callback {

    public void handleOutput(Report report, String s) {
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

    ReportSelector selector = new ReportSelector();
    try {
      selector.select(ReportLoader.getInstance().getReportByName(REGISTRY.get("lastreport", (String) null)));
    } catch (Throwable t) {
    }

    if (0 != WindowManager.getInstance().openDialog("report", RESOURCES.getString("report.reports"), WindowManager.QUESTION_MESSAGE, selector, Action2.okCancel(), ReportView.this))
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

    
    gedcom = context.getGedcom();

    
    actionStart.setEnabled(!actionStop.isEnabled() && gedcom != null);

  }

  
  void showConsole(boolean show) {
    if (show) {
      ((CardLayout) getLayout()).first(this);
      actionConsole.setEnabled(getComponentCount() > 1);
      actionConsole.setImage(imgGui);
    } else {
      ((CardLayout) getLayout()).last(this);
      actionConsole.setEnabled(true);
      actionConsole.setImage(imgConsole);
    }
  }

  
  void showResult(Object result) {

    
    if (result == null)
      return;

    
    if (result instanceof InterruptedException) {
      output.add("*** cancelled");
      return;
    }

    if (result instanceof Throwable) {
      CharArrayWriter buf = new CharArrayWriter(256);
      ((Throwable) result).printStackTrace(new PrintWriter(buf));
      output.add("*** exception caught" + '\n' + buf);
      return;
    }

    
    if (result instanceof File) {
      File file = (File) result;
      if (file.getName().endsWith(".htm") || file.getName().endsWith(".html")) {
        try {
          result = file.toURI().toURL();
        } catch (Throwable t) {
          
        }
      } else {
        FileAssociation association = FileAssociation.get(file, (String) null, this);
        if (association != null)
          association.execute(file);
        return;
      }
    }

    
    if (result instanceof URL) {
      try {
        output.setPage((URL) result);
      } catch (IOException e) {
        output.add("*** can't open URL " + result + ": " + e.getMessage());
      }
      return;
    }

    
    if (result instanceof JComponent) {
      JComponent c = (JComponent) result;
      c.setMinimumSize(new Dimension(0, 0));
      add((JComponent) result, "result");
      showConsole(false);
      return;
    }

    
    if (result instanceof genj.fo.Document) {

      genj.fo.Document doc = (genj.fo.Document) result;
      String title = "Document " + doc.getTitle();

      Registry foRegistry = Registry.get(getClass());

      Action[] actions = Action2.okCancel();
      FormatOptionsWidget options = new FormatOptionsWidget(doc, foRegistry);
      options.connect(actions[0]);
      if (0 != WindowManager.getInstance().openDialog("reportdoc", title, WindowManager.QUESTION_MESSAGE, options, actions, this))
        return;

      
      Format formatter = options.getFormat();
      File file = null;
      String progress = null;
      if (formatter.getFileExtension() == null)
        return;

      file = options.getFile();
      if (file == null)
        return;
      file.getParentFile().mkdirs();

      
      options.remember(foRegistry);

      
      try {
        formatter.format(doc, file);
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "formatting " + doc + " failed", t);
        output.add("*** formatting " + doc + " failed");
        return;
      }

      
      showResult(file);

      return;
    }

    
    output.add("*** report returned unknown result " + result);
  }

  
  public void populate(ToolBar toolbar) {

    toolbar.add(actionStart);
    toolbar.add(actionStop);
    toolbar.add(actionConsole);
    toolbar.add(new ActionSave());

    
  }

  
  private class ActionStop extends Action2 {
    private Action2 start;

    protected ActionStop(Action2 start) {
      setImage(imgStop);
      setTip(RESOURCES, "report.stop.tip");
      setEnabled(false);
      this.start = start;
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

  
  private class ActionConsole extends Action2 {
    protected ActionConsole() {
      setImage(imgConsole);
      setTip(RESOURCES, "report.output");
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent event) {
      showConsole(!output.isVisible());
    }
  }

  
  private class ActionSave extends Action2 {
    protected ActionSave() {
      setImage(imgSave);
      setTip(RESOURCES, "report.save.tip");
    }

    public void actionPerformed(ActionEvent event) {

      
      JFileChooser chooser = new JFileChooser(".");
      chooser.setDialogTitle("Save Output");

      if (JFileChooser.APPROVE_OPTION != chooser.showDialog(ReportView.this, "Save")) {
        return;
      }
      File file = chooser.getSelectedFile();
      if (file == null) {
        return;
      }

      
      if (file.exists()) {
        int rc = WindowManager.getInstance().openDialog(null, RESOURCES.getString("title"), WindowManager.WARNING_MESSAGE, "File exists. Overwrite?", Action2.yesNo(), ReportView.this);
        if (rc != 0) {
          return;
        }
      }

      
      final OutputStreamWriter out;
      try {
        out = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF8"));
      } catch (IOException ex) {
        WindowManager.getInstance().openDialog(null, RESOURCES.getString("title"), WindowManager.ERROR_MESSAGE, "Error while saving to\n" + file.getAbsolutePath(), Action2.okOnly(), ReportView.this);
        return;
      }

      
      try {
        BufferedReader in = new BufferedReader(new StringReader(output.getText()));
        while (true) {
          String line = in.readLine();
          if (line == null)
            break;
          out.write(line);
          out.write("\n");
        }
        in.close();
        out.close();

      } catch (Exception ex) {
      }

      
    }

  } 

  
  private class Output extends JEditorPane implements MouseListener, MouseMotionListener {

    
    private String id = null;

    
    private Output() {
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

        
        Document doc = getDocument();

        
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
      Document doc = getDocument();
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

