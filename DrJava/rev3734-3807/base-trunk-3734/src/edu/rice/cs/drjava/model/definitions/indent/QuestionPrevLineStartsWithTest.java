

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionPrevLineStartsWithTest extends IndentRulesTestCase {

  
  public void testNoPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("{", null, null);
    
    
    _setDocText("}\nfoo();\n}\n");
    assertTrue("line after close brace (no open brace)", !rule.applyRule(_doc, 2, Indenter.OTHER));
    assertTrue("line after text (no open brace)", !rule.applyRule(_doc, 9, Indenter.OTHER));
    assertTrue("line after text (no open brace)", !rule.applyRule(_doc, 10, Indenter.OTHER));
    
    
    rule = new QuestionPrevLineStartsWith("*", null, null);
    _setDocText("{\nfoo();");
    assertTrue("no star", !rule.applyRule(_doc, 6, Indenter.OTHER));
    
  }
  
  
  public void testStartOfDocument() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("{", null, null);
    
    
    _setDocText("\nfoo();");
    assertTrue("first line", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("second line", !rule.applyRule(_doc, 2, Indenter.OTHER));
  }
  
  
  public void testPrefixOnCurrLine() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("}", null, null);
    
    
    _setDocText("} foo();");
    assertTrue("before brace", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("after brace", !rule.applyRule(_doc, 2, Indenter.OTHER));
    
    
    _setDocText("foo();\n bar(); } foo();");
    assertTrue("before brace", !rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("after brace", !rule.applyRule(_doc, 18, Indenter.OTHER));
  }
  
  
  public void testStartsWithPrefixWithText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("}", null, null);
        
    
    _setDocText("}bar();\nfoo();\nbar();");
    assertTrue("line of brace (no space)", !rule.applyRule(_doc, 2, Indenter.OTHER));
    assertTrue("line after brace (no space)", rule.applyRule(_doc, 8, Indenter.OTHER));
    assertTrue("two lines after brace (no space)", !rule.applyRule(_doc, 16, Indenter.OTHER));
    
    
    rule = new QuestionPrevLineStartsWith("*", null, null);
    _setDocText("foo\n * comment\nbar");
    assertTrue("just before star (with space)", !rule.applyRule(_doc, 4, Indenter.OTHER));
    assertTrue("just after star (with space)", !rule.applyRule(_doc, 6, Indenter.OTHER));
    assertTrue("line after star (with space)", rule.applyRule(_doc, 16, Indenter.OTHER));
  }
  
  
  public void testStartsWithPrefixNoText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("*", null, null);
    
    
    _setDocText("foo();\n*\nbar();\n}");
    assertTrue("line of star (no space)", !rule.applyRule(_doc, 8, Indenter.OTHER));
    assertTrue("line after star (no space)", rule.applyRule(_doc, 10, Indenter.OTHER));
    assertTrue("two lines after star (no space)", !rule.applyRule(_doc, 16, Indenter.OTHER));
    
    
    _setDocText("foo();\n   * \nbar();\n{");
    assertTrue("line of star (with space)", !rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("just after star (with space)", !rule.applyRule(_doc, 11, Indenter.OTHER));
    assertTrue("line after star (with space)", rule.applyRule(_doc, 13, Indenter.OTHER));
  }
  
  
  public void testMultipleCharPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("* ", null, null);
    
    
    _setDocText("*\n *\n * \n * foo\nbar");
    assertTrue("star", !rule.applyRule(_doc, 2, Indenter.OTHER));
    assertTrue("space star", !rule.applyRule(_doc, 5, Indenter.OTHER));
    assertTrue("space star space", rule.applyRule(_doc, 11, Indenter.OTHER));
    assertTrue("space star space text", rule.applyRule(_doc, 16, Indenter.OTHER));
  }
  
  
  public void testCommentedPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("*", null, null);
    
    
    _setDocText("/**\n* \ncomment\n*/");
    assertTrue("just before star", !rule.applyRule(_doc, 4, Indenter.OTHER));
    assertTrue("just after star", !rule.applyRule(_doc, 6, Indenter.OTHER));
    assertTrue("line after star", rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("line after star", !rule.applyRule(_doc, 15, Indenter.OTHER));
  }
  
  
  public void testDoesNotStartWithPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionPrevLineStartsWith("*", null, null);
    
    
    _setDocText("foo(); *\nbar();\n");
    assertTrue("line after star", !rule.applyRule(_doc, 10, Indenter.OTHER));
  }


}
