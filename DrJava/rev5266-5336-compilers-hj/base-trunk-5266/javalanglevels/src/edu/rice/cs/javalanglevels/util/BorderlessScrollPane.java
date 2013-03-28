

package edu.rice.cs.javalanglevels.util;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class BorderlessScrollPane extends JScrollPane {
  
  private static final Border DEFAULT = new EtchedBorder();

  
  
  
  public BorderlessScrollPane() {
    super();
    setBorder(DEFAULT);
  }
  public BorderlessScrollPane(Component view) {
    super(view);
    setBorder(DEFAULT);
  }
  public BorderlessScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
    super(view,vsbPolicy,hsbPolicy);
    setBorder(DEFAULT);
  }
  public BorderlessScrollPane(int vsbPolicy, int hsbPolicy) {
    super(vsbPolicy,hsbPolicy);
    setBorder(DEFAULT);
  }
}
