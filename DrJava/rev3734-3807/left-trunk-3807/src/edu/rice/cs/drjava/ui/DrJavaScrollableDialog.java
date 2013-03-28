

package edu.rice.cs.drjava.ui;

import javax.swing.*;

import edu.rice.cs.util.swing.ScrollableDialog;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;


public class DrJavaScrollableDialog extends ScrollableDialog {

  
  public DrJavaScrollableDialog(JFrame parent, String title, String header, String text) {
    this(parent, title, header, text, DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  
  public DrJavaScrollableDialog(JFrame parent, String title, String header,
                                String text, int width, int height)
  {
    super(parent, title, header, text, width, height);
    setTextFont(DrJava.getConfig().getSetting(OptionConstants.FONT_MAIN));
  }
}