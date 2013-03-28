
package org.jmol.viewer;


import java.awt.Component;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

class MouseManager14 extends MouseManager11
  implements MouseWheelListener {

  MouseManager14(Component display, Viewer viewer) {
    super(display, viewer);
    
    if (display == null)
      return;
    display.addMouseWheelListener(this);
  }

  void removeMouseListeners14() {
    viewer.getDisplay().removeMouseWheelListener(this);
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    e.consume();
    mouseWheel(e.getWhen(), e.getWheelRotation(), e.getModifiers());
  }
}
