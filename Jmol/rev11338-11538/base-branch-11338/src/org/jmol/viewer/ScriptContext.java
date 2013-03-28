

package org.jmol.viewer;

import java.util.Hashtable;

public class ScriptContext {
  
  public String fullpath = "";
  public String filename;
  public String functionName;
  public String script;
  public short[] lineNumbers;
  public int[][] lineIndices;
  public Token[][] aatoken;
  public Token[] statement;
  public int statementLength;
  public int pc;
  public int pcEnd = Integer.MAX_VALUE;
  public int lineEnd = Integer.MAX_VALUE;
  public int iToken;
  public StringBuffer outputBuffer;
  public Hashtable contextVariables;
  public boolean isStateScript;
  public String errorMessage;
  public String errorMessageUntranslated;
  public int iCommandError = -1;
  public String errorType;
  public ScriptContext[] stack;
  public int scriptLevel;
  public boolean isSyntaxCheck;
  public boolean executionStepping;
  public boolean executionPaused;
  public String scriptExtensions;
  public String contextPath = " >> ";

  ScriptContext() {
  }
}