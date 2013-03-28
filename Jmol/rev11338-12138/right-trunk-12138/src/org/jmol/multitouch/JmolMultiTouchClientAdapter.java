
package org.jmol.multitouch;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;
import javax.vecmath.Point3f;

import org.jmol.api.JmolMultiTouchAdapter;
import org.jmol.api.JmolMultiTouchClient;
import org.jmol.util.Logger;
import org.jmol.viewer.Viewer;

public abstract class JmolMultiTouchClientAdapter implements JmolMultiTouchAdapter {

  protected JmolMultiTouchClient actionManager;
  protected Component display;
  
  

  public abstract void dispose();
  
  public void setMultiTouchClient(Viewer viewer, JmolMultiTouchClient client,
                              boolean isSimulation) {
    this.display = viewer.getDisplay();
    actionManager = client; 
  }
  
  private static int screenWidth, screenHeight;
  static {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    screenWidth = screen.width;
    screenHeight = screen.height;
    if (Logger.debugging)
      Logger.info("screen resolution: " + screenWidth + " x " + screenHeight);
  }
  
  public void mouseMoved(int x, int y) {
    
    
  }

  protected Point xyTemp = new Point();
  protected Point3f ptTemp = new Point3f();
  protected void fixXY(float x, float y, boolean isAbsolute) {
    xyTemp.setLocation(x * screenWidth, y * screenHeight);
    if (isAbsolute)
      SwingUtilities.convertPointFromScreen(xyTemp, display);
    ptTemp.set(xyTemp.x, xyTemp.y, Float.NaN);
  }
} 
