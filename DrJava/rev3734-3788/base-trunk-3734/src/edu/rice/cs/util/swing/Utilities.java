

package edu.rice.cs.util.swing;

import java.awt.EventQueue;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.StringOps;

import edu.rice.cs.drjava.ui.AWTExceptionHandler;
  
public class Utilities {
  
  
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
  
  public static void clearEventQueue() {
    Utilities.invokeAndWait(new Runnable() { public void run() { } });
  }
  
  
  public static void show(final String msg) { 
     Utilities.invokeAndWait(new Runnable() { public void run() { JOptionPane.showMessageDialog(null, msg); } } );
  }
  
  
  public static void showTrace(final Throwable t) { 
    Utilities.invokeAndWait(new Runnable() { public void run() { new AWTExceptionHandler().handle(t); } } );
  } 
  
  
  public static void showDebug(String msg) { showMessageBox(msg, "Debug Message"); }
  
  
  public static void showMessageBox(final String msg, final String title) {
    
    Utilities.invokeAndWait(new Runnable() { public void run() {
      Utilities.TextAreaMessageDialog.showDialog(null, title, msg); 
    } } );
  }

  public static void showStackTrace(final Throwable t) {
    Utilities.invokeAndWait(new Runnable() { public void run() { 
      JOptionPane.showMessageDialog(null, StringOps.getStackTrace(t));
    } } );
  }
  
  
  
  public static class TextAreaMessageDialog extends JDialog {
    
    
    public static boolean TEST_MODE = false;

    
    public static void showDialog(Component comp, String title, String message) {
      if (TEST_MODE) System.out.println(title + ": " + message);
      else {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        TextAreaMessageDialog dialog = new TextAreaMessageDialog(frame, comp, title, message);
        dialog.setVisible(true);
      }
    }

    
    private TextAreaMessageDialog(Frame frame, Component comp, String title, String message) {
      super(frame, title, true);
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      
      JButton okButton = new JButton("OK");
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          TextAreaMessageDialog.this.dispose();
        }
      });
      getRootPane().setDefaultButton(okButton);

      JTextArea textArea = new JTextArea(message);
      textArea.setEditable(false);
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(false);
      textArea.setBackground(SystemColor.window);

      Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
      textArea.setBorder(emptyBorder);

      Container contentPane = getContentPane();
      contentPane.add(textArea, BorderLayout.CENTER);
      contentPane.add(okButton, BorderLayout.SOUTH);

      Dimension parentDim = (comp!=null)?(comp.getSize()):getToolkit().getScreenSize();
      int xs = (int)parentDim.getWidth()/4;
      int ys = (int)parentDim.getHeight()/5;
      setSize(Math.max(xs,350), Math.max(ys, 250));
      setLocationRelativeTo(comp);
    }
  }
}
