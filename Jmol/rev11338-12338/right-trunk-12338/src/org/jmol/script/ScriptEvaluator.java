
package org.jmol.script;

import java.awt.Image;
import java.io.IOException;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.api.MinimizerInterface;
import org.jmol.atomdata.RadiusData;
import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.i18n.GT;
import org.jmol.modelset.Atom;
import org.jmol.modelset.AtomCollection;
import org.jmol.modelset.Bond;
import org.jmol.modelset.BoxInfo;
import org.jmol.modelset.Group;
import org.jmol.modelset.LabelToken;
import org.jmol.modelset.MeasurementData;
import org.jmol.modelset.ModelCollection;
import org.jmol.modelset.ModelSet;
import org.jmol.modelset.Bond.BondSet;
import org.jmol.modelset.ModelCollection.StateScript;
import org.jmol.shape.Object2d;
import org.jmol.shape.Shape;
import org.jmol.util.BitSetUtil;
import org.jmol.util.ColorEncoder;
import org.jmol.util.Escape;

import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.util.Parser;
import org.jmol.util.Point3fi;
import org.jmol.util.Quaternion;
import org.jmol.util.TextFormat;
import org.jmol.modelset.TickInfo;
import org.jmol.viewer.ActionManager;
import org.jmol.viewer.FileManager;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.PropertyManager;
import org.jmol.viewer.StateManager;
import org.jmol.viewer.Viewer;

public class ScriptEvaluator {

  
  
  public static final String SCRIPT_COMPLETED = "Script completed";

  public ScriptEvaluator(Viewer viewer) {
    this.viewer = viewer;
    compiler = viewer.compiler;
    definedAtomSets = viewer.definedAtomSets;
  }

  
  
  
  public boolean compileScriptString(String script, boolean tQuiet) {
    clearState(tQuiet);
    contextPath = "[script]";
    return compileScript(null, script, debugScript);
  }

  public boolean compileScriptFile(String filename, boolean tQuiet) {
    clearState(tQuiet);
    contextPath = filename;
    return compileScriptFileInternal(filename, null, null, null);
  }

  public void evaluateCompiledScript(boolean isCmdLine_c_or_C_Option,
                      boolean isCmdLine_C_Option, boolean historyDisabled,
                      boolean listCommands) {
    boolean tempOpen = this.isCmdLine_C_Option;
    this.isCmdLine_C_Option = isCmdLine_C_Option;
    viewer.pushHoldRepaint("runEval");
    interruptExecution = executionPaused = false;
    executionStepping = false;
    isExecuting = true;
    currentThread = Thread.currentThread();
    isSyntaxCheck = this.isCmdLine_c_or_C_Option = isCmdLine_c_or_C_Option;
    timeBeginExecution = System.currentTimeMillis();
    this.historyDisabled = historyDisabled;
    setErrorMessage(null);
    try {
      try {
        setScriptExtensions();
        instructionDispatchLoop(listCommands);
        String script = viewer.getInterruptScript();
        if (script != "")
          runScript(script, null);
      } catch (Error er) {
        viewer.handleError(er, false);
        setErrorMessage("" + er + " " + viewer.getShapeErrorState());
        errorMessageUntranslated = "" + er;
        scriptStatusOrBuffer(errorMessage);
      }
    } catch (ScriptException e) {
      setErrorMessage(e.toString());
      errorMessageUntranslated = e.getErrorMessageUntranslated();
      scriptStatusOrBuffer(errorMessage);
      viewer.notifyError((errorMessage != null
          && errorMessage.indexOf("java.lang.OutOfMemoryError") >= 0 ? "Error"
          : "ScriptException"), errorMessage, errorMessageUntranslated);
    }
    timeEndExecution = System.currentTimeMillis();
    this.isCmdLine_C_Option = tempOpen;
    if (errorMessage == null && interruptExecution)
      setErrorMessage("execution interrupted");
    else if (!tQuiet && !isSyntaxCheck)
      viewer.scriptStatus(SCRIPT_COMPLETED);
    isExecuting = isSyntaxCheck = isCmdLine_c_or_C_Option = historyDisabled = false;
    viewer.setTainted(true);
    viewer.popHoldRepaint("runEval");
  }

  
  public void runScript(String script, StringBuffer outputBuffer)
      throws ScriptException {
    
    pushContext(null);
    contextPath += " >> script() ";

    this.outputBuffer = outputBuffer;
    if (compileScript(null, script + JmolConstants.SCRIPT_EDITOR_IGNORE, false))
      instructionDispatchLoop(false);
    popContext();
  }

  
  public ScriptContext checkScriptSilent(String script) {
    ScriptContext sc = compiler.compile(null, script, false, true, false, true);
    if (sc.errorType != null)
      return sc;
    getScriptContext(sc, false);
    isSyntaxCheck = true;
    isCmdLine_c_or_C_Option = isCmdLine_C_Option = false;
    pc = 0;
    try {
      instructionDispatchLoop(false);
    } catch (ScriptException e) {
      setErrorMessage(e.toString());
      sc = getScriptContext();
    }
    isSyntaxCheck = false;
    return sc;
  }

  
  
  private boolean tQuiet;
  protected boolean isSyntaxCheck;
  private boolean isCmdLine_C_Option;
  protected boolean isCmdLine_c_or_C_Option;
  private boolean historyDisabled;
  protected boolean logMessages;
  private boolean debugScript;
  
  public void setDebugging() {
    debugScript = viewer.getDebugScript();
    logMessages = (debugScript && Logger.debugging);
  }

  private boolean interruptExecution;
  private boolean executionPaused;
  private boolean executionStepping;
  private boolean isExecuting;

  private long timeBeginExecution;
  private long timeEndExecution;

  public int getExecutionWalltime() {
    return (int) (timeEndExecution - timeBeginExecution);
  }

  public void haltExecution() {
    resumePausedExecution();
    interruptExecution = true;
  }

  public void pauseExecution() {
    if (isSyntaxCheck)
      return;
    delay(-100);
    viewer.popHoldRepaint("pauseExecution");
    executionStepping = false;
    executionPaused = true;
  }

  public void stepPausedExecution() {
    executionStepping = true;
    executionPaused = false;
    
    
  }

  public void resumePausedExecution() {
    executionPaused = false;
    executionStepping = false;
  }

  public boolean isScriptExecuting() {
    return isExecuting && !interruptExecution;
  }

  public boolean isExecutionPaused() {
    return executionPaused;
  }

  public boolean isExecutionStepping() {
    return executionStepping;
  }

  
  public String getNextStatement() {
    return (pc < aatoken.length ? 
        setErrorLineMessage(functionName, filename,
            getLinenumber(null), pc, statementAsString(aatoken[pc], -9999)) : "");  
  }
  
  
  private String getCommand(int pc, boolean allThisLine, boolean addSemi) {
    if (pc >= lineIndices.length)
      return "";
    if (allThisLine) {
      int pt0 = -1;
      int pt1 = script.length();
      for (int i = 0; i < lineNumbers.length; i++)
        if (lineNumbers[i] == lineNumbers[pc]) {
          if (pt0 < 0)
            pt0 = lineIndices[i][0];
          pt1 = lineIndices[i][1];
        } else if (lineNumbers[i] == 0 || lineNumbers[i] > lineNumbers[pc]) {
          break;
        }
      if (pt1 == script.length() - 1 && script.endsWith("}"))
        pt1++;
      return (pt0 == script.length() || pt1 < pt0 ? "" 
          : script.substring(Math.max(pt0, 0), Math.min(script.length(), pt1)));
    }
    int ichBegin = lineIndices[pc][0];
    int ichEnd = lineIndices[pc][1];
    
      
        
    String s = "";
    if (ichBegin < 0 || ichEnd <= ichBegin || ichEnd > script.length())
      return "";
    try {
      s = script.substring(ichBegin, ichEnd);
      if (s.indexOf("\\\n") >= 0)
        s = TextFormat.simpleReplace(s, "\\\n", "  ");
      if (s.indexOf("\\\r") >= 0)
        s = TextFormat.simpleReplace(s, "\\\r", "  ");
      
      
      
      
      if (s.length() > 0 && !s.endsWith(";"))
        s += ";";
    } catch (Exception e) {
      Logger.error("darn problem in Eval getCommand: ichBegin=" + ichBegin
          + " ichEnd=" + ichEnd + " len = " + script.length() + "\n" + e);
    }
    return s;
  }

  private void logDebugScript(int ifLevel) {
    if (logMessages) {
      if (statement.length > 0)
        Logger.debug(statement[0].toString());
      for (int i = 1; i < statementLength; ++i)
        Logger.debug(statement[i].toString());
    }
    iToken = -9999;
    if (logMessages) {
      StringBuffer strbufLog = new StringBuffer(80);
      String s = (ifLevel > 0 ? "                          ".substring(0,
          ifLevel * 2) : "");
      strbufLog.append(s).append(statementAsString(statement, iToken));
      viewer.scriptStatus(strbufLog.toString());
    } else {
      String cmd = getCommand(pc, false, false);
      viewer.scriptStatus(cmd);
    }

  }

  
  
  private final static String EXPRESSION_KEY = "e_x_p_r_e_s_s_i_o_n";

  

  public static Object evaluateExpression(Viewer viewer, Object expr) {
    
    ScriptEvaluator e = new ScriptEvaluator(viewer);
    try {
      if (expr instanceof String) {
        if (e.compileScript(null, EXPRESSION_KEY + " = " + expr, false)) {
          e.contextVariables = viewer.getContextVariables();
          e.setStatement(0);
          return e.parameterExpression(2, 0, "", false);
        }
      } else if (expr instanceof Token[]) {
        e.contextVariables = viewer.getContextVariables();
        return e.expression((Token[]) expr, 0, 0, true, false, true, false);
      }
    } catch (Exception ex) {
      Logger.error("Error evaluating: " + expr + "\n" + ex);
    }
    return "ERROR";
  }

  
  public static BitSet getAtomBitSet(ScriptEvaluator e, Object atomExpression) {
    if (atomExpression instanceof BitSet)
      return (BitSet) atomExpression;
    BitSet bs = new BitSet();
    try {
      e.pushContext(null);
      String scr = "select (" + atomExpression + ")";
      scr = TextFormat.replaceAllCharacters(scr, "\n\r", "),(");
      scr = TextFormat.simpleReplace(scr, "()", "(none)");
      if (e.compileScript(null, scr, false)) {
        e.statement = e.aatoken[0];
        bs = e.expression(e.statement, 1, 0, false, false, true, true);
      }
      e.popContext();
    } catch (Exception ex) {
      Logger.error("getAtomBitSet " + atomExpression + "\n" + ex);
    }
    return bs;
  }

  
  public static Vector getAtomBitSetVector(ScriptEvaluator e, int atomCount,
                                    Object atomExpression) {
    Vector V = new Vector();
    BitSet bs = getAtomBitSet(e, atomExpression);
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
      V.addElement(new Integer(i));
    return V;
  }

  private Object parameterExpression(int pt, int ptMax, String key,
                                     boolean asVector) throws ScriptException {
    return parameterExpression(pt, ptMax, key, asVector, -1, false, null, null);
  }

  
  private Object parameterExpression(int pt, int ptMax, String key,
                                     boolean asVector, int ptAtom,
                                     boolean isArrayItem, Hashtable localVars,
                                     String localVar) throws ScriptException {

    
    Object v, res;
    boolean isImplicitAtomProperty = (localVar != null);
    boolean isOneExpressionOnly = (pt < 0);
    boolean returnBoolean = (key == null);
    boolean returnString = (key != null && key.length() == 0);
    if (isOneExpressionOnly)
      pt = -pt;
    int nParen = 0;
    ScriptMathProcessor rpn = new ScriptMathProcessor(this, isArrayItem,
        asVector);
    if (pt == 0 && ptMax == 0) 
      pt = 2;
    if (ptMax < pt)
      ptMax = statementLength;
    out: for (int i = pt; i < ptMax; i++) {
      v = null;
      int tok = getToken(i).tok;
      if (isImplicitAtomProperty && tokAt(i + 1) != Token.per) {
        ScriptVariable token = (localVars != null
            && localVars.containsKey(theToken.value) ? null
            : getBitsetPropertySelector(i, false));
        if (token != null) {
          rpn.addX((ScriptVariable) localVars.get(localVar));
          if (!rpn.addOp(token)) {
            error(ERROR_invalidArgument);
          }
          if (token.intValue == Token.function
              && tokAt(iToken + 1) != Token.leftparen) {
            rpn.addOp(Token.tokenLeftParen);
            rpn.addOp(Token.tokenRightParen);
          }
          i = iToken;
          continue;
        }
      }
      switch (tok) {
      case Token.ifcmd:
        if (getToken(++i).tok != Token.leftparen)
          error(ERROR_invalidArgument);
        if (localVars == null)
          localVars = new Hashtable();
        res = parameterExpression(++i, -1, null, false, -1, false, localVars,
            localVar);
        boolean TF = ((Boolean) res).booleanValue();
        int iT = iToken;
        if (getToken(iT++).tok != Token.semicolon)
          error(ERROR_invalidArgument);
        parameterExpression(iT, -1, null, false);
        int iF = iToken;
        if (tokAt(iF++) != Token.semicolon)
          error(ERROR_invalidArgument);
        parameterExpression(-iF, -1, null, false, 1, false, localVars, localVar);
        int iEnd = iToken;
        if (tokAt(iEnd) != Token.rightparen)
          error(ERROR_invalidArgument);
        v = parameterExpression(TF ? iT : iF, TF ? iF : iEnd, "XXX", false, 1,
            false, localVars, localVar);
        i = iEnd;
        break;
      case Token.forcmd:
      case Token.select:
        boolean isFunctionOfX = (pt > 0);
        boolean isFor = (isFunctionOfX && tok == Token.forcmd);
        
        
        
        
        String dummy;
        
        
        if (isFunctionOfX) {
          if (getToken(++i).tok != Token.leftparen
              || !Token.tokAttr(getToken(++i).tok, Token.identifier))
            error(ERROR_invalidArgument);
          dummy = parameterAsString(i);
          if (getToken(++i).tok != Token.semicolon)
            error(ERROR_invalidArgument);
        } else {
          dummy = "_x";
        }
        
        
        v = tokenSetting(-(++i)).value;
        if (!(v instanceof BitSet))
          error(ERROR_invalidArgument);
        BitSet bsAtoms = (BitSet) v;
        i = iToken;
        if (isFunctionOfX && getToken(i++).tok != Token.semicolon)
          error(ERROR_invalidArgument);
        
        
        
        
        BitSet bsSelect = new BitSet();
        BitSet bsX = new BitSet();
        String[] sout = (isFor ? new String[BitSetUtil.cardinalityOf(bsAtoms)]
            : null);
        ScriptVariable t = null;
        if (localVars == null)
          localVars = new Hashtable();
        bsX.set(0);
        localVars.put(dummy, t = ScriptVariable.getVariableSelected(0, bsX)
            .setName(dummy));
        
        int pt2 = -1;
        if (isFunctionOfX) {
          pt2 = i - 1;
          int np = 0;
          int tok2;
          while (np >= 0 && ++pt2 < ptMax) {
            if ((tok2 = tokAt(pt2)) == Token.rightparen)
              np--;
            else if (tok2 == Token.leftparen)
              np++;
          }
        }
        int p = 0;
        int jlast = 0;
        int j = bsAtoms.nextSetBit(0);
        if (j < 0) {
          iToken = pt2 - 1;
        } else if (!isSyntaxCheck) {
          for (; j >= 0; j = bsAtoms.nextSetBit(j + 1)) {
            if (jlast >= 0)
              bsX.clear(jlast);
            jlast = j;
            bsX.set(j);
            t.index = j;
            res = parameterExpression(i, pt2, (isFor ? "XXX" : null), isFor, j,
                false, localVars, isFunctionOfX ? null : dummy);
            if (isFor) {
              if (res == null || ((Vector) res).size() == 0)
                error(ERROR_invalidArgument);
              sout[p++] = ScriptVariable.sValue((ScriptVariable) ((Vector) res)
                  .elementAt(0));
            } else if (((Boolean) res).booleanValue()) {
              bsSelect.set(j);
            }
          }
        }
        if (isFor) {
          v = sout;
        } else if (isFunctionOfX) {
          v = bsSelect;
        } else {
          return bitsetVariableVector(bsSelect);
        }
        i = iToken + 1;
        break;
      case Token.semicolon: 
        break out;
      case Token.decimal:
        rpn.addXNum(ScriptVariable.getVariable(theToken.value));
        break;
      case Token.spec_seqcode:
      case Token.integer:
        rpn.addXNum(ScriptVariable.intVariable(theToken.intValue));
        break;
      
      case Token.plane:
        if (tokAt(iToken + 1) == Token.leftparen) {
          if (!rpn.addOp(theToken, true))
            error(ERROR_invalidArgument);
          break;
        }
        rpn.addX(new ScriptVariable(theToken));
        break;
      case Token.atomname:
      case Token.atomtype:
      case Token.branch:
      case Token.boundbox:
      case Token.chain:
      case Token.coord:
      case Token.element:
      case Token.group:
      case Token.model:
      case Token.molecule:
      case Token.site:
      case Token.structure:
        
      case Token.on:
      case Token.off:
      case Token.string:
      case Token.point3f:
      case Token.point4f:
      case Token.bitset:
        rpn.addX(new ScriptVariable(theToken));
        break;
      case Token.dollarsign:
        rpn.addX(new ScriptVariable(Token.point3f, centerParameter(i)));
        i = iToken;
        break;
      case Token.leftbrace:
        v = getPointOrPlane(i, false, true, true, false, 3, 4);
        i = iToken;
        break;
      case Token.expressionBegin:
        if (tokAt(i + 1) == Token.all && tokAt(i + 2) == Token.expressionEnd) {
          tok = Token.all;
          iToken += 2;
        }
        
      case Token.all:
        if (tok == Token.all)
          v = viewer.getModelAtomBitSet(-1, true);
        else
          v = expression(statement, i, 0, true, true, true, true);
        i = iToken;
        if (nParen == 0 && isOneExpressionOnly) {
          iToken++;
          return bitsetVariableVector(v);
        }
        break;
      case Token.expressionEnd:
        i++;
        break out;
      case Token.rightbrace:
        error(ERROR_invalidArgument);
        break;
      case Token.comma: 
        if (!rpn.addOp(theToken))
          error(ERROR_invalidArgument);
        break;
      case Token.per:
        ScriptVariable token = getBitsetPropertySelector(i + 1, false);
        if (token == null)
          error(ERROR_invalidArgument);
        
        boolean isUserFunction = (token.intValue == Token.function);
        boolean allowMathFunc = true;
        int tok2 = tokAt(iToken + 2);
        if (tokAt(iToken + 1) == Token.per) {
          switch (tok2) {
          case Token.all:
            tok2 = Token.minmaxmask;
            if (tokAt(iToken + 3) == Token.per
                && tokAt(iToken + 4) == Token.bin)
              tok2 = Token.allfloat;
            
          case Token.min:
          case Token.max:
          case Token.stddev:
          case Token.sum:
          case Token.sum2:
          case Token.average:
            allowMathFunc = (isUserFunction || tok2 == Token.minmaxmask || tok2 == Token.allfloat);
            token.intValue |= tok2;
            getToken(iToken + 2);
          }
        }
        allowMathFunc &= (tokAt(iToken + 1) == Token.leftparen || isUserFunction);
        if (!rpn.addOp(token, allowMathFunc))
          error(ERROR_invalidArgument);
        i = iToken;
        if (token.intValue == Token.function && tokAt(i + 1) != Token.leftparen) {
          rpn.addOp(Token.tokenLeftParen);
          rpn.addOp(Token.tokenRightParen);
        }
        break;
      default:
        if (Token.tokAttr(theTok, Token.mathop)
            || Token.tokAttr(theTok, Token.mathfunc)) {
          if (!rpn.addOp(theToken)) {
            if (ptAtom >= 0) {
              
              break out;
            }
            error(ERROR_invalidArgument);
          }
          if (theTok == Token.leftparen)
            nParen++;
          else if (theTok == Token.rightparen) {
            if (--nParen == 0 && isOneExpressionOnly) {
              iToken++;
              break out;
            }
          }
        } else if (Token.tokAttr(theTok, Token.identifier)
            && viewer.isFunction((String) theToken.value)) {
          if (!rpn.addOp(new ScriptVariable(Token.function, theToken.value))) {
            
            error(ERROR_invalidArgument);
          }
          if (tokAt(i + 1) != Token.leftparen) {
            rpn.addOp(Token.tokenLeftParen);
            rpn.addOp(Token.tokenRightParen);
          }
        } else {
          String name = parameterAsString(i).toLowerCase();
          if (isSyntaxCheck)
            v = name;
          else if ((localVars == null || (v = localVars.get(name)) == null)
              && (v = getContextVariableAsVariable(name)) == null)
            rpn.addX(viewer.getOrSetNewVariable(name, false));
          break;
        }
      }
      if (v != null) {
        if (v instanceof BitSet)
          rpn.addX((BitSet) v);
        else
          rpn.addX(v);
      }
    }
    ScriptVariable result = rpn.getResult(false, key);
    if (result == null) {
      if (!isSyntaxCheck)
        rpn.dumpStacks("null result");
      error(ERROR_endOfStatementUnexpected);
    }
    if (result.tok == Token.vector)
      return result.value;
    if (returnBoolean)
      return Boolean.valueOf(ScriptVariable.bValue(result));
    if (returnString) {
      if (result.tok == Token.string)
        result.intValue = Integer.MAX_VALUE;
      return ScriptVariable.sValue(result);
    }
    switch (result.tok) {
    case Token.on:
    case Token.off:
      return Boolean.valueOf(result.intValue == 1);
    case Token.integer:
      return new Integer(result.intValue);
    case Token.bitset:
    case Token.decimal:
    case Token.string:
    case Token.point3f:
    default:
      return result.value;
    }
  }

  Object bitsetVariableVector(Object v) {
    Vector resx = new Vector();
    if (v instanceof BitSet)
      resx.addElement(new ScriptVariable(Token.bitset, v));
    return resx;
  }

  Object getBitsetIdent(BitSet bs, String label, Object tokenValue,
                        boolean useAtomMap, int index, boolean isExplicitlyAll) {
    boolean isAtoms = !(tokenValue instanceof BondSet);
    if (isAtoms) {
      if (label == null)
        label = viewer.getStandardLabelFormat();
      else if (label.length() == 0)
        label = "%[label]";
    }
    int pt = (label == null ? -1 : label.indexOf("%"));
    boolean haveIndex = (index != Integer.MAX_VALUE);
    if (bs == null || isSyntaxCheck || isAtoms && pt < 0) {
      if (label == null)
        label = "";
      return isExplicitlyAll ? new String[] { label } : (Object) label;
    }
    ModelSet modelSet = viewer.getModelSet();
    int n = 0;
    int[] indices = (isAtoms || !useAtomMap ? null : ((BondSet) tokenValue)
        .getAssociatedAtoms());
    if (indices == null && label != null && label.indexOf("%D") > 0)
      indices = viewer.getAtomIndices(bs);
    boolean asIdentity = (label == null || label.length() == 0);
    Hashtable htValues = (isAtoms || asIdentity ? null : LabelToken
        .getBondLabelValues());
    LabelToken[] tokens = (asIdentity ? null : isAtoms ? LabelToken.compile(
        viewer, label, '\0', null) : LabelToken.compile(viewer, label, '\1',
        htValues));
    int nmax = (haveIndex ? 1 : BitSetUtil.cardinalityOf(bs));
    String[] sout = new String[nmax];
    for (int j = (haveIndex ? index : bs.nextSetBit(0)); j >= 0; j = bs.nextSetBit(j + 1)) {
      String str;
      if (isAtoms) {
        if (asIdentity)
          str = modelSet.getAtomAt(j).getInfo();
        else
          str = LabelToken.formatLabel(viewer, modelSet.getAtomAt(j), null,
              tokens, '\0', indices);
      } else {
        Bond bond = modelSet.getBondAt(j);
        if (asIdentity)
          str = bond.getIdentity();
        else
          str = LabelToken.formatLabel(viewer, bond, tokens, htValues, indices);
      }
      str = TextFormat.formatString(str, "#", (n + 1));
      sout[n++] = str;
      if (haveIndex)
        break;
    }
    return nmax == 1 && !isExplicitlyAll ? sout[0] : (Object) sout;
  }

  private ScriptVariable getBitsetPropertySelector(int i, boolean mustBeSettable)
      throws ScriptException {
    int tok = getToken(i).tok;
    String s = null;
    switch (tok) {
    case Token.min:
    case Token.max:
    case Token.average:
    case Token.stddev:
    case Token.sum:
    case Token.sum2:
    case Token.property:
      break;
    default:
      if (Token.tokAttrOr(tok, Token.atomproperty, Token.mathproperty))
        break;
      if (!Token.tokAttr(tok, Token.identifier))
        return null;
      String name = parameterAsString(i);
      switch (tok = Token.getSettableTokFromString(name)) {
      case Token.atomx:
      case Token.atomy:
      case Token.atomz:
      case Token.qw:
        break;
      default:
        if (!mustBeSettable && viewer.isFunction(name)) {
          tok = Token.function;
          break;
        }
        return null;
      }
      break;
    }
    if (mustBeSettable && !Token.tokAttr(tok, Token.settable))
      return null;
    if (s == null)
      s = parameterAsString(i).toLowerCase();
    return new ScriptVariable(Token.propselector, tok, s);
  }

  protected Object getBitsetProperty(BitSet bs, int tok, Point3f ptRef,
                                     Point4f planeRef, Object tokenValue,
                                     Object opValue, boolean useAtomMap,
                                     int index, boolean asVector)
      throws ScriptException {

    
    
    

    boolean haveIndex = (index != Integer.MAX_VALUE);

    boolean isAtoms = haveIndex || !(tokenValue instanceof BondSet);
    

    int minmaxtype = tok & Token.minmaxmask;
    boolean allFloat = (minmaxtype == Token.allfloat);
    boolean isExplicitlyAll = (minmaxtype == Token.minmaxmask || allFloat);
    tok &= ~Token.minmaxmask;
    if (tok == Token.nada)
      tok = (isAtoms ? Token.atoms : Token.bonds);

    

    boolean isPt = false;
    boolean isInt = false;
    boolean isString = false;
    switch (tok) {
    case Token.xyz:
    case Token.vibxyz:
    case Token.fracxyz:
    case Token.unitxyz:
    case Token.color:
      isPt = true;
      break;
    case Token.function:
    case Token.distance:
      break;
    default:
      isInt = Token.tokAttr(tok, Token.intproperty)
          && !Token.tokAttr(tok, Token.floatproperty);
      
      isString = !isInt && Token.tokAttr(tok, Token.strproperty);
      
    }

    

    Point3f pt = (isPt || !isAtoms ? new Point3f() : null);
    if (isString || isExplicitlyAll)
      minmaxtype = Token.all;
    Vector vout = (minmaxtype == Token.all ? new Vector() : null);

    BitSet bsNew = null;
    String userFunction = null;
    Vector params = null;
    BitSet bsAtom = null;
    ScriptVariable tokenAtom = null;
    Point3f ptT = null;
    float[] data = null;

    switch (tok) {
    case Token.atoms:
    case Token.bonds:
      if (isSyntaxCheck)
        return bs;
      bsNew = (tok == Token.atoms ? (isAtoms ? bs : viewer.getAtomBits(
          Token.bonds, bs)) : (isAtoms ? (BitSet) new BondSet(viewer
          .getBondsForSelectedAtoms(bs)) : bs));
      int i;
      switch (minmaxtype) {
      case Token.min:
        i = bsNew.nextSetBit(0);
        break;
      case Token.max:
        i = bsNew.length() - 1;
        break;
      case Token.stddev:
      case Token.sum:
      case Token.sum2:
        return new Float(Float.NaN);
      default:
        return bsNew;
      }
      bsNew.clear();
      if (i >= 0)
        bsNew.set(i);
      return bsNew;
    case Token.identify:
      switch (minmaxtype) {
      case 0:
      case Token.all:
        return getBitsetIdent(bs, null, tokenValue, useAtomMap, index,
            isExplicitlyAll);
      }
      return "";
    case Token.function:
      userFunction = (String) ((Object[]) opValue)[0];
      params = (Vector) ((Object[]) opValue)[1];
      bsAtom = new BitSet();
      tokenAtom = new ScriptVariable(Token.bitset, bsAtom);
      break;
    case Token.straightness:
    case Token.surfacedistance:
      viewer.autoCalculate(tok);
      break;
    case Token.distance:
      if (ptRef == null && planeRef == null)
        return new Point3f();
      break;
    case Token.color:
      ptT = new Point3f();
      break;
    case Token.property:
      data = viewer.getDataFloat((String) opValue);
      break;
    }

    int n = 0;
    int ivvMinMax = 0;
    int ivMinMax = 0;
    float fvMinMax = 0;
    double sum = 0;
    double sum2 = 0;
    switch (minmaxtype) {
    case Token.min:
      ivMinMax = Integer.MAX_VALUE;
      fvMinMax = Float.MAX_VALUE;
      break;
    case Token.max:
      ivMinMax = Integer.MIN_VALUE;
      fvMinMax = -Float.MAX_VALUE;
      break;
    }
    ModelSet modelSet = viewer.getModelSet();
    int mode = (isPt ? 3 : isString ? 2 : isInt ? 1 : 0);
    if (isAtoms) {
      boolean haveBitSet = (bs != null);
      int iModel = -1;
      int i0, i1;
      if (haveIndex) {
        i0 = index;
        i1 = index + 1;
      } else if (haveBitSet) {
        i0 = bs.nextSetBit(0);
        i1 = bs.length();
      } else {
        i0 = 0;
        i1 = viewer.getAtomCount();
      }
      if (isSyntaxCheck)
        i1 = 0;
      for (int i = i0; i >= 0 && i < i1; i = (haveBitSet ? bs.nextSetBit(i + 1)
          : i + 1)) {
        n++;
        Atom atom = modelSet.getAtomAt(i);
        switch (mode) {
        case 0: 
          float fv = Float.MAX_VALUE;
          switch (tok) {
          case Token.function:
            bsAtom.set(i);
            fv = ScriptVariable.fValue(getFunctionReturn(userFunction, params,
                tokenAtom));
            bsAtom.clear(i);
            break;
          case Token.property:
            fv = (data == null ? 0 : data[i]);
            break;
          case Token.distance:
            if (planeRef != null)
              fv = Measure.distanceToPlane(planeRef, atom);
            else
              fv = atom.distance(ptRef);
            break;
          default:
            fv = Atom.atomPropertyFloat(viewer, atom, tok);
          }
          if (fv == Float.MAX_VALUE || Float.isNaN(fv)
              && minmaxtype != Token.all) {
            n--; 
            continue;
          }
          switch (minmaxtype) {
          case Token.min:
            if (fv < fvMinMax)
              fvMinMax = fv;
            break;
          case Token.max:
            if (fv > fvMinMax)
              fvMinMax = fv;
            break;
          case Token.all:
            vout.add(new Float(fv));
            break;
          case Token.sum2:
          case Token.stddev:
            sum2 += ((double) fv) * fv;
            
          case Token.sum:
          default:
            sum += fv;
          }
          break;
        case 1: 
          int iv = 0;
          switch (tok) {
          case Token.symop:
            
            
            
            
            if (atom.getModelIndex() != iModel)
              iModel = atom.getModelIndex();
            BitSet bsSym = atom.getAtomSymmetry();
            int p = 0;
            switch (minmaxtype) {
            case Token.min:
              ivvMinMax = Integer.MAX_VALUE;
              break;
            case Token.max:
              ivvMinMax = Integer.MIN_VALUE;
              break;
            }
            for (int k = bsSym.nextSetBit(0); k >= 0; k = bsSym
                .nextSetBit(k + 1)) {
              iv += k + 1;
              switch (minmaxtype) {
              case Token.min:
                ivvMinMax = Math.min(ivvMinMax, k + 1);
                break;
              case Token.max:
                ivvMinMax = Math.max(ivvMinMax, k + 1);
                break;
              }
              p++;
            }
            switch (minmaxtype) {
            case Token.min:
            case Token.max:
              iv = ivvMinMax;
            }
            n += p - 1;
            break;
          case Token.configuration:
          case Token.cell:
            error(ERROR_unrecognizedAtomProperty, Token.nameOf(tok));
          default:
            iv = Atom.atomPropertyInt(atom, tok);
          }
          switch (minmaxtype) {
          case Token.min:
            if (iv < ivMinMax)
              ivMinMax = iv;
            break;
          case Token.max:
            if (iv > ivMinMax)
              ivMinMax = iv;
            break;
          case Token.all:
            vout.add(new Integer(iv));
            break;
          case Token.sum2:
          case Token.stddev:
            sum2 += ((double) iv) * iv;
            
          case Token.sum:
          default:
            sum += iv;
          }
          break;
        case 2: 
          vout.add(Atom.atomPropertyString(atom, tok));
          break;
        case 3: 
          Tuple3f t = Atom.atomPropertyTuple(atom, tok);
          if (t == null)
            error(ERROR_unrecognizedAtomProperty, Token.nameOf(tok));
          pt.add(t);
          if (minmaxtype == Token.all) {
            vout.add(new Point3f(pt));
            pt.set(0, 0, 0);
          }
          break;
        }
        if (haveIndex)
          break;
      }
    } else { 
      boolean isAll = (bs == null);
      int i0 = (isAll ? 0 : bs.nextSetBit(0));
      int i1 = viewer.getBondCount();
      for (int i = i0; i >= 0 && i < i1; i = (isAll ? i + 1 : bs.nextSetBit(i + 1))) {
        n++;
        Bond bond = modelSet.getBondAt(i);
        switch (tok) {
        case Token.length:
          float fv = bond.getAtom1().distance(bond.getAtom2());
          switch (minmaxtype) {
          case Token.min:
            if (fv < fvMinMax)
              fvMinMax = fv;
            break;
          case Token.max:
            if (fv > fvMinMax)
              fvMinMax = fv;
            break;
          case Token.all:
            vout.add(new Float(fv));
            break;
          case Token.sum2:
          case Token.stddev:
            sum2 += (double) fv * fv;
            
          case Token.sum:
          default:
            sum += fv;
          }
          break;
        case Token.xyz:
          switch (minmaxtype) {
          case Token.all:
            pt.set(bond.getAtom1());
            pt.add(bond.getAtom2());
            pt.scale(0.5f);
            vout.add(new Point3f(pt));
            break;
          default:
            pt.add(bond.getAtom1());
            pt.add(bond.getAtom2());
            n++;
          }
          break;
        case Token.color:
          Graphics3D.colorPointFromInt(viewer.getColorArgbOrGray(bond
              .getColix()), ptT);
          switch (minmaxtype) {
          case Token.all:
            vout.add(new Point3f(ptT));
            break;
          default:
            pt.add(ptT);
          }
          break;
        default:
          error(ERROR_unrecognizedBondProperty, Token.nameOf(tok));
        }
      }
    }
    if (minmaxtype == Token.all) {
      if (asVector)
        return vout;
      int len = vout.size();
      if (isString && !isExplicitlyAll && len == 1)
        return vout.get(0);
      if (tok == Token.sequence) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++)
          sb.append((String) vout.get(i));
        return sb.toString();
      }
      if (allFloat) {
        Float[] fout = new Float[len];
        Point3f zero = (len > 0 && isPt ? new Point3f() : null);
        for (int i = len; --i >= 0;) {
          Object v = vout.get(i);
          switch (mode) {
          case 0:
            fout[i] = (Float) v;
            break;
          case 1:
            fout[i] = new Float(((Integer) v).floatValue());
            break;
          case 2:
            fout[i] = new Float(Parser.parseFloat((String) v));
            break;
          case 3:
            fout[i] = new Float(((Point3f) v).distance(zero));
            break;
          }
        }
        return fout;
      }
      String[] sout = new String[len];
      for (int i = len; --i >= 0;) {
        Object v = vout.get(i);
        if (v instanceof Point3f)
          sout[i] = Escape.escape((Point3f) v);
        else
          sout[i] = "" + vout.get(i);
      }
      return sout;
    }
    if (isPt)
      return (n == 0 ? pt : new Point3f(pt.x / n, pt.y / n, pt.z / n));
    if (n == 0 || n == 1 && minmaxtype == Token.stddev)
      return new Float(Float.NaN);
    if (isInt) {
      switch (minmaxtype) {
      case Token.min:
      case Token.max:
        return new Integer(ivMinMax);
      }
    }
    switch (minmaxtype) {
    case Token.min:
    case Token.max:
      sum = fvMinMax;
      break;
    case Token.sum:
      break;
    case Token.sum2:
      sum = sum2;
      break;
    case Token.stddev:
      
      
      
      
      sum = Math.sqrt((sum2 - sum * sum / n) / (n - 1));
      break;
    default:
      sum /= n;
      break;
    }
    return new Float(sum);
  }

  private void setBitsetProperty(BitSet bs, int tok, int iValue, float fValue,
                                 Token tokenValue) throws ScriptException {
    if (isSyntaxCheck || BitSetUtil.cardinalityOf(bs) == 0)
      return;
    String[] list = null;
    String sValue = null;
    float[] fvalues = null;
    int nValues;
    switch (tok) {
    case Token.xyz:
    case Token.fracxyz:
    case Token.vibxyz:
      if (tokenValue.tok == Token.point3f) {
        viewer.setAtomCoord(bs, tok, tokenValue.value);
      } else if (tokenValue.tok == Token.list) {
        list = (String[]) tokenValue.value;
        if ((nValues = list.length) == 0)
          return;
        Point3f[] values = new Point3f[nValues];
        for (int i = nValues; --i >= 0;) {
          Object o = Escape.unescapePoint(list[i]);
          if (!(o instanceof Point3f))
            error(ERROR_unrecognizedParameter, "ARRAY", list[i]);
          values[i] = (Point3f) o;
        }
        viewer.setAtomCoord(bs, tok, values);
      }
      return;
    case Token.color:
      if (tokenValue.tok == Token.point3f)
        iValue = Graphics3D.colorPtToInt((Point3f) tokenValue.value);
      else if (tokenValue.tok == Token.list) {
        list = (String[]) tokenValue.value;
        if ((nValues = list.length) == 0)
          return;
        int[] values = new int[nValues];
        for (int i = nValues; --i >= 0;) {
          Object pt = Escape.unescapePoint(list[i]);
          if (pt instanceof Point3f)
            values[i] = Graphics3D.colorPtToInt((Point3f) pt);
          else
            values[i] = Graphics3D.getArgbFromString(list[i]);
          if (values[i] == 0
              && (values[i] = Parser.parseInt(list[i])) == Integer.MIN_VALUE)
            error(ERROR_unrecognizedParameter, "ARRAY", list[i]);
        }
        viewer.setShapeProperty(JmolConstants.SHAPE_BALLS, "colorValues",
            values, bs);
        return;
      }
      viewer.setShapeProperty(JmolConstants.SHAPE_BALLS, "color",
          tokenValue.tok == Token.string ? tokenValue.value : new Integer(
              iValue), bs);
      return;
    case Token.label:
    case Token.format:
      if (tokenValue.tok == Token.list)
        list = (String[]) tokenValue.value;
      else
        sValue = ScriptVariable.sValue(tokenValue);
      viewer.setAtomProperty(bs, tok, iValue, fValue, sValue, fvalues, list);
      return;
    case Token.element:
    case Token.elemno:
      clearDefinedVariableAtomSets();
      break;
    }
    if (tokenValue.tok == Token.list || tokenValue.tok == Token.string) {
      list = (tokenValue.tok == Token.list ? (String[]) tokenValue.value
          : Parser.getTokens(ScriptVariable.sValue(tokenValue)));
      if ((nValues = list.length) == 0)
        return;
      fvalues = new float[nValues];
      for (int i = nValues; --i >= 0;)
        fvalues[i] = (tok == Token.element ? JmolConstants
            .elementNumberFromSymbol(list[i]) : Parser.parseFloat(list[i]));
      if (tokenValue.tok == Token.string && nValues == 1) {
        fValue = fvalues[0];
        iValue = (int) fValue;
        sValue = list[0];
        list = null;
        fvalues = null;
      }
    }
    viewer.setAtomProperty(bs, tok, iValue, fValue, sValue, fvalues, list);
  }

  
  
  private final static int scriptLevelMax = 10;

  private Thread currentThread;
  protected Viewer viewer;
  protected ScriptCompiler compiler;
  private Hashtable definedAtomSets;
  private StringBuffer outputBuffer;
  private ScriptContext[] stack = new ScriptContext[scriptLevelMax];

  private String contextPath = "";
  private String filename;
  private String functionName;
  private boolean isStateScript;
  private int scriptLevel;
  private int scriptReportingLevel = 0;
  private int commandHistoryLevelMax = 0;

  
  private Token[][] aatoken;
  private short[] lineNumbers;
  private int[][] lineIndices;
  private Hashtable contextVariables;
  public Hashtable getContextVariables() {
    return contextVariables;
  }

  private String script;
  public String getScript() {
    return script;
  }

  
  protected int pc; 
  private String thisCommand;
  private String fullCommand;
  private Token[] statement;
  private int statementLength;
  private int iToken;
  private int lineEnd;
  private int pcEnd;
  private String scriptExtensions;

  

  private boolean compileScript(String filename, String strScript,
                                boolean debugCompiler) {
    this.filename = filename;
    strScript = fixScriptPath(strScript, filename);
    getScriptContext(compiler.compile(filename, strScript, false, false,
        debugCompiler, false), false);
    isStateScript = (script.indexOf(Viewer.STATE_VERSION_STAMP) >= 0);
    String s = script;
    pc = setScriptExtensions();
    if (!isSyntaxCheck && viewer.isScriptEditorVisible()
        && strScript.indexOf(JmolConstants.SCRIPT_EDITOR_IGNORE) < 0)
      viewer.scriptStatus("");
    script = s;
    return !error;
  }

  private String fixScriptPath(String strScript, String filename) {
    if (filename != null && strScript.indexOf("$SCRIPT_PATH$") >= 0) {
      String path = filename;
      
      int pt = Math.max(filename.lastIndexOf("|"), filename.lastIndexOf("/"));
      path = path.substring(0, pt + 1);
      strScript = TextFormat.simpleReplace(strScript, "$SCRIPT_PATH$/", path);
      
      strScript = TextFormat.simpleReplace(strScript, "$SCRIPT_PATH$", path);
    }
    return strScript;
  }

  private int setScriptExtensions() {
    String extensions = scriptExtensions;
    if (extensions == null)
      return 0;
    int pt = extensions.indexOf("##SCRIPT_STEP");
    if (pt >= 0) {
      executionStepping = true;
    }
    pt = extensions.indexOf("##SCRIPT_START=");
    if (pt < 0)
      return 0;
    pt = Parser.parseInt(extensions.substring(pt + 15));
    if (pt == Integer.MIN_VALUE)
      return 0;
    for (pc = 0; pc < lineIndices.length; pc++) {
      if (lineIndices[pc][0] > pt || lineIndices[pc][1] >= pt)
        break;
    }
    if (pc > 0 && pc < lineIndices.length && lineIndices[pc][0] > pt)
      --pc;
    return pc;
  }

  private void runScript(String script) throws ScriptException {
    if (!viewer.isPreviewOnly())
      runScript(script, outputBuffer);
  }

  private boolean compileScriptFileInternal(String filename, String localPath, 
                                            String remotePath, String scriptPath) {
    
    if (filename.toLowerCase().indexOf("javascript:") == 0)
      return compileScript(filename, viewer.jsEval(filename.substring(11)),
          debugScript);
    String[] data = new String[2];
    data[0] = filename;
    if (!viewer.getFileAsString(data, Integer.MAX_VALUE, false)) {
      setErrorMessage("io error reading " + data[0] + ": " + data[1]);
      return false;
    }
    this.filename = filename;
    String script = fixScriptPath(data[1], data[0]);
    if (scriptPath == null) {
      scriptPath = viewer.getFullPath(filename);
      scriptPath = scriptPath.substring(0, scriptPath.lastIndexOf("/"));
    }
    script = FileManager.setScriptFileReferences(script, localPath, remotePath, scriptPath);
    return compileScript(filename, script, debugScript);
  }


  
  
  private Object getParameter(String key, boolean asToken) {
    Object v = getContextVariableAsVariable(key);
    if (v == null)
      v = viewer.getParameter(key);
    if (asToken)
      return (v instanceof ScriptVariable ? (ScriptVariable) v : ScriptVariable
          .getVariable(v));
    return (v instanceof ScriptVariable ? ScriptVariable
        .oValue((ScriptVariable) v) : v);
  }

  private String getParameterEscaped(String var) {
    ScriptVariable v = getContextVariableAsVariable(var);
    return (v == null ? "" + viewer.getParameterEscaped(var) : Escape
        .escape(v.value));
  }

  private String getStringParameter(String var, boolean orReturnName) {
    ScriptVariable v = getContextVariableAsVariable(var);
    if (v != null)
      return ScriptVariable.sValue(v);
    String val = "" + viewer.getParameter(var);
    return (val.length() == 0 && orReturnName ? var : val);
  }

  private Object getNumericParameter(String var) {
    if (var.equalsIgnoreCase("_modelNumber")) {
      int modelIndex = viewer.getCurrentModelIndex();
      return new Integer(modelIndex < 0 ? 0 : viewer
          .getModelFileNumber(modelIndex));
    }
    ScriptVariable v = getContextVariableAsVariable(var);
    if (v == null) {
      Object val = viewer.getParameter(var);
      if (!(val instanceof String))
        return val;
      v = new ScriptVariable(Token.string, val);
    }
    return ScriptVariable.nValue(v);
  }

  private ScriptVariable getContextVariableAsVariable(String var) {
    if (var.equals("expressionBegin"))
      return null;
    var = var.toLowerCase();
    if (contextVariables != null && contextVariables.containsKey(var))
      return (ScriptVariable) contextVariables.get(var);
    for (int i = scriptLevel; --i >= 0;)
      if (stack[i].contextVariables != null
          && stack[i].contextVariables.containsKey(var))
        return (ScriptVariable) stack[i].contextVariables.get(var);
    return null;
  }

  private Object getStringObjectAsVariable(String s, String key) {
    if (s == null || s.length() == 0)
      return s;
    Object v = ScriptVariable.unescapePointOrBitsetAsVariable(s);
    if (v instanceof String && key != null)
      v = viewer.setUserVariable(key, new ScriptVariable(Token.string, (String) v));
    return v;
  }

  private boolean loadFunction(String name, Vector params) {
    ScriptFunction function = viewer.getFunction(name);
    if (function == null)
      return false;
    aatoken = function.aatoken;
    lineNumbers = function.lineNumbers;
    lineIndices = function.lineIndices;
    script = function.script;
    pc = 0;
    if (function.names != null) {
      contextVariables = new Hashtable();
      function.setVariables(contextVariables, params);
    }
    functionName = name;
    return true;
  }

  protected ScriptVariable getFunctionReturn(String name, Vector params,
                                   ScriptVariable tokenAtom)
      throws ScriptException {
    pushContext(null);
    contextPath += " >> function " + name;
    loadFunction(name, params);
    if (tokenAtom != null)
      contextVariables.put("_x", tokenAtom);
    instructionDispatchLoop(false);
    ScriptVariable v = getContextVariableAsVariable("_retval");
    popContext();
    return v;
  }

  private void clearDefinedVariableAtomSets() {
    definedAtomSets.remove("# variable");
  }

  
  private void defineSets() {
    if (!definedAtomSets.containsKey("# static")) {
      for (int i = 0; i < JmolConstants.predefinedStatic.length; i++)
        defineAtomSet(JmolConstants.predefinedStatic[i]);
      defineAtomSet("# static");
    }
    if (definedAtomSets.containsKey("# variable"))
      return;
    for (int i = 0; i < JmolConstants.predefinedVariable.length; i++)
      defineAtomSet(JmolConstants.predefinedVariable[i]);
    
    

    int firstIsotope = JmolConstants.firstIsotope;
    
    for (int i = JmolConstants.elementNumberMax; --i >= 0;) {
      String definition = "@" + JmolConstants.elementNameFromNumber(i) + " _e="
          + i;
      defineAtomSet(definition);
    }
    
    for (int i = JmolConstants.elementNumberMax; --i >= 0;) {
      String definition = "@_" + JmolConstants.elementSymbolFromNumber(i) + " "
          + JmolConstants.elementNameFromNumber(i);
      defineAtomSet(definition);
    }
    
    for (int i = firstIsotope; --i >= 0;) {
      String definition = "@" + JmolConstants.altElementNameFromIndex(i)
          + " _e=" + JmolConstants.altElementNumberFromIndex(i);
      defineAtomSet(definition);
    }
    
    
    
    
    for (int i = JmolConstants.altElementMax; --i >= firstIsotope;) {
      String def = " element=" + JmolConstants.altElementNumberFromIndex(i);
      String definition = "@_" + JmolConstants.altElementSymbolFromIndex(i);
      defineAtomSet(definition + def);
      definition = "@_" + JmolConstants.altIsotopeSymbolFromIndex(i);
      defineAtomSet(definition + def);
      definition = "@" + JmolConstants.altElementNameFromIndex(i);
      if (definition.length() > 1)
        defineAtomSet(definition + def);
    }
    defineAtomSet("# variable");
  }

  private void defineAtomSet(String script) {
    if (script.indexOf("#") == 0) {
      definedAtomSets.put(script, Boolean.TRUE);
      return;
    }
    ScriptContext sc = compiler.compile("#predefine", script, true, false, false, false);
    if (sc.errorType != null) {
        viewer
           .scriptStatus("JmolConstants.java ERROR: predefined set compile error:"
              + script
              + "\ncompile error:"
              + sc.errorMessageUntranslated);
      return;
    }

    if (sc.aatoken.length != 1) {
      viewer
          .scriptStatus("JmolConstants.java ERROR: predefinition does not have exactly 1 command:"
              + script);
      return;
    }
    Token[] statement = sc.aatoken[0];
    if (statement.length <= 2) {
      viewer.scriptStatus("JmolConstants.java ERROR: bad predefinition length:"
          + script);
      return;
    }
    int tok = statement[iToken = 1].tok;
    if (!Token.tokAttr(tok, Token.identifier) 
        && !Token.tokAttr(tok, Token.predefinedset)) {
      viewer.scriptStatus("JmolConstants.java ERROR: invalid variable name:"
          + script);
      return;
    }
    definedAtomSets.put(statement[1].value, statement);
  }

  private BitSet lookupIdentifierValue(String identifier)
      throws ScriptException {
    
    

    

    BitSet bs = lookupValue(identifier, false);
    if (bs != null)
      return BitSetUtil.copy(bs);

    
    bs = getAtomBits(Token.identifier, identifier);
    return (bs == null ? new BitSet() : bs);
  }

  private BitSet lookupValue(String setName, boolean plurals)
      throws ScriptException {
    if (isSyntaxCheck) {
      return new BitSet();
    }
    defineSets();
    Object value = definedAtomSets.get(setName);
    boolean isDynamic = false;
    if (value == null) {
      value = definedAtomSets.get("!" + setName);
      isDynamic = (value != null);
    }
    if (value instanceof BitSet)
      return (BitSet) value;
    if (value instanceof Token[]) {
      pushContext(null);
      BitSet bs = expression((Token[]) value, -2, 0, true, false, true, true);
      popContext();
      if (!isDynamic)
        definedAtomSets.put(setName, bs);
      return bs;
    }
    if (plurals)
      return null;
    int len = setName.length();
    if (len < 5) 
      return null;
    if (setName.charAt(len - 1) != 's')
      return null;
    if (setName.endsWith("ies"))
      setName = setName.substring(0, len - 3) + 'y';
    else
      setName = setName.substring(0, len - 1);
    return lookupValue(setName, true);
  }

  public void deleteAtomsInVariables(BitSet bsDeleted) {
    Enumeration e = definedAtomSets.keys();
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      Object value = definedAtomSets.get(key);
      if (value instanceof BitSet)
        BitSetUtil.deleteBits((BitSet) value, bsDeleted);
    }
  }

  
  private boolean setStatement(int pc) throws ScriptException {
    statement = aatoken[pc];
    statementLength = statement.length;
    if (statementLength == 0)
      return true;
    Token[] fixed;
    int i;
    int tok;
    for (i = 1; i < statementLength; i++)
      if (statement[i].tok == Token.define)
        break;
    if (i == statementLength)
      return i == statementLength;
    fixed = new Token[statementLength];
    fixed[0] = statement[0];
    boolean isExpression = false;
    int j = 1;
    for (i = 1; i < statementLength; i++) {
      switch (tok = getToken(i).tok) {
      case Token.define:
        Object v;
        
        String s;
        String var = parameterAsString(++i);
        boolean isClauseDefine = (tokAt(i) == Token.expressionBegin);
        if (isClauseDefine) {
          Vector val = (Vector) parameterExpression(++i, 0, "_var", true);
          if (val == null || val.size() == 0)
            error(ERROR_invalidArgument);
          i = iToken;
          ScriptVariable vt = (ScriptVariable) val.elementAt(0);
          v = (vt.tok == Token.list ? vt : ScriptVariable.oValue(vt));
        } else {
          v = getParameter(var, false);
        }
        tok = tokAt(0);
        boolean forceString = (Token.tokAttr(tok, Token.implicitStringCommand) 
            || tok == Token.load || tok == Token.script); 
        if (v instanceof ScriptVariable) {
          fixed[j] = (Token) v;
          if (isExpression && fixed[j].tok == Token.list)
            fixed[j] = new ScriptVariable(Token.bitset, getAtomBitSet(this,
                ScriptVariable.sValue((ScriptVariable) fixed[j])));
        } else if (v instanceof Boolean) {
          fixed[j] = (((Boolean) v).booleanValue() ? Token.tokenOn
              : Token.tokenOff);
        } else if (v instanceof Integer) {
          
          
          
          
          fixed[j] = new Token(Token.integer, ((Integer) v).intValue(), v);

        } else if (v instanceof Float) {
          fixed[j] = new Token(Token.decimal, JmolConstants.modelValue("" + v),
              v);
        } else if (v instanceof String) {
          v = getStringObjectAsVariable((String) v, null);
          if (v instanceof ScriptVariable) {
            fixed[j] = (Token) v;
          } else {
            s = (String) v;
            if (isExpression) {
              fixed[j] = new Token(Token.bitset, getAtomBitSet(this, s));
            } else {
              
              
              
              
              
              
              
              
              
              
              
              
              
              
              
              tok = (isClauseDefine || forceString 
                  || s.indexOf(".") >= 0
                  || s.indexOf(" ") >= 0 || s.indexOf("=") >= 0
                  || s.indexOf(";") >= 0 || s.indexOf("[") >= 0
                  || s.indexOf("{") >= 0 ? Token.string : Token.identifier);
              fixed[j] = new Token(tok, v);
            }
          }
        } else if (v instanceof BitSet) {
          fixed[j] = new Token(Token.bitset, v);
        } else if (v instanceof Point3f) {
          fixed[j] = new Token(Token.point3f, v);
        } else if (v instanceof Point4f) {
          fixed[j] = new Token(Token.point4f, v);
        } else if (v instanceof Matrix3f) {
          fixed[j] = new Token(Token.matrix3f, v);
        } else if (v instanceof Matrix4f) {
          fixed[j] = new Token(Token.matrix4f, v);
        } else if (v instanceof String[]) {
          fixed[j] = new Token(Token.string, Escape.escape((String[])v, true));
        } else {
          Point3f center = getObjectCenter(var, Integer.MIN_VALUE);
          if (center == null) 
            error(ERROR_invalidArgument);
          fixed[j] = new Token(Token.point3f, center);
        }
        if (j == 1 && statement[0].tok == Token.set
            && fixed[j].tok != Token.identifier)
          error(ERROR_invalidArgument);
        break;
      case Token.expressionBegin:
      case Token.expressionEnd:
        
        isExpression = (tok == Token.expressionBegin);
        fixed[j] = statement[i];
        break;
      default:
        fixed[j] = statement[i];
      }

      j++;
    }
    statement = fixed;
    for (i = j; i < statement.length; i++)
      statement[i] = null;
    statementLength = j;
    return true;
  }

  

  private void clearState(boolean tQuiet) {
    for (int i = scriptLevelMax; --i >= 0;)
      stack[i] = null;
    scriptLevel = 0;
    setErrorMessage(null);
    contextPath = "";
    this.tQuiet = tQuiet;
  }

  private void pushContext(ScriptFunction function) throws ScriptException {
    if (scriptLevel == scriptLevelMax)
      error(ERROR_tooManyScriptLevels);
    ScriptContext context = getScriptContext();
    stack[scriptLevel++] = context;
    if (isCmdLine_c_or_C_Option)
      Logger.info("-->>-------------".substring(0, scriptLevel + 5) + filename);
  }

  public ScriptContext getScriptContext() {
    ScriptContext context = new ScriptContext();
    context.contextPath = contextPath;
    context.filename = filename;
    context.functionName = functionName;
    context.script = script;
    context.lineNumbers = lineNumbers;
    context.lineIndices = lineIndices;
    context.aatoken = aatoken;    
    context.statement = statement;
    context.statementLength = statementLength;
    context.pc = pc;
    context.lineEnd = lineEnd;
    context.pcEnd = pcEnd;
    context.iToken = iToken;
    context.outputBuffer = outputBuffer;
    context.contextVariables = contextVariables;
    context.isStateScript = isStateScript;
    
    context.errorMessage = errorMessage;
    context.errorType = errorType;
    context.iCommandError = iCommandError;

    context.stack = stack;
    context.scriptLevel = scriptLevel;
    context.isSyntaxCheck = isSyntaxCheck;
    context.executionStepping = executionStepping;
    context.executionPaused = executionPaused;
    context.scriptExtensions = scriptExtensions;
    return context;
  }

  private void getScriptContext(ScriptContext context, boolean isFull) { 

    
    
    script = context.script;
    lineNumbers = context.lineNumbers;
    lineIndices = context.lineIndices;
    aatoken = context.aatoken;
    contextVariables = context.contextVariables;
    scriptExtensions = context.scriptExtensions;
    if (!isFull) {
      error = (context.errorType != null);
      errorMessage = context.errorMessage;
      errorMessageUntranslated = context.errorMessageUntranslated;
      iCommandError = context.iCommandError;
      errorType = context.errorType;
      return;
    }
    
    contextPath = context.contextPath;
    filename = context.filename;
    functionName = context.functionName;
    statement = context.statement;
    statementLength = context.statementLength;
    pc = context.pc;
    lineEnd = context.lineEnd;
    pcEnd = context.pcEnd;
    iToken = context.iToken;
    outputBuffer = context.outputBuffer;
    isStateScript = context.isStateScript;
  }
  
  private void popContext() {
    if (isCmdLine_c_or_C_Option)
      Logger.info("--<<-------------".substring(0, scriptLevel + 5) + filename);
    if (scriptLevel == 0)
      return;
    ScriptContext context = stack[--scriptLevel];
    stack[scriptLevel] = null;
    getScriptContext(context, true);
  }

  private String getContext(boolean withVariables) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < scriptLevel; i++) {
      if (withVariables) {
        if (stack[i].contextVariables != null) {
          sb.append(getScriptID(stack[i]));
          sb.append(StateManager.getVariableList(stack[i].contextVariables, 80));
        }
      } else {
        sb.append(setErrorLineMessage(stack[i].functionName, stack[i].filename,
            getLinenumber(stack[i]), stack[i].pc,
            statementAsString(stack[i].statement, -9999)));
      }
    }
    if (withVariables) {
      if (contextVariables != null) {
        sb.append(getScriptID(null));
        sb.append(StateManager.getVariableList(contextVariables, 80));
      }
    } else {
      sb.append(setErrorLineMessage(functionName, filename,
      getLinenumber(null), pc, statementAsString(statement, -9999)));
    }

    return sb.toString();
  }

  private int getLinenumber(ScriptContext c) {
    return (c == null ? lineNumbers[pc] : c.lineNumbers[c.pc]);
  }

  private String getScriptID(ScriptContext context) {
    String fuName = (context == null ? functionName : "function " + context.functionName);
    String fiName = (context == null ? filename : context.filename);    
    return "\n# " + fuName + " (file " + fiName + ")\n";
  }

  
  
  private boolean error;
  private String errorMessage;
  protected String errorMessageUntranslated;
  protected String errorType;
  protected int iCommandError;
  


  public String getErrorMessage() {
    return errorMessage;
  }

  public String getErrorMessageUntranslated() {
    return errorMessageUntranslated == null ? errorMessage
        : errorMessageUntranslated;
  }

  private void setErrorMessage(String err) {
    errorMessageUntranslated = null;
    if (err == null) {
      error = false;
      errorType = null;
      errorMessage = null;
      iCommandError = -1;
      return;
    }
    error = true;
    if (errorMessage == null) 
                              
      errorMessage = GT._("script ERROR: ");
    errorMessage += err;
  }

  private boolean ignoreError;

  private void planeExpected() throws ScriptException {
    error(ERROR_planeExpected, "{a b c d}",
        "\"xy\" \"xz\" \"yz\" \"x=...\" \"y=...\" \"z=...\"", "$xxxxx");
  }

  private void integerOutOfRange(int min, int max) throws ScriptException {
    error(ERROR_integerOutOfRange, "" + min, "" + max);
  }

  private void numberOutOfRange(float min, float max) throws ScriptException {
    error(ERROR_numberOutOfRange, "" + min, "" + max);
  }

  void error(int iError) throws ScriptException {
    error(iError, null, null, null, false);
  }

  void error(int iError, String value) throws ScriptException {
    error(iError, value, null, null, false);
  }

  void error(int iError, String value, String more) throws ScriptException {
    error(iError, value, more, null, false);
  }

  void error(int iError, String value, String more, String more2)
      throws ScriptException {
    error(iError, value, more, more2, false);
  }

  private void warning(int iError, String value, String more)
      throws ScriptException {
    error(iError, value, more, null, true);
  }

  void error(int iError, String value, String more, String more2,
             boolean warningOnly) throws ScriptException {
    String strError = ignoreError ? null : errorString(iError, value, more,
        more2, true);
    String strUntranslated = (!ignoreError && GT.getDoTranslate() ? errorString(
        iError, value, more, more2, false)
        : null);
    if (!warningOnly)
      evalError(strError, strUntranslated);
    showString(strError);
  }

  void evalError(String message, String strUntranslated) throws ScriptException {
    if (ignoreError)
      throw new NullPointerException();
    if (!isSyntaxCheck) {
      
      
      viewer.setCursor(Viewer.CURSOR_DEFAULT);
      viewer.setBooleanProperty("refreshing", true);
    }
    throw new ScriptException(message, strUntranslated);
  }

  final static int ERROR_axisExpected = 0;
  final static int ERROR_backgroundModelError = 1;
  final static int ERROR_badArgumentCount = 2;
  final static int ERROR_badMillerIndices = 3;
  final static int ERROR_badRGBColor = 4;
  final static int ERROR_booleanExpected = 5;
  final static int ERROR_booleanOrNumberExpected = 6;
  final static int ERROR_booleanOrWhateverExpected = 7;
  final static int ERROR_colorExpected = 8;
  final static int ERROR_colorOrPaletteRequired = 9;
  final static int ERROR_commandExpected = 10;
  final static int ERROR_coordinateOrNameOrExpressionRequired = 11;
  final static int ERROR_drawObjectNotDefined = 12;
  final static int ERROR_endOfStatementUnexpected = 13;
  final static int ERROR_expressionExpected = 14;
  final static int ERROR_expressionOrIntegerExpected = 15;
  final static int ERROR_filenameExpected = 16;
  final static int ERROR_fileNotFoundException = 17;
  final static int ERROR_incompatibleArguments = 18;
  final static int ERROR_insufficientArguments = 19;
  final static int ERROR_integerExpected = 20;
  final static int ERROR_integerOutOfRange = 21;
  final static int ERROR_invalidArgument = 22;
  final static int ERROR_invalidParameterOrder = 23;
  final static int ERROR_keywordExpected = 24;
  final static int ERROR_moCoefficients = 25;
  final static int ERROR_moIndex = 26;
  final static int ERROR_moModelError = 27;
  final static int ERROR_moOccupancy = 28;
  final static int ERROR_moOnlyOne = 29;
  final static int ERROR_multipleModelsNotOK = 30;
  final static int ERROR_noData = 31;
  final static int ERROR_noPartialCharges = 32;
  final static int ERROR_noUnitCell = 33;
  final static int ERROR_numberExpected = 34;
  final static int ERROR_numberMustBe = 35;
  final static int ERROR_numberOutOfRange = 36;
  final static int ERROR_objectNameExpected = 37;
  final static int ERROR_planeExpected = 38;
  final static int ERROR_propertyNameExpected = 39;
  final static int ERROR_spaceGroupNotFound = 40;
  final static int ERROR_stringExpected = 41;
  final static int ERROR_stringOrIdentifierExpected = 42;
  final static int ERROR_tooManyPoints = 43;
  final static int ERROR_tooManyScriptLevels = 44;
  final static int ERROR_unrecognizedAtomProperty = 45;
  final static int ERROR_unrecognizedBondProperty = 46;
  final static int ERROR_unrecognizedCommand = 47;
  final static int ERROR_unrecognizedExpression = 48;
  final static int ERROR_unrecognizedObject = 49;
  final static int ERROR_unrecognizedParameter = 50;
  final static int ERROR_unrecognizedParameterWarning = 51;
  final static int ERROR_unrecognizedShowParameter = 52;
  final static int ERROR_what = 53;
  final static int ERROR_writeWhat = 54;

  static String errorString(int iError, String value, String more,
                            String more2, boolean translated) {
    boolean doTranslate = false;
    if (!translated && (doTranslate = GT.getDoTranslate()) == true)
      GT.setDoTranslate(false);
    String msg;
    switch (iError) {
    default:
      msg = "Unknown error message number: " + iError;
      break;
    case ERROR_axisExpected:
      msg = GT._("x y z axis expected");
      break;
    case ERROR_backgroundModelError:
      msg = GT._("{0} not allowed with background model displayed");
      break;
    case ERROR_badArgumentCount:
      msg = GT._("bad argument count");
      break;
    case ERROR_badMillerIndices:
      msg = GT._("Miller indices cannot all be zero.");
      break;
    case ERROR_badRGBColor:
      msg = GT._("bad [R,G,B] color");
      break;
    case ERROR_booleanExpected:
      msg = GT._("boolean expected");
      break;
    case ERROR_booleanOrNumberExpected:
      msg = GT._("boolean or number expected");
      break;
    case ERROR_booleanOrWhateverExpected:
      msg = GT._("boolean, number, or {0} expected");
      break;
    case ERROR_colorExpected:
      msg = GT._("color expected");
      break;
    case ERROR_colorOrPaletteRequired:
      msg = GT._("a color or palette name (Jmol, Rasmol) is required");
      break;
    case ERROR_commandExpected:
      msg = GT._("command expected");
      break;
    case ERROR_coordinateOrNameOrExpressionRequired:
      msg = GT._("{x y z} or $name or (atom expression) required");
      break;
    case ERROR_drawObjectNotDefined:
      msg = GT._("draw object not defined");
      break;
    case ERROR_endOfStatementUnexpected:
      msg = GT._("unexpected end of script command");
      break;
    case ERROR_expressionExpected:
      msg = GT._("valid (atom expression) expected");
      break;
    case ERROR_expressionOrIntegerExpected:
      msg = GT._("(atom expression) or integer expected");
      break;
    case ERROR_filenameExpected:
      msg = GT._("filename expected");
      break;
    case ERROR_fileNotFoundException:
      msg = GT._("file not found");
      break;
    case ERROR_incompatibleArguments:
      msg = GT._("incompatible arguments");
      break;
    case ERROR_insufficientArguments:
      msg = GT._("insufficient arguments");
      break;
    case ERROR_integerExpected:
      msg = GT._("integer expected");
      break;
    case ERROR_integerOutOfRange:
      msg = GT._("integer out of range ({0} - {1})");
      break;
    case ERROR_invalidArgument:
      msg = GT._("invalid argument");
      break;
    case ERROR_invalidParameterOrder:
      msg = GT._("invalid parameter order");
      break;
    case ERROR_keywordExpected:
      msg = GT._("keyword expected");
      break;
    case ERROR_moCoefficients:
      msg = GT._("no MO coefficient data available");
      break;
    case ERROR_moIndex:
      msg = GT._("An MO index from 1 to {0} is required");
      break;
    case ERROR_moModelError:
      msg = GT._("no MO basis/coefficient data available for this frame");
      break;
    case ERROR_moOccupancy:
      msg = GT._("no MO occupancy data available");
      break;
    case ERROR_moOnlyOne:
      msg = GT._("Only one molecular orbital is available in this file");
      break;
    case ERROR_multipleModelsNotOK:
      msg = GT._("{0} require that only one model be displayed");
      break;
    case ERROR_noData:
      msg = GT._("No data available");
      break;
    case ERROR_noPartialCharges:
      msg = GT
          ._("No partial charges were read from the file; Jmol needs these to render the MEP data.");
      break;
    case ERROR_noUnitCell:
      msg = GT._("No unit cell");
      break;
    case ERROR_numberExpected:
      msg = GT._("number expected");
      break;
    case ERROR_numberMustBe:
      msg = GT._("number must be ({0} or {1})");
      break;
    case ERROR_numberOutOfRange:
      msg = GT._("decimal number out of range ({0} - {1})");
      break;
    case ERROR_objectNameExpected:
      msg = GT._("object name expected after '$'");
      break;
    case ERROR_planeExpected:
      msg = GT
          ._("plane expected -- either three points or atom expressions or {0} or {1} or {2}");
      break;
    case ERROR_propertyNameExpected:
      msg = GT._("property name expected");
      break;
    case ERROR_spaceGroupNotFound:
      msg = GT._("space group {0} was not found.");
      break;
    case ERROR_stringExpected:
      msg = GT._("quoted string expected");
      break;
    case ERROR_stringOrIdentifierExpected:
      msg = GT._("quoted string or identifier expected");
      break;
    case ERROR_tooManyPoints:
      msg = GT._("too many rotation points were specified");
      break;
    case ERROR_tooManyScriptLevels:
      msg = GT._("too many script levels");
      break;
    case ERROR_unrecognizedAtomProperty:
      msg = GT._("unrecognized atom property");
      break;
    case ERROR_unrecognizedBondProperty:
      msg = GT._("unrecognized bond property");
      break;
    case ERROR_unrecognizedCommand:
      msg = GT._("unrecognized command");
      break;
    case ERROR_unrecognizedExpression:
      msg = GT._("runtime unrecognized expression");
      break;
    case ERROR_unrecognizedObject:
      msg = GT._("unrecognized object");
      break;
    case ERROR_unrecognizedParameter:
      msg = GT._("unrecognized {0} parameter");
      break;
    case ERROR_unrecognizedParameterWarning:
      msg = GT
          ._("unrecognized {0} parameter in Jmol state script (set anyway)");
      break;
    case ERROR_unrecognizedShowParameter:
      msg = GT._("unrecognized SHOW parameter --  use {0}");
      break;
    case ERROR_what:
      msg = "{0}";
      break;
    case ERROR_writeWhat:
      msg = GT._("write what? {0} or {1} \"filename\"");
      break;
    }
    if (msg.indexOf("{0}") < 0) {
      if (value != null)
        msg += ": " + value;
    } else {
      msg = TextFormat.simpleReplace(msg, "{0}", value);
      if (msg.indexOf("{1}") >= 0)
        msg = TextFormat.simpleReplace(msg, "{1}", more);
      else if (more != null)
        msg += ": " + more;
      if (msg.indexOf("{2}") >= 0)
        msg = TextFormat.simpleReplace(msg, "{2}", more);
    }
    if (doTranslate)
      GT.setDoTranslate(true);
    return msg;
  }

  String contextTrace() {
    StringBuffer sb = new StringBuffer();
    for (;;) {
      sb.append(setErrorLineMessage(functionName, filename, getLinenumber(null),
          pc, statementAsString(statement, iToken)));
      if (scriptLevel > 0)
        popContext();
      else
        break;
    }
    return sb.toString();
  }

  static String setErrorLineMessage(String functionName, String filename,
                                    int lineCurrent, int pcCurrent,
                                    String lineInfo) {
    String err = "\n----";
    if (filename != null || functionName != null)
      err += "line " + lineCurrent + " command " + (pcCurrent + 1) + " of "
          + (functionName == null ? filename : "function " + functionName ) + ":";
    err += "\n         " + lineInfo;
    return err;
  }

  class ScriptException extends Exception {

    private String message;
    private String untranslated;

    ScriptException(String msg, String untranslated) {
      errorType = message = msg;
      iCommandError = pc;
      this.untranslated = (untranslated == null ? msg : untranslated);
      if (message == null) {
        message = "";
        return;
      }
      
      String s = contextTrace();
      message += s;
      this.untranslated += s;
      if (isSyntaxCheck
          || msg.indexOf("file recognized as a script file:") >= 0)
        return;
      Logger.error("eval ERROR: " + toString());
      if (viewer.autoExit)
        viewer.exitJmol();      
    }

    protected String getErrorMessageUntranslated() {
      return untranslated;
    }

    public String toString() {
      return message;
    }
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    str.append("Eval\n pc:");
    str.append(pc);
    str.append("\n");
    str.append(aatoken.length);
    str.append(" statements\n");
    for (int i = 0; i < aatoken.length; ++i) {
      str.append("----\n");
      Token[] atoken = aatoken[i];
      for (int j = 0; j < atoken.length; ++j) {
        str.append(atoken[j]);
        str.append('\n');
      }
      str.append('\n');
    }
    str.append("END\n");
    return str.toString();
  }

  private String statementAsString(Token[] statement, int iTok) {
    if (statement.length == 0)
      return "";
    StringBuffer sb = new StringBuffer();
    int tok = statement[0].tok;
    switch (tok) {
    case Token.nada:
      String s = (String) statement[0].value;
      return (s.startsWith("/") ? "/" : "#") + s;
    case Token.end:
      if (statement.length == 2 && statement[1].tok == Token.function)
        return ((ScriptFunction) (statement[1].value)).toString();
    }
    boolean useBraces = true;
    
    boolean inBrace = false;
    boolean inClauseDefine = false;
    boolean setEquals = (tok == Token.set
        && ((String) statement[0].value) == "" && statement[0].intValue == '=' && tokAt(1) != Token.expressionBegin);
    int len = statement.length;
    for (int i = 0; i < len; ++i) {
      Token token = statement[i];
      if (token == null) {
        len = i;
        break;
      }
      if (iTok == i - 1)
        sb.append(" <<");
      if (i != 0)
        sb.append(' ');
      if (i == 2 && setEquals) {
        setEquals = false;
        if (token.tok != Token.opEQ)
          sb.append("= ");
      }
      if (iTok == i && token.tok != Token.expressionEnd)
        sb.append(">> ");
      switch (token.tok) {
      case Token.expressionBegin:
        if (useBraces)
          sb.append("{");
        continue;
      case Token.expressionEnd:
        if (inClauseDefine && i == statementLength - 1)
          useBraces = false;
        if (useBraces)
          sb.append("}");
        continue;
      case Token.leftsquare:
      case Token.rightsquare:
        break;
      case Token.leftbrace:
      case Token.rightbrace:
        inBrace = (token.tok == Token.leftbrace);
        break;
      case Token.define:
        if (i > 0 && ((String) token.value).equals("define")) {
          sb.append("@");
          if (tokAt(i + 1) == Token.expressionBegin) {
            if (!useBraces)
              inClauseDefine = true;
            useBraces = true;
          }
          continue;
        }
        break;
      case Token.on:
        sb.append("true");
        continue;
      case Token.off:
        sb.append("false");
        continue;
      case Token.select:
        break;
      case Token.integer:
        sb.append(token.intValue);
        continue;
      case Token.point3f:
      case Token.point4f:
      case Token.bitset:
        sb.append(ScriptVariable.sValue(token));
        continue;
      case Token.seqcode:
        sb.append('^');
        continue;
      case Token.spec_seqcode_range:
        if (token.intValue != Integer.MAX_VALUE)
          sb.append(token.intValue);
        else
          sb.append(Group.getSeqcodeString(getSeqCode(token)));
        token = statement[++i];
        sb.append(' ');
        
        sb.append(inBrace ? "-" : "- ");
        
      case Token.spec_seqcode:
        if (token.intValue != Integer.MAX_VALUE)
          sb.append(token.intValue);
        else
          sb.append(Group.getSeqcodeString(getSeqCode(token)));
        continue;
      case Token.spec_chain:
        sb.append("*:");
        sb.append((char) token.intValue);
        continue;
      case Token.spec_alternate:
        sb.append("*%");
        if (token.value != null)
          sb.append(token.value.toString());
        continue;
      case Token.spec_model:
        sb.append("*/");
        
      case Token.spec_model2:
      case Token.decimal:
        if (token.intValue < Integer.MAX_VALUE) {
          sb.append(Escape.escapeModelFileNumber(token.intValue));
        } else {
          sb.append("" + token.value);
        }
        continue;
      case Token.spec_resid:
        sb.append('[');
        sb.append(Group.getGroup3((short) token.intValue));
        sb.append(']');
        continue;
      case Token.spec_name_pattern:
        sb.append('[');
        sb.append(token.value);
        sb.append(']');
        continue;
      case Token.spec_atom:
        sb.append("*.");
        break;
      case Token.cell:
        if (token.value instanceof Point3f) {
          Point3f pt = (Point3f) token.value;
          sb.append("cell={").append(pt.x).append(" ").append(pt.y).append(" ")
              .append(pt.z).append("}");
          continue;
        }
        break;
      case Token.string:
        sb.append("\"").append(token.value).append("\"");
        continue;
      case Token.opEQ:
      case Token.opLE:
      case Token.opGE:
      case Token.opGT:
      case Token.opLT:
      case Token.opNE:
        
        if (token.intValue == Token.property) {
          sb.append((String) statement[++i].value).append(" ");
        } else if (token.intValue != Integer.MAX_VALUE)
          sb.append(Token.nameOf(token.intValue)).append(" ");
        break;
      default:
        if (Token.tokAttr(token.tok, Token.identifier) || !logMessages)
          break;
        sb.append('\n').append(token.toString()).append('\n');
        continue;
      }
      if (token.value != null)
        
        sb.append(token.value.toString());
    }
    if (iTok >= len - 1)
      sb.append(" <<");
    return sb.toString();
  }

  
  
  
  private void setShapeProperty(int shapeType, String propertyName,
                                Object propertyValue) {
    if (!isSyntaxCheck)
      viewer.setShapeProperty(shapeType, propertyName, propertyValue);
  }

  private void setShapeSize(int shapeType, int size) {
    
    if (!isSyntaxCheck)
      viewer.setShapeSize(shapeType, size, null);
  }

  private void setShapeSize(int shapeType, RadiusData rd) {
    if (!isSyntaxCheck)
      viewer.setShapeSize(shapeType, rd, null);
  }

  private void setBooleanProperty(String key, boolean value) {
    if (!isSyntaxCheck)
      viewer.setBooleanProperty(key, value);
  }

  private boolean setIntProperty(String key, int value) {
    if (!isSyntaxCheck)
      viewer.setIntProperty(key, value);
    return true;
  }

  private boolean setFloatProperty(String key, float value) {
    if (!isSyntaxCheck)
      viewer.setFloatProperty(key, value);
    return true;
  }

  private void setStringProperty(String key, String value) {
    if (!isSyntaxCheck)
      viewer.setStringProperty(key, value);
  }

  private void showString(String str) {
    if (isSyntaxCheck)
      return;
    if (outputBuffer != null)
      outputBuffer.append(str).append('\n');
    else
      viewer.showString(str, false);
  }

  private void scriptStatusOrBuffer(String s) {
    if (outputBuffer != null) {
      outputBuffer.append(s).append('\n');
      return;
    }
    viewer.scriptStatus(s);
  }

  

  private Token[] tempStatement;
  private boolean isBondSet;
  private Object expressionResult;

  private BitSet expression(int index) throws ScriptException {
    if (!checkToken(index))
      error(ERROR_badArgumentCount);
    return expression(statement, index, 0, true, false, true, true);
  }

  private BitSet expression(Token[] code, int pcStart, int pcStop,
                            boolean allowRefresh, boolean allowUnderflow,
                            boolean mustBeBitSet, boolean andNotDeleted)
      throws ScriptException {
    
    
    
    
    

    isBondSet = false;
    if (code != statement) {
      tempStatement = statement;
      statement = code;
    }
    ScriptMathProcessor rpn = new ScriptMathProcessor(this, false, false);
    Object val;
    int comparisonValue = Integer.MAX_VALUE;
    boolean refreshed = false;
    iToken = 1000;
    boolean ignoreSubset = (pcStart < 0);
    boolean isInMath = false;
    int nExpress = 0;
    int atomCount = viewer.getAtomCount();
    if (ignoreSubset)
      pcStart = -pcStart;
    ignoreSubset |= isSyntaxCheck;
    if (pcStop == 0 && code.length > pcStart)
      pcStop = pcStart + 1;
    
    
    expression_loop: for (int pc = pcStart; pc < pcStop; ++pc) {
      iToken = pc;
      Token instruction = code[pc];
      if (instruction == null)
        break;
      Object value = instruction.value;
      
      
      switch (instruction.tok) {
      case Token.expressionBegin:
        pcStart = pc;
        pcStop = code.length;
        nExpress++;
        break;
      case Token.expressionEnd:
        nExpress--;
        if (nExpress > 0)
          continue;
        break expression_loop;
      case Token.leftbrace:
        if (isPoint3f(pc)) {
          Point3f pt = getPoint3f(pc, true);
          if (pt != null) {
            rpn.addX(pt);
            pc = iToken;
            break;
          }
        }
        break; 
      case Token.rightbrace:
        break;
      case Token.leftsquare:
        isInMath = true;
        rpn.addOp(instruction);
        break;
      case Token.rightsquare:
        isInMath = false;
        rpn.addOp(instruction);
        break;
      case Token.define:
        rpn.addX(getAtomBitSet(this, (String) value));
        break;
      case Token.hkl:
        rpn.addX(new ScriptVariable(instruction));
        rpn.addX(new ScriptVariable(Token.point4f, hklParameter(pc + 2)));
        pc = iToken;
        break;
      case Token.plane:
        rpn.addX(new ScriptVariable(instruction));
        rpn.addX(new ScriptVariable(Token.point4f, planeParameter(pc + 2)));
        pc = iToken;
        break;
      case Token.coord:
        rpn.addX(new ScriptVariable(instruction));
        rpn.addX(getPoint3f(pc + 2, true));
        pc = iToken;
        break;
      case Token.string:
        rpn.addX(new ScriptVariable(instruction));
        
        if (((String) value).equals("hkl")) {
          rpn.addX(new ScriptVariable(Token.point4f, hklParameter(pc + 2)));
          pc = iToken;
        }
        break;
      case Token.within:
      case Token.substructure:
      case Token.connected:
      case Token.comma:
        rpn.addOp(instruction);
        break;
      case Token.all:
        rpn.addX(viewer.getModelAtomBitSet(-1, true));
        break;
      case Token.none:
        rpn.addX(new BitSet());
        break;
      case Token.on:
      case Token.off:
        rpn.addX(new ScriptVariable(instruction));
        break;
      case Token.selected:
        rpn.addX(BitSetUtil.copy(viewer.getSelectionSet()));
        break;
      case Token.subset:
        BitSet bsSubset = viewer.getSelectionSubset();
        rpn.addX(bsSubset == null ? viewer.getModelAtomBitSet(-1, true)
            : BitSetUtil.copy(bsSubset));
        break;
      case Token.hidden:
        rpn.addX(BitSetUtil.copy(viewer.getHiddenSet()));
        break;
      case Token.displayed:
        rpn.addX(BitSetUtil.copyInvert(viewer.getHiddenSet(), atomCount));
        break;
      case Token.visible:
        if (!isSyntaxCheck && !refreshed)
          viewer.setModelVisibility();
        refreshed = true;
        rpn.addX(viewer.getVisibleSet());
        break;
      case Token.clickable:
        
        if (!isSyntaxCheck && allowRefresh)
          refresh();
        rpn.addX(viewer.getClickableSet());
        break;
      case Token.carbohydrate:
      case Token.dna:
      case Token.hetero:
      case Token.isaromatic:
      case Token.nucleic:
      case Token.protein:
      case Token.purine:
      case Token.pyrimidine:
      case Token.rna:
      case Token.spec_atom:
      case Token.spec_name_pattern:
      case Token.spec_alternate:
      case Token.specialposition:
      case Token.symmetry:
      case Token.unitcell:
        rpn.addX(getAtomBits(instruction.tok, (String) value));
        break;
      case Token.spec_model:
        
        
      case Token.spec_model2:
        
        int iModel = instruction.intValue;
        if (iModel == Integer.MAX_VALUE && value instanceof Integer) {
          
          iModel = ((Integer) value).intValue();
          if (!viewer.haveFileSet()) {
            rpn.addX(getAtomBits(Token.spec_model, new Integer(iModel)));
            break;
          }
          if (iModel < 1000)
            iModel = iModel * 1000000;
          else
            iModel = (iModel / 1000) * 1000000 + iModel % 1000;
        }
        rpn.addX(bitSetForModelFileNumber(iModel));
        break;
      case Token.spec_resid:
      case Token.spec_chain:
        rpn
            .addX(getAtomBits(instruction.tok,
                new Integer(instruction.intValue)));
        break;
      case Token.spec_seqcode:
        if (isInMath)
          rpn.addXNum(ScriptVariable.intVariable(instruction.intValue));
        else
          rpn.addX(getAtomBits(Token.spec_seqcode, new Integer(
              getSeqCode(instruction))));
        break;
      case Token.spec_seqcode_range:
        if (isInMath) {
          rpn.addXNum(ScriptVariable.intVariable(instruction.intValue));
          rpn.addX(Token.tokenMinus);
          rpn.addXNum(ScriptVariable.intVariable(code[++pc].intValue));
          break;
        }
        int chainID = (pc + 3 < code.length && code[pc + 2].tok == Token.opAnd
            && code[pc + 3].tok == Token.spec_chain ? code[pc + 3].intValue
            : '\t');
        rpn.addX(getAtomBits(Token.spec_seqcode_range, new int[] {
            getSeqCode(instruction), getSeqCode(code[++pc]), chainID }));
        if (chainID != '\t')
          pc += 2;
        break;
      case Token.cell:
        Point3f pt = (Point3f) value;
        rpn.addX(getAtomBits(Token.cell, new int[] { (int) (pt.x * 1000),
            (int) (pt.y * 1000), (int) (pt.z * 1000) }));
        break;
      case Token.thismodel:
        rpn
            .addX(viewer
                .getModelAtomBitSet(viewer.getCurrentModelIndex(), true));
        break;
      case Token.hydrogen:
      case Token.amino:
      case Token.backbone:
      case Token.solvent:
      case Token.helix:
      case Token.sidechain:
      case Token.surface:
        rpn.addX(lookupIdentifierValue((String) value));
        break;
      case Token.opLT:
      case Token.opLE:
      case Token.opGE:
      case Token.opGT:
      case Token.opEQ:
      case Token.opNE:
        val = code[++pc].value;
        int tokOperator = instruction.tok;
        int tokWhat = instruction.intValue;
        String property = (tokWhat == Token.property ? (String) val : null);
        if (property != null)
          val = code[++pc].value;
        if (tokWhat == Token.configuration && tokOperator != Token.opEQ)
          error(ERROR_invalidArgument);
        if (isSyntaxCheck) {
          rpn.addX(new BitSet());
          break;
        }
        boolean isModel = (tokWhat == Token.model);
        boolean isIntProperty = Token.tokAttr(tokWhat, Token.intproperty);
        boolean isFloatProperty = Token.tokAttr(tokWhat, Token.floatproperty);
        boolean isIntOrFloat = isIntProperty && isFloatProperty;
        boolean isStringProperty = !isIntProperty
            && Token.tokAttr(tokWhat, Token.strproperty);
        if (tokWhat == Token.element)
          isIntProperty = !(isStringProperty = false);
        int tokValue = code[pc].tok;
        comparisonValue = code[pc].intValue;
        float comparisonFloat = Float.NaN;
        if (val instanceof Point3f) {
          if (tokWhat == Token.color) {
            comparisonValue = Graphics3D.colorPtToInt((Point3f) val);
            tokValue = Token.integer;
            isIntProperty = true;
          }
        } else if (val instanceof String) {
          if (tokWhat == Token.color) {
            comparisonValue = Graphics3D.getArgbFromString((String) val);
            if (comparisonValue == 0 && Token.tokAttr(tokValue, Token.identifier)) {
              val = getStringParameter((String) val, true);
              if (((String)val).startsWith("{")) {
                val = Escape.unescapePoint((String) val);
                if (val instanceof Point3f)
                  comparisonValue = Graphics3D.colorPtToInt((Point3f) val);
                else
                  comparisonValue = 0;
              } else {
                comparisonValue = Graphics3D.getArgbFromString((String) val);
              }
            }
            tokValue = Token.integer;
            isIntProperty = true;
          } else if (isStringProperty) {
            if (Token.tokAttr(tokValue, Token.identifier))
              val = getStringParameter((String) val, true);
          } else {
            if (Token.tokAttr(tokValue, Token.identifier))
              val = getNumericParameter((String) val);
            if (val instanceof String) {
              if (tokWhat == Token.structure || tokWhat == Token.element)
                isStringProperty = !(isIntProperty = (comparisonValue != Integer.MAX_VALUE));
              else
                val = ScriptVariable.nValue(code[pc]);
            }
            if (val instanceof Integer)
              comparisonFloat = comparisonValue = ((Integer) val).intValue();
            else if (val instanceof Float && isModel)
              comparisonValue = ModelCollection
                  .modelFileNumberFromFloat(((Float) val).floatValue());
          }
        }
        if (isStringProperty && !(val instanceof String)) {
          val = "" + val;
        }
        if (val instanceof Integer || tokValue == Token.integer) {
          if (isModel) {
            if (comparisonValue >= 1000000)
              tokWhat = -Token.model;
          } else if (isIntOrFloat) {
            isFloatProperty = false;
          } else if (isFloatProperty) {
            comparisonFloat = comparisonValue;
          }
        } else if (val instanceof Float) {
          if (isModel) {
            tokWhat = -Token.model;
          } else {
            comparisonFloat = ((Float) val).floatValue();
            if (isIntOrFloat) {
              isIntProperty = false;
            } else if (isIntProperty) {
              comparisonValue = (int) comparisonFloat;
            }
          }
        } else if (!isStringProperty) {
          iToken++;
          error(ERROR_invalidArgument);
        }
        if (isModel && comparisonValue >= 1000000
            && comparisonValue % 1000000 == 0) {
          comparisonValue /= 1000000;
          tokWhat = Token.file;
          isModel = false;
        }
        if (tokWhat == -Token.model && tokOperator == Token.opEQ) {
          rpn.addX(bitSetForModelFileNumber(comparisonValue));
          break;
        }
        if (value != null && ((String) value).indexOf("-") >= 0) {
          if (isIntProperty)
            comparisonValue = -comparisonValue;
          else if (!Float.isNaN(comparisonFloat))
            comparisonFloat = -comparisonFloat;
        }
        float[] data = (tokWhat == Token.property ? viewer
            .getDataFloat(property) : null);
        rpn.addX(isIntProperty ? compareInt(tokWhat, data, tokOperator,
            comparisonValue) : isStringProperty ? compareString(tokWhat,
            tokOperator, (String) val) : compareFloat(tokWhat, data,
            tokOperator, comparisonFloat));
        break;
      case Token.bitset:
      case Token.point3f:
      case Token.point4f:
        rpn.addX(value);
        break;
      case Token.decimal:
      case Token.integer:
        rpn.addXNum(new ScriptVariable(instruction));
        break;
      default:
        if (Token.tokAttr(instruction.tok, Token.mathop)) {
          rpn.addOp(instruction);
          break;
        } 
        if (instruction.tok == Token.identifier) {
          val = getParameter((String) value, false);
          if (val instanceof String)
            val = getStringObjectAsVariable((String) val, null);
          if (val instanceof String || val instanceof String[])
            val = lookupIdentifierValue((String) value);
          rpn.addX(val);
          break;
        }
        error(ERROR_unrecognizedExpression);
      }
    }
    expressionResult = rpn.getResult(allowUnderflow, null);
    if (expressionResult == null) {
      if (allowUnderflow)
        return null;
      if (!isSyntaxCheck)
        rpn.dumpStacks("after getResult");
      error(ERROR_endOfStatementUnexpected);
    }
    expressionResult = ((ScriptVariable) expressionResult).value;
    if (expressionResult instanceof String
        && (mustBeBitSet || ((String) expressionResult).startsWith("({"))) {
      
      expressionResult = (isSyntaxCheck ? new BitSet() : getAtomBitSet(this,
          (String) expressionResult));
    }
    if (!mustBeBitSet && !(expressionResult instanceof BitSet))
      return null; 
    BitSet bs = (expressionResult instanceof BitSet ? (BitSet) expressionResult
        : new BitSet());
    isBondSet = (expressionResult instanceof BondSet);
    BitSet bsDeleted = viewer.getDeletedAtoms();
    if (!isBondSet && bsDeleted != null)
      bs.andNot(bsDeleted);
    BitSet bsSubset = viewer.getSelectionSubset();
    if (!ignoreSubset && bsSubset != null && !isBondSet)
      bs.and(bsSubset);
    if (tempStatement != null) {
      statement = tempStatement;
      tempStatement = null;
    }
    return bs;
  }

  private BitSet compareFloat(int tokWhat, float[] data, int tokOperator,
                              float comparisonFloat) {
    BitSet bs = new BitSet();
    int atomCount = viewer.getAtomCount();
    ModelSet modelSet = viewer.getModelSet();
    Atom[] atoms = modelSet.atoms;
    float propertyFloat = 0;
    viewer.autoCalculate(tokWhat);
    for (int i = 0; i < atomCount; ++i) {
      boolean match = false;
      Atom atom = atoms[i];
      switch (tokWhat) {
      default:
        propertyFloat = Atom.atomPropertyFloat(viewer, atom, tokWhat);
        break;
      case Token.property:
        if (data == null || data.length <= i)
          continue;
        propertyFloat = data[i];
      }
      match = compareFloat(tokOperator, propertyFloat, comparisonFloat);
      if (match)
        bs.set(i);
    }
    return bs;
  }

  private BitSet compareString(int tokWhat, int tokOperator,
                               String comparisonString) throws ScriptException {
    BitSet bs = new BitSet();
    Atom[] atoms = viewer.getModelSet().atoms;
    int atomCount = viewer.getAtomCount();
    boolean isCaseSensitive = (tokWhat == Token.chain && viewer
        .getChainCaseSensitive());
    if (!isCaseSensitive)
      comparisonString = comparisonString.toLowerCase();
    for (int i = 0; i < atomCount; ++i) {
      String propertyString = Atom.atomPropertyString(atoms[i], tokWhat);
      if (!isCaseSensitive)
        propertyString = propertyString.toLowerCase();
      if (compareString(tokOperator, propertyString, comparisonString))
        bs.set(i);
    }
    return bs;
  }

  protected BitSet compareInt(int tokWhat, float[] data, int tokOperator,
                              int comparisonValue) {
    int propertyValue = Integer.MAX_VALUE;
    BitSet propertyBitSet = null;
    int bitsetComparator = tokOperator;
    int bitsetBaseValue = comparisonValue;
    int atomCount = viewer.getAtomCount();
    ModelSet modelSet = viewer.getModelSet();
    Atom[] atoms = modelSet.atoms;
    int imax = -1;
    int imin = 0;
    int iModel = -1;
    int[] cellRange = null;
    int nOps = 0;
    BitSet bs;
    
    switch (tokWhat) {
    case Token.symop:
      switch (bitsetComparator) {
      case Token.opGE:
      case Token.opGT:
        imax = Integer.MAX_VALUE;
        break;
      }
      break;
    case Token.atomindex:
      switch (tokOperator) {
      case Token.opLT:
        return BitSetUtil.newBitSet(0, comparisonValue);
      case Token.opLE:
        return BitSetUtil.newBitSet(0, comparisonValue + 1);
      case Token.opGE:
        return BitSetUtil.newBitSet(comparisonValue, atomCount);
      case Token.opGT:
        return BitSetUtil.newBitSet(comparisonValue + 1, atomCount);
      case Token.opEQ:
        return BitSetUtil.newBitSet(comparisonValue, comparisonValue + 1);
      case Token.opNE:
      default:
        bs = BitSetUtil.setAll(atomCount);
        if (comparisonValue >= 0)
          bs.clear(comparisonValue);
        return bs;
      }
    }
    bs = new BitSet();
    for (int i = 0; i < atomCount; ++i) {
      boolean match = false;
      Atom atom = atoms[i];
      switch (tokWhat) {
      default:
        propertyValue = Atom.atomPropertyInt(atom, tokWhat);
        break;
      case Token.configuration:
        
        return BitSetUtil.copy(viewer.getConformation(-1, comparisonValue - 1,
            false));
      case Token.symop:
        propertyBitSet = atom.getAtomSymmetry();
        if (atom.getModelIndex() != iModel) {
          iModel = atom.getModelIndex();
          cellRange = modelSet.getModelCellRange(iModel);
          nOps = modelSet.getModelSymmetryCount(iModel);
        }
        if (bitsetBaseValue >= 200) {
          if (cellRange == null)
            continue;
          
          comparisonValue = bitsetBaseValue % 1000;
          int symop = bitsetBaseValue / 1000 - 1;
          if (symop < 0) {
            match = true;
          } else if (nOps == 0 || symop >= 0
              && !(match = propertyBitSet.get(symop))) {
            continue;
          }
          bitsetComparator = Token.none;
          if (symop < 0)
            propertyValue = atom.getCellTranslation(comparisonValue, cellRange,
                nOps);
          else
            propertyValue = atom.getSymmetryTranslation(symop, cellRange, nOps);
        } else if (nOps > 0) {
          if (comparisonValue > nOps) {
            if (bitsetComparator != Token.opLT
                && bitsetComparator != Token.opLE)
              continue;
          }
          if (bitsetComparator == Token.opNE) {
            if (comparisonValue > 0 && comparisonValue <= nOps
                && !propertyBitSet.get(comparisonValue)) {
              bs.set(i);
            }
            continue;
          }
        }
        switch (bitsetComparator) {
        case Token.opLT:
          imax = comparisonValue - 1;
          break;
        case Token.opLE:
          imax = comparisonValue;
          break;
        case Token.opGE:
          imin = comparisonValue - 1;
          break;
        case Token.opGT:
          imin = comparisonValue;
          break;
        case Token.opEQ:
          imax = comparisonValue;
          imin = comparisonValue - 1;
          break;
        case Token.opNE:
          match = !propertyBitSet.get(comparisonValue);
          break;
        }
        if (imin < 0)
          imin = 0;
        if (imin < imax) {
          int pt = propertyBitSet.nextSetBit(imin);
          if (pt >= 0 && pt < imax)
            match = true;
        }
        
        if (!match || propertyValue == Integer.MAX_VALUE)
          tokOperator = Token.none;
      }
      if (tokOperator != Token.none)
        match = compareInt(tokOperator, propertyValue, comparisonValue);
      if (match)
        bs.set(i);
    }
    return bs;
  }

  private boolean compareString(int tokOperator, String propertyValue,
                                String comparisonValue) throws ScriptException {
    switch (tokOperator) {
    case Token.opEQ:
    case Token.opNE:
      return (TextFormat.isMatch(propertyValue, comparisonValue, true, true) == (tokOperator == Token.opEQ));
    default:
      error(ERROR_invalidArgument);
    }
    return false;
  }

  private static boolean compareInt(int tokOperator, int propertyValue,
                                    int comparisonValue) {
    switch (tokOperator) {
    case Token.opLT:
      return propertyValue < comparisonValue;
    case Token.opLE:
      return propertyValue <= comparisonValue;
    case Token.opGE:
      return propertyValue >= comparisonValue;
    case Token.opGT:
      return propertyValue > comparisonValue;
    case Token.opEQ:
      return propertyValue == comparisonValue;
    case Token.opNE:
      return propertyValue != comparisonValue;
    }
    return false;
  }

  private static boolean compareFloat(int tokOperator, float propertyFloat,
                                      float comparisonFloat) {
    switch (tokOperator) {
    case Token.opLT:
      return propertyFloat < comparisonFloat;
    case Token.opLE:
      return propertyFloat <= comparisonFloat;
    case Token.opGE:
      return propertyFloat >= comparisonFloat;
    case Token.opGT:
      return propertyFloat > comparisonFloat;
    case Token.opEQ:
      return propertyFloat == comparisonFloat;
    case Token.opNE:
      return propertyFloat != comparisonFloat;
    }
    return false;
  }

  private BitSet getAtomBits(int tokType, Object specInfo) {
    return (isSyntaxCheck ? new BitSet() : viewer
        .getAtomBits(tokType, specInfo));
  }

  private static int getSeqCode(Token instruction) {
    return (instruction.intValue != Integer.MAX_VALUE ? Group.getSeqcode(
        instruction.intValue, ' ') : ((Integer) instruction.value).intValue());
  }

  

  private int checkLast(int i) throws ScriptException {
    return checkLength(i + 1) - 1;  
  }
  
  private int checkLength(int length) throws ScriptException {
    if (length >= 0)
      return checkLength(length, 0);
    
    if (statementLength > -length) {
      iToken = -length;
      error(ERROR_badArgumentCount);
    }
    return statementLength;
  }

  private int checkLength(int length, int errorPt) throws ScriptException {
    if (statementLength != length) {
      iToken = errorPt > 0 ? errorPt : statementLength;
      error(errorPt > 0 ? ERROR_invalidArgument : ERROR_badArgumentCount);
    }
    return statementLength;
  }

  private int checkLength23() throws ScriptException {
    iToken = statementLength;
    if (statementLength != 2 && statementLength != 3)
      error(ERROR_badArgumentCount);
    return statementLength;
  }

  private int checkLength34() throws ScriptException {
    iToken = statementLength;
    if (statementLength != 3 && statementLength != 4)
      error(ERROR_badArgumentCount);
    return statementLength;
  }

  private int theTok;
  private Token theToken;

  private Token getToken(int i) throws ScriptException {
    if (!checkToken(i))
      error(ERROR_endOfStatementUnexpected);
    theToken = statement[i];
    theTok = theToken.tok;
    return theToken;
  }

  private int tokAt(int i) {
    return (i < statementLength ? statement[i].tok : Token.nada);
  }

  private int tokAt(int i, Token[] args) {
    return (i < args.length ? args[i].tok : Token.nada);
  }

  private Token tokenAt(int i, Token[] args) {
    return (i < args.length ? args[i] : null);
  }

  private boolean checkToken(int i) {
    return (iToken = i) < statementLength;
  }

  private int modelNumberParameter(int index) throws ScriptException {
    int iFrame = 0;
    boolean useModelNumber = false;
    switch (tokAt(index)) {
    case Token.integer:
      useModelNumber = true;
      
    case Token.decimal:
      iFrame = getToken(index).intValue; 
                                         
      break;
    default:
      error(ERROR_invalidArgument);
    }
    return viewer.getModelNumberIndex(iFrame, useModelNumber, true);
  }

  private String optParameterAsString(int i) throws ScriptException {
    if (i >= statementLength)
      return "";
    return parameterAsString(i);
  }

  private String parameterAsString(int i) throws ScriptException {
    getToken(i);
    if (theToken == null)
      error(ERROR_endOfStatementUnexpected);
    return (theTok == Token.integer ? "" + theToken.intValue : ""
        + theToken.value);
  }

  private int intParameter(int index) throws ScriptException {
    if (checkToken(index))
      if (getToken(index).tok == Token.integer)
        return theToken.intValue;
    error(ERROR_integerExpected);
    return 0;
  }

  private int intParameter(int i, int min, int max) throws ScriptException {
    int val = intParameter(i);
    if (val < min || val > max)
      integerOutOfRange(min, max);
    return val;
  }

  private boolean isFloatParameter(int index) {
    switch (tokAt(index)) {
    case Token.integer:
    case Token.decimal:
      return true;
    }
    return false;
  }

  private float floatParameter(int i, float min, float max)
      throws ScriptException {
    float val = floatParameter(i);
    if (val < min || val > max)
      numberOutOfRange(min, max);
    return val;
  }

  private float floatParameter(int index) throws ScriptException {
    if (checkToken(index)) {
      getToken(index);
      switch (theTok) {
      case Token.spec_seqcode_range:
        return -theToken.intValue;
      case Token.spec_seqcode:
      case Token.integer:
        return theToken.intValue;
      case Token.spec_model2:
      case Token.decimal:
        return ((Float) theToken.value).floatValue();
      }
    }
    error(ERROR_numberExpected);
    return 0;
  }

  
  private float[] floatParameterSet(int i, int nMin, int nMax)
      throws ScriptException {
    int tok = tokAt(i);
    boolean haveBrace = (tok == Token.leftbrace);
    boolean haveSquare = (tok == Token.leftsquare);
    float[] fparams = null;
    Vector v = new Vector();
    int n = 0;
    if (haveBrace || haveSquare)
      i++;
    Point3f pt;
    if (tokAt(i) == Token.string) {
      String s = stringParameter(i);
      s = TextFormat.replaceAllCharacters(s, "{},[]\"'", ' ');
      fparams = Parser.parseFloatArray(s);
      n = fparams.length;
    } else {
      while (n < nMax) {
        tok = tokAt(i);
        if (haveBrace && tok == Token.rightbrace || haveSquare && tok == Token.rightsquare)
          break;
        switch (tok) {
        case Token.comma:
        case Token.leftbrace:
        case Token.rightbrace:
          break;
        case Token.string:
          break;
        case Token.point3f:
          pt = getPoint3f(i, false);
          v.add(new Float(pt.x));
          v.add(new Float(pt.y));
          v.add(new Float(pt.z));
          n += 3;
          break;
        case Token.point4f:
          Point4f pt4 = getPoint4f(i);
          v.add(new Float(pt4.x));
          v.add(new Float(pt4.y));
          v.add(new Float(pt4.z));
          v.add(new Float(pt4.w));
          n += 4;
          break;
        default:
          v.add(new Float(floatParameter(i)));
          n++;
        }
        i++;
      }
    }
    if (haveBrace && tokAt(i++) != Token.rightbrace
        || haveSquare && tokAt(i++) != Token.rightsquare)
      error(ERROR_invalidArgument);
    iToken = i - 1;
    if (n < nMin || n > nMax)
      error(ERROR_invalidArgument);
    if (fparams == null) {
      fparams = new float[n];
      for (int j = 0; j < n; j++)
        fparams[j] = ((Float) v.get(j)).floatValue();
    }
    return fparams;
  }

  private String stringParameter(int index) throws ScriptException {
    if (!checkToken(index) || getToken(index).tok != Token.string)
      error(ERROR_stringExpected);
    return (String) theToken.value;
  }

  private String[] stringParameterSet(int i)
      throws ScriptException {
    switch (tokAt(i)) {
    case Token.string:
      String s = stringParameter(i);
      if (s.startsWith("[\"")) {
        Object o = viewer.evaluateExpression(s);
        if (o instanceof String)
          return TextFormat.split((String) o, '\n');
      }
      return  new String[] { s };            
    case Token.leftsquare:
      ++i;
      break;
    default:
      error(ERROR_invalidArgument);
    }
    int tok;
    Vector v = new Vector();
    while ((tok = tokAt(i)) != Token.rightsquare) {
        switch (tok) {
        case Token.comma:
          break;
        case Token.string:
          v.add(stringParameter(i));
          break;
        default:
        case Token.nada:
          error(ERROR_invalidArgument);
        }
        i++;
      }
    iToken = i;
    int n = v.size();    
    String[] sParams = new String[n];
      for (int j = 0; j < n; j++)
        sParams[j] = (String) v.get(j);
    return sParams;
  }
  
  private String objectNameParameter(int index) throws ScriptException {
    if (!checkToken(index))
      error(ERROR_objectNameExpected);
    return parameterAsString(index);
  }

  private boolean booleanParameter(int i) throws ScriptException {
    if (statementLength == i)
      return true;
    switch (getToken(checkLast(i)).tok) {
    case Token.on:
      return true;
    case Token.off:
      return false;
    default:
      error(ERROR_booleanExpected);
    }
    return false;
  }

  private Point3f atomCenterOrCoordinateParameter(int i) throws ScriptException {
    switch (getToken(i).tok) {
    case Token.bitset:
    case Token.expressionBegin:
      BitSet bs = expression(statement, i, 0, true, false, false, true);
      if (bs != null)
        return viewer.getAtomSetCenter(bs);
      if (expressionResult instanceof Point3f)
        return (Point3f) expressionResult;
      error(ERROR_invalidArgument);
      break;
    case Token.leftbrace:
    case Token.point3f:
      return getPoint3f(i, true);
    }
    error(ERROR_invalidArgument);
    
    return null;
  }

  private boolean isCenterParameter(int i) {
    int tok = tokAt(i);
    return (tok == Token.dollarsign || tok == Token.leftbrace
        || tok == Token.expressionBegin || tok == Token.point3f || tok == Token.bitset);
  }

  private Point3f centerParameter(int i) throws ScriptException {
    Point3f center = null;
    expressionResult = null;
    if (checkToken(i)) {
      switch (getToken(i).tok) {
      case Token.dollarsign:
        int index = Integer.MIN_VALUE;
        String id = objectNameParameter(++i);
        
        if (tokAt(i + 1) == Token.leftsquare) {
          index = intParameter(i + 2);
          if (getToken(i + 3).tok != Token.rightsquare)
            error(ERROR_invalidArgument);
        }
        if (isSyntaxCheck)
          return new Point3f();
        if ((center = getObjectCenter(id, index)) == null)
          error(ERROR_drawObjectNotDefined, id);
        break;
      case Token.bitset:
      case Token.expressionBegin:
      case Token.leftbrace:
      case Token.point3f:
        center = atomCenterOrCoordinateParameter(i);
        break;
      }
    }
    if (center == null)
      error(ERROR_coordinateOrNameOrExpressionRequired);
    return center;
  }

  private Point4f planeParameter(int i) throws ScriptException {
    Vector3f vAB = new Vector3f();
    Vector3f vAC = new Vector3f();
    Point4f plane = null;
    boolean isNegated = (tokAt(i) == Token.minus);
    if (isNegated)
      i++;
    if (i < statementLength)
      switch (getToken(i).tok) {
      case Token.point4f:
        plane = (Point4f) theToken.value;
        break;
      case Token.dollarsign:
        String id = objectNameParameter(++i);
        if (isSyntaxCheck)
          return new Point4f();
        int shapeType = viewer.getShapeIdFromObjectName(id);
        switch (shapeType) {
        case JmolConstants.SHAPE_DRAW:
          setShapeProperty(JmolConstants.SHAPE_DRAW, "thisID", id);
          Point3f[] points = (Point3f[]) viewer.getShapeProperty(
              JmolConstants.SHAPE_DRAW, "vertices");
          if (points == null || points.length < 3 
              || points[0] == null || points[1] == null || points[2] == null)
            break;
          plane = Measure.getPlaneThroughPoints(points[0], points[1],
              points[2], new Vector3f(), vAB, vAC);
          break;
        case JmolConstants.SHAPE_ISOSURFACE:
          setShapeProperty(JmolConstants.SHAPE_ISOSURFACE, "thisID", id);
          plane = (Point4f) viewer.getShapeProperty(
              JmolConstants.SHAPE_ISOSURFACE, "plane");
          break;
        }
        break;
      case Token.identifier:
      case Token.string:
        String str = parameterAsString(i);
        if (str.equalsIgnoreCase("xy"))
          return new Point4f(0, 0, 1, 0);
        if (str.equalsIgnoreCase("xz"))
          return new Point4f(0, 1, 0, 0);
        if (str.equalsIgnoreCase("yz"))
          return new Point4f(1, 0, 0, 0);
        iToken += 2;
        if (str.equalsIgnoreCase("x")) {
          if (!checkToken(++i) || getToken(i++).tok != Token.opEQ)
            evalError("x=?", null);
          plane = new Point4f(1, 0, 0, -floatParameter(i));
          break;
        }

        if (str.equalsIgnoreCase("y")) {
          if (!checkToken(++i) || getToken(i++).tok != Token.opEQ)
            evalError("y=?", null);
          plane = new Point4f(0, 1, 0, -floatParameter(i));
          break;
        }
        if (str.equalsIgnoreCase("z")) {
          if (!checkToken(++i) || getToken(i++).tok != Token.opEQ)
            evalError("z=?", null);
          plane = new Point4f(0, 0, 1, -floatParameter(i));
          break;
        }
        break;
      case Token.leftbrace:
        if (!isPoint3f(i)) {
          plane = getPoint4f(i);
          break;
        }
        
      case Token.bitset:
      case Token.expressionBegin:
        Point3f pt1 = atomCenterOrCoordinateParameter(i);
        if (getToken(++iToken).tok == Token.comma)
          ++iToken;
        Point3f pt2 = atomCenterOrCoordinateParameter(iToken);
        if (getToken(++iToken).tok == Token.comma)
          ++iToken;
        Point3f pt3 = atomCenterOrCoordinateParameter(iToken);
        i = iToken;
        Vector3f norm = new Vector3f();
        float w = Measure.getNormalThroughPoints(pt1, pt2, pt3, norm, vAB,
            vAC);
        plane = new Point4f(norm.x, norm.y, norm.z, w);
        if (!isSyntaxCheck && Logger.debugging)
          Logger.debug("points: " + pt1 + pt2 + pt3 + " defined plane: " + plane);
        break;
      }
    if (plane == null)
      planeExpected();
    if (isNegated) {
      plane.scale(-1);
    }
    return plane;
  }

  private Point4f hklParameter(int i) throws ScriptException {
    if (!isSyntaxCheck && viewer.getCurrentUnitCell() == null)
      error(ERROR_noUnitCell);
    Point3f pt = (Point3f) getPointOrPlane(i, false, true, false, true, 3, 3);
    Point4f p = getHklPlane(pt);
    if (p == null)
      error(ERROR_badMillerIndices);
    if (!isSyntaxCheck && Logger.debugging)
      Logger.info("defined plane: " + p);
    return p;
  }

  protected Point4f getHklPlane(Point3f pt) {
    Vector3f vAB = new Vector3f();
    Vector3f vAC = new Vector3f();
    Point3f pt1 = new Point3f(pt.x == 0 ? 1 : 1 / pt.x, 0, 0);
    Point3f pt2 = new Point3f(0, pt.y == 0 ? 1 : 1 / pt.y, 0);
    Point3f pt3 = new Point3f(0, 0, pt.z == 0 ? 1 : 1 / pt.z);
    
    if (pt.x == 0 && pt.y == 0 && pt.z == 0) {
      return null;
    } else if (pt.x == 0 && pt.y == 0) {
      pt1.set(1, 0, pt3.z);
      pt2.set(0, 1, pt3.z);
    } else if (pt.y == 0 && pt.z == 0) {
      pt2.set(pt1.x, 0, 1);
      pt3.set(pt1.x, 1, 0);
    } else if (pt.z == 0 && pt.x == 0) {
      pt3.set(0, pt2.y, 1);
      pt1.set(1, pt2.y, 0);
    } else if (pt.x == 0) {
      pt1.set(1, pt2.y, 0);
    } else if (pt.y == 0) {
      pt2.set(0, 1, pt3.z);
    } else if (pt.z == 0) {
      pt3.set(pt1.x, 0, 1);
    }
    viewer.toCartesian(pt1);
    viewer.toCartesian(pt2);
    viewer.toCartesian(pt3);
    Vector3f plane = new Vector3f();
    float w = Measure.getNormalThroughPoints(pt1, pt2, pt3, plane, vAB, vAC);
    return new Point4f(plane.x, plane.y, plane.z, w);
  }

  private int getMadParameter() throws ScriptException {
    
    int mad = 1;
    switch (getToken(1).tok) {
    case Token.only:
      restrictSelected(false, false);
      break;
    case Token.on:
      break;
    case Token.off:
      mad = 0;
      break;
    case Token.integer:
      int radiusRasMol = intParameter(1, 0, 750);
      mad = radiusRasMol * 4 * 2;
      break;
    case Token.decimal:
      mad = (int) (floatParameter(1, 0, 3) * 1000 * 2);
      break;
    default:
      error(ERROR_booleanOrNumberExpected);
    }
    return mad;
  }

  private int getSetAxesTypeMad(int index) throws ScriptException {
    if (index == statementLength)
      return 1;
    switch (getToken(checkLast(index)).tok) {
    case Token.on:
      return 1;
    case Token.off:
      return 0;
    case Token.dotted:
      return -1;
    case Token.integer:
      return intParameter(index, -1, 19);
    case Token.decimal:
      float angstroms = floatParameter(index, 0, 2);
      return (int) (angstroms * 1000 * 2);
    }
    error(ERROR_booleanOrWhateverExpected, "\"DOTTED\"");
    return 0;
  }

  private boolean isColorParam(int i) {
    int tok = tokAt(i);
    return (tok == Token.leftsquare || tok == Token.point3f 
        || isPoint3f(i) || (tok == Token.string 
        || Token.tokAttr(tok, Token.identifier))
            && Graphics3D.getArgbFromString((String) statement[i].value) != 0);
  }

  private int getArgbParam(int index) throws ScriptException {
    return getArgbParam(index, false);
  }

  private int getArgbParamLast(int index, boolean allowNone)
      throws ScriptException {
    int icolor = getArgbParam(index, allowNone);
    checkLast(iToken);
    return icolor;
  }

  private int getArgbParam(int index, boolean allowNone) throws ScriptException {
    Point3f pt = null;
    if (checkToken(index)) {
      switch (getToken(index).tok) {
      default:
        if (theTok != Token.string 
            && !Token.tokAttr(theTok, Token.identifier))
          break;
        return Graphics3D.getArgbFromString(parameterAsString(index));
      case Token.leftsquare:
        return getColorTriad(++index);
      case Token.point3f:
        pt = (Point3f) theToken.value;
        break;
      case Token.leftbrace:
        pt = getPoint3f(index, false);
        break;
      case Token.none:
        if (allowNone)
          return 0;
      }
    }
    if (pt == null)
      error(ERROR_colorExpected);
    return Graphics3D.colorPtToInt(pt);
  }

  private int getColorTriad(int i) throws ScriptException {
    float[] colors = new float[3];
    int n = 0;
    String hex = "";
    getToken(i);
    Point3f pt = null;
    float val = 0;
    out: switch (theTok) {
    case Token.integer:
    case Token.spec_seqcode:
    case Token.decimal:
      for (; i < statementLength; i++) {
        switch (getToken(i).tok) {
        case Token.comma:
          continue;
        case Token.identifier:
          if (n != 1 || colors[0] != 0)
            error(ERROR_badRGBColor);
          hex = "0" + parameterAsString(i);
          break out;
        case Token.decimal:
          if (n > 2)
            error(ERROR_badRGBColor);
          val = floatParameter(i);
          break;
        case Token.integer:
          if (n > 2)
            error(ERROR_badRGBColor);
          val = theToken.intValue;
          break;
        case Token.spec_seqcode:
          if (n > 2)
            error(ERROR_badRGBColor);
          val = ((Integer) theToken.value).intValue() % 256;
          break;
        case Token.rightsquare:
          if (n != 3)
            error(ERROR_badRGBColor);
          --i;
          pt = new Point3f(colors[0], colors[1], colors[2]);
          break out;
        default:
          error(ERROR_badRGBColor);
        }
        colors[n++] = val;
      }
      error(ERROR_badRGBColor);
      break;
    case Token.point3f:
      pt = (Point3f) theToken.value;
      break;
    case Token.identifier:
      hex = parameterAsString(i);
      break;
    default:
      error(ERROR_badRGBColor);
    }
    if (getToken(++i).tok != Token.rightsquare)
      error(ERROR_badRGBColor);
    if (pt != null)
      return Graphics3D.colorPtToInt(pt);
    if ((n = Graphics3D.getArgbFromString("[" + hex + "]")) == 0)
      error(ERROR_badRGBColor);
    return n;
  }

  private boolean coordinatesAreFractional;

  private boolean isPoint3f(int i) {
    
    boolean isOK;
    if ((isOK = (tokAt(i) == Token.point3f)) || tokAt(i) == Token.point4f
        || isFloatParameter(i + 1) && isFloatParameter(i + 2)
        && isFloatParameter(i + 3) && isFloatParameter(i + 4))
      return isOK;
    ignoreError = true;
    int t = iToken;
    isOK = true;
    try {
      getPoint3f(i, true);
    } catch (Exception e) {
      isOK = false;
    }
    ignoreError = false;
    iToken = t;
    return isOK;
  }

  private Point3f getPoint3f(int i, boolean allowFractional)
      throws ScriptException {
    return (Point3f) getPointOrPlane(i, false, allowFractional, true, false, 3,
        3);
  }

  private Point4f getPoint4f(int i) throws ScriptException {
    return (Point4f) getPointOrPlane(i, false, false, false, false, 4, 4);
  }

  private Object getPointOrPlane(int index, boolean integerOnly,
                                 boolean allowFractional, boolean doConvert,
                                 boolean implicitFractional, int minDim,
                                 int maxDim) throws ScriptException {
    
    
    float[] coord = new float[6];
    int n = 0;
    coordinatesAreFractional = implicitFractional;
    if (tokAt(index) == Token.point3f) {
      if (minDim <= 3 && maxDim >= 3)
        return (Point3f) getToken(index).value;
      error(ERROR_invalidArgument);
    }
    if (tokAt(index) == Token.point4f) {
      if (minDim <= 4 && maxDim >= 4)
        return (Point4f) getToken(index).value;
      error(ERROR_invalidArgument);
    }
    int multiplier = 1;
    out: for (int i = index; i < statement.length; i++) {
      switch (getToken(i).tok) {
      case Token.leftbrace:
      case Token.comma:
        
      case Token.opAnd:
        break;
      case Token.rightbrace:
        break out;
      case Token.minus:
        multiplier = -1;
        break;
      case Token.spec_seqcode_range:
        if (n == 6)
          error(ERROR_invalidArgument);
        coord[n++] = theToken.intValue;
        multiplier = -1;
        break;
      case Token.integer:
      case Token.spec_seqcode:
        if (n == 6)
          error(ERROR_invalidArgument);
        coord[n++] = theToken.intValue * multiplier;
        multiplier = 1;
        break;
      case Token.divide:
        getToken(++i);
      case Token.spec_model: 
        n--;
        if (n < 0 || integerOnly)
          error(ERROR_invalidArgument);
        if (theToken.value instanceof Integer || theTok == Token.integer) {
          coord[n++] /= (theToken.intValue == Integer.MAX_VALUE ? ((Integer) theToken.value)
              .intValue()
              : theToken.intValue);
        } else if (theToken.value instanceof Float) {
          coord[n++] /= ((Float) theToken.value).floatValue();
        }
        coordinatesAreFractional = true;
        break;
      case Token.decimal:
      case Token.spec_model2:
        if (integerOnly)
          error(ERROR_invalidArgument);
        if (n == 6)
          error(ERROR_invalidArgument);
        coord[n++] = ((Float) theToken.value).floatValue();
        break;
      default:
        error(ERROR_invalidArgument);
      }
    }
    if (n < minDim || n > maxDim)
      error(ERROR_invalidArgument);
    if (n == 3) {
      Point3f pt = new Point3f(coord[0], coord[1], coord[2]);
      if (coordinatesAreFractional && doConvert && !isSyntaxCheck)
        viewer.toCartesian(pt);
      return pt;
    }
    if (n == 4) {
      if (coordinatesAreFractional) 
                                    
        error(ERROR_invalidArgument);
      Point4f plane = new Point4f(coord[0], coord[1], coord[2], coord[3]);
      return plane;
    }
    return coord;
  }

  private Point3f xypParameter(int index) throws ScriptException {
    
    
    
    
    

    if (tokAt(index) != Token.leftsquare || !isFloatParameter(++index))
      return null;
    Point3f pt = new Point3f();
    pt.x = floatParameter(index);
    if (tokAt(++index) == Token.comma)
      index++;
    if (!isFloatParameter(index))
      return null;
    pt.y = floatParameter(index);
    boolean isPercent = (tokAt(++index) == Token.percent);
    if (isPercent)
      ++index;
    if (tokAt(index) != Token.rightsquare)
      return null;
    iToken = index;
    pt.z = (isPercent ? -1 : 1) * Float.MAX_VALUE;
    return pt;
  }

  private int intSetting(int pt, int val, int min, int max)
      throws ScriptException {
    if (val == Integer.MAX_VALUE)
      val = intSetting(pt);
    if (val != Integer.MIN_VALUE && val < min || val > max)
      integerOutOfRange(min, max);
    return val;
  }

  private int intSetting(int pt) throws ScriptException {
    if (pt == statementLength)
      return Integer.MIN_VALUE;
    Vector v = (Vector) parameterExpression(pt, -1, "XXX", true);
    if (v == null || v.size() == 0)
      error(ERROR_invalidArgument);
    return ScriptVariable.iValue((ScriptVariable) v.elementAt(0));
  }

  private float floatSetting(int pt, float min, float max)
      throws ScriptException {
    if (pt == statementLength)
      return Float.NaN;
    float val = floatSetting(pt);
    if (val < min || val > max)
      numberOutOfRange(min, max);
    return val;
  }

  private float floatSetting(int pt) throws ScriptException {
    Vector v = (Vector) parameterExpression(pt, -1, "XXX", true);
    if (v == null || v.size() == 0)
      error(ERROR_invalidArgument);
    return ScriptVariable.fValue((ScriptVariable) v.elementAt(0));
  }

  private String stringSetting(int pt, boolean isJmolSet)
      throws ScriptException {
    if (isJmolSet && statementLength == pt + 1)
      return parameterAsString(pt);
    Vector v = (Vector) parameterExpression(pt, -1, "XXX", true);
    if (v == null || v.size() == 0)
      error(ERROR_invalidArgument);
    return ScriptVariable.sValue((ScriptVariable) v.elementAt(0));
  }

  private ScriptVariable tokenSetting(int pt) throws ScriptException {
    Vector v = (Vector) parameterExpression(pt, -1, "XXX", true);
    if (v == null || v.size() == 0)
      error(ERROR_invalidArgument);
    return (ScriptVariable) v.elementAt(0);
  }


  

  
  private boolean isCommandDisplayable(int i) {
    if (i >= aatoken.length || i >= pcEnd || aatoken[i] == null)
      return false;
    return (lineIndices[i][1] > lineIndices[i][0]);
  }

  
  private boolean checkContinue() {
    if (interruptExecution)
      return false;

    if (executionStepping && isCommandDisplayable(pc)) {
      viewer.scriptStatus("Next: " + getNextStatement(), "stepping -- type RESUME to continue", 0, null);
      executionPaused = true;
    } else if (!executionPaused) {
      return true;
    }
  
    if (true || Logger.debugging) {
      Logger.info("script execution paused at command " + (pc + 1) + " level " + scriptLevel + ": " + thisCommand);
    }
      
    try {
      while (executionPaused) {
        viewer.popHoldRepaint("pause");
        Thread.sleep(100);
        refresh();
        String script = viewer.getInterruptScript();
        if (script != "") {
          resumePausedExecution();
          setErrorMessage(null);
          pc--; 
          try {
            runScript(script);
          } catch (Exception e) {
            setErrorMessage("" + e);
          } catch (Error er) {
            setErrorMessage("" + er);
          }
          if (error) {
            popContext();
            scriptStatusOrBuffer(errorMessage);
            setErrorMessage(null);
          }
          pc++;
          pauseExecution();
        }
        viewer.pushHoldRepaint("pause");
      }
      if (!isSyntaxCheck && !interruptExecution && !executionStepping) {
        viewer.scriptStatus("script execution " + (error || interruptExecution ? "interrupted" : "resumed"));
      }
    } catch (Exception e) {
      viewer.pushHoldRepaint("pause");
    }
    Logger.debug("script execution resumed");
    
    return !error && !interruptExecution;
  }

  
  private void instructionDispatchLoop(boolean doList) throws ScriptException {
    long timeBegin = 0;
    boolean isForCheck = false; 

    debugScript = logMessages = false;
    if (!isSyntaxCheck)
      setDebugging();
    if (logMessages) {
      timeBegin = System.currentTimeMillis();
      viewer.scriptStatus("Eval.instructionDispatchLoop():" + timeBegin);
      viewer.scriptStatus(script);
    }
    if (pcEnd == 0)
      pcEnd = Integer.MAX_VALUE;
    if (lineEnd == 0)
      lineEnd = Integer.MAX_VALUE;
    String lastCommand = "";
    if (aatoken == null)
      return;
    for (; pc < aatoken.length && pc < pcEnd; pc++) {
      if (!isSyntaxCheck && !checkContinue())
        break;
      if (lineNumbers[pc] > lineEnd)
        break;
      Token token = (aatoken[pc].length == 0 ? null : aatoken[pc][0]);
      
      
      if (!historyDisabled && !isSyntaxCheck
          && scriptLevel <= commandHistoryLevelMax && !tQuiet) {
        String cmdLine = getCommand(pc, true, true);
        if (token != null
            && cmdLine.length() > 0
            && !cmdLine.equals(lastCommand)
            && (token.tok == Token.function || !Token.tokAttr(token.tok,
                Token.flowCommand)))
          viewer.addCommand(lastCommand = cmdLine);
      }
      if (!setStatement(pc)) {
        Logger.info(getCommand(pc, true, false)
            + " -- STATEMENT CONTAINING @{} SKIPPED");
        continue;
      }
      thisCommand = getCommand(pc, false, true);
      fullCommand = thisCommand + getNextComment();
      iToken = 0;
      String script = viewer.getInterruptScript();
      if (script != "")
        runScript(script);
      if (doList || !isSyntaxCheck) {
        int milliSecDelay = viewer.getScriptDelay();
        if (doList || milliSecDelay > 0 && scriptLevel > 0) {
          if (milliSecDelay > 0)
            delay(-(long) milliSecDelay);
          viewer.scriptEcho("$[" + scriptLevel + "." + lineNumbers[pc] + "."
              + (pc + 1) + "] " + thisCommand);
        }
      }
      if (isSyntaxCheck) {
        if (isCmdLine_c_or_C_Option)
          Logger.info(thisCommand);
        if (statementLength == 1 && statement[0].tok != Token.function)
          
          continue;
      } else {
        if (debugScript)
          logDebugScript(0);
        if (scriptLevel == 0 && viewer.logCommands())
          viewer.log(thisCommand);
        if (logMessages && token != null)
          Logger.debug(token.toString());
      }
      if (token == null)
        continue;

      if (Token.tokAttr(token.tok, Token.shapeCommand))
        processShapeCommand(token.tok);
      else
        switch (token.tok) {
        case Token.nada:
          break;
        case Token.breakcmd:
        case Token.continuecmd:
        case Token.elsecmd:
        case Token.elseif:
        case Token.end:
        case Token.endifcmd:
        case Token.forcmd:
        case Token.gotocmd:
        case Token.ifcmd:
        case Token.loop:
        case Token.whilecmd:
          isForCheck = flowControl(token.tok, isForCheck);
          break;
        case Token.animation:
          animation();
          break;
        case Token.background:
          background(1);
          break;
        case Token.bind:
          bind();
          break;
        case Token.bondorder:
          bondorder();
          break;
        case Token.calculate:
          calculate();
          break;
        case Token.cd:
          cd();
          break;
        case Token.center:
          center(1);
          break;
        case Token.centerAt:
          centerAt();
          break;
        case Token.color:
          color();
          break;
        case Token.configuration:
          configuration();
          break;
        case Token.connect:
          connect(1);
          break;
        case Token.console:
          console();
          break;
        case Token.data:
          data();
          break;
        case Token.define:
          define();
          break;
        case Token.delay:
          delay();
          break;
        case Token.delete:
          delete();
          break;
        case Token.depth:
          slab(true);
          break;
        case Token.display:
          display(true);
          break;
        case Token.exit: 
          if (!isSyntaxCheck && pc > 0)
            viewer.clearScriptQueue();
        case Token.exitjmol:
          if (isSyntaxCheck || viewer.isApplet())
            return;
          viewer.exitJmol();
          break;
        case Token.file:
          file();
          break;
        case Token.font:
          font(-1, 0);
          break;
        case Token.frame:
        case Token.model:
          frame(1);
          break;
        case Token.function:
          function();
          break;
        case Token.getproperty:
          getProperty();
          break;
        case Token.help:
          help();
          break;
        case Token.hide:
          display(false);
          break;
        case Token.hbond:
          hbond(true);
          break;
        case Token.history:
          history(1);
          break;
        case Token.hover:
          hover();
          break;
        case Token.initialize:
          viewer.initialize();
          break;
        case Token.invertSelected:
          invertSelected();
          break;
        case Token.javascript:
          script(Token.javascript);
          break;
        case Token.load:
          load();
          break;
        case Token.log:
          log();
          break;
        case Token.message:
          message();
          break;
        case Token.minimize:
          minimize();
          break;
        case Token.move:
          move();
          break;
        case Token.moveto:
          moveto();
          break;
        case Token.navigate:
          navigate();
          break;
        case Token.pause: 
          pause();
          break;
        case Token.print:
          print();
          break;
        case Token.quaternion:
          dataFrame(JmolConstants.JMOL_DATA_QUATERNION);
          break;
        case Token.ramachandran:
          dataFrame(JmolConstants.JMOL_DATA_RAMACHANDRAN);
          break;
        case Token.quit: 
          if (!isSyntaxCheck)
            interruptExecution = (pc > 0 || !viewer.usingScriptQueue());
          break;
        case Token.refresh:
          refresh();
          break;
        case Token.reset:
          reset();
          break;
        case Token.restore:
          restore();
          break;
        case Token.restrict:
          restrict();
          break;
        case Token.resume:
          if (!isSyntaxCheck)
            resumePausedExecution();
          break;
        case Token.returncmd:
          returnCmd();
          break;
        case Token.rotate:
          rotate(false, false);
          break;
        case Token.rotateSelected:
          rotate(false, true);
          break;
        case Token.save:
          save();
          break;
        case Token.set:
          set();
          break;
        case Token.script:
          script(Token.script);
          break;
        case Token.select:
          select(1);
          break;
        case Token.selectionhalos:
          selectionHalo(1);
          break;
       case Token.show:
          show();
          break;
        case Token.slab:
          slab(false);
          break;
        case Token.spin:
          rotate(true, false);
          break;
        case Token.ssbond:
          ssbond();
          break;
        case Token.struts:
          struts();
          break;
        case Token.step:
          if (pause())
            stepPausedExecution();
          break;
        case Token.stereo:
          stereo();
          break;
        case Token.structure:
          structure();
          break;
        case Token.subset:
          subset();
          break;
        case Token.sync:
          sync();
          break;
        case Token.timeout:
          timeout(1);
          break;
        case Token.translate:
          translate();
          break;
        case Token.translateSelected:
          translateSelected();
          break;
        case Token.unbind:
          unbind();
          break;
        case Token.vibration:
          vibration();
          break;
        case Token.write:
          write(null);
          break;
        case Token.zap:
          zap(true);
          break;
        case Token.zoom:
          zoom(false);
          break;
        case Token.zoomTo:
          zoom(true);
          break;
        default:
          error(ERROR_unrecognizedCommand);
        }
      if (!isSyntaxCheck && !tQuiet)
        viewer.setCursor(Viewer.CURSOR_DEFAULT);
      
      if (executionStepping) {
        executionPaused = (isCommandDisplayable(pc + 1));
      }
    }
  }

  private void processShapeCommand(int tok) throws ScriptException {
    int iShape;
    switch (tok) {
    case Token.axes:
      iShape = JmolConstants.SHAPE_AXES;
      axes(1);
      break;
    case Token.backbone:
      proteinShape(iShape = JmolConstants.SHAPE_BACKBONE);
      break;
    case Token.boundbox:
      iShape = JmolConstants.SHAPE_BBCAGE;
      boundbox(1);
      break;
    case Token.cartoon:
      proteinShape(iShape = JmolConstants.SHAPE_CARTOON);
      break;
    case Token.dipole:
      iShape = JmolConstants.SHAPE_DIPOLES;
      dipole();
      break;
    case Token.dots:
      dots(iShape = JmolConstants.SHAPE_DOTS);
      break;
    case Token.draw:
      iShape = JmolConstants.SHAPE_DRAW;
      draw();
      break;
    case Token.echo:
      iShape = JmolConstants.SHAPE_ECHO;
      echo(1, false);
      break;
    case Token.ellipsoid:
      iShape = JmolConstants.SHAPE_ELLIPSOIDS;
      ellipsoid();
      break;
    case Token.frank:
      iShape = JmolConstants.SHAPE_FRANK;
      frank(1);
      break;
    case Token.geosurface:
      dots(iShape = JmolConstants.SHAPE_GEOSURFACE);
      break;
    case Token.halo:
      setAtomShapeSize(iShape = JmolConstants.SHAPE_HALOS, 0.2f);
      break;
    case Token.isosurface:
      isosurface(iShape = JmolConstants.SHAPE_ISOSURFACE);
      break;
    case Token.label:
      iShape = JmolConstants.SHAPE_LABELS;
      label(1);
      break;
    case Token.lcaocartoon:
      iShape = JmolConstants.SHAPE_LCAOCARTOON;
      lcaoCartoon();
      break;
    case Token.measurements:
    case Token.measure:
      iShape = JmolConstants.SHAPE_MEASURES;
      measure();
      break;
    case Token.meshRibbon:
      proteinShape(iShape = JmolConstants.SHAPE_MESHRIBBON);
      break;
    case Token.mo:
      iShape = JmolConstants.SHAPE_MO;
      mo(false);
      break;
    case Token.plot3d:
      isosurface(iShape = JmolConstants.SHAPE_PLOT3D);
      break;
    case Token.pmesh:
      isosurface(iShape = JmolConstants.SHAPE_PMESH);
      break;
    case Token.polyhedra:
      iShape = JmolConstants.SHAPE_POLYHEDRA;
      polyhedra();
      break;
    case Token.ribbon:
      proteinShape(iShape = JmolConstants.SHAPE_RIBBONS);
      break;
    case Token.rocket:
      proteinShape(iShape = JmolConstants.SHAPE_ROCKETS);
      break;
    case Token.spacefill: 
      setAtomShapeSize(iShape = JmolConstants.SHAPE_BALLS, 1f);
      break;
    case Token.star:
      setAtomShapeSize(iShape = JmolConstants.SHAPE_STARS, 1f);
      break;
    case Token.strands:
      proteinShape(iShape = JmolConstants.SHAPE_STRANDS);
      break;
    case Token.trace:
      proteinShape(iShape = JmolConstants.SHAPE_TRACE);
      break;
    case Token.unitcell:
      iShape = JmolConstants.SHAPE_UCCAGE;
      unitcell(1);
      break;
    case Token.vector:
      iShape = JmolConstants.SHAPE_VECTORS;
      vector();
      break;
    case Token.wireframe:
      iShape = JmolConstants.SHAPE_STICKS;
      wireframe();
      break;
    default:
      iShape = -1;
    }
    if (iShape < 0)
      error(ERROR_unrecognizedCommand);
    setShapeProperty(iShape, "setXml", null);
  }

  private boolean flowControl(int tok, boolean isForCheck) throws ScriptException {
    switch (tok) {
    case Token.gotocmd:
      String strTo = parameterAsString(checkLast(1));
      int pcTo = -1;
      for (int i = 0; i < aatoken.length; i++) {
        Token[] tokens = aatoken[i];
        if (tokens[0].tok == Token.message || tokens[0].tok == Token.nada)
          if (tokens[tokens.length - 1].value.toString().equalsIgnoreCase(strTo)) {
            pcTo = i;
            break;
          }
      }
      if (pcTo < 0)
        error(ERROR_invalidArgument);
      if (!isSyntaxCheck)
        pc = pcTo - 1; 
      return isForCheck;
    case Token.loop:
      
      delay();
      if (!isSyntaxCheck)
        pc = -1;
      return isForCheck;
    }
    int pt = statement[0].intValue;
    boolean isDone = (pt < 0 && !isSyntaxCheck);
    boolean isOK = true;
    int ptNext = 0;
    switch (tok) {
    case Token.ifcmd:
    case Token.elseif:
      isOK = (!isDone && ifCmd());
      if (isSyntaxCheck)
        break;
      ptNext = Math.abs(aatoken[Math.abs(pt)][0].intValue);
      ptNext = (isDone || isOK ? -ptNext : ptNext);
      aatoken[Math.abs(pt)][0].intValue = ptNext;
      break;
    case Token.elsecmd:
      checkLength(1);
      if (pt < 0 && !isSyntaxCheck)
        pc = -pt - 1;
      break;
    case Token.endifcmd:
      checkLength(1);
      break;
    case Token.end: 
      if (getToken(checkLast(1)).tok == Token.function) {
        viewer.addFunction((ScriptFunction) theToken.value);
        return isForCheck;
      }
      isForCheck = (theTok == Token.forcmd);
      isOK = (theTok == Token.ifcmd);
      break;
    case Token.whilecmd:
      isForCheck = false;
      if (!ifCmd() && !isSyntaxCheck)
        pc = pt;
      break;
    case Token.breakcmd:
      if (!isSyntaxCheck)
        pc = aatoken[pt][0].intValue;
      if (statementLength > 1)
        intParameter(checkLast(1));
      break;
    case Token.continuecmd:
      isForCheck = true;
      if (!isSyntaxCheck)
        pc = pt - 1;
      if (statementLength > 1)
        intParameter(checkLast(1));
      break;
    case Token.forcmd:
      
      
      
      
      int[] pts = new int[2];
      int j = 0;
      BitSet bsIn = null;
      for (int i = 1, nSkip = 0; i < statementLength && j < 2; i++) {
        switch (tokAt(i)) {
        case Token.semicolon:
          if (nSkip > 0)
            nSkip--;
          else
            pts[j++] = i;
          break;
        case Token.in:
          nSkip -= 2;
          bsIn = expression(++i);
          i = iToken;
          break;
        case Token.select:
          nSkip += 2;
          break;
        }

      }
      if (isForCheck) {
        j = pts[1] + 1;
        isForCheck = false;
      } else {
        j = 2;
        if (tokAt(j) == Token.var)
          j++;
      }
      String key = parameterAsString(j);
      if (Token.tokAttr(tokAt(j), Token.misc) || getContextVariableAsVariable(key) != null) {
        if (bsIn == null && getToken(++j).tok != Token.opEQ)
          error(ERROR_invalidArgument);
        if (bsIn == null) {
          setVariable(++j, statementLength - 1, key, 0);
        } else {
          setVariable(j + 2, statementLength - 1, key + "_set", 0);
          setVariable(j + 2, statementLength - 1, key, 0);
        }
      }
      isOK = ((Boolean) parameterExpression(pts[0] + 1, pts[1], null, false))
          .booleanValue();
      pt++;
      break;
    }
    if (!isOK && !isSyntaxCheck)
      pc = Math.abs(pt) - 1;
    return isForCheck;
  }

  private boolean ifCmd() throws ScriptException {
    return ((Boolean) parameterExpression(1, 0, null, false)).booleanValue();
  }

  private void returnCmd() throws ScriptException {
    ScriptVariable t = getContextVariableAsVariable("_retval");
    if (t == null) {
      if (!isSyntaxCheck)
        interruptExecution = true;
      return;
    }
    Vector v = (statementLength == 1 ? null : (Vector) parameterExpression(1,
        0, null, true));
    if (isSyntaxCheck)
      return;
    ScriptVariable tv = (v == null || v.size() == 0 ? ScriptVariable
        .intVariable(0) : (ScriptVariable) v.get(0));
    t.value = tv.value;
    t.intValue = tv.intValue;
    t.tok = tv.tok;
    pcEnd = pc;
  }

  private void help() throws ScriptException {
    if (isSyntaxCheck)
      return;
    String what = optParameterAsString(1);
    int pt = 0;
    if (what.toLowerCase().startsWith("mouse")
        && (pt = what.indexOf(" ")) >= 0 && pt == what.lastIndexOf(" ")) {
      showString(viewer.getBindingInfo(what.substring(pt + 1)));
      return;
    }    
    Token t = Token.getTokenFromName(what);
    if (t != null && (t.tok & Token.scriptCommand) != 0)
      what = "?command=" + what;
    viewer.getHelp(what);
  }

  private void move() throws ScriptException {
    if (statementLength > 11)
      error(ERROR_badArgumentCount);
    
    Vector3f dRot = new Vector3f(floatParameter(1), floatParameter(2),
        floatParameter(3));
    float dZoom = floatParameter(4);
    Vector3f dTrans = new Vector3f(intParameter(5), intParameter(6),
        intParameter(7));
    float dSlab = floatParameter(8);
    float floatSecondsTotal = floatParameter(9);
    int fps = (statementLength == 11 ? intParameter(10) : 30);
    if (isSyntaxCheck)
      return;
    refresh();
    viewer.move(dRot, dZoom, dTrans, dSlab, floatSecondsTotal, fps);
  }

  private void moveto() throws ScriptException {
    
    
    
    
    
    
    
    
    
    if (statementLength == 2 && tokAt(1) == Token.stop) {
      if (!isSyntaxCheck)
        viewer.stopMotion();
      return;
    }
      
    if (statementLength == 2 && isFloatParameter(1)) {
      float f = floatParameter(1);
      if (isSyntaxCheck)
        return;
      if (f > 0)
        refresh();
      viewer.moveTo(f, null, JmolConstants.axisZ, 0, null, 100, 0, 0, 0, null,
          Float.NaN, Float.NaN, Float.NaN);
      return;
    }
    Vector3f axis = new Vector3f(Float.NaN, 0, 0);
    Point3f center = null;
    int i = 1;
    float floatSecondsTotal = (isFloatParameter(i) ? floatParameter(i++) : 2.0f);
    float degrees = 90;
    BitSet bsCenter = null;
    switch (getToken(i).tok) {
    case Token.quaternion:
      Quaternion q;
      boolean isMolecular = false;
      if (tokAt(++i) == Token.molecular) {
        
        isMolecular = true;
        i++;
      }
      if (tokAt(i) == Token.bitset || tokAt(i) == Token.expressionBegin) {
        isMolecular = true;
        center = centerParameter(i);
        if (!(expressionResult instanceof BitSet))
          error(ERROR_invalidArgument);  
        bsCenter = (BitSet) expressionResult;
        q = (isSyntaxCheck ? new Quaternion() 
            : viewer.getAtomQuaternion(bsCenter.nextSetBit(0)));
      } else {
        q = new Quaternion(getPoint4f(i));
      }
      i = iToken + 1;      
      if (q == null)
        error(ERROR_invalidArgument);
      AxisAngle4f aa = q.toAxisAngle4f();
      axis.set(aa.x, aa.y, aa.z);
      
      degrees = (isMolecular ? -1 : 1) * (float)(aa.angle * 180.0 / Math.PI);
      break;
    case Token.point4f:
    case Token.point3f:
    case Token.leftbrace:
      
      if (isPoint3f(i)) {
        axis.set(getPoint3f(i, true));
        i = iToken + 1;
        degrees = floatParameter(i++);
      } else {
        Point4f pt4 = getPoint4f(i);
        i = iToken + 1;
        axis.set(pt4.x, pt4.y, pt4.z);
        degrees = (pt4.x == 0 && pt4.y == 0 && pt4.z == 0 ? Float.NaN : pt4.w);
      }
      break;
    case Token.front:
      axis.set(1, 0, 0);
      degrees = 0f;
      checkLength(++i);
      break;
    case Token.back:
      axis.set(0, 1, 0);
      degrees = 180f;
      checkLength(++i);
      break;
    case Token.left:
      axis.set(0, 1, 0);
      checkLength(++i);
      break;
    case Token.right:
      axis.set(0, -1, 0);
      checkLength(++i);
      break;
    case Token.top:
      axis.set(1, 0, 0);
      checkLength(++i);
      break;
    case Token.bottom:
      axis.set(-1, 0, 0);
      checkLength(++i);
      break;
    default:
      
      axis = new Vector3f(floatParameter(i++), floatParameter(i++),
          floatParameter(i++));
      degrees = floatParameter(i++);
    }
    if (Float.isNaN(axis.x) || Float.isNaN(axis.y) || Float.isNaN(axis.z))
      axis.set(0, 0, 0);
    else if (axis.length() == 0 && degrees == 0)
      degrees = Float.NaN;
    boolean isChange = !viewer.isInPosition(axis, degrees);
    
    float zoom = (isFloatParameter(i) ? floatParameter(i++) : Float.NaN);
    
    float xTrans = 0;
    float yTrans = 0;
    if (isFloatParameter(i) && !isCenterParameter(i)) {
      xTrans = floatParameter(i++);
      yTrans = floatParameter(i++);
      if (!isChange && Math.abs(xTrans - viewer.getTranslationXPercent()) >= 1)
        isChange = true;
      if (!isChange && Math.abs(yTrans - viewer.getTranslationYPercent()) >= 1)
        isChange = true;
    }
    if (bsCenter == null && i != statementLength) {
      
      center = centerParameter(i);
      if (expressionResult instanceof BitSet)
        bsCenter = (BitSet) expressionResult;
      i = iToken + 1;
    }
    float rotationRadius = Float.NaN;
    float zoom0 = viewer.getZoomSetting();
    if (center != null) {
      if (!isChange && center.distance(viewer.getRotationCenter()) >= 0.1)
        isChange = true;
      
      if (isFloatParameter(i))
        rotationRadius = floatParameter(i++);
      if (!isCenterParameter(i)) {
        if ((rotationRadius == 0 || Float.isNaN(rotationRadius))
            && (zoom == 0 || Float.isNaN(zoom))) {
          
          float newZoom = Math.abs(getZoom(i, bsCenter, (zoom == 0 ? 0 : zoom0)));
          i = iToken + 1;
          zoom = newZoom;
        } else {
          if (!isChange
              && Math.abs(rotationRadius - viewer.getRotationRadius()) >= 0.1)
            isChange = true;
        }
      }
    }
    if (zoom == 0 || Float.isNaN(zoom))
      zoom = 100;
    if (Float.isNaN(rotationRadius))
      rotationRadius = 0;

    if (!isChange && Math.abs(zoom - zoom0) >= 1)
      isChange = true;
    

    Point3f navCenter = null;
    float xNav = Float.NaN;
    float yNav = Float.NaN;
    float navDepth = Float.NaN;

    if (i != statementLength) {
      navCenter = centerParameter(i);
      i = iToken + 1;
      if (i != statementLength) {
        xNav = floatParameter(i++);
        yNav = floatParameter(i++);
      }
      if (i != statementLength)
        navDepth = floatParameter(i++);
    }

    if (i != statementLength)
      error(ERROR_badArgumentCount);

    if (isSyntaxCheck)
      return;
    if (!isChange)
      floatSecondsTotal = 0;
    if (floatSecondsTotal > 0)
      refresh();
    viewer.moveTo(floatSecondsTotal, center, axis, degrees, null, zoom, xTrans, yTrans,
        rotationRadius, navCenter, xNav, yNav, navDepth);
  }

  private void navigate() throws ScriptException {
    
    if (statementLength == 1) {
      setBooleanProperty("navigationMode", true);
      return;
    }
    Vector3f rotAxis = new Vector3f(0, 1, 0);
    Point3f pt;
    if (statementLength == 2) {
      switch (getToken(1).tok) {
      case Token.on:
      case Token.off:
        if (isSyntaxCheck)
          return;
        viewer.setObjectMad(JmolConstants.SHAPE_AXES, "axes", 1);
        setShapeProperty(JmolConstants.SHAPE_AXES, "position", new Point3f(50, 50, Float.MAX_VALUE));
        setBooleanProperty("navigationMode", true);
        viewer.setNavOn(theTok == Token.on);
        return;
      case Token.stop:
        if (!isSyntaxCheck)
          viewer.setNavXYZ(0, 0, 0);
        return;
      case Token.point3f:
        break;
      default:
        error(ERROR_invalidArgument);
      }
    }
    if (!viewer.getNavigationMode())
      setBooleanProperty("navigationMode", true);
    for (int i = 1; i < statementLength; i++) {
      float timeSec = (isFloatParameter(i) ? floatParameter(i++) : 2f);
      if (timeSec < 0)
        error(ERROR_invalidArgument);
      if (!isSyntaxCheck && timeSec > 0)
        refresh();
      switch (getToken(i).tok) {
      case Token.point3f:
      case Token.leftbrace:
        
        pt = getPoint3f(i, true);
        iToken++;
        if (iToken != statementLength)
          error(ERROR_invalidArgument);
        if (isSyntaxCheck)
          return;
        viewer.setNavXYZ(pt.x, pt.y, pt.z);
        return;
      case Token.depth:
        float depth = floatParameter(++i);
        if (!isSyntaxCheck)
          viewer.setNavigationDepthPercent(timeSec, depth);
        continue;
      case Token.center:
        pt = centerParameter(++i);
        i = iToken;
        if (!isSyntaxCheck)
          viewer.navigate(timeSec, pt);
        continue;
      case Token.rotate:
        switch (getToken(++i).tok) {
        case Token.identifier:
          String str = parameterAsString(i++);
          if (str.equalsIgnoreCase("x")) {
            rotAxis.set(1, 0, 0);
            break;
          }
          if (str.equalsIgnoreCase("y")) {
            rotAxis.set(0, 1, 0);
            break;
          }
          if (str.equalsIgnoreCase("z")) {
            rotAxis.set(0, 0, 1);
            break;
          }
          error(ERROR_invalidArgument); 
          break;
        case Token.point3f:
        case Token.leftbrace:
          rotAxis.set(getPoint3f(i, true));
          i = iToken + 1;
          break;
        }
        float degrees = floatParameter(i);
        if (!isSyntaxCheck)
          viewer.navigate(timeSec, rotAxis, degrees);
        continue;
      case Token.translate:
        float x = Float.NaN;
        float y = Float.NaN;
        if (isFloatParameter(++i)) {
          x = floatParameter(i);
          y = floatParameter(++i);
        } else if (getToken(i).tok == Token.identifier) {
          String str = parameterAsString(i);
          if (str.equalsIgnoreCase("x"))
            x = floatParameter(++i);
          else if (str.equalsIgnoreCase("y"))
            y = floatParameter(++i);
          else
            error(ERROR_invalidArgument);
        } else {
          pt = centerParameter(i);
          i = iToken;
          if (!isSyntaxCheck)
            viewer.navTranslate(timeSec, pt);
          continue;
        }
        if (!isSyntaxCheck)
          viewer.navTranslatePercent(timeSec, x, y);
        continue;
      case Token.divide:
        continue;
      case Token.trace:
        Point3f[][] pathGuide;
        Vector vp = new Vector();
        BitSet bs = expression(++i);
        i = iToken;
        if (isSyntaxCheck)
          return;
        viewer.getPolymerPointsAndVectors(bs, vp);
        int n;
        if ((n = vp.size()) > 0) {
          pathGuide = new Point3f[n][];
          for (int j = 0; j < n; j++) {
            pathGuide[j] = (Point3f[]) vp.get(j);
          }
          viewer.navigate(timeSec, pathGuide);
          continue;
        }
        break;
      case Token.surface:
        if (i != 1)
          error(ERROR_invalidArgument);
        if (isSyntaxCheck)
          return;
        viewer.navigateSurface(timeSec, optParameterAsString(2));
        continue;
      case Token.path:
        Point3f[] path;
        float[] theta = null; 
        if (getToken(i + 1).tok == Token.dollarsign) {
          i++;
          
          String pathID = objectNameParameter(++i);
          if (isSyntaxCheck)
            return;
          setShapeProperty(JmolConstants.SHAPE_DRAW, "thisID", pathID);
          path = (Point3f[]) viewer.getShapeProperty(
              JmolConstants.SHAPE_DRAW, "vertices");
          refresh();
          if (path == null)
            error(ERROR_invalidArgument);
          int indexStart = (int) (isFloatParameter(i + 1) ? floatParameter(++i)
              : 0);
          int indexEnd = (int) (isFloatParameter(i + 1) ? floatParameter(++i)
              : Integer.MAX_VALUE);
          if (!isSyntaxCheck)
            viewer.navigate(timeSec, path, theta, indexStart, indexEnd);
          continue;
        }
        Vector v = new Vector();
        while (isCenterParameter(i + 1)) {
          v.addElement(centerParameter(++i));
          i = iToken;
        }
        if (v.size() > 0) {
          path = new Point3f[v.size()];
          for (int j = 0; j < v.size(); j++) {
            path[j] = (Point3f) v.get(j);
          }
          if (!isSyntaxCheck)
            viewer.navigate(timeSec, path, theta, 0, Integer.MAX_VALUE);
          continue;
        }
        
      default:
        error(ERROR_invalidArgument);
      }
    }
  }

  private void bondorder() throws ScriptException {
    checkLength(-3);
    int order = 0;
    switch (getToken(1).tok) {
    case Token.integer:
    case Token.decimal:
      if ((order = JmolConstants.getBondOrderFromFloat(floatParameter(1))) == JmolConstants.BOND_ORDER_NULL)
        error(ERROR_invalidArgument);
      break;
    default:
      if ((order = JmolConstants.getBondOrderFromString(parameterAsString(1))) == JmolConstants.BOND_ORDER_NULL)
        error(ERROR_invalidArgument);
      
      if (order == JmolConstants.BOND_PARTIAL01 && tokAt(2) == Token.decimal) {
        order = JmolConstants
            .getPartialBondOrderFromInteger(statement[2].intValue);
      }
    }
    setShapeProperty(JmolConstants.SHAPE_STICKS, "bondOrder", new Integer(order));
  }

  private void console() throws ScriptException {
    switch (getToken(1).tok) {
    case Token.off:
      if (!isSyntaxCheck)
        viewer.showConsole(false);
      break;
    case Token.on:
      if (isSyntaxCheck)
        break;
      viewer.showConsole(true);
      viewer.clearConsole();
      break;
    default:
      error(ERROR_invalidArgument);
    }
  }

  private void centerAt() throws ScriptException {
    String relativeTo = null;
    switch (getToken(1).tok) {
    case Token.absolute:
      relativeTo = "absolute";
      break;
    case Token.average:
      relativeTo = "average";
      break;
    case Token.boundbox:
      relativeTo = "boundbox";
      break;
    default:
      error(ERROR_invalidArgument);
    }
    Point3f pt = new Point3f(0, 0, 0);
    if (statementLength == 5) {
      
      pt.x = floatParameter(2);
      pt.y = floatParameter(3);
      pt.z = floatParameter(4);
    } else if (isCenterParameter(2)) {
      pt = centerParameter(2);
      checkLast(iToken);
    } else {
      checkLength(2);
    }
    if (!isSyntaxCheck)
      viewer.setCenterAt(relativeTo, pt);
  }

  private void stereo() throws ScriptException {
    int stereoMode = JmolConstants.STEREO_DOUBLE;
    
    
    
    

    float degrees = JmolConstants.DEFAULT_STEREO_DEGREES;
    boolean degreesSeen = false;
    int[] colors = null;
    int colorpt = 0;
    for (int i = 1; i < statementLength; ++i) {
      if (isColorParam(i)) {
        if (colorpt > 1)
          error(ERROR_badArgumentCount);
        if (colorpt == 0)
          colors = new int[2];
        if (!degreesSeen)
          degrees = 3;
        colors[colorpt] = getArgbParam(i);
        if (colorpt++ == 0)
          colors[1] = ~colors[0];
        i = iToken;
        continue;
      }
      switch (getToken(i).tok) {
      case Token.on:
        checkLast(iToken = 1);
        iToken = 1;
        break;
      case Token.off:
        checkLast(iToken = 1);
        stereoMode = JmolConstants.STEREO_NONE;
        break;
      case Token.integer:
      case Token.decimal:
        degrees = floatParameter(i);
        degreesSeen = true;
        break;
      case Token.identifier:
        if (!degreesSeen)
          degrees = 3;
        stereoMode = JmolConstants.getStereoMode(parameterAsString(i));
        if (stereoMode != JmolConstants.STEREO_UNKNOWN)
          break;
        
      default:
        error(ERROR_invalidArgument);
      }
    }
    if (isSyntaxCheck)
      return;
    viewer.setStereoMode(colors, stereoMode, degrees);
  }

  private void connect(int index) throws ScriptException {

    final float[] distances = new float[2];
    BitSet[] atomSets = new BitSet[2];
    atomSets[0] = atomSets[1] = viewer.getSelectionSet();
    float radius = Float.NaN;
    int color = Integer.MIN_VALUE;
    int distanceCount = 0;
    int bondOrder = JmolConstants.BOND_ORDER_NULL;
    int bo;
    int operation = JmolConstants.CONNECT_MODIFY_OR_CREATE;
    boolean isDelete = false;
    boolean haveType = false;
    boolean haveOperation = false;
    String translucency = null;
    float translucentLevel = Float.MAX_VALUE;
    boolean isColorOrRadius = false;
    int nAtomSets = 0;
    int nDistances = 0;
    BitSet bsBonds = new BitSet();
    boolean isBonds = false;
    int expression2 = 0;
    int ptColor = 0;
    

    if (statementLength == 1) {
      viewer.rebond();
      return;
    }

    for (int i = index; i < statementLength; ++i) {
      switch (getToken(i).tok) {
      case Token.on:
      case Token.off:
        checkLength(2);
        if (!isSyntaxCheck)
          viewer.rebond();
        return;
      case Token.integer:
      case Token.decimal:
        if (nAtomSets > 0) {
          if (haveType || isColorOrRadius)
            error(ERROR_invalidParameterOrder);
          bo = JmolConstants.getBondOrderFromFloat(floatParameter(i));
          if (bo == JmolConstants.BOND_ORDER_NULL)
            error(ERROR_invalidArgument);
          bondOrder = bo;
          haveType = true;
          break;
        }
        if (++nDistances > 2)
          error(ERROR_badArgumentCount);
        distances[distanceCount++] = floatParameter(i);
        break;
      case Token.bitset:
      case Token.expressionBegin:
        if (nAtomSets > 2 || isBonds && nAtomSets > 0)
          error(ERROR_badArgumentCount);
        if (haveType || isColorOrRadius)
          error(ERROR_invalidParameterOrder);
        atomSets[nAtomSets++] = expression(i);
        isBonds = isBondSet;
        if (nAtomSets == 2) {
          int pt = iToken;
          for (int j = i; j < pt; j++)
            if (tokAt(j) == Token.identifier
                && parameterAsString(j).equals("_1")) {
              expression2 = i;
              break;
            }
          iToken = pt;
        }
        i = iToken;
        break;
      case Token.color:
        int tok = tokAt(i + 1);
        if (tok != Token.translucent && tok != Token.opaque)
          ptColor = i + 1;
        continue;
      case Token.translucent:
      case Token.opaque:
        if (translucency != null)
          error(ERROR_invalidArgument);
        isColorOrRadius = true;
        translucency = parameterAsString(i);
        if (theTok == Token.translucent && isFloatParameter(i + 1))
          translucentLevel = getTranslucentLevel(++i);
        ptColor = i + 1;
        break;
      case Token.pdb:
        boolean isAuto = (tokAt(2) == Token.auto);
        checkLength(isAuto ? 3 : 2);
        if (!isSyntaxCheck)
          viewer.setPdbConectBonding(isAuto);
        return;
      case Token.adjust:
      case Token.auto:
      case Token.create:
      case Token.modify:
      case Token.modifyorcreate:
        
        haveOperation = true;
        if (++i != statementLength)
          error(ERROR_invalidParameterOrder);
        operation = theTok;
        if (theTok == Token.auto
            && !(bondOrder == JmolConstants.BOND_ORDER_NULL
                || bondOrder == JmolConstants.BOND_H_REGULAR || bondOrder == JmolConstants.BOND_AROMATIC))
          error(ERROR_invalidArgument);
        break;
      case Token.struts:
        if (!isColorOrRadius) {
          color = 0xFFFFFF;
          translucency = "translucent";
          translucentLevel = 0.5f;
          radius = viewer.getStrutDefaultRadius();
          isColorOrRadius = true;
        }
        if (!haveOperation)
          operation = JmolConstants.CONNECT_MODIFY_OR_CREATE;
        haveOperation = true;
        
      case Token.identifier:
      case Token.aromatic:
      case Token.hbond:
        if (ptColor == i)
          break;
        
        if (isColorParam(i)) {
          ptColor = -i;
          break;
        }
        String cmd = parameterAsString(i);
        if ((bo = JmolConstants.getBondOrderFromString(cmd)) == JmolConstants.BOND_ORDER_NULL) {
          error(ERROR_invalidArgument);
        }
        
        if (haveType)
          error(ERROR_incompatibleArguments);
        haveType = true;
        if (bo == JmolConstants.BOND_PARTIAL01) {
          switch (tokAt(i + 1)) {
          case Token.decimal:
            bo = JmolConstants
                .getPartialBondOrderFromInteger(statement[++i].intValue);
            break;
          case Token.integer:
            bo = (short) intParameter(++i);
            break;
          }
        }
        bondOrder = bo;
        break;
      case Token.radius:
        radius = floatParameter(++i);
        isColorOrRadius = true;
        break;
      case Token.none:
      case Token.delete:
        if (++i != statementLength)
          error(ERROR_invalidParameterOrder);
        operation = JmolConstants.CONNECT_DELETE_BONDS;
        
        
        isDelete = true;
        isColorOrRadius = false;
        break;
      default:
        ptColor = i;
        break;
      }
      
      if (ptColor == -i || ptColor == i && isColorParam(i)) {
        color = getArgbParam(i);
        i = iToken;
        isColorOrRadius = true;
      } else if (ptColor == i) {
        error(ERROR_invalidArgument);
      }
    }
    if (isSyntaxCheck)
      return;
    if (distanceCount < 2) {
      if (distanceCount == 0)
        distances[0] = JmolConstants.DEFAULT_MAX_CONNECT_DISTANCE;
      distances[1] = distances[0];
      distances[0] = JmolConstants.DEFAULT_MIN_CONNECT_DISTANCE;
    }
    if (translucency != null || !Float.isNaN(radius)
        || color != Integer.MIN_VALUE) {
      if (!haveType)
        bondOrder = JmolConstants.BOND_ORDER_ANY;
      if (!haveOperation)
        operation = JmolConstants.CONNECT_MODIFY_ONLY;
    }
    int nNew = 0;
    int nModified = 0;
    int[] result;
    if (expression2 > 0) {
      BitSet bs = new BitSet();
      definedAtomSets.put("_1", bs);
      BitSet bs0 = atomSets[0];
      for (int atom1 = bs0.nextSetBit(0); atom1 >= 0; atom1 = bs0
          .nextSetBit(atom1 + 1)) {
        bs.set(atom1);
        result = viewer.makeConnections(distances[0], distances[1], bondOrder,
            operation, bs, expression(expression2), bsBonds, isBonds);
        nNew += result[0];
        nModified += result[1];
        bs.clear(atom1);
      }
    } else {
      result = viewer.makeConnections(distances[0], distances[1], bondOrder,
          operation, atomSets[0], atomSets[1], bsBonds, isBonds);
      nNew += result[0];
      nModified += result[1];
    }
    if (isDelete) {
      if (!(tQuiet || scriptLevel > scriptReportingLevel))
        scriptStatusOrBuffer(GT._("{0} connections deleted", nModified));
      return;
    }
    if (isColorOrRadius) {
      viewer.selectBonds(bsBonds);
      if (!Float.isNaN(radius))
        viewer.setShapeSize(JmolConstants.SHAPE_STICKS, (int) (radius * 2000),
            bsBonds);
      if (color != Integer.MIN_VALUE)
        viewer.setShapeProperty(JmolConstants.SHAPE_STICKS, "color",
            new Integer(color), bsBonds);
      if (translucency != null) {
        if (translucentLevel == Float.MAX_VALUE)
          translucentLevel = viewer.getDefaultTranslucent();
        viewer.setShapeProperty(JmolConstants.SHAPE_STICKS, "translucentLevel",
            new Float(translucentLevel));
        viewer.setShapeProperty(JmolConstants.SHAPE_STICKS, "translucency",
            translucency, bsBonds);
      }
    }
    if (!(tQuiet || scriptLevel > scriptReportingLevel))
      scriptStatusOrBuffer(GT._("{0} new bonds; {1} modified", new Object[] {
          new Integer(nNew), new Integer(nModified) }));
  }

  private float getTranslucentLevel(int i) throws ScriptException {
    float f = floatParameter(i);
    return (theTok == Token.integer && f > 0 && f < 9 ? f + 1 : f);
  }

  private void getProperty() throws ScriptException {
    if (isSyntaxCheck)
      return;
    String retValue = "";
    String property = optParameterAsString(1);
    String name = property;
    if (name.indexOf(".") >= 0)
      name = name.substring(0, name.indexOf("."));
    if (name.indexOf("[") >= 0)
      name = name.substring(0, name.indexOf("["));
    int propertyID = PropertyManager.getPropertyNumber(name);
    String param = optParameterAsString(2);
    int tok = tokAt(2);
    BitSet bs = (tok == Token.expressionBegin || tok == Token.bitset ? expression(2)
        : null);
    if (property.length() > 0 && propertyID < 0) {
      
      property = ""; 
      param = "";
    } else if (propertyID >= 0 && statementLength < 3) {
      param = PropertyManager.getDefaultParam(propertyID);
      if (param.equals("(visible)")) {
        viewer.setModelVisibility();
        bs = viewer.getVisibleSet();
      }
    } else if (propertyID == PropertyManager.PROP_FILECONTENTS_PATH) {
      for (int i = 3; i < statementLength; i++)
        param += parameterAsString(i);
    }
    retValue = (String) viewer.getProperty("readable", property,
       (bs == null ? (Object) param : (Object) bs));
    showString(retValue);
  }

  private void background(int i) throws ScriptException {
    getToken(i);
    int argb;
    if (theTok == Token.image) {
      
      String file = parameterAsString(checkLast(++i));
      if (isSyntaxCheck)
        return;
      Hashtable htParams = new Hashtable();
      Object image = null;
      if (!file.equalsIgnoreCase("none") && file.length() > 0)
        image = viewer.getFileAsImage(file, htParams);
      if (image instanceof String)
        evalError((String) image, null);
      viewer.setBackgroundImage((String) htParams.get("fullPathName"),
          (Image) image);
      return;
    }
    if (isColorParam(i) || theTok == Token.none) {
      argb = getArgbParamLast(i, true);
      if (isSyntaxCheck)
        return;
      viewer.setObjectArgb("background", argb);
      viewer.setBackgroundImage(null, null);
      return;
    }
    int iShape = getShapeType(theTok);
    colorShape(iShape, i + 1, true);
  }

  private void center(int i) throws ScriptException {
    
    
    if (statementLength == 1) {
      viewer.setNewRotationCenter(null);
      return;
    }
    Point3f center = centerParameter(i);
    if (center == null)
      error(ERROR_invalidArgument);
    if (!isSyntaxCheck)
      viewer.setNewRotationCenter(center);
  }

  private String setObjectProperty() throws ScriptException {
    String s = "";
    String id = getShapeNameParameter(2);
    Object[] data = new Object[] { id, null };
    if (isSyntaxCheck)
      return "";
    int iTok = iToken;
    int tokCommand = tokAt(0);
    boolean isWild = TextFormat.isWild(id);
    for (int iShape = JmolConstants.SHAPE_DIPOLES;;) {
      if (iShape != JmolConstants.SHAPE_MO
          && viewer.getShapeProperty(iShape, "checkID", data)) {
        setShapeProperty(iShape, "thisID", id);
        switch (tokCommand) {
        case Token.delete:
          setShapeProperty(iShape, "delete", null);
          break;
        case Token.hide:
        case Token.display:
          setShapeProperty(iShape, "hidden",
              tokCommand == Token.display ? Boolean.FALSE : Boolean.TRUE);
          break;
        case Token.show:
          if (iShape == JmolConstants.SHAPE_ISOSURFACE && !isWild)
            return getIsosurfaceJvxl(false, JmolConstants.SHAPE_ISOSURFACE);
          else if (iShape == JmolConstants.SHAPE_PMESH && !isWild)
            return getIsosurfaceJvxl(true, JmolConstants.SHAPE_PMESH);
          s += (String) viewer.getShapeProperty(iShape, "command") + "\n";
        case Token.color:
          colorShape(iShape, iTok + 1, false);
          break;
        }
        if (!isWild)
          break;
      }
      if (iShape == JmolConstants.SHAPE_DIPOLES)
        iShape = JmolConstants.SHAPE_MAX_HAS_ID;
      if (--iShape < JmolConstants.SHAPE_MIN_HAS_ID)
        break;
    }
    return s;
  }

  private void color() throws ScriptException {
    int argb = 0;
    if (isColorParam(1)) {
      colorObject(Token.atoms, 1);
      return;
    }
    switch (getToken(1).tok) {
    case Token.dollarsign:
      setObjectProperty();
      return;
    case Token.none:
    case Token.spacefill:
    case Token.amino:
    case Token.chain:
    case Token.group:
    case Token.shapely:
    case Token.structure:
    case Token.temperature:
    case Token.fixedtemp:
    case Token.formalcharge:
    case Token.partialcharge:
    case Token.straightness:
    case Token.surfacedistance:
    case Token.vanderwaals:
    case Token.monomer:
    case Token.molecule:
    case Token.altloc:
    case Token.insertion:
    case Token.translucent:
    case Token.opaque:
    case Token.jmol:
    case Token.rasmol:
    case Token.symop:
    case Token.user:
    case Token.property:
      colorObject(Token.atoms, 1);
      return;
    case Token.string:
      String strColor = stringParameter(1);
      if (!isSyntaxCheck)
        viewer.setPropertyColorScheme(strColor, true);
      if (tokAt(2) == Token.range || tokAt(2) == Token.absolute) {
        float min = floatParameter(3);
        float max = floatParameter(4);
        if (!isSyntaxCheck)
          viewer.setCurrentColorRange(min, max);
      }
      return;
    case Token.range:
    case Token.absolute:
      float min = floatParameter(2);
      float max = floatParameter(checkLast(3));
      if (!isSyntaxCheck)
        viewer.setCurrentColorRange(min, max);
      return;
    case Token.background:
      argb = getArgbParamLast(2, true);
      if (!isSyntaxCheck)
        viewer.setObjectArgb("background", argb);
      return;
    case Token.bitset:
    case Token.expressionBegin:
      colorObject(Token.atoms, -1);
      return;
    case Token.rubberband:
      argb = getArgbParamLast(2, false);
      if (!isSyntaxCheck)
        viewer.setRubberbandArgb(argb);
      return;
    case Token.selectionhalos:
      int i = 2;
      if (tokAt(2) == Token.opaque)
        i++;
      argb = getArgbParamLast(i, true);
      if (isSyntaxCheck)
        return;
      viewer.loadShape(JmolConstants.SHAPE_HALOS);
      setShapeProperty(JmolConstants.SHAPE_HALOS, "argbSelection", new Integer(
          argb));
      return;
    case Token.axes:
    case Token.boundbox:
    case Token.unitcell:
    case Token.identifier:
    case Token.hydrogen:
      
      String str = parameterAsString(1);
      if (checkToken(2)) {
        switch (getToken(2).tok) {
        case Token.rasmol:
          argb = Token.rasmol;
          break;
        case Token.none:
        case Token.jmol:
          argb = Token.jmol;
          break;
        default:
          argb = getArgbParam(2);
        }
      }
      if (argb == 0)
        error(ERROR_colorOrPaletteRequired);
      checkLast(iToken);
      if (str.equalsIgnoreCase("axes")) {
        setStringProperty("axesColor", Escape.escapeColor(argb));
        return;
      } else if (StateManager.getObjectIdFromName(str) >= 0) {
        if (!isSyntaxCheck)
          viewer.setObjectArgb(str, argb);
        return;
      }
      if (changeElementColor(str, argb))
        return;
      error(ERROR_invalidArgument);
      break;
    case Token.isosurface:
      setShapeProperty(JmolConstants.SHAPE_ISOSURFACE, "thisID",
          JmolConstants.PREVIOUS_MESH_ID);
      
    default:
      colorObject(theTok, 2);
    }
  }

  private boolean changeElementColor(String str, int argb) {
    for (int i = JmolConstants.elementNumberMax; --i >= 0;) {
      if (str.equalsIgnoreCase(JmolConstants.elementNameFromNumber(i))) {
        if (!isSyntaxCheck)
          viewer.setElementArgb(i, argb);
        return true;
      }
    }
    for (int i = JmolConstants.altElementMax; --i >= 0;) {
      if (str.equalsIgnoreCase(JmolConstants.altElementNameFromIndex(i))) {
        if (!isSyntaxCheck)
          viewer.setElementArgb(JmolConstants.altElementNumberFromIndex(i),
              argb);
        return true;
      }
    }
    if (str.charAt(0) != '_')
      return false;
    for (int i = JmolConstants.elementNumberMax; --i >= 0;) {
      if (str.equalsIgnoreCase("_" + JmolConstants.elementSymbolFromNumber(i))) {
        if (!isSyntaxCheck)
          viewer.setElementArgb(i, argb);
        return true;
      }
    }
    for (int i = JmolConstants.altElementMax; --i >= JmolConstants.firstIsotope;) {
      if (str
          .equalsIgnoreCase("_" + JmolConstants.altElementSymbolFromIndex(i))) {
        if (!isSyntaxCheck)
          viewer.setElementArgb(JmolConstants.altElementNumberFromIndex(i),
              argb);
        return true;
      }
      if (str
          .equalsIgnoreCase("_" + JmolConstants.altIsotopeSymbolFromIndex(i))) {
        if (!isSyntaxCheck)
          viewer.setElementArgb(JmolConstants.altElementNumberFromIndex(i),
              argb);
        return true;
      }
    }
    return false;
  }

  private void colorObject(int tokObject, int index) throws ScriptException {
    colorShape(getShapeType(tokObject), index, false);
  }

  private void colorShape(int shapeType, int index, boolean isBackground)
      throws ScriptException {
    String translucency = null;
    Object colorvalue = null;
    BitSet bs = null;
    String prefix = "";
    boolean isColor = false;
    int typeMask = 0;
    float translucentLevel = Float.MAX_VALUE;
    if (index < 0) {
      bs = expression(-index);
      index = iToken + 1;
      if (isBondSet)
        shapeType = JmolConstants.SHAPE_STICKS;
    }
    if (isBackground)
      getToken(index);
    else if ((isBackground = (getToken(index).tok == Token.background)) == true)
      getToken(++index);
    if (isBackground)
      prefix = "bg";
    if (!isSyntaxCheck && shapeType == JmolConstants.SHAPE_MO && !mo(true))
      return;
    if (theTok == Token.translucent || theTok == Token.opaque) {
      translucency = parameterAsString(index++);
      if (theTok == Token.translucent && isFloatParameter(index))
        translucentLevel = getTranslucentLevel(index++);
    }
    int tok = 0;
    if (index < statementLength && tokAt(index) != Token.on
        && tokAt(index) != Token.off) {
      isColor = true;
      tok = getToken(index).tok;
      if (isColorParam(index)) {
        int argb = getArgbParam(index, false);
        colorvalue = (argb == 0 ? null : new Integer(argb));
        if (translucency == null && tokAt(index = iToken + 1) != Token.nada) {
          getToken(index);
          if (translucency == null
              && (theTok == Token.translucent || theTok == Token.opaque)) {
            translucency = parameterAsString(index);
            if (theTok == Token.translucent && isFloatParameter(index + 1))
              translucentLevel = getTranslucentLevel(++index);
          }
          
          
        }
      } else if (shapeType == JmolConstants.SHAPE_LCAOCARTOON) {
        iToken--; 
      } else {
        
        
        

        
        String name = parameterAsString(index).toLowerCase();
        boolean isByElement = (name.indexOf(ColorEncoder.BYELEMENT_PREFIX) == 0);
        boolean isColorIndex = (isByElement || name
            .indexOf(ColorEncoder.BYRESIDUE_PREFIX) == 0);
        byte pid = (isColorIndex || shapeType == JmolConstants.SHAPE_ISOSURFACE ? JmolConstants.PALETTE_PROPERTY
            : tok == Token.spacefill ? JmolConstants.PALETTE_CPK
                : JmolConstants.getPaletteID(name));
        
        if (pid == JmolConstants.PALETTE_UNKNOWN
            || (pid == JmolConstants.PALETTE_TYPE || pid == JmolConstants.PALETTE_ENERGY)
            && shapeType != JmolConstants.SHAPE_HSTICKS)
          error(ERROR_invalidArgument);
        Object data = null;
        if (pid == JmolConstants.PALETTE_PROPERTY) {
          if (isColorIndex) {
            if (!isSyntaxCheck) {
              data = getBitsetProperty(null, (isByElement ? Token.elemno
                  : Token.groupid)
                  | Token.minmaxmask, null, null, null, null, false,
                  Integer.MAX_VALUE, false);
            }
          } else {
            if (!isColorIndex && shapeType != JmolConstants.SHAPE_ISOSURFACE)
              index++;
            if (name.equals("property")
                && Token.tokAttr((tok = getToken(index).tok),
                    Token.atomproperty)
                && !Token.tokAttr(tok, Token.strproperty)) {
              if (!isSyntaxCheck) {
                data = getBitsetProperty(null, getToken(index++).tok
                    | Token.minmaxmask, null, null, null, null, false,
                    Integer.MAX_VALUE, false);
              }
            }
          }
          if (data != null && !(data instanceof float[])) {
            if (data instanceof String[]) {
              float[] fdata = new float[((String[]) data).length];
              Parser.parseFloatArray((String[]) data, null, fdata);
              data = fdata;
            } else {
              error(ERROR_invalidArgument);
            }
          }
        } else if (pid == JmolConstants.PALETTE_VARIABLE) {
          index++;
          name = parameterAsString(index++);
          data = new float[viewer.getAtomCount()];
          Parser.parseFloatArray("" + getParameter(name, false), null,
              (float[]) data);
          pid = JmolConstants.PALETTE_PROPERTY;
        }
        if (pid == JmolConstants.PALETTE_PROPERTY) {
          String scheme = (tokAt(index) == Token.string ? parameterAsString(
              index++).toLowerCase() : null);
          if (scheme != null) {
            setStringProperty("propertyColorScheme", scheme);
            isColorIndex = (scheme.indexOf(ColorEncoder.BYELEMENT_PREFIX) == 0 || scheme
                .indexOf(ColorEncoder.BYRESIDUE_PREFIX) == 0);
          }
          float min = 0;
          float max = Float.MAX_VALUE;
          if (!isColorIndex
              && (tokAt(index) == Token.absolute || tokAt(index) == Token.range)) {
            min = floatParameter(index + 1);
            max = floatParameter(index + 2);
            index += 3;
            if (min == max && shapeType == JmolConstants.SHAPE_ISOSURFACE) {
              float[] range = (float[]) viewer.getShapeProperty(shapeType,
                  "dataRange");
              if (range != null) {
                min = range[0];
                max = range[1];
              }
            } else if (min == max)
              max = Float.MAX_VALUE;
          }
          if (!isSyntaxCheck) {
            if (shapeType != JmolConstants.SHAPE_ISOSURFACE
                && max != -Float.MAX_VALUE) {
              if (data == null)
                viewer.setCurrentColorRange(name);
              else
                viewer.setCurrentColorRange((float[]) data, null);
            }
            if (max != Float.MAX_VALUE)
              viewer.setCurrentColorRange(min, max);
          }
          if (shapeType == JmolConstants.SHAPE_ISOSURFACE)
            prefix = "remap";
        } else {
          index++;
        }
        colorvalue = new Byte((byte) pid);
        checkLength(index);
      }
    }
    if (isSyntaxCheck || shapeType < 0)
      return;
    typeMask = (shapeType == JmolConstants.SHAPE_STRUTS ? JmolConstants.BOND_STRUT
        : shapeType == JmolConstants.SHAPE_HSTICKS ? JmolConstants.BOND_HYDROGEN_MASK
        : shapeType == JmolConstants.SHAPE_SSSTICKS ? JmolConstants.BOND_SULFUR_MASK
            : shapeType == JmolConstants.SHAPE_STICKS ? JmolConstants.BOND_COVALENT_MASK
                : 0);
    if (typeMask == 0) {
      viewer.loadShape(shapeType);
      if (shapeType == JmolConstants.SHAPE_LABELS)
        setShapeProperty(JmolConstants.SHAPE_LABELS, "setDefaults", viewer
            .getNoneSelected());
    } else {
      if (bs != null) {
        viewer.selectBonds(bs);
        bs = null;
      }
      shapeType = JmolConstants.SHAPE_STICKS;
      setShapeProperty(shapeType, "type", new Integer(typeMask));
    }
    if (isColor) {
      
      
      
      switch (tok) {
      case Token.surfacedistance:
      case Token.straightness:
        viewer.autoCalculate(tok);
        break;
      case Token.temperature:
        if (viewer.isRangeSelected())
          viewer.clearBfactorRange();
        break;
      case Token.group:
        viewer.calcSelectedGroupsCount();
        break;
      case Token.monomer:
        viewer.calcSelectedMonomersCount();
        break;
      case Token.molecule:
        viewer.calcSelectedMoleculesCount();
        break;
      }
      if (bs == null)
        viewer.setShapeProperty(shapeType, prefix + "color", colorvalue);
      else
        viewer.setShapeProperty(shapeType, prefix + "color", colorvalue, bs);
    }
    if (translucency != null)
      setShapeTranslucency(shapeType, prefix, translucency, translucentLevel,
          bs);
    if (typeMask != 0)
      viewer.setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
          JmolConstants.BOND_COVALENT_MASK));
  }

  private void colorShape(int shapeType, int typeMask,int argb, String translucency, float translucentLevel, BitSet bs) {

    if (typeMask != 0) {
      setShapeProperty(shapeType = JmolConstants.SHAPE_STICKS, "type", new Integer(typeMask));
    }
    viewer.setShapeProperty(shapeType, "color", new Integer(argb), bs);
    if (translucency != null)
      setShapeTranslucency(shapeType, "", translucency, translucentLevel,
          bs);
    if (typeMask != 0)
      viewer.setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
          JmolConstants.BOND_COVALENT_MASK));
  }
  
  private void setShapeTranslucency(int shapeType, String prefix,
                                    String translucency,
                                    float translucentLevel, BitSet bs) {
    if (translucentLevel == Float.MAX_VALUE)
      translucentLevel = viewer.getDefaultTranslucent();
    setShapeProperty(shapeType, "translucentLevel", new Float(translucentLevel));
    if (prefix == null)
      return;
    if (bs == null)
      setShapeProperty(shapeType, prefix + "translucency", translucency);
    else if (!isSyntaxCheck)
      viewer.setShapeProperty(shapeType, prefix + "translucency", translucency,
          bs);
  }

  private void cd() throws ScriptException {
    if (isSyntaxCheck)
      return;
    String dir = (statementLength == 1 ? null : parameterAsString(1));
    showString(viewer.cd(dir));
  }

  private Object[] data;

  private void data() throws ScriptException {
    String dataString = null;
    String dataLabel = null;
    boolean isOneValue = false;
    int i;
    switch (iToken = statementLength) {
    case 5:
      
      dataString = parameterAsString(2);
      
    case 4:
    case 2:
      dataLabel = parameterAsString(1);
      if (dataLabel.equalsIgnoreCase("clear")) {
        if (!isSyntaxCheck)
          viewer.setData(null, null, 0, 0, 0, 0, 0);
        return;
      }
      if ((i = dataLabel.indexOf("@")) >= 0) {
        dataString = "" + getParameter(dataLabel.substring(i + 1), false);
        dataLabel = dataLabel.substring(0, i).trim();
      } else if (dataString == null && (i = dataLabel.indexOf(" ")) >= 0) {
        dataString = dataLabel.substring(i + 1).trim();
        dataLabel = dataLabel.substring(0, i).trim();
        isOneValue = true;
      }
      break;
    default:
      error(ERROR_badArgumentCount);
    }
    dataLabel = dataLabel.toLowerCase();
    String dataType = dataLabel + " ";
    dataType = dataType.substring(0, dataType.indexOf(" "));
    boolean isModel = dataType.equals("model");
    boolean isAppend = dataType.equals("append");
    boolean processModel = ((isModel || isAppend) && (!isSyntaxCheck || isCmdLine_C_Option));
    if ((isModel || isAppend) && dataString == null)
      error(ERROR_invalidArgument);
    int userType = -1;
    if (processModel) {
      
      char newLine = viewer.getInlineChar();
      if (dataString.length() > 0 && dataString.charAt(0) != newLine)
        newLine = '\0';
      int modelCount = viewer.getModelCount()
          - (viewer.getFileName().equals("zapped") ? 1 : 0);
      boolean appendNew = viewer.getAppendNew();
      viewer.loadInline(dataString, newLine, isAppend);
      if (isAppend && appendNew) {
        viewer.setAnimationRange(-1, -1);
        viewer.setCurrentModelIndex(modelCount);
      }
    }
    if (isSyntaxCheck && !processModel)
      return;
    data = new Object[3];
    if (dataType.equals("element_vdw")) {
      
      data[0] = dataType;
      data[1] = dataString.replace(';', '\n');
      int n = JmolConstants.elementNumberMax;
      int[] eArray = new int[n + 1];
      for (int ie = 1; ie <= n; ie++)
        eArray[ie] = ie;
      data[2] = eArray;
      viewer.setData("element_vdw", data, n, 0, 0, 0, 0);
      return;
    }
    if (dataType.indexOf("data2d_") == 0) {
      
      data[0] = dataLabel;
      data[1] = Parser.parseFloatArray2d(dataString);
      viewer.setData(dataLabel, data, 0, 0, 0, 0, 0);
      return;
    }
    if (dataType.indexOf("data3d_") == 0) {
      
      data[0] = dataLabel;
      data[1] = Parser.parseFloatArray3d(dataString);
      viewer.setData(dataLabel, data, 0, 0, 0, 0, 0);
      return;
    }
    String[] tokens = Parser.getTokens(dataLabel);
    if (dataType.indexOf("property_") == 0
        && !(tokens.length == 2 && tokens[1].equals("set"))) {
      BitSet bs = viewer.getSelectionSet();
      data[0] = dataType;
      int atomNumberField = (isOneValue ? 0 : ((Integer) viewer
          .getParameter("propertyAtomNumberField")).intValue());
      int atomNumberFieldColumnCount = (isOneValue ? 0 : ((Integer) viewer
          .getParameter("propertyAtomNumberColumnCount")).intValue());
      int propertyField = (isOneValue ? Integer.MIN_VALUE : ((Integer) viewer
          .getParameter("propertyDataField")).intValue());
      int propertyFieldColumnCount = (isOneValue ? 0 : ((Integer) viewer
          .getParameter("propertyDataColumnCount")).intValue());
      if (!isOneValue && dataLabel.indexOf(" ") >= 0) {
        if (tokens.length == 3) {
          
          dataLabel = tokens[0];
          atomNumberField = Parser.parseInt(tokens[1]);
          propertyField = Parser.parseInt(tokens[2]);
        }
        if (tokens.length == 5) {
          
          
          dataLabel = tokens[0];
          atomNumberField = Parser.parseInt(tokens[1]);
          atomNumberFieldColumnCount = Parser.parseInt(tokens[2]);
          propertyField = Parser.parseInt(tokens[3]);
          propertyFieldColumnCount = Parser.parseInt(tokens[4]);
        }
      }
      if (atomNumberField < 0)
        atomNumberField = 0;
      if (propertyField < 0)
        propertyField = 0;
      int atomCount = viewer.getAtomCount();
      int[] atomMap = null;
      BitSet bsTemp = new BitSet(atomCount);
      if (atomNumberField > 0) {
        atomMap = new int[atomCount + 2];
        for (int j = 0; j <= atomCount; j++)
          atomMap[j] = -1;
        for (int j = bs.nextSetBit(0); j >= 0; j = bs.nextSetBit(j + 1)) {
          int atomNo = viewer.getAtomNumber(j);
          if (atomNo > atomCount + 1 || atomNo < 0 || bsTemp.get(atomNo))
            continue;
          bsTemp.set(atomNo);
          atomMap[atomNo] = j;
        }
        data[2] = atomMap;
      } else {
        data[2] = BitSetUtil.copy(bs);
      }
      data[1] = dataString;
      viewer.setData(dataType, data, atomCount, atomNumberField,
          atomNumberFieldColumnCount, propertyField, propertyFieldColumnCount);
      return;
    }
    userType = AtomCollection.getUserSettableType(dataType);
    if (userType >= 0) {
      
      viewer.setAtomData(userType, dataType, dataString);
      return;
    }
    
    data[0] = dataLabel;
    data[1] = dataString;
    viewer.setData(dataType, data, 0, 0, 0, 0, 0);
  }

  private void define() throws ScriptException {
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    String setName = (String) getToken(1).value;
    BitSet bs = expression(2);
    if (isSyntaxCheck)
      return;
    boolean isDynamic = (setName.indexOf("dynamic_") == 0);
    if (isDynamic) {
      Token[] code = new Token[statementLength];
      for (int i = statementLength; --i >= 0;)
        code[i] = statement[i];
      definedAtomSets.put("!" + setName.substring(8), code);
      viewer.addStateScript(thisCommand, false, true);
    } else {
      definedAtomSets.put(setName, bs);
      setStringProperty("@" + setName, Escape.escape(bs));
    }
  }

  private void echo(int index, boolean isImage) throws ScriptException {
    if (isSyntaxCheck)
      return;
    String text = optParameterAsString(index);
    if (viewer.getEchoStateActive()) {
      if (isImage) {
        Hashtable htParams = new Hashtable();
        Object image = viewer.getFileAsImage(text, htParams);
        if (image instanceof String) {
          text = (String) image;
        } else {
          setShapeProperty(JmolConstants.SHAPE_ECHO, "text", htParams
              .get("fullPathName"));
          setShapeProperty(JmolConstants.SHAPE_ECHO, "image", image);
          text = null;
        }
      } else if (text.startsWith("\1") ) {
        
        text = text.substring(1);
        isImage = true;
      }
      if (text != null)
        setShapeProperty(JmolConstants.SHAPE_ECHO, "text", text);
    }
    if (!isImage && viewer.getRefreshing())
      showString(viewer.formatText(text));
  }

  private void message() throws ScriptException {
    String text = parameterAsString(checkLast(1));
    if (isSyntaxCheck)
      return;
    String s = viewer.formatText(text);
    if (outputBuffer == null)
      viewer.showMessage(s);
    if (!s.startsWith("_"))
      scriptStatusOrBuffer(s);
  }

  private void log() throws ScriptException {
    if (statementLength == 1)
      error(ERROR_badArgumentCount);
    if (isSyntaxCheck)
      return;
    String s = (String) parameterExpression(1, 0, "", false);
    if (tokAt(1) == Token.off)
      setStringProperty("logFile", "");
    else
      viewer.log(s);
  }

  private void print() throws ScriptException {
    if (statementLength == 1)
      error(ERROR_badArgumentCount);
    String s = (String) parameterExpression(1, 0, "", false);
    if (isSyntaxCheck)
      return;
    if (outputBuffer != null)
      outputBuffer.append(s).append('\n');
    else
      viewer.showString(s, true);
  }

  private boolean pause() throws ScriptException {
    if (isSyntaxCheck)
      return false;
    String msg = optParameterAsString(1);
    if (!viewer.getBooleanProperty("_useCommandThread")) {
      
      
    }
    if (viewer.autoExit || !viewer.haveDisplay)
      return false;
    if (scriptLevel == 0 && pc == aatoken.length - 1) {
      viewer.scriptStatus("nothing to pause: " + msg); 
      return false;
    }
    msg = (msg.length() == 0 ? ": RESUME to continue." 
        : ": " + viewer.formatText(msg));
    pauseExecution();
    viewer.scriptStatus("script execution paused" + msg, "script paused for RESUME");
    return true;
  }

  private void label(int index) throws ScriptException {
    if (isSyntaxCheck)
      return;
    viewer.loadShape(JmolConstants.SHAPE_LABELS);
    String strLabel = null;
    switch (getToken(index).tok) {
    case Token.on:
      strLabel = viewer.getStandardLabelFormat();
      break;
    case Token.off:
      break;
    case Token.hide:
    case Token.display:
      setShapeProperty(JmolConstants.SHAPE_LABELS, "display", 
          theTok == Token.display ? Boolean.TRUE : Boolean.FALSE);
      return;
    default:
      strLabel = parameterAsString(index);
    }
    viewer.setLabel(strLabel);
  }

  private void hover() throws ScriptException {
    if (isSyntaxCheck)
      return;
    String strLabel = parameterAsString(1);
    if (strLabel.equalsIgnoreCase("on"))
      strLabel = "%U";
    else if (strLabel.equalsIgnoreCase("off"))
      strLabel = null;
    viewer.setHoverLabel(strLabel);
  }

  private void load() throws ScriptException {
    boolean isAppend = false;
    Vector firstLastSteps = null;
    int modelCount = viewer.getModelCount()
        - (viewer.getFileName().equals("zapped") ? 1 : 0);
    boolean appendNew = viewer.getAppendNew();
    StringBuffer loadScript = new StringBuffer("load");
    int nFiles = 1;
    Hashtable htParams = new Hashtable();
    int i = 1;
    
    
    String modelName = null;
    String filename = null;
    String[] filenames = null;
    String[] tempFileInfo = null;
    int tokType = 0;
    int tok;
    boolean doLoadFiles = (!isSyntaxCheck || isCmdLine_C_Option);
    String errMsg = null;
    String sOptions = "";
    
    
    
    if (statementLength == 1) {
      i = 0;
    } else {
      modelName = parameterAsString(i);
      tok = tokAt(i);
      if (modelName.equalsIgnoreCase("data")) {
        modelName = stringParameter(++i);
        if (!isSyntaxCheck)
          htParams.put("parameterData", viewer.getFileAsString(modelName));
        loadScript.append(" data /*file*/").append(modelName).append(Escape.escape(modelName));
        tok = tokAt(++i);
        modelName = parameterAsString(i);
      } 
      if (tok == Token.identifier || modelName.equalsIgnoreCase("fileset")) {
        if (modelName.equals("menu")) {
          String m = parameterAsString(checkLast(2));
          if (!isSyntaxCheck)
            viewer.setMenu(m, true);
          return;
        }
        i++;
        loadScript.append(" " + modelName);
        isAppend = (modelName.equalsIgnoreCase("append"));
        tokType = (Parser.isOneOf(modelName.toLowerCase(),
            JmolConstants.LOAD_ATOM_DATA_TYPES) ? Token
            .getTokenFromName(modelName.toLowerCase()).tok : 0);
        if (tokType > 0) {
          
          
          htParams.put("atomDataOnly", Boolean.TRUE);
          htParams.put("modelNumber", new Integer(1));
          if (tokType == Token.vibration)
            tokType = Token.vibxyz;
          tempFileInfo = viewer.getFileInfo();
        }
        if (isAppend
            && ((filename = optParameterAsString(i))
                .equalsIgnoreCase("trajectory") || filename
                .equalsIgnoreCase("models"))) {
          modelName = filename;
          loadScript.append(" " + modelName);
          i++;
        }
        if (tokType > 0)
          isAppend = true;
        if (modelName.equalsIgnoreCase("trajectory")
            || modelName.equalsIgnoreCase("models")) {
          if (modelName.equalsIgnoreCase("trajectory"))
            htParams.put("isTrajectory", Boolean.TRUE);
          if (isPoint3f(i)) {
            Point3f pt = getPoint3f(i, false);
            i = iToken + 1;
            
            htParams.put("firstLastStep", new int[] { (int) pt.x, (int) pt.y,
                (int) pt.z });
            loadScript.append(" " + Escape.escape(pt));
          } else if (tokAt(i) == Token.bitset) {
            htParams.put("bsModels", (BitSet) getToken(i++).value);
          } else {
            htParams.put("firstLastStep", new int[] { 0, -1, 1 });
          }
        }
      } else {
        modelName = "fileset";
      }
      if (getToken(i).tok != Token.string)
        error(ERROR_filenameExpected);
    }
    
    
    
    
    int filePt = i;
    String localName = null;
    if (tokAt(filePt + 1) == Token.as) {
      if (scriptLevel != 0)
        error(ERROR_invalidArgument);
      localName = stringParameter(i + 2);
      i += 2;
    }
    if (statementLength == i + 1) {
      if (i == 0 || (filename = parameterAsString(filePt)).length() == 0)
        filename = viewer.getFullPathName();
      if (filename == null) {
        zap(false);
        return;
      }
      if (filename.indexOf("[]") >= 0)
        return;
    } else if (getToken(i + 1).tok == Token.leftbrace
        || theTok == Token.point3f || theTok == Token.integer
        || theTok == Token.manifest 
        || theTok == Token.packed
        || theTok == Token.filter && tokAt(i + 3) != Token.coord
        || theTok == Token.identifier && tokAt(i + 3) != Token.coord) {
      if ((filename = parameterAsString(filePt)).length() == 0)
        filename = viewer.getFullPathName();
      if (filePt == i)
        i++;
      if (filename == null) {
        zap(false);
        return;
      }
      if (filename.indexOf("[]") >= 0)
        return;
      if ((tok = tokAt(i)) == Token.manifest) {
        String manifest = stringParameter(++i);
        htParams.put("manifest", manifest);
        sOptions += " MANIFEST " + Escape.escape(manifest);
        tok = tokAt(++i);
      }
      if (tok == Token.integer) {
        int n = intParameter(i);
        sOptions += " " + n;
        if (n < 0)
          htParams.put("vibrationNumber", new Integer(-n));
        else
          htParams.put("modelNumber", new Integer(n));
        tok = tokAt(++i);
      }
      Point3f lattice = null;
      if (tok == Token.leftbrace || tok == Token.point3f) {
        lattice = getPoint3f(i, false);
        i = iToken + 1;
        tok = tokAt(i);
      }
      boolean isPacked = false;
      if (tok == Token.packed) {
        if (lattice == null)
          lattice = new Point3f(555, 555, -1);
        isPacked = true;
      }
      if (lattice != null) {
        i = iToken + 1;
        htParams.put("lattice", lattice);
        sOptions += " {" + (int) lattice.x + " " + (int) lattice.y + " "
            + (int) lattice.z + "}";
        if (isPacked) {
          htParams.put("packed", Boolean.TRUE);
          sOptions += " PACKED";
        }
        int iGroup = -1;
        float distance = 0;
        
        if (tokAt(i) == Token.range) {
          i++;
          distance = floatParameter(i++);
          sOptions += " range " + distance;
        }
        htParams.put("symmetryRange", new Float(distance));
        if (tokAt(i) == Token.spacegroup) {
          ++i;
          String spacegroup = TextFormat.simpleReplace(parameterAsString(i++),
              "''", "\"");
          sOptions += " spacegroup " + Escape.escape(spacegroup);
          if (spacegroup.equalsIgnoreCase("ignoreOperators")) {
            iGroup = -999;
          } else {
            if (spacegroup.indexOf(",") >= 0) 
              if ((lattice.x < 9 && lattice.y < 9 && lattice.z == 0))
                spacegroup += "#doNormalize=0";
            iGroup = viewer.getSymmetry().determineSpaceGroupIndex(spacegroup);
            if (iGroup == -1)
              iGroup = -2;
            htParams.put("spaceGroupName", spacegroup);
          }
          htParams.put("spaceGroupIndex", new Integer(iGroup));
        }
        if (tokAt(i) == Token.unitcell) {
          ++i;
          htParams.put("spaceGroupIndex", new Integer(iGroup));
          float[] fparams = floatParameterSet(i, 6, 6);
          i = iToken;
          sOptions += " unitcell {";
          for (int j = 0; j < 6; j++)
            sOptions += (j == 0 ? "" : " ") + fparams[j];
          sOptions += "}";
          htParams.put("unitcell", fparams);
        }
      }
      if (tokAt(i) == Token.filter) {
        String filter = stringParameter(++i);
        htParams.put("filter", filter);
        sOptions += " FILTER " + Escape.escape(filter);
      }
    } else {
      if (i != 2) {
        modelName = parameterAsString(i++);
        loadScript.append(" ").append(Escape.escape(modelName));
      }
      Point3f pt = null;
      BitSet bs = null;
      Vector fNames = new Vector();
      while (i < statementLength) {
        switch (tokAt(i)) {
        case Token.filter:
          String filter = stringParameter(++i);
          htParams.put("filter", filter);
          loadScript.append(" FILTER ").append(Escape.escape(filter));
          ++i;
          continue;
        case Token.coord:
          htParams.remove("isTrajectory");
          if (firstLastSteps == null) {
            firstLastSteps = new Vector();
            pt = new Point3f(0, -1, 1);
          }
          if (isPoint3f(++i)) {
            pt = getPoint3f(i, false);
            i = iToken + 1;
          } else if (tokAt(i) == Token.bitset) {
            bs = (BitSet) getToken(i).value;
            pt = null;
            i = iToken + 1;
          }
          break;
        case Token.identifier:
          error(ERROR_invalidArgument);
        }
        fNames.add(filename = parameterAsString(i++));
        if (pt != null) {
          firstLastSteps.addElement(new int[] { (int) pt.x, (int) pt.y,
              (int) pt.z });
          loadScript.append(" COORD " + Escape.escape(pt));
        } else if (bs != null) {
          firstLastSteps.addElement(bs);
          loadScript.append(" COORD " + Escape.escape(bs));
        }
        loadScript.append(" /*file*/").append(Escape.escape(filename));
      }
      if (firstLastSteps != null)
        htParams.put("firstLastSteps", firstLastSteps);
      nFiles = fNames.size();
      filenames = new String[nFiles];
      for (int j = 0; j < nFiles; j++)
        filenames[j] = (String) fNames.get(j);
      filename = modelName;
    }
    if (!doLoadFiles)
      return;
    if (filenames == null) {
      
      if (filename.startsWith("@") && filename.length() > 1) {
        htParams.put("fileData", getStringParameter(filename.substring(1),
            false));
        filename = "string";
      }
    }
    
    
    
    OutputStream os = null;
    if (localName != null) {
      os = viewer.getOutputStream(localName);
      if (os == null)
        Logger.error("Could not create output stream for " + localName);
      else
        htParams.put("OutputStream", os);
    }
    errMsg = viewer.loadModelFromFile(filename, filenames, isAppend, htParams,
        tokType);
    if (os != null)
      try {
        Logger.info(GT._("file {0} created", localName));
        os.close();
      } catch (IOException e) {
        Logger.error("error closing file " + e.getMessage());
      }
    if (tokType > 0) {
      
      
      viewer.setFileInfo(tempFileInfo);
      if (errMsg != null && !isCmdLine_c_or_C_Option)
        evalError(errMsg, null);
      return;
    }
    if (filenames == null) {
      
      loadScript.append(" ");
      if (!filename.equals("string") && !filename.equals("string[]"))
        loadScript.append("/*file*/");
      if (localName != null)
        localName = viewer.getFullPath(localName);
      loadScript.append(Escape.escape((localName != null 
          ? localName : (modelName = (String) htParams
          .get("fullPathName")))));
      loadScript.append(sOptions);
    }
    viewer.addLoadScript(loadScript.toString());
    if (errMsg != null && !isCmdLine_c_or_C_Option) {
      if (errMsg.indexOf("NOTE: file recognized as a script file:") == 0) {
        viewer.addLoadScript("-");
        errMsg = TextFormat.trim(errMsg, "\n");
        runScript("script \"" + errMsg.substring(40) + "\"");
        return;
      }
      evalError(errMsg, null);
    }
    if (isAppend && (appendNew || nFiles > 1)) {
      viewer.setAnimationRange(-1, -1);
      viewer.setCurrentModelIndex(modelCount);
    }
    if (logMessages)
      scriptStatusOrBuffer("Successfully loaded:" + modelName);
    String defaultScript = viewer.getDefaultLoadScript();
    String msg = "";
    if (defaultScript.length() > 0)
      msg += "\nUsing defaultLoadScript: " + defaultScript;
    String script = (String) viewer.getModelSetAuxiliaryInfo("jmolscript");
    if (script != null && viewer.getAllowEmbeddedScripts()) {
      msg += "\nAdding embedded #jmolscript: " + script;
      defaultScript += ";" + script;
      defaultScript = "allowEmbeddedScripts = false;" + defaultScript
          + ";allowEmbeddedScripts = true;";
    }
    if (msg.length() > 0)
      Logger.info(msg);
    if (defaultScript.length() > 0 && !isCmdLine_c_or_C_Option)
      
      runScript(defaultScript);
  }

  private String getFullPathName() throws ScriptException {
    String filename = (!isSyntaxCheck || isCmdLine_C_Option ? viewer
        .getFullPathName()
        : "test.xyz");
    if (filename == null)
      error(ERROR_invalidArgument);
    return filename;
  }

  private void dataFrame(int datatype) throws ScriptException {
    boolean isQuaternion = false;
    boolean isDraw = (tokAt(0) == Token.draw);
    int pt0 = (isDraw ? 1 : 0);
    boolean isDerivative = false;
    boolean isSecondDerivative = false;
    boolean isRamachandranRelative = false;
    int pt = statementLength - 1;
    String type = optParameterAsString(pt).toLowerCase();
    switch (datatype) {
    case JmolConstants.JMOL_DATA_RAMACHANDRAN:
      if (type.equalsIgnoreCase("draw")) {
        isDraw = true;
        type = optParameterAsString(--pt).toLowerCase();
      }
      isRamachandranRelative = (pt > pt0 && type.startsWith("r"));
      type = "ramachandran" + (isRamachandranRelative ? " r" : "")
          + (isDraw ? " draw" : "");
      break;
    case JmolConstants.JMOL_DATA_QUATERNION:
      isQuaternion = true;
      
      if (type.equalsIgnoreCase("draw")) {
        isDraw = true;
        type = optParameterAsString(--pt).toLowerCase();
      } 
      isDerivative = (type.startsWith("deriv") || type.startsWith("diff"));
      isSecondDerivative = (isDerivative && type.indexOf("2") > 0);
      if (isDerivative)
        pt--;
      if (type.equalsIgnoreCase("helix") || type.equalsIgnoreCase("axis")) {
        isDraw = true;
        isDerivative = true;
        pt = -1;
      }
      type = ((pt <= pt0 ? "" : optParameterAsString(pt)) + "w")
          .substring(0, 1);
      if (type.equals("a") || type.equals("r"))
        isDerivative = true;
      if (!Parser.isOneOf(type, "w;x;y;z;r;a")) 
        evalError("QUATERNION [w,x,y,z,a,r] [difference][2]", null);
      type = "quaternion " + type + (isDerivative ? " difference" : "")
          + (isSecondDerivative ? "2" : "") + (isDraw ? " draw" : "");
      break;
    }
    if (isSyntaxCheck) 
      return;
    
    int modelIndex = viewer.getCurrentModelIndex();
    if (modelIndex < 0)
      error(ERROR_multipleModelsNotOK, type);
    modelIndex = viewer.getJmolDataSourceFrame(modelIndex);
    if (isDraw) {
      runScript(viewer.getPdbData(modelIndex, type));
      return;
    }
    int ptDataFrame = viewer.getJmolDataFrameIndex(modelIndex, type);
    if (ptDataFrame > 0) {
      
      viewer.setCurrentModelIndex(ptDataFrame, true);
      
      
      
      
      return;
    }
    String[] savedFileInfo = viewer.getFileInfo();
    boolean oldAppendNew = viewer.getAppendNew();
    viewer.setAppendNew(true);
    String data = viewer.getPdbData(modelIndex, type);
    boolean isOK = (data != null && viewer.loadInline(data, true) == null);
    viewer.setAppendNew(oldAppendNew);
    viewer.setFileInfo(savedFileInfo);
    if (!isOK)
      return;
    StateScript ss = viewer.addStateScript(type, true, false);
    int modelCount = viewer.getModelCount();
    viewer.setJmolDataFrame(type, modelIndex, modelCount - 1);
    String script;
    switch (datatype) {
    case JmolConstants.JMOL_DATA_RAMACHANDRAN:
    default:
      viewer.setFrameTitle(modelCount - 1, type + " plot for model "
          + viewer.getModelNumberDotted(modelIndex));
      script = "frame 0.0; frame last; reset;"
          + "select visible; color structure; spacefill 3.0; wireframe 0;"
          + "draw ramaAxisX" + modelCount + " {200 0 0} {-200 0 0} \"phi\";"
          + "draw ramaAxisY" + modelCount + " {0 200 0} {0 -200 0} \"psi\";"
      
      
      ;
      break;
    case JmolConstants.JMOL_DATA_QUATERNION:
      viewer.setFrameTitle(modelCount - 1, type + " for model "
          + viewer.getModelNumberDotted(modelIndex));
      String color = (Graphics3D.getHexCode(viewer.getColixBackgroundContrast()));
      script = "frame 0.0; frame last; reset;"
          + "select visible; wireframe 0; " + "isosurface quatSphere"
          + modelCount
          + " resolution 1.0 color " + color + " sphere 10.0 mesh nofill frontonly translucent 0.8;"
          + "draw quatAxis" + modelCount
          + "X {10 0 0} {-10 0 0} color red \"x\";" + "draw quatAxis"
          + modelCount + "Y {0 10 0} {0 -10 0} color green \"y\";"
          + "draw quatAxis" + modelCount
          + "Z {0 0 10} {0 0 -10} color blue \"z\";" + "color structure;"
          + "draw quatCenter" + modelCount + "{0 0 0} scale 0.02";
      break;
    }
    runScript(script);
    ss.setModelIndex(viewer.getCurrentModelIndex());
    viewer.setRotationRadius(isQuaternion ? 12.5f : 260f, true);
    viewer.loadShape(JmolConstants.SHAPE_ECHO);
    showString("frame " + viewer.getModelNumberDotted(modelCount - 1)
        + " created: " + type);
  }

  private void measure() throws ScriptException {
    if (statementLength == 1) {
      viewer.hideMeasurements(false);
      return;
    }
    switch (statementLength) {
    case 2:
      switch (getToken(1).tok) {
      case Token.on:
        if (!isSyntaxCheck)
          viewer.hideMeasurements(false);
        return;
      case Token.off:
        if (!isSyntaxCheck)
          viewer.hideMeasurements(true);
        return;
      case Token.delete:
        if (!isSyntaxCheck)
          viewer.clearAllMeasurements();
        return;
      case Token.string:
        if (!isSyntaxCheck)
          viewer.setMeasurementFormats(stringParameter(1));
        return;
      }
      error(ERROR_keywordExpected, "ON, OFF, DELETE");
      break;
    case 3: 
      if (getToken(1).tok == Token.delete) {
        if (getToken(2).tok == Token.all) {
          if (!isSyntaxCheck)
            viewer.clearAllMeasurements();
        } else {
          int i = intParameter(2) - 1;
          if (!isSyntaxCheck)
            viewer.deleteMeasurement(i);
        }
        return;
      }
    }

    int nAtoms = 0;
    int expressionCount = 0;
    int modelIndex = -1;
    int atomIndex = -1;
    int ptFloat = -1;
    int[] countPlusIndexes = new int[5];
    float[] rangeMinMax = new float[] { Float.MAX_VALUE, Float.MAX_VALUE };
    boolean isAll = false;
    boolean isAllConnected = false;
    boolean isNotConnected = false;
    boolean isRange = true;
    int tokAction = Token.opToggle;
    String strFormat = null;
    Vector points = new Vector();
    BitSet bs = new BitSet();
    Object value = null;
    TickInfo tickInfo = null;
    for (int i = 1; i < statementLength; ++i) {
      switch (getToken(i).tok) {
      case Token.identifier:
        error(ERROR_keywordExpected, "ALL, ALLCONNECTED, DELETE");
      default:
        error(ERROR_expressionOrIntegerExpected);
      case Token.opNot:
        if (tokAt(i + 1) != Token.connected)
          error(ERROR_invalidArgument);
        i++;
        isNotConnected = true;
        break;
      case Token.connected:
      case Token.allconnected:
      case Token.all:
        isAllConnected = (theTok == Token.allconnected);
        atomIndex = -1;
        isAll = true;
        if (isAllConnected && isNotConnected)
          error(ERROR_invalidArgument);
        break;
      case Token.decimal:
        isAll = true;
        isRange = true;
        ptFloat = (ptFloat + 1) % 2;
        rangeMinMax[ptFloat] = floatParameter(i);
        break;
      case Token.delete:
        if (tokAction != Token.opToggle)
          error(ERROR_invalidArgument);
        tokAction = Token.delete;
        break;
      case Token.integer:
        int iParam = intParameter(i);
        if (isAll) {
          isRange = true; 
          ptFloat = (ptFloat + 1) % 2;
          rangeMinMax[ptFloat] = iParam;
        } else {
          atomIndex = viewer.getAtomIndexFromAtomNumber(iParam);
          if (!isSyntaxCheck && atomIndex < 0)
            return;
          if (value != null)
            error(ERROR_invalidArgument);
          if ((countPlusIndexes[0] = ++nAtoms) > 4)
            error(ERROR_badArgumentCount);
          countPlusIndexes[nAtoms] = atomIndex;
        }
        break;
      case Token.modelindex:
        modelIndex = intParameter(++i);
        break;
      case Token.off:
        if (tokAction != Token.opToggle)
          error(ERROR_invalidArgument);
        tokAction = Token.off;
        break;
      case Token.on:
        if (tokAction != Token.opToggle)
          error(ERROR_invalidArgument);
        tokAction = Token.on;
        break;
      case Token.range:
        isAll = true;
        isRange = true; 
        atomIndex = -1;
        break;
      case Token.string:
        
        strFormat = stringParameter(i);
        break;
      case Token.ticks:
        tickInfo = checkTicks(i);
        i = iToken;
        tokAction = Token.define;
        break;
      case Token.bitset:
      case Token.expressionBegin:
      case Token.leftbrace:
      case Token.point3f:
      case Token.dollarsign:
        if (atomIndex >= 0)
          error(ERROR_invalidArgument);
        expressionResult = Boolean.FALSE;
        value = centerParameter(i);
        if (expressionResult instanceof BitSet) {
          value = bs = (BitSet) expressionResult;
          if (!isSyntaxCheck && bs.length() == 0)
            return;
        }
        if (value instanceof Point3f) {
          Point3fi v = new Point3fi();
          v.set((Point3f) value);
          v.modelIndex = (short) modelIndex;
          value = v;
        }
        if ((nAtoms = ++expressionCount) > 4)
          error(ERROR_badArgumentCount);
        points.addElement(value);
        i = iToken;
        break;
      }
    }
    if (nAtoms < 2 && (tickInfo == null || nAtoms == 1))
      error(ERROR_badArgumentCount);
    if (strFormat != null && strFormat.indexOf(nAtoms + ":") != 0)
      strFormat = nAtoms + ":" + strFormat;
    if (isRange && rangeMinMax[1] < rangeMinMax[0]) {
      rangeMinMax[1] = rangeMinMax[0];
      rangeMinMax[0] = (rangeMinMax[1] == Float.MAX_VALUE ? Float.MAX_VALUE
          : -200F);
    }
    if (isSyntaxCheck)
      return;
    if (value != null || tickInfo != null) {
      if (value == null)
        tickInfo.id = "default";
      setShapeProperty(JmolConstants.SHAPE_MEASURES, "measure",
          new MeasurementData(points, tokAction, rangeMinMax, strFormat, null,
              tickInfo, isAllConnected, isNotConnected, isAll));
      return;
    }
    switch (tokAction) {
    case Token.delete:
      viewer.deleteMeasurement(countPlusIndexes);
      break;
    case Token.on:
      viewer.showMeasurement(countPlusIndexes, true);
      break;
    case Token.off:
      viewer.showMeasurement(countPlusIndexes, false);
      break;
    default:
      viewer.toggleMeasurement(countPlusIndexes, strFormat);
    }
  }

  private void refresh() {
    if (isSyntaxCheck)
      return;
    viewer.setTainted(true);
    viewer.requestRepaintAndWait();
  }

  private void reset() throws ScriptException {
    checkLength(-2);
    if (isSyntaxCheck)
      return;
    if (statementLength == 1) {
      viewer.reset();
      return;
    }
    
    switch (tokAt(1)) {
    case Token.function:
      viewer.clearFunctions();
      return;
    case Token.vanderwaals:
      viewer.setData("element_vdw", new Object[] { null, "" }, 0, 0, 0, 0, 0);
      return;
    case Token.aromatic:
      viewer.resetAromatic();
      return;
    }
    String var = parameterAsString(1);
    if (var.charAt(0) == '_')
      error(ERROR_invalidArgument);
    viewer.unsetProperty(var);
  }

  private void restrict() throws ScriptException {
    boolean isBond = (tokAt(1) == Token.bonds);
    select(isBond ? 2 : 1);
    if (isSyntaxCheck)
      return;
    restrictSelected(isBond, true);
  }

  private void restrictSelected(boolean isBond, boolean doInvert) {
    BitSet bsSelected = BitSetUtil.copy(viewer.getSelectionSet());
    if (doInvert)
      viewer.invertSelection();
    BitSet bsSubset = viewer.getSelectionSubset();
    if (doInvert && bsSubset != null) {
      bsSelected = BitSetUtil.copy(viewer.getSelectionSet());
      bsSelected.and(bsSubset);
      viewer.setSelectionSet(bsSelected);
      BitSetUtil.invertInPlace(bsSelected, viewer.getAtomCount());
      bsSelected.and(bsSubset);
    }
    BitSetUtil.andNot(bsSelected, viewer.getDeletedAtoms());
    boolean bondmode = viewer.getBondSelectionModeOr();
    
    if (!isBond)
      setBooleanProperty("bondModeOr", true);
    setShapeSize(JmolConstants.SHAPE_STICKS, 0);
    
    
    setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
        JmolConstants.BOND_STRUT));
    setShapeSize(JmolConstants.SHAPE_STICKS, 0);
    setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
        JmolConstants.BOND_COVALENT_MASK));
    
    for (int shapeType = JmolConstants.SHAPE_MAX_SIZE_ZERO_ON_RESTRICT; --shapeType >= 0;)
      if (shapeType != JmolConstants.SHAPE_MEASURES)
        setShapeSize(shapeType, 0);
    setShapeProperty(JmolConstants.SHAPE_POLYHEDRA, "delete", null);
    viewer.setLabel(null);

    if (!isBond)
      setBooleanProperty("bondModeOr", bondmode);
    viewer.setSelectionSet(bsSelected);
  }

  private void rotate(boolean isSpin, boolean isSelected)
      throws ScriptException {

    
    

    

    if (statementLength == 2)
      switch (getToken(1).tok) {
      case Token.on:
        if (!isSyntaxCheck)
          viewer.setSpinOn(true);
        return;
      case Token.off:
        if (!isSyntaxCheck)
          viewer.setSpinOn(false);
        return;
      }

    BitSet bsAtoms = null;
    float degrees = Float.MIN_VALUE;
    int nPoints = 0;
    float endDegrees = Float.MAX_VALUE;
    boolean isMolecular = false;
    Point3f[] points = new Point3f[2];
    Vector3f rotAxis = new Vector3f(0, 1, 0);
    int direction = 1;
    int tok;
    boolean axesOrientationRasmol = viewer.getAxesOrientationRasmol();
    for (int i = 1; i < statementLength; ++i) {
      switch (tok = getToken(i).tok) {
      case Token.spin:
        isSpin = true;
        continue;
      case Token.minus:
        direction = -1;
        continue;
      case Token.quaternion:
        i++;
        
      case Token.point4f:
        Quaternion q = new Quaternion(getPoint4f(i));
        rotAxis.set(q.getNormal());
        degrees = q.getTheta();
        break;
      case Token.axisangle:
        if (isPoint3f(++i)) {
          rotAxis.set(centerParameter(i));
          break;
        }
        Point4f p4 = getPoint4f(i);
        rotAxis.set(p4.x, p4.y, p4.z);
        degrees = p4.w;
        break;
      case Token.internal:
      case Token.molecular:
        isMolecular = true;
        continue;
      case Token.identifier:
        String str = parameterAsString(i);
        if (str.equalsIgnoreCase("x")) {
          rotAxis.set(direction, 0, 0);
          continue;
        }
        if (str.equalsIgnoreCase("y")) {
          rotAxis.set(0, (axesOrientationRasmol && !isMolecular ? -direction
              : direction), 0);
          continue;
        }
        if (str.equalsIgnoreCase("z")) {
          rotAxis.set(0, 0, direction);
          continue;
        }
        error(ERROR_invalidArgument);
      case Token.branch:
        int iAtom1 = expression(++i).nextSetBit(0);
        int iAtom2 = expression(++iToken).nextSetBit(0);
        if (iAtom1 < 0 || iAtom2 < 0)
          return;
        bsAtoms = viewer.getBranchBitSet(iAtom2, iAtom1);
        isMolecular = true;
        points[0] = viewer.getAtomPoint3f(iAtom1);
        points[1] = viewer.getAtomPoint3f(iAtom2);
        nPoints = 2;
        i = iToken;
        break;
      case Token.bitset:
      case Token.expressionBegin:
      case Token.leftbrace:
      case Token.point3f:
      case Token.dollarsign:
        if (nPoints == 2) 
          error(ERROR_tooManyPoints);
        
        
        Point3f pt1 = centerParameter(i);
        if (!isSyntaxCheck && tok == Token.dollarsign
            && tokAt(i + 2) != Token.leftsquare)
          rotAxis = getDrawObjectAxis(objectNameParameter(++i), Integer.MIN_VALUE);
        points[nPoints++] = pt1;
        break;
      case Token.comma:
        continue;
      case Token.integer:
      case Token.decimal:
        
        if (degrees == Float.MIN_VALUE)
          degrees = floatParameter(i);
        else {
          endDegrees = degrees;
          degrees = floatParameter(i);
          if (endDegrees * degrees < 0) {
            
            
            degrees = -endDegrees / degrees;
          }
          isSpin = true;
        }
        continue;
      default:
        error(ERROR_invalidArgument);
      }
      i = iToken;
    }
    if (isSyntaxCheck)
      return;
    if (degrees == Float.MIN_VALUE)
      degrees = 10;
    if (isSelected && bsAtoms == null)
      bsAtoms = viewer.getSelectionSet();
    if (nPoints < 2) {
      if (!isMolecular) {
        
        
        
        
        
        viewer.rotateAxisAngleAtCenter(points[0], rotAxis, degrees, endDegrees,
            isSpin, bsAtoms);
        return;
      }
      if (nPoints == 0)
        points[0] = new Point3f();
      
      
      
      
      points[1] = new Point3f(points[0]);
      points[1].add(rotAxis);
    }
    if (points[0].distance(points[1]) == 0) {
      points[1] = new Point3f(points[0]);
      points[1].y += 1.0;
    }
    viewer.rotateAboutPointsInternal(points[0], points[1], degrees, endDegrees,
        isSpin, bsAtoms);
  }

  private Point3f getObjectCenter(String axisID, int index) {
    Object[] data = new Object[] { axisID, new Integer(index), null };
    return (viewer
        .getShapeProperty(JmolConstants.SHAPE_DRAW, "getCenter", data)
        || viewer.getShapeProperty(JmolConstants.SHAPE_ISOSURFACE, "getCenter",
            data) ? (Point3f) data[2] : null);
  }

  private Point3f[] getObjectBoundingBox(String id) {
    Object[] data = new Object[] { id, null, null };
    return (viewer.getShapeProperty(JmolConstants.SHAPE_ISOSURFACE, "getBoundingBox",
            data) ? (Point3f[]) data[2] : null);
  }

  private Vector3f getDrawObjectAxis(String axisID, int index) {
    Object[] data = new Object[] { axisID, new Integer(index), null };
    return (viewer.getShapeProperty(JmolConstants.SHAPE_DRAW,
        "getSpinAxis", data) ? (Vector3f) data[2] : null);
  }

  private void script(int tok) throws ScriptException {
    boolean loadCheck = true;
    boolean isCheck = false;
    boolean doStep = false;
    int lineNumber = 0;
    int pc = 0;
    int lineEnd = 0;
    int pcEnd = 0;
    int i = 2;
    String theScript = parameterAsString(1);
    String filename = null;
    String localPath = null;
    String remotePath = null;
    String scriptPath = null;
    if (tok == Token.javascript) {
      checkLength(2);
      if (!isSyntaxCheck)
        viewer.jsEval(theScript);
      return;
    }
    tok = tokAt(1);
    if (tok != Token.string)
      error(ERROR_filenameExpected);
    filename = parameterAsString(1);
    if (filename.equalsIgnoreCase("applet")) {
      
      String appID = parameterAsString(2);
      theScript = parameterExpression(3, 0, "_script", false).toString();
      checkLast(iToken);
      if (isSyntaxCheck)
        return;
      if (appID.length() == 0 || appID.equals("all"))
        appID = "*";
      if (!appID.equals(".")) {
        viewer.jsEval(appID + "\1" + theScript);
        if (!appID.equals("*"))
          return;
      }
    } else {
      theScript = null;
      tok = tokAt(statementLength - 1);
      doStep = (tok == Token.step);
      if (filename.equalsIgnoreCase("inline")) {
        theScript = parameterExpression(2, (doStep ? statementLength - 1 : 0), "_script", false).toString();
        i = iToken + 1;
      }
      while (filename.equalsIgnoreCase("localPath")
          || filename.equalsIgnoreCase("remotePath")
          || filename.equalsIgnoreCase("scriptPath")) {
        if (filename.equalsIgnoreCase("localPath"))
          localPath = parameterAsString(i++);
        else if (filename.equalsIgnoreCase("scriptPath"))
          scriptPath = parameterAsString(i++);
        else
          remotePath = parameterAsString(i++);
        filename = parameterAsString(i++);
      }
      if ((tok = tokAt(i)) == Token.check) {
        isCheck = true;
        tok = tokAt(++i);        
      }
      if (tok == Token.noload) {
        loadCheck = false;
        tok = tokAt(++i);        
      }
      if (tok == Token.line || tok == Token.lines) {
        i++;
        lineEnd = lineNumber = Math.max(intParameter(i++), 0);
        if (checkToken(i)) {
          if (getToken(i).tok == Token.minus)
            lineEnd = (checkToken(++i) ? intParameter(i++) : 0);
          else
            lineEnd = -intParameter(i++);
          if (lineEnd <= 0)
            error(ERROR_invalidArgument);
        }
      } else if (tok == Token.command || tok == Token.commands) {
        i++;
        pc = Math.max(intParameter(i++) - 1, 0);
        pcEnd = pc + 1;
        if (checkToken(i)) {
          if (getToken(i).tok == Token.minus)
            pcEnd = (checkToken(++i) ? intParameter(i++) : 0);
          else
            pcEnd = -intParameter(i++);
          if (pcEnd <= 0)
            error(ERROR_invalidArgument);
        }
      }
      checkLength(doStep ? i + 1 : i);
    }
    if (isSyntaxCheck && !isCmdLine_c_or_C_Option)
      return;
    if (isCmdLine_c_or_C_Option)
      isCheck = true;
    boolean wasSyntaxCheck = isSyntaxCheck;
    boolean wasScriptCheck = isCmdLine_c_or_C_Option;
    if (isCheck)
      isSyntaxCheck = isCmdLine_c_or_C_Option = true;
    pushContext(null);
    contextPath += " >> " + filename;
    if (theScript == null ? compileScriptFileInternal(filename, localPath, remotePath, scriptPath) 
        : compileScript(null, theScript, false)) {
      this.pcEnd = pcEnd;
      this.lineEnd = lineEnd;
      while (pc < lineNumbers.length && lineNumbers[pc] < lineNumber)
        pc++;
      this.pc = pc;
      boolean saveLoadCheck = isCmdLine_C_Option;
      isCmdLine_C_Option &= loadCheck;
      executionStepping |= doStep;
      instructionDispatchLoop(isCheck);
      if (debugScript && viewer.getMessageStyleChime())
        viewer.scriptStatus("script <exiting>");
      isCmdLine_C_Option = saveLoadCheck;
      popContext();
    } else {
      Logger.error(GT._("script ERROR: ") + errorMessage);
      popContext();
      if (wasScriptCheck) {
        setErrorMessage(null);
      } else {
        evalError(null, null);
      }
    }

    isSyntaxCheck = wasSyntaxCheck;
    isCmdLine_c_or_C_Option = wasScriptCheck;
  }

  private void function() throws ScriptException {
    if (isSyntaxCheck && !isCmdLine_c_or_C_Option)
      return;
    String name = (String) getToken(0).value;
    if (!viewer.isFunction(name))
      error(ERROR_commandExpected);
    Vector params = (statementLength == 1 || statementLength == 3
        && tokAt(1) == Token.leftparen && tokAt(2) == Token.rightparen ? null
        : (Vector) parameterExpression(1, 0, null, true));
    if (isSyntaxCheck)
      return;
    pushContext(null);
    contextPath += " >> function " + name;
    loadFunction(name, params);
    instructionDispatchLoop(false);
    popContext();
  }

  private void sync() throws ScriptException {
    
    checkLength(-3);
    String text = "";
    String applet = "";
    switch (statementLength) {
    case 1:
      applet = "*";
      text = "ON";
      break;
    case 2:
      applet = parameterAsString(1);
      if (applet.indexOf("jmolApplet") == 0 || Parser.isOneOf(applet, "*;.;^")) {
        text = "ON";
        if (!isSyntaxCheck)
          viewer.syncScript(text, applet);
        applet = ".";
        break;
      }
      text = applet;
      applet = "*";
      break;
    case 3:
      applet = parameterAsString(1);
      text = (tokAt(2) == Token.stereo ? Viewer.SYNC_GRAPHICS_MESSAGE
          : parameterAsString(2));
      break;
    }
    if (isSyntaxCheck)
      return;
    viewer.syncScript(text, applet);
  }

  private void history(int pt) throws ScriptException {
    
    if (statementLength == 1) {
      
      showString(viewer.getSetHistory(Integer.MAX_VALUE));
      return;
    }
    if (pt == 2) {
      
      int n = intParameter(checkLast(2));
      if (n < 0)
        error(ERROR_invalidArgument);
      if (!isSyntaxCheck)
        viewer.getSetHistory(n == 0 ? 0 : -2 - n);
      return;
    }
    switch (getToken(checkLast(1)).tok) {
    
    case Token.on:
    case Token.clear:
      if (!isSyntaxCheck)
        viewer.getSetHistory(Integer.MIN_VALUE);
      return;
    case Token.off:
      if (!isSyntaxCheck)
        viewer.getSetHistory(0);
      break;
    default:
      error(ERROR_keywordExpected, "ON, OFF, CLEAR");
    }
  }

  private void display(boolean isDisplay) throws ScriptException {
    if (tokAt(1) == Token.dollarsign) {
      setObjectProperty();
      return;
    }
    BitSet bs = (statementLength == 1 ? null : expression(1));
    if (isSyntaxCheck)
      return;
    if (isDisplay)
      viewer.display(bs, tQuiet);
    else
      viewer.hide(bs, tQuiet);
  }

  private void delete() throws ScriptException {
    if (statementLength == 1) {
      zap(true);
      return;
    }
    if (tokAt(1) == Token.dollarsign) {
      setObjectProperty();
      return;
    }
    BitSet bs = expression(statement, 1, 0, true, false, true, false);
    if (isSyntaxCheck)
      return;
    int nDeleted = viewer.deleteAtoms(bs, false);
    if (!(tQuiet || scriptLevel > scriptReportingLevel))
      scriptStatusOrBuffer(GT._("{0} atoms deleted", nDeleted));
  }

  private void minimize() throws ScriptException {
    BitSet bsSelected = null;
    int steps = Integer.MAX_VALUE;
    float crit = 0;
    boolean addHydrogen = false;
    MinimizerInterface minimizer = viewer.getMinimizer(false);
    
    for (int i = 1; i < statementLength; i++)
      switch (getToken(i).tok) {
      case Token.clear:
        checkLength(2);
        if (isSyntaxCheck || minimizer == null)
          return;
        minimizer.setProperty("clear", null);
        return;
      case Token.constraint:
        if (i != 1)
          error(ERROR_invalidArgument);
        int n = 0;
        i++;
        float targetValue = 0;
        int[] aList = new int[5];
        if (tokAt(i) == Token.clear) {
          checkLength(2);
        } else {
          while (n < 4 && !isFloatParameter(i)) {
            aList[++n] = expression(i).nextSetBit(0);
            i = iToken + 1;
          }
          aList[0] = n;
          targetValue = floatParameter(checkLast(i));
        }
        if (!isSyntaxCheck)
          viewer.getMinimizer(true).setProperty("constraint",
              new Object[] { aList, new int[n], new Float(targetValue) });
        return;
      case Token.stop:
      case Token.cancel:
        checkLength(2);
        if (isSyntaxCheck || minimizer == null)
          return;
        minimizer.setProperty(parameterAsString(i), null);
        return;
      case Token.fix:
      case Token.fixed:
        if (i != 1)
          error(ERROR_invalidArgument);
        BitSet bsFixed = expression(++i);
        if (bsFixed.nextSetBit(0) < 0)
          bsFixed = null;
        checkLength(iToken + 1, 1);
        if (!isSyntaxCheck)
          viewer.getMinimizer(true).setProperty("fixed", bsFixed);
        return;
      case Token.energy:
        steps = 0;
        continue;
      case Token.addhydrogens:
        addHydrogen = true;
        continue;
      case Token.step:
      case Token.steps:
        steps = intParameter(++i);
        continue;
      case Token.criterion:
        crit = floatParameter(++i);
        continue;
      case Token.select:
        bsSelected = expression(++i);
        i = iToken;
        continue;
      default:
        error(ERROR_invalidArgument);
        break;
      }
    if (isSyntaxCheck)
      return;
    if (bsSelected == null)
      bsSelected = viewer.getModelAtomBitSet(viewer.getVisibleFramesBitSet().nextSetBit(0), false);
    try {
      viewer.getMinimizer(true).minimize(steps, crit, bsSelected, addHydrogen);
    } catch (Exception e) {
      evalError(e.getMessage(), null);
    }
  }

  private void select(int i) throws ScriptException {
    
    if (statementLength == 1) {
      viewer.select(null, tQuiet || scriptLevel > scriptReportingLevel);
      return;
    }
    if (statementLength == 2 && tokAt(1) == Token.only)
      return; 
    
    viewer.setNoneSelected(statementLength == 4 && tokAt(2) == Token.none);
    
    if (tokAt(2) == Token.bitset && getToken(2).value instanceof BondSet
        || getToken(2).tok == Token.bonds && getToken(3).tok == Token.bitset) {
      if (statementLength == iToken + 2) {
        if (!isSyntaxCheck)
          viewer.selectBonds((BitSet) theToken.value);
        return;
      }
      error(ERROR_invalidArgument);
    }
    if (getToken(2).tok == Token.measure) {
      if (statementLength == 5 && getToken(3).tok == Token.bitset) {
        if (!isSyntaxCheck)
          setShapeProperty(JmolConstants.SHAPE_MEASURES, "select",
              theToken.value);
        return;
      }
      error(ERROR_invalidArgument);
    }
    BitSet bs = null;
    if (getToken(1).intValue == 0) {
      Object v = tokenSetting(0).value;
      if (!(v instanceof BitSet))
        error(ERROR_invalidArgument);
      checkLast(iToken);
      bs = (BitSet) v;
    } else {
      bs = expression(i);
    }
    if (isSyntaxCheck)
      return;
    if (isBondSet) {
      viewer.selectBonds(bs);
    } else {
      viewer.select(bs, tQuiet || scriptLevel > scriptReportingLevel);
    }
  }

  private void subset() throws ScriptException {
    BitSet bs = (statementLength == 1 ? null : expression(-1));
    if (isSyntaxCheck)
      return;
    
    
    
    
    viewer.setSelectionSubset(bs);
    
    
    
  }

  private void invertSelected() throws ScriptException {
    
    
    
    Point3f pt = null;
    Point4f plane = null;
    if (statementLength == 1) {
      if (isSyntaxCheck)
        return;
      BitSet bs = viewer.getSelectionSet();
      pt = viewer.getAtomSetCenter(bs);
      viewer.invertSelected(pt, bs);
      return;
    }
    String type = parameterAsString(1);

    if (type.equalsIgnoreCase("point")) {
      pt = centerParameter(2);
    } else if (type.equalsIgnoreCase("plane")) {
      plane = planeParameter(2);
    } else if (type.equalsIgnoreCase("hkl")) {
      plane = hklParameter(2);
    }
    checkLength(iToken + 1, 1);
    if (plane == null && pt == null)
      error(ERROR_invalidArgument);
    if (isSyntaxCheck)
      return;
    viewer.invertSelected(pt, plane);
  }

  private void translateSelected() throws ScriptException {
    
    
    Point3f pt = getPoint3f(1, true);
    if (!isSyntaxCheck)
      viewer.setAtomCoordRelative(pt, null);
  }

  private void translate() throws ScriptException {
    
    
    
    
    if (isPoint3f(1)) {
      Point3f pt = getPoint3f(1, true);
      BitSet bs = (iToken + 1 < statementLength ? expression(++iToken) : null);
      checkLast(iToken);
      if (isSyntaxCheck)
        return;
      if (bs == null)
        viewer.setAtomCoordRelative(pt, null);
      else
        viewer.setAtomCoordRelative(pt, bs);
      return;
    }
    char type = (optParameterAsString(3).toLowerCase() + '\0').charAt(0);
    checkLength(type == '\0' ? 3 : 4);
    float percent = floatParameter(2);
    if (getToken(1).tok == Token.identifier) {
      char xyz = parameterAsString(1).toLowerCase().charAt(0);
      switch (xyz) {
      case 'x':
      case 'y':
      case 'z':
        if (isSyntaxCheck)
          return;
        viewer.translate(xyz, percent, type);
        return;
      }
    }
    error(ERROR_axisExpected);
  }

  private void zap(boolean isZapCommand) throws ScriptException {
    if (statementLength == 1 || !isZapCommand) {
      viewer.zap(true, isZapCommand && !isStateScript);
      refresh();
      return;
    }
    BitSet bs = expression(1);
    if (isSyntaxCheck)
      return;
    int nDeleted = viewer.deleteAtoms(bs, true);
    boolean isQuiet = (tQuiet || scriptLevel > scriptReportingLevel);
    if (!isQuiet)
      scriptStatusOrBuffer(GT._("{0} atoms deleted", nDeleted));
    viewer.select(null, isQuiet);
  }

  private void zoom(boolean isZoomTo) throws ScriptException {
    if (!isZoomTo) {
      
      
      int tok = (statementLength > 1 ? getToken(1).tok : Token.on);
      switch (tok) {
      case Token.in:
      case Token.out:
        break;
      case Token.on:
      case Token.off:
        if (statementLength > 2)
          error(ERROR_badArgumentCount);
        if (!isSyntaxCheck)
          setBooleanProperty("zoomEnabled", tok == Token.on);
        return;
      }
    }
    Point3f center = null;
    Point3f currentCenter = viewer.getRotationCenter();
    int i = 1;
    
    float time = (isZoomTo ? (isFloatParameter(i) ? floatParameter(i++) : 2f)
        : 0f);
    if (time < 0) {
      
      i--;
      time = 0;
    }
    
    int ptCenter = 0;
    BitSet bsCenter = null;
    if (isCenterParameter(i)) {
      ptCenter = i;
      center = centerParameter(i);
      if (expressionResult instanceof BitSet)
        bsCenter = (BitSet) expressionResult;
      i = iToken + 1;
    }

    
    boolean isSameAtom = false && (center != null && currentCenter
        .distance(center) < 0.1);
    
    
    float zoom = viewer.getZoomSetting();
    float newZoom = getZoom(i, bsCenter, zoom);
    i = iToken + 1;
    float xTrans = Float.NaN;
    float yTrans = Float.NaN;
    if (i != statementLength) {
      xTrans = floatParameter(i++);
      yTrans = floatParameter(i++);
    }
    if (i != statementLength)
      error(ERROR_invalidArgument);
    if (newZoom < 0) {
      newZoom = -newZoom; 
      if (isZoomTo) {
        
        if (statementLength == 1 || isSameAtom)
          newZoom *= 2;
        else if (center == null)
          newZoom /= 2;
      }
    }
    float max = viewer.getMaxZoomPercent();
    if (newZoom < 5 || newZoom > max)
      numberOutOfRange(5, max);
    if (!viewer.isWindowCentered()) {
      
      if (center != null) {
        BitSet bs = expression(ptCenter);
        if (!isSyntaxCheck)
          viewer.setCenterBitSet(bs, false);
      }
      center = viewer.getRotationCenter();
      if (Float.isNaN(xTrans))
        xTrans = viewer.getTranslationXPercent();
      if (Float.isNaN(yTrans))
        yTrans = viewer.getTranslationYPercent();
    }
    if (isSyntaxCheck)
      return;
    if (Float.isNaN(xTrans))
      xTrans = 0;
    if (Float.isNaN(yTrans))
      yTrans = 0;
    if (isSameAtom && Math.abs(zoom - newZoom) < 1)
      time = 0;
    viewer.moveTo(time, center, JmolConstants.center, Float.NaN, null, newZoom,
        xTrans, yTrans, Float.NaN, null, Float.NaN, Float.NaN, Float.NaN);
  }

  private float getZoom(int i, BitSet bs, float currentZoom)
      throws ScriptException {
    

    float zoom = (isFloatParameter(i) ? floatParameter(i++) : Float.NaN);
    if (zoom == 0 || currentZoom == 0) {
      
      if (bs == null)
        error(ERROR_invalidArgument);
      float r = viewer.calcRotationRadius(bs);
      currentZoom = viewer.getRotationRadius() / r * 100;
      zoom = Float.NaN;
    }
    if (zoom < 0) {
      
      zoom += currentZoom;
    } else if (Float.isNaN(zoom)) {
      
      
      int tok = tokAt(i);
      switch (tok) {
      case Token.out:
      case Token.in:
        zoom = currentZoom * (tok == Token.out ? 0.5f : 2f);
        i++;
        break;
      case Token.divide:
      case Token.times:
      case Token.plus:
        float value = floatParameter(++i);
        i++;
        switch (tok) {
        case Token.divide:
          zoom = currentZoom / value;
          break;
        case Token.times:
          zoom = currentZoom * value;
          break;
        case Token.plus:
          zoom = currentZoom + value;
          break;
        }
        break;
      default:
        
        zoom = (bs == null ? -currentZoom : currentZoom);
      }
    }
    iToken = i - 1;
    return zoom;
  }

  private void delay() throws ScriptException {
    long millis = 0;
    switch (getToken(1).tok) {
    case Token.on: 
      millis = 1;
      break;
    case Token.integer:
      millis = intParameter(1) * 1000;
      break;
    case Token.decimal:
      millis = (long) (floatParameter(1) * 1000);
      break;
    default:
      error(ERROR_numberExpected);
    }
    if (!isSyntaxCheck)
      delay(millis);
  }

  private void delay(long millis) {
    long timeBegin = System.currentTimeMillis();
    refresh();
    int delayMax;
    if (millis < 0)
      millis = -millis;
    else if ((delayMax = viewer.getDelayMaximum()) > 0 && millis > delayMax)
      millis = delayMax;
    millis -= System.currentTimeMillis() - timeBegin;
    int seconds = (int) millis / 1000;
    millis -= seconds * 1000;
    if (millis <= 0)
      millis = 1;
    while (seconds >= 0 && millis > 0 && !interruptExecution
        && currentThread == Thread.currentThread()) {
      viewer.popHoldRepaint("delay");
      try {
        Thread.sleep((seconds--) > 0 ? 1000 : millis);
      } catch (InterruptedException e) {
      }
      viewer.pushHoldRepaint("delay");
    }
  }

  private void slab(boolean isDepth) throws ScriptException {
    boolean TF = false;
    Point4f plane = null;
    String str;
    if (isCenterParameter(1) || tokAt(1) == Token.point4f)
      plane = planeParameter(1);
    else
      switch (getToken(1).tok) {
      case Token.integer:
        int percent = intParameter(checkLast(1));
        if (!isSyntaxCheck)
          if (isDepth)
            viewer.depthToPercent(percent);
          else
            viewer.slabToPercent(percent);
        return;
      case Token.on:
        checkLength(2);
        TF = true;
        
      case Token.off:
        checkLength(2);
        setBooleanProperty("slabEnabled", TF);
        return;
      case Token.reset:
        checkLength(2);
        if (isSyntaxCheck)
          return;
        viewer.slabReset();
        setBooleanProperty("slabEnabled", true);
        return;
      case Token.set:
        checkLength(2);
        if (isSyntaxCheck)
          return;
        viewer.setSlabDepthInternal(isDepth);
        setBooleanProperty("slabEnabled", true);
        return;
      case Token.minus:
        str = parameterAsString(2);
        if (str.equalsIgnoreCase("hkl"))
          plane = hklParameter(3);
        else if (str.equalsIgnoreCase("plane"))
          plane = planeParameter(3);
        if (plane == null)
          error(ERROR_invalidArgument);
        plane.scale(-1);
        break;
      case Token.plane:
        switch (getToken(2).tok) {
        case Token.none:
          break;
        default:
          plane = planeParameter(2);
        }
        break;
      case Token.hkl:
        plane = (getToken(2).tok == Token.none ? null : hklParameter(2));
        break;
      case Token.reference:
        
        return;
      default:
        error(ERROR_invalidArgument);
      }
    if (!isSyntaxCheck)
      viewer.slabInternal(plane, isDepth);
  }

  private void ellipsoid() throws ScriptException {
    int mad = 0;
    int i = 1;
    switch (getToken(1).tok) {
    case Token.on:
      mad = 50;
      break;
    case Token.off:
      break;
    case Token.integer:
      mad = intParameter(1);
      break;
    case Token.id:
    case Token.times:
    case Token.identifier:
      viewer.loadShape(JmolConstants.SHAPE_ELLIPSOIDS);
      if (theTok == Token.id)
        i++;
      setShapeId(JmolConstants.SHAPE_ELLIPSOIDS, i, false);
      i = iToken;
      while (++i < statementLength) {
        String key = parameterAsString(i);
        Object value = null;
        switch (tokAt(i)) {
        case Token.axes:
          Vector3f[] axes = new Vector3f[3];
          for (int j = 0; j < 3; j++) {
            axes[j] = new Vector3f();
            axes[j].set(centerParameter(++i));
            i = iToken;
          }
          value = axes;
          break;
        case Token.center:
          value = centerParameter(++i);
          i = iToken;
          break;
        case Token.color:
          float translucentLevel = Float.NaN;
          i++;
          if ((theTok = tokAt(i)) == Token.translucent) {
            value = "translucent";
            if (isFloatParameter(++i))
              translucentLevel = getTranslucentLevel(i++);
            else
              translucentLevel = viewer.getDefaultTranslucent();
          } else if (theTok == Token.opaque) {
            value = "opaque";
            i++;
          }
          if (isColorParam(i)) {
            setShapeProperty(JmolConstants.SHAPE_ELLIPSOIDS, "color",
                new Integer(getArgbParam(i)));
            i = iToken;
          }
          if (value == null)
            continue;
          if (!Float.isNaN(translucentLevel))
            setShapeProperty(JmolConstants.SHAPE_ELLIPSOIDS,
                "translucentLevel", new Float(translucentLevel));
          key = "translucency";
          break;
        case Token.delete:
          value = Boolean.TRUE;
          checkLength(3);
          break;
        case Token.modelindex:
          value = new Integer(intParameter(++i));
          break;
        case Token.on:
          value = Boolean.TRUE;
          break;
        case Token.off:
          key = "on";
          value = Boolean.FALSE;
          break;
        case Token.scale:
          value = new Float(floatParameter(++i));
          break;
        }
        if (value == null)
          error(ERROR_invalidArgument);
        setShapeProperty(JmolConstants.SHAPE_ELLIPSOIDS, key.toLowerCase(),
            value);
      }
      setShapeProperty(JmolConstants.SHAPE_ELLIPSOIDS, "thisID", null);
      return;
    default:
      error(ERROR_invalidArgument);
    }
    setShapeSize(JmolConstants.SHAPE_ELLIPSOIDS, mad);
  }

  private String getShapeNameParameter(int i) throws ScriptException {
    String id = parameterAsString(i);
    boolean isWild = id.equals("*");
    if (id.length() == 0)
      error(ERROR_invalidArgument);
    if (isWild) {
      switch (tokAt(i + 1)) {
      case Token.nada:
      case Token.on:
      case Token.off:
      case Token.displayed:
      case Token.hidden:
      case Token.color:
      case Token.delete:
        break;
      default:
        id += optParameterAsString(++i);
      }
    }
    if (tokAt(i + 1) == Token.times)
      id += parameterAsString(++i);
    iToken = i;
    return id;
  }

  private String setShapeId(int iShape, int i, boolean idSeen)
      throws ScriptException {
    if (idSeen)
      error(ERROR_invalidArgument);
    String name = getShapeNameParameter(i).toLowerCase();
    setShapeProperty(iShape, "thisID", name);
    return name;
  }

  private void setAtomShapeSize(int shape, float scale) throws ScriptException {
    
    RadiusData rd = null;
    int tok = tokAt(1);
    switch (tok) {
    case Token.only:
      restrictSelected(false, false);
      break;
    case Token.on:
      break;
    case Token.off:
      scale = 0;
      break;
    default:
      rd = encodeRadiusParameter(1); 
      if (Float.isNaN(rd.value))
        error(ERROR_invalidArgument);
    }
    if (rd == null)
      rd = new RadiusData(scale, RadiusData.TYPE_FACTOR, JmolConstants.VDW_AUTO);
    setShapeSize(shape, rd);
  }

  

  private RadiusData encodeRadiusParameter(int index) throws ScriptException {

    float value = Float.NaN;
    int type = RadiusData.TYPE_ABSOLUTE;
    int vdwType = 0;

    int tok = getToken(index).tok;
    switch (tok) {
    case Token.adpmax:
    case Token.adpmin:
    case Token.ionic:
    case Token.temperature:
    case Token.vanderwaals:
      value = 1;
      type = RadiusData.TYPE_FACTOR;
      vdwType = (tok == Token.vanderwaals ? 0 : tok);
      tok = tokAt(++index);
      break;
    }
    
    switch (tok) {
    case Token.auto:
    case Token.rasmol:
    case Token.babel:
    case Token.babel21:
    case Token.jmol:
      value = 1;
      type = RadiusData.TYPE_FACTOR;
      --index;
      break;
    case Token.plus:
    case Token.decimal:
      if (tok == Token.plus) {
        index++;
        type = RadiusData.TYPE_OFFSET;
      } else {
        type = RadiusData.TYPE_ABSOLUTE;
        vdwType = Integer.MAX_VALUE;
      }
      value = floatParameter(index, 0, Atom.RADIUS_MAX);
      break;
    case Token.integer:
      value = intParameter(index);
      if (tokAt(index + 1) == Token.percent) {
        index++;
        type = RadiusData.TYPE_FACTOR;
        if (value < 0 || value > 200)
          integerOutOfRange(0, 200);
        value /= 100;
        break;
      }
      
      
      if (value > 749 || value < -200)
        integerOutOfRange(-200, 749);
      if (value > 0) {
        value /= 250;
        type = RadiusData.TYPE_ABSOLUTE;
      } else {
        value /= -100;
        type = RadiusData.TYPE_FACTOR;        
      }
      break;
    default:
      if (value == 1)
        index--;
    }
    if (vdwType == 0) {
      iToken = index;
      vdwType = JmolConstants.getVdwType(optParameterAsString(++index));
      if (vdwType < 0) {
        vdwType = JmolConstants.getVdwType("auto");
      } else {
        iToken = index;
      }
    }
    return new RadiusData(value, type, vdwType);
  }

  private void structure() throws ScriptException {
    String type = parameterAsString(1).toLowerCase();
    byte iType = 0;
    BitSet bs = null;
    if (type.equals("helix"))
      iType = JmolConstants.PROTEIN_STRUCTURE_HELIX;
    else if (type.equals("sheet"))
      iType = JmolConstants.PROTEIN_STRUCTURE_SHEET;
    else if (type.equals("turn"))
      iType = JmolConstants.PROTEIN_STRUCTURE_TURN;
    else if (type.equals("none"))
      iType = JmolConstants.PROTEIN_STRUCTURE_NONE;
    else
      error(ERROR_invalidArgument);
    switch (tokAt(2)) {
    case Token.bitset:
    case Token.expressionBegin:
      bs = expression(2);
      checkLast(iToken);
      break;
    default:
      checkLength(2);
    }
    if (isSyntaxCheck)
      return;
    clearDefinedVariableAtomSets();
    viewer.setProteinType(iType, bs);
  }

  private void wireframe() throws ScriptException {
    int mad = getMadParameter();
    if (isSyntaxCheck)
      return;
    setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
        JmolConstants.BOND_COVALENT_MASK));
    setShapeSize(JmolConstants.SHAPE_STICKS, mad);
  }

  private void ssbond() throws ScriptException {
    int mad = getMadParameter();
    setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
        JmolConstants.BOND_SULFUR_MASK));
    setShapeSize(JmolConstants.SHAPE_STICKS, mad);
    setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
        JmolConstants.BOND_COVALENT_MASK));
  }

  private void struts() throws ScriptException {
    boolean defOn = (tokAt(1) == Token.only || tokAt(1) == Token.on || statementLength == 1);
    int mad = getMadParameter();
    if (defOn)
      mad = (int) (viewer.getStrutDefaultRadius() * 2000f);
    setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
        JmolConstants.BOND_STRUT));
    setShapeSize(JmolConstants.SHAPE_STICKS, mad);
    setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
        JmolConstants.BOND_COVALENT_MASK));
  }

  private void hbond(boolean isCommand) throws ScriptException {
    if (statementLength == 2 && getToken(1).tok == Token.calculate) {
      if (isSyntaxCheck)
        return;
      int n = viewer.autoHbond(null);
      scriptStatusOrBuffer(GT._("{0} hydrogen bonds", n));
      return;
    }
    if (statementLength == 2 && getToken(1).tok == Token.delete) {
      if (isSyntaxCheck)
        return;
      connect(0);
      return;
    }
    int mad = getMadParameter();
    setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
        JmolConstants.BOND_HYDROGEN_MASK));
    setShapeSize(JmolConstants.SHAPE_STICKS, mad);
    setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
        JmolConstants.BOND_COVALENT_MASK));
  }

  private void configuration() throws ScriptException {
    
    
    BitSet bsAtoms;
    if (statementLength == 1) {
      bsAtoms = viewer.setConformation();
      viewer.addStateScript("select", null, viewer.getSelectionSet(), null,
          "configuration", true, false);
    } else {
      int n = intParameter(checkLast(1));
      if (isSyntaxCheck)
        return;
      bsAtoms = viewer.getConformation(viewer.getCurrentModelIndex(), n - 1, true);
      viewer.addStateScript("configuration " + n + ";", true, false);
    }
    if (isSyntaxCheck)
      return;
    boolean addHbonds = viewer.hasCalculatedHBonds(bsAtoms);
    setShapeProperty(JmolConstants.SHAPE_STICKS, "type", new Integer(
        JmolConstants.BOND_HYDROGEN_MASK));
    viewer.setShapeSize(JmolConstants.SHAPE_STICKS, 0,
        bsAtoms);
    if (addHbonds)
      viewer.autoHbond(bsAtoms, bsAtoms, null, 0, 0);
    viewer.select(bsAtoms, tQuiet);
  }

  private void vector() throws ScriptException {
    int type = RadiusData.TYPE_SCREEN;
    float value = 1;
    checkLength(-3);
    switch (iToken = statementLength) {
    case 1:
      break;
    case 2:
      switch (getToken(1).tok) {
      case Token.on:
        break;
      case Token.off:
        value = 0;
        break;
      case Token.integer:
        
        type = RadiusData.TYPE_SCREEN;
        value = intParameter(1, 0, 19);
        break;
      case Token.decimal:
        
        type = RadiusData.TYPE_ABSOLUTE;
        value = floatParameter(1, 0, 3);
        break;
      default:
        error(ERROR_booleanOrNumberExpected);
      }
      break;
    case 3:
      if (tokAt(1) == Token.scale) {
        setFloatProperty("vectorScale", floatParameter(2, -10, 10));
        return;
      }
    }
    setShapeSize(JmolConstants.SHAPE_VECTORS, new RadiusData(value, type, 0));
  }

  private void dipole() throws ScriptException {
    
    String propertyName = null;
    Object propertyValue = null;
    boolean iHaveAtoms = false;
    boolean iHaveCoord = false;
    boolean idSeen = false;

    viewer.loadShape(JmolConstants.SHAPE_DIPOLES);
    if (tokAt(1) == Token.list && listIsosurface(JmolConstants.SHAPE_DIPOLES))
      return;
    setShapeProperty(JmolConstants.SHAPE_DIPOLES, "init", null);
    if (statementLength == 1) {
      setShapeProperty(JmolConstants.SHAPE_DIPOLES, "thisID", null);
      return;
    }
    for (int i = 1; i < statementLength; ++i) {
      propertyName = null;
      propertyValue = null;
      switch (getToken(i).tok) {
      case Token.on:
        propertyName = "on";
        break;
      case Token.off:
        propertyName = "off";
        break;
      case Token.delete:
        propertyName = "delete";
        break;
      case Token.integer:
      case Token.decimal:
        propertyName = "value";
        propertyValue = new Float(floatParameter(i));
        break;
      case Token.bitset:
        propertyName = "atomBitset";
        
      case Token.expressionBegin:
        if (propertyName == null)
          propertyName = (iHaveAtoms || iHaveCoord ? "endSet" : "startSet");
        propertyValue = expression(i);
        i = iToken;
        iHaveAtoms = true;
        break;
      case Token.leftbrace:
      case Token.point3f:
        
        Point3f pt = getPoint3f(i, true);
        i = iToken;
        propertyName = (iHaveAtoms || iHaveCoord ? "endCoord" : "startCoord");
        propertyValue = pt;
        iHaveCoord = true;
        break;
      case Token.bonds:
        propertyName = "bonds";
        break;
      case Token.calculate:
        propertyName = "calculate";
        break;
      case Token.id:
        setShapeId(JmolConstants.SHAPE_DIPOLES, ++i, idSeen);
        i = iToken;
        break;
      case Token.cross:
        propertyName = "cross";
        propertyValue = Boolean.TRUE;
        break;
      case Token.nocross:
        propertyName = "cross";
        propertyValue = Boolean.FALSE;
        break;
      case Token.offset:
        float v = floatParameter(++i);
        if (theTok == Token.integer) {
          propertyName = "offsetPercent";
          propertyValue = new Integer((int) v);
        } else {
          propertyName = "offset";
          propertyValue = new Float(v);
        }
        break;
      case Token.offsetside:
        propertyName = "offsetSide";
        propertyValue = new Float(floatParameter(++i));
        break;
        
      case Token.val:
        propertyName = "value";
        propertyValue = new Float(floatParameter(++i));
        break;
      case Token.width:
        propertyName = "width";
        propertyValue = new Float(floatParameter(++i));
        break;
      default:
        if (theTok == Token.times || Token.tokAttr(theTok, Token.identifier)) {
          setShapeId(JmolConstants.SHAPE_DIPOLES, i, idSeen);
          i = iToken;
          break;
        }
        error(ERROR_invalidArgument);
      }
      idSeen = (theTok != Token.delete && theTok != Token.calculate);
      if (propertyName != null)
        setShapeProperty(JmolConstants.SHAPE_DIPOLES, propertyName,
            propertyValue);
    }
    if (iHaveCoord || iHaveAtoms)
      setShapeProperty(JmolConstants.SHAPE_DIPOLES, "set", null);
  }

  private void animationMode() throws ScriptException {
    float startDelay = 1, endDelay = 1;
    if (statementLength > 5)
      error(ERROR_badArgumentCount);
    int animationMode = JmolConstants.ANIMATION_ONCE;
    switch (getToken(2).tok) {
    case Token.loop:
      animationMode = JmolConstants.ANIMATION_LOOP;
      break;
    case Token.once:
      startDelay = endDelay = 0;
      break;
    case Token.palindrome:
      animationMode = JmolConstants.ANIMATION_PALINDROME;
      break;
    case Token.identifier:
      error(ERROR_invalidArgument);
    }
    if (statementLength >= 4) {
      startDelay = endDelay = floatParameter(3);
      if (statementLength == 5)
        endDelay = floatParameter(4);
    }
    if (!isSyntaxCheck)
      viewer.setAnimationReplayMode(animationMode, startDelay, endDelay);
  }

  private void vibration() throws ScriptException {
    checkLength(-3);
    float period = 0;
    switch (getToken(1).tok) {
    case Token.on:
      checkLength(2);
      period = viewer.getVibrationPeriod();
      break;
    case Token.off:
      checkLength(2);
      period = 0;
      break;
    case Token.integer:
    case Token.decimal:
      checkLength(2);
      period = floatParameter(1);
      break;
    case Token.scale:
      setFloatProperty("vibrationScale", floatParameter(2, -10, 10));
      return;
    case Token.period:
      setFloatProperty("vibrationPeriod", floatParameter(2));
      return;
    case Token.identifier:
      error(ERROR_invalidArgument);
    default:
      period = -1;
    }
    if (period < 0)
      error(ERROR_invalidArgument);
    if (isSyntaxCheck)
      return;
    if (period == 0) {
      viewer.setVibrationOff();
      return;
    }
    viewer.setVibrationPeriod(-period);
  }

  private void animationDirection() throws ScriptException {
    int i = 2;
    int direction = 0;
    switch (tokAt(i)) {
    case Token.minus:
      direction = -intParameter(++i);
      break;
    case Token.plus:
      direction = intParameter(++i);
      break;
    case Token.integer:
      direction = intParameter(i);
      if (direction > 0)
        direction = 0;
      break;
    default:           
      error(ERROR_invalidArgument);
    }
    checkLength(++i);
    if (direction != 1 && direction != -1)
      error(ERROR_numberMustBe, "-1", "1");
    if (!isSyntaxCheck)
      viewer.setAnimationDirection(direction);
  }

  private void calculate() throws ScriptException {
    boolean isSurface = false;
    BitSet bs;
    BitSet bs2;
    if ((iToken = statementLength) >= 2) {
      clearDefinedVariableAtomSets();
      switch (getToken(1).tok) {
      case Token.straightness:
        if (!isSyntaxCheck) {
          viewer.calculateStraightness();
          viewer.addStateScript(thisCommand, false, true);
        }
        return;
      case Token.hydrogen:
        checkLength(2);
        if (isSyntaxCheck)
          return;
        viewer.addHydrogens(null);
        return;
      case Token.pointgroup:
        pointGroup();
        return;
      case Token.surface:
        isSurface = true;
        
        
      case Token.surfacedistance:
        
        boolean isFrom = false;
        switch (tokAt(2)) {
        case Token.within:
          iToken++;
          break;
        case Token.nada:
          isFrom = !isSurface;
          break;
        case Token.from:
          isFrom = true;
          iToken++;
          break;
        default:  
          isFrom = true;
        }
        bs = (iToken + 1 < statementLength ? expression(++iToken) : viewer
            .getSelectionSet());
        checkLength(++iToken);
        if (isSyntaxCheck)
          return;
        viewer.calculateSurface(bs, (isFrom ? Float.MAX_VALUE : -1));
        return;
      case Token.struts:
        bs = (iToken + 1 < statementLength ? expression(++iToken) : null);
        bs2 = (iToken + 1 < statementLength ? expression(++iToken) : null);
        checkLength(++iToken);
        if (isSyntaxCheck)
          return;
        int n = viewer.calculateStruts(bs, bs2);
        if (n > 0)
          colorShape(JmolConstants.SHAPE_STRUTS, JmolConstants.BOND_STRUT,
            0x0FFFFFF, "translucent", 0.5f, null);
        showString(GT._("{0} struts added", n));
        return;
      case Token.volume:
        if (!isSyntaxCheck) {
          float val = viewer.getVolume(null, null);
          showString("" + Math.round(val * 10)/10f + " A^3; " + Math.round(val * 6.02)/10f 
              + " cm^3/mole (VDW " + viewer.getDefaultVdwTypeNameOrData(Integer.MIN_VALUE) + ")" );
          return;
        }
        break;
      case Token.aromatic:
        checkLength(2);
        if (!isSyntaxCheck)
          viewer.assignAromaticBonds();
        return;
      case Token.identifier:
        checkLength(2);
        break;
      case Token.hbond:
        if (statementLength == 2) {
          if (!isSyntaxCheck) 
            viewer.autoHbond(null);
          return;
        }
        BitSet bs1 = expression(2);
        bs2 = expression(iToken + 1);
        if (!isSyntaxCheck) {
          int nBonds = viewer.autoHbond(bs1, bs2, null, -1, -1);
          showString(nBonds + " hydrogen bonds created");
        }
        return;
      case Token.structure:
        bs = (statementLength == 2 ? null : expression(2));
        if (isSyntaxCheck)
          return;
        if (bs == null)
          bs = viewer.getAtomBitSet(null);
        viewer.calculateStructures(bs);
        viewer.addStateScript(thisCommand, false, true);
        return;
      }
    }
    error(
        ERROR_what,
        "CALCULATE",
        "aromatic? hbonds? polymers? straightness? structure? strut? surfaceDistance FROM? surfaceDistance WITHIN? volume?");
  }

  private void pointGroup() throws ScriptException {
    switch (tokAt(0)) {
    case Token.calculate:
      if (!isSyntaxCheck)
        showString(viewer.calculatePointGroup());
      return;
    case Token.show:
      if (!isSyntaxCheck)
        showString(viewer.getPointGroupAsString(false, null, 0, 0));
      return;
    }
    
    int pt = 2;
    String type = (tokAt(pt) == Token.scale ? "" : optParameterAsString(pt));
    float scale = 1;
    int index = 0;
    if (type.length() > 0) {
      if (isFloatParameter(++pt))
        index = intParameter(pt++);
    }
    if (tokAt(pt) == Token.scale)
      scale = floatParameter(++pt);
    if (!isSyntaxCheck)
      runScript(viewer.getPointGroupAsString(true, type, index, scale));
  }

  private void dots(int iShape) throws ScriptException {
    if (!isSyntaxCheck)
      viewer.loadShape(iShape);
    setShapeProperty(iShape, "init", null);
    float value = Float.NaN;
    int type = 0;
    int ipt = 1;
    switch (getToken(1).tok) {
    case Token.only:
      restrictSelected(false, false);
      value = 1;
      type = RadiusData.TYPE_FACTOR;
      break;
    case Token.on:
      value = 1;
      type = RadiusData.TYPE_FACTOR;
      break;
    case Token.off:
      value = 0;
      break;
    case Token.integer:
      int dotsParam = intParameter(ipt++);
      if (tokAt(ipt) == Token.radius) {
        setShapeProperty(iShape, "atom", new Integer(dotsParam));
        setShapeProperty(iShape, "radius", new Float(floatParameter(++ipt)));
        if (tokAt(++ipt) == Token.color) {
          setShapeProperty(iShape, "colorRGB", new Integer(getArgbParam(++ipt)));
          ipt++;
        }
        if (getToken(ipt).tok != Token.bitset)
          error(ERROR_invalidArgument);
        setShapeProperty(iShape, "dots", statement[ipt].value);
        return;
      }
      break;
    }
    RadiusData rd = (Float.isNaN(value) ? encodeRadiusParameter(1)
        : new RadiusData(value, type, 0));
    if (Float.isNaN(rd.value))
      error(ERROR_invalidArgument);
    setShapeSize(iShape, rd);
  }

  private void proteinShape(int shapeType) throws ScriptException {
    int mad = 0;
    
    switch (getToken(1).tok) {
    case Token.only:
      if (isSyntaxCheck)
        return;
      restrictSelected(false, false);
      mad = -1;
      break;
    case Token.on:
      mad = -1; 
      break;
    case Token.off:
      break;
    case Token.structure:
      mad = -2;
      break;
    case Token.temperature:
    case Token.displacement:
      mad = -4;
      break;
    case Token.integer:
      mad = (intParameter(1, 0, 499) * 8);
      break;
    case Token.decimal:
      mad = (int) (floatParameter(1, 0, Shape.RADIUS_MAX) * 2000);
      break;
    case Token.bitset:
      if (!isSyntaxCheck)
        viewer.loadShape(shapeType);
      setShapeProperty(shapeType, "bitset", theToken.value);
      return;
    default:
      error(ERROR_booleanOrNumberExpected);
    }
    setShapeSize(shapeType, mad);
  }

  private void animation() throws ScriptException {
    boolean animate = false;
    switch (getToken(1).tok) {
    case Token.on:
      animate = true;
      
    case Token.off:
      if (!isSyntaxCheck)
        viewer.setAnimationOn(animate);
      break;
    case Token.frame:
      frame(2);
      break;
    case Token.mode:
      animationMode();
      break;
    case Token.direction:
      animationDirection();
      break;
    case Token.fps:
      setIntProperty("animationFps", intParameter(checkLast(2)));
      break;
    default:
      frameControl(1, true);
    }
  }

  private void file() throws ScriptException {
    int file = intParameter(checkLast(1));
    if (isSyntaxCheck)
      return;
    int modelIndex = viewer.getModelNumberIndex(file * 1000000 + 1, false,
        false);
    int modelIndex2 = -1;
    if (modelIndex >= 0) {
      modelIndex2 = viewer.getModelNumberIndex((file + 1) * 1000000 + 1, false,
          false);
      if (modelIndex2 < 0)
        modelIndex2 = viewer.getModelCount();
      modelIndex2--;
    }
    viewer.setAnimationOn(false);
    viewer.setAnimationDirection(1);
    viewer.setAnimationRange(modelIndex, modelIndex2);
    viewer.setCurrentModelIndex(-1);
  }

  private void frame(int offset) throws ScriptException {
    boolean useModelNumber = true;
    
    
    if (statementLength == 1 && offset == 1) {
      int modelIndex = viewer.getCurrentModelIndex();
      int m;
      if (!isSyntaxCheck && modelIndex >= 0
          && (m = viewer.getJmolDataSourceFrame(modelIndex)) >= 0)
        viewer.setCurrentModelIndex(m == modelIndex ? Integer.MIN_VALUE : m);
      return;
    }
    switch (tokAt(1)) {
    case Token.title:
      if (statementLength == 3) {
        if (!isSyntaxCheck)
          viewer.setFrameTitle(parameterAsString(2));
        return;
      }
      break;
    case Token.align:
      BitSet bs = (statementLength == 2 || tokAt(2) == Token.none ? null
          : expression(2));
      if (!isSyntaxCheck)
        viewer.setFrameOffsets(bs);
      return;
    }
    if (getToken(offset).tok == Token.minus) {
      ++offset;
      if (getToken(checkLast(offset)).tok != Token.integer || intParameter(offset) != 1)
        error(ERROR_invalidArgument);
      if (!isSyntaxCheck)
        viewer.setAnimation(Token.prev);
      return;
    }
    boolean isPlay = false;
    boolean isRange = false;
    boolean isAll = false;
    boolean isHyphen = false;
    int[] frameList = new int[] { -1, -1 };
    int nFrames = 0;

    for (int i = offset; i < statementLength; i++) {
      switch (getToken(i).tok) {
      case Token.all:
      case Token.times:
        checkLength(offset + (isRange ? 2 : 1));
        isAll = true;
        break;
      case Token.minus: 
        if (nFrames != 1)
          error(ERROR_invalidArgument);
        isHyphen = true;
        break;
      case Token.none:
        checkLength(offset + 1);
        break;
      case Token.decimal:
        useModelNumber = false;
        if (floatParameter(i) < 0)
          isHyphen = true;
        
      case Token.integer:
        if (nFrames == 2)
          error(ERROR_invalidArgument);
        int iFrame = statement[i].intValue;
        if (iFrame == -1) {
          checkLength(offset + 1);
          if (!isSyntaxCheck)
            viewer.setAnimation(Token.prev);
          return;
        }
        if (iFrame >= 1000 && iFrame < 1000000 && viewer.haveFileSet())
          iFrame = (iFrame / 1000) * 1000000 + (iFrame % 1000); 
        if (!useModelNumber && iFrame == 0)
          isAll = true; 
        if (iFrame >= 1000000)
          useModelNumber = false;
        frameList[nFrames++] = iFrame;
        break;
      case Token.play:
        isPlay = true;
        break;
      case Token.range:
        isRange = true;
        break;
      default:
        frameControl(offset, false);
        return;
      }
    }
    boolean haveFileSet = viewer.haveFileSet();
    if (isRange && nFrames == 0)
      isAll = true;
    if (isSyntaxCheck)
      return;
    if (isAll) {
      viewer.setAnimationOn(false);
      viewer.setAnimationRange(-1, -1);
      if (!isRange) {
        viewer.setCurrentModelIndex(-1);
      }
      return;
    }
    if (nFrames == 2 && !isRange)
      isHyphen = true;
    if (haveFileSet)
      useModelNumber = false;
    else if (useModelNumber)
      for (int i = 0; i < nFrames; i++)
        if (frameList[i] >= 0)
          frameList[i] %= 1000000;
    int modelIndex = viewer.getModelNumberIndex(frameList[0], useModelNumber,
        false);
    int modelIndex2 = -1;
    if (haveFileSet && nFrames == 1 && modelIndex < 0 && frameList[0] != 0) {
      
      if (frameList[0] < 1000000)
        frameList[0] *= 1000000;
      if (frameList[0] % 1000000 == 0) {
        frameList[0]++;
        modelIndex = viewer.getModelNumberIndex(frameList[0], false, false);
        if (modelIndex >= 0) {
          modelIndex2 = viewer.getModelNumberIndex(frameList[0] + 1000000,
              false, false);
          if (modelIndex2 < 0)
            modelIndex2 = viewer.getModelCount();
          modelIndex2--;
          if (isRange)
            nFrames = 2;
          else if (!isHyphen && modelIndex2 != modelIndex)
            isHyphen = true;
          isRange = isRange || modelIndex == modelIndex2;
                                                         
                                                         
                                                         
        }
      } else {
        
        return;
      }
    }

    if (!isPlay && !isRange || modelIndex >= 0)
      viewer.setCurrentModelIndex(modelIndex, false);
    if (isPlay && nFrames == 2 || isRange || isHyphen) {
      if (modelIndex2 < 0)
        modelIndex2 = viewer.getModelNumberIndex(frameList[1], useModelNumber,
            false);
      viewer.setAnimationOn(false);
      viewer.setAnimationDirection(1);
      viewer.setAnimationRange(modelIndex, modelIndex2);
      viewer.setCurrentModelIndex(isHyphen && !isRange ? -1
          : modelIndex >= 0 ? modelIndex : 0, false);
    }
    if (isPlay)
      viewer.setAnimation(Token.resume);
  }

  BitSet bitSetForModelFileNumber(int m) {
    
    BitSet bs = new BitSet();
    if (isSyntaxCheck)
      return bs;
    int modelCount = viewer.getModelCount();
    boolean haveFileSet = viewer.haveFileSet();
    if (m < 1000000 && haveFileSet)
      m *= 1000000;
    int pt = m % 1000000;
    if (pt == 0) {
      int model1 = viewer.getModelNumberIndex(m + 1, false, false);
      if (model1 < 0)
        return bs;
      int model2 = (m == 0 ? modelCount : viewer.getModelNumberIndex(
          m + 1000001, false, false));
      if (model1 < 0)
        model1 = 0;
      if (model2 < 0)
        model2 = modelCount;
      if (viewer.isTrajectory(model1))
        model2 = model1 + 1;
      for (int j = model1; j < model2; j++)
        bs.or(viewer.getModelAtomBitSet(j, false));
    } else {
      int modelIndex = viewer.getModelNumberIndex(m, false, true);
      if (modelIndex >= 0)
        bs.or(viewer.getModelAtomBitSet(modelIndex, false));
    }
    return bs;
  }

  private void frameControl(int i, boolean isSubCmd) throws ScriptException {
    switch (getToken(checkLast(i)).tok) {
    case Token.playrev:
    case Token.play:
    case Token.resume:
    case Token.pause:
    case Token.next:
    case Token.prev:
    case Token.rewind:
    case Token.first:
    case Token.last:
      if (!isSyntaxCheck)
        viewer.setAnimation(theTok);
      return;
    }
    error(ERROR_invalidArgument);
  }

  private int getShapeType(int tok) throws ScriptException {
    int iShape = JmolConstants.shapeTokenIndex(tok);
    if (iShape < 0)
      error(ERROR_unrecognizedObject);
    return iShape;
  }

  private void font(int shapeType, float fontsize) throws ScriptException {
    String fontface = "SansSerif";
    String fontstyle = "Plain";
    int sizeAdjust = 0;
    float scaleAngstromsPerPixel = -1;
    switch (iToken = statementLength) {
    case 6:
      scaleAngstromsPerPixel = floatParameter(5);
      if (scaleAngstromsPerPixel >= 5) 
        scaleAngstromsPerPixel = viewer.getZoomSetting()
            / scaleAngstromsPerPixel / viewer.getScalePixelsPerAngstrom(false);
      
    case 5:
      if (getToken(4).tok != Token.identifier)
        error(ERROR_invalidArgument);
      fontstyle = parameterAsString(4);
      
    case 4:
      if (getToken(3).tok != Token.identifier)
        error(ERROR_invalidArgument);
      fontface = parameterAsString(3);
      if (!isFloatParameter(2))
        error(ERROR_numberExpected);
      fontsize = floatParameter(2);
      shapeType = getShapeType(getToken(1).tok);
      break;
    case 3:
      if (!isFloatParameter(2))
        error(ERROR_numberExpected);
      if (shapeType == -1) {
        shapeType = getShapeType(getToken(1).tok);
        fontsize = floatParameter(2);
      } else {
        if (fontsize >= 1)
          fontsize += (sizeAdjust = 5);
      }
      break;
    case 2:
    default:
      if (shapeType == JmolConstants.SHAPE_LABELS) {
        
        fontsize = JmolConstants.LABEL_DEFAULT_FONTSIZE;
        break;
      }
      error(ERROR_badArgumentCount);
    }
    if (shapeType == JmolConstants.SHAPE_LABELS) {
      if (fontsize < 0
          || fontsize >= 1
          && (fontsize < JmolConstants.LABEL_MINIMUM_FONTSIZE || fontsize > JmolConstants.LABEL_MAXIMUM_FONTSIZE))
        integerOutOfRange(JmolConstants.LABEL_MINIMUM_FONTSIZE - sizeAdjust,
            JmolConstants.LABEL_MAXIMUM_FONTSIZE - sizeAdjust);
      setShapeProperty(JmolConstants.SHAPE_LABELS, "setDefaults", viewer
          .getNoneSelected());
    }
    if (isSyntaxCheck)
      return;
    Font3D font3d = viewer.getFont3D(fontface, fontstyle, fontsize);
    viewer.loadShape(shapeType);
    setShapeProperty(shapeType, "font", font3d);
    if (scaleAngstromsPerPixel >= 0)
      setShapeProperty(shapeType, "scalereference", new Float(
          scaleAngstromsPerPixel));
  }

  private void set() throws ScriptException {
    
    if (statementLength == 1) {
      showString(viewer.getAllSettings(null));
      return;
    }
    boolean isJmolSet = (parameterAsString(0).equals("set"));
    String key = optParameterAsString(1);
    if (isJmolSet && statementLength == 2 && key.indexOf("?") >= 0) {
      showString(viewer.getAllSettings(key.substring(0, key.indexOf("?"))));
      return;
    }
    int tok = getToken(1).tok;
    int newTok = 0;
    String sval;
    int ival = Integer.MAX_VALUE;

    boolean showing = (!isSyntaxCheck && !tQuiet
        && scriptLevel <= scriptReportingLevel && !((String) statement[0].value)
        .equals("var"));

    
    

    switch (tok) {
    case Token.axes:
      axes(2);
      return;
    case Token.background:
      background(2);
      return;
    case Token.boundbox:
      boundbox(2);
      return;
    case Token.frank:
      frank(2);
      return;
    case Token.history:
      history(2);
      return;
    case Token.label:
      label(2);
      return;
    case Token.unitcell:
      unitcell(2);
      return;
    case Token.display:
    case Token.selectionhalos:
      selectionHalo(2);
      return;
    case Token.timeout:
      timeout(2);
      return;
    }

    
    
    

    

    switch (tok) {
    case Token.bondmode:
      setBondmode();
      return;
    case Token.debug:
      if (isSyntaxCheck)
        return;
      int iLevel = (tokAt(2) == Token.off || tokAt(2) == Token.integer
          && intParameter(2) == 0 ? 4 : 5);
      Logger.setLogLevel(iLevel);
      setIntProperty("logLevel", iLevel);
      if (iLevel == 4) {
        viewer.setDebugScript(false);
        if (showing)
          viewer.showParameter("debugScript", true, 80);
      }
      setDebugging();
      if (showing)
        viewer.showParameter("logLevel", true, 80);
      return;
    case Token.echo:
      setEcho();
      return;
    case Token.fontsize:
      font(JmolConstants.SHAPE_LABELS, checkLength23() == 2 ? 0
          : floatParameter(2));
      return;
    case Token.hbond:
      setHbond();
      return;
    case Token.measure:
    case Token.measurements:
      setMonitor();
      return;
    case Token.ssbond: 
      setSsbond();
      return;
    case Token.togglelabel:
      setLabel("toggle");
      return;
    case Token.usercolorscheme:
      setUserColors();
      return;
    }

    
    
    

    boolean justShow = true;

    switch (tok) {
    case Token.axesscale:
      setFloatProperty("axesScale", floatSetting(2, -100, 100));
      break;
    case Token.backgroundmodel:
      if (statementLength > 2) {
        String modelDotted = stringSetting(2, false);
        int modelNumber;
        boolean useModelNumber = false;
        if (modelDotted.indexOf(".") < 0) {
          modelNumber = Parser.parseInt(modelDotted);
          useModelNumber = true;
        } else {
          modelNumber = JmolConstants.modelValue(modelDotted);
        }
        if (isSyntaxCheck)
          return;
        int modelIndex = viewer.getModelNumberIndex(modelNumber,
            useModelNumber, true);
        viewer.setBackgroundModelIndex(modelIndex);
        return;
      }
      break;
    case Token.defaultvdw:
      
      if (statementLength > 2) {
        sval = (statementLength == 3
            && JmolConstants.getVdwType(parameterAsString(2)) == JmolConstants.VDW_UNKNOWN 
            ? stringSetting(2, false)
            : parameterAsString(2));
        if (JmolConstants.getVdwType(sval) < 0)
          error(ERROR_invalidArgument);
        setStringProperty(key, sval);
      }
      break;
    case Token.defaultlattice:
      if (statementLength > 2) {
        Point3f pt;
        Vector v = (Vector) parameterExpression(2, 0, "XXX", true);
        if (v == null || v.size() == 0)
          error(ERROR_invalidArgument);
        ScriptVariable var = (ScriptVariable) v.elementAt(0);
        if (var.tok == Token.point3f)
          pt = (Point3f) var.value;
        else {
          int ijk = ScriptVariable.iValue(var);
          if (ijk < 555)
            pt = new Point3f();
          else
            pt = viewer.getSymmetry().ijkToPoint3f(ijk + 111);
        }
        if (!isSyntaxCheck)
          viewer.setDefaultLattice(pt);
      }
      break;
    case Token.defaults:
    case Token.defaultcolorscheme:
      
      if (statementLength > 2) {
        if ((theTok = tokAt(2)) == Token.jmol || theTok == Token.rasmol) {
          sval = parameterAsString(checkLast(2)).toLowerCase();
        } else {
          sval = stringSetting(2, false).toLowerCase();
        }
        if (!sval.equals("jmol") && !sval.equals("rasmol"))
          error(ERROR_invalidArgument);
        setStringProperty(key, sval);
      }
      break;
    case Token.dipolescale:
      setFloatProperty("dipoleScale", floatSetting(2, -10, 10));
      break;
    case Token.formalcharge:
      ival = intSetting(2);
      if (ival == Integer.MIN_VALUE)
        error(ERROR_invalidArgument);
      if (!isSyntaxCheck)
        viewer.setFormalCharges(ival);
      return;
    case Token.historylevel:
      
      ival = intSetting(2);
      if (!isSyntaxCheck) {
        if (ival != Integer.MIN_VALUE)
          commandHistoryLevelMax = ival;
        setIntProperty(key, ival);
      }
      break;
    case Token.language:
      
      
      if (statementLength > 2)
        setStringProperty(key, stringSetting(2, isJmolSet));
      break;
    case Token.measurementunits:
      if (statementLength > 2)
        setMeasurementUnits(stringSetting(2, isJmolSet));
      break;
    case Token.phongexponent:
      setIntProperty(key, intSetting(2, Integer.MAX_VALUE, 0, 1000));
      break;
    case Token.picking:
      if (statementLength > 2) {
        setPicking();
        return;
      }
      break;
    case Token.pickingstyle:
      if (statementLength > 2) {
        setPickingStyle();
        return;
      }
      break;
    case Token.property: 
      
      break;
    case Token.scriptreportinglevel:
      
      ival = intSetting(2);
      if (!isSyntaxCheck) {
        if (ival != Integer.MIN_VALUE)
          scriptReportingLevel = ival;
        setIntProperty(key, ival);
      }
      break;
    case Token.solventproberadius:
      setFloatProperty(key, floatSetting(2, 0, 10));
      break;
    case Token.specular:
    case Token.specularpercent:
    case Token.ambientpercent:
    case Token.diffusepercent:
      ival = intSetting(2);
      if (tok == Token.specular) {
        if (ival == Integer.MIN_VALUE || ival == 0 || ival == 1) {
          justShow = false;
          break;
        }
        tok = Token.specularpercent;
        key = "specularPercent";
      }
      setIntProperty(key, intSetting(2, ival, 0, 100));
      break;
    case Token.specularpower:
    case Token.specularexponent:
      ival = intSetting(2);
      if (tok == Token.specularpower) {
        if (ival >= 0) {
          justShow = false;
          break;
        }
        tok = Token.specularexponent;
        key = "specularExponent";
        if (ival < -10 || ival > -1)
          integerOutOfRange(-10, -1);
        ival = -ival;
      }
      setIntProperty(key, intSetting(2, ival, 0, 10));
      break;
    case Token.strands:
    case Token.strandcount:
    case Token.strandcountformeshribbon:
    case Token.strandcountforstrands:
      if (tok == Token.strands) {
        tok = Token.strandcount;
        key = "strandCount";
      }
      setIntProperty(key, intSetting(2, Integer.MAX_VALUE, 0, 20));
      break;
    default:
      justShow = false;
    }

    if (justShow && !showing)
      return;

    

    boolean isContextVariable = (!justShow && !isJmolSet && getContextVariableAsVariable(key) != null);

    if (!justShow && !isContextVariable) {

      

      switch (tok) {
      case Token.bonds:
        newTok = Token.showmultiplebonds;
        break;
      case Token.hetero:
        newTok = Token.selecthetero;
        break;
      case Token.hydrogen:
        newTok = Token.selecthydrogen;
        break;
      case Token.measurementnumbers:
        newTok = Token.measurementlabels;
        break;
      case Token.radius:
        newTok = Token.solventproberadius;
        setFloatProperty("solventProbeRadius", floatSetting(2, 0, 10));
        justShow = true;
        break;
      case Token.scale3d:
        newTok = Token.scaleangstromsperinch;
        break;
      case Token.solvent:
        newTok = Token.solventprobe;
        break;
      case Token.color:
        newTok = Token.defaultcolorscheme;
        break;
      case Token.spin:
        sval = parameterAsString(2).toLowerCase();
        switch ("x;y;z;fps".indexOf(sval + ";")) {
        case 0:
          newTok = Token.spinx;
          break;
        case 2:
          newTok = Token.spiny;
          break;
        case 4:
          newTok = Token.spinz;
          break;
        case 6:
          newTok = Token.spinfps;
          break;
        default:
          error(ERROR_unrecognizedParameter, "set SPIN ", sval);
        }
        if (!isSyntaxCheck)
           viewer.setSpin(sval, (int) floatParameter(checkLast(3)));
        justShow = true;
        break;
      }
    }

    if (newTok != 0) {
      key = Token.nameOf(tok = newTok);
    } else if (!justShow && !isContextVariable) {
      
      if (key.charAt(0) == '_') 
        error(ERROR_invalidArgument);

      

      String lckey = key.toLowerCase();
      if (lckey.indexOf("label") == 0
          && Parser
              .isOneOf(key.substring(5).toLowerCase(),
                  "front;group;atom;offset;offsetexact;pointer;alignment;toggle;scalereference")) {
        if (setLabel(key.substring(5)))
          return;
      }
      if (lckey.indexOf("callback") >= 0)
        tok = Token.setparam;
    }

    if (isJmolSet && !Token.tokAttr(tok, Token.setparam)) {
      iToken = 1;
      if (!isStateScript)
        error(ERROR_unrecognizedParameter, "SET", key);
      warning(ERROR_unrecognizedParameterWarning, "SET", key);
    }
    
    if (!justShow && isJmolSet) {
      
      switch (statementLength) {
      case 2:
        
        
        setBooleanProperty(key, true);
        justShow = true;
        break;
      case 3:
        
        
        if (ival != Integer.MAX_VALUE) {
          
          setIntProperty(key, ival);
          justShow = true;
        }
        break;
      }
    }

    if (!justShow && !isJmolSet && tokAt(2) == Token.none) {
      if (!isSyntaxCheck)
        viewer.removeUserVariable(key);
      justShow = true;
    }

    if (!justShow) {
      int tok2 = (tokAt(1) == Token.expressionBegin ? 0 : tokAt(2));
      int setType = statement[0].intValue;
      
      
      
      
      
      
      

      int pt = (tok2 == Token.opEQ ? 3
      
          : setType == '=' && !key.equals("return") && tok2 != Token.opEQ ? 0
          
          
          
              : 2
              
              
              
              
              
              
              
              
      );
      setVariable(pt, 0, key, setType);
      if (!isJmolSet)
        return;
    }
    if (showing)
      viewer.showParameter(key, true, 80);
  }

  private void setBondmode() throws ScriptException {
    boolean bondmodeOr = false;
    switch (getToken(checkLast(2)).tok) {
    case Token.opAnd:
      break;
    case Token.opOr:
      bondmodeOr = true;
      break;
    default:
      error(ERROR_invalidArgument);
    }
    setBooleanProperty("bondModeOr", bondmodeOr);
  }

  private void setEcho() throws ScriptException {
    String propertyName = "target";
    Object propertyValue = null;
    boolean echoShapeActive = true;
    
    int len = 3;
    switch (getToken(2).tok) {
    case Token.off:
      checkLength(3);
      echoShapeActive = false;
      propertyName = "allOff";
      break;
    case Token.hide:
    case Token.hidden:
      propertyName = "hidden";
      propertyValue = Boolean.TRUE;
      break;
    case Token.on:
    case Token.display:
    case Token.displayed:
      propertyName = "hidden";
      propertyValue = Boolean.FALSE;
      break;
    case Token.none:
      echoShapeActive = false;
      
    case Token.all:
      checkLength(3);
      
    case Token.left:
    case Token.right:
    case Token.top:
    case Token.bottom:
    case Token.center:
    case Token.identifier:
      propertyValue = parameterAsString(2);
      break;
    case Token.model:
      int modelIndex = modelNumberParameter(3);
      if (isSyntaxCheck)
        return;
      if (modelIndex >= viewer.getModelCount())
        error(ERROR_invalidArgument);
      propertyName = "model";
      propertyValue = new Integer(modelIndex);
      len = 4;
      break;
    case Token.image:
      
      echo(3, true);
      return;
    case Token.depth:
      
      propertyName = "%zpos";
      propertyValue = new Integer((int) floatParameter(3));
      len = 4;
      break;
    case Token.string:
      echo(2, false);
      return;
    default:
      if (!Token.tokAttr(theTok, Token.identifier))
        error(ERROR_invalidArgument);
      propertyValue = parameterAsString(2);
      break;
    }
    if (!isSyntaxCheck) {
      viewer.setEchoStateActive(echoShapeActive);
      viewer.loadShape(JmolConstants.SHAPE_ECHO);
      setShapeProperty(JmolConstants.SHAPE_ECHO, propertyName, propertyValue);
    }
    if (statementLength == len)
      return;
    propertyName = "align";
    propertyValue = null;
    
    if (statementLength == 4) {
      if (isCenterParameter(3)) {
        setShapeProperty(JmolConstants.SHAPE_ECHO, "xyz", centerParameter(3));
        return;
      }
      switch (getToken(3).tok) {
      case Token.off:
        propertyName = "off";
        break;
      case Token.hidden:
        propertyName = "hidden";
        propertyValue = Boolean.TRUE;
        break;
      case Token.displayed:
      case Token.on:
        propertyName = "hidden";
        propertyValue = Boolean.FALSE;
        break;
      case Token.model:
        int modelIndex = modelNumberParameter(4);
        if (isSyntaxCheck)
          return;
        if (modelIndex >= viewer.getModelCount())
          error(ERROR_invalidArgument);
        propertyName = "model";
        propertyValue = new Integer(modelIndex);
        break;
      case Token.left:
      case Token.right:
      case Token.top:
      case Token.bottom:
      case Token.center:
      case Token.identifier:
        propertyValue = parameterAsString(3);
        break;
      default:
        if (!Token.tokAttr(theTok, Token.identifier))
          error(ERROR_invalidArgument);
        propertyValue = parameterAsString(3);
        break;
      }
      setShapeProperty(JmolConstants.SHAPE_ECHO, propertyName, propertyValue);
      return;
    }
    
    
    
    
    if (statementLength == 5) {
      switch (tokAt(3)) {
      case Token.script:
        propertyName = "script";
        propertyValue = parameterAsString(4);
        break;
      case Token.model:
        int modelIndex = modelNumberParameter(4);
        if (!isSyntaxCheck && modelIndex >= viewer.getModelCount())
          error(ERROR_invalidArgument);
        propertyName = "model";
        propertyValue = new Integer(modelIndex);
        break;
      case Token.image:
        
        echo(4, true);
        return;
      case Token.depth:
        propertyName = "%zpos";
        propertyValue = new Integer((int) floatParameter(4));
        break;
      }
      if (propertyValue != null) {
        setShapeProperty(JmolConstants.SHAPE_ECHO, propertyName, propertyValue);
        return;
      }
    }
    
    

    getToken(4);
    int i = 3;
    
    if (isCenterParameter(i)) {
      if (!isSyntaxCheck)
        setShapeProperty(JmolConstants.SHAPE_ECHO, "xyz", centerParameter(i));
      return;
    }
    String type = "xypos";
    if ((propertyValue = xypParameter(i)) == null) {
      int pos = intParameter(i++);
      propertyValue = new Integer(pos);
      if (tokAt(i) == Token.percent) {
        type = "%xpos";
        i++;
      } else {
        type = "xpos";
      }
      setShapeProperty(JmolConstants.SHAPE_ECHO, type, propertyValue);
      pos = intParameter(i++);
      propertyValue = new Integer(pos);
      if (tokAt(i) == Token.percent) {
        type = "%ypos";
        i++;
      } else {
        type = "ypos";
      }
    }
    setShapeProperty(JmolConstants.SHAPE_ECHO, type, propertyValue);
  }

  private boolean setLabel(String str) throws ScriptException {
    viewer.loadShape(JmolConstants.SHAPE_LABELS);
    Object propertyValue = null;
    setShapeProperty(JmolConstants.SHAPE_LABELS, "setDefaults", viewer
        .getNoneSelected());
    while (true) {
      if (str.equals("scalereference")) {
        float scaleAngstromsPerPixel = floatParameter(2);
        if (scaleAngstromsPerPixel >= 5) 
          scaleAngstromsPerPixel = viewer.getZoomSetting()
              / scaleAngstromsPerPixel
              / viewer.getScalePixelsPerAngstrom(false);
        propertyValue = new Float(scaleAngstromsPerPixel);
        break;
      }
      if (str.equals("offset") || str.equals("offsetexact")) {
        int xOffset = intParameter(2, -127, 127);
        int yOffset = intParameter(3, -127, 127);
        propertyValue = new Integer(Object2d.getOffset(xOffset, yOffset));
        break;
      }
      if (str.equals("alignment")) {
        switch (getToken(2).tok) {
        case Token.left:
        case Token.right:
        case Token.center:
          str = "align";
          propertyValue = theToken.value;
          break;
        default:
          error(ERROR_invalidArgument);
        }
        break;
      }
      if (str.equals("pointer")) {
        int flags = Object2d.POINTER_NONE;
        switch (getToken(2).tok) {
        case Token.off:
        case Token.none:
          break;
        case Token.background:
          flags |= Object2d.POINTER_BACKGROUND;
        case Token.on:
          flags |= Object2d.POINTER_ON;
          break;
        default:
          error(ERROR_invalidArgument);
        }
        propertyValue = new Integer(flags);
        break;
      }
      if (str.equals("toggle")) {
        iToken = 1;
        BitSet bs = (statementLength == 2 ? null : expression(2));
        checkLast(iToken);
        if (!isSyntaxCheck)
          viewer.togglePickingLabel(bs);
        return true;
      }
      iToken = 1;
      boolean TF = (statementLength == 2 || getToken(2).tok == Token.on);
      if (str.equals("front") || str.equals("group")) {
        if (!TF && tokAt(2) != Token.off)
          error(ERROR_invalidArgument);
        if (!TF)
          str = "front";
        propertyValue = (TF ? Boolean.TRUE : Boolean.FALSE);
        break;
      }
      if (str.equals("atom")) {
        if (!TF && tokAt(2) != Token.off)
          error(ERROR_invalidArgument);
        str = "front";
        propertyValue = (TF ? Boolean.FALSE : Boolean.TRUE);
        break;
      }
      return false;
    }
    BitSet bs = (iToken + 1 < statementLength ? expression(++iToken) : null);
    checkLast(iToken);
    if (isSyntaxCheck)
      return true;
    if (bs == null)
      setShapeProperty(JmolConstants.SHAPE_LABELS, str, propertyValue);
    else
      viewer.setShapeProperty(JmolConstants.SHAPE_LABELS, str, propertyValue,
          bs);
    return true;
  }

  private void setMonitor() throws ScriptException {
    
    
    int tok = tokAt(checkLast(2));
    switch (tok) {
    case Token.on:
    case Token.off:
      setShapeProperty(JmolConstants.SHAPE_MEASURES, "showMeasurementNumbers",
          tok == Token.on ? Boolean.TRUE : Boolean.FALSE);
      return;
    case Token.dotted:
    case Token.integer:
    case Token.decimal:
      setShapeSize(JmolConstants.SHAPE_MEASURES, getSetAxesTypeMad(2));
      return;
    }
    setMeasurementUnits(parameterAsString(2));
  }

  private boolean setMeasurementUnits(String units) throws ScriptException {
    if (!StateManager.isMeasurementUnit(units))
      error(ERROR_unrecognizedParameter, "set measurementUnits ", units);
    if (!isSyntaxCheck)
      viewer.setMeasureDistanceUnits(units);
    return true;
  }

  

  private void setSsbond() throws ScriptException {
    boolean ssbondsBackbone = false;
    
    switch (tokAt(checkLast(2))) {
    case Token.backbone:
      ssbondsBackbone = true;
      break;
    case Token.sidechain:
      break;
    default:
      error(ERROR_invalidArgument);
    }
    setBooleanProperty("ssbondsBackbone", ssbondsBackbone);
  }

  private void setHbond() throws ScriptException {
    boolean bool = false;
    switch (tokAt(checkLast(2))) {
    case Token.backbone:
      bool = true;
      
    case Token.sidechain:
      setBooleanProperty("hbondsBackbone", bool);
      break;
    case Token.solid:
      bool = true;
      
    case Token.dotted:
      setBooleanProperty("hbondsSolid", bool);
      break;
    default:
      error(ERROR_invalidArgument);
    }
  }

  private void setPicking() throws ScriptException {
    
    if (statementLength == 2) {
      setStringProperty("picking", "identify");
      return;
    }
    
    if (statementLength > 4 || tokAt(2) == Token.string) {
      setStringProperty("picking", stringSetting(2, false));
      return;
    }
    int i = 2;
    
    
    
    String type = "SELECT";
    switch (getToken(2).tok) {
    case Token.select:
    case Token.measure:
    case Token.spin:
      if (checkLength34() == 4) {
        type = parameterAsString(2).toUpperCase();
        if (type.equals("SPIN"))
          setIntProperty("pickingSpinRate", intParameter(3));
        else
          i = 3;
      }
    case Token.delete:
      break;
    default:
      checkLength(3);
    }

    
    
    
    
    
    
    

    String str = parameterAsString(i);
    switch (getToken(i).tok) {
    case Token.on:
    case Token.normal:
      str = "identify";
      break;
    case Token.none:
      str = "off";
      break;
    case Token.select:
      str = "atom";
      break;
    case Token.label:
      str = "label";
      break;
    case Token.bonds: 
      str = "bond";
      break;
    case Token.delete:
      checkLength(4);
      if (tokAt(3) != Token.bonds)
        error(ERROR_invalidArgument);
      str = "deleteBond";
      break;
    }
    int mode = JmolConstants.getPickingMode(str);
    if (mode < 0)
      error(ERROR_unrecognizedParameter, "SET PICKING " + type, str);
    setStringProperty("picking", str);
  }

  private void setPickingStyle() throws ScriptException {
    if (statementLength > 4 || tokAt(2) == Token.string) {
      setStringProperty("pickingStyle", stringSetting(2, false));
      return;
    }
    int i = 2;
    boolean isMeasure = false;
    String type = "SELECT";
    switch (getToken(2).tok) {
    case Token.measure:
      isMeasure = true;
      type = "MEASURE";
      
    case Token.select:
      if (checkLength34() == 4)
        i = 3;
      break;
    default:
      checkLength(3);
    }
    String str = parameterAsString(i);
    switch (getToken(i).tok) {
    case Token.none:
    case Token.off:
      str = (isMeasure ? "measureoff" : "toggle");
      break;
    case Token.on:
      if (isMeasure)
        str = "measure";
      break;
    }
    if (JmolConstants.getPickingStyle(str) < 0)
      error(ERROR_unrecognizedParameter, "SET PICKINGSTYLE " + type, str);
    setStringProperty("pickingStyle", str);
  }

  private void timeout(int index) throws ScriptException {
    
    
    
    
    
    
    String name = null;
    String script = null;
    int mSec = 0;
    if (statementLength == index) {
      showString(viewer.showTimeout(null));
      return;
    }
    for (int i = index; i < statementLength; i++)
      switch (getToken(i).tok) {
      case Token.id:
        name = parameterAsString(++i);
        break;
      case Token.off:
        break;
      case Token.integer:
        mSec = intParameter(i);
        break;
      case Token.decimal:
        mSec = (int) (floatParameter(i) * 1000);
        break;
      default:
        if (name == null)
          name = parameterAsString(i);
        else if (script == null)
          script = parameterAsString(i);
        else
          error(ERROR_invalidArgument);
        break;
    }
    if (!isSyntaxCheck)
      viewer.setTimeout(name, mSec, script);
  }

  private void setUserColors() throws ScriptException {
    Vector v = new Vector();
    for (int i = 2; i < statementLength; i++) {
      int argb = getArgbParam(i);
      v.addElement(new Integer(argb));
      i = iToken;
    }
    if (isSyntaxCheck)
      return;
    int n = v.size();
    int[] scale = new int[n];
    for (int i = n; --i >= 0;)
      scale[i] = ((Integer) v.elementAt(i)).intValue();
    Viewer.setUserScale(scale);
  }

  private void setVariable(int pt, int ptMax, String key,
                           int setType) throws ScriptException {

    
    

    

    BitSet bs = null;
    String propertyName = "";
    int tokProperty = Token.nada;
    boolean isArrayItem = (statement[0].intValue == '[');
    boolean settingProperty = false;
    boolean isExpression = false;
    boolean settingData = (key.startsWith("property_"));
    ScriptVariable t = (settingData ? null : getContextVariableAsVariable(key));
    boolean isUserVariable = (t != null);

    if (pt > 0 && tokAt(pt - 1) == Token.expressionBegin) {
      bs = expression(pt - 1);
      pt = iToken + 1;
      isExpression = true;
    }
    if (tokAt(pt) == Token.per) {
      settingProperty = true;
      ScriptVariable token = getBitsetPropertySelector(++pt, true);
      if (token == null)
        error(ERROR_invalidArgument);
      if (tokAt(++pt) != Token.opEQ)
        error(ERROR_invalidArgument);
      pt++;
      tokProperty = token.intValue;
      propertyName = (String) token.value;
    }
    if (isExpression && !settingProperty)
      error(ERROR_invalidArgument);

    

    Object v = parameterExpression(pt, ptMax, key, true, -1, isArrayItem, null,
        null);
    if (v == null)
      return;
    int nv = ((Vector) v).size();
    if (nv == 0 || !isArrayItem && nv > 1 || isArrayItem && nv != 3)
      error(ERROR_invalidArgument);
    if (isSyntaxCheck)
      return;
    ScriptVariable tv = (ScriptVariable) ((Vector) v).get(isArrayItem ? 2 : 0);

    

    boolean needVariable = (!isUserVariable && !isExpression && !settingData 
        && (isArrayItem || settingProperty || !(tv.value instanceof String
        || tv.tok == Token.integer || tv.value instanceof Integer 
        || tv.value instanceof Float || tv.value instanceof Boolean)));

    if (needVariable) {
      t = viewer.getOrSetNewVariable(key, true);
      if (t == null) { 
        error(ERROR_invalidArgument);
      }
      isUserVariable = true;
    }

    if (isArrayItem) {

      

      int index = ScriptVariable.iValue((ScriptVariable) ((Vector) v).get(0));
      t.setSelectedValue(index, tv);
      return;
    }
    if (settingProperty) {
      if (!isExpression) {
        if (!(t.value instanceof BitSet))
          error(ERROR_invalidArgument);
        bs = (BitSet) t.value;
      }
      if (propertyName.startsWith("property_")) {
        viewer.setData(propertyName, new Object[] { propertyName,
            ScriptVariable.sValue(tv), BitSetUtil.copy(bs) }, viewer.getAtomCount(), 0, 0,
            tv.tok == Token.list ? Integer.MAX_VALUE : Integer.MIN_VALUE, 0);
        return;
      }
      setBitsetProperty(bs, tokProperty, ScriptVariable.iValue(tv),
          ScriptVariable.fValue(tv), tv);
      return;
    }

    if (isUserVariable) {
      t.set(tv);
      return;
    }

    v = ScriptVariable.oValue(tv);

    if (key.startsWith("property_")) {
      int n = viewer.getAtomCount();
      if (v instanceof String[])
        v = TextFormat.join((String[]) v, '\n', 0);
      viewer.setData(key,
          new Object[] { key, "" + v, BitSetUtil.copy(viewer.getSelectionSet()) }, n, 0, 0,
          Integer.MIN_VALUE, 0);
      return;
    }
    String str;
    if (v instanceof Boolean) {
      setBooleanProperty(key, ((Boolean) v).booleanValue());
    } else if (v instanceof Integer) {
      setIntProperty(key, ((Integer) v).intValue());
    } else if (v instanceof Float) {
      setFloatProperty(key, ((Float) v).floatValue());
    } else if (v instanceof String) {
      setStringProperty(key, (String) v);
    } else if (v instanceof BondSet) {
      setStringProperty(key, Escape.escape((BitSet) v, false));
    } else if (v instanceof BitSet) {
      setStringProperty(key, Escape.escape((BitSet) v));
    } else if (v instanceof Point3f) {
      str = Escape.escape((Point3f) v);
      setStringProperty(key, str);
    } else if (v instanceof Point4f) {
      str = Escape.escape((Point4f) v);
      setStringProperty(key, str);
    } else {
      Logger.error("ERROR -- return from propertyExpression was " + v);
    }
  }

  private void axes(int index) throws ScriptException {
    
    TickInfo tickInfo = checkTicks(index);
    index = iToken + 1;
    int tok = tokAt(index);
    String type = optParameterAsString(index).toLowerCase();
    if (statementLength == index + 1
        && Parser.isOneOf(type, "window;unitcell;molecular")) {
      setBooleanProperty("axes" + type, true);
      return;
    }
    
    switch (tok) {
    case Token.scale:
      setFloatProperty("axesScale", floatParameter(checkLast(++index)));
      return;
    case Token.label:
      switch (tok = tokAt(index + 1)) {
      case Token.off:
      case Token.on:
        checkLength(index + 2);
        setShapeProperty(JmolConstants.SHAPE_AXES, "labels"
            + (tok == Token.on ? "On" : "Off"), null);
        return;
      }
      checkLength(index + 4);
      
      setShapeProperty(JmolConstants.SHAPE_AXES, "labels", new String[] {
          parameterAsString(++index), parameterAsString(++index),
          parameterAsString(++index) });
      return;
    }
    
    if (type.equals("position")) {
      Point3f xyp;
      if (tokAt(++index) == Token.off) {
        xyp = new Point3f();
      } else {
        xyp = xypParameter(index);
        if (xyp == null)
          error(ERROR_invalidArgument);
        index = iToken;
      }
      setShapeProperty(JmolConstants.SHAPE_AXES, "position", xyp);
      return;
    }
    int mad = getSetAxesTypeMad(index);
    if (isSyntaxCheck)
      return;
    viewer.setObjectMad(JmolConstants.SHAPE_AXES, "axes", mad);
    if (tickInfo != null)
      setShapeProperty(JmolConstants.SHAPE_AXES, "tickInfo", tickInfo);
  }

  private void boundbox(int index) throws ScriptException {
    TickInfo tickInfo = checkTicks(index);
    index = iToken + 1;
    float scale = 1;
    if (tokAt(index) == Token.scale) {
      scale = floatParameter(++index);
      if (!isSyntaxCheck && scale == 0)
        error(ERROR_invalidArgument);
      index++;
      if (index == statementLength) {
        if (!isSyntaxCheck)
          viewer.setBoundBox(null, null, true, scale);
        return;
      }  
    }
    boolean byCorner = (tokAt(index) == Token.corners);
    if (byCorner)
      index++;
    if (isCenterParameter(index)) {
      expressionResult = null;
      int index0 = index;
      Point3f pt1 = centerParameter(index);
      index = iToken + 1;
      if (byCorner || isCenterParameter(index)) {
        
        
        Point3f pt2 = (byCorner ? centerParameter(index) : getPoint3f(index,
            true));
        index = iToken + 1;
        if (!isSyntaxCheck)
          viewer.setBoundBox(pt1, pt2, byCorner, scale);
      } else if (expressionResult != null && expressionResult instanceof BitSet) {
        
        if (!isSyntaxCheck)
          viewer.calcBoundBoxDimensions((BitSet) expressionResult, scale);
      } else if (expressionResult == null && tokAt(index0) == Token.dollarsign) {
          if (isSyntaxCheck)
            return;
          Point3f[] bbox = getObjectBoundingBox(objectNameParameter(++index0));
          if (bbox == null)
            error(ERROR_invalidArgument);
          viewer.setBoundBox(bbox[0], bbox[1], true, scale);
          index = iToken + 1;
      } else {
        error(ERROR_invalidArgument);
      }
      if (index == statementLength)
        return;
    }
    int mad = getSetAxesTypeMad(index);
    if (isSyntaxCheck)
      return;
    if (tickInfo != null)
      setShapeProperty(JmolConstants.SHAPE_BBCAGE, "tickInfo", tickInfo);
    viewer.setObjectMad(JmolConstants.SHAPE_BBCAGE, "boundbox", mad);
  }

  private TickInfo checkTicks(int index) throws ScriptException {
    iToken = index - 1;
    if (tokAt(index) != Token.ticks)
      return null;
    TickInfo tickInfo;
    String str = " ";
    if (tokAt(index + 1) == Token.identifier) {
      str = parameterAsString(++index).toLowerCase();
      if (!str.equals("x") && !str.equals("y") && !str.equals("z"))
        error(ERROR_invalidArgument);
    }
    if (tokAt(++index) == Token.none) {
      tickInfo = new TickInfo(null);
      tickInfo.type = str;
      iToken = index;
      return tickInfo;
    }
    tickInfo = new TickInfo(getPoint3f(index, false));
    tickInfo.type = str;
    if (tokAt(iToken + 1) == Token.format)
      tickInfo.tickLabelFormats = stringParameterSet(iToken + 2);
    if (tokAt(iToken + 1) == Token.scale) {
      if (isFloatParameter(iToken + 2)) {
        float f = floatParameter(iToken + 2);
        tickInfo.scale = new Point3f(f, f, f);
      } else if (tokAt(iToken + 2) == Token.unitcell) {
        tickInfo.scale = new Point3f(
            1/viewer.getUnitCellInfo(JmolConstants.INFO_A),
            1/viewer.getUnitCellInfo(JmolConstants.INFO_B),
            1/viewer.getUnitCellInfo(JmolConstants.INFO_C));
        if (Float.isNaN(tickInfo.scale.x))
          tickInfo.scale = null;
        iToken += 2;
      } else {
        tickInfo.scale = getPoint3f(iToken + 2, true);
      }
    }
    if (tokAt(iToken + 1) == Token.first)
      tickInfo.first = floatParameter(iToken + 2);
    if (tokAt(iToken + 1) == Token.point)
      tickInfo.reference = centerParameter(iToken + 2);
    return tickInfo;
  }

  private void unitcell(int index) throws ScriptException {
    int icell = Integer.MAX_VALUE;
    int mad = Integer.MAX_VALUE;
    Point3f pt = null;
    TickInfo tickInfo = checkTicks(index);
    index = iToken;
    if (statementLength == index + 2) {
      if (getToken(index + 1).tok == Token.integer 
          && intParameter(index + 1) >= 111)
        icell = intParameter(++index);
    } else if (statementLength > index + 1) {
      pt = (Point3f) getPointOrPlane(++index, false, true, false, true, 3, 3);
      index = iToken;
    }
    mad = getSetAxesTypeMad(++index);
    checkLast(iToken);
    if (isSyntaxCheck)
      return;
    if (icell != Integer.MAX_VALUE)
      viewer.setCurrentUnitCellOffset(icell);
    viewer.setObjectMad(JmolConstants.SHAPE_UCCAGE, "unitCell", mad);
    if (pt != null)
      viewer.setCurrentUnitCellOffset(pt);
    if (tickInfo != null)
      setShapeProperty(JmolConstants.SHAPE_UCCAGE, "tickInfo", tickInfo);
  }

  private void frank(int index) throws ScriptException {
    setBooleanProperty("frank", booleanParameter(index));
  }

  private void selectionHalo(int pt) throws ScriptException {
    boolean showHalo = false;
    switch (pt == statementLength ? Token.on : getToken(pt).tok) {
    case Token.on:
    case Token.selected:
      showHalo = true;
    case Token.off:
    case Token.none:
    case Token.normal:
      setBooleanProperty("selectionHalos", showHalo);
      break;
    default:
      error(ERROR_invalidArgument);
    }
  }

  private void save() throws ScriptException {
    if (statementLength > 1) {
      String saveName = optParameterAsString(2);
      switch (tokAt(1)) {
      case Token.rotation:
        if (!isSyntaxCheck)
          viewer.saveOrientation(saveName);
        return;
      case Token.orientation:
        if (!isSyntaxCheck)
          viewer.saveOrientation(saveName);
        return;
      case Token.bonds:
        if (!isSyntaxCheck)
          viewer.saveBonds(saveName);
        return;
      case Token.state:
        if (!isSyntaxCheck)
          viewer.saveState(saveName);
        return;
      case Token.structure:
        if (!isSyntaxCheck)
          viewer.saveStructure(saveName);
        return;
      case Token.coord:
        if (!isSyntaxCheck)
          viewer.saveCoordinates(saveName, viewer.getSelectionSet());
        return;
      case Token.selection:
        if (!isSyntaxCheck)
          viewer.saveSelection(saveName);
        return;
      }
    }
    error(ERROR_what, "SAVE",
        "bonds? coordinates? orientation? selection? state? structure?");
  }

  private void restore() throws ScriptException {
    
    if (statementLength > 1) {
      String saveName = optParameterAsString(2);
      if (getToken(1).tok != Token.orientation)
        checkLength23();
      float timeSeconds;
      switch (getToken(1).tok) {
      case Token.rotation:
        timeSeconds = (statementLength > 3 ? floatParameter(3) : 0);
        if (timeSeconds < 0)
          error(ERROR_invalidArgument);
        if (!isSyntaxCheck)
          viewer.restoreRotation(saveName, timeSeconds);
        return;
      case Token.orientation:
        timeSeconds = (statementLength > 3 ? floatParameter(3) : 0);
        if (timeSeconds < 0)
          error(ERROR_invalidArgument);
        if (!isSyntaxCheck)
          viewer.restoreOrientation(saveName, timeSeconds);
        return;
      case Token.bonds:
        if (!isSyntaxCheck)
          viewer.restoreBonds(saveName);
        return;
      case Token.coord:
        if (isSyntaxCheck)
          return;
        String script = viewer.getSavedCoordinates(saveName);
        if (script == null)
          error(ERROR_invalidArgument);
        runScript(script);
        return;
      case Token.state:
        if (isSyntaxCheck)
          return;
        String state = viewer.getSavedState(saveName);
        if (state == null)
          error(ERROR_invalidArgument);
        runScript(state);
        return;
      case Token.structure:
        if (isSyntaxCheck)
          return;
        String shape = viewer.getSavedStructure(saveName);
        if (shape == null)
          error(ERROR_invalidArgument);
        runScript(shape);
        return;
      case Token.selection:
        if (!isSyntaxCheck)
          viewer.restoreSelection(saveName);
        return;
      }
    }
    error(ERROR_what, "RESTORE",
        "bonds? coords? orientation? selection? state? structure?");
  }

  String write(Token[] args) throws ScriptException {
    int pt = 0;
    boolean isApplet = viewer.isApplet();
    boolean isCommand = false;
    String driverList = viewer.getExportDriverList();
    if (args == null) {
      args = statement;
      isCommand = true;
      pt++;
    }
    int argCount = (isCommand ? statementLength : args.length);
    int tok = (isCommand && args.length == 1 ? Token.clipboard
        : tokAt(pt, args));
    int len = 0;
    int width = -1;
    int height = -1;
    String type = "SPT";
    String data = "";
    String type2 = "";
    String fileName = null;
    boolean isCoord = false;
    boolean isShow = false;
    boolean isExport = false;
    BitSet bsFrames = null;
    String localPath = null;
    String remotePath = null;
    String val = null;
    int quality = Integer.MIN_VALUE;
    if (tok == Token.string) {
      Token t = Token.getTokenFromName(ScriptVariable.sValue(args[pt]));
      if (t != null)
        tok = t.tok;
    }
    switch (tok) {
    case Token.pointgroup:
      type = "PGRP";
      pt++;
      type2 = ScriptVariable.sValue(tokenAt(pt, args)).toLowerCase();
      if (type2.equals("draw"))
        pt++;
      break;
    case Token.quaternion:
      pt++;
      type2 = ScriptVariable.sValue(tokenAt(pt, args)).toLowerCase();
      if (Parser.isOneOf(type2, "w;x;y;z;a;r"))
        pt++;
      else
        type2 = "w";
      type = ScriptVariable.sValue(tokenAt(pt, args)).toLowerCase();
      boolean isDerivative = (type.indexOf("deriv") == 0 || type
          .indexOf("diff") == 0);
      if (isDerivative || type2.equals("a") || type2.equals("r")) {
        type2 += " difference" + (type.indexOf("2") >= 0 ? "2" : "");
        if (isDerivative)
          type = ScriptVariable.sValue(tokenAt(++pt, args)).toLowerCase();
      }
      if (type.equals("draw")) {
        type2 += " draw";
        pt++;
      }
      type2 = "quaternion " + type2;
      type = "QUAT";
      break;
    case Token.ramachandran:
      pt++;
      type2 = ScriptVariable.sValue(tokenAt(pt, args)).toLowerCase();
      if (Parser.isOneOf(type2, "r;c;p"))
        pt++;
      else
        type2 = "";
      type = ScriptVariable.sValue(tokenAt(pt, args)).toLowerCase();
      if (type.equals("draw")) {
        type2 += " draw";
        pt++;
      }
      type2 = "ramachandran " + type2;
      type = "RAMA";
      break;
    case Token.function:
      type = "FUNCS";
      pt++;
      break;
    case Token.coord:
    case Token.data:
      type = ScriptVariable.sValue(tokenAt(++pt, args)).toLowerCase();
      type = "data";
      isCoord = true;
      break;
    case Token.state:
    case Token.script:
      val = ScriptVariable.sValue(tokenAt(++pt, args)).toLowerCase();
      while (val.equals("localpath") || val.equals("remotepath")) {
        if (val.equals("localpath"))
          localPath = ScriptVariable.sValue(tokenAt(++pt, args));
        else
          remotePath = ScriptVariable.sValue(tokenAt(++pt, args));
        val = ScriptVariable.sValue(tokenAt(++pt, args)).toLowerCase();
      }
      type = "SPT";
      break;
    case Token.mo:
      type = "MO";
      pt++;
      break;
    case Token.pmesh:
      type = "PMESH";
      pt++;
      break;
    case Token.mesh:
      type = "MESH";
      pt++;
      break;
    case Token.isosurface:
      type = "ISO";
      pt++;
      break;
    case Token.history:
      type = "HIS";
      pt++;
      break;
    case Token.var:
      pt += 2;
      type = "VAR";
      break;
    case Token.file:
      type = "FILE";
      pt++;
      break;
    case Token.image:
    case Token.identifier:
    case Token.string:
    case Token.frame:
      type= ScriptVariable.sValue(tokenAt(pt, args)).toLowerCase();
      if (tok == Token.image) {
        pt++;
      } else if (tok == Token.frame) {
        BitSet bsAtoms;
        if (pt + 1 < argCount && args[++pt].tok == Token.expressionBegin
            || args[pt].tok == Token.bitset) {
          bsAtoms = expression(args, pt, 0, true, false, true, true);
          pt = iToken + 1;
        } else {
          bsAtoms = viewer.getModelAtomBitSet(-1, false);
        }
        if (!isSyntaxCheck)
          bsFrames = viewer.getModelBitSet(bsAtoms, true);
      } else if (Parser.isOneOf(type, driverList.toLowerCase())) {
        
        pt++;
        type = type.substring(0, 1).toUpperCase() + type.substring(1);
        isExport = true;
        fileName = "Jmol." + type;
      } else if (type.equals("menu")) {
        pt++;
        type = "MENU";
      } else if (type.equals("zip")) {
        type = "ZIP";
        pt++;
      } else if (type.equals("zipall")) {
        type = "ZIPALL";
        pt++;
      } else {
        type = "(image)";
      }
      if (tokAt(pt, args) == Token.integer) {
        width = ScriptVariable.iValue(tokenAt(pt++, args));
        height = ScriptVariable.iValue(tokenAt(pt++, args));
      }
      break;
    }
    val = ScriptVariable.sValue(tokenAt(pt, args));
    if (val.equalsIgnoreCase("clipboard")) {
      if (isSyntaxCheck)
        return "";
      
      
      
    } else if (Parser.isOneOf(val.toLowerCase(), "png;jpg;jpeg;jpg64;jpeg64")
        && tokAt(pt + 1, args) == Token.integer) {
      quality = ScriptVariable.iValue(tokenAt(++pt, args));
    } else if (Parser.isOneOf(val.toLowerCase(), "xyz;mol;pdb;cml")) {
      type = val.toUpperCase();
      if (pt + 1 == argCount)
        pt++;
    }

    

    
    
    
    

    if (type.equals("(image)")
        && Parser.isOneOf(val.toUpperCase(),
            "GIF;JPG;JPG64;JPEG;JPEG64;PNG;PPM")) {
      type = val.toUpperCase();
      pt++;
    }

    if (pt + 2 == argCount) {
      data = ScriptVariable.sValue(tokenAt(++pt, args));
      if (data.length() > 0 && data.charAt(0) != '.')
        type = val.toUpperCase();
    }
    switch (tokAt(pt, args)) {
    case Token.nada:
      isShow = true;
      break;
    case Token.identifier:
    case Token.string:
      fileName = ScriptVariable.sValue(tokenAt(pt, args));
      if (pt == argCount - 3 && tokAt(pt + 1, args) == Token.per) {
        
        
        fileName += "." + ScriptVariable.sValue(tokenAt(pt + 2, args));
      }
      if (type != "VAR" && pt == 1)
        type = "image";
      else if (fileName.length() > 0 && fileName.charAt(0) == '.'
          && (pt == 2 || pt == 3)) {
        fileName = ScriptVariable.sValue(tokenAt(pt - 1, args)) + fileName;
        if (type != "VAR" && pt == 2)
          type = "image";
      }
      if (fileName.equalsIgnoreCase("clipboard"))
        fileName = null;
      break;
    case Token.clipboard:
      break;
    default:
      error(ERROR_invalidArgument);
    }
    if (type.equals("image") || type.equals("frame")) {
      if (fileName != null && fileName.indexOf(".") >= 0)
        type = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
      else
        type = "JPG";
      if (type.equals("MNU"))
        type = "MENU";
      else if (type.equals("WRL") || type.equals("VRML")) {
        type = "Vrml";
        isExport = true;
      } else if (type.equals("X3D")) {
        type = "X3d";
        isExport = true;
      } else if (type.equals("IDTF")) {
        type = "Idtf";
        isExport = true;
      } else if (type.equals("MA")) {
        type = "Maya";
        isExport = true;
      } else if (type.equals("JVXL")) {
        type = "ISOX";
      } else if (type.equals("XJVXL")) {
        type = "ISOX";
      } else if (type.equals("MESH")) {
        type = "MESH";
      } else if (type.equals("JMOL")) {
        type = "ZIPALL";
      }
    }
    if (type.equals("data")) {
      if (fileName != null && fileName.indexOf(".") >= 0)
        type = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
      else
        type = "XYZ";
    }
    boolean isImage = Parser.isOneOf(type, "GIF;JPEG64;JPEG;JPG64;JPG;PPM;PNG");
    if (isImage && (isApplet && !viewer.isSignedApplet() || isShow))
      type = "JPG64";
    if (!isImage
        && !isExport
        && !Parser.isOneOf(type,
            "ZIP;ZIPALL;SPT;HIS;MO;ISO;ISOX;MESH;PMESH;VAR;FILE;CML;XYZ;MENU;MOL;PDB;PGRP;QUAT;RAMA;FUNCS;"))
      error(
          ERROR_writeWhat,
          "ALL|COORDS|FILE|FUNCTIONS|HISTORY|IMAGE|ISOSURFACE|MENU|MO|POINTGROUP|QUATERNION [w,x,y,z] [derivative]"
              + "|RAMACHANDRAN|STATE|VAR x  CLIPBOARD",
          "JPG|JPG64|PNG|GIF|PPM|SPT|JVXL|XJVXL|MESH|PMESH|CML|XYZ|MOL|PDB|"
              + driverList.toUpperCase().replace(';', '|'));
    if (isSyntaxCheck)
      return "";
    data = type.intern();
    Object bytes = null;
    if (isExport) {
      
      boolean isPovRay = type.equals("Povray");
      data = viewer.generateOutput(data, isPovRay ? fileName : null, width,
          height);
      if (data == null || data.length() == 0)
        return "";
      if (isPovRay) {
        if (!isCommand)
          return data;
        fileName = data.substring(data.indexOf("File created: ") + 14);
        fileName = fileName.substring(0, fileName.indexOf("\n"));
        fileName = fileName.substring(0, fileName.lastIndexOf(" ("));
        String msg = viewer.createImage(fileName + ".ini", "ini", data,
            Integer.MIN_VALUE, 0, 0, null);
        if (msg != null) {
          if (!msg.startsWith("OK"))
            evalError(msg, null);
          scriptStatusOrBuffer("Created " + fileName + ".ini:\n\n" + data);
        }
        return "";
      }
    } else if (data == "MENU") {
      data = viewer.getMenu("");
    } else if (data == "PGRP") {
      data = viewer.getPointGroupAsString(type2.equals("draw"), null, 0, 1.0f);
    } else if (data == "PDB") {
      data = viewer.getPdbData(null);
    } else if (data == "XYZ" || data == "MOL" || data == "CML") {
      data = viewer.getData("selected", data);
    } else if (data == "QUAT" || data == "RAMA") {
      int modelIndex = viewer.getCurrentModelIndex();
      if (modelIndex < 0)
        error(ERROR_multipleModelsNotOK, "write " + type2);
      data = viewer.getPdbData(modelIndex, type2);
      type = "PDB";
    } else if (data == "FUNCS") {
      data = viewer.getFunctionCalls(null);
      type = "TXT";
    } else if (data == "FILE") {
      if (isShow)
        data = viewer.getCurrentFileAsString();
      else
        bytes = viewer.getCurrentFileAsBytes();
      if ("?".equals(fileName))
        fileName = "?Jmol." + viewer.getParameter("_fileType");
      quality = Integer.MIN_VALUE;
    } else if (data == "VAR") {
      data = ScriptVariable.sValue((ScriptVariable) getParameter(ScriptVariable
              .sValue(tokenAt(isCommand ? 2 : 1, args)), true));
      type = "TXT";
    } else if (data == "SPT") {
      if (isCoord) {
        BitSet tainted = viewer.getTaintedAtoms(AtomCollection.TAINT_COORD);
        viewer.setAtomCoordRelative(new Point3f(0, 0, 0), null);
        data = (String) viewer.getProperty("string", "stateInfo", null);
        viewer.setTaintedAtoms(tainted, AtomCollection.TAINT_COORD);
      } else {
        data = (String) viewer.getProperty("string", "stateInfo", null);
        if (localPath != null || remotePath != null)
          data = FileManager.setScriptFileReferences(data, localPath, remotePath, null);
      }
    } else if (data == "ZIP" || data == "ZIPALL") {
      data = (String) viewer.getProperty("string", "stateInfo", null);
      bytes = viewer.createImage(fileName, type, data, Integer.MIN_VALUE, -1, -1);
    } else if (data == "HIS") {
      data = viewer.getSetHistory(Integer.MAX_VALUE);
      type = "SPT";
    } else if (data == "MO") {
      data = getMoJvxl(Integer.MAX_VALUE);
      type = "XJVXL";
    } else if (data == "PMESH") {
      if ((data = getIsosurfaceJvxl(true, JmolConstants.SHAPE_PMESH)) == null)
        error(ERROR_noData);
      type = "XJVXL";
    } else if (data == "ISO" || data == "ISOX" || data == "MESH") {
      if ((data = getIsosurfaceJvxl(data == "MESH", JmolConstants.SHAPE_ISOSURFACE)) == null)
        error(ERROR_noData);
      type = (data.indexOf("<?xml") >= 0 ? "XJVXL" : "JVXL");
      if (!isShow)
        showString((String) viewer.getShapeProperty(
            JmolConstants.SHAPE_ISOSURFACE, "jvxlFileInfo"));
    } else {
      
      len = -1;
      if (quality < 0)
        quality = -1;
    }
    if (data == null)
      data = "";
    if (len == 0)
      len = (bytes == null ? data.length()
          : bytes instanceof String ? ((String) bytes).length()
              : ((byte[]) bytes).length);
    if (isImage) {
      refresh();
      if (width < 0)
        width = viewer.getScreenWidth();
      if (height < 0)
        height = viewer.getScreenHeight();
    }
    if (!isCommand)
      return data;
    if (isShow) {
      showString(data);
    } else if (bytes != null && bytes instanceof String) {
      
      scriptStatusOrBuffer((String) bytes);
    } else {
      if (bytes == null && (!isImage || fileName != null))
        bytes = data;
      String msg = viewer.createImage(fileName, type, bytes, quality, width,
          height, bsFrames);
      if (msg != null) {
        if (!msg.startsWith("OK"))
          evalError(msg, null);
        scriptStatusOrBuffer(msg
            + (isImage ? "; width=" + width + "; height=" + height : ""));
      }
    }
    return "";
  }

  private void show() throws ScriptException {
    String value = null;
    String str = parameterAsString(1);
    String msg = null;
    String name = null;
    int len = 2;
    Token token = getToken(1); 
    int tok = (token instanceof ScriptVariable ? Token.nada : token.tok);
    if (tok == Token.string) {
      token = Token.getTokenFromName(str);
      if (token != null)
        tok = token.tok;
    }
    if (tok == Token.symop && statementLength > 3) {
      Point3f pt1 = centerParameter(2);
      Point3f pt2 = centerParameter(++iToken);
      if (isSyntaxCheck)
        return;
      len = ++iToken;
      msg = viewer.getSymmetryOperation(null, 0, pt1, pt2);
    } else {
      checkLength(-3);
    }
    if (statementLength == 2 && str.indexOf("?") >= 0) {
      showString(viewer.getAllSettings(str.substring(0, str.indexOf("?"))));
      return;
    }
    switch (tok) {
    case Token.nada:
      msg = Escape.escape(((ScriptVariable)theToken).value);
      break;
    case Token.symop:
      if (msg == null) {
        int iop = (checkLength23() == 2 ? 0 : intParameter(2));
        if (!isSyntaxCheck)
          msg = viewer.getSymmetryOperation(null, iop, null, null);
        len = -3;
      }
      break;
    case Token.vanderwaals:
      if (statementLength == 2) {
        if (!isSyntaxCheck)
          showString(viewer.getDefaultVdwTypeNameOrData(-1));
        return;
      }
      int vdwType = JmolConstants.getVdwType(parameterAsString(2));
      if (vdwType == JmolConstants.VDW_UNKNOWN)
        error(ERROR_invalidArgument);
      if (!isSyntaxCheck)
        showString(viewer.getDefaultVdwTypeNameOrData(vdwType));
      return;
    case Token.function:
      checkLength23();
      if (!isSyntaxCheck)
        showString(viewer.getFunctionCalls(optParameterAsString(2)));
      return;
    case Token.set:
      checkLength(2);
      if (!isSyntaxCheck)
        showString(viewer.getAllSettings(null));
      return;
    case Token.url:
      
      if ((len = statementLength) == 2) {
        if (!isSyntaxCheck)
          viewer.showUrl(getFullPathName());
        return;
      }
      name = parameterAsString(2);
      if (!isSyntaxCheck)
        viewer.showUrl(name);
      return;
    case Token.color:
      str = "defaultColorScheme";
      break;
    case Token.scale3d:
      str = "scaleAngstromsPerInch";
      break;
    case Token.quaternion:
    case Token.ramachandran:
      if (isSyntaxCheck)
        return;
      int modelIndex = viewer.getCurrentModelIndex();
      if (modelIndex < 0)
        error(ERROR_multipleModelsNotOK, "show " + theToken.value);
      msg = viewer.getPdbData(modelIndex,
          theTok == Token.quaternion ? "quaternion w" : "ramachandran");
      break;
    case Token.trace:
      if (!isSyntaxCheck)
        msg = getContext(false);
      break;
    case Token.colorscheme:
      name = optParameterAsString(2);
      if (name.length() > 0)
        len = 3;
      if (!isSyntaxCheck)
        value = viewer.getColorSchemeList(name, true);
      break;
    case Token.variables:
      if (!isSyntaxCheck)
        msg = viewer.getVariableList() + getContext(true);
      break;   
    case Token.trajectory:
      if (!isSyntaxCheck)
        msg = viewer.getTrajectoryInfo();
      break;
    case Token.historylevel:
      value = "" + commandHistoryLevelMax;
      break;
    case Token.loglevel:
      value = "" + Viewer.getLogLevel();
      break;
    case Token.debugscript:
      value = "" + viewer.getDebugScript();
      break;
    case Token.strandcount:
      msg = "set strandCountForStrands "
        + viewer.getStrandCount(JmolConstants.SHAPE_STRANDS)
        + "; set strandCountForMeshRibbon "
        + viewer.getStrandCount(JmolConstants.SHAPE_MESHRIBBON);
      break;
    case Token.timeout:
      msg = viewer.showTimeout((len = statementLength) == 2 
          ? null : parameterAsString(2));
      break;
    case Token.defaultlattice:
      value = Escape.escape(viewer.getDefaultLattice());
      break;      
    case Token.minimize:
      if (!isSyntaxCheck)
        msg = viewer.getMinimizationInfo();
      break;
    case Token.axes:
      switch (viewer.getAxesMode()) {
      case JmolConstants.AXES_MODE_UNITCELL:
        msg = "set axesUnitcell";
        break;
      case JmolConstants.AXES_MODE_BOUNDBOX:
        msg = "set axesWindow";
        break;
      default:
        msg = "set axesMolecular";
      }
      break;
    case Token.bondmode:
      msg = "set bondMode " + (viewer.getBondSelectionModeOr() ? "OR" : "AND");
      break;
    case Token.strands:
      if (!isSyntaxCheck)
        msg = "set strandCountForStrands "
            + viewer.getStrandCount(JmolConstants.SHAPE_STRANDS)
            + "; set strandCountForMeshRibbon "
            + viewer.getStrandCount(JmolConstants.SHAPE_MESHRIBBON);
      break;
    case Token.hbond:
      msg = "set hbondsBackbone " + viewer.getHbondsBackbone()
          + ";set hbondsSolid " + viewer.getHbondsSolid();
      break;
    case Token.spin:
      if (!isSyntaxCheck)
        msg = viewer.getSpinState();
      break;
    case Token.ssbond:
      msg = "set ssbondsBackbone " + viewer.getSsbondsBackbone();
      break;
    case Token.display:
    case Token.selectionhalos:
      msg = "selectionHalos "
          + (viewer.getSelectionHaloEnabled() ? "ON" : "OFF");
      break;
    case Token.hetero:
      msg = "set selectHetero " + viewer.getRasmolSetting(tok);
      break;
    case Token.addhydrogens:
      msg = Escape.escapeArray(viewer.getAdditionalHydrogens(null, true));
      break;
    case Token.hydrogen:
      msg = "set selectHydrogens " + viewer.getRasmolSetting(tok);
      break;
    case Token.ambientpercent:
    case Token.diffusepercent:
    case Token.specular:
    case Token.specularpower:
    case Token.specularexponent:
      if (!isSyntaxCheck)
        msg = viewer.getSpecularState();
      break;
    case Token.save:
      if (!isSyntaxCheck)
        msg = viewer.listSavedStates();
      break;
    case Token.unitcell:
      if (!isSyntaxCheck)
        msg = viewer.getUnitCellInfoText();
      break;
    case Token.coord:
      if ((len = statementLength) == 2) {
        if (!isSyntaxCheck)
          msg = viewer.getCoordinateState(viewer.getSelectionSet());
        break;
      }
      String nameC = parameterAsString(2);
      if (!isSyntaxCheck)
        msg = viewer.getSavedCoordinates(nameC);
      break;
    case Token.state:
      if ((len = statementLength) == 2) {
        if (!isSyntaxCheck)
          msg = viewer.getStateInfo();
        break;
      }
      name = parameterAsString(2);
      if (!isSyntaxCheck)
        msg = viewer.getSavedState(name);
      break;
    case Token.structure:
      if ((len = statementLength) == 2) {
        if (!isSyntaxCheck)
          msg = viewer.getProteinStructureState();
        break;
      }
      String shape = parameterAsString(2);
      if (!isSyntaxCheck)
        msg = viewer.getSavedStructure(shape);
      break;
    case Token.data:
      String type = ((len = statementLength) == 3 ? parameterAsString(2) : null);
      if (!isSyntaxCheck) {
        Object[] data = (type == null ? this.data : viewer.getData(type));
        msg = (data == null ? "no data" : "data \""
            + data[0]
            + "\"\n"
            + (data[1] instanceof float[] ? Escape.escape((float[]) data[1],
                true) : data[1] instanceof float[][] ? Escape.escape(
                (float[][]) data[1], false) : "" + data[1]))
            + "\nend \"" + data[0] + "\";";
      }
      break;
    case Token.spacegroup:
      Hashtable info = null;
      if ((len = statementLength) == 2) {
        if (!isSyntaxCheck) {
          info = viewer.getSpaceGroupInfo(null);
        }
      } else {
        String sg = parameterAsString(2);
        if (!isSyntaxCheck)
          info = viewer.getSpaceGroupInfo(TextFormat.simpleReplace(sg, "''",
            "\""));
      }
      if (info != null)
        msg = "" + info.get("spaceGroupInfo") + info.get("symmetryInfo");
      break;
    case Token.dollarsign:
      len = 3;
      msg = setObjectProperty();
      break;
    case Token.boundbox:
      if (!isSyntaxCheck) {
        msg = viewer.getBoundBoxCommand(true);
      }
      break;
    case Token.center:
      if (!isSyntaxCheck)
        msg = "center " + Escape.escape(viewer.getRotationCenter());
      break;
    case Token.draw:
      if (!isSyntaxCheck)
        msg = (String) viewer.getShapeProperty(JmolConstants.SHAPE_DRAW,
            "command");
      break;
    case Token.file:
      
      if (statementLength == 2) {
        if (!isSyntaxCheck)
          msg = viewer.getCurrentFileAsString();
        break;
      }
      len = 3;
      value = parameterAsString(2);
      if (!isSyntaxCheck)
        msg = viewer.getFileAsString(value);
      break;
    case Token.frame:
      if (tokAt(2) == Token.all && (len = 3) > 0)
        msg = viewer.getModelFileInfoAll();
      else
        msg = viewer.getModelFileInfo();
      break;
    case Token.history:
      int n = ((len = statementLength) == 2 ? Integer.MAX_VALUE
          : intParameter(2));
      if (n < 1)
        error(ERROR_invalidArgument);
      if (!isSyntaxCheck) {
        viewer.removeCommand();
        msg = viewer.getSetHistory(n);
      }
      break;
    case Token.isosurface:
      if (!isSyntaxCheck)
        msg = (String) viewer.getShapeProperty(JmolConstants.SHAPE_ISOSURFACE,
            "jvxlDataXml");
      break;
    case Token.mo:
      if (optParameterAsString(2).equalsIgnoreCase("list")) {
        msg = viewer.getMoInfo(-1);
        len = 3;
      } else {
        int ptMO = ((len = statementLength) == 2 ? Integer.MIN_VALUE
            : intParameter(2));
        if (!isSyntaxCheck)
          msg = getMoJvxl(ptMO);
      }
      break;
    case Token.model:
      if (!isSyntaxCheck)
        msg = viewer.getModelInfoAsString();
      break;
    case Token.measurements:
      if (!isSyntaxCheck)
        msg = viewer.getMeasurementInfoAsString();
      break;
    case Token.translation:
    case Token.rotation:
    case Token.moveto:
      if (!isSyntaxCheck)
        msg = viewer.getOrientationText(tok, null);
      break;
    case Token.orientation:
      len = 2;
      if (statementLength > 3)
        break;
      switch (tok = tokAt(2)) {
      case Token.translation:
      case Token.rotation:
      case Token.moveto:
      case Token.nada:
        if (!isSyntaxCheck)
          msg = viewer.getOrientationText(tok, null);
        break;
      default:
        name = optParameterAsString(2);
        msg = viewer.getOrientationText(0, name);    
      }
      len = statementLength;
      break;
    case Token.pdbheader:
      if (!isSyntaxCheck)
        msg = viewer.getPDBHeader();
      break;
    case Token.pointgroup:
      pointGroup();
      return;
    case Token.symmetry:
      if (!isSyntaxCheck)
        msg = viewer.getSymmetryInfoAsString();
      break;
    case Token.transform:
      if (!isSyntaxCheck)
        msg = "transform:\n" + viewer.getTransformText();
      break;
    case Token.zoom:
      msg = "zoom "
          + (viewer.getZoomEnabled() ? ("" + viewer.getZoomSetting()) : "off");
      break;
    case Token.frank:
      msg = (viewer.getShowFrank() ? "frank ON" : "frank OFF");
      break;
    case Token.radius:
      str = "solventProbeRadius";
      break;
    
    case Token.chain:
    case Token.sequence:
    case Token.residue:
    case Token.selected:
    case Token.group:
    case Token.atoms:
    case Token.info:
    case Token.bonds:
      msg = viewer.getChimeInfo(tok);
      break;
    
    case Token.echo:
    case Token.fontsize:
    case Token.property: 
    case Token.help:
    case Token.solvent:
      value = "?";
      break;
    case Token.identifier:
      if (str.equalsIgnoreCase("fileHeader")) {
        if (!isSyntaxCheck)
          msg = viewer.getPDBHeader();
      } else if (str.equalsIgnoreCase("menu")) {
        if (!isSyntaxCheck)
          value = viewer.getMenu("");
      } else if (str.equalsIgnoreCase("mouse")) {
        String qualifiers = ((len = statementLength) == 2 
            ? null : parameterAsString(2));
        if (!isSyntaxCheck)
          msg = viewer.getBindingInfo(qualifiers);
      }
      break;
    }
    checkLength(len);
    if (isSyntaxCheck)
      return;
    if (msg != null)
      showString(msg);
    else if (value != null)
      showString(str + " = " + value);
    else if (str != null) {
      if (str.indexOf(" ") >= 0)
        showString(str);
      else
        showString(str + " = " + getParameterEscaped(str));
    }
  }

  private String getIsosurfaceJvxl(boolean asMesh, int iShape) {
    if (isSyntaxCheck)
      return "";
    return (String) viewer.getShapeProperty(iShape, asMesh ? "jvxlMeshXml" : "jvxlDataXml");
  }

  private String getMoJvxl(int ptMO) throws ScriptException {
    
    viewer.loadShape(JmolConstants.SHAPE_MO);
    int modelIndex = viewer.getCurrentModelIndex();
    if (modelIndex < 0)
      error(ERROR_multipleModelsNotOK, "MO isosurfaces");
    Hashtable moData = (Hashtable) viewer.getModelAuxiliaryInfo(modelIndex,
        "moData");
    if (moData == null)
      error(ERROR_moModelError);
    Integer n = (Integer) viewer.getShapeProperty(JmolConstants.SHAPE_MO, "moNumber");
    if (n == null || ((Integer)n).intValue() == 0) {
      setShapeProperty(JmolConstants.SHAPE_MO, "init", new Integer(modelIndex));
      setShapeProperty(JmolConstants.SHAPE_MO, "moData", moData);
    } else if (ptMO == Integer.MAX_VALUE) {
    }
    return (String) viewer.getShapeProperty(JmolConstants.SHAPE_MO, "showMO",
        ptMO);
  }

  private String extractCommandOption(String name) {
    int i = fullCommand.indexOf(name + "=");
    return (i < 0 ? null : Parser.getNextQuotedString(fullCommand, i));
  }

  private void draw() throws ScriptException {
    viewer.loadShape(JmolConstants.SHAPE_DRAW);
    switch (tokAt(1)) {
    case Token.list:
      if (listIsosurface(JmolConstants.SHAPE_DRAW))
        return;
      break;
    case Token.pointgroup:
      pointGroup();
      return;
    case Token.quaternion:
      dataFrame(JmolConstants.JMOL_DATA_QUATERNION);
      return;
    case Token.helix:
      dataFrame(JmolConstants.JMOL_DATA_QUATERNION);
      return;
    case Token.ramachandran:
      dataFrame(JmolConstants.JMOL_DATA_RAMACHANDRAN);
      return;
    }
    boolean havePoints = false;
    boolean isInitialized = false;
    boolean isSavedState = false;
    boolean isTranslucent = false;
    boolean isIntersect = false;
    boolean isFrame = false;
    Point4f plane;
    int tokIntersect = 0;
    float translucentLevel = Float.MAX_VALUE;
    int colorArgb = Integer.MIN_VALUE;
    int intScale = 0;
    String swidth = "";
    int iptDisplayProperty = 0;
    Point3f center = null;
    String thisId = initIsosurface(JmolConstants.SHAPE_DRAW);
    boolean idSeen = (thisId != null);
    boolean isWild = (idSeen && viewer.getShapeProperty(
        JmolConstants.SHAPE_DRAW, "ID") == null);
    for (int i = iToken; i < statementLength; ++i) {
      String propertyName = null;
      Object propertyValue = null;
      switch (getToken(i).tok) {
      case Token.unitcell:
      case Token.boundbox:
        if (isSyntaxCheck)
          break;
        Vector vp = viewer.getPlaneIntersection(theTok, null, intScale / 100f,
            0);
        intScale = 0;
        if (vp == null)
          continue;
        propertyName = "polygon";
        propertyValue = vp;
        havePoints = true;
        break;
      case Token.intersection:
        switch (getToken(++i).tok) {
        case Token.unitcell:
        case Token.boundbox:
          tokIntersect = theTok;
          isIntersect = true;
          continue;
        case Token.dollarsign:
          propertyName = "intersect";
          propertyValue = objectNameParameter(++i);
          i = iToken;
          isIntersect = true;
          havePoints = true;
          break;
        default:
          error(ERROR_invalidArgument);
        }
        break;
      case Token.polygon:
        int nVertices = intParameter(++i);
        Point3f[] points = new Point3f[nVertices];
        for (int j = 0; j < nVertices; j++, i = iToken)
          points[j] = getPoint3f(++iToken, true);
        Vector v = new Vector();
        v.add(points);
        int nTriangles = intParameter(++i);
        int[][] polygons = new int[nTriangles][];
        for (int j = 0; j < nTriangles; j++, i = iToken) {
          float[] f = floatParameterSet(++i, 3, 4);
          polygons[j] = new int[] { (int) f[0], (int) f[1], (int) f[2],
              (f.length == 3 ? 7 : (int) f[3]) };
        }
        v.add(polygons);
        propertyName = "polygon";
        propertyValue = v;
        havePoints = true;
        break;
      case Token.symop:
        String xyz = null;
        int iSym = 0;
        plane = null;
        Point3f target = null;
        switch (tokAt(++i)) {
        case Token.string:
          xyz = stringParameter(i);
          break;
        case Token.matrix4f:
          xyz = ScriptVariable.sValue(getToken(i));
          break;
        case Token.integer:
        default:
          if (!isCenterParameter(i))
            iSym = intParameter(i++);
          if (isCenterParameter(i))
            center = centerParameter(i);
          if (isCenterParameter(iToken + 1))
            target = centerParameter(++iToken);
          if (isSyntaxCheck)
            return;
          i = iToken;
        }
        BitSet bsAtoms = null;
        if (center == null && i + 1 < statementLength) {
          center = centerParameter(++i);
          
          
          bsAtoms = (tokAt(i) == Token.bitset
              || tokAt(i) == Token.expressionBegin ? expression(i) : null);
          i = iToken + 1;
        }
        checkLast(iToken);
        if (!isSyntaxCheck)
          runScript((String) viewer.getSymmetryInfo(bsAtoms, xyz, iSym, center,
              target, thisId, Token.draw));
        return;
      case Token.frame:
        isFrame = true;
        
        continue;
      case Token.leftbrace:
      case Token.point4f:
      case Token.point3f:
        
        if (theTok == Token.point4f || !isPoint3f(i)) {
          propertyValue = getPoint4f(i);
          if (isFrame) {
            checkLast(iToken);
            if (!isSyntaxCheck)
              runScript((new Quaternion((Point4f) propertyValue)).draw(
                  (thisId == null ? "frame" : thisId), " " + swidth,
                  (center == null ? new Point3f() : center), intScale / 100f));
            return;
          }
          propertyName = "planedef";
        } else {
          propertyValue = center = getPoint3f(i, true);
          propertyName = "coord";
        }
        i = iToken;
        havePoints = true;
        break;
      case Token.hkl:
      case Token.plane:
        if (!havePoints && !isIntersect && tokIntersect == 0
            && theTok != Token.hkl) {
          propertyName = "plane";
          break;
        }
        if (theTok == Token.plane) {
          plane = planeParameter(++i);
        } else {
          plane = hklParameter(++i);
        }
        i = iToken;
        if (tokIntersect != 0) {
          if (isSyntaxCheck)
            break;
          Vector vpc = viewer.getPlaneIntersection(tokIntersect, plane,
              intScale / 100f, 0);
          intScale = 0;
          if (vpc == null)
            continue;
          propertyName = "polygon";
          propertyValue = vpc;
        } else {
          propertyValue = plane;
          propertyName = "planedef";
        }
        havePoints = true;
        break;
      case Token.linedata:
        propertyName = "lineData";
        propertyValue = floatParameterSet(++i, 0, Integer.MAX_VALUE);
        i = iToken;
        havePoints = true;
        break;
      case Token.bitset:
      case Token.expressionBegin:
        propertyName = "atomSet";
        propertyValue = expression(i);
        if (isFrame)
          center = centerParameter(i);
        i = iToken;
        havePoints = true;
        break;
      case Token.list:
        propertyName = "modelBasedPoints";
        propertyValue = theToken.value;
        havePoints = true;
        break;
      case Token.comma: 
        break;
      case Token.leftsquare:
        
        propertyValue = xypParameter(i);
        if (propertyValue != null) {
          i = iToken;
          propertyName = "coord";
          havePoints = true;
          break;
        }
        if (isSavedState)
          error(ERROR_invalidArgument);
        isSavedState = true;
        break;
      case Token.rightsquare:
        if (!isSavedState)
          error(ERROR_invalidArgument);
        isSavedState = false;
        break;
      case Token.reverse:
        propertyName = "reverse";
        break;
      case Token.string:
        propertyValue = stringParameter(i);
        propertyName = "title";
        break;
      case Token.vector:
        propertyName = "vector";
        break;
      case Token.length:
        propertyValue = new Float(floatParameter(++i));
        propertyName = "length";
        break;
      case Token.decimal:
        
        propertyValue = new Float(floatParameter(i));
        propertyName = "length";
        break;
      case Token.modelindex:
        propertyName = "modelIndex";
        propertyValue = new Integer(intParameter(++i));
        break;
      case Token.integer:
        if (isSavedState) {
          propertyName = "modelIndex";
          propertyValue = new Integer(intParameter(i));
        } else {
          intScale = intParameter(i);
        }
        break;
      case Token.scale:
        if (++i >= statementLength)
          error(ERROR_numberExpected);
        switch (getToken(i).tok) {
        case Token.integer:
          intScale = intParameter(i);
          continue;
        case Token.decimal:
          intScale = (int) (floatParameter(i) * 100);
          continue;
        }
        error(ERROR_numberExpected);
        break;
      case Token.id:
        thisId = setShapeId(JmolConstants.SHAPE_DRAW, ++i, idSeen);
        isWild = (viewer.getShapeProperty(JmolConstants.SHAPE_DRAW, "ID") == null);
        i = iToken;
        break;
      case Token.modelbased:
        propertyName = "fixed";
        propertyValue = Boolean.FALSE;
        break;
      case Token.fixed:
        propertyName = "fixed";
        propertyValue = Boolean.TRUE;
        break;
      case Token.offset:
        Point3f pt = getPoint3f(++i, true);
        i = iToken;
        propertyName = "offset";
        propertyValue = pt;
        break;
      case Token.crossed:
        propertyName = "crossed";
        break;
      case Token.width:
        propertyValue = new Float(floatParameter(++i));
        propertyName = "width";
        swidth = (String) propertyName + " " + propertyValue;
        break;
      case Token.line:
        propertyName = "line";
        propertyValue = Boolean.TRUE;
        break;
      case Token.curve:
        propertyName = "curve";
        break;
      case Token.arc:
        propertyName = "arc";
        break;
      case Token.arrow:
        propertyName = "arrow";
        break;
      case Token.circle:
        propertyName = "circle";
        break;
      case Token.cylinder:
        propertyName = "cylinder";
        break;
      case Token.vertices:
        propertyName = "vertices";
        break;
      case Token.nohead:
        propertyName = "nohead";
        break;
      case Token.rotate45:
        propertyName = "rotate45";
        break;
      case Token.perpendicular:
        propertyName = "perp";
        break;
      case Token.diameter:
        float f = floatParameter(++i);
        propertyValue = new Float(f);
        propertyName = (tokAt(i) == Token.decimal ? "width" : "diameter");
        swidth = (String) propertyName
            + (tokAt(i) == Token.decimal ? " " + f : " " + ((int) f));
        break;
      case Token.dollarsign:
        
        if ((tokAt(i + 2) == Token.leftsquare || isFrame)) {
          Point3f pto = center = centerParameter(i);
          i = iToken;
          propertyName = "coord";
          propertyValue = pto;
          havePoints = true;
          break;
        }
        
        propertyValue = objectNameParameter(++i);
        propertyName = "identifier";
        havePoints = true;
        break;
      case Token.color:
      case Token.translucent:
      case Token.opaque:
        if (theTok != Token.color)
          --i;
        if (tokAt(i + 1) == Token.translucent) {
          i++;
          isTranslucent = true;
          if (isFloatParameter(i + 1))
            translucentLevel = getTranslucentLevel(++i);
        } else if (tokAt(i + 1) == Token.opaque) {
          i++;
          isTranslucent = true;
          translucentLevel = 0;
        }
        if (isColorParam(i + 1)) {
          colorArgb = getArgbParam(++i);
          i = iToken;
        } else if (!isTranslucent) {
          error(ERROR_invalidArgument);
        }
        idSeen = true;
        continue;
      default:
        if (!setMeshDisplayProperty(JmolConstants.SHAPE_DRAW, 0, theTok)) {
          if (theTok == Token.times || Token.tokAttr(theTok, Token.identifier)) {
            thisId = setShapeId(JmolConstants.SHAPE_DRAW, i, idSeen);
            i = iToken;
            break;
          }
          error(ERROR_invalidArgument);
        }
        if (iptDisplayProperty == 0)
          iptDisplayProperty = i;
        i = iToken;
        continue;
      }
      idSeen = (theTok != Token.delete);
      if (havePoints && !isInitialized && !isFrame) {
        setShapeProperty(JmolConstants.SHAPE_DRAW, "points", new Integer(
            intScale));
        isInitialized = true;
        intScale = 0;
      }
      if (havePoints && isWild)
        error(ERROR_invalidArgument);
      if (propertyName != null)
        setShapeProperty(JmolConstants.SHAPE_DRAW, propertyName, propertyValue);
    }
    if (havePoints) {
      setShapeProperty(JmolConstants.SHAPE_DRAW, "set", null);
    }
    if (colorArgb != Integer.MIN_VALUE)
      setShapeProperty(JmolConstants.SHAPE_DRAW, "color",
          new Integer(colorArgb));
    if (isTranslucent)
      setShapeTranslucency(JmolConstants.SHAPE_DRAW, "", "translucent",
          translucentLevel, null);
    if (intScale != 0) {
      setShapeProperty(JmolConstants.SHAPE_DRAW, "scale", new Integer(intScale));
    }
    if (iptDisplayProperty > 0) {
      if (!setMeshDisplayProperty(JmolConstants.SHAPE_DRAW, iptDisplayProperty,
          getToken(iptDisplayProperty).tok))
        error(ERROR_invalidArgument);
    }
  }

  private void polyhedra() throws ScriptException {
    
    boolean needsGenerating = false;
    boolean onOffDelete = false;
    boolean typeSeen = false;
    boolean edgeParameterSeen = false;
    boolean isDesignParameter = false;
    int nAtomSets = 0;
    viewer.loadShape(JmolConstants.SHAPE_POLYHEDRA);
    setShapeProperty(JmolConstants.SHAPE_POLYHEDRA, "init", null);
    String setPropertyName = "centers";
    String decimalPropertyName = "radius_";
    boolean isTranslucent = false;
    float translucentLevel = Float.MAX_VALUE;
    int color = Integer.MIN_VALUE;
    for (int i = 1; i < statementLength; ++i) {
      if (isColorParam(i)) {
        color = getArgbParam(i);
        i = iToken;
        continue;
      }
      String propertyName = null;
      Object propertyValue = null;
      switch (getToken(i).tok) {
      case Token.delete:
      case Token.on:
      case Token.off:
        if (i + 1 != statementLength || needsGenerating || nAtomSets > 1
            || nAtomSets == 0 && setPropertyName == "to")
          error(ERROR_incompatibleArguments);
        propertyName = parameterAsString(i);
        onOffDelete = true;
        break;
      case Token.opEQ:
      case Token.comma:
        continue;
      case Token.bonds:
        if (nAtomSets > 0)
          error(ERROR_invalidParameterOrder);
        needsGenerating = true;
        propertyName = "bonds";
        break;
      case Token.radius:
        decimalPropertyName = "radius";
        continue;
      case Token.integer:
      case Token.decimal:
        if (nAtomSets > 0 && !isDesignParameter)
          error(ERROR_invalidParameterOrder);
        if (theTok == Token.integer) {
          if (decimalPropertyName == "radius_") {
            propertyName = "nVertices";
            propertyValue = new Integer(intParameter(i));
            needsGenerating = true;
            break;
          }
        }
        propertyName = (decimalPropertyName == "radius_" ? "radius"
            : decimalPropertyName);
        propertyValue = new Float(floatParameter(i));
        decimalPropertyName = "radius_";
        isDesignParameter = false;
        needsGenerating = true;
        break;
      case Token.bitset:
      case Token.expressionBegin:
        if (typeSeen)
          error(ERROR_invalidParameterOrder);
        if (++nAtomSets > 2)
          error(ERROR_badArgumentCount);
        if (setPropertyName == "to")
          needsGenerating = true;
        propertyName = setPropertyName;
        setPropertyName = "to";
        propertyValue = expression(i);
        i = iToken;
        break;
      case Token.to:
        if (nAtomSets > 1)
          error(ERROR_invalidParameterOrder);
        if (getToken(i + 1).tok == Token.bitset) {
          propertyName = "toBitSet";
          propertyValue = getToken(++i).value;
          needsGenerating = true;
          break;
        } else if (!needsGenerating) {
            error(ERROR_insufficientArguments);
        }
        setPropertyName = "to";
        continue;
      case Token.facecenteroffset:
        if (!needsGenerating)
          error(ERROR_insufficientArguments);
        decimalPropertyName = "faceCenterOffset";
        isDesignParameter = true;
        continue;
      case Token.distancefactor:
        if (!needsGenerating)
          error(ERROR_insufficientArguments);
        decimalPropertyName = "distanceFactor";
        isDesignParameter = true;
        continue;
      case Token.color:
      case Token.translucent:
      case Token.opaque:
        isTranslucent = false;
        if (theTok != Token.color)
          --i;
        if (tokAt(i + 1) == Token.translucent) {
          i++;
          isTranslucent = true;
          if (isFloatParameter(++i))
            translucentLevel = getTranslucentLevel(i);
        } else if (tokAt(i + 1) == Token.opaque) {
          i++;
          isTranslucent = true;
          translucentLevel = 0;
        }
        if (isColorParam(i + 1)) {
          color = getArgbParam(i);
          i = iToken;
        } else if (!isTranslucent)
          error(ERROR_invalidArgument);
        continue;
      case Token.collapsed:
      case Token.flat:
        propertyName = "collapsed";
        propertyValue = (theTok == Token.collapsed ? Boolean.TRUE
            : Boolean.FALSE);
        if (typeSeen)
          error(ERROR_incompatibleArguments);
        typeSeen = true;
        break;
      case Token.noedges:
      case Token.edges:
      case Token.frontedges:
        if (edgeParameterSeen)
          error(ERROR_incompatibleArguments);
        propertyName = parameterAsString(i);
        edgeParameterSeen = true;
        break;
      default:
        error(ERROR_invalidArgument);
      }
      setShapeProperty(JmolConstants.SHAPE_POLYHEDRA, propertyName,
          propertyValue);
      if (onOffDelete)
        return;
    }
    if (!needsGenerating && !typeSeen && !edgeParameterSeen)
      error(ERROR_insufficientArguments);
    if (needsGenerating)
      setShapeProperty(JmolConstants.SHAPE_POLYHEDRA, "generate", null);
    if (color != Integer.MIN_VALUE)
      setShapeProperty(JmolConstants.SHAPE_POLYHEDRA, "colorThis", new Integer(
          color));
    if (isTranslucent)
      setShapeTranslucency(JmolConstants.SHAPE_POLYHEDRA, "", "translucent",
          translucentLevel, null);
  }

  private void lcaoCartoon() throws ScriptException {
    viewer.loadShape(JmolConstants.SHAPE_LCAOCARTOON);
    if (tokAt(1) == Token.list
        && listIsosurface(JmolConstants.SHAPE_LCAOCARTOON))
      return;
    setShapeProperty(JmolConstants.SHAPE_LCAOCARTOON, "init", null);
    if (statementLength == 1) {
      setShapeProperty(JmolConstants.SHAPE_LCAOCARTOON, "lcaoID", null);
      return;
    }
    boolean idSeen = false;
    String translucency = null;
    for (int i = 1; i < statementLength; i++) {
      String propertyName = null;
      Object propertyValue = null;
      switch (getToken(i).tok) {
      case Token.center:
        
        isosurface(JmolConstants.SHAPE_LCAOCARTOON);
        return;
      case Token.rotate:
        Vector3f rotAxis = new Vector3f();
        switch (getToken(++i).tok) {
        case Token.identifier:
          String str = parameterAsString(i);
          float radians = floatParameter(++i)
              * JmolConstants.radiansPerDegree;
          if (str.equalsIgnoreCase("x")) {
            rotAxis.set(radians, 0, 0);
            break;
          }
          if (str.equalsIgnoreCase("y")) {
            rotAxis.set(0, radians, 0);
            break;
          }
          if (str.equalsIgnoreCase("z")) {
            rotAxis.set(0, 0, radians);
            break;
          }
          error(ERROR_invalidArgument);
        default:
          error(ERROR_invalidArgument);
        }
        propertyName = "rotationAxis";
        propertyValue = rotAxis;
        break;
      case Token.on:
      case Token.display:
      case Token.displayed:
        propertyName = "on";
        break;
      case Token.off:
      case Token.hide:
      case Token.hidden:
        propertyName = "off";
        break;
      case Token.delete:
        propertyName = "delete";
        break;
      case Token.integer:
      case Token.decimal:
        propertyName = "scale";
        propertyValue = new Float(floatParameter(++i));
        break;
      case Token.bitset:
      case Token.expressionBegin:
        propertyName = "select";
        propertyValue = expression(i);
        i = iToken;
        break;
      case Token.color:
        translucency = setColorOptions(i + 1, JmolConstants.SHAPE_LCAOCARTOON,
            -2);
        if (translucency != null)
          setShapeProperty(JmolConstants.SHAPE_LCAOCARTOON, "settranslucency",
              translucency);
        i = iToken;
        idSeen = true;
        continue;
      case Token.translucent:
      case Token.opaque:
        setMeshDisplayProperty(JmolConstants.SHAPE_LCAOCARTOON, i, theTok);
        i = iToken;
        idSeen = true;
        continue;
      case Token.string:
        propertyValue = stringParameter(i);
        propertyName = "create";
        if (optParameterAsString(i + 1).equalsIgnoreCase("molecular")) {
          i++;
          propertyName = "molecular";
        }
        break;
      case Token.select:
        if (tokAt(i + 1) == Token.bitset
            || tokAt(i + 1) == Token.expressionBegin) {
          propertyName = "select";
          propertyValue = expression(i + 1);
          i = iToken;
        } else {
          propertyName = "selectType";
          propertyValue = parameterAsString(++i);
        }
        break;
      case Token.scale:
        propertyName = "scale";
        propertyValue = new Float(floatParameter(++i));
        break;
      case Token.lonepair:
      case Token.lp:
        propertyName = "lonePair";
        break;
      case Token.radical:
      case Token.rad:
        propertyName = "radical";
        break;        
      case Token.molecular:
        propertyName = "molecular";
        break;
      case Token.create:
        propertyValue = parameterAsString(++i);
        propertyName = "create";
        if (optParameterAsString(i + 1).equalsIgnoreCase("molecular")) {
          i++;
          propertyName = "molecular";
        }
        break;
      case Token.id:
        propertyValue = getShapeNameParameter(++i);
        i = iToken;
        if (idSeen)
          error(ERROR_invalidArgument);
        propertyName = "lcaoID";
        break;
      default:
        if (theTok == Token.times || Token.tokAttr(theTok, Token.identifier)) {
          if (theTok != Token.times)
            propertyValue = parameterAsString(i);
          if (idSeen)
            error(ERROR_invalidArgument);
          propertyName = "lcaoID";
          break;
        }
        break;
      }
      if (theTok != Token.delete)
        idSeen = true;
      if (propertyName == null)
        error(ERROR_invalidArgument);
      setShapeProperty(JmolConstants.SHAPE_LCAOCARTOON, propertyName,
          propertyValue);
    }
    setShapeProperty(JmolConstants.SHAPE_LCAOCARTOON, "clear", null);
  }

  private boolean mo(boolean isInitOnly) throws ScriptException {
    int offset = Integer.MAX_VALUE;
    BitSet bsModels = viewer.getVisibleFramesBitSet();
    Vector propertyList = new Vector();
    for (int i = bsModels.nextSetBit(0); i >= 0; i = bsModels.nextSetBit(i + 1)) {
      viewer.loadShape(JmolConstants.SHAPE_MO);
      if (tokAt(1) == Token.list && listIsosurface(JmolConstants.SHAPE_MO))
        return true;
      setShapeProperty(JmolConstants.SHAPE_MO, "init", new Integer(i));
      String title = null;
      int moNumber = ((Integer) viewer.getShapeProperty(JmolConstants.SHAPE_MO,
          "moNumber")).intValue();
      if (isInitOnly)
        return true;
      if (moNumber == 0)
        moNumber = Integer.MAX_VALUE;
      String propertyName = null;
      Object propertyValue = null;
      switch (getToken(1).tok) {
      case Token.integer:
        moNumber = intParameter(1);
        break;
      case Token.next:
        moNumber = Token.next;
        break;
      case Token.prev:
        moNumber = Token.prev;
        break;
      case Token.color:
        setColorOptions(2, JmolConstants.SHAPE_MO, 2);
        break;
      case Token.plane:
        
        propertyName = "plane";
        propertyValue = planeParameter(2);
        break;
      case Token.scale:
        propertyName = "scale";
        propertyValue = new Float(floatParameter(2));
        break;
      case Token.cutoff:
        if (tokAt(2) == Token.plus) {
          propertyName = "cutoffPositive";
          propertyValue = new Float(floatParameter(3));
        } else {
          propertyName = "cutoff";
          propertyValue = new Float(floatParameter(2));
        }
        break;
      case Token.debug:
        propertyName = "debug";
        break;
      case Token.noplane:
        propertyName = "plane";
        break;
      case Token.pointsperangstrom:
      case Token.resolution:
        propertyName = "resolution";
        propertyValue = new Float(floatParameter(2));
        break;
      case Token.squared:
        propertyName = "squareData";
        propertyValue = Boolean.TRUE;
        break;
      case Token.titleformat:
        if (2 < statementLength && tokAt(2) == Token.string) {
          propertyName = "titleFormat";
          propertyValue = parameterAsString(2);
        }
        break;
      case Token.homo:
      case Token.lumo:
        if ((offset = moOffset(1)) == Integer.MAX_VALUE)
          error(ERROR_invalidArgument);
        moNumber = 0;
        break;
      case Token.identifier:
        error(ERROR_invalidArgument);
      default:
        int ipt = iToken;
        if (!setMeshDisplayProperty(JmolConstants.SHAPE_MO, 0, theTok))
          error(ERROR_invalidArgument);
        setShapeProperty(JmolConstants.SHAPE_MO, "setProperties", propertyList);
        setMeshDisplayProperty(JmolConstants.SHAPE_MO, ipt, tokAt(ipt));
      return true;
      }
      if (propertyName != null)
        addShapeProperty(propertyList, propertyName, propertyValue);
      if (moNumber != Integer.MAX_VALUE) {
        if (tokAt(2) == Token.string)
          title = parameterAsString(2);
        if (!isSyntaxCheck)
          viewer.setCursor(Viewer.CURSOR_WAIT);
        setMoData(propertyList, moNumber, offset, i, title);
        addShapeProperty(propertyList, "finalize", null);
        setShapeProperty(JmolConstants.SHAPE_MO, "setProperties", propertyList);
      }

    }
    return true;
  }

  private String setColorOptions(int index, int iShape, int nAllowed)
      throws ScriptException {
    getToken(index);
    String translucency = "opaque";
    if (theTok == Token.translucent) {
      translucency = "translucent";
      if (nAllowed < 0) {
        float value = (isFloatParameter(index + 1) ? floatParameter(++index)
            : Float.MAX_VALUE);
        setShapeTranslucency(iShape, null, "translucent", value, null);
      } else {
        setMeshDisplayProperty(iShape, index, theTok);
      }
    } else if (theTok == Token.opaque) {
      if (nAllowed >= 0)
        setMeshDisplayProperty(iShape, index, theTok);
    } else {
      iToken--;
    }
    nAllowed = Math.abs(nAllowed);
    for (int i = 0; i < nAllowed; i++) {
      if (isColorParam(iToken + 1)) {
        setShapeProperty(iShape, "colorRGB",
            new Integer(getArgbParam(++iToken)));
      } else if (iToken < index) {
        error(ERROR_invalidArgument);
      } else {
        break;
      }
    }
    return translucency;
  }

  private int moOffset(int index) throws ScriptException {
    boolean isHomo = (getToken(index).tok == Token.homo);
    int offset = Integer.MAX_VALUE;
    offset = (isHomo ? 0 : 1);
    int tok = tokAt(++index); 
    if (tok == Token.integer && intParameter(index) < 0)
      offset += intParameter(index);
    else if (tok == Token.plus)
      offset += intParameter(++index);
    else if (tok == Token.minus)
      offset -= intParameter(++index);
    return offset;
  }

  private void setMoData(Vector propertyList, int moNumber, int offset, int modelIndex,
                         String title) throws ScriptException {
    if (isSyntaxCheck)
      return;
    if (modelIndex < 0) {
      modelIndex = viewer.getCurrentModelIndex();
      if (modelIndex < 0)
        error(ERROR_multipleModelsNotOK, "MO isosurfaces");
    }
    Hashtable moData = (Hashtable) viewer.getModelAuxiliaryInfo(modelIndex,
        "jmolSurfaceInfo");
    int firstMoNumber = moNumber;
    if (moData != null && ((String) moData.get("surfaceDataType")).equals("mo")) {
      
      
    } else {
      moData = (Hashtable) viewer.getModelAuxiliaryInfo(modelIndex, "moData");
      if (moData == null)
        error(ERROR_moModelError);
      int lastMoNumber = (moData.containsKey("lastMoNumber") ? ((Integer) moData
          .get("lastMoNumber")).intValue()
          : 0);
      if (moNumber == Token.prev)
        moNumber = lastMoNumber - 1;
      else if (moNumber == Token.next)
        moNumber = lastMoNumber + 1;
      Vector mos = (Vector) (moData.get("mos"));
      int nOrb = (mos == null ? 0 : mos.size());
      if (nOrb == 0)
        error(ERROR_moCoefficients);
      if (nOrb == 1 && moNumber > 1)
        error(ERROR_moOnlyOne);
      if (offset != Integer.MAX_VALUE) {
        
        if (moData.containsKey("HOMO")) {
          moNumber = ((Integer) moData.get("HOMO")).intValue() + offset;
        } else {
          for (int i = 0; i < nOrb; i++) {
            Hashtable mo = (Hashtable) mos.get(i);
            if (!mo.containsKey("occupancy"))
              error(ERROR_moOccupancy);
            if (((Float) mo.get("occupancy")).floatValue() == 0) {
              moNumber = i + offset;
              break;
            }
          }
        }
        Logger.info("MO " + moNumber);
      }
      if (moNumber < 1 || moNumber > nOrb)
        error(ERROR_moIndex, "" + nOrb);
    }
    moData.put("lastMoNumber", new Integer(moNumber));
    addShapeProperty(propertyList, "moData", moData);
    if (title != null)
      addShapeProperty(propertyList, "title", title);
    if (firstMoNumber < 0)
      addShapeProperty(propertyList, "charges", viewer.getAtomicCharges());
    addShapeProperty(propertyList, "molecularOrbital", new Integer(
        firstMoNumber < 0 ? -moNumber : moNumber));
    addShapeProperty(propertyList, "clear", null);
  }

  private String initIsosurface(int iShape) throws ScriptException {

    

    setShapeProperty(iShape, "init", fullCommand);
    iToken = 0;
    if (tokAt(1) == Token.delete || tokAt(2) == Token.delete
        && tokAt(++iToken) == Token.all) {
      setShapeProperty(iShape, "delete", null);
      iToken += 2;
      if (statementLength > iToken) {
        setShapeProperty(iShape, "init", fullCommand);
        setShapeProperty(iShape, "thisID", JmolConstants.PREVIOUS_MESH_ID);
      }
      return null;
    }
    iToken = 1;
    if (!setMeshDisplayProperty(iShape, 0, tokAt(1))) {
      setShapeProperty(iShape, "thisID", JmolConstants.PREVIOUS_MESH_ID);
      if (iShape != JmolConstants.SHAPE_DRAW)
        setShapeProperty(iShape, "title", new String[] { thisCommand });
      if (tokAt(2) == Token.times && tokAt(1) != Token.id) {
        String id = setShapeId(iShape, 1, false);
        iToken++;
        return id;
      }
    }
    return null;
  }

  private String getNextComment() {
    
    String nextCommand = getCommand(pc + 1, false, true);
    return (nextCommand.startsWith("#") ? nextCommand : "");
  }

  private boolean listIsosurface(int iShape) throws ScriptException {
    if (getToken(1).value instanceof String[]) 
      return false;
    checkLength(2);
    if (!isSyntaxCheck)
      showString((String) viewer.getShapeProperty(iShape, "list"));
    return true;
  }

  private void isosurface(int iShape) throws ScriptException {
    viewer.loadShape(iShape);
    if (tokAt(1) == Token.list && listIsosurface(iShape))
      return;
    int colorRangeStage = 0;
    int signPt = 0;
    int iptDisplayProperty = 0;
    boolean isIsosurface = (iShape == JmolConstants.SHAPE_ISOSURFACE);
    boolean isPmesh = (iShape == JmolConstants.SHAPE_PMESH);
    boolean isPlot3d = (iShape == JmolConstants.SHAPE_PLOT3D);

    boolean surfaceObjectSeen = false;
    boolean planeSeen = false;
    boolean doCalcArea = false;
    boolean doCalcVolume = false;
    boolean isCavity = false;
    boolean haveRadius = false;
    boolean isFxy = false;
    boolean isColorMesh = false;
    float[] nlmZ = new float[5];
    float[] data = null;
    int thisSetNumber = 0;
    int nFiles = 0;
    int nX, nY, nZ, ptX, ptY;
    BitSet bs;
    Vector v;
    Point3f[] pts;
    String str = null;
    int modelIndex = (isSyntaxCheck ? 0 : viewer.getCurrentModelIndex());
    if (!isSyntaxCheck)
      viewer.setCursor(Viewer.CURSOR_WAIT);
    boolean idSeen = (initIsosurface(iShape) != null);
    boolean isWild = (idSeen && viewer.getShapeProperty(iShape, "ID") == null);
    boolean isColorSchemeTranslucent = false;
    String translucency = null;
    String colorScheme = null;
    short[] discreteColixes = null;
    Vector propertyList = new Vector();
    if (isPmesh || isPlot3d)
      addShapeProperty(propertyList, "fileType", "Pmesh");
    for (int i = iToken; i < statementLength; ++i) {
      if (isColorParam(i)) {
        if (i != signPt)
          error(ERROR_invalidParameterOrder);
        addShapeProperty(propertyList,
            (isColorMesh ? "colorMesh" : "colorRGB"), new Integer(
                getArgbParam(i)));
        i = iToken;
        signPt = i + 1;
        idSeen = true;
        continue;
      }
      String propertyName = null;
      Object propertyValue = null;
      getToken(i);
      if (theTok == Token.identifier
          && (str = parameterAsString(i)).equalsIgnoreCase("inline"))
        theTok = Token.string;
      switch (theTok) {
      case Token.boundbox:
        if (!isSyntaxCheck) {
          if (fullCommand.indexOf("# BBOX=") >= 0) {
            String[] bbox = TextFormat.split(extractCommandOption("# BBOX"),
                ',');
            pts = new Point3f[] { (Point3f) Escape.unescapePoint(bbox[0]),
                (Point3f) Escape.unescapePoint(bbox[1]) };
          } else {
            pts = viewer.getBoundBoxVertices();
          }
          addShapeProperty(propertyList, "commandOption", "BBOX=\""
              + Escape.escape(pts[0]) + "," + Escape.escape(pts[1]) + "\"");
          addShapeProperty(propertyList, "boundingBox", pts);
        }
        continue;
      case Token.pmesh:
        isPmesh = true;
        addShapeProperty(propertyList, "fileType", "Pmesh");
        continue;
      case Token.within:
        float distance = floatParameter(++i);
        propertyName = "withinPoints";
        Point3f ptc = centerParameter(++i);
        BoxInfo bbox = null;
        i = iToken;
        if (fullCommand.indexOf("# WITHIN=") >= 0)
          bs = Escape.unescapeBitset(extractCommandOption("# WITHIN"));
        else
          bs = (expressionResult instanceof BitSet ? (BitSet) expressionResult
              : null);
        if (bs != null) {
          bbox = viewer.getBoxInfo(bs, -distance);
          pts = new Point3f[] { bbox.getBboxVertices()[0],
              bbox.getBboxVertices()[7] };
          addShapeProperty(propertyList, "commandOption", "WITHIN=\""
              + Escape.escape(bs) + "\"");
          v = new Vector();
          if (bs.cardinality() == 1)
            v.add(viewer.getAtomPoint3f(bs.nextSetBit(0)));
        } else {
          Point3f pt1 = new Point3f(distance, distance, distance);
          Point3f pt0 = new Point3f(ptc);
          pt0.sub(pt1);
          pt1.add(ptc);
          pts = new Point3f[] { pt0, pt1 };
          v = new Vector();
          v.add(ptc);
        }
        propertyValue = new Object[] { new Float(distance), pts, bs, v };
        if (v.size() == 1) {
          addShapeProperty(propertyList, "withinDistance", new Float(distance));
          addShapeProperty(propertyList, "withinPoint", (Point3f) v.get(0));
        }
        break;
      case Token.property:
        addShapeProperty(propertyList, "propertySmoothing", viewer
            .getIsosurfacePropertySmoothing() ? Boolean.TRUE : Boolean.FALSE);
        str = parameterAsString(i);
        propertyName = "property";
        if (!isCavity && str.toLowerCase().indexOf("property_") == 0) {
          data = new float[viewer.getAtomCount()];
          if (isSyntaxCheck)
            continue;
          data = viewer.getDataFloat(str);
          if (data == null)
            error(ERROR_invalidArgument);
          propertyValue = data;
          break;
        }
        int tokProperty = getToken(++i).tok;
        int atomCount = viewer.getAtomCount();
        data = (isCavity ? new float[0] : new float[atomCount]);
        if (isCavity)
          
          error(ERROR_invalidArgument);
        if (!isSyntaxCheck && !isCavity) {
          Atom[] atoms = viewer.getModelSet().atoms;
          viewer.autoCalculate(tokProperty);
          for (int iAtom = atomCount; --iAtom >= 0;) {
            data[iAtom] = Atom.atomPropertyFloat(viewer, atoms[iAtom],
                tokProperty);
          }
        }
        if (tokProperty == Token.color)
          colorScheme = "colorRGB";
        propertyValue = data;
        break;
      case Token.model:
        if (surfaceObjectSeen)
          error(ERROR_invalidArgument);
        modelIndex = modelNumberParameter(++i);
        if (modelIndex < 0) {
          propertyName = "fixed";
          propertyValue = Boolean.TRUE;
          break;
        }
        propertyName = "modelIndex";
        propertyValue = new Integer(modelIndex);
        break;
      case Token.select:
        propertyName = "select";
        propertyValue = expression(++i);
        i = iToken;
        break;
      case Token.set:
        thisSetNumber = intParameter(++i);
        break;
      case Token.offset:
        propertyName = "offset";
        propertyValue = centerParameter(++i);
        i = iToken;
        break;
      case Token.center:
        propertyName = "center";
        propertyValue = centerParameter(++i);
        i = iToken;
        break;
      case Token.color:
        if (tokAt(i + 1) == Token.density) {
          i++;
          propertyName = "colorDensity";
          break;
        }
        

        colorRangeStage = 0;
        if (getToken(i + 1).tok == Token.string) {
          colorScheme = parameterAsString(++i);
          if (colorScheme.indexOf(" ") > 0) {
            discreteColixes = Graphics3D.getColixArray(colorScheme);
            if (discreteColixes == null)
              error(ERROR_badRGBColor);
          }
        } else if (theTok == Token.mesh) {
          isColorMesh = true;
          i++;
        }
        if ((theTok = tokAt(i + 1)) == Token.translucent
            || tokAt(i + 1) == Token.opaque) {
          translucency = setColorOptions(i + 1, JmolConstants.SHAPE_ISOSURFACE,
              -2);
          i = iToken;
        }
        switch (tokAt(i + 1)) {
        case Token.absolute:
        case Token.range:
          getToken(++i);
          colorRangeStage = 1;
          propertyName = "rangeAll";
          if (tokAt(i + 1) == Token.all)
            getToken(++i);
          break;
        default:
          signPt = i + 1;
          continue;
        }
        break;
      case Token.file:
        continue;
      case Token.plus:
        if (colorRangeStage == 0) {
          propertyName = "cutoffPositive";
          propertyValue = new Float(floatParameter(++i));
        }
        break;
      case Token.decimal:
      case Token.integer:
        
        propertyName = (colorRangeStage == 1 ? "red"
            : colorRangeStage == 2 ? "blue" : "cutoff");
        propertyValue = new Float(floatParameter(i));
        if (colorRangeStage > 0)
          ++colorRangeStage;
        break;
      case Token.ionic:
      case Token.vanderwaals:
        RadiusData rd = encodeRadiusParameter(i);
        if (Float.isNaN(rd.value))
          rd.value = 100;
        propertyValue = rd;
        propertyName = "radius";
        haveRadius = true;
        i = iToken;
        break;
      case Token.plane:
        
        planeSeen = true;
        propertyName = "plane";
        propertyValue = planeParameter(++i);
        i = iToken;
        break;
      case Token.scale3d:
        propertyName = "scale3d";
        propertyValue = new Float(floatParameter(++i));
        break;
      case Token.scale:
        propertyName = "scale";
        propertyValue = new Float(floatParameter(++i));
        break;
      case Token.all:
        if (idSeen)
          error(ERROR_invalidArgument);
        propertyName = "thisID";
        break;
      case Token.ellipsoid:
        
        
        surfaceObjectSeen = true;
        ++i;
        try {
          propertyValue = getPoint4f(i);
          propertyName = "ellipsoid";
          i = iToken;
          break;
        } catch (ScriptException e) {
        }
        try {
          propertyName = "ellipsoid";
          propertyValue = floatParameterSet(i, 6, 6);
          i = iToken;
          break;
        } catch (ScriptException e) {
        }
        bs = expression(i);
        int iAtom = bs.nextSetBit(0);
        Atom[] atoms = viewer.getModelSet().atoms;
        if (iAtom >= 0)
          propertyValue = atoms[iAtom].getEllipsoid();
        if (propertyValue == null)
          return;
        i = iToken;
        propertyName = "ellipsoid";
        if (!isSyntaxCheck)
          addShapeProperty(propertyList, "center", viewer.getAtomPoint3f(iAtom));
        break;
      case Token.hkl:
        
        planeSeen = true;
        propertyName = "plane";
        propertyValue = hklParameter(++i);
        i = iToken;
        break;
      case Token.lcaocartoon:
        surfaceObjectSeen = true;
        String lcaoType = parameterAsString(++i);
        addShapeProperty(propertyList, "lcaoType", lcaoType);
        switch (getToken(++i).tok) {
        case Token.bitset:
        case Token.expressionBegin:
          propertyName = "lcaoCartoon";
          bs = expression(i);
          i = iToken;
          int atomIndex = bs.nextSetBit(0);
          modelIndex = 0;
          Point3f pt;
          if (atomIndex < 0) {
            if (!isSyntaxCheck)
              error(ERROR_expressionExpected);
            pt = new Point3f();
          } else {
            modelIndex = viewer.getAtomModelIndex(atomIndex);
            pt = viewer.getAtomPoint3f(atomIndex);
          }
          addShapeProperty(propertyList, "modelIndex", new Integer(modelIndex));
          Vector3f[] axes = { new Vector3f(), new Vector3f(), new Vector3f(pt),
              new Vector3f() };
          if (!isSyntaxCheck)
            viewer.getHybridizationAndAxes(atomIndex, axes[0], axes[1],
                lcaoType, false);
          propertyValue = axes;
          break;
        default:
          error(ERROR_expressionExpected);
        }
        break;
      case Token.mo:
        
        int moNumber = Integer.MAX_VALUE;
        int offset = Integer.MAX_VALUE;
        switch (tokAt(++i)) {
        case Token.nada:
          error(ERROR_badArgumentCount);
        case Token.homo:
        case Token.lumo:
          if ((offset = moOffset(i)) != Integer.MAX_VALUE) {
            moNumber = 0;
            i = iToken;
          }
          break;
        case Token.integer:
          moNumber = intParameter(i);
          break;
        }
        setMoData(propertyList, moNumber, offset, modelIndex, null);
        surfaceObjectSeen = true;
        continue;
      case Token.mep:
        float[] partialCharges = null;
        try {
          partialCharges = viewer.getPartialCharges();
        } catch (Exception e) {
        }
        if (!isSyntaxCheck && partialCharges == null)
          error(ERROR_noPartialCharges);
        surfaceObjectSeen = true;
        propertyName = "mep";
        propertyValue = partialCharges;
        break;
      case Token.sasurface:
      case Token.solvent:
        surfaceObjectSeen = true;
        addShapeProperty(propertyList, "bsSolvent",
            lookupIdentifierValue("solvent"));
        propertyName = (theTok == Token.sasurface ? "sasurface" : "solvent");
        float radius = (isFloatParameter(i + 1) ? floatParameter(++i) : viewer
            .getSolventProbeRadius());
        propertyValue = new Float(radius);
        break;
      case Token.volume:
        doCalcVolume = !isSyntaxCheck;
        break;
      case Token.id:
        setShapeId(iShape, ++i, idSeen);
        isWild = (viewer.getShapeProperty(iShape, "ID") == null);
        i = iToken;
        break;
      case Token.colorscheme:
        if (tokAt(i + 1) == Token.translucent) {
          isColorSchemeTranslucent = true;
          i++;
        }
        colorScheme = parameterAsString(++i);
        break;
      case Token.addhydrogens:
        propertyName = "addHydrogens";
        propertyValue = Boolean.TRUE;
        break;
      case Token.angstroms:
        propertyName = "angstroms";
        break;
      case Token.anisotropy:
        propertyName = "anisotropy";
        propertyValue = getPoint3f(++i, false);
        i = iToken;
        break;
      case Token.area:
        doCalcArea = !isSyntaxCheck;
        break;
      case Token.atomicorbital:
      case Token.orbital:
        surfaceObjectSeen = true;
        nlmZ[0] = intParameter(++i);
        nlmZ[1] = intParameter(++i);
        nlmZ[2] = intParameter(++i);
        nlmZ[3] = (isFloatParameter(i + 1) ? floatParameter(++i) : 6f);
        propertyName = "hydrogenOrbital";
        propertyValue = nlmZ;
        break;
      case Token.binary:
        
        
        continue;
      case Token.blockdata:
        propertyName = "blockData";
        propertyValue = Boolean.TRUE;
        break;
      case Token.cap:
        propertyName = "cappingPlane";
        propertyValue = planeParameter(++i);
        i = iToken;
        break;
      case Token.slab:
        propertyName = "slabbingPlane";
        Point4f plane = planeParameter(++i);
        i = iToken;
        float off = (isFloatParameter(i + 1) ? floatParameter(++i) : Float.NaN);
        if (!Float.isNaN(off))
          plane.w -= off;
        propertyValue = plane;
        break;
      case Token.cavity:
        if (!isIsosurface)
          error(ERROR_invalidArgument);
        isCavity = true;
        if (isSyntaxCheck)
          continue;
        float cavityRadius = (isFloatParameter(i + 1) ? floatParameter(++i)
            : 1.2f);
        float envelopeRadius = (isFloatParameter(i + 1) ? floatParameter(++i)
            : 10f);
        if (envelopeRadius > 10f)
          integerOutOfRange(0, 10);
        addShapeProperty(propertyList, "envelopeRadius", new Float(
            envelopeRadius));
        addShapeProperty(propertyList, "cavityRadius", new Float(cavityRadius));
        propertyName = "cavity";
        break;
      case Token.contour:
      case Token.contours:
        propertyName = "contour";
        switch (tokAt(i + 1)) {
        case Token.discrete:
          propertyValue = floatParameterSet(i + 2, 1, Integer.MAX_VALUE);
          i = iToken;
          break;
        case Token.increment:
          Point3f pt = getPoint3f(i + 2, false);
          if (pt.z <= 0)
            error(ERROR_invalidArgument); 
          propertyValue = pt;
          i = iToken;
          break;
        default:
          propertyValue = new Integer(
              tokAt(i + 1) == Token.integer ? intParameter(++i) : 0);
        }
        break;
      case Token.cutoff:
        if (++i < statementLength && getToken(i).tok == Token.plus) {
          propertyName = "cutoffPositive";
          propertyValue = new Float(floatParameter(++i));
        } else {
          propertyName = "cutoff";
          propertyValue = new Float(floatParameter(i));
        }
        break;
      case Token.downsample:
        propertyName = "downsample";
        propertyValue = new Integer(intParameter(++i));
        break;
      case Token.eccentricity:
        propertyName = "eccentricity";
        propertyValue = getPoint4f(++i);
        i = iToken;
        break;
      case Token.ed:
        setMoData(propertyList, -1, 0, modelIndex, null);
        surfaceObjectSeen = true;
        continue;
      case Token.debug:
      case Token.nodebug:
        propertyName = "debug";
        propertyValue = (theTok == Token.debug ? Boolean.TRUE : Boolean.FALSE);
        break;
      case Token.fixed:
        propertyName = "fixed";
        propertyValue = Boolean.TRUE;
        break;
      case Token.fullplane:
        propertyName = "fullPlane";
        propertyValue = Boolean.TRUE;
        break;
      case Token.functionxy:
        
        
        Vector vxy = new Vector();
        if (getToken(++i).tok != Token.string)
          error(ERROR_what,
              "functionXY must be followed by a function name in quotes.");
        String name = parameterAsString(i++);
        
        String dName = extractCommandOption("# DATA" + (isFxy ? "2" : ""));
        if (dName != null)
          name = dName;
        boolean isXYZ = (name.indexOf("data2d_") == 0);
        vxy.addElement(name); 
        vxy.addElement(getPoint3f(i, false)); 
        Point4f pt4;
        ptX = ++iToken;
        vxy.addElement(pt4 = getPoint4f(ptX)); 
        nX = (int) pt4.x;
        ptY = ++iToken;
        vxy.addElement(pt4 = getPoint4f(ptY)); 
        nY = (int) pt4.x;
        vxy.addElement(getPoint4f(++iToken)); 
        if (nX == 0 || nY == 0)
          error(ERROR_invalidArgument);
        if (!isSyntaxCheck) {
          float[][] fdata = (isXYZ ? viewer.getDataFloat2D(name) : viewer
              .functionXY(name, nX, nY));
          if (isXYZ) {
            nX = (fdata == null ? 0 : fdata.length);
            nY = 3;
          } else {
            nX = Math.abs(nX);
            nY = Math.abs(nY);
          }
          if (fdata == null) {
            iToken = ptX;
            error(ERROR_what, "fdata is null.");
          }
          if (fdata.length != nX && !isXYZ) {
            iToken = ptX;
            error(ERROR_what, "fdata length is not correct: " + fdata.length
                + " " + nX + ".");
          }
          for (int j = 0; j < nX; j++) {
            if (fdata[j] == null) {
              iToken = ptY;
              error(ERROR_what, "fdata[" + j + "] is null.");
            }
            if (fdata[j].length != nY) {
              iToken = ptY;
              error(ERROR_what, "fdata[" + j + "] is not the right length: "
                  + fdata[j].length + " " + nY + ".");
            }
          }
          vxy.addElement(fdata); 
        }
        i = iToken;
        propertyName = "functionXY";
        propertyValue = vxy;
        isFxy = surfaceObjectSeen = true;
        break;
      case Token.functionxyz:
        
        
        v = new Vector();
        if (getToken(++i).tok != Token.string)
          error(ERROR_what,
              "functionXYZ must be followed by a function name in quotes.");
        String fName = parameterAsString(i++);
        
        String dataName = extractCommandOption("# DATA" + (isFxy ? "2" : ""));
        if (dataName != null)
          fName = dataName;
        boolean isXYZV = (fName.indexOf("data3d_") == 0);
        if (dataName != null)
          fName = dataName;
        v.addElement(fName); 
        v.addElement(getPoint3f(i, false)); 
        Point4f pt;
        ptX = ++iToken;
        v.addElement(pt = getPoint4f(ptX)); 
        nX = (int) pt.x;
        ptY = ++iToken;
        v.addElement(pt = getPoint4f(ptY)); 
        nY = (int) pt.x;
        v.addElement(pt = getPoint4f(++iToken)); 
        nZ = (int) pt.x;
        if (nX == 0 || nY == 0)
          error(ERROR_invalidArgument);
        if (!isSyntaxCheck) {
          float[][][] xyzdata = (isXYZV ? viewer.getDataFloat3D(fName) : viewer
              .functionXYZ(fName, nX, nY, nZ));
          nX = Math.abs(nX);
          nY = Math.abs(nY);
          if (xyzdata == null) {
            iToken = ptX;
            error(ERROR_what, "xyzdata is null.");
          }
          if (xyzdata.length != nX || xyzdata[0].length != nY
              || xyzdata[0][0].length != nZ) {
            iToken = ptX;
            error(ERROR_what, "xyzdata[" + xyzdata.length + "]["
                + xyzdata[0].length + "][" + xyzdata[0][0].length
                + "] is not of size [" + nX + "][" + nY + "][" + nZ + "]");
          }
          v.addElement(xyzdata); 
        }
        i = iToken;
        propertyName = "functionXYZ";
        propertyValue = v;
        isFxy = surfaceObjectSeen = true;
        break;
      case Token.gridpoints:
        propertyName = "gridPoints";
        break;
      case Token.ignore:
        propertyName = "ignore";
        propertyValue = expression(++i);
        i = iToken;
        break;
      case Token.insideout:
        propertyName = "insideOut";
        break;
      case Token.internal:
      case Token.interior:
        propertyName = "pocket";
        propertyValue = Boolean.FALSE;
        break;
      case Token.lobe:
        
        surfaceObjectSeen = true;
        propertyName = "lobe";
        propertyValue = getPoint4f(++i);
        i = iToken;
        break;
      case Token.lonepair:
      case Token.lp:
        
        surfaceObjectSeen = true;
        propertyName = "lp";
        propertyValue = getPoint4f(++i);
        i = iToken;
        break;
      case Token.map:
        if (haveRadius && !surfaceObjectSeen) {
          surfaceObjectSeen = true;
          addShapeProperty(propertyList, "bsSolvent",
              lookupIdentifierValue("solvent"));
          addShapeProperty(propertyList, "sasurface", new Float(0));
        }
        surfaceObjectSeen = !isCavity;
        propertyName = "map";
        break;
      case Token.maxset:
        propertyName = "maxset";
        propertyValue = new Integer(intParameter(++i));
        break;
      case Token.minset:
        propertyName = "minset";
        propertyValue = new Integer(intParameter(++i));
        break;
      case Token.radical:
        
        surfaceObjectSeen = true;
        propertyName = "rad";
        propertyValue = getPoint4f(++i);
        i = iToken;
        break;
      case Token.modelbased:
        propertyName = "fixed";
        propertyValue = Boolean.FALSE;
        break;
      case Token.molecular:
        surfaceObjectSeen = true;
        propertyName = "molecular";
        propertyValue = new Float(1.4);
        break;
      case Token.object:
      case Token.obj:
        addShapeProperty(propertyList, "fileType", "Obj");
        continue;
      case Token.phase:
        if (surfaceObjectSeen)
          error(ERROR_invalidArgument);
        propertyName = "phase";
        propertyValue = (tokAt(i + 1) == Token.string ? stringParameter(++i)
            : "_orb");
        break;
      case Token.pocket:
        propertyName = "pocket";
        propertyValue = Boolean.TRUE;
        break;
      case Token.pointsperangstrom:
      case Token.resolution:
        propertyName = "resolution";
        propertyValue = new Float(floatParameter(++i));
        break;
      case Token.reversecolor:
        propertyName = "reverseColor";
        propertyValue = Boolean.TRUE;
        break;
      case Token.sign:
        signPt = i + 1;
        propertyName = "sign";
        propertyValue = Boolean.TRUE;
        colorRangeStage = 1;
        break;
      case Token.sphere:
        
        surfaceObjectSeen = true;
        propertyName = "sphere";
        propertyValue = new Float(floatParameter(++i));
        break;
      case Token.squared:
        propertyName = "squareData";
        propertyValue = Boolean.TRUE;
        break;
      case Token.variable:
        propertyName = "property";
        data = new float[viewer.getAtomCount()];
        if (!isSyntaxCheck) {
          Parser.parseFloatArray(""
              + getParameter(parameterAsString(++i), false), null, data);
        }
        propertyValue = data;
        break;
      case Token.string:
        propertyName = surfaceObjectSeen || planeSeen ? "mapColor" : "readFile";
        
        String filename = parameterAsString(i);
        if (filename.equals("TESTDATA") && Viewer.testData != null) {
          propertyValue = Viewer.testData;
          break;
        }
        if (filename.equals("TESTDATA2") && Viewer.testData2 != null) {
          propertyValue = Viewer.testData2;
          break;
        }
        if (filename.length() == 0) {
          if (surfaceObjectSeen || planeSeen)
            propertyValue = viewer.getModelAuxiliaryInfo(modelIndex,
                "jmolMappedDataInfo");
          if (propertyValue == null)
            propertyValue = viewer.getModelAuxiliaryInfo(modelIndex,
                "jmolSurfaceInfo");
          surfaceObjectSeen = true;
          if (propertyValue != null)
            break;
          filename = getFullPathName();
        }
        surfaceObjectSeen = true;
        if (tokAt(i + 1) == Token.integer)
          addShapeProperty(propertyList, "fileIndex", new Integer(
              intParameter(++i)));
        if (filename.equals("string")) {
          propertyValue = (isSyntaxCheck ? null : viewer.getFileInfo()[3]);
          addShapeProperty(propertyList, "fileName", "");
        } else if (filename.equalsIgnoreCase("INLINE")) {
          
          if (tokAt(i + 1) != Token.string)
            error(ERROR_stringExpected);
          
          String sdata = parameterAsString(++i);
          if (isPmesh)
            sdata = TextFormat.replaceAllCharacters(sdata, "{,}|", ' ');
          if (logMessages)
            Logger.debug("pmesh inline data:\n" + sdata);
          propertyValue = (isSyntaxCheck ? null : sdata);
          addShapeProperty(propertyList, "fileName", "");
        } else if (!isSyntaxCheck) {
          String[] fullPathNameOrError;
          String localName = null;
          if (fullCommand.indexOf("# FILE" + nFiles + "=") >= 0) {
            filename = extractCommandOption("# FILE" + nFiles);
            if (tokAt(i + 1) == Token.as)
              i += 2; 
          } else if (tokAt(i + 1) == Token.as) {
            localName = viewer.getFullPath(stringParameter(i = i + 2));
            fullPathNameOrError = viewer.getFullPathNameOrError(localName);
            localName = fullPathNameOrError[0];
            addShapeProperty(propertyList, "localName", localName);
          }
          
          fullPathNameOrError = viewer.getFullPathNameOrError(filename);
          filename = fullPathNameOrError[0];
          if (fullPathNameOrError[1] != null)
            error(ERROR_fileNotFoundException, filename + ":"
                + fullPathNameOrError[1]);
          Logger.info("reading isosurface data from " + filename);
          addShapeProperty(propertyList, "commandOption", "FILE" + (nFiles++)
              + "=" + Escape.escape(localName == null ? filename : localName));
          addShapeProperty(propertyList, "fileName", filename);
          
          propertyValue = null;
        }
        break;
      case Token.identifier:
        if (str.equalsIgnoreCase("LINK")) { 
          propertyName = "link";
          break;
        } else if (str.equalsIgnoreCase("REMAPPABLE")) { 
          propertyName = "remappable";
          break;
        } else {
          propertyName = "thisID";
          propertyValue = str;
        }
        
      default:
        if (planeSeen && !surfaceObjectSeen) {
          addShapeProperty(propertyList, "nomap", new Float(0));
          surfaceObjectSeen = true;
        }
        if (!setMeshDisplayProperty(iShape, 0, theTok)) {
          if (Token.tokAttr(theTok, Token.identifier) && !idSeen) {
            setShapeId(iShape, i, idSeen);
            i = iToken;
            break;
          }
          error(ERROR_invalidArgument);
        }
        if (iptDisplayProperty == 0)
          iptDisplayProperty = i;
        i = statementLength - 1;
        break;
      }
      idSeen = (theTok != Token.delete);
      if (propertyName == "property" && !surfaceObjectSeen) {
        surfaceObjectSeen = true;
        addShapeProperty(propertyList, "bsSolvent",
            lookupIdentifierValue("solvent"));
        propertyName = "sasurface";
        propertyValue = new Float(0);
      }
      if (isWild && surfaceObjectSeen)
        error(ERROR_invalidArgument);
      if (propertyName != null)
        addShapeProperty(propertyList, propertyName, propertyValue);
    }
    if ((isCavity || haveRadius) && !surfaceObjectSeen) {
      surfaceObjectSeen = true;
      addShapeProperty(propertyList, "bsSolvent",
          lookupIdentifierValue("solvent"));
      addShapeProperty(propertyList, "sasurface", new Float(0));
    }

    if (planeSeen && !surfaceObjectSeen) {
      addShapeProperty(propertyList, "nomap", new Float(0));
      surfaceObjectSeen = true;
    }
    if (thisSetNumber > 0)
      addShapeProperty(propertyList, "getSurfaceSets", new Integer(
          thisSetNumber - 1));
    if (discreteColixes != null)
      addShapeProperty(propertyList, "colorDiscrete", discreteColixes);
    else if (colorScheme != null)
      addShapeProperty(propertyList, "setColorScheme",
          new Object[] { colorScheme,
              isColorSchemeTranslucent ? Boolean.TRUE : Boolean.FALSE });

    
    setShapeProperty(iShape, "setProperties", propertyList);

    if (iptDisplayProperty > 0) {
      if (!setMeshDisplayProperty(iShape, iptDisplayProperty,
          getToken(iptDisplayProperty).tok))
        error(ERROR_invalidArgument);
    }

    Object area = null;
    Object volume = null;
    if (doCalcArea) {
      area = viewer.getShapeProperty(iShape, "area");
      if (area instanceof Float)
        viewer.setFloatProperty("isosurfaceArea", ((Float) area).floatValue());
      else
        viewer.setUserVariable("isosurfaceArea", ScriptVariable
            .getVariable(area));
    }
    if (doCalcVolume) {
      volume = (doCalcVolume ? viewer.getShapeProperty(iShape, "volume") : null);
      if (volume instanceof Float)
        viewer.setFloatProperty("isosurfaceVolume", ((Float) volume)
            .floatValue());
      else
        viewer.setUserVariable("isosurfaceVolume", ScriptVariable
            .getVariable(volume));
    }
    if (surfaceObjectSeen && isIsosurface && !isSyntaxCheck) {
      setShapeProperty(iShape, "finalize", null);
      Integer n = (Integer) viewer.getShapeProperty(iShape, "count");
      float[] dataRange = (float[]) viewer
          .getShapeProperty(iShape, "dataRange");
      String s = (String) viewer.getShapeProperty(iShape, "ID");
      if (s != null) {
        s += " created with cutoff = "
            + viewer.getShapeProperty(iShape, "cutoff")
            + " ; number of isosurfaces = " + n;
        if (dataRange != null && dataRange[0] != dataRange[1])
          s += "\ncolor range " + dataRange[2] + " " + dataRange[3]
              + "; mapped data range " + dataRange[0] + " to " + dataRange[1];
        if (doCalcArea)
          s += "\nisosurfaceArea = " + Escape.escapeArray(area);
        if (doCalcVolume)
          s += "\nisosurfaceVolume = " + Escape.escapeArray(volume);
        showString(s);
      }
    } else if (doCalcArea || doCalcVolume) {
      if (doCalcArea)
        showString("isosurfaceArea = " + Escape.escapeArray(area));
      if (doCalcVolume)
        showString("isosurfaceVolume = " + Escape.escapeArray(volume));
    }
    if (translucency != null)
      setShapeProperty(iShape, "translucency", translucency);
    setShapeProperty(iShape, "clear", null);
  }

  private void addShapeProperty(Vector propertyList, String key,
                                Object value) {
    if (!isSyntaxCheck)
      propertyList.add(new Object[] {key, value});
  }

  
  private boolean setMeshDisplayProperty(int shape, int i, int tok)
      throws ScriptException {
    String propertyName = null;
    Object propertyValue = null;
    boolean checkOnly = (i == 0);
    
    if (!checkOnly)
      iToken = i;
    switch (tok) {
    case Token.opaque:
    case Token.translucent:
      if (!checkOnly)
        colorShape(shape, iToken, false);
        return true;
    case Token.nada:
    case Token.delete:
    case Token.on:
    case Token.off:
    case Token.hide:
    case Token.hidden:
    case Token.display:
    case Token.displayed:
      if (iToken == 1)
        setShapeProperty(shape, "thisID", null);
      if (tok == Token.nada)
        return (iToken == 1);
      if (checkOnly)
        return true;
      if (tok == Token.delete) {
        setShapeProperty(shape, "delete", null);
        return true;
      }
      if (tok == Token.hidden || tok == Token.hide)
        tok = Token.off;
      else if (tok == Token.displayed || tok == Token.display)
        tok = Token.on;
      
    case Token.frontlit:
    case Token.backlit:
    case Token.fullylit:
    case Token.contourlines:
    case Token.nocontourlines:
    case Token.dots:
    case Token.nodots:
    case Token.mesh:
    case Token.nomesh:
    case Token.fill:
    case Token.nofill:
    case Token.triangles:
    case Token.notriangles:
    case Token.frontonly:
    case Token.notfrontonly:
      propertyName = "token";
      propertyValue = new Integer(tok);
      break;
    }
    if (propertyName == null)
      return false;
    if (checkOnly)
      return true;
    setShapeProperty(shape, propertyName, propertyValue);
    if ((tok = tokAt(iToken + 1)) != Token.nada) {
      if (!setMeshDisplayProperty(shape, ++iToken, tok))
        --iToken;
    }
    return true;
  }
  
  private void bind() throws ScriptException {
    
    String mouseAction = stringParameter(1);
    String name = parameterAsString(2);
    Point3f range1 = null;
    Point3f range2 = null;
    if (tokAt(3) == Token.range) {
      range1 = xypParameter(4);
      range2 = xypParameter(++iToken);
      checkLast(iToken);
    } else {
      checkLength(3);
    }
    if (!isSyntaxCheck)
      viewer.bindAction(mouseAction, name, range1, range2); 
  }
  private void unbind() throws ScriptException {
    
    if (statementLength != 1)
      checkLength23();
    String mouseAction = optParameterAsString(1);
    String name = optParameterAsString(2);
    if (mouseAction.length() == 0 || tokAt(1) == Token.all)
      mouseAction = null;
    if (name.length() == 0 || tokAt(2) == Token.all)
      name = null;
    if (name == null && mouseAction != null 
        && ActionManager.getActionFromName(mouseAction) >= 0) {
      name = mouseAction;
      mouseAction = null;
    }
    if (!isSyntaxCheck)
      viewer.unBindAction(mouseAction, name);
  }
}
