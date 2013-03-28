

package edu.rice.cs.drjava.model.compiler;

import java.util.List;
import java.io.File;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.plt.reflect.JavaVersion;


public abstract class JavacCompiler implements CompilerInterface {
  
  private JavaVersion.FullVersion _version;
  private String _location;
  protected List<? extends File> _defaultBootClassPath;
  
  protected JavacCompiler(JavaVersion.FullVersion version, String location, List<? extends File> defaultBootClassPath) {
    _version = version;
    _location = location;
    _defaultBootClassPath = defaultBootClassPath;
  }
  
  public abstract boolean isAvailable();
  
  public abstract List<? extends DJError> compile(List<? extends File> files, List<? extends File> classPath, 
                                                        List<? extends File> sourcePath, File destination, 
                                                        List<? extends File> bootClassPath, String sourceVersion, 
                                                        boolean showWarnings);
  
  public JavaVersion version() { return _version.majorVersion(); } 
 
  public String getName() { return "JDK " + _version.versionString(); }
  
  public String getDescription() { return getName() + " from " + _location; }
  
  public String toString() { return getName(); }

}
