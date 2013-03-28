



package edu.rice.cs.dynamicjava.interpreter;

import java.util.*;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Lambda2;
import edu.rice.cs.plt.lambda.WrappedException;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.symbol.*;
import edu.rice.cs.dynamicjava.symbol.TypeSystem.*;
import edu.rice.cs.dynamicjava.symbol.type.*;

import koala.dynamicjava.tree.*;
import koala.dynamicjava.tree.visitor.*;
import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.interpreter.TypeUtil;

import static koala.dynamicjava.interpreter.NodeProperties.*;
import static edu.rice.cs.plt.debug.DebugUtil.debug;



public class ExpressionChecker {
  
  private final TypeContext context;
  private final TypeSystem ts;
  private final Options opt;

  public ExpressionChecker(TypeContext ctx, Options options) {
    context = ctx;
    ts = options.typeSystem();
    opt = options;
  }
  
  public Type check(Expression e) {
    return e.acceptVisitor(new ExpressionVisitor(Option.<Type>none()));
  }
  
  public Type check(Expression e, Type expected) {
    return e.acceptVisitor(new ExpressionVisitor(Option.some(expected)));
  }
  
  public Type check(Expression e, Option<Type> expected) {
    return e.acceptVisitor(new ExpressionVisitor(expected));
  }
  
  public Iterable<Type> checkList(Iterable<? extends Expression> l) {
    return IterUtil.mapSnapshot(l, new ExpressionVisitor(Option.<Type>none()));
  }
  
  public Iterable<Type> checkList(Iterable<? extends Expression> l, Type expected) {
    return IterUtil.mapSnapshot(l, new ExpressionVisitor(Option.some(expected)));
  }
  
  public Iterable<Type> checkList(Iterable<? extends Expression> l, Option<Type> expected) {
    return IterUtil.mapSnapshot(l, new ExpressionVisitor(expected));
  }
  
  private Type checkTypeName(TypeName t) {
    return new TypeNameChecker(context, opt).check(t);
  }
  
  private Iterable<Type> checkTypeNameList(Iterable<? extends TypeName> l) {
    return new TypeNameChecker(context, opt).checkList(l);
  }
  
  
  public void checkConstructorCall(ConstructorCall node) {
    if (node.getExpression() != null) {
      throw new ExecutionError("not.implemented", node);
    }
    
    Iterable<? extends Expression> args = node.getArguments();
    checkList(args);
    Iterable<Type> targs = IterUtil.empty();
    
    
    
    Type type;
    if (node.isSuper()) { type = context.getThis().immediateSuperclass(); }
    else { type = SymbolUtil.thisType(context.getThis()); }
    if (type == null) {
      throw new IllegalArgumentException("Can't check a ConstructorCall in this context");
    }
    
    try {
      ConstructorInvocation inv = ts.lookupConstructor(type, targs, args, Option.<Type>none(), context.accessModule());
      
      
      
      
      
      DJConstructor k = inv.constructor();
      if (k.accessibility().equals(Access.PRIVATE) && !k.accessModule().equals(context.accessModule())) {
        setErrorStrings(node, ts.typePrinter().print(type));
        throw new ExecutionError("inaccessible.super.call", node);
      }
      checkThrownExceptions(inv.thrown(), node);
      node.setArguments(CollectUtil.makeList(inv.args()));
      setConstructor(node, k);
      setType(node, type);
    }
    catch (InvalidTypeArgumentException e) {
      throw new ExecutionError("type.argument", node);
    }
    catch (UnmatchedLookupException e) {
      throw unmatchedFunctionError("constructor", e, node, type, "", targs, args, Option.<Type>none(), false);
    }
  }
  
  
  public DJField checkEnumSwitchCase(Expression exp, Type enumT) {
    if (!(exp instanceof AmbiguousName)) {
      throw new ExecutionError("invalid.enum.constant", exp);
    }
    List<IdentifierToken> ids = ((AmbiguousName) exp).getIdentifiers();
    if (ids.size() != 1) {
      throw new ExecutionError("invalid.enum.constant", exp);
    }
    String name = ids.get(0).image();
    Expression translation = new SimpleFieldAccess(name);
    setTranslation(exp, translation);
    try {
      
      
      
      StaticFieldReference ref = ts.lookupStaticField(enumT, name, context.accessModule());
      setField(translation, ref.field());
      Type t = ts.capture(ref.type());
      if (!ts.isSubtype(t, enumT)) {
        throw new ExecutionError("invalid.enum.constant", exp);
      }
      addRuntimeCheck(translation, t, ref.field().type());
      setType(translation, t);
      setType(exp, t);
      if (hasValue(translation)) { setValue(exp, getValue(translation)); }
      return ref.field();
    }
    catch (UnmatchedLookupException e) {
      throw new ExecutionError("invalid.enum.constant", exp);
    }
  }
  
  
  private ExecutionError unmatchedFunctionError(String kind, UnmatchedLookupException e, Node node, Type type,
                                                String name, Iterable<? extends Type> targs,
                                                Iterable<? extends Expression> args, Option<Type> expected,
                                                boolean onlyStatic) {
    final TypePrinter printer = ts.typePrinter();
    String error = ((e.matches() > 1) ? "ambiguous." : "no.such.") + kind;
    Iterable<? extends Function> candidates = IterUtil.empty();
    boolean noMatch = false;
    if (e instanceof UnmatchedFunctionLookupException) {
      candidates = ((UnmatchedFunctionLookupException) e).candidates();
      if (IterUtil.isEmpty(candidates)) { noMatch = true; }
    }
    else if (e instanceof AmbiguousFunctionLookupException) {
      candidates = ((AmbiguousFunctionLookupException) e).candidates();
    }
    if (error.equals("no.such.method") && noMatch) { error += ".name"; }
    else {
      if (!IterUtil.isEmpty(targs)) { error += ".poly"; }
      if (expected.isSome()) { error += ".expected"; }
      if (!IterUtil.isEmpty(candidates)) { error += ".candidates"; }
    }
    String typeS = (onlyStatic ? "static " : "") + printer.print(type);
    String expectedS = expected.isSome() ? printer.print(expected.unwrap()) : "";
    String candidatesS;
    if (IterUtil.sizeOf(candidates, 2) == 1) {
     candidatesS = printer.print(IterUtil.first(candidates));
    }
    else {
      String prefix = "\n        "; 
      Lambda<Function, String> printSig = new Lambda<Function, String>() {
        public String value(Function f) { return printer.print(f); }
      };
      candidatesS = IterUtil.toString(IterUtil.map(candidates, printSig), prefix, prefix, "");
    }
    setErrorStrings(node, typeS, name, printer.print(targs), nodeTypesString(args, printer), expectedS, candidatesS);
    throw new ExecutionError(error, node);
  }
  
  
  private void checkThrownExceptions(Iterable<? extends Type> thrownTypes, Node node) {
    Iterable<Type> allowed = IterUtil.compose(TypeSystem.RUNTIME_EXCEPTION,
                                              context.getDeclaredThrownTypes());
    for (Type thrown : thrownTypes) {
      if (ts.isAssignable(TypeSystem.EXCEPTION, thrown)) {
        boolean valid = false;
        for (Type t : allowed) {
          if (ts.isAssignable(t, thrown)) { valid = true; break; }
        }
        if (!valid) {
          setErrorStrings(node, ts.typePrinter().print(thrown));
          throw new ExecutionError("uncaught.exception", node);
        }
      }
    }
  }
  
