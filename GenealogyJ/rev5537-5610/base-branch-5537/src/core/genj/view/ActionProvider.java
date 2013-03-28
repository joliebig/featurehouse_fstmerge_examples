
package genj.view;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;

import java.util.List;


public interface ActionProvider {
  
  
  public List createActions(Property[] properties);
  
  
  public List createActions(Property property);

  
  public List createActions(Entity entity);

  
  public List createActions(Gedcom gedcom);

} 
