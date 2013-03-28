

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class ActionStartPrevStmtPlusTest extends IndentRulesTestCase {
  
  public void testNoPrevStmt() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartPrevStmtPlus("", true);
    IndentRuleAction rule2 = new ActionStartPrevStmtPlus("  ", true);

    _setDocText("foo();\n");
    rule1.indentLine(_doc, 2, Indenter.OTHER);
    assertEquals("no prev stmt, no suffix",
                 "foo();\n",
                 _doc.getText());
    
    _setDocText("foo();\n");
    rule2.indentLine(_doc, 2, Indenter.OTHER);
    assertEquals("no prev stmt, suffix two spaces",
                 "  foo();\n",
                 _doc.getText());
  }
  
  public void testPrevStmtPrevLine() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartPrevStmtPlus("", true);
    IndentRuleAction rule2 = new ActionStartPrevStmtPlus("  ", true);

    _setDocText("  foo().\n//boo();\n/*y=x+1;\nfoo(){}*/\nbar();\nbiz();\n");
    rule1.indentLine(_doc, 44, Indenter.OTHER);
    assertEquals("prev stmt on prev line, no suffix",
                 "  foo().\n//boo();\n/*y=x+1;\nfoo(){}*/\nbar();\n  biz();\n",
                 _doc.getText());
    
    _setDocText("  foo().\n//boo();\n/*y=x+1;\nfoo(){}*/\nbar();\nbiz();\n");
    rule2.indentLine(_doc, 44, Indenter.OTHER);
    assertEquals("prev stmt on prev line, suffix two spaces",
                 "  foo().\n//boo();\n/*y=x+1;\nfoo(){}*/\nbar();\n    biz();\n",
                 _doc.getText());
  }

  public void testPrevStmtSeveralLinesBeforeCurrLocation() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartPrevStmtPlus("", true);
    IndentRuleAction rule2 = new ActionStartPrevStmtPlus("  ", true);
    
    _setDocText("  foo();\n//y=x+1;\n/*void blah {\n}*/\n  ';' + blah.\n//foo\nx;\n");
    rule1.indentLine(_doc, 56, Indenter.OTHER);
    assertEquals("prev stmt serveral lines before, no suffix",
                 "  foo();\n//y=x+1;\n/*void blah {\n}*/\n  ';' + blah.\n//foo\n  x;\n",
                 _doc.getText());
    
    _setDocText("  foo();\n//y=x+1;\n/*void blah {\n}*/\n  ';' + blah.\n//foo\nx;\n");
    rule2.indentLine(_doc, 56, Indenter.OTHER);
    assertEquals("prev stmt serveral lines before, suffix two spaces", 
                 "  foo();\n//y=x+1;\n/*void blah {\n}*/\n  ';' + blah.\n//foo\n    x;\n",
                 _doc.getText());
  }
  
  public void testColonNotDelim() throws BadLocationException {
    IndentRuleAction rule = new ActionStartPrevStmtPlus("", false);
    
    _setDocText("test2 = x ? y :\n" +     
                "  z;\n" +     
                "foo();\n");     
    rule.indentLine(_doc, 21, Indenter.OTHER);
    assertEquals("Colon is not a delimiter",
                 "test2 = x ? y :\n" +     
                 "  z;\n" +     
                 "foo();\n",
                 _doc.getText());
  }


  public void testAfterArrayAssign() throws BadLocationException {
    IndentRuleAction rule = new ActionStartPrevStmtPlus("", false);
    
    _setDocText("a = {\n" +
                "  b,c,d\n" + 
                "};\n" +
                "   a;");     
    
    rule.indentLine(_doc, 17, Indenter.OTHER);
    assertEquals("After array assignment",
                 "a = {\n" + 
                 "  b,c,d\n" +
                 "};\n" +
                 "a;",
                 _doc.getText());
  }
  public void testAfterArrayAssignMultiSemi() throws BadLocationException {
    IndentRuleAction rule = new ActionStartPrevStmtPlus("", false);
    
    _setDocText("a = {\n" +
                "  b,c,d\n" + 
                "};;;\n" +
                "   a;");     
    
    rule.indentLine(_doc, 19, Indenter.OTHER);
    assertEquals("After array assignment multi semi colons",
                 "a = {\n" + 
                 "  b,c,d\n" +
                 "};;;\n" +
                 "a;",
                 _doc.getText());
  }

  
  
}



