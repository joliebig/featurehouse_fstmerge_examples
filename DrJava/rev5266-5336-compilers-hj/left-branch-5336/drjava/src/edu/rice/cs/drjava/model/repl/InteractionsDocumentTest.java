

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.util.text.EditDocumentException;


public final class InteractionsDocumentTest extends DrJavaTestCase {
  protected InteractionsDocument _doc;
  
  static final String TEST_BANNER = "This is a test banner";
  
  
  protected void setUp() throws Exception {
    super.setUp();
    
    _doc = new InteractionsDocument(new InteractionsDJDocument());
    _doc.setBanner(TEST_BANNER);
  }

  
  public void testCannotEditBeforePrompt() throws EditDocumentException {
    TestBeep testBeep = new TestBeep();
    _doc.setBeep(testBeep);
    int origLength = _doc.getLength();

    
    _doc.insertText(1, "text", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("Number of beeps", 1, testBeep.numBeeps);
    assertEquals("Doc length", origLength, _doc.getLength());
  }

  
  public void testClearCurrent() throws EditDocumentException {
    int origLength = _doc.getLength();
    _doc.insertText(origLength, "text", InteractionsDocument.DEFAULT_STYLE);
    _doc.insertBeforeLastPrompt("before", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("Length after inserts", origLength + 10, _doc.getLength()); 
    _doc.clearCurrentInteraction();
    assertEquals("Length after clear", origLength + 6, _doc.getLength()); 
  }

  
  public void testContentsAndReset() throws EditDocumentException {
    String banner = TEST_BANNER;
    String prompt = _doc.getPrompt();
    String newBanner = "THIS IS A NEW BANNER\n";
    assertEquals("Contents before insert", banner + prompt, _doc.getDocText(0, _doc.getLength()));
    
    _doc.insertText(_doc.getLength(), "text", InteractionsDocument.DEFAULT_STYLE);
    _doc.insertBeforeLastPrompt("before", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("Contents before reset", banner + "before" + prompt + "text",  
                 _doc.getDocText(0, _doc.getLength()));
    _doc.reset(newBanner);
    assertEquals("Contents after reset", newBanner + prompt, _doc.getDocText(0, _doc.getLength()));
  }

  
  public void testInsertNewline() throws EditDocumentException {
    int origLength = _doc.getLength();
    _doc.insertText(origLength, "command", InteractionsDocument.DEFAULT_STYLE);
    assertEquals("current interaction before newline", "command", _doc.getCurrentInteraction());
    _doc.insertNewline(origLength + 2);
    assertEquals("current interaction after newline", "co" + "\n"  + "mmand",
                 _doc.getCurrentInteraction());
  }

  
  public void testRecallFromHistory() throws EditDocumentException {
    String origText = _doc.getDocText(0, _doc.getLength());
    _doc.addToHistory("command");
    assertEquals("Contents before recall prev", origText, _doc.getDocText(0, _doc.getLength()));

    _doc.recallPreviousInteractionInHistory();
    assertEquals("Contents after recall prev", origText + "command", _doc.getDocText(0, _doc.getLength()));

    _doc.recallNextInteractionInHistory();
    assertEquals("Contents after recall next", origText, _doc.getDocText(0, _doc.getLength()));
  }


  
  public static class TestBeep implements Runnable {
    int numBeeps = 0;
    public void run() { numBeeps++; }
  }
}



