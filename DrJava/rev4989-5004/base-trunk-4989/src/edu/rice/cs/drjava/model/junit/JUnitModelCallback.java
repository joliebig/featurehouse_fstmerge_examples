

package edu.rice.cs.drjava.model.junit;

import java.io.File;
import edu.rice.cs.util.classloader.ClassFileError;


public interface JUnitModelCallback {
  
  
  public void nonTestCase(boolean isTestAll, boolean didCompileFail);
  
  
  public void classFileError(ClassFileError e);
  
  
  public void testSuiteStarted(int numTests);
  
  
  public void testStarted(String testName);
  
  
  public void testEnded(String testName, boolean wasSuccessful, boolean causedError);
  
  
  public void testSuiteEnded(JUnitError[] errors);
  
  
  public File getFileForClassName(String className);
  
  
  public Iterable<File> getClassPath();
  
  
  public void junitJVMReady();
}