
package org.jmol.shape;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;

import org.jmol.g3d.Graphics3D;

abstract class FontLineShapeRenderer extends ShapeRenderer {

  float imageFontScaling;
  
  Point3i pt0 = new Point3i();
  Point3i pt1 = new Point3i();
  
  protected void render(int mad, Point3f[] vertices, Point3f[] screens,
                        Point3f[] axisPoints, int firstLine) {
    
    g3d.setColix(colix);
    float zSum = 0;
    for (int i = 8; --i >= 0;) {
      viewer.transformPointNoClip(vertices[i], screens[i]);
      zSum += screens[i].z;
    }
    int widthPixels = mad;
    if (mad >= 20)
      widthPixels = viewer.scaleToScreen((int)(zSum / 8), mad);
    int axisPt = 2;
    for (int i = firstLine * 2; i < 24; i += 2) {
      int edge0 = Bbcage.edges[i];
      int edge1 = Bbcage.edges[i + 1];
      if (axisPoints != null && edge0 == 0)
        viewer.transformPointNoClip(axisPoints[axisPt--], screens[0]);
      renderLine(screens[edge0], screens[edge1], widthPixels, Graphics3D.ENDCAPS_SPHERICAL, pt0, pt1);
    }
  }
}

