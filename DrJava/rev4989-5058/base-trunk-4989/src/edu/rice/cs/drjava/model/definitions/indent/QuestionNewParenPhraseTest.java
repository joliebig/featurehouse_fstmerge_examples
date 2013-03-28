

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionNewParenPhraseTest extends IndentRulesTestCase {

  
  public void testStartOfDocument() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("\nfoo();");
    assertTrue("first line", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("second line", !rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
  }
  
  
  public void testNoParenDelims() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("foo\nbar.\ny");
    assertTrue("second line", !rule.testApplyRule(_doc, 4, Indenter.IndentReason.OTHER));
    assertTrue("third line", !rule.testApplyRule(_doc, 9, Indenter.IndentReason.OTHER));
  }
  
  
  public void testParenDelimsWithText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
        
    
    _setDocText("new Foo(\nx,\ny;\na[\nbar])\n{");
    assertTrue("line after paren", rule.testApplyRule(_doc, 9, Indenter.IndentReason.OTHER));
    assertTrue("line after comma", rule.testApplyRule(_doc, 12, Indenter.IndentReason.OTHER));
    assertTrue("line after semicolon", rule.testApplyRule(_doc, 15, Indenter.IndentReason.OTHER));
    assertTrue("line after bracket", rule.testApplyRule(_doc, 18, Indenter.IndentReason.OTHER));
    assertTrue("line after close paren", !rule.testApplyRule(_doc, 24, Indenter.IndentReason.OTHER));
  }
  
  
  public void testParenDelimsNoText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("(\n,\n;\n[\nfoo\nbar");
    assertTrue("line after paren", rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
    assertTrue("line after comma", rule.testApplyRule(_doc, 4, Indenter.IndentReason.OTHER));
    assertTrue("line after semicolon", rule.testApplyRule(_doc, 6, Indenter.IndentReason.OTHER));
    assertTrue("line after bracket", rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
    assertTrue("line after text", !rule.testApplyRule(_doc, 12, Indenter.IndentReason.OTHER));
  }
  
  
  public void testParenDelimsWithComment() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("for (int i; // comment\ni < 2; /** comment */\ni++) {");
    assertTrue("// comment", rule.testApplyRule(_doc, 23, Indenter.IndentReason.OTHER));
    assertTrue("/* */ comment", rule.testApplyRule(_doc, 45, Indenter.IndentReason.OTHER));
  }
  
  
  public void testMultipleBlankLinesBack() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("for(\n\nint i;\n\n\ni > 0;;\n)");
    assertTrue("line after open paren", rule.testApplyRule(_doc, 5, Indenter.IndentReason.OTHER));
    assertTrue("two lines after open paren", rule.testApplyRule(_doc, 6, Indenter.IndentReason.OTHER));
    assertTrue("line after semicolon", rule.testApplyRule(_doc, 13, Indenter.IndentReason.OTHER));
    assertTrue("two lines after semicolon", rule.testApplyRule(_doc, 16, Indenter.IndentReason.OTHER));
  }
  
  
  public void testMultipleCommentLinesBack() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("for(\n//\n/** foo * /int i;\n\n// bar\ni > 0;;\n)");
    assertTrue("line after open paren", rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("two lines after open paren", rule.testApplyRule(_doc, 18, Indenter.IndentReason.OTHER));
    assertTrue("line after semicolon", rule.testApplyRule(_doc, 25, Indenter.IndentReason.OTHER));
    assertTrue("two lines after semicolon", rule.testApplyRule(_doc, 28, Indenter.IndentReason.OTHER));
  }
  
  
  public void testDoesNotEndWithParenDelim() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("foo(bar.\nx,y\n)");
    assertTrue("line after paren", !rule.testApplyRule(_doc, 9, Indenter.IndentReason.OTHER));
    assertTrue("line after comma", !rule.testApplyRule(_doc, 13, Indenter.IndentReason.OTHER));
  }
  
  
  public void testOperatorDelim() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("foo(x +\ny\n)");
    assertTrue("line after operator", rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
    assertTrue("line after comma", !rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
  }
  
  
  public void testIgnoreDelimsOnLine() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("foo(x.\ny()\n)");
    assertTrue("after paren, but not new phrase", !rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
  }

}
