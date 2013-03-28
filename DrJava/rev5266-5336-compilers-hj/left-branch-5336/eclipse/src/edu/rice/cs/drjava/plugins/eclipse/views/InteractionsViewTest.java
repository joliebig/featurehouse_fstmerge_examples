

package edu.rice.cs.drjava.plugins.eclipse.views;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.swt.SWT;

import edu.rice.cs.drjava.plugins.eclipse.repl.EclipseInteractionsModel;

import edu.rice.cs.drjava.model.repl.*;


import edu.rice.cs.drjava.plugins.eclipse.util.text.SWTDocumentAdapter;
import edu.rice.cs.util.text.EditDocumentException;

import junit.framework.*;



public class InteractionsViewTest extends TestCase {
  
  protected Display _display;
  protected Shell _shell;
  
  protected StyledText _text;
  protected SWTDocumentAdapter _adapter;
  protected EclipseInteractionsModel _model;
  protected InteractionsDocument _doc;
  protected InteractionsView _view;
  protected InteractionsController _controller;
  
  
  
  public InteractionsViewTest(String name) {
    super(name);
  }
  
  
  public static Test suite() {
    return new TestSuite(InteractionsViewTest.class);
  }
  
  
  
  public void setUp() {
    _display = new Display();
    _shell = new Shell(_display, SWT.TITLE | SWT.CLOSE);
    
    _text = new StyledText(_shell, SWT.WRAP | SWT.V_SCROLL);
    _adapter = new SWTDocumentAdapter(_text);
    _view = new InteractionsView();
    _view.setTextPane(_text);
    
    

    _model = new EclipseInteractionsModel(_adapter);
    _controller = new InteractionsController(_model, _adapter, _view);
    _view.setController(_controller);
    _doc = _model.getDocument();
  }
  
  public void tearDown() {
    _controller.dispose();
    _controller = null;
    _doc = null;
    _adapter = null;
    _model = null;
    _text = null;
    
    _view = null;
    _shell.dispose();
    _shell = null;
    _display.dispose();
    _display = null;
    System.gc();
  }
  
  
  public void testInitialPosition() {
    assertEquals("Initial caret not in the correct position.",
                 _text.getCaretOffset(),
                 _doc.getPromptPos());
  }
  
  
  public void testCaretMovementCyclesWhenAtPrompt() throws EditDocumentException {
    _doc.insertText(_doc.getLength(), "test text", InteractionsDocument.DEFAULT_STYLE);
    _controller.moveToPrompt();
    
    _controller.moveLeftAction();
    assertEquals("Caret was not cycled when moved left at the prompt.",
                 _doc.getLength(),
                 _text.getCaretOffset());
  }
  
  
  public void testCaretMovementCyclesWhenAtEnd() throws EditDocumentException {
    _doc.insertText(_doc.getLength(), "test text", InteractionsDocument.DEFAULT_STYLE);
    _controller.moveToEnd();
    
    _controller.moveRightAction();
    assertEquals("Caret was not cycled when moved right at the end.",
                 _doc.getPromptPos(),
                 _text.getCaretOffset());
  }

  
  public void testLeftBeforePromptMovesToPrompt() {
    _text.setCaretOffset(1);
    _controller.moveLeftAction();
    assertEquals("Left arrow doesn't move to prompt when caret is before prompt.",
                 _doc.getPromptPos(),
                 _text.getCaretOffset());
  }
  
  
  public void testRightBeforePromptMovesToEnd() {
    _text.setCaretOffset(1);
    _controller.moveRightAction();
    assertEquals("Right arrow doesn't move to end when caret is before prompt.",
                 _doc.getLength(),
                 _text.getCaretOffset());
  }
  
  
  public void testHistoryRecallPrevMovesToEnd() {
    _text.setCaretOffset(1);
    _controller.historyPrevAction();
    assertEquals("Caret not moved to end on up arrow.",
                 _doc.getLength(),
                 _text.getCaretOffset());
  }
  
  
  public void testHistoryRecallNextMovesToEnd() {
    _text.setCaretOffset(1);
    _controller.historyNextAction();
    assertEquals("Caret not moved to end on down arrow.",
                 _doc.getLength(),
                 _text.getCaretOffset());
  }
  
  public void testCaretStaysAtEndDuringInteraction() throws EditDocumentException {
    _doc.setInProgress(true);
    _doc.insertText(_doc.getLength(), "simulated output", InteractionsDocument.DEFAULT_STYLE);
    _doc.setInProgress(false);
    assertEquals("Caret is at the end after output while in progress.",
                 _doc.getLength(),
                 _text.getCaretOffset());
  }
  
  
  public void testCaretMovesUpToPromptAfterInsert() throws EditDocumentException {
    _doc.insertText(_doc.getLength(), "typed text", InteractionsDocument.DEFAULT_STYLE);
    _text.setCaretOffset(1);
    _doc.insertBeforeLastPrompt("simulated output", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("Caret is at the prompt after output inserted.",
                 _doc.getPromptPos(),
                 _text.getCaretOffset());
    
    _doc.insertPrompt();
    _text.setCaretOffset(1);
    _doc.insertBeforeLastPrompt("simulated output", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("Caret is at the end after output inserted.",
                 _doc.getPromptPos(),
                 _text.getCaretOffset());
  }
  
  
  public void testClearCurrentInteraction() throws EditDocumentException {
    _doc.insertText(_doc.getLength(), "typed text", InteractionsDocument.DEFAULT_STYLE);
    _controller.moveToEnd();
    
    _doc.clearCurrentInteraction();
    assertEquals("Caret is at the prompt after output cleared.",
                 _doc.getPromptPos(),
                 _text.getCaretOffset());
    assertEquals("Prompt is at the end after output cleared.",
                 _doc.getLength(),
                 _doc.getPromptPos());
  }
  
  
  public void testCannotEditBeforePrompt() throws EditDocumentException {
    int origLength = _doc.getLength();
    _doc.insertText(1, "typed text", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("Document should not have changed.",
                 origLength,
                 _doc.getLength());
  }
  
  
  public void testCaretUpdatedOnInsert() throws EditDocumentException {
    _doc.insertText(_doc.getLength(), "typed text",
                    InteractionsDocument.DEFAULT_STYLE);
    int pos = _doc.getLength() - 5;
    _text.setCaretOffset(pos);
    
    
    _doc.insertBeforeLastPrompt("aa", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("caret should be in correct position",
                 pos + 2, _text.getCaretOffset());
    
    
    _text.setCaretOffset(_doc.getPromptPos());
    _doc.insertBeforeLastPrompt("b", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("caret should be at prompt",
                 _doc.getPromptPos(), _text.getCaretOffset());
    
    
    _text.setCaretOffset(0);
    _doc.insertBeforeLastPrompt("ccc", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("caret should be at prompt",
                 _doc.getPromptPos(), _text.getCaretOffset());
  }
}
