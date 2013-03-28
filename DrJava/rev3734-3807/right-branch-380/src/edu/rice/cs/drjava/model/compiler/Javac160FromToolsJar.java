

package edu.rice.cs.drjava.model.compiler;

import edu.rice.cs.util.classloader.ToolsJarClassLoader;


public class Javac160FromToolsJar extends CompilerProxy {
  public static final CompilerInterface ONLY = new Javac160FromToolsJar();
  
  private static final String VERSION = System.getProperty("java.specification.version");

  
  private Javac160FromToolsJar() {
    super("edu.rice.cs.drjava.model.compiler.Javac160Compiler", new ToolsJarClassLoader());
  }

  public boolean isAvailable() { return VERSION.equals("1.6") && super.isAvailable(); }
  public String getName() { return "javac 1.6.0"; }
}
