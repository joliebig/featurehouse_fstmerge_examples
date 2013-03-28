

package edu.rice.cs.drjava.ui;

import java.awt.Component;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;

import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.model.repl.InteractionsDocumentTest.TestBeep;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.*;
import edu.rice.cs.util.text.*;
import edu.rice.cs.util.swing.Utilities;


public final class MainFrameTest extends MultiThreadedTestCase {

  private MainFrame _frame;

  
  private File _tempDir;
  
  
  protected boolean _compileDone;
  protected Object _compileLock = new Object();
  
  
  protected boolean _closeDone;
  protected Object _closeLock = new Object();


  
  
  public void setUp() throws Exception {
    super.setUp();
    Utilities.invokeAndWait(new Runnable() { public void run() { _frame = new MainFrame(); }});
    _frame.pack();
  }

  public void tearDown() throws Exception {
    _frame.dispose();
    _frame = null;
    super.tearDown();
  }

  
  public void testCreateManualToolbarButton() {
    Action a = new AbstractAction("Test Action") { public void actionPerformed(ActionEvent ae) { } };
    
    a.putValue(Action.SHORT_DESCRIPTION, "test tooltip");
    JButton b = _frame._createManualToolbarButton(a);

    assertTrue("Returned JButton is enabled.", ! b.isEnabled());
    assertEquals("Tooltip text not set.", "test tooltip", b.getToolTipText());

  }

  
  public void testDocLocationAfterSwitch() throws BadLocationException {
    final DefinitionsPane pane = _frame.getCurrentDefPane();
    OpenDefinitionsDocument doc = pane.getOpenDefDocument();
    doc.insertString(0, "abcd", null);
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        pane.setCaretPosition(3); 
      }
    }); 
      
    assertEquals("Location of old doc before switch", 3, doc.getCurrentLocation());
      
    
    SingleDisplayModel model = _frame.getModel();
    final OpenDefinitionsDocument oldDoc = doc;
    final OpenDefinitionsDocument newDoc = model.newFile();

    
    DefinitionsPane curPane;
    OpenDefinitionsDocument curDoc;
    curPane = _frame.getCurrentDefPane();
    curDoc = curPane.getOpenDefDocument();
    assertEquals("New curr DefPane's document", newDoc, curDoc);
    assertEquals("Location in new document", 0, newDoc.getCurrentLocation());

    
    model.setActiveNextDocument(); 
    assertEquals("Next active doc", oldDoc, model.getActiveDocument());
                 
    
    curPane = _frame.getCurrentDefPane();
    curDoc = curPane.getOpenDefDocument();
    assertEquals("Current document is old document", oldDoc, curDoc);
    assertEquals("Location of old document", 3, curDoc.getCurrentLocation());

  }

  

  

  
  public void testCorrectInteractionsDocument() throws EditDocumentException {
    InteractionsPane pane = _frame.getInteractionsPane();
    SingleDisplayModel model = _frame.getModel();
    InteractionsDJDocument doc = model.getSwingInteractionsDocument();

    
    model.getInteractionsModel().getDocument().setBeep(new TestBeep());

    
    assertTrue("UI's int. doc. should equals Model's int. doc.", pane.getDocument() == doc);

    int origLength = doc.getLength();
    doc.insertText(1, "typed text", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("Document should not have changed.", origLength, doc.getLength());

  }

  
  public void testMultilineIndentAfterScroll() throws BadLocationException {
    String text =
      "public class stuff {\n" +
      "private int _int;\n" +
      "private Bar _bar;\n" +
      "public void foo() {\n" +
      "_bar.baz(_int);\n" +
      "}\n" +
      "}\n";
    
    String indented =
      "public class stuff {\n" +
      "  private int _int;\n" +
      "  private Bar _bar;\n" +
      "  public void foo() {\n" +
      "    _bar.baz(_int);\n" +
      "  }\n" +
      "}\n";
    
    int oldPos;
    final int newPos = 20;
    
    final DefinitionsPane pane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = pane.getOpenDefDocument();
    
    DrJava.getConfig().setSetting(OptionConstants.INDENT_LEVEL, new Integer(2));
    doc.insertString(0, text, null);
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        pane.setCaretPosition(0);
        pane.endCompoundEdit(); } 
    });
    
    assertEquals("Should have inserted correctly.", text, doc.getText());
    
    doc.indentLines(0, doc.getLength());
    assertEquals("Should have indented.", indented, doc.getText());
    oldPos = pane.getCaretPosition();


    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        pane.setCaretPosition(newPos);

      }
    });
    doc.getUndoManager().undo();  
    
    assertEquals("Should have undone.", text, doc.getText());
    Utilities.clearEventQueue();
    
    int rePos = pane.getCaretPosition();

    assertEquals("Undo should have restored caret position.", oldPos, rePos);
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        pane.setCaretPosition(newPos);
        doc.getUndoManager().redo();
      }
    });
    assertEquals("redo",indented, doc.getText());
    assertEquals("redo restores caret position", oldPos, pane.getCaretPosition());

  }

  
  public void testGlassPaneEditableState() {
    SingleDisplayModel model = _frame.getModel();

    OpenDefinitionsDocument doc1 = model.newFile();
    OpenDefinitionsDocument doc2 = model.newFile();

    

    JScrollPane pane1 = _frame._createDefScrollPane(doc1);
    JScrollPane pane2 = _frame._createDefScrollPane(doc2);

    DefinitionsPane defPane1 = (DefinitionsPane) pane1.getViewport().getView();
    DefinitionsPane defPane2 = (DefinitionsPane) pane2.getViewport().getView();

    _frame._switchDefScrollPane();
    assertTrue("Start: defPane1",defPane1.isEditable());
    assertTrue("Start: defPane2",defPane2.isEditable());
    _frame.hourglassOn();
    assertTrue("Glass on: defPane1",defPane1.isEditable());
    assertTrue("Glass on: defPane2",(!defPane2.isEditable()));
    model.setActiveDocument(doc1);
    
    _frame._switchDefScrollPane();
    assertTrue("Doc Switch: defPane1",(! defPane1.isEditable()));
    assertTrue("Doc Switch: defPane2",defPane2.isEditable());
    _frame.hourglassOff();
    assertTrue("End: defPane1",defPane1.isEditable());
    assertTrue("End: defPane2",defPane2.isEditable());

  }

  private KeyEvent makeFindKeyEvent(Component c, long when) {
    return new KeyEvent(c, KeyEvent.KEY_PRESSED, when, KeyEvent.CTRL_MASK, KeyEvent.VK_F, 'F');
  }
  
  
  public void testGlassPaneHidesKeyEvents() {
    SingleDisplayModel model = _frame.getModel();

    OpenDefinitionsDocument doc1 = model.newFile();
    OpenDefinitionsDocument doc2 = model.newFile();

    

    JScrollPane pane1 = _frame._createDefScrollPane(doc1);
    JScrollPane pane2 = _frame._createDefScrollPane(doc2);

    DefinitionsPane defPane1 = (DefinitionsPane) pane1.getViewport().getView();
    DefinitionsPane defPane2 = (DefinitionsPane) pane2.getViewport().getView();
    
    _frame.hourglassOn();

    defPane1.processKeyEvent(makeFindKeyEvent(defPane1, 70));
    assertTrue("the find replace dialog should not come up", !_frame.getFindReplaceDialog().isDisplayed());
    _frame.getInteractionsPane().processKeyEvent(makeFindKeyEvent(_frame.getInteractionsPane(), 0));
    assertTrue("the find replace dialog should not come up", !_frame.getFindReplaceDialog().isDisplayed());

    _frame.hourglassOff();

  }

  
  
  public void testSaveButtonEnabled() throws IOException {
    String user = System.getProperty("user.name");
    _tempDir = FileOps.createTempDirectory("DrJava-test-" + user);
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
    FileOps.writeStringToFile(forceOpenClass1_file, forceOpenClass1_string);
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
    
    assertTrue("the save button should not be enabled after opening a document", !_frame.saveEnabledHuh());

  }
  
  
  public void testDancingUIFileOpened() throws IOException {
    
    
     String user = System.getProperty("user.name");
     _tempDir = FileOps.createTempDirectory("DrJava-test-" + user);
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

     FileOps.writeStringToFile(forceOpenClass1_file, forceOpenClass1_string);
     FileOps.writeStringToFile(forceOpenClass2_file, forceOpenClass2_string);
     FileOps.writeStringToFile(forceOpenClass3_file, forceOpenClass3_string);
     forceOpenClass1_file.deleteOnExit();
     forceOpenClass2_file.deleteOnExit();
     forceOpenClass3_file.deleteOnExit();

     
     
     final ComponentAdapter listener = new ComponentAdapter() {
       public void componentResized(ComponentEvent event) {
         _testFailed = true;
         fail("testDancingUI: Open Documents List danced!");
       }
     };
     final SingleDisplayModelCompileListener compileListener = new SingleDisplayModelCompileListener();


       Utilities.invokeLater(new Runnable() { public void run() {
         _frame.pack();
         _frame.open(new FileOpenSelector() {
           public File[] getFiles() {
             File[] return_me = new File[1];
             return_me[0] = new File(_tempDir, "ForceOpenClass1.java");
             return return_me;
           }
         });
         _frame.getModel().addListener(compileListener);
         _frame.addComponentListenerToOpenDocumentsList(listener);
         _compileDone = false;
         _frame.getCompileAllButton().doClick();
       }});



     synchronized(_compileLock) {
       try { while (! _compileDone) _compileLock.wait(); }
       catch(InterruptedException e) { fail(e.toString()); }
     }
     
     if (! FileOps.deleteDirectory(_tempDir))
       System.out.println("Couldn't fully delete directory " + _tempDir.getAbsolutePath() + "\nDo it by hand.\n");
   

  }

  
  public void testDancingUIFileClosed() throws IOException {
    
    String user = System.getProperty("user.name");
    _tempDir = FileOps.createTempDirectory("DrJava-test-" + user);
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
    
    FileOps.writeStringToFile(forceOpenClass1_file, forceOpenClass1_string);
    forceOpenClass1_file.deleteOnExit();
    
    final ComponentAdapter listener = new ComponentAdapter() {
      public void componentResized(ComponentEvent event) {
        _testFailed = true;
        fail("testDancingUI: Open Documents List danced!");
      }
    };
    final SingleDisplayModelFileClosedListener closeListener = new SingleDisplayModelFileClosedListener();
    
    try {
      Utilities.invokeAndWait(new Runnable() { public void run() {

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
        _closeDone = false;
        
        _frame.getCloseButton().doClick();
      }});
    }
    catch(UnexpectedException e) { fail(e.toString()); }
    

    
    synchronized(_closeLock) {
      try { while (! _closeDone) _closeLock.wait(); }
      catch(InterruptedException e) { fail(e.toString()); }
    }
    
    if (! FileOps.deleteDirectory(_tempDir)) {
      System.out.println("Couldn't fully delete directory " + _tempDir.getAbsolutePath() +
                         "\nDo it by hand.\n");
    }

  }

  
  class SingleDisplayModelCompileListener extends GlobalModelTestCase.TestListener
    implements GlobalModelListener {

    public void compileStarted() { }

    
    public void compileEnded(File workDir, File[] excludedFiles) {
      synchronized(_compileLock) { 
        _compileDone = true;
        _compileLock.notify();
      }
    }

    public void fileOpened(OpenDefinitionsDocument doc) { }
    public void activeDocumentChanged(OpenDefinitionsDocument active) { }
  }

  
  class SingleDisplayModelFileClosedListener extends GlobalModelTestCase.TestListener
    implements GlobalModelListener {

    public void fileClosed(OpenDefinitionsDocument doc) {
      synchronized(_closeLock) {
        _closeDone = true;
        _closeLock.notify();
      }
    }

    public void fileOpened(OpenDefinitionsDocument doc) { }
    public void newFileCreated(OpenDefinitionsDocument doc) { }
    public void activeDocumentChanged(OpenDefinitionsDocument active) { }
  }

  
  protected File tempFile(String fileName) throws IOException {
    File f =  File.createTempFile(fileName, ".java", _tempDir).getCanonicalFile();
    f.deleteOnExit();
    return f;
  }
  
  
  public void testGotoFileUnderCursor() throws IOException {

    String user = System.getProperty("user.name");
    _tempDir = FileOps.createTempDirectory("DrJava-test-" + user);

    final File goto1_file = new File(_tempDir, "GotoFileUnderCursor1.java");
    String goto1_string = "GotoFileUnderCursorTest";
    FileOps.writeStringToFile(goto1_file, goto1_string);
    goto1_file.deleteOnExit();

    final File goto2_file = new File(_tempDir, "GotoFileUnderCursorTest.java");
    String goto2_string = "GotoFileUnderCursor1";
    FileOps.writeStringToFile(goto2_file, goto2_string);
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
    
    
    SingleDisplayModel model = _frame.getModel();
    OpenDefinitionsDocument goto1_doc = model.getDocumentForFile(goto1_file);
    OpenDefinitionsDocument goto2_doc = model.getDocumentForFile(goto2_file);
    model.setActiveDocument(model.getDocumentForFile(goto1_file));
    assertEquals("Document contains the incorrect text", goto1_string, model.getActiveDocument().getText());
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _frame._gotoFileUnderCursor(); }
    });
    assertEquals("Incorrect active document; did not go to?", goto2_doc, model.getActiveDocument());
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _frame._gotoFileUnderCursor(); }
    });
    assertEquals("Incorrect active document; did not go to?", goto1_doc, model.getActiveDocument());
  }
  
  
  public void testGotoFileUnderCursorAppendJava() throws IOException {
    String user = System.getProperty("user.name");
    _tempDir = FileOps.createTempDirectory("DrJava-test-" + user);

    final File goto1_file = new File(_tempDir, "GotoFileUnderCursor2Test.java");
    String goto1_string = "GotoFileUnderCursor2";
    FileOps.writeStringToFile(goto1_file, goto1_string);
    goto1_file.deleteOnExit();

    final File goto2_file = new File(_tempDir, "GotoFileUnderCursor2.java");
    String goto2_string = "GotoFileUnderCursor2Test";
    FileOps.writeStringToFile(goto2_file, goto2_string);
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
    
    SingleDisplayModel model = _frame.getModel();
    OpenDefinitionsDocument goto1_doc = model.getDocumentForFile(goto1_file);
    OpenDefinitionsDocument goto2_doc = model.getDocumentForFile(goto2_file);
    model.setActiveDocument(model.getDocumentForFile(goto1_file));
    assertEquals("Document contains the incorrect text", goto1_string, model.getActiveDocument().getText());
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _frame._gotoFileUnderCursor(); }
    });
    assertEquals("Incorrect active document; did not go to?", goto2_doc, model.getActiveDocument());
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _frame._gotoFileUnderCursor(); }
    });
    assertEquals("Incorrect active document; did not go to?", goto1_doc, model.getActiveDocument());
  }
  
  
  public void testGotoFileUnderCursorShowDialog() throws IOException {

    String user = System.getProperty("user.name");
    _tempDir = FileOps.createTempDirectory("DrJava-test-" + user);

    final File goto1_file = new File(_tempDir, "GotoFileUnderCursor3.java");
    String goto1_string = "GotoFileUnderCursor";
    FileOps.writeStringToFile(goto1_file, goto1_string);
    goto1_file.deleteOnExit();

    final File goto2_file = new File(_tempDir, "GotoFileUnderCursor4.java");
    String goto2_string = "GotoFileUnderCursor3";
    FileOps.writeStringToFile(goto2_file, goto2_string);
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
                            
    SingleDisplayModel model = _frame.getModel();
    OpenDefinitionsDocument goto1_doc = model.getDocumentForFile(goto1_file);
    OpenDefinitionsDocument goto2_doc = model.getDocumentForFile(goto2_file);
    model.setActiveDocument(model.getDocumentForFile(goto1_file));

    assertEquals("Document contains the incorrect text", goto1_string, model.getActiveDocument().getText());
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _frame._gotoFileUnderCursor(); } });                    
    Utilities.clearEventQueue();  
                            

    assertEquals("Did not open _gotoFileDialog", 1, count[1]);
  }
}
