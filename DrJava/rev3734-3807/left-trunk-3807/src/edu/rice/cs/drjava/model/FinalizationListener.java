

package edu.rice.cs.drjava.model;


public interface FinalizationListener<T> {
 
  
  public void finalized(FinalizationEvent<T> fe);
  
}