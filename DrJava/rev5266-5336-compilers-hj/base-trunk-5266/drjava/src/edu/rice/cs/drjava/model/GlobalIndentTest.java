

package edu.rice.cs.drjava.model;

import javax.swing.text.BadLocationException;
import java.util.List;

import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.util.OperationCanceledException;


public final class GlobalIndentTest extends GlobalModelTestCase {
  private static final String FOO_EX_1 = "public class Foo {\n";
  private static final String FOO_EX_2 = "int foo;\n";
  private static final String BAR_CALL_1 = "bar(monkey,\n";
  private static final String BAR_CALL_2 = "banana)\n";


  
  
  public void testIndentGrowTabAtStart() throws BadLocationException, OperationCanceledException {
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, FOO_EX_1, null);
    openDoc.insertString(FOO_EX_1.length(), " " + FOO_EX_2, null);
    openDoc.setCurrentLocation(FOO_EX_1.length());
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(FOO_EX_1 + "  " + FOO_EX_2, openDoc);
    _assertLocation(FOO_EX_1.length() + 2, openDoc);
  }
  
  
  public void testIndentGrowTabAtMiddle() throws BadLocationException, OperationCanceledException {
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, FOO_EX_1, null);
    openDoc.insertString(FOO_EX_1.length(), " " + FOO_EX_2, null);
    openDoc.setCurrentLocation(FOO_EX_1.length() + 5);
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(FOO_EX_1 + "  " + FOO_EX_2, openDoc);
    _assertLocation(FOO_EX_1.length() + 6, openDoc);
  }
  
  
  public void testIndentGrowTabAtEnd() throws BadLocationException, OperationCanceledException {
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, FOO_EX_1, null);
    openDoc.insertString(FOO_EX_1.length(), " " + FOO_EX_2, null);
    openDoc.setCurrentLocation(openDoc.getLength() - 1);
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(FOO_EX_1 + "  " + FOO_EX_2, openDoc);
    _assertLocation(openDoc.getLength() - 1, openDoc);
  }
  
  
  public void testIndentShrinkTabAtStart() throws BadLocationException, OperationCanceledException{
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, FOO_EX_1, null);
    openDoc.insertString(FOO_EX_1.length(), "   " + FOO_EX_2, null);
    openDoc.setCurrentLocation(FOO_EX_1.length());
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(FOO_EX_1 + "  " + FOO_EX_2, openDoc);
    _assertLocation(FOO_EX_1.length() + 2, openDoc);
  }
  
  
  public void testIndentShrinkTabAtMiddle() throws BadLocationException, OperationCanceledException {
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, FOO_EX_1, null);
    openDoc.insertString(FOO_EX_1.length(), "   " + FOO_EX_2, null);
    openDoc.setCurrentLocation(FOO_EX_1.length() + 5);
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(FOO_EX_1 + "  " + FOO_EX_2, openDoc);
    _assertLocation(FOO_EX_1.length() + 4, openDoc);
  }
  
  
  public void testIndentShrinkTabAtEnd()
    throws BadLocationException, OperationCanceledException {
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, FOO_EX_1, null);
    openDoc.insertString(FOO_EX_1.length(), "   " + FOO_EX_2, null);
    openDoc.setCurrentLocation(openDoc.getLength() - 1);
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(FOO_EX_1 + "  " + FOO_EX_2, openDoc);
    _assertLocation(openDoc.getLength() - 1, openDoc);
  }
  
  
  public void testIndentSameAsLineAboveAtStart() throws BadLocationException, OperationCanceledException {
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, FOO_EX_2, null);
    openDoc.insertString(FOO_EX_2.length(), "   " + FOO_EX_2, null);
    openDoc.setCurrentLocation(FOO_EX_2.length());
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(FOO_EX_2 + FOO_EX_2, openDoc);
    _assertLocation(FOO_EX_2.length(), openDoc);
  }
  
  
  public void testIndentSameAsLineAboveAtEnd() throws BadLocationException, OperationCanceledException {
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, FOO_EX_2, null);
    openDoc.insertString(FOO_EX_2.length(), "   " + FOO_EX_2, null);
    openDoc.setCurrentLocation(openDoc.getLength() - 1);
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(FOO_EX_2 + FOO_EX_2, openDoc);
    _assertLocation(openDoc.getLength() - 1, openDoc);
  }
  
  
  public void testIndentInsideParenAtStart() throws BadLocationException, OperationCanceledException {
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, BAR_CALL_1, null);
    openDoc.insertString(BAR_CALL_1.length(), BAR_CALL_2, null);
    openDoc.setCurrentLocation(BAR_CALL_1.length());
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(BAR_CALL_1 + "    " + BAR_CALL_2, openDoc);
    _assertLocation(BAR_CALL_1.length() + 4, openDoc);
  }
  
  
  public void testIndentInsideParenAtEnd() throws BadLocationException, OperationCanceledException {
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, BAR_CALL_1, null);
    openDoc.insertString(BAR_CALL_1.length(), BAR_CALL_2, null);
    openDoc.setCurrentLocation(openDoc.getLength() - 1);
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(BAR_CALL_1 + "    " + BAR_CALL_2, openDoc);
    _assertLocation(openDoc.getLength() - 1, openDoc);
  }
  
  
  public void testIndentDoesNothing() throws BadLocationException, OperationCanceledException {
    OpenDefinitionsDocument openDoc = _getOpenDoc();
    
    openDoc.insertString(0, FOO_EX_2 + FOO_EX_2, null);
    openDoc.setCurrentLocation(openDoc.getLength() - 1);
    int loc = openDoc.getCurrentLocation();
    openDoc.indentLines(loc, loc, Indenter.IndentReason.OTHER, null);
    _assertContents(FOO_EX_2 + FOO_EX_2, openDoc);
    _assertLocation(openDoc.getLength() - 1, openDoc);
  }
  
  
  
  
  









  
  
  private OpenDefinitionsDocument _getOpenDoc() {
    _assertNumOpenDocs(1);
    OpenDefinitionsDocument doc = _model.newFile();
    doc.setIndent(2);
    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    _assertNumOpenDocs(2);
    return docs.get(0);
  }
  
  private void _assertNumOpenDocs(int num) {
    assertEquals("number of open documents", num, _model.getOpenDefinitionsDocuments().size());
  }
  
  private void _assertContents(String expected, OpenDefinitionsDocument document) throws BadLocationException {
    assertEquals("document contents", expected, document.getText());
  }
  
  private void _assertLocation(int loc, OpenDefinitionsDocument openDoc) {
    assertEquals("current def'n loc", loc, openDoc.getCurrentLocation());
  }
}
