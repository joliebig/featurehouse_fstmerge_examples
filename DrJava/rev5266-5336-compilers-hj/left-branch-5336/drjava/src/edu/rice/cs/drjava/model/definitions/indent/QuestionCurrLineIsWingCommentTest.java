



package edu.rice.cs.drjava.model.definitions.indent;


public final class QuestionCurrLineIsWingCommentTest extends IndentRulesTestCase {

  static IndentRuleQuestion _rule = new QuestionCurrLineIsWingComment(null, null);

  public void testWingComment() throws javax.swing.text.BadLocationException {
    _setDocText("// This is a wing comment");
    assertTrue("A valid wing comment 1", _rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("A valid wing comment 2", _rule.testApplyRule(_doc, 4, Indenter.IndentReason.OTHER));
    assertTrue("A valid wing comment 3", _rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
  }
  public void testSpaces() throws javax.swing.text.BadLocationException {
    _setDocText("/*\n \n*/");
    assertFalse("A block comment 1", _rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertFalse("A block comment 2", _rule.testApplyRule(_doc, 3, Indenter.IndentReason.OTHER));
    assertFalse("A block comment 3", _rule.testApplyRule(_doc, 5, Indenter.IndentReason.OTHER));
  }
  
  static String cornerCase = " //\n";
  
  public void testCornerCase() throws javax.swing.text.BadLocationException {
    _setDocText(cornerCase);
    assertFalse("Corner Case 1", _rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertFalse("Corner Case 2", _rule.testApplyRule(_doc, 1, Indenter.IndentReason.OTHER));
    assertFalse("Corner Case 3", _rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
    assertFalse("Corner Case 4", _rule.testApplyRule(_doc, 3, Indenter.IndentReason.OTHER));
  }
   
  public void testWingInsideBlock() throws javax.swing.text.BadLocationException {
    _setDocText("/*//\n \n */");
    assertFalse("Wing Inside BlockComment 1", _rule.testApplyRule(_doc, Indenter.IndentReason.OTHER));
    assertFalse("Wing Inside BlockComment 2", _rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
  }
}
