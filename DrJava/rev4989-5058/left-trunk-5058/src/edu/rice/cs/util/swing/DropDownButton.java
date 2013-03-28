

package edu.rice.cs.util.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class DropDownButton extends JButton {
  
  private JPopupMenu popup = new JPopupMenu();

  
  private boolean popupVisible = false; 
  
  
  public DropDownButton(){ 
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae){ 
        JPopupMenu popup = getPopupMenu(); 
        popup.addPopupMenuListener(new PopupMenuListener() {
          public void popupMenuWillBecomeVisible(PopupMenuEvent e){ 
            popupVisible = true; 
          }
          public void popupMenuWillBecomeInvisible(PopupMenuEvent e){ 
            popupVisible = false; 
            ((JPopupMenu)e.getSource()).removePopupMenuListener(this); 
          }
          public void popupMenuCanceled(PopupMenuEvent e){ 
            popupVisible = false; 
          }
        }); 
        popup.show(DropDownButton.this, 0, getHeight()); 
      }
    }); 
  }

  
  public JPopupMenu getPopupMenu() {
    return popup;
  }
} 
