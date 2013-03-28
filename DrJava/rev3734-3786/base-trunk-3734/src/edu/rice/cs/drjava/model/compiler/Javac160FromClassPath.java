

package edu.rice.cs.drjava.model.compiler;


public class Javac160FromClassPath extends CompilerProxy {
  
  public static final CompilerInterface ONLY = new Javac160FromClassPath();

  private static final String VERSION = System.getProperty("java.specification.version");

  
  private Javac160FromClassPath() {
    super("edu.rice.cs.drjava.model.compiler.Javac160Compiler", Javac160FromClassPath.class.getClassLoader());
  }

  public boolean isAvailable() { return VERSION.equals("1.6") && super.isAvailable(); }
  public String getName() { return "javac 1.6.0"; }
}
