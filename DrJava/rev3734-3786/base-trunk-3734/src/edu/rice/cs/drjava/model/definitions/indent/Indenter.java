

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;


public class Indenter {

  public Indenter(int indentLevel) { buildTree(indentLevel); }
  
  

  
  public static final int ENTER_KEY_PRESS = 1;

  
  public static final int OTHER = 0;

  
  protected IndentRule _topRule;

  
  public void buildTree(int indentLevel) {
    char[] indent = new char[indentLevel];
    java.util.Arrays.fill(indent,' ');
    String oneLevel = new String(indent);

    boolean autoCloseComments = DrJava.getConfig().
      getSetting(OptionConstants.AUTO_CLOSE_COMMENTS).booleanValue();
    
    IndentRule
      
      rule37 = new ActionStartCurrStmtPlus(oneLevel),
      rule36 = new ActionStartStmtOfBracePlus(oneLevel),
      rule35 = rule37,
      rule34 = new QuestionExistsCharInStmt('?', ':', rule35, rule36),
      rule33 = new QuestionLineContains(':', rule34, rule37),
      rule32 = new ActionStartCurrStmtPlus(""),
      rule31 = new QuestionCurrLineStartsWithSkipComments("{", rule32, rule33),
      rule39 = new ActionStartPrevStmtPlus("", true),
      rule29 = rule36,
      rule28 = new ActionStartPrevStmtPlus("", false),
      rule40 = rule28,
      rule30 = new QuestionExistsCharInPrevStmt('?', rule40, rule39),
      rule27 = new QuestionExistsCharInStmt('?', ':', rule28, rule29),
      rule26 = new QuestionLineContains(':', rule27, rule30),
      rule25 = new QuestionStartingNewStmt(rule26, rule31),
      rule24 = rule25,
      rule23 = rule36,
      rule22 = new QuestionHasCharPrecedingOpenBrace(new char[] {'=',',','{'},rule23,rule24),
      rule21 = rule36,
      rule20 = new QuestionStartAfterOpenBrace(rule21, rule22),
      rule19 = new ActionStartStmtOfBracePlus(""),
      rule18 = new QuestionCurrLineStartsWithSkipComments("}", rule19, rule20),
      rule17 = new QuestionBraceIsCurly(rule18, rule25),
      rule16 = new ActionBracePlus(" " + oneLevel),
      rule15 = new ActionBracePlus(" "),
      rule38 = new QuestionCurrLineStartsWith(")",rule30,rule15),  
        
      rule14 = new QuestionNewParenPhrase(rule38, rule16), 
      rule13 = new QuestionBraceIsParenOrBracket(rule14, rule17),

      
      rule12 = new ActionStartPrevLinePlus(""),
      rule11 = rule12,
      rule10 = new ActionStartPrevLinePlus("* "),
      rule09 = new QuestionCurrLineEmptyOrEnterPress(rule10, rule11),
      rule08 = rule12,
      rule07 = new QuestionCurrLineStartsWith("*", rule08, rule09),
      rule06 = new QuestionPrevLineStartsWith("*", rule07, rule12),
      rule05 = new ActionStartPrevLinePlus(" "),
      rule04 = new ActionStartPrevLinePlus(" * "),
        
      rule41 = new ActionStartPrevLinePlusMultilinePreserve(new String[] { " * \n", " */" }, 0, 3, 0, 3),
      rule42 = new QuestionFollowedByStar(rule04, rule41),
      rule03 = new QuestionCurrLineEmptyOrEnterPress((autoCloseComments? rule42 : rule04), rule05),
      rule02 = new QuestionPrevLineStartsComment(rule03, rule06),

      rule43 = new ActionDoNothing(),
      rule44 = new QuestionCurrLineIsWingComment(rule43, rule13),
      rule01 = new QuestionInsideComment(rule02, rule44);
       


    _topRule = rule01;
  }
  
  
  public boolean indent(AbstractDJDocument doc, int reason) {

    return _topRule.indentLine(doc, reason);
  }
}



