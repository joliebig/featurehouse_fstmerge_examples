

package edu.rice.cs.util.swing;

import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;


public abstract class SwingFrame extends JFrame {
  
  
  private volatile boolean _initComplete;
  
  
  public SwingFrame() { super(); }
  public SwingFrame(GraphicsConfiguration gc) { super(gc); }
  public SwingFrame(String title) { super(title); }
  public SwingFrame(String title, GraphicsConfiguration gc) { super(title, gc); }
  
  public boolean duringInit() { return ! _initComplete; }
  public void initDone() { _initComplete = true; }
}
    
  
  
  
  