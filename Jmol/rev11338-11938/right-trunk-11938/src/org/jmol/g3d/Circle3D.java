
package org.jmol.g3d;


final class Circle3D {

  Graphics3D g3d;

  Circle3D(Graphics3D g3d) {
    this.g3d = g3d;
  }

  int xCenter, yCenter, zCenter;
  int sizeCorrection;

  void plotCircleCenteredClipped(int xCenter, int yCenter, int zCenter,
                                 int diameter) {
    if (g3d.isClippedXY(diameter, xCenter, yCenter))
      return;
    
    int r = diameter / 2;
    this.sizeCorrection = 1 - (diameter & 1);
    this.xCenter = xCenter;
    this.yCenter = yCenter;
    this.zCenter = zCenter;
    int x = r;
    int y = 0;
    int xChange = 1 - 2 * r;
    int yChange = 1;
    int radiusError = 0;
    while (x >= y) {
      plot8CircleCenteredClipped(x, y);
      ++y;
      radiusError += yChange;
      yChange += 2;
      if (2 * radiusError + xChange > 0) {
        --x;
        radiusError += xChange;
        xChange += 2;
      }
    }
  }

  void plotCircleCenteredUnclipped(int xCenter, int yCenter, int zCenter,
                                   int diameter) {
    int r = diameter / 2;
    this. sizeCorrection = 1 - (diameter & 1);
    this.xCenter = xCenter;
    this.yCenter = yCenter;
    this.zCenter = zCenter;
    int x = r;
    int y = 0;
    int xChange = 1 - 2*r;
    int yChange = 1;
    int radiusError = 0;
    while (x >= y) {
      plot8CircleCenteredUnclipped(x, y);
      ++y;
      radiusError += yChange;
      yChange += 2;
      if (2*radiusError + xChange > 0) {
        --x;
        radiusError += xChange;
        xChange += 2;
      }
    }
  }

  private void plot8CircleCenteredClipped(int dx, int dy) {
    g3d.plotPixelClipped(xCenter+dx-sizeCorrection,
                          yCenter+dy-sizeCorrection, zCenter);
    g3d.plotPixelClipped(xCenter+dx-sizeCorrection, yCenter-dy, zCenter);
    g3d.plotPixelClipped(xCenter-dx, yCenter+dy-sizeCorrection, zCenter);
    g3d.plotPixelClipped(xCenter-dx, yCenter-dy, zCenter);

    g3d.plotPixelClipped(xCenter+dy-sizeCorrection,
                     yCenter+dx-sizeCorrection, zCenter);
    g3d.plotPixelClipped(xCenter+dy-sizeCorrection, yCenter-dx, zCenter);
    g3d.plotPixelClipped(xCenter-dy, yCenter+dx-sizeCorrection, zCenter);
    g3d.plotPixelClipped(xCenter-dy, yCenter-dx, zCenter);
  }

  private void plot8CircleCenteredUnclipped(int dx, int dy) {
    g3d.plotPixelUnclipped(xCenter+dx-sizeCorrection,
                            yCenter+dy-sizeCorrection, zCenter);
    g3d.plotPixelUnclipped(xCenter+dx-sizeCorrection, yCenter-dy, zCenter);
    g3d.plotPixelUnclipped(xCenter-dx, yCenter+dy-sizeCorrection, zCenter);
    g3d.plotPixelUnclipped(xCenter-dx, yCenter-dy, zCenter);

    g3d.plotPixelUnclipped(xCenter+dy-sizeCorrection,
                            yCenter+dx-sizeCorrection, zCenter);
    g3d.plotPixelUnclipped(xCenter+dy-sizeCorrection, yCenter-dx, zCenter);
    g3d.plotPixelUnclipped(xCenter-dy, yCenter+dx-sizeCorrection, zCenter);
    g3d.plotPixelUnclipped(xCenter-dy, yCenter-dx, zCenter);
  }

  private void plot8FilledCircleCenteredClipped(int dx, int dy) {
    g3d.plotPixelsClipped(2*dx+1-sizeCorrection,
                          xCenter-dx, yCenter+dy-sizeCorrection, zCenter);
    g3d.plotPixelsClipped(2*dx+1-sizeCorrection,
                          xCenter-dx, yCenter-dy, zCenter);
    g3d.plotPixelsClipped(2*dy+1-sizeCorrection,
                          xCenter-dy, yCenter+dx-sizeCorrection, zCenter);
    g3d.plotPixelsClipped(2*dy+1-sizeCorrection,
                          xCenter-dy, yCenter-dx, zCenter);
  }

  private void plot8FilledCircleCenteredUnclipped(int dx, int dy) {
    g3d.plotPixelsUnclipped(2*dx+1-sizeCorrection,
                            xCenter-dx, yCenter+dy-sizeCorrection, zCenter);
    g3d.plotPixelsUnclipped(2*dx+1-sizeCorrection,
                            xCenter-dx, yCenter-dy, zCenter);
    g3d.plotPixelsUnclipped(2*dy+1-sizeCorrection,
                            xCenter-dy, yCenter+dx-sizeCorrection, zCenter);
    g3d.plotPixelsUnclipped(2*dy+1-sizeCorrection,
                            xCenter-dy, yCenter-dx, zCenter);
  }

  void plotFilledCircleCenteredClipped(int xCenter, int yCenter, int zCenter,
                                       int diameter) {
    
    int r = diameter / 2;
    this. sizeCorrection = 1 - (diameter & 1);
    this.xCenter = xCenter;
    this.yCenter = yCenter;
    this.zCenter = zCenter;
    int x = r;
    int y = 0;
    int xChange = 1 - 2*r;
    int yChange = 1;
    int radiusError = 0;
    while (x >= y) {
      plot8FilledCircleCenteredClipped(x, y);
      ++y;
      radiusError += yChange;
      yChange += 2;
      if (2*radiusError + xChange > 0) {
        --x;
        radiusError += xChange;
        xChange += 2;
      }
    }
  }

  void plotFilledCircleCenteredUnclipped(int xCenter, int yCenter, int zCenter,
                                       int diameter) {
    
    int r = diameter / 2;
    this.xCenter = xCenter;
    this.yCenter = yCenter;
    this.zCenter = zCenter;
    int x = r;
    int y = 0;
    int xChange = 1 - 2*r;
    int yChange = 1;
    int radiusError = 0;
    while (x >= y) {
      plot8FilledCircleCenteredUnclipped(x, y);
      ++y;
      radiusError += yChange;
      yChange += 2;
      if (2*radiusError + xChange > 0) {
        --x;
        radiusError += xChange;
        xChange += 2;
      }
    }
  }
}