  private void addRuntimeCheck(Node node, Type expectedType, Type declaredActualType) {
    if (!ts.isSubtype(ts.erase(declaredActualType), ts.erase(expectedType))) {
      setCheckedType(node, ts.erasedClass(expectedType));
    }
  }
  
  private String nodeTypesString(Iterable<? extends Node> nodes, TypePrinter printer) {
    return printer.print(IterUtil.map(nodes, NODE_TYPE));
  }
  
  
  
  private class ExpressionVisitor extends AbstractVisitor<Type> implements Lambda<Expression, Type> {
  
    
    private final Option<Type> expected;
    
    public ExpressionVisitor(Option<Type> exp) { expected = exp; }
    
    public Type value(Expression e) { return e.acceptVisitor(this); }
    
    
    @Override public Type visit(AmbiguousName node) {
      Node resolved = resolveAmbiguousName(node);
      if (resolved instanceof ReferenceTypeName) {
        setErrorStrings(node, ((ReferenceTypeName) resolved).getRepresentation());
        throw new ExecutionError("undefined.name", node);
      }
      else {
        Expression resolvedExp = (Expression) resolved;
        setTranslation(node, resolvedExp);
        resolvedExp.acceptVisitor(this);
        
        
        if (hasVariableType(resolvedExp)) { setVariableType(node, getVariableType(resolvedExp)); }
        if (hasValue(resolvedExp)) { setValue(node, getValue(resolvedExp)); }
        if (hasField(resolvedExp)) { setField(node, getField(resolvedExp)); }
        if (hasVariable(resolvedExp)) { setVariable(node, getVariable(resolvedExp)); }
        return setType(node, getType(resolvedExp));
      }
    }
    
    
    private Node resolveAmbiguousName(AmbiguousName node) {
      Iterator<IdentifierToken> ids = node.getIdentifiers().iterator();
      IdentifierToken first = ids.next();
      Expression resultExp = null;
      
      if (context.localVariableExists(first.image(), ts)) {
        resultExp = new VariableAccess(first.image(), first.getSourceInfo()); 
      }
      else if (context.fieldExists(first.image(), ts)) {
        resultExp = new SimpleFieldAccess(first.image(), first.getSourceInfo());
      }
      else {
        
        IdentifierToken last = first;
        String className = first.image();
        List<IdentifierToken> classIds = new LinkedList<IdentifierToken>();
        classIds.add(first);
        Type classType;
        
        while (!context.typeExists(className, ts)) {
          if (!ids.hasNext()) {
            setErrorStrings(node, className);
            throw new ExecutionError("undefined.name", node);
          }
          last = ids.next();
          className += "." + last.image();
          classIds.add(last);
        }
        try {
          DJClass c = context.getTopLevelClass(className, ts);
          if (c != null) {
            classType = ts.makeClassType(c);
          }
          else {
            classType = context.getTypeVariable(className, ts);
            if (classType == null) {
              
              Type outer = context.typeContainingMemberClass(className, ts);
              classType = ts.lookupStaticClass(outer, className, IterUtil.<Type>empty(), context.accessModule());
              
            }
          }
        }
        catch (AmbiguousNameException e) {
          setErrorStrings(node, className);
          throw new ExecutionError("ambiguous.name", node);
        }
        catch (InvalidTypeArgumentException e) { throw new ExecutionError("type.argument.arity", node); }
        catch (UnmatchedLookupException e) {
          if (e.matches() == 0) { throw new ExecutionError("undefined.name.noinfo", node); }
          else {
            setErrorStrings(node, className);
            throw new ExecutionError("ambiguous.name", node);
          }
        }
        
        
        while (ids.hasNext() && resultExp == null) {
          IdentifierToken memberName = ids.next();
          if (ts.containsField(classType, memberName.image(), context.accessModule())) {
            ReferenceTypeName rt = new ReferenceTypeName(classIds, SourceInfo.span(first, last)); 
            resultExp = new StaticFieldAccess(rt, memberName.image(), SourceInfo.span(first, memberName)); 
          }
          else if (ts.containsClass(classType, memberName.image(), context.accessModule())) {
            last = memberName;
            className += "." + last.image();
            classIds.add(last);
            try {
              ClassType memberType = ts.lookupStaticClass(classType, memberName.image(), IterUtil.<Type>empty(),
                                                          context.accessModule());
              classType = memberType;
            }
            catch (InvalidTypeArgumentException e) { throw new ExecutionError("type.argument.arity", node); }
            catch (UnmatchedLookupException e) {
              if (e.matches() == 0) { throw new ExecutionError("undefined.name.noinfo", node); }
              else {
                setErrorStrings(node, memberName.image());
                throw new ExecutionError("ambiguous.name", node);
              }
            }
            
          }
          else {
            setErrorStrings(node, ts.typePrinter().print(classType), memberName.image());
            throw new ExecutionError("no.such.member", node);
          }
        }
        
        if (resultExp == null) { 
          return new ReferenceTypeName(classIds, SourceInfo.span(first, last));
        }
      }
      
      
      while (ids.hasNext()) {
        IdentifierToken field = ids.next();
        resultExp = new ObjectFieldAccess(resultExp, field.image(), SourceInfo.span(first, field));
      }
      return resultExp;
    }
    
    
    
    @Override public Type visit(Literal node) {
      setValue(node, node.getValue());
      if (node instanceof NullLiteral) { return setType(node, TypeSystem.NULL); }
      else if (node instanceof StringLiteral) { return setType(node, TypeSystem.STRING); }
      else { return setType(node, SymbolUtil.typeOfPrimitiveClass(node.getType())); }
    }
    
