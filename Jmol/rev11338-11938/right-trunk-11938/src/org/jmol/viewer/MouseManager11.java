
package org.jmol.viewer;


import java.awt.Component;
import java.awt.Event;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.jmol.viewer.binding.Binding;

class MouseManager11 extends MouseManager
  implements MouseListener, MouseMotionListener {

  MouseManager11(Component display, Viewer viewer, ActionManager actionManager) {
    super(display, viewer, actionManager);
    display.addMouseListener(this);
    display.addMouseMotionListener(this);
  }

  void removeMouseListeners11() {
    viewer.getDisplay().removeMouseListener(this);
    viewer.getDisplay().removeMouseMotionListener(this);
  }

  boolean handleOldJvm10Event(Event e) {
    return false;
  }

  public void mouseClicked(MouseEvent e) {
    mouseClicked(e.getWhen(), e.getX(), e.getY(), e.getModifiers(),
                 e.getClickCount());
  }

  public void mouseEntered(MouseEvent e) {
    mouseEntered(e.getWhen(), e.getX(), e.getY());
  }
  
  public void mouseExited(MouseEvent e) {
    mouseExited(e.getWhen(), e.getX(), e.getY());
  }
  
  public void mousePressed(MouseEvent e) {
    mousePressed(e.getWhen(), e.getX(), e.getY(), e.getModifiers(),
                 e.isPopupTrigger());
  }
  
  public void mouseReleased(MouseEvent e) {
    mouseReleased(e.getWhen(), e.getX(), e.getY(), e.getModifiers());
  }

  public void mouseDragged(MouseEvent e) {
    int modifiers = e.getModifiers();
    
    if ((modifiers & Binding.LEFT_MIDDLE_RIGHT) == 0)
      modifiers |= Binding.LEFT;
          
    mouseDragged(e.getWhen(), e.getX(), e.getY(), modifiers);
  }

  public void mouseMoved(MouseEvent e) {
    mouseMoved(e.getWhen(), e.getX(), e.getY(), e.getModifiers());
  }
}
