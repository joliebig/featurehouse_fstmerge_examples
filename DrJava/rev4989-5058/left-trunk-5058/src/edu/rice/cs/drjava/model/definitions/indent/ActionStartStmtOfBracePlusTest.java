

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class ActionStartStmtOfBracePlusTest extends IndentRulesTestCase {


  
  public void testSingleLineContract() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartStmtOfBracePlus(0);
    IndentRuleAction rule2 = new ActionStartStmtOfBracePlus(3); 
    
    String text = "public void foo() {\nbar();";
    String aligned1 = text;
    String aligned2 = "public void foo() {\n   bar();";
    
    _setDocText(text);

    rule1.testIndentLine(_doc, 20, Indenter.IndentReason.OTHER);

    assertEquals("single line contract, no indent, no suffix", aligned1, _doc.getText());
    
    _setDocText(text);

    rule2.testIndentLine(_doc, 20, Indenter.IndentReason.OTHER);

    assertEquals("single line contract, no indent, with suffix", aligned2, _doc.getText());
  }
  
  
  public void testIndentedSingleLineContract() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartStmtOfBracePlus(0);
    IndentRuleAction rule2 = new ActionStartStmtOfBracePlus(3); 
    
    String text = "  y = new Foo() {\nbar();";
    String aligned1 = "  y = new Foo() {\n  bar();";
    String aligned2 = "  y = new Foo() {\n     bar();";
    
    _setDocText(text);

    rule1.testIndentLine(_doc, 20, Indenter.IndentReason.OTHER);

    assertEquals("single line contract, with indent, no suffix", 
                 aligned1, _doc.getText());
    
    _setDocText(text);

    rule2.testIndentLine(_doc, 20, Indenter.IndentReason.OTHER);

    assertEquals("single line contract, with indent, with suffix", 
                 aligned2, _doc.getText());
  }
  
  
  public void testMultiLineContract() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartStmtOfBracePlus(0);
    IndentRuleAction rule2 = new ActionStartStmtOfBracePlus(2); 
    
    String text = "    foobar();\n" +
                  "    int foo(int x,\n" +
                  "            int y) {\n" + 
                  "bar();";
    String aligned1 = "    foobar();\n" +
                      "    int foo(int x,\n" +
                      "            int y) {\n" + 
                      "    bar();";
    String aligned2 = "    foobar();\n" +
                      "    int foo(int x,\n" +
                      "            int y) {\n" + 
                      "      bar();";
    
    _setDocText(text);

    rule1.testIndentLine(_doc, 56, Indenter.IndentReason.OTHER);

    assertEquals("multi line contract, with indent, no suffix", aligned1, _doc.getText());
    
    _setDocText(text);

    rule2.testIndentLine(_doc, 56, Indenter.IndentReason.OTHER);

    assertEquals("multi line contract, with indent, with suffix", aligned2, _doc.getText());
  }
  
  
  public void testForStatement() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartStmtOfBracePlus(0);
    IndentRuleAction rule2 = new ActionStartStmtOfBracePlus(3); 
    
    String text = "  for (int i=0; i<j; i++) {\nbar();";
    String aligned1 = "  for (int i=0; i<j; i++) {\n  bar();";
    String aligned2 = "  for (int i=0; i<j; i++) {\n     bar();";
    
    _setDocText(text);

    rule1.testIndentLine(_doc, 28, Indenter.IndentReason.OTHER);

    assertEquals("for statement, with indent, no suffix", 
                 aligned1, _doc.getText());
    
    _setDocText(text);

    rule2.testIndentLine(_doc, 28, Indenter.IndentReason.OTHER);

    assertEquals("for statement, with indent, with suffix", 
                 aligned2, _doc.getText());
  }
  
  
  public void testMultiLineForStatement() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartStmtOfBracePlus(0);
    IndentRuleAction rule2 = new ActionStartStmtOfBracePlus(2); 
    
    String text = "  for (int i=0;\n" +
                  "       i<j;\n" +
                  "       i++)\n" +
                  "  {\n" +
                  "bar();";
    String aligned1 = "  for (int i=0;\n" +
                      "       i<j;\n" +
                      "       i++)\n" +
                      "  {\n" +
                      "  bar();";
    String aligned2 = "  for (int i=0;\n" +
                      "       i<j;\n" +
                      "       i++)\n" +
                      "  {\n" +
                      "    bar();";
    
    _setDocText(text);

    rule1.testIndentLine(_doc, 44, Indenter.IndentReason.OTHER);

    assertEquals("multi-line for statement, with indent, no suffix", aligned1, _doc.getText());
    
    _setDocText(text);

    rule2.testIndentLine(_doc, 44, Indenter.IndentReason.OTHER);

    assertEquals("multi-line for statement, with indent, with suffix", aligned2, _doc.getText());
  }
  
  
  
  
  public void testCommentedBrace() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartStmtOfBracePlus(0);
    IndentRuleAction rule2 = new ActionStartStmtOfBracePlus(2); 
    
    String text = "  void foo()\n" +
                  "  {\n" +
                  "      // {\n" +
                  "foo();\n";
    String aligned1 = "  void foo()\n" +
                      "  {\n" +
                      "      // {\n" +
                      "  foo();\n";
    String aligned2 = "  void foo()\n" +
                      "  {\n" +
                      "      // {\n" +
                      "    foo();\n";
    
    _setDocText(text);

    rule1.testIndentLine(_doc, 30, Indenter.IndentReason.OTHER);

    assertEquals("commented brace, no suffix", aligned1, _doc.getText());
    
    _setDocText(text);

    rule2.testIndentLine(_doc, 30, Indenter.IndentReason.OTHER);

    assertEquals("commented brace, with suffix", aligned2, _doc.getText());
  }
  
  
  
  
  public void testNoBrace() throws BadLocationException {
    IndentRuleAction rule1 = new ActionStartStmtOfBracePlus(0);
    IndentRuleAction rule2 = new ActionStartStmtOfBracePlus(2); 
    
    String text = "package foo;\n" +
                  "import bar.*;\n";
    String aligned1 = "package foo;\n" +
                      "import bar.*;\n";
    String aligned2 = "package foo;\n" +
                      "  import bar.*;\n";
    
    _setDocText(text);

    rule1.testIndentLine(_doc, 13, Indenter.IndentReason.OTHER);

    assertEquals("no brace, no suffix", aligned1, _doc.getText());
    
    _setDocText(text);

    rule2.testIndentLine(_doc, 13, Indenter.IndentReason.OTHER);

    assertEquals("no brace, with suffix", aligned2, _doc.getText());
  }
}



