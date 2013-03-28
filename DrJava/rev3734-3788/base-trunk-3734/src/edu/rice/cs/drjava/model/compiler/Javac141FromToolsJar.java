

package edu.rice.cs.drjava.model.compiler;

import edu.rice.cs.util.classloader.ToolsJarClassLoader;


public class Javac141FromToolsJar extends CompilerProxy {
  public static final CompilerInterface ONLY = new Javac141FromToolsJar();

  
  private Javac141FromToolsJar() {
    super("edu.rice.cs.drjava.model.compiler.Javac141Compiler", new ToolsJarClassLoader());
  }
}
