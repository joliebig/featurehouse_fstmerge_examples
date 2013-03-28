
package genj.io;

import genj.gedcom.Entity;
import genj.gedcom.Property;


public interface Filter {

  public String getName();
  
  public boolean veto(Property property);

  public boolean veto(Entity entity);
  
} 
  
