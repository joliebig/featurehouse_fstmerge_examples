
package genj.util.swing;


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;


public class PopupWidget extends JButton {
  
  
  private List items = new ArrayList();

  
  private boolean isFireOnClick = false;
  
  
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
    this(text, icon, null);
  }

  
  public PopupWidget(String text, Icon icon, List actions) {
    
    super(text, icon);
    
    setModel(new Model());
    
    if (actions!=null) setActions(actions);
    
    setFocusable(false);
    
    setMargin(new Insets(2,2,2,2));
    
  }
  
  
  public void addNotify() {
    
    super.addNotify();
    
    if (getParent() instanceof JToolBar) 
      setMaximumSize(new Dimension(128,128));
  }

  
  
  protected JToolBar getToolBar() {
    if (!(getParent() instanceof JToolBar)) return null;
    return (JToolBar)getParent();
  }
  
  
  public void cancelPopup() {
    if (popup!=null) {
      popup.setVisible(false);
      popup=null;
    }
  }
  
  
  public void showPopup() {
    
    
    cancelPopup();

    
    popup = createPopup();
    if (popup==null)
      return;
  
    
    int x=0, y=0;
    JToolBar bar = getToolBar();
    if (bar==null) {
      x += getWidth();
    } else {
      if (JToolBar.VERTICAL==bar.getOrientation()) {
        x += bar.getLocation().x==0 ? getWidth() : -popup.getPreferredSize().width;
      } else {
        y += bar.getLocation().y==0 ? getHeight() : -popup.getPreferredSize().height;
      }
    }
    
    
    popup.show(PopupWidget.this, x, y);

  }
  
  
  protected JPopupMenu createPopup() {
    
    
    List as = getActions(); 
    if (as.isEmpty()) 
      return null;

    
    JPopupMenu popup = new JPopupMenu();
    
    if (as.size()>16)
      popup.setLayout(new GridLayout(0,(int)Math.ceil(as.size()/16F)));
    MenuHelper mh = new MenuHelper();
    mh.pushMenu(popup);
    mh.createItems(as);

    
    
    return popup;
  }
  
  
  public List getActions() {
    return items;
  }
  
  
  public void setActions(List actions) {
    items = actions;
  }

  
  public void setFocusable(boolean focusable) {
    try {
      super.setFocusable(focusable);
    } catch (Throwable t) {
      
      super.setRequestFocusEnabled(false);
    }
  }
  
  
  public void setFireOnClick(boolean set) {
    isFireOnClick = set;
  }

  
  private class Model extends DefaultButtonModel implements Runnable {
    boolean popupTriggered;
    
    public void setPressed(boolean b) {
      
      super.setPressed(b);
      
      if (b) {
        popupTriggered = true;
        SwingUtilities.invokeLater(this);
      }
    }
    
    public void run() { 
      if (popupTriggered)
        showPopup(); 
    }
    
    protected void fireActionPerformed(ActionEvent e) {
      
      if (isFireOnClick) { 
        
        
        popupTriggered = false;
        cancelPopup();
        
        List as = getActions();
        if (!as.isEmpty())
          ((Action2)as.get(0)).trigger();
      }
    }
  } 
  
} 
