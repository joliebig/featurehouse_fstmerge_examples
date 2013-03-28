

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;
import java.io.*;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;

import junit.framework.TestCase;


public class BodyTypeChecker extends Bob {
  
  protected BodyData _bodyData;
  
  
  public BodyTypeChecker(BodyData bodyData, File file, String packageName, LinkedList<String> importedFiles, 
                         LinkedList<String> importedPackages, LinkedList<VariableData> vars, 
                         LinkedList<Pair<SymbolData, JExpression>> thrown) {
    super(bodyData, file, packageName, importedFiles, importedPackages, vars, thrown);
    _bodyData = bodyData;
  }
  
   
  protected Data _getData() { return _bodyData; }
  
  
  public TypeData forSimpleThisReferenceOnly(SimpleThisReference that) {
    return _bodyData.getSymbolData().getInstanceData();
  }

  
  public TypeData forSimpleSuperReferenceOnly(SimpleSuperReference that) {
    return _bodyData.getSymbolData().getSuperClass().getInstanceData();
  }
  
  
  protected BodyTypeChecker createANewInstanceOfMe(BodyData bodyData, 
                                                   File file, 
                                                   String pakage, 
                                                   LinkedList<String> importedFiles, 
                                                   LinkedList<String> importedPackages, 
                                                   LinkedList<VariableData> vars, 
                                                   LinkedList<Pair<SymbolData, JExpression>> thrown) {
    return new BodyTypeChecker(bodyData, file, pakage, importedFiles, importedPackages, vars, thrown);
  }
  
  
  public TypeData forInstanceInitializer(InstanceInitializer that) {
    return forBlock(that.getCode());
  }
  
  
  public TypeData forUninitializedVariableDeclaratorOnly(UninitializedVariableDeclarator that, 
                                                         TypeData type_result, 
                                                         TypeData name_result) {
    _vars.addLast(_bodyData.getVar(that.getName().getText()));
    return null;
  }


  
  public TypeData forBodyOnly(Body that, TypeData[] items_result) {
    for (int i = 0; i < items_result.length; i++) {
      if (items_result[i] != null && !(that.getStatements()[i] instanceof ExpressionStatement)) {

        
        if (i < items_result.length - 1) {
        
          
          _addError("Unreachable statement.", (JExpression)that.getStatements()[i+1]);
        }
       
        return items_result[i];
      }
    }
    return null;
  }
  
  
  public TypeData forBracedBodyOnly(BracedBody that, TypeData[] items_result) {
    return forBodyOnly(that, items_result);
  }
  
  
  public TypeData forUnbracedBodyOnly(UnbracedBody that, TypeData[] items_result) {
    return forBodyOnly(that, items_result);
  }

  
  
