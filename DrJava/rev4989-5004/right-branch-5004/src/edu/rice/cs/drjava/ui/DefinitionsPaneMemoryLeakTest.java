

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.MultiThreadedTestCase;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.FileOps;
import static edu.rice.cs.drjava.model.GlobalModelTestCase.FileSelector;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import java.util.Date;


public final class DefinitionsPaneMemoryLeakTest extends MultiThreadedTestCase {
  File _tempDir;

  private volatile MainFrame _frame;    
  
  public void setUp() throws Exception {
    super.setUp();
    
    String user = System.getProperty("user.name");
    _tempDir =  FileOps.createTempDirectory("DrJava-test-" + user );
    
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        DrJava.getConfig().resetToDefaults();
        _frame = new MainFrame();
        _frame.pack(); 
      }
    });
  }
  
  public void tearDown() throws Exception {
    Utilities.invokeLater(new Runnable() {
      public void run() {
        _frame.dispose();
        _frame = null;
      }
    });
    Utilities.clearEventQueue();
    super.tearDown();
  }
  
  private volatile int _finalPaneCt;
  private volatile int _finalDocCt;
  private volatile DefinitionsDocument preventFinalization;

  private void appendDocAndSave(final OpenDefinitionsDocument d, String s, final String fileName) {
    d.getDocument().append(s);
    edu.rice.cs.util.swing.Utilities.invokeAndWait(new Runnable() { public void run() {
      try {
        d.saveFile(new FileSelector(new File(_tempDir, fileName)));
      }
      catch(Exception e) { throw new edu.rice.cs.util.UnexpectedException(e); }
    } });
  }
  
  public void testDocumentPaneMemoryLeak()  throws InterruptedException, IOException {
    
    DocChangeListener listener = new DocChangeListener();
    
    _finalPaneCt = 0;
    _finalDocCt = 0;
    
    FinalizationListener<DefinitionsPane> fl = new FinalizationListener<DefinitionsPane>() {
      public void finalized(FinalizationEvent<DefinitionsPane> e) {  _finalPaneCt++; }
    };
    
    FinalizationListener<DefinitionsDocument> fldoc = new FinalizationListener<DefinitionsDocument>() {
      public void finalized(FinalizationEvent<DefinitionsDocument> e) {  _finalDocCt++; }
    };
    
    final SingleDisplayModel _model = _frame.getModel();
    _model.addListener(listener);
    
    listener.reset();
    OpenDefinitionsDocument d1 = _model.newFile();





    appendDocAndSave(d1, "d1 "+System.identityHashCode(d1.getDocument()), "d1");    
    d1.addFinalizationListener(fldoc);
    listener.waitDocChanged();
    DefinitionsPane p1 = _frame.getCurrentDefPane();
    p1.addFinalizationListener(fl);

    assertEquals("Doc1 setup correctly", d1, p1.getOpenDefDocument());



    listener.reset();
    OpenDefinitionsDocument d2 = _model.newFile();





    appendDocAndSave(d2, "d2 "+System.identityHashCode(d2.getDocument()), "d2");
    d2.addFinalizationListener(fldoc);
    listener.waitDocChanged();
    DefinitionsPane p2 = _frame.getCurrentDefPane();
    p2.addFinalizationListener(fl);

    assertEquals("Doc2 setup correctly", d2, p2.getOpenDefDocument());


    
    listener.reset();
    OpenDefinitionsDocument d3 = _model.newFile();





    appendDocAndSave(d3, "d3 "+System.identityHashCode(d3.getDocument()), "d3");
    d3.addFinalizationListener(fldoc);
    listener.waitDocChanged();
    DefinitionsPane p3 = _frame.getCurrentDefPane();
    p3.addFinalizationListener(fl);

    assertEquals("Doc3 setup correctly", d3, p3.getOpenDefDocument());


       
    listener.reset();
    OpenDefinitionsDocument d4 = _model.newFile();





    appendDocAndSave(d4, "d4 "+System.identityHashCode(d4.getDocument()), "d4");
    d4.addFinalizationListener(fldoc);
    listener.waitDocChanged();
    DefinitionsPane p4 = _frame.getCurrentDefPane();
    p4.addFinalizationListener(fl);

    assertEquals("Doc4 setup correctly", d4, p4.getOpenDefDocument());


        
    listener.reset();
    OpenDefinitionsDocument d5 = _model.newFile();





    appendDocAndSave(d5, "d5 "+System.identityHashCode(d5.getDocument()), "d5");
    d5.addFinalizationListener(fldoc);
    listener.waitDocChanged();
    DefinitionsPane p5 = _frame.getCurrentDefPane();
    p5.addFinalizationListener(fl);

    assertEquals("Doc5 setup correctly", d5, p5.getOpenDefDocument());   


    
    listener.reset();
    OpenDefinitionsDocument d6 = _model.newFile();





    appendDocAndSave(d6, "d6 "+System.identityHashCode(d6.getDocument()), "d6");
    d6.addFinalizationListener(fldoc);
    listener.waitDocChanged();
    DefinitionsPane p6 = _frame.getCurrentDefPane();
    p6.addFinalizationListener(fl);

    assertEquals("Doc6 setup correctly", d6, p6.getOpenDefDocument()); 



    
    
    StringBuilder sbIdHashCodes = new StringBuilder();
    sbIdHashCodes.append("_frame = "+_frame.getClass().getName()+" idHash "+System.identityHashCode(_frame)+"\n");
    sbIdHashCodes.append("_model = "+_model.getClass().getName()+" idHash "+System.identityHashCode(_frame)+"\n");
    sbIdHashCodes.append("p1     = "+p1.getClass().getName()+" idHash "+System.identityHashCode(p1)+"\n");
    sbIdHashCodes.append("p2     = "+p2.getClass().getName()+" idHash "+System.identityHashCode(p2)+"\n");
    sbIdHashCodes.append("p3     = "+p3.getClass().getName()+" idHash "+System.identityHashCode(p3)+"\n");
    sbIdHashCodes.append("p4     = "+p4.getClass().getName()+" idHash "+System.identityHashCode(p4)+"\n");
    sbIdHashCodes.append("p5     = "+p5.getClass().getName()+" idHash "+System.identityHashCode(p5)+"\n");
    sbIdHashCodes.append("p6     = "+p6.getClass().getName()+" idHash "+System.identityHashCode(p6)+"\n");
    sbIdHashCodes.append("d1     = "+d1.getDocument().getClass().getName()+" idHash "+System.identityHashCode(d1.getDocument())+"\n");
    sbIdHashCodes.append("d2     = "+d2.getDocument().getClass().getName()+" idHash "+System.identityHashCode(d2.getDocument())+"\n");
    sbIdHashCodes.append("d3     = "+d3.getDocument().getClass().getName()+" idHash "+System.identityHashCode(d3.getDocument())+"\n");
    sbIdHashCodes.append("d4     = "+d4.getDocument().getClass().getName()+" idHash "+System.identityHashCode(d4.getDocument())+"\n");
    sbIdHashCodes.append("d5     = "+d5.getDocument().getClass().getName()+" idHash "+System.identityHashCode(d5.getDocument())+"\n");
    sbIdHashCodes.append("d6     = "+d6.getDocument().getClass().getName()+" idHash "+System.identityHashCode(d6.getDocument()));

    

    p1 = p2 = p3 = p4 = p5 = p6 = null;
    d1 = d2 = d3 = d4 = d5 = d6 = null;

    
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.closeAllFiles(); } });
    Utilities.clearEventQueue();
    
    assertEquals("All files closed", 7, listener.getClosedCt());  
    
    int ct = 0;
    do {  
      
      Utilities.clearEventQueue();
      Utilities.clearEventQueue();
      
      System.gc();
      System.runFinalization();
      System.gc();
      ct++; 
    }
    while (ct < 10 && (_finalDocCt != 6 || _finalPaneCt != 6));

    if (ct == 10) {
      
      LOG.setEnabled(true);
      LOG.log(sbIdHashCodes.toString());
      System.out.println(sbIdHashCodes.toString());
      try { LOG.log("heap dump in "+dumpHeap()); }
      catch(Exception e) {
        System.err.println("Could not dump heap.");
        e.printStackTrace(System.err);
      }
      


    }

    if (ct > 1) System.out.println("testDocumentPaneMemoryLeak required " + ct + " iterations");



    
    assertEquals("all the defdocs should have been garbage collected", 6, _finalDocCt);
    assertEquals("all the panes should have been garbage collected", 6, _finalPaneCt);


  }
  
  public static final edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("heap.log",false);
  
  
  public static File dumpHeap() throws IOException, InterruptedException {
    String javaHome = System.getenv("JAVA_HOME");
    char SEP = File.separatorChar;
    
    
    File jps = new File(javaHome+SEP+"bin"+SEP+"jps");
    
    if (!jps.exists()) jps = new File(javaHome+SEP+"bin"+SEP+"jps.exe");
    
    
    ProcessBuilder pb = new ProcessBuilder(jps.getAbsolutePath());
    LOG.log(java.util.Arrays.toString(pb.command().toArray()));
    Process jpsProc = pb.start();
    jpsProc.waitFor();
    LOG.log("jps returned "+jpsProc.exitValue());
    
    
    BufferedReader br = new BufferedReader(new InputStreamReader(jpsProc.getInputStream()));
    Integer pid = null;
    String line = null;
    while((pid==null) && (line=br.readLine())!=null) {
      LOG.log(line);
      
      if (line.indexOf("JUnitTestRunner")>=0) {
        pid = new Integer(line.substring(0,line.indexOf(' ')));
      }
    }
    if (pid==null) throw new FileNotFoundException("Could not detect PID");
    LOG.log("PID is "+pid);
    
    
    File jmap = new File(javaHome+SEP+"bin"+SEP+"jmap");
    
    if (!jmap.exists()) jmap = new File(javaHome+SEP+"bin"+SEP+"jmap.exe");
    
    
    pb = new ProcessBuilder(jmap.getAbsolutePath(),
                            "-heap:format=b",
                            pid.toString());
    LOG.log(java.util.Arrays.toString(pb.command().toArray()));
    Process jmapProc = pb.start();
    jmapProc.waitFor();
    LOG.log("jmap returned "+jmapProc.exitValue());
    
    
    br = new BufferedReader(new InputStreamReader(jmapProc.getInputStream()));
    while((line=br.readLine())!=null) {
      LOG.log(line);
    }
    
    
    File dump = new File("heap.bin");
    if (!dump.exists()) { throw new FileNotFoundException("heap.bin not found"); }
    File newDump = new File("heap-DefinitionsPaneTest-"+pid+"-"+System.currentTimeMillis()+".bin");
    dump.renameTo(newDump);
    return newDump;
  }
  
  static class DocChangeListener extends DummyGlobalModelListener {
    private Object lock = new Object();
    private boolean docChanged = false;
    private int closedCt = 0;
    
    @Override public void activeDocumentChanged(OpenDefinitionsDocument active) {
      synchronized(lock) { 
        docChanged = true;
        lock.notifyAll();
      }
    }
    public void waitDocChanged() throws InterruptedException {
      synchronized(lock) {
        while (! docChanged) lock.wait();
      }
    }
    public void fileClosed(OpenDefinitionsDocument d) { closedCt++; }
    public void reset() { 
      docChanged = false; 
      closedCt = 0;
    }
    public int getClosedCt() { return closedCt; }
  }
}

