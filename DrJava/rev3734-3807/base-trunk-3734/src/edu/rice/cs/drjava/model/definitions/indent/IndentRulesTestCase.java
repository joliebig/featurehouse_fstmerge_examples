

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.model.AbstractDJDocument;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;



public abstract class IndentRulesTestCase extends DrJavaTestCase {

  protected AbstractDJDocument _doc;

 

  
  public void setUp() throws Exception {
    super.setUp();
    
    
    _doc = new AbstractDJDocument() {
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
  
  public void tearDown() throws Exception {
    _doc = null;
    
    System.gc();
    super.tearDown();
  }
  
  
  protected final void _setDocText(String text)
    throws BadLocationException {
    _doc.clear();
    _doc.insertString(0, text, null);
  }
  
  
  
  
  
  

  
  
  
  protected void _assertContents(String expected) throws BadLocationException {
    assertEquals("document contents", 
                 expected, 
                 _doc.getText());
  }

}
