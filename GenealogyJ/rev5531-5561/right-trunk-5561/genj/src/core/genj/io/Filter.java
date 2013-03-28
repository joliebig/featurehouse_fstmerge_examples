
package genj.io;

import genj.gedcom.Property;


public interface Filter {

    
  public boolean checkFilter(Property property);
  
  
  public String getFilterName();
  
} 
  
