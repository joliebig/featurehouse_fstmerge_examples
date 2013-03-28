

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

    assertTrue("At DOCSTART.", ! rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("At identifier.",  ! rule.applyRule(_doc, 10, Indenter.OTHER));
    assertTrue("At start of array.", !rule.applyRule(_doc, 25, Indenter.OTHER));
    assertTrue("START starts one-line comment.", rule.applyRule(_doc, 54, Indenter.OTHER));
    assertTrue("START starts one-line comment.", rule.applyRule(_doc, 60, Indenter.OTHER));
    assertTrue("START starts javadoc comment.", rule.applyRule(_doc, 104, Indenter.OTHER));
    assertTrue("START starts javadoc comment.", rule.applyRule(_doc, 110, Indenter.OTHER));
    assertTrue("Line inside javadoc comment.", rule.applyRule(_doc, 130, Indenter.OTHER));
    assertTrue("Line closes javadoc comment.", rule.applyRule(_doc, 150, Indenter.OTHER));
    assertTrue("START is stil in first.", rule.applyRule(_doc, 180, Indenter.OTHER));
    assertTrue("Second pseudo array element.", ! rule.applyRule(_doc, 230, Indenter.OTHER));
    assertTrue("Start of multi-line comment.", !rule.applyRule(_doc, 260, Indenter.OTHER));
    assertTrue("Line inside multi-line comment.", !rule.applyRule(_doc, 275, Indenter.OTHER));
    assertTrue("Line inside multi-line comment.", !rule.applyRule(_doc, 300, Indenter.OTHER));
    assertTrue("Line closes multi-line comment.", !rule.applyRule(_doc, 399, Indenter.OTHER));
    assertTrue("Last close brace", !rule.applyRule(_doc, 400, Indenter.OTHER));
    assertTrue("At end of document.", !rule.applyRule(_doc, 401, Indenter.OTHER));
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

    assertTrue("At DOCSTART.", ! rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("At identifier.",  ! rule.applyRule(_doc, 10, Indenter.OTHER));
    assertTrue("At start of outer array", !rule.applyRule(_doc, 25, Indenter.OTHER));

    assertTrue("Before start of inner array", rule.applyRule(_doc, 50, Indenter.OTHER));

    assertTrue("Same line as inner {.", rule.applyRule(_doc, 54, Indenter.OTHER));
    assertTrue("Line after inner {.", !rule.applyRule(_doc, 75, Indenter.OTHER));
    assertTrue("START is stil in first.", !rule.applyRule(_doc, 180, Indenter.OTHER));

    assertTrue("Second pseudo array element.",  rule.applyRule(_doc, 230, Indenter.OTHER));
    assertTrue("In multi-line comment.", ! rule.applyRule(_doc, 260, Indenter.OTHER));

    assertTrue("multi-line comment w/ = {.",  ! rule.applyRule(_doc, 275, Indenter.OTHER));

    assertTrue("Line inside multi-line comment.", !rule.applyRule(_doc, 300, Indenter.OTHER));
    assertTrue("Line closes multi-line comment.", !rule.applyRule(_doc, 399, Indenter.OTHER));

    assertTrue("Last close brace",  rule.applyRule(_doc, 400, Indenter.OTHER));
    assertTrue("At end of document.",  rule.applyRule(_doc, 401, Indenter.OTHER));
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

    assertTrue("At DOCSTART.",    ! rule.applyRule(_doc, 0, Indenter.OTHER));
    assertTrue("At identifier.",  ! rule.applyRule(_doc, 10, Indenter.OTHER));
    assertTrue("At start of outer array", !rule.applyRule(_doc, 25, Indenter.OTHER));

    assertTrue("Before start of inner array", ! rule.applyRule(_doc, 50, Indenter.OTHER));
    assertTrue("Same line as inner {.", !rule.applyRule(_doc, 54, Indenter.OTHER));
    assertTrue("Line after inner {.", !rule.applyRule(_doc, 75, Indenter.OTHER));
    assertTrue("START is stil in first.", !rule.applyRule(_doc, 180, Indenter.OTHER));

  }
}
