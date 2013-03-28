
package org.jmol.viewer;


import java.awt.Component;
import java.awt.Event;

class MouseManager10 extends MouseManager {

  MouseManager10(Component display, Viewer viewer) {
    super(display, viewer);
    
  }

  private int applyLeftMouse(int modifiers) {
    
    return ((modifiers & MIDDLE_RIGHT) == 0)  ? (modifiers | LEFT) : modifiers;
  }

  int xWhenPressed, yWhenPressed, modifiersWhenPressed10;

  boolean handleOldJvm10Event(Event e) {
    int x = e.x, y = e.y, modifiers = e.modifiers;
    long time = e.when;
    modifiers = applyLeftMouse(modifiers);
    switch (e.id) {
    case Event.MOUSE_DOWN:
      xWhenPressed = x; yWhenPressed = y; modifiersWhenPressed10 = modifiers;
      mousePressed(time, x, y, modifiers, false);
      break;
    case Event.MOUSE_DRAG:
      mouseDragged(time, x, y, modifiers);
      break;
    case Event.MOUSE_ENTER:
      mouseEntered(time, x, y);
      break;
    case Event.MOUSE_EXIT:
      mouseExited(time, x, y);
      break;
    case Event.MOUSE_MOVE:
      mouseMoved(time, x, y, modifiers);
      break;
    case Event.MOUSE_UP:
      mouseReleased(time, x, y, modifiers);
      
      if (x == xWhenPressed && y == yWhenPressed &&
          modifiers == modifiersWhenPressed10) {
        
        mouseClicked(time, x, y, modifiers, 1);
      }
      break;
    default:
      return false;
    }
    return true;
  }
}
