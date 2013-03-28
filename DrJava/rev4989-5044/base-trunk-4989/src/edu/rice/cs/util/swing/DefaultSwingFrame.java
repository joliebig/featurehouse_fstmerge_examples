

package edu.rice.cs.util.swing;

import java.awt.GraphicsConfiguration;

import edu.rice.cs.util.swing.SwingFrame;


public class DefaultSwingFrame extends SwingFrame {
  
  
  public DefaultSwingFrame() { 
    super(); 
    initDone();
  }
  public DefaultSwingFrame(GraphicsConfiguration gc) { 
    super(gc);
    initDone();
  }
  public DefaultSwingFrame(String title) { 
    super(title);
    initDone();
  }
  public DefaultSwingFrame(String title, GraphicsConfiguration gc) { 
    super(title, gc); 
    initDone();
  }
}