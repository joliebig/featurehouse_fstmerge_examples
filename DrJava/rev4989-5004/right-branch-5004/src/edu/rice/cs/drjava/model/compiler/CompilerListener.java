

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.util.List;


public interface CompilerListener {
  
  
  public void compileStarted();

  
  public void compileEnded(File workDir, List<? extends File> excludedFiles);
  
  
  public void compileAborted(Exception e);
  
  
  public void saveBeforeCompile();

  
  public void saveUntitled();
  
  
  public void activeCompilerChanged();
}