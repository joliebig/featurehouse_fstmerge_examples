

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class ActionDoNothingTest extends IndentRulesTestCase {


  
  public void testEmptyString() throws BadLocationException {
    IndentRuleAction rule = new ActionDoNothing();
    String text = "";
    
    _setDocText(text);
    rule.indentLine(_doc, 0, Indenter.OTHER);
    _assertContents(text);
  }
  
  
  public void testNoLeadingSpaces() throws BadLocationException {
    IndentRuleAction rule = new ActionDoNothing();
    String text = "foo();\nbar();";
    
    
    _setDocText(text);
    rule.indentLine(_doc, 0, Indenter.OTHER);
    _assertContents(text);
    
    
    rule.indentLine(_doc, 9, Indenter.OTHER);
    _assertContents(text);
  }

  
  public void testLeadingSpaces() throws BadLocationException {
    IndentRuleAction rule = new ActionDoNothing();
    String text = "  foo();\n     bar();";
    
    
    _setDocText(text);
    rule.indentLine(_doc, 0, Indenter.OTHER);
    _assertContents(text);
    
    
    rule.indentLine(_doc, 10, Indenter.OTHER);
    _assertContents(text);
  }

}
