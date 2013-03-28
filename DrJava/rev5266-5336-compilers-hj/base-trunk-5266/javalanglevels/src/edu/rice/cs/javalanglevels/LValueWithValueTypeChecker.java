

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;
import java.io.File;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;

import junit.framework.TestCase;



public class LValueWithValueTypeChecker extends JExpressionIFAbstractVisitor<TypeData> {
  
  
  private final TestAssignable _testAssignableInstance;

  
  private final Bob _bob;

  
  public LValueWithValueTypeChecker(Bob bob) {
    _testAssignableInstance = new TestAssignable(bob._data, bob._file, bob._package, bob._importedFiles, bob._importedPackages, bob._vars, bob._thrown);
    _bob = bob;
  }
  
  
  public TypeData defaultCase(JExpressionIF that) {
    _bob._addError("You cannot assign a value to an expression of this kind.  Values can only be assigned to fields or variables", that);
    return null;
  }
  
   
  public TypeData forIncrementExpression(IncrementExpression that) {
    _bob._addError("You cannot assign a value to an increment expression", that);
    return null;
  }

  
  public TypeData forNameReference(NameReference that) {
    return that.visit(_testAssignableInstance);
  }

  
  public TypeData forArrayAccess(ArrayAccess that) {
    return that.visit(_testAssignableInstance);
  }
      
  
  public TypeData forParenthesized(Parenthesized that) {
    return that.getValue().visit(this);
  }
  
  
  private class TestAssignable extends ExpressionTypeChecker {
  
    public TestAssignable(Data data, File file, String packageName, LinkedList<String> importedFiles, LinkedList<String> importedPackages, LinkedList<VariableData> vars, LinkedList<Pair<SymbolData, JExpression>> thrown) {
      super(data, file, packageName, importedFiles, importedPackages, vars, thrown);
    }

    
    public TypeData forSimpleNameReference(SimpleNameReference that) {
      Word myWord = that.getName();
      myWord.visit(this);
      
      VariableData reference = getFieldOrVariable(myWord.getText(), _data, _data.getSymbolData(), that, _vars, true, true);
      if (reference != null) {
        if (!reference.hasValue()) {
          _addError("You cannot use " + reference.getName() + " here, because it may not have been given a value", that.getName());
        }
        else if (reference.isFinal()) {
          _addError("You cannot assign a new value to " + reference.getName() + " because it is immutable and has already been given a value", that.getName());
        }
        
        
        else if (! reference.hasModifier("static") && inStaticMethod()) {
          _addError("Non static field or variable " + reference.getName() + " cannot be referenced from a static context", that);
        }
        
        return reference.getType().getInstanceData();
        
      }
      
      SymbolData classR = findClassReference(null, myWord.getText(), that);
      if (classR == SymbolData.AMBIGUOUS_REFERENCE) {return null;}
      if (classR != null) {
        if (checkAccessibility(that, classR.getMav(), classR.getName(), classR, _data.getSymbolData(), "class or interface", false)) {
          return classR;
        }
      }
      PackageData packageD = new PackageData(myWord.getText());
      return packageD;
    }
    
    
    public TypeData forComplexNameReference(ComplexNameReference that) {
      ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, _vars, _thrown);
      TypeData lhs = that.getEnclosing().visit(etc);
      _bob.thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned);
      Word myWord = that.getName();
      
      
      if (lhs instanceof PackageData) {
        SymbolData classRef =  findClassReference(lhs, myWord.getText(), that);
        if (classRef != null) {return classRef;}
        return new PackageData((PackageData) lhs, myWord.getText());
      }
      
      
      VariableData reference = getFieldOrVariable(myWord.getText(), lhs.getSymbolData(), _data.getSymbolData(), that);
      if (reference != null) {
        if (lhs instanceof SymbolData) {
          
          if (!reference.hasModifier("static")) {
            _addError("Non-static variable " + reference.getName() + " cannot be accessed from the static context " + Data.dollarSignsToDots(lhs.getName()) + ".  Perhaps you meant to instantiate an instance of " + Data.dollarSignsToDots(lhs.getName()), that);
            return reference.getType().getInstanceData();
          }
        }
        
        
        if (!reference.hasValue()) {
          _addError("You cannot use " + reference.getName() + " here, because it may not have been given a value", that.getName());
        }
        
        
        if (!canBeAssigned(reference)) {
          _addError("You cannot assign a new value to " + reference.getName() + " because it is immutable and has already been given a value", that.getName());
        }
        return reference.getType().getInstanceData();
      }
      
      
      SymbolData sd = getSymbolData(true, myWord.getText(), lhs.getSymbolData(), that, false);
      if (sd != null && sd != SymbolData.AMBIGUOUS_REFERENCE) {
        
        if (!checkAccessibility(that, sd.getMav(), sd.getName(), sd, _data.getSymbolData(), "class or interface")) {return null;}

        
        if (!sd.hasModifier("static")) {
          _addError("Non-static inner class " +Data.dollarSignsToDots(sd.getName()) + " cannot be accessed from this context.  Perhaps you meant to instantiate it", that);
        }
        
        
        else if (lhs instanceof InstanceData) {
          _addError("You cannot reference the static inner class " + Data.dollarSignsToDots(sd.getName()) + " from an instance of " + Data.dollarSignsToDots(lhs.getName()), that);
        }
        return sd;
      }
      
