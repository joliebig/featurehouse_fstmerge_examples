
package org.jmol.applet;

import java.awt.*;
import javax.swing.UIManager;

import org.jmol.api.*;

 public class Jvm12 {

  protected JmolViewer viewer;
  public Component awtComponent;
  
  protected String appletContext;

  Jvm12(Component awtComponent, JmolViewer viewer, String appletContext) {
    this.awtComponent = awtComponent;
    this.viewer = viewer;
    this.appletContext = appletContext;
    try {
      UIManager
          .setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception exc) {
      System.err.println("Error loading L&F: " + exc);
    }
  }

  private final Rectangle rectClip = new Rectangle();
  private final Dimension dimSize = new Dimension();

  Rectangle getClipBounds(Graphics g) {
    return g.getClipBounds(rectClip);
  }

  public Dimension getSize() {
    return awtComponent.getSize(dimSize);
  }

}
