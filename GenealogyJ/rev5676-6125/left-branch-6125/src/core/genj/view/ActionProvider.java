
package genj.view;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;

import java.util.List;


public interface ActionProvider {
  
  
  public List createActions(Property[] properties, ViewManager manager);
  
  
  public List createActions(Property property, ViewManager manager);

  
  public List createActions(Entity entity, ViewManager manager);

  
  public List createActions(Gedcom gedcom, ViewManager manager);

} 
