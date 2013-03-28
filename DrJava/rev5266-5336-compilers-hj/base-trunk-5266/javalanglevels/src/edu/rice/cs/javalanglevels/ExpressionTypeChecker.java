

package edu.rice.cs.javalanglevels;

import edu.rice.cs.javalanglevels.tree.*;
import edu.rice.cs.javalanglevels.parser.JExprParser;
import java.util.*;
import java.io.File;
import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.iter.*;

import junit.framework.TestCase;


public class ExpressionTypeChecker extends Bob {
  
  public final JavaVersion JAVA_VERSION = LanguageLevelConverter.OPT.javaVersion();
  
  
  public ExpressionTypeChecker(Data data, File file, String packageName, LinkedList<String> importedFiles, 
                               LinkedList<String> importedPackages, LinkedList<VariableData> vars, 
                               LinkedList<Pair<SymbolData, JExpression>> thrown) {
    super(data, file, packageName, importedFiles, importedPackages, vars, thrown);
    if (vars == null) throw new RuntimeException("vars == null in new ExpressionTypeChecker operation");
  }
  
  
  
  public TypeData forSimpleAssignmentExpression(SimpleAssignmentExpression that) {
    TypeData value_result = that.getValue().visit(this);
    TypeData name_result = that.getName().visit(new LValueTypeChecker(this));
    return forSimpleAssignmentExpressionOnly(that, name_result, value_result);
  }
  
  
  public TypeData forSimpleAssignmentExpressionOnly(SimpleAssignmentExpression that, TypeData name_result, 
                                                    TypeData value_result) {
    if (name_result == null || value_result == null) {return null;}
    
    
    if (!assertFound(name_result, that) || !assertFound(value_result, that)) {
      return null;
    }
    
    
    if (assertInstanceType(name_result, "You cannot assign a value to the type " + name_result.getName(), that) &&
        assertInstanceType(value_result, "You cannot use the type name " + value_result.getName() + 
                           " on the right hand side of an assignment", that)) {
      
      
      if (! value_result.getSymbolData().isAssignableTo(name_result.getSymbolData(), JAVA_VERSION)) {
        _addError("You cannot assign something of type " + value_result.getName() + " to something of type " + 
                  name_result.getName(), that);
      }
    }   
    return name_result.getInstanceData();
  }
  
  
  public TypeData forPlusAssignmentExpression(PlusAssignmentExpression that) {
    TypeData value_result = that.getValue().visit(this);
    TypeData name_result = that.getName().visit(new LValueWithValueTypeChecker(this));
    
    return forPlusAssignmentExpressionOnly(that, name_result, value_result);
  }
  
  
  public TypeData forPlusAssignmentExpressionOnly(PlusAssignmentExpression that, TypeData name_result, 
                                                  TypeData value_result) {
    if (name_result == null || value_result == null) {return null;}
    
    
    if (! assertFound(name_result, that) || !assertFound(value_result, that)) {
      return null;
    }
    
    
    SymbolData string = getSymbolData("java.lang.String", that, false, false);
    
    if (name_result.getSymbolData().isAssignableTo(string, JAVA_VERSION)) {
      
      assertInstanceType(name_result, "The arguments to a Plus Assignment Operator (+=) must both be instances, " + 
                         "but you have specified a type name", that);
      assertInstanceType(value_result, "The arguments to a Plus Assignment Operator (+=) must both be instances, " + 
                         "but you have specified a type name", that);
      return string.getInstanceData();
    }
    
    else { 
      if (!name_result.getSymbolData().isNumberType(JAVA_VERSION) ||
          !value_result.getSymbolData().isNumberType(JAVA_VERSION)) {
        _addError("The arguments to the Plus Assignment Operator (+=) must either include an instance of a String " + 
                  "or both be numbers.  You have specified arguments of type " + name_result.getName() + " and " + 
                  value_result.getName(), that);
        return string.getInstanceData(); 
      }
      
      else if (! value_result.getSymbolData().isAssignableTo(name_result.getSymbolData(), 
                                                             JAVA_VERSION)) {
        _addError("You cannot increment something of type " + name_result.getName() + " with something of type " + 
                  value_result.getName(), that);
      }
      
      else {
        assertInstanceType(name_result, "The arguments to the Plus Assignment Operator (+=) must both be instances, " + 
                           "but you have specified a type name", that);
        assertInstanceType(value_result, "The arguments to the Plus Assignment Operator (+=) must both be instances, " + 
                           "but you have specified a type name", that);
      }
      
      return name_result.getInstanceData();
    }
  }
  
  
  public TypeData forNumericAssignmentExpression(NumericAssignmentExpression that) {
    TypeData value_result = that.getValue().visit(this);
    TypeData name_result = that.getName().visit(new LValueWithValueTypeChecker(this));
    
    return forNumericAssignmentExpressionOnly(that, name_result, value_result);
  }
  
  
  public TypeData forMinusAssignmentExpression(MinusAssignmentExpression that) {
    return forNumericAssignmentExpression(that);
  }
  
  
  public TypeData forMultiplyAssignmentExpression(MultiplyAssignmentExpression that) {
    return forNumericAssignmentExpression(that);
  }
  
  
  public TypeData forDivideAssignmentExpression(DivideAssignmentExpression that) {
    return forNumericAssignmentExpression(that);
  }
  
   
  public TypeData forModAssignmentExpression(ModAssignmentExpression that) {
    return forNumericAssignmentExpression(that);
  }
  
  
  public TypeData forNumericAssignmentExpressionOnly(NumericAssignmentExpression that, TypeData name_result, 
                                                     TypeData value_result) {
    if (name_result == null || value_result == null) {return null;}
    
    
    if (!assertFound(name_result, that) || !assertFound(value_result, that)) {
      return null;
    }
    
    
    if (assertInstanceType(name_result, "You cannot use a numeric assignment (-=, %=, *=, /=) on the type " + 
                           name_result.getName(), that) &&
        assertInstanceType(value_result, "You cannot use the type name " + value_result.getName() + 
                           " on the left hand side of a numeric assignment (-=, %=, *=, /=)", that)) {
      
      boolean error = false;
      
      if (!name_result.getSymbolData().isNumberType(JAVA_VERSION)) {
        _addError("The left side of this expression is not a number.  " + 
                  "Therefore, you cannot apply a numeric assignment (-=, %=, *=, /=) to it", that);
        error=true;
      }
      if (!value_result.getSymbolData().isNumberType(JAVA_VERSION)) {
        _addError("The right side of this expression is not a number.  " + 
                  "Therefore, you cannot apply a numeric assignment (-=, %=, *=, /=) to it", that);
        error = true;
      }
      
      
      
      if (!error && !value_result.getSymbolData().isAssignableTo(name_result.getSymbolData(), 
                                                                 JAVA_VERSION)) {
        _addError("You cannot use a numeric assignment (-=, %=, *=, /=) on something of type " + name_result.getName() + 
                  " with something of type " + value_result.getName(), that);
      }
    }  
    return name_result.getInstanceData();  
  }
  
  
  public TypeData forShiftAssignmentExpressionOnly(ShiftAssignmentExpression that, TypeData name_result, 
                                                   TypeData value_result) {
    throw new RuntimeException ("Internal Program Error: Shift assignment operators are not supported.  " + 
                                "This should have been caught before the TypeChecker.  Please report this bug.");
  }
  
  
  public TypeData forBitwiseAssignmentExpressionOnly(BitwiseAssignmentExpression that, TypeData name_result, TypeData value_result) {
    throw new RuntimeException ("Internal Program Error: Bitwise assignment operators are not supported.  " + 
                                "This should have been caught before the TypeChecker.  Please report this bug.");
  }
  
  
  public TypeData forBooleanExpressionOnly(BooleanExpression that, TypeData left_result, TypeData right_result) {
    if (left_result == null || right_result == null)  return null;
    
    
    if (! assertFound(left_result, that) || ! assertFound(right_result, that)) return null;
    
    if (assertInstanceType(left_result, "The left side of this expression is a type, not an instance", that) &&
        !left_result.getSymbolData().isAssignableTo(SymbolData.BOOLEAN_TYPE, JAVA_VERSION)) {
      
      _addError("The left side of this expression is not a boolean value.  " + 
                "Therefore, you cannot apply a Boolean Operator (&&, ||) to it", that);
    }
    
    if (assertInstanceType(right_result, "The right side of this expression is a type, not an instance", that) &&
        ! right_result.getSymbolData().isAssignableTo(SymbolData.BOOLEAN_TYPE, JAVA_VERSION)) {
      
      _addError("The right side of this expression is not a boolean value.  " + 
                "Therefore, you cannot apply a Boolean Operator (&&, ||) to it", that);
    }
    
    
    return SymbolData.BOOLEAN_TYPE.getInstanceData();
  }
  
  
  public TypeData forBitwiseBinaryExpressionOnly(BitwiseBinaryExpression that, TypeData left_result, 
                                                 TypeData right_result) {
    throw new RuntimeException ("Internal Program Error: Bitwise operators are not supported.  " + 
                                "This should have been caught before the TypeChecker.  Please report this bug.");
  }
  
  
  public TypeData forEqualityExpressionOnly(EqualityExpression that, TypeData left_result, TypeData right_result) {
    if (left_result == null || right_result == null) return null;
    
    
    if (!assertFound(left_result, that) || !assertFound(right_result, that)) return null;
    
    
    SymbolData left = left_result.getSymbolData();
    SymbolData right = right_result.getSymbolData();
    if (left.isPrimitiveType() || right.isPrimitiveType()) {
      if (!((left.isNumberType(JAVA_VERSION) &&
             right.isNumberType(JAVA_VERSION)) ||
            (left.isAssignableTo(SymbolData.BOOLEAN_TYPE, JAVA_VERSION)
               && right.isAssignableTo(SymbolData.BOOLEAN_TYPE, JAVA_VERSION)))) {
        _addError("At least one of the arguments to this Equality Operator (==, !=) is primitive.  Therefore, they " + 
                  "must either both be number types or both be boolean types.  You have specified expressions with type " +
                  left_result.getName() + " and " + right_result.getName(), that);
      }
    }
    
    
    
    assertInstanceType(left_result, "The arguments to this Equality Operator(==, !=) must both be instances.  " +
                       "Instead, you have referenced a type name on the left side", that);
    assertInstanceType(right_result, "The arguments to this Equality Operator(==, !=) must both be instances.  " + 
                       "Instead, you have referenced a type name on the right side", that);
    
    return SymbolData.BOOLEAN_TYPE.getInstanceData();
  }
  
  
  public TypeData forComparisonExpressionOnly(ComparisonExpression that, TypeData left_result, TypeData right_result) {
    if (left_result == null || right_result == null) {return null;}
    
    
    if (!assertFound(left_result, that) || !assertFound(right_result, that)) return null;
    
    if (!left_result.getSymbolData().isNumberType(JAVA_VERSION)) {
      _addError("The left side of this expression is not a number.  Therefore, you cannot apply a Comparison Operator" +
                " (<, >; <=, >=) to it", that);
    }
    else {
      assertInstanceType(left_result, "The left side of this expression is a type, not an instance", that);
    }
    
    if (!right_result.getSymbolData().isNumberType(JAVA_VERSION)) {
      _addError("The right side of this expression is not a number.  Therefore, you cannot apply a Comparison Operator" +
                " (<, >; <=, >=) to it", that);
    }
    else {
      assertInstanceType(right_result, "The right side of this expression is a type, not an instance", that);
    }    
    
    return SymbolData.BOOLEAN_TYPE.getInstanceData();
  }
  
  
  public TypeData forShiftBinaryExpressionOnly(ShiftBinaryExpression that, TypeData left_result, TypeData right_result) {
    throw new RuntimeException ("Internal Program Error: BinaryShifts are not supported.  " + 
                                "This should have been caught before the TypeChecker.  Please report this bug.");
  }
  
  
  
  public TypeData forPlusExpressionOnly(PlusExpression that, TypeData left_result, TypeData right_result) {
    if (left_result == null || right_result == null) {return null;}
    
    
    if (!assertFound(left_result, that) || !assertFound(right_result, that)) {
      return null;
    }
    
    SymbolData string = getSymbolData("java.lang.String", that, false, false);
    
    if (left_result.getSymbolData().isAssignableTo(string, JAVA_VERSION) ||
        right_result.getSymbolData().isAssignableTo(string, JAVA_VERSION)) {
      
      assertInstanceType(left_result, "The arguments to the Plus Operator (+) must both be instances, " + 
                         "but you have specified a type name", that);
      assertInstanceType(right_result, "The arguments to the Plus Operator (+) must both be instances, " + 
                         "but you have specified a type name", that);
      return string.getInstanceData();
    }
    
    else { 
      if (!left_result.getSymbolData().isNumberType(JAVA_VERSION) ||
          !right_result.getSymbolData().isNumberType(JAVA_VERSION)) {
        _addError("The arguments to the Plus Operator (+) must either include an instance of a String or both be" + 
                  " numbers.  You have specified arguments of type " + left_result.getName() + " and " + 
                  right_result.getName(), that);
        return string.getInstanceData(); 
      }
      else {
        assertInstanceType(left_result, "The arguments to the Plus Operator (+) must both be instances, but you have" + 
                           " specified a type name", that);
        assertInstanceType(right_result, "The arguments to the Plus Operator (+) must both be instances, but you have" + 
                           " specified a type name", that);
      }
      
      return _getLeastRestrictiveType(left_result.getSymbolData(), right_result.getSymbolData()).getInstanceData();
      
    }
  }
  
  
  public TypeData forNumericBinaryExpressionOnly(NumericBinaryExpression that, TypeData left_result, TypeData right_result) {
    if (left_result == null || right_result == null) {return null;}
    
    
    if (!assertFound(left_result, that) || !assertFound(right_result, that)) {
      return null;
    }
    
    if (assertInstanceType(left_result, "The left side of this expression is a type, not an instance", that) &&
        !left_result.getSymbolData().isNumberType(JAVA_VERSION)) {
      
      _addError("The left side of this expression is not a number.  Therefore, you cannot apply a Numeric Binary" + 
                " Operator (*, /, -, %) to it", that);
      return right_result.getInstanceData();
    }
    
    if (assertInstanceType(right_result, "The right side of this expression is a type, not an instance", that) &&
        !right_result.getSymbolData().isNumberType(JAVA_VERSION)) {
      
      _addError("The right side of this expression is not a number.  Therefore, you cannot apply a Numeric Binary " + 
                "Operator (*, /, -, %) to it", that);
      return left_result.getInstanceData();
    }
    
    
    return _getLeastRestrictiveType(left_result.getSymbolData(), right_result.getSymbolData()).getInstanceData();
  }
  
  
  public TypeData forNoOpExpressionOnly(NoOpExpression that, TypeData left_result, TypeData right_result) {
    throw new RuntimeException("Internal Program Error: The student is missing an operator.  " + 
                               "This should have been caught before the TypeChecker.  Please report this bug.");
  }
  
  
  
  public TypeData forIncrementExpression(IncrementExpression that) {
    TypeData value_result = that.getValue().visit(new LValueWithValueTypeChecker(this));
    return forIncrementExpressionOnly(that, value_result);
  }
  
  
  
  public TypeData forPositivePrefixIncrementExpression(PositivePrefixIncrementExpression that) {
    return forIncrementExpression(that);
  }
  
  public TypeData forNegativePrefixIncrementExpression(NegativePrefixIncrementExpression that) {
    return forIncrementExpression(that);
  }
  
  public TypeData forPositivePostfixIncrementExpression(PositivePostfixIncrementExpression that) {
    return forIncrementExpression(that);
  }
  
  public TypeData forNegativePostfixIncrementExpression(NegativePostfixIncrementExpression that) {
    return forIncrementExpression(that);
  }
  
  
  
  public TypeData forIncrementExpressionOnly(IncrementExpression that, TypeData value_result) {
    if (value_result == null) {return null;}
    
    
    if (!assertFound(value_result, that)) {
      return null;
    }
    
    if (assertInstanceType(value_result, "You cannot increment or decrement " + value_result.getName() + 
                           ", because it is a class name not an instance", that)) {
      if (!value_result.getSymbolData().isNumberType(JAVA_VERSION)) {
        _addError("You cannot increment or decrement something that is not a number type." + 
                  "  You have specified something of type " + value_result.getName(), that);
      }
    }
    return value_result.getInstanceData();
  }
  
  
  public TypeData forNumericUnaryExpressionOnly(NumericUnaryExpression that, TypeData value_result) {
    if (value_result==null) {return null;}
    
    
    if (!assertFound(value_result, that)) {
      return null;
    }
    
    if (assertInstanceType(value_result, "You cannot use a numeric unary operator (+, -) with " + value_result.getName() + 
                           ", because it is a class name, not an instance", that) &&
        !value_result.getSymbolData().isNumberType(JAVA_VERSION)) {
      
      _addError("You cannot apply this unary operator to something of type " + value_result.getName() + 
                ".  You can only apply it to a numeric type such as double, int, or char", that);
      return value_result;
    }
    
    
    return _getLeastRestrictiveType(value_result.getSymbolData(), SymbolData.INT_TYPE).getInstanceData();
  }
  
  
  public TypeData forBitwiseNotExpressionOnly(BitwiseNotExpression that, TypeData value_result) {
    throw new RuntimeException("Internal Program Error: BitwiseNot is not supported.  " + 
                               "It should have been caught before getting to the TypeChecker.  Please report this bug.");
  }
  
  
  
  public TypeData forNotExpressionOnly(NotExpression that, TypeData value_result) {
    if (value_result == null) {return null;}
    
    
    if (!assertFound(value_result, that)) {
      return null;
    }
    
    if (assertInstanceType(value_result, 
                           "You cannot use the not (!) operator with " + value_result.getName() + 
                           ", because it is a class name, not an instance", that) &&
        ! value_result.getSymbolData().isAssignableTo(SymbolData.BOOLEAN_TYPE, 
                                                      JAVA_VERSION)) {
      
      _addError("You cannot use the not (!) operator with something of type " + value_result.getName() + 
                ". Instead, it should be used with an expression of boolean type", that);
    }
    
    return SymbolData.BOOLEAN_TYPE.getInstanceData(); 
    
  }
  
  
  public TypeData forConditionalExpressionOnly(ConditionalExpression that, TypeData condition_result, 
                                               TypeData forTrue_result, TypeData forFalse_result) {
    throw new RuntimeException ("Internal Program Error: Conditional expressions are not supported.  " + 
                                "This should have been caught before the TypeChecker.  Please report this bug.");
  }
  
  
  public TypeData forInstanceofExpressionOnly(InstanceofExpression that, TypeData type_result, TypeData value_result) {
    if (type_result == null)  return null; 
    
    
    if (! assertFound(value_result, that) || ! assertFound(type_result, that)) return null;
    
    if (type_result.isInstanceType()) {
      _addError("You are trying to test if an expression value belongs to an instance of a type, which is not allowed."
                  + "  Perhaps you meant to check membership in the type itself, " + type_result.getName(),
                that);
    }
    
    else if (assertInstanceType(value_result, "You are trying to test if " + value_result.getName() + 
                                " belongs to type, but it is a class or interface type, not an instance", that) 
               && ! value_result.getSymbolData().isCastableTo(type_result.getSymbolData(), JAVA_VERSION)) {
      
      _addError("You cannot test whether an expression of type " + value_result.getName() + " belongs to type "
                  + type_result.getName() + " because they are not related", 
                that);
    }
    
    return SymbolData.BOOLEAN_TYPE.getInstanceData();
  }
  
  
  public TypeData forCastExpressionOnly(CastExpression that, TypeData type_result, TypeData value_result) {
    if (type_result == null || value_result == null)  return null; 
    
    
    if (! assertFound(value_result, that) || ! assertFound(type_result, that)) return null;
    
    if (type_result.isInstanceType()) {
      _addError("You are trying to cast to an instance of a type, which is not allowed.  " + 
                "Perhaps you meant to cast to the type itself, " + type_result.getName(), that);
    }
    
    else if (assertInstanceType(value_result, "You are trying to cast " + value_result.getName() + 
                                ", which is a class or interface type, not an instance", that) &&
             !value_result.getSymbolData().isCastableTo(type_result.getSymbolData(), 
                                                        JAVA_VERSION)) {
      
      _addError("You cannot cast an expression of type " + value_result.getName() + " to type " + 
                type_result.getName() + " because they are not related", that);
    }
    
    return type_result.getInstanceData();
  }
  
  
  
  public TypeData forEmptyExpressionOnly(EmptyExpression that) {
    throw new RuntimeException("Internal Program Error: EmptyExpression encountered.  Student is missing something." + 
                               "  Should have been caught before TypeChecker.  Please report this bug.");
  }
  
  
  public InstanceData classInstantiationHelper(ClassInstantiation that, SymbolData classToInstantiate) {
    if (classToInstantiate == null) {return null;}
    Expression[] expr = that.getArguments().getExpressions();
    InstanceData[] args = new InstanceData[expr.length];
    for (int i = 0; i<expr.length; i++) {
      Expression e = expr[i];
      TypeData type = e.visit(this);
      if (type == null || !assertFound(type, expr[i]) || 
          ! assertInstanceType(type, "Cannot pass a class or interface name as a constructor argument", e)) {
        
        return classToInstantiate.getInstanceData();
      }
      args[i] = type.getInstanceData();
    }
    
    MethodData md = 
      _lookupMethod(LanguageLevelVisitor.getUnqualifiedClassName(that.getType().getName()), classToInstantiate, args, 
                    that, "No constructor found in class " + Data.dollarSignsToDots(classToInstantiate.getName()) + 
                    " with signature: ", true, _getData().getSymbolData());
    
    if (md == null) {return classToInstantiate.getInstanceData();}
    
    
    String[] thrown = md.getThrown();
    for (int i = 0; i<thrown.length; i++) {
      _thrown.addLast(new Pair<SymbolData, JExpression>(getSymbolData(thrown[i], _getData(), that), that));
    }
    
    return classToInstantiate.getInstanceData();
  }
  
  
  
  
  
