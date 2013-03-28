

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionBraceIsParenOrBracketTest extends IndentRulesTestCase {
  
  
  private String _text;
  
  private final IndentRuleQuestion _rule = new QuestionBraceIsParenOrBracket(null, null);
  
  public void testParen() throws BadLocationException {
    int i;
    
    
    _text = "boolean method(int[] a, String b)";
    _setDocText(_text);
    
    for (i = 0; i < _text.length(); i++)
      assertTrue("START has no brace.", !_rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    
    _text =
      "boolean method\n" +
      "    (int[] a, String b)";
    
    _setDocText(_text);
    
    for (i = 0; i < _text.length(); i++)
      assertTrue("START has no brace.", !_rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    
    _text =
      "boolean method(\n" +
      "    int[] a, String b)";
    
    _setDocText(_text);
    
    for (i = 0; i < 16; i++)
      assertTrue("START has no brace.", !_rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    
    
    for (i = 16; i < _text.length(); i++)
      assertTrue("START's brace is an open paren.", _rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    
    
    _text =
      "if (<cond>) {\n" +
      "    if (\n" +
      "        <cond>) { ... }}";
    
    _setDocText(_text);
    
    for (i = 0; i < 23; i++)
      assertTrue("START has no brace.", !_rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    
    
    for (i = 23; i < _text.length(); i++)
      assertTrue("START's brace is an open paren.", _rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    
    
    _text =
      "method(\n" +
      "       array1, foo(array1[x]))\n" +
      " <other stuff>";
    
    _setDocText(_text);
    
    assertTrue("START has no brace.", !_rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("START has no brace", !_rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("START's brace is an open paren.", _rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
    assertTrue("START's brace is an open paren.", _rule.testApplyRule(_doc, 30, Indenter.IndentReason.OTHER));
    assertTrue("START has no brace.", !_rule.testApplyRule(_doc, _text.length() - 1, Indenter.IndentReason.OTHER));
  }
  
  public void testBracket() throws BadLocationException
  {
    int i;
    
    
    
    _text =
      "boolean method(int[\n" +
      "                   ELTS]\n" +
      "               a, String b)";
    
    _setDocText(_text);
    
    for (i = 0; i < 20; i++)
      assertTrue("START has no brace.", !_rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    
    
    for (i = 20; i < 29; i++)
      assertTrue("START's brace is an open bracket.", _rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    for (i = 29; i < _text.length(); i++)
      assertTrue("START's brace is an open paren.", _rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    
    
    _text = "array1[i]\n" +
      "       [j]";
    
    _setDocText(_text);
    
    for (i = 0; i < _text.length(); i++)
      assertTrue("START has no brace.", !_rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    
    
    _text =
      "array1[\n" +
      "           i][\n" +
      "              j]";
    
    _setDocText(_text);
    
    assertTrue("START's paren is an open bracket.", _rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
    assertTrue("START's paren is an open bracket.", _rule.testApplyRule(_doc, 22, Indenter.IndentReason.OTHER));
    assertTrue("START's paren is an open bracket.", _rule.testApplyRule(_doc, 23, Indenter.IndentReason.OTHER));
  }
  
  public void testCurly() throws BadLocationException
  {
    
    
    _text =
      "class X extends Base\n" +
      "{\n" +
      "}";
    
    _setDocText(_text);
    
    assertTrue("START has no brace.", !_rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("START has no brace.", !_rule.testApplyRule(_doc, 20, Indenter.IndentReason.OTHER));
    assertTrue("START is curly brace.", !_rule.testApplyRule(_doc, 21, Indenter.IndentReason.OTHER));
    assertTrue("START is close brace.", !_rule.testApplyRule(_doc, 23, Indenter.IndentReason.OTHER));
    
    
    
    _text =
      "class X extends Base\n" +
      "{\n" +
      "    int bla() { return 44; }\n" +
      "}";
    
    _setDocText(_text);
    
    assertTrue("START has no brace.", !_rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("START has no brace.", !_rule.testApplyRule(_doc, 20, Indenter.IndentReason.OTHER));
    assertTrue("START is curly brace.", !_rule.testApplyRule(_doc, 21, Indenter.IndentReason.OTHER));
    assertTrue("START's brace is curly brace.", !_rule.testApplyRule(_doc, 23, Indenter.IndentReason.OTHER));
    assertTrue("START is close curly brace.", !_rule.testApplyRule(_doc, _text.length() - 1, Indenter.IndentReason.OTHER));
    
    
    
    _text =
      "class X extends Base\n" +
      "{}\n" +
      "class Y extends Base\n" +
      "{}";
    
    _setDocText(_text);
    
    assertTrue("START has no brace.", !_rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("START has no brace.", !_rule.testApplyRule(_doc, 20, Indenter.IndentReason.OTHER));
    assertTrue("START is open curly brace.", !_rule.testApplyRule(_doc, 21, Indenter.IndentReason.OTHER));
    assertTrue("START has no brace.", !_rule.testApplyRule(_doc, 24, Indenter.IndentReason.OTHER));
    assertTrue("START's brace is open curly brace.", !_rule.testApplyRule(_doc, _text.length() - 1, Indenter.IndentReason.OTHER));
  }
  
  public void testBracketWithArrayComprehension() throws BadLocationException {
    int i;
    
    
    
    _text =
      "f(int[\n" +
      "                   ] {1, 2, 3},\n" +
      "               a, String b)";
    
    _setDocText(_text);
    
    for (i = 0; i < 7; i++)
      assertTrue("START has no brace.", !_rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    
    
    for (i = 7; i < 27; i++)
      assertTrue("START's brace is an open bracket.", _rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
    
    for (i = 27; i < _text.length(); i++)
      assertTrue("START's brace is an open paren.", _rule.testApplyRule(_doc, i, Indenter.IndentReason.OTHER));
  }
}






