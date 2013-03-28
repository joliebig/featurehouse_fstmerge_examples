
package genj.view;

import genj.gedcom.Context;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Window;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;


public abstract class View extends JPanel implements SelectionListener {

  
  public View() {
    super(new BorderLayout()); 
    setMinimumSize(new Dimension());
  }
  
  
  public View(LayoutManager lm) {
    super(lm);
  }
  
  @Override
  public Component add(Component comp) {
    
    if (getLayout() instanceof BorderLayout && getComponentCount()==0)
      super.add(comp, BorderLayout.CENTER);
    else
      super.add(comp);
    
    return comp;
  }
  
  
  public void commit() {
  }

  
  public void closing() {
  }

  
  public static View getView(Component componentInView) {
    do {
      if (componentInView instanceof View)
        return (View)componentInView;
      
      if (componentInView instanceof JPopupMenu)
        componentInView = ((JPopupMenu)componentInView).getInvoker();
      else if (componentInView instanceof Window)
        componentInView = ((Window)componentInView).getOwner();
      else
        componentInView = componentInView.getParent();
    } while (componentInView!=null);
    
    throw new IllegalArgumentException("Cannot find view for component");
  }

  
  public void setContext(Context context, boolean isActionPerformed) {
    
  }

  
  public void populate(ToolBar toolbar) {
    
  }
  
}
