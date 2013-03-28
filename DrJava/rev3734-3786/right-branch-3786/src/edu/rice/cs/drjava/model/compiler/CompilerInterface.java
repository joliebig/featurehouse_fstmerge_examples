

package edu.rice.cs.drjava.model.compiler;

import  java.io.File;
import  edu.rice.cs.util.ClassPathVector;


public interface CompilerInterface {
  
  
  
  CompilerError[] compile(File sourceRoot, File[] files);
  
  
  CompilerError[] compile(File[] sourceRoots, File[] files);

  
  boolean isAvailable();

  
  String getName();

  
  String toString();
  
   
  void setExtraClassPath(String extraClassPath);
  
  
  void setExtraClassPath(ClassPathVector extraClassPath);
  
  
  void setAllowAssertions(boolean allow);
  
  
  void setWarningsEnabled(boolean warningsEnabled);
  
   
  void addToBootClassPath(File s);
  
  
  void setBuildDirectory(File dir);
  
}



