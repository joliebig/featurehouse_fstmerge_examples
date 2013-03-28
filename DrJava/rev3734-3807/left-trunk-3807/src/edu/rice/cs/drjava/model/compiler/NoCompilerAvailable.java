

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.ClassPathVector;


public class NoCompilerAvailable implements CompilerInterface {
  public static final CompilerInterface ONLY = new NoCompilerAvailable();
  private static final String MESSAGE = "No compiler is available.";

  private NoCompilerAvailable() { }

  public CompilerError[] compile(File sourceRoot, File[] files) {
    File[] sourceRoots = new File[] { sourceRoot };
    return compile(sourceRoots, files);
  }
  
  public CompilerError[] compile(File[] sourceRoots, File[] files) {
    CompilerError error = new CompilerError(files[0], -1, -1, MESSAGE, false);
    return new CompilerError[] { error };
  }

  public boolean isAvailable() { return true; }

  public String getName() { return "(no compiler available)"; }

  public String toString() { return getName(); }

   
  public void setExtraClassPath(String extraClassPath) { }
  
  
  public void setExtraClassPath(ClassPathVector extraClassPath) { }
    
  
  public void setAllowAssertions(boolean allow) { }
  
  
  public void setWarningsEnabled(boolean warningsEnabled) { }
  
  
  public void addToBootClassPath( File cp) {
    throw new UnexpectedException( new Exception("Method only implemented in JSR14Compiler"));
  }
  
  public void setBuildDirectory(File builddir) { }
}



