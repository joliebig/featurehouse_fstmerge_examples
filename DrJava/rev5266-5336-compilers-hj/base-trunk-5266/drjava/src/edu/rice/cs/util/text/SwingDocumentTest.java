

package edu.rice.cs.util.text;

import edu.rice.cs.drjava.DrJavaTestCase;

import javax.swing.text.BadLocationException;


public class SwingDocumentTest extends DrJavaTestCase {
  protected SwingDocument _doc;
  
  public void setUp() throws Exception {
    super.setUp();
    _doc = new SwingDocument();
  }
  
  public void tearDown() throws Exception {
    _doc = null;
    super.tearDown();
  }
  
  
  public void testBasicDocOps() throws EditDocumentException {
    _doc.insertText(0, "one", null);
    assertEquals("first doc contents", "one", _doc.getText());
    
    _doc.insertText(_doc.getLength(), " three", null);
    assertEquals("second doc contents", "one three", _doc.getText());
    
    _doc.removeText(0, 3);
    _doc.insertText(0, "two", null);
    assertEquals("third doc contents", "two thr", _doc.getDocText(0, 7));
    
    _doc.append(" four", (String) null);
    assertEquals("fourth doc contents", "two three four", _doc.getText());
  }
  
  
  public void testException() {
    try {
      _doc.insertText(5, "test", null);
      fail("should have thrown an exception");
    }
    catch (EditDocumentException e) {  }
  }
  
  
  public void testEditCondition() throws EditDocumentException, BadLocationException {
    DocumentEditCondition c = new DocumentEditCondition() {
      public boolean canInsertText(int offs) { return (offs > 5); }
      public boolean canRemoveText(int offs) { return (offs == 1); }
    };
    _doc.insertText(0, "initial", null);
    assertEquals("first doc contents", "initial", _doc.getDocText(0, _doc.getLength()));
    
    _doc.setEditCondition(c);
    _doc.insertText(4, "1", null);
    assertEquals("insertText should be rejected", "initial", _doc.getText());
    _doc.insertString(2, "1", null);
    assertEquals("insertString should be rejected", "initial", _doc.getText());
    _doc.insertText(6, "2", null);
    assertEquals("insertText should be accepted", "initia2l", _doc.getText());
    _doc.forceInsertText(2, "3", null);
    assertEquals("forceInsertText should be accepted", "in3itia2l", _doc.getText());
    
    _doc.removeText(3, 1);
    assertEquals("removeText should be rejected", "in3itia2l", _doc.getText());
    _doc.remove(6, 1);
    assertEquals("remove should be rejected", "in3itia2l", _doc.getText());
    _doc.removeText(1, 2);
    assertEquals("removeText should be accepted", "iitia2l", _doc.getText());
    _doc.forceRemoveText(6, 1);
    assertEquals("forceRemove should be accepted", "iitia2", _doc.getText());
    _doc.append("THE END", (String) null);
    assertEquals("forceRemove should be accepted", "iitia2THE END", _doc.getText());
  }
}
