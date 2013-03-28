package edu.rice.cs.dynamicjava.symbol;


public enum Access {
  PUBLIC, PROTECTED, PACKAGE, PRIVATE;
  
  
  public static interface Limited {
    
    public Access accessibility();
    
    public Module accessModule();
    
    public String declaredName();
  }
  
  
  public static interface Module {
    public String packageName();
  }
}
