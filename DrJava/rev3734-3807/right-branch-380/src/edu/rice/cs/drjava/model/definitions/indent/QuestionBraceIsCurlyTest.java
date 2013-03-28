

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionBraceIsCurlyTest extends IndentRulesTestCase {
  
  
  
  
  private String _text;
  
  private final IndentRuleQuestion _rule = new QuestionBraceIsCurly(null, null);
  
  public void testWithParen() throws BadLocationException {
    int i;
    
    
    
    _text = "boolean method(int[] a, String b) {}";
    _setDocText(_text);
    
    for (i = 0; i < _text.length(); i++)
      assertTrue("START has no brace.", !_rule.applyRule(_doc, i, Indenter.OTHER));
      
    
    
    _text = 
      "boolean method() {\n" +
      "}";
    
    _setDocText(_text);
    
    assertTrue("START has no brace.", !_rule.applyRule(_doc, 18, Indenter.OTHER));
    assertTrue("START's brace is curly brace.", _rule.applyRule(_doc, 19, Indenter.OTHER));
    
    
    
    _text = 
      "boolean method(\n" +
      "    int[] a, String b)\n" +
      "{}";
    
    _setDocText(_text);
    
    assertTrue("START is open curly brace.", !_rule.applyRule(_doc, _text.length() - 2, Indenter.OTHER));
    assertTrue("START is open curly brace.", !_rule.applyRule(_doc, _text.length() - 1, Indenter.OTHER));
    
    
    
    _text = 
      "if (<cond>) {\n" +
      "    if (\n" +
      "        <cond>) { ... }}";
    
    _setDocText(_text);
    
    assertTrue("START's brace is open curly brace.", _rule.applyRule(_doc, 14, Indenter.OTHER));
    assertTrue("START's brace is open curly brace.", _rule.applyRule(_doc, 22, Indenter.OTHER));
    assertTrue("START's brace is an open paren.", !_rule.applyRule(_doc, 23, Indenter.OTHER));
    
    
    
    _text = 
      "array[\n" +
      "    new Listener() {\n" +
      "        method() {\n" +
      "        }\n" +
      "    }]";
    
    _setDocText(_text);
    
    assertTrue("START has no brace.", !_rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("START's brace is open bracket.", !_rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("START's brace is an open curly brace.", _rule.applyRule(_doc, 28, Indenter.OTHER));
    assertTrue("START's brace is an open curly brace.", _rule.applyRule(_doc, 30, Indenter.OTHER));
    assertTrue("START's brace is an open curly brace.", _rule.applyRule(_doc, _text.length() - 1, Indenter.OTHER));
  }
  
  public void testOnlyCurly() throws BadLocationException
  {
    
    
    _text =
      "{ /* block1 */ }\n" +
      "{ /* block2 */ }\n" +
      "{ /* block3 */ }";
    
    _setDocText(_text);
    
    assertTrue("START has no brace.", !_rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("START has no brace.", !_rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("START has no brace.", !_rule.applyRule(_doc, 28, Indenter.OTHER));
    assertTrue("START has no brace.", !_rule.applyRule(_doc, 30, Indenter.OTHER));
    assertTrue("START has no brace.", !_rule.applyRule(_doc, _text.length() - 1, Indenter.OTHER));
    
    
    
    _text =
      "{\n" +
      "    {\n" +
      "        {}\n" +
      "    }\n" +
      "}";
    
    _setDocText(_text);
    
    assertTrue("START has no brace.", !_rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("START's brace is an open curly brace.", _rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("START's brace is an open curly brace.", _rule.applyRule(_doc, 18, Indenter.OTHER));
    assertTrue("START's brace is an open curly brace.", _rule.applyRule(_doc, 19, Indenter.OTHER));
    assertTrue("START's brace is an open curly brace.", _rule.applyRule(_doc, _text.length() - 1, Indenter.OTHER));
    
    
    
    _text =
      "class Foo {\n" +
      "}";
    _setDocText(_text);
    
    assertTrue("Close brace immediately after open brace.", _rule.applyRule(_doc, 12, Indenter.OTHER));
    
    
    
    _text =
      "class Foo {\n" +
      "  method m()\n" +
      "  {\n" +
      "  }\n" +
      "}";
    _setDocText(_text);
    
    assertTrue("Close brace immediately after open brace.", _rule.applyRule(_doc, 29, Indenter.OTHER));
  }
}

