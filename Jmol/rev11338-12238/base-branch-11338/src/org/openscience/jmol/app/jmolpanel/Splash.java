
package org.openscience.jmol.app.jmolpanel;

import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Frame;
import java.awt.Window;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jmol.i18n.GT;
import org.openscience.jmol.app.SplashInterface;

public class Splash extends Window implements SplashInterface {

  private Image splashImage;
  private int imgWidth, imgHeight;
  private static final int BORDERSIZE = 10;
  private static final Color BORDERCOLOR = Color.blue;
  private String status = GT._("Loading...");
  private int textY;
  private int statusTop;
  private static final int STATUSSIZE = 10;
  private static final Color TEXTCOLOR = Color.white;

  public Splash(Frame parent, ImageIcon ii) {

    super(new Frame());
    splashImage = ii.getImage();
    imgWidth = splashImage.getWidth(this);
    imgHeight = splashImage.getHeight(this);
    if (parent == null)
      return;
    showSplashScreen();
    parent.addWindowListener(new WindowListener());
  }

  public void showSplashScreen() {

    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension screenSize = tk.getScreenSize();
    setBackground(BORDERCOLOR);
    int w = imgWidth + (BORDERSIZE * 2);
    int h = imgHeight + (BORDERSIZE * 2) + STATUSSIZE;
    int x = (screenSize.width - w) / 2;
    int y = (screenSize.height - h) / 2;
    setBounds(x, y, w, h);
    statusTop = BORDERSIZE + imgHeight;
    textY = BORDERSIZE + STATUSSIZE + imgHeight + 1;
    show();

  }

  public void paint(Graphics g) {

    g.drawImage(splashImage, BORDERSIZE, BORDERSIZE, imgWidth, imgHeight,
        this);
    g.setColor(BORDERCOLOR);
    g.fillRect(BORDERSIZE, statusTop, imgWidth, textY);
    g.setColor(TEXTCOLOR);
    g.drawString(status, BORDERSIZE, textY);
  }

  public void showStatus(String message) {

    if (message != null) {
      status = message;
      Graphics g = this.getGraphics();
      if (g == null) {
        return;
      }
      g.setColor(BORDERCOLOR);
      g.fillRect(BORDERSIZE, statusTop, imgWidth + BORDERSIZE, textY);
      g.setColor(TEXTCOLOR);
      g.drawString(status, BORDERSIZE, textY);
    }
  }

  class WindowListener extends WindowAdapter {

    public void windowActivated(WindowEvent we) {
      setVisible(false);
      dispose();
    }
  }
}
