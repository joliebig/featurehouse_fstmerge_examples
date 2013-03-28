

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionStartAfterOpenBraceTest extends IndentRulesTestCase {
  private String _text;
  
  private IndentRuleQuestion _rule = new QuestionStartAfterOpenBrace(null, null);
  
  public void testNoBrace() throws BadLocationException {
    _text = "method(\nint[] a, String b) {}";
    _setDocText(_text);
    assertTrue("START has no preceding brace.", !_rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("START immediately follows an open paren, not a brace.", !_rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
    assertTrue("START immediately follows an open paren, not a brace.", !_rule.testApplyRule(_doc, _text.length()-1, Indenter.IndentReason.OTHER));
  }
  
  public void testRightAfterBrace() throws BadLocationException {
    
    _text = 
      "boolean method() {\n" +
      "}";
    
    _setDocText(_text);
    assertTrue("START immediately follows an open brace.", _rule.testApplyRule(_doc, 19, Indenter.IndentReason.OTHER));
    
    _text = 
      "boolean method(\n" +
      "    int[] a, String b)\n" +
      "{\n" +
      "}";
    
    _setDocText(_text); 
    assertTrue("START immediately follows an open paren.", !_rule.testApplyRule(_doc, 40, Indenter.IndentReason.OTHER));
    assertTrue("START immediately follows an open brace.", _rule.testApplyRule(_doc, 41, Indenter.IndentReason.OTHER));
  }
  
  public void testWSAfterBrace() throws BadLocationException {
    
    _text = 
      "if (<cond>) {\n" +
      "\n" +
      "    if (\n" +
      "        <cond>) { ... }}";
    
    _setDocText(_text);
    
    assertTrue("START immediatly follows an open brace.", _rule.testApplyRule(_doc, 14, Indenter.IndentReason.OTHER));
    assertTrue("Only WS between open brace and START.", _rule.testApplyRule(_doc, 15, Indenter.IndentReason.OTHER));
    assertTrue("Only WS between open brace and START.", _rule.testApplyRule(_doc, 23, Indenter.IndentReason.OTHER));
    assertTrue("START immediatly follows an open paren.", !_rule.testApplyRule(_doc, 25, Indenter.IndentReason.OTHER)); 
  }
  
  public void testCommentsAfterBrace() throws BadLocationException {
    
    _text = 
      "class Foo {   \n" +  
      "              \n" +  
      "  /*          \n" +  
      "   *          \n" +
      "   */         \n" +
      "  int field;  \n" +
      "}";
    
    _setDocText(_text);
    
    assertTrue("START = 0.", !_rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("START = 0.", !_rule.testApplyRule(_doc, 14, Indenter.IndentReason.OTHER));
    assertTrue("Only WS between START and open brace.", _rule.testApplyRule(_doc, 15, Indenter.IndentReason.OTHER));
    assertTrue("Only WS between START and open brace.", _rule.testApplyRule(_doc, 30, Indenter.IndentReason.OTHER));
    assertTrue("Only WS between START and open brace.", _rule.testApplyRule(_doc, 44, Indenter.IndentReason.OTHER));
    assertTrue("Only comment and WS between START and open brace.", _rule.testApplyRule(_doc, 45, Indenter.IndentReason.OTHER));
    assertTrue("Only comment and WS between START and open brace.", _rule.testApplyRule(_doc, 60, Indenter.IndentReason.OTHER));
    assertTrue("Only comment and WS between START and open brace.", _rule.testApplyRule(_doc, 77, Indenter.IndentReason.OTHER));
  }
  
  public void testBraceLastCharOnLine() throws BadLocationException {
    _setDocText("{\n");
    assertTrue("Brace only char on line.", _rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
    
    _setDocText("void foo() {\n");
    assertTrue("Brace last char on line.", _rule.testApplyRule(_doc, 13, Indenter.IndentReason.OTHER));
  }
  
  public void testTextAfterBrace() throws BadLocationException {
    _setDocText("{ hello\n  foo();");
    assertTrue("Text on line after brace.", _rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
  }
}