  public TypeData forSimpleNamedClassInstantiation(SimpleNamedClassInstantiation that) {
    SymbolData type = getSymbolData(that.getType().getName(), _getData(), that);
    if (type == null) {return null;}
    
    
    String name = that.getType().getName();
    int lastIndexOfDot = name.lastIndexOf(".");
    if (!type.hasModifier("static") && (type.getOuterData() != null) && lastIndexOfDot != -1) {
      String firstPart = name.substring(0, lastIndexOfDot);
      String secondPart = name.substring(lastIndexOfDot + 1, name.length()); 
      _addError(Data.dollarSignsToDots(type.getName()) + " is not a static inner class, and thus cannot be " + 
                "instantiated from this context.  Perhaps you meant to use an instantiation of the form new " + 
                firstPart + "().new " + secondPart + "()", that);
    }
    InstanceData result = classInstantiationHelper(that, type);
    if (result != null && result.getSymbolData().hasModifier("abstract")) {
      _addError(Data.dollarSignsToDots(type.getName()) + " is abstract and thus cannot be instantiated", that);
    }
    return result;
  }
  
  
  public TypeData forComplexNamedClassInstantiation(ComplexNamedClassInstantiation that) {
    TypeData enclosingType = that.getEnclosing().visit(this);
    if ((enclosingType == null) || ! assertFound(enclosingType, that.getEnclosing())) { return null; }
    
    else {
      
      checkAccessibility(that, enclosingType.getSymbolData().getMav(), enclosingType.getSymbolData().getName(), 
                         enclosingType.getSymbolData(), _data.getSymbolData(), "class or interface", true);
      
      
      
      
      SymbolData innerClass = getSymbolData(that.getType().getName(), enclosingType.getSymbolData(), that.getType());
      if (innerClass == null) {return null;}
      
      
      checkAccessibility(that, innerClass.getMav(), innerClass.getName(), innerClass, _data.getSymbolData(), 
                         "class or interface", true);
      InstanceData result = classInstantiationHelper(that, innerClass);
      if (result == null) {return null;}
      boolean resultIsStatic = result.getSymbolData().hasModifier("static");
      
      if (!enclosingType.isInstanceType() && !resultIsStatic) {
        _addError ("The constructor of a non-static inner class can only be called on an instance of its" + 
                   " containing class (e.g. new " + Data.dollarSignsToDots(enclosingType.getName()) + "().new " +
                   that.getType().getName() + "())", that);
      }
      else if (resultIsStatic) {
        _addError("You cannot instantiate a static inner class or interface with this syntax.  Instead, try new " + 
                  Data.dollarSignsToDots(result.getName()) + "()", that);
      }
      
      if (result.getSymbolData().hasModifier("abstract")) {
        _addError(Data.dollarSignsToDots(result.getName()) + " is abstract and thus cannot be instantiated", that);
      }
      return result;
    }
  }
  
  
  
  public SymbolData handleAnonymousClassInstantiation(AnonymousClassInstantiation that, SymbolData superC) {
    SymbolData sd = _data.getNextAnonymousInnerClass();
    if (sd == null) {
      _addError("Nested anonymous classes are not supported at any language lavel", that);
      return sd;


    }
    if (sd.getSuperClass() == null) {
      if (superC == null) {
        throw new RuntimeException("Internal Program Error:  Superclass data for " + sd + " is null." + 
                                   "  Please report this bug.");
      }
      if (superC.isInterface()) {
        sd.setSuperClass(symbolTable.get("java.lang.Object")); 
        sd.addInterface(superC);
      }
      else { sd.setSuperClass(superC);}
    }
    LanguageLevelVisitor.createAccessors(sd, _file);
    
    return sd;
  }
  
  
  public TypeData forSimpleAnonymousClassInstantiation(SimpleAnonymousClassInstantiation that) {
    if (_data.isDoublyAnonymous()) {
      _addError(_data + "is a nested anonymous class, which is not supported at any language level", that);
      return null;
    }
    
    final SymbolData superclass_result = getSymbolData(that.getType().getName(), _data, that); 
    
    
    SymbolData myData = handleAnonymousClassInstantiation(that, superclass_result);
    if (myData == null) return null;
    
    
    
    String name = that.getType().getName();
    int lastIndexOfDot = name.lastIndexOf(".");
    if (!superclass_result.hasModifier("static") && !superclass_result.isInterface() && 
        (superclass_result.getOuterData() != null) && lastIndexOfDot != -1) {
      String firstPart = name.substring(0, lastIndexOfDot);
      String secondPart = name.substring(lastIndexOfDot + 1, name.length());
      _addError(Data.dollarSignsToDots(superclass_result.getName()) + 
                " is not a static inner class, and thus cannot be instantiated from this context." + 
                "  Perhaps you meant to use an instantiation of the form new " + Data.dollarSignsToDots(firstPart) + 
                "().new " + Data.dollarSignsToDots(secondPart) + "()", that);
    }
    
    
    
    if (superclass_result.isInterface()) {
      Expression[] expr = that.getArguments().getExpressions();
      if (expr.length > 0) { 
        _addError("You are creating an anonymous inner class that directly implements an interface, thus you should" + 
                  " use the Object constructor which takes in no arguments.  However, you have specified " + 
                  expr.length + " arguments", that);}
    }
    
    else classInstantiationHelper(that, superclass_result); 
    
    
    
    LinkedList<VariableData> vars = cloneVariableDataList(_vars);
    vars.addAll(myData.getVars());
    final TypeData body_result = that.getBody().visit(new ClassBodyTypeChecker(myData, _file, _package, _importedFiles, 
                                                                               _importedPackages, vars, _thrown));
    
    
    _checkAbstractMethods(myData, that);
    return myData.getInstanceData();  
  }
  
  
  public TypeData forComplexAnonymousClassInstantiation(ComplexAnonymousClassInstantiation that) {
    if (_data.isDoublyAnonymous()) {
      _addError(_data + "is a nested anonymous class, which is not supported at any language level", that);
      return null;
    }
    TypeData enclosingType = that.getEnclosing().visit(this);
    if ((enclosingType == null) || ! assertFound(enclosingType, that.getEnclosing())) { return null; }
    
    
    checkAccessibility(that, enclosingType.getSymbolData().getMav(), enclosingType.getSymbolData().getName(), 
                       enclosingType.getSymbolData(), _data.getSymbolData(), "class or interface", true);
    
    final SymbolData superclass_result = getSymbolData(that.getType().getName(), enclosingType.getSymbolData(), 
                                                       that.getType());
    
    
    
    
    SymbolData myData = handleAnonymousClassInstantiation(that, superclass_result);
    if (myData == null) return null;
    
    
    
    
    boolean resultIsStatic;
    
    if (superclass_result.isInterface()) {
      Expression[] expr = that.getArguments().getExpressions();
      if (expr.length > 0) { 
        _addError("You are creating an anonymous inner class that directly implements an interface, thus you should" + 
                  " use the Object constructor which takes in no arguments.  However, you have specified " + 
                  expr.length + " arguments", that);
      }
      resultIsStatic = true;
    }
    
    
    else { 
      InstanceData result = classInstantiationHelper(that, superclass_result); 
      if (result == null) {return null;}
      
      resultIsStatic = result.getSymbolData().hasModifier("static");
    }
    
    if (!enclosingType.isInstanceType() && !resultIsStatic) {
      _addError ("The constructor of a non-static inner class can only be called on an instance of its containing" + 
                 " class (e.g. new " + Data.dollarSignsToDots(enclosingType.getName()) + "().new " + 
                 that.getType().getName() + "())", that);
    }
    
    else if (enclosingType.isInstanceType() && resultIsStatic) {
      _addError("You cannot instantiate a static inner class or interface with this syntax.  Instead, try new " + 
                Data.dollarSignsToDots(superclass_result.getName()) + "()", that);
    }
    
    
    LinkedList<VariableData> vars = cloneVariableDataList(_vars);
    vars.addAll(myData.getVars());
    
    final TypeData body_result = that.getBody().visit(new ClassBodyTypeChecker(myData, _file, _package, _importedFiles, 
                                                                               _importedPackages, vars, _thrown));
    
    
    _checkAbstractMethods(myData, that);
    
    return myData.getInstanceData(); 
  }
  
  
  
  public TypeData forSimpleThisConstructorInvocation(SimpleThisConstructorInvocation that) {
    _addError("This constructor invocations are only allowed as the first statement of a constructor body", that);
    return null;
  }
  
  
  public TypeData forComplexThisConstructorInvocation(ComplexThisConstructorInvocation that) {
    _addError("Constructor invocations of this form are never allowed", that);
    return null;
  }
  
  
  public TypeData forSimpleNameReference(SimpleNameReference that) {
    Word myWord = that.getName();
    myWord.visit(this);
    
    
    
    VariableData reference = getFieldOrVariable(myWord.getText(), _data, _data.getSymbolData(), that, _vars, true, true);
    if (reference != null) {
      if (! reference.hasValue()) {
        _addError("You cannot use " + reference.getName() + " because it may not have been given a value", that.getName());
      }
      
      
      if (inStaticMethod() && ! reference.hasModifier("static")  && ! reference.isLocalVariable()) {
        _addError("Non-static variable or field " + reference.getName() + " cannot be referenced from a static context", that);
      }
      
      return reference.getType().getInstanceData();  
    }
    
    
    SymbolData classR = findClassReference(null, myWord.getText(), that);
    if (classR != null && classR != SymbolData.AMBIGUOUS_REFERENCE) {
      
      if (checkAccessibility(that, classR.getMav(), classR.getName(), classR, _data.getSymbolData(), 
                             "class or interface", false)) {
        return classR;
      }
    }
    if (classR == SymbolData.AMBIGUOUS_REFERENCE) {return null;}
    
    PackageData packageD = new PackageData(myWord.getText());
    return packageD;
  }
  
  
  
  public TypeData forComplexNameReference(ComplexNameReference that) {
    TypeData lhs = that.getEnclosing().visit(this);
    if (lhs == null) return null;   
    
    Word myWord = that.getName();
    
    
    if (lhs instanceof PackageData) {
      SymbolData classRef =  findClassReference(lhs, myWord.getText(), that);
      if (classRef != null) { return classRef; }
      return new PackageData((PackageData) lhs, myWord.getText());
    }
    if (_data == null) return null;  
    checkAccessibility(that, lhs.getSymbolData().getMav(), lhs.getSymbolData().getName(), lhs.getSymbolData(), 
                       _data.getSymbolData(), "class or interface", true);
    
    
    VariableData reference = getFieldOrVariable(myWord.getText(), lhs.getSymbolData(), _data.getSymbolData(), that);
    if (reference != null) {
      if (lhs instanceof SymbolData) {
        
        if (! reference.hasModifier("static")) {
          _addError("Non-static variable " + reference.getName() + " cannot be accessed from the static context " + 
                    Data.dollarSignsToDots(lhs.getName()) + ".  Perhaps you meant to instantiate an instance of " + 
                    Data.dollarSignsToDots(lhs.getName()), that);
          return reference.getType().getInstanceData();
        }
      }
      
      
      if (!reference.hasValue()) {
        _addError("You cannot use " + reference.getName() + " here, because it may not have been given a value", 
                  that.getName());
      }
      
      return reference.getType().getInstanceData();
    }
    
    
    SymbolData sd = getSymbolData(true, myWord.getText(), lhs.getSymbolData(), that, false); 
    if (sd != null && sd != SymbolData.AMBIGUOUS_REFERENCE) {
      if (!checkAccessibility(that, sd.getMav(), sd.getName(), sd, _data.getSymbolData(), "class or interface")) {
        return null;
      }
      if (!sd.hasModifier("static")) {
        _addError("Non-static inner class " + Data.dollarSignsToDots(sd.getName()) + 
                  " cannot be accessed from this context.  Perhaps you meant to instantiate it", that);
      }
      
      
      else if (lhs instanceof InstanceData) {
        _addError("You cannot reference the static inner class " + Data.dollarSignsToDots(sd.getName()) + 
                  " from an instance of " + Data.dollarSignsToDots(lhs.getName()) + ".  Perhaps you meant to say " 
                    + Data.dollarSignsToDots(sd.getName()), that);
      }
      return sd;
    }
    
    if (sd != SymbolData.AMBIGUOUS_REFERENCE) { 
      _addError("Could not resolve " + myWord.getText() + " from the context of " + Data.dollarSignsToDots(lhs.getName()),
                that);
    }
    return null;
  }
  
  
  
  public TypeData forSimpleThisReference(SimpleThisReference that) {
    if (inStaticMethod()) {
      _addError("'this' cannot be referenced from within a static method", that);
    }
    return _getData().getSymbolData().getInstanceData();
  }
  
  
  public TypeData forComplexThisReferenceOnly(ComplexThisReference that, TypeData enclosing_result) {
    
    if ((enclosing_result == null) || ! assertFound(enclosing_result, that.getEnclosing())) { return null; }
    
    if (inStaticMethod()) {
      _addError("'this' cannot be referenced from within a static method", that);
    }
    
    if (enclosing_result.isInstanceType()) {
      _addError("'this' can only be referenced from a type name, but you have specified an instance of that type.", that);
    }
    
    SymbolData myData = _getData().getSymbolData();
    if (!myData.isInnerClassOf(enclosing_result.getSymbolData(), true)) {
      
      if (myData.isInnerClassOf(enclosing_result.getSymbolData(), false)) {
        _addError("You cannot reference " + enclosing_result.getName() + ".this from here, because " + myData.getName() + 
                  " or one of its enclosing classes " +
                  "is static.  Thus, an enclosing instance of " + enclosing_result.getName() + " does not exist", that);
      }
      else {
        _addError("You cannot reference " + enclosing_result.getName() + ".this from here, because " + enclosing_result.getName() + 
                  " is not an outer class of " + myData.getName(), that);
      }
    }
    
    return enclosing_result.getInstanceData();
  }
  
  
  public TypeData forSimpleSuperReference(SimpleSuperReference that) {
    if (inStaticMethod()) {
      _addError("'super' cannot be referenced from within a static method", that);
    }
    SymbolData superClass = _getData().getSymbolData().getSuperClass();
    if (superClass == null) {  
      _addError("The class " + _getData().getSymbolData().getName() + " does not have a super class", that);
      return null;
    }
    return superClass.getInstanceData();
  }
  
  
  public TypeData forComplexSuperReferenceOnly(ComplexSuperReference that, TypeData enclosing_result) {
    
    if ((enclosing_result == null) || ! assertFound(enclosing_result, that.getEnclosing())) { return null; }
    
    if (inStaticMethod()) {
      _addError("'super' cannot be referenced from within a static method", that);
    }
    if (enclosing_result.isInstanceType()) {
      _addError("'super' can only be referenced from a type name, but you have specified an instance of that type.", that);
    }
    
    SymbolData myData = _getData().getSymbolData();
    if (!myData.isInnerClassOf(enclosing_result.getSymbolData(), true)) {
      
      if (myData.isInnerClassOf(enclosing_result.getSymbolData(), false)) {
        _addError("You cannot reference " + enclosing_result.getName() + ".super from here, because " + myData.getName() + 
                  " or one of its enclosing classes " +
                  "is static.  Thus, an enclosing instance of " + enclosing_result.getName() + " does not exist", that);
      }
      else {
        _addError("You cannot reference " + enclosing_result.getName() + ".super from here, because " + 
                  enclosing_result.getName() + " is not an outer class of " + myData.getName(), that);
      }
    }
    
    SymbolData superClass = enclosing_result.getSymbolData().getSuperClass();
    if (superClass == null) {  
      _addError("The class " + enclosing_result.getName() + " does not have a super class", that);
      return null;
    }
    
    return superClass.getInstanceData();
  }
  
  
  
  
  
  public TypeData forArrayAccessOnly(ArrayAccess that, TypeData lhs, TypeData index) {
    
    if (lhs == null || index == null) {return null;}
    
    
    if (!assertFound(lhs, that) || !assertFound(index, that)) {
      return null;
    }
    
    if (assertInstanceType(lhs, "You cannot access an array element of a type name", that) &&
        ! (lhs.getSymbolData() instanceof ArrayData)) {
      _addError("The variable referred to by this array access is a " + lhs.getSymbolData().getName() + ", not an array",
                that);
      return lhs.getInstanceData();
    }
    
    if (assertInstanceType(index, "You have used a type name in place of an array index", that) &&
        !index.getSymbolData().isAssignableTo(SymbolData.INT_TYPE, JAVA_VERSION)) {
      _addError("You cannot reference an array element with an index of type " + index.getSymbolData().getName() + 
                ".  Instead, you must use an int", that);
      
    }
    
    return ((ArrayData)lhs.getSymbolData()).getElementType().getInstanceData();
    
    
  }
  
  
  
  public TypeData forStringLiteralOnly(StringLiteral that) {
    return symbolTable.get("java.lang.String").getInstanceData();
  }
  
  public TypeData forIntegerLiteralOnly(IntegerLiteral that) {
    return SymbolData.INT_TYPE.getInstanceData();
  }
  
  public TypeData forLongLiteralOnly(LongLiteral that) {
    return SymbolData.LONG_TYPE.getInstanceData();
  }
  
  public TypeData forFloatLiteralOnly(FloatLiteral that) {
    return SymbolData.FLOAT_TYPE.getInstanceData();
  }
  
  public TypeData forDoubleLiteralOnly(DoubleLiteral that) {
    return SymbolData.DOUBLE_TYPE.getInstanceData();
  }
  
  public TypeData forCharLiteralOnly(CharLiteral that) {
    return SymbolData.CHAR_TYPE.getInstanceData();
  }
  
  public TypeData forBooleanLiteralOnly(BooleanLiteral that) {
    return SymbolData.BOOLEAN_TYPE.getInstanceData();
  }
  
  public TypeData forNullLiteralOnly(NullLiteral that) {
    return SymbolData.NULL_TYPE.getInstanceData();
  }
  
  public TypeData forClassLiteralOnly(ClassLiteral that) {
    return symbolTable.get("java.lang.Class").getInstanceData();
  }
  
  
  
  public TypeData forParenthesizedOnly(Parenthesized that, TypeData value_result) {
    if (value_result == null) {return null;}
    
    if (!assertFound(value_result, that.getValue())) {
      return null;
    }
    
    assertInstanceType(value_result, "This class or interface name cannot appear in parentheses", that);
    return value_result.getInstanceData();
  }
  
  
  public TypeData methodInvocationHelper(MethodInvocation that, TypeData context) {
    Expression[] exprs = that.getArguments().getExpressions();
    TypeData[] args = new TypeData[exprs.length];
    InstanceData[] newArgs = new InstanceData[exprs.length];
    for (int i = 0; i < exprs.length; i++) {
      args[i] = exprs[i].visit(this);
      if (args[i] == null) {
        return null;
      }
      
      if (! assertFound(args[i], that)) return null;
      if (! args[i].isInstanceType()) {
        _addError("Cannot pass a class or interface name as an argument to a method." +
                  "  Perhaps you meant to create an instance or use " + args[i].getName() + ".class", exprs[i]);
      }
      newArgs[i]=args[i].getInstanceData();
      
    }
    
    
    
    MethodData md = _lookupMethod(that.getName().getText(), context.getSymbolData(), newArgs, that, 
                                  "No method found in class " + context.getName() + " with signature: ", 
                                  false, _getData().getSymbolData());
    
    if (md == null)  return null;
    
    if (! context.isInstanceType() && ! md.hasModifier("static")) {
      _addError("Cannot access the non-static method " + md.getName() + " from a static context", that);
    }
    
    
    String[] thrown = md.getThrown();
    for (int i = 0; i<thrown.length; i++) {
      _thrown.addLast(new Pair<SymbolData, JExpression>(getSymbolData(thrown[i], _getData(), that), that));
    }
    
    SymbolData returnType = md.getReturnType();
    if (returnType == null) {
      _addError("Internal error: returnType is null", that);
      return null;
    }
    
    return returnType.getInstanceData();
  }
  
  
  
  public TypeData forSimpleMethodInvocation(SimpleMethodInvocation that) {
    TypeData context = _getData().getSymbolData().getInstanceData();
    if (inStaticMethod()) context = context.getSymbolData();  
    return methodInvocationHelper(that, context);
  }
  
  
  
  public TypeData forComplexMethodInvocation(ComplexMethodInvocation that) {
    TypeData context = that.getEnclosing().visit(this);
    if (! assertFound(context, that.getEnclosing()) || context == null)  return null;
    
    
    checkAccessibility(that, context.getSymbolData().getMav(), context.getSymbolData().getName(), 
                       context.getSymbolData(), _data.getSymbolData(), "class or interface", true);
    
    
    

    return methodInvocationHelper(that, context);
  }
  
  
  
  protected boolean canBeAssigned(VariableData vd) {
    return ! vd.isFinal() || ! vd.hasValue();
  }
  
  
  
  
  protected SymbolData _getLeastRestrictiveType(SymbolData sd1, SymbolData sd2) {
    if ((sd1.isDoubleType(JAVA_VERSION) &&
         sd2.isNumberType(JAVA_VERSION)) ||
        (sd2.isDoubleType(JAVA_VERSION) &&
         sd1.isNumberType(JAVA_VERSION))) {
      return SymbolData.DOUBLE_TYPE;
    }
    else if ((sd1.isFloatType(JAVA_VERSION) &&
              sd2.isNumberType(JAVA_VERSION)) ||
             (sd2.isFloatType(JAVA_VERSION) &&
              sd1.isNumberType(JAVA_VERSION))) {
      return SymbolData.FLOAT_TYPE;
    }
    else if ((sd1.isLongType(JAVA_VERSION) &&
              sd2.isNumberType(JAVA_VERSION)) ||
             (sd2.isLongType(JAVA_VERSION) &&
              sd1.isNumberType(JAVA_VERSION))) {
      return SymbolData.LONG_TYPE;
    }
    else if (sd1.isBooleanType(JAVA_VERSION) &&
             sd2.isBooleanType(JAVA_VERSION)) {
      return SymbolData.BOOLEAN_TYPE;
    }
    else return SymbolData.INT_TYPE; 
  }
  
  
  
  
  
  public TypeData forConditionalExpression(ConditionalExpression that) {
    throw new RuntimeException("Internal Program Error: Conditional expressions are not supported.  This should have been caught before the Type Checker.  Please report this bug.");
  }
  
  
  public TypeData forInstanceofExpression(InstanceofExpression that) {
    
    final SymbolData type_result = getSymbolData(that.getType().getName(), _data.getSymbolData(), that.getType(), false);
    final TypeData value_result = that.getValue().visit(this);
    
    if (type_result == null) {
      _addError(that.getType().getName()
                  + " cannot appear as the type of a instanceof expression because it is not a valid type", 
                that.getType());
      return null;
    }
    
    if (! assertFound(value_result, that.getValue())) {
      
      
      return SymbolData.BOOLEAN_TYPE.getInstanceData();
    }
    
    
    return forInstanceofExpressionOnly(that, type_result, value_result);
  }
  
  
  
  
  public TypeData forCastExpression(CastExpression that) {
    
    final SymbolData type_result = getSymbolData(that.getType().getName(), _data.getSymbolData(), that.getType(), false);
    final TypeData value_result = that.getValue().visit(this);
    
    if (type_result == null) {
      _addError(that.getType().getName() + " cannot appear as the type of a cast expression because it is not a valid type", that.getType());
      return null;
    }
    
    if (value_result == null || !assertFound(value_result, that.getValue())) {
      
      
      return type_result.getInstanceData();
    }
    
    
    return forCastExpressionOnly(that, type_result, value_result);
  }
  
  
  
