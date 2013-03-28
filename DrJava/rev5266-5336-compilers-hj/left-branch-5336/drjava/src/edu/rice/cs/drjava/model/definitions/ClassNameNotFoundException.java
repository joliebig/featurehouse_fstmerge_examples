

package edu.rice.cs.drjava.model.definitions;


public class ClassNameNotFoundException extends Exception {
  
  
  public static final ClassNameNotFoundException DEFAULT = new 
    ClassNameNotFoundException("No top level class name found");
  
  public ClassNameNotFoundException(String s) {
    super(s);
  }
}