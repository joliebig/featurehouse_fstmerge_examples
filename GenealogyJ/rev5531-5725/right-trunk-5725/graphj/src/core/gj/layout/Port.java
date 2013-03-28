
package gj.layout;


public enum Port {
  
  North() { @Override public Port opposite() { return South; } },
  West () { @Override public Port opposite() { return East ; } },
  East () { @Override public Port opposite() { return West ; } },
  South() { @Override public Port opposite() { return North; } },
  None () { @Override public Port opposite() { return this ; } },
  Fixed() { @Override public Port opposite() { return this ; } };
  
  public abstract Port opposite();
  
} 

