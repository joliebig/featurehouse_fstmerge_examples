



package edu.rice.cs.dynamicjava.interpreter;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.lambda.Lambda;

import koala.dynamicjava.tree.*;
import koala.dynamicjava.tree.tiger.*;
import koala.dynamicjava.tree.visitor.*;
import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.interpreter.TypeUtil;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.symbol.*;
import edu.rice.cs.dynamicjava.symbol.type.*;
import edu.rice.cs.dynamicjava.symbol.TypeSystem.*;

import static koala.dynamicjava.interpreter.NodeProperties.*;

import static edu.rice.cs.plt.debug.DebugUtil.debug;



public class StatementChecker extends AbstractVisitor<TypeContext> implements Lambda<Node, TypeContext> {

  private final TypeContext context;
  private final Options opt;
  private final TypeSystem ts; 

  public StatementChecker(TypeContext ctx, Options options) {
    context = ctx;
    opt = options;
    ts = opt.typeSystem();
  }
  
  public TypeContext value(Node n) { return n.acceptVisitor(this); }
  
  public TypeContext checkList(Iterable<? extends Node> l) {
    ExecutionError error = null;
    TypeContext c = context;
    for (Node n : l) {
      try { c = n.acceptVisitor(new StatementChecker(c, opt)); }
      catch (ExecutionError e) {
        if (hasErrorContext(n)) { c = getErrorContext(n); }
        if (error == null) { error = e; }
      }
    }
    if (error != null) { throw error; }
    return c;
  }

  private Type checkType(Expression exp) { return new ExpressionChecker(context, opt).check(exp); }
  
  private Type checkType(Expression exp, Type expected) {
    return new ExpressionChecker(context, opt).check(exp, expected);
  }
  
  @SuppressWarnings("unused") private Iterable<Type> checkTypes(Iterable<? extends Expression> l) {
    return new ExpressionChecker(context, opt).checkList(l);
  }
  
  private Type checkTypeName(TypeName t) {
    return new TypeNameChecker(context, opt).check(t);
  }
  
  
  
  @Override public TypeContext visit(PackageDeclaration node) {
    return context.setPackage(node.getName());
  }

  
  @Override public TypeContext visit(ImportDeclaration node) {
    if (node.isStatic()) {
      
      if (node.isPackage()) {
        
        ClassType t = resolveClassName(node.getName(), node);
        if (t == null) {
          setErrorStrings(node, node.getName());
          throw new ExecutionError("undefined.class", node);
        }
        return context.importStaticMembers(t.ofClass());
      }
            
      else {
        
        Pair<String, String> split = splitName(node.getName());
        if (split.first() == null) {
          setErrorStrings(node, node.getName());
          throw new ExecutionError("undefined.name", node);
        }
        ClassType t = resolveClassName(split.first(), node);
        if (t == null) {
          setErrorStrings(node, node.getName());
          throw new ExecutionError("undefined.class", node);
        }
        String member = split.second();
        TypeContext result = context;
        if (ts.containsStaticField(t, member, context.accessModule())) {
          result = result.importField(t.ofClass(), member);
        }
        if (ts.containsStaticMethod(t, member, context.accessModule())) {
          result = result.importMethod(t.ofClass(), member);
        }
        if (ts.containsStaticClass(t, member, context.accessModule())) {
          result = result.importMemberClass(t.ofClass(), member);
        }
        if (result == context) {
          setErrorStrings(node, node.getName());
          throw new ExecutionError("undefined.name", node);
        }
        return result;
      }
          
    }
    else {
      
      if (node.isPackage()) {
        
        ClassType t = resolveClassName(node.getName(), node);
        if (t == null) { return context.importTopLevelClasses(node.getName()); }
        else { return context.importMemberClasses(t.ofClass()); }
      }
      
      else {
        
        Pair<String, String> split = splitName(node.getName());
        if (split.first() != null) {
          ClassType t = resolveClassName(split.first(), node);
          if (t != null) {
            if (ts.containsClass(t, split.second(), context.accessModule())) {
              return context.importMemberClass(t.ofClass(), split.second());
            }
            else {
              setErrorStrings(node, ts.typePrinter().print(t), split.second());
              throw new ExecutionError("no.such.inner.class", node);
            }
          }
        }
        try {
          DJClass c = context.getTopLevelClass(node.getName(), ts);
          if (c == null) {
            setErrorStrings(node, node.getName());
            throw new ExecutionError("undefined.class", node);
          }
          else { return context.importTopLevelClass(c); }
        }
        catch (AmbiguousNameException e) {
          setErrorStrings(node, node.getName());
          throw new ExecutionError("ambiguous.name", node);
        }
      }
      
    }
  }
  