    private DJClass resolveThis(Option<String> outerName, Node node) {
      DJClass result;
      if (outerName.isNone()) {
        result = context.getThis();
        if (result == null) { throw new ExecutionError("this.undefined", node); }
      }
      else {
        result = context.getThis(outerName.unwrap());
        if (result == null) {
          setErrorStrings(node, outerName.unwrap());
          throw new ExecutionError("undefined.class", node);
        }
      }
      return result;
    }
    
    
    @Override public Type visit(ThisExpression node) {
      DJClass thisC = resolveThis(node.getClassName(), node);
      setDJClass(node, thisC);
      return setType(node, SymbolUtil.thisType(thisC));
    }
    
    
    @Override public Type visit(VariableAccess node) {
      LocalVariable v = context.getLocalVariable(node.getVariableName(), ts);
      setVariable(node, v);
      setVariableType(node, v.type());
      return setType(node, ts.capture(v.type()));
    }
    
    
    @Override public Type visit(SimpleFieldAccess node) {
      try {
        ClassType t = context.typeContainingField(node.getFieldName(), ts);
        if (t == null) {
          setErrorStrings(node, node.getFieldName());
          throw new ExecutionError("undefined.name", node);
        }
        DJClass enclosingThis = context.getThis(t, ts);
        boolean onlyStatic = (enclosingThis == null);
        FieldReference ref;
        if (onlyStatic) {
          ref = ts.lookupStaticField(t, node.getFieldName(), context.accessModule());
        }
        else {
          Expression obj = TypeUtil.makeEmptyExpression(node);
          setType(obj, t);
          ref = ts.lookupField(obj, node.getFieldName(), context.accessModule());
        }
        setField(node, ref.field());
        Option<Object> val = ref.field().constantValue();
        if (val.isSome()) { setValue(node, val.unwrap()); }
        setVariableType(node, ref.type());
        if (!onlyStatic) { setDJClass(node, enclosingThis); }
        Type result = ts.capture(ref.type());
        addRuntimeCheck(node, result, ref.field().type());
        return setType(node, result);
      }
      catch (AmbiguousNameException e) {
        setErrorStrings(node, node.getFieldName());
        throw new ExecutionError("ambiguous.name", node);
      }
      catch (UnmatchedLookupException e) {
        if (e.matches() == 0) { throw new ExecutionError("undefined.name.noinfo", node); }
        else {
          setErrorStrings(node, node.getFieldName());
          throw new ExecutionError("ambiguous.name", node);
        }
      }
    }
    
    
    @Override public Type visit(ObjectFieldAccess node) {
      Expression receiver = node.getExpression();
      Type receiverT = check(receiver);
      try {
        ObjectFieldReference ref = ts.lookupField(receiver, node.getFieldName(), context.accessModule());
        node.setExpression(ref.object());
        setField(node, ref.field());
        setVariableType(node, ref.type());
        Type result = ts.capture(ref.type());
        addRuntimeCheck(node, result, ref.field().type());
        return setType(node, result);
      }
      catch (UnmatchedLookupException e) {
        setErrorStrings(node, ts.typePrinter().print(receiverT), node.getFieldName());
        if (e.matches() > 1) { throw new ExecutionError("ambiguous.field", node); }
        else { throw new ExecutionError("no.such.field", node); }
      }
    }
    
    
    @Override public Type visit(SuperFieldAccess node) {
      DJClass c = resolveThis(node.getClassName(), node);
      Type t = c.immediateSuperclass();
      if (t == null) {
        throw new ExecutionError("super.undefined", node);
      }
      Expression obj = TypeUtil.makeEmptyExpression(node);
      setType(obj, t);
      try {
        FieldReference ref = ts.lookupField(obj, node.getFieldName(), context.accessModule());
        setField(node, ref.field());
        setDJClass(node, c);
        setVariableType(node, ref.type());
        Type result = ts.capture(ref.type());
        addRuntimeCheck(node, result, ref.field().type());
        return setType(node, result);
      }
      catch (UnmatchedLookupException e) {
        setErrorStrings(node, ts.typePrinter().print(t), node.getFieldName());
        if (e.matches() > 1) { throw new ExecutionError("ambiguous.field", node); }
        else { throw new ExecutionError("no.such.field", node); }
      }
    }
    
    
    @Override public Type visit(StaticFieldAccess node) {
      Type t = checkTypeName(node.getFieldType());
      try {
        FieldReference ref = ts.lookupStaticField(t, node.getFieldName(), context.accessModule());
        setField(node, ref.field());
        Option<Object> val = ref.field().constantValue();
        if (val.isSome()) { setValue(node, val.unwrap()); }
        setVariableType(node, ref.type());
        Type result = ts.capture(ref.type());
        addRuntimeCheck(node, result, ref.field().type());
        return setType(node, result);
      }
      catch (UnmatchedLookupException e) {
        setErrorStrings(node, ts.typePrinter().print(t), node.getFieldName());
        if (e.matches() > 1) { throw new ExecutionError("ambiguous.field", node); }
        else { throw new ExecutionError("no.such.static.field", node); }
      }
    }
    
    
    @Override public Type visit(SimpleMethodCall node) {
      Iterable<? extends Expression> args = node.getArguments();
      checkList(args);
      Iterable<Type> targs = checkTypeNameList(node.getTypeArgs().unwrap(Collections.<TypeName>emptyList())); 
      
      Type t;
      if (context.localFunctionExists(node.getMethodName(), ts)) {
        Iterable<LocalFunction> matches = context.getLocalFunctions(node.getMethodName(), ts);
        t = ts.makeClassType(new FunctionWrapperClass(context.accessModule(), matches));
      }
      else {
        t = context.typeContainingMethod(node.getMethodName(), ts);
        if (t == null) {
          setErrorStrings(node, node.getMethodName());
          throw new ExecutionError("undefined.name", node);
        }
      }
      
      DJClass enclosingThis = context.getThis(t, ts);
      boolean onlyStatic = (enclosingThis == null);
      try {
        MethodInvocation inv;
        if (onlyStatic) {
          inv = ts.lookupStaticMethod(t, node.getMethodName(), targs, args, expected, context.accessModule());
        }
        else {
          Expression obj = TypeUtil.makeEmptyExpression(node);
          setType(obj, t);
          inv = ts.lookupMethod(obj, node.getMethodName(), targs, args, expected, context.accessModule());
        }
        checkThrownExceptions(inv.thrown(), node);
        node.setArguments(CollectUtil.makeList(inv.args()));
        setMethod(node, inv.method());
        if (!onlyStatic) { setDJClass(node, enclosingThis); }
        Type result = ts.capture(inv.returnType());
        debug.logValue("Type of method call " + node.getMethodName(), ts.wrap(result));
        addRuntimeCheck(node, result, inv.method().returnType());
        return setType(node, result);
      }
      catch (InvalidTypeArgumentException e) {
        throw new ExecutionError("type.argument", node);
      }
      catch (UnmatchedLookupException e) {
        throw unmatchedFunctionError("method", e, node, t, node.getMethodName(), targs, args, expected, onlyStatic);
      }
    }
    
    
    @Override public Type visit(ObjectMethodCall node) {
      Expression receiver = node.getExpression();
      if (receiver instanceof AmbiguousName) {
        Node resolved = resolveAmbiguousName((AmbiguousName) receiver);
        if (resolved instanceof ReferenceTypeName) {
          
          Expression translation = new StaticMethodCall((ReferenceTypeName) resolved, node.getTypeArgs(),
                                                        node.getMethodName(), node.getArguments(),
                                                        node.getSourceInfo());
          setTranslation(node, translation);
          translation.acceptVisitor(this);
          return setType(node, getType(translation));
        }
        else { receiver = (Expression) resolved; }
      }
      
      Type receiverT = check(receiver);
      
      Iterable<? extends Expression> args = node.getArguments();
      checkList(args);
      Iterable<Type> targs = checkTypeNameList(node.getTypeArgs().unwrap(Collections.<TypeName>emptyList())); 
      
      try {
        
        ObjectMethodInvocation inv = ts.lookupMethod(receiver, node.getMethodName(), targs, args, expected,
                                                     context.accessModule());
        checkThrownExceptions(inv.thrown(), node);
        node.setExpression(inv.object());
        node.setArguments(CollectUtil.makeList(inv.args()));
        setMethod(node, inv.method());
        Type result = ts.capture(inv.returnType());
        debug.logValue("Type of method call " + node.getMethodName(), ts.wrap(result));
        addRuntimeCheck(node, result, inv.method().returnType());
        return setType(node, result);
      }
      catch (InvalidTypeArgumentException e) {
        throw new ExecutionError("type.argument", node);
      }
      catch (UnmatchedLookupException e) {
        throw unmatchedFunctionError("method", e, node, receiverT, node.getMethodName(), targs, args, expected, false);
      }
    }
    
    
    @Override public Type visit(SuperMethodCall node) {
      DJClass c = resolveThis(node.getClassName(), node);
      Type t = c.immediateSuperclass();
      if (t == null) {
        throw new ExecutionError("super.undefined", node);
      }
      
      Iterable<? extends Expression> args = node.getArguments();
      checkList(args);
      Iterable<Type> targs = checkTypeNameList(node.getTypeArgs().unwrap(Collections.<TypeName>emptyList())); 
      
      Expression obj = TypeUtil.makeEmptyExpression(node);
      setType(obj, t);
      try {
        MethodInvocation inv = ts.lookupMethod(obj, node.getMethodName(), targs, args, expected,
                                               context.accessModule());
        checkThrownExceptions(inv.thrown(), node);
        node.setArguments(CollectUtil.makeList(inv.args()));
        setMethod(node, inv.method());
        setDJClass(node, c);
        Type result = ts.capture(inv.returnType());
        debug.logValue("Type of method call " + node.getMethodName(), ts.wrap(result));
        addRuntimeCheck(node, result, inv.method().returnType());
        return setType(node, result);
      }
      catch (InvalidTypeArgumentException e) {
        throw new ExecutionError("type.argument", node);
      }
      catch (UnmatchedLookupException e) {
        throw unmatchedFunctionError("method", e, node, t, node.getMethodName(), targs, args, expected, false);
      }
    }
    
    
    @Override public Type visit(StaticMethodCall node) {
      Type t = checkTypeName(node.getMethodType());
      
      Iterable<? extends Expression> args = node.getArguments();
      checkList(args);
      Iterable<Type> targs = checkTypeNameList(node.getTypeArgs().unwrap(Collections.<TypeName>emptyList())); 
      
      try {
        
        MethodInvocation inv = ts.lookupStaticMethod(t, node.getMethodName(), targs, args, expected,
                                                     context.accessModule());
        checkThrownExceptions(inv.thrown(), node);
        node.setArguments(CollectUtil.makeList(inv.args()));
        setMethod(node, inv.method());
        Type result = ts.capture(inv.returnType());
        debug.logValue("Type of method call " + node.getMethodName(), ts.wrap(result));
        addRuntimeCheck(node, result, inv.method().returnType());
        return setType(node, result);
      }
      catch (InvalidTypeArgumentException e) {
        throw new ExecutionError("type.argument", node);
      }
      catch (UnmatchedLookupException e) {
        throw unmatchedFunctionError("method", e, node, t, node.getMethodName(), targs, args, expected, true);
      }
    }
    
    
    @Override public Type visit(ArrayAllocation node) {
      Type elementType = checkTypeName(node.getElementType());
      if (! ts.isReifiable(elementType)) {
        throw new ExecutionError("reifiable.type", node);
      }
      Type result = elementType;
      for (int i = 0; i < node.getDimension(); i++) {
        result = new SimpleArrayType(result);
      }
      
      checkList(node.getSizes(), TypeSystem.INT);
      List<Expression> newSizes = new ArrayList<Expression>(node.getSizes().size());
      for (Expression exp : node.getSizes()) {
        try {
          Expression newExp = ts.unaryPromote(ts.makePrimitive(exp));
          if (!(getType(newExp) instanceof IntType)) {
            throw new ExecutionError("array.dimension.type", node);
          }
          newSizes.add(newExp);
        }
        catch (UnsupportedConversionException e) {
          throw new ExecutionError("array.dimension.type", node);
        }
      }
      node.setSizes(newSizes);
      
      if (node.getInitialization() != null) { check(node.getInitialization(), result); }
      
      setErasedType(node, ts.erasedClass(result));
      return setType(node, result);
    }
    
    
    @Override public Type visit(ArrayInitializer node) {
      
      
      
      
      
      TypeName elementTypeName = node.getElementType();
      if (elementTypeName == null) {
        throw new ExecutionError("array.initializer.type", node);
      }
      Type elementType = checkTypeName(elementTypeName);
      if (expected.isSome() && ts.isArray(expected.unwrap())) {
        checkList(node.getCells(), ts.arrayElementType(expected.unwrap()));
      }
      else {
        checkList(node.getCells());
      }
      List<Expression> newCells = new ArrayList<Expression>(node.getCells().size());
      for (Expression exp : node.getCells()) {
        try { newCells.add(ts.assign(elementType, exp)); }
        catch (UnsupportedConversionException e) {
          Type expT = getType(exp);
          TypePrinter printer = ts.typePrinter();
          setErrorStrings(exp, printer.print(expT), printer.print(elementType));
          throw new ExecutionError("assignment.types", exp);
        }
      }
      node.setCells(newCells);
      Type result = new SimpleArrayType(elementType);
      setErasedType(node, ts.erasedClass(result));
      return setType(node, result);
    }
    
    
    @Override public Type visit(SimpleAllocation node) {
      Type t = checkTypeName(node.getCreationType());
      if (!ts.isConcrete(t)) {
        setErrorStrings(node, ts.typePrinter().print(t));
        throw new ExecutionError("allocation.type", node);
      }

      Option<Type> dynamicOuter = ts.dynamicallyEnclosingType(t);
      if (dynamicOuter.isSome()) {
        DJClass enclosingThis = context.getThis(dynamicOuter.unwrap(), ts);
        if (enclosingThis == null) {
          TypePrinter printer = ts.typePrinter();
          setErrorStrings(node, printer.print(t), printer.print(dynamicOuter.unwrap()));
          throw new ExecutionError("inner.allocation", node);
        }
        else { setEnclosingThis(node, enclosingThis); }
      }
      
      Iterable<? extends Expression> args = node.getArguments();
      checkList(args);
      Iterable<Type> targs = checkTypeNameList(node.getTypeArgs().unwrap(Collections.<TypeName>emptyList()));
      
      try {
        ConstructorInvocation inv = ts.lookupConstructor(t, targs, args, expected, context.accessModule());
        checkThrownExceptions(inv.thrown(), node);
        node.setArguments(CollectUtil.makeList(inv.args()));
        setConstructor(node, inv.constructor());
        return setType(node, t);
      }
      catch (InvalidTypeArgumentException e) {
        throw new ExecutionError("type.argument", node);
      }
      catch (UnmatchedLookupException e) {
        throw unmatchedFunctionError("constructor", e, node, t, "", targs, args, expected, false);
      }
    }
    
    
    @Override public Type visit(AnonymousAllocation node) {
      Type t = checkTypeName(node.getCreationType());
      if (!ts.isExtendable(t) && !ts.isImplementable(t)) {
        setErrorStrings(node, ts.typePrinter().print(t));
        throw new ExecutionError("invalid.supertype", node);
      }
      
      Option<Type> dynamicOuter = ts.dynamicallyEnclosingType(t);
      if (dynamicOuter.isSome()) {
        DJClass enclosingThis = context.getThis(dynamicOuter.unwrap(), ts);
        if (enclosingThis == null) {
          TypePrinter printer = ts.typePrinter();
          setErrorStrings(node, printer.print(t), printer.print(dynamicOuter.unwrap()));
          throw new ExecutionError("inner.allocation", node);
        }
        else { setEnclosingThis(node, enclosingThis); }
      }
      
      Iterable<? extends Expression> args = node.getArguments();
      checkList(args);
      Iterable<Type> targs = checkTypeNameList(node.getTypeArgs().unwrap(Collections.<TypeName>emptyList())); 
      
      if (!(IterUtil.isEmpty(args) && IterUtil.isEmpty(targs) && ts.isImplementable(t))) {
        
        try {
          ConstructorInvocation inv = ts.lookupConstructor(t, targs, args, expected, context.accessModule());
          checkThrownExceptions(inv.thrown(), node);
          node.setArguments(CollectUtil.makeList(inv.args()));
        }
        catch (InvalidTypeArgumentException e) {
          throw new ExecutionError("type.argument", node);
        }
        catch (UnmatchedLookupException e) {
          throw unmatchedFunctionError("constructor", e, node, t, "", targs, args, expected, false);
        }
      }
      
      TreeClassLoader loader = new TreeClassLoader(context.getClassLoader(), opt);
      TreeClass c = new TreeClass(context.makeAnonymousClassName(), null, context.accessModule(), node, loader, opt);
      setDJClass(node, c);
      ClassChecker checker = new ClassChecker(c, loader, context, opt);
      checker.initializeClassSignatures(node);
      checker.checkSignatures(node);
      checker.checkBodies(node);
      
      setConstructor(node, IterUtil.first(c.declaredConstructors()));
      return setType(node, ts.makeClassType(c));
    }
    
    
    @Override public Type visit(InnerAllocation node) {
      Type enclosing = check(node.getExpression());
      Iterable<Type> classTargs = checkTypeNameList(node.getClassTypeArgs().unwrap(Collections.<TypeName>emptyList())); 
      
      try {
        ClassType t = ts.lookupClass(node.getExpression(), node.getClassName(), classTargs, context.accessModule());
        if (t.ofClass().isStatic()) {
          setErrorStrings(node, node.getClassName(), ts.typePrinter().print(getType(node.getExpression())));
          throw new ExecutionError("static.inner.allocation", node);
        }
        if (!ts.isConcrete(t)) {
          setErrorStrings(node, ts.typePrinter().print(t));
          throw new ExecutionError("allocation.type", node);
        }
        
        Iterable<? extends Expression> args = node.getArguments();
        checkList(args);
        Iterable<Type> targs = checkTypeNameList(node.getTypeArgs().unwrap(Collections.<TypeName>emptyList())); 
        
        try {
          ConstructorInvocation inv = ts.lookupConstructor(t, targs, args, expected, context.accessModule());
          checkThrownExceptions(inv.thrown(), node);
          node.setArguments(CollectUtil.makeList(inv.args()));
          setConstructor(node, inv.constructor());
          return setType(node, t);
        }
        catch (InvalidTypeArgumentException e) {
          throw new ExecutionError("type.argument", node);
        }
        catch (UnmatchedLookupException e) {
          throw unmatchedFunctionError("constructor", e, node, t, "", targs, args, expected, false);
        }
      }
      catch (InvalidTypeArgumentException e) {
        throw new ExecutionError("type.argument", node);
      }
      catch (UnmatchedLookupException e) {
        setErrorStrings(node, ts.typePrinter().print(enclosing), node.getClassName());
        if (e.matches() > 1) { throw new ExecutionError("ambiguous.inner.class", node); }
        else { throw new ExecutionError("no.such.inner.class", node); }
      }
    }
    
    
    @Override public Type visit(AnonymousInnerAllocation node) {
      Type enclosing = check(node.getExpression());
      Iterable<Type> classTargs = checkTypeNameList(node.getClassTypeArgs().unwrap(Collections.<TypeName>emptyList())); 
      
      try {
        ClassType t = ts.lookupClass(node.getExpression(), node.getClassName(), classTargs, context.accessModule());
        if (t.ofClass().isStatic()) {
          setErrorStrings(node, node.getClassName(), ts.typePrinter().print(getType(node.getExpression())));
          throw new ExecutionError("static.inner.allocation", node);
        }
        if (!ts.isExtendable(t)) {
          setErrorStrings(node, ts.typePrinter().print(t));
          throw new ExecutionError("invalid.supertype", node);
        }
        setSuperType(node, t);
        
        Iterable<? extends Expression> args = node.getArguments();
        checkList(args);
        Iterable<Type> targs = checkTypeNameList(node.getTypeArgs().unwrap(Collections.<TypeName>emptyList())); 
        
        try {
          ConstructorInvocation inv = ts.lookupConstructor(t, targs, args, expected, context.accessModule());
          checkThrownExceptions(inv.thrown(), node);
          node.setArguments(CollectUtil.makeList(inv.args()));
        }
        catch (InvalidTypeArgumentException e) {
          throw new ExecutionError("type.argument", node);
        }
        catch (UnmatchedLookupException e) {
          throw unmatchedFunctionError("constructor", e, node, t, "", targs, args, expected, false);
        }
      }
      catch (InvalidTypeArgumentException e) {
        throw new ExecutionError("type.argument", node);
      }
      catch (UnmatchedLookupException e) {
        setErrorStrings(node, ts.typePrinter().print(enclosing), node.getClassName());
        if (e.matches() > 1) { throw new ExecutionError("ambiguous.inner.class", node); }
        else { throw new ExecutionError("no.such.inner.class", node); }
      }
      
      TreeClassLoader loader = new TreeClassLoader(context.getClassLoader(), opt);
      TreeClass c = new TreeClass(context.makeAnonymousClassName(), null, context.accessModule(), node, loader, opt);
      setDJClass(node, c);
      ClassChecker checker = new ClassChecker(c, loader, context, opt);
      checker.initializeClassSignatures(node);
      checker.checkSignatures(node);
      checker.checkBodies(node);

      setConstructor(node, IterUtil.first(c.declaredConstructors()));
      return setType(node, ts.makeClassType(c));
    }
    
