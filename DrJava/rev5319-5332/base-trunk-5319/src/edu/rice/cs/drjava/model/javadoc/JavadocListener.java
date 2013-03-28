

package edu.rice.cs.drjava.model.javadoc;

import java.io.File;
import java.util.List;
import edu.rice.cs.drjava.model.compiler.CompilerListener;


public interface JavadocListener {
  
  
  public void saveBeforeJavadoc();

  
  public void compileBeforeJavadoc(final CompilerListener afterCompile);
  
  
  public void javadocStarted();
  
  
  public void javadocEnded(boolean success, File destDir, boolean allDocs);
}
