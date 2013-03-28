

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;


public class Indenter {
  
  public Indenter(int indentLevel) { 
    _indentLevel = indentLevel;
    buildTree(indentLevel); 
  }
  
  protected int _indentLevel;
  
  
  public enum IndentReason {
    
    ENTER_KEY_PRESS,
      
      OTHER
  }
  
  
  protected IndentRule _topRule;
  
  public int getIndentLevel() { return _indentLevel; }
  
  
  public void buildTree(int indentLevel) {
    char[] indent = new char[indentLevel];
    java.util.Arrays.fill(indent,' ');
    
    boolean autoCloseComments = false;
    try { autoCloseComments = DrJava.getConfig().getSetting(OptionConstants.AUTO_CLOSE_COMMENTS).booleanValue(); }
    catch(Exception e) {  }  
    
    IndentRule
      
      rule60 = new ActionStartPrevLinePlus(""),
      rule37 = new ActionStartCurrStmtPlus(indentLevel),
      rule36 = new ActionStartStmtOfBracePlus(indentLevel),
      


      rule34 = new QuestionExistsCharInStmt('?', ':', rule37, rule36),
      rule33 = new QuestionLineContains(':', rule34, rule37),
      rule32 = new ActionStartCurrStmtPlus(0),
      rule31 = new QuestionCurrLineStartsWithSkipComments("{", rule32, rule33),
      rule39 = new ActionStartPrevStmtPlus(0, true),  

      rule28 = new ActionStartPrevStmtPlus(0, false),
      rule30 = new QuestionExistsCharInPrevStmt('?', rule28, rule39),
      rule27 = new QuestionExistsCharInStmt('?', ':', rule28, rule36),
      rule26 = new QuestionLineContains(':', rule27, rule30),
      rule25 = new QuestionStartingNewStmt(rule26, rule31),  
      rule24 = new QuestionPrevLineStartsWith("@", rule60, rule25),  
      
      rule22 = new QuestionHasCharPrecedingOpenBrace(new char[] {'=',',','{'}, rule36, rule24),  
      rule20 = new QuestionStartAfterOpenBrace(rule36, rule22),  
      rule19 = new ActionStartStmtOfBracePlus(0),  
      
      
      rule18 = new QuestionCurrLineStartsWithSkipComments("}", rule19, rule20),
      
      rule17 = new QuestionBraceIsCurly(rule18, rule24),  
      rule16 = new ActionBracePlus(1 + indentLevel),
      rule15 = new ActionBracePlus(1),
      
      rule38 = new ActionBracePlus(0),
      rule14 = new QuestionNewParenPhrase(rule15, rule16),  
      rule23 = new QuestionNewParenPhrase(rule30, rule38),  
      rule21 = new QuestionCurrLineStartsWith(")", rule23, rule14), 
      
      rule13 = new QuestionBraceIsParenOrBracket(rule21, rule17),   
      
      
      rule12 = new ActionStartPrevLinePlus(""),

      rule10 = new ActionStartPrevLinePlus("* "),
      rule09 = new QuestionCurrLineEmptyOrEnterPress(rule10, rule12),

      rule07 = new QuestionCurrLineStartsWith("*", rule12, rule09),
      rule06 = new QuestionPrevLineStartsWith("*", rule07, rule12),
      rule05 = new ActionStartPrevLinePlus(" "),    
      rule04 = new ActionStartPrevLinePlus(" * "),  
      rule46 = new ActionStartPrevLinePlus("  * "), 
      rule47 = new ActionStartPrevLinePlus("  "),   
      rule45 = new QuestionPrevLineStartsJavaDocWithText(rule46, rule04),  
      rule48 = new QuestionPrevLineStartsJavaDocWithText(rule47, rule05),  
      rule41 = new ActionStartPrevLinePlusMultilinePreserve(new String[] { " * \n", " */" }, 0, 3, 0, 3),
      rule49 = new ActionStartPrevLinePlusMultilinePreserve(new String[] { "  * \n", "  */"}, 0, 4, 0, 4),
      rule50 = new QuestionPrevLineStartsJavaDocWithText(rule49, rule41),
      
      rule03 = new QuestionCurrLineEmptyOrEnterPress(rule45, rule48),




      rule51 = new QuestionCurrLineEmpty(rule50, rule03), 
      rule02 = new QuestionPrevLineStartsComment(autoCloseComments ? rule51 : rule03, rule06),
      rule43 = new ActionDoNothing(),
      rule44 = new QuestionCurrLineIsWingComment(rule43, rule13),
      rule01 = new QuestionInsideComment(rule02, rule44);
    
    _topRule = rule01;
  }
  
  
  public boolean indent(AbstractDJDocument doc, Indenter.IndentReason reason) {

    return _topRule.indentLine(doc, reason);
  }
}



