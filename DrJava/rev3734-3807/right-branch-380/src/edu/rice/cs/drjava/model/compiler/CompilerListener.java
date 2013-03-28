

package edu.rice.cs.drjava.model.compiler;

import java.io.File;


public interface CompilerListener {
  
  
  public void compileStarted();

  
  public void compileEnded(File workDir, File[] excludedFiles);
  
  
  public void saveBeforeCompile();

  
  public void saveUntitled();
}