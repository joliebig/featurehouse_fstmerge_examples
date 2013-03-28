

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.plt.reflect.JavaVersion;


public class NoCompilerAvailable implements CompilerInterface {
  public static final CompilerInterface ONLY = new NoCompilerAvailable();
  private static final String MESSAGE = "No compiler is available.";
  
  private NoCompilerAvailable() { }
  
  public boolean isAvailable() { return false; }
  
  public List<? extends DJError> compile(List<? extends File> files, List<? extends File> classPath, 
                                               List<? extends File> sourcePath, File destination, 
                                               List<? extends File> bootClassPath, String sourceVersion, boolean showWarnings) {
    return Arrays.asList(new DJError(MESSAGE, false));
  }
  
  public JavaVersion version() { return JavaVersion.UNRECOGNIZED; }
  
  public String getName() { return "(no compiler available)"; }
  
  public String getDescription() { return getName(); }
  
  
  @Override
  public String toString() { return "None"; }
}
