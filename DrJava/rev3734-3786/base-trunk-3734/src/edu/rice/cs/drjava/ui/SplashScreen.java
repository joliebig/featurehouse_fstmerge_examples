

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.Dimension;


public class SplashScreen extends JWindow {
  public static final String SPLASH_ICON = "splash.png";
  private ImageIcon _icon;

  
  public SplashScreen() {
    _icon = MainFrame.getIcon(SPLASH_ICON);
    getContentPane().add(new JLabel(_icon, SwingConstants.CENTER));
    setSize(_icon.getIconWidth(), _icon.getIconHeight());
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = getSize();
    setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
  }
}
