
package org.openscience.jmol.app;

import java.awt.Point;

import javax.swing.JFrame;

import org.openscience.jmol.app.jmolpanel.*;

public class Jmol extends JmolPanel {

  public Jmol(JmolApp jmolApp, Splash splash, JFrame frame, Jmol parent, int startupWidth,
      int startupHeight, String commandOptions, Point loc) {
    super(jmolApp, splash, frame, parent, startupWidth, startupHeight, commandOptions, loc);
  }

  public static void main(String[] args) {
    JmolApp jmolApp = new JmolApp(args);
    startJmol(jmolApp);     
  }

  public static Jmol getJmol(JFrame baseframe, 
                             int width, int height, String commandOptions) {
    JmolApp jmolApp = new JmolApp(new String[] {});
    jmolApp.startupHeight = height;
    jmolApp.startupWidth = width;
    jmolApp.commandOptions = commandOptions;
    return getJmol(jmolApp, baseframe);
  }
  
}