  private ClassType resolveClassName(String name, Node node) {
    String topLevelName = "";
    ClassType result = null;
    boolean first = true;
    for (String piece : name.split("\\.")) {
      if (result == null) {
        if (!first) { topLevelName += "."; }
        first = false;
        topLevelName += piece;
        try {
          DJClass c = context.getTopLevelClass(topLevelName, ts);
          result = (c == null) ? null : ts.makeClassType(c);
        }
        catch (AmbiguousNameException e) {
          setErrorStrings(node, topLevelName);
          throw new ExecutionError("ambiguous.name", node);
        }
      }
      else {
        try { result = ts.lookupClass(result, piece, IterUtil.<Type>empty(), context.accessModule()); }
        catch (InvalidTypeArgumentException e) { throw new RuntimeException("can't create raw type"); }
        catch (UnmatchedLookupException e) {
          setErrorStrings(node, ts.typePrinter().print(result), piece);
          if (e.matches() > 1) { throw new ExecutionError("ambiguous.inner.class", node); }
          else { throw new ExecutionError("no.such.inner.class", node); }
        }
      }
    }
    return result;
  }
  
  private Pair<String, String> splitName(String name) {
    int dot = name.lastIndexOf('.');
    if (dot == -1) { return Pair.make(null, name); }
    else { return Pair.make(name.substring(0, dot), name.substring(dot+1)); }
  }
  
  
  @Override public TypeContext visit(VariableDeclaration node) {
    if (node.getType() == null) {
      
      Type initT = checkType(node.getInitializer());
      LocalVariable v = new LocalVariable(node.getName(), initT, node.getModifiers().isFinal());
      setVariable(node, v);
      setErasedType(node, ts.erasedClass(initT));
      return new LocalContext(context, v);
    }
    else {
      boolean initialized = (node.getInitializer() != null);
      Type t = checkTypeName(node.getType());
      LocalVariable v = new LocalVariable(node.getName(), t, initialized && node.getModifiers().isFinal());
      setVariable(node, v);
      setErasedType(node, ts.erasedClass(t));
      TypeContext newContext = new LocalContext(context, v);
      
      if (initialized) {
        try {
          Type initT = checkType(node.getInitializer(), t);
          try {
            Expression newInit = ts.assign(t, node.getInitializer());
            node.setInitializer(newInit);
          }
          catch (UnsupportedConversionException e) {
            TypePrinter printer = ts.typePrinter();
            setErrorStrings(node, printer.print(initT), printer.print(t));
            throw new ExecutionError("assignment.types", node);
          }
        }
        catch (ExecutionError e) { setErrorContext(node, newContext); throw e; }
      }
      return newContext;
    }
  }
  
  @Override public TypeContext visit(ClassDeclaration node) {
    return handleTypeDeclaration(node);
  }
  
  @Override public TypeContext visit(InterfaceDeclaration node) {
    return handleTypeDeclaration(node);
  }
  
  private TypeContext handleTypeDeclaration(TypeDeclaration node) {
    TreeClassLoader loader = new TreeClassLoader(context.getClassLoader(), opt);
    DJClass c = new TreeClass(context.makeClassName(node.getName()), null, context.accessModule(), node, loader, opt);
    setDJClass(node, c);
    ClassChecker classChecker = new ClassChecker(c, loader, context, opt);
    classChecker.initializeClassSignatures(node);
    classChecker.checkSignatures(node);
    classChecker.checkBodies(node);
    return new LocalContext(context, loader, c);
  }

  
  @Override public TypeContext visit(MethodDeclaration node) {
    LocalFunction f = new LocalFunction(node);
    
    TypeContext sigContext = new FunctionSignatureContext(context, f);
    TypeNameChecker sigChecker = new TypeNameChecker(sigContext, opt);

    Iterable<TypeParameter> tparams = node.getTypeParams().unwrap(Collections.<TypeParameter>emptyList());
    sigChecker.checkTypeParameters(tparams);

    Type returnT = sigChecker.check(node.getReturnType());
    setErasedType(node, ts.erasedClass(returnT));
    for (FormalParameter p : node.getParameters()) {
      Type t = sigChecker.check(p.getType());
      setVariable(p, new LocalVariable(p.getName(), t, p.getModifiers().isFinal()));
    }
    for (ReferenceTypeName n : node.getExceptions()) { sigChecker.check(n); }
    
    if (node.getBody() == null) {
      setErrorStrings(node, node.getName());
      throw new ExecutionError("missing.method.body", node);
    }
    TypeContext bodyContext = new FunctionContext(sigContext, f);
    node.getBody().acceptVisitor(new StatementChecker(bodyContext, opt));
    
    return new LocalContext(context, f);
  }
  
  
  
