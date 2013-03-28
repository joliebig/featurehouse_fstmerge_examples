

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.util.List;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.plt.reflect.JavaVersion;


public interface CompilerInterface {
  
  
  boolean isAvailable();
  
  
  List<? extends DJError> compile(List<? extends File> files, List<? extends File> classPath, 
                                        List<? extends File> sourcePath, File destination, 
                                        List<? extends File> bootClassPath, String sourceVersion, boolean showWarnings);
  
  
  JavaVersion version();
  
  
  String getName();
  
  
  String getDescription();
  
  
  String toString();
  
}
