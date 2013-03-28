

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.model.definitions.indent.IndentRulesTestCase;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;


public final class BraceInfoTest extends DrJavaTestCase {
  private String _text;
  private AbstractDJDocument _document;
  
  public void setUp() throws Exception {
    super.setUp();
    _document = new AbstractDJDocument(IndentRulesTestCase.TEST_INDENT_LEVEL) {
      protected int startCompoundEdit() {
        
        return 0;
      }
      protected void endCompoundEdit(int key) {  }
      protected void endLastCompoundEdit() {  }
      protected void addUndoRedo(AbstractDocument.DefaultDocumentEvent chng, Runnable undoCommand, Runnable doCommand) {
        
      }
      protected void _styleChanged() {  }
    };
  }
  
  private void _lineEnclosingBraceTestHelper(int location, String msg, int expDist, String expBraceType) {
    _document.setCurrentLocation(location);
    BraceInfo info = _document._getLineEnclosingBrace();
    assertEquals(msg + ": distance", expDist, info.distance());
    assertEquals(msg + ": braceType", expBraceType, info.braceType());
  }
  
  private void _enclosingBraceTestHelper(int location, String msg, int expDist, String expBraceType) {
    _document.setCurrentLocation(location);
    BraceInfo info = _document._getEnclosingBrace();
    assertEquals(msg + ": distance", expDist, info.distance());
    assertEquals(msg + ": braceType", expBraceType, info.braceType());
  }
  
  public void testFieldsForCurrentLocation() throws BadLocationException {
    
    _text = "foo {\nvoid m1(int a,\nint b) {\n}\n}";
    
    
    
    
    _document.clear();
    _document.insertString(0, _text, null);
    
    _lineEnclosingBraceTestHelper(0, "0 -- no brace or newline", -1 , "");
    _enclosingBraceTestHelper(0, "0 -- no brace or newline", -1 , "");
    _lineEnclosingBraceTestHelper(4, "No brace or newline", -1, "");
    _enclosingBraceTestHelper(4, "No brace or newline", -1, "");
    _lineEnclosingBraceTestHelper(5, "Curly brace preceding but no newline", -1, "");
    _enclosingBraceTestHelper(5, "Curly brace preceding but no newline", 1, "{");
    _lineEnclosingBraceTestHelper(6, "Curly brace preceding a newline", 2, "{");
    _enclosingBraceTestHelper(6, "Curly brace preceding a newline", 2, "{");
    _lineEnclosingBraceTestHelper(10, "Curly brace preceding a newline", 2, "{");
    _enclosingBraceTestHelper(10, "Curly brace preceding a newline", 6, "{");
    _lineEnclosingBraceTestHelper(13, "Curly brace preceding a newline", 2, "{");  
    _enclosingBraceTestHelper(13, "Curly brace preceding a newline; '(' follows", 9, "{");
    _lineEnclosingBraceTestHelper(14, "Curly brace preceding a newline", 2, "{");
    _enclosingBraceTestHelper(14, "Paren preceding on current line", 1, "(");
    _lineEnclosingBraceTestHelper(20, "Curly brace preceding a newline", 2, "{");  
    _enclosingBraceTestHelper(20, "Paren preceding on current line", 7, "(");
    _lineEnclosingBraceTestHelper(21, "Newline after paren", 8, "(");
    _enclosingBraceTestHelper(21, "Newline after paren", 8, "(");
    
    _lineEnclosingBraceTestHelper(26, "Just before close paren", 8, "(");
    _enclosingBraceTestHelper(26, "Just before close paren", 13, "(");
    _lineEnclosingBraceTestHelper(28, "After close paren, just before second open brace", 8, "(");
    _enclosingBraceTestHelper(28, "After close paren, just before second open brace", 24, "{");
    _lineEnclosingBraceTestHelper(29, "Just before newline in second set of braces", 8, "(");
    _enclosingBraceTestHelper(29, "Just before newline in second set of braces", 1, "{");
    _lineEnclosingBraceTestHelper(30, "Just after newline in second set of braces", 2, "{");
    _enclosingBraceTestHelper(30, "Just after newline in second set of braces", 2, "{");
    _lineEnclosingBraceTestHelper(31, "Just after a close curly brace on a new line", 2, "{");
    _enclosingBraceTestHelper(31, "Just after a close curly brace on a new line", 27, "{");
    _lineEnclosingBraceTestHelper(32, "Just after newline, just before final close brace", 28, "{");
    _enclosingBraceTestHelper(32, "Just after newline, just before final close brace", 28, "{");
    _lineEnclosingBraceTestHelper(33, "End of text", 28, "{");
    _enclosingBraceTestHelper(33, "End of text, just after final closng brace", -1, "");
  }
}
