
package genj.view;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.util.swing.Action2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

  
public class ViewContext extends Context {
  
  private List<Action2> actions = new ArrayList<Action2>();
  
  
  public ViewContext(Context context) {
    super(context);
  }
  
  
  public ViewContext(Gedcom ged) {
    super(ged);
  }
  
  
  public ViewContext(Property prop) {
    super(prop);
  }
  
  
  public ViewContext(Entity entity) {
    super(entity);
  }
  
  
  public ViewContext addAction(Action2 action) {
    actions.add(action);
    return this;
  }
  
  
  public ViewContext addActions(Action2.Group group) {
    actions.add(group);
    return this;
  }
  
  
  public List<Action2> getActions() {
    return Collections.unmodifiableList(actions);
  }
  
} 
