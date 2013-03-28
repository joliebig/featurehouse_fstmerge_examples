

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionCurrLineStartsWithSkipCommentsTest extends IndentRulesTestCase
{
  private String _text;
    
  private IndentRuleQuestion _rule;
  
  public void testNoPrefix() throws BadLocationException
  {
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

    
    
    assertTrue("At DOCSTART.", rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("At start of block.", rule.applyRule(_doc, 25, Indenter.OTHER));
    assertTrue("START starts one-line comment.", rule.applyRule(_doc, 54, Indenter.OTHER));
    assertTrue("START starts one-line comment.", rule.applyRule(_doc, 60, Indenter.OTHER));
    assertTrue("START starts javadoc comment.", rule.applyRule(_doc, 104, Indenter.OTHER));
    assertTrue("START starts javadoc comment.", rule.applyRule(_doc, 110, Indenter.OTHER));
    assertTrue("Line inside javadoc comment.", !rule.applyRule(_doc, 130, Indenter.OTHER));
    assertTrue("Line closes javadoc comment.", rule.applyRule(_doc, 150, Indenter.OTHER));
    assertTrue("START is free.", rule.applyRule(_doc, 180, Indenter.OTHER));
    assertTrue("START is free.", rule.applyRule(_doc, 230, Indenter.OTHER));
    assertTrue("START starts multi-line comment.", rule.applyRule(_doc, 260, Indenter.OTHER));
    assertTrue("Line inside multi-line comment.", !rule.applyRule(_doc, 275, Indenter.OTHER));
    assertTrue("Line inside multi-line comment.", !rule.applyRule(_doc, 300, Indenter.OTHER));
    assertTrue("Line closes multi-line comment.", rule.applyRule(_doc, 399, Indenter.OTHER));
    assertTrue("START is free.", rule.applyRule(_doc, 400, Indenter.OTHER));
    assertTrue("At end of document.", rule.applyRule(_doc, 401, Indenter.OTHER));
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

    assertTrue("At DOCSTART - line doesn't start with an open brace.",      !_rule.applyRule(_doc,   0, Indenter.OTHER));
    assertTrue("Line starts a block, but not the start of the line.",       !_rule.applyRule(_doc,  25, Indenter.OTHER));
    assertTrue("Inside block - line starts with an alphanumeric character.",!_rule.applyRule(_doc,  30, Indenter.OTHER));
    assertTrue("Line starts a one-line comment.",                           !_rule.applyRule(_doc,  54, Indenter.OTHER));
    assertTrue("Line starts a one-line comment.",                           !_rule.applyRule(_doc,  60, Indenter.OTHER));
    assertTrue("Line starts with alphanumeric character.",                  !_rule.applyRule(_doc,  80, Indenter.OTHER));
    assertTrue("Line starts a javadoc comment.",                            !_rule.applyRule(_doc, 104, Indenter.OTHER));
    assertTrue("Line starts a javadoc comment.",                            !_rule.applyRule(_doc, 110, Indenter.OTHER));
    assertTrue("Line inside javadoc comment.",                              !_rule.applyRule(_doc, 130, Indenter.OTHER));
    assertTrue("Line starts with alphanumeric character.",                  !_rule.applyRule(_doc, 180, Indenter.OTHER));
    assertTrue("Line closes comment. It follows an open brace.",             _rule.applyRule(_doc, 201, Indenter.OTHER));
    assertTrue("Line closes comment. It follows an open brace.",             _rule.applyRule(_doc, 221, Indenter.OTHER));
    assertTrue("At end of block - line starts with a close brace.",         !_rule.applyRule(_doc, 225, Indenter.OTHER));
    assertTrue("Line starts a multi-line comment.",                         !_rule.applyRule(_doc, 260, Indenter.OTHER));
    assertTrue("Line inside multi-line comment.",                           !_rule.applyRule(_doc, 275, Indenter.OTHER));
    assertTrue("Line inside multi-line comment.",                           !_rule.applyRule(_doc, 300, Indenter.OTHER));
    assertTrue("Line closes comment. It follows an open brace.",             _rule.applyRule(_doc, 325, Indenter.OTHER));
    assertTrue("Line starts with a close brace.",                           !_rule.applyRule(_doc, 355, Indenter.OTHER));
    assertTrue("Empty line.",                                               !_rule.applyRule(_doc, 390, Indenter.OTHER));
    assertTrue("At last character - line starts with a close brace.",       !_rule.applyRule(_doc, 400, Indenter.OTHER));
    assertTrue("At end of document - line starts with a close brace.",      !_rule.applyRule(_doc, 401, Indenter.OTHER));
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

    assertTrue("At DOCSTART - line doesn't start with a close brace.",      !_rule.applyRule(_doc,   0, Indenter.OTHER));
    assertTrue("At start of block - line starts with an open brace.",       !_rule.applyRule(_doc,  25, Indenter.OTHER));
    assertTrue("Inside block - line starts with an open brace.",            !_rule.applyRule(_doc,  30, Indenter.OTHER));
    assertTrue("Line starts a one-line comment.",                           !_rule.applyRule(_doc,  54, Indenter.OTHER));
    assertTrue("Line starts a one-line comment.",                           !_rule.applyRule(_doc,  60, Indenter.OTHER));
    assertTrue("Line starts with alphanumeric character.",                  !_rule.applyRule(_doc,  80, Indenter.OTHER));
    assertTrue("Line starts a javadoc comment.",                            !_rule.applyRule(_doc, 104, Indenter.OTHER));
    assertTrue("Line starts a javadoc comment.",                            !_rule.applyRule(_doc, 110, Indenter.OTHER));
    assertTrue("Line inside javadoc comment.",                              !_rule.applyRule(_doc, 130, Indenter.OTHER));
    assertTrue("Line closes multi-line comment, it follows a close brace.",  _rule.applyRule(_doc, 150, Indenter.OTHER));
    assertTrue("Line starts with alphanumeric character.",                  !_rule.applyRule(_doc, 180, Indenter.OTHER));
    assertTrue("Line starts with a comment, it follows a close brace.",      _rule.applyRule(_doc, 221, Indenter.OTHER));
    assertTrue("At end of block - line starts with a slash.",               !_rule.applyRule(_doc, 225, Indenter.OTHER));
    assertTrue("Line starts a multi-line comment.",                         !_rule.applyRule(_doc, 260, Indenter.OTHER));
    assertTrue("Line inside multi-line comment.",                           !_rule.applyRule(_doc, 275, Indenter.OTHER));
    assertTrue("Line inside multi-line comment.",                           !_rule.applyRule(_doc, 300, Indenter.OTHER));
    assertTrue("Line inside multi-line comment.",                           !_rule.applyRule(_doc, 325, Indenter.OTHER));
    assertTrue("Line closes multi-line comment, it follows a slash.",       !_rule.applyRule(_doc, 355, Indenter.OTHER));
    assertTrue("Line starts with a star.",                                  !_rule.applyRule(_doc, 376, Indenter.OTHER));
    assertTrue("At last character - line starts with a close brace.",        _rule.applyRule(_doc, 400, Indenter.OTHER));
    assertTrue("At end of document - line starts with a close brace.",       _rule.applyRule(_doc, 401, Indenter.OTHER));
  }
}