    @Override public Type visit(ConstructorCall node) {
      throw new ExecutionError("constructor.call", node);
    }
    

    
    @Override public Type visit(ArrayAccess node) {
      Type arrayType = check(node.getExpression());
      if (!ts.isArray(arrayType)) {
        setErrorStrings(node, ts.typePrinter().print(arrayType));
        throw new ExecutionError("array.required", node);
      }
      Type elementType = ts.arrayElementType(arrayType);
      
      check(node.getCellNumber(), TypeSystem.INT);
      try {
        Expression cell = ts.unaryPromote(ts.makePrimitive(node.getCellNumber()));
        if (!(getType(cell) instanceof IntType)) {
          throw new ExecutionError("array.index.type", node);
        }
        node.setCellNumber(cell);
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("array.index.type", node);
      }
      
      setVariableType(node, elementType);
      return setType(node, ts.capture(elementType));
      
    }
    
    
    @Override public Type visit(TypeExpression node) {
      Type t = checkTypeName(node.getType());
      setErasedType(node.getType(), ts.erasedClass(t));
      
      Type targ = t;
      if (ts.isEqual(t, TypeSystem.VOID)) { targ = TypeSystem.VOID_CLASS; }
      else if (!ts.isReference(t)) {
        Expression pseudoExp = TypeUtil.makeEmptyExpression(node.getType());
        setType(pseudoExp, t);
        try {
          Expression boxedPseudoExp = ts.makeReference(pseudoExp);
          targ = getType(boxedPseudoExp);
        }
        catch (UnsupportedConversionException e) {
          throw new ExecutionError("reference.type", node);
        }
      }
      return setType(node, ts.reflectionClassOf(targ));
    }
    
    
    @Override public Type visit(NotExpression node) {
      check(node.getExpression(), TypeSystem.BOOLEAN);
      try {
        Expression exp = ts.makePrimitive(node.getExpression());
        if (!(getType(exp) instanceof BooleanType)) {
          throw new ExecutionError("not.expression.type", node);
        }
        node.setExpression(exp);
        return setType(node, getType(exp));
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("not.expression.type", node);
      }
    }
    
    
    @Override public Type visit(ComplementExpression node) {
      check(node.getExpression());
      try {
        Expression exp = ts.unaryPromote(ts.makePrimitive(node.getExpression()));
        if (!(getType(exp) instanceof IntegralType)) {
          throw new ExecutionError("complement.expression.type", node);
        }
        node.setExpression(exp);
        return setType(node, getType(exp));
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("complement.expression.type", node);
      }
    }
    
