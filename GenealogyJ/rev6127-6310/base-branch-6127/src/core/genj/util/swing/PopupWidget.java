
package genj.util.swing;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;


public class PopupWidget extends JButton {
  
  
  private List<JComponent> items = new ArrayList<JComponent>();

  
  private long fireOnClickWithin = 0;
  
  
  private JPopupMenu popup;
    
  
  public PopupWidget() {
    this((Icon)null);
  }

  
  public PopupWidget(Icon icon) {
    this(null, icon);
  }

  
  public PopupWidget(String text) {
    this(text, null);
  }

  
  public PopupWidget(String text, Icon icon) {
    
    super(text, icon);
    
    setModel(new Model());
    
    setFocusable(false);
    
    setMargin(new Insets(2,2,2,2));
    
    popup = new JPopupMenu();
    
  }
  











  
  
  public void cancelPopup() {
    popup.setVisible(false);
  }
  
  protected void setPopupSize(Dimension d) {
    
    Component c = getPopup();
    Dimension min = c.getMinimumSize();
    d.width = Math.max(min.width, d.width);
    d.height = Math.max(min.height, d.height);
    Dimension old = c.getSize();
    while (c!=null && c.getSize().equals(old)) {
      c.setSize(d);
      c = c.getParent();
    }
    getPopup().revalidate();
  }
  
  
  public void showPopup() {
    
    
    cancelPopup();

    
    popup = getPopup();
    if (popup==null)
      return;
  
    
    int x=0, y=0;
    
    if (!(getParent() instanceof JToolBar)) {
      x += getWidth();
    } else {
      JToolBar bar = (JToolBar)getParent();
      if (JToolBar.VERTICAL==bar.getOrientation()) {
        x += bar.getLocation().x==0 ? getWidth() : -popup.getPreferredSize().width;
      } else {
        y += bar.getLocation().y==0 ? getHeight() : -popup.getPreferredSize().height;
      }
    }
    
    
    popup.show(PopupWidget.this, x, y);

  }
  
  
  protected JPopupMenu getPopup() {
    return popup;
  }
  
  
  public void addItem(Component c) {
    popup.add(c);
  }

  public void addItems(List<? extends Action> actions) {
    for (Action action : actions)
      addItem(action);
  }

  public void addItem(Action action) {
    popup.add(new JMenuItem(action));
  }
  
  public void removeItems() {
    popup.removeAll();
  }

  
  public void setFireOnClickWithin(long ms) {
    fireOnClickWithin = ms;
  }

  
  private class Model extends DefaultButtonModel implements Runnable {
    private long triggerTime = 0;
    
    public void setPressed(boolean b) {
      
      super.setPressed(b);
      
      if (b) 
        SwingUtilities.invokeLater(this);
    }
    
    public void run() { 
      showPopup(); 
      triggerTime = System.currentTimeMillis();
    }
    
    protected void fireActionPerformed(ActionEvent e) {
      
      if (fireOnClickWithin>0 && System.currentTimeMillis()-triggerTime<fireOnClickWithin) { 
        for (int i=0;i<popup.getComponentCount();i++) {
          Component c = popup.getComponent(i);
          if (c instanceof AbstractButton) {
            cancelPopup();
            ((AbstractButton)c).doClick();
            break;
          }
        }
      }
    }
  } 
  
} 