  public TypeData forVoidReturnStatementOnly(VoidReturnStatement that) {
    MethodData md = _bodyData.getMethodData();
    if (md.getReturnType() != SymbolData.VOID_TYPE) {
      _addError("Cannot return void when the method's expected return type is not void.", that);

      
      return md.getReturnType().getInstanceData();
    }
    return SymbolData.VOID_TYPE.getInstanceData();
  }

  
  public TypeData forValueReturnStatement(ValueReturnStatement that) {
    ExpressionTypeChecker etc = 
      new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, _vars, _thrown);
    TypeData value_result = that.getValue().visit(etc);
    thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);
    return forValueReturnStatementOnly(that, value_result);
  }

  
  public TypeData forValueReturnStatementOnly(ValueReturnStatement that, TypeData value_result) {
    MethodData md = _bodyData.getMethodData();
    SymbolData expected = md.getReturnType();
    
    if (expected == null) {
      
      return value_result;
    }
    
    if (value_result == null || ! assertFound(value_result, that)) { 
      
      return expected.getInstanceData(); 
    }
    
    if (value_result != null && !value_result.isInstanceType()) {
     _addError("You cannot return a class or interface name.  Perhaps you meant to say " + value_result.getName() +
               ".class or to create an instance", that);
     return value_result.getInstanceData();
    }
    
    if (expected == SymbolData.VOID_TYPE) {
      _addError("Cannot return a value when the method's expected return type is void.", that);
      
      return SymbolData.VOID_TYPE.getInstanceData();
    }
    else if (!_isAssignableFrom(expected, value_result.getSymbolData())) {
      _addError("This method expected to return type: \"" + 
                expected.getName() + "\" but here returned type: \"" + value_result.getName() + "\".", that);
    }
    return value_result;
  }
  
  
  public TypeData forForStatement(ForStatement that) {
    Boolean expOk = Boolean.TRUE;
    if (that.getCondition() instanceof Expression) {
      Expression exp = (Expression) that.getCondition();
      
      expOk = exp.visit(new NoAssignmentAllowedInExpression("the conditional expression of a for-statement"));
    }

    LinkedList<VariableData> newVars = cloneVariableDataList(_vars);
    BodyTypeChecker btc = 
      createANewInstanceOfMe(_bodyData, _file, _package, _importedFiles, _importedPackages, newVars, _thrown);
    final TypeData init_result = that.getInit().visit(btc);
    
    ExpressionTypeChecker etc = 
      new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, btc._vars, _thrown);
    final TypeData condition_result = that.getCondition().visit(etc);
    btc.thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);
    final TypeData update_result = that.getUpdate().visit(btc);
    final TypeData code_result = that.getCode().visit(btc);
    
    
    
    unassignVariableDatas(btc.thingsThatHaveBeenAssigned);
    if (expOk.booleanValue()) 
      return forForStatementOnly(that, init_result, condition_result, update_result, code_result);
    else {return null;}
  }
  
  
  
  public TypeData forForStatementOnly(ForStatement that, TypeData init_result, TypeData condition_result, 
                                      TypeData update_result, TypeData code_result) {
    if (condition_result != null && assertFound(condition_result, that)) { 
      if (!condition_result.isInstanceType()) {
        _addError("This for-statement's conditional expression must be a boolean value. Instead, it is a class or " +
                  "interface name", that);
      }
      else if (!condition_result.getSymbolData().isBooleanType(LanguageLevelConverter.OPT.javaVersion())) {
        _addError("This for-statement's conditional expression must be a boolean value. Instead, its type is " + 
                  condition_result.getName(), that);
      }
    }
    return null;
  }
  
  
  public TypeData forIfThenStatement(IfThenStatement that) {
    Boolean expOk = Boolean.TRUE;
    if (that.getTestExpression() instanceof Expression) {
      Expression exp = that.getTestExpression();
      
      expOk = exp.visit(new NoAssignmentAllowedInExpression("the conditional expression of an if-then statement"));
    }

    
    
    ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, 
                                                          _vars, _thrown);
    final TypeData testExpression_result = that.getTestExpression().visit(etc);
    thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);

    
    
    BodyTypeChecker btc = createANewInstanceOfMe(_bodyData, _file, _package, _importedFiles, _importedPackages, 
                                                 cloneVariableDataList(_vars), _thrown);
    final TypeData thenStatement_result = that.getThenStatement().visit(btc);
    
    
    unassignVariableDatas(btc.thingsThatHaveBeenAssigned);
    
    if (expOk.booleanValue()) {return forIfThenStatementOnly(that, testExpression_result, thenStatement_result);}
    return null;
  }
  
  
  public TypeData forIfThenStatementOnly(IfThenStatement that, TypeData testExpression_result, 
                                         TypeData thenStatement_result) {
    if (testExpression_result != null && assertFound(testExpression_result, that.getTestExpression())) {
      if (!testExpression_result.isInstanceType()) {
        _addError("This if-then-statement's conditional expression must be a boolean value. Instead, it is a class " +
                    "or interface name", that);
      }
      else if (!testExpression_result.getSymbolData().isBooleanType(LanguageLevelConverter.OPT.javaVersion())) {
        _addError("This if-then-statement's conditional expression must be a boolean value. Instead, its type is " + 
                  testExpression_result.getName(), that.getTestExpression());
      }
    }
    return null;
  }
  

  
  public TypeData forIfThenElseStatement(IfThenElseStatement that) {
    Boolean expOk = Boolean.TRUE;
    if (that.getTestExpression() instanceof Expression) {
      Expression exp = that.getTestExpression();
      
      expOk = exp.visit(new NoAssignmentAllowedInExpression("the conditional expression of an if-then-else statement"));
    }
    
    
    
    ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, 
                                                          _vars, _thrown);
    final TypeData testExpression_result = that.getTestExpression().visit(etc);
    thingsThatHaveBeenAssigned = etc.thingsThatHaveBeenAssigned;
    
    
    
    BodyTypeChecker btcThen = createANewInstanceOfMe(_bodyData, _file, _package, _importedFiles, _importedPackages, 
                                                     cloneVariableDataList(_vars), _thrown);
    final TypeData thenStatement_result = that.getThenStatement().visit(btcThen);
    
    unassignVariableDatas(btcThen.thingsThatHaveBeenAssigned);


    
    
    BodyTypeChecker btcElse = createANewInstanceOfMe(_bodyData, _file, _package, _importedFiles, _importedPackages, 
                                                     cloneVariableDataList(_vars), _thrown);
    final TypeData elseStatement_result = that.getElseStatement().visit(btcElse);
    
    unassignVariableDatas(btcElse.thingsThatHaveBeenAssigned);

    
    reassignVariableDatas(btcThen.thingsThatHaveBeenAssigned, btcElse.thingsThatHaveBeenAssigned);
    
    if (expOk.booleanValue()) {return forIfThenElseStatementOnly(that, testExpression_result, thenStatement_result, 
                                                                 elseStatement_result);}
    return null;
  }
    
  
  public TypeData forIfThenElseStatementOnly(IfThenElseStatement that, TypeData testExpression_result, 
                                             TypeData thenStatement_result, TypeData elseStatement_result) {
    if (testExpression_result != null && assertFound(testExpression_result, that.getTestExpression())) {
      if (!testExpression_result.isInstanceType()) {
        _addError("This if-then-else statement's conditional expression must be a boolean value. Instead, it is a " +
                  "class or interface name", 
                  that);
      }
      else if (!testExpression_result.getSymbolData().isBooleanType(LanguageLevelConverter.OPT.javaVersion())) {
        _addError("This if-then-else statement's conditional expression must be a boolean value. Instead, its type is "
                    + testExpression_result.getName(), that.getTestExpression());
      }
    }

    if (testExpression_result == null || thenStatement_result == null || elseStatement_result == null) return null;
     
    
    
    
    SymbolData result = getCommonSuperType(thenStatement_result.getSymbolData(), elseStatement_result.getSymbolData());
    if (result==null) {return null;}
    return result.getInstanceData();
  } 

  
  public TypeData forWhileStatement(WhileStatement that) {
    Boolean expOk = Boolean.TRUE;
    if (that.getCondition() instanceof Expression) {
      Expression exp = that.getCondition();
      
      expOk = exp.visit(new NoAssignmentAllowedInExpression("the condition expression of a while statement"));
    }
    
    
    
    
    ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, 
                                                          _vars, _thrown);
    final TypeData condition_result = that.getCondition().visit(etc);
    thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);

    
    
    BodyTypeChecker btc = createANewInstanceOfMe(_bodyData, _file, _package, _importedFiles, _importedPackages, 
                                                 cloneVariableDataList(_vars), _thrown);
    final TypeData code_result = that.getCode().visit(btc);
    unassignVariableDatas(btc.thingsThatHaveBeenAssigned);
    if (expOk.booleanValue()) {return forWhileStatementOnly(that, condition_result, code_result);}
    return null;
  }
    
  
  public TypeData forWhileStatementOnly(WhileStatement that, TypeData condition_result, TypeData code_result) {
    if (condition_result != null && assertFound(condition_result, that.getCondition())) {
      if (! condition_result.isInstanceType()) {
        _addError("This while-statement's conditional expression must be a boolean value. Instead, it is a class or " +
                    "interface name", that);
      }
      else if (!condition_result.getSymbolData().isBooleanType(LanguageLevelConverter.OPT.javaVersion())) {
        _addError("This while-statement's conditional expression must be a boolean value. Instead, its type is " + 
                  condition_result.getName(), that.getCondition());
      }
    }
    return null;
  }
  
  
  public TypeData forDoStatement(DoStatement that) {
    
    
    BodyTypeChecker btc = createANewInstanceOfMe(_bodyData, _file, _package, _importedFiles, _importedPackages, 
                                                 cloneVariableDataList(_vars), _thrown);
    final TypeData code_result = that.getCode().visit(btc);
    unassignVariableDatas(btc.thingsThatHaveBeenAssigned);
    
    Boolean expOk = Boolean.TRUE;
    if (that.getCondition() instanceof Expression) {
      Expression exp = that.getCondition();
      
      expOk = exp.visit(new NoAssignmentAllowedInExpression("the condition expression of a do statement"));
    }

    
    ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, _vars, _thrown);
    final TypeData condition_result = that.getCondition().visit(etc);
    thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);

    if (expOk.booleanValue()) {return forDoStatementOnly(that, code_result, condition_result);}
    return null;
    
  }


  
  public TypeData forDoStatementOnly(DoStatement that, TypeData code_result, TypeData condition_result) {
    if (condition_result != null && assertFound(condition_result, that.getCondition())) {
      if (!condition_result.isInstanceType()) {
        _addError("This do-statement's conditional expression must be a boolean value. Instead, it is a class or interface name", that.getCondition());
      }
      else if (!condition_result.getSymbolData().isBooleanType(LanguageLevelConverter.OPT.javaVersion())) {
        _addError("This do-statement's conditional expression must be a boolean value. Instead, its type is " + condition_result.getName(), that.getCondition());
      }
    }
    if (code_result == null) {return null;}
    return code_result.getInstanceData();
  }
  
  
  public TypeData forSwitchStatement(SwitchStatement that) {
    Expression exp = that.getTest();
    
    exp.visit(new NoAssignmentAllowedInExpression("the switch expression of a switch statement"));

    
    ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, _vars, _thrown);
    final TypeData test_result = that.getTest().visit(etc);
    thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);
    
    
    if (test_result == null || !assertFound(test_result, exp)) {return null;}
    if (!(_isAssignableFrom(SymbolData.INT_TYPE, test_result.getSymbolData()) || _isAssignableFrom(SymbolData.CHAR_TYPE, test_result.getSymbolData()))) {
      _addError("The switch expression must be either an int or a char.  You have used a " + test_result.getSymbolData().getName(), that.getTest());
    }
    
    final TypeData[] cases_result = makeArrayOfRetType(that.getCases().length);
    BodyTypeChecker[] btcs = new BodyTypeChecker[that.getCases().length];
    HashSet<Integer> labels = new HashSet<Integer>();
    LinkedList<VariableData> variablesAssigned = new LinkedList<VariableData>();
    boolean seenDefault = false;
    boolean hadCaseReturn = false;
    
    
    for (int i = 0; i < that.getCases().length; i++) {
      SwitchCase sc = that.getCases()[i];
      btcs[i] = createANewInstanceOfMe(_bodyData, _file, _package, _importedFiles, _importedPackages, cloneVariableDataList(_vars), _thrown);
      cases_result[i] = sc.visit(btcs[i]);
      if (sc instanceof LabeledCase) {
        LabeledCase lc = (LabeledCase) sc;
        
          Integer toCheck = null;
          if (lc.getLabel() instanceof CharLiteral)  { 
            toCheck = (int) ((CharLiteral) lc.getLabel()).getValue();
          }
          if (lc.getLabel() instanceof IntegerLiteral) {
            toCheck = ((IntegerLiteral) lc.getLabel()).getValue();
          }
          if (toCheck != null) {
            if (labels.contains(toCheck))
              _addError("You cannot have two switch cases with the same label " + toCheck, lc.getLabel());
            else labels.add(toCheck);
          }
      }
      else {
        if (seenDefault) _addError("A switch statement can only have one default case", sc);
        seenDefault = true;
      }
      
      
      
      

      if (cases_result[i] != null || (i == cases_result.length - 1)) { 
        if (! hadCaseReturn) {
          variablesAssigned = btcs[i].thingsThatHaveBeenAssigned; 
          hadCaseReturn = true;
        }
        else {
          Iterator<VariableData> iter = variablesAssigned.iterator();
          while (iter.hasNext()) {
            if (!btcs[i].thingsThatHaveBeenAssigned.contains(iter.next())) {iter.remove();}
          }
        }
      }
      
      
      unassignVariableDatas(btcs[i].thingsThatHaveBeenAssigned);
    }

    
    if (seenDefault) {
      for (VariableData vd : variablesAssigned) vd.gotValue();
    }
    return forSwitchStatementOnly(that, test_result, cases_result, seenDefault);
}

  
   public TypeData forSwitchStatementOnly(SwitchStatement that, TypeData test_result, TypeData[] cases_result, 
                                          boolean sawDefault) {
     
     
     if (!sawDefault) return null;
     
     
     for (int i = 0; i<cases_result.length; i++) {
       if (cases_result[i] != null && cases_result[i].getSymbolData() == SymbolData.NOT_FOUND) {return null;}
     }
     
     
     if (cases_result[cases_result.length-1] == null) return null;
     
     return _bodyData.getMethodData().getReturnType().getInstanceData();
  }
  
  
  
   public TypeData forLabeledCase(LabeledCase that) {
     ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, 
                                                           _vars, _thrown);
    final TypeData label_result = that.getLabel().visit(etc);
    thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);
    Expression exp = that.getLabel();
    
    if (label_result == null || !assertFound(label_result, exp)) return null;
    
    
    if (!(exp instanceof LexicalLiteral || exp instanceof NumericUnaryExpression && 
          ((NumericUnaryExpression) exp).getValue() instanceof LexicalLiteral)) {
      _addError("The labels of a switch statement must be constants.  You are using a more complicated expression of" +
                " type " + label_result.getSymbolData().getName(), 
                that.getLabel());
    }
    else if (!_isAssignableFrom(SymbolData.INT_TYPE, label_result.getSymbolData())) {
      _addError("The labels of a switch statement must be constants of int or char type.  You specified a constant of" +
                " type " + label_result.getSymbolData().getName(), 
                that.getLabel());
    }
    
    return forSwitchCase(that);
  }
  
  
  public TypeData forDefaultCase(DefaultCase that) { return forSwitchCase(that); }
  
  
  public TypeData forSwitchCase(SwitchCase that) {
    final TypeData code_result = that.getCode().visit(this);
    
    
    
    
    
    if (code_result == null && that.getCode().getStatements().length > 0) {
      _addError("You must end a non-empty switch case with a break or return statement at the Advanced level", that);
    }
    return code_result;
  }
  
  
  public TypeData forBlock(Block that) {
    BlockData bd = _bodyData.getNextBlock();
    if (bd == null) 
      throw new RuntimeException("Internal Program Error: Enclosing body does not contain this block." +
                                                 "  Please report this bug");
    
    BodyTypeChecker btc = createANewInstanceOfMe(bd, _file, _package, _importedFiles, _importedPackages, 
                                                 cloneVariableDataList(_vars), _thrown);
    TypeData statements_result = that.getStatements().visit(btc);
    thingsThatHaveBeenAssigned.addAll(btc.thingsThatHaveBeenAssigned);
    return statements_result;
  }
  
  
  
  public TypeData forTypeOnly(Type that) {
    Data sd = getSymbolData(that.getName(), _data, that);
    while (sd != null && !LanguageLevelVisitor.isJavaLibraryClass(sd.getSymbolData().getName())) {
      if (!checkAccessibility(that, sd.getMav(), sd.getName(), sd.getSymbolData(), _bodyData.getSymbolData(), "class")) {
        return null;
      }
      sd = sd.getOuterData();
    }
    return null;
  }
  
  
  protected void checkDuplicateExceptions(TryCatchStatement that) {
    
    LinkedList<SymbolData> catchBlockExceptions = new LinkedList<SymbolData>();
    CatchBlock[] catchBlocks = that.getCatchBlocks();
    for (int i = 0; i < catchBlocks.length; i++) {
      catchBlockExceptions.addLast(getSymbolData(catchBlocks[i].getException().getDeclarator().getType().getName(),
                                                 _data, catchBlocks[i].getException()));
    }
    for (int i = 0; i < catchBlockExceptions.size(); i++) {
      for (int j = i+1; j < catchBlockExceptions.size(); j++) {
        if (catchBlockExceptions.get(j) != null && catchBlockExceptions.get(j).isSubClassOf(catchBlockExceptions.get(i))) {
          _addError("Exception " + catchBlockExceptions.get(j).getName() + 
                    " has already been caught", catchBlocks[j].getException());
        }
      }
    }
  }
  
  
  protected SymbolData getCommonSuperType(SymbolData s1, SymbolData s2) {
    if ((s1 == null) && (s2 == null)) {
      return null;
    }
    
    if (s1 == SymbolData.NOT_FOUND && s2 != null) {return SymbolData.NOT_FOUND;}
    if (s2 == SymbolData.NOT_FOUND && s1 != null) {return SymbolData.NOT_FOUND;}
    
    if (s1 == null && s1 != SymbolData.NOT_FOUND) { return s2; }
    if (s2 == null && s1 != SymbolData.NOT_FOUND) {return s1;}
    if (s1==null || s2==null) {return null;}
    if (s1 == SymbolData.EXCEPTION) { return s2; }
    if (s2 == SymbolData.EXCEPTION) { return s1; }
    
    
    SymbolData sd = getCommonSuperTypeBaseCase(s1, s2);
    if (sd != null ) { return sd; }
    sd = getCommonSuperTypeBaseCase(s2, s1);
    if (sd != null) { return sd; }
    
    
    if (s1.getSuperClass() == null) {
      
      
      return getSymbolData("java.lang.Object", _data, new NullLiteral(SourceInfo.NO_INFO));
    }
    
    
    sd = getCommonSuperType(s1.getSuperClass(), s2);
    if (sd != null) {
      return sd;
    }
    
    
    for (SymbolData currSd : s1.getInterfaces()) {
      sd = getCommonSuperType(currSd, s2);
      if (sd != null) {
        return sd;
      }
    }
    return null;
  }


  
  protected boolean isException(SymbolData sd) {
    return sd == SymbolData.EXCEPTION || 
      sd.isSubClassOf(getSymbolData("java.lang.Throwable", new NullLiteral(SourceInfo.NO_INFO), false, false));
  }
  
  
  protected InstanceData tryCatchLeastRestrictiveType(InstanceData tryBlock_result, InstanceData[] catchBlocks_result, 
                                                      InstanceData finallyBlock_result) {
  
    if (tryBlock_result == null || tryBlock_result == SymbolData.NOT_FOUND.getInstanceData()) 
      return finallyBlock_result;
    TypeData leastRestrictiveType = tryBlock_result;
    for (int i = 0; i < catchBlocks_result.length; i++) {
      if (catchBlocks_result[i] == null) return finallyBlock_result;
      if (catchBlocks_result[i] != SymbolData.NOT_FOUND.getInstanceData() && 
          _isAssignableFrom(catchBlocks_result[i].getSymbolData(), leastRestrictiveType.getSymbolData())) {
        leastRestrictiveType = catchBlocks_result[i];
      }
    }

    SymbolData result;
    if (leastRestrictiveType == null && finallyBlock_result == null) return null;
    else if (leastRestrictiveType == null) result = getCommonSuperType(null, finallyBlock_result.getSymbolData());
    else if (finallyBlock_result == null) result = getCommonSuperType(leastRestrictiveType.getSymbolData(), null);
    else result = getCommonSuperType(leastRestrictiveType.getSymbolData(), finallyBlock_result.getSymbolData()); 
   
    if (result != null) return result.getInstanceData();
    return null;
  }
  
  
  public boolean isUncaughtCheckedException(SymbolData sd, JExpression that) {
    if (isCheckedException(sd, that)) {
      MethodData md = _bodyData.getMethodData();
      for (int i = 0; i<md.getThrown().length; i++) {
        
        if (sd.isSubClassOf(getSymbolData(md.getThrown()[i], _data, that))) {return false;}
      }
      return true;
    }
    return false;
  }

  
  protected void makeSureCaughtStuffWasThrown(TryCatchStatement that, SymbolData[] caught_array, LinkedList<Pair<SymbolData, JExpression>> thrown) {
    
    for (int i = 0; i < caught_array.length; i++) {
      SymbolData currCaughtSD = caught_array[i];

      boolean foundThrownException = false;
      if (isCheckedException(currCaughtSD, that) && 
          ! currCaughtSD.getName().equals("java.lang.Exception") &&
          ! currCaughtSD.getName().equals("java.lang.Throwable")) {

        for (Pair<SymbolData, JExpression> p : thrown) {
          SymbolData sd = p.getFirst();
          if (sd.isSubClassOf(currCaughtSD)) {
            foundThrownException = true;
          }
        }
        if (!foundThrownException) {

          _addError("The exception " + currCaughtSD.getName() + 
                    " is never thrown in the body of the corresponding try block", 
                    that.getCatchBlocks()[i]);
        }
      } 
    }
  }
  
  
  protected void compareThrownAndCaught(TryCatchStatement that, SymbolData[] caught_array, 
                                        LinkedList<Pair<SymbolData, JExpression>> thrown) {
    LinkedList<SymbolData> caught = new LinkedList<SymbolData>();
    for (int i = 0; i<caught_array.length; i++) {
      caught.addLast(caught_array[i]);
    }

    
    for (Pair<SymbolData, JExpression> p : thrown) {
      SymbolData sd = p.getFirst();
      JExpression j = p.getSecond();
      if (isUncaughtCheckedException(sd, j)) {
        boolean foundCatchBlock = false;
        for (SymbolData currCaughtSD : caught) {
          if (sd.isSubClassOf(currCaughtSD)) {
            foundCatchBlock = true;
          }
        }
        
        
        if (!foundCatchBlock) {
          handleUncheckedException(sd, j);
        }
      }
    }
    
    makeSureCaughtStuffWasThrown(that, caught_array, thrown);
  }

  
  public TypeData forTryCatchFinallyStatementOnly(TryCatchFinallyStatement that, TypeData tryBlock_result, TypeData[] catchBlocks_result, TypeData finallyBlock_result) {
    checkDuplicateExceptions(that);
    
    
    InstanceData[] ids = new InstanceData[catchBlocks_result.length];
    for (int i = 0; i<ids.length; i++) {
      if (catchBlocks_result[i] != null) {
        ids[i]=catchBlocks_result[i].getInstanceData();
      }
      else {ids[i]=null;}
    }
    
    
    if (tryBlock_result == null && finallyBlock_result==null) {return tryCatchLeastRestrictiveType(null, ids, null);}
    if (tryBlock_result == null) {return tryCatchLeastRestrictiveType(null, ids, finallyBlock_result.getInstanceData());}
    if (finallyBlock_result == null) {return tryCatchLeastRestrictiveType(tryBlock_result.getInstanceData(), ids, null);}

    return tryCatchLeastRestrictiveType(tryBlock_result.getInstanceData(), ids, finallyBlock_result.getInstanceData());
  }
 
   
  public TypeData forTryCatchFinallyStatement(TryCatchFinallyStatement that) {
    
    BodyTypeChecker btc = new TryCatchBodyTypeChecker(_bodyData, _file, _package, _importedFiles, _importedPackages, cloneVariableDataList(_vars), new LinkedList<Pair<SymbolData, JExpression>>());
    final TypeData tryBlock_result = that.getTryBlock().visit(btc);

    unassignVariableDatas(btc.thingsThatHaveBeenAssigned);
    
    BodyTypeChecker[] catchTCs = new BodyTypeChecker[that.getCatchBlocks().length];
    LinkedList<LinkedList<VariableData>> catchVars = new LinkedList<LinkedList<VariableData>>();
    CatchBlock[] catchBlocks = that.getCatchBlocks();
    final TypeData[] catchBlocks_result = makeArrayOfRetType(catchBlocks.length);
    final SymbolData[] caughtExceptions = new SymbolData[catchBlocks.length];

    for (int i = 0; i < catchBlocks.length; i++) {
      catchTCs[i] = createANewInstanceOfMe(_bodyData, _file, _package, _importedFiles, _importedPackages, cloneVariableDataList(_vars), _thrown);
      catchBlocks_result[i] = catchBlocks[i].visit(catchTCs[i]);
      unassignVariableDatas(catchTCs[i].thingsThatHaveBeenAssigned);
      catchVars.addLast(catchTCs[i].thingsThatHaveBeenAssigned);
      caughtExceptions[i] = getSymbolData(catchBlocks[i].getException().getDeclarator().getType().getName(), _data, catchBlocks[i]);
    }

    BodyTypeChecker btcFinally = createANewInstanceOfMe(_bodyData, _file, _package, _importedFiles, _importedPackages, cloneVariableDataList(_vars), _thrown);
    TypeData finallyBlock_result = that.getFinallyBlock().visit(btcFinally);
    
    
    
    reassignLotsaVariableDatas(btc.thingsThatHaveBeenAssigned, catchVars);

    
    
    if (finallyBlock_result == null) {
      compareThrownAndCaught(that, caughtExceptions, btc._thrown);
    }
    
    else { 
      _thrown = new LinkedList<Pair<SymbolData, JExpression>>();
    }
    
    if (finallyBlock_result == SymbolData.NOT_FOUND.getInstanceData()) {
      finallyBlock_result = null;
    }
    
    
    if (this instanceof TryCatchBodyTypeChecker) {
      _thrown.addAll(btc._thrown);
    }
    
    return forTryCatchFinallyStatementOnly(that, tryBlock_result, catchBlocks_result, finallyBlock_result);
  }
  
  
  
  public TypeData forCatchBlock(CatchBlock that) {
    VariableDeclarator dec = that.getException().getDeclarator();
    SymbolData exception_result = getSymbolData(dec.getType().getName(), _data, dec.getType());
    
    BlockData bd = _bodyData.getNextBlock();
    if (bd == null) { throw new RuntimeException("Internal Program Error: Enclosing body does not contain this block.  Please report this bug"); }
    VariableData vd = bd.getVar(dec.getName().getText());
    if (vd == null) { throw new RuntimeException("Internal Program Error: Catch block does not contain its exception variable.  Please report this bug"); }
    LinkedList<VariableData> newVars = cloneVariableDataList(_vars);
    newVars.addLast(vd);
    BodyTypeChecker btc = createANewInstanceOfMe(bd, _file, _package, _importedFiles, _importedPackages, newVars, _thrown);
    TypeData block_result = that.getBlock().getStatements().visit(btc);
    thingsThatHaveBeenAssigned.addAll(btc.thingsThatHaveBeenAssigned);
    return forCatchBlockOnly(that, exception_result, block_result);
  }
  
  
  public TypeData forCatchBlockOnly(CatchBlock that, SymbolData exception_result, TypeData block_result) {
    return block_result;
  }
  
  
  public TypeData forNormalTryCatchStatementOnly(NormalTryCatchStatement that, TypeData tryBlock_result, TypeData[] catchBlocks_result) {
    checkDuplicateExceptions(that);
    InstanceData[] ids = new InstanceData[catchBlocks_result.length];
    for (int i = 0; i<catchBlocks_result.length; i++) {
      ids[i]=(InstanceData) catchBlocks_result[i];

    }
      
    return tryCatchLeastRestrictiveType((InstanceData) tryBlock_result, ids, null);
  }
  
  
  public TypeData forNormalTryCatchStatement(NormalTryCatchStatement that) {
    BodyTypeChecker btc = new TryCatchBodyTypeChecker(_bodyData, _file, _package, _importedFiles, _importedPackages, cloneVariableDataList(_vars), new LinkedList<Pair<SymbolData, JExpression>>());
    final TypeData tryBlock_result = that.getTryBlock().visit(btc);
    unassignVariableDatas(btc.thingsThatHaveBeenAssigned);

    LinkedList<LinkedList<VariableData>> catchVars = new LinkedList<LinkedList<VariableData>>();
    BodyTypeChecker[] catchTCs = new BodyTypeChecker[that.getCatchBlocks().length];

    CatchBlock[] catchBlocks = that.getCatchBlocks();
    final TypeData[] catchBlocks_result = makeArrayOfRetType(catchBlocks.length);
    final SymbolData[] caughtExceptions = new SymbolData[catchBlocks.length];
    for (int i = 0; i < catchBlocks.length; i++) {
      catchTCs[i] = createANewInstanceOfMe(_bodyData, _file, _package, _importedFiles, _importedPackages, cloneVariableDataList(_vars), _thrown);
      catchBlocks_result[i] = catchBlocks[i].visit(catchTCs[i]);
      unassignVariableDatas(catchTCs[i].thingsThatHaveBeenAssigned);
      catchVars.addLast(catchTCs[i].thingsThatHaveBeenAssigned);
      caughtExceptions[i] = getSymbolData(catchBlocks[i].getException().getDeclarator().getType().getName(), _data, catchBlocks[i]);
    }
    
    
    reassignLotsaVariableDatas(btc.thingsThatHaveBeenAssigned, catchVars);
    
    compareThrownAndCaught(that, caughtExceptions, btc._thrown);    

    
    if (this instanceof TryCatchBodyTypeChecker) {
      _thrown.addAll(btc._thrown);
    }   
    return forNormalTryCatchStatementOnly(that, tryBlock_result, catchBlocks_result);
  }
  
  
  
  private class NoAssignmentAllowedInExpression extends JExpressionIFAbstractVisitor<Boolean> {
    String _location;
    private NoAssignmentAllowedInExpression(String location) { _location = location; }
  
    
    public Boolean defaultCase(JExpressionIF that) { return Boolean.TRUE; }
  
    
    public Boolean forIncrementExpression(IncrementExpression that) {
      _addError("You cannot use an increment or decrement expression in " + _location +  " at any language level", that);
      return Boolean.FALSE;
    }
  
    
    public Boolean forAssignmentExpression(AssignmentExpression that) {
      _addError("You cannot use an assignment expression in " + _location +  " at any language level", that);
      return Boolean.FALSE;
    }
    
    
    public Boolean forSimpleAssignmentExpression(SimpleAssignmentExpression that) {
      _addError("You cannot use an assignment expression in " + _location +  " at any language level" + ".  Perhaps you meant to compare two values with '=='", that);
      return Boolean.FALSE;
    }

  }
  
   
  public static class BodyTypeCheckerTest extends TestCase {
    
    private BodyTypeChecker _bbtc;
    
    private BodyData _bd1;
    private BodyData _bd2;
    
    private SymbolData _sd1;
    private SymbolData _sd2;
    private SymbolData _sd3;
    private SymbolData _sd4;
    private SymbolData _sd5;
    private SymbolData _sd6;
    private ModifiersAndVisibility _publicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"});
    private ModifiersAndVisibility _protectedMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"protected"});
    private ModifiersAndVisibility _privateMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"private"});
    private ModifiersAndVisibility _packageMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[0]);
    private ModifiersAndVisibility _abstractMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"abstract"});
    private ModifiersAndVisibility _finalMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final"});
    
    
    public BodyTypeCheckerTest() {
      this("");
    }
    public BodyTypeCheckerTest(String name) {
      super(name);
    }
    
    public void setUp() {
      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _sd3 = new SymbolData("zebra");
      _sd4 = new SymbolData("u.like.emu");
      _sd5 = new SymbolData("");
      _sd6 = new SymbolData("cebu");

      _bd1 = new MethodData("methodName1", 
                            _packageMav, 
                            new TypeParameter[0], 
                            SymbolData.INT_TYPE, 
                            new VariableData[] { new VariableData("i", _publicMav, SymbolData.INT_TYPE, true, null), new VariableData(SymbolData.BOOLEAN_TYPE) },
                            new String[0],
                            _sd1,
                            null); 
      ((MethodData)_bd1).getParams()[0].setEnclosingData(_bd1);                      
      ((MethodData)_bd1).getParams()[1].setEnclosingData(_bd1);                      

      _bd2 = new MethodData("methodName2", 
                            _packageMav, 
                            new TypeParameter[0], 
                            SymbolData.VOID_TYPE, 
                            new VariableData[] { new VariableData(SymbolData.INT_TYPE) },
                            new String[0],
                            _sd1,
                            null); 
       ((MethodData)_bd2).getParams()[0].setEnclosingData(_bd2);
                            
      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      _bd1.addEnclosingData(_sd1);
      _bd1.addVars(((MethodData)_bd1).getParams());
      _bd2.addVars(((MethodData)_bd2).getParams());
      _bbtc = new BodyTypeChecker(_bd1, new File(""), "", new LinkedList<String>(), new LinkedList<String>(), 
                                  new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData,JExpression>>());
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make());
      _bbtc._importedPackages.addFirst("java.lang");
    }
    
    public void testForUninitializedVariableDeclaratorOnly() {
      VariableData vd1 = new VariableData("Mojo", _publicMav, SymbolData.INT_TYPE, false, _bd1);
      _bd1.addVar(vd1);
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                                                                new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                                                new Word(SourceInfo.NO_INFO, "Mojo"));
      uvd.visit(_bbtc);
      assertTrue("_vars should contain Mojo.", _bbtc._vars.contains(vd1));
    }
    
    public void testForInitializedVariableDeclaratorOnly() {
      _bbtc.symbolTable.put("int", SymbolData.INT_TYPE);
      VariableData vd1 = new VariableData("Mojo", _publicMav, SymbolData.INT_TYPE, true, _bd1);
      _bd1.addVar(vd1);
      InitializedVariableDeclarator ivd = new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                                                            new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                                                            new Word(SourceInfo.NO_INFO, "Mojo"), 
                                                                            new IntegerLiteral(SourceInfo.NO_INFO, 1));
      ivd.visit(_bbtc);
      assertEquals("There should be no errors.", 0, errors.size());
      assertTrue("_vars should contain Mojo.", _bbtc._vars.contains(vd1));
      ivd = new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                              new PrimitiveType(SourceInfo.NO_INFO, "int"), 
                                              new Word(SourceInfo.NO_INFO, "Santa's Little Helper"), 
                                              new IntegerLiteral(SourceInfo.NO_INFO, 1));
      try {
        ivd.visit(_bbtc);
        fail("Should have thrown a RuntimeException because there's no field named Santa's Little Helper.");
      }
      catch (RuntimeException re) {
        assertEquals("The error message should be correct.", "Internal Program Error: The field or variable Santa's Little Helper was not found in this block.  Please report this bug.", re.getMessage());
      }
    }
    
    public void testForBracedBodyOnly() {
      
      BracedBody bb1 = new BracedBody(SourceInfo.NO_INFO,
                                      new BodyItemI[] { new ValueReturnStatement(SourceInfo.NO_INFO,
                                                                                 new IntegerLiteral(SourceInfo.NO_INFO, 1))});
      TypeData sd = bb1.visit(_bbtc);
      assertEquals("There should be no errors", 0, errors.size());
      assertEquals("Should return int type", SymbolData.INT_TYPE.getInstanceData(), sd);
      BracedBody bb2 = new BracedBody(SourceInfo.NO_INFO,
                                      new BodyItemI[] { new ValueReturnStatement(SourceInfo.NO_INFO,
                                                                                 new CharLiteral(SourceInfo.NO_INFO, 'e'))});
     
      sd = bb2.visit(_bbtc);
      assertEquals("There should be no errors", 0, errors.size());
      assertEquals("Should return char type", SymbolData.CHAR_TYPE.getInstanceData(), sd);
      BracedBody bb3 = new BracedBody(SourceInfo.NO_INFO,
                                      new BodyItemI[] { new ValueReturnStatement(SourceInfo.NO_INFO,
                                                                                 new IntegerLiteral(SourceInfo.NO_INFO, 1)),
                                                        new ValueReturnStatement(SourceInfo.NO_INFO,
                                                                                 new CharLiteral(SourceInfo.NO_INFO, 'e'))});
      
      sd = bb3.visit(_bbtc);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "Unreachable statement.", errors.get(0).getFirst());
      assertEquals("Should return int type", SymbolData.INT_TYPE.getInstanceData(), sd);
      
      BracedBody bb4 = new BracedBody(SourceInfo.NO_INFO,
                                      new BodyItemI[0]);
      
      sd = bb4.visit(_bbtc);
      assertEquals("There should still be one error", 1, errors.size());
      assertEquals("The error message should still be be correct", "Unreachable statement.", errors.get(0).getFirst());
      assertEquals("Should return null", null, sd);
    }
    
    public void testForVoidReturnStatementOnly() {
      _bbtc._bodyData = _bd2; 

      
      BracedBody bb1 = new BracedBody(SourceInfo.NO_INFO, 
                                      new BodyItemI[] { new VoidReturnStatement(SourceInfo.NO_INFO)});

      TypeData sd = bb1.visit(_bbtc);

      assertEquals("There should be no errors.", 0, errors.size());
      assertEquals("Should return void type.", SymbolData.VOID_TYPE.getInstanceData(), sd);

      
      _bbtc._bodyData = _bd1;
      sd = bb1.visit(_bbtc);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("Should return int type", SymbolData.INT_TYPE.getInstanceData(), sd);
      assertEquals("Error message should be correct", "Cannot return void when the method's expected return type is not void.", errors.get(0).getFirst());

    }
   
    public void testforValueReturnStatementOnly() {
      
      BracedBody bb1 = new BracedBody(SourceInfo.NO_INFO,
                                      new BodyItemI[] { new ValueReturnStatement(SourceInfo.NO_INFO,
                                                                                 new BooleanLiteral(SourceInfo.NO_INFO, true))});
      TypeData sd = bb1.visit(_bbtc);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("Should return boolean type", SymbolData.BOOLEAN_TYPE.getInstanceData(), sd);
      assertEquals("Error message should be correct", "This method expected to return type: \"int\" but here returned type: \"boolean\".", errors.get(0).getFirst());
      
      
      BracedBody bb2 = new BracedBody(SourceInfo.NO_INFO,
                                      new BodyItemI[] { new ValueReturnStatement(SourceInfo.NO_INFO,
                                                                                 new CharLiteral(SourceInfo.NO_INFO, 'c'))});
      sd = bb2.visit(_bbtc);
      assertEquals("There should be still be one error", 1, errors.size());
      assertEquals("Should return char type", SymbolData.CHAR_TYPE.getInstanceData(), sd);
      assertEquals("Error message should still be correct", "This method expected to return type: \"int\" but here returned type: \"boolean\".", errors.get(0).getFirst());
      
      
      
      _bbtc._bodyData = _bd2; 
      
      BracedBody bb3 = new BracedBody(SourceInfo.NO_INFO,
                                      new BodyItemI[] { new ValueReturnStatement(SourceInfo.NO_INFO,
                                                                                 new IntegerLiteral(SourceInfo.NO_INFO, 1))});

      sd = bb3.visit(_bbtc);
      assertEquals("There should be two errors", 2, errors.size());
      assertEquals("Should return void type", SymbolData.VOID_TYPE.getInstanceData(), sd);
      assertEquals("Error message should be correct", "Cannot return a value when the method's expected return type is void.", errors.get(1).getFirst());

      
      BracedBody bb4 = new BracedBody(SourceInfo.NO_INFO,
                                      new BodyItemI[] { new ValueReturnStatement(SourceInfo.NO_INFO,
                                                                                 new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "int")))});

      sd = bb4.visit(_bbtc);
      assertEquals("There should be 3 errors", 3, errors.size());
      assertEquals("Should return int type", SymbolData.INT_TYPE.getInstanceData(), sd);
      assertEquals("Error message should be correct", "You cannot return a class or interface name.  Perhaps you meant to say int.class or to create an instance", errors.getLast().getFirst());

    }
    
    public void testForIfThenElseStatementOnly() {
      
      IfThenElseStatement ites1 = new IfThenElseStatement(SourceInfo.NO_INFO,
                                                          new IntegerLiteral(SourceInfo.NO_INFO, 1),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 4)),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new CharLiteral(SourceInfo.NO_INFO, 'j')));

      TypeData sd = ites1.visit(_bbtc);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("Error message should be correct", "This if-then-else statement's conditional expression must be a boolean value. Instead, its type is int", errors.get(0).getFirst());

      assertEquals("Should return integer type", SymbolData.INT_TYPE.getInstanceData(), sd);
                                                    
      
      
      IfThenElseStatement ites2 = new IfThenElseStatement(SourceInfo.NO_INFO,
                                                          new BooleanLiteral(SourceInfo.NO_INFO, true),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 4)),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new BooleanLiteral(SourceInfo.NO_INFO, true)));

      sd = ites2.visit(_bbtc);
      assertEquals("There should be two errors", 2, errors.size());
      
      assertEquals("Should return Object type", "java.lang.Object", sd.getName());
      assertEquals("Error message should be correct", 
                   "This method expected to return type: \"int\" but here returned type: \"boolean\".", 
                   errors.get(1).getFirst());                                                          

      
      IfThenElseStatement ites3 = new IfThenElseStatement(SourceInfo.NO_INFO,
                                                          new BooleanLiteral(SourceInfo.NO_INFO, true),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 4)),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new CharLiteral(SourceInfo.NO_INFO, 'f')));

      sd = ites3.visit(_bbtc);
      assertEquals("There should still be two errors", 2, errors.size());
      assertEquals("Should return int type", SymbolData.INT_TYPE.getInstanceData(), sd);
      
      
      IfThenElseStatement ites4 = new IfThenElseStatement(SourceInfo.NO_INFO,
                                                          new BooleanLiteral(SourceInfo.NO_INFO, true),
                                                          new EmptyStatement(SourceInfo.NO_INFO),
                                                          new EmptyStatement(SourceInfo.NO_INFO));

      sd = ites4.visit(_bbtc);
      assertEquals("There should still be two errors", 2, errors.size());
      assertEquals("Should return null type", null, sd);
      
      
      IfThenElseStatement ites5 = new IfThenElseStatement(SourceInfo.NO_INFO,
                                                          new BooleanLiteral(SourceInfo.NO_INFO, true),
                                                          new EmptyStatement(SourceInfo.NO_INFO),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 3)));

      sd = ites5.visit(_bbtc);
      assertEquals("There should still be two errors", 2, errors.size());
      assertEquals("Should return null type", null, sd);      

      
      
      IfThenElseStatement ites6 = new IfThenElseStatement(SourceInfo.NO_INFO,
                                                          new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "boolean")),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 4)),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 4)));
                                                          
      sd = ites6.visit(_bbtc);
      assertEquals("There should be 3 errors", 3, errors.size());
      
      assertEquals("Should return Integer type", SymbolData.INT_TYPE.getInstanceData(), sd);
      assertEquals("Error message should be correct", 
                   "This if-then-else statement's conditional expression must be a boolean value. Instead, it is a class or interface name", 
                   errors.get(2).getFirst());                                                          
}
    
    public void testForBlock() {
      
      Block b = new Block(SourceInfo.NO_INFO, 
                          new BracedBody(SourceInfo.NO_INFO,
                                         new BodyItemI[] { new ValueReturnStatement(SourceInfo.NO_INFO,
                                                                                    new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")))}));
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      LinkedList<VariableData> vars = new LinkedList<VariableData>();
      vars.addLast(new VariableData("i", _publicMav, SymbolData.INT_TYPE, true, _bd1));
      _bbtc._vars = vars;
      TypeData sd = b.visit(_bbtc);
      assertEquals("There should not be any errors.", 0, errors.size());
      assertEquals("Should return int type.", SymbolData.INT_TYPE.getInstanceData(), sd);
    }
    
    public void testForIfThenStatementOnly() {
      SymbolData sd1 = SymbolData.BOOLEAN_TYPE;
      SymbolData sd2 = SymbolData.INT_TYPE;

      IfThenStatement its = new IfThenStatement(SourceInfo.NO_INFO, 
                                                new NullLiteral(SourceInfo.NO_INFO), 
                                                new EmptyStatement(SourceInfo.NO_INFO));
      

      
      assertEquals("sd1 is boolean type, so should not add error. Returns null.", null, 
                   _bbtc.forIfThenStatementOnly(its, sd1.getInstanceData(), null));
      assertEquals("No errors should have been added", 0, errors.size());
      
      
      assertEquals("sd2 is not boolean type, so should add error. Returns null.", null, 
                   _bbtc.forIfThenStatementOnly(its, sd2.getInstanceData(), null));
      assertEquals("Should now be one error.", 1, errors.size());
      assertEquals("Error message should be correct.", "This if-then-statement's conditional expression must be a boolean value. Instead, its type is int", errors.getLast().getFirst());
      
      
      assertEquals("sd1 is not an instance, so should add error. Returns null.", null, 
                   _bbtc.forIfThenStatementOnly(its, sd1, null));
      assertEquals("Should now be 2 errors.", 2, errors.size());
      assertEquals("Error message should be correct.", "This if-then-statement's conditional expression must be a boolean value. Instead, it is a class or interface name", errors.getLast().getFirst());
    }
   
    public void testForIfThenStatement() {
      
      
      Expression te = new LessThanExpression(SourceInfo.NO_INFO, 
                                             new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                     new Word(SourceInfo.NO_INFO, "j")),
                                             new IntegerLiteral(SourceInfo.NO_INFO, 5));
      Statement ts = 
        new ExpressionStatement(SourceInfo.NO_INFO, 
                                new SimpleAssignmentExpression(SourceInfo.NO_INFO, 
                                                               new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                                       new Word(SourceInfo.NO_INFO, "i")), 
                                                               new IntegerLiteral(SourceInfo.NO_INFO, 10)));
      IfThenStatement ift = new IfThenStatement(SourceInfo.NO_INFO, te, ts);
      
      PrimitiveType intt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      UninitializedVariableDeclarator uvd = 
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "i"));
      FormalParameter param = 
        new FormalParameter(SourceInfo.NO_INFO, 
                            new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, 
                                                                new Word(SourceInfo.NO_INFO, "j")), false);
      BracedBody bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] { 
        new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift});
      
      ConcreteMethodDef cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                                     new ReferenceType[0], bb);

      VariableData vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      VariableData vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      MethodData md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);
      md1.addVar(vd1);
      md1.addVar(vd2);
      
      LinkedList<VariableData> vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, 
                                  new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      
      
      
      ift.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());

      
      

      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, true, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, cmd1);

      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);
      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, 
                                  new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;

      ift.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertTrue("vd2 should also be assigned", vd2.hasValue());
 
      
      
      te = new LessThanExpression(SourceInfo.NO_INFO, 
                                  new SimpleNameReference(SourceInfo.NO_INFO, 
                                                          new Word(SourceInfo.NO_INFO, "j")),
                                  new IntegerLiteral(SourceInfo.NO_INFO, 5));
      Statement assignStatement = 
        new ExpressionStatement(SourceInfo.NO_INFO, 
                                new SimpleAssignmentExpression(SourceInfo.NO_INFO, 
                                                               new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                                       new Word(SourceInfo.NO_INFO, "i")), 
                                                               new IntegerLiteral(SourceInfo.NO_INFO, 10)));
      Statement returnStatement = 
        new ValueReturnStatement(SourceInfo.NO_INFO, 
                                 new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")));
      ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement, returnStatement}));
      ift = new IfThenStatement(SourceInfo.NO_INFO, te, ts);
      
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                   new ReferenceType[0], bb);

      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                           new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);
      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      ift.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());
      assertEquals("There should be no errors", 0, errors.size());
      errors = new LinkedList<Pair<String, JExpressionIF>>();
      
      
      te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));
      ts = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));
      ift = new IfThenStatement(SourceInfo.NO_INFO, te, ts);
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                                     new ReferenceType[0], bb);

      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      
      te = new PlusAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));
      ts = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));
      ift = new IfThenStatement(SourceInfo.NO_INFO, te, ts);
      
      ift.visit(_bbtc);
      assertEquals("There should now be one error", 1, errors.size());
      assertEquals("Error message should be correct", "You cannot use an assignment expression in the conditional expression of an if-then statement at any language level", errors.get(0).getFirst());
      
      
    }
    
    public void testForIfThenElseStatement() {
      
      
      
      Expression te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));
      Statement ts = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));

      IfThenElseStatement ift = new IfThenElseStatement(SourceInfo.NO_INFO, te, ts, new EmptyStatement(SourceInfo.NO_INFO));
      
      PrimitiveType intt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "i"));
      FormalParameter param = new FormalParameter(SourceInfo.NO_INFO, new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "j")), false);
      BracedBody bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift});
      
      ConcreteMethodDef cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                                     new ReferenceType[0], bb);

      VariableData vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      VariableData vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      MethodData md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      LinkedList<VariableData> vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;      
      
      ift.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());
      
      
      te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));
      Statement assignStatement = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));
      ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement}));
      ift = new IfThenElseStatement(SourceInfo.NO_INFO, te, new EmptyStatement(SourceInfo.NO_INFO), ts);
      
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                   new ReferenceType[0], bb);
                                   
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                           new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      ift.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());
      assertEquals("There should be no errors", 0, errors.size());
      
      

      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, true, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));

      ift.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertTrue("vd2 should also be assigned", vd2.hasValue());
 
      
      
      te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));
      assignStatement = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));
      Statement returnStatement = new ValueReturnStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")));
      ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement, returnStatement}));
      ift = new IfThenElseStatement(SourceInfo.NO_INFO, te, ts, new EmptyStatement(SourceInfo.NO_INFO));
      
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                   new ReferenceType[0], bb);

      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                           new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));

      
      ift.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());
      assertEquals("There should be no errors", 0, errors.size());
      errors = new LinkedList<Pair<String, JExpressionIF>>();

      
      
      te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));
      assignStatement = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));
      ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement}));
      ift = new IfThenElseStatement(SourceInfo.NO_INFO, te, ts, new ExpressionStatement(SourceInfo.NO_INFO, new EqualsExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word (SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 32))));
      
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                   new ReferenceType[0], bb);
                                   
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                           new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
       
     
      ift.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "You cannot use i because it may not have been given a value", errors.get(0).getFirst());
      
      
      te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));
      assignStatement = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));
      ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement}));
      ift = new IfThenElseStatement(SourceInfo.NO_INFO, te, ts, ts);
      
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                   new ReferenceType[0], bb);
                                   
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                           new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      
      ift.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertTrue("vd2 should be assigned", vd2.hasValue());
      assertEquals("There should be one error", 1, errors.size());
      
      
      
      te = new PlusAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 5));
      assignStatement = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));      
      ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement}));
      ift = new IfThenElseStatement(SourceInfo.NO_INFO, te, new EmptyStatement(SourceInfo.NO_INFO), ts);
      
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                   new ReferenceType[0], bb);
                                   
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, true, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                           new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      
      ift.visit(_bbtc);
      assertEquals("There should now be two errors", 2, errors.size());
      assertEquals("The error message should be correct", "You cannot use an assignment expression in the conditional expression of an if-then-else statement at any language level", errors.get(1).getFirst());
      
      
      te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));
      returnStatement = new ValueReturnStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")));
      ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {returnStatement}));
      BreakStatement bs = new UnlabeledBreakStatement(SourceInfo.NO_INFO);
      ift = new IfThenElseStatement(SourceInfo.NO_INFO, te, ts, bs);
      
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), ift});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                   new ReferenceType[0], bb);
                                   
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, true, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                           new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));

      assertEquals("Should return SymbolData.NOT_FOUND", SymbolData.NOT_FOUND.getInstanceData(), ift.visit(_bbtc));
      
      assertEquals("There should still be two errors", 2, errors.size());      
    }
    
    public void testForForStatement() {
      
      
      Expression te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));

      UnparenthesizedExpressionList sel = new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10))});
      ForStatement fs = new ForStatement(SourceInfo.NO_INFO, sel, te, new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]), new EmptyStatement(SourceInfo.NO_INFO));
      
      
      PrimitiveType intt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "i"));
      FormalParameter param = new FormalParameter(SourceInfo.NO_INFO, new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "j")), false);
      BracedBody bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), fs});
      
      ConcreteMethodDef cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                                     intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                                     new ReferenceType[0], bb);
                                                     
      VariableData vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      VariableData vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      MethodData md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                      new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      LinkedList<VariableData> vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;      

      fs.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());
      assertEquals("There should be no errors", 0, errors.size());
      
      
      te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));
      VariableDeclaration vd = new VariableDeclaration (SourceInfo.NO_INFO, _publicMav, new VariableDeclarator[] { new InitializedVariableDeclarator(SourceInfo.NO_INFO, new PrimitiveType(SourceInfo.NO_INFO, "int"), new Word(SourceInfo.NO_INFO, "i"), new IntegerLiteral(SourceInfo.NO_INFO, 10))});
      UnparenthesizedExpressionList sel2 = new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {te});
      fs = new ForStatement(SourceInfo.NO_INFO, sel, te, sel2, new ExpressionStatement(SourceInfo.NO_INFO, te));
            
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {fs});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                   new ReferenceType[0], bb);
                                   
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                           new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      
      fs.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());
      assertEquals("There should be no errors", 0, errors.size());
      
      
      Statement ts = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));


      sel = new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10))});
      fs = new ForStatement(SourceInfo.NO_INFO, sel, new EmptyForCondition(SourceInfo.NO_INFO), new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]), new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {ts})));
      
      
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), fs});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                                     intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                                     new ReferenceType[0], bb);
                                                     
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                      new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;      
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      fs.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());
      assertEquals("There should be no errors", 0, errors.size());

      


      sel = new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10))});
      fs = new ForStatement(SourceInfo.NO_INFO, sel, new EmptyForCondition(SourceInfo.NO_INFO), new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]), new EmptyStatement(SourceInfo.NO_INFO));
      
      
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), fs});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                                     intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                                     new ReferenceType[0], bb);
                                                     
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, true, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                      new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;      
      
      fs.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertTrue("vd2 should be assigned", vd2.hasValue());
      assertEquals("Should be 0 errors", 0, errors.size());
      

      te = new PlusAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")), new IntegerLiteral(SourceInfo.NO_INFO, 5));
      vd = new VariableDeclaration (SourceInfo.NO_INFO, _publicMav, new VariableDeclarator[] { new InitializedVariableDeclarator(SourceInfo.NO_INFO, new PrimitiveType(SourceInfo.NO_INFO, "int"), new Word(SourceInfo.NO_INFO, "i"), new IntegerLiteral(SourceInfo.NO_INFO, 10))});
      sel2 = new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {te});
      fs = new ForStatement(SourceInfo.NO_INFO, sel, te, sel2, new EmptyStatement(SourceInfo.NO_INFO));
            
      bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {fs});
      
      cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                   intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                   new ReferenceType[0], bb);
                                   
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                           new VariableData[] {vd1}, new String[0], _sd1, cmd1);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      
      te = new PositivePrefixIncrementExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")));
      vd = new VariableDeclaration (SourceInfo.NO_INFO, _publicMav, new VariableDeclarator[] { new InitializedVariableDeclarator(SourceInfo.NO_INFO, new PrimitiveType(SourceInfo.NO_INFO, "int"), new Word(SourceInfo.NO_INFO, "i"), new IntegerLiteral(SourceInfo.NO_INFO, 10))});
      sel2 = new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {te});
      fs = new ForStatement(SourceInfo.NO_INFO, sel, te, sel2, new EmptyStatement(SourceInfo.NO_INFO));

      fs.visit(_bbtc);
      assertEquals("There should be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "You cannot use an increment or decrement expression in the conditional expression of a for-statement at any language level", errors.get(0).getFirst());
      
      
    }
    
    public void testForWhileStatement() {
      
      Expression te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));


      Statement assignStatement = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));      

      Statement ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement}));
      WhileStatement ws = new WhileStatement(SourceInfo.NO_INFO, te, ts);
      
      VariableData vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      VariableData vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      
      MethodData md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, null);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      LinkedList<VariableData> vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      
      ws.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());

      
      
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, true, null);
      
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, null);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      
      ws.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertTrue("vd2 should be assigned", vd2.hasValue());

     
      
      te = new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 5));
      ws = new WhileStatement(SourceInfo.NO_INFO, te, ts);

      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, true, null);
      
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, null);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));

      
      ws.visit(_bbtc);
      assertEquals("There should be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "You cannot use an assignment expression in the condition expression of a while statement at any language level.  Perhaps you meant to compare two values with '=='", errors.get(0).getFirst());
      
      

    }
    
    public void testForWhileStatementOnly() {
      
      Expression te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));

      
      Statement assignStatement = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));      

      Statement ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement}));
      WhileStatement ws = new WhileStatement(SourceInfo.NO_INFO, te, ts);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));

      assertEquals("Should return null", null, _bbtc.forWhileStatementOnly(ws, SymbolData.BOOLEAN_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("There should be no errors", 0, errors.size());

      
      assertEquals("Should return null", null, _bbtc.forWhileStatementOnly(ws, SymbolData.INT_TYPE.getInstanceData(), SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("There should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "This while-statement's conditional expression must be a boolean value. Instead, its type is int", errors.get(0).getFirst());
 
      
      assertEquals("Should return null", null, _bbtc.forWhileStatementOnly(ws, SymbolData.BOOLEAN_TYPE, SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("There should be 2 error", 2, errors.size());
      assertEquals("Error message should be correct", "This while-statement's conditional expression must be a boolean value. Instead, it is a class or interface name", errors.getLast().getFirst());
    }
    
    public void testForForStatementOnly() {
      
      Expression te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));

      UnparenthesizedExpressionList sel = new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10))});
      ForStatement fs = new ForStatement(SourceInfo.NO_INFO, sel, new NullLiteral(SourceInfo.NO_INFO), new UnparenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]), new EmptyStatement(SourceInfo.NO_INFO));
  
      
      
      assertEquals("Should return null", null, _bbtc.forForStatementOnly(fs, SymbolData.INT_TYPE, SymbolData.BOOLEAN_TYPE.getInstanceData(), SymbolData.INT_TYPE, SymbolData.INT_TYPE));
      assertEquals("There should be no errors", 0, errors.size());
      
                   
      
      assertEquals("Should return null", null, _bbtc.forForStatementOnly(fs, SymbolData.INT_TYPE, SymbolData.DOUBLE_TYPE.getInstanceData(), SymbolData.INT_TYPE, SymbolData.CHAR_TYPE));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "This for-statement's conditional expression must be a boolean value. Instead, its type is double", errors.get(0).getFirst());

      
      assertEquals("Should return null", null, _bbtc.forForStatementOnly(fs, SymbolData.INT_TYPE, SymbolData.BOOLEAN_TYPE, SymbolData.INT_TYPE, SymbolData.CHAR_TYPE));
      assertEquals("Should be 2 error", 2, errors.size());
      assertEquals("The error message should be correct", "This for-statement's conditional expression must be a boolean value. Instead, it is a class or interface name", errors.getLast().getFirst());
    }
    
    public void testForDoStatement() {
      
      Expression te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));


      Statement assignStatement = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));      

      Statement ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement}));
      DoStatement ds = new DoStatement(SourceInfo.NO_INFO, ts, te);
      
      VariableData vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      VariableData vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, false, null);
      
      MethodData md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, null);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      LinkedList<VariableData> vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      
      ds.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertFalse("vd2 should not be assigned", vd2.hasValue());

      
      
      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, true, null);
      
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, null);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      
      ds.visit(_bbtc);
      assertTrue("vd1 should be assigned", vd1.hasValue());
      assertTrue("vd2 should be assigned", vd2.hasValue());

     
      
      te = new PlusAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 5));
      ds = new DoStatement(SourceInfo.NO_INFO, ts, te);

      vd1 = new VariableData("j", _packageMav, SymbolData.INT_TYPE, true, null);
      vd2 = new VariableData("i", _packageMav, SymbolData.INT_TYPE, true, null);
      
      md1 = new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE,
                                     new VariableData[] {vd1}, new String[0], _sd1, null);
      
      vd1.setEnclosingData(md1);
      vd2.setEnclosingData(md1);

      md1.addVar(vd1);
      md1.addVar(vd2);
      
      vars = new LinkedList<VariableData>();
      vars.addLast(vd1);
      vars.addLast(vd2);
      _bbtc = new BodyTypeChecker(md1, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, vars, new LinkedList<Pair<SymbolData, JExpression>>());
      _bbtc._bodyData = md1;
      _bbtc._data = md1;
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      
      ds.visit(_bbtc);
      assertEquals("There should be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "You cannot use an assignment expression in the condition expression of a do statement at any language level", errors.get(0).getFirst());
      
    }
    
    public void testForDoStatementOnly() {
      Expression te = new LessThanExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")),
        new IntegerLiteral(SourceInfo.NO_INFO, 5));

      
      Statement assignStatement = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 10)));      

      Statement ts = new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignStatement}));
      DoStatement ds = new DoStatement(SourceInfo.NO_INFO, ts, te);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));

      
      assertEquals("Should return int", SymbolData.INT_TYPE.getInstanceData(), _bbtc.forDoStatementOnly(ds, SymbolData.INT_TYPE, SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("There should be no errors", 0, errors.size());

      
      assertEquals("Should return int", SymbolData.INT_TYPE.getInstanceData(), _bbtc.forDoStatementOnly(ds, SymbolData.INT_TYPE, SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "This do-statement's conditional expression must be a boolean value. Instead, its type is double", errors.get(0).getFirst());

      
      assertEquals("Should return double", SymbolData.DOUBLE_TYPE.getInstanceData(), _bbtc.forDoStatementOnly(ds, SymbolData.DOUBLE_TYPE.getInstanceData(), SymbolData.BOOLEAN_TYPE));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("The error message should be correct", "This do-statement's conditional expression must be a boolean value. Instead, it is a class or interface name", errors.getLast().getFirst());
    }
    

   public void testForSwitchStatementOnly() {
     
     SwitchStatement ss = new SwitchStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 1), new SwitchCase[0]);
     assertEquals("Should return null--no default block", null, _bbtc.forSwitchStatementOnly(ss, 
                                                                                             SymbolData.CHAR_TYPE.getInstanceData(), 
                                                                                             new TypeData[] {SymbolData.INT_TYPE}, 
                                                                                             false));
     
     
     assertEquals("Should return null--has a not-found block", null, _bbtc.forSwitchStatementOnly(ss, 
                                                                                             SymbolData.CHAR_TYPE.getInstanceData(), 
                                                                                             new TypeData[] {SymbolData.NOT_FOUND, SymbolData.INT_TYPE}, 
                                                                                             true));
     
     assertEquals("Should return null--has a not-found block", null, _bbtc.forSwitchStatementOnly(ss, 
                                                                                             SymbolData.CHAR_TYPE.getInstanceData(), 
                                                                                             new TypeData[] {SymbolData.INT_TYPE, SymbolData.NOT_FOUND}, 
                                                                                             true));
                  
     
     
     assertEquals("Should return null--last block is null", null, _bbtc.forSwitchStatementOnly(ss, 
                                                                                             SymbolData.CHAR_TYPE.getInstanceData(), 
                                                                                             new TypeData[] {SymbolData.INT_TYPE, SymbolData.CHAR_TYPE, null}, 
                                                                                             true));
                                                                                             
     
     
     assertEquals("Should NOT return null", SymbolData.INT_TYPE.getInstanceData(), _bbtc.forSwitchStatementOnly(ss, 
                                                                                             SymbolData.CHAR_TYPE.getInstanceData(), 
                                                                                             new TypeData[] {SymbolData.INT_TYPE, SymbolData.CHAR_TYPE, null, SymbolData.CHAR_TYPE}, 
                                                                                             true));
                                                                                             
    }
 
    public void testForSwitchStatement() {
      _bbtc._vars.addLast(new VariableData("dan", _publicMav, SymbolData.INT_TYPE, true, _bbtc._bodyData));
      
      
      SwitchStatement ss = new SwitchStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "dan")), new IntegerLiteral(SourceInfo.NO_INFO, 5)), new SwitchCase[0]);
      assertEquals("Should return null", null, ss.visit(_bbtc));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "You cannot use an assignment expression in the switch expression of a switch statement at any language level.  Perhaps you meant to compare two values with '=='", errors.getLast().getFirst());
      
      
      ss = new SwitchStatement(SourceInfo.NO_INFO, new DoubleLiteral(SourceInfo.NO_INFO, 4.2), new SwitchCase[0]);
      assertEquals("Should return null", null, ss.visit(_bbtc));
      assertEquals("Should be 2 error", 2, errors.size());
      assertEquals("Error message should be correct", "The switch expression must be either an int or a char.  You have used a double", errors.getLast().getFirst());

      
      UnbracedBody emptyBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);

      LabeledCase l1 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5), emptyBody);
      LabeledCase l2 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5), emptyBody);
      LabeledCase l3 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 7), emptyBody);

      ss = new SwitchStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "dan")), new SwitchCase[] {l1, l2, l3});
      assertEquals("Should return null", null, ss.visit(_bbtc));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "You cannot have two switch cases with the same label 5", errors.getLast().getFirst());
      
      
      DefaultCase dc1 = new DefaultCase(SourceInfo.NO_INFO, emptyBody);
      ss = new SwitchStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "dan")), new SwitchCase[] {dc1, dc1});
      assertEquals("Should return null", null, ss.visit(_bbtc));
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", "A switch statement can only have one default case", errors.getLast().getFirst());

      
      VariableData xData = new VariableData("x", _publicMav, SymbolData.INT_TYPE, false, _bbtc._bodyData);
      _bbtc._vars.addLast(xData);

      ExpressionStatement assignX = new ExpressionStatement(SourceInfo.NO_INFO, new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "x")), new IntegerLiteral(SourceInfo.NO_INFO, 5)));
      UnbracedBody returnBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignX, new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5))});
      UnbracedBody breakBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignX, new UnlabeledBreakStatement(SourceInfo.NO_INFO)});
      UnbracedBody breakNoAssignBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new UnlabeledBreakStatement(SourceInfo.NO_INFO)});
      UnbracedBody fallThroughBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[] {assignX});
      UnbracedBody fallThroughNoAssignBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      
      SwitchCase c1 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5), returnBody);
      SwitchCase c2 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 6), breakBody);
      SwitchCase c3 = new DefaultCase(SourceInfo.NO_INFO, breakBody);
      
      ss = new SwitchStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "dan")), new SwitchCase[] {c1, c2, c3});
      
      assertEquals("Should return null", null, ss.visit(_bbtc));
      assertEquals("Should still be 4 errors", 4, errors.size());
      assertTrue("x has been assigned", xData.hasValue());
      
      
      xData.lostValue();
      
      c1 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5), fallThroughNoAssignBody);
      c2 = new DefaultCase(SourceInfo.NO_INFO, breakBody);
      c3 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 6), breakBody);
      ss = new SwitchStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "dan")), new SwitchCase[] {c1, c2, c3});
     
      assertEquals("Should return null", null, ss.visit(_bbtc));
      assertEquals("Should still be 4 errors", 4, errors.size());
      assertTrue("x has been assigned", xData.hasValue());
      
      
      xData.lostValue();
      
      c1 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5), fallThroughNoAssignBody);
      c2 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 6), breakNoAssignBody);
      c3 = new DefaultCase(SourceInfo.NO_INFO, breakBody);
      ss = new SwitchStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "dan")), new SwitchCase[] {c1, c2, c3});
     
      assertEquals("Should return null", null, ss.visit(_bbtc));
      assertEquals("Should still be 4 errors", 4, errors.size());
      assertFalse("x has not been assigned", xData.hasValue());
      
      
      xData.lostValue();
      
      c1 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5), fallThroughNoAssignBody);
      c2 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 6), fallThroughNoAssignBody);
      c3 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 7), breakBody);
      ss = new SwitchStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "dan")), new SwitchCase[] {c1, c2, c3});
     
      assertEquals("Should return null", null, ss.visit(_bbtc));
      assertEquals("Should still be 4 errors", 4, errors.size());
      assertFalse("x has not been assigned", xData.hasValue());
      
      
      xData.lostValue();
      
      c1 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5), fallThroughNoAssignBody);
      c2 = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 6), fallThroughNoAssignBody);
      c3 = new DefaultCase(SourceInfo.NO_INFO, fallThroughBody);
      ss = new SwitchStatement(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "dan")), new SwitchCase[] {c1, c2, c3});
     
      assertEquals("Should return null", null, ss.visit(_bbtc));
      assertEquals("Should be 5 errors", 5, errors.size());
      assertEquals("The error message should be correct", "You must end a non-empty switch case with a break or return statement at the Advanced level", errors.getLast().getFirst());
      assertTrue("x has been assigned", xData.hasValue());
      
    }
    
    public void testForLabeledCase() {
      symbolTable.put("java.lang.String", new SymbolData("java.lang.String"));
      UnbracedBody emptyBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      
      LabeledCase lc = new LabeledCase(SourceInfo.NO_INFO, new CharLiteral(SourceInfo.NO_INFO, 'e'), emptyBody);
      assertEquals("Should return null", null, lc.visit(_bbtc));
      assertEquals("There should be no errors", 0, errors.size());

      lc = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 27), emptyBody);
      assertEquals("Should return null", null, lc.visit(_bbtc));
      assertEquals("There should be no errors", 0, errors.size());
      
      
      UnbracedBody nonEmptyBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5))});
      lc = new LabeledCase(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 27), nonEmptyBody);
      TypeData result = lc.visit(_bbtc);
      assertEquals("There should be no errors", 0, errors.size());
      assertEquals("Should return int", SymbolData.INT_TYPE.getInstanceData(), result);
      assertEquals("There should be no errors", 0, errors.size());
      
      
      
      
      
      lc = new LabeledCase(SourceInfo.NO_INFO, new PlusExpression(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5), new IntegerLiteral(SourceInfo.NO_INFO, 42)), emptyBody);
      assertEquals("Should return null", null, lc.visit(_bbtc));
      assertEquals("There should be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "The labels of a switch statement must be constants.  You are using a more complicated expression of type int", errors.getLast().getFirst());

      
      _bbtc._vars.addLast(new VariableData("dan", _publicMav, SymbolData.INT_TYPE, true, _bbtc._bodyData));
      lc = new LabeledCase(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "dan")), emptyBody);
      assertEquals("Should return null", null, lc.visit(_bbtc));
      assertEquals("There should now be 2 errors", 2, errors.size());
      assertEquals("The error message should be correct", "The labels of a switch statement must be constants.  You are using a more complicated expression of type int", errors.getLast().getFirst());
                         
      
      lc = new LabeledCase(SourceInfo.NO_INFO, new StringLiteral(SourceInfo.NO_INFO, "hi!"), emptyBody);
      assertEquals("Should return null", null, lc.visit(_bbtc));
      assertEquals("There should now be 3 errors", 3, errors.size());
      assertEquals("The error message should be correct", "The labels of a switch statement must be constants of int or char type.  You specified a constant of type java.lang.String", errors.getLast().getFirst());

    }
    
    public void testForDefaultCase() {
      UnbracedBody emptyBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      UnbracedBody returnBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5))});
      UnbracedBody breakBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new UnlabeledBreakStatement(SourceInfo.NO_INFO)});
      
      
      DefaultCase dc = new DefaultCase(SourceInfo.NO_INFO, emptyBody);
      assertEquals("Should return null", null, dc.visit(_bbtc));
      assertEquals("There should be no errors", 0, errors.size());

      
      dc = new DefaultCase(SourceInfo.NO_INFO, returnBody);
      assertEquals("Should return int", SymbolData.INT_TYPE.getInstanceData(), dc.visit(_bbtc));
      assertEquals("There should be no errors", 0, errors.size());
       
      
      dc = new DefaultCase(SourceInfo.NO_INFO, breakBody);
      assertEquals("Should return NOT_FOUND", SymbolData.NOT_FOUND, dc.visit(_bbtc));
      assertEquals("There should be no errors", 0, errors.size());
    }
    
    public void testForSwitchCase() {
      UnbracedBody emptyBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      UnbracedBody returnBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5))});
      UnbracedBody breakBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new UnlabeledBreakStatement(SourceInfo.NO_INFO)});
      UnbracedBody nonEmptyBody = new UnbracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new EmptyStatement(SourceInfo.NO_INFO)});
     
      
      DefaultCase dc = new DefaultCase(SourceInfo.NO_INFO, emptyBody);
      assertEquals("Should return null", null, _bbtc.forSwitchCase(dc));
      assertEquals("There should be no errors", 0, errors.size());

      
      dc = new DefaultCase(SourceInfo.NO_INFO, returnBody);
      assertEquals("Should return int", SymbolData.INT_TYPE.getInstanceData(), _bbtc.forSwitchCase(dc));
      assertEquals("There should be no errors", 0, errors.size());
       
      
      dc = new DefaultCase(SourceInfo.NO_INFO, breakBody);
      assertEquals("Should return NOT_FOUND", SymbolData.NOT_FOUND, _bbtc.forSwitchCase(dc));
      assertEquals("There should be no errors", 0, errors.size());
      
      
      dc = new DefaultCase(SourceInfo.NO_INFO, nonEmptyBody);
      assertEquals("Should return null", null, _bbtc.forSwitchCase(dc));
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "You must end a non-empty switch case with a break or return statement at the Advanced level", errors.getLast().getFirst());

    }

    public void testCreateANewInstanceOfMe() {
      
      BodyTypeChecker btc = _bbtc.createANewInstanceOfMe(_bbtc._bodyData, _bbtc._file, _bbtc._package, _bbtc._importedFiles, _bbtc._importedPackages, _bbtc._vars, _bbtc._thrown);
      assertTrue("Should be an instance of BodyTypeChecker", btc instanceof BodyTypeChecker);
      assertFalse("Should not be an instance of ConstructorBodyTypeChecker", btc instanceof ConstructorBodyTypeChecker);

    }
    
    public void testCheckDuplicateExceptions() {
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      Block b = new Block(SourceInfo.NO_INFO, emptyBody);

      NormalTryCatchStatement ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[0]);
      TryCatchFinallyStatement tcfs = new TryCatchFinallyStatement(SourceInfo.NO_INFO, b, new CatchBlock[0], b);
      _bbtc.checkDuplicateExceptions(ntcs);
      _bbtc.checkDuplicateExceptions(tcfs);
      assertEquals("Should be no errors", 0, errors.size());
      
      UninitializedVariableDeclarator uvd1 = 
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.lang.Exception", new Type[0]), 
                                            new Word(SourceInfo.NO_INFO, "e"));
      UninitializedVariableDeclarator uvd2 = 
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new ClassOrInterfaceType(SourceInfo.NO_INFO, "RuntimeException", new Type[0]), 
                                            new Word(SourceInfo.NO_INFO, "e"));
      UninitializedVariableDeclarator uvd3 =
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new ClassOrInterfaceType(SourceInfo.NO_INFO, "IOException", new Type[0]), 
                                            new Word(SourceInfo.NO_INFO, "e"));

      FormalParameter fp1 = new FormalParameter(SourceInfo.NO_INFO, uvd1, false);
      FormalParameter fp2 = new FormalParameter(SourceInfo.NO_INFO, uvd2, false);
      FormalParameter fp3 = new FormalParameter(SourceInfo.NO_INFO, uvd3, false);
      
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(new File(""), 
                                 "", 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      
      llv.errors = new LinkedList<Pair<String, JExpressionIF>>();
      llv._errorAdded=false;

      llv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      llv.visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      llv._hierarchy = new Hashtable<String, TypeDefBase>();
      llv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();

      llv._importedFiles.addLast("java.io.IOException");
      SymbolData e = llv.getSymbolData("java.lang.Exception", SourceInfo.NO_INFO, true);
      SymbolData re = llv.getSymbolData("java.lang.RuntimeException", SourceInfo.NO_INFO, true);
      SymbolData ioe = llv.getSymbolData("java.io.IOException", SourceInfo.NO_INFO, true);
      
      symbolTable.put("java.lang.Exception", e);
      symbolTable.put("java.lang.RuntimeException", re);
      symbolTable.put("java.io.IOException", ioe);
      

      CatchBlock c1 = new CatchBlock(SourceInfo.NO_INFO, fp1, b);
      CatchBlock c2 = new CatchBlock(SourceInfo.NO_INFO, fp2, b);
      CatchBlock c3 = new CatchBlock(SourceInfo.NO_INFO, fp3, b);
      _bbtc._importedFiles.addLast("java.io.IOException");
      
      
      ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[] {c1});
      _bbtc.checkDuplicateExceptions(ntcs);
      assertEquals("Should be no errors", 0, errors.size());
      
      
      ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[]{c1, c2});
      _bbtc.checkDuplicateExceptions(ntcs);

      assertEquals("Should be one error", 1, errors.size());
      assertEquals("Error message should be correct", "Exception java.lang.RuntimeException has already been caught", errors.get(0).getFirst());

      
      ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[]{c2, c3});
      _bbtc.checkDuplicateExceptions(ntcs);
      assertEquals("Should still be one error", 1, errors.size());
      
      
      ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[]{c1, c2, c3});
      _bbtc.checkDuplicateExceptions(ntcs);

      assertEquals("Should be two errors", 2, errors.size());
      assertEquals("2nd Error message should be correct", "Exception java.lang.RuntimeException has already been caught", errors.get(0).getFirst());
      assertEquals("3rd Error message should be correct", "Exception java.io.IOException has already been caught", errors.get(1).getFirst());
      
      
      ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[]{c2, c1});
      _bbtc.checkDuplicateExceptions(ntcs);
      assertEquals("Should still be two errors", 2, errors.size());
    }
    
    public void testTryCatchLeastRestrictiveType() {

      InstanceData[] sdArray = new InstanceData[] {SymbolData.BYTE_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData(), SymbolData.SHORT_TYPE.getInstanceData()};
      assertEquals("Should return long type", SymbolData.LONG_TYPE.getInstanceData(), _bbtc.tryCatchLeastRestrictiveType(SymbolData.LONG_TYPE.getInstanceData(), sdArray, null));
      assertEquals("Should return Object", "java.lang.Object", _bbtc.tryCatchLeastRestrictiveType(SymbolData.SHORT_TYPE.getInstanceData(), sdArray, SymbolData.BOOLEAN_TYPE.getInstanceData()).getName());
      assertEquals("Should return double type", SymbolData.DOUBLE_TYPE.getInstanceData(), _bbtc.tryCatchLeastRestrictiveType(SymbolData.SHORT_TYPE.getInstanceData(), sdArray, SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("Should return null", null, _bbtc.tryCatchLeastRestrictiveType(null, sdArray, null));
      assertEquals("Should return int type", SymbolData.INT_TYPE.getInstanceData(), _bbtc.tryCatchLeastRestrictiveType(SymbolData.SHORT_TYPE.getInstanceData(), sdArray, null));
      assertEquals("Should return long type", SymbolData.LONG_TYPE.getInstanceData(), _bbtc.tryCatchLeastRestrictiveType(null, sdArray, SymbolData.LONG_TYPE.getInstanceData()));
      
      sdArray = new InstanceData[] {null, SymbolData.INT_TYPE.getInstanceData()};
      assertEquals("Should return null", null, _bbtc.tryCatchLeastRestrictiveType(SymbolData.INT_TYPE.getInstanceData(), sdArray, null));
      assertEquals("Should return short", SymbolData.SHORT_TYPE.getInstanceData(), _bbtc.tryCatchLeastRestrictiveType(SymbolData.INT_TYPE.getInstanceData(), sdArray, SymbolData.SHORT_TYPE.getInstanceData()));
      
      SymbolData sd = new SymbolData("java.lang.Object");
      SymbolData sd2 = new SymbolData("java.lang.String");
      sd.setIsContinuation(false);
      sd2.setIsContinuation(false);
      symbolTable.put("java.lang.Object", sd);
      symbolTable.put("java.lang.String", sd2);
      sd2.setSuperClass(sd);
      
      assertEquals("Should return Object", sd.getInstanceData(), _bbtc.tryCatchLeastRestrictiveType(sd2.getInstanceData(), new InstanceData[]{sd.getInstanceData(), sd2.getInstanceData()}, null));
    }
    
    public void testHandleMethodInvocation() {
      
      MethodData md = new MethodData("Fun", _publicMav, new TypeParameter[0], _sd1, 
                                     new VariableData[0], 
                                     new String[0], 
                                     _sd1,
                                     null);
      
      MethodData md2 = new MethodData("InTheSun", _publicMav, new TypeParameter[0], _sd1,
                                      new VariableData[0],
                                      new String[] {"java.lang.RuntimeException", "java.io.IOException"},
                                      _sd1,
                                      null);
                                      
      NullLiteral nl = new NullLiteral(SourceInfo.NO_INFO);

      _bbtc._importedFiles.addLast("java.io.IOException");
      
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(new File(""), "", new LinkedList<String>(), new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      
      llv.errors = new LinkedList<Pair<String, JExpressionIF>>();
      llv._errorAdded = false;

      llv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      llv.visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      llv._hierarchy = new Hashtable<String, TypeDefBase>();
      llv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();

      llv._importedFiles.addLast("java.io.IOException");


      SymbolData re = llv.getSymbolData("java.lang.RuntimeException", SourceInfo.NO_INFO, true);
      SymbolData ioe = llv.getSymbolData("java.io.IOException", SourceInfo.NO_INFO, true);
      
      symbolTable.put("java.lang.RuntimeException", re);
      symbolTable.put("java.io.IOException", ioe);

      
      _bbtc.handleMethodInvocation(md, nl);
      assertEquals("There should be no exceptions in _thrown", 0, _bbtc._thrown.size());
      
      _bbtc.handleMethodInvocation(md2, nl);
      assertEquals("There should be 2 exceptions in _thrown", 2, _bbtc._thrown.size());
      assertEquals("The first exception should be java.lang.RuntimeException", re, _bbtc._thrown.get(0).getFirst());
      assertEquals("The second exception should be java.lang.IOException", ioe, _bbtc._thrown.get(1).getFirst());
    }
    
    public void testForThrowStatement() {
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(new File(""), 
                                 "", 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      
      llv.errors = new LinkedList<Pair<String, JExpressionIF>>();
      llv._errorAdded=false;

      llv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      llv.visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      llv._hierarchy = new Hashtable<String, TypeDefBase>();
      llv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();

      SymbolData re = llv.getSymbolData("java.lang.RuntimeException", SourceInfo.NO_INFO, true);
      symbolTable.put("java.lang.RuntimeException", re);
      
      VariableData vd = new VariableData("myException", _publicMav, re, true, _bbtc._bodyData);
      _bbtc._vars.addLast(vd);
      Expression e = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myException"));
      ThrowStatement ts = new ThrowStatement(SourceInfo.NO_INFO, e);
      
      assertEquals("Should return EXCEPTION", SymbolData.EXCEPTION.getInstanceData(), ts.visit(_bbtc));
      
      assertEquals("There should be 1 exception in _thrown", 1, _bbtc._thrown.size());
      assertEquals("The exception should be java.lang.RuntimeException", re, _bbtc._thrown.get(0).getFirst());

    }
    
    public void testMakeSureCaughtStuffWasThrown() {
      
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      Block b = new Block(SourceInfo.NO_INFO, emptyBody);

      PrimitiveType intt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "i"));
      FormalParameter param = new FormalParameter(SourceInfo.NO_INFO, new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "j")), false);

      NormalTryCatchStatement ntcs =
        new NormalTryCatchStatement(SourceInfo.NO_INFO, b,
                                    new CatchBlock[] {new CatchBlock(SourceInfo.NO_INFO,  param, b)});
      SymbolData javaLangThrowable =  _bbtc.getSymbolData("java.lang.Throwable", ntcs, false, true); 
                                     
      _bbtc.symbolTable.put("java.lang.Throwable", javaLangThrowable);
      SymbolData exception = new SymbolData("my.crazy.exception");
      exception.setSuperClass(javaLangThrowable);
      SymbolData exception2 = new SymbolData("A&M.beat.Rice.in.BaseballException");
      exception2.setSuperClass(javaLangThrowable);
      SymbolData exception3 = new SymbolData("aegilha");
      exception3.setSuperClass(javaLangThrowable);
      LinkedList<Pair<SymbolData, JExpression>> thrown = new LinkedList<Pair<SymbolData, JExpression>>();

      
      _bbtc.makeSureCaughtStuffWasThrown(ntcs, new SymbolData[0], thrown);
      assertEquals("There should be no errors", 0, errors.size());
      
      Pair<SymbolData, JExpression> p = new Pair<SymbolData, JExpression>(exception, ntcs);
      thrown.addLast(p);
      _bbtc.makeSureCaughtStuffWasThrown(ntcs, new SymbolData[]{exception}, thrown);
      assertEquals("There should still be no errors", 0, errors.size());
      
      thrown.remove(p);
      
      _bbtc.makeSureCaughtStuffWasThrown(ntcs, new SymbolData[] {exception2}, thrown);


      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "The exception A&M.beat.Rice.in.BaseballException is never thrown in the body of the corresponding try block", errors.get(0).getFirst());
    }
    
    public void testIsCheckedException() {
      SymbolData th = new SymbolData("java.lang.Throwable");
      th.setIsContinuation(false);
      SymbolData r = new SymbolData("java.lang.Error");
      r.setIsContinuation(false);
      r.setSuperClass(th);
      SymbolData ex = new SymbolData("java.lang.Exception");
      ex.setIsContinuation(false);
      ex.setSuperClass(th);
      SymbolData re = new SymbolData("java.lang.RuntimeException");
      re.setIsContinuation(false);
      re.setSuperClass(ex);
      symbolTable.put("java.lang.Throwable", th);
      symbolTable.put("java.lang.RuntimeException", re);
      symbolTable.put("java.lang.Error", r);
      symbolTable.put("java.lang.Exception", ex);
      SymbolData e1 = new SymbolData("exception1");
      e1.setSuperClass(ex);
      SymbolData e2 = new SymbolData("exception2");
      e2.setSuperClass(re);
      SymbolData e3 = new SymbolData("exception3");
      e3.setSuperClass(r);
      
      NullLiteral nl = new NullLiteral(SourceInfo.NO_INFO);
      
      assertTrue("Does not subclass RuntimeException or Error", _bbtc.isCheckedException(e1, nl));
      assertFalse("Subclasses java.lang.RuntimeException", _bbtc.isCheckedException(e2, nl));
      assertFalse("Subclasses java.lang.Error", _bbtc.isCheckedException(e3, nl));
      
    }
    
    public void testIsUncheckedException() {
      
      SymbolData th = new SymbolData("java.lang.Throwable");
      th.setIsContinuation(false);
      SymbolData r = new SymbolData("java.lang.Error");
      r.setIsContinuation(false);
      r.setSuperClass(th);
      SymbolData ex = new SymbolData("java.lang.Exception");
      ex.setIsContinuation(false);
      ex.setSuperClass(th);
      SymbolData re = new SymbolData("java.lang.RuntimeException");
      re.setIsContinuation(false);
      re.setSuperClass(ex);
      symbolTable.put("java.lang.Throwable", th);
      symbolTable.put("java.lang.RuntimeException", re);
      symbolTable.put("java.lang.Error", r);
      symbolTable.put("java.lang.Exception", ex);

      SymbolData e1 = new SymbolData("exception1");
      e1.setIsContinuation(false);
      e1.setSuperClass(ex);
      symbolTable.put("exception1", e1);
      SymbolData e2 = new SymbolData("exception2");
      e2.setSuperClass(re);
      SymbolData e3 = new SymbolData("exception3");
      e3.setSuperClass(r);
      SymbolData e4 = new SymbolData("exception4");
      e4.setSuperClass(e1);
      
      NullLiteral nl = new NullLiteral(SourceInfo.NO_INFO);
      
      assertTrue("Does not subclass RuntimeException or Error or anything in the method data", _bbtc.isUncaughtCheckedException(e1, nl));
      assertFalse("Subclasses java.lang.RuntimeException", _bbtc.isUncaughtCheckedException(e2, nl));
      assertFalse("Subclasses java.lang.Error", _bbtc.isUncaughtCheckedException(e3, nl));
      
      
      _bbtc._bodyData.getMethodData().setThrown(new String[] {"exception1"});
      assertFalse("Is in method data", _bbtc.isUncaughtCheckedException(e1, nl));
      
      assertFalse("Superclass is in method data", _bbtc.isUncaughtCheckedException(e4, nl));
      
      
    }
    
    public void testHandleUncheckedException() {
      JExpression j = new SimpleMethodInvocation(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myMethod"), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i"))}));
      
      _bbtc.handleUncheckedException(new SymbolData("i.have.a.shoe"), j);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "The method myMethod is declared to throw the exception i.have.a.shoe which needs to be caught or declared to be thrown", errors.get(0).getFirst()); 
      Expression e = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myException"));
      j = new ThrowStatement(SourceInfo.NO_INFO, e);
      _bbtc.handleUncheckedException(new SymbolData("you.have.a.pot"), j);
      assertEquals("There should be two errors", 2, errors.size());
      assertEquals("The error message should be correct", "This statement throws the exception you.have.a.pot which needs to be caught or declared to be thrown", errors.get(1).getFirst());

    }
    
    public void testCompareThrownAndCaught() {
      JExpression j = new SimpleMethodInvocation(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myMethod"), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i"))}));
      
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      Block b = new Block(SourceInfo.NO_INFO, emptyBody);

      PrimitiveType intt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "i"));
      FormalParameter param = new FormalParameter(SourceInfo.NO_INFO, new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "j")), false);

      NormalTryCatchStatement ntcs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[] {new CatchBlock(SourceInfo.NO_INFO,  param, b)});

      SymbolData javaLangThrowable =  _bbtc.getSymbolData("java.lang.Throwable", ntcs, false, true); 
      _bbtc.symbolTable.put("java.lang.Throwable", javaLangThrowable);
      SymbolData exception = new SymbolData("my.crazy.exception");
      exception.setSuperClass(javaLangThrowable);
      SymbolData exception2 = new SymbolData("A&M.beat.Rice.in.BaseballException");
      exception2.setSuperClass(javaLangThrowable);
      SymbolData exception3 = new SymbolData("aegilha");
      exception3.setSuperClass(exception2);
      SymbolData[] caught_array = new SymbolData[] { exception, exception2 };
      LinkedList<Pair<SymbolData, JExpression>> thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      thrown.addLast(new Pair<SymbolData, JExpression>(exception, j));
      thrown.addLast(new Pair<SymbolData, JExpression>(exception2, ntcs));
      thrown.addLast(new Pair<SymbolData, JExpression>(exception3, ntcs));
      
      _bbtc.compareThrownAndCaught(ntcs, caught_array, thrown);
      assertEquals("There should be no errors", 0, errors.size());
      
      _bbtc.compareThrownAndCaught(ntcs, new SymbolData[] {exception2}, thrown);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "The method myMethod is declared to throw the exception my.crazy.exception which needs to be caught or declared to be thrown", errors.get(0).getFirst());

    }
    
    public void testForBracedBody() {
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(new File(""), 
                                 "", 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      llv.errors = new LinkedList<Pair<String, JExpressionIF>>();
      llv._errorAdded=false;

      llv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      llv.visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      llv._hierarchy = new Hashtable<String, TypeDefBase>();
      llv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();

      SymbolData eb = llv.getSymbolData("java.util.prefs.BackingStoreException", SourceInfo.NO_INFO, true);
      SymbolData re = llv.getSymbolData("java.lang.RuntimeException", SourceInfo.NO_INFO, true);


      
      
      BracedBody plainBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new UnlabeledBreakStatement(SourceInfo.NO_INFO)});
      plainBody.visit(_bbtc);
      assertEquals("There should be no errors", 0, errors.size());

      
      BracedBody runtimeBB = new BracedBody(SourceInfo.NO_INFO, 
                                     new BodyItemI[] { 
        new ThrowStatement(SourceInfo.NO_INFO, 
                           new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, 
                                         new ClassOrInterfaceType(SourceInfo.NO_INFO, 
                                                                 "java.lang.RuntimeException", 
                                                                 new Type[0]), 
                                                             new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0])))});
      runtimeBB.visit(_bbtc);
      assertEquals("There should be no errors", 0, errors.size());
      
      
      
      BracedBody bb = new BracedBody(SourceInfo.NO_INFO, 
                                     new BodyItemI[] { 
        new ThrowStatement(SourceInfo.NO_INFO, 
        new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, 
                                         new ClassOrInterfaceType(SourceInfo.NO_INFO, 
                                                                 "java.util.prefs.BackingStoreException", 
                                                                 new Type[0]), 
                                          new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new StringLiteral(SourceInfo.NO_INFO, "wee")})))});

      _bbtc._bodyData.getMethodData().setThrown(new String[]{"java.util.prefs.BackingStoreException"});
      _bbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();

      bb.visit(_bbtc);
      assertEquals("There should still be no errors", 0, errors.size());
      
      
      _bbtc._bodyData.getMethodData().setThrown(new String[0]);
      _bbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      
      bb.visit(_bbtc);

      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "This statement throws the exception java.util.prefs.BackingStoreException which needs to be caught or declared to be thrown", errors.get(0).getFirst());
      
      
      MethodData badMethod = new MethodData("throwsException", 
                                            _packageMav, 
                                            new TypeParameter[0], 
                                            SymbolData.INT_TYPE, 
                                            new VariableData[0],
                                            new String[] {"java.util.prefs.BackingStoreException"},
                                            _sd1,
                                            null);
      _bbtc._bodyData.getSymbolData().addMethod(badMethod);                                      
      _bbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      BracedBody bbMethod = 
        new BracedBody(SourceInfo.NO_INFO, 
                       new BodyItemI[] { 
        new ExpressionStatement(SourceInfo.NO_INFO, 
                                new SimpleMethodInvocation(SourceInfo.NO_INFO, 
                                                           new Word(SourceInfo.NO_INFO, "throwsException"), 
                                                           new ParenthesizedExpressionList(SourceInfo.NO_INFO, 
                                                                                           new Expression[0])))});
      bbMethod.visit(_bbtc);
      assertEquals("There should be two errors", 2, errors.size());
      assertEquals("The error message should be correct", 
                   "The method throwsException is declared to throw the exception java.util.prefs.BackingStoreException" + 
                   " which needs to be caught or declared to be thrown", errors.getLast().getFirst());
      
      
      _bbtc._bodyData.getMethodData().setThrown(new String[] {"java.util.prefs.BackingStoreException"});
      bbMethod.visit(_bbtc);
      assertEquals("There should still be two errors", 2, errors.size());
      

      
      _bbtc._bodyData.getMethodData().setThrown(new String[0]);
      _sd3.setMav(_publicMav);
      _sd3.setIsContinuation(false);
      _bbtc.symbolTable.put(_sd3.getName(), _sd3);
      MethodData constructor = new MethodData("zebra", _publicMav, new TypeParameter[0], _sd3, new VariableData[0], new String[] {"java.util.prefs.BackingStoreException"}, _sd3, null);
      _sd3.addMethod(constructor);
      BracedBody bbConstr = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[]{new ExpressionStatement(SourceInfo.NO_INFO, new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, new ClassOrInterfaceType(SourceInfo.NO_INFO, _sd3.getName(), new Type[0]), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0])))});
      _bbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      bbConstr.visit(_bbtc);
      assertEquals("There should be three errors", 3, errors.size());
      assertEquals("The error message should be correct", "The constructor for the class zebra is declared to throw the exception java.util.prefs.BackingStoreException which needs to be caught or declared to be thrown.", errors.getLast().getFirst());
      

      
      _bbtc._bodyData.getMethodData().setThrown(new String[] {"java.util.prefs.BackingStoreException"});
      bbConstr.visit(_bbtc);
      assertEquals("There should still be three errors", 3, errors.size());

      
    }
    
    public void testForTryCatchFinallyStatement() {
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(new File(""), 
                                 "", 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      llv.errors = new LinkedList<Pair<String, JExpressionIF>>();
      llv._errorAdded = false;


      llv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      llv.visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      llv._hierarchy = new Hashtable<String, TypeDefBase>();
      llv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();

      SymbolData eb = llv.getSymbolData("java.util.prefs.BackingStoreException", SourceInfo.NO_INFO, true);
      SymbolData re = llv.getSymbolData("java.lang.RuntimeException", SourceInfo.NO_INFO, true);
      
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      BracedBody bb = new BracedBody(SourceInfo.NO_INFO, 
                                     new BodyItemI[] { 
        new ThrowStatement(SourceInfo.NO_INFO, 
        new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, 
                                         new ClassOrInterfaceType(SourceInfo.NO_INFO, 
                                                                 "java.util.prefs.BackingStoreException", 
                                                                 new Type[0]), 
                                          new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[]{new StringLiteral(SourceInfo.NO_INFO, "arg")})))});
      
      Block b = new Block(SourceInfo.NO_INFO, bb);
      Block b2 = new Block(SourceInfo.NO_INFO, emptyBody);
    
      _bbtc._bodyData.getMethodData().setThrown(new String[0]);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));


      
      
      TryCatchFinallyStatement tcfs = new TryCatchFinallyStatement(SourceInfo.NO_INFO, b, new CatchBlock[0], b2);
      tcfs.visit(_bbtc);
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "This statement throws the exception java.util.prefs.BackingStoreException which needs to be caught or declared to be thrown", errors.get(0).getFirst());
            
      
      IfThenElseStatement ites1 = new IfThenElseStatement(SourceInfo.NO_INFO,
                                                          new BooleanLiteral(SourceInfo.NO_INFO, true),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 4)),
                                                          new EmptyStatement(SourceInfo.NO_INFO));
                                                          
      BracedBody bb2 = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {ites1});
      TryCatchFinallyStatement tcfs2 = new TryCatchFinallyStatement(SourceInfo.NO_INFO, b, new CatchBlock[0], new Block(SourceInfo.NO_INFO, bb2));
      _bbtc._bodyData.removeAllBlocks();
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.resetBlockIterator();
      
      TypeData result = tcfs2.visit(_bbtc);  
      assertEquals("Should return Exception", SymbolData.EXCEPTION.getInstanceData(), result);
      assertEquals("Should still be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", 
                   "This statement throws the exception java.util.prefs.BackingStoreException " +
                   "which needs to be caught or declared to be thrown", 
                   errors.get(0).getFirst());
                                      
      
      IfThenElseStatement ites2 = new IfThenElseStatement(SourceInfo.NO_INFO,
                                                          new BooleanLiteral(SourceInfo.NO_INFO, false),
                                                          new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 4)),
                                                          new UnlabeledBreakStatement(SourceInfo.NO_INFO));
                                      
      BracedBody bb3 = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {ites2});
      TryCatchFinallyStatement tcfs3 = new TryCatchFinallyStatement(SourceInfo.NO_INFO, b, new CatchBlock[0], new Block(SourceInfo.NO_INFO, bb3));

      _bbtc._bodyData.removeAllBlocks();
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.resetBlockIterator();

      assertEquals("Should return Exception", SymbolData.EXCEPTION.getInstanceData(), tcfs3.visit(_bbtc));
      assertEquals("Should still be 1 error", 1, errors.size());
      

      
      _bbtc._bodyData.getMethodData().setReturnType(SymbolData.VOID_TYPE);
      IfThenElseStatement ites3 = new IfThenElseStatement(SourceInfo.NO_INFO,
                                                          new BooleanLiteral(SourceInfo.NO_INFO, true),
                                                          new VoidReturnStatement(SourceInfo.NO_INFO),
                                                          new VoidReturnStatement(SourceInfo.NO_INFO));
                                                          
      BracedBody bb4 = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {ites3});
      TryCatchFinallyStatement tcfs4 = new TryCatchFinallyStatement(SourceInfo.NO_INFO, b, new CatchBlock[0], new Block(SourceInfo.NO_INFO, bb4));
      _bbtc._bodyData.removeAllBlocks();
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.resetBlockIterator();
      
      result = tcfs4.visit(_bbtc);
      assertEquals("Should return SymbolData.VOID_TYPE", SymbolData.VOID_TYPE.getInstanceData(), result);
      
      assertEquals("Should still still be 1 error", 1, errors.size());

      _bbtc._bodyData.getMethodData().setReturnType(SymbolData.INT_TYPE);


      
      
      
      
      
      
      TryCatchFinallyStatement inner = new TryCatchFinallyStatement(SourceInfo.NO_INFO, b, new CatchBlock[0], b2);
      TryCatchFinallyStatement nested = new TryCatchFinallyStatement(SourceInfo.NO_INFO, 
                                             new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {inner})), 
                                             new CatchBlock[0], b2);
                                             
      BlockData innerBD = new BlockData(_bbtc._bodyData);
      innerBD.addBlock(new BlockData(innerBD));
      innerBD.addBlock(new BlockData(innerBD));

      _bbtc._bodyData.removeAllBlocks();
      _bbtc._bodyData.addBlock(innerBD);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      _bbtc._bodyData.resetBlockIterator();
      
      nested.visit(_bbtc);  
      assertEquals("There should still be 1 errors", 1, errors.size());
      assertEquals("Error message should be correct", "This statement throws the exception java.util.prefs.BackingStoreException " +
                   "which needs to be caught or declared to be thrown", errors.get(0).getFirst());
                                      
      
      UninitializedVariableDeclarator uvd1 = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.util.prefs.BackingStoreException", new Type[0]), new Word(SourceInfo.NO_INFO, "e"));
      FormalParameter fp1 = new FormalParameter(SourceInfo.NO_INFO, uvd1, false);
      BlockData catchBD = new BlockData(_bbtc._bodyData);
      VariableData fpData = new VariableData("e", null, eb, true, catchBD);
      catchBD.addVar(fpData);

      CatchBlock cb = new CatchBlock(SourceInfo.NO_INFO, fp1, b2);
      TryCatchFinallyStatement nested2 = new TryCatchFinallyStatement(SourceInfo.NO_INFO, new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {inner})), new CatchBlock[] {cb}, b2);
      _bbtc._bodyData.removeAllBlocks();
      innerBD.resetBlockIterator();
      _bbtc._bodyData.addBlock(innerBD);
      _bbtc._bodyData.addBlock(catchBD);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.resetBlockIterator();

      nested2.visit(_bbtc);
      assertEquals("There should still be 1 error", 1, errors.size());
      
      
      BracedBody reb = new BracedBody(SourceInfo.NO_INFO, 
                                     new BodyItemI[] { 
        new ThrowStatement(SourceInfo.NO_INFO, 
        new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, 
                                         new ClassOrInterfaceType(SourceInfo.NO_INFO, 
                                                                 "java.lang.RuntimeException", 
                                                                 new Type[0]), 
                                         new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0])))});
      
      
      
      
      
      
      
      
      
    
      TryCatchFinallyStatement inner3 = new TryCatchFinallyStatement(SourceInfo.NO_INFO, 
                                                                     new Block(SourceInfo.NO_INFO, reb), new CatchBlock[0], b2);
      TryCatchFinallyStatement nested3 = new TryCatchFinallyStatement(SourceInfo.NO_INFO, 
                                                                      new Block(SourceInfo.NO_INFO, 
                                                                                new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {inner3})), 
                                                                      new CatchBlock[0], b2);
                                                                                
      _bbtc._bodyData.removeAllBlocks();
      innerBD.resetBlockIterator();
      _bbtc._bodyData.addBlock(innerBD);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.resetBlockIterator();

      nested3.visit(_bbtc);
      assertEquals("There should still be 1 errors", 1, errors.size());
      
      
      _bbtc._bodyData.getMethodData().setThrown(new String[]{"java.util.prefs.BackingStoreException"});
      innerBD.resetBlockIterator();
      _bbtc._bodyData.resetBlockIterator();
      nested.visit(_bbtc);
      assertEquals("There should still be 1 error!", 1, errors.size());
    }

    public void testForNormalTryCatchStatement() {
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(new File(""), 
                                 "", 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      llv.errors = new LinkedList<Pair<String, JExpressionIF>>();
      llv._errorAdded=false;


      llv.continuations = new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>();
      llv.visitedFiles = new LinkedList<Pair<LanguageLevelVisitor, edu.rice.cs.javalanglevels.tree.SourceFile>>();      
      llv._hierarchy = new Hashtable<String, TypeDefBase>();
      llv._classesToBeParsed = new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>();

      SymbolData eb = llv.getSymbolData("java.util.prefs.BackingStoreException", SourceInfo.NO_INFO, true);
      SymbolData re = llv.getSymbolData("java.lang.RuntimeException", SourceInfo.NO_INFO, true);
      
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      BracedBody bb = new BracedBody(SourceInfo.NO_INFO, 
                                     new BodyItemI[] { 
        new ThrowStatement(SourceInfo.NO_INFO, 
        new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, 
                                         new ClassOrInterfaceType(SourceInfo.NO_INFO, 
                                                                 "java.util.prefs.BackingStoreException", 
                                                                 new Type[0]), 
                                          new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[]{new StringLiteral(SourceInfo.NO_INFO, "boo")})))});
      
      Block b = new Block(SourceInfo.NO_INFO, bb);
      Block b2 = new Block(SourceInfo.NO_INFO, emptyBody);
    
      _bbtc._bodyData.getMethodData().setThrown(new String[0]);
      
      

      NormalTryCatchStatement tcfs = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[0]);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      tcfs.visit(_bbtc);
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "This statement throws the exception java.util.prefs.BackingStoreException which needs to be caught or declared to be thrown", errors.get(0).getFirst());
            

      NormalTryCatchStatement inner = new NormalTryCatchStatement(SourceInfo.NO_INFO, b, new CatchBlock[0]);
      NormalTryCatchStatement nested = new NormalTryCatchStatement(SourceInfo.NO_INFO, new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {inner})), new CatchBlock[0]);
      
      BlockData innerBD = new BlockData(_bbtc._bodyData);
      innerBD.addBlock(new BlockData(innerBD));
      innerBD.addBlock(new BlockData(innerBD));

      _bbtc._bodyData.removeAllBlocks();
      _bbtc._bodyData.addBlock(innerBD);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      
      _bbtc._bodyData.resetBlockIterator();

      nested.visit(_bbtc);
      assertEquals("There should still be be 1 error", 1, errors.size());  
      assertEquals("Error message should be correct", 
                   "This statement throws the exception java.util.prefs.BackingStoreException " + 
                   "which needs to be caught or declared to be thrown", 
                   errors.get(0).getFirst());
                                      

      UninitializedVariableDeclarator uvd1 = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, new ClassOrInterfaceType(SourceInfo.NO_INFO, "java.util.prefs.BackingStoreException", new Type[0]), new Word(SourceInfo.NO_INFO, "e"));
      FormalParameter fp1 = new FormalParameter(SourceInfo.NO_INFO, uvd1, false);
      BlockData catchBD = new BlockData(_bbtc._bodyData);
      VariableData fpData = new VariableData("e", null, eb, true, catchBD);
      catchBD.addVar(fpData);
     

      CatchBlock cb = new CatchBlock(SourceInfo.NO_INFO, fp1, b2);
      NormalTryCatchStatement nested2 = new NormalTryCatchStatement(SourceInfo.NO_INFO, new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {inner})), new CatchBlock[] {cb});

      _bbtc._bodyData.removeAllBlocks();
      innerBD.resetBlockIterator();
      _bbtc._bodyData.addBlock(innerBD);
      _bbtc._bodyData.addBlock(catchBD);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.resetBlockIterator();

      nested2.visit(_bbtc);
      assertEquals("There should still be 1 error", 1, errors.size());
      

      BracedBody reb = new BracedBody(SourceInfo.NO_INFO, 
                                     new BodyItemI[] { 
        new ThrowStatement(SourceInfo.NO_INFO, 
        new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, 
                                         new ClassOrInterfaceType(SourceInfo.NO_INFO, 
                                                                 "java.lang.RuntimeException", 
                                                                 new Type[0]), 
                                         new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0])))});
      
      NormalTryCatchStatement inner3 = new NormalTryCatchStatement(SourceInfo.NO_INFO, new Block(SourceInfo.NO_INFO, reb), new CatchBlock[0]);
      NormalTryCatchStatement nested3 = new NormalTryCatchStatement(SourceInfo.NO_INFO, new Block(SourceInfo.NO_INFO, new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {inner3})), new CatchBlock[0]);
      
      _bbtc._bodyData.removeAllBlocks();
      innerBD.resetBlockIterator();
      _bbtc._bodyData.addBlock(innerBD);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.resetBlockIterator();

      
      nested3.visit(_bbtc);
      assertEquals("There should still be 1 error", 1, errors.size());
      
      
      _bbtc._bodyData.getMethodData().setThrown(new String[]{"java.util.prefs.BackingStoreException"});
      _bbtc._bodyData.removeAllBlocks();
      innerBD.resetBlockIterator();
      _bbtc._bodyData.addBlock(innerBD);
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.addBlock(new BlockData(_bbtc._bodyData));
      _bbtc._bodyData.resetBlockIterator();

      nested.visit(_bbtc);
      assertEquals("There should still be 1 error!", 1, errors.size());
    }
  }
}