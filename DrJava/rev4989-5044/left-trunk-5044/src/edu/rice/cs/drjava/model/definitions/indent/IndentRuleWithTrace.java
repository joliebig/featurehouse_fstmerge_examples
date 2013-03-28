

package edu.rice.cs.drjava.model.definitions.indent;

import edu.rice.cs.drjava.model.AbstractDJDocument;


import java.util.ArrayList;
import java.io.PrintStream;


public abstract class IndentRuleWithTrace implements IndentRule {

  private static ArrayList<String> trace = null;
  private static boolean startOver = true;
  private static boolean ruleTraceEnabled = false;

  public static final String YES = "Yes";
  public static final String NO = "No";
  public static final String TERMINUS_RULE = "";

  
  public static void printLastIndentTrace(PrintStream ps) {
    if (trace == null) {
      ps.println("No trace to print");
    } 
    else {
      for (int x = 0; x < trace.size(); x++) {
        ps.println(trace.get(x));
      }
      ps.println("******************************");
    }
  }

  public static void setRuleTraceEnabled(boolean ruleTraceEnabled) {
    IndentRuleWithTrace.ruleTraceEnabled = ruleTraceEnabled;
  }

  static ArrayList<String> getTrace() { return trace; }

  
  protected static void _addToIndentTrace(String ruleName, String direction, boolean terminus) {
    if (ruleTraceEnabled) {
      if (startOver) trace = new ArrayList<String>();
      startOver = terminus;
      trace.add(ruleName + " " + direction);
    }
  }

  
  public boolean indentLine(AbstractDJDocument doc, int pos, Indenter.IndentReason reason) {
    int oldPos = doc.getCurrentLocation();
    doc.setCurrentLocation(pos);
    indentLine(doc, reason);
    if (oldPos > doc.getLength()) oldPos = doc.getLength();
    doc.setCurrentLocation(oldPos);
    return false;
  }

  
  public boolean indentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
    _addToIndentTrace(getRuleName(), TERMINUS_RULE, true);

    
    
    return true;
  }
  
  
  public boolean testIndentLine(AbstractDJDocument doc, int pos, Indenter.IndentReason reason) {
    return indentLine(doc, pos, reason); 
  }
  
  
   public boolean testIndentLine(AbstractDJDocument doc, Indenter.IndentReason reason) {
     return indentLine(doc, reason); 
   }

  
  public String getRuleName() { return this.getClass().getName(); }
}
