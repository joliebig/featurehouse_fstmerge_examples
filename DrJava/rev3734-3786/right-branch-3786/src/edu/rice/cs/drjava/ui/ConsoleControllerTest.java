

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.model.GlobalModelTestCase;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.text.EditDocumentException;


public final class ConsoleControllerTest extends GlobalModelTestCase {
  
  protected InteractionsDJDocument _adapter;

  
  protected ConsoleDocument _doc;

  
  protected InteractionsPane _pane;

  
  protected ConsoleController _controller;

  
  protected Object _lock;

  
  public void setUp() throws Exception {
    super.setUp();
    _adapter = _model.getSwingConsoleDocument();
    _doc = _model.getConsoleDocument();
    _controller = new TestConsoleController(_doc, _adapter);
    _pane = _controller.getPane();
    _model.getInteractionsModel().setInputListener(_controller.getInputListener());
    _lock = _controller.getInputWaitObject();  
  }

  
  public void tearDown() throws Exception {
    _adapter = null;
    _doc = null;
    _controller = null;
    _pane = null;
    super.tearDown();
  }

  
  public void testBasicConsoleInput()
    throws EditDocumentException, InterruptedException {
    Thread inputGenerator = new InputGeneratorThread("a");
    String result;
    synchronized(_lock) {
      inputGenerator.start();
      _lock.wait();  
      
    }
    result = interpret("System.in.read()");

    String expected = 
      String.valueOf((int) 'a');
    assertEquals("read() returns the correct character", expected, result);
    result = interpret("System.in.read()");
    assertEquals("second read() should get the end-of-line character",
                 String.valueOf((int) System.getProperty("line.separator").charAt(0)), result);
  }

  
  protected class InputGeneratorThread extends Thread {
    private String _text;
    
    public InputGeneratorThread(String text) {
      _text = text;
    }
    public void run() {
      try {
        synchronized(_lock) {
          _lock.notify(); 
          _lock.wait();   
          
          _doc.insertText(_doc.getLength(), _text, ConsoleDocument.DEFAULT_STYLE);
          _controller.enterAction.actionPerformed(null);
        }
      }
      catch (Exception e) {
        listenerFail("InputGenerator failed:  " + e);
      }
    }
  }

  
  protected class TestConsoleController extends ConsoleController {
    public TestConsoleController(ConsoleDocument doc, InteractionsDJDocument adapter) {
      super(doc, adapter);
    }

    protected void _waitForInput() {
      synchronized(_lock) {
        _lock.notify();  
        super._waitForInput();
      }
    }
  }
}
