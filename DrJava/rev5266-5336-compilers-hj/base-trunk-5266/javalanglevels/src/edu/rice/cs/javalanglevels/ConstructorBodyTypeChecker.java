

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;
import java.io.*;

import junit.framework.TestCase;


public class ConstructorBodyTypeChecker extends BodyTypeChecker {

  
  public ConstructorBodyTypeChecker(BodyData bodyData, File file, String packageName, LinkedList<String> importedFiles, LinkedList<String> importedPackages, LinkedList<VariableData> vars, LinkedList<Pair<SymbolData, JExpression>> thrown) {
    super(bodyData, file, packageName, importedFiles, importedPackages, vars, thrown);
  }
 
  
  
  protected BodyTypeChecker createANewInstanceOfMe(BodyData bodyData, File file, String pakage, LinkedList<String> importedFiles, LinkedList<String> importedPackages, LinkedList<VariableData> vars, LinkedList<Pair<SymbolData, JExpression>> thrown) {
    return new ConstructorBodyTypeChecker(bodyData, file, pakage, importedFiles, importedPackages, vars, thrown);
  }
  
  
  
  public TypeData simpleThisConstructorInvocationAllowed(SimpleThisConstructorInvocation that) {
    
    
    String name = LanguageLevelVisitor.getUnqualifiedClassName(_data.getSymbolData().getName());
    InstanceData[] args = getArgTypesForInvocation(that.getArguments());
    if (args == null) {return null;}
    MethodData cd = _lookupMethod(name, _data.getSymbolData(), args, that, 
                           "No constructor found in class " + _data.getSymbolData().getName() + " with signature: ", 
                           true, _data.getSymbolData());
    
    if (cd==null) {return null;}
    
    
    LinkedList<VariableData> myFields = _data.getSymbolData().getVars();
    for (int i = 0; i<myFields.size(); i++) {
      if (myFields.get(i).hasModifier("final")) {
        _vars.get(_vars.indexOf(myFields.get(i))).gotValue();
        thingsThatHaveBeenAssigned.addLast(_vars.get(_vars.indexOf(myFields.get(i))));
      }
    }
    
    
    String[] thrown = cd.getThrown();
    for (int i = 0; i<thrown.length; i++) {
      _thrown.addLast(new Pair<SymbolData, JExpression>(getSymbolData(thrown[i], _getData(), that), that));
    }
    
    return null;
  }
  
  
  public TypeData complexThisConstructorInvocationNotAllowed(ComplexThisConstructorInvocation that) {
    _addError("Constructor Invocations of this form are never allowed.  A constructor invocation can appear here, but it must either be a super constructor invocation or have the form this(...)", that);
    return null;
  }
  
  
  public TypeData simpleSuperConstructorInvocationAllowed(SimpleSuperConstructorInvocation that) {
    
    SymbolData superClass = _data.getSymbolData().getSuperClass();
    
    if (superClass == null) {  
      _addError("The class " + _data.getSymbolData().getName() + " does not have a super class", that);
      return null;
    }

    
    if (superClass.getOuterData() != null && !(superClass.hasModifier("static"))) {
      _addError(superClass.getName() + " is a non-static inner class of " + superClass.getOuterData().getName() + ".  Its constructor must be invoked from an instance of its outer class", that);
      return null;
    }

    
    
    String name = LanguageLevelVisitor.getUnqualifiedClassName(superClass.getName());
    InstanceData[] args = getArgTypesForInvocation(that.getArguments());
    if (args == null) {return null;}
    MethodData cd = _lookupMethod(name, superClass, args, that, 
                           "No constructor found in class " + superClass.getName() + " with signature: ", 
                           true, superClass);

    if (cd == null) {return null;}
    
    
    String[] thrown = cd.getThrown();
    for (int i = 0; i<thrown.length; i++) {
      _thrown.addLast(new Pair<SymbolData, JExpression>(getSymbolData(thrown[i], _getData(), that), that));
    }

    
    return null;
  }
  
  
  
