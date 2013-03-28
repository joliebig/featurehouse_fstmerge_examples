package edu.rice.cs.dynamicjava.interpreter;

import edu.rice.cs.dynamicjava.symbol.*;
import edu.rice.cs.dynamicjava.symbol.type.ClassType;
import edu.rice.cs.dynamicjava.symbol.type.VariableType;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class ClassSignatureContext extends DelegatingContext {
  
  private DJClass _c;
  private ClassLoader _loader;
  
  public ClassSignatureContext(TypeContext next, DJClass c, ClassLoader loader) {
    super(next);
    _c = c;
    _loader = loader;
  }
  
  protected ClassSignatureContext duplicate(TypeContext next) {
    return new ClassSignatureContext(next, _c, _loader);
  }
  
  
  @Override public boolean typeExists(String name, TypeSystem ts) {
    return matchesClass(name) || matchesTypeVariable(name) || super.typeExists(name, ts);
  }
  
  private boolean matchesTopLevelClass(String name) {
    return !_c.isAnonymous() && _c.declaringClass() == null && _c.declaredName().equals(name);
  }
  
  private boolean matchesMemberClass(String name) {
    return !_c.isAnonymous() && _c.declaringClass() != null && _c.declaredName().equals(name);
  }
  
  private boolean matchesClass(String name) {
    return !_c.isAnonymous() && _c.declaredName().equals(name);
  }
  
  private boolean matchesTypeVariable(String name) {
    return declaredTypeVariable(name) != null;
  }
  
  private VariableType declaredTypeVariable(String name) {
    for (VariableType t : _c.declaredTypeParameters()) {
      if (t.symbol().name().equals(name)) { return t; }
    }
    return null;
  }
  
  
  @Override public boolean topLevelClassExists(String name, TypeSystem ts) {
    return matchesTopLevelClass(name) ||
          (!matchesMemberClass(name) && !matchesTypeVariable(name) && super.topLevelClassExists(name, ts));
  }
  
  
  @Override public DJClass getTopLevelClass(String name, TypeSystem ts) throws AmbiguousNameException {
    if (matchesTopLevelClass(name)) {
      return _c;
    }
    else if (!matchesMemberClass(name) && !matchesTypeVariable(name)) {
      return super.getTopLevelClass(name, ts);
    }
    else { return null; }
  }
  
  
  @Override public boolean memberClassExists(String name, TypeSystem ts) {
    return matchesMemberClass(name) ||
          (!matchesTopLevelClass(name) && !matchesTypeVariable(name) && super.memberClassExists(name, ts));
  }
  
  
  @Override public ClassType typeContainingMemberClass(String name, TypeSystem ts) throws AmbiguousNameException {
    debug.logStart(new String[]{"class","name"}, _c, name); try {
      
    if (matchesMemberClass(name)) {
      return SymbolUtil.thisType(_c.declaringClass());
    }
    else if (!matchesTopLevelClass(name) && !matchesTypeVariable(name)) {
      return super.typeContainingMemberClass(name, ts);
    }
    else { return null; }
    
    } finally { debug.logEnd(); }
  }
  
  
  @Override public boolean typeVariableExists(String name, TypeSystem ts) {
    return matchesTypeVariable(name) ||
          (!matchesClass(name) && super.typeVariableExists(name, ts));
  }
  
  
  @Override public VariableType getTypeVariable(String name, TypeSystem ts) {
    VariableType result = declaredTypeVariable(name);
    if (result != null) { return result; }
    else if (!matchesClass(name)) { return super.getTypeVariable(name, ts); }
    else { return null; }
  }
  
  @Override public ClassLoader getClassLoader() { return _loader; }
  
  @Override public Access.Module accessModule() { return _c.accessModule(); }
  
}
