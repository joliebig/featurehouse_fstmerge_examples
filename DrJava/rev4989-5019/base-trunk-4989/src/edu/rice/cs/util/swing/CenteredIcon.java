

package edu.rice.cs.util.swing;

import javax.swing.*;
import java.awt.*;


public class CenteredIcon implements Icon {
  private Icon _base;
  private int _w=0;
  private int _h=0;
  public CenteredIcon(Icon base, int width, int height) {
    _base = base;
    _w = width;
    _h = height;
  }
  public int getIconHeight(){
    return _h;
  }
  public int getIconWidth(){
    return _w;
  }
  public void paintIcon(Component c, Graphics g, int x, int y){
    Shape oldClip = g.getClip();
    g.setClip(x, y, _w, _h);
    int centerX = x + (_w/2);
    int centerY = y + (_h/2);
    int subRadX = _base.getIconWidth() / 2;
    int subRadY = _base.getIconHeight() / 2;
    _base.paintIcon(c, g, centerX - subRadX, centerY - subRadY);
    g.setClip(oldClip);
  }
  public Icon getBaseIcon() { return _base; }
}