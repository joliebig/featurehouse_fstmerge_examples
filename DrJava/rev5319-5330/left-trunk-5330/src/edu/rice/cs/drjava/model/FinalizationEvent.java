

package edu.rice.cs.drjava.model;


public class FinalizationEvent<T> {
 
  private T _obj;
  
  
  public FinalizationEvent(T obj) {
    _obj = obj;
  }
  
  
  public T getObject() {
    return _obj;
  }
  
}