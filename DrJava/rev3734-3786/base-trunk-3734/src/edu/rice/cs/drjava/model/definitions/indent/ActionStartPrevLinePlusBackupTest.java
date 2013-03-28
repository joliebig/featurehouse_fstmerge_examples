



package edu.rice.cs.drjava.model.definitions.indent;


public class ActionStartPrevLinePlusBackupTest extends ActionStartPrevLinePlusTest {
  
  
  private IndentRuleAction makeAction(String suffix) {
    return new ActionStartPrevLinePlusBackup(suffix, suffix.length());
  }
  
  
  private IndentRuleAction makeBackupAction(String suffix, int position) {
    return new ActionStartPrevLinePlusBackup(suffix, position);
  }
  
  private String _noIndent = "foo\nbar";
  private String _evenIndent = "  foo\n  bar";
  private String _unevenIndent = "  foo\nbar";
  private String _noIndentRes = "foo\nabc bar";
  private String _evenIndentRes = "  foo\n  abc bar";
  private String _unevenIndentRes = "  foo\n  abc bar";
  
  
  public void testMoveToStart() throws javax.swing.text.BadLocationException {
    moveTestHelper(_noIndent, _noIndentRes, 0, 7, 0, 4);
    moveTestHelper(_evenIndent, _evenIndentRes, 0, 11, 0, 8);
    moveTestHelper(_unevenIndent, _unevenIndentRes, 2, 9, 0, 8);
  }
  
  
  public void testMoveToEnd() throws javax.swing.text.BadLocationException {
    moveTestHelper(_noIndent, _noIndentRes, 0, 4, 4, 8);
    moveTestHelper(_evenIndent, _evenIndentRes, 0, 6, 4, 12);
    moveTestHelper(_unevenIndent, _unevenIndentRes, 2, 6, 4, 12);
  }
  
  
  public void testMoveToMiddle() throws javax.swing.text.BadLocationException {
    moveTestHelper(_noIndent, _noIndentRes, 0, 4, 2, 6);
    moveTestHelper(_evenIndent, _evenIndentRes, 0, 6, 2, 10);
    moveTestHelper(_unevenIndent, _unevenIndentRes, 2, 6, 2, 10);
  }
  
  
  private void moveTestHelper(String text, String result, int deltaLen,
                              int before, int position, int after)
      throws javax.swing.text.BadLocationException {
    _setDocText(text);
    _doc.setCurrentLocation(before);  
    
    String suffix = "abc ";
    makeBackupAction(suffix, position).indentLine(_doc, Indenter.OTHER);
    assertEquals("text length",
                 text.length() + deltaLen + suffix.length(),
                 _doc.getLength());
    assertEquals("text contents", result, _doc.getText());
    assertEquals("location", after, _doc.getCurrentLocation());
  }
}
