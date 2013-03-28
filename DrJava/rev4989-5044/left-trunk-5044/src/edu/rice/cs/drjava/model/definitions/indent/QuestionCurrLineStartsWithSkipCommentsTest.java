

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionCurrLineStartsWithSkipCommentsTest extends IndentRulesTestCase {
  private String _text;
    
  private IndentRuleQuestion _rule;
  
  public void testNoPrefix() throws BadLocationException {
    _text =
      "class A                 \n" + 
      "{                       \n" + 
      "    // one line comment \n" + 
      "    int method1         \n" + 
      "    /**                 \n" + 
      "     * javadoc comment  \n" + 
      "     */                 \n" + 
      "    int method()        \n" + 
      "    {                   \n" + 
      "    }                   \n" + 
      "    /* multi line       \n" + 
      "       comment          \n" + 
      "    boolean method()    \n" + 
      "    {                   \n" + 
      "    }                   \n" + 
      "    */                  \n" + 
      "}";                           

    _setDocText(_text);

    IndentRuleQuestion rule = new QuestionCurrLineStartsWithSkipComments("", null, null);

    
    
    assertTrue("At 0.", rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("At start of block.", rule.testApplyRule(_doc, 25, Indenter.IndentReason.OTHER));

    assertTrue("START starts one-line comment.", ! rule.testApplyRule(_doc, 54, Indenter.IndentReason.OTHER));
    assertTrue("START starts one-line comment.", ! rule.testApplyRule(_doc, 60, Indenter.IndentReason.OTHER));
    assertTrue("START starts javadoc comment.", ! rule.testApplyRule(_doc, 104, Indenter.IndentReason.OTHER));
    assertTrue("START starts javadoc comment.", ! rule.testApplyRule(_doc, 110, Indenter.IndentReason.OTHER));
    assertTrue("Line inside javadoc comment.", ! rule.testApplyRule(_doc, 130, Indenter.IndentReason.OTHER));
    assertTrue("Line closes javadoc comment.", ! rule.testApplyRule(_doc, 150, Indenter.IndentReason.OTHER));
    assertTrue("START is free.", rule.testApplyRule(_doc, 180, Indenter.IndentReason.OTHER));
    assertTrue("START is free.", rule.testApplyRule(_doc, 230, Indenter.IndentReason.OTHER));
    assertTrue("START starts multi-line comment.", ! rule.testApplyRule(_doc, 260, Indenter.IndentReason.OTHER));
    assertTrue("Line inside multi-line comment.", ! rule.testApplyRule(_doc, 275, Indenter.IndentReason.OTHER));
    assertTrue("Line inside multi-line comment.", ! rule.testApplyRule(_doc, 300, Indenter.IndentReason.OTHER));
    assertTrue("Line closes multi-line comment.", ! rule.testApplyRule(_doc, 399, Indenter.IndentReason.OTHER));
    assertTrue("START is free.", rule.testApplyRule(_doc, 400, Indenter.IndentReason.OTHER));
    assertTrue("At end of document.", rule.testApplyRule(_doc, 401, Indenter.IndentReason.OTHER));
  }

  public void testOpenBracePrefix() throws BadLocationException
  {
    _text =
      "class A extends         \n" + 
      "B {                     \n" + 
      "    // {        }       \n" + 
      "    int field;          \n" + 
      "    /**                 \n" + 
      "     * {        }       \n" + 
      "     */                 \n" + 
      "    int method() /*     \n" + 
      " */ {                   \n" + 
      "    }                   \n" + 
      "    /* multi line       \n" + 
      "       comment          \n" + 
      "    boolean method()    \n" + 
      "/**stuff*/   {  // stuff\n" + 
      "             }          \n" + 
      "                        \n" + 
      "}";                           

    _setDocText(_text);

    _rule = new QuestionCurrLineStartsWithSkipComments("{", null, null);

    assertTrue("At 0 - line doesn't start with an open brace.",      !_rule.testApplyRule(_doc,   0, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a block, but not the start of the line.",       !_rule.testApplyRule(_doc,  25, Indenter.IndentReason.OTHER));
    assertTrue("Inside block - line starts with an alphanumeric character.",!_rule.testApplyRule(_doc,  30, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a one-line comment.",                           !_rule.testApplyRule(_doc,  54, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a one-line comment.",                           !_rule.testApplyRule(_doc,  60, Indenter.IndentReason.OTHER));
    assertTrue("Line starts with alphanumeric character.",                  !_rule.testApplyRule(_doc,  80, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a javadoc comment.",                            !_rule.testApplyRule(_doc, 104, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a javadoc comment.",                            !_rule.testApplyRule(_doc, 110, Indenter.IndentReason.OTHER));
    assertTrue("Line inside javadoc comment.",                              !_rule.testApplyRule(_doc, 130, Indenter.IndentReason.OTHER));
    assertTrue("Line starts with alphanumeric character.",                  !_rule.testApplyRule(_doc, 180, Indenter.IndentReason.OTHER));
    assertTrue("Line closes comment. It follows an open brace.",             _rule.testApplyRule(_doc, 201, Indenter.IndentReason.OTHER));
    assertTrue("Line closes comment. It follows an open brace.",             _rule.testApplyRule(_doc, 221, Indenter.IndentReason.OTHER));
    assertTrue("At end of block - line starts with a close brace.",         !_rule.testApplyRule(_doc, 225, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a multi-line comment.",                         !_rule.testApplyRule(_doc, 260, Indenter.IndentReason.OTHER));
    assertTrue("Line inside multi-line comment.",                           !_rule.testApplyRule(_doc, 275, Indenter.IndentReason.OTHER));
    assertTrue("Line inside multi-line comment.",                           !_rule.testApplyRule(_doc, 300, Indenter.IndentReason.OTHER));
    assertTrue("Line closes comment. It follows an open brace.",             _rule.testApplyRule(_doc, 325, Indenter.IndentReason.OTHER));
    assertTrue("Line starts with a close brace.",                           !_rule.testApplyRule(_doc, 355, Indenter.IndentReason.OTHER));
    assertTrue("Empty line.",                                               !_rule.testApplyRule(_doc, 390, Indenter.IndentReason.OTHER));
    assertTrue("At last character - line starts with a close brace.",       !_rule.testApplyRule(_doc, 400, Indenter.IndentReason.OTHER));
    assertTrue("At end of document - line starts with a close brace.",      !_rule.testApplyRule(_doc, 401, Indenter.IndentReason.OTHER));
  }
    
  public void testCloseBracePrefix() throws BadLocationException
  {
    _text =
      "class A                 \n" + 
      "{                       \n" + 
      "    // }         }      \n" + 
      "    int field;          \n" + 
      "    /**                 \n" + 
      "     * javadoc comment  \n" + 
      "     */   }             \n" + 
      "    int method()        \n" + 
      "/**/}                   \n" + 
      "/ * }                   \n" + 
      "    /* multi line       \n" + 
      "       comment          \n" + 
      "    boolean method()    \n" + 
      "    {                   \n" + 
      "*/ / }                  \n" + 
      "   * }                  \n" + 
      "}";                           

    _setDocText(_text);
    
    _rule = new QuestionCurrLineStartsWithSkipComments("}", null, null);

    assertTrue("At 0 - line doesn't start with a close brace.",      !_rule.testApplyRule(_doc,   0, Indenter.IndentReason.OTHER));
    assertTrue("At start of block - line starts with an open brace.",       !_rule.testApplyRule(_doc,  25, Indenter.IndentReason.OTHER));
    assertTrue("Inside block - line starts with an open brace.",            !_rule.testApplyRule(_doc,  30, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a one-line comment.",                           !_rule.testApplyRule(_doc,  54, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a one-line comment.",                           !_rule.testApplyRule(_doc,  60, Indenter.IndentReason.OTHER));
    assertTrue("Line starts with alphanumeric character.",                  !_rule.testApplyRule(_doc,  80, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a javadoc comment.",                            !_rule.testApplyRule(_doc, 104, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a javadoc comment.",                            !_rule.testApplyRule(_doc, 110, Indenter.IndentReason.OTHER));
    assertTrue("Line inside javadoc comment.",                              !_rule.testApplyRule(_doc, 130, Indenter.IndentReason.OTHER));
    assertTrue("Line closes multi-line comment, it follows a close brace.",  _rule.testApplyRule(_doc, 150, Indenter.IndentReason.OTHER));
    assertTrue("Line starts with alphanumeric character.",                  !_rule.testApplyRule(_doc, 180, Indenter.IndentReason.OTHER));
    assertTrue("Line starts with a comment, it follows a close brace.",      _rule.testApplyRule(_doc, 221, Indenter.IndentReason.OTHER));
    assertTrue("At end of block - line starts with a slash.",               !_rule.testApplyRule(_doc, 225, Indenter.IndentReason.OTHER));
    assertTrue("Line starts a multi-line comment.",                         !_rule.testApplyRule(_doc, 260, Indenter.IndentReason.OTHER));
    assertTrue("Line inside multi-line comment.",                           !_rule.testApplyRule(_doc, 275, Indenter.IndentReason.OTHER));
    assertTrue("Line inside multi-line comment.",                           !_rule.testApplyRule(_doc, 300, Indenter.IndentReason.OTHER));
    assertTrue("Line inside multi-line comment.",                           !_rule.testApplyRule(_doc, 325, Indenter.IndentReason.OTHER));
    assertTrue("Line closes multi-line comment, it follows a slash.",       !_rule.testApplyRule(_doc, 355, Indenter.IndentReason.OTHER));
    assertTrue("Line starts with a star.",                                  !_rule.testApplyRule(_doc, 376, Indenter.IndentReason.OTHER));
    assertTrue("At last character - line starts with a close brace.",        _rule.testApplyRule(_doc, 400, Indenter.IndentReason.OTHER));
    assertTrue("At end of document - line starts with a close brace.",       _rule.testApplyRule(_doc, 401, Indenter.IndentReason.OTHER));
  }
}
