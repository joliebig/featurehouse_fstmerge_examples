

package edu.rice.cs.drjava.ui;


import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDocumentTest.TestBeep;
import edu.rice.cs.drjava.model.repl.InteractionsModel;
import edu.rice.cs.drjava.model.repl.InteractionsModelTest.TestInteractionsModel;
import edu.rice.cs.drjava.ui.InteractionsController;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.plt.concurrent.CompletionMonitor;
import java.util.Date;


public final class InteractionsPaneTest extends DrJavaTestCase {
  
  private static final char UNDEFINED = KeyEvent.CHAR_UNDEFINED;
  private static final int PRESSED = KeyEvent.KEY_PRESSED;
  private static final int RELEASED = KeyEvent.KEY_RELEASED;
  private static final int SHIFT = InputEvent.SHIFT_MASK;
  private static final int TYPED = KeyEvent.KEY_TYPED;
  private static final int VK_UNDEF = KeyEvent.VK_UNDEFINED;
  
  protected volatile InteractionsDJDocument _adapter;
  protected volatile InteractionsModel _model;
  protected volatile InteractionsDocument _doc;
  protected volatile InteractionsPane _pane;
  protected volatile InteractionsController _controller;
  
  
  public void setUp() throws Exception {
    super.setUp();
    _adapter = new InteractionsDJDocument();
    _model = new TestInteractionsModel(_adapter);
    _doc = _model.getDocument();
    _pane = new InteractionsPane(_adapter) {
      public int getPromptPos() { return _model.getDocument().getPromptPos(); }
    };
    
    _pane.setBeep(new TestBeep());
    _controller = new InteractionsController(_model, _adapter, _pane, new Runnable() { public void run() { } });



  }
  
