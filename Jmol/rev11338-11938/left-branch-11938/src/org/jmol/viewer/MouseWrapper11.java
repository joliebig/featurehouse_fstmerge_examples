
package org.jmol.viewer;

import java.awt.Component;

class MouseWrapper11 {
  static MouseManager alloc(Component component, Viewer viewer) {
    return new MouseManager11(component, viewer);
  }
}
