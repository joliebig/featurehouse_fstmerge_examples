

package edu.rice.cs.drjava.ui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.Toolkit;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.util.List;

import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.model.repl.InteractionsDocumentTest.TestBeep;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.*;
import edu.rice.cs.util.text.*;
import edu.rice.cs.util.swing.Utilities;


public final class MainFrameTest extends MultiThreadedTestCase {
  
  private volatile MainFrame _frame;
  
  
  private volatile File _tempDir;
  
  
  protected volatile boolean _openDone;
  protected final Object _openLock = new Object();
  
  
  protected volatile boolean _closeDone;
  protected final Object _closeLock = new Object();
  
  
  protected volatile boolean _compileDone;
  protected final Object _compileLock = new Object();
  
  private final static Log _log = new Log("MainFrameTest.txt", false);
  
  
  public void setUp() throws Exception {
    super.setUp();
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {

        
        _frame  = new MainFrame();

        EventQueue.invokeLater(new Runnable() { public void run() { _frame.pack(); } });

      }
    });
  }
  
  public void tearDown() throws Exception {


        _frame.dispose();
        _frame = null;
         MainFrameTest.super.tearDown(); 



    super.tearDown();
  }
  
  JButton _but;
  
  public void testCreateManualToolbarButton() {
    final Action a = new AbstractAction("Test Action") { public void actionPerformed(ActionEvent ae) { } };
    
    a.putValue(Action.SHORT_DESCRIPTION, "test tooltip");
    Utilities.invokeAndWait(new Runnable() { public void run() { _but = _frame._createManualToolbarButton(a); } });
    
    assertTrue("Returned JButton is enabled.", ! _but.isEnabled());
    assertEquals("Tooltip text not set.", "test tooltip", _but.getToolTipText());
    _log.log("testCreateManualToobarButton completed");
  }
  
  
  public void testDocLocationAfterSwitch() throws BadLocationException {
    final DefinitionsPane pane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = pane.getOpenDefDocument();
    setDocText(doc.getDocument(), "abcd");
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        doc.setCurrentLocation(3); 
        pane.setCaretPosition(3);  
      } 
    }); 
    
    assertEquals("Location of old doc before switch", 3, doc.getCurrentLocation());
    assertEquals("Location of cursor in old document", 3, pane.getCaretPosition());
    
    
    SingleDisplayModel model = _frame.getModel();
    final OpenDefinitionsDocument oldDoc = doc;
    final DefinitionsPane oldPane = pane;
    final OpenDefinitionsDocument newDoc = model.newFile();
    
    
    DefinitionsPane curPane;
    OpenDefinitionsDocument curDoc;
    curPane = _frame.getCurrentDefPane();
    curDoc = curPane.getOpenDefDocument();
    assertEquals("New curr DefPane's document", newDoc, curDoc);
    assertEquals("Location in new document", 0, newDoc.getCurrentLocation());
    
    
    model.setActiveNextDocument(); 
    Utilities.clearEventQueue();
    assertEquals("Next active doc", oldDoc, model.getActiveDocument());

   
    
    curPane = _frame.getCurrentDefPane();
    curDoc = curPane.getOpenDefDocument();
    assertEquals("Next active pane", oldPane, curPane);
    assertEquals("Current document is old document", oldDoc, curDoc);
    assertEquals("Location of caret in old document", 3, curPane.getCaretPosition());
    _log.log("testDocLocationAfterSwitch completed");
  }
  
  
  private String _data;
  
  
  public void testClearLine() throws BadLocationException, UnsupportedFlavorException, IOException {
    
    final DefinitionsPane pane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = pane.getOpenDefDocument();
    final String clipString = "***Clipboard***";

    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        
        try { 
          doc.insertString(0, "abcdefg", null);
          pane.setCaretPosition(5);
          




          
          _frame.validate();






          
          
          Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
          
          
          clip.setContents(new StringSelection(clipString), _frame);
          Transferable contents = clip.getContents(_frame);
          
          
          pane.setCaretPosition(2);
          _frame.validate();

          
          _frame._clearLineAction.actionPerformed(new ActionEvent(pane, 0, "Clear Line"));

          _frame.validate();
          
          
          contents = clip.getContents(null);
          _data = (String) contents.getTransferData(DataFlavor.stringFlavor);
        }
        catch(Throwable t) { listenerFail(t.getMessage()); }
      }
    });
    Utilities.clearEventQueue();
    
    assertEquals("Clipboard contents should be unchanged after Clear Line.", clipString, _data);
    
    
    assertEquals("Current line of text should be truncated by Clear Line.", "ab", doc.getText());
    _log.log("testClearLine completed");
  }
  
  
  public void testCutLine() throws BadLocationException {
    
    
    final DefinitionsPane pane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = pane.getOpenDefDocument();

    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        
        try { 
          doc.insertString(0, "abcdefg", null);
          pane.setCaretPosition(5);
          _frame.validate();




          pane.setSelectionStart(2);
          pane.setSelectionEnd(7);
          _frame.validate();
          pane.cut();

          
          
          
          

          _frame.validate();

          


          
          
          Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
          Transferable contents = clip.getContents(null);
          _data = (String) contents.getTransferData(DataFlavor.stringFlavor);
        }
        catch(Throwable t) { listenerFail(t.getMessage()); }
      }
    });
    Utilities.clearEventQueue();
    assertEquals("Clipboard contents should be changed after Cut Line.", "cdefg", _data);
    
    
    assertEquals("Current line of text should be truncated by Cut Line.", "ab", doc.getText());
    _log.log("testCutLine completed");
  }
  
  
  
  public void testCorrectInteractionsDocument() throws EditDocumentException {
    InteractionsPane pane = _frame.getInteractionsPane();
    final SingleDisplayModel model = _frame.getModel();
    InteractionsDJDocument doc = model.getSwingInteractionsDocument();
    
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        model.getInteractionsModel().getDocument().setBeep(new TestBeep()); 
      }
    });
    Utilities.clearEventQueue();
    
    
    assertTrue("UI's int. doc. should equals Model's int. doc.", pane.getDocument() == doc);
    
    int origLength = doc.getLength();
    doc.insertText(1, "typed text", InteractionsDocument.DEFAULT_STYLE);
    Utilities.clearEventQueue();
    assertEquals("Document should not have changed.", origLength, doc.getLength());
    _log.log("testCorrectInteractionsDocument completed");
  }
  
  
  public void testMultilineIndentAfterScroll() throws BadLocationException, InterruptedException {
    final String text =
      "public class stuff {\n" +
      "private int _int;\n" +
      "private Bar _bar;\n" +
      "public void foo() {\n" +
      "_bar.baz(_int);\n" +
      "}\n" +
      "}\n";
    
    final String indented =
      "public class stuff {\n" +
      "  private int _int;\n" +
      "  private Bar _bar;\n" +
      "  public void foo() {\n" +
      "    _bar.baz(_int);\n" +
      "  }\n" +
      "}\n";
    
    final int newPos = 20;
    
    final DefinitionsPane pane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = pane.getOpenDefDocument();
    
    setConfigSetting(OptionConstants.INDENT_LEVEL, Integer.valueOf(2));

    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        doc.append(text, null);
        pane.setCaretPosition(0);
        pane.endCompoundEdit(); 
      } 
    });
    
    Utilities.clearEventQueue();
    assertEquals("Should have inserted correctly.", text, doc.getText());
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { doc.indentLines(0, doc.getLength()); }
    });
    
    assertEquals("Should have indented.", indented, doc.getText());
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        doc.getUndoManager().undo();

      }
    }); 

    assertEquals("Should have undone.", text, doc.getText());
    


    

    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        pane.setCaretPosition(newPos);
        doc.getUndoManager().redo();
      }
    });
    Utilities.clearEventQueue();
    
    assertEquals("redo",indented, doc.getText());

    _log.log("testMultilineIndentAfterScroll completed");
  }
  
  JScrollPane _pane1, _pane2;
  DefinitionsPane _defPane1, _defPane2;
  
  
  public void testGlassPaneEditableState() {
    SingleDisplayModel model = _frame.getModel();
    
    final OpenDefinitionsDocument doc1 = model.newFile();
    final OpenDefinitionsDocument doc2 = model.newFile();
    
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        
        _pane1 = _frame._createDefScrollPane(doc1);
        _pane2 = _frame._createDefScrollPane(doc2);
        
        _defPane1 = (DefinitionsPane) _pane1.getViewport().getView();
        _defPane2 = (DefinitionsPane) _pane2.getViewport().getView();
        
        _frame._switchDefScrollPane();
      }
    });
    
    Utilities.clearEventQueue(); 
    
    assertTrue("Start: defPane1", _defPane1.isEditable());
    assertTrue("Start: defPane2", _defPane2.isEditable());
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _frame.hourglassOn(); } });
    Utilities.clearEventQueue();
    
    assertTrue("Glass on: defPane1", _defPane1.isEditable());
    assertTrue("Glass on: defPane2",(! _defPane2.isEditable()));
    model.setActiveDocument(doc1);
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _frame._switchDefScrollPane(); } });
    Utilities.clearEventQueue();
    
    assertTrue("Doc Switch: defPane1",(! _defPane1.isEditable()));
    assertTrue("Doc Switch: defPane2", _defPane2.isEditable());
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _frame.hourglassOff(); } });
    Utilities.clearEventQueue();
    
    assertTrue("End: defPane1", _defPane1.isEditable());
    assertTrue("End: defPane2", _defPane2.isEditable());
    _log.log("testGlassPaneEditableState completed");
  }
  
  private KeyEvent makeFindKeyEvent(Component c, long when) {
    return new KeyEvent(c, KeyEvent.KEY_PRESSED, when, KeyEvent.CTRL_MASK, KeyEvent.VK_F, 'F');
  }
  
  
  public void testGlassPaneHidesKeyEvents() {
    SingleDisplayModel model = _frame.getModel();
    
    final OpenDefinitionsDocument doc1 = model.newFile();
    final OpenDefinitionsDocument doc2 = model.newFile();
    
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        _pane1 = _frame._createDefScrollPane(doc1);
        _pane2 = _frame._createDefScrollPane(doc2);
        _defPane1 = (DefinitionsPane) _pane1.getViewport().getView();
        _defPane2 = (DefinitionsPane) _pane2.getViewport().getView();
        _frame.validate();
        _frame.hourglassOn();
        _defPane1.processKeyEvent(makeFindKeyEvent(_defPane1, 70));
        _frame.validate();
      }
    });
    Utilities.clearEventQueue();
    
    assertTrue("the find replace dialog should not come up", ! _frame.getFindReplaceDialog().isDisplayed());
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        _frame.getInteractionsPane().processKeyEvent(makeFindKeyEvent(_frame.getInteractionsPane(), 0));
        _frame.validate();
      }
    });
    Utilities.clearEventQueue();
    
    assertTrue("the find replace dialog should not come up", ! _frame.getFindReplaceDialog().isDisplayed());
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _frame.hourglassOff(); } });
    _log.log("testGlassPaneHidesKeyEvents completed");
  }
  
  
  
  public void testSaveButtonEnabled() throws IOException {
    String user = System.getProperty("user.name");
    _tempDir = IOUtil.createAndMarkTempDirectory("DrJava-test-" + user, "");
    File forceOpenClass1_file = new File(_tempDir, "ForceOpenClass1.java");
    String forceOpenClass1_string =
      "public class ForceOpenClass1 {\n" +
      "  ForceOpenClass2 class2;\n" +
      "  ForceOpenClass3 class3;\n\n" +
      "  public ForceOpenClass1() {\n" +
      "    class2 = new ForceOpenClass2();\n" +
      "    class3 = new ForceOpenClass3();\n" +
      "  }\n" +
      "}";
    
    IOUtil.writeStringToFile(forceOpenClass1_file, forceOpenClass1_string);
    forceOpenClass1_file.deleteOnExit();
    
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        _frame.pack();
        _frame.open(new FileOpenSelector() {
          public File[] getFiles() {
            File[] return_me = new File[1];
            return_me[0] = new File(_tempDir, "ForceOpenClass1.java");
            return return_me;
          }
        });
      }
    }); 
    Utilities.clearEventQueue();
    
    assertTrue("the save button should not be enabled after opening a document", !_frame.isSaveEnabled());
    _log.log("testSaveButtonEnabled completed");
  }
  
  
  public void testDancingUIFileOpened() throws IOException {
    
    
    
    _log.log("Starting testingDancingUIFileOpened");
    
    final GlobalModel _model = _frame.getModel();
    
    String user = System.getProperty("user.name");
    _tempDir = IOUtil.createAndMarkTempDirectory("DrJava-test-" + user, "");
    
    File forceOpenClass1_file = new File(_tempDir, "ForceOpenClass1.java");
    String forceOpenClass1_string =
      "public class ForceOpenClass1 {\n" +
      "  ForceOpenClass2 class2;\n" +
      "  ForceOpenClass3 class3;\n\n" +
      "  public ForceOpenClass1() {\n" +
      "    class2 = new ForceOpenClass2();\n" +
      "    class3 = new ForceOpenClass3();\n" +
      "  }\n" +
      "}";
    
    File forceOpenClass2_file = new File(_tempDir, "ForceOpenClass2.java");
    String forceOpenClass2_string =
      "public class ForceOpenClass2 {\n" +
      "  inx x = 4;\n" +
      "}";
    
    File forceOpenClass3_file = new File(_tempDir, "ForceOpenClass3.java");
    String forceOpenClass3_string =
      "public class ForceOpenClass3 {\n" +
      "  String s = \"asf\";\n" +
      "}";
    
    IOUtil.writeStringToFile(forceOpenClass1_file, forceOpenClass1_string);
    IOUtil.writeStringToFile(forceOpenClass2_file, forceOpenClass2_string);
    IOUtil.writeStringToFile(forceOpenClass3_file, forceOpenClass3_string);
    forceOpenClass1_file.deleteOnExit();
    forceOpenClass2_file.deleteOnExit();
    forceOpenClass3_file.deleteOnExit();
    
    _log.log("DancingUIFileOpened Set Up");
    
    
    
    
    
    final ComponentAdapter listener = new ComponentAdapter() {
      public void componentResized(ComponentEvent event) {
        _testFailed = true;
        fail("testDancingUI: Open Documents List danced!");
      }
    };
    final SingleDisplayModelFileOpenedListener openListener = new SingleDisplayModelFileOpenedListener();
    final SingleDisplayModelCompileListener compileListener = new SingleDisplayModelCompileListener();
    
    _openDone = false;
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {

        _frame.pack();
        _frame.addComponentListenerToOpenDocumentsList(listener);
      }
    });
    Utilities.clearEventQueue();
    
    _model.addListener(openListener);
    
    _log.log("opening file");
    
    Utilities.invokeLater(new Runnable() {
      public void run() {
        _frame.open(new FileOpenSelector() {
          public File[] getFiles() {
            File[] return_me = new File[1];
            return_me[0] = new File(_tempDir, "ForceOpenClass1.java");
            return return_me;
          }
        });
      }
    });
    Utilities.clearEventQueue();
    
    
    synchronized(_openLock) {
      try { while (! _openDone) _openLock.wait(); }
      catch(InterruptedException e) { fail(e.toString()); }
    }
    
    _model.removeListener(openListener);
    
    _log.log("File opened");
    
    _compileDone = false;
    _model.addListener(compileListener);
    
    
    
    Utilities.invokeLater(new Runnable() { 
      public void run() { 
        _log.log("saving all files");
        _frame._saveAll();
        _log.log("invoking compileAll action");
        _frame.getCompileAllButton().doClick();
      }
    });
    Utilities.clearEventQueue();
    
    synchronized(_compileLock) {
      try { while (! _compileDone) _compileLock.wait(); }
      catch(InterruptedException e) { fail(e.toString()); }
    }
    _log.log("File saved and compiled");
    
    if (! IOUtil.deleteRecursively(_tempDir))
      System.out.println("Couldn't fully delete directory " + _tempDir.getAbsolutePath() + "\nDo it by hand.\n");
    
    _log.log("testDancingUIFileOpened completed");
  }
  
  
  public void testDancingUIFileClosed() throws IOException {
    
    String user = System.getProperty("user.name");
    _tempDir = IOUtil.createAndMarkTempDirectory("DrJava-test-" + user, "");
    File forceOpenClass1_file = new File(_tempDir, "ForceOpenClass1.java");
    String forceOpenClass1_string =
      "public class ForceOpenClass1 {\n" +
      "  ForceOpenClass2 class2;\n" +
      "  ForceOpenClass3 class3;\n\n" +
      "  public ForceOpenClass1() {\n" +
      "    class2 = new ForceOpenClass2();\n" +
      "    class3 = new ForceOpenClass3();\n" +
      "  }\n" +
      "}";
    
    IOUtil.writeStringToFile(forceOpenClass1_file, forceOpenClass1_string);
    forceOpenClass1_file.deleteOnExit();
    
    final ComponentAdapter listener = new ComponentAdapter() {
      public void componentResized(ComponentEvent event) {
        _testFailed = true;
        fail("testDancingUI: Open Documents List danced!");
      }
    };
    final SingleDisplayModelFileClosedListener closeListener = new SingleDisplayModelFileClosedListener();
    
    _closeDone = false;
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {

        _frame.pack();
        _frame.addComponentListenerToOpenDocumentsList(listener);
        _frame.open(new FileOpenSelector() {
          public File[] getFiles() {
            File[] return_me = new File[1];
            return_me[0] = new File(_tempDir, "ForceOpenClass1.java");
            return return_me;
          }
        });
        _frame.getModel().addListener(closeListener);
      }
    });
    Utilities.clearEventQueue();
    
    
    Utilities.invokeLater(new Runnable() { 
      public void run() { _frame.getCloseButton().doClick(); }
    });
    
    _log.log("Waiting for file closing");
    
    synchronized(_closeLock) {
      try { while (! _closeDone) _closeLock.wait(); }
      catch(InterruptedException e) { fail(e.toString()); }
    }
    
    if (! IOUtil.deleteRecursively(_tempDir)) {
      System.out.println("Couldn't fully delete directory " + _tempDir.getAbsolutePath() + "\nDo it by hand.\n");
    }
    _log.log("testDancingUIFileClosed completed");
  }
  
  
  class SingleDisplayModelCompileListener extends GlobalModelTestCase.TestListener implements GlobalModelListener {
    
    @Override public void compileStarted() { }
    
    
    @Override public void compileEnded(File workDir, List<? extends File> excludedFiles) {
      synchronized(_compileLock) { 
        _compileDone = true;
        _compileLock.notify();
      }
    }
    
    @Override public void fileOpened(OpenDefinitionsDocument doc) { }
    @Override public void activeDocumentChanged(OpenDefinitionsDocument active) { }
  }
  
  
  class SingleDisplayModelFileOpenedListener extends GlobalModelTestCase.TestListener implements GlobalModelListener {
    
    @Override public void fileClosed(OpenDefinitionsDocument doc) { }
    
    @Override public void fileOpened(OpenDefinitionsDocument doc) { }
    
    @Override public void newFileCreated(OpenDefinitionsDocument doc) { }
    @Override public void activeDocumentChanged(OpenDefinitionsDocument doc) { 
      synchronized(_openLock) {
        _openDone = true;
        _openLock.notify();
      }
    }
  }
  
  
  class SingleDisplayModelFileClosedListener extends GlobalModelTestCase.TestListener implements GlobalModelListener {
    
    @Override public void fileClosed(OpenDefinitionsDocument doc) {
      synchronized(_closeLock) {
        _closeDone = true;
        _closeLock.notify();
      }
    }
    
    @Override public void fileOpened(OpenDefinitionsDocument doc) { }
    @Override public void newFileCreated(OpenDefinitionsDocument doc) { }
    @Override public void activeDocumentChanged(OpenDefinitionsDocument active) { }
  }
  
  
  protected File tempFile(String fileName) throws IOException {
    File f =  File.createTempFile(fileName, ".java", _tempDir).getCanonicalFile();
    f.deleteOnExit();
    return f;
  }
  
  
  public void testGotoFileUnderCursor() throws IOException {

    String user = System.getProperty("user.name");
    _tempDir = IOUtil.createAndMarkTempDirectory("DrJava-test-" + user, "");
    
    final File goto1_file = new File(_tempDir, "GotoFileUnderCursor1.java");
    String goto1_string = "GotoFileUnderCursorTest";
    IOUtil.writeStringToFile(goto1_file, goto1_string);
    goto1_file.deleteOnExit();
    
    final File goto2_file = new File(_tempDir, "GotoFileUnderCursorTest.java");
    String goto2_string = "GotoFileUnderCursor1";
    IOUtil.writeStringToFile(goto2_file, goto2_string);
    goto2_file.deleteOnExit();
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        _frame.pack();
        _frame.open(new FileOpenSelector() {
          public File[] getFiles() { return new File[] { goto1_file, goto2_file }; }
        });
      }
    });
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        _frame.initGotoFileDialog();
        _frame._gotoFileDialog.addWindowListener(new WindowListener() {
          public void windowActivated(WindowEvent e) { throw new RuntimeException("Should not activate _gotoFileDialog"); }
          public void windowClosed(WindowEvent e) { throw new RuntimeException("Should not close _gotoFileDialog"); }
          public void windowClosing(WindowEvent e) { throw new RuntimeException("Should not be closing _gotoFileDialog"); }
          public void windowDeactivated(WindowEvent e) { throw new RuntimeException("Should not deactivate _gotoFileDialog"); }
          public void windowDeiconified(WindowEvent e) { throw new RuntimeException("Should not deiconify _gotoFileDialog"); }
          public void windowIconified(WindowEvent e) { throw new RuntimeException("Should not iconify _gotoFileDialog"); }
          public void windowOpened(WindowEvent e) { throw new RuntimeException("Should not open _gotoFileDialog"); }
        });
      }});
    
    Utilities.clearEventQueue();
    SingleDisplayModel model = _frame.getModel();
    OpenDefinitionsDocument goto1_doc = model.getDocumentForFile(goto1_file);
    OpenDefinitionsDocument goto2_doc = model.getDocumentForFile(goto2_file);
    model.setActiveDocument(model.getDocumentForFile(goto1_file));
    assertEquals("Document contains the incorrect text", goto1_string, model.getActiveDocument().getText());
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _frame._gotoFileUnderCursor(); }
    });
    
    Utilities.clearEventQueue();
    assertEquals("Incorrect active document; did not go to?", goto2_doc, model.getActiveDocument());
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _frame._gotoFileUnderCursor(); }
    });
    
    Utilities.clearEventQueue();
    assertEquals("Incorrect active document; did not go to?", goto1_doc, model.getActiveDocument());
    
    _log.log("gotoFileUnderCursor completed");
  }
  
  
  public void testGotoFileUnderCursorAppendJava() throws IOException {
    String user = System.getProperty("user.name");
    _tempDir = IOUtil.createAndMarkTempDirectory("DrJava-test-" + user, "");
    
    final File goto1_file = new File(_tempDir, "GotoFileUnderCursor2Test.java");
    String goto1_string = "GotoFileUnderCursor2";
    IOUtil.writeStringToFile(goto1_file, goto1_string);
    goto1_file.deleteOnExit();
    
    final File goto2_file = new File(_tempDir, "GotoFileUnderCursor2.java");
    String goto2_string = "GotoFileUnderCursor2Test";
    IOUtil.writeStringToFile(goto2_file, goto2_string);
    goto2_file.deleteOnExit();
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        _frame.pack();
        _frame.open(new FileOpenSelector() {
          public File[] getFiles() {
            return new File[] { goto1_file, goto2_file };
          }
        });
      }
    });
    
    Utilities.clearEventQueue();
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        _frame.initGotoFileDialog();
        _frame._gotoFileDialog.addWindowListener(new WindowListener() {
          public void windowActivated(WindowEvent e) { throw new RuntimeException("Should not activate _gotoFileDialog"); }
          public void windowClosed(WindowEvent e) { throw new RuntimeException("Should not close _gotoFileDialog"); }
          public void windowClosing(WindowEvent e) { throw new RuntimeException("Should not be closing _gotoFileDialog"); }
          public void windowDeactivated(WindowEvent e) { throw new RuntimeException("Should not deactivate _gotoFileDialog"); }
          public void windowDeiconified(WindowEvent e) { throw new RuntimeException("Should not deiconify _gotoFileDialog"); }
          public void windowIconified(WindowEvent e) { throw new RuntimeException("Should not iconify _gotoFileDialog"); }
          public void windowOpened(WindowEvent e) { throw new RuntimeException("Should not open _gotoFileDialog"); }
        });
      }});
    
    Utilities.clearEventQueue();
    
    SingleDisplayModel model = _frame.getModel();
    OpenDefinitionsDocument goto1_doc = model.getDocumentForFile(goto1_file);
    OpenDefinitionsDocument goto2_doc = model.getDocumentForFile(goto2_file);
    model.setActiveDocument(model.getDocumentForFile(goto1_file));
    assertEquals("Document contains the incorrect text", goto1_string, model.getActiveDocument().getText());
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _frame._gotoFileUnderCursor(); }
    });
    
    Utilities.clearEventQueue();
    
    assertEquals("Incorrect active document; did not go to?", goto2_doc, model.getActiveDocument());
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _frame._gotoFileUnderCursor(); }
    });
    
    Utilities.clearEventQueue();
    assertEquals("Incorrect active document; did not go to?", goto1_doc, model.getActiveDocument());
    
    _log.log("gotoFileUnderCursorAppendJava completed");
  }
  
  
  public void testGotoFileUnderCursorShowDialog() throws IOException {

    String user = System.getProperty("user.name");
    _tempDir = IOUtil.createAndMarkTempDirectory("DrJava-test-" + user, "");
    
    final File goto1_file = new File(_tempDir, "GotoFileUnderCursor3.java");
    String goto1_string = "GotoFileUnderCursor";
    IOUtil.writeStringToFile(goto1_file, goto1_string);
    goto1_file.deleteOnExit();
    
    final File goto2_file = new File(_tempDir, "GotoFileUnderCursor4.java");
    String goto2_string = "GotoFileUnderCursor3";
    IOUtil.writeStringToFile(goto2_file, goto2_string);
    goto2_file.deleteOnExit();
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        _frame.pack();
        _frame.open(new FileOpenSelector() {
          public File[] getFiles() { return new File[] { goto1_file, goto2_file }; }
        });
      }
    });
    
    final int[] count = new int[2];
    Utilities.invokeAndWait(new Runnable() {
      public void run() { 
        _frame.initGotoFileDialog();
        _frame._gotoFileDialog.addWindowListener(new WindowListener() {
          public void windowActivated(WindowEvent e) { ++count[0]; }
          public void windowClosed(WindowEvent e) { throw new RuntimeException("Should not close _gotoFileDialog"); }
          public void windowClosing(WindowEvent e) { throw new RuntimeException("Should not be closing _gotoFileDialog"); }
          public void windowDeactivated(WindowEvent e) {  }
          public void windowDeiconified(WindowEvent e) { throw new RuntimeException("Should not deiconify _gotoFileDialog"); }
          public void windowIconified(WindowEvent e) { throw new RuntimeException("Should not iconify _gotoFileDialog"); }
          public void windowOpened(WindowEvent e) { ++count[1]; }
        });
      }
    });
    
    Utilities.clearEventQueue();
    
    SingleDisplayModel model = _frame.getModel();
    model.setActiveDocument(model.getDocumentForFile(goto1_file));
    
    assertEquals("Document contains the incorrect text", goto1_string, model.getActiveDocument().getText());
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _frame._gotoFileUnderCursor(); } });                    
    Utilities.clearEventQueue();  
    
    
    

    

    
    _log.log("gotoFileUnderCursorShowDialog completed");
  }
}
