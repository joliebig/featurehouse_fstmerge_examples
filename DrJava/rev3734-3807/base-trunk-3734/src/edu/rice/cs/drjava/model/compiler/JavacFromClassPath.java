

package edu.rice.cs.drjava.model.compiler;


public class JavacFromClassPath extends CompilerProxy {
  public static final CompilerInterface ONLY = new JavacFromClassPath();

  
  private JavacFromClassPath() {
    super("edu.rice.cs.drjava.model.compiler.JavacGJCompiler",
          JavacFromClassPath.class.getClassLoader());
  }

  public String getName() { return super.getName(); }
}
