package edu.rice.cs.dynamicjava.symbol;


public interface Library {
  
  
  public Iterable<DJClass> declaredClasses(String fullName);
  
  
  public ClassLoader classLoader();

}
