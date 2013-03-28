

package edu.rice.cs.drjava.model.definitions.indent;

import java.util.ArrayList;

import javax.swing.text.BadLocationException;


public final class IndentRuleWithTraceTest extends IndentRulesTestCase {

  public void testTrace() throws BadLocationException{
    IndentRuleWithTrace.setRuleTraceEnabled(true);
    IndentRule
      rule4 = new ActionBracePlus(2),
      rule3 = new QuestionBraceIsCurly(rule4, rule4),
      rule2 = new QuestionBraceIsParenOrBracket(rule3, rule3);
    IndentRuleQuestion
      rule1 = new QuestionInsideComment(rule2, rule2);
    String text =
      "public class foo {\n" +
      "/**\n" +
      " * This method does nothing\n" + 
      " */\n" +
      "public void method1(){\n" +
      "}\n" +
      "}\n";

    
    _setDocText(text);
    rule1.indentLine(_doc, 23, Indenter.IndentReason.OTHER);
    rule1.indentLine(_doc, 75, Indenter.IndentReason.OTHER);

    String[] expected = {"edu.rice.cs.drjava.model.definitions.indent.QuestionInsideComment No",
    "edu.rice.cs.drjava.model.definitions.indent.QuestionBraceIsParenOrBracket No",
    "edu.rice.cs.drjava.model.definitions.indent.QuestionBraceIsCurly Yes",
    "edu.rice.cs.drjava.model.definitions.indent.ActionBracePlus "};

    ArrayList<String> actual = IndentRuleWithTrace.getTrace();

    assertEquals("steps in trace", 4, actual.size());
    for(int x = 0; x < actual.size(); x++) {
      assertEquals("check trace step " + x, expected[x], actual.get(x));
    }
  }
}