  public TypeData forUninitializedArrayInstantiationOnly(UninitializedArrayInstantiation that, TypeData type_result, 
                                                         TypeData[] dimensions_result) {
    
    Expression[] dims = that.getDimensionSizes().getExpressions();
    for (int i = 0; i<dimensions_result.length; i++) {
      if (dimensions_result[i] != null && assertFound(dimensions_result[i], dims[i])) {
        if (!dimensions_result[i].getSymbolData().isAssignableTo(SymbolData.INT_TYPE, 
                                                                 JAVA_VERSION)) {
          _addError("The dimensions of an array instantiation must all be ints.  You have specified something of type " +
                    dimensions_result[i].getName(), dims[i]);
        }
        else {
          assertInstanceType(dimensions_result[i], "All dimensions of an array instantiation must be instances." + 
                             "  You have specified the type " + dimensions_result[i].getName(), dims[i]);
        }               
      }
    }
    
    if (type_result instanceof ArrayData) {
      int dim = ((ArrayData) type_result).getDimensions();
      if (dimensions_result.length > dim) {
        
        _addError("You are trying to initialize an array of type " + type_result.getName() + " which requires " + dim +
                  " dimensions, but you have specified " + dimensions_result.length + " dimensions--the wrong number", 
                  that);
      }
    }
    
    
    if (type_result == null || !assertFound(type_result, that)) {return null;}
    return type_result.getInstanceData();
  }
  
  
  public TypeData forSimpleUninitializedArrayInstantiation(SimpleUninitializedArrayInstantiation that) {
    final SymbolData type_result = getSymbolData(that.getType().getName(), _data.getSymbolData(), that.getType());
    final TypeData[] dimensions_result = makeArrayOfRetType(that.getDimensionSizes().getExpressions().length);
    
    for (int i = 0; i<that.getDimensionSizes().getExpressions().length; i++) {
      dimensions_result[i] = that.getDimensionSizes().getExpressions()[i].visit(this);
    }
    return forUninitializedArrayInstantiationOnly(that, type_result, dimensions_result);
  }
  
  
  
  public TypeData forComplexUninitializedArrayInstantiation(ComplexUninitializedArrayInstantiation that) {
    throw new RuntimeException("Internal Program Error: Complex Uninitialized Array Instantiations are not legal Java." +
                               "  This should have been caught before the Type Checker.  Please report this bug.");
  }
  
  
  
  public TypeData forArrayInitializer(ArrayInitializer that) {
    throw new RuntimeException("Internal Program Error: forArrayInitializer should never be called, but it was." + 
                               "  Please report this bug.");
  }
  
  
  public TypeData forSimpleInitializedArrayInstantiation(SimpleInitializedArrayInstantiation that) {
    SymbolData type_result = getSymbolData(that.getType().getName(), _data, that.getType());
    TypeData elementResult = forArrayInitializerHelper(that.getInitializer(), type_result);
    if (type_result == null) {return null;}
    return type_result.getInstanceData();
  }
  
  
  public TypeData forComplexInitializedArrayInstantiation(ComplexInitializedArrayInstantiation that) {
    throw new RuntimeException("Internal Program Error: Complex Initialized Array Instantiations are not legal Java." + 
                               "  This should have been caught before the Type Checker.  Please report this bug.");
  }
  
  
  
  public TypeData forInnerClassDef(InnerClassDef that) {
    String className = that.getName().getText();
    SymbolData sd = _data.getInnerClassOrInterface(className); 
    
    if (checkForCyclicInheritance(sd, new LinkedList<SymbolData>(), that)) {
      return null;
    }
    final TypeData mav_result = that.getMav().visit(this);
    final TypeData name_result = that.getName().visit(this);
    final TypeData[] typeParameters_result = makeArrayOfRetType(that.getTypeParameters().length);
    for (int i = 0; i < that.getTypeParameters().length; i++) {
      typeParameters_result[i] = that.getTypeParameters()[i].visit(this);
    }
    final TypeData superclass_result = that.getSuperclass().visit(this);
    final TypeData[] interfaces_result = makeArrayOfRetType(that.getInterfaces().length);
    for (int i = 0; i < that.getInterfaces().length; i++) {
      interfaces_result[i] = that.getInterfaces()[i].visit(this);
    }
    final TypeData body_result = that.getBody().visit(new ClassBodyTypeChecker(sd, _file, _package, _importedFiles, 
                                                                               _importedPackages, _vars, _thrown));


    return null;
  }
  
  
  void reassignVariableDatas(LinkedList<VariableData> l1, LinkedList<VariableData> l2) {
    for (int i = 0; i<l1.size(); i++) { 
      if (l2.contains(l1.get(i))) l1.get(i).gotValue();
    }
  }
  
  
  void reassignLotsaVariableDatas(LinkedList<VariableData> tryBlock, LinkedList<LinkedList<VariableData>> catchBlocks) {
    for (int i = 0; i<tryBlock.size(); i++) {
      boolean seenIt = true;
      for (int j = 0; j<catchBlocks.size(); i++) {
        if (!catchBlocks.get(j).contains(tryBlock.get(i))) {seenIt = false;}
      }
      
      if (seenIt) {        
        tryBlock.get(i).gotValue();
      }
    }
  }
  
  
  
  
  public void handleUncheckedException(SymbolData sd, JExpression j) {
    if (j instanceof MethodInvocation) {
      _addError("The method " + ((MethodInvocation)j).getName().getText() + " is declared to throw the exception " + sd.getName() + " which needs to be caught or declared to be thrown", j);
    }
    else if (j instanceof ThrowStatement) {
      _addError("This statement throws the exception " + sd.getName() + " which needs to be caught or declared to be thrown", j);
    }
    else if (j instanceof ClassInstantiation) {
      _addError("The constructor for the class " + ((ClassInstantiation)j).getType().getName() + " is declared to throw the exception " + sd.getName() + " which needs to be caught or declared to be thrown.", j);
    }
    else {
      throw new RuntimeException("Internal Program Error: Something besides a method invocation or throw statement threw an exception.  Please report this bug.");
    }
  }
  
  
  
  public boolean isCheckedException(SymbolData sd, JExpression that) {
    return sd.isSubClassOf(getSymbolData("java.lang.Throwable", _data, that, false)) &&
      ! sd.isSubClassOf(getSymbolData("java.lang.RuntimeException", _data, that, false)) &&
      ! sd.isSubClassOf(getSymbolData("java.lang.Error", _data, that, false));
  }
  
  
  public boolean isUncaughtCheckedException(SymbolData sd, JExpression that) {
    return isCheckedException(sd, that);
  }
  
  
  public TypeData forBracedBody(BracedBody that) {
    final TypeData[] items_result = makeArrayOfRetType(that.getStatements().length);
    for (int i = 0; i < that.getStatements().length; i++) {
      items_result[i] = that.getStatements()[i].visit(this);
      
      for (int j = 0; j<this._thrown.size(); j++) {
        if (isUncaughtCheckedException(this._thrown.get(j).getFirst(), that)) {
          handleUncheckedException(this._thrown.get(j).getFirst(), this._thrown.get(j).getSecond());
        }
      }
    }
    
    return forBracedBodyOnly(that, items_result);
  }
  
  
  public TypeData forEmptyForCondition(EmptyForCondition that) {
    return SymbolData.BOOLEAN_TYPE.getInstanceData();
  }
  
  
  public static class ExpressionTypeCheckerTest extends TestCase {
    
    private ExpressionTypeChecker _etc;
    
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
    
    
    public ExpressionTypeCheckerTest() {
      this("");
    }
    public ExpressionTypeCheckerTest(String name) {
      super(name);
    }
    
    public void setUp() {
      errors = new LinkedList<Pair<String, JExpressionIF>>();
      LanguageLevelConverter.symbolTable.clear();
      LanguageLevelConverter._newSDs.clear();
      _etc = 
        new ExpressionTypeChecker(null, new File(""), "", new LinkedList<String>(), new LinkedList<String>(), 
                                  new LinkedList<VariableData>(), new LinkedList<Pair<SymbolData, JExpression>>());
      LanguageLevelConverter.OPT = new Options(JavaVersion.JAVA_5, EmptyIterable.<File>make());
      _etc._importedPackages.addFirst("java.lang");
      _sd1 = new SymbolData("i.like.monkey");
      _sd2 = new SymbolData("i.like.giraffe");
      _sd3 = new SymbolData("zebra");
      _sd4 = new SymbolData("u.like.emu");
      _sd5 = new SymbolData("");
      _sd6 = new SymbolData("cebu");
      _etc._data = _sd1;
    }
    