  @Override public TypeContext visit(WhileStatement node) {
    checkType(node.getCondition());
    try {
      Expression exp = ts.makePrimitive(node.getCondition());
      if (!(getType(exp) instanceof BooleanType)) {
        throw new ExecutionError("condition.type", node);
      }
      node.setCondition(exp);
    }
    catch (UnsupportedConversionException e) {
      throw new ExecutionError("condition.type", node);
    }
    
    node.getBody().acceptVisitor(this);
    return context;
  }

  
  @Override public TypeContext visit(DoStatement node) {
    node.getBody().acceptVisitor(this);
    checkType(node.getCondition());
    try {
      Expression exp = ts.makePrimitive(node.getCondition());
      if (!(getType(exp) instanceof BooleanType)) {
        throw new ExecutionError("condition.type", node);
      }
      node.setCondition(exp);
    }
    catch (UnsupportedConversionException e) {
      throw new ExecutionError("condition.type", node);
    }

    return context;
  }

  
  @Override public TypeContext visit(ForStatement node) {
    TypeContext newContext = context;
    if (node.getInitialization() != null) { newContext = checkList(node.getInitialization()); }
    StatementChecker checker = new StatementChecker(newContext, opt);
    
    if (node.getCondition() != null) {
      checker.checkType(node.getCondition());
      try {
        Expression exp = ts.makePrimitive(node.getCondition());
        if (!(getType(exp) instanceof BooleanType)) {
          throw new ExecutionError("condition.type", node);
        }
        node.setCondition(exp);
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("condition.type", node);
      }
    }
    
    if (node.getUpdate() != null) { checker.checkList(node.getUpdate()); }

    node.getBody().acceptVisitor(checker);
    return context; 
  }

  
  @Override public TypeContext visit(ForEachStatement node) {
    FormalParameter p = node.getParameter();
    Type paramT = checkTypeName(p.getType());
    LocalVariable var = setVariable(p, new LocalVariable(p.getName(), paramT, p.getModifiers().isFinal()));
    TypeContext newContext = new LocalContext(context, var);
    Type collType = checkType(node.getCollection());

    if (ts.isArray(collType)) {
      Type elementType = ts.arrayElementType(collType);
      if (!ts.isAssignable(paramT, elementType)) {
        TypePrinter printer = ts.typePrinter();
        setErrorStrings(node, printer.print(elementType), printer.print(paramT));
        throw new ExecutionError("assignment.types", node);
      }
    }
    else if (ts.isIterable(collType)) {
      try {
        MethodInvocation iteratorInv = ts.lookupMethod(node.getCollection(), "iterator", IterUtil.<Type>empty(),
                                                       IterUtil.<Expression>empty(), Option.<Type>none(),
                                                       context.accessModule());
        
        
        Expression getIterator = TypeUtil.makeEmptyExpression(node.getCollection());
        setType(getIterator, iteratorInv.returnType());
        MethodInvocation nextInv = ts.lookupMethod(getIterator, "next", IterUtil.<Type>empty(),
                                                   IterUtil.<Expression>empty(), Option.<Type>none(),
                                                   context.accessModule());
        
        if (!ts.isAssignable(paramT, nextInv.returnType())) {
          TypePrinter printer = ts.typePrinter();
          setErrorStrings(node, printer.print(nextInv.returnType()), printer.print(paramT));
          throw new ExecutionError("assignment.types", node);
        }
      }
      catch (TypeSystemException e) { throw new RuntimeException("ts.isIterable() lied"); }
    }
    else {
      throw new ExecutionError("iterable.type", node);
    }
    
    node.getBody().acceptVisitor(new StatementChecker(newContext, opt));
    return context; 
  }

  
  @Override public TypeContext visit(IfThenStatement node) {
    checkType(node.getCondition());
    try {
      Expression exp = ts.makePrimitive(node.getCondition());
      if (!(getType(exp) instanceof BooleanType)) {
        throw new ExecutionError("condition.type", node);
      }
      node.setCondition(exp);
    }
    catch (UnsupportedConversionException e) {
      throw new ExecutionError("condition.type", node);
    }

    node.getThenStatement().acceptVisitor(this);
    return context;
  }

  
  @Override public TypeContext visit(IfThenElseStatement node) {
    checkType(node.getCondition());
    try {
      Expression exp = ts.makePrimitive(node.getCondition());
      if (!(getType(exp) instanceof BooleanType)) {
        throw new ExecutionError("condition.type", node);
      }
      node.setCondition(exp);
    }
    catch (UnsupportedConversionException e) {
      throw new ExecutionError("condition.type", node);
    }

    node.getThenStatement().acceptVisitor(this);
    node.getElseStatement().acceptVisitor(this);
    return context;
  }

  
  @Override public TypeContext visit(SwitchStatement node) {
    Type t = checkType(node.getSelector());
    boolean switchEnum = ts.isEnum(t);
    if (!switchEnum) {
      try {
        Expression exp = ts.makePrimitive(node.getSelector());
        if (!(getType(exp) instanceof IntegralType) || (getType(exp) instanceof LongType)) {
          setErrorStrings(node, ts.typePrinter().print(t));
          throw new ExecutionError("selector.type", node);
        }
        node.setSelector(exp);
        t = getType(exp);
      }
      catch (UnsupportedConversionException e) {
        throw new ExecutionError("selector.type", node);
      }
    }
    
    Set<Object> values = new HashSet<Object>();
    boolean hasDefault = false;
    for (SwitchBlock bk : node.getBindings()) {
      
      if (bk.getExpression() == null) {
        if (hasDefault) { throw new ExecutionError("duplicate.switch.case", node); }
        hasDefault = true;
      }
      else if (switchEnum) {
        DJField val = new ExpressionChecker(context, opt).checkEnumSwitchCase(bk.getExpression(), t);
        if (values.contains(val)) {
          throw new ExecutionError("duplicate.switch.case", bk);
        }
        values.add(val);
      }
      else {
        Expression exp = bk.getExpression();
        checkType(exp);
        if (!hasValue(exp) || getValue(exp) == null) {
          throw new ExecutionError("invalid.constant", exp);
        }
        if (!ts.isAssignable(t, getType(exp), getValue(exp))) {
          setErrorStrings(exp, ts.typePrinter().print(getType(exp)));
          throw new ExecutionError("switch.label.type", exp);
        }
        if (values.contains(getValue(exp))) { 
          throw new ExecutionError("duplicate.switch.case", bk);
        }
        values.add(getValue(exp));
      }
      
      if (bk.getStatements() != null) { checkList(bk.getStatements()); }
    }
      
    return context;
  }

  
  @Override public TypeContext visit(SwitchBlock node) {
    if (node.getExpression() != null) { checkType(node.getExpression()); }
    if (node.getStatements() != null) { checkList(node.getStatements()); }
    return context;
  }

  
  @Override public TypeContext visit(LabeledStatement node) {
    return node.getStatement().acceptVisitor(this);
  }

  
  @Override public TypeContext visit(TryStatement node) {
    List<Type> caughtTypes = new LinkedList<Type>();
    for (CatchStatement c : node.getCatchStatements()) {
      FormalParameter p = c.getException();
      Type caughtT = checkTypeName(p.getType());
      if (!ts.isAssignable(TypeSystem.THROWABLE, caughtT)) {
        setErrorStrings(c, ts.typePrinter().print(caughtT));
        throw new ExecutionError("catch.type", c);
      }
      if (!ts.isReifiable(caughtT)) {
        throw new ExecutionError("reifiable.type", c);
      }
      setVariable(p, new LocalVariable(p.getName(), caughtT, p.getModifiers().isFinal()));
      setErasedType(c, ts.erasedClass(caughtT));
      caughtTypes.add(caughtT);
    }
    
    TypeContext tryContext = new TryBlockContext(context, caughtTypes);
    node.getTryBlock().acceptVisitor(new StatementChecker(tryContext, opt));
    
    for (CatchStatement c : node.getCatchStatements()) {
      TypeContext catchContext = new LocalContext(context, getVariable(c.getException()));
      c.getBlock().acceptVisitor(new StatementChecker(catchContext, opt));
    }
    
    if (node.getFinallyBlock() != null) { node.getFinallyBlock().acceptVisitor(this); }
    
    return context;
  }

  
  @Override public TypeContext visit(ThrowStatement node) {
    Type thrown = checkType(node.getExpression());
    if (!ts.isAssignable(TypeSystem.THROWABLE, thrown)) {
      setErrorStrings(node, ts.typePrinter().print(thrown));
      throw new ExecutionError("throw.type", node);
    }
    else if (ts.isAssignable(TypeSystem.EXCEPTION, thrown)) {
      boolean valid = false;
      Iterable<Type> allowed = IterUtil.compose(TypeSystem.RUNTIME_EXCEPTION,
                                                context.getDeclaredThrownTypes());
      for (Type t : allowed) {
        if (ts.isAssignable(t, thrown)) { valid = true; break; }
      }
      if (!valid) {
        setErrorStrings(node, ts.typePrinter().print(thrown));
        throw new ExecutionError("uncaught.exception", node);
      }
    }
    return context;
  }

  
  @Override public TypeContext visit(ReturnStatement node) {
    Type expected = context.getReturnType();
    if (expected == null) { throw new ExecutionError("return.not.allowed", node); }

    if (node.getExpression() == null) {
      if (!expected.equals(TypeSystem.VOID)) {
        TypePrinter printer = ts.typePrinter();
        setErrorStrings(node, printer.print(TypeSystem.VOID), printer.print(expected));
        throw new ExecutionError("return.type", node);
      }
    }
    else {
      checkType(node.getExpression(), expected);
      try {
        Expression newExp = ts.assign(expected, node.getExpression());
        node.setExpression(newExp);
      }
      catch (UnsupportedConversionException e) {
        TypePrinter printer = ts.typePrinter();
        setErrorStrings(node, printer.print(getType(node.getExpression())), printer.print(expected));
        throw new ExecutionError("return.type", node);
      }
    }
    
    return context;
  }
  
    
  @Override public TypeContext visit(AssertStatement node) {
    checkType(node.getCondition());
    try {
      Expression exp = ts.makePrimitive(node.getCondition());
      if (!(getType(exp) instanceof BooleanType)) {
        throw new ExecutionError("condition.type", node);
      }
      node.setCondition(exp);
    }
    catch (UnsupportedConversionException e) {
      throw new ExecutionError("condition.type", node);
    }
    
    if (node.getFailString() != null) {
      Type failType = checkType(node.getFailString());
      if (failType instanceof VoidType) { throw new ExecutionError("assertion.fail.type", node); }
    }
    
    return context;
  }
  
  
  @Override public TypeContext visit(SynchronizedStatement node) {
    Type lockT = checkType(node.getLock());
    if (!ts.isReference(lockT)) { throw new ExecutionError("lock.type", node); }
    node.getBody().acceptVisitor(this);
    return context;
  }

  
  @Override public TypeContext visit(BlockStatement node) {
    checkList(node.getStatements());
    return context;
  }
  
  @Override public TypeContext visit(EmptyStatement node) {
    return context;
  }
  
  @Override public TypeContext visit(BreakStatement node) {
    return context; 
  }

  @Override public TypeContext visit(ContinueStatement node) {
    return context; 
  }

  @Override public TypeContext visit(ExpressionStatement node) {
    if (node.getExpression() instanceof SimpleAssignExpression &&
        !opt.requireVariableType() && (node.getHasSemicolon() || !opt.requireSemicolon())) {
      SimpleAssignExpression assign = (SimpleAssignExpression) node.getExpression();
      if (assign.getLeftExpression() instanceof AmbiguousName) {
        AmbiguousName ambigName = (AmbiguousName) assign.getLeftExpression();
        if (ambigName.getIdentifiers().size() == 1) {
          String name = ambigName.getRepresentation();
          if (!context.variableExists(name, opt.typeSystem())) {
            Node decl = new VariableDeclaration(ModifierSet.make(), null, name, assign.getRightExpression(),
                                                node.getSourceInfo());
            setStatementTranslation(node, decl);
            return decl.acceptVisitor(this);
          }
        }
      }
    }
    
    checkType(node.getExpression());
    return context;
  }
  
}
