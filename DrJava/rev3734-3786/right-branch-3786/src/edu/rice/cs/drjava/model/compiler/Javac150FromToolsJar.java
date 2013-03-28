

package edu.rice.cs.drjava.model.compiler;

import edu.rice.cs.util.classloader.ToolsJarClassLoader;


public class Javac150FromToolsJar extends CompilerProxy {
  public static final CompilerInterface ONLY = new Javac150FromToolsJar();

  
  private Javac150FromToolsJar() {
    super("edu.rice.cs.drjava.model.compiler.Javac150Compiler", new ToolsJarClassLoader());
  }

  public boolean isAvailable() { return VERSION.equals("1.5") && super.isAvailable(); }
  public String getName() { return "javac 1.5.0"; }
}
