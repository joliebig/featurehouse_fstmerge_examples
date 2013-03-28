

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

import java.lang.ref.WeakReference;
import static org.netbeans.test.MemoryTestUtils.*;


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
  
  private volatile DefinitionsDocument preventFinalization;

  private volatile int _finalPaneCt;
  private volatile int _finalDocCt;
  private volatile StringBuilder sbIdHashCodes;
  
  public static final int PANE_COUNT = 6;
  public static final boolean DUMP_STACK = false;
  
  
  private void runIsolatedDrJavaSession() throws InterruptedException, IOException {
    DocChangeListener listener = new DocChangeListener();
    
    _finalPaneCt = 0;
    _finalDocCt = 0;
    
    FinalizationListener<DefinitionsPane> fl = new FinalizationListener<DefinitionsPane>() {
      public void finalized(FinalizationEvent<DefinitionsPane> e) { _finalPaneCt++; }
    };
    
    FinalizationListener<DefinitionsDocument> fldoc = new FinalizationListener<DefinitionsDocument>() {
      public void finalized(FinalizationEvent<DefinitionsDocument> e) { _finalDocCt++; }
    };
    
    final SingleDisplayModel _model = _frame.getModel();
    _model.addListener(listener);
    
    OpenDefinitionsDocument[] d = new OpenDefinitionsDocument[PANE_COUNT];
    DefinitionsPane[] p = new DefinitionsPane[PANE_COUNT];
    
    for(int i=0; i<PANE_COUNT; ++i) {
      listener.reset();
      d[i] = _model.newFile();
      try {
        java.lang.reflect.Field fTimeStamp = d[i].getClass().getSuperclass().getDeclaredField("_timestamp");
        fTimeStamp.setAccessible(true);
        fTimeStamp.setLong(d[i],System.identityHashCode(d[i]));
      } catch(Exception e) {
        println("Couldn't set _timestamp field of Document "+i+" to identity hashcode ");
        throw new RuntimeException(e);
      }
      d[i].addFinalizationListener(fldoc);
      listener.waitDocChanged();
      p[i] = _frame.getCurrentDefPane();
      p[i].addFinalizationListener(fl);
      println("Listener attached to DefinitionsPane "+i+" 0x" + hexIdentityHashCode(p[i]));
      println("\tDocument is 0x" + hexIdentityHashCode(d[i]));
      try {
        java.lang.reflect.Field fTimeStamp = d[i].getClass().getSuperclass().getDeclaredField("_timestamp");
        fTimeStamp.setAccessible(true);
        println("\tDocument's _timestamp is set to " + Long.toHexString(fTimeStamp.getLong(d[i])));
      } catch(Exception e) {
        println("Couldn't get _timestamp field of Document "+i);
        throw new RuntimeException(e);
      }
      assertEquals("Doc "+i+" set up correctly", d[i], p[i].getOpenDefDocument());
    }

    
    
    sbIdHashCodes = new StringBuilder();
    sbIdHashCodes.append("_frame = "+_frame.getClass().getName()+"@0x"+hexIdentityHashCode(_frame)+"\n");
    sbIdHashCodes.append("_model = "+_model.getClass().getName()+"@0x"+hexIdentityHashCode(_frame)+"\n");
    for(int i=0; i<PANE_COUNT;++i) {
      sbIdHashCodes.append("p["+i+"]   = "+p[i].getClass().getName()+"@0x"+hexIdentityHashCode(p[i])+"\n");
    }
    for(int i=0; i<PANE_COUNT;++i) {
      sbIdHashCodes.append("d["+i+"]   = "+d[i].getClass().getName()+"@0x"+hexIdentityHashCode(d[i])+"\n");
    }

    WeakReference[] wd = new WeakReference[PANE_COUNT];
    WeakReference[] wp = new WeakReference[PANE_COUNT];
    for(int i=0; i<PANE_COUNT;++i) {
      wd[i] = new WeakReference<OpenDefinitionsDocument>(d[i]);
      wp[i] = new WeakReference<DefinitionsPane>(p[i]);
    }
    
    

    for(int i=0; i<PANE_COUNT; ++i) {
      p[i] = null;
      d[i] = null;
    }
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.closeAllFiles(); } });
    Utilities.clearEventQueue();
    
    assertEquals("All files closed", PANE_COUNT+1, listener.getClosedCt()); 
    
    _model.newFile();  
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.closeAllFiles(); } });
    Utilities.clearEventQueue();
    
    
    
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _model.closeAllFiles(); } });
    Utilities.clearEventQueue();
    
    for(int i=0; i<PANE_COUNT; ++i) {
      assertGC("Document "+i+" leaked", wd[i]);
      assertGC("Pane "+i+" leaked", wp[i]);
    }
  }
    
  
  public void testDocumentPaneMemoryLeak() throws InterruptedException, IOException {
    println("---- testDocumentPaneMemoryLeak ----");

    
    runIsolatedDrJavaSession();
    
    int ct = 0;
    do {  
      
      Utilities.clearEventQueue();
      Utilities.clearEventQueue();
      
      System.gc();
      System.runFinalization();
      System.gc();
      ct++; 
    }
    while (ct < 10 && (_finalDocCt < PANE_COUNT || _finalPaneCt < PANE_COUNT));

    if (DUMP_STACK && (ct == 10)) {
      
      boolean isEnabled = LOG.isEnabled();
      LOG.setEnabled(true);
      LOG.log(sbIdHashCodes.toString());
      try { LOG.log("heap dump in "+dumpHeap()); }
      catch(Exception e) {
        println("Could not dump heap.");
        e.printStackTrace(System.err);
      }
      LOG.setEnabled(isEnabled);
      
      
      
      
      
      
      
      
      
      
      
      




    }

    if (ct > 1) println("testDocumentPaneMemoryLeak required " + ct + " iterations");



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
    while((pid == null) && (line=br.readLine()) != null) {
      LOG.log(line);
      
      if (line.indexOf("JUnitTestRunner")>=0) {
        pid = new Integer(line.substring(0,line.indexOf(' ')));
      }
    }
    if (pid == null) throw new FileNotFoundException("Could not detect PID");
    LOG.log("PID is "+pid);
    
    
    File jmap = new File(javaHome+SEP+"bin"+SEP+"jmap");
    
    if (! jmap.exists()) jmap = new File(javaHome+SEP+"bin"+SEP+"jmap.exe");
    
    
    pb = new ProcessBuilder(jmap.getAbsolutePath(),
                            "-heap:format=b",
                            pid.toString());
    LOG.log(java.util.Arrays.toString(pb.command().toArray()));
    Process jmapProc = pb.start();
    jmapProc.waitFor();
    LOG.log("jmap returned "+jmapProc.exitValue());
    
    
    br = new BufferedReader(new InputStreamReader(jmapProc.getInputStream()));
    while((line=br.readLine()) != null) {
      LOG.log(line);
    }
    
    
    File dump = new File("heap.bin");
    if (!dump.exists()) { throw new FileNotFoundException("heap.bin not found"); }
    File newDump = new File("heap-DefinitionsPaneTest-" + pid + "-" + System.currentTimeMillis() + ".bin");
    dump.renameTo(newDump);
    return newDump;
  }
  
  
  public static String hexIdentityHashCode(Object o) {
    return Integer.toHexString(System.identityHashCode(o));
  }
  
  public static void println(String s) {
    if (DUMP_STACK) System.err.println(s);
    LOG.log(s);
  }
}

