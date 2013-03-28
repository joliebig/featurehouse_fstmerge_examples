

package edu.rice.cs.drjava.plugins.eclipse.util.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.rice.cs.util.text.DocumentEditCondition;
import edu.rice.cs.util.text.EditDocumentException;

import junit.framework.TestCase;


public class SWTDocumentAdapterTest extends TestCase {
  
  
  
  
  
  protected Display _display;
  protected Shell _shell;
  protected StyledText _pane;
  protected SWTDocumentAdapter _doc;
  
  
  public void setUp() {
    _display = new Display();
    _shell = new Shell(_display, SWT.TITLE | SWT.CLOSE);
    _pane = new StyledText(_shell, 0);
    _doc = new SWTDocumentAdapter(_pane);
  }
  
  
  public void tearDown() {
    _doc = null;
    _pane = null;
    _shell.dispose();
    _shell = null;
    _display.dispose();
    _display = null;
    System.gc();
  }
  
  
  public void testBasicDocOps() throws EditDocumentException {
    _doc.insertText(0, "one", null);
    assertEquals("first doc contents", "one",
                 _doc.getDocText(0, _doc.getLength()));
    
    _doc.insertText(_doc.getLength(), " three", null);
    assertEquals("second doc contents", "one three",
                 _doc.getDocText(0, _doc.getLength()));
    
    _doc.removeText(0, 3);
    _doc.insertText(0, "two", null);
    assertEquals("third doc contents", "two thr", _doc.getDocText(0, 7));
  }
  
  
  public void testException() {
    try {
      _doc.insertText(5, "test", null);
      fail("should have thrown an exception");
    }
    catch (EditDocumentException e) {
      
    }
  }
  
  
  public void testEditCondition() 
    throws EditDocumentException
  {
    DocumentEditCondition c = new DocumentEditCondition() {
      public boolean canInsertText(int offs, String str, String style) {
        return (offs > 5);
      }
      public boolean canRemoveText(int offs, int len) {
        return (len == 2);
      }
    };
    _doc.insertText(0, "initial", null);
    assertEquals("first doc contents", "initial",
                 _doc.getDocText(0, _doc.getLength()));
    
    _doc.setEditCondition(c);
    _doc.insertText(4, "1", null);
    assertEquals("insertText should be rejected", "initial",
                 _doc.getDocText(0, _doc.getLength()));
    _pane.replaceTextRange(2, 0, "1");
    assertEquals("replaceTextRange should be rejected", "initial",
                 _doc.getDocText(0, _doc.getLength()));
    _doc.insertText(6, "2", null);
    assertEquals("insertText should be accepted", "initia2l",
                 _doc.getDocText(0, _doc.getLength()));
    _doc.forceInsertText(2, "3", null);
    assertEquals("forceInsertText should be accepted", "in3itia2l",
                 _doc.getDocText(0, _doc.getLength()));
    
    _doc.removeText(1, 1);
    assertEquals("removeText should be rejected", "in3itia2l",
                 _doc.getDocText(0, _doc.getLength()));
    _pane.replaceTextRange(6, 1, "");
    assertEquals("replaceTextRange should be rejected", "in3itia2l",
                 _doc.getDocText(0, _doc.getLength()));
    _doc.removeText(1, 2);
    assertEquals("removeText should be accepted", "iitia2l",
                 _doc.getDocText(0, _doc.getLength()));
    _doc.forceRemoveText(6, 1);
    assertEquals("forceRemove should be accepted", "iitia2",
                 _doc.getDocText(0, _doc.getLength()));
    
  }
  
}
