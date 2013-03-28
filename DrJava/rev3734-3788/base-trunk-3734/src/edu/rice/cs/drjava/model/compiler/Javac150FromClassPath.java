

package edu.rice.cs.drjava.model.compiler;


public class Javac150FromClassPath extends CompilerProxy {
  
  public static final CompilerInterface ONLY = new Javac150FromClassPath();

  private static final String VERSION = System.getProperty("java.specification.version");

  
  private Javac150FromClassPath() {
    super("edu.rice.cs.drjava.model.compiler.Javac150Compiler", Javac150FromClassPath.class.getClassLoader());
  }

  public boolean isAvailable() { return VERSION.equals("1.5") && super.isAvailable(); }
  public String getName() { return "javac 1.5.0"; }
}
