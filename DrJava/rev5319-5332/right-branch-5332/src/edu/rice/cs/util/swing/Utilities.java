

package edu.rice.cs.util.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.StringOps;

public class Utilities {
  
  
  public static volatile boolean TEST_MODE = false;
  
  public static final String JGOODIES_PACKAGE = "com.jgoodies.looks";
  
  
  public static void invokeLater(Runnable task) {
    if (EventQueue.isDispatchThread()) {
      task.run(); 
      return;
    }
    EventQueue.invokeLater(task);
  }
  
  public static void invokeAndWait(Runnable task) {
    if (EventQueue.isDispatchThread()) {
      task.run(); 
      return;
    }
    try { EventQueue.invokeAndWait(task); }
    catch(Exception e) { throw new UnexpectedException(e); }
  }
  
  public static void main(String[] args) { clearEventQueue(); }

  
  public static void clearEventQueue() { clearEventQueue(true); }
  
  
  public static void clearEventQueue(boolean newEvents) {
    assert ! EventQueue.isDispatchThread();
    final EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
    do {
      
      try { EventQueue.invokeAndWait(LambdaUtil.NO_OP); }
      catch (Exception e) { throw new UnexpectedException(e); }
    } while (newEvents && (null != q.peekEvent()));
  }
  
  
  public static void show(final String msg) { 
    Utilities.invokeAndWait(new Runnable() { public void run() {
      new edu.rice.cs.drjava.ui.DrJavaScrollableDialog(null,
                                                       "Debug Message",
                                                       "Debug Message from Utilities.show():",
                                                       msg,
                                                       false).show(); } } );
  }
  
  
  public static void showDebug(String msg) { showMessageBox(msg, "Debug Message"); }
  
  
  public static void showMessageBox(final String msg, final String title) {
    if (TEST_MODE) System.out.println(title + ": " + msg); else {
      
      Utilities.invokeAndWait(new Runnable() { public void run() {
        new edu.rice.cs.drjava.ui.DrJavaScrollableDialog(null,
                                                         title,
                                                         "Message:",
                                                         msg,
                                                         false).show();
      } } );
    }
  }
  
  public static void showStackTrace(final Throwable t) {
    Utilities.invokeAndWait(new Runnable() { public void run() { 
      new edu.rice.cs.drjava.ui.DrJavaScrollableDialog(null,
                                                       "Stack Trace",
                                                       "Stack Trace:",
                                                       StringOps.getStackTrace(t),
                                                       false).show();
    } } );
  }
  
  
  public static String getClipboardSelection(Component c) {
    Clipboard cb = c.getToolkit().getSystemClipboard();
    if (cb == null) return null;
    Transferable t = cb.getContents(null);
    if (t == null) return null;
    String s = null;
    try {
      java.io.Reader r = DataFlavor.stringFlavor.getReaderForText(t);
      int ch;
      final StringBuilder sb = new StringBuilder();
      while ((ch=r.read()) !=-1 ) { sb.append((char)ch); }
      s = sb.toString();
    }
    catch(UnsupportedFlavorException ufe) {  }
    catch(java.io.IOException ioe) {  }
    return s;
  }
  
  
  public static AbstractAction createDelegateAction(String newName, final Action delegate) {
    return new AbstractAction(newName) {
      public void actionPerformed(ActionEvent ae) { delegate.actionPerformed(ae); }
    };
  }
  
  
  public static boolean isPlasticLaf() {
    LookAndFeel laf = UIManager.getLookAndFeel();
    return laf != null && laf.getClass().getName().startsWith(JGOODIES_PACKAGE);
  }
  
  
  public static boolean isPlasticLaf(String name) {
    return name != null && name.startsWith(JGOODIES_PACKAGE);
  }
  
  
  public static void setPopupLoc(Window popup, Component owner) {
    Rectangle frameRect = popup.getBounds();
    
    Point ownerLoc = null;
    Dimension ownerSize = null;
    if (owner != null && owner.isVisible()) {
      ownerLoc = owner.getLocation();
      ownerSize = owner.getSize();
    }
    else {
      
      
      GraphicsDevice[] dev = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
      Rectangle rec = dev[0].getDefaultConfiguration().getBounds();
      ownerLoc = rec.getLocation();
      ownerSize = rec.getSize();
    }
    
    
    Point loc = new Point(ownerLoc.x + (ownerSize.width - frameRect.width) / 2,
                          ownerLoc.y + (ownerSize.height - frameRect.height) / 2);
    frameRect.setLocation(loc);
    
    
    GraphicsConfiguration gcBest = null;
    int gcBestArea = -1;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();
    for (GraphicsDevice gd: gs) {
      GraphicsConfiguration gc = gd.getDefaultConfiguration();
      Rectangle isect = frameRect.intersection(gc.getBounds());
      int gcArea = isect.width*isect.height;
      if (gcArea > gcBestArea) {
        gcBest = gc;
        gcBestArea = gcArea;
      }
    }
    
    
    Rectangle screenRect = gcBest.getBounds();
    Dimension screenSize = screenRect.getSize();
    Dimension frameSize = popup.getSize();
    
    if (frameSize.height > screenSize.height) frameSize.height = screenSize.height;
    if (frameSize.width > screenSize.width) frameSize.width = screenSize.width;
    
    frameRect.setSize(frameSize);
    
    
    loc = new Point(ownerLoc.x + (ownerSize.width - frameRect.width) / 2,
                    ownerLoc.y + (ownerSize.height - frameRect.height) / 2);
    frameRect.setLocation(loc);
    
    
    if (frameRect.x < screenRect.x) frameRect.x = screenRect.x;
    if (frameRect.x + frameRect.width > screenRect.x + screenRect.width)
      frameRect.x = screenRect.x + screenRect.width - frameRect.width;
    
    if (frameRect.y < screenRect.y) frameRect.y = screenRect.y;
    if (frameRect.y + frameRect.height > screenRect.y + screenRect.height)
      frameRect.y = screenRect.y + screenRect.height - frameRect.height;
    
    popup.setSize(frameRect.getSize());
    popup.setLocation(frameRect.getLocation());
  }
  
  
  public static PropertyChangeListener enableDisableWith(Action observable, final Action observer) {
    PropertyChangeListener pcl = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("enabled")) { observer.setEnabled((Boolean)e.getNewValue()); }
      }
    };
    observable.addPropertyChangeListener(pcl);
    return pcl;
  }
  
  
  public static int getComponentIndex(Component component) {
    if (component != null && component.getParent() != null) {
      Container c = component.getParent();
      for (int i = 0; i < c.getComponentCount(); i++) {
        if (c.getComponent(i) == component)
          return i;
      }
    }
    
    return -1;
  }
}
