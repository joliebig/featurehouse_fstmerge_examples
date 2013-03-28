

package edu.rice.cs.drjava.model.junit;

import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import java.util.List;


public interface JUnitListener {
  
  
  public void nonTestCase(boolean isTestAll, boolean didCompileFail);
  
  
  public void classFileError(ClassFileError e);
  
  
  public void junitStarted();
  
  
  public void junitClassesStarted();
  
  
  public void junitSuiteStarted(int numTests);
  
  
  public void junitTestStarted(String name);
  
  
  public void junitTestEnded(String name, boolean wasSuccessful, boolean causedError);
  
  
  public void junitEnded();
  
  
  public void compileBeforeJUnit(final CompilerListener l, List<OpenDefinitionsDocument> outOfSync);
}