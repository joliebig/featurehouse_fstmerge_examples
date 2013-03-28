

package org.jmol.shape;

import org.jmol.g3d.Font3D;

import java.awt.FontMetrics;

public class Frank extends FontLineShape {

  

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
