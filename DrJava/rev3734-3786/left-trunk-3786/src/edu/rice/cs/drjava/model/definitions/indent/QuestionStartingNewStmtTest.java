

package edu.rice.cs.drjava.model.definitions.indent;

import javax.swing.text.BadLocationException;


public final class QuestionStartingNewStmtTest extends IndentRulesTestCase {

  
  public void testStartOfStmtCheckForEndCharacters() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionStartingNewStmt(null, null);

    
    _setDocText("import java.util.Vector;\n");
    _doc.setCurrentLocation(4);
    assertTrue("starting new stmt, prev char docstart",
        rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("foo();\nbar();\n");
    _doc.setCurrentLocation(7);
    assertTrue("starting new stmt, prev char ';'",
        rule.applyRule(_doc, Indenter.OTHER));
    
    
    _setDocText("public void foo() {\nfoo()\n");
    _doc.setCurrentLocation(20);
    assertTrue("starting new stmt, prev char '{'",
        rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("x();\n}\nfoo()\n");
    _doc.setCurrentLocation(7);
    assertTrue("starting new stmt, prev char '}'",
        rule.applyRule(_doc, Indenter.OTHER));
  }  

  
  public void testStartOfStmtIgnoreWhiteSpaceAndCommentsInBetween() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionStartingNewStmt(null, null);
  
    
    _setDocText("bar();\n\t   \n  foo();");
    _doc.setCurrentLocation(12);
    assertTrue("starting new stmt, ignore whitespace in between",
        rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("} // note:\n//please ignore me\nfoo();\n");
    _doc.setCurrentLocation(30);
    assertTrue("starting new stmt, ignore single line comments",
        rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("{ /* in a comment\nstill in a comment\ndone */\nfoo();");
    _doc.setCurrentLocation(45);
    assertTrue("starting new stmt, ignore multi-line comments",
        rule.applyRule(_doc, Indenter.OTHER));

    _setDocText("bar();\n/* blah */ foo();\n");
    _doc.setCurrentLocation(18);
    assertTrue("starting new stmt, ignore multi-line comment on same " +
        "line as new stmt",
        rule.applyRule(_doc, Indenter.OTHER));

    _setDocText("method foo() {\n" +
  "}\n" +
  "     ");
    _doc.setCurrentLocation(17);
    assertTrue("Blank line with no non-WS after",
        rule.applyRule(_doc, Indenter.OTHER));

    _setDocText("method foo() {\n" +
  "}\n" +
  "     \n" +
  "// comment");
    _doc.setCurrentLocation(17);
    assertTrue("Blank line with comments after, but no non-WS",
        rule.applyRule(_doc, Indenter.OTHER));
  }

  
  public void testNotStartOfStmtDueToEndCharactersInCommentsOrQuotes() throws BadLocationException {
    IndentRuleQuestion rule = new QuestionStartingNewStmt(null, null);

    
    _setDocText("x = bar + \";\" + \"}\" + \"{\"\n+ foo;\n");
    _doc.setCurrentLocation(26);
    assertTrue("not starting new stmt, ignore end chars in quotes",
        !rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("x = bar.//;{}\nfoo();\n");
    _doc.setCurrentLocation(14);
    assertTrue("not starting new stmt, ignore end chars in single-line comments",
        !rule.applyRule(_doc, Indenter.OTHER));

    
    _setDocText("x = bar./*;\n{\n}\n*/\nfoo();\n");
    _doc.setCurrentLocation(19);
    assertTrue("not starting new stmt, ignore end chars in multi-line comments",
        !rule.applyRule(_doc, Indenter.OTHER));
  }
}
