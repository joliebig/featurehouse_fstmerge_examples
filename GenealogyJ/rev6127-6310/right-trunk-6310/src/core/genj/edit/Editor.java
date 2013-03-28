
package genj.edit;

import genj.gedcom.Context;
import genj.util.ChangeSupport;
import genj.view.ViewContext;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;


 abstract class Editor extends JPanel {
  
  protected ChangeSupport changes = new ChangeSupport(this);
  protected List<Action> actions = new ArrayList<Action>();

  
  public abstract ViewContext getContext();
  
  
  public abstract void setContext(Context context);
  
  
  public abstract void commit();
  
  
  public List<Action> getActions() {
    return actions;
  }

  public void addChangeListener(ChangeListener listener) {
    changes.addChangeListener(listener);
  }
  
  public void removeChangeListener(ChangeListener listener) {
    changes.removeChangeListener(listener);
  }

} 
