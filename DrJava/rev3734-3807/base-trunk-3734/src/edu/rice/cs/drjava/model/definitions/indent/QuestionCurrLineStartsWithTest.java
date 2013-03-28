

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionCurrLineStartsWithTest extends IndentRulesTestCase {

  
  public void testNoPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("{", null, null);
    
    
    _setDocText("foo();\n}\n");
    assertTrue("no open brace", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("line of close brace (no open brace)", !rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("line after close brace (no open brace)", !rule.applyRule(_doc, 8, Indenter.OTHER));
    
    
    rule = new QuestionCurrLineStartsWith("}", null, null);
    _setDocText("{\nfoo();");
    assertTrue("no close brace", !rule.applyRule(_doc, 0, Indenter.OTHER));
  }
  
  
  public void testStartsWithPrefixWithText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("}", null, null);
        
    
    _setDocText("foo();\n}bar();\n");
    assertTrue("line before brace (no space)", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("just before brace (no space)", rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("just after brace (no space)", rule.applyRule(_doc, 9, Indenter.OTHER));
    assertTrue("line after brace (no space)", !rule.applyRule(_doc, 15, Indenter.OTHER));
    
    
    rule = new QuestionCurrLineStartsWith("*", null, null);
    _setDocText("foo\n * comment\nbar");
    assertTrue("line before star (with space)", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("just before star (with space)", rule.applyRule(_doc, 4, Indenter.OTHER));
    assertTrue("just after star (with space)", rule.applyRule(_doc, 6, Indenter.OTHER));
    assertTrue("line after star (with space)", !rule.applyRule(_doc, 15, Indenter.OTHER));
  }
  
  
  public void testStartsWithPrefixNoText() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("{", null, null);
    
    
    _setDocText("foo();\n{\nbar();\n");
    assertTrue("line before brace (no space)", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("just before brace (no space)", rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("just after brace (no space)", rule.applyRule(_doc, 8, Indenter.OTHER));
    assertTrue("line after brace (no space)", !rule.applyRule(_doc, 10, Indenter.OTHER));
    
    
    _setDocText("foo();\n   {\nbar();\n");
    assertTrue("line before brace (with space)", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("just before brace (with space)", rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("just after brace (with space)", rule.applyRule(_doc, 11, Indenter.OTHER));
    assertTrue("line after brace (with space)", !rule.applyRule(_doc, 14, Indenter.OTHER));
  }
  
  
  public void testMultipleCharPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith(".*.", null, null);
    
    
    _setDocText("*\n.*\n.*.\n.*.foo");
    assertTrue("star", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("dot star", !rule.applyRule(_doc, 2, Indenter.OTHER));
    assertTrue("dot star dot", rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("dot star dot text", rule.applyRule(_doc, 9, Indenter.OTHER));
  }
    
  
  public void testCommentedPrefixDontSearchComment() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("{", null, null);
    
    
    _setDocText("foo();\n// {\nbar();\n");
    assertTrue("just before brace", !rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("just after brace", !rule.applyRule(_doc, 11, Indenter.OTHER));
    assertTrue("line after brace", !rule.applyRule(_doc, 12, Indenter.OTHER));
  }

  
  public void testCommentedPrefixSearchComment() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("*", null, null);
    
    
    _setDocText("/**\n* \ncomment\n");
    assertTrue("line before star", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("just before star", rule.applyRule(_doc, 4, Indenter.OTHER));
    assertTrue("just after star", rule.applyRule(_doc, 6, Indenter.OTHER));
    assertTrue("line after star", !rule.applyRule(_doc, 7, Indenter.OTHER));
  }
  
  
  public void testDoesNotStartWithPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("}", null, null);
    
    
    _setDocText("foo(); }\nbar();\n");
    assertTrue("before brace", !rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("just before brace", !rule.applyRule(_doc, 7, Indenter.OTHER));
    assertTrue("just after brace", !rule.applyRule(_doc, 8, Indenter.OTHER));
    assertTrue("line after brace", !rule.applyRule(_doc, 10, Indenter.OTHER));
  }

  
  public void testPrefixAtEnd() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("}", null, null);
    
    _setDocText("void foo() {\n}");
    assertTrue("first line", !rule.applyRule(_doc, 3, Indenter.OTHER));
    assertTrue("end of first line", !rule.applyRule(_doc, 12, Indenter.OTHER));
    assertTrue("beginning of second line", rule.applyRule(_doc, 13, Indenter.OTHER));
    assertTrue("end of second line", rule.applyRule(_doc, 14, Indenter.OTHER));
  }
  
  
  public void testMultCharPrefix() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionCurrLineStartsWith("abcdefg", null, null);
    
    _setDocText("   abcdefghij\n  abcde");
    assertTrue("first line, beginning", rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("first line, mid", rule.applyRule(_doc, 6, Indenter.OTHER));
    assertTrue("first line, end", rule.applyRule(_doc, 13, Indenter.OTHER));
    assertTrue("second line, beginning", !rule.applyRule(_doc, 14, Indenter.OTHER));
    assertTrue("second line, mid", !rule.applyRule(_doc, 18, Indenter.OTHER));
    assertTrue("second line, end", !rule.applyRule(_doc, 21, Indenter.OTHER));
  }
}