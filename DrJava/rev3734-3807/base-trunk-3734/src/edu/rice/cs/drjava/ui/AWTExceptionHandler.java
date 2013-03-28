

package edu.rice.cs.drjava.ui;
import javax.swing.JFrame;

public class AWTExceptionHandler {
  private static JFrame frame = null;

  public static void setFrame(JFrame f) { frame = f; }

  public void handle(Throwable thrown) {
    if (frame == null) frame = new JFrame();
    new UncaughtExceptionWindow(frame, thrown);
  }
}
