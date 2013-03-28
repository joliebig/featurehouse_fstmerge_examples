

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionCurrLineStartsWithTest extends IndentRulesTestCase {

  
  public void testNoPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("{", null, null);
    
    
    _setDocText("foo();\n}\n");
    assertTrue("no open brace", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("line of close brace (no open brace)", !rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("line after close brace (no open brace)", !rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
    
    
    rule = new QuestionCurrLineStartsWith("}", null, null);
    _setDocText("{\nfoo();");
    assertTrue("no close brace", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
  }
  
  
  public void testStartsWithPrefixWithText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("}", null, null);
        
    
    _setDocText("foo();\n}bar();\n");
    assertTrue("line before brace (no space)", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("just before brace (no space)", rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("just after brace (no space)", rule.testApplyRule(_doc, 9, Indenter.IndentReason.OTHER));
    assertTrue("line after brace (no space)", !rule.testApplyRule(_doc, 15, Indenter.IndentReason.OTHER));
    
    
    rule = new QuestionCurrLineStartsWith("*", null, null);
    _setDocText("foo\n * comment\nbar");
    assertTrue("line before star (with space)", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("just before star (with space)", rule.testApplyRule(_doc, 4, Indenter.IndentReason.OTHER));
    assertTrue("just after star (with space)", rule.testApplyRule(_doc, 6, Indenter.IndentReason.OTHER));
    assertTrue("line after star (with space)", !rule.testApplyRule(_doc, 15, Indenter.IndentReason.OTHER));
  }
  
  
  public void testStartsWithPrefixNoText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("{", null, null);
    
    
    _setDocText("foo();\n{\nbar();\n");
    assertTrue("line before brace (no space)", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("just before brace (no space)", rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("just after brace (no space)", rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
    assertTrue("line after brace (no space)", !rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
    
    
    _setDocText("foo();\n   {\nbar();\n");
    assertTrue("line before brace (with space)", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("just before brace (with space)", rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("just after brace (with space)", rule.testApplyRule(_doc, 11, Indenter.IndentReason.OTHER));
    assertTrue("line after brace (with space)", !rule.testApplyRule(_doc, 14, Indenter.IndentReason.OTHER));
  }
  
  
  public void testMultipleCharPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith(".*.", null, null);
    
    
    _setDocText("*\n.*\n.*.\n.*.foo");
    assertTrue("star", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("dot star", !rule.testApplyRule(_doc, 2, Indenter.IndentReason.OTHER));
    assertTrue("dot star dot", rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("dot star dot text", rule.testApplyRule(_doc, 9, Indenter.IndentReason.OTHER));
  }
    
  
  public void testCommentedPrefixDontSearchComment() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("{", null, null);
    
    
    _setDocText("foo();\n// {\nbar();\n");
    assertTrue("just before brace", !rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("just after brace", !rule.testApplyRule(_doc, 11, Indenter.IndentReason.OTHER));
    assertTrue("line after brace", !rule.testApplyRule(_doc, 12, Indenter.IndentReason.OTHER));
  }

  
  public void testCommentedPrefixSearchComment() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("*", null, null);
    
    
    _setDocText("/**\n* \ncomment\n");
    assertTrue("line before star", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("just before star", rule.testApplyRule(_doc, 4, Indenter.IndentReason.OTHER));
    assertTrue("just after star", rule.testApplyRule(_doc, 6, Indenter.IndentReason.OTHER));
    assertTrue("line after star", !rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
  }
  
  
  public void testDoesNotStartWithPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("}", null, null);
    
    
    _setDocText("foo(); }\nbar();\n");
    assertTrue("before brace", !rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("just before brace", !rule.testApplyRule(_doc, 7, Indenter.IndentReason.OTHER));
    assertTrue("just after brace", !rule.testApplyRule(_doc, 8, Indenter.IndentReason.OTHER));
    assertTrue("line after brace", !rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
  }

  
  public void testPrefixAtEnd() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("}", null, null);
    
    _setDocText("void foo() {\n}");
    assertTrue("first line", !rule.testApplyRule(_doc, 3, Indenter.IndentReason.OTHER));
    assertTrue("end of first line", !rule.testApplyRule(_doc, 12, Indenter.IndentReason.OTHER));
    assertTrue("beginning of second line", rule.testApplyRule(_doc, 13, Indenter.IndentReason.OTHER));
    assertTrue("end of second line", rule.testApplyRule(_doc, 14, Indenter.IndentReason.OTHER));
  }
  
  
  public void testMultCharPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("abcdefg", null, null);
    
    _setDocText("   abcdefghij\n  abcde");
    assertTrue("first line, beginning", rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("first line, mid", rule.testApplyRule(_doc, 6, Indenter.IndentReason.OTHER));
    assertTrue("first line, end", rule.testApplyRule(_doc, 13, Indenter.IndentReason.OTHER));
    assertTrue("second line, beginning", !rule.testApplyRule(_doc, 14, Indenter.IndentReason.OTHER));
    assertTrue("second line, mid", !rule.testApplyRule(_doc, 18, Indenter.IndentReason.OTHER));
    assertTrue("second line, end", !rule.testApplyRule(_doc, 21, Indenter.IndentReason.OTHER));
  }
}