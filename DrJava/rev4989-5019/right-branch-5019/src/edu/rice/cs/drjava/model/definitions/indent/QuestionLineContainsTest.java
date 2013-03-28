

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionLineContainsTest extends IndentRulesTestCase {

  
  public void testLineContainsColon() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionLineContains(':', null, null);

    
    _setDocText("return test ? x : y;\n}\n");
    _doc.setCurrentLocation(0);
    assertTrue("colon in text (after startdoc)",
        rule.testApplyRule(_doc, Indenter.IndentReason.OTHER));
    _setDocText("foo();\nreturn test ? x : y;\n}\n");
    _doc.setCurrentLocation(10);
    assertTrue("colon in text (after newline)",
        rule.testApplyRule(_doc, Indenter.IndentReason.OTHER));
    _doc.setCurrentLocation(25);
    assertTrue("colon in text (after colon on line)",
        rule.testApplyRule(_doc, Indenter.IndentReason.OTHER));
  }    
  
  
  public void testLineDoesNotContainColon() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionLineContains(':', null, null);
    
    
    _setDocText("foo();\nreturn test ? x : y;\n}\n");
    _doc.setCurrentLocation(6);
    assertTrue("no colon", !rule.testApplyRule(_doc, Indenter.IndentReason.OTHER));
    _doc.setCurrentLocation(28);
    assertTrue("line of close brace (no colon in text)", !rule.testApplyRule(_doc, Indenter.IndentReason.OTHER));
  }

  
  public void testLineDoesNotContainColonDueToComments() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionLineContains(':', null, null);

    
    _setDocText("//case 1:\nreturn test; //? x : y\n}\n");
    _doc.setCurrentLocation(0);
    assertTrue("entire line with colon in comment (no colon, single line comment)",
        !rule.testApplyRule(_doc, Indenter.IndentReason.OTHER));
    _doc.setCurrentLocation(10);
    assertTrue("part of line with colon in comment (no colon, single line comment)",
        !rule.testApplyRule(_doc, Indenter.IndentReason.OTHER));

    
    _setDocText("foo();\nreturn test; /*? x : y*/\n}\n");
    _doc.setCurrentLocation(7);
    assertTrue("no colon, colon in multi-line comment", !rule.testApplyRule(_doc, Indenter.IndentReason.OTHER));
  }

  
  public void testLineDoesNotContainColonDueToQuotes() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionLineContains(':', null, null);
  
    
    _setDocText("foo();\nreturn \"total: \" + sum\n}\n");
    _doc.setCurrentLocation(7);
    assertTrue("no colon, colon in quotes", !rule.testApplyRule(_doc, Indenter.IndentReason.OTHER));
  }
}
