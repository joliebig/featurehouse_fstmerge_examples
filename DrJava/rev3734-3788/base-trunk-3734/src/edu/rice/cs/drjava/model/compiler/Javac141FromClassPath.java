

package edu.rice.cs.drjava.model.compiler;


public class Javac141FromClassPath extends CompilerProxy {
  public static final CompilerInterface ONLY = new Javac141FromClassPath();

  
  private Javac141FromClassPath() {
    super("edu.rice.cs.drjava.model.compiler.Javac141Compiler", Javac141FromClassPath.class.getClassLoader());
  }
}
