

package edu.rice.cs.drjava.model.definitions.indent;


public final class QuestionInsideCommentTest extends IndentRulesTestCase {

  static IndentRuleQuestion _rule = new QuestionInsideComment(null, null);
  
  public void setUp() throws Exception {
    super.setUp();
    try {
      _setDocText("\n/*\nfoo\n*/\nbar\nfoo /* bar\n// /*\nfoo */ bar\n// /*\nblah");
      
      
      
      
      
      
      
      
      
      
      
      
    } catch (javax.swing.text.BadLocationException ex) {
      throw new RuntimeException("Bad Location Exception");
    }
  }
      
  
  public void testDocStart() throws javax.swing.text.BadLocationException {      
    assertEquals(false, _rule.applyRule(_doc, 0, Indenter.OTHER));
  }
  public void testLineBeginsComment() throws javax.swing.text.BadLocationException {
    assertEquals(false, _rule.applyRule(_doc, 3, Indenter.OTHER));
  }
  public void testFooLine() throws javax.swing.text.BadLocationException {
    assertEquals(true, _rule.applyRule(_doc, 6, Indenter.OTHER));
  }
  public void testLineEndsComment() throws javax.swing.text.BadLocationException {
    assertEquals(true, _rule.applyRule(_doc, 9, Indenter.OTHER));
  }
  public void testBarLine() throws javax.swing.text.BadLocationException {
    assertEquals(false, _rule.applyRule(_doc, 13, Indenter.OTHER));
  }
  public void testSlashStarMidLineBefore() throws javax.swing.text.BadLocationException {
    assertEquals(false, _rule.applyRule(_doc, 16, Indenter.OTHER));
  }
  public void testSlashStarMidLineAfter() throws javax.swing.text.BadLocationException {
    assertEquals(false, _rule.applyRule(_doc, 24, Indenter.OTHER));
  }
  public void testCommentedOutSlashStar() throws javax.swing.text.BadLocationException {
    assertEquals(true, _rule.applyRule(_doc, 30, Indenter.OTHER));
  }
  public void testStarSlashMidLineBefore() throws javax.swing.text.BadLocationException {
    assertEquals(true, _rule.applyRule(_doc, 33, Indenter.OTHER));
  }
  public void testStarSlashMidLineAfter() throws javax.swing.text.BadLocationException {
    assertEquals(true, _rule.applyRule(_doc, 41, Indenter.OTHER));
  }
  public void testAfterCommentedOutSlashStar() throws javax.swing.text.BadLocationException {
    assertEquals(false, _rule.applyRule(_doc, 49, Indenter.OTHER));
  }
  
}
