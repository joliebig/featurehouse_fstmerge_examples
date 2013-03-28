package edu.rice.cs.dynamicjava.sourcechecker;

import java.util.LinkedList;
import java.util.List;

import koala.dynamicjava.interpreter.NodeProperties;
import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.tree.*;
import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.interpreter.*;
import edu.rice.cs.plt.collect.UnindexedRelation;
import edu.rice.cs.plt.collect.Relation;
import edu.rice.cs.plt.tuple.Pair;

public class CompilationUnitChecker {
  
  
  private final TypeContext _context;
  private final Options _opt;
  
  public CompilationUnitChecker(TypeContext context, Options opt) {
    _context = context;
    _opt = opt;
  }
  
  
  public Relation<TypeDeclaration, ClassChecker> extractDeclarations(CompilationUnit u) throws InterpreterException {
    TypeContext c = _context;
    PackageDeclaration pkg = u.getPackage();
    if (pkg != null) { c = c.setPackage(pkg.getName()); }
    List<CheckerException> errors = new LinkedList<CheckerException>();
    for (ImportDeclaration imp : u.getImports()) {
      try { c = imp.acceptVisitor(new StatementChecker(c, _opt)); }
      catch (ExecutionError e) { errors.add(new CheckerException(e)); }
    }
    if (!errors.isEmpty()) { throw new CompositeException(errors); }
    
    Relation<TypeDeclaration, ClassChecker> results = UnindexedRelation.makeLinkedHashBased();
    ClassLoader loader = _context.getClassLoader(); 
    for (Node decl : u.getDeclarations()) {
      if (decl instanceof TypeDeclaration) {
        ClassChecker checker = new ClassChecker(NodeProperties.getDJClass(decl), loader, c, _opt);
        results.add(Pair.make((TypeDeclaration) decl, checker));
      }
      else { throw new RuntimeException("Unrecognized compilation unit declaration"); }
    }
    return results;
  }
  
}
