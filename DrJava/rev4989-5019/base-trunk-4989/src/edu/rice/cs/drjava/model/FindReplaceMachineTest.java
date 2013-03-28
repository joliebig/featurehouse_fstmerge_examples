

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;

import javax.swing.text.BadLocationException;
import java.io.File;


public class FindReplaceMachineTest extends DrJavaTestCase {
  private volatile OpenDefinitionsDocument _doc;  
  private volatile OpenDefinitionsDocument _docPrev;
  private volatile OpenDefinitionsDocument _docNext;
  private volatile FindResult _result;      
  private volatile FindReplaceMachine _frm;
  private volatile File _tempDir;
  private volatile int _offset;
  private static final AbstractGlobalModel _model = new AbstractGlobalModel();  
  
  private static final String EVIL_TEXT =
    "Hear no evil, see no evil, speak no evil.";
  private static final String EVIL_TEXT_PREV =
    "Hear no evilprev, see no evilprev, speak no evilprev.";
  private static final String EVIL_TEXT_NEXT =
    "Hear no evilnext, see no evilnext, speak no evilnext.";
  private static final String FIND_WHOLE_WORD_TEST_1 =
    "public class Foo\n" +
    "{\n" +
    "        /**\n" +
    "         * Barry Good!\n" +
    "         * (what I really mean is bar)\n" +
    "         */\n" +
    "        public void bar() \n" +
    "        {\n" +
    "                this.bar();\n" +
    "        }\n" +
    "}";
  
  private static final String FIND_MULTI_LINE_SEARCH_STR = "{" + StringOps.EOL;
  
  private static final String IGNORE_TEXT =
    
    "/* \" */  plt \n" +           
    "\" /* \"  plt \n" +           
    "/* // */  plt \n" +           
    "\" // \"  plt \n" +           
    "\" \\\" \"  plt \n" +         
    "\'\"\' plt \n" +              
    "\'//\' plt \n" +              
    "\'/*\' plt \n" +              
    
    
    "/*This is a block comment*/ This is not a block comment\n" +
    "//This is a line comment \n This is not a line comment\n" +
    "\"This is a string\" This is not a string\n" +
    "\'@\' That was a character, but this is not: @\n" +
    "/*This is a two-lined \n commment*/ This is not a two-lined comment";
  
  
  public void setUp() throws Exception {
    super.setUp();
    String user = System.getProperty("user.name");
    _tempDir = IOUtil.createAndMarkTempDirectory("DrJava-test-" + user, "");
    _docPrev = _model.newFile(_tempDir);;
    _doc = _model.newFile(_tempDir);
    _docNext = _model.newFile(_tempDir);
    
    _frm = new FindReplaceMachine(_model, _model.getDocumentIterator());
    _frm.setDocument(_doc);
  }  
  
  public void tearDown() throws Exception {
    _frm.cleanUp();
    _frm = null;
    _model.closeAllFiles();
    _tempDir = null;
    super.tearDown();
  }
  
  private void _initFrm(int pos) { _frm.setPosition(pos); }
  
