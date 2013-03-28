
package genj.view;

import genj.gedcom.Context;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Window;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;

public abstract class View extends JPanel implements SelectionListener {

  private List<SelectionListener> listeners = new CopyOnWriteArrayList<SelectionListener>();

  
  public View() {
  }
  
  
  public View(LayoutManager lm) {
    super(lm);
  }
  
  
  public void commit() {
  }

  
  public boolean closing() {
    return true;
  }
  
  public void addSelectionListener(SelectionListener listener) {
    listeners.add(listener);
  }
  
  
  public void removeSelectionListener(SelectionListener listener) {
    listeners.remove(listener);
  }

  
  public void fireSelection(Context context, boolean isActionPerformed) {
    if (context==null)
      throw new IllegalArgumentException("context cannot be null");
    for (SelectionListener listener : listeners) {
      listener.select(context, isActionPerformed);
    }
  }
  
  
  
  
  public static View getView(Component componentInView) {
    do {
      if (componentInView instanceof View)
        return (View)componentInView;
      if (componentInView instanceof Window)
        componentInView = ((Window)componentInView).getOwner();
      else
        componentInView = componentInView.getParent();
    } while (componentInView!=null);
    
    throw new IllegalArgumentException("Cannot find view for component");
  }
  
  public static void fireSelection(Component componentInView, Context context, boolean isActionPerformed) {
    View.getView(componentInView).fireSelection(context, isActionPerformed);
  }

  
  public View get(ViewFactory factory) {
    
    return null;
  }
  
  
  public void select(Context context, boolean isActionPerformed) {
    
  }

  
  public void populate(ToolBar toolbar) {
    
  }
  
}
