

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.DJDocument;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;



public final class IndentInfoTest extends DrJavaTestCase {
  private String _text;
  
  
  private IndentInfo _info;
  
  private DJDocument _document;
  
  
  public void setUp() throws Exception {
    super.setUp();
    
    
    _document = new AbstractDJDocument() {
      protected int startCompoundEdit() {
        
        return 0;
      }
      protected void endCompoundEdit(int key) {
        
      }
      protected void endLastCompoundEdit() {
        
      }
      protected void addUndoRedo(AbstractDocument.DefaultDocumentEvent chng, Runnable undoCommand, Runnable doCommand) {
        
      }
      protected void _styleChanged() {
        
      }
      protected Indenter makeNewIndenter(int indentLevel) {
        return new Indenter(indentLevel);
      }
    };
  }
  
  private void _infoTestHelper(int location, String message,
                               int expDistToPrevNewline, int expDistToBrace,
                               int expDistToNewline, int expDistToBraceCurrent,
                               int expDistToNewlineCurrent)
  {
    _document.setCurrentLocation(location);
    
    _info = _document.getIndentInformation();
    
    assertEquals(message + " -- distToPrevNewline", expDistToPrevNewline, _info.distToPrevNewline);
    assertEquals(message + " -- distToBrace", expDistToBrace, _info.distToBrace);
    assertEquals(message + " -- distToNewline", expDistToNewline, _info.distToNewline);
    assertEquals(message + " -- distToBraceCurrent", expDistToBraceCurrent, _info.distToBraceCurrent);
    assertEquals(message + " -- distToNewlineCurrent", expDistToNewlineCurrent, _info.distToNewlineCurrent);
  }
  
  public void testFieldsForCurrentLocation() throws BadLocationException {
    
    _text = "foo {\nvoid m1(int a,\nint b) {\n}\n}";
    
    
    
    
    _document.clear();
    _document.insertString(0, _text, null);
    
    _infoTestHelper(0, "DOCSTART -- no brace or newline",     -1, -1, -1, -1, -1);
    _infoTestHelper(4, "Location has no brace or newline",    -1, -1, -1, -1, -1);
    _infoTestHelper(5, "Location has a brace but no newline", -1, -1, -1,  1, -1);
    _infoTestHelper(6, "Location has a brace and a newline",   0,  2, -1,  2, -1);
    _infoTestHelper(10, "Location has a brace and a newline",  4,  6, -1,  6, -1);
    _infoTestHelper(13, "Location has a brace and a newline",  7,  9, -1,  9, -1);
    _infoTestHelper(14, "Location has a brace and a newline",  8, 10, -1,  1,  8);
    _infoTestHelper(20, "At \\n within parens",               14, 16, -1,  7, 14);
    _infoTestHelper(21, "Second line within parens",           0,  8, 15,  8, 15);
    _infoTestHelper(26, "On close paren",                      5, 13, 20, 13, 20);
    _infoTestHelper(28, "On second open brace",                7, 15, 22, 24, -1);
    _infoTestHelper(29, "On \\n in second set of braces",      8, 16, 23,  1,  8);
    _infoTestHelper(30, "Close brace of method declaration",   0,  2,  9,  2,  9);
    _infoTestHelper(31, "Last \\n",                            1,  3, 10, 27, -1);
    _infoTestHelper(32, "Final close brace",                   0, 28, -1, 28, -1);
  }
}