  public void testCreateMachineSuccess() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(4);

  }
  























  
  public void testFindNextUpdatesCurrent() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(0);
    _assertOffset(_frm, 0);
    _frm.setFindWord("evil");
    
    _testFindNextSucceeds(_frm, 0, 12);

  }
  
  public void testFindNextAndFailIsOnMatch() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(0);
    _assertOffset(_frm, 0);
    _frm.setFindWord("evil");
    _testFindNextSucceeds(_frm, 0, 12);
    _doc.insertString(9, "-", null);
    assertTrue("no longer on find text", !_frm.onMatch());

  }
  
  public void testFindNextOnSuffix() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(1);
    _assertOffset(_frm, 1);
    _frm.setFindWord("Hear");
    _testFindNextSucceeds(_frm, 0, 4);
    _doc.insertString(1, "-", null);
    assertTrue("no longer on find text", ! _frm.onMatch());

  }
  
  public void testFindPrevOnPrefix() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(3);
    _frm.setSearchBackwards(true);
    _assertOffset(_frm, 3);
    _frm.setFindWord("Hear");
    _testFindNextSucceeds(_frm, 0, 0);
    _doc.insertString(1, "-", null);
    assertTrue("no longer on find text", ! _frm.onMatch());

  }
  
  public void testMultipleCallsToFindNext() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(0);
    _assertOffsets(_frm, 0, 0);
    _frm.setFindWord("evil");
    _testFindNextSucceeds(_frm, 0, 12);
    _testFindNextSucceeds(_frm, 0, 25);
    _testFindNextSucceeds(_frm, 0, 40);

  }
  
  public void testStartFromTopContinue() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(5);
    _assertOffsets(_frm, 5, 5);
    _frm.setFindWord("Hear");
    _testFindNextSucceeds(_frm, 5, 4);

  }
  
  public void testNotInDocument() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(5);
    _assertOffsets(_frm, 5, 5);
    _frm.setFindWord("monkey");
    _testFindNextFails(_frm, 5, 5);

  }
  
  public void testSimpleReplace() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(0);
    _assertOffsets(_frm, 0, 0);
    _frm.setFindWord("evil");
    _frm.setReplaceWord("monkey");
    _testFindNextSucceeds(_frm, 0, 12);
    Utilities.invokeAndWait(new Runnable() { public void run() { _frm.replaceCurrent(); } });
    assertEquals("new replaced text", "Hear no monkey, see no evil, speak no evil.", _doc.getText());

  }
  
  
  public void testReplaceAllContinue() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(15);
    _assertOffsets(_frm, 15, 15);
    _frm.setFindWord("evil");
    _frm.setReplaceWord("monkey");
    replaceAll();
    assertEquals("revised text", "Hear no monkey, see no monkey, speak no monkey.", _doc.getText());

  }
  
  public void testFindNoMatchCase() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(0);
    _assertOffsets(_frm, 0, 0);
    _frm.setMatchCase(false);
    _frm.setFindWord("eViL");
    _testFindNextSucceeds(_frm, 0, 12);

  }
  
  public void testReplaceAllContinueNoMatchCase() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _initFrm(15);
    _assertOffsets(_frm, 15, 15);
    _frm.setFindWord("eViL");
    _frm.setReplaceWord("monkey");
    _frm.setMatchCase(false);
    replaceAll();
    assertEquals("revised text", "Hear no monkey, see no monkey, speak no monkey.", _doc.getText());

  }
  
  public void testReplaceAllBackwards() throws BadLocationException {
    _doc.insertString(0, "hElo helO", null);
    _initFrm(3);
    _frm.setFindWord("heLo");
    _frm.setReplaceWord("cool");
    _frm.setMatchCase(false);
    _frm.setSearchBackwards(true);
    replaceAll();
    assertEquals("backwards replace", "cool cool", _doc.getText());

  }
  
  public void testFindMatchWithCaretInMiddle() throws BadLocationException {
    _doc.insertString(0, "hello hello", null);
    _initFrm(3);
    _frm.setFindWord("hello");
    _frm.setMatchCase(false);
    _frm.setSearchBackwards(false);
    _testFindNextSucceeds(_frm, 3, 11);
    _testFindNextSucceeds(_frm, 3, 5);

  }
  
  public void testFindMatchWithCaretInMiddleBackwards() throws BadLocationException {
    _doc.insertString(0, "hello hello", null);
    _initFrm(8);
    _frm.setFindWord("helLo");
    _frm.setMatchCase(false);
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 8, 0);
    _testFindNextSucceeds(_frm, 8, 6);

  }
  
  
  public void testReplaceCreatesMatch() throws BadLocationException {
    _doc.insertString(0, "hhelloello", null);
    _initFrm(1);
    _frm.setFindWord("hello");
    _frm.setMatchCase(false);
    _frm.setSearchBackwards(false);
    _frm.setReplaceWord("");
    replaceAll();
    assertEquals("replace creates new match", "hello", _doc.getText());

  }
  
  
  public void testReplaceCreatesMatchBackwards() throws BadLocationException {
    _doc.insertString(0, "hhelloello", null);
    _initFrm(1);
    _frm.setFindWord("hello");
    _frm.setMatchCase(false);
    _frm.setSearchBackwards(true);
    _frm.setReplaceWord("");
    replaceAll();
    assertEquals("replace creates new match", "hello", _doc.getText());

  }
  
  
  public void testReplaceAllSameWord() throws BadLocationException {
    _doc.insertString(0, "cool cool", null);
    _initFrm(3);
    _frm.setFindWord("cool");
    _frm.setMatchCase(false);
    _frm.setSearchBackwards(false);
    _frm.setReplaceWord("cool");
    replaceAll();
    assertEquals("replace all with the same word", "cool cool", _doc.getText());

    _frm.setSearchBackwards(true);
    replaceAll();
    assertEquals("replace all backward with the same word", "cool cool", _doc.getText());

  }
  
  
  public void testFindPartialSubstrings() throws BadLocationException {
    _doc.insertString(0, "ooAooAoo", null);
    _initFrm(0);
    _frm.setFindWord("ooAo");
    _frm.setMatchCase(false);
    _frm.setSearchBackwards(false);
    _testFindNextSucceeds(_frm, 0, 4);
    _testFindNextSucceeds(_frm, 0, 4);
    
    _initFrm(8);
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 8, 3);
    _testFindNextSucceeds(_frm, 8, 3);

  }
  
  
  public void testSearchesDoNotRepeatWhenChangingDirection() throws BadLocationException {
    _doc.insertString(0, "int int int", null);
    _initFrm(0);
    _frm.setFindWord("int");
    _frm.setMatchCase(false);
    _frm.setSearchBackwards(false);
    _testFindNextSucceeds(_frm, 0, 3);
    _testFindNextSucceeds(_frm, 0, 7);
    
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 0);
    
    _frm.setLastFindWord();
    _frm.setSearchBackwards(false);
    _testFindNextSucceeds(_frm, 0, 7);
    
    _frm.setLastFindWord();
    _frm.positionChanged();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 4);

  }
  
  
  public void testFindReplaceInAllOpenFiles() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _docPrev.insertString(0, EVIL_TEXT_PREV, null);
    _docNext.insertString(0, EVIL_TEXT_NEXT, null);
    
    _initFrm(40);
    _frm.setFindWord("evil");
    _frm.setMatchCase(false);
    _frm.setSearchBackwards(false);
    _frm.setSearchAllDocuments(true);
    _testFindNextSucceeds(_frm, 12, 12, _docNext);
    _testFindNextSucceeds(_frm, 12, 29, _docNext);
    _testFindNextSucceeds(_frm, 12, 48, _docNext);
    _testFindNextSucceeds(_frm, 12, 12, _docPrev);
    _testFindNextSucceeds(_frm, 12, 29, _docPrev);
    _testFindNextSucceeds(_frm, 12, 48, _docPrev);
    _testFindNextSucceeds(_frm, 12, 12, _doc);
    _testFindNextSucceeds(_frm, 12, 25, _doc);
    _testFindNextSucceeds(_frm, 12, 40, _doc);
    _testFindNextSucceeds(_frm, 12, 12, _docNext);

    _frm.setLastFindWord();

    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 36, 36, _doc);
    _testFindNextSucceeds(_frm, 36, 21, _doc);
    _testFindNextSucceeds(_frm, 36, 8, _doc);
    _testFindNextSucceeds(_frm, 44, 44, _docPrev);
    _frm.setReplaceWord("monkey");
    replaceAll();
    assertEquals("revised text", "Hear no monkey, see no monkey, speak no monkey.", _doc.getText());
    assertEquals("revised text", "Hear no monkeyprev, see no monkeyprev, speak no monkeyprev.", _docPrev.getText());
    assertEquals("revised text", "Hear no monkeynext, see no monkeynext, speak no monkeynext.", _docNext.getText());

  }
  
  public void testFindReplaceInAllOpenFilesWholeWord() throws BadLocationException {
    _doc.insertString(0, EVIL_TEXT, null);
    _docPrev.insertString(0, EVIL_TEXT_PREV, null);
    _docNext.insertString(0, EVIL_TEXT_NEXT, null);
    
    _initFrm(40);
    _frm.setFindWord("no");
    _frm.setMatchWholeWord();
    _frm.setMatchCase(false);
    _frm.setSearchBackwards(false);
    _frm.setSearchAllDocuments(true);
    _testFindNextSucceeds(_frm, 7, 7, _docNext);
    _testFindNextSucceeds(_frm, 7, 24, _docNext);
    _testFindNextSucceeds(_frm, 7, 43, _docNext);
    _testFindNextSucceeds(_frm, 7, 7, _docPrev);
    _testFindNextSucceeds(_frm, 7, 24, _docPrev);
    _testFindNextSucceeds(_frm, 7, 43, _docPrev);
    _testFindNextSucceeds(_frm, 7, 7, _doc);
    _testFindNextSucceeds(_frm, 7, 20, _doc);
    _testFindNextSucceeds(_frm, 7, 35, _doc);
    _testFindNextSucceeds(_frm, 7, 7, _docNext);
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 33, 33, _doc);
    _testFindNextSucceeds(_frm, 33, 18, _doc);
    _testFindNextSucceeds(_frm, 33, 5, _doc);
    _testFindNextSucceeds(_frm, 41, 41, _docPrev);
    _frm.setReplaceWord("monkey");
    replaceAll();
    assertEquals("revised text",
                 "Hear monkey evil, see monkey evil, speak monkey evil.",
                 _doc.getText(0, _doc.getLength()));
    assertEquals("revised text",
                 "Hear monkey evilprev, see monkey evilprev, speak monkey evilprev.",
                 _docPrev.getText(0, _docPrev.getLength()));
    assertEquals("revised text",
                 "Hear monkey evilnext, see monkey evilnext, speak monkey evilnext.",
                 _docNext.getText(0, _docNext.getLength()));

  }
  
  public void testFindMultiLine() throws BadLocationException {

    _doc.insertString(0, FIND_WHOLE_WORD_TEST_1, null);

    _initFrm(0);
    _frm.setFindWord(FIND_MULTI_LINE_SEARCH_STR);
    _frm.setSearchBackwards(false);
    
    _testFindNextSucceeds(_frm, 0, 19);

  }
  
  public void testWholeWordSearchOnTestString1() throws BadLocationException {

    _doc.insertString(0, FIND_WHOLE_WORD_TEST_1, null);

    _initFrm(0);
    _frm.setFindWord("bar");
    _frm.setMatchWholeWord();
    _frm.setSearchBackwards(false);
    
    _testFindNextSucceeds(_frm, 0, 91);
    _testFindNextSucceeds(_frm, 0, 128);
    _testFindNextSucceeds(_frm, 0, 166);
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 125);
    _testFindNextSucceeds(_frm, 0, 88);
    _testFindNextSucceeds(_frm, 0, 163);
    
    _frm.setFindWord("ubl");
    _testFindNextFails(_frm, 0, 163);
    
    _frm.setSearchBackwards(false);
    _frm.setFindWord("pub");
    _testFindNextFails(_frm, 0, 163);
    
    _frm.setSearchBackwards(true);
    _frm.setFindWord("pub");
    _testFindNextFails(_frm, 0, 163);

  }
  
  public void testWholeWordSearchIgnore() throws BadLocationException {
    _doc.insertString(0, IGNORE_TEXT, null);

    _initFrm(0);
    _frm.setFindWord("plt");
    _frm.setMatchWholeWord();
    _frm.setIgnoreCommentsAndStrings(true);
    _frm.setSearchBackwards(false);
    
    _testFindNextSucceeds(_frm, 0, 12);
    _testFindNextSucceeds(_frm, 0, 25);
    _testFindNextSucceeds(_frm, 0, 40);
    _testFindNextSucceeds(_frm, 0, 53);
    _testFindNextSucceeds(_frm, 0, 66);
    _testFindNextSucceeds(_frm, 0, 75);
    _testFindNextSucceeds(_frm, 0, 85);
    _testFindNextSucceeds(_frm, 0, 95);
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 82);
    _testFindNextSucceeds(_frm, 0, 72);
    _testFindNextSucceeds(_frm, 0, 63);
    _testFindNextSucceeds(_frm, 0, 50);
    _testFindNextSucceeds(_frm, 0, 37);
    _testFindNextSucceeds(_frm, 0, 22);
    _testFindNextSucceeds(_frm, 0, 9);
    _testFindNextSucceeds(_frm, 0, 92);
    
    _frm.setSearchBackwards(false);
    _frm.setFindWord("comment");
    _testFindNextSucceeds(_frm, 0, 152);
    _testFindNextSucceeds(_frm, 0, 206);
    _testFindNextSucceeds(_frm, 0, 358);
    
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 199);
    _testFindNextSucceeds(_frm, 0, 145);
    _testFindNextSucceeds(_frm, 0, 351);
    
    _frm.setSearchBackwards(false);
    _frm.setFindWord("@");
    _testFindNextSucceeds(_frm, 0, 291);
    
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 290);
    
    _frm.setSearchBackwards(false);
    _frm.setFindWord("string");
    _testFindNextSucceeds(_frm, 0, 246);
    
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 240);

  }
  
  public void testAnyOccurrenceSearchIgnore() throws BadLocationException {
    _doc.insertString(0, IGNORE_TEXT, null);

    _initFrm(0);
    _frm.setFindWord("lt");
    _frm.setIgnoreCommentsAndStrings(true);
    _frm.setSearchBackwards(false);
    
    _testFindNextSucceeds(_frm, 0, 12);
    _testFindNextSucceeds(_frm, 0, 25);
    _testFindNextSucceeds(_frm, 0, 40);
    _testFindNextSucceeds(_frm, 0, 53);
    _testFindNextSucceeds(_frm, 0, 66);
    _testFindNextSucceeds(_frm, 0, 75);
    _testFindNextSucceeds(_frm, 0, 85);
    _testFindNextSucceeds(_frm, 0, 95);
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 83);
    _testFindNextSucceeds(_frm, 0, 73);
    _testFindNextSucceeds(_frm, 0, 64);
    _testFindNextSucceeds(_frm, 0, 51);
    _testFindNextSucceeds(_frm, 0, 38);
    _testFindNextSucceeds(_frm, 0, 23);
    _testFindNextSucceeds(_frm, 0, 10);
    _testFindNextSucceeds(_frm, 0, 93);
    
    _frm.setSearchBackwards(false);
    _frm.setFindWord("ment");
    _testFindNextSucceeds(_frm, 0, 152);
    _testFindNextSucceeds(_frm, 0, 206);
    _testFindNextSucceeds(_frm, 0, 358);
    
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 202);
    _testFindNextSucceeds(_frm, 0, 148);
    _testFindNextSucceeds(_frm, 0, 354);
    
    _frm.setSearchBackwards(false);
    _frm.setFindWord("@");
    _testFindNextSucceeds(_frm, 0, 291);
    
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 290);
    
    _frm.setSearchBackwards(false);
    _frm.setFindWord("ring");
    _testFindNextSucceeds(_frm, 0, 246);
    
    _frm.setLastFindWord();
    _frm.setSearchBackwards(true);
    _testFindNextSucceeds(_frm, 0, 242);

  }
  
  private void _testFindNextSucceeds(final FindReplaceMachine frm, int start, final int found, 
                                     OpenDefinitionsDocument doc) {
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        try {
          _result = frm.findNext();
          OpenDefinitionsDocument newDoc = _result.getDocument();
          if (frm.getDocument() != newDoc) {
            

            frm.setDocument(newDoc);
            frm.setPosition(found);
          }
        }
        catch (Exception e) { throw new UnexpectedException(e); }
      }
    });
    
    Utilities.clearEventQueue();
    assertEquals("documents should equal", doc.toString(), frm.getDocument().toString());
    assertEquals("findNext return value", found, _result.getFoundOffset());
    _assertOffsets(frm, start, found);
    assertTrue("on find text", frm.onMatch());
  } 
  
  private void _testFindNextSucceeds(final FindReplaceMachine frm, int start, int found) {
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        try { _offset = frm.findNext().getFoundOffset(); }
        catch(Exception e) { throw new UnexpectedException(e); }
      }
    });
    assertEquals("findNext return value", found, _offset);
    _assertOffsets(frm, start, found);
    assertTrue("on find text", frm.onMatch());
  }
  
  private void _testFindNextFails(final FindReplaceMachine frm, int start, int current) {
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        try { _offset = frm.findNext().getFoundOffset(); }
        catch(Exception e) { throw new UnexpectedException(e); }
      }
    });
    assertEquals("findNext return value", -1, _offset);
    _assertOffsets(frm, start, current);
  }
  
  private void _assertOffsets(FindReplaceMachine frm, int start, int current) {


    assertEquals("current offset", current, frm.getCurrentOffset());
  }
  
  private void _assertOffset(FindReplaceMachine frm, int current) {
    assertEquals("current offset", current, frm.getCurrentOffset());
  }
  
  private void replaceAll() {
    Utilities.invokeAndWait(new Runnable() { public void run() { _frm.replaceAll(); } });
  }
  




  





}
