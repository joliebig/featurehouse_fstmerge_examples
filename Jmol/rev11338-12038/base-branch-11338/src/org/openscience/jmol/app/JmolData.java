
package org.openscience.jmol.app;

import java.awt.Dimension;
import org.jmol.api.JmolViewer;
import org.jmol.util.TextFormat;

public class JmolData {
  
  

  public JmolApp jmolApp;
  public JmolViewer viewer;
  
  public static JmolData getJmol(int width, int height, String commandOptions) {
    JmolApp jmolApp = new JmolApp();
    jmolApp.haveDisplay = false;
    jmolApp.startupHeight = height;
    jmolApp.startupWidth = width;
    jmolApp.isDataOnly = true;
    String[] args = TextFormat.split(commandOptions, ' '); 
    jmolApp.parseCommandLine(args);
    return new JmolData(jmolApp);
  }

  public JmolData(JmolApp jmolApp) {
    this.jmolApp = jmolApp;
    viewer = JmolViewer.allocateViewer(null, null, 
        null, null, null, jmolApp.commandOptions, null);
    viewer.setScreenDimension(new Dimension(jmolApp.startupWidth, jmolApp.startupHeight));
    jmolApp.startViewer(viewer, null);
  }
  
  public static void main(String[] args) {
    
    JmolApp jmolApp = new JmolApp();
    jmolApp.isDataOnly = true;
    jmolApp.haveConsole = false;
    jmolApp.haveDisplay = false;
    jmolApp.exitUponCompletion = true;
    jmolApp.parseCommandLine(args);    
    new JmolData(jmolApp);
  }
  
}  

