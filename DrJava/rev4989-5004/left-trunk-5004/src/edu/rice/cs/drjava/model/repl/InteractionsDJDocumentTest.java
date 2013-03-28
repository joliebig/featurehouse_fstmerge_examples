

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.ui.MainFrame;
import edu.rice.cs.util.text.EditDocumentException;
import edu.rice.cs.util.swing.Utilities;

import java.io.File;


public final class InteractionsDJDocumentTest extends DrJavaTestCase {

  protected InteractionsDJDocument _adapter;
  protected InteractionsModel _model;
  protected InteractionsDocument _doc;
  protected MainFrame mf;
  
  
  protected void setUp() throws Exception {
    super.setUp();
    
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        mf = new MainFrame();
        GlobalModel gm = mf.getModel();
        _model = gm.getInteractionsModel();
        _adapter = gm.getSwingInteractionsDocument();
        _doc = gm.getInteractionsDocument();
        assert _model._pane != null;  
      }
    });
  }
  
  private boolean _interpreterRestarted = false;
  
  public void test1() {
    try { helpTestStylesListContentAndReset(); }
    catch(Throwable t) { t.printStackTrace(); }
  }
  
  
  public void helpTestStylesListContentAndReset() throws EditDocumentException, InterruptedException {

    
    
    final Object _restartLock = new Object();
    
    assertEquals("StylesList before insert should contain 2 pairs", 2, _adapter.getStyles().length);

    
    int blen = _model.getStartUpBanner().length();




    
    
    String styleElt1 = "((0, " + blen + "), object.return.style)";
    String styleElt2 = "((" + blen + ", " + (blen + 2) + "), default)";

    assertEquals("The first element pushed on StylesList before insertion should be", styleElt1,
                 _adapter.getStyles()[1].toString());
    assertEquals("The second element pushed on StylesList before insertion should be", styleElt2,
                 _adapter.getStyles()[0].toString());
    
    
    _doc.append("5", InteractionsDocument.NUMBER_RETURN_STYLE);
    
    
    String styleElt3 = "((" + (blen + 2) + ", " + (blen + 3) + "), number.return.style)";

    assertEquals("StylesList before reset should contain 3 pairs", 3, _adapter.getStyles().length);
    
    assertEquals("The first element pushed on StylesList before reset should be", styleElt1,
                 _adapter.getStyles()[2].toString());
    assertEquals("The second element pushed on StylesList before reset should be", styleElt2,
                 _adapter.getStyles()[1].toString());
    assertEquals("The last element pushed on StylesList before reset should be", styleElt3,
                 _adapter.getStyles()[0].toString());
    



    
    


    InteractionsListener restartCommand = new DummyInteractionsListener() {
      public void interpreterReady(File wd) {
        synchronized(_restartLock) {
          _interpreterRestarted = true;
          _restartLock.notify();
        }
      }};
    _model.addListener(restartCommand);
                                   
    
    _model.setWaitingForFirstInterpreter(false);
    
    synchronized(_restartLock) { _interpreterRestarted = false; }
      
    
    File f = _model.getWorkingDirectory();
    _model.resetInterpreter(f, true);
    


    
    synchronized(_restartLock) { while (! _interpreterRestarted) _restartLock.wait(); }
    _model.removeListener(restartCommand);
    



   
    Utilities.clearEventQueue();  
    assertEquals("StylesList after reset should contain 2 pairs", 2, _adapter.getStyles().length);
    
    assertEquals("The first element pushed on StylesList after reset should be", styleElt1,
                 _adapter.getStyles()[1].toString());
    assertEquals("The second element pushed on StylesList after reset should be", styleElt2,
                 _adapter.getStyles()[0].toString());

  }

  public void test2() {
    try { helpTestCannotAddNullStyleToList(); }
    catch(Throwable t) { t.printStackTrace(); }
  }
  
  public void helpTestCannotAddNullStyleToList() throws EditDocumentException {

    
    assertEquals("StylesList before insert should contain 2 pairs", 2, _adapter.getStyles().length);

    
    _doc.append("5", InteractionsDocument.NUMBER_RETURN_STYLE);
    Utilities.clearEventQueue();

    assertEquals("StylesList should contain 3 pairs", 3, _adapter.getStyles().length);

    
    _doc.append("6", null);
     Utilities.clearEventQueue();

    assertEquals("StylesList should still contain 3 pairs - null string should not have been inserted",
                 3, _adapter.getStyles().length);
  }
}