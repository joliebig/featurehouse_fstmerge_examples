

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionExistsCharInStmtTest extends IndentRulesTestCase {
  
  
  public void testColonInTernaryOpOneLineStmts() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionExistsCharInStmt('?', ':', null, null);
   
    
    _setDocText("case 1: foo()\ncase default: break;\n");
    _doc.setCurrentLocation(0);
    assertTrue("colon not in ternary op, one line stmt, no '?'",
        !rule.applyRule(_doc, Indenter.OTHER));
    _doc.setCurrentLocation(16);
    assertTrue("after newline (colon not in ternary op, one line stmt, no '?')",
        !rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("foo();\nreturn (test ? x : y;)\n");
    _doc.setCurrentLocation(10);
    assertTrue("colon in ternary op, same line", 
        rule.applyRule(_doc, Indenter.OTHER));
  }

  
  public void testColonInTernaryOpTwoStmtsOnOneLine() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionExistsCharInStmt('?', ':', null, null);

    
    _setDocText("foo();\nreturn (test ? x : y); case default: break;\n");
    _doc.setCurrentLocation(7);
    assertTrue("colon in ternary op, two stmts on one line",
        rule.applyRule(_doc, Indenter.OTHER));
    
    _setDocText("foo();\ncase default: break; return test ? x : y;\n");
    
    _doc.setCurrentLocation(7);
    assertTrue("colon not in ternary op, two stmts on one line",
        !rule.applyRule(_doc, Indenter.OTHER));
  }

  
  public void testColonInTernaryOpMultiLineStmts() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionExistsCharInStmt('?', ':', null, null);

    
    _setDocText("foo();\nreturn test ?\nx : y;\n");
    _doc.setCurrentLocation(22);
    assertTrue("colon in ternary op, multi-line stmt",
        rule.applyRule(_doc, Indenter.OTHER));
  }

  
  public void testColonInTernaryOpIgnoreWhitespaceAndComments() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionExistsCharInStmt('?', ':', null, null);

    
    _setDocText("foo;\nreturn test ?\n    \n \t \nx : y;\n");
    _doc.setCurrentLocation(28);
    assertTrue("colon in ternary op, multi-line stmt, ignores whitespace",
        rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("foo();\nreturn test ? //{\n//case 1: bar();\nx() : y();\n");
    _doc.setCurrentLocation(42);
    assertTrue("colon in ternary op, ignores single line comments",
        rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("foo();\nreturn test ? /* {\ncase 1 : bar();*/\nx() : y();\n");
    _doc.setCurrentLocation(44);
    assertTrue("colon in ternary op, ignores multi-line comments",
        rule.applyRule(_doc, Indenter.OTHER));
  }

  
  public void testColonNotInTernaryOpDueToQuestionMarkInCommentsOrQuotes() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionExistsCharInStmt('?', ':', null, null);

    
    _setDocText("foo();\nreturn test; //?\ncase default: break;\n");
    _doc.setCurrentLocation(38);
    assertTrue("colon not in ternary op, ignores '?' in single-line comments",
        !rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("foo();\nreturn test; /* huh? okay */\ncase default: break;\n");
    _doc.setCurrentLocation(36);
    assertTrue("colon not in ternary op, ignores '?' in multi-line comments",
        !rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("foo();\nreturn str + \"?\";\ncase default: break;\n");
    _doc.setCurrentLocation(25);
    assertTrue("colon not in ternary op, ignores '?' in quotes",
        !rule.applyRule(_doc, Indenter.OTHER));

  }

  
  public void testColonNotInTernaryOpMultiLineStmts() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionExistsCharInStmt('?', ':', null, null);

    
    _setDocText("return test ? x : y;\ncase 1\n: foo();\n");
    _doc.setCurrentLocation(28);
    assertTrue("colon not in ternary op, multi-line stmt",
        !rule.applyRule(_doc, Indenter.OTHER));

    
    
    _setDocText("foo()\nreturn test ? x :\ny; case default: break;\n");
    _doc.setCurrentLocation(24);
    assertTrue("colon not in ternary op, multi-line stmt, same line as end of ternary op",
        !rule.applyRule(_doc, Indenter.OTHER));
  }
}