    @Override public Type visit(PlusExpression node) { return handleNumericUnaryExpression(node); }
    
    @Override public Type visit(MinusExpression node) { return handleNumericUnaryExpression(node); }
    
    
    private Type handleNumericUnaryExpression(UnaryExpression node) {
      check(node.getExpression());
      try {
        Expression exp = ts.unaryPromote(ts.makePrimitive(node.getExpression()));
        node.setExpression(exp);
        Type result = setType(node, getType(exp));
        evaluateConstantExpression(node);
        return result;
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("numeric.expression.type", node);
      }
    }
    
    
    @Override public Type visit(AddExpression node) {
      Type leftT = check(node.getLeftExpression());
      Type rightT = check(node.getRightExpression());
      if (ts.isSubtype(leftT, TypeSystem.STRING) || ts.isSubtype(rightT, TypeSystem.STRING)) {
        try {
          Expression left = ts.makeReference(node.getLeftExpression());
          Expression right = ts.makeReference(node.getRightExpression());
          node.setLeftExpression(left);
          node.setRightExpression(right);
          setOperation(node, ExpressionEvaluator.CONCATENATE);
          setType(node, TypeSystem.STRING);
          evaluateConstantExpression(node);
          return TypeSystem.STRING;
        }
        catch (UnsupportedConversionException e) {
          throw new ExecutionError("addition.type", node);
        }
      }
      else {
        try {
          Expression left = ts.makePrimitive(node.getLeftExpression());
          Expression right = ts.makePrimitive(node.getRightExpression());
          Pair<Expression, Expression> promoted = ts.binaryPromote(left, right);
          node.setLeftExpression(promoted.first());
          node.setRightExpression(promoted.second());
          setOperation(node, ExpressionEvaluator.ADD);
          Type result = setType(node, getType(promoted.first()));
          evaluateConstantExpression(node);
          return result;
        }
        catch (UnsupportedConversionException e) {
          throw new ExecutionError("addition.type", node);
        }
      }
    }
    
    
    @Override public Type visit(AddAssignExpression node) { 
      Type leftT = check(node.getLeftExpression());
      Type rightT = check(node.getRightExpression());
      if (ts.isEqual(leftT, TypeSystem.STRING)) {
        try {
          Expression right = ts.makeReference(node.getRightExpression());
          setLeftExpression(node, node.getLeftExpression()); 
          node.setRightExpression(right);
          setOperation(node, ExpressionEvaluator.CONCATENATE);
        }
        catch (UnsupportedConversionException e) {
          throw new ExecutionError("addition.type", node);
        }
      }
      else if (ts.isSubtype(leftT, TypeSystem.STRING) || ts.isSubtype(rightT, TypeSystem.STRING)) {
        throw new ExecutionError("addition.type", node);
      }
      else {
        try {
          Expression left = ts.makePrimitive(node.getLeftExpression());
          Expression right = ts.makePrimitive(node.getRightExpression());
          Pair<Expression, Expression> promoted = ts.binaryPromote(left, right);
          setLeftExpression(node, promoted.first()); 
          node.setRightExpression(promoted.second());
          setOperation(node, ExpressionEvaluator.ADD);
        }
        catch (UnsupportedConversionException e) {
          throw new ExecutionError("addition.type", node);
        }
      }
      
      if (!hasVariableType(node.getLeftExpression())) {
        throw new ExecutionError("addition.type", node);
      }
      
      return setType(node, leftT);
    }
    
