

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.DummyGlobalModelListener;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.ui.InteractionsPane;
import edu.rice.cs.drjava.ui.MainFrame;
import edu.rice.cs.util.text.EditDocumentException;

import java.io.File;


public final class InteractionsDJDocumentTest extends DrJavaTestCase {

  protected InteractionsDJDocument _adapter;
  protected InteractionsModel _model;
  protected InteractionsDocument _doc;
  protected InteractionsPane _pane;
  protected MainFrame mf;
  
  
  protected void setUp() throws Exception {
    super.setUp();
    mf = new MainFrame();
    GlobalModel gm = mf.getModel();
    _model = gm.getInteractionsModel();
    _adapter = gm.getSwingInteractionsDocument();
    _doc = gm.getInteractionsDocument();
  }
  
  private boolean _interpreterRestarted = false;
  
  
  public void testStylesListContentAndReset() throws EditDocumentException, InterruptedException {
    
    
    final Object _restartLock = new Object();
    
    assertEquals("StylesList before insert should contain 2 pairs", 2, _adapter.getStylesList().size());
    
    int blen = InteractionsModel.getStartUpBanner().length();
    
    
    String styleElt1 = "((0, " + blen + "), object.return.style)";
    String styleElt2 = "((" + blen + ", " + (blen + 2) + "), default)";

    assertEquals("The first element pushed on StylesList before insertion should be", styleElt1,
                 _adapter.getStylesList().get(1).toString());
    assertEquals("The second element pushed on StylesList before insertion should be", styleElt2,
                 _adapter.getStylesList().get(0).toString());
    
    
    _doc.insertText(_doc.getLength(), "5", InteractionsDocument.NUMBER_RETURN_STYLE);
    
    
    String styleElt3 = "((" + (blen + 2) + ", " + (blen + 3) + "), number.return.style)";

    assertEquals("StylesList before reset should contain 3 pairs", 3, _adapter.getStylesList().size());
    
    assertEquals("The first element pushed on StylesList before reset should be", styleElt1,
                 _adapter.getStylesList().get(2).toString());
    assertEquals("The second element pushed on StylesList before reset should be", styleElt2,
                 _adapter.getStylesList().get(1).toString());
    assertEquals("The last element pushed on StylesList before reset should be", styleElt3,
                 _adapter.getStylesList().get(0).toString());
    



    
    

    InteractionsListener restartCommand = new DummyGlobalModelListener() {
      public void interpreterReady(File wd) {
        synchronized (_restartLock) {
          _interpreterRestarted = true;
          _restartLock.notify();
        }
      }};
    _model.addListener(restartCommand);
                                   
    
    _model.setWaitingForFirstInterpreter(false);
    
    synchronized(_restartLock) { _interpreterRestarted = false; }
      
    
    File f = new File(System.getProperty("user.dir"));
    _model.resetInterpreter(f);  

    
    synchronized(_restartLock) { while (! _interpreterRestarted) _restartLock.wait(); }
    _model.removeListener(restartCommand);
    



   
    assertEquals("StylesList after reset should contain 2 pairs", 2, _adapter.getStylesList().size());
    
    assertEquals("The first element pushed on StylesList after reset should be", styleElt1,
                 _adapter.getStylesList().get(1).toString());
    assertEquals("The second element pushed on StylesList after reset should be", styleElt2,
                 _adapter.getStylesList().get(0).toString());
    
    

  }

  
  public void testCannotAddNullStyleToList() throws EditDocumentException {
    
    assertEquals("StylesList before insert should contain 2 pairs",
                 2, _adapter.getStylesList().size());

    
    _doc.insertText(_doc.getLength(), "5", InteractionsDocument.NUMBER_RETURN_STYLE);

    assertEquals("StylesList should contain 3 pairs",
                 3, _adapter.getStylesList().size());

    
    _doc.insertText(_doc.getLength(), "6", null);

    assertEquals("StylesList should still contain 3 pairs - null string should not have been inserted",
                 3, _adapter.getStylesList().size());
  }
}