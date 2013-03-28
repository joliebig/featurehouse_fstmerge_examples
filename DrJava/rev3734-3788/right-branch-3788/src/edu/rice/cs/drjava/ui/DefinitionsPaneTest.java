

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Date;


public final class DefinitionsPaneTest extends DrJavaTestCase {
  
  private MainFrame _frame;
  
  
  public void setUp() throws Exception {
    super.setUp();
    DrJava.getConfig().resetToDefaults();
    _frame = new MainFrame();
  }
  
  public void tearDown() throws Exception {
    _frame.dispose();
    _frame = null;
    super.tearDown();
  }
  
  
  public void testShiftBackspace() throws BadLocationException {
    DefinitionsPane definitions = _frame.getCurrentDefPane();
    OpenDefinitionsDocument doc = definitions.getOpenDefDocument();
    _assertDocumentEmpty(doc, "before testing");
    doc.insertString(0, "test", null);
    
    definitions.setCaretPosition(4);
    int shiftBackspaceCode =
      OptionConstants.KEY_DELETE_PREVIOUS.getDefault().getKeyCode();
    
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_PRESSED,
                                             (new Date()).getTime(),
                                             InputEvent.SHIFT_MASK,
                                             shiftBackspaceCode,
                                             KeyEvent.CHAR_UNDEFINED));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_RELEASED,
                                             (new Date()).getTime(),
                                             InputEvent.SHIFT_MASK,
                                             shiftBackspaceCode,
                                             KeyEvent.CHAR_UNDEFINED));
    _assertDocumentContents(doc, "tes", "Did not delete on shift+backspace");
    
    
    int shiftDeleteCode =
      OptionConstants.KEY_DELETE_NEXT.getDefault().getKeyCode();
    definitions.setCaretPosition(1);
    
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_PRESSED,
                                             (new Date()).getTime(),
                                             InputEvent.SHIFT_MASK,
                                             shiftDeleteCode,
                                             KeyEvent.CHAR_UNDEFINED));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_RELEASED,
                                             (new Date()).getTime(),
                                             InputEvent.SHIFT_MASK,
                                             shiftDeleteCode,
                                             KeyEvent.CHAR_UNDEFINED));
    _assertDocumentContents(doc, "ts", "Did not delete on shift+delete");
  }

  
  
  public void testTypeBraceNotInCode() throws BadLocationException {
    DefinitionsPane definitions = _frame.getCurrentDefPane();
    OpenDefinitionsDocument doc = definitions.getOpenDefDocument();
    _assertDocumentEmpty(doc, "before testing");
    doc.insertString(0, "  \"", null);
    
    definitions.setCaretPosition(3);
    
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_TYPED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_UNDEFINED, '{'));
    _assertDocumentContents(doc, "  \"{", "Brace should not indent in a string");
  }
  
  
  
  
  public void testMetaKeyPress() throws BadLocationException {
    DefinitionsPane definitions = _frame.getCurrentDefPane();
    OpenDefinitionsDocument doc = definitions.getOpenDefDocument();
    _assertDocumentEmpty(doc, "point 0");
    
    definitions.processKeyEvent(new KeyEvent(definitions, KeyEvent.KEY_PRESSED, (new Date()).getTime(),
                                             InputEvent.META_MASK, KeyEvent.VK_META, KeyEvent.CHAR_UNDEFINED));
    _assertDocumentEmpty(doc, "point 1");
    definitions.processKeyEvent(new KeyEvent(definitions, KeyEvent.KEY_PRESSED, (new Date()).getTime(),
                                             InputEvent.META_MASK, KeyEvent.VK_W, KeyEvent.CHAR_UNDEFINED));
    _assertDocumentEmpty(doc, "point 2");
    definitions.processKeyEvent(new KeyEvent(definitions, KeyEvent.KEY_TYPED, (new Date()).getTime(),
                                             InputEvent.META_MASK, KeyEvent.VK_UNDEFINED, 'w'));
    _assertDocumentEmpty(doc, "point 3");
    definitions.processKeyEvent(new KeyEvent(definitions, KeyEvent.KEY_RELEASED, (new Date()).getTime(),
                                             InputEvent.META_MASK, KeyEvent.VK_W, KeyEvent.CHAR_UNDEFINED));
    _assertDocumentEmpty(doc, "point 4");
    definitions.processKeyEvent(new KeyEvent(definitions, KeyEvent.KEY_RELEASED, (new Date()).getTime(),
                                             0, KeyEvent.VK_META, KeyEvent.CHAR_UNDEFINED));
    _assertDocumentEmpty(doc, "point 5");
  }
  
  
  public void testMultilineCommentOrUncommentAfterScroll() throws BadLocationException {
    DefinitionsPane pane = _frame.getCurrentDefPane();
    OpenDefinitionsDocument doc = pane.getOpenDefDocument();
    String text =
      "public class stuff {\n" +
      "  private int _int;\n" +
      "  private Bar _bar;\n" +
      "  public void foo() {\n" +
      "    _bar.baz(_int);\n" +
      "  }\n" +
      "}\n";
    
    String commented =
      "//public class stuff {\n" +
      "//  private int _int;\n" +
      "//  private Bar _bar;\n" +
      "//  public void foo() {\n" +
      "//    _bar.baz(_int);\n" +
      "//  }\n" +
      "//}\n";
    
    int newPos = 20;
    
    doc.insertString(0, text, null);
    assertEquals("insertion",text, doc.getText());
    
    
    
    pane.endCompoundEdit();
    doc.commentLines(0,doc.getLength());
    
    assertEquals("commenting",commented, doc.getText());
    int oldPos = pane.getCaretPosition();
    pane.setCaretPosition(newPos);
    doc.getUndoManager().undo();
    assertEquals("undo commenting",text, doc.getText(0,doc.getLength()));
    assertEquals("undoing commenting restores caret position", oldPos, pane.getCaretPosition());
    pane.setCaretPosition(newPos);
    doc.getUndoManager().redo();
    assertEquals("redo commenting",commented, doc.getText(0,doc.getLength()));
    assertEquals("redoing commenting restores caret position", oldPos, pane.getCaretPosition());
    
    
    
    pane.endCompoundEdit();    
    doc.uncommentLines(0,doc.getLength());
    
    assertEquals("uncommenting",text, doc.getText(0,doc.getLength()));
    oldPos = pane.getCaretPosition();
    pane.setCaretPosition(newPos);
    doc.getUndoManager().undo();
    assertEquals("undo uncommenting",commented, doc.getText(0,doc.getLength()));
    assertEquals("undoing uncommenting restores caret position", oldPos, pane.getCaretPosition());
    pane.setCaretPosition(newPos);
    doc.getUndoManager().redo();
    assertEquals("redo uncommenting",text, doc.getText(0,doc.getLength()));
    assertEquals("redoing uncommenting restores caret position", oldPos, pane.getCaretPosition());
  }
  
  protected void _assertDocumentEmpty(DJDocument doc, String message) throws BadLocationException {
    _assertDocumentContents(doc, "", message);
  }
  
  protected void _assertDocumentContents(DJDocument doc, String contents, String message)
    throws BadLocationException {
    assertEquals(message, contents, doc.getText());
  }
  
  public void testGranularUndo() throws BadLocationException {
    DefinitionsPane definitions = _frame.getCurrentDefPane();
    OpenDefinitionsDocument doc = definitions.getOpenDefDocument();
    
    
    
    assertEquals("Should start out empty.", "",
                 doc.getText());
    
    
    
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_PRESSED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_A, KeyEvent.CHAR_UNDEFINED));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_TYPED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_UNDEFINED, 'a'));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_RELEASED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_A, KeyEvent.CHAR_UNDEFINED));
    definitions.setCaretPosition(doc.getLength());
    
    
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_PRESSED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_EXCLAMATION_MARK, KeyEvent.CHAR_UNDEFINED));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_TYPED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_UNDEFINED, '!'));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_RELEASED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_EXCLAMATION_MARK, KeyEvent.CHAR_UNDEFINED));
    definitions.setCaretPosition(doc.getLength());
    
    
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_PRESSED,
                                             (new Date()).getTime(),
                                             InputEvent.SHIFT_MASK,
                                             KeyEvent.VK_B, KeyEvent.CHAR_UNDEFINED));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_TYPED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_UNDEFINED, 'B'));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_RELEASED,
                                             (new Date()).getTime(),
                                             InputEvent.SHIFT_MASK,
                                             KeyEvent.VK_B, KeyEvent.CHAR_UNDEFINED));
    definitions.setCaretPosition(doc.getLength());
    
    
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_PRESSED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_9, KeyEvent.CHAR_UNDEFINED));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_TYPED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_UNDEFINED, '9'));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_RELEASED,
                                             (new Date()).getTime(),
                                             0,
                                             KeyEvent.VK_9, KeyEvent.CHAR_UNDEFINED));
    definitions.setCaretPosition(doc.getLength());
    assertEquals("The text should have been inserted", "a!B9",
                 doc.getText());
    
    
    final KeyStroke ks = DrJava.getConfig().getSetting(OptionConstants.KEY_UNDO);
    final Action a = KeyBindingManager.Singleton.get(ks);
    
    final KeyEvent e = new KeyEvent(definitions,
                                    KeyEvent.KEY_PRESSED,
                                    0,
                                    ks.getModifiers(),
                                    ks.getKeyCode(),
                                    KeyEvent.CHAR_UNDEFINED);
    definitions.processKeyEvent(e);
    
    
    
    
    assertEquals("Should have undone correctly.", "",
                 doc.getText());
    
    
    
    
    
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_PRESSED,
                                             (new Date()).getTime(),
                                             InputEvent.ALT_MASK,
                                             KeyEvent.VK_Q, KeyEvent.CHAR_UNDEFINED));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_TYPED,
                                             (new Date()).getTime(),
                                             InputEvent.ALT_MASK,
                                             KeyEvent.VK_UNDEFINED, 'Q'));
    definitions.processKeyEvent(new KeyEvent(definitions,
                                             KeyEvent.KEY_RELEASED,
                                             (new Date()).getTime(),
                                             InputEvent.ALT_MASK,
                                             KeyEvent.VK_Q, KeyEvent.CHAR_UNDEFINED));
    
    
    SwingUtilities.notifyAction(a, ks, e, e.getSource(), e.getModifiers());
    
    
    
    
    
  }
  
  
  public void testActiveAndInactive() {
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
  }
  
  
  private int _finalCount;
  private int _finalDocCount;
  public void testDocumentPaneMemoryLeak()  throws InterruptedException, java.io.IOException{
    _finalCount = 0;
    _finalDocCount = 0;
    
    
    FinalizationListener<DefinitionsPane> fl = new FinalizationListener<DefinitionsPane>() {
      public void finalized(FinalizationEvent<DefinitionsPane> e) {
        _finalCount++;

      }
    };
    
    FinalizationListener<DefinitionsDocument> fldoc = new FinalizationListener<DefinitionsDocument>() {
      public void finalized(FinalizationEvent<DefinitionsDocument> e) {
        _finalDocCount++;
      }
    };
    
    SingleDisplayModel _model = _frame.getModel();
    _model.newFile().addFinalizationListener(fldoc);
    _frame.getCurrentDefPane().addFinalizationListener(fl);

    _model.newFile().addFinalizationListener(fldoc);
    _frame.getCurrentDefPane().addFinalizationListener(fl);

    _model.newFile().addFinalizationListener(fldoc);
    _frame.getCurrentDefPane().addFinalizationListener(fl);

    _model.newFile().addFinalizationListener(fldoc);
    _frame.getCurrentDefPane().addFinalizationListener(fl);

    _model.newFile().addFinalizationListener(fldoc);
    _frame.getCurrentDefPane().addFinalizationListener(fl);

    _model.newFile().addFinalizationListener(fldoc);
    _frame.getCurrentDefPane().addFinalizationListener(fl);

    
    
    
    _model.closeAllFiles();
    Utilities.clearEventQueue();
    
    System.gc();
    System.runFinalization();

    

    assertEquals("all the defdocs should have been garbage collected", 6, _finalDocCount);
    assertEquals("all the panes should have been garbage collected", 6, _finalCount);


  }
  
  
  
  
  
  public void testFrenchKeyStrokes() throws IOException, InterruptedException {
    
    DefinitionsPane pane = _frame.getCurrentDefPane(); 
    
    KeyEvent ke = new KeyEvent(pane, KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, 'T'); 
    pane.processKeyEvent(ke);
    assertFalse("The KeyEvent for pressing \"T\" should not involve an Alt Key if this fails we are in trouble!", pane.checkAltKey());
    
    ke = new KeyEvent(pane, KeyEvent.KEY_TYPED, 0, InputEvent.ALT_MASK, KeyEvent.VK_UNDEFINED, '{'); 
    pane.processKeyEvent(ke);
    assertTrue("Alt should have been registered and allowed to pass!", pane.checkAltKey());
    
    ke = new KeyEvent(pane, KeyEvent.KEY_TYPED, 0, InputEvent.ALT_MASK, KeyEvent.VK_UNDEFINED, '}'); 
    pane.processKeyEvent(ke);
    assertTrue("Alt should have been registered and allowed to pass!", pane.checkAltKey());
    
    
    ke = new KeyEvent(pane, KeyEvent.KEY_TYPED, 0, InputEvent.ALT_MASK, KeyEvent.VK_UNDEFINED, '['); 
    pane.processKeyEvent(ke);
    assertTrue("Alt should have been registered and allowed to pass!", pane.checkAltKey());
    
    ke = new KeyEvent(pane, KeyEvent.KEY_TYPED, 0, InputEvent.ALT_MASK, KeyEvent.VK_UNDEFINED, ']'); 
    pane.processKeyEvent(ke);
    assertTrue("Alt should have been registered and allowed to pass!", pane.checkAltKey());
  } 





  public void testBackspace() throws BadLocationException {
    DefinitionsPane definitions = _frame.getCurrentDefPane();
    OpenDefinitionsDocument doc = definitions.getOpenDefDocument();
    _assertDocumentEmpty(doc, "before testing");
    doc.insertString(0, "test", null);
    
    definitions.setCaretPosition(4);
    int backspaceCode = KeyEvent.VK_BACK_SPACE;
     
     definitions.processKeyEvent(new KeyEvent(definitions,
                                              KeyEvent.KEY_PRESSED,
                                              (new Date()).getTime(),
                                              0,
                                              backspaceCode,
                                              KeyEvent.CHAR_UNDEFINED));
     definitions.processKeyEvent(new KeyEvent(definitions,
                                              KeyEvent.KEY_RELEASED,
                                              (new Date()).getTime(),
                                              0,
                                              backspaceCode,
                                              KeyEvent.CHAR_UNDEFINED));
     definitions.processKeyEvent(new KeyEvent(definitions,
                                              KeyEvent.KEY_TYPED,
                                              (new Date()).getTime(),
                                              0,
                                              KeyEvent.VK_UNDEFINED,
                                              '\b'));
     _assertDocumentContents(doc, "tes", "Deleting with Backspace went wrong");
  }
  
  
  
  public void testMatchBraceText() {
    try{
      DefinitionsPane definitions = _frame.getCurrentDefPane();
      OpenDefinitionsDocument doc = definitions.getOpenDefDocument();
      _assertDocumentEmpty(doc, "before testing");
      doc.insertString(0, 
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
      
      String fileName = GlobalModelNaming.getDisplayFullPath(doc);
      
      definitions.setCaretPosition(4);
      assertEquals("Should display the document path", fileName, _frame.getFileNameField());
      definitions.setCaretPosition(115);
      assertEquals("Should display the line matched", "Matches:      new Object() {", _frame.getFileNameField());
      definitions.setCaretPosition(102);
      assertEquals("Should display the document matched", fileName, _frame.getFileNameField());
      definitions.setCaretPosition(119);
      assertEquals("Should display the line matched", "Matches:   private void _method()...{", _frame.getFileNameField());
      definitions.setCaretPosition(121);
      assertEquals("Should display the line matched", "Matches: public class Foo {", _frame.getFileNameField());
      definitions.setCaretPosition(122);
      assertEquals("Should display only one brace when matching an open brace that is the first character in a line",
                   "Matches: {", _frame.getFileNameField());
    }
    catch (BadLocationException e) {throw new UnexpectedException(e);}
  }


  class KeyTestListener implements KeyListener {
    
    public void keyPressed(KeyEvent e) {
      DefinitionsPaneTest.fail("Unexpected keypress " + e);
    }
    
    public void keyReleased(KeyEvent e) {
      DefinitionsPaneTest.fail("Unexpected keyrelease " + e);
    }
    
    public void keyTyped(KeyEvent e) {
      DefinitionsPaneTest.fail("Unexpected keytyped " + e);
    }
    
    public boolean done() {
      return true;
    }
  }
}