    @Override public Type visit(SubtractExpression node) {  return handleNumericExpression(node); }
    
    @Override public Type visit(MultiplyExpression node) {  return handleNumericExpression(node); }
    
    @Override public Type visit(DivideExpression node) {  return handleNumericExpression(node); }
    
    @Override public Type visit(RemainderExpression node) {  return handleNumericExpression(node); }
    
    
    private Type handleNumericExpression(BinaryExpression node) {
      check(node.getLeftExpression());
      check(node.getRightExpression());
      try {
        Expression left = ts.makePrimitive(node.getLeftExpression());
        Expression right = ts.makePrimitive(node.getRightExpression());
        Pair<Expression, Expression> promoted = ts.binaryPromote(left, right);
        node.setLeftExpression(promoted.first());
        node.setRightExpression(promoted.second());
        Type result = setType(node, getType(promoted.first()));
        evaluateConstantExpression(node);
        return result;
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("numeric.expression.type", node);
      }
    }
    
    @Override public Type visit(SubtractAssignExpression node) { return handleNumericAssignmentExpression(node); }
    
    @Override public Type visit(MultiplyAssignExpression node) {  return handleNumericAssignmentExpression(node); }
    
    @Override public Type visit(DivideAssignExpression node) {  return handleNumericAssignmentExpression(node); }
    
    @Override public Type visit(RemainderAssignExpression node) { return handleNumericAssignmentExpression(node); }
    
    
    private Type handleNumericAssignmentExpression(BinaryExpression node) {
      Type result = check(node.getLeftExpression());
      check(node.getRightExpression());
      try {
        Expression left = ts.makePrimitive(node.getLeftExpression());
        Expression right = ts.makePrimitive(node.getRightExpression());
        Pair<Expression, Expression> promoted = ts.binaryPromote(left, right);
        
        if (!hasVariableType(node.getLeftExpression())) {
          throw new ExecutionError("numeric.expression.type", node);
        }
        setLeftExpression(node, promoted.first()); 
        node.setRightExpression(promoted.second());
        return setType(node, result);
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("numeric.expression.type", node);
      }
    }
    
