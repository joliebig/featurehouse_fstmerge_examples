
package genj.view;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.util.swing.Action2;

import java.util.List;


public interface ActionProvider {

  
  public final static int
    HIGH = 90,
    NORMAL = 50,
    LOW = 10;

  
  public int getPriority();

  
  public List<Action2> createActions(Property[] properties);
  
  
  public List<Action2> createActions(Property property);

  
  public List<Action2> createActions(Entity entity);

  
  public List<Action2> createActions(Gedcom gedcom);

} 
