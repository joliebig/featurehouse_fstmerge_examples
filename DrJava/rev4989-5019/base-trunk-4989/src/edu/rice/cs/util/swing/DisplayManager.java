

package edu.rice.cs.util.swing;

import javax.swing.Icon;


public interface DisplayManager<T> {
  
  
  public Icon getIcon(T f);
  
  
  public String getName(T f);
    
}