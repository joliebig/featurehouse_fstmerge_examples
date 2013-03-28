
package genj.report;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.UnitOfWork;
import genj.io.FileAssociation;
import genj.option.OptionsWidget;
import genj.util.GridBagHelper;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.ImageIcon;
import genj.view.ContextSelectionEvent;
import genj.view.ToolBarSupport;
import genj.view.ViewContext;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;


public class ReportView extends JPanel implements ToolBarSupport {

   static Logger LOG = Logger.getLogger("genj.report");

  
  private final static long FLUSH_WAIT = 200;
  private final static String EOL= System.getProperty("line.separator");

  
  private final static ImageIcon
    imgStart = new ImageIcon(ReportView.class,"Start"      ),
    imgStop  = new ImageIcon(ReportView.class,"Stop"       ),
    imgSave  = new ImageIcon(ReportView.class,"Save"       ),
    imgReload= new ImageIcon(ReportView.class,"Reload"     ),
    imgGroup = new ImageIcon(ReportView.class,"Group"      );


  
  private Gedcom      gedcom;

  
  private JLabel      lFile,lAuthor,lVersion;
  private JTextPane   tpInfo;
  private JEditorPane taOutput;
  private ReportList  listOfReports;
  private JTabbedPane tabbedPane;
  private ActionStart actionStart = new ActionStart();
  private ActionStop actionStop = new ActionStop(actionStart);
  private OptionsWidget owOptions;

  private HTMLEditorKit editorKit;

  
  private Registry registry;

  
   static final Resources RESOURCES = Resources.get(ReportView.class);

  
  private String title;

  
  public ReportView(String theTitle, Gedcom theGedcom, Registry theRegistry) {

    
    gedcom   = theGedcom;
    registry = theRegistry;
    title    = theTitle;

    
    setLayout(new BorderLayout());

    
    tabbedPane = new JTabbedPane();
    add(tabbedPane,"Center");

    
    Callback callback = new Callback();
    tabbedPane.add(RESOURCES.getString("report.reports"),createReportList(callback));
    tabbedPane.add(RESOURCES.getString("report.options"), createReportOptions());
    tabbedPane.add(RESOURCES.getString("report.output"),createReportOutput(callback));

    
  }

  
  public void removeNotify() {
    
    super.removeNotify();
    
    ReportLoader.getInstance().saveOptions();
  }

  
  private JPanel createReportList(Callback callback) {

    
    JPanel reportPanel = new JPanel();
    reportPanel.setBorder(new EmptyBorder(3,3,3,3));
    GridBagHelper gh = new GridBagHelper(reportPanel);

    
    listOfReports = new ReportList(ReportLoader.getInstance().getReports(),
            registry.get("group", ReportList.VIEW_TREE), registry);
    listOfReports.setSelectionListener(callback);

    JScrollPane spList = new JScrollPane(listOfReports) {
      
      public Dimension getMinimumSize() {
        return super.getPreferredSize();
      }
    };
    spList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    gh.add(spList,1,0,1,5,GridBagHelper.GROWFILL_VERTICAL);

    
    gh.setParameter(GridBagHelper.FILL_HORIZONTAL);
    gh.setInsets(new Insets(0, 0, 0, 5));

    lFile = new JLabel("");
    lFile.setForeground(Color.black);

    gh.add(new JLabel(RESOURCES.getString("report.file")),2,0);
    gh.add(lFile,3,0,1,1,GridBagHelper.GROWFILL_HORIZONTAL);

    

    lAuthor = new JLabel("");
    lAuthor.setForeground(Color.black);

    gh.add(new JLabel(RESOURCES.getString("report.author")),2,1);
    gh.add(lAuthor,3,1,1,1,GridBagHelper.GROWFILL_HORIZONTAL);

    
    lVersion = new JLabel();
    lVersion.setForeground(Color.black);

    gh.add(new JLabel(RESOURCES.getString("report.version")),2,2);
    gh.add(lVersion,3,2);

    editorKit = new HTMLEditorKit(this.getClass());
    
    tpInfo = new JTextPane();
    tpInfo.setEditable(false);
    tpInfo.setEditorKit(editorKit);
    tpInfo.setFont(new JTextField().getFont()); 
    tpInfo.addHyperlinkListener(new FollowHyperlink(tpInfo));
    JScrollPane spInfo = new JScrollPane(tpInfo);
    gh.add(new JLabel(RESOURCES.getString("report.info")),2,3);
    gh.add(spInfo,2,4,2,1,GridBagHelper.FILL_BOTH);

    
    return reportPanel;

  }

  
  private JComponent createReportOutput(Callback callback) {

    
    taOutput = new JEditorPane();
    taOutput.setContentType("text/plain");
    taOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
    taOutput.setEditable(false);
    taOutput.addHyperlinkListener(new FollowHyperlink(taOutput));
    taOutput.addMouseMotionListener(callback);
    taOutput.addMouseListener(callback);

    
    return new JScrollPane(taOutput);
  }

  
  private JComponent createReportOptions() {
    owOptions = new OptionsWidget(getName());
    return owOptions;
  }

  
  public Dimension getPreferredSize() {
    return new Dimension(480,320);
  }

  
   void run(Report report, Object context) {
    
    if (!actionStart.isEnabled()) 
      return;
    
    


    
    listOfReports.setSelection(report);
    
    actionStart.setContext(context);
    actionStart.trigger();
  }

  
  private boolean setRunning(boolean on) {

    
    actionStart.setEnabled(!on);
    actionStop .setEnabled(on);

    taOutput.setCursor(Cursor.getPredefinedCursor(
      on?Cursor.WAIT_CURSOR:Cursor.DEFAULT_CURSOR
    ));

    
    return true;
  }

  
  public void populate(JToolBar bar) {

    
    ButtonHelper bh = new ButtonHelper().setContainer(bar).setInsets(0);

    bh.create(actionStart);
    bh.create(actionStop);
    bh.create(new ActionSave());
    bh.create(new ActionReload());
    bh.create(new ActionGroup());

    
  }

  
  private class ActionReload extends Action2 {
    protected ActionReload() {
      setImage(imgReload);
      setTip(RESOURCES, "report.reload.tip");
      setEnabled(!ReportLoader.getInstance().isReportsInClasspath());
    }
    protected void execute() {
      
      tabbedPane.getModel().setSelectedIndex(0);
      listOfReports.setSelection(null);
      
      ReportLoader.clear();
      
      Report reports[] = ReportLoader.getInstance().getReports();
      
      listOfReports.setReports(reports);
      
    }
  } 

  
  private class ActionStop extends Action2 {
    private Action2 start;
    protected ActionStop(Action2 start) {
      setImage(imgStop);
      setTip(RESOURCES, "report.stop.tip");
      setEnabled(false);
      this.start=start;
    }
    protected void execute() {
      start.cancel(false);
    }
  } 

  
  private class ActionStart extends Action2 {

    
    private Object context;

    
    private Report instance;

    
    private PrintWriter out;

    
    protected ActionStart() {
      
      setAsync(ASYNC_SAME_INSTANCE);
      
      setImage(imgStart);
      setTip(RESOURCES, "report.start.tip");
    }
    
    protected void setContext(Object context) {
      this.context = context;
    }

    
    protected boolean preExecute() {

      
      owOptions.stopEditing();

      
      setRunning(true);

      
      Report report = listOfReports.getSelection();
      if (report==null)
        return false;

      out = new PrintWriter(new OutputWriter());

      
      instance = report.getInstance(ReportView.this, out);

      
      Object useContext = context;
      context = null;
      
      if (useContext==null) {
        if (instance.getStartMethod(gedcom)!=null)
          useContext = gedcom;
        else  for (int i=0;i<Gedcom.ENTITIES.length;i++) {
          String tag = Gedcom.ENTITIES[i];
          Entity sample = gedcom.getFirstEntity(tag);
          if (instance.accepts(sample)!=null) {
            
            
            String txt = instance.accepts(sample.getClass());
            if (txt==null) Gedcom.getName(tag);
            
            
            useContext = instance.getEntityFromUser(txt, gedcom, tag);
            if (useContext==null) 
              return false;
            break;
          }
        }
      }

      
      if (useContext==null||report.accepts(useContext)==null) {
        WindowManager.getInstance(getTarget()).openDialog(null,report.getName(),WindowManager.ERROR_MESSAGE,RESOURCES.getString("report.noaccept"),Action2.okOnly(),ReportView.this);
        return false;
      }
      context = useContext;

      
      taOutput.setContentType("text/plain");
      taOutput.setText("");

      
      return true;
    }
    
