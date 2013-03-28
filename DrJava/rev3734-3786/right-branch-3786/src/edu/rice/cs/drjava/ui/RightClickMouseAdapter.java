

package edu.rice.cs.drjava.ui;

import java.awt.event.*;


public abstract class RightClickMouseAdapter extends MouseAdapter {
  
  protected abstract void _popupAction(MouseEvent e);

  
  public void mousePressed(MouseEvent e) {
    if (e.isPopupTrigger()) {
      _popupAction(e);
    }
  }

  
  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      _popupAction(e);
    }
  }
}
