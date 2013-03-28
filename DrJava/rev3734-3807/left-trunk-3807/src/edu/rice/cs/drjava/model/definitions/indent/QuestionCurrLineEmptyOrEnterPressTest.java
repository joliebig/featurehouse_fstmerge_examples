



package edu.rice.cs.drjava.model.definitions.indent;


public final class QuestionCurrLineEmptyOrEnterPressTest extends IndentRulesTestCase {

  static IndentRuleQuestion _rule = new QuestionCurrLineEmptyOrEnterPress(null, null);

   
  public void testEmpty() throws javax.swing.text.BadLocationException {
    
    
    
    _setDocText("/*\n\n*/");
    
    assertTrue("nothing on line", _rule.applyRule(_doc, 3, Indenter.OTHER));
  }
  public void testSpaces() throws javax.swing.text.BadLocationException {
    
    
    
    _setDocText("/*\n        \n*/");
    
    assertTrue("only spaces", _rule.applyRule(_doc, 6, Indenter.OTHER));
  }
  
  static String stuffExample = "/*\n   foo   \n*/";
  
  
  
  
  
  public void testStuffBefore() throws javax.swing.text.BadLocationException {
    _setDocText(stuffExample);
    assertTrue("text before the cursor", !_rule.applyRule(_doc, 3, Indenter.OTHER));
  }
  public void testStuffAfter() throws javax.swing.text.BadLocationException {
    _setDocText(stuffExample);
    assertTrue("text after the cursor", !_rule.applyRule(_doc, 11, Indenter.OTHER));
  }
  
  public void testLineWithStar() throws javax.swing.text.BadLocationException {
    _setDocText("/*\n * foo\n */");
    assertTrue("line with a star", !_rule.applyRule(_doc, 5, Indenter.OTHER));
  }
}