  public void tearDown() throws Exception {





    super.tearDown();
  }
  
  
  public void testInitialPosition() {
    assertEquals("Initial caret not in the correct position.", _pane.getCaretPosition(), _doc.getPromptPos());
  }
  
  
  public void testCaretMovementCyclesWhenAtPrompt() throws EditDocumentException {
    _doc.append("test text", InteractionsDocument.DEFAULT_STYLE);
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        _controller.moveToPrompt();
        _controller.moveLeftAction.actionPerformed(null);
      }
    });
    assertEquals("Caret was not cycled when moved left at the prompt.", _doc.getLength(), _pane.getCaretPosition());
  }
  
  
  public void testCaretMovementCyclesWhenAtEnd() throws EditDocumentException {
    _doc.append("test text", InteractionsDocument.DEFAULT_STYLE);
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        _controller.moveToEnd();
        _controller.moveRightAction.actionPerformed(null);
      }
    });
    assertEquals("Caret was not cycled when moved right at the end.", _doc.getPromptPos(), _pane.getCaretPosition());
  }
  
  
  public void testLeftBeforePromptMovesToPrompt() {
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        _pane.setCaretPosition(1);
        _controller.moveLeftAction.actionPerformed(null);
      }
    });
    assertEquals("Left arrow doesn't move to prompt when caret is before prompt.",
                 _doc.getPromptPos(),
                 _pane.getCaretPosition());
  }
  
  
  public void testRightBeforePromptMovesToEnd() {
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        _pane.setCaretPosition(1);
        _controller.moveRightAction.actionPerformed(null);
      }
    });
    assertEquals("Right arrow doesn't move to end when caret is before prompt.",
                 _doc.getLength(),
                 _pane.getCaretPosition());
  }
  
  
  public void testHistoryRecallPrevMovesToEnd() {
    Utilities.invokeAndWait(new Runnable() {  
      public void run() {
        _pane.setCaretPosition(1);
        _controller.historyPrevAction.actionPerformed(null);
      }
    });
    assertEquals("Caret not moved to end on up arrow.", _doc.getLength(), _pane.getCaretPosition());
  }
  
  
  public void testHistoryRecallNextMovesToEnd() {
    Utilities.invokeAndWait(new Runnable() { 
      public void run() {
        _pane.setCaretPosition(1);
        _controller.historyNextAction.actionPerformed(null);
      }
    });
    assertEquals("Caret not moved to end on down arrow.", _doc.getLength(), _pane.getCaretPosition());
  }
  
  public void testCaretStaysAtEndDuringInteraction() throws EditDocumentException {


    _doc.setInProgress(true);

    _model.replSystemOutPrint("simulated output");
    Utilities.clearEventQueue();
    _doc.setInProgress(false);



    assertEquals("Caret is at the end after output while in progress.", _doc.getLength(), _pane.getCaretPosition());
  }
  
  
  public void testCaretMovesUpToPromptAfterInsert() throws EditDocumentException {
    _model.replSystemOutPrint("typed text");


    _model.replSystemOutPrint("simulated output");
    Utilities.clearEventQueue();
    assertEquals("Caret is at the prompt after output inserted.", _doc.getPromptPos(), _pane.getCaretPosition());
  }
  
  
  public void testClearCurrentInteraction() throws EditDocumentException {
    _doc.append("typed text", InteractionsDocument.DEFAULT_STYLE);
    Utilities.invokeAndWait(new Runnable() { public void run() { _controller.moveToEnd(); } });
    
    _doc.clearCurrentInteraction();
    Utilities.clearEventQueue();
    assertEquals("Caret is at the prompt after output cleared.", _doc.getPromptPos(), _pane.getCaretPosition());
    assertEquals("Prompt is at the end after output cleared.", _doc.getLength(), _doc.getPromptPos());
  }
  
  
  public void testCannotEditBeforePrompt() throws EditDocumentException {
    int origLength = _doc.getLength();
    _doc.insertText(1, "typed text", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("Document should not have changed.", origLength, _doc.getLength());
  }
  
  
  public void testCaretUpdatedOnInsert() throws EditDocumentException {
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        
        
        _pane.processKeyEvent(new KeyEvent(_pane, PRESSED, (new Date()).getTime(), SHIFT, KeyEvent.VK_T, UNDEFINED));
        _pane.processKeyEvent(new KeyEvent(_pane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, 'T'));
        _pane.processKeyEvent(new KeyEvent(_pane, RELEASED, (new Date()).getTime(), SHIFT, KeyEvent.VK_T, UNDEFINED));
        
        
        _pane.processKeyEvent(new KeyEvent(_pane, PRESSED, (new Date()).getTime(), SHIFT, KeyEvent.VK_Y, UNDEFINED));
        _pane.processKeyEvent(new KeyEvent(_pane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, 'Y'));
        _pane.processKeyEvent(new KeyEvent(_pane, RELEASED, (new Date()).getTime(), SHIFT, KeyEvent.VK_Y, UNDEFINED));
        
        
        _pane.processKeyEvent(new KeyEvent(_pane, PRESSED, (new Date()).getTime(), SHIFT, KeyEvent.VK_P, UNDEFINED));
        _pane.processKeyEvent(new KeyEvent(_pane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, 'P'));
        _pane.processKeyEvent(new KeyEvent(_pane, RELEASED, (new Date()).getTime(), SHIFT, KeyEvent.VK_P, UNDEFINED));
        
        
        _pane.processKeyEvent(new KeyEvent(_pane, PRESSED, (new Date()).getTime(), SHIFT, KeyEvent.VK_E, UNDEFINED));
        _pane.processKeyEvent(new KeyEvent(_pane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, 'E'));
        _pane.processKeyEvent(new KeyEvent(_pane, RELEASED, (new Date()).getTime(), SHIFT, KeyEvent.VK_E, UNDEFINED));
        
        
        _pane.processKeyEvent(new KeyEvent(_pane, PRESSED, (new Date()).getTime(), SHIFT, KeyEvent.VK_D, UNDEFINED));
        _pane.processKeyEvent(new KeyEvent(_pane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, 'D'));
        _pane.processKeyEvent(new KeyEvent(_pane, RELEASED, (new Date()).getTime(), SHIFT, KeyEvent.VK_D, UNDEFINED));
      }
    });

    Utilities.clearEventQueue();
    Utilities.clearEventQueue();

    assertEquals("caret should be at end of document", _doc.getLength(), _pane.getCaretPosition());
    
    final int pos = _doc.getLength() - 5;
    Utilities.invokeAndWait(new Runnable() { public void run() { _pane.setCaretPosition(pos); } });



    
    
    _model.replSystemErrPrint("aa");
    Utilities.clearEventQueue();



    assertEquals("caret should be in correct position", pos + 2, _pane.getCaretPosition());
    
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _pane.setCaretPosition(_doc.getPromptPos()); } });
    _model.replSystemOutPrint("b");
    Utilities.clearEventQueue();
    assertEquals("caret should be at prompt", _doc.getPromptPos(), _pane.getCaretPosition());
    
    _model.replSystemErrPrint("ccc");
    Utilities.clearEventQueue();

    assertEquals("caret should be at prompt", _doc.getPromptPos(), _pane.getCaretPosition());
    
    
    final int newPos = _doc.getPromptPos();
    
    _pane.setCaretPosition(newPos + 1);

    Utilities.invokeAndWait(new Runnable() { 
      public void run() { 
        
        _pane.processKeyEvent(new KeyEvent(_pane, PRESSED, (new Date()).getTime(), SHIFT, KeyEvent.VK_D, UNDEFINED));
        _pane.processKeyEvent(new KeyEvent(_pane, TYPED, (new Date()).getTime(), 0, VK_UNDEF, 'D'));
        _pane.processKeyEvent(new KeyEvent(_pane, RELEASED, (new Date()).getTime(), SHIFT, KeyEvent.VK_D, UNDEFINED));
      } 
    });
    Utilities.clearEventQueue();
    assertEquals("caret should be one char after the inserted D", newPos + 2, _pane.getCaretPosition());
  }
  
  public void testSystemIn_NOJOIN() {
    final Object bufLock = new Object();
    final StringBuilder buf = new StringBuilder();
    
    final CompletionMonitor completionMonitor = new CompletionMonitor();
    
    _controller.addConsoleStateListener(new InteractionsController.ConsoleStateListener() {
      public void consoleInputStarted(InteractionsController c) {
        completionMonitor.signal();
      }     
      public void consoleInputCompleted(String text, InteractionsController c) {
        
        
      }
    });
    
    
    new Thread("Testing System.in") {
      public void run() {
        synchronized(bufLock) {
          String s = _controller.getInputListener().getConsoleInput();
          buf.append(s);
        }
      }
    }.start();
    
    
    completionMonitor.attemptEnsureSignaled();
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { 
        _controller.insertConsoleText("test-text"); 
        _controller.interruptConsoleInput();
      }
    });
    
    
    synchronized(bufLock) {
      
      assertEquals("Should have returned the correct text.", "test-text", buf.toString());
    }
  }
  
  



















































































  
}
