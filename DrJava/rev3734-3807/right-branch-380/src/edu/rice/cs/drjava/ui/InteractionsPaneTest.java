

package edu.rice.cs.drjava.ui;


import java.io.File;
import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.FileOption;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.DummyGlobalModelListener;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDocumentTest.TestBeep;
import edu.rice.cs.drjava.model.repl.InteractionsModel;
import edu.rice.cs.drjava.model.repl.InteractionsModelTest.TestInteractionsModel;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.CompletionMonitor;


public final class InteractionsPaneTest extends DrJavaTestCase {

  protected InteractionsDJDocument _adapter;
  protected InteractionsModel _model;
  protected InteractionsDocument _doc;
  protected InteractionsPane _pane;
  protected InteractionsController _controller;

  
  public void setUp() throws Exception {
    super.setUp();
    _adapter = new InteractionsDJDocument();
    _model = new TestInteractionsModel(_adapter);
    _doc = _model.getDocument();
    _pane = new InteractionsPane(_adapter) {
      public int getPromptPos() {
       return _model.getDocument().getPromptPos();
      }
    };
    
    _pane.setBeep(new TestBeep());
    _controller = new InteractionsController(_model, _adapter, _pane);

  }

  public void tearDown() throws Exception {
    _controller = null;
    _doc = null;
    _model = null;
    _pane = null;
    _adapter = null;
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

    _doc.append("simulated output", InteractionsDocument.DEFAULT_STYLE);
    Utilities.clearEventQueue();
    _doc.setInProgress(false);


    assertEquals("Caret is at the end after output while in progress.",
                 _doc.getLength(),
                 _pane.getCaretPosition());
  }

  
  public void testCaretMovesUpToPromptAfterInsert() throws EditDocumentException {
    _doc.append("typed text", InteractionsDocument.DEFAULT_STYLE);
    Utilities.invokeAndWait(new Runnable() { public void run() { _pane.setCaretPosition(1); } });

    _doc.insertBeforeLastPrompt("simulated output", InteractionsDocument.DEFAULT_STYLE);

    Utilities.clearEventQueue();

    assertEquals("Caret is at the prompt after output inserted.", _doc.getPromptPos(), _pane.getCaretPosition());

    _doc.insertPrompt();
    Utilities.invokeAndWait(new Runnable() { public void run() { _pane.setCaretPosition(1); } });
    _doc.insertBeforeLastPrompt("simulated output", InteractionsDocument.DEFAULT_STYLE);
    Utilities.clearEventQueue();
    assertEquals("Caret is at the end after output inserted.", _doc.getPromptPos(), _pane.getCaretPosition());
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
    _doc.modifyLock();
    int origLength = 0;
    try {
      origLength = _doc.getLength();
      _doc.insertText(1, "typed text", InteractionsDocument.DEFAULT_STYLE);
    }
    finally { _doc.modifyUnlock(); }
    assertEquals("Document should not have changed.", origLength, _doc.getLength());
  }

  
  public void testCaretUpdatedOnInsert() throws EditDocumentException {
    _doc.append("typed text", InteractionsDocument.DEFAULT_STYLE);
    final int pos = _doc.getLength() - 5;
    Utilities.invokeAndWait(new Runnable() { public void run() { _pane.setCaretPosition(pos); } });

    
    _doc.insertBeforeLastPrompt("aa", InteractionsDocument.DEFAULT_STYLE);
     Utilities.clearEventQueue();
    assertEquals("caret should be in correct position", pos + 2, _pane.getCaretPosition());

    
    Utilities.invokeAndWait(new Runnable() { public void run() { _pane.setCaretPosition(_doc.getPromptPos()); } });
    _doc.insertBeforeLastPrompt("b", InteractionsDocument.DEFAULT_STYLE);
    Utilities.clearEventQueue();
    assertEquals("caret should be at prompt", _doc.getPromptPos(), _pane.getCaretPosition());

    
    Utilities.invokeAndWait(new Runnable() { public void run() { _pane.setCaretPosition(0); } });
    _doc.insertBeforeLastPrompt("ccc", InteractionsDocument.DEFAULT_STYLE);
    Utilities.clearEventQueue();
    assertEquals("caret should be at prompt", _doc.getPromptPos(), _pane.getCaretPosition());

    
    final int newPos = _doc.getPromptPos();
    
    Utilities.invokeAndWait(new Runnable() { public void run() { _pane.setCaretPosition(newPos+1); } });
    _doc.insertText(newPos, "d", InteractionsDocument.DEFAULT_STYLE);
    Utilities.clearEventQueue();
    assertEquals("caret should be immediately after the d", newPos + 1, _pane.getCaretPosition());
  }
































  
  public void testSystemIn() {
    final Object bufLock = new Object();
    final StringBuffer buf = new StringBuffer();
    
    final CompletionMonitor completionMonitor = new CompletionMonitor();
    
    _controller.addConsoleStateListener(new InteractionsController.ConsoleStateListener() {
      public void consoleInputStarted(InteractionsController c) {
        completionMonitor.set();
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
    
    
    completionMonitor.waitOne();
        
    _controller.insertConsoleText("test-text");
    _controller.interruptConsoleInput();
    
    
    synchronized(bufLock) {
      assertEquals("Should have returned the correct text.", "test-text\n", buf.toString());
    }
  }
  
  
  private int _firstPrompt, _secondPrompt, _size;
  private boolean _resetDone;
  
  public void testPromptListClearedOnReset() throws Exception {
    
    MainFrame mf = new MainFrame();
    
    final Object _resetLock = new Object();
    
    Utilities.clearEventQueue();
    GlobalModel gm = mf.getModel();
    _controller = mf.getInteractionsController();
    _model = gm.getInteractionsModel();
    _adapter = gm.getSwingInteractionsDocument();
    _doc = gm.getInteractionsDocument();
    _pane = mf.getInteractionsPane();
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _pane.resetPrompts(); }
    });


    assertEquals("PromptList before insert should contain 0 elements", 0, _pane.getPromptList().size());
        
    
    
    _doc.append("5", InteractionsDocument.NUMBER_RETURN_STYLE);
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _pane.setCaretPosition(_doc.getLength()); }
    });
    
    assertEquals("PromptList after insert should contain 1 element", 1, _pane.getPromptList().size());    
    assertEquals("First prompt should be saved as being at position",
                 InteractionsModel.getStartUpBanner().length() + InteractionsDocument.DEFAULT_PROMPT.length(),
                 (int)_pane.getPromptList().get(0)); 
    
    _doc.insertPrompt();
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { 
        _pane.setCaretPosition(_doc.getLength());
        _firstPrompt = (int) _pane.getPromptList().get(0); 
        _secondPrompt = (int) _pane.getPromptList().get(1); 
      }
    });
    
    assertEquals("PromptList after insertion of new prompt should contain 2 elements", 2, _pane.getPromptList().size());
    assertEquals("First prompt should be saved as being at position",
                 InteractionsModel.getStartUpBanner().length() + InteractionsDocument.DEFAULT_PROMPT.length(),
                 _firstPrompt); 
    assertEquals("Second prompt should be saved as being at position",
                 InteractionsModel.getStartUpBanner().length() + InteractionsDocument.DEFAULT_PROMPT.length() * 2 + 1,
                 _secondPrompt); 
    
    synchronized(_resetLock) { _resetDone = false; }
    _model.addListener(new DummyGlobalModelListener() {
      public void interpreterReady(File wd) {
        synchronized(_resetLock) {
          _resetDone = true;
          _resetLock.notifyAll();
        }
      }});
      
    _model.resetInterpreter(FileOption.NULL_FILE);
 
    
    synchronized(_resetLock) { while (! _resetDone) _resetLock.wait(); }
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() { _size = _pane.getPromptList().size(); }
    });
    

    
    assertEquals("PromptList after reset should contain one element", 1, _size);
  }
    
}
