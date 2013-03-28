

package edu.rice.cs.drjava.model;
import java.util.List;


public interface Finalizable<T> {
 
  
  public void addFinalizationListener(FinalizationListener<T> fl);

  
  public List<FinalizationListener<T>> getFinalizationListeners();

}