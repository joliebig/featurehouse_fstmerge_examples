

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionNewParenPhraseTest extends IndentRulesTestCase {

  
  public void testStartOfDocument() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("\nfoo();");
    assertTrue("first line", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("second line", !rule.applyRule(_doc, 2, Indenter.OTHER));
  }
  
  
  public void testNoParenDelims() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("foo\nbar.\ny");
    assertTrue("second line", !rule.applyRule(_doc, 4, Indenter.OTHER));
    assertTrue("third line", !rule.applyRule(_doc, 9, Indenter.OTHER));
  }
  
  
  public void testParenDelimsWithText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
        
    
    _setDocText("new Foo(\nx,\ny;\na[\nbar])\n{");
    assertTrue("line after paren", rule.applyRule(_doc, 9, Indenter.OTHER));
    assertTrue("line after comma", rule.applyRule(_doc, 12, Indenter.OTHER));
    assertTrue("line after semicolon", rule.applyRule(_doc, 15, Indenter.OTHER));
    assertTrue("line after bracket", rule.applyRule(_doc, 18, Indenter.OTHER));
    assertTrue("line after close paren", !rule.applyRule(_doc, 24, Indenter.OTHER));
  }
  
  
  public void testParenDelimsNoText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("(\n,\n;\n[\nfoo\nbar");
    assertTrue("line after paren", rule.applyRule(_doc, 2, Indenter.OTHER));
    assertTrue("line after comma", rule.applyRule(_doc, 4, Indenter.OTHER));
    assertTrue("line after semicolon", rule.applyRule(_doc, 6, Indenter.OTHER));
    assertTrue("line after bracket", rule.applyRule(_doc, 8, Indenter.OTHER));
    assertTrue("line after text", !rule.applyRule(_doc, 12, Indenter.OTHER));
  }
  
  
  public void testParenDelimsWithComment() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("for (int i; // comment\ni < 2; /** comment */\ni++) {");
    assertTrue("// comment", rule.applyRule(_doc, 23, Indenter.OTHER));
    assertTrue("/* */ comment", rule.applyRule(_doc, 45, Indenter.OTHER));
  }
  
  
  public void testMultipleBlankLinesBack() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("for(\n\nint i;\n\n\ni > 0;;\n)");
    assertTrue("line after open paren", rule.applyRule(_doc, 5, Indenter.OTHER));
    assertTrue("two lines after open paren", rule.applyRule(_doc, 6, Indenter.OTHER));
    assertTrue("line after semicolon", rule.applyRule(_doc, 13, Indenter.OTHER));
    assertTrue("two lines after semicolon", rule.applyRule(_doc, 16, Indenter.OTHER));
  }
  
  
  public void testMultipleCommentLinesBack() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("for(\n//\n/** foo * /int i;\n\n// bar\ni > 0;;\n)");
    assertTrue("line after open paren", rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("two lines after open paren", rule.applyRule(_doc, 18, Indenter.OTHER));
    assertTrue("line after semicolon", rule.applyRule(_doc, 25, Indenter.OTHER));
    assertTrue("two lines after semicolon", rule.applyRule(_doc, 28, Indenter.OTHER));
  }
  
  
  public void testDoesNotEndWithParenDelim() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("foo(bar.\nx,y\n)");
    assertTrue("line after paren", !rule.applyRule(_doc, 9, Indenter.OTHER));
    assertTrue("line after comma", !rule.applyRule(_doc, 13, Indenter.OTHER));
  }
  
  
  public void testOperatorDelim() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("foo(x +\ny\n)");
    assertTrue("line after operator", rule.applyRule(_doc, 8, Indenter.OTHER));
    assertTrue("line after comma", !rule.applyRule(_doc, 10, Indenter.OTHER));
  }
  
  
  public void testIgnoreDelimsOnLine() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionNewParenPhrase(null, null);
    
    
    _setDocText("foo(x.\ny()\n)");
    assertTrue("after paren, but not new phrase", !rule.applyRule(_doc, 10, Indenter.OTHER));
  }

}
