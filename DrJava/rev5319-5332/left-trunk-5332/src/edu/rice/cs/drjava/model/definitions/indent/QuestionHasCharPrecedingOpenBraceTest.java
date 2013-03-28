

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionHasCharPrecedingOpenBraceTest extends IndentRulesTestCase
{
  private String _text;
    


  public void testIsIn1DArray() throws BadLocationException
  { 
    _text =
      "int[2][] a =            \n" + 
      "{                       \n" + 
      "    a, //  line comment \n" + 
      "    int b,              \n" + 
      "    /**                 \n" + 
      "     * javadoc comment  \n" + 
      "     */                 \n" + 
      "    START               \n" + 
      "    },                  \n" + 
      "    {                   \n" + 
      "    /*  {  multi line   \n" + 
      "       comment  }       \n" + 
      "    boolean method()    \n" + 
      "    {                   \n" + 
      "    }                   \n" + 
      "    */}                 \n" + 
      "}";                           

    _setDocText(_text);
    
    char [] chars = {'='};
    IndentRuleQuestion rule = new QuestionHasCharPrecedingOpenBrace(chars, null, null);

    assertTrue("At 0.", ! rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("At identifier.",  ! rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
    assertTrue("At start of array.", !rule.testApplyRule(_doc, 25, Indenter.IndentReason.OTHER));
    assertTrue("START starts one-line comment.", rule.testApplyRule(_doc, 54, Indenter.IndentReason.OTHER));
    assertTrue("START starts one-line comment.", rule.testApplyRule(_doc, 60, Indenter.IndentReason.OTHER));
    assertTrue("START starts javadoc comment.", rule.testApplyRule(_doc, 104, Indenter.IndentReason.OTHER));
    assertTrue("START starts javadoc comment.", rule.testApplyRule(_doc, 110, Indenter.IndentReason.OTHER));
    assertTrue("Line inside javadoc comment.", rule.testApplyRule(_doc, 130, Indenter.IndentReason.OTHER));
    assertTrue("Line closes javadoc comment.", rule.testApplyRule(_doc, 150, Indenter.IndentReason.OTHER));
    assertTrue("START is stil in first.", rule.testApplyRule(_doc, 180, Indenter.IndentReason.OTHER));
    assertTrue("Second pseudo array element.", ! rule.testApplyRule(_doc, 230, Indenter.IndentReason.OTHER));
    assertTrue("Start of multi-line comment.", !rule.testApplyRule(_doc, 260, Indenter.IndentReason.OTHER));
    assertTrue("Line inside multi-line comment.", !rule.testApplyRule(_doc, 275, Indenter.IndentReason.OTHER));
    assertTrue("Line inside multi-line comment.", !rule.testApplyRule(_doc, 300, Indenter.IndentReason.OTHER));
    assertTrue("Line closes multi-line comment.", !rule.testApplyRule(_doc, 399, Indenter.IndentReason.OTHER));
    assertTrue("Last close brace", !rule.testApplyRule(_doc, 400, Indenter.IndentReason.OTHER));
    assertTrue("At end of document.", !rule.testApplyRule(_doc, 401, Indenter.IndentReason.OTHER));
  }
  public void testIsIn2DArray() throws BadLocationException
  { 
    _text =
      "int[2][] a =            \n" + 
      "{                       \n" + 
      "  {                     \n" + 
      "    a, //  line comment \n" + 
      "    int b,              \n" + 
      "    /**                 \n" + 
      "     */                 \n" + 
      "    START               \n" + 
      "    },                  \n" + 
      "    {                   \n" + 
      "    /* = { multi line   \n" + 
      "       comment  }       \n" + 
      "    boolean method()    \n" + 
      "    {                   \n" + 
      "    }                   \n" + 
      "    */}                 \n" + 
      "}"                          + 
      "";

    _setDocText(_text);
    
    char [] chars = {'='};
    IndentRuleQuestion rule = new QuestionHasCharPrecedingOpenBrace(chars, null, null);

    assertTrue("At 0.", ! rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("At identifier.",  ! rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
    assertTrue("At start of outer array", !rule.testApplyRule(_doc, 25, Indenter.IndentReason.OTHER));

    assertTrue("Before start of inner array", rule.testApplyRule(_doc, 50, Indenter.IndentReason.OTHER));

    assertTrue("Same line as inner {.", rule.testApplyRule(_doc, 54, Indenter.IndentReason.OTHER));
    assertTrue("Line after inner {.", !rule.testApplyRule(_doc, 75, Indenter.IndentReason.OTHER));
    assertTrue("START is stil in first.", !rule.testApplyRule(_doc, 180, Indenter.IndentReason.OTHER));

    assertTrue("Second pseudo array element.",  rule.testApplyRule(_doc, 230, Indenter.IndentReason.OTHER));
    assertTrue("In multi-line comment.", ! rule.testApplyRule(_doc, 260, Indenter.IndentReason.OTHER));

    assertTrue("multi-line comment w/ = {.",  ! rule.testApplyRule(_doc, 275, Indenter.IndentReason.OTHER));

    assertTrue("Line inside multi-line comment.", !rule.testApplyRule(_doc, 300, Indenter.IndentReason.OTHER));
    assertTrue("Line closes multi-line comment.", !rule.testApplyRule(_doc, 399, Indenter.IndentReason.OTHER));

    assertTrue("Last close brace",  rule.testApplyRule(_doc, 400, Indenter.IndentReason.OTHER));
    assertTrue("At end of document.",  rule.testApplyRule(_doc, 401, Indenter.IndentReason.OTHER));
  }
  public void testNoEquals() throws BadLocationException
  { 
    _text =
      "int[2][] a             \n" + 
      "{                       \n" + 
      "  {                     \n" + 
      "    a, //  line comment \n" + 
      "    int b,              \n" + 
      "    /**                 \n" + 
      "     */                 \n" + 
      "    START               \n" + 
      "    },                  \n" + 
      "    {                   \n" + 
      "    /* = { multi line   \n" + 
      "       comment  }       \n" + 
      "    boolean method()    \n" + 
      "    {                   \n" + 
      "    }                   \n" + 
      "    */}                 \n" + 
      "}"                          + 
      "";

    _setDocText(_text);
    
    char [] chars = {'='};
    IndentRuleQuestion rule = new QuestionHasCharPrecedingOpenBrace(chars, null, null);

    assertTrue("At 0.",    ! rule.testApplyRule(_doc, 0, Indenter.IndentReason.OTHER));
    assertTrue("At identifier.",  ! rule.testApplyRule(_doc, 10, Indenter.IndentReason.OTHER));
    assertTrue("At start of outer array", !rule.testApplyRule(_doc, 25, Indenter.IndentReason.OTHER));

    assertTrue("Before start of inner array", ! rule.testApplyRule(_doc, 50, Indenter.IndentReason.OTHER));
    assertTrue("Same line as inner {.", !rule.testApplyRule(_doc, 54, Indenter.IndentReason.OTHER));
    assertTrue("Line after inner {.", !rule.testApplyRule(_doc, 75, Indenter.IndentReason.OTHER));
    assertTrue("START is stil in first.", !rule.testApplyRule(_doc, 180, Indenter.IndentReason.OTHER));

  }
}
