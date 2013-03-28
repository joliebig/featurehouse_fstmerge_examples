



package edu.rice.cs.dynamicjava.interpreter;

import java.util.*;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Lambda;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.symbol.*;
import edu.rice.cs.dynamicjava.symbol.TypeSystem.*;
import edu.rice.cs.dynamicjava.symbol.type.*;

import koala.dynamicjava.tree.*;
import koala.dynamicjava.tree.tiger.*;
import koala.dynamicjava.tree.visitor.*;
import koala.dynamicjava.interpreter.error.ExecutionError;

import static koala.dynamicjava.interpreter.NodeProperties.*;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class TypeNameChecker {
  
  private final TypeContext context;
  private final TypeSystem ts;
  @SuppressWarnings("unused") private final Options opt;
  private final TypeNameVisitor visitor; 

  public TypeNameChecker(TypeContext ctx, Options options) {
    context = ctx;
    ts = options.typeSystem();
    opt = options;
    visitor = new TypeNameVisitor();
  }
  
  
  public Type check(TypeName t) {
    Type result = t.acceptVisitor(visitor);
    ensureWellFormed(t);
    return result;
  }
  
  
  public Type checkStructure(TypeName t) {
    return t.acceptVisitor(visitor);
  }
  
  
  public void ensureWellFormed(TypeName t) {
    if (!ts.isWellFormed(getType(t))) {
      throw new ExecutionError("malformed.type", t);
    }
  }
  
  
  public Iterable<Type> checkList(Iterable<? extends TypeName> l) {
    Iterable<Type> result = IterUtil.mapSnapshot(l, visitor);
    ensureWellFormedList(l);
    return result;
  }
  
  
  public Iterable<Type> checkStructureForList(Iterable<? extends TypeName> l) {
    Iterable<Type> result = IterUtil.mapSnapshot(l, visitor);
    return result;
  }
  
  
  public void ensureWellFormedList(Iterable<? extends TypeName> l) {
    for (TypeName t : l) { ensureWellFormed(t); }
  }
  
  
  
  public void checkTypeParameters(Iterable<? extends TypeParameter> tparams) {
    checkStructureForTypeParameters(tparams);
    ensureWellFormedTypeParameters(tparams);
  }

  
  public void checkStructureForTypeParameters(Iterable<? extends TypeParameter> tparams) {
    for (TypeParameter tparam : tparams) {
      setTypeVariable(tparam, new VariableType(new BoundedSymbol(tparam, tparam.getRepresentation())));
    }
    for (TypeParameter param : tparams) {
      Type upperBound = checkStructure(param.getBound());
      if (!param.getInterfaceBounds().isEmpty()) {
        
        upperBound = new IntersectionType(IterUtil.compose(upperBound, checkList(param.getInterfaceBounds())));
      }
      BoundedSymbol b = getTypeVariable(param).symbol();
      b.initializeUpperBound(upperBound);
      b.initializeLowerBound(TypeSystem.NULL);
    }
  }
  
  
  public void ensureWellFormedTypeParameters(Iterable<? extends TypeParameter> tparams) {
    for (TypeParameter tparam : tparams) {
      if (!ts.isWellFormed(getTypeVariable(tparam))) {
        throw new ExecutionError("malformed.type", tparam);
      }
    }
  }
  
  private class TypeNameVisitor extends AbstractVisitor<Type> implements Lambda<TypeName, Type> {
  
    public Type value(TypeName t) { return t.acceptVisitor(this); }
        
    
    @Override public Type visit(BooleanTypeName node) { return setType(node, TypeSystem.BOOLEAN); }
    
    
    @Override public Type visit(ByteTypeName node) { return setType(node, TypeSystem.BYTE); }
    
    
    @Override public Type visit(ShortTypeName node) { return setType(node, TypeSystem.SHORT); }
    
    
    @Override public Type visit(CharTypeName node) { return setType(node, TypeSystem.CHAR); }
    
    
    @Override public Type visit(IntTypeName node) { return setType(node, TypeSystem.INT); }
    
    
    @Override public Type visit(LongTypeName node) { return setType(node, TypeSystem.LONG); }
    
    
    @Override public Type visit(FloatTypeName node) { return setType(node, TypeSystem.FLOAT); }
    
    
    @Override public Type visit(DoubleTypeName node) { return setType(node, TypeSystem.DOUBLE); }
    
    
    @Override public Type visit(VoidTypeName node) { return setType(node, TypeSystem.VOID); }
    
    
    @Override public Type visit(ReferenceTypeName node) {
      Iterator<? extends IdentifierToken> ids = node.getIdentifiers().iterator();
      String name = "";
      Type t = null;
      
      boolean first = true;
      while (t == null && ids.hasNext()) {
        if (!first) { name += "."; }
        first = false;
        name += ids.next().image();
        
        try {
          DJClass c = context.getTopLevelClass(name, ts);
          if (c != null) { t = ts.makeClassType(c); }
          else {
            t = context.getTypeVariable(name, ts);
            if (t == null) {
              Type outer = context.typeContainingMemberClass(name, ts);
              if (outer != null) { t = ts.lookupClass(outer, name, IterUtil.<Type>empty(), context.accessModule()); }
            }
          }
        }
        catch (AmbiguousNameException e) {
          setErrorStrings(node, name);
          throw new ExecutionError("ambiguous.name", node);
        }
        catch (InvalidTypeArgumentException e) { throw new ExecutionError("type.argument.arity", node); }
        catch (UnmatchedLookupException e) {
          if (e.matches() == 0) { throw new ExecutionError("undefined.name.noinfo", node); }
          else {
            setErrorStrings(node, name);
            throw new ExecutionError("ambiguous.name", node);
          }
        }
      }
      while (ids.hasNext()) {
        String nextId = ids.next().image();
        try {
          ClassType memberType = ts.lookupClass(t, nextId, IterUtil.<Type>empty(), context.accessModule());
          t = memberType;
        }
        catch (InvalidTypeArgumentException e) { throw new ExecutionError("type.argument.arity", node); }
        catch (UnmatchedLookupException e) {
          if (e.matches() == 0) { throw new ExecutionError("undefined.name.noinfo", node); }
          else {
            setErrorStrings(node, nextId);
            throw new ExecutionError("ambiguous.name", node);
          }
        }
      }
      
      if (t == null) { 
        setErrorStrings(node, node.getRepresentation());
        throw new ExecutionError("undefined.class", node);
      }
      return setType(node, t);
    }
    
    
    @Override public Type visit(GenericReferenceTypeName node) {
      Iterator<? extends IdentifierToken> ids = node.getIdentifiers().iterator();
      Iterator<? extends List<? extends TypeName>> allTargs = node.getTypeArguments().iterator();
      String name = "";
      Type t = null;
      
      boolean first = true;
      while (t == null && ids.hasNext()) {
        if (!first) { name += "."; }
        first = false;
        name += ids.next().image();
        List<? extends TypeName> targsNames = allTargs.next();
        Iterable<Type> targs = checkStructureForList(targsNames);
        
        try {
          DJClass c = context.getTopLevelClass(name, ts);
          t = (c == null) ? null : ts.makeClassType(c, targs);
          if (t == null) {
            Type outer = context.typeContainingMemberClass(name, ts);
            if (outer != null) { t = ts.lookupClass(outer, name, targs, context.accessModule()); }
          }
          if (t == null) { 
            if (!IterUtil.isEmpty(targs)) {
              setErrorStrings(node, name);
              throw new ExecutionError("undefined.class", node);
            }
            t = context.getTypeVariable(name, ts);
          }
        }
        catch (AmbiguousNameException e) {
          setErrorStrings(node, name);
          throw new ExecutionError("ambiguous.name", node);
        }
        catch (InvalidTypeArgumentException e) { throw new ExecutionError("type.argument.arity", node); }
        catch (UnmatchedLookupException e) {
          if (e.matches() == 0) { throw new ExecutionError("undefined.name.noinfo", node); }
          else {
            setErrorStrings(node, name);
            throw new ExecutionError("ambiguous.name", node);
          }
        }
      }
      
      while (ids.hasNext()) {
        String nextId = ids.next().image();
        try {
          Iterable<Type> targs = checkStructureForList(allTargs.next());
          ClassType memberType = ts.lookupClass(t, nextId, targs, context.accessModule());
          t = memberType;
        }
        catch (InvalidTypeArgumentException e) { throw new ExecutionError("type.argument", node); }
        catch (UnmatchedLookupException e) {
          if (e.matches() == 0) { throw new ExecutionError("undefined.name.noinfo", node); }
          else {
            setErrorStrings(node, nextId);
            throw new ExecutionError("ambiguous.name", node);
          }
        }
      }
      
      if (t == null) { 
        setErrorStrings(node, node.getRepresentation());
        throw new ExecutionError("undefined.class", node);
      }
      return setType(node, t);
    }
    
    
    @Override public Type visit(HookTypeName node) {
      Type upper = TypeSystem.OBJECT;
      if (node.getUpperBound().isSome()) {
        upper = checkStructure(node.getUpperBound().unwrap());
        if (!ts.isReference(upper)) {
          setErrorStrings(node, ts.typePrinter().print(upper));
          throw new ExecutionError("wildcard.bound", node);
        }
      }
      
      Type lower = TypeSystem.NULL;
      if (node.getLowerBound().isSome()) {
        lower = checkStructure(node.getLowerBound().unwrap());
        if (!ts.isReference(lower)) {
          setErrorStrings(node, ts.typePrinter().print(lower));
          throw new ExecutionError("wildcard.bound", node);
        }
      }

      return setType(node, new Wildcard(new BoundedSymbol(node, upper, lower)));
    }
    
    
    @Override public Type visit(ArrayTypeName node) {
      Type elementType = checkStructure(node.getElementType());
      Type arrayT = node.isVararg() ? new VarargArrayType(elementType) :
        new SimpleArrayType(elementType);
      return setType(node, arrayT);
    }
    
  }
  
}
