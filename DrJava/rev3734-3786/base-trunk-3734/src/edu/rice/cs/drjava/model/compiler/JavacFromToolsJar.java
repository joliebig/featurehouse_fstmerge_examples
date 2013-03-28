

package edu.rice.cs.drjava.model.compiler;

import edu.rice.cs.util.classloader.ToolsJarClassLoader;


public class JavacFromToolsJar extends CompilerProxy {
  public static final CompilerInterface ONLY = new JavacFromToolsJar();

  
  private JavacFromToolsJar() {
    super("edu.rice.cs.drjava.model.compiler.JavacGJCompiler",
          new ToolsJarClassLoader());
  }

  
  public String getName() {
    return super.getName() + " (tools.jar)";
  }
}