    @Override public Type visit(EqualExpression node) {
      return handleEqualityExpression(node, ExpressionEvaluator.OBJECT_EQUAL, 
                                      ExpressionEvaluator.PRIMITIVE_EQUAL);
    }
    
    @Override public Type visit(NotEqualExpression node) {
      return handleEqualityExpression(node, ExpressionEvaluator.OBJECT_NOT_EQUAL,
                                      ExpressionEvaluator.PRIMITIVE_NOT_EQUAL);
    }
    
    
    private Type handleEqualityExpression(BinaryExpression node, Lambda2<Object, Object, Object> objectCase,
                                          Lambda2<Object, Object, Object>  primitiveCase) {
      Type leftT = check(node.getLeftExpression());
      Type rightT = check(node.getRightExpression());
      if (ts.isReference(leftT) && ts.isReference(rightT)) {
        if (ts.isDisjoint(leftT, rightT)) {
          TypePrinter printer = ts.typePrinter();
          setErrorStrings(node, printer.print(leftT), printer.print(rightT));
          throw new ExecutionError("compare.type", node);
        }
        setOperation(node, objectCase);
      }
      else {
        try {
          Expression left = ts.makePrimitive(node.getLeftExpression());
          Expression right = ts.makePrimitive(node.getRightExpression());
          
          if (getType(left) instanceof BooleanType && getType(right) instanceof BooleanType) {
            node.setLeftExpression(left);
            node.setRightExpression(right);
          }
          else if (getType(left) instanceof NumericType && getType(right) instanceof NumericType) {
            Pair<Expression, Expression> promoted = ts.binaryPromote(left, right);
            left = promoted.first();
            right = promoted.second();
            node.setLeftExpression(promoted.first());
            node.setRightExpression(promoted.second());
          }
          else {
            TypePrinter printer = ts.typePrinter();
            setErrorStrings(node, printer.print(leftT), printer.print(rightT));
            throw new ExecutionError("compare.type", node);
          }
          setOperation(node, primitiveCase);
        }
        catch (UnsupportedConversionException e) {
            TypePrinter printer = ts.typePrinter();
            setErrorStrings(node, printer.print(leftT), printer.print(rightT));
          throw new ExecutionError("compare.type", node);
        }
      }
      setType(node, TypeSystem.BOOLEAN);
      evaluateConstantExpression(node);
      return TypeSystem.BOOLEAN;
    }
    
    @Override public Type visit(LessExpression node) { return handleRelationalExpression(node); }
    
    @Override public Type visit(LessOrEqualExpression node) { return handleRelationalExpression(node); }
    
    @Override public Type visit(GreaterExpression node) { return handleRelationalExpression(node); }
    
    @Override public Type visit(GreaterOrEqualExpression node) { return handleRelationalExpression(node); }
    
    
    private Type handleRelationalExpression(BinaryExpression node) {
      check(node.getLeftExpression());
      check(node.getRightExpression());
      try {
        Expression left = ts.makePrimitive(node.getLeftExpression());
        Expression right = ts.makePrimitive(node.getRightExpression());
        Pair<Expression, Expression> promoted = ts.binaryPromote(left, right);
        node.setLeftExpression(promoted.first());
        node.setRightExpression(promoted.second());
        setType(node, TypeSystem.BOOLEAN);
        evaluateConstantExpression(node);
        return TypeSystem.BOOLEAN;
      }
      catch (UnsupportedConversionException e) {
        TypePrinter printer = ts.typePrinter();
        setErrorStrings(node, printer.print(getType(node.getLeftExpression())),
                        printer.print(getType(node.getRightExpression())));
        throw new ExecutionError("compare.type", node);
      }
    }
    
    @Override public Type visit(BitAndExpression node) { return handleBitwiseExpression(node); }
    
    @Override public Type visit(BitOrExpression node) { return handleBitwiseExpression(node); }
    
    @Override public Type visit(ExclusiveOrExpression node) { return handleBitwiseExpression(node); }
    
    
    private Type handleBitwiseExpression(BinaryExpression node) {
      check(node.getLeftExpression());
      check(node.getRightExpression());
      try {
        Expression left = ts.makePrimitive(node.getLeftExpression());
        Expression right = ts.makePrimitive(node.getRightExpression());
        
        if (getType(left) instanceof BooleanType && getType(right) instanceof BooleanType) {
          
        }
        else if (getType(left) instanceof IntegralType && getType(right) instanceof IntegralType) {
          Pair<Expression, Expression> promoted = ts.binaryPromote(left, right);
          left = promoted.first();
          right = promoted.second();
        }
        else {
          throw new ExecutionError("bitwise.expression.type", node);
        }
        node.setLeftExpression(left);
        node.setRightExpression(right);
        Type result = setType(node, getType(left));
        evaluateConstantExpression(node);
        return result;
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("bitwise.expression.type", node);
      }
    }
    
    @Override public Type visit(BitAndAssignExpression node) { return handleBitwiseAssignmentExpression(node); }
    
    @Override public Type visit(BitOrAssignExpression node) { return handleBitwiseAssignmentExpression(node); }
    
    @Override public Type visit(ExclusiveOrAssignExpression node) { return handleBitwiseAssignmentExpression(node); }
    
    
    private Type handleBitwiseAssignmentExpression(BinaryExpression node) {
      Type result = check(node.getLeftExpression());
      check(node.getRightExpression());
      try {
        Expression left = ts.makePrimitive(node.getLeftExpression());
        Expression right = ts.makePrimitive(node.getRightExpression());
        if (getType(left) instanceof BooleanType && getType(right) instanceof BooleanType) {
          
        }
        else if (getType(left) instanceof IntegralType && getType(right) instanceof IntegralType) {
          Pair<Expression, Expression> promoted = ts.binaryPromote(left, right);
          left = promoted.first();
          right = promoted.second();
        }
        else {
          throw new ExecutionError("bitwise.expression.type", node);
        }
        
        if (!hasVariableType(node.getLeftExpression())) {
          throw new ExecutionError("bitwise.expression.type", node);
        }
        setLeftExpression(node, left); 
        node.setRightExpression(right);
        return setType(node, result);
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("bitwise.expression.type", node);
      }
    }
    
    @Override public Type visit(ShiftLeftExpression node) { return handleShiftExpression(node); }
    
    @Override public Type visit(ShiftRightExpression node) { return handleShiftExpression(node); }
    
