

package org.jmol.shape;

import org.jmol.g3d.Font3D;
import org.jmol.i18n.GT;

import java.awt.FontMetrics;
import java.util.BitSet;

public class Frank extends FontShape {

  

  final static String defaultFontName = "SansSerif";
  final static String defaultFontStyle = "Bold";
  final static int defaultFontSize = 16;
  final static int frankMargin = 4;

  String frankString = "Jmol";
  Font3D currentMetricsFont3d;
  Font3D baseFont3d;
  int frankWidth;
  int frankAscent;
  int frankDescent;
  int x, y, dx, dy;

  public void initShape() {
    super.initShape();
    myType = "frank";
    baseFont3d = font3d = g3d.getFont3D(defaultFontName, defaultFontStyle, defaultFontSize);
    calcMetrics();
  }

  public boolean wasClicked(int x, int y) {
    int width = viewer.getScreenWidth();
    int height = viewer.getScreenHeight();
    return (width > 0 && height > 0 
        && x > width - frankWidth - frankMargin
        && y > height - frankAscent - frankMargin);
  }

  public boolean checkObjectHovered(int x, int y, BitSet bsVisible) {
    if (!wasClicked(x, y) || !viewer.menuEnabled())
      return false;
    if (g3d.isDisplayAntialiased()) {
      
      x <<= 1;
      y <<= 1;
    }      
    viewer.hoverOn(x, y, GT._("Click for menu..."));
    return true;
  }
  
  void calcMetrics() {
    if (viewer.isSignedApplet())
      frankString = "Jmol_S";
    if (font3d == currentMetricsFont3d) 
      return;
    currentMetricsFont3d = font3d;
    FontMetrics fm = font3d.fontMetrics;
    frankWidth = fm.stringWidth(frankString);
    frankDescent = fm.getDescent();
    frankAscent = fm.getAscent();
  }

  void getFont(float imageFontScaling) {
    font3d = g3d.getFont3DScaled(baseFont3d, imageFontScaling);
    calcMetrics();
  }
}
