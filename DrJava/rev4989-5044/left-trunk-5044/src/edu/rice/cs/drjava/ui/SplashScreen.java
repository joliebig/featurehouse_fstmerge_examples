

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class SplashScreen extends JWindow {
  private static final String SPLASH_ICON = "splash.png";
  private static final int PAUSE_TIME = 4000; 
  
  private ImageIcon _icon;

  
  public SplashScreen() {
    _icon = MainFrame.getIcon(SPLASH_ICON);
    getContentPane().add(new JLabel(_icon, SwingConstants.CENTER));
    setSize(_icon.getIconWidth(), _icon.getIconHeight());
    
    
    GraphicsDevice[] dev = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    Rectangle rec = dev[0].getDefaultConfiguration().getBounds();
    Point ownerLoc = rec.getLocation();
    Dimension ownerSize = rec.getSize();
    Dimension frameSize = getSize();
    setLocation(ownerLoc.x + (ownerSize.width - frameSize.width) / 2,
                ownerLoc.y + (ownerSize.height - frameSize.height) / 2);
  }
  
  
  public void flash() {
    setVisible(true);
    repaint();
    Timer cleanup = new Timer(PAUSE_TIME, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    cleanup.setRepeats(false);
    cleanup.start();
  }
  
}