    @Override public Type visit(UnsignedShiftRightExpression node) { return handleShiftExpression(node); }
    
    
    private Type handleShiftExpression(BinaryExpression node) {
      check(node.getLeftExpression());
      check(node.getRightExpression());
      try {
        Expression left = ts.unaryPromote(ts.makePrimitive(node.getLeftExpression()));
        Expression right = ts.unaryPromote(ts.makePrimitive(node.getRightExpression()));
        node.setLeftExpression(left);
        node.setRightExpression(right);
        
        if (!(getType(left) instanceof IntegralType) || !(getType(right) instanceof IntegralType)) {
          throw new ExecutionError("shift.expression.type", node);
        }
        
        Type result = setType(node, getType(left));
        evaluateConstantExpression(node);
        return result;
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("shift.expression.type", node);
      }
    }
    
    @Override public Type visit(ShiftLeftAssignExpression node) { return handleShiftAssignmentExpression(node); }
    
    @Override public Type visit(ShiftRightAssignExpression node) { return handleShiftAssignmentExpression(node); }
    
    @Override public Type visit(UnsignedShiftRightAssignExpression node) { return handleShiftAssignmentExpression(node); }
    
    
    private Type handleShiftAssignmentExpression(BinaryExpression node) {
      Type result = check(node.getLeftExpression());
      check(node.getRightExpression());
      try {
        Expression left = ts.unaryPromote(ts.makePrimitive(node.getLeftExpression()));
        Expression right = ts.unaryPromote(ts.makePrimitive(node.getRightExpression()));
        
        if (!(getType(left) instanceof IntegralType) || !(getType(right) instanceof IntegralType) || 
            !hasVariableType(node.getLeftExpression())) {
          throw new ExecutionError("shift.expression.type", node);
        }
        
        setLeftExpression(node, left); 
        node.setRightExpression(right);
        return setType(node, result);
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("shift.expression.type", node);
      }
    }
    
    @Override public Type visit(AndExpression node) { return handleBooleanExpression(node); }
    
    @Override public Type visit(OrExpression node) { return handleBooleanExpression(node); }
    
    
    private Type handleBooleanExpression(BinaryExpression node) {
      check(node.getLeftExpression(), TypeSystem.BOOLEAN);
      check(node.getRightExpression(), TypeSystem.BOOLEAN);
      try {
        Expression left = ts.makePrimitive(node.getLeftExpression());
        Expression right = ts.makePrimitive(node.getRightExpression());    
        if (!(getType(left) instanceof BooleanType) || !(getType(right) instanceof BooleanType)) {
          throw new ExecutionError("boolean.expression.type", node);
        }
        node.setLeftExpression(left);
        node.setRightExpression(right);
        setType(node, TypeSystem.BOOLEAN);
        evaluateConstantExpression(node);
        return TypeSystem.BOOLEAN;
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("boolean.expression.type", node);
      }
    }
    
    private void evaluateConstantExpression(BinaryExpression node) {
      if (hasValue(node.getLeftExpression()) && hasValue(node.getRightExpression())) {
        try {
          Object val = new ExpressionEvaluator(RuntimeBindings.EMPTY, opt).value(node);
          setValue(node, val);
        }
        catch (WrappedException e) {  }
      }
    }
    
    private void evaluateConstantExpression(UnaryExpression node) {
      if (hasValue(node.getExpression())) {
        try {
          Object val = new ExpressionEvaluator(RuntimeBindings.EMPTY, opt).value(node);
          setValue(node, val);
        }
        catch (WrappedException e) {  }
      }
    }
    
    
    @Override public Type visit(InstanceOfExpression node) {
      Type expT = check(node.getExpression());
      Type targetT = checkTypeName(node.getReferenceType());
      if (!ts.isReference(expT) || !ts.isReference(targetT) || ts.isDisjoint(targetT, expT)) {
        throw new ExecutionError("instanceof.type", node);
      }
      if (!ts.isReifiable(targetT)) {
        throw new ExecutionError("reifiable.type", node);
      }
      setErasedType(node.getReferenceType(), ts.erasedClass(targetT));
      return setType(node, TypeSystem.BOOLEAN);
    }
    
    
    @Override public Type visit(ConditionalExpression node) {
      check(node.getConditionExpression(), TypeSystem.BOOLEAN);
      check(node.getIfTrueExpression(), expected);
      check(node.getIfFalseExpression(), expected);
      
      try {
        Expression cond = ts.makePrimitive(node.getConditionExpression());
        if (!(getType(cond) instanceof BooleanType)) {
          throw new ExecutionError("condition.type", node);
        }
        node.setConditionExpression(cond);
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("condition.type", node);
      }
      
      try {
        Pair<Expression, Expression> joined = ts.mergeConditional(node.getIfTrueExpression(),
                                                      node.getIfFalseExpression());
        node.setIfTrueExpression(joined.first());
        node.setIfFalseExpression(joined.second());
        return setType(node, ts.capture(getType(joined.first())));
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("conditional.type", node);
      }
    }
    
    
    @Override public Type visit(SimpleAssignExpression node) {
      Expression left = node.getLeftExpression();
      Type result = check(left);

      if (!hasVariableType(left)) {
        throw new ExecutionError("left.expression", node);
      }
      if (hasVariable(left) && getVariable(left).isFinal()) {
        setErrorStrings(node, getVariable(left).declaredName());
        throw new ExecutionError("cannot.modify", node);
      }
      if (hasField(left) && getField(left).isFinal()) {
        DJClass initializing = context.initializingClass();
        if (initializing == null || !initializing.equals(getField(left).declaringClass())) {
          setErrorStrings(node, getField(left).declaredName());
          throw new ExecutionError("cannot.modify", node);
        }
      }      
      Type target = getVariableType(left);
      Type rightT = check(node.getRightExpression(), target);
      try {
        Expression newRight = ts.assign(target, node.getRightExpression());
        node.setRightExpression(newRight);
        return setType(node, result);
      }
      catch (UnsupportedConversionException e) {
        TypePrinter printer = ts.typePrinter();
        setErrorStrings(node, printer.print(rightT), printer.print(target));
        throw new ExecutionError("assignment.types", node);
      }
    }
    
    @Override public Type visit(PostIncrement node) { return handleIncrementExpression(node); }
    
    @Override public Type visit(PreIncrement node) { return handleIncrementExpression(node); }
    
    @Override public Type visit(PostDecrement node) { return handleIncrementExpression(node); }
    
    @Override public Type visit(PreDecrement node) { return handleIncrementExpression(node); }
    
    
    private Type handleIncrementExpression(UnaryExpression node) {
      Type result = check(node.getExpression());
      try {
        Expression exp = ts.makePrimitive(node.getExpression());
        
        if (!(getType(exp) instanceof NumericType) || !hasVariableType(node.getExpression())) {
          throw new ExecutionError("increment.type", node);
        }
        
        setLeftExpression(node, exp);
        return setType(node, result);
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("increment.type", node);
      }
    }
    
    
    @Override public Type visit(CastExpression node) {
      Type t = checkTypeName(node.getTargetType());
      Type fromT = check(node.getExpression());
      try {
        Expression exp = ts.cast(t, node.getExpression());
        node.setExpression(exp);
        return setType(node, ts.capture(t));
      }
      catch (UnsupportedConversionException e) {
        TypePrinter printer = ts.typePrinter();
        setErrorStrings(node, printer.print(fromT), printer.print(t));
        throw new ExecutionError("cast.types", node);
      }
    }
    
  }
  
}