  public TypeData complexSuperConstructorInvocationAllowed(ComplexSuperConstructorInvocation that) {
    
    ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, _vars, _thrown);
    TypeData enclosingResult = that.getEnclosing().visit(etc);
    thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);

    if (!assertFound(enclosingResult, that)) {return null;};
    SymbolData superClass = _data.getSymbolData().getSuperClass();
    
    
    if (superClass == null) { 
      _addError("A qualified super constructor invocation can only be used to invoke the constructor of your super class from the context of its outer class.  The class " + _data.getSymbolData().getName() + " does not have a super class, so you cannot do this here", that);
      return null;
    }
    else if (superClass.getOuterData() == null) { 
      _addError("A qualified super constructor invocation can only be used to invoke the constructor of your super class from the context of its outer class.  The super class " + superClass.getName() + " does not have an outer class, so you cannot do this here", that);
      return null;
    }
    
    else if (enclosingResult == null) {
      _addError("A qualified super constructor invocation can only be used to invoke the constructor of your super class from the context of its outer class.", that);
      return null;
    }
    
    else if (superClass.getOuterData() != enclosingResult.getSymbolData()) {
      _addError("A qualified super constructor invocation can only be used to invoke the constructor of your super class from the context of its outer class.  The class or interface " + enclosingResult.getSymbolData().getName() + " is not the outer class of the super class " + superClass.getName(), that);
      return null;
    }
    
    else if (!enclosingResult.isInstanceType()) {
      _addError("A qualified super constructor invocation can only be made from the context of an instance of the outer class of the super class.  You have specified a type name", that);
      return null;
    }
      
      
    else if (superClass.hasModifier("static")) {
      _addError("A qualified super constructor invocation can only be used to invoke the constructor of a non-static super class from the context of its outer class.  The super class " + superClass.getName() + " is a static inner class", that);
      return null;
    }

    
    String name = LanguageLevelVisitor.getUnqualifiedClassName(superClass.getName());
    InstanceData[] args = getArgTypesForInvocation(that.getArguments());
    if (args == null) {return null;}
    MethodData cd = _lookupMethod(name, superClass, args, that, 
                                  "No constructor found in class " + superClass.getName() + " with signature: ", 
                                  true, superClass);

    if (cd == null) {return null;}
    
    String[] thrown = cd.getThrown();
    for (int i = 0; i<thrown.length; i++) {
      _thrown.addLast(new Pair<SymbolData, JExpression>(getSymbolData(thrown[i], _getData(), that), that));
    }

    
    return null;
  }
  
  
  private void implicitSuperConstructor(BracedBody that) {
    SymbolData superClass = _data.getSymbolData().getSuperClass();
    
    if (superClass == null) {  
      return;
    }

    
    if (superClass.getOuterData() != null && !(superClass.hasModifier("static"))) {
      _addError("There is an implicit call to the constructor of " + superClass.getName() + " here, but " + superClass.getName() + " is a non-static inner class of " + superClass.getOuterData().getName() + ".  Thus, you must explicitly invoke its constructor from an instance of its outer class", that);
      return;
    }

    
    
    String name = LanguageLevelVisitor.getUnqualifiedClassName(superClass.getName());
    
    
    
    
    
    MethodData cd = _lookupMethod(name, superClass, new InstanceData[0], that, 
                           "You must invoke one of " + superClass.getName() + "'s constructors here.  You can either explicitly invoke one of its exisitng constructors or add a constructor with signature: ", 
                           true, superClass);

    if (cd == null) return;
    
    
    String[] thrown = cd.getThrown();
    for (int i = 0; i<thrown.length; i++) {
      _thrown.addLast(new Pair<SymbolData, JExpression>(getSymbolData(thrown[i], _getData(), that), that));
    }
    
    
    return;
  }
    
  
  public TypeData forVoidReturnStatementOnly(VoidReturnStatement that) {
    
    return _bodyData.getSymbolData().getInstanceData();
  }

  
  public TypeData forValueReturnStatementOnly(ValueReturnStatement that, TypeData value_result) {
      _addError("You cannot return a value from a class's constructor", that);
      return _bodyData.getSymbolData().getInstanceData();
  }

  
  public TypeData forBracedBody(BracedBody that) {
    int startIndex = 0;
    final TypeData[] items_result = makeArrayOfRetType(that.getStatements().length);
    if (items_result.length > 0) {
      
      if (that.getStatements()[0] instanceof ExpressionStatement) {
        Expression firstExpression = ((ExpressionStatement) that.getStatements()[0]).getExpression();
        if (firstExpression instanceof SimpleThisConstructorInvocation) {
          items_result[0] = simpleThisConstructorInvocationAllowed((SimpleThisConstructorInvocation) firstExpression);
          startIndex ++;
        }
        
        else if (firstExpression instanceof ComplexThisConstructorInvocation) {
          items_result[0] = complexThisConstructorInvocationNotAllowed((ComplexThisConstructorInvocation) firstExpression);
          startIndex++;
        }    
        else if (firstExpression instanceof SimpleSuperConstructorInvocation) {
          items_result[0] = simpleSuperConstructorInvocationAllowed((SimpleSuperConstructorInvocation) firstExpression);
          startIndex++;
        }
        else if (firstExpression instanceof ComplexSuperConstructorInvocation) {
          items_result[0] = complexSuperConstructorInvocationAllowed((ComplexSuperConstructorInvocation) firstExpression);
          startIndex++;
        }      
      }
    }
    if (startIndex == 0) {
      implicitSuperConstructor(that);
    }
    
    for (int j = 0; j<this._thrown.size(); j++) {
      if (isUncaughtCheckedException(this._thrown.get(j).getFirst(), that)) {
        handleUncheckedException(this._thrown.get(j).getFirst(), this._thrown.get(j).getSecond());
      }
      
    }
    
    for (int i = startIndex; i < that.getStatements().length; i++) {
        items_result[i] = that.getStatements()[i].visit(this);
      
      for (int j = 0; j<this._thrown.size(); j++) {
        if (isUncaughtCheckedException(this._thrown.get(j).getFirst(), that)) {
          handleUncheckedException(this._thrown.get(j).getFirst(), this._thrown.get(j).getSecond());
        }
      }
    }

    return forBracedBodyOnly(that, items_result);
  }
  
  
   
  public static class ConstructorBodyTypeCheckerTest extends TestCase {
    
    private ConstructorBodyTypeChecker _cbtc;
    
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
    
    
    public ConstructorBodyTypeCheckerTest() {
      this("");
    }
    public ConstructorBodyTypeCheckerTest(String name) {
      super(name);
    }
    
    public void setUp() {
      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _sd3 = new SymbolData("zebra");
      _sd4 = new SymbolData("u.like.emu");
      _sd5 = new SymbolData("elephant");
      _sd6 = new SymbolData("cebu");

      _bd1 = new MethodData("monkey", 
                            _packageMav, 
                            new TypeParameter[0], 
                            _sd1, 
                            new VariableData[] { new VariableData("i", _publicMav, SymbolData.INT_TYPE, true, null), new VariableData(SymbolData.BOOLEAN_TYPE) },
                            new String[0],
                            _sd1,
                            null); 
      ((MethodData) _bd1).getParams()[0].setEnclosingData(_bd1);                      
      ((MethodData) _bd1).getParams()[1].setEnclosingData(_bd1);                      

      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      _bd1.addEnclosingData(_sd1);
      _bd1.addVars(((MethodData)_bd1).getParams());
      _cbtc = new ConstructorBodyTypeChecker(_bd1, new File(""), "", new LinkedList<String>(), new LinkedList<String>(), new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>());
      _cbtc._importedPackages.addFirst("java.lang");
    }
    
    
    public void testForVoidReturnStatementOnly() {
      _cbtc._bodyData = _bd1; 

      
      BracedBody bb1 = new BracedBody(SourceInfo.NO_INFO, 
                                      new BodyItemI[] { new VoidReturnStatement(SourceInfo.NO_INFO)});

      TypeData sd = bb1.visit(_cbtc);

      assertEquals("There should be no errors.", 0, errors.size());
      assertEquals("Should return i.like.monkey type.", _sd1.getInstanceData(), sd);

    }
   
    public void testforValueReturnStatementOnly() {
      
      BracedBody bb1 = new BracedBody(SourceInfo.NO_INFO,
                                      new BodyItemI[] { new ValueReturnStatement(SourceInfo.NO_INFO,
                                                                                 new BooleanLiteral(SourceInfo.NO_INFO, true))});
      TypeData sd = bb1.visit(_cbtc);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("Should return i.like.monkey type", _sd1.getInstanceData(), sd);
      assertEquals("Error message should be correct", "You cannot return a value from a class's constructor", errors.get(0).getFirst());

    }
    
    public void testCreateANewInstanceOfMe() {
      
      BodyTypeChecker btc = _cbtc.createANewInstanceOfMe(_cbtc._bodyData, _cbtc._file, _cbtc._package, _cbtc._importedFiles, _cbtc._importedPackages, _cbtc._vars, _cbtc._thrown);
      assertTrue("Should be an instance of ConstructorBodyTypeChecker", btc instanceof ConstructorBodyTypeChecker);
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


      
      _sd3.setIsContinuation(false);
      _sd3.setMav(_publicMav);
      _sd1.setSuperClass(_sd3);
      
      _cbtc._bodyData.getMethodData().setThrown(new String[0]);
      _sd3.setMav(_publicMav);
      _sd3.setIsContinuation(false);
      _cbtc.symbolTable.put(_sd3.getName(), _sd3);
      MethodData constructor = new MethodData("zebra", _publicMav, new TypeParameter[0], _sd3, new VariableData[0], new String[] {"java.util.prefs.BackingStoreException"}, _sd3, null);
      _sd3.addMethod(constructor);
      
      BracedBody supConstr = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[]{new ExpressionStatement(SourceInfo.NO_INFO, new SimpleSuperConstructorInvocation(SourceInfo.NO_INFO, new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0])))});
      _cbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      supConstr.visit(_cbtc);
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "The constructor of this class's super class could throw the exception java.util.prefs.BackingStoreException, so the enclosing constructor needs to be declared to throw it", errors.getLast().getFirst());

      
      
      _cbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      _cbtc._bodyData.getMethodData().setThrown(new String[] {"java.util.prefs.BackingStoreException"});
      supConstr.visit(_cbtc);
      assertEquals("There should still be one error", 1, errors.size());
      

      _cbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      _cbtc._bodyData.getMethodData().setThrown(new String[0]);
      BracedBody emptyBody = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      emptyBody.visit(_cbtc);
      assertEquals("There should be 2 errors", 2, errors.size());
      assertEquals("The error message should be correct", "There is an implicit call to the superclass's constructor here.  That constructor could throw the exception java.util.prefs.BackingStoreException, so the enclosing constructor needs to be declared to throw it", errors.getLast().getFirst());

      
      _cbtc._bodyData.getMethodData().setThrown(new String[] {"java.util.prefs.BackingStoreException"});
      emptyBody.visit(_cbtc);
      assertEquals("There should still be two errors", 2, errors.size());
      
      
      _cbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      BracedBody thisConstr = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[]{new ExpressionStatement(SourceInfo.NO_INFO, new SimpleThisConstructorInvocation(SourceInfo.NO_INFO, new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0])))});

      MethodData thisConstructor = new MethodData("cebu", _publicMav, new TypeParameter[0], _sd6, new VariableData[0], new String[] {"java.util.prefs.BackingStoreException"}, _sd6, null);
      MethodData thisConstructorNoThrown = new MethodData("cebu", _publicMav, new TypeParameter[0], _sd6, new VariableData[0], new String[0], _sd6, null);
      _sd6.addMethod(thisConstructor);
      BodyData oldData = _cbtc._bodyData;
      _cbtc._data = thisConstructorNoThrown;
      _cbtc._bodyData = thisConstructorNoThrown;

      thisConstr.visit(_cbtc);
      assertEquals("There should be 3 errors", 3, errors.size());
      assertEquals("The error message should be correct", "This constructor could throw the exception java.util.prefs.BackingStoreException, so this enclosing constructor needs to be declared to throw it", errors.getLast().getFirst());

      
      _cbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      _cbtc._bodyData.getMethodData().setThrown(new String[] {"java.util.prefs.BackingStoreException"});
      thisConstr.visit(_cbtc);
      assertEquals("There should still be 3 errors", 3, errors.size());
      
      
      
      _cbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();
      
      _sd5.setIsContinuation(false);
      _sd5.addInnerClass(_sd3);
      _sd3.setOuterData(_sd5);
      
      _sd5.setMav(_publicMav);
      _sd5.setIsContinuation(false);
      symbolTable.put("elephant", _sd5);
      
      BracedBody complexSC = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new ExpressionStatement(SourceInfo.NO_INFO, new ComplexSuperConstructorInvocation(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "e")), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0])))});
      oldData.getMethodData().setThrown(new String[0]);
      _cbtc._vars.add(new VariableData("e", _publicMav, _sd5, true, _sd3));
      _cbtc._data = oldData;
      _cbtc._bodyData = oldData;
      
      complexSC.visit(_cbtc);
      assertEquals("There should be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", "The constructor of this class's super class could throw the exception java.util.prefs.BackingStoreException, so the enclosing constructor needs to be declared to throw it", errors.getLast().getFirst());

      
      _cbtc._thrown = new LinkedList<Pair<SymbolData, JExpression>>();

      _cbtc._bodyData.getMethodData().setThrown(new String[] {"java.util.prefs.BackingStoreException"});
      complexSC.visit(_cbtc);
      assertEquals("There should still be 4 errors", 4, errors.size());

    }
    
    public void testSimpleThisConstructorInvocationAllowed() {
      
      MethodData constructor = new MethodData("zebra", _publicMav, new TypeParameter[0], _sd3, new VariableData[] {new VariableData(SymbolData.INT_TYPE)}, new String[0], _sd3, null);
      _sd3.addMethod(constructor);
      _cbtc._bodyData = constructor;
      _cbtc._data = constructor;
      
      VariableData vd1 = new VariableData("i", _finalMav, SymbolData.INT_TYPE, false, _sd3);
      VariableData vd2 = new VariableData("d", _finalMav, SymbolData.DOUBLE_TYPE, false, _sd3);
      VariableData vd3 = new VariableData("notFinal", _publicMav, SymbolData.BOOLEAN_TYPE, false, _sd3);
      _cbtc._vars.add(vd1);
      _cbtc._vars.add(vd2);
      _cbtc._vars.add(vd3);
      _sd3.addVar(vd1);
      _sd3.addVar(vd2);
      _sd3.addVar(vd3);
      
      SimpleThisConstructorInvocation constr = new SimpleThisConstructorInvocation(SourceInfo.NO_INFO, new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new IntegerLiteral(SourceInfo.NO_INFO, 5)}));
      _cbtc.simpleThisConstructorInvocationAllowed(constr);
      assertEquals("Should be no errors", 0, errors.size());
      assertEquals("vd1 should have value", true, vd1.hasValue());
      assertEquals("vd2 should have value", true, vd2.hasValue());
      assertEquals("vd3 is not final, and thus should not have a value", false, vd3.hasValue());
      assertEquals("thrown should have 0 elements", 0, _cbtc._thrown.size());
      
      
      vd1.lostValue();
      vd2.lostValue();
      
      SimpleThisConstructorInvocation constr2 = new SimpleThisConstructorInvocation(SourceInfo.NO_INFO, new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      _cbtc.simpleThisConstructorInvocationAllowed(constr2);
      assertEquals("Should be one error", 1, errors.size());
      assertEquals("Error message should be correct", "No constructor found in class zebra with signature: zebra().", errors.getLast().getFirst());
      assertFalse("vd1 should not have value", vd1.hasValue());
      assertFalse("vd2 should not have value", vd2.hasValue());
      assertFalse("vd3 should not have a value", vd3.hasValue());
      assertEquals("thrown should have 0 elements", 0, _cbtc._thrown.size());

      
      
      constructor.setThrown(new String[] {"java.util.prefs.BackingStoreException"});
      _cbtc.simpleThisConstructorInvocationAllowed(constr);
      assertEquals("Should still be 1 error", 1, errors.size());
      assertTrue("vd1 should have a value", vd1.hasValue());
      assertTrue("vd2 should have a value", vd2.hasValue());
      assertFalse("vd3 is not final, and thus should not have a value", vd3.hasValue());
      assertEquals("thrown should have 1 element", 1, _cbtc._thrown.size());
      
    }

    
    public void testComplexThisConstructorInvocationNotAllowed() {
      ComplexThisConstructorInvocation constr = new ComplexThisConstructorInvocation(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      _cbtc.complexThisConstructorInvocationNotAllowed(constr);
      assertEquals("There should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "Constructor Invocations of this form are never allowed.  A constructor invocation can appear here, but it must either be a super constructor invocation or have the form this(...)", errors.getLast().getFirst());
    }
    

    public void testSimpleSuperConstructorInvocationAllowed() {
      
      SimpleSuperConstructorInvocation constr = new SimpleSuperConstructorInvocation(SourceInfo.NO_INFO, new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new IntegerLiteral(SourceInfo.NO_INFO, 5)}));
      _cbtc.simpleSuperConstructorInvocationAllowed(constr);
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "The class i.like.monkey does not have a super class", errors.getLast().getFirst());
      
      
      _sd1.setSuperClass(_sd3);
      _cbtc.simpleSuperConstructorInvocationAllowed(constr);
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "No constructor found in class zebra with signature: zebra(int).", errors.getLast().getFirst());
      
      
      MethodData constructor = new MethodData("zebra", _publicMav, new TypeParameter[0], _sd3, new VariableData[] {new VariableData(SymbolData.INT_TYPE)}, new String[0], _sd3, null);
      _sd3.addMethod(constructor);
      _cbtc.simpleSuperConstructorInvocationAllowed(constr);
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      constructor.setThrown(new String[] {"java.util.prefs.BackingStoreException"});
      _cbtc.simpleSuperConstructorInvocationAllowed(constr);
      assertEquals("Should still be 2 errors", 2, errors.size());
      assertEquals("thrown should have 1 element", 1, _cbtc._thrown.size());
      
      
      constructor.setThrown(new String[0]);
      _sd3.setOuterData(_sd5);
      _cbtc.simpleSuperConstructorInvocationAllowed(constr);
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "zebra is a non-static inner class of elephant.  Its constructor must be invoked from an instance of its outer class", errors.getLast().getFirst());
      

      
      _sd3.addModifier("static");
      _cbtc.simpleSuperConstructorInvocationAllowed(constr);
      assertEquals("Should still be 3 errors", 3, errors.size());
 
    }
    
    public void testComplexSuperConstructorInvocationAllowed() {

      
      ComplexSuperConstructorInvocation constr1 = new ComplexSuperConstructorInvocation(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "nonExistant")), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      _cbtc.complexSuperConstructorInvocationAllowed(constr1);
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "Could not resolve symbol nonExistant", errors.getLast().getFirst());
      
      
      ComplexSuperConstructorInvocation constr2 = new ComplexSuperConstructorInvocation(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "zebra")), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      symbolTable.put("zebra", _sd3);
      _sd3.setIsContinuation(false);
      _sd3.setMav(_publicMav);
      _cbtc.complexSuperConstructorInvocationAllowed(constr2);
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "A qualified super constructor invocation can only be used to invoke the constructor of your super class from the context of its outer class.  The class i.like.monkey does not have a super class, so you cannot do this here", errors.getLast().getFirst());

      
      _sd1.setSuperClass(_sd5);
      _cbtc.complexSuperConstructorInvocationAllowed(constr2);
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "A qualified super constructor invocation can only be used to invoke the constructor of your super class from the context of its outer class.  The super class elephant does not have an outer class, so you cannot do this here", errors.getLast().getFirst());
      
      
      _sd5.setOuterData(_sd3);
      ComplexSuperConstructorInvocation constr3 = new ComplexSuperConstructorInvocation(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "u.like.emu")), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      symbolTable.put("u.like.emu", _sd4);
      _sd4.setPackage("u.like");
      _sd4.setIsContinuation(false);
      _sd4.setMav(_publicMav);
      _cbtc.complexSuperConstructorInvocationAllowed(constr3);
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", "A qualified super constructor invocation can only be used to invoke the constructor of your super class from the context of its outer class.  The class or interface u.like.emu is not the outer class of the super class elephant", errors.getLast().getFirst());
      
      
      _cbtc.complexSuperConstructorInvocationAllowed(constr2);
      assertEquals("Should be 5 errors", 5, errors.size());
      assertEquals("Error message should be correct", "A qualified super constructor invocation can only be made from the context of an instance of the outer class of the super class.  You have specified a type name", errors.getLast().getFirst());

      
      ComplexSuperConstructorInvocation constr4 = new ComplexSuperConstructorInvocation(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "var")), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      _cbtc._vars.add(new VariableData("var", _publicMav, _sd3, true, _sd1));
      _cbtc.complexSuperConstructorInvocationAllowed(constr4);
      assertEquals("Should be 6 errors", 6, errors.size());
      assertEquals("Error message should be correct", "No constructor found in class elephant with signature: elephant().", errors.getLast().getFirst());
      
      
      MethodData constructor = new MethodData("elephant", _publicMav, new TypeParameter[0], _sd5, new VariableData[0], new String[] {"java.util.prefs.BackingStoreException"}, _sd5, null);
      _sd5.addMethod(constructor);
      _cbtc.complexSuperConstructorInvocationAllowed(constr4);
      assertEquals("Should still be 6 errors", 6, errors.size());
      assertEquals("_thrown should now have 1 element", 1, _cbtc._thrown.size());
      
      
      _sd5.addModifier("static");
      _cbtc.complexSuperConstructorInvocationAllowed(constr4);
      assertEquals("Should be 7 errors", 7, errors.size());
      assertEquals("Error message should be correct", "A qualified super constructor invocation can only be used to invoke the constructor of a non-static super class from the context of its outer class.  The super class elephant is a static inner class", errors.getLast().getFirst());
    }
    
    
    public void testImplicitSuperConstructor() {
      BracedBody constr = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]);
      
      _sd1.setSuperClass(_sd3);
      _cbtc.implicitSuperConstructor(constr);
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "You must invoke one of zebra's constructors here.  You can either explicitly invoke one of its exisitng constructors or add a constructor with signature: zebra().", errors.getLast().getFirst());
      
      
      MethodData constructor = new MethodData("zebra", _publicMav, new TypeParameter[0], _sd3, new VariableData[0], new String[0], _sd3, null);
      _sd3.addMethod(constructor);
      _cbtc.implicitSuperConstructor(constr);
      assertEquals("Should still be 1 error", 1, errors.size());
      
      
      constructor.setThrown(new String[] {"java.util.prefs.BackingStoreException"});
      _cbtc.implicitSuperConstructor(constr);
      assertEquals("Should still be 1 error", 1, errors.size());
      assertEquals("thrown should have 1 element", 1, _cbtc._thrown.size());
      
      
      constructor.setThrown(new String[0]);
      _sd3.setOuterData(_sd5);
      _cbtc.implicitSuperConstructor(constr);
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "There is an implicit call to the constructor of zebra here, but zebra is a non-static inner class of elephant.  Thus, you must explicitly invoke its constructor from an instance of its outer class", errors.getLast().getFirst());
      

      
      _sd3.addModifier("static");
      _cbtc.implicitSuperConstructor(constr);
      assertEquals("Should still be 2 errors", 2, errors.size());
    }
  }
}


  