      if (sd != SymbolData.AMBIGUOUS_REFERENCE) {_addError("Could not resolve " + myWord.getText() + " from the context of " + Data.dollarSignsToDots(lhs.getName()), that);}
      return null;
    }
    
    
    public TypeData forArrayAccess(ArrayAccess that) {
      ExpressionTypeChecker etc = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, _vars, _thrown);
      TypeData lhs = that.getArray().visit(etc);
      _bob.thingsThatHaveBeenAssigned.addAll(etc.thingsThatHaveBeenAssigned); 
      
      ExpressionTypeChecker indexTC = new ExpressionTypeChecker(_data, _file, _package, _importedFiles, _importedPackages, _vars, _thrown);
      TypeData index = that.getIndex().visit(indexTC);
      _bob.thingsThatHaveBeenAssigned.addAll(indexTC.thingsThatHaveBeenAssigned); 
      
      return forArrayAccessOnly(that, lhs, index);
    }
    
  }
  

  
  public static class LValueWithValueTypeCheckerTest extends TestCase {
    
    private LValueWithValueTypeChecker _lvtc;
    LValueWithValueTypeChecker.TestAssignable _ta;
    
    
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
    private ModifiersAndVisibility _finalPublicMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"final", "public"});
    private ModifiersAndVisibility _publicAbstractMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public", "abstract"});
    private ModifiersAndVisibility _publicStaticMav = new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public", "static"});
    
    
    public LValueWithValueTypeCheckerTest() {
      this("");
    }
    public LValueWithValueTypeCheckerTest(String name) {
      super(name);
    }
    
    public void setUp() {
      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _sd3 = new SymbolData("zebra");
      _sd4 = new SymbolData("u.like.emu");
      _sd5 = new SymbolData("");
      _sd6 = new SymbolData("cebu");
      _lvtc = new LValueWithValueTypeChecker(new Bob(_sd1, new File(""), "", new LinkedList<String>(), new LinkedList<String>(), new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>()));
      _ta = _lvtc._testAssignableInstance;
      _lvtc._bob.errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();

      _lvtc._bob._importedPackages.addFirst("java.lang");
    }
    
    

    public void testDefaultCase() {
      
      new NullLiteral(SourceInfo.NO_INFO).visit(_lvtc);
      assertEquals("Should be 1 error", 1, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "You cannot assign a value to an expression of this kind.  Values can only be assigned to fields or variables",
                   _lvtc._bob.errors.getLast().getFirst());

      
      new PlusExpression(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 21), new IntegerLiteral(SourceInfo.NO_INFO, 22)).visit(_lvtc);
      assertEquals("Should be 2 errors", 2, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "You cannot assign a value to an expression of this kind.  Values can only be assigned to fields or variables",
                   _lvtc._bob.errors.getLast().getFirst());
    }
    
    public void testForIncrementExpression() {
      
      PositivePrefixIncrementExpression p = new PositivePrefixIncrementExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "bob")));
      assertEquals("Should return null", null, p.visit(_lvtc));
      assertEquals("Should be 1 error", 1, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "You cannot assign a value to an increment expression", _lvtc._bob.errors.getLast().getFirst());
    }
  

    public void testForSimpleNameReference() {
      
      SimpleNameReference var = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "variable1"));
      VariableData varData = new VariableData("variable1", _publicMav, SymbolData.INT_TYPE, false, _ta._data);
      _ta._vars.add(varData);
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), var.visit(_lvtc));
      assertEquals("Should be 1 error", 1, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "You cannot use variable1 here, because it may not have been given a value", _lvtc._bob.errors.getLast().getFirst());
      
      
      varData.gotValue();
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), var.visit(_lvtc));
      assertEquals("Should still be 1 error", 1, _lvtc._bob.errors.size());

      
      varData.setMav(_finalMav);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), var.visit(_lvtc));
      assertEquals("Should be 2 errors", 2, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "You cannot assign a new value to variable1 because it is immutable and has already been given a value", _lvtc._bob.errors.getLast().getFirst());
      varData.setMav(_publicMav);
      
      
      MethodData newContext = new MethodData("method", _publicStaticMav, new TypeParameter[0], SymbolData.INT_TYPE, new VariableData[0], new String[0], _sd1, new NullLiteral(SourceInfo.NO_INFO)); 
      _ta._data = newContext;
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), var.visit(_lvtc));
      assertEquals("Should be 3 errors", 3, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "Non static field or variable variable1 cannot be referenced from a static context", _lvtc._bob.errors.getLast().getFirst());
      _ta._data = _sd1;
      
      
      _ta._vars = new LinkedList<VariableData>();
      _sd1.setSuperClass(_sd2);
      _sd2.addVar(varData);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), var.visit(_lvtc));
      assertEquals("Should still be 3 errors", 3, _lvtc._bob.errors.size());

      
      SimpleNameReference className = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Frog"));
      SymbolData frog = new SymbolData("Frog");
      frog.setIsContinuation(false);
      _lvtc._bob.symbolTable.put("Frog", frog);
      
      
      TypeData result = className.visit(_lvtc);
      assertTrue("Result should be a PackageData since Frog is not accessible", result instanceof PackageData);
      assertEquals("Should have correct name", "Frog", result.getName());
      assertEquals("Should still be 3 errors", 3, _lvtc._bob.errors.size());
      
      
      frog.setMav(_publicMav);
      assertEquals("Should return Frog", frog, className.visit(_lvtc));
      assertEquals("Should still be 3 errors", 3, _lvtc._bob.errors.size());
      
      
      SimpleNameReference fake = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "notRealReference"));
      assertEquals("Should return package data", "notRealReference", (fake.visit(_lvtc)).getName());
      assertEquals("Should still be just 3 errors", 3, _lvtc._bob.errors.size());
      
      
      SimpleNameReference ambigRef = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "ambigThing"));

      SymbolData interfaceD = new SymbolData("interface");
      interfaceD.setIsContinuation(false);
      interfaceD.setInterface(true);
      interfaceD.setMav(_publicMav);
      
      SymbolData classD = new SymbolData("superClass");
      classD.setIsContinuation(false);
      classD.setMav(_publicMav);
      
      SymbolData ambigThingI = new SymbolData("ambigThing");
      ambigThingI.setIsContinuation(false);
      ambigThingI.setInterface(true);
      interfaceD.addInnerInterface(ambigThingI);
      ambigThingI.setOuterData(interfaceD);
      ambigThingI.setMav(_publicStaticMav);
      
      SymbolData ambigThingC = new SymbolData("ambigThing");
      ambigThingC.setIsContinuation(false);
      classD.addInnerClass(ambigThingC);
      ambigThingC.setOuterData(classD);
      ambigThingC.setMav(_publicStaticMav);
      
      _sd6.addInterface(interfaceD);
      _sd6.setSuperClass(classD);
      
      _sd6.setMav(_publicMav);
      _sd6.setIsContinuation(false);
      
      _ta._data = _sd6;

      assertEquals("Should return null", null, ambigRef.visit(_lvtc));
      assertEquals("Should be 4 errors", 4, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "Ambiguous reference to class or interface ambigThing", _lvtc._bob.errors.getLast().getFirst());    
    }
    
    
    public void testForComplexNameReference() {
      
      
      
      ComplexNameReference ref1 = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "java")), new Word(SourceInfo.NO_INFO, "lang"));
      assertEquals("Should return correct package data", "java.lang", ref1.visit(_lvtc).getName());
      assertEquals("Should be no errors", 0, _lvtc._bob.errors.size());
      
      
      ComplexNameReference ref2 = new ComplexNameReference(SourceInfo.NO_INFO, ref1, new Word(SourceInfo.NO_INFO, "String"));
      SymbolData string = new SymbolData("java.lang.String");
      string.setPackage("java.lang");
      string.setMav(_publicMav);
      string.setIsContinuation(false);
      _lvtc._bob.symbolTable.put("java.lang.String", string);
      
      assertEquals("Should return string", string, ref2.visit(_lvtc));

      assertEquals("Should still be no errors", 0, _lvtc._bob.errors.size());
      

      
      
      
      VariableData myVar = new VariableData("myVar", _publicStaticMav, SymbolData.DOUBLE_TYPE, true, string);
      string.addVar(myVar);
      ComplexNameReference varRef1 = new ComplexNameReference(SourceInfo.NO_INFO, ref2, new Word(SourceInfo.NO_INFO, "myVar"));
      
      
      assertEquals("Should return Double_Type instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef1.visit(_lvtc));
      assertEquals("There should still be no errors", 0, _lvtc._bob.errors.size());
      
      
      myVar.lostValue();
      assertEquals("Should return Double_Type instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef1.visit(_lvtc));
      assertEquals("There should be one error", 1, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "You cannot use myVar here, because it may not have been given a value", _lvtc._bob.errors.getLast().getFirst());

     
      
      myVar.setMav(_publicMav);
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef1.visit(_lvtc));
      assertEquals("Should be 2 errors", 2, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "Non-static variable myVar cannot be accessed from the static context java.lang.String.  Perhaps you meant to instantiate an instance of java.lang.String", _lvtc._bob.errors.getLast().getFirst());
      
      
      
      VariableData stringVar = new VariableData("s", _publicMav, string, true, _lvtc._bob._data);
      _ta._vars.add(stringVar);
      myVar.gotValue();
      ComplexNameReference varRef2 = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "s")), new Word(SourceInfo.NO_INFO, "myVar"));
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef2.visit(_lvtc));
      assertEquals("Should still just be 2 errors", 2, _lvtc._bob.errors.size());
      
      
      myVar.setMav(_finalPublicMav);
      myVar.gotValue();
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef2.visit(_lvtc));
      assertEquals("Should be 3 errors", 3, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "You cannot assign a new value to myVar because it is immutable and has already been given a value", _lvtc._bob.errors.getLast().getFirst());
      myVar.setMav(_publicMav);

      
      myVar.setMav(_publicMav);
      string.setVars(new LinkedList<VariableData>());
      string.setSuperClass(_sd2);
      _sd2.addVar(myVar);
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef2.visit(_lvtc));
      assertEquals("Should still be 3 errors", 3, _lvtc._bob.errors.size());

      
      VariableData vd1 = new VariableData("Mojo", _publicMav, SymbolData.INT_TYPE, true, _sd1);
      VariableData vd2 = new VariableData("Santa's Little Helper", _publicMav, _sd1, true, _sd2);
      VariableData vd3 = new VariableData("Snowball1", _publicMav, _sd2, true, _sd3);
      _sd3.addVar(vd3);
      _sd2.addVar(vd2);
      _sd1.addVar(vd1);
      
      ComplexNameReference varRef3 = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Snowball1")),
                                                        new Word(SourceInfo.NO_INFO, "Santa's Little Helper"));
      ComplexNameReference varRef4 = new ComplexNameReference(SourceInfo.NO_INFO, varRef3, new Word(SourceInfo.NO_INFO, "Mojo"));
      
      Data oldData = _lvtc._bob._data;
      _lvtc._bob._data = _sd3;
      _lvtc._bob._vars.add(vd3);

      TypeData result = varRef4.visit(_lvtc);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), result);
      assertEquals("Should still be 3 errors", 3, _lvtc._bob.errors.size());
      

      _lvtc._bob._data = oldData;
      

      
      SymbolData inner = new SymbolData("java.lang.String$Inner");
      inner.setPackage("java.lang");
      inner.setIsContinuation(false);
      inner.setOuterData(string);
      string.addInnerClass(inner);


      
      ComplexNameReference innerRef0 = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "s")), new Word(SourceInfo.NO_INFO, "Inner"));
      assertEquals("Should return null", null, innerRef0.visit(_lvtc));
      assertEquals("Should be 4 errors", 4, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "The class or interface java.lang.String.Inner is package protected because there is no access specifier and cannot be accessed from i.like.monkey", _lvtc._bob.errors.getLast().getFirst());

      inner.setMav(_publicMav);
      
      
      ComplexNameReference innerRef1 = new ComplexNameReference(SourceInfo.NO_INFO, ref2, new Word(SourceInfo.NO_INFO, "Inner"));
      assertEquals("Should return inner", inner, innerRef1.visit(_lvtc));
      assertEquals("Should be 5 errors", 5, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "Non-static inner class java.lang.String.Inner cannot be accessed from this context.  Perhaps you meant to instantiate it", _lvtc._bob.errors.getLast().getFirst());
  
      
      ComplexNameReference innerRef2 = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "s")), new Word(SourceInfo.NO_INFO, "Inner"));
      assertEquals("Should return inner", inner, innerRef2.visit(_lvtc));
      assertEquals("Should be 6 errors", 6, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "Non-static inner class java.lang.String.Inner cannot be accessed from this context.  Perhaps you meant to instantiate it", _lvtc._bob.errors.getLast().getFirst());

      
      inner.setMav(_publicStaticMav);
      assertEquals("Should return inner", inner, innerRef2.visit(_lvtc));
      assertEquals("Should be 7 errors", 7, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "You cannot reference the static inner class java.lang.String.Inner from an instance of java.lang.String", _lvtc._bob.errors.getLast().getFirst());
      
      
      
      ComplexNameReference noSense = new ComplexNameReference(SourceInfo.NO_INFO, ref2, new Word(SourceInfo.NO_INFO, "nonsense"));
      assertEquals("Should return null", null, noSense.visit(_lvtc));
      assertEquals("Should be 8 errors", 8, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "Could not resolve nonsense from the context of java.lang.String", _lvtc._bob.errors.getLast().getFirst());
    
    
      
      ComplexNameReference ambigRef = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "cebu")), new Word(SourceInfo.NO_INFO, "ambigThing"));

      SymbolData interfaceD = new SymbolData("interface");
      interfaceD.setIsContinuation(false);
      interfaceD.setInterface(true);
      interfaceD.setMav(_publicMav);
      
      SymbolData classD = new SymbolData("superClass");
      classD.setIsContinuation(false);
      classD.setMav(_publicMav);
      
      SymbolData ambigThingI = new SymbolData("ambigThing");
      ambigThingI.setIsContinuation(false);
      ambigThingI.setInterface(true);
      interfaceD.addInnerInterface(ambigThingI);
      ambigThingI.setOuterData(interfaceD);
      ambigThingI.setMav(_publicStaticMav);
      
      SymbolData ambigThingC = new SymbolData("ambigThing");
      ambigThingC.setIsContinuation(false);
      classD.addInnerClass(ambigThingC);
      ambigThingC.setOuterData(classD);
      ambigThingC.setMav(_publicStaticMav);
      
      _sd6.addInterface(interfaceD);
      _sd6.setSuperClass(classD);
      
      _lvtc._bob.symbolTable.put("cebu", _sd6);
      _sd6.setMav(_publicMav);
      _sd6.setIsContinuation(false);

      assertEquals("Should return null", null, ambigRef.visit(_lvtc));
      assertEquals("Should be 9 errors", 9, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "Ambiguous reference to class or interface ambigThing", _lvtc._bob.errors.getLast().getFirst());    
    
    }
      
    
    public void testForArrayAccess() {
      ArrayData intArray = 
        new ArrayData(SymbolData.INT_TYPE, 
                      new LanguageLevelVisitor(_lvtc._bob._file,
                                               _lvtc._bob._package, 
                                               _lvtc._bob._importedFiles, 
                                               _lvtc._bob._importedPackages, 
                                               new LinkedList<String>(), 
                                               new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>(), 
                                               new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>()),
                      SourceInfo.NO_INFO);
      VariableData variable1 = new VariableData("variable1", _publicMav, intArray, true, _ta._data);
      _ta._vars.add(variable1);
      
      VariableData intVar = new VariableData("intVar", _publicMav, SymbolData.INT_TYPE, true, _ta._data);
      _ta._vars.add(intVar);

      MethodData makeArray = new MethodData("makeArray", _privateMav, new TypeParameter[0], intArray, new VariableData[0], new String[0], _ta._data.getSymbolData(), new NullLiteral(SourceInfo.NO_INFO));
      _ta._data.getSymbolData().addMethod(makeArray);

      
      
      ArrayAccess a1 = new ArrayAccess(SourceInfo.NO_INFO, 
                                       new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "variable1")),
                                       new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "intVar")));

      assertEquals("should return int", SymbolData.INT_TYPE.getInstanceData(), a1.visit(_lvtc));
      assertEquals("Should be 0 errors", 0, _lvtc._bob.errors.size());
      
      
      
      
      ArrayAccess a2 = new ArrayAccess(SourceInfo.NO_INFO,
                                       new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "variable1")),
                                       new PlusExpression(SourceInfo.NO_INFO,
                                                          new IntegerLiteral(SourceInfo.NO_INFO, 12),
                                                          new IntegerLiteral(SourceInfo.NO_INFO, 22)));
                                                               
      assertEquals("should return int", SymbolData.INT_TYPE.getInstanceData(), a2.visit(_lvtc));
      assertEquals("Should be 0 errors", 0, _lvtc._bob.errors.size());
      
      
      
      ArrayAccess a3 = new ArrayAccess(SourceInfo.NO_INFO,
                                       new SimpleMethodInvocation(SourceInfo.NO_INFO,
                                                                  new Word(SourceInfo.NO_INFO, "makeArray"),
                                                                  new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0])),
                                       new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "intVar")));
      assertEquals("should return int", SymbolData.INT_TYPE.getInstanceData(), a3.visit(_lvtc));
      assertEquals("Should be 0 errors", 0, _lvtc._bob.errors.size());
    }
      
    public void testForParenthesized() {
      
      VariableData x = new VariableData("x", _publicMav, SymbolData.INT_TYPE, true, _ta._data);
      _ta._vars.add(x);
      
      
      Parenthesized p1 = new Parenthesized(SourceInfo.NO_INFO,
                                           new Parenthesized(SourceInfo.NO_INFO,
                                                             new Parenthesized(SourceInfo.NO_INFO,
                                                                              new SimpleNameReference(SourceInfo.NO_INFO,
                                                                                                      new Word(SourceInfo.NO_INFO, "x")))));
      
      assertEquals("should return int", SymbolData.INT_TYPE.getInstanceData(), p1.visit(_lvtc));
      assertEquals("Should be 0 errors", 0, _lvtc._bob.errors.size());
      
      
      Parenthesized p2 = new Parenthesized(SourceInfo.NO_INFO,
                                           new Parenthesized(SourceInfo.NO_INFO,
                                                             new IntegerLiteral(SourceInfo.NO_INFO, 1)));
      assertEquals("should return null", null, p2.visit(_lvtc));
      assertEquals("Should be 1 error", 1, _lvtc._bob.errors.size());
      assertEquals("Error message should be correct", "You cannot assign a value to an expression of this kind.  Values can only be assigned to fields or variables", 
                   _lvtc._bob.errors.getLast().getFirst());
      
    }
    
    
    
    
    
  }
}
