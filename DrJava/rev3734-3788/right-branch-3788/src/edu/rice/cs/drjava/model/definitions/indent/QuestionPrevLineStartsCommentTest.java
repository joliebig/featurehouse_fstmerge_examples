



package edu.rice.cs.drjava.model.definitions.indent;


public final class QuestionPrevLineStartsCommentTest extends IndentRulesTestCase {

  static IndentRuleQuestion rule2 = new QuestionPrevLineStartsComment(null,
              null);
  private static String example1 = "/*\nfoo\nbar\n*/";
  
  
  
  
  
  private static String example2 = "foo /* bar\nblah\nmoo\n*/";
  
  
  
  
  
  private static String example3 = "/*\nfoo\n// /*\nbar\n*/";
  
  
  
  
  
  

  public void testSimpleFirstLine() throws javax.swing.text.BadLocationException {
    _setDocText(example1);
    assertEquals(true, rule2.applyRule(_doc, 3, Indenter.OTHER));
  }
  
  public void testSimpleSecondLine() throws javax.swing.text.BadLocationException {
    _setDocText(example1);
    assertEquals(false, rule2.applyRule(_doc, 7, Indenter.OTHER));
  }
  
  public void testSlashStarMidLineFirstLine() throws javax.swing.text.BadLocationException {
    _setDocText(example2);
    assertEquals(true, rule2.applyRule(_doc, 11, Indenter.OTHER));
  }
  public void testSlashStarMidLineSecondLine() throws javax.swing.text.BadLocationException {
    _setDocText(example2);
    assertEquals(false, rule2.applyRule(_doc, 16, Indenter.OTHER));
  }
  public void testCommentedOutSlashStarBefore() throws javax.swing.text.BadLocationException {
    _setDocText(example3);
    assertEquals(true, rule2.applyRule(_doc, 3, Indenter.OTHER));
  }
  public void testCommentedOutSlashStarAt() throws javax.swing.text.BadLocationException {
    _setDocText(example3);
    assertEquals(false, rule2.applyRule(_doc, 7, Indenter.OTHER));
  }
  public void testCommentedOutSlashStarAfter() throws javax.swing.text.BadLocationException {
    _setDocText(example3);
    assertEquals(false, rule2.applyRule(_doc, 13, Indenter.OTHER));
  }
}


