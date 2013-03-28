

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public class ActionStartPrevLinePlusMultilinePreserveTest extends IndentRulesTestCase {
  
  
  private IndentRuleAction makeAction(String[] suffices,
                                      int cursorLine, int cursorPos,
                                      int psrvLine, int psrvPos) {
    return new ActionStartPrevLinePlusMultilinePreserve(suffices,
                                                        cursorLine, cursorPos,
                                                        psrvLine, psrvPos);
  }
  
  
  public void helperCommentTest(String start, int loc, int endLoc, String finish) throws 
    BadLocationException {
      _setDocText(start);
      _doc.setCurrentLocation(loc);
      makeAction(new String[]{" * \n", " */"},0,3,0,3).indentLine(_doc, Indenter.ENTER_KEY_PRESS);
      assertEquals(endLoc, _doc.getCurrentLocation());
      
      assertEquals(finish, _doc.getText());
  }

  public void test1() throws BadLocationException {
    helperCommentTest("/**\n",
                      4, 7,
                      "/**\n * \n */");
  }

  public void test2() throws BadLocationException {
    helperCommentTest("   /**\n",
                      7, 13,
                      "   /**\n    * \n    */");
  }

  public void test3() throws BadLocationException {
    helperCommentTest("/* abc\ndefg\n   hijklmnop",
                      7, 10,
                      "/* abc\n * defg\n */\n   hijklmnop");
  }

  public void test4() throws BadLocationException {
    helperCommentTest("/* \nThis is a comment */",
                      4, 7,
                      "/* \n * This is a comment */\n */");
  }

  public void test5() throws BadLocationException {
    helperCommentTest("/* This is code\n     and more */",
                      16, 19,
                      "/* This is code\n *      and more */\n */");
  }

  public void test6() throws BadLocationException {













    helperCommentTest("/* This \nis a comment block\n   That is already closed */",
                      9, 12,
                      "/* This \n * is a comment block\n */\n   That is already closed */");
  }
  
  public void test7() throws BadLocationException {
















  helperCommentTest("/* This \nis a comment block\n * That is already closed \n */",
                      9, 12,
                      "/* This \n * is a comment block\n */\n * That is already closed \n */");
  }
  
  public void xtest8() throws BadLocationException {






    helperCommentTest("/* ABC \n */",
                      8, 11,
                      "/* ABC \n *  */\n */");
  }
  
  public void xtest9() throws BadLocationException {








    helperCommentTest("/**\n * Text\n */",
                      4, 7,
                      "/**\n * \n * Text\n */");
  }
  
  public void test10() throws BadLocationException {






    
    helperCommentTest("/** This is \nbad */ **/",
                      13, 16,
                      "/** This is \n * bad */ **/\n */");
  }

  public void xtest11() throws BadLocationException {






    helperCommentTest("/** ABC **/ \n /** ABC **/",
                      13, 13,
                      "/** ABC **/ \n/** ABC **/");
  }
  
}