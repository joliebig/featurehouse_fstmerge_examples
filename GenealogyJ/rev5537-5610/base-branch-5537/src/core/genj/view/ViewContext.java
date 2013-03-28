
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
  
  private ViewManager manager;
  private List actions = new ArrayList();
  
  
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
  
  
  public List getActions() {
    return Collections.unmodifiableList(actions);
  }
  
  
   void setManager(ViewManager set) {
    manager = set;
  }
  
  
  public ViewManager getManager() {
    return manager;
  }
  
} 