    public void testForCastExpression() {
      CastExpression ce = new CastExpression(SourceInfo.NO_INFO, 
                                             new PrimitiveType(SourceInfo.NO_INFO, "dan"), 
                                             new NullLiteral(SourceInfo.NO_INFO));
      
      
      assertEquals("Should return null", null, ce.visit(_etc));
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("Error message should be correct", 
                   "dan cannot appear as the type of a cast expression because it is not a valid type", 
                   errors.getLast().getFirst());
      
      
      CastExpression ce2 = 
        new CastExpression(SourceInfo.NO_INFO,
                           new PrimitiveType(SourceInfo.NO_INFO, "int"),
                           new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "notReal")));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), ce2.visit(_etc));
      assertEquals("There should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "Could not resolve symbol notReal", errors.getLast().getFirst());
      
      
      CastExpression ce3 = new CastExpression(SourceInfo.NO_INFO,
                                              new PrimitiveType(SourceInfo.NO_INFO, "int"),
                                              new DoubleLiteral(SourceInfo.NO_INFO, 5));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), ce3.visit(_etc));
      assertEquals("There should still be 2 errors", 2, errors.size());
      
      
    }
    
    public void testForCastExpressionOnly() {
      SymbolData sd1 = SymbolData.DOUBLE_TYPE;
      SymbolData sd2 = SymbolData.BOOLEAN_TYPE;
      SymbolData sd3 = SymbolData.INT_TYPE;
      
      CastExpression cd = new CastExpression(SourceInfo.NO_INFO, 
                                             JExprParser.NO_TYPE, 
                                             new NullLiteral(SourceInfo.NO_INFO));
      
      assertEquals("When value_result is subtype of type_result, return type_result.", sd1.getInstanceData(), 
                   _etc.forCastExpressionOnly(cd, sd1, sd3.getInstanceData()));
      assertEquals("Should not throw an error.", 0, errors.size());
      assertEquals("When type_result is subtype of value_result, return type_result.", sd3.getInstanceData(), 
                   _etc.forCastExpressionOnly(cd, sd3, sd1.getInstanceData()));
      assertEquals("Should not throw an error.", 0, errors.size());
      assertEquals("When type_result and value_result are not subtypes of each other, return type_result", 
                   sd2.getInstanceData(), _etc.forCastExpressionOnly(cd, sd2, sd1.getInstanceData()));
      assertEquals("Should now be one error.", 1, errors.size());
      assertEquals("Error message should be correct.", "You cannot cast an expression of type " + sd1.getName() 
                     + " to type " + sd2.getName() + " because they are not related", 
                   errors.getLast().getFirst());     
      SymbolData foo = new SymbolData("Foo");
      SymbolData fooMama = new SymbolData("FooMama");
      foo.setSuperClass(fooMama);
      assertEquals("When value_result is a SymbolData, return type_result", fooMama.getInstanceData(), 
                   _etc.forCastExpressionOnly(cd, fooMama, foo));
      assertEquals("There should be 2 errors.", 2, errors.size());
      assertEquals("Error message should be correct.", 
                   "You are trying to cast Foo, which is a class or interface type, not an instance.  " 
                     + "Perhaps you meant to create a new instance of Foo",
                   errors.getLast().getFirst());
    }
    
    public void testForEmptyExpressionOnly() {
      EmptyExpression ee = new EmptyExpression(SourceInfo.NO_INFO);
      try {
        _etc.forEmptyExpressionOnly(ee);
        fail("Should have thrown exception");
      }
      catch (RuntimeException e) {
        assertEquals("Error message should be correct", "Internal Program Error: EmptyExpression encountered.  Student is missing something.  Should have been caught before TypeChecker.  Please report this bug.", e.getMessage());
      }
    }
    
    public void test_getLeastRestrictiveType() {
      
      assertEquals("Should return double.", SymbolData.FLOAT_TYPE, _etc._getLeastRestrictiveType(SymbolData.INT_TYPE, SymbolData.FLOAT_TYPE));
      assertEquals("Should return double.", SymbolData.FLOAT_TYPE, _etc._getLeastRestrictiveType(SymbolData.FLOAT_TYPE, SymbolData.FLOAT_TYPE));
      assertEquals("Should return int.", SymbolData.INT_TYPE, _etc._getLeastRestrictiveType(SymbolData.INT_TYPE, SymbolData.CHAR_TYPE));
      assertEquals("Should return char.", SymbolData.INT_TYPE, _etc._getLeastRestrictiveType(SymbolData.CHAR_TYPE, SymbolData.CHAR_TYPE));
    }
    
    public void test_isAssignableFrom() {
      assertTrue("Should be assignable.", _etc._isAssignableFrom(SymbolData.DOUBLE_TYPE, SymbolData.DOUBLE_TYPE));
      assertTrue("Should be assignable.", _etc._isAssignableFrom(SymbolData.DOUBLE_TYPE, SymbolData.INT_TYPE));
      assertTrue("Should be assignable.", _etc._isAssignableFrom(SymbolData.DOUBLE_TYPE, SymbolData.CHAR_TYPE));
      assertTrue("Should be assignable.", _etc._isAssignableFrom(SymbolData.INT_TYPE, SymbolData.INT_TYPE));
      assertTrue("Should be assignable.", _etc._isAssignableFrom(SymbolData.INT_TYPE, SymbolData.CHAR_TYPE));
      assertTrue("Should be assignable.", _etc._isAssignableFrom(SymbolData.CHAR_TYPE, SymbolData.CHAR_TYPE));
      
      _sd2.setSuperClass(_sd1);
      assertTrue("Should be assignable.", _etc._isAssignableFrom(_sd1, _sd1));
      assertTrue("Should be assignable.", _etc._isAssignableFrom(_sd1, _sd2));
    }
    
    
    
    public void testRandomExpressions() {
      
      PositiveExpression pe = new PositiveExpression(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5));
      PositiveExpression pe2 = new PositiveExpression(SourceInfo.NO_INFO, pe);
      NegativeExpression pe3 = new NegativeExpression(SourceInfo.NO_INFO, pe2);
      PositiveExpression pe4 = new PositiveExpression(SourceInfo.NO_INFO, pe3);
      PositiveExpression pe5 = new PositiveExpression(SourceInfo.NO_INFO, pe4);
      NegativeExpression pe6 = new NegativeExpression(SourceInfo.NO_INFO, pe5);
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), pe6.visit(_etc));
      assertEquals("Should be no errors", 0, errors.size());
    }
    
    public void testForSimpleUninitializedArrayInstantiation() {
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(_etc._file, 
                                 _etc._package, 
                                 _etc._importedFiles, 
                                 _etc._importedPackages, 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());


      
      SourceInfo si = SourceInfo.NO_INFO;
      
      ArrayData intArray = new ArrayData(SymbolData.INT_TYPE, llv, si);
      intArray.setIsContinuation(false);
      symbolTable.remove("int[]");
      symbolTable.put("int[]", intArray);
      
      ArrayData intArrayArray = new ArrayData(intArray, llv, si);
      intArrayArray.setIsContinuation(false);
      symbolTable.put("int[][]", intArrayArray);
      
      ArrayData intArray3 = new ArrayData(intArrayArray, llv, si);
      intArray3.setIsContinuation(false);
      symbolTable.put("int[][][]", intArray3);
      
      Expression i1 = new IntegerLiteral(si, 5);
      Expression i2 = new PlusExpression(si, new IntegerLiteral(si, 5), new IntegerLiteral(si, 7));
      Expression i3 = new CharLiteral(si, 'c');
      Expression badIndexD = new DoubleLiteral(si, 4.2);
      Expression badIndexL = new LongLiteral(si, 4l);
      
      
      SimpleUninitializedArrayInstantiation sa1 = 
        new SimpleUninitializedArrayInstantiation(si, new ArrayType(si, "int[][][]", 
                                                                    new ArrayType(si, "int[][]", 
                                                                                  new ArrayType(si, "int[]", new PrimitiveType(si, "int")))), 
                                                  new DimensionExpressionList(si, new Expression[] {i1, i2, i3}));
      assertEquals("Should return instance of int[][][]", intArray3.getInstanceData(), sa1.visit(_etc));
      assertEquals("There should be no errors", 0, errors.size());
      
      
      SimpleUninitializedArrayInstantiation sa2 = new SimpleUninitializedArrayInstantiation(si, new ArrayType(si, "int[][][]", new ArrayType(si, "int[][]", new ArrayType(si, "int[]", new PrimitiveType(si, "int")))), 
                                                                                            new DimensionExpressionList(si, new Expression[] {i1, i2, badIndexD}));
      assertEquals("Should return instance of int[][][]", intArray3.getInstanceData(), sa2.visit(_etc));
      
      
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", "The dimensions of an array instantiation must all be ints.  You have specified something of type double", errors.getLast().getFirst());
      
      
      SimpleUninitializedArrayInstantiation sa3 = new SimpleUninitializedArrayInstantiation(si, new ArrayType(si, "Jonathan[]", new ClassOrInterfaceType(si, "Jonathan", new Type[0])), 
                                                                                            new DimensionExpressionList(si, new Expression[]{i1}));
      assertEquals("Should return null", null, sa3.visit(_etc));
      assertEquals("There should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "Class or variable Jonathan[] not found.", errors.getLast().getFirst());
      
      
      SimpleUninitializedArrayInstantiation sa4 = new SimpleUninitializedArrayInstantiation(si, new ArrayType(si, "int[][]", new ArrayType(si, "int[]", new PrimitiveType(si, "int"))), 
                                                                                            new DimensionExpressionList(si, new Expression[] {i1, i2, i3}));
      assertEquals("Should return instance of int[][]", intArrayArray.getInstanceData(), sa4.visit(_etc));
      assertEquals("There should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "You are trying to initialize an array of type int[][] which requires 2 dimensions, but you have specified 3 dimensions--the wrong number", errors.getLast().getFirst());
      
      
      
      SimpleUninitializedArrayInstantiation sa5 = new SimpleUninitializedArrayInstantiation(si, new ArrayType(si, "int[][][]", new ArrayType(si, "int[][]", new ArrayType(si, "int[]", new PrimitiveType(si, "int")))), 
                                                                                            new DimensionExpressionList(si, new Expression[] {i1, i2}));
      assertEquals("Should return instance of int[][][]", intArray3.getInstanceData(), sa5.visit(_etc));
      assertEquals("There should still be 3 errors", 3, errors.size());
      
      
      intArray3.setMav(_privateMav);
      assertEquals("Should return instance of int[][][]", intArray3.getInstanceData(), sa1.visit(_etc));
      assertEquals("There should be no errors", 4, errors.size());
      assertEquals("Error message should be correct", "The class or interface int[][][] is private and cannot be accessed from i.like.monkey", errors.getLast().getFirst());
      intArray3.setMav(_publicMav);
    }
    
    
    
    public void testForComplexUninitializedArrayInstantiation() {
      ComplexUninitializedArrayInstantiation ca1 = new ComplexUninitializedArrayInstantiation(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "my")),
                                                                                              new ArrayType(SourceInfo.NO_INFO, "type[][][]", new ArrayType(SourceInfo.NO_INFO, "type[][]", new ArrayType(SourceInfo.NO_INFO, "type[]", new ClassOrInterfaceType(SourceInfo.NO_INFO, "type", new Type[0])))), 
                                                                                              new DimensionExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      
      try {
        ca1.visit(_etc);
        fail("Should have throw runtime exception");
      }
      catch (RuntimeException e) {
        assertEquals("Correct exception should have been thrown","Internal Program Error: Complex Uninitialized Array Instantiations are not legal Java.  This should have been caught before the Type Checker.  Please report this bug." , e.getMessage());
      }
    }    
    
    public void testForUninitializedArrayInstantiationOnly() {
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(_etc._file, 
                                 _etc._package, 
                                 _etc._importedFiles, 
                                 _etc._importedPackages, 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      


      
      SourceInfo si = SourceInfo.NO_INFO;
      
      ArrayData intArray = new ArrayData(SymbolData.INT_TYPE, llv, si);
      intArray.setIsContinuation(false);
      symbolTable.remove("int[]");
      symbolTable.put("int[]", intArray);
      
      ArrayData intArrayArray = new ArrayData(intArray, llv, si);
      intArrayArray.setIsContinuation(false);
      symbolTable.put("int[][]", intArrayArray);
      
      ArrayData intArray3 = new ArrayData(intArrayArray, llv, si);
      intArray3.setIsContinuation(false);
      symbolTable.put("int[][][]", intArray3);
      
      
      SimpleUninitializedArrayInstantiation sa1 = new SimpleUninitializedArrayInstantiation(si, new ArrayType(si, "int[][][]", new ArrayType(si, "int[][]", new ArrayType(si, "int[]", new PrimitiveType(si, "int")))), 
                                                                                            new DimensionExpressionList(si, new Expression[] {new NullLiteral(si), new NullLiteral(si), new NullLiteral(si)}));
      
      assertEquals("Should return int[][][] instance", intArray3.getInstanceData(), _etc.forUninitializedArrayInstantiationOnly(sa1, intArray3, new TypeData[] {SymbolData.INT_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()}));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return int[][][] instance", intArray3.getInstanceData(), _etc.forUninitializedArrayInstantiationOnly(sa1, intArray3, new TypeData[] {SymbolData.INT_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData(), SymbolData.CHAR_TYPE.getInstanceData()}));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return int[][][] instance", intArray3.getInstanceData(), _etc.forUninitializedArrayInstantiationOnly(sa1, intArray3, new TypeData[] {SymbolData.INT_TYPE.getInstanceData(), SymbolData.INT_TYPE, SymbolData.CHAR_TYPE.getInstanceData()}));
      assertEquals("Should be one error", 1, errors.size());
      assertEquals("Error message should be correct", "All dimensions of an array instantiation must be instances.  You have specified the type int.  Perhaps you meant to create a new instance of int", errors.getLast().getFirst());
      
      
      assertEquals("Should return int[][][] instance", intArray3.getInstanceData(), _etc.forUninitializedArrayInstantiationOnly(sa1, intArray3, new TypeData[] {SymbolData.INT_TYPE.getInstanceData(), SymbolData.BOOLEAN_TYPE, SymbolData.CHAR_TYPE.getInstanceData()}));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "The dimensions of an array instantiation must all be ints.  You have specified something of type boolean" , errors.getLast().getFirst());
      
      
    }
    
    public void testForArrayInitializer() {
      ArrayInitializer ai = new ArrayInitializer(SourceInfo.NO_INFO, new VariableInitializerI[] {new IntegerLiteral(SourceInfo.NO_INFO, 2)});
      try {
        ai.visit(_etc);
        fail("Should have throw runtime exception");
      }
      catch(RuntimeException e) {
        assertEquals("Exception message should be correct", "Internal Program Error: forArrayInitializer should never be called, but it was.  Please report this bug.", e.getMessage());
      }
      
    }
    
    public void testForSimpleInitializedArrayInstantiation() {
      IntegerLiteral e1 = new IntegerLiteral(SourceInfo.NO_INFO, 5);
      IntegerLiteral e2 = new IntegerLiteral(SourceInfo.NO_INFO, 7);
      SimpleNameReference e3 = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "int"));
      BooleanLiteral e4 = new BooleanLiteral(SourceInfo.NO_INFO, true);
      DoubleLiteral e5 = new DoubleLiteral(SourceInfo.NO_INFO, 4.2);
      CharLiteral e6 = new CharLiteral(SourceInfo.NO_INFO, 'e');
      SimpleNameReference e7 = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "int"));
      
      ArrayType intArrayType = new ArrayType(SourceInfo.NO_INFO, "int[]", new PrimitiveType(SourceInfo.NO_INFO, "int"));
      
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(_etc._file, 
                                 _etc._package, 
                                 _etc._importedFiles, 
                                 _etc._importedPackages, 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      
      ArrayData intArray = new ArrayData(SymbolData.INT_TYPE, llv, SourceInfo.NO_INFO);
      intArray.setIsContinuation(false);
      symbolTable.remove("int[]");
      symbolTable.put("int[]", intArray);
      
      
      InitializedArrayInstantiation good = new SimpleInitializedArrayInstantiation(SourceInfo.NO_INFO, intArrayType, new ArrayInitializer(SourceInfo.NO_INFO, new VariableInitializerI[] {e1, e2}));
      assertEquals("Should return int array instance", intArray.getInstanceData(), good.visit(_etc));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      good = new SimpleInitializedArrayInstantiation(SourceInfo.NO_INFO, intArrayType, new ArrayInitializer(SourceInfo.NO_INFO, new VariableInitializerI[] {e1, e2, e6}));
      assertEquals("Should return int array instance", intArray.getInstanceData(), good.visit(_etc));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      InitializedArrayInstantiation bad = new SimpleInitializedArrayInstantiation(SourceInfo.NO_INFO, new PrimitiveType(SourceInfo.NO_INFO, "int"), new ArrayInitializer(SourceInfo.NO_INFO, new VariableInitializerI[] {e1, e2}));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), bad.visit(_etc));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "You cannot initialize the non-array type int with an array initializer", errors.getLast().getFirst());
      
      
      
      bad = new SimpleInitializedArrayInstantiation(SourceInfo.NO_INFO, intArrayType, new ArrayInitializer(SourceInfo.NO_INFO, new VariableInitializerI[] {e1, e4, e2, e6}));
      assertEquals("Should return int array instance", intArray.getInstanceData(), bad.visit(_etc));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "The elements of this initializer should have type int but element 1 has type boolean", errors.getLast().getFirst());
      
      
      bad = new SimpleInitializedArrayInstantiation(SourceInfo.NO_INFO, intArrayType, new ArrayInitializer(SourceInfo.NO_INFO, new VariableInitializerI[] {e1, e5, e2, e6}));
      assertEquals("Should return int array instance", intArray.getInstanceData(), bad.visit(_etc));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "The elements of this initializer should have type int but element 1 has type double", errors.getLast().getFirst());
      
      
      bad = new SimpleInitializedArrayInstantiation(SourceInfo.NO_INFO, new PrimitiveType(SourceInfo.NO_INFO, "ej"), new ArrayInitializer(SourceInfo.NO_INFO, new VariableInitializerI[] {e1, e2}));
      assertEquals("Should return null", null, bad.visit(_etc));
      assertEquals("Should be 4 error", 4, errors.size());
      assertEquals("Error message should be correct", "Class or variable ej not found.", errors.getLast().getFirst());
      
      
      bad = new SimpleInitializedArrayInstantiation(SourceInfo.NO_INFO, intArrayType, new ArrayInitializer(SourceInfo.NO_INFO, new VariableInitializerI[] {e1, e7}));
      assertEquals("Should return instance of int[]", intArray.getInstanceData(), bad.visit(_etc));
      assertEquals("Should now be 5 error messages", 5, errors.size());
      assertEquals("Error message should be correct", "The elements of this initializer should all be instances, but you have specified the type name int.  Perhaps you meant to create a new instance of int", errors.getLast().getFirst());
      
      
    }
    
    
    
    
    public void testForSimpleAssignmentExpressionOnly() {
      SimpleAssignmentExpression sae = new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")), new IntegerLiteral(SourceInfo.NO_INFO, 5));
      
      
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), _etc.forSimpleAssignmentExpressionOnly(sae, SymbolData.DOUBLE_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return null", null, _etc.forSimpleAssignmentExpressionOnly(sae, null, SymbolData.INT_TYPE));
      assertEquals("Should return null", null, _etc.forSimpleAssignmentExpressionOnly(sae, SymbolData.INT_TYPE, null));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      PackageData pd = new PackageData("bad_reference");
      assertEquals("Should return null", null, _etc.forSimpleAssignmentExpressionOnly(sae, pd, SymbolData.INT_TYPE));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "Could not resolve symbol bad_reference", errors.get(0).getFirst());
      
      
      
      assertEquals("Should return null", null, _etc.forSimpleAssignmentExpressionOnly(sae, SymbolData.INT_TYPE, pd));
      assertEquals("Should only be 1 error", 1, errors.size());  
      assertEquals("Error message should be correct", "Could not resolve symbol bad_reference", errors.get(0).getFirst());
      
      
      assertEquals("Should return double instance", 
                   SymbolData.DOUBLE_TYPE.getInstanceData(), 
                   _etc.forSimpleAssignmentExpressionOnly(sae, 
                                                          SymbolData.DOUBLE_TYPE, 
                                                          SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should now be 2 errors", 2, errors.size());  
      assertEquals("Error message should be correct", 
                   "You cannot assign a value to the type double.  Perhaps you meant to create a new instance of double", 
                   errors.get(1).getFirst());
      
      assertEquals("Should return double instance", 
                   SymbolData.DOUBLE_TYPE.getInstanceData(), 
                   _etc.forSimpleAssignmentExpressionOnly(sae, 
                                                          SymbolData.DOUBLE_TYPE.getInstanceData(), 
                                                          SymbolData.INT_TYPE));
      assertEquals("Should now be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot use the type name int on the right hand side of an assignment.  " +
                   "Perhaps you meant to create a new instance of int", 
                   errors.get(2).getFirst());
      
      
      assertEquals("Should return int instance", 
                   SymbolData.INT_TYPE.getInstanceData(), 
                   _etc.forSimpleAssignmentExpressionOnly(sae, 
                                                          SymbolData.INT_TYPE.getInstanceData(), 
                                                          SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("Should now be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot assign something of type double to something of type int", 
                   errors.get(3).getFirst());
      
    }
    
    
    public void testForPlusAssignmentExpressionOnly() {
      PlusAssignmentExpression pae = 
        new PlusAssignmentExpression(SourceInfo.NO_INFO, 
                                     new IntegerLiteral(SourceInfo.NO_INFO, 5), new IntegerLiteral(SourceInfo.NO_INFO, 6));
      
      
      SymbolData string = new SymbolData("java.lang.String");
      string.setIsContinuation(false);
      string.setPackage("java.lang");
      string.setMav(_publicMav);
      symbolTable.put("java.lang.String", string);
      
      assertEquals("Should return string instance", 
                   string.getInstanceData(), 
                   _etc.forPlusAssignmentExpressionOnly(pae, string.getInstanceData(), 
                                                        SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return double instance", 
                   SymbolData.DOUBLE_TYPE.getInstanceData(), 
                   _etc.forPlusAssignmentExpressionOnly(pae, 
                                                        SymbolData.DOUBLE_TYPE.getInstanceData(), 
                                                        SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return null", null, _etc.forPlusAssignmentExpressionOnly(pae, null, SymbolData.INT_TYPE));
      assertEquals("Should return null", null, _etc.forPlusAssignmentExpressionOnly(pae, SymbolData.INT_TYPE, null));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      PackageData pd = new PackageData("bad_reference");
      assertEquals("Should return null", null, _etc.forPlusAssignmentExpressionOnly(pae, pd, SymbolData.INT_TYPE));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "Could not resolve symbol bad_reference", errors.getLast().getFirst());
      
      
      
      assertEquals("Should return null", null, _etc.forPlusAssignmentExpressionOnly(pae, SymbolData.INT_TYPE, pd));
      assertEquals("Should be 1 error", 1, errors.size());  
      assertEquals("Error message should be correct", "Could not resolve symbol bad_reference", errors.get(0).getFirst());
      
      
      assertEquals("Should return string instance", 
                   string.getInstanceData(), 
                   _etc.forPlusAssignmentExpressionOnly(pae, string, 
                                                        SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should now be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct",
                   "The arguments to a Plus Assignment Operator (+=) must both be instances, but you have specified " +
                   "a type name.  Perhaps you meant to create a new instance of java.lang.String", 
                   errors.get(1).getFirst());
      
      
      assertEquals("Should return string instance", string.getInstanceData(), 
                   _etc.forPlusAssignmentExpressionOnly(pae, string.getInstanceData(), 
                                                        SymbolData.INT_TYPE));
      assertEquals("Should now be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct",
                   "The arguments to a Plus Assignment Operator (+=) must both be instances, " +
                   "but you have specified a type name.  Perhaps you meant to create a new instance of int" , 
                   errors.get(2).getFirst());
      
      
      assertEquals("Should return string, by default", string.getInstanceData(), 
                   _etc.forPlusAssignmentExpressionOnly(pae, _sd2.getInstanceData(), 
                                                        SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should now be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", 
                   "The arguments to the Plus Assignment Operator (+=) must either include an instance of a String " +
                   "or both be numbers.  You have specified arguments of type " + _sd2.getName() + " and int", 
                   errors.get(3).getFirst());
      
      
      assertEquals("should return string, by default", string.getInstanceData(),
                   _etc.forPlusAssignmentExpressionOnly(pae, SymbolData.INT_TYPE.getInstanceData(),
                                                        _sd2.getInstanceData()));
      assertEquals("Should now be 5 errors", 5, errors.size());  
      assertEquals("Error message should be correct", 
                   "The arguments to the Plus Assignment Operator (+=) must either include an instance of a String " +
                   "or both be numbers.  You have specified arguments of type int and " + _sd2.getName(), 
                   errors.get(4).getFirst());
      
      assertEquals("Should return int instance", 
                   SymbolData.INT_TYPE.getInstanceData(), 
                   _etc.forPlusAssignmentExpressionOnly(pae, SymbolData.INT_TYPE.getInstanceData(), 
                                                        SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("Should now be 6 errors", 6, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot increment something of type int with something of type double", 
                   errors.get(5).getFirst());
      
      
      assertEquals("Should return double instance", 
                   SymbolData.DOUBLE_TYPE.getInstanceData(), 
                   _etc.forPlusAssignmentExpressionOnly(pae, SymbolData.DOUBLE_TYPE, SymbolData.INT_TYPE));
      assertEquals("Should now be 8 errors", 8, errors.size());
      assertEquals("Second error message should be new", 
                   "The arguments to the Plus Assignment Operator (+=) must both be instances, but you have specified " +
                   "a type name.  Perhaps you meant to create a new instance of double", 
                   errors.get(6).getFirst());
      assertEquals("First error message should be new", 
                   "The arguments to the Plus Assignment Operator (+=) must both be instances, but you have specified " +
                   "a type name.  Perhaps you meant to create a new instance of int", 
                   errors.get(7).getFirst());
    }
    
    public void testForNumericAssignmentExpressionOnly() {
      NumericAssignmentExpression nae = 
        new MinusAssignmentExpression(SourceInfo.NO_INFO, 
                                      new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")),
                                      new IntegerLiteral(SourceInfo.NO_INFO, 5));
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forNumericAssignmentExpressionOnly(nae, SymbolData.INT_TYPE.getInstanceData(), SymbolData.CHAR_TYPE.getInstanceData()));
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), _etc.forNumericAssignmentExpressionOnly(nae, SymbolData.DOUBLE_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      
      assertEquals("Should return null", null, _etc.forNumericAssignmentExpressionOnly(nae, null, SymbolData.INT_TYPE));
      assertEquals("Should return null", null, _etc.forNumericAssignmentExpressionOnly(nae, SymbolData.INT_TYPE, null));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      PackageData pd = new PackageData("bad_reference");
      assertEquals("Should return null", null, _etc.forNumericAssignmentExpressionOnly(nae, pd, SymbolData.INT_TYPE));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "Could not resolve symbol bad_reference", errors.get(0).getFirst());
      
      
      assertEquals("Should return null", null, _etc.forNumericAssignmentExpressionOnly(nae, SymbolData.INT_TYPE, pd));
      assertEquals("Should still be 1 error", 1, errors.size());  
      assertEquals("Error message should be correct", "Could not resolve symbol bad_reference", errors.get(0).getFirst());
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forNumericAssignmentExpressionOnly(nae, SymbolData.INT_TYPE, SymbolData.CHAR_TYPE.getInstanceData()));
      assertEquals("Should be 2 errors", 2, errors.size());  
      assertEquals("Error message should be correct", 
                   "You cannot use a numeric assignment (-=, %=, *=, /=) on the type int.  Perhaps you meant to create " +
                   "a new instance of int", 
                   errors.get(1).getFirst());
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forNumericAssignmentExpressionOnly(nae, SymbolData.INT_TYPE.getInstanceData(), SymbolData.CHAR_TYPE));
      assertEquals("Should now be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot use the type name char on the left hand side of a numeric assignment (-=, %=, *=, /=)." +
                   "  Perhaps you meant to create a new instance of char", 
                   errors.get(2).getFirst());
      
      
      assertEquals("Should return sd2 instance", _sd2.getInstanceData(), 
                   _etc.forNumericAssignmentExpressionOnly(nae, _sd2.getInstanceData(), 
                                                           SymbolData.CHAR_TYPE.getInstanceData()));
      assertEquals("Should now be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", 
                   "The left side of this expression is not a number.  Therefore, you cannot apply " + 
                   "a numeric assignment (-=, %=, *=, /=) to it", 
                   errors.get(3).getFirst());
      
      
      assertEquals("Should return int instance", 
                   SymbolData.INT_TYPE.getInstanceData(), 
                   _etc.forNumericAssignmentExpressionOnly(nae, 
                                                           SymbolData.INT_TYPE.getInstanceData(), 
                                                           _sd2.getInstanceData()));
      assertEquals("Should still be 5 errors", 5, errors.size());  
      assertEquals("Error message should be correct", 
                   "The right side of this expression is not a number.  Therefore, you cannot apply " +
                   "a numeric assignment (-=, %=, *=, /=) to it", 
                   errors.get(4).getFirst());
      
      
      assertEquals("Should return int instance", 
                   SymbolData.INT_TYPE.getInstanceData(), 
                   _etc.forNumericAssignmentExpressionOnly(nae, 
                                                           SymbolData.INT_TYPE.getInstanceData(), 
                                                           SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("Should be 6 errors", 6, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot use a numeric assignment (-=, %=, *=, /=) on something of type int with something of " +
                   "type double", 
                   errors.get(5).getFirst());
    }
    
    
    public void testForShiftAssignmentExpressionOnly() {
      ShiftAssignmentExpression sae = new LeftShiftAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")), new IntegerLiteral(SourceInfo.NO_INFO, 2));
      try {
        _etc.forShiftAssignmentExpressionOnly(sae, _sd1, _sd2);
        fail("forShiftAssignmentExpressionOnly should have thrown a runtime exception");
      }
      catch (RuntimeException e) {
        assertEquals("Exception message should be correct", "Internal Program Error: Shift assignment operators are not supported.  This should have been caught before the TypeChecker.  Please report this bug.", e.getMessage());
      }
    }
    
    public void testForBitwiseAssignmentExpressionOnly() {
      BitwiseAssignmentExpression bae = new BitwiseXorAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")), new IntegerLiteral(SourceInfo.NO_INFO, 2));
      try {
        _etc.forBitwiseAssignmentExpressionOnly(bae, _sd1, _sd2);
        fail("forBitwiseAssignmentExpressionOnly should have thrown a runtime exception");
      }
      catch (RuntimeException e) {
        assertEquals("Exception message should be correct", "Internal Program Error: Bitwise assignment operators are not supported.  This should have been caught before the TypeChecker.  Please report this bug.", e.getMessage());
      }
    }
    
    
    public void testForBooleanExpressionOnly() {
      BooleanExpression be = new OrExpression(SourceInfo.NO_INFO, new BooleanLiteral(SourceInfo.NO_INFO, true), new BooleanLiteral(SourceInfo.NO_INFO, false));
      
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forBooleanExpressionOnly(be, SymbolData.BOOLEAN_TYPE.getInstanceData(), SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("There should be no errors", 0, errors.size());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forBooleanExpressionOnly(be, SymbolData.BOOLEAN_TYPE, SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("There should now be 1 error", 1, errors.size());
      assertEquals("The error message should be correct", "The left side of this expression is a type, not an instance.  Perhaps you meant to create a new instance of boolean", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forBooleanExpressionOnly(be, SymbolData.INT_TYPE.getInstanceData(), SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("There should now be 2 errors", 2, errors.size());
      assertEquals("The error message should be correct", "The left side of this expression is not a boolean value.  Therefore, you cannot apply a Boolean Operator (&&, ||) to it", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forBooleanExpressionOnly(be, SymbolData.BOOLEAN_TYPE.getInstanceData(), SymbolData.BOOLEAN_TYPE));
      assertEquals("There should now be 3 errors", 3, errors.size());
      assertEquals("The error message should be correct", "The right side of this expression is a type, not an instance.  Perhaps you meant to create a new instance of boolean", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forBooleanExpressionOnly(be, SymbolData.BOOLEAN_TYPE.getInstanceData(), SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("There should now be 4 errors", 4, errors.size());
      assertEquals("The error message should be correct", "The right side of this expression is not a boolean value.  Therefore, you cannot apply a Boolean Operator (&&, ||) to it", errors.getLast().getFirst());
      
    }
    
    public void testForBitwiseBinaryExpressionOnly() {
      BitwiseBinaryExpression bbe = new BitwiseAndExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")), new IntegerLiteral(SourceInfo.NO_INFO, 2));
      try {
        _etc.forBitwiseBinaryExpressionOnly(bbe, _sd2, _sd3);
        fail("forBitwiseBinaryExpressionOnly should have thrown a runtime exception");
      }
      catch (RuntimeException e) {
        assertEquals("Exception message should be correct", "Internal Program Error: Bitwise operators are not supported.  This should have been caught before the TypeChecker.  Please report this bug.", e.getMessage());
      }
    }
    
    
    public void testForEqualityExpressionOnly() {
      EqualityExpression ee = new EqualsExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), 
                                                   new NullLiteral(SourceInfo.NO_INFO));
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), 
                   _etc.forEqualityExpressionOnly(ee, SymbolData.BOOLEAN_TYPE.getInstanceData(), SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, SymbolData.INT_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      
      SymbolData integer = new SymbolData("java.lang.Integer");
      integer.setIsContinuation(false);
      symbolTable.put("java.lang.Integer", integer);
      
      SymbolData bool = new SymbolData("java.lang.Boolean");
      bool.setIsContinuation(false);
      symbolTable.put("java.lang.Boolean", bool);
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, SymbolData.INT_TYPE.getInstanceData(), integer.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, integer.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, SymbolData.BOOLEAN_TYPE.getInstanceData(), bool.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, bool.getInstanceData(), SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, _sd1.getInstanceData(), _sd2.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, SymbolData.INT_TYPE.getInstanceData(), SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "At least one of the arguments to this Equality Operator (==, !=) is primitive.  Therefore, they must either both be number types or both be boolean types.  You have specified expressions with type int and boolean", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, SymbolData.INT_TYPE.getInstanceData(), _sd1.getInstanceData()));
      assertEquals("There should now be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "At least one of the arguments to this Equality Operator (==, !=) is primitive.  Therefore, they must either both be number types or both be boolean types.  You have specified expressions with type int and i.like.monkey", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, _sd1.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("There should now be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "At least one of the arguments to this Equality Operator (==, !=) is primitive.  Therefore, they must either both be number types or both be boolean types.  You have specified expressions with type i.like.monkey and int", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, _sd1, _sd2.getInstanceData()));
      assertEquals("There should now be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", "The arguments to this Equality Operator(==, !=) must both be instances.  Instead, you have referenced a type name on the left side.  Perhaps you meant to create a new instance of " + _sd1.getName(), errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forEqualityExpressionOnly(ee, _sd1.getInstanceData(), _sd2));
      assertEquals("There should now be 5 errors", 5, errors.size());
      assertEquals("Error message should be correct", "The arguments to this Equality Operator(==, !=) must both be instances.  Instead, you have referenced a type name on the right side.  Perhaps you meant to create a new instance of " + _sd2.getName(), errors.getLast().getFirst());
      
    }
    
    
    public void testForComparisonExpressionOnly() {
      ComparisonExpression ce = new LessThanExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), new NullLiteral(SourceInfo.NO_INFO));
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forComparisonExpressionOnly(ce, SymbolData.DOUBLE_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("There should be no errors", 0, errors.size());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forComparisonExpressionOnly(ce, SymbolData.BOOLEAN_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("Error message should be correct", "The left side of this expression is not a number.  Therefore, you cannot apply a Comparison Operator (<, >; <=, >=) to it", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forComparisonExpressionOnly(ce, SymbolData.DOUBLE_TYPE, SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("There should be two errors", 2, errors.size());
      assertEquals("Error message should be correct", "The left side of this expression is a type, not an instance.  Perhaps you meant to create a new instance of double", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forComparisonExpressionOnly(ce, SymbolData.DOUBLE_TYPE.getInstanceData(), _sd1.getInstanceData()));
      assertEquals("There should be three errors", 3, errors.size());
      assertEquals("Error message should be correct", "The right side of this expression is not a number.  Therefore, you cannot apply a Comparison Operator (<, >; <=, >=) to it", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forComparisonExpressionOnly(ce, SymbolData.DOUBLE_TYPE.getInstanceData(), SymbolData.INT_TYPE));
      assertEquals("There should be four errors", 4, errors.size());
      assertEquals("Error message should be correct", "The right side of this expression is a type, not an instance.  Perhaps you meant to create a new instance of int", errors.getLast().getFirst());
      
    }
    
    
    public void testForShiftBinaryExpressionOnly() {
      ShiftBinaryExpression sbe = new LeftShiftExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "j")), new IntegerLiteral(SourceInfo.NO_INFO, 42));
      try {
        _etc.forShiftBinaryExpressionOnly(sbe, _sd2, _sd3);
        fail("forShiftBinaryExpressionOnly should have thrown a runtime exception");
      }
      catch (RuntimeException e) {
        assertEquals("Exception message should be correct", "Internal Program Error: BinaryShifts are not supported.  This should have been caught before the TypeChecker.  Please report this bug.", e.getMessage());
      }
    }
    
    
    public void testForPlusExpressionOnly() {
      PlusExpression pe = new PlusExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), new NullLiteral(SourceInfo.NO_INFO));
      SymbolData string = new SymbolData("java.lang.String");
      string.setPackage("java.lang");
      string.setIsContinuation(false);
      symbolTable.put("java.lang.String", string);
      
      
      assertEquals("Should return String instance", string.getInstanceData(), _etc.forPlusExpressionOnly(pe, string.getInstanceData(), _sd1.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return String instance", string.getInstanceData(), _etc.forPlusExpressionOnly(pe, _sd1.getInstanceData(), string.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return String instance", string.getInstanceData(), _etc.forPlusExpressionOnly(pe, string.getInstanceData(), string.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return Double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), _etc.forPlusExpressionOnly(pe, SymbolData.DOUBLE_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return String instance", string.getInstanceData(), _etc.forPlusExpressionOnly(pe, string.getInstanceData(), _sd1));
      assertEquals("Should be one error", 1, errors.size());
      assertEquals("Error message should be correct", "The arguments to the Plus Operator (+) must both be instances, but you have specified a type name.  Perhaps you meant to create a new instance of " + _sd1.getName(), errors.getLast().getFirst());
      
      
      assertEquals("Should return String instance", string.getInstanceData(), _etc.forPlusExpressionOnly(pe, string, string.getInstanceData()));
      assertEquals("Should be two errors", 2, errors.size());
      assertEquals("Error message should be correct", "The arguments to the Plus Operator (+) must both be instances, but you have specified a type name.  Perhaps you meant to create a new instance of java.lang.String", errors.getLast().getFirst());
      
      
      assertEquals("Should return String instance", string.getInstanceData(), _etc.forPlusExpressionOnly(pe, SymbolData.INT_TYPE.getInstanceData(), SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("Should be three errors", 3, errors.size());
      assertEquals("Error message should be correct", "The arguments to the Plus Operator (+) must either include an instance of a String or both be numbers.  You have specified arguments of type int and boolean", errors.getLast().getFirst());
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forPlusExpressionOnly(pe, SymbolData.INT_TYPE, SymbolData.CHAR_TYPE.getInstanceData()));
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", "The arguments to the Plus Operator (+) must both be instances, but you have specified a type name.  Perhaps you meant to create a new instance of int", errors.getLast().getFirst());
      
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forPlusExpressionOnly(pe, SymbolData.INT_TYPE.getInstanceData(), SymbolData.CHAR_TYPE));
      assertEquals("Should be 5 errors", 5, errors.size());
      assertEquals("Error message should be correct", "The arguments to the Plus Operator (+) must both be instances, but you have specified a type name.  Perhaps you meant to create a new instance of char", errors.getLast().getFirst());
      
      
    }
    
    
    public void testForNumericBinaryExpressionOnly() {
      NumericBinaryExpression nbe = new ModExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), new NullLiteral(SourceInfo.NO_INFO));
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forNumericBinaryExpressionOnly(nbe, SymbolData.INT_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("There should be no errors", 0, errors.size());
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forNumericBinaryExpressionOnly(nbe, SymbolData.INT_TYPE.getInstanceData(), SymbolData.CHAR_TYPE.getInstanceData()));
      assertEquals("There should be no errors", 0, errors.size());
      
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), _etc.forNumericBinaryExpressionOnly(nbe, SymbolData.INT_TYPE.getInstanceData(), SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("There should be no errors", 0, errors.size());
      
      
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), _etc.forNumericBinaryExpressionOnly(nbe, SymbolData.INT_TYPE, SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "The left side of this expression is a type, not an instance.  Perhaps you meant to create a new instance of int", errors.getLast().getFirst());
      
      
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), _etc.forNumericBinaryExpressionOnly(nbe, SymbolData.BOOLEAN_TYPE.getInstanceData(), SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "The left side of this expression is not a number.  Therefore, you cannot apply a Numeric Binary Operator (*, /, -, %) to it", errors.getLast().getFirst());
      
      
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), _etc.forNumericBinaryExpressionOnly(nbe, SymbolData.INT_TYPE.getInstanceData(), SymbolData.DOUBLE_TYPE));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "The right side of this expression is a type, not an instance.  Perhaps you meant to create a new instance of double", errors.getLast().getFirst());
      
      
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forNumericBinaryExpressionOnly(nbe, SymbolData.INT_TYPE.getInstanceData(), SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", "The right side of this expression is not a number.  Therefore, you cannot apply a Numeric Binary Operator (*, /, -, %) to it", errors.getLast().getFirst());
      
      
    }
    
    public void testForNoOpExpressionOnly() {
      NoOpExpression noe = new NoOpExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), new NullLiteral(SourceInfo.NO_INFO));
      try {
        _etc.forNoOpExpressionOnly(noe, null, null);
        fail("Should have thrown runtime exception");
      }
      catch (RuntimeException e) {
        assertEquals("Error message should be correct", "Internal Program Error: The student is missing an operator.  This should have been caught before the TypeChecker.  Please report this bug.", e.getMessage());
      }
    }
    
    public void testForIncrementExpressionOnly() {
      IncrementExpression ie = new PositivePrefixIncrementExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "i")));
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forIncrementExpressionOnly(ie, SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      
      assertEquals("Should return null", null, _etc.forIncrementExpressionOnly(ie, null));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      PackageData pd = new PackageData("bad_reference");
      assertEquals("Should return null", null, _etc.forIncrementExpressionOnly(ie, pd));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "Could not resolve symbol bad_reference", errors.getLast().getFirst());
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forIncrementExpressionOnly(ie, SymbolData.INT_TYPE));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "You cannot increment or decrement int, because it is a class name not an instance.  Perhaps you meant to create a new instance of int", errors.getLast().getFirst());
      
      
      assertEquals("Should return sd2 instance", _sd2.getInstanceData(), _etc.forIncrementExpressionOnly(ie, _sd2.getInstanceData()));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "You cannot increment or decrement something that is not a number type.  You have specified something of type " + _sd2.getName(), errors.getLast().getFirst());
    }
    
    
    
    public void testForNumericUnaryExpressionOnly() {
      NumericUnaryExpression nue = new PositiveExpression(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5));
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forNumericUnaryExpressionOnly(nue, SymbolData.CHAR_TYPE.getInstanceData()));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forNumericUnaryExpressionOnly(nue, SymbolData.BYTE_TYPE.getInstanceData()));
      assertEquals("There should be no errors", 0, errors.size());
      
      
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), _etc.forNumericUnaryExpressionOnly(nue, SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("There should be no errors", 0, errors.size());
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forNumericUnaryExpressionOnly(nue, SymbolData.INT_TYPE));
      assertEquals("Should be one error", 1, errors.size());
      assertEquals("Error message should be correct", "You cannot use a numeric unary operator (+, -) with int, because it is a class name, not an instance.  Perhaps you meant to create a new instance of int", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forNumericUnaryExpressionOnly(nue, SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "You cannot apply this unary operator to something of type boolean.  You can only apply it to a numeric type such as double, int, or char", errors.getLast().getFirst());
    }
    
    
    public void testForBitwiseNotExpressionOnly() {
      BitwiseNotExpression bne = new BitwiseNotExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "t")));
      try {
        _etc.forBitwiseNotExpressionOnly(bne, _sd3);
        fail("forBitwiseNotExpressionOnly should have thrown a runtime exception");
      }
      catch (RuntimeException e) {
        assertEquals("Exception message should be correct", "Internal Program Error: BitwiseNot is not supported.  It should have been caught before getting to the TypeChecker.  Please report this bug.", e.getMessage());
      }
    }
    
    
    public void testForNotExpressionOnly() {
      NotExpression ne = new NotExpression(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO));
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forNotExpressionOnly(ne, SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forNotExpressionOnly(ne, SymbolData.BOOLEAN_TYPE));
      assertEquals("Should be one error", 1, errors.size());
      assertEquals("Error message should be correct","You cannot use the not (!) operator with boolean, because it is a class name, not an instance.  Perhaps you meant to create a new instance of boolean", errors.getLast().getFirst());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forNotExpressionOnly(ne, SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should be two errors", 2, errors.size());
      assertEquals("Error message should be correct", "You cannot use the not (!) operator with something of type int. Instead, it should be used with an expression of boolean type", errors.getLast().getFirst());
    }
    
    
    public void testForConditionalExpressionOnly() {
      SymbolData sd1 = SymbolData.DOUBLE_TYPE;
      SymbolData sd2 = SymbolData.BOOLEAN_TYPE;
      SymbolData sd3 = SymbolData.INT_TYPE;
      ConditionalExpression cd = new ConditionalExpression(SourceInfo.NO_INFO, 
                                                           new BooleanLiteral(SourceInfo.NO_INFO, true),
                                                           new IntegerLiteral(SourceInfo.NO_INFO, 5),
                                                           new IntegerLiteral(SourceInfo.NO_INFO, 79));
      
      try {
        _etc.forConditionalExpressionOnly(cd, _sd3, _sd2, _sd1);
        fail("Should have thrown an exception.");
      }
      catch (Exception e) {
        assertEquals("Exception message should be correct", "Internal Program Error: Conditional expressions are not supported.  This should have been caught before the TypeChecker.  Please report this bug.", e.getMessage());
        
      }
    }  
    
    public void testForInstanceOfExpressionOnly() {
      SymbolData sd1 = SymbolData.DOUBLE_TYPE;
      SymbolData sd2 = SymbolData.BOOLEAN_TYPE;
      SymbolData sd3 = SymbolData.INT_TYPE;
      InstanceofExpression ioe = new InstanceofExpression(SourceInfo.NO_INFO,          
                                                          new NullLiteral(SourceInfo.NO_INFO),
                                                          JExprParser.NO_TYPE);  
      
      assertEquals("When value_result is subtype of type_result, return BOOLEAN type_result.", sd2.getInstanceData(), 
                   _etc.forInstanceofExpressionOnly(ioe, sd1, sd3.getInstanceData()));
      assertEquals("Should not throw an error.", 0, errors.size());
      assertEquals("When type_result is subtype of value_result, return BOOLEAN type_result.", sd2.getInstanceData(), 
                   _etc.forInstanceofExpressionOnly(ioe, sd3, sd1.getInstanceData()));
      assertEquals("Should not throw an error.", 0, errors.size());
      assertEquals("When type_result and value_result are not subtypes of each other, return BOOLEAN type_result", 
                   sd2.getInstanceData(),
                   _etc.forInstanceofExpressionOnly(ioe, sd2, sd1.getInstanceData()));
      assertEquals("Should now be one error.", 1, errors.size());
      assertEquals("Error message should be correct.", "You cannot test whether an expression of type " + sd1.getName() 
                     + " belongs to type " + sd2.getName() + " because they are not related", 
                   errors.getLast().getFirst());     
      SymbolData foo = new SymbolData("Foo");
      SymbolData fooMama = new SymbolData("FooMama");
      foo.setSuperClass(fooMama);
      assertEquals("When value_result is a SymbolData, return BOOLEAN type_result",  sd2.getInstanceData(), 
                   _etc.forInstanceofExpressionOnly(ioe, foo, fooMama));
      assertEquals("There should be 2 errors.", 2, errors.size());
      assertEquals("Error message should be correct.", 
                   "You are trying to test if FooMama belongs to type, but it is a class or interface type, "
                     + "not an instance.  Perhaps you meant to create a new instance of FooMama",
                   errors.getLast().getFirst());
    }
    
    public void testClassInstantiationHelper() {
      ClassInstantiation simpleCI = new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, 
                                                                      new ClassOrInterfaceType(SourceInfo.NO_INFO, "testClass", new Type[0]),
                                                                      new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      ClassInstantiation complexCI = new ComplexNamedClassInstantiation(SourceInfo.NO_INFO,
                                                                        new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Outer")),
                                                                        new ClassOrInterfaceType(SourceInfo.NO_INFO, "Inner", new Type[0]),
                                                                        new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      
      ClassInstantiation badArgs = new SimpleNamedClassInstantiation(SourceInfo.NO_INFO,
                                                                     new ClassOrInterfaceType(SourceInfo.NO_INFO, "anotherClass", new Type[0]),
                                                                     new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "int"))}));
      
      SymbolData testClass = new SymbolData("testClass");
      SymbolData outer = new SymbolData("Outer");
      SymbolData outerInner = new SymbolData("Outer.Inner");
      outer.addInnerClass(outerInner);
      outerInner.setOuterData(outer);
      
      
      assertEquals("Should return null", null, _etc.classInstantiationHelper(simpleCI, null));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), _etc.classInstantiationHelper(badArgs, SymbolData.DOUBLE_TYPE));
      assertEquals("Should be one error", 1, errors.size());
      assertEquals("Error message should be correct", "Cannot pass a class or interface name as a constructor argument.  Perhaps you meant to create a new instance of int", errors.getLast().getFirst());
      
      
      assertEquals("Should return instance of testClass", testClass.getInstanceData(), _etc.classInstantiationHelper(simpleCI, testClass));
      assertEquals("Should be two errors", 2, errors.size());
      assertEquals("Error message should be correct", "No constructor found in class testClass with signature: testClass().", errors.getLast().getFirst());
      
      assertEquals("Should return instance of Outer.Inner", outerInner.getInstanceData(), _etc.classInstantiationHelper(complexCI, outerInner));
      assertEquals("Should be three errors", 3, errors.size());
      assertEquals("Error message should be correct", "No constructor found in class Outer.Inner with signature: Inner().", errors.getLast().getFirst());
      
      
      
      MethodData md = new MethodData("testClass", _publicMav, new TypeParameter[0], testClass, 
                                     new VariableData[0], 
                                     new String[0], 
                                     testClass,
                                     null);
      testClass.addMethod(md);
      assertEquals("Should return instance of testClass", testClass.getInstanceData(), _etc.classInstantiationHelper(simpleCI, testClass));
      assertEquals("Should still be just three errors", 3, errors.size());
    }
    
    
    public void testForSimpleNamedClassInstantiation() { 
      SimpleNamedClassInstantiation ci1 = new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, new ClassOrInterfaceType(SourceInfo.NO_INFO, "simpleClass", new Type[0]), 
                                                                            new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new IntegerLiteral(SourceInfo.NO_INFO, 5)}));
      
      SimpleNamedClassInstantiation ci3 = new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, new ClassOrInterfaceType(SourceInfo.NO_INFO, "simpleClass", new Type[0]), 
                                                                            new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      
      
      assertEquals("Should return null, since simpleClass is not in symbol table", null, ci1.visit(_etc));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "Class or variable simpleClass not found.", errors.getLast().getFirst());
      
      
      SymbolData simpleClass = new SymbolData("simpleClass");
      simpleClass.setIsContinuation(false);
      MethodData cons1 = new MethodData("simpleClass", _publicMav, new TypeParameter[0], simpleClass, 
                                        new VariableData[0], 
                                        new String[0], 
                                        simpleClass,
                                        null);
      simpleClass.addMethod(cons1);
      symbolTable.put("simpleClass", simpleClass);
      
      assertEquals("Should return simpleClass even though it could not really access it", simpleClass.getInstanceData(), ci3.visit(_etc));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "The class or interface simpleClass is package protected because there is no access specifier and cannot be accessed from i.like.monkey", errors.getLast().getFirst());
      
      
      simpleClass.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      
      assertEquals("Should return simpleClass even though it could not find constructor", simpleClass.getInstanceData(), ci1.visit(_etc));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "No constructor found in class simpleClass with signature: simpleClass(int).", errors.getLast().getFirst());
      
      
      MethodData cons2 = new MethodData("simpleClass", _publicMav, new TypeParameter[0], simpleClass, 
                                        new VariableData[] {new VariableData(SymbolData.INT_TYPE)}, 
                                        new String[0], 
                                        simpleClass,
                                        null);
      simpleClass.addMethod(cons2);                                   
      assertEquals("Should return simpleClass", simpleClass.getInstanceData(), ci1.visit(_etc));
      assertEquals("Should still be 3 errors", 3, errors.size());
      
      
      
      simpleClass.addModifier("abstract");
      assertEquals("Should return simpleClass even though it cannot really be instantiated", simpleClass.getInstanceData(), ci1.visit(_etc));
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", "simpleClass is abstract and thus cannot be instantiated", errors.getLast().getFirst());
      
      
      
      SimpleNamedClassInstantiation ci2 = new SimpleNamedClassInstantiation(SourceInfo.NO_INFO, new ClassOrInterfaceType(SourceInfo.NO_INFO, "A.B", new Type[0]), 
                                                                            new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      
      
      SymbolData a = new SymbolData("A");
      a.setIsContinuation(false);
      SymbolData b = new SymbolData("A$B");
      b.setIsContinuation(false);
      b.setOuterData(a);
      a.addInnerClass(b);
      MethodData consb = new MethodData("B", _publicMav, new TypeParameter[0], b, 
                                        new VariableData[0], 
                                        new String[0], 
                                        b,
                                        null);
      b.addMethod(consb);
      symbolTable.put("A", a);
      a.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      b.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      
      
      
      
      assertEquals("Should return A.B", b.getInstanceData(), ci2.visit(_etc));
      assertEquals("Should be 5 errors", 5, errors.size());
      assertEquals("Error message should be correct", "A.B is not a static inner class, and thus cannot be instantiated from this context.  Perhaps you meant to use an instantiation of the form new A().new B()", errors.getLast().getFirst());
      
      b.addModifier("static");
      assertEquals("Should return A.B", b.getInstanceData(), ci2.visit(_etc));
      assertEquals("Should still be just 5 errors", 5, errors.size());
      
    }
    
    public void testForComplexNamedClassInstantiation() {
      ComplexNamedClassInstantiation ci1 = 
        new ComplexNamedClassInstantiation(SourceInfo.NO_INFO, 
                                           new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "o")), 
                                           new ClassOrInterfaceType(SourceInfo.NO_INFO, "innerClass", new Type[0]),                                  
                                           new ParenthesizedExpressionList(SourceInfo.NO_INFO, 
                                                                           new Expression[] { new IntegerLiteral(SourceInfo.NO_INFO, 5)}));
      
      ComplexNamedClassInstantiation ci2 = 
        new ComplexNamedClassInstantiation(SourceInfo.NO_INFO, 
                                           new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                   new Word(SourceInfo.NO_INFO, "o")), 
                                           new ClassOrInterfaceType(SourceInfo.NO_INFO, "innerClass", new Type[0]), 
                                           new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      
      
      assertEquals("Should return null", null, ci1.visit(_etc));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "Could not resolve symbol o", errors.getLast().getFirst());
      
      
      SymbolData outerClass = new SymbolData("outer");
      outerClass.setIsContinuation(false);
      SymbolData innerClass = new SymbolData("outer$innerClass");
      innerClass.setIsContinuation(false);
      outerClass.addInnerClass(innerClass);
      innerClass.setOuterData(outerClass);
      MethodData cons1 = new MethodData("innerClass", _publicMav, new TypeParameter[0], innerClass, 
                                        new VariableData[0], 
                                        new String[0], 
                                        innerClass,
                                        null);
      innerClass.addMethod(cons1);
      symbolTable.put("outer", outerClass);
      _etc._vars.addLast(new VariableData("o", _publicMav, outerClass, true, _etc._data));
      outerClass.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      innerClass.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      
      assertEquals("Should return innerClass even though it could not find constructor", innerClass.getInstanceData(), ci1.visit(_etc));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "No constructor found in class outer.innerClass with signature: innerClass(int).", errors.getLast().getFirst());
      
      
      MethodData cons2 = new MethodData("innerClass", _publicMav, new TypeParameter[0], innerClass, 
                                        new VariableData[] {new VariableData(SymbolData.INT_TYPE)}, 
                                        new String[0], 
                                        innerClass,
                                        null);
      innerClass.addMethod(cons2);                                   
      assertEquals("Should return innerClass", innerClass.getInstanceData(), ci1.visit(_etc));
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      innerClass.addModifier("abstract");
      assertEquals("Should return innerClass even though it cannot really be instantiated", innerClass.getInstanceData(), ci1.visit(_etc));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "outer.innerClass is abstract and thus cannot be instantiated", errors.getLast().getFirst());              
      
      
      ComplexNamedClassInstantiation ci3 = new ComplexNamedClassInstantiation(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "outer")), new ClassOrInterfaceType(SourceInfo.NO_INFO, "innerClass", new Type[0]), 
                                                                              new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));             
      outerClass.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      innerClass.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      assertEquals("Should return innerClass even though the syntax was wrong", innerClass.getInstanceData(), ci3.visit(_etc));
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", 
                   "The constructor of a non-static inner class can only be called on an instance of its containing class (e.g. new outer().new innerClass())", errors.getLast().getFirst());
      
      
      innerClass.addModifier("static");
      assertEquals("Should return innerClass even though the syntax was wrong", innerClass.getInstanceData(), ci1.visit(_etc));
      assertEquals("Should be 5 errors", 5, errors.size());
      
      assertEquals("Error message should be correct", 
                   "You cannot instantiate a static inner class or interface with this syntax.  Instead, try new outer.innerClass()",
                   errors.getLast().getFirst());
      
      
      
      innerClass.setMav(_publicMav);
      ComplexNamedClassInstantiation ci4 = new ComplexNamedClassInstantiation(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "o")), new ClassOrInterfaceType(SourceInfo.NO_INFO, "notInnerClass", new Type[0]), 
                                                                              new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {new IntegerLiteral(SourceInfo.NO_INFO, 5)}));
      assertEquals("Should return null", null, ci4.visit(_etc));
      assertEquals("Should be 6 errors", 6, errors.size());
      assertEquals("Error message should be correct", "Class or variable notInnerClass not found.", errors.getLast().getFirst());
      
      
      
      innerClass.setMav(_privateMav);
      assertEquals("Should return inner class", innerClass.getInstanceData(), ci1.visit(_etc));
      assertEquals("Should be 7 errors", 7, errors.size());
      assertEquals("Error message should be correct", "The class or interface outer.innerClass is private and cannot be accessed from i.like.monkey", errors.getLast().getFirst());
      
      
      outerClass.setMav(_privateMav);
      innerClass.setMav(_publicMav);
      assertEquals("Should return inner class", innerClass.getInstanceData(), ci1.visit(_etc));
      assertEquals("Should be 8 errors", 8, errors.size());
      assertEquals("Error message should be correct", "The class or interface outer is private and cannot be accessed from i.like.monkey", errors.getLast().getFirst());
    }
    
    public void testForSimpleThisConstructorInvocation() {
      
      SimpleThisConstructorInvocation stci = 
        new SimpleThisConstructorInvocation(SourceInfo.NO_INFO, 
                                            new ParenthesizedExpressionList(SourceInfo.NO_INFO, 
                                                                            new Expression[0]));
      assertEquals("Should return null", null, stci.visit(_etc));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", 
                   "This constructor invocations are only allowed as the first statement of a constructor body", 
                   errors.getLast().getFirst());
    }
    
    public void testForComplexThisConstructorInvocation() {
      
      ComplexThisConstructorInvocation ctci = new ComplexThisConstructorInvocation(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "something")), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      assertEquals("Should return null", null, ctci.visit(_etc));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "Constructor invocations of this form are never allowed", errors.getLast().getFirst());
    }
    
    public void testForSimpleNameReference() {
      
      SimpleNameReference var = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "variable1"));
      VariableData varData = new VariableData("variable1", _publicMav, SymbolData.INT_TYPE, false, _etc._data);
      _etc._vars.add(varData);
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), var.visit(_etc));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "You cannot use variable1 because it may not have been given a value", errors.getLast().getFirst());
      
      
      varData.gotValue();
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), var.visit(_etc));
      assertEquals("Should still be 1 error", 1, errors.size());
      
      
      MethodData newContext =
        new MethodData("method", _publicStaticMav, new TypeParameter[0], SymbolData.INT_TYPE,
                       new VariableData[0], new String[0], _sd1, new NullLiteral(SourceInfo.NO_INFO)); 
      _etc._data = newContext;
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), var.visit(_etc));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "Non-static variable or field variable1 cannot be referenced from a static context", errors.getLast().getFirst());
      _etc._data = _sd1;
        
      
      _etc._vars = new LinkedList<VariableData>();
      _sd1.setSuperClass(_sd2);
      _sd2.addVar(varData);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), var.visit(_etc));
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      SimpleNameReference className = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Frog"));
      SymbolData frog = new SymbolData("Frog");
      frog.setIsContinuation(false);
      symbolTable.put("Frog", frog);
      
      
      TypeData result = className.visit(_etc);
      assertTrue("Result should be a PackageData since Frog is not accessible", result instanceof PackageData);
      assertEquals("Should have correct name", "Frog", result.getName());
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      frog.setMav(_publicMav);
      assertEquals("Should return Frog", frog, className.visit(_etc));
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      SimpleNameReference fake = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "notRealReference"));
      assertEquals("Should return package data", "notRealReference", (fake.visit(_etc)).getName());
      assertEquals("Should still be just 2 errors", 2, errors.size());
      
      
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
      
      _etc._data = _sd6;
      
      assertEquals("Should return null", null, ambigRef.visit(_etc));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", 
                   "Ambiguous reference to class or interface ambigThing", 
                   errors.getLast().getFirst());    
    }
    
    
    public void testForComplexNameReference() {
      
      
      
      ComplexNameReference ref1 = 
        new ComplexNameReference(SourceInfo.NO_INFO, 
                                 new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "java")), 
                                 new Word(SourceInfo.NO_INFO, "lang"));
      assertEquals("Should return correct package data", "java.lang", ref1.visit(_etc).getName());
      assertEquals("Should be no errors", 0, errors.size());
      
      
      ComplexNameReference ref2 = 
        new ComplexNameReference(SourceInfo.NO_INFO, ref1, new Word(SourceInfo.NO_INFO, "String"));
      SymbolData string = new SymbolData("java.lang.String");
      string.setPackage("java.lang");
      string.setMav(_publicMav);
      string.setIsContinuation(false);
      symbolTable.put("java.lang.String", string);
      
      assertEquals("Should return string", string, ref2.visit(_etc));
      
      assertEquals("Should still be no errors", 0, errors.size());
      
      
      
      
      
      VariableData myVar = new VariableData("myVar", _publicStaticMav, SymbolData.DOUBLE_TYPE, true, string);
      string.addVar(myVar);
      ComplexNameReference varRef1 = new ComplexNameReference(SourceInfo.NO_INFO, ref2, new Word(SourceInfo.NO_INFO, "myVar"));
      
      
      assertEquals("Should return Double_Type instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef1.visit(_etc));
      assertEquals("There should still be no errors", 0, errors.size());
      
      
      myVar.lostValue();
      assertEquals("Should return Double_Type instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef1.visit(_etc));
      assertEquals("There should still be one error", 1, errors.size());
      assertEquals("Error message should be correct", "You cannot use myVar here, because it may not have been given a value", errors.getLast().getFirst());
      
      
      myVar.gotValue();
      myVar.setMav(_publicMav);
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef1.visit(_etc));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "Non-static variable myVar cannot be accessed from the static context java.lang.String.  Perhaps you meant to instantiate an instance of java.lang.String", errors.getLast().getFirst());
      
      
      
      VariableData stringVar = new VariableData("s", _publicMav, string, true, _etc._data);
      _etc._vars.add(stringVar);
      ComplexNameReference varRef2 = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "s")), new Word(SourceInfo.NO_INFO, "myVar"));
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef2.visit(_etc));
      assertEquals("Should still just be 2 errors", 2, errors.size());
      
      
      string.setVars(new LinkedList<VariableData>());
      string.setSuperClass(_sd2);
      _sd2.addVar(myVar);
      assertEquals("Should return double instance", SymbolData.DOUBLE_TYPE.getInstanceData(), varRef2.visit(_etc));
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      VariableData vd1 = new VariableData("Mojo", _publicMav, SymbolData.INT_TYPE, true, _sd1);
      VariableData vd2 = new VariableData("Santa's Little Helper", _publicMav, _sd1, true, _sd2);
      VariableData vd3 = new VariableData("Snowball1", _publicMav, _sd2, true, _sd3);
      _sd3.addVar(vd3);
      _sd2.addVar(vd2);
      _sd1.addVar(vd1);
      
      ComplexNameReference varRef3 = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Snowball1")),
                                                              new Word(SourceInfo.NO_INFO, "Santa's Little Helper"));
      ComplexNameReference varRef4 = new ComplexNameReference(SourceInfo.NO_INFO, varRef3, new Word(SourceInfo.NO_INFO, "Mojo"));
      
      Data oldData = _etc._data;
      _etc._data = _sd3;
      _etc._vars.add(vd3);
      _sd3.setMav(_publicMav);
      _sd1.setMav(_publicMav);
      _sd2.setMav(_publicMav);
      
      TypeData result = varRef4.visit(_etc);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), result);
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      _etc._data = oldData;
      
      
      
      SymbolData inner = new SymbolData("java.lang.String$Inner");
      inner.setPackage("java.lang");
      inner.setIsContinuation(false);
      inner.setOuterData(string);
      string.addInnerClass(inner);
      
      
      ComplexNameReference innerRef0 = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "s")), new Word(SourceInfo.NO_INFO, "Inner"));
      assertEquals("Should return null", null, innerRef0.visit(_etc));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "The class or interface java.lang.String.Inner is package protected because there is no access specifier and cannot be accessed from i.like.monkey", errors.getLast().getFirst());
      
      inner.setMav(_publicMav);
      
      
      
      ComplexNameReference innerRef1 = new ComplexNameReference(SourceInfo.NO_INFO, ref2, new Word(SourceInfo.NO_INFO, "Inner"));
      assertEquals("Should return inner", inner, innerRef1.visit(_etc));
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", "Non-static inner class java.lang.String.Inner cannot be accessed from this context.  Perhaps you meant to instantiate it", errors.getLast().getFirst());
      
      
      ComplexNameReference innerRef2 = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "s")), new Word(SourceInfo.NO_INFO, "Inner"));
      assertEquals("Should return inner", inner, innerRef2.visit(_etc));
      assertEquals("Should still be 5 errors", 5, errors.size());
      assertEquals("Error message should be correct", "Non-static inner class java.lang.String.Inner cannot be accessed from this context.  Perhaps you meant to instantiate it", errors.getLast().getFirst());
      
      
      inner.setMav(_publicStaticMav);
      assertEquals("Should return inner", inner, innerRef2.visit(_etc));
      assertEquals("Should be 6 errors", 6, errors.size());
      assertEquals("Error message should be correct", "You cannot reference the static inner class java.lang.String.Inner from an instance of java.lang.String.  Perhaps you meant to say java.lang.String.Inner", errors.getLast().getFirst());
      
      
      
      ComplexNameReference noSense = new ComplexNameReference(SourceInfo.NO_INFO, ref2, new Word(SourceInfo.NO_INFO, "nonsense"));
      assertEquals("Should return null", null, noSense.visit(_etc));
      assertEquals("Should be 7 errors", 7, errors.size());
      assertEquals("Error message should be correct", "Could not resolve nonsense from the context of java.lang.String", errors.getLast().getFirst());
      
      
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
      
      symbolTable.put("cebu", _sd6);
      _sd6.setMav(_publicMav);
      _sd6.setIsContinuation(false);
      
      assertEquals("Should return null", null, ambigRef.visit(_etc));
      assertEquals("Should be 8 errors", 8, errors.size());
      assertEquals("Error message should be correct", "Ambiguous reference to class or interface ambigThing", errors.getLast().getFirst());    
      
      
      
      
      inner.setMav(_publicStaticMav);
      string.setMav(_privateMav);
      
      assertEquals("Should return inner", inner, innerRef1.visit(_etc));
      assertEquals("Should be 9 errors", 9, errors.size());
      assertEquals("Error message should be correct", "The class or interface java.lang.String is private and cannot be accessed from i.like.monkey", errors.getLast().getFirst());
    }
    
    
    public void testForSimpleThisReference() {
      SimpleThisReference str = new SimpleThisReference(SourceInfo.NO_INFO);
      
      
      assertEquals("Should return i.like.monkey instance", _etc._data.getSymbolData().getInstanceData(), str.visit(_etc));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      MethodData sm = new MethodData("staticMethod", new VariableData[0]);
      sm.setMav(_publicStaticMav);
      sm.setOuterData(_etc._data);
      _etc._data = sm;
      
      assertEquals("Should return i.like.monkey instance", _etc._data.getSymbolData().getInstanceData(), str.visit(_etc));
      assertEquals("Should be one errors", 1, errors.size());
      assertEquals("Error message should be correct", "'this' cannot be referenced from within a static method", errors.getLast().getFirst());
    }
    
    
    public void testForComplexThisReferenceOnly() {
      ComplexThisReference ctr = new ComplexThisReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "context")));
      
      
      assertEquals("Should return null", null, _etc.forComplexThisReferenceOnly(ctr, null));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      
      assertEquals("Should return null", null, _etc.forComplexThisReferenceOnly(ctr, new PackageData("context")));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct","Could not resolve symbol context" , errors.getLast().getFirst());
      
      
      SymbolData contextClass = new SymbolData("context");
      contextClass.setIsContinuation(false);
      contextClass.setMav(_publicMav);
      
      assertEquals("Should return instance of this", contextClass.getInstanceData(), _etc.forComplexThisReferenceOnly(ctr, contextClass));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("The error message should be correct", "You cannot reference context.this from here, because context is not an outer class of i.like.monkey", errors.getLast().getFirst());
      
      
      _etc._data.setOuterData(contextClass);
      contextClass.addInnerClass(_etc._data.getSymbolData());
      
      assertEquals("Should return instance of this", contextClass.getInstanceData(), _etc.forComplexThisReferenceOnly(ctr, contextClass));
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      MethodData sm = new MethodData("staticMethod", new VariableData[0]);
      sm.setMav(_publicStaticMav);
      sm.setOuterData(_etc._data);
      _etc._data = sm;
      
      assertEquals("Should return instance of this", contextClass.getInstanceData(), _etc.forComplexThisReferenceOnly(ctr, contextClass));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("The error message should be correct", "'this' cannot be referenced from within a static method", errors.getLast().getFirst());
      
      
      _etc._data = sm.getOuterData();
      assertEquals("Should return instance of this", contextClass.getInstanceData(), _etc.forComplexThisReferenceOnly(ctr, contextClass.getInstanceData()));
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("The error message should be correct", "'this' can only be referenced from a type name, but you have specified an instance of that type.", errors.getLast().getFirst());
      
      
      
      
      _etc._data.getSymbolData().addModifier("static");
      assertEquals("Should return instance of this", contextClass.getInstanceData(), _etc.forComplexThisReferenceOnly(ctr, contextClass));
      assertEquals("Should be 5 errors", 5, errors.size());
      assertEquals("Error message should be correct", "You cannot reference context.this from here, because i.like.monkey or one of its enclosing classes is static.  Thus, an enclosing instance of context does not exist", errors.getLast().getFirst());
      
    }
    
    public void testForSimpleSuperReference() {
      SimpleSuperReference ssr = new SimpleSuperReference(SourceInfo.NO_INFO);
      _sd1.setSuperClass(_sd2);
      
      
      assertEquals("Should return _sd2", _sd2.getInstanceData(), ssr.visit(_etc));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      MethodData sm = new MethodData("staticMethod", new VariableData[0]);
      sm.setMav(_publicStaticMav);
      sm.setOuterData(_etc._data);
      _etc._data = sm;
      
      assertEquals("Should return _sd2", _sd2.getInstanceData(), ssr.visit(_etc));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "'super' cannot be referenced from within a static method", errors.getLast().getFirst());
    }
    
    
    public void testForComplexSuperReference() {
      ComplexSuperReference csr = new ComplexSuperReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "context")));
      
      
      assertEquals("Should return null", null, _etc.forComplexSuperReferenceOnly(csr, null));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      
      assertEquals("Should return null", null, _etc.forComplexSuperReferenceOnly(csr, new PackageData("context")));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct","Could not resolve symbol context" , errors.getLast().getFirst());
      
      
      SymbolData contextClass = new SymbolData("context");
      contextClass.setIsContinuation(false);
      contextClass.setMav(_publicMav);
      contextClass.setSuperClass(_sd2);
      
      
      assertEquals("Should return instance of super", _sd2.getInstanceData(), _etc.forComplexSuperReferenceOnly(csr, contextClass));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("The error message should be correct", "You cannot reference context.super from here, because context is not an outer class of i.like.monkey", errors.getLast().getFirst());
      
      
      
      _etc._data.setOuterData(contextClass);
      contextClass.addInnerClass(_etc._data.getSymbolData());
      
      assertEquals("Should return instance of super", _sd2.getInstanceData(), _etc.forComplexSuperReferenceOnly(csr, contextClass));
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      
      MethodData sm = new MethodData("staticMethod", new VariableData[0]);
      sm.setMav(_publicStaticMav);
      sm.setOuterData(_etc._data);
      _etc._data = sm;
      
      assertEquals("Should return instance of super", _sd2.getInstanceData(), _etc.forComplexSuperReferenceOnly(csr, contextClass));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("The error message should be correct", "'super' cannot be referenced from within a static method", errors.getLast().getFirst());
      
      
      _etc._data = sm.getOuterData();
      assertEquals("Should return instance of super", _sd2.getInstanceData(), _etc.forComplexSuperReferenceOnly(csr, contextClass.getInstanceData()));
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("The error message should be correct", "'super' can only be referenced from a type name, but you have specified an instance of that type.", errors.getLast().getFirst());
      
      
      
      _etc._data.getSymbolData().addModifier("static");
      assertEquals("Should return instance of super", _sd2.getInstanceData(), _etc.forComplexSuperReferenceOnly(csr, contextClass));
      assertEquals("Should be 5 errors", 5, errors.size());
      assertEquals("Error message should be correct", "You cannot reference context.super from here, because i.like.monkey or one of its enclosing classes is static.  Thus, an enclosing instance of context does not exist", errors.getLast().getFirst());
      
    }
    
    public void testForArrayAccessOnly() {
      ArrayAccess aa = 
        new ArrayAccess(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO), new NullLiteral(SourceInfo.NO_INFO));
      
      Hashtable<SymbolData, LanguageLevelVisitor> testNewSDs = LanguageLevelConverter._newSDs;
      LanguageLevelVisitor testLLVisitor = 
        new LanguageLevelVisitor(_etc._file, 
                                 _etc._package, 
                                 _etc._importedFiles, 
                                 _etc._importedPackages, 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());
      
      
      ArrayData ad = new ArrayData(SymbolData.INT_TYPE, testLLVisitor, SourceInfo.NO_INFO);             
      
      assertEquals("should return int", SymbolData.INT_TYPE.getInstanceData(), _etc.forArrayAccessOnly(aa, ad.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("should return int", SymbolData.INT_TYPE.getInstanceData(), _etc.forArrayAccessOnly(aa, ad.getInstanceData(), SymbolData.CHAR_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return null", null, _etc.forArrayAccessOnly(aa, null, SymbolData.INT_TYPE));
      assertEquals("Should return null", null, _etc.forArrayAccessOnly(aa, SymbolData.INT_TYPE, null));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      PackageData pd = new PackageData("bad_reference");
      assertEquals("Should return null", null, _etc.forArrayAccessOnly(aa, pd, SymbolData.INT_TYPE));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", 
                   "Could not resolve symbol bad_reference", 
                   errors.getLast().getFirst());
      
      
      
      assertEquals("Should return null", null, _etc.forArrayAccessOnly(aa, SymbolData.INT_TYPE, pd));
      assertEquals("Should still be 1 error", 1, errors.size());  
      assertEquals("Error message should be correct", 
                   "Could not resolve symbol bad_reference", 
                   errors.getLast().getFirst());
      
      
      assertEquals("Should return int", 
                   SymbolData.INT_TYPE.getInstanceData(), 
                   _etc.forArrayAccessOnly(aa, ad, SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should now be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot access an array element of a type name.  Perhaps you meant to create " +
                   "a new instance of int[]", 
                   errors.get(1).getFirst());
      
      
      assertEquals("Should return char", SymbolData.CHAR_TYPE.getInstanceData(), _etc.forArrayAccessOnly(aa, SymbolData.CHAR_TYPE.getInstanceData(), SymbolData.INT_TYPE.getInstanceData()));
      assertEquals("Should now be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", 
                   "The variable referred to by this array access is a char, not an array", 
                   errors.get(2).getFirst());
      
      
      assertEquals("should return int", SymbolData.INT_TYPE.getInstanceData(), _etc.forArrayAccessOnly(aa, ad.getInstanceData(), SymbolData.INT_TYPE));
      assertEquals("Should now be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", 
                   "You have used a type name in place of an array index.  Perhaps you meant to create " +
                   "a new instance of int", 
                   errors.get(3).getFirst());
      
      
      assertEquals("should return int", SymbolData.INT_TYPE.getInstanceData(), _etc.forArrayAccessOnly(aa, ad.getInstanceData(), SymbolData.DOUBLE_TYPE.getInstanceData()));
      assertEquals("Should now be 5 errors", 5, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot reference an array element with an index of type double.  Instead, you must use an int",
                   errors.get(4).getFirst());   
    }
    
    
    public void testLiterals() {
      StringLiteral sl = new StringLiteral(SourceInfo.NO_INFO, "string literal!");
      IntegerLiteral il = new IntegerLiteral(SourceInfo.NO_INFO, 4);
      LongLiteral ll = new LongLiteral(SourceInfo.NO_INFO, 5);
      FloatLiteral fl = new FloatLiteral(SourceInfo.NO_INFO, 1.2f);
      DoubleLiteral dl = new DoubleLiteral(SourceInfo.NO_INFO, 4.2);
      CharLiteral cl = new CharLiteral(SourceInfo.NO_INFO, 'c');
      BooleanLiteral bl = new BooleanLiteral(SourceInfo.NO_INFO, true);
      NullLiteral nl = new NullLiteral(SourceInfo.NO_INFO);
      ClassLiteral csl = new ClassLiteral(SourceInfo.NO_INFO, new ClassOrInterfaceType(SourceInfo.NO_INFO, "monkey", new Type[0]));
      
      SymbolData string = new SymbolData("java.lang.String");
      string.setIsContinuation(false);
      string.setPackage("java.lang");
      string.setMav(_publicMav);
      SymbolData classD = new SymbolData("java.lang.Class");
      classD.setIsContinuation(false);
      classD.setPackage("java.lang");
      classD.setMav(_publicMav);
      
      symbolTable.put("java.lang.String", string);
      symbolTable.put("java.lang.Class", classD);
      
      assertEquals("Should return string", string.getInstanceData(), sl.visit(_etc));
      assertEquals("Should return int", SymbolData.INT_TYPE.getInstanceData(), il.visit(_etc));
      assertEquals("Should return long", SymbolData.LONG_TYPE.getInstanceData(), ll.visit(_etc));
      assertEquals("Should return float", SymbolData.FLOAT_TYPE.getInstanceData(), fl.visit(_etc));
      assertEquals("Should return double", SymbolData.DOUBLE_TYPE.getInstanceData(), dl.visit(_etc));
      assertEquals("Should return char", SymbolData.CHAR_TYPE.getInstanceData(), cl.visit(_etc));
      assertEquals("Should return boolean", SymbolData.BOOLEAN_TYPE.getInstanceData(), bl.visit(_etc));
      assertEquals("Should return null type", SymbolData.NULL_TYPE.getInstanceData(), nl.visit(_etc));
      
      assertEquals("Should return class", classD.getInstanceData(), _etc.forClassLiteralOnly(csl));
      
      
    }
    
    
    public void testForParenthesizedOnly() {
      Parenthesized p = new Parenthesized(SourceInfo.NO_INFO, new NullLiteral(SourceInfo.NO_INFO));
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.forParenthesizedOnly(p, SymbolData.BOOLEAN_TYPE.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return null", null, _etc.forParenthesizedOnly(p, null));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return null", null, _etc.forParenthesizedOnly(p, new PackageData("bob")));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct","Could not resolve symbol bob" , errors.getLast().getFirst());
      
      
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), _etc.forParenthesizedOnly(p, SymbolData.INT_TYPE));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct","This class or interface name cannot appear in parentheses.  Perhaps you meant to create a new instance of int" , errors.getLast().getFirst());
      
      
    }
    
    
    public void testMethodInvocationHelper() {
      MethodInvocation noArgs = new SimpleMethodInvocation(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myName"), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      MethodInvocation typeArg = new SimpleMethodInvocation(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myName"), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[]{new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "int"))}));
      MethodInvocation oneIntArg = new SimpleMethodInvocation(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myName"), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[]{new IntegerLiteral(SourceInfo.NO_INFO, 5)}));
      MethodInvocation oneDoubleArg = new SimpleMethodInvocation(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myName"), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[]{new DoubleLiteral(SourceInfo.NO_INFO, 4.2)}));
      
      
      MethodData noArgsM = new MethodData("myName", _publicMav, new TypeParameter[0], SymbolData.BOOLEAN_TYPE, new VariableData[0], new String[0], _sd2, new NullLiteral(SourceInfo.NO_INFO));
      _sd2.addMethod(noArgsM);
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), _etc.methodInvocationHelper(noArgs, _sd2.getInstanceData()));
      assertEquals("Should be no errors", 0, errors.size());
      
      
      assertEquals("Should return null", null, _etc.methodInvocationHelper(oneIntArg, _sd2.getInstanceData()));
      assertEquals("Should be one error", 1, errors.size());
      assertEquals("Error message should be correct", "No method found in class " + _sd2.getName() + " with signature: myName(int).", errors.getLast().getFirst());
      
      
      MethodData intArg = new MethodData("myName", _publicMav, new TypeParameter[0], SymbolData.LONG_TYPE, new VariableData[] {new VariableData(SymbolData.INT_TYPE)}, new String[0], _sd2, new NullLiteral(SourceInfo.NO_INFO));
      _sd2.addMethod(intArg);
      assertEquals("Should return long instance", SymbolData.LONG_TYPE.getInstanceData(), _etc.methodInvocationHelper(typeArg, _sd2.getInstanceData()));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "Cannot pass a class or interface name as an argument to a method.  Perhaps you meant to create an instance or use int.class", errors.getLast().getFirst());
      
      
      assertEquals("Should return long instance", SymbolData.LONG_TYPE.getInstanceData(), _etc.methodInvocationHelper(oneIntArg, _sd2.getInstanceData()));
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      
      assertEquals("Should return long instance", SymbolData.LONG_TYPE.getInstanceData(), _etc.methodInvocationHelper(oneIntArg, _sd2));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "Cannot access the non-static method myName from a static context", errors.getLast().getFirst());
      
      
      
      MethodData doubleArg = new MethodData("myName", _publicStaticMav, new TypeParameter[0], SymbolData.CHAR_TYPE, new VariableData[] {new VariableData(SymbolData.DOUBLE_TYPE)}, new String[0], _sd2, new NullLiteral(SourceInfo.NO_INFO));
      _sd2.addMethod(doubleArg);
      assertEquals("Should return char instance", SymbolData.CHAR_TYPE.getInstanceData(), _etc.methodInvocationHelper(oneDoubleArg, _sd2));
      assertEquals("Should still be 3 errors", 3, errors.size());
    }
    
    
    
    public void testForSimpleMethodInvocation() {
      MethodInvocation noArgs = new SimpleMethodInvocation(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myName"), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      MethodInvocation oneIntArg = new SimpleMethodInvocation(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myName"), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[]{new IntegerLiteral(SourceInfo.NO_INFO, 5)}));
      MethodInvocation oneDoubleArg = new SimpleMethodInvocation(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myName"), new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[]{new DoubleLiteral(SourceInfo.NO_INFO, 4.2)}));
      
      
      assertEquals("Should return null", null, noArgs.visit(_etc));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "No method found in class i.like.monkey with signature: myName().", errors.getLast().getFirst());
      
      
      MethodData noArgsM = new MethodData("myName", _publicMav, new TypeParameter[0], SymbolData.BOOLEAN_TYPE, new VariableData[0], new String[0], _sd1, new NullLiteral(SourceInfo.NO_INFO));
      _sd1.addMethod(noArgsM);
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), noArgs.visit(_etc));
      assertEquals("Should still just be 1 error", 1, errors.size());
      
      
      MethodData doubleArg = new MethodData("myName", _publicStaticMav, new TypeParameter[0], SymbolData.CHAR_TYPE, new VariableData[] {new VariableData(SymbolData.DOUBLE_TYPE)}, new String[0], _sd1, new NullLiteral(SourceInfo.NO_INFO));
      _sd1.addMethod(doubleArg);
      
      assertEquals("Should return char instance", SymbolData.CHAR_TYPE.getInstanceData(), oneDoubleArg.visit(_etc));
      assertEquals("Should still be just 1 error", 1, errors.size());
      
      
      _etc._data = doubleArg;
      assertEquals("Should return char instance", SymbolData.CHAR_TYPE.getInstanceData(), oneDoubleArg.visit(_etc));
      assertEquals("Should still be just 1 error", 1, errors.size());
      
      
      MethodData intArg = new MethodData("myName", _publicMav, new TypeParameter[0], SymbolData.LONG_TYPE, new VariableData[] {new VariableData(SymbolData.INT_TYPE)}, new String[0], _sd1, new NullLiteral(SourceInfo.NO_INFO));
      _sd1.addMethod(intArg);
      assertEquals("Should return long instance", SymbolData.LONG_TYPE.getInstanceData().getName(), oneIntArg.visit(_etc).getName());
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", "Cannot access the non-static method myName from a static context", errors.getLast().getFirst());
      
    }
    
    
    public void testForComplexMethodInvocation() {
      MethodInvocation staticNoArgs = 
        new ComplexMethodInvocation(SourceInfo.NO_INFO, 
                                    new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "giraffe")),
                                    new Word(SourceInfo.NO_INFO, "myName"), 
                                    new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      MethodInvocation noArgs = 
        new ComplexMethodInvocation(SourceInfo.NO_INFO, 
                                    new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "g")), 
                                    new Word(SourceInfo.NO_INFO, "myName"), 
                                    new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]));
      MethodInvocation oneIntArg = 
        new ComplexMethodInvocation(SourceInfo.NO_INFO, 
                                    new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "g")), 
                                    new Word(SourceInfo.NO_INFO, "myName"), 
                                    new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] { 
        new IntegerLiteral(SourceInfo.NO_INFO, 5)}));
      MethodInvocation staticOneDoubleArg = 
        new ComplexMethodInvocation(SourceInfo.NO_INFO, 
                                    new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "giraffe")),
                                    new Word(SourceInfo.NO_INFO, "myName"), 
                                    new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {
        new DoubleLiteral(SourceInfo.NO_INFO, 4.2)}));
      MethodInvocation oneDoubleArg = 
        new ComplexMethodInvocation(SourceInfo.NO_INFO, 
                                    new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "g")), 
                                    new Word(SourceInfo.NO_INFO, "myName"), 
                                    new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] {
        new DoubleLiteral(SourceInfo.NO_INFO, 4.2)}));
      
      SymbolData g = new SymbolData("giraffe");
      g.setIsContinuation(false);
      g.setMav(_publicMav);
      symbolTable.put("giraffe", g);
      
      
      VariableData var = new VariableData("g", _publicMav, g, true, _sd1);
      _etc._vars.addLast(var);
      
      
      assertEquals("Should return null", null, noArgs.visit(_etc));
      assertEquals("Should be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "No method found in class giraffe with signature: myName().", errors.getLast().getFirst());
      
      
      MethodData noArgsM = new MethodData("myName", _publicMav, new TypeParameter[0], SymbolData.BOOLEAN_TYPE, new VariableData[0], new String[0], g, new NullLiteral(SourceInfo.NO_INFO));
      g.addMethod(noArgsM);
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), noArgs.visit(_etc));
      assertEquals("Should still just be 1 error", 1, errors.size());
      
      
      MethodData doubleArg = 
        new MethodData("myName", _publicStaticMav, new TypeParameter[0], SymbolData.CHAR_TYPE, 
                       new VariableData[] { new VariableData(SymbolData.DOUBLE_TYPE) }, 
                       new String[0], g, new NullLiteral(SourceInfo.NO_INFO));
      g.addMethod(doubleArg);
      
      assertEquals("Should return char instance", SymbolData.CHAR_TYPE.getInstanceData(), oneDoubleArg.visit(_etc));
      assertEquals("Should still be just 1 error", 1, errors.size());
      
      
      staticOneDoubleArg.visit(_etc);
      assertEquals("Should return char instance", SymbolData.CHAR_TYPE.getInstanceData(), 
                   staticOneDoubleArg.visit(_etc));
      assertEquals("Should still be just 1 error", 1, errors.size());
      
      
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(),
                   staticNoArgs.visit(_etc));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", 
                   "Cannot access the non-static method myName from a static context", 
                   errors.getLast().getFirst());
      
      
      _etc._data = doubleArg;
      var.setMav(_publicStaticMav);
      assertEquals("Should return char instance", SymbolData.CHAR_TYPE.getInstanceData(), oneDoubleArg.visit(_etc));
      assertEquals("Should still be just 2 errors", 2, errors.size());
      
      
      MethodData intArg = 
        new MethodData("myName", _publicMav, new TypeParameter[0], SymbolData.LONG_TYPE, 
                       new VariableData[] { new VariableData(SymbolData.INT_TYPE)}, 
                       new String[0], g, new NullLiteral(SourceInfo.NO_INFO));
      g.addMethod(intArg);
      assertEquals("Should return long instance", SymbolData.LONG_TYPE.getInstanceData().getName(), 
                   oneIntArg.visit(_etc).getName());
      assertEquals("Should be 2 errors", 2, errors.size());

      
      
      _etc._data = _sd1;
      g.setMav(_privateMav);
      noArgsM.setMav(_publicStaticMav);
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), noArgs.visit(_etc));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "The class or interface giraffe is private and cannot be accessed from i.like.monkey", errors.getLast().getFirst());
      
    }
    
    
    public void testCanBeAssigned() {
      VariableData finalWithValue = new VariableData("i", _finalMav, SymbolData.INT_TYPE, true, _sd1);
      VariableData finalWithOutValue = new VariableData("i", _finalMav, SymbolData.INT_TYPE, false, _sd1);
      VariableData notFinalWithValue = new VariableData("i", _publicMav, SymbolData.INT_TYPE, true, _sd1);
      VariableData notFinalWithOutValue = new VariableData("i", _publicMav, SymbolData.INT_TYPE, false, _sd1);
      
      assertFalse("Should not be assignable", _etc.canBeAssigned(finalWithValue));
      assertTrue("Should be assignable", _etc.canBeAssigned(finalWithOutValue));
      assertTrue("Should be assignable", _etc.canBeAssigned(notFinalWithValue));
      assertTrue("Should be assignable", _etc.canBeAssigned(notFinalWithOutValue));
      
      
    }
    
    
    public void testForSimpleAssignment() {
      VariableData vd4 = new VariableData("Flanders", _publicMav, SymbolData.INT_TYPE, true, _sd4);
      VariableData vd5 = new VariableData("Ned", _publicMav, _sd4, true, _sd5);
      _sd5.addVar(vd5);
      _sd4.addVar(vd4);
      _etc._vars.add(vd5);
      _etc._data = _sd5;
      
      ComplexNameReference nf = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Ned")), new Word(SourceInfo.NO_INFO, "Flanders"));
      SimpleAssignmentExpression sa = new SimpleAssignmentExpression(SourceInfo.NO_INFO, nf, new IntegerLiteral(SourceInfo.NO_INFO, 5));
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), sa.visit(_etc));
      assertEquals("Should be 0 errors", 0, errors.size());
      
      
      vd4.gotValue();
      vd4.setMav(_finalPublicMav);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), sa.visit(_etc));
      assertEquals("Should now be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", "You cannot assign a value to Flanders because it is immutable and has already been given a value",
                   errors.getLast().getFirst());
      
      
      vd4.setMav(_publicMav);
      SimpleAssignmentExpression sa2 = new SimpleAssignmentExpression(SourceInfo.NO_INFO, nf, nf);
      _sd4.setMav(_publicMav);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), sa2.visit(_etc));
      assertEquals("There should be 1 error", 1, errors.size());
      
      
      vd4.lostValue();
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), sa2.visit(_etc));
      assertEquals("There should be 2 errors", 2, errors.size());
      assertEquals("The error message should be correct", "You cannot use Flanders here, because it may not have been given a value", errors.getLast().getFirst());
      
      
      SimpleAssignmentExpression sa3 = new SimpleAssignmentExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "int")), new IntegerLiteral(SourceInfo.NO_INFO, 5));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), sa3.visit(_etc));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", "You cannot assign a value to the type int.  Perhaps you meant to create a new instance of int", errors.getLast().getFirst());
      
      
      SimpleAssignmentExpression sa4 = new SimpleAssignmentExpression(SourceInfo.NO_INFO, nf, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "int")));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), sa4.visit(_etc));
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", "You cannot use the type name int on the right hand side of an assignment.  Perhaps you meant to create a new instance of int", errors.getLast().getFirst());
      
      
      LanguageLevelVisitor llv = 
        new LanguageLevelVisitor(_etc._file, 
                                 _etc._package, 
                                 _etc._importedFiles, 
                                 _etc._importedPackages, 
                                 new LinkedList<String>(), 
                                 new Hashtable<String, Pair<TypeDefBase, LanguageLevelVisitor>>(), 
                                 new Hashtable<String, Pair<SourceInfo, LanguageLevelVisitor>>());


      ArrayData boolArray = new ArrayData(SymbolData.BOOLEAN_TYPE, llv, SourceInfo.NO_INFO);
      boolArray.setIsContinuation(false);
      symbolTable.remove("boolean[]");
      symbolTable.put("boolean[]", boolArray);
      VariableData myArrayVD = new VariableData("myArray", _publicMav, boolArray, true, _etc._data);
      _etc._vars.addLast(myArrayVD);
      
      SimpleAssignmentExpression sa5 = new SimpleAssignmentExpression(SourceInfo.NO_INFO, new ArrayAccess(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "myArray")), new IntegerLiteral(SourceInfo.NO_INFO, 5)), new BooleanLiteral(SourceInfo.NO_INFO, true));
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), sa5.visit(_etc));
      assertEquals("Should still be 4 errors", 4, errors.size());
      
      
      
    }
    
    public void testForPlusAssignmentExpression() {
      VariableData vd4 = new VariableData("Flanders", _publicMav, SymbolData.INT_TYPE, true, _sd4);
      VariableData vd5 = new VariableData("Ned", _publicMav, _sd4, true, _sd5);
      _sd5.addVar(vd5);
      _sd4.addVar(vd4);
      _etc._vars.add(vd5);
      _etc._data = _sd5;
      
      
      
      ComplexNameReference nf = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Ned")), new Word(SourceInfo.NO_INFO, "Flanders"));
      PlusAssignmentExpression pa = new PlusAssignmentExpression(SourceInfo.NO_INFO, nf, new IntegerLiteral(SourceInfo.NO_INFO, 5));
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), pa.visit(_etc));
      assertEquals("Should be 0 errors", 0, errors.size());
      
      
      vd4.lostValue();
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), pa.visit(_etc));
      assertEquals("Should now be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot use Flanders here, because it may not have been given a value",
                   errors.get(0).getFirst());
      
      
      vd4.gotValue();
      vd4.setMav(_finalPublicMav);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), pa.visit(_etc));
      assertEquals("Should now be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot assign a new value to Flanders because it is immutable and has already been given a value",
                   errors.get(1).getFirst());
      
      
      vd4.setMav(_publicMav);
      _sd4.setMav(_publicMav);
      PlusAssignmentExpression pa2 = new PlusAssignmentExpression(SourceInfo.NO_INFO, nf, nf);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), pa2.visit(_etc));
      assertEquals("There should still be 2 errors", 2, errors.size());
      
      
      vd4.lostValue();
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), pa2.visit(_etc));
      assertEquals("There should still be 2 errors", 2, errors.size());  
      assertEquals("The first error message should be correct", 
                   "You cannot use Flanders here, because it may not have been given a value", 
                   errors.get(0).getFirst());
      
      
      SymbolData stringSD = new SymbolData("java.lang.String");
      stringSD.setIsContinuation(false);
      stringSD.setPackage("java.lang");
      symbolTable.remove("java.lang.String");
      symbolTable.put("java.lang.String", stringSD);
      VariableData s = new VariableData("s", _publicMav, stringSD, true, _etc._data);
      _etc._vars.add(s);
      
      SimpleNameReference sRef = new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "s"));
      
      PlusAssignmentExpression pa3 = 
        new PlusAssignmentExpression(SourceInfo.NO_INFO, sRef, new BooleanLiteral(SourceInfo.NO_INFO, true));
      TypeData result =  pa3.visit(_etc);
      assertEquals("string concatenation with string at the front.  Should return String type", 
                   stringSD.getInstanceData(), 
                   pa3.visit(_etc));
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      PlusAssignmentExpression pa4 = 
        new PlusAssignmentExpression(SourceInfo.NO_INFO, sRef, new StringLiteral(SourceInfo.NO_INFO, "cat"));
      assertEquals("string concatenation with string on both sides.  Should return String type", 
                   stringSD.getInstanceData(), pa4.visit(_etc));
      assertEquals("Should still be 2 errors", 2, errors.size());
      
      
      vd4.gotValue();
      PlusAssignmentExpression pa5 = 
        new PlusAssignmentExpression(SourceInfo.NO_INFO, nf, new StringLiteral(SourceInfo.NO_INFO, "house "));
      assertEquals("string + concatenation with string at back.  Should give error", 
                   stringSD.getInstanceData(), 
                   pa5.visit(_etc));
      assertEquals("Should now be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", 
                   "The arguments to the Plus Assignment Operator (+=) must either include an instance of a String " + 
                   "or both be numbers.  You have specified arguments of type int and java.lang.String", 
                   errors.get(2).getFirst());
    }
    
    public void testForNumericAssignmentExpression() {
      VariableData vd4 = new VariableData("Flanders", _publicMav, SymbolData.INT_TYPE, true, _sd4);
      VariableData vd5 = new VariableData("Ned", _publicMav, _sd4, true, _sd5);
      _sd5.addVar(vd5);
      _sd4.addVar(vd4);
      _etc._vars.add(vd5);
      _etc._data = _sd5;
      
      
      ComplexNameReference nf = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Ned")), new Word(SourceInfo.NO_INFO, "Flanders"));
      NumericAssignmentExpression na = new MinusAssignmentExpression(SourceInfo.NO_INFO, nf, new IntegerLiteral(SourceInfo.NO_INFO, 5));
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), na.visit(_etc));
      assertEquals("Should be 0 errors", 0, errors.size());
      
      
      vd4.lostValue();
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), na.visit(_etc));
      assertEquals("Should now be 1 error", 1, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot use Flanders here, because it may not have been given a value",
                   errors.get(0).getFirst());
      
      
      vd4.gotValue();
      vd4.setMav(_finalPublicMav);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), na.visit(_etc));
      assertEquals("Should now be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot assign a new value to Flanders because it is immutable and has already been given a value",
                   errors.get(1).getFirst());
      
      
      vd4.setMav(_publicMav);
      _sd4.setMav(_publicMav);
      NumericAssignmentExpression na2 = new ModAssignmentExpression(SourceInfo.NO_INFO, nf, nf);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), na2.visit(_etc));
      assertEquals("There should be 2 errors", 2, errors.size());
      
      
      vd4.lostValue();
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), na2.visit(_etc));
      assertEquals("There should still be 2 errors", 2, errors.size());  
      assertEquals("The new error message should be correct", 
                   "You cannot use Flanders here, because it may not have been given a value", 
                   errors.get(0).getFirst()); 
    }
    
    public void testForIncrementExpression() {
      VariableData vd4 = new VariableData("Flanders", _publicMav, SymbolData.INT_TYPE, true, _sd4);
      VariableData vd5 = new VariableData("Ned", _publicMav, _sd4, true, _sd5);
      _sd5.addVar(vd5);
      _sd4.addVar(vd4);
      _etc._vars.add(vd5);
      _etc._data = _sd5;
      
      
      ComplexNameReference nf = new ComplexNameReference(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "Ned")), new Word(SourceInfo.NO_INFO, "Flanders"));
      PositivePrefixIncrementExpression ppi = new PositivePrefixIncrementExpression(SourceInfo.NO_INFO, nf);
      
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), ppi.visit(_etc));
      assertEquals("Should still be 0 errors", 0, errors.size());
      
      
      vd4.lostValue();
      assertEquals("Should return int instance.", SymbolData.INT_TYPE.getInstanceData(), ppi.visit(_etc));
      assertEquals("Should now be 1 errors", 1, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot use Flanders here, because it may not have been given a value",
                   errors.get(0).getFirst());
      
      
      vd4.gotValue();
      vd4.setMav(_finalPublicMav);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), ppi.visit(_etc));
      assertEquals("Should now be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot assign a new value to Flanders because it is immutable and has already been given a value",
                   errors.get(1).getFirst());
      
      
      PositivePrefixIncrementExpression ppi2 = new PositivePrefixIncrementExpression(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "int")));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), ppi2.visit(_etc));
      assertEquals("There should now be 3 errors", 3, errors.size());
      assertEquals("The error message should be correct", 
                   "You cannot increment or decrement int, because it is a class name not an instance.  " +
                   "Perhaps you meant to create a new instance of int", 
                   errors.get(2).getFirst());
      
      
      
      PositivePrefixIncrementExpression ppi3 = new PositivePrefixIncrementExpression(SourceInfo.NO_INFO, new Parenthesized(SourceInfo.NO_INFO, new SimpleNameReference(SourceInfo.NO_INFO, new Word(SourceInfo.NO_INFO, "int"))));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), ppi3.visit(_etc));
      assertEquals("There should now be 4 errors", 4, errors.size());  
      assertEquals("The error message should be correct", 
                   "You cannot increment or decrement int, because it is a class name not an instance.  " +
                   "Perhaps you meant to create a new instance of int",
                   errors.get(3).getFirst());
      
      
      
      vd4.setMav(_publicMav);
      NegativePostfixIncrementExpression npi = new NegativePostfixIncrementExpression(SourceInfo.NO_INFO, nf);
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), npi.visit(_etc));
      assertEquals("Should still be 4 errors", 4, errors.size());
      
      
      vd4.lostValue();
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), npi.visit(_etc));
      assertEquals("Should still be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot use Flanders here, because it may not have been given a value",
                   errors.get(0).getFirst());      
      
      
      vd4.gotValue();
      vd4.setMav(_finalPublicMav);
      assertEquals("Should return int instance.", SymbolData.INT_TYPE.getInstanceData(), npi.visit(_etc));
      assertEquals("Should still be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot assign a new value to Flanders because it is immutable and has already been given a value",
                   errors.get(1).getFirst());
      
      
      
      NegativePostfixIncrementExpression npi2 = 
        new NegativePostfixIncrementExpression(SourceInfo.NO_INFO, 
                                               new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                       new Word(SourceInfo.NO_INFO, "int")));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), npi2.visit(_etc));
      assertEquals("There should be 5 errors", 5, errors.size());
      assertEquals("The error message should be correct", 
                   "You cannot increment or decrement int, because it is a class name not an instance.  Perhaps you " + 
                   "meant to create a new instance of int", 
                   errors.get(4).getFirst());
      
      
      NegativePostfixIncrementExpression npi3 = 
        new NegativePostfixIncrementExpression(SourceInfo.NO_INFO, 
                                               new Parenthesized(SourceInfo.NO_INFO, 
                                                                 new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                                         new Word(SourceInfo.NO_INFO, "int"))));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), npi3.visit(_etc));
      assertEquals("There should be 6 errors", 6, errors.size());  
      assertEquals("The error message should be correct", 
                   "You cannot increment or decrement int, because it is a class name not an instance.  " + 
                   "Perhaps you meant to create a new instance of int", 
                   errors.get(5).getFirst());
      
      
      
      vd4.setMav(_publicMav);
      PositivePrefixIncrementExpression ppi4 = 
        new PositivePrefixIncrementExpression(SourceInfo.NO_INFO, 
                                              new Parenthesized(SourceInfo.NO_INFO, 
                                                                new NegativePrefixIncrementExpression(SourceInfo.NO_INFO, nf)));
      assertEquals("Should return null", null, ppi4.visit(_etc));
      assertEquals("Should have added 1 error", 7, errors.size());
      assertEquals("Should have correct error message",
                   "You cannot assign a value to an increment expression", 
                   errors.getLast().getFirst());
      

      VariableData s = new VariableData("s", _publicMav, SymbolData.BOOLEAN_TYPE, true, _etc._data);
      _etc._vars.addLast(s);
      PositivePrefixIncrementExpression ppi5 = 
        new PositivePrefixIncrementExpression(SourceInfo.NO_INFO, 
                                              new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                      new Word(SourceInfo.NO_INFO, "s")));
      assertEquals("Should return boolean instance", SymbolData.BOOLEAN_TYPE.getInstanceData(), ppi5.visit(_etc));
      assertEquals("Should have added 1 error", 8, errors.size());
      assertEquals("Should have correct error message", 
                   "You cannot increment or decrement something that is not a number type.  You have specified " +
                   "something of type boolean", errors.get(7).getFirst());
      
      
      PositivePrefixIncrementExpression ppi6 = new PositivePrefixIncrementExpression(SourceInfo.NO_INFO, new Parenthesized(SourceInfo.NO_INFO, new Parenthesized(SourceInfo.NO_INFO, nf)));
      assertEquals("Should return int instance", SymbolData.INT_TYPE.getInstanceData(), ppi6.visit(_etc));
      assertEquals("Should still be 8 errors", 8, errors.size());
    }
    
    
    public void testForSimpleAnonymousClassInstantiation() {
      AnonymousClassInstantiation basic = new SimpleAnonymousClassInstantiation(SourceInfo.NO_INFO, new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]), 
                                                                                new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]),
                                                                                new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      
      SymbolData object = new SymbolData("java.lang.Object");
      object.setIsContinuation(false);
      object.setPackage("java.lang");
      object.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      MethodData cdObj = 
        new MethodData("Object", _publicMav, new TypeParameter[0], object, new VariableData[0], new String[0], object, basic);
      object.addMethod(cdObj);
      
      
      symbolTable.put("java.lang.Object", object);
      
      








      
      _sd1.setAnonymousInnerClassNum(0);
      
      
      SymbolData anon1 = new SymbolData("i.like.monkey$1");
      anon1.setIsContinuation(false);
      anon1.setPackage("i.like");
      anon1.setMav(_publicMav);
      anon1.setOuterData(_sd1);
      _sd1.addInnerClass(anon1);
      
      assertEquals("Should return anon1 instance", anon1.getInstanceData(), basic.visit(_etc));
      
      assertEquals("Should be no errors", 0, errors.size());
      
      
      VariableDeclaration vdecl = new VariableDeclaration(SourceInfo.NO_INFO,
                                                          _packageMav,
                                                          new VariableDeclarator[] {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                            new Word (SourceInfo.NO_INFO, "field1")),
          new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                              new PrimitiveType(SourceInfo.NO_INFO, "boolean"), 
                                              new Word (SourceInfo.NO_INFO, "field2"))});      
      
      PrimitiveType intt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      UninitializedVariableDeclarator uvd = new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "i"));
      FormalParameter param = new FormalParameter(SourceInfo.NO_INFO, new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "j")), false);
      BracedBody bb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {new VariableDeclaration(SourceInfo.NO_INFO,  _packageMav, new UninitializedVariableDeclarator[]{uvd}), new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5))});
      
      ConcreteMethodDef cmd1 = new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                                                     intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                                                     new ReferenceType[0], bb);
      BracedBody classBb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] { vdecl, cmd1 });
      
      SimpleAnonymousClassInstantiation  complicated = new SimpleAnonymousClassInstantiation(SourceInfo.NO_INFO, 
                                                                                             new ClassOrInterfaceType(SourceInfo.NO_INFO, "name", new Type[0]), 
                                                                                             new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]), 
                                                                                             classBb);
      SymbolData sd = new SymbolData("name");
      sd.setIsContinuation(false);
      sd.setMav(_publicMav);
      symbolTable.put("name", sd);
      SymbolData anon2 = new SymbolData("i.like.monkey$2");
      anon2.setIsContinuation(false);
      anon2.setPackage("i.like");
      anon2.setSuperClass(sd);
      anon2.setOuterData(_sd1);
      _sd1.addInnerClass(anon2);
      
      VariableData vd1 = new VariableData("field1", _publicMav, SymbolData.DOUBLE_TYPE, true, sd);
      VariableData vd2 = new VariableData("field2", _publicMav, SymbolData.DOUBLE_TYPE, true, sd);
      sd.addVar(vd1);
      sd.addVar(vd2);
      
      MethodData md = 
        new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, new VariableData[] {
        new VariableData("j", _finalMav, SymbolData.INT_TYPE, true, null)}, new String[0], sd, cmd1);
      md.getParams()[0].setEnclosingData(md);
      MethodData cd = 
        new MethodData("name", _publicMav, new TypeParameter[0], sd, new VariableData[0], new String[0], sd, cmd1);
      anon2.addMethod(md);
      sd.addMethod(cd);
      
      assertEquals("Should return anon2.  ", anon2.getInstanceData(), complicated.visit(_etc));
      assertEquals("There should be no errors", 0, errors.size());
      
      _etc._data.addVar(new VariableData("myAnon", _publicMav, sd, false, _etc._data));
      
      
      
      _sd1.setAnonymousInnerClassNum(1);
      symbolTable.put("int", SymbolData.INT_TYPE);
      VariableDeclaration vd =
        new VariableDeclaration(SourceInfo.NO_INFO, _publicMav, new VariableDeclarator[] { 
        new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                          new ClassOrInterfaceType(SourceInfo.NO_INFO, "name", new Type[0]), 
                                          new Word(SourceInfo.NO_INFO, "myAnon"), complicated)});
      vd.visit(_etc);
      assertEquals("There should still be no errors", 0, errors.size());
      
      
      _sd1.setAnonymousInnerClassNum(1);
      MethodInvocation mie = 
        new ComplexMethodInvocation(SourceInfo.NO_INFO, 
                                    complicated, 
                                    new Word(SourceInfo.NO_INFO, "myMethod"),
                                    new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[] { 
        new IntegerLiteral(SourceInfo.NO_INFO, 5)}));
      assertEquals("Should return int", SymbolData.INT_TYPE.getInstanceData(), mie.visit(_etc));
      assertEquals("There should still be no errors", 0, errors.size());
      

      _sd1.setAnonymousInnerClassNum(1);
      
      Expression nr = new ComplexNameReference(SourceInfo.NO_INFO, complicated, new Word(SourceInfo.NO_INFO, "field1"));
      assertEquals("Should return double", SymbolData.DOUBLE_TYPE.getInstanceData(), nr.visit(_etc));
      assertEquals("There should be no errors...still!", 0, errors.size());
      

      _sd1.setAnonymousInnerClassNum(1);
      sd.setMav(_publicAbstractMav);
      sd.addMethod(new MethodData("yeah", _abstractMav, new TypeParameter[0], SymbolData.BOOLEAN_TYPE, 
                                  new VariableData[0], new String[0], sd, cmd1));
      
      assertEquals("Should return anon2 instance", anon2.getInstanceData(), complicated.visit(_etc));
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", 
                   "This anonymous inner class must override the abstract method: yeah() in name", 
                   errors.get(0).getFirst());
      
      
      SimpleAnonymousClassInstantiation nestedNonStatic = 
        new SimpleAnonymousClassInstantiation(SourceInfo.NO_INFO, 
                                              new ClassOrInterfaceType(SourceInfo.NO_INFO, "A.B", new Type[0]), 
                                              new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]), 
                                              new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      SymbolData a = new SymbolData("A");
      a.setIsContinuation(false);
      SymbolData b = new SymbolData("A$B");
      b.setIsContinuation(false);
      b.setOuterData(a);
      a.addInnerClass(b);
      MethodData consb = new MethodData("B", _publicMav, new TypeParameter[0], b, 
                                        new VariableData[0], 
                                        new String[0], 
                                        b,
                                        null);
      b.addMethod(consb);
      symbolTable.put("A", a);
      a.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      b.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      
      SymbolData anon3 = new SymbolData("i.like.monkey$3");
      anon3.setIsContinuation(false);
      anon3.setMav(_publicMav);
      _sd1.addInnerClass(anon3);
      anon3.setOuterData(_sd1);
      
      
      assertEquals("Should return anon3", anon3.getInstanceData(), nestedNonStatic.visit(_etc));
      assertEquals("Should be 2 errors", 2, errors.size());
      
      assertEquals("Error message should be correct", 
                   "A.B is not a static inner class, and thus cannot be instantiated from this context.  "
                     + "Perhaps you meant to use an instantiation of the form new A().new B()",
                   errors.getLast().getFirst());
      
      _sd1.setAnonymousInnerClassNum(2);
      
      b.addModifier("static");
      assertEquals("Should return anon3", anon3.getInstanceData(), nestedNonStatic.visit(_etc));
      assertEquals("Should still be just 2 errors", 2, errors.size());
      
    }
    
    public void testForComplexAnonymousClassInstantiation() {
      AnonymousClassInstantiation basic = 
        new ComplexAnonymousClassInstantiation(SourceInfo.NO_INFO, 
                                               new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                       new Word(SourceInfo.NO_INFO, "bob")),
                                               new ClassOrInterfaceType(SourceInfo.NO_INFO, "Object", new Type[0]), 
                                               new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]),
                                               new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      VariableData bob = new VariableData("bob", _publicMav, _sd2, true, _sd1);
      _etc._vars.add(bob);
      
      SymbolData object = new SymbolData("java.lang.Object");
      object.setIsContinuation(false);
      object.setPackage("java.lang");
      object.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      MethodData cdObj = 
        new MethodData("Object", _publicMav, new TypeParameter[0], object, new VariableData[0], new String[0], object, basic);
      object.addMethod(cdObj);
      
      _sd2.addInnerClass(object);
      object.setOuterData(_sd2);
      
      











      
      _sd1.setAnonymousInnerClassNum(0);
      
      
      SymbolData anon1 = new SymbolData("i.like.monkey$1");
      anon1.setIsContinuation(false);
      anon1.setPackage("i.like");
      anon1.setMav(_publicMav);
      anon1.setOuterData(_sd1);
      _sd1.addInnerClass(anon1);
      
      assertEquals("Should return anon1 instance", anon1.getInstanceData(), basic.visit(_etc));
      
      assertEquals("Should be no errors", 0, errors.size());
      
      
      VariableDeclaration vdecl = new VariableDeclaration(SourceInfo.NO_INFO,
                                                          _packageMav,
                                                          new VariableDeclarator[] {
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                            new PrimitiveType(SourceInfo.NO_INFO, "double"), 
                                            new Word (SourceInfo.NO_INFO, "field1")),
          new UninitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                              new PrimitiveType(SourceInfo.NO_INFO, "boolean"), 
                                              new Word (SourceInfo.NO_INFO, "field2"))});      
      
      PrimitiveType intt = new PrimitiveType(SourceInfo.NO_INFO, "int");
      UninitializedVariableDeclarator uvd = 
        new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, new Word(SourceInfo.NO_INFO, "i"));
      FormalParameter param = 
        new FormalParameter(SourceInfo.NO_INFO, 
                            new UninitializedVariableDeclarator(SourceInfo.NO_INFO, intt, 
                                                                new Word(SourceInfo.NO_INFO, "j")), false);
      BracedBody bb = 
        new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] {
        new VariableDeclaration(SourceInfo.NO_INFO, _packageMav, new UninitializedVariableDeclarator[]{uvd}), 
          new ValueReturnStatement(SourceInfo.NO_INFO, new IntegerLiteral(SourceInfo.NO_INFO, 5))});
      
      ConcreteMethodDef cmd1 = 
        new ConcreteMethodDef(SourceInfo.NO_INFO, _publicMav, new TypeParameter[0], 
                              intt, new Word(SourceInfo.NO_INFO, "myMethod"), new FormalParameter[] {param}, 
                              new ReferenceType[0], bb);
      BracedBody classBb = new BracedBody(SourceInfo.NO_INFO, new BodyItemI[] { vdecl, cmd1 });
      
      ComplexAnonymousClassInstantiation  complicated = 
        new ComplexAnonymousClassInstantiation(SourceInfo.NO_INFO, 
                                               new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                       new Word(SourceInfo.NO_INFO, "bob")),
                                               new ClassOrInterfaceType(SourceInfo.NO_INFO, "name", new Type[0]), 
                                               new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]), 
                                               classBb);
      SymbolData sd = new SymbolData("name");
      sd.setIsContinuation(false);
      sd.setMav(_publicMav);
      symbolTable.put("name", sd);
      SymbolData anon2 = new SymbolData("i.like.monkey$2");
      anon2.setIsContinuation(false);
      anon2.setPackage("i.like");
      anon2.setSuperClass(null);
      anon2.setOuterData(_sd1);
      _sd1.addInnerClass(anon2);
      
      VariableData vd1 = new VariableData("field1", _publicMav, SymbolData.DOUBLE_TYPE, true, sd);
      VariableData vd2 = new VariableData("field2", _publicMav, SymbolData.DOUBLE_TYPE, true, sd);
      sd.addVar(vd1);
      sd.addVar(vd2);
      
      MethodData md = 
        new MethodData("myMethod", _publicMav, new TypeParameter[0], SymbolData.INT_TYPE, new VariableData[] {
        new VariableData("j", _finalMav, SymbolData.INT_TYPE, true, null)}, new String[0], sd, cmd1);
      md.getParams()[0].setEnclosingData(md);
      MethodData cd = 
        new MethodData("name", _publicMav, new TypeParameter[0], sd, new VariableData[0], new String[0], sd, cmd1);
      anon2.addMethod(md);
      sd.addMethod(cd);
      
      
      assertEquals("Should return anon2.  ", anon2.getInstanceData(), complicated.visit(_etc));
      assertEquals("There should be no errors", 0, errors.size());
      
      _etc._data.addVar(new VariableData("myAnon", _publicMav, sd, false, _etc._data));
      
      
      
      _sd1.setAnonymousInnerClassNum(1);
      symbolTable.put("int", SymbolData.INT_TYPE);
      VariableDeclaration vd = 
        new VariableDeclaration(SourceInfo.NO_INFO, _publicMav, new VariableDeclarator[] { 
        new InitializedVariableDeclarator(SourceInfo.NO_INFO, 
                                          new ClassOrInterfaceType(SourceInfo.NO_INFO, "name", new Type[0]),
                                          new Word(SourceInfo.NO_INFO, "myAnon"), 
                                          complicated)});
      vd.visit(_etc);
      assertEquals("There should still be no errors", 0, errors.size());
      
      
      _sd1.setAnonymousInnerClassNum(1);
      MethodInvocation mie = 
        new ComplexMethodInvocation(SourceInfo.NO_INFO, complicated, 
                                    new Word(SourceInfo.NO_INFO, "myMethod"),
                                    new ParenthesizedExpressionList(SourceInfo.NO_INFO, 
                                                                    new Expression[] { 
        new IntegerLiteral(SourceInfo.NO_INFO, 5)}));
      assertEquals("Should return int", SymbolData.INT_TYPE.getInstanceData(), mie.visit(_etc));
      assertEquals("There should still be no errors", 0, errors.size());
      

      _sd1.setAnonymousInnerClassNum(1);
      
      Expression nr = new ComplexNameReference(SourceInfo.NO_INFO, complicated, new Word(SourceInfo.NO_INFO, "field1"));
      assertEquals("Should return double", SymbolData.DOUBLE_TYPE.getInstanceData(), nr.visit(_etc));
      assertEquals("There should be no errors...still!", 0, errors.size());
      
      
      _sd1.setAnonymousInnerClassNum(1);
      sd.setMav(_publicAbstractMav);
      sd.addMethod(new MethodData("yeah", _abstractMav, new TypeParameter[0], SymbolData.BOOLEAN_TYPE, 
                                  new VariableData[0], new String[0], sd, cmd1));
      
      assertEquals("Should return anon2 instance", anon2.getInstanceData(), complicated.visit(_etc));
      assertEquals("There should be one error", 1, errors.size());
      assertEquals("The error message should be correct", 
                   "This anonymous inner class must override the abstract method: yeah() in name", 
                   errors.get(0).getFirst());
      

      ComplexAnonymousClassInstantiation nestedNonStatic = 
        new ComplexAnonymousClassInstantiation(SourceInfo.NO_INFO, 
                                               new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                       new Word(SourceInfo.NO_INFO, "a")),
                                               new ClassOrInterfaceType(SourceInfo.NO_INFO, "B", new Type[0]), 
                                               new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]), 
                                               new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      
      SymbolData a = new SymbolData("A");
      a.setIsContinuation(false);
      SymbolData b = new SymbolData("A$B");
      b.setIsContinuation(false);
      b.setOuterData(a);
      a.addInnerClass(b);
      MethodData consb = 
        new MethodData("B", _publicMav, new TypeParameter[0], b, new VariableData[0], new String[0], b, null);
      b.addMethod(consb);
      symbolTable.put("A", a);
      a.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      b.setMav(new ModifiersAndVisibility(SourceInfo.NO_INFO, new String[] {"public"}));
      
      SymbolData anon3 = new SymbolData("i.like.monkey$3");
      anon3.setIsContinuation(false);
      anon3.setMav(_publicMav);
      _sd1.addInnerClass(anon3);
      anon3.setOuterData(_sd1);
      VariableData aVar = new VariableData("a", _publicMav, a, true, _sd1);
      _etc._vars.add(aVar);
      
      
      assertEquals("Should return anon3", anon3.getInstanceData(), nestedNonStatic.visit(_etc));
      assertEquals("Should still be just 1 error", 1, errors.size());      
      
      
      _sd1.setAnonymousInnerClassNum(2);
      a.setMav(_privateMav);
      assertEquals("Should return anon3", anon3.getInstanceData(), nestedNonStatic.visit(_etc));
      assertEquals("Should be 2 errors", 2, errors.size());
      assertEquals("Error message should be correct", 
                   "The class or interface A is private and cannot be accessed from i.like.monkey", 
                   errors.getLast().getFirst());
      a.setMav(_publicMav);
      
      
      _sd1.setAnonymousInnerClassNum(2);
      b.setMav(_publicStaticMav);
      assertEquals("Should return anon3", anon3.getInstanceData(), nestedNonStatic.visit(_etc));
      assertEquals("Should be 3 errors", 3, errors.size());
      assertEquals("Error message should be correct", 
                   "You cannot instantiate a static inner class or interface with this syntax.  Instead, try new A.B()", 
                   errors.getLast().getFirst());
      
      
      ComplexAnonymousClassInstantiation nested = 
        new ComplexAnonymousClassInstantiation(SourceInfo.NO_INFO, 
                                               new SimpleNameReference(SourceInfo.NO_INFO, 
                                                                       new Word(SourceInfo.NO_INFO, "A")),
                                               new ClassOrInterfaceType(SourceInfo.NO_INFO, "B", new Type[0]), 
                                               new ParenthesizedExpressionList(SourceInfo.NO_INFO, new Expression[0]), 
                                               new BracedBody(SourceInfo.NO_INFO, new BodyItemI[0]));
      _sd1.setAnonymousInnerClassNum(2);
      b.setMav(_publicMav);
      assertEquals("Should return anon3", anon3.getInstanceData(), nested.visit(_etc));
      assertEquals("Should be 4 errors", 4, errors.size());
      assertEquals("Error message should be correct", 
                   "The constructor of a non-static inner class can only be called on an instance of its "
                     + "containing class (e.g. new A().new B())", 
                   errors.getLast().getFirst());
      
    }
  }
}
