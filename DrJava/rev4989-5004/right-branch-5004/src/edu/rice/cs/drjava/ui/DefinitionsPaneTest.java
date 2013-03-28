

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.MultiThreadedTestCase;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.swing.Utilities;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import java.util.Date;


public final class DefinitionsPaneTest extends MultiThreadedTestCase {

  private volatile MainFrame _frame;
  
  public static final Log _log = new Log("DefinitionsPaneTest.txt", false);  
  
  private static final char UNDEFINED = KeyEvent.CHAR_UNDEFINED;
  private static final int PRESSED = KeyEvent.KEY_PRESSED;
  private static final int RELEASED = KeyEvent.KEY_RELEASED;
  private static final int SHIFT = InputEvent.SHIFT_MASK;
  private static final int TYPED = KeyEvent.KEY_TYPED;
  private static final int VK_UNDEF = KeyEvent.VK_UNDEFINED;
  private static final int META = KeyEvent.VK_META;
  private static final int W = KeyEvent.VK_W;
  private static final int M_MASK = InputEvent.META_MASK;
  private static final int BANG = KeyEvent.VK_EXCLAMATION_MARK;
  private static final int ALT = InputEvent.ALT_MASK;
  
  private static final int DEL_NEXT = OptionConstants.KEY_DELETE_NEXT.getDefault().get(0).getKeyCode();
  private static final int DEL_PREV = OptionConstants.KEY_DELETE_PREVIOUS.getDefault().get(0).getKeyCode();
    
  
  public void setUp() throws Exception {
    super.setUp();
    
    
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
        _log.log("Main Frame disposed");
        _frame = null;
      }
    });
    Utilities.clearEventQueue();
    super.tearDown();
  }
  
  
  public void testShiftBackspace() throws BadLocationException {

    final DefinitionsPane defPane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = defPane.getOpenDefDocument();
  
    _assertDocumentEmpty(doc, "before testing");
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        doc.append("test", null);
        defPane.setCaretPosition(4);
        
        
        defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), SHIFT, DEL_PREV, UNDEFINED));
        _log.log("first key event processed");
        defPane.processKeyEvent(new KeyEvent(defPane, RELEASED, (new Date()).getTime(), SHIFT, DEL_PREV, UNDEFINED));
        _frame.validate();
      }
    });
    Utilities.clearEventQueue();
    
    _log.log("second key event processed");
    _assertDocumentContents(doc, "tes", "Did not delete on shift+backspace");
    _log.log("Halfway through testShiftBackspace");
    
     
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        
        defPane.setCaretPosition(1);
        
        defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), SHIFT, DEL_NEXT, UNDEFINED));
        defPane.processKeyEvent(new KeyEvent(defPane, RELEASED, (new Date()).getTime(), SHIFT, DEL_NEXT, UNDEFINED));
        _frame.validate();
      }
    });
    Utilities.clearEventQueue();
    _assertDocumentContents(doc, "ts", "Did not delete on shift+delete");
    _log.log("testShiftBackSpace completed");
   
  }

  
  
  public void testTypeBraceNotInCode() throws BadLocationException {
    final DefinitionsPane defPane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = defPane.getOpenDefDocument();
    _assertDocumentEmpty(doc, "before testing");
    _log.log("calling invokeAndWait in testTypeBraceNotInCode");
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        doc.append("  \"", null);
        defPane.setCaretPosition(3);
        
        defPane.processKeyEvent(new KeyEvent(defPane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, '{'));
      }
    });
    Utilities.clearEventQueue();
        
    _assertDocumentContents(doc, "  \"{", "Brace should not indent in a string");
    _log.log("testTypeBraceNotInCode completed");
  }
  
  
  public void testTypeEnterNotInCode() throws BadLocationException, InterruptedException, InvocationTargetException {
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        
        final DefinitionsPane defPane = _frame.getCurrentDefPane();

        final OpenDefinitionsDocument doc = defPane.getOpenDefDocument();
        try { 
          _assertDocumentEmpty(doc, "before testing");
          doc.insertString(0, "/**", null);
          defPane.setCaretPosition(3);
          
          int enter = KeyEvent.VK_ENTER;
          defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), 0, enter, UNDEFINED));
          defPane.processKeyEvent(new KeyEvent(defPane, RELEASED, (new Date()).getTime(), 0, enter, UNDEFINED));
          _frame.validate();
        }
        catch(Throwable t) { listenerFail(t); }
        
        _log.log("Completed processing of keyEvents");
        
        _assertDocumentContents(doc, "/**\n * ", "Enter should indent in a comment");
        _log.log("testTypeEnterNotInCode completed");
      }
    });
  }
  
  
  public void testMetaKeyPress() throws BadLocationException {
    final DefinitionsPane defPane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = defPane.getOpenDefDocument();
    _assertDocumentEmpty(doc, "point 0");
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        
        defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), M_MASK, META, UNDEFINED));
        _frame.validate();
      }
    });
    Utilities.clearEventQueue();
        
    _assertDocumentEmpty(doc, "point 1");
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), M_MASK, W, UNDEFINED));
        _frame.validate();
      }
    }); 
    Utilities.clearEventQueue();
    
    _assertDocumentEmpty(doc, "point 2");
        
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        defPane.processKeyEvent(new KeyEvent(defPane, TYPED, (new Date()).getTime(), M_MASK, VK_UNDEF, 'w'));
        _frame.validate();
      }
    });
    Utilities.clearEventQueue();
    
    _assertDocumentEmpty(doc, "point 3");
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        defPane.processKeyEvent(new KeyEvent(defPane, RELEASED, (new Date()).getTime(), M_MASK, W, UNDEFINED));
        _frame.validate();
      }
    });
    Utilities.clearEventQueue();
     
    _assertDocumentEmpty(doc, "point 4");
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        defPane.processKeyEvent(new KeyEvent(defPane, RELEASED, (new Date()).getTime(), 0, META, UNDEFINED));
        _frame.validate();
      }
    });
    Utilities.clearEventQueue();
    
    _assertDocumentEmpty(doc, "point 5");
    
    _log.log("testMetaKeyPress completed");
  }
  
  
  private int _redoPos;
  
  
  public void testMultilineCommentOrUncommentAfterScroll() throws BadLocationException {
    
    final DefinitionsPane pane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = pane.getOpenDefDocument();
    final String text =
      "public class stuff {\n" +
      "  private int _int;\n" +
      "  private Bar _bar;\n" +
      "  public void foo() {\n" +
      "    _bar.baz(_int);\n" +
      "  }\n" +
      "}\n";
    
    final String commented =
      "//public class stuff {\n" +
      "//  private int _int;\n" +
      "//  private Bar _bar;\n" +
      "//  public void foo() {\n" +
      "//    _bar.baz(_int);\n" +
      "//  }\n" +
      "//}\n";
    
    
    
    
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        doc.append(text, null);
        assertEquals("insertion", text, doc.getText());


        pane.endCompoundEdit();
        doc.commentLines(0, doc.getLength()); 

        assertEquals("commenting", commented, doc.getText());
        int newPos = doc.getCurrentLocation();


        doc.getUndoManager().undo(); 


        assertEquals("undo commenting", text, doc.getText());







        assertEquals("undoing commenting restores cursor position", 0, doc.getCurrentLocation());
        
        doc.getUndoManager().redo();
        assertEquals("redo commenting", commented, doc.getText());
        assertEquals("redoing commenting restores cursor position", newPos, doc.getCurrentLocation());

        pane.endCompoundEdit(); 
        doc.uncommentLines(0, doc.getLength()); 
        assertEquals("uncommenting", text, doc.getText());

        _redoPos = doc.getCurrentLocation();  
    
        doc.getUndoManager().undo();
        
      } });
    
    
    

    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        

    
        assertEquals("undo uncommenting", commented, doc.getText());


        
        assertEquals("undoing uncommenting restores cursor position", 0, doc.getCurrentLocation());
    
        doc.getUndoManager().redo();
        assertEquals("redo uncommenting",text, doc.getText());
        assertEquals("redoing uncommenting restores cursor position", _redoPos, doc.getCurrentLocation());
                                                        

      }
    });
    
    _log.log("testMultiLineCommentOrUncommentAfterScroll completed");
  }
  
  protected void _assertDocumentEmpty(DJDocument doc, String message) {
    _assertDocumentContents(doc, "", message);
  }
  
  protected void _assertDocumentContents(DJDocument doc, String contents, String message) {
    assertEquals(message, contents, doc.getText());
  }
  
  public void testGranularUndo() throws BadLocationException {
    final DefinitionsPane defPane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = defPane.getOpenDefDocument();
    
    
    
    assertEquals("Should start out empty.", "",  doc.getText());
    
    
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), 0, KeyEvent.VK_A, UNDEFINED));
        defPane.processKeyEvent(new KeyEvent(defPane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, 'a'));
        defPane.processKeyEvent(new KeyEvent(defPane, RELEASED, (new Date()).getTime(), 0, KeyEvent.VK_A, UNDEFINED));

        assertEquals("caret at line end", doc.getLength(), defPane.getCaretPosition());
        
        
        defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), 0, BANG, UNDEFINED));
        defPane.processKeyEvent(new KeyEvent(defPane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, '!'));
        defPane.processKeyEvent(new KeyEvent(defPane, RELEASED, (new Date()).getTime(), 0, BANG, UNDEFINED));

        assertEquals("caret at line end", doc.getLength(), defPane.getCaretPosition());
        
        
        defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), SHIFT, KeyEvent.VK_B, UNDEFINED));
        defPane.processKeyEvent(new KeyEvent(defPane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, 'B'));
        defPane.processKeyEvent(new KeyEvent(defPane, RELEASED, (new Date()).getTime(), SHIFT, KeyEvent.VK_B, UNDEFINED));

        assertEquals("caret at line end", doc.getLength(), defPane.getCaretPosition());
        
        
        defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), 0, KeyEvent.VK_9, UNDEFINED));
        defPane.processKeyEvent(new KeyEvent(defPane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, '9'));
        defPane.processKeyEvent(new KeyEvent(defPane, RELEASED, (new Date()).getTime(), 0, KeyEvent.VK_9, UNDEFINED));

        assertEquals("caret at line end", doc.getLength(), defPane.getCaretPosition());
        _frame.validate();
      } 
    });
    Utilities.clearEventQueue();
    
    assertEquals("The text should have been inserted", "a!B9",  doc.getText());
    
    
    final Vector<KeyStroke> ks = DrJava.getConfig().getSetting(OptionConstants.KEY_UNDO);
    final Action a = KeyBindingManager.ONLY.get(ks.get(0));
    
    final KeyEvent e = new KeyEvent(defPane, PRESSED, 0, ks.get(0).getModifiers(), ks.get(0).getKeyCode(), UNDEFINED);
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
      defPane.processKeyEvent(e); 
      _frame.validate();
      } 
    });
    Utilities.clearEventQueue();
  
    assertEquals("Should have undone correctly.", "", doc.getText());
    
    
    
    
    
    
    
     Utilities.invokeAndWait(new Runnable() {
       public void run() {
         defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), ALT, KeyEvent.VK_Q, UNDEFINED));
         defPane.processKeyEvent(new KeyEvent(defPane,
                                                  TYPED,
                                                  (new Date()).getTime(),
                                                  ALT,
                                                  VK_UNDEF, 'Q'));
         defPane.processKeyEvent(new KeyEvent(defPane,
                                                  RELEASED,
                                                  (new Date()).getTime(),
                                                  ALT,
                                                  KeyEvent.VK_Q, UNDEFINED));
         
         
         SwingUtilities.notifyAction(a, ks.get(0), e, e.getSource(), e.getModifiers());
         _frame.validate();
    
       }
     });
     Utilities.clearEventQueue();
    
    
    
     
     _log.log("testGranularUndo completed");
  }
  
  
  public void testActiveAndInactive() {
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        SingleDisplayModel _model = _frame.getModel();  
        
        DefinitionsPane pane1, pane2;
        DJDocument doc1, doc2;
        
        pane1 = _frame.getCurrentDefPane(); 
        doc1 = pane1.getDJDocument();
        assertTrue("the active pane should have an open definitions document", doc1 instanceof OpenDefinitionsDocument);
        
        _model.newFile();  
        pane2 = _frame.getCurrentDefPane();  
        doc2 = pane2.getDJDocument();
        
        assertTrue("the active pane should have an open definitions document", doc2 instanceof OpenDefinitionsDocument);
        
        _model.setActiveNextDocument();    
        DefinitionsPane pane = _frame.getCurrentDefPane();
        assertEquals("Confirm that next pane is the other pane", pane1, pane);
        
        assertTrue("pane2 should have an open definitions document", doc2 instanceof OpenDefinitionsDocument);
        assertTrue("pane1 should have an open definitions document", doc1 instanceof OpenDefinitionsDocument);
        
        _log.log("testActiveAndInactive completed");
      }
    });
  }
  
  
  
  
  
  public void testFrenchKeyStrokes() throws IOException, InterruptedException {
    
    final DefinitionsPane pane = _frame.getCurrentDefPane(); 
    
    final KeyEvent ke1 = new KeyEvent(pane, TYPED, 0, 0, VK_UNDEF, 'T'); 
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        pane.processKeyEvent(ke1); 
        _frame.validate();
      } 
    });
    Utilities.clearEventQueue();
    
    assertFalse("The KeyEvent for pressing \"T\" should not involve an Alt Key if this fails we are in trouble!", pane.checkAltKey());
    
    final KeyEvent ke2 = new KeyEvent(pane, TYPED, 0, ALT, VK_UNDEF, '{'); 
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        pane.processKeyEvent(ke2); 
        _frame.validate();   
      } 
    });
    Utilities.clearEventQueue();
        
    assertTrue("Alt should have been registered and allowed to pass!", pane.checkAltKey());
    
    final KeyEvent ke3 = new KeyEvent(pane, TYPED, 0, ALT, VK_UNDEF, '}'); 
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        pane.processKeyEvent(ke3); 
        _frame.validate();
      } 
    });
    Utilities.clearEventQueue();
    
    assertTrue("Alt should have been registered and allowed to pass!", pane.checkAltKey());
    
    
    final KeyEvent ke4 = new KeyEvent(pane, TYPED, 0, ALT, VK_UNDEF, '['); 
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        pane.processKeyEvent(ke4);
        _frame.validate();
      } 
    });
    Utilities.clearEventQueue();
    
    assertTrue("Alt should have been registered and allowed to pass!", pane.checkAltKey());
    
    final KeyEvent ke5 = new KeyEvent(pane, TYPED, 0, ALT, VK_UNDEF, ']'); 
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        pane.processKeyEvent(ke5); 
        _frame.validate();
      } 
    });
    Utilities.clearEventQueue();
    
    assertTrue("Alt should have been registered and allowed to pass!", pane.checkAltKey());
    
    _log.log("testFrenchKeyStrokes completed");
  } 


  public void testBackspace() {
   
    Utilities.invokeAndWait(new Runnable() { 
      
      public void run() { 
        final DefinitionsPane defPane = _frame.getCurrentDefPane();
        final OpenDefinitionsDocument doc = defPane.getOpenDefDocument();
        _assertDocumentEmpty(doc, "before testing");
        doc.append("test", null);
        defPane.setCaretPosition(4);
        final int VK_BKSP = KeyEvent.VK_BACK_SPACE;
        
        defPane.processKeyEvent(new KeyEvent(defPane, PRESSED, (new Date()).getTime(), 0, VK_BKSP, UNDEFINED));
        defPane.processKeyEvent(new KeyEvent(defPane, RELEASED, (new Date()).getTime(), 0, VK_BKSP, UNDEFINED));
        defPane.processKeyEvent(new KeyEvent(defPane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, '\b'));
        _frame.validate();
        _assertDocumentContents(doc, "tes", "Deleting with Backspace went wrong");
        _log.log("testBackSpace completed");
      }
    });
    
  
  }
  
  private volatile String _result;
  
  
  public void testMatchBraceText() {

    final DefinitionsPane defPane = _frame.getCurrentDefPane();
    final OpenDefinitionsDocument doc = defPane.getOpenDefDocument();
    Utilities.clearEventQueue();
    
    _assertDocumentEmpty(doc, "before testing");
    
    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        doc.append( 
                   "{\n" +
                   "public class Foo {\n" + 
                   "  private int whatev\n" + 
                   "  private void _method()\n" + 
                   "  {\n" + 
                   "     do stuff\n" + 
                   "     new Object() {\n" + 
                   "         }\n" + 
                   "  }\n" +
                   "}" +
                   "}"
                     , null);
        
        defPane.setCaretPosition(4); 
      } 
    });
    
    
    Utilities.invokeAndWait(new Runnable() { public void run() {  _result = _frame.getFileNameField(); } });
    
    final String taggedFileName = "Editing " + doc.getCompletePath();
    assertEquals("Should display the document path", taggedFileName, _result);
    
    Utilities.invokeAndWait(new Runnable() { public void run() {  defPane.setCaretPosition(115); } });
    
    Utilities.invokeAndWait(new Runnable() { public void run() {  _result = _frame.getFileNameField(); } });
    assertEquals("Should display the line matched", "Bracket matches:      new Object() {", _result);
    
    Utilities.invokeAndWait(new Runnable() { public void run() { defPane.setCaretPosition(102);  } });
    
    Utilities.invokeAndWait(new Runnable() { public void run() {  _result = _frame.getFileNameField(); } });
    assertEquals("Should display the document matched", "Bracket matches:      new Object(", _result);
    
    Utilities.invokeAndWait(new Runnable() { public void run() { defPane.setCaretPosition(119); } });
    
    Utilities.invokeAndWait(new Runnable() { public void run() {  _result = _frame.getFileNameField(); } });

    assertEquals("Should display the line matched", "Bracket matches:   private void _method()...{", _result);
    
    Utilities.invokeAndWait(new Runnable() { public void run() { defPane.setCaretPosition(121); } });
    
    Utilities.invokeAndWait(new Runnable() { public void run() {  _result = _frame.getFileNameField(); } });
    assertEquals("Should display the line matched", "Bracket matches: public class Foo {", _frame.getFileNameField());
    
    Utilities.invokeAndWait(new Runnable() { public void run() { defPane.setCaretPosition(122); } });
    
    Utilities.invokeAndWait(new Runnable() { public void run() {  _result = _frame.getFileNameField(); } });
    assertEquals("Should display only one brace when matching an open brace that is the first character in a line",
                 "Bracket matches: {", _result);
    
    _log.log("testMatchBraceTest completed");
  }

  static class KeyTestListener implements KeyListener {
    
    public void keyPressed(KeyEvent e) { DefinitionsPaneTest.fail("Unexpected keypress " + e); }
    public void keyReleased(KeyEvent e) { DefinitionsPaneTest.fail("Unexpected keyrelease " + e); }
    public void keyTyped(KeyEvent e) { DefinitionsPaneTest.fail("Unexpected keytyped " + e);  }
    public boolean done() { return true; }
  }

}

