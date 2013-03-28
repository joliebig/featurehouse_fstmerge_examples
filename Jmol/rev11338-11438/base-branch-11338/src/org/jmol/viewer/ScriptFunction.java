

package org.jmol.viewer;

import java.util.Hashtable;
import java.util.Vector;

class ScriptFunction {

  

  

  int pt0;
  int chpt0;
  int cmdpt0 = -1;
  String name;
  String script;
  Token[][] aatoken;
  short[] lineNumbers;
  int[][] lineIndices;
  int nParameters;
  Vector names = new Vector();

  Hashtable variables = new Hashtable();
  public boolean isVariable(String ident) {
    return variables.containsKey(ident);
  }

  ScriptVariable returnValue;

  ScriptFunction(String name) {
    this.name = name;
  }

  void setVariables(Hashtable contextVariables, Vector params) {
    int nParams = (params == null ? 0 : params.size());
    for (int i = names.size(); --i >= 0;) {
      String name = ((String) names.get(i)).toLowerCase();
      ScriptVariable var = (i < nParameters && i < nParams ?
          (ScriptVariable) params.get(i) : null);
      if (var != null && var.tok != Token.list)
        var = new ScriptVariable(var);
      contextVariables.put(name, (var == null ? 
          (new ScriptVariable(Token.string, "")).setName(name) : var));
    }
    contextVariables.put("_retval", ScriptVariable.intVariable(0));
  }

  public void unsetVariables(Hashtable contextVariables, Vector params) {
    
    int nParams = (params == null ? 0 : params.size());
    int nNames = names.size();
    if (nParams == 0 || nNames == 0)
      return;
    for (int i = 0; i < nNames && i < nParams; i++) {
      ScriptVariable global = (ScriptVariable) params.get(i);
      if (global.tok != Token.list)
        continue;
      ScriptVariable local = (ScriptVariable) contextVariables.get(((String) names.get(i)).toLowerCase());
      if (local.tok != Token.list)
        continue;
      global.value = local.value;
    }
  }

  void addVariable(String name, boolean isParameter) {
    variables.put(name, name);
    names.add(name);
    if (isParameter)
      nParameters++;
  }

  static void setFunction(ScriptFunction function, String script,
                          int ichCurrentCommand, int pt, short[] lineNumbers,
                          int[][] lineIndices, Vector lltoken) {
    int cmdpt0 = function.cmdpt0;
    int chpt0 = function.chpt0;
    int nCommands = pt - cmdpt0;
    function.setScript(script.substring(chpt0, ichCurrentCommand));
    Token[][] aatoken = function.aatoken = new Token[nCommands][];
    function.lineIndices = new int[nCommands][];
    function.lineNumbers = new short[nCommands];
    short line0 = (short) (lineNumbers[cmdpt0] - 1);
    for (int i = 0; i < nCommands; i++) {
      function.lineNumbers[i] = (short) (lineNumbers[cmdpt0 + i] - line0);
      function.lineIndices[i] = new int[] {lineIndices[cmdpt0 + i][0] - chpt0, lineIndices[cmdpt0 + i][1] - chpt0 };
      aatoken[i] = (Token[]) lltoken.get(cmdpt0 + i);
      if (aatoken[i].length > 0) {
        Token tokenCommand = aatoken[i][0];
        if (Token.tokAttr(tokenCommand.tok, Token.flowCommand))
          tokenCommand.intValue -= (tokenCommand.intValue < 0 ? -cmdpt0
              : cmdpt0);
      }
    }
    for (int i = pt; --i >= cmdpt0;) {
      lltoken.remove(i);
      lineIndices[i][0] = lineIndices[i][1] = 0;
    }
  }

  private void setScript(String s) {
    script = s;
    if (script != null && script != "" && !script.endsWith("\n"))
      script += "\n";
  }

  public String getSignature() {
    StringBuffer s = new StringBuffer();
    s.append("function ").append(name).append(" (");
    for (int i = 0; i < nParameters; i++) {
      if (i > 0)
        s.append(", ");
      s.append(names.get(i));
    }
    s.append(")");
    return s.toString();
  }

  public String toString() {
    StringBuffer s = new StringBuffer("/*\n * ");
    s.append(name).append("\n */\n").append(getSignature()).append("{\n");
    if (script != null)
      s.append(script);
    s.append("}\n");
    return s.toString();
  }

}
