

package edu.rice.cs.drjava.model.junit;

import java.util.List;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;


public interface JUnitListener {

  
  public void nonTestCase(boolean isTestAll);
  
  
  public void classFileError(ClassFileError e);

  
  public void compileBeforeJUnit();

  
  public void junitStarted();

  
  public void junitClassesStarted();
  
  
  public void junitSuiteStarted(int numTests);

  
  public void junitTestStarted(String name);

  
  public void junitTestEnded(String name, boolean wasSuccessful, boolean causedError);

  
  public void junitEnded();
}