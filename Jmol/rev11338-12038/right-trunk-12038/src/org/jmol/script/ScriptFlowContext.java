

package org.jmol.script;

class ScriptFlowContext {
  
  
  private ScriptCompiler compiler;
  Token token;
  private int pt0;
  ScriptFunction function;
  private ScriptFlowContext parent;
  int lineStart;
  int commandStart;
  int ptLine;
  int ptCommand;
  boolean forceEndIf = true;
  String ident;
  
  ScriptFlowContext(ScriptCompiler compiler, Token token, int pt0, ScriptFlowContext parent) {
    this.compiler = compiler;
    this.token = token;
    this.ident = (String)token.value;
    this.pt0 = pt0;
    this.parent = parent;
    lineStart = ptLine = this.compiler.lineCurrent;
    commandStart = ptCommand = this.compiler.iCommand;
    
  }

  ScriptFlowContext getBreakableContext(int nLevelsUp) {
    ScriptFlowContext f = this;
    while (f != null && (f.token.tok != Token.forcmd
      && f.token.tok != Token.whilecmd || nLevelsUp-- > 0))
      f = f.getParent();
    return f;
  }
  
  boolean checkForceEndIf(int pt) {
    boolean test = forceEndIf 
        && ptCommand < this.compiler.iCommand 
        && ptLine == this.compiler.lineCurrent;
    
    if (test) 
      forceEndIf = false;
    return test;
  }

  int getPt0() {
    return pt0;
  }
  
  int setPt0(int pt0) {
    this.pt0 = pt0;
    setLine();
    return pt0;
  }

  void setLine() {
    ptLine = this.compiler.lineCurrent;
    ptCommand = this.compiler.iCommand + 1;
  }
  
  public String toString() {
    return "ident " + ident
        + " line " + lineStart 
        + " command " + commandStart;  
  }
  
  ScriptFlowContext getParent() {
    
    return parent;
  }
  
  String path() {
    String s = "";
    ScriptFlowContext f = this;
    while (f != null) {
      s = f.ident + "-" + s;
      f = f.parent;
    }
    return "[" + s + "]";
  }
  
  void setFunction(ScriptFunction function) {
    this.function = function;
  }
}