    protected void execute() {

      try{
        
        if (instance.isReadOnly())
          instance.start(context);
        else
          gedcom.doUnitOfWork(new UnitOfWork() {
            public void perform(Gedcom gedcom) {
              try {
                instance.start(context);
              } catch (Throwable t) {
                throw new RuntimeException(t);
              }
            }
          });
      
      } catch (Throwable t) {
        Throwable cause = t.getCause();
        if (cause instanceof InterruptedException)
          instance.println("***cancelled");
        else
          instance.println(cause!=null?cause:t);
      }
    }

    
    protected void postExecute(boolean preExecuteResult) {
      
      context = null;

      
      setRunning(false);

      
      if (out!=null) {
        out.flush();
        out.close();
      }

      
      if (!preExecuteResult)
        return;

      
      URL url = null;
      try {
        AbstractDocument doc = (AbstractDocument)taOutput.getDocument();
        Element p = doc.getParagraphElement(doc.getLength()-1);
        String line = doc.getText(p.getStartOffset(), p.getEndOffset()-p.getStartOffset());
        url = new URL(line);
      } catch (Throwable t) {
      }

      if (url!=null) {
        try {
          taOutput.setPage(url);
        } catch (IOException e) {
          LOG.log(Level.WARNING, "couldn't show html in report output", e);
        }
      }

      
    }
  } 

  
  private class ActionSave extends Action2 {
    protected ActionSave() {
      setImage(imgSave);
      setTip(RESOURCES, "report.save.tip");
    }
    protected void execute() {
      
      
      JFileChooser chooser = new JFileChooser(".");
      chooser.setDialogTitle("Save Output");

      if (JFileChooser.APPROVE_OPTION != chooser.showDialog(ReportView.this,"Save")) {
        return;
      }
      File file = chooser.getSelectedFile();
      if (file==null) {
        return;
      }

      
      if (file.exists()) {
        int rc = WindowManager.getInstance(getTarget()).openDialog(null, title, WindowManager.WARNING_MESSAGE, "File exists. Overwrite?", Action2.yesNo(), ReportView.this);
        if (rc!=0) {
          return;
        }
      }

      
      final OutputStreamWriter out;
      try {
        out = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF8"));
      } catch (IOException ex) {
        WindowManager.getInstance(getTarget()).openDialog(null,title,WindowManager.ERROR_MESSAGE,"Error while saving to\n"+file.getAbsolutePath(),Action2.okOnly(),ReportView.this);
        return;
      }

      
      try {
        Document doc = taOutput.getDocument();
        BufferedReader in = new BufferedReader(new StringReader(doc.getText(0, doc.getLength())));
        while (true) {
          String line = in.readLine();
          if (line==null) break;
          out.write(line);
          out.write("\n");
        }
        in.close();
        out.close();

      } catch (Exception ex) {
      }

      
    }

  } 

  
  private class ActionGroup extends Action2 {
    
    protected ActionGroup() {
      setImage(imgGroup);
      setTip(RESOURCES, "report.group.tip");
    }

    
    protected void execute() {
        int viewType = listOfReports.getViewType();
        if (viewType == ReportList.VIEW_LIST)
            listOfReports.setViewType(ReportList.VIEW_TREE);
        else
            listOfReports.setViewType(ReportList.VIEW_LIST);
        registry.put("group", listOfReports.getViewType());
    }
  } 

  
  private class FollowHyperlink implements HyperlinkListener {

    private JEditorPane editor;

    
    private FollowHyperlink(JEditorPane editor) {
      this.editor = editor;
    }

    
    public void hyperlinkUpdate(HyperlinkEvent e) {
      
      if (e.getEventType()!=HyperlinkEvent.EventType.ACTIVATED)
        return;
      
      try {
        if (e.getDescription().startsWith("#")) 
            editor.scrollToReference(e.getDescription().substring(1));
        else {
          
          Report report = listOfReports.getSelection();
          URL url = report!=null ? new URL(report.getFile().toURI().toURL(), e.getDescription()) : new URL(e.getDescription());
          FileAssociation.open(url, editor);
        }          
          
      } catch (Throwable t) {
        LOG.log(Level.FINE, "Can't handle URL for "+e.getDescription());
      }
      
    }

  } 

  
  private class Callback extends MouseAdapter implements MouseMotionListener,
      ReportSelectionListener {

    
    private String id = null;

    
    public void valueChanged(Report report) {
      
      if (report == null) {
        lFile    .setText("");
        lAuthor  .setText("");
        lVersion .setText("");
        tpInfo   .setText("");
        owOptions.setOptions(Collections.EMPTY_LIST);
      } else {
        editorKit.setFrom(report.getClass());
        lFile    .setText(report.getFile().getName());
        lAuthor  .setText(report.getAuthor());
        lVersion .setText(getReportVersion(report));
        tpInfo   .setText(report.getInfo().replaceAll("\n", "<br>"));
        tpInfo   .setCaretPosition(0);
        owOptions.setOptions(report.getOptions());
      }
    }

    
    private String getReportVersion(Report report) {
      String version = report.getVersion();
      String update = report.getLastUpdate();
      if (update != null)
        version += " - " + RESOURCES.getString("report.updated") + ": " + update;
      return version;
    }

    
    public void mouseMoved(MouseEvent e) {

      
      id = markIDat(e.getPoint());

      
    }

    
    public void mouseClicked(MouseEvent e) {
      if (id!=null) {
        Entity entity = gedcom.getEntity(id);
        if (entity!=null)
          WindowManager.broadcast(new ContextSelectionEvent(new ViewContext(entity), ReportView.this, e.getClickCount()>1));
      }
    }

    
    private String markIDat(Point loc) {

      try {
        
        int pos = taOutput.viewToModel(loc);
        if (pos<0)
          return null;

        
        Document doc = taOutput.getDocument();

        
        for (int i=0;;i++) {
          
          if (i==10)
            return null;
          
          if (pos==0 || !Character.isLetterOrDigit(doc.getText(pos-1, 1).charAt(0)) )
            break;
          
          pos--;
        }

        
        int len = 0;
        while (true) {
          
          if (len==10)
            return null;
          
          if (pos+len==doc.getLength())
            break;
          
          if (!Character.isLetterOrDigit(doc.getText(pos+len, 1).charAt(0)))
            break;
          
          len++;
        }

        
        if (len<2)
          return null;
        String id = doc.getText(pos, len);
        if (gedcom.getEntity(id)==null)
          return null;

        
        taOutput.requestFocusInWindow();
        taOutput.setCaretPosition(pos);
        taOutput.moveCaretPosition(pos+len);

        
        return id;

        
      } catch (BadLocationException ble) {
      }

      
      return null;
    }

    
    public void mouseDragged(MouseEvent e) {
      
    }

  } 

  
  private class OutputWriter extends Writer {

    
    private StringBuffer buffer = new StringBuffer(4*1024);

    
    private long lastFlush = -1;

    
    public void close() {
      
      buffer.setLength(0);
    }

    
    public void flush() {

      
      if (buffer.length()==0)
        return;

      
      tabbedPane.getModel().setSelectedIndex(2);

      
      lastFlush = System.currentTimeMillis();

      
      String txt = buffer.toString();
      buffer.setLength(0);
      Document doc = taOutput.getDocument();
      try {
        doc.insertString(doc.getLength(), txt, null);
      } catch (Throwable t) {
      }

      
    }

    
    public void write(char[] cbuf, int off, int len) throws IOException {
      
      for (int i=0;i<len;i++) {
        char c = cbuf[off+i];
        if (c!='\r') buffer.append(c);
      }
      
      if (System.currentTimeMillis()-lastFlush > FLUSH_WAIT)
        flush();
      
    }

  } 

} 
