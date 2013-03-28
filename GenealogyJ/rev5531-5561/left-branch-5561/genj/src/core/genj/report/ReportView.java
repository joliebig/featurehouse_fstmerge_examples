
package genj.report;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.EditorHyperlinkSupport;
import genj.util.swing.ImageIcon;
import genj.view.ToolBar;
import genj.view.View;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;



public class ReportView extends View {

   static Logger LOG = Logger.getLogger("genj.report");

  
  private final static long FLUSH_WAIT = 200;
  private final static String EOL= System.getProperty("line.separator");

  
  private final static ImageIcon
    imgStart = new ImageIcon(ReportView.class,"Start"      ),
    imgStop  = new ImageIcon(ReportView.class,"Stop"       ),
    imgSave  = new ImageIcon(ReportView.class,"Save"       );


  
  private Gedcom      gedcom;

  
  private Output      output;
  private ActionStart actionStart = new ActionStart();
  private ActionStop  actionStop = new ActionStop(actionStart);

  
  private Registry registry;

  
   static final Resources RESOURCES = Resources.get(ReportView.class);

  
  private String title;

  
  public ReportView(String theTitle, Context context, Registry theRegistry) {

    
    gedcom   = context.getGedcom();
    registry = theRegistry;
    title    = theTitle;

    
    output = new Output();

    
    setLayout(new BorderLayout());
    add(new JScrollPane(output), BorderLayout.CENTER);

    
  }

  
  public void removeNotify() {
    
    super.removeNotify();
    
    ReportLoader.getInstance().saveOptions();
  }
  
  
  public void startReport(Report report, Object context) {
    
  }
  
  public void startReport() {
    
















































































































    
  }

  
  public void stopReport() {
    
  }
  
  
  public void populate(ToolBar toolbar) {

    toolbar.add(actionStart);
    toolbar.add(actionStop);
    toolbar.add(new ActionSave());

    
  }

  
  private class ActionStop extends Action2 {
    private Action2 start;
    protected ActionStop(Action2 start) {
      setImage(imgStop);
      setTip(RESOURCES, "report.stop.tip");
      setEnabled(false);
      this.start=start;
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

  
  private class ActionSave extends Action2 {
    protected ActionSave() {
      setImage(imgSave);
      setTip(RESOURCES, "report.save.tip");
    }
    public void actionPerformed(ActionEvent event) {
      
      
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
        BufferedReader in = new BufferedReader(new StringReader(output.getText()));
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
      if (id!=null) {
        Entity entity = gedcom.getEntity(id);
        if (entity!=null)
          fireSelection(new Context(entity), e.getClickCount()>1);
      }
    }

    
    private String markIDat(Point loc) {

      try {
        
        int pos = viewToModel(loc);
        if (pos<0)
          return null;

        
        Document doc = getDocument();

        
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

        
        requestFocusInWindow();
        setCaretPosition(pos);
        moveCaretPosition(pos+len);

        
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

  
  private class OutputWriter extends Writer {

    
    private StringBuffer buffer = new StringBuffer(4*1024);

    
    private long lastFlush = -1;

    
    public void close() {
      
      buffer.setLength(0);
    }

    
    public void flush() {

      
      if (buffer.length()==0)
        return;

      
      lastFlush = System.currentTimeMillis();

      
      String txt = buffer.toString();
      buffer.setLength(0);
      
      output.add(txt);

      
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
