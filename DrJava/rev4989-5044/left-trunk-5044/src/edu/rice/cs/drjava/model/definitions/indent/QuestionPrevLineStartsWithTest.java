

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionPrevLineStartsWithTest extends IndentRulesTestCase {

  
  public void testNoPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("{", null, null);
    
    
    _setDocText("}\nfoo();\n}\n");
    assertTrue("line after close brace (no open brace)", !rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
    assertTrue("line after text (no open brace)", !rule.testApplyRule(_doc, 9, Indenter.IndentReason.OTHER));
    assertTrue("line after text (no open brace)", !rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
    
    
    rule = new QuestionPrevLineStartsWith("*", null, null);
    _setDocText("{\nfoo();");
    assertTrue("no star", !rule.testApplyRule(_doc, 6, Indenter.IndentReason.OTHER));
    
  }
  
  
  public void testStartOfDocument() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("{", null, null);
    
    
    _setDocText("\nfoo();");
    assertTrue("first line", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("second line", !rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
  }
  
  
  public void testPrefixOnCurrLine() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("}", null, null);
    
    
    _setDocText("} foo();");
    assertTrue("before brace", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("after brace", !rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
    
    
    _setDocText("foo();\n bar(); } foo();");
    assertTrue("before brace", !rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("after brace", !rule.testApplyRule(_doc, 18, Indenter.IndentReason.OTHER));
  }
  
  
  public void testStartsWithPrefixWithText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("}", null, null);
        
    
    _setDocText("}bar();\nfoo();\nbar();");
    assertTrue("line of brace (no space)", !rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
    assertTrue("line after brace (no space)", rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
    assertTrue("two lines after brace (no space)", !rule.testApplyRule(_doc, 16, Indenter.IndentReason.OTHER));
    
    
    rule = new QuestionPrevLineStartsWith("*", null, null);
    _setDocText("foo\n * comment\nbar");
    assertTrue("just before star (with space)", !rule.testApplyRule(_doc, 4, Indenter.IndentReason.OTHER));
    assertTrue("just after star (with space)", !rule.testApplyRule(_doc, 6, Indenter.IndentReason.OTHER));
    assertTrue("line after star (with space)", rule.testApplyRule(_doc, 16, Indenter.IndentReason.OTHER));
  }
  
  
  public void testStartsWithPrefixNoText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("*", null, null);
    
    
    _setDocText("foo();\n*\nbar();\n}");
    assertTrue("line of star (no space)", !rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
    assertTrue("line after star (no space)", rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
    assertTrue("two lines after star (no space)", !rule.testApplyRule(_doc, 16, Indenter.IndentReason.OTHER));
    
    
    _setDocText("foo();\n   * \nbar();\n{");
    assertTrue("line of star (with space)", !rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("just after star (with space)", !rule.testApplyRule(_doc, 11, Indenter.IndentReason.OTHER));
    assertTrue("line after star (with space)", rule.testApplyRule(_doc, 13, Indenter.IndentReason.OTHER));
  }
  
  
  public void testMultipleCharPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("* ", null, null);
    
    
    _setDocText("*\n *\n * \n * foo\nbar");
    assertTrue("star", !rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
    assertTrue("space star", !rule.testApplyRule(_doc, 5, Indenter.IndentReason.OTHER));
    assertTrue("space star space", rule.testApplyRule(_doc, 11, Indenter.IndentReason.OTHER));
    assertTrue("space star space text", rule.testApplyRule(_doc, 16, Indenter.IndentReason.OTHER));
  }
  
  
  public void testCommentedPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("*", null, null);
    
    
    _setDocText("/**\n* \ncomment\n*/");
    assertTrue("just before star", !rule.testApplyRule(_doc, 4, Indenter.IndentReason.OTHER));
    assertTrue("just after star", !rule.testApplyRule(_doc, 6, Indenter.IndentReason.OTHER));
    assertTrue("line after star", rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("line after star", !rule.testApplyRule(_doc, 15, Indenter.IndentReason.OTHER));
  }
  
  
  public void testCommentPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("/**", null, null);
    
    
    _setDocText("/**\n* \ncomment\n*/");
    assertTrue("just before star", rule.testApplyRule(_doc, 4, Indenter.IndentReason.OTHER));
    assertTrue("just after star", rule.testApplyRule(_doc, 6, Indenter.IndentReason.OTHER));
    assertTrue("line after star", !rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("line after star", !rule.testApplyRule(_doc, 15, Indenter.IndentReason.OTHER));
  }
  
  
  public void testDoesNotStartWithPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("*", null, null);
    
    
    _setDocText("foo(); *\nbar();\n");
    assertTrue("line after star", !rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
  }


}
