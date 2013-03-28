

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class ActionStartPrevStmtPlusTest extends IndentRulesTestCase {
  
  public void testNoPrevStmt() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartPrevStmtPlus(0, true);
    IndentRuleAction rule2 = new ActionStartPrevStmtPlus(2, true);

    _setDocText("foo();\n");
    rule1.testIndentLine(_doc, 2, Indenter.IndentReason.OTHER);
    assertEquals("no prev stmt, no suffix", "foo();\n", _doc.getText());
    
    _setDocText("foo();\n");
    rule2.testIndentLine(_doc, 2, Indenter.IndentReason.OTHER);
    assertEquals("no prev stmt, suffix two spaces", "  foo();\n", _doc.getText());
  }
  
  public void testPrevStmtPrevLine() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartPrevStmtPlus(0, true);
    IndentRuleAction rule2 = new ActionStartPrevStmtPlus(2, true);

    _setDocText("  foo().\n//boo();\n/*y=x+1;\nfoo(){}*/\nbar();\nbiz();\n");

    rule1.testIndentLine(_doc, 44, Indenter.IndentReason.OTHER);

    assertEquals("prev stmt on prev line, no suffix",
                 "  foo().\n//boo();\n/*y=x+1;\nfoo(){}*/\nbar();\n  biz();\n",
                 _doc.getText());
    
    _setDocText("  foo().\n//boo();\n/*y=x+1;\nfoo(){}*/\nbar();\nbiz();\n");

    rule2.testIndentLine(_doc, 44, Indenter.IndentReason.OTHER);

    assertEquals("prev stmt on prev line, suffix two spaces",
                 "  foo().\n//boo();\n/*y=x+1;\nfoo(){}*/\nbar();\n    biz();\n",
                 _doc.getText());
  }

  public void testPrevStmtSeveralLinesBeforeCurrLocation() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartPrevStmtPlus(0, true);
    IndentRuleAction rule2 = new ActionStartPrevStmtPlus(2, true);
    
    _setDocText("  foo();\n//y=x+1;\n/*void blah {\n}*/\n  ';' + blah.\n//foo\nx;\n");
    rule1.testIndentLine(_doc, 56, Indenter.IndentReason.OTHER);
    assertEquals("prev stmt serveral lines before, no suffix",
                 "  foo();\n//y=x+1;\n/*void blah {\n}*/\n  ';' + blah.\n//foo\n  x;\n",
                 _doc.getText());
    
    _setDocText("  foo();\n//y=x+1;\n/*void blah {\n}*/\n  ';' + blah.\n//foo\nx;\n");
    rule2.testIndentLine(_doc, 56, Indenter.IndentReason.OTHER);
    assertEquals("prev stmt serveral lines before, suffix two spaces", 
                 "  foo();\n//y=x+1;\n/*void blah {\n}*/\n  ';' + blah.\n//foo\n    x;\n",
                 _doc.getText());
  }
  
  public void testColonNotDelim() throws BadLocationException {
    IndentRuleAction rule = new ActionStartPrevStmtPlus(0, false);
    
    _setDocText("test2 = x ? y :\n" +     
                "  z;\n" +     
                "foo();\n");     
    rule.testIndentLine(_doc, 21, Indenter.IndentReason.OTHER);
    assertEquals("Colon is not a delimiter",
                 "test2 = x ? y :\n" +     
                 "  z;\n" +     
                 "foo();\n",
                 _doc.getText());
  }


  public void testAfterArrayAssign() throws BadLocationException {
    IndentRuleAction rule = new ActionStartPrevStmtPlus(0, false);
    
    _setDocText("a = {\n" +
                "  b,c,d\n" + 
                "};\n" +
                "   a;");     
    
    rule.testIndentLine(_doc, 17, Indenter.IndentReason.OTHER);
    assertEquals("After array assignment",
                 "a = {\n" + 
                 "  b,c,d\n" +
                 "};\n" +
                 "a;",
                 _doc.getText());
  }
  public void testAfterArrayAssignMultiSemi() throws BadLocationException {
    IndentRuleAction rule = new ActionStartPrevStmtPlus(0, false);
    
    _setDocText("a = {\n" +
                "  b,c,d\n" + 
                "};;;\n" +
                "   a;");     
    
    rule.testIndentLine(_doc, 19, Indenter.IndentReason.OTHER);
    assertEquals("After array assignment multi semi colons",
                 "a = {\n" + 
                 "  b,c,d\n" +
                 "};;;\n" +
                 "a;",
                 _doc.getText());
  }
  
  public void testAnonymousInnerClassAssign() throws BadLocationException {
    IndentRuleAction rule = new ActionStartPrevStmtPlus(0, false);
    
    _setDocText("Runnable command = new Runnable() {\n" + 
                "  public void run() { ... }\n" +
                "};\n" +
                "   command.run();");  
    rule.testIndentLine(_doc, 67, Indenter.IndentReason.OTHER);
    assertEquals("After anonymous inner class assignment",
                 "Runnable command = new Runnable() {\n" + 
                "  public void run() { ... }\n" +
                "};\n" +
                "command.run();",
                 _doc.getText());
  }
  
  public void testAnonymousInnerClassArg() throws BadLocationException {
    IndentRuleAction rule = new ActionStartPrevStmtPlus(0, false);
    
    _setDocText("setCommand(new Runnable() {\n" + 
                "  public void run() { ... }\n" +
                "});\n" +
                "   command.run();");  
    rule.testIndentLine(_doc, 60, Indenter.IndentReason.OTHER);
    assertEquals("After method call with anonymous inner class argument",
                 "setCommand(new Runnable() {\n" + 
                 "  public void run() { ... }\n" +
                 "});\n" +
                 "command.run();",
                 _doc.getText());
  } 

  
  
}



