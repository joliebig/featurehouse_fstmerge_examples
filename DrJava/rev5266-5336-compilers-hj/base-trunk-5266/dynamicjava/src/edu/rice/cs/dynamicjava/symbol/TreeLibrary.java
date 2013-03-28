package edu.rice.cs.dynamicjava.symbol;

import koala.dynamicjava.interpreter.NodeProperties;
import koala.dynamicjava.tree.CompilationUnit;
import koala.dynamicjava.tree.Node;
import koala.dynamicjava.tree.PackageDeclaration;
import koala.dynamicjava.tree.TypeDeclaration;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.interpreter.TreeClassLoader;
import edu.rice.cs.plt.collect.IndexedInjectiveRelation;
import edu.rice.cs.plt.collect.InjectiveRelation;
import edu.rice.cs.plt.collect.TotalMap;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Lambda;

public class TreeLibrary implements Library {
  
  private final TotalMap<String, InjectiveRelation<String, DJClass>> _packages;
  private final TreeClassLoader _loader;

  
  public TreeLibrary(Iterable<CompilationUnit> cus, ClassLoader parentLoader, Options opt) {
    _packages = new TotalMap<String, InjectiveRelation<String, DJClass>>(
        new Lambda<String, InjectiveRelation<String, DJClass>>() {
          public InjectiveRelation<String, DJClass> value(String pkg) {
            return new IndexedInjectiveRelation<String, DJClass>();
          }
        }, true);
    _loader = new TreeClassLoader(parentLoader, opt); 
    for (CompilationUnit cu : cus) {
      PackageDeclaration pd = cu.getPackage();
      String pkg = (pd == null) ? "" : pd.getName();
      InjectiveRelation<String, DJClass> classes = _packages.get(pkg);
      for (Node ast : cu.getDeclarations()) {
        if (ast instanceof TypeDeclaration) {
          String declaredName = ((TypeDeclaration) ast).getName();
          String fullName = pkg.equals("") ? declaredName : pkg + "." + declaredName;
          DJClass c = new TreeClass(fullName, null, null, ast, _loader, opt);
          NodeProperties.setDJClass(ast, c);
          classes.add(declaredName, c);
        }
      }
    }
  }
  
  public Iterable<DJClass> declaredClasses(String fullName) {
    String packageName, className;
    int dot = fullName.lastIndexOf('.');
    if (dot == -1) { packageName = ""; className = fullName; }
    else { packageName = fullName.substring(0, dot); className = fullName.substring(dot + 1); }
    if (_packages.containsOverride(packageName)) {
      return _packages.get(packageName).matchFirst(className);
    }
    else { return IterUtil.empty(); }
  }

  public ClassLoader classLoader() { return _loader; }
}
