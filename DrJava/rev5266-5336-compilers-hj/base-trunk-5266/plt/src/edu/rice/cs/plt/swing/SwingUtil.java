

package edu.rice.cs.plt.swing;

import java.io.File;
import java.io.Reader;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.Border;

import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.lambda.WrappedException;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.reflect.ReflectException;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.io.FilePredicate;

import static edu.rice.cs.plt.debug.DebugUtil.error;

public class SwingUtil {
  
  
  public static Color gray(float degree) {
    float x = 1.0f - degree;
    return new Color(x, x, x);
  }
  
  
  public static JFrame makeMainApplicationFrame(String title, int width, int height) {
    JFrame result = new JFrame(title);
    result.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    result.getContentPane().setPreferredSize(new Dimension(width, height));
    return result;
  }
  
  
  public static JFrame makeDisposableFrame(String title, int width, int height) {
    JFrame result = new JFrame(title);
    result.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    result.getContentPane().setPreferredSize(new Dimension(width, height));
    return result;
  }
  
  
  public static JFrame makeReusableFrame(String title, int width, int height) {
    JFrame result = new JFrame(title);
    result.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    
    result.getContentPane().setPreferredSize(new Dimension(width, height));
    return result;
  }
  
  public static void onWindowClosed(Window w, final Runnable r) {
    w.addWindowListener(new WindowAdapter() {
      public void windowClosed(WindowEvent e) { r.run(); }
    });
  }
  
  
  public static void displayWindow(Window w) {
    w.pack();
    w.setVisible(true);
  }
  
  
  public static JPanel makeHorizontalBoxPanel() {
    JPanel result = new JPanel();
    result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
    return result;
  }
  
  
  public static JPanel makeHorizontalBoxPanel(int margin) {
    return makeHorizontalBoxPanel(margin, margin, margin, margin);
  }
  
  
  public static JPanel makeHorizontalBoxPanel(int vMargin, int hMargin) {
    return makeHorizontalBoxPanel(vMargin, hMargin, vMargin, hMargin);
  }
  
  
  public static JPanel makeHorizontalBoxPanel(int topMargin, int leftMargin,
                                              int bottomMargin, int rightMargin) {
    JPanel result = makeHorizontalBoxPanel();
    result.setBorder(BorderFactory.createEmptyBorder(topMargin, leftMargin, bottomMargin, rightMargin));
    return result;
  }
  
  
  public static JPanel makeVerticalBoxPanel() {
    JPanel result = new JPanel();
    result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
    return result;
  }
  
  
  public static JPanel makeVerticalBoxPanel(int margin) {
    return makeVerticalBoxPanel(margin, margin, margin, margin);
  }
  
  
  public static JPanel makeVerticalBoxPanel(int vMargin, int hMargin) {
    return makeVerticalBoxPanel(vMargin, hMargin, vMargin, hMargin);
  }
  
  
  public static JPanel makeVerticalBoxPanel(int topMargin, int leftMargin,
                                            int bottomMargin, int rightMargin) {
    JPanel result = makeVerticalBoxPanel();
    result.setBorder(BorderFactory.createEmptyBorder(topMargin, leftMargin, bottomMargin, rightMargin));
    return result;
  }
  
  
  public static JPanel makeBorderPanel() {
    return new JPanel(new BorderLayout());
  }
  
  
  public static JPanel makeBorderPanel(int margin) {
    return makeBorderPanel(margin, margin, margin, margin);
  }
  
  
  public static JPanel makeBorderPanel(int vMargin, int hMargin) {
    return makeBorderPanel(vMargin, hMargin, vMargin, hMargin);
  }
  
  
  public static JPanel makeBorderPanel(int topMargin, int leftMargin,
                                       int bottomMargin, int rightMargin) {
    JPanel result = makeBorderPanel();
    result.setBorder(BorderFactory.createEmptyBorder(topMargin, leftMargin, bottomMargin, rightMargin));
    return result;
  }
  
  
  public static JPanel makeFlowPanel() {
    
    return new JPanel();
  }
  
  
  public static JPanel makeFlowPanel(int margin) {
    return makeFlowPanel(margin, margin, margin, margin);
  }
  
  
  public static JPanel makeFlowPanel(int vMargin, int hMargin) {
    return makeFlowPanel(vMargin, hMargin, vMargin, hMargin);
  }
  
  
  public static JPanel makeFlowPanel(int topMargin, int leftMargin,
                                     int bottomMargin, int rightMargin) {
    JPanel result = makeFlowPanel();
    result.setBorder(BorderFactory.createEmptyBorder(topMargin, leftMargin, bottomMargin, rightMargin));
    return result;
  }
  
  
  public static void add(Container parent, Component... children) {
    for (Component child : children) { parent.add(child); }
  }
  
  
  public static void setBackground(Color c, Component... components) {
    for (Component cm : components) { cm.setBackground(c); }
  }
    
  
  public static void setForeground(Color c, Component... components) {
    for (Component cm : components) { cm.setForeground(c); }
  }
    
  
  public static void setBorder(Border b, JComponent... components) {
    for (JComponent c : components) { c.setBorder(b); }
  }
    
  
  public static void setEmptyBorder(int margin, JComponent... components) {
    setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin), components);
  }
    
  
  public static void setEmptyBorder(int vMargin, int hMargin, JComponent... components) {
    setBorder(BorderFactory.createEmptyBorder(vMargin, hMargin, vMargin, hMargin), components);
  }
    
  
  public static void setEmptyBorder(int top, int left, int bottom, int right, JComponent... components) {
    setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right), components);
  }
    
  
  public static void setLineBorder(JComponent... components) {
    setBorder(BorderFactory.createLineBorder(Color.BLACK), components);
  }
    
  
  public static void setFont(Font f, Component... components) {
    for (Component c : components) { c.setFont(f); }
  }
    
  
  public static void setFont(String name, int size, Component... components) {
    setFont(new Font(name, Font.PLAIN, size), components);
  }
  
  
  public static void setSerifFont(int size, Component... components) {
    setFont(new Font("Serif", Font.PLAIN, size), components);
  }
  
  
  public static void setSansSerifFont(int size, Component... components) {
    setFont(new Font("SansSerif", Font.PLAIN, size), components);
  }
  
  
  public static void setMonospacedFont(int size, Component... components) {
    setFont(new Font("Monospaced", Font.PLAIN, size), components);
  }
  
  
  public static void setDialogFont(int size, Component... components) {
    setFont(new Font("Dialog", Font.PLAIN, size), components);
  }
  
  
  public static void setDialogInputFont(int size, Component... components) {
    setFont(new Font("DialogInput", Font.PLAIN, size), components);
  }
  
  
  public static void setEnabled(boolean b, Component... components) {
    for (Component c : components) { c.setEnabled(b); }
  }
    
  
  public static void setFocusable(boolean b, Component... components) {
    for (Component c : components) { c.setFocusable(b); }
  }
    
  
  public static void setVisible(boolean b, Component... components) {
    for (Component c : components) { c.setVisible(b); }
  }
    
  
  public static void setOpaque(boolean b, JComponent... components) {
    for (JComponent c : components) { c.setOpaque(b); }
  }
    
  
  public static void setPreferredSize(Dimension d, JComponent... components) {
    for (JComponent c : components) { c.setPreferredSize(d); }
  }
    
  
  public static void setPreferredSize(int width, int height, JComponent... components) {
    setPreferredSize(new Dimension(width, height), components);
  }
  
  
  public static void setMaximumSize(Dimension d, JComponent... components) {
    for (JComponent c : components) { c.setMaximumSize(d); }
  }
    
  
  public static void setMaximumSize(int width, int height, JComponent... components) {
    setMaximumSize(new Dimension(width, height), components);
  }
  
  
  public static void setMinimumSize(Dimension d, JComponent... components) {
    for (JComponent c : components) { c.setMinimumSize(d); }
  }
    
  
  public static void setMinimumSize(int width, int height, JComponent... components) {
    setMinimumSize(new Dimension(width, height), components);
  }
  
  
  public static void setLeftAlignment(JComponent... components) {
    setAlignmentX(Component.LEFT_ALIGNMENT, components);
  }
  
  
  public static void setRightAlignment(JComponent... components) {
    setAlignmentX(Component.RIGHT_ALIGNMENT, components);
  }
  
  
  public static void setHorizontalCenterAlignment(JComponent... components) {
    setAlignmentX(Component.CENTER_ALIGNMENT, components);
  }
  
  
  public static void setTopAlignment(JComponent... components) {
    setAlignmentY(Component.TOP_ALIGNMENT, components);
  }
  
  
  public static void setBottomAlignment(JComponent... components) {
    setAlignmentY(Component.BOTTOM_ALIGNMENT, components);
  }
  
  
  public static void setVerticalCenterAlignment(JComponent... components) {
    setAlignmentY(Component.CENTER_ALIGNMENT, components);
  }
  
  
  public static void setAlignmentX(float a, JComponent... components) {
    for (JComponent c : components) { c.setAlignmentX(a); }
  }
    
  
  public static void setAlignmentY(float a, JComponent... components) {
    for (JComponent c : components) { c.setAlignmentY(a); }
  }
    
  
  
  public static void invokeLater(Runnable task) {
    if (EventQueue.isDispatchThread()) { task.run(); }
    else { EventQueue.invokeLater(task); }
  }
  
  
  public static void invokeAndWait(Runnable task) {
    if (EventQueue.isDispatchThread()) { task.run(); }
    else {
      try { EventQueue.invokeAndWait(task); }
      catch (InterruptedException e) { throw new WrappedException(e); }
      catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        
        if (cause instanceof RuntimeException) { throw (RuntimeException) cause; }
        else if (cause instanceof Error) { throw (Error) cause; }
        else { error.log("Unexpected InvocationTargetException caused by invokeAndWait", cause); }
      }
    }
  }
  
  
  public static void clearEventQueue() throws InterruptedException {
    if (SwingUtilities.isEventDispatchThread()) {
      throw new IllegalStateException("Can't clear the event queue from within the event dispatch thread");
    }
    try { SwingUtilities.invokeAndWait(LambdaUtil.NO_OP); }
    catch (InvocationTargetException e) {
      
      error.log(e);
    }
  }
  
  
  public static void attemptClearEventQueue() {
    try { clearEventQueue(); }
    catch (InterruptedException e) {  }
  }
  
  
  public static ActionListener asActionListener(final Runnable r) {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) { r.run(); }
    };
  }
  
  
  public static ActionListener asActionListener(final Runnable1<? super ActionEvent> r) {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) { r.run(e); }
    };
  }
  
  
  public static Runnable1<ActionEvent> asRunnable(final ActionListener l) {
    return new Runnable1<ActionEvent>() {
      public void run(ActionEvent e) { l.actionPerformed(e); }
    };
  }
  
  
  public static javax.swing.filechooser.FileFilter asSwingFileFilter(final java.io.FileFilter filter, 
                                                                     final String description) {
    return new javax.swing.filechooser.FileFilter() {
      public boolean accept(File f) { return filter.accept(f); }
      public String getDescription() { return description; }
    };
  }
  
  
  public static javax.swing.filechooser.FileFilter asSwingFileFilter(Predicate<? super File> p, String description) {
    return asSwingFileFilter((java.io.FileFilter) IOUtil.asFilePredicate(p), description);
  }
  
  
  public static javax.swing.filechooser.FileFilter asSwingFileFilter(FilePredicate p, String description) {
    return asSwingFileFilter((java.io.FileFilter) p, description);
  }
  
  
  public static ActionListener disposeAction(final Window w) {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) { w.dispose(); }
    };
  }
  
  
  public static void showDebug(String msg) { showPopup("Debug Message", msg); }
  
  
  public static void showPopup(final String title, final String msg) {
    invokeAndWait(new Runnable() { public void run() {
      TextAreaMessageDialog.showDialog(null, title, msg); 
    } } );
  }
  
   
  public static void showApplet(URL classPath, String className, int width, int height) throws ReflectException {
    showApplet("Applet Viewer", classPath, className, width, height, Collections.<String, String>emptyMap());
  }
  
   
  public static void showApplet(String title, URL classPath, String className, int width, int height)
      throws ReflectException {
    showApplet(title, classPath, className, width, height, Collections.<String, String>emptyMap());
  }
  
   
  public static void showApplet(String title, URL classPath, String className, int width, int height,
                                Map<String, String> params) throws ReflectException {
    Applet a = (Applet) ReflectUtil.loadObject(new URLClassLoader(new URL[]{ classPath }), className);
    showApplet(title, a, width, height, classPath, params);
  }
  
  
  public static void showApplet(Applet applet, int width, int height) {
    showApplet("Applet Viewer", applet, width, height, null, Collections.<String, String>emptyMap());
  }

  
  public static void showApplet(String title, Applet applet, int width, int height) {
    showApplet(title, applet, width, height, null, Collections.<String, String>emptyMap());
  }
  
  
  public static void showApplet(String title, Applet applet, int width, int height, URL root) {
    showApplet(title, applet, width, height, root);
  }
  
  
  public static void showApplet(String title, Applet applet, int width, int height, URL root,
                                Map<String, String> params) {
    JFrame frame = makeDisposableFrame(title, width, height);
    frame.add(new AppletComponent(applet, width, height, root, params));
    displayWindow(frame);
  }
  
  
  public static String getClipboardSelection(Component c) {
    Clipboard cb = c.getToolkit().getSystemClipboard();
    if (cb==null) return null;
    Transferable t = cb.getContents(null);
    if (t==null) return null;
    String s = null;
    try {
      Reader r = DataFlavor.stringFlavor.getReaderForText(t);
      s = IOUtil.toString(r);
    }
    catch(UnsupportedFlavorException ufe) {  }
    catch(java.io.IOException ioe) {  }
    return s;
  }
  
  
  
  public static Action getAction(JTextComponent component, String actionName) {
    for (Action a : component.getActions()) {
      if (actionName.equals(a.getValue(Action.NAME))) { return a; }
    }
    return null;
  }
  
  
  public static Map<String, Action> getActions(JTextComponent component) {
    Map<String, Action> result = new HashMap<String, Action>();
    for (Action a : component.getActions()) {
      
      result.put((String) a.getValue(Action.NAME), a);
    }
    return result;
  }
  


  
  public static void setPopupLoc(Window popup) { setPopupLoc(popup, popup.getOwner()); }
  
  
  public static void setPopupLoc(Window popup, Component owner) {
    Rectangle frameRect = popup.getBounds();
    
    Point ownerLoc = null;
    Dimension ownerSize = null;
    if(owner!=null) {
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
      if (gcArea>gcBestArea) {
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
  
  
}
