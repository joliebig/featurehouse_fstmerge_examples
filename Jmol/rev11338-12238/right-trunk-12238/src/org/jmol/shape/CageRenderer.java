
package org.jmol.shape;

import javax.vecmath.Point3f;

import org.jmol.modelset.BoxInfo;
import org.jmol.util.Point3fi;

abstract class CageRenderer extends FontLineShapeRenderer {

  

  protected final Point3f[] screens = new Point3f[8];
  {
    for (int i = 8; --i >= 0; )
      screens[i] = new Point3f();
  }

  protected char[] tickEdges;
  
  abstract protected void setEdges();
  
  protected void initRenderer() {
    setEdges();
  }
  
  protected void render(int mad, Point3f[] vertices, Point3f[] axisPoints,
                        int firstLine) {
    clearBox();
    g3d.setColix(colix);
    FontLineShape fls = (FontLineShape) shape;
    imageFontScaling = viewer.getImageFontScaling();
    font3d = g3d.getFont3DScaled(fls.font3d, imageFontScaling);

    float zSum = 0;
    for (int i = 8; --i >= 0;) {
      viewer.transformPointNoClip(vertices[i], screens[i]);
      zSum += screens[i].z;
    }
    
    int diameter = getDiameter((int) (zSum / 8), mad);
    int axisPt = 2;
    char edge = 0;
    for (int i = firstLine * 2; i < 24; i += 2) {
      int edge0 = BoxInfo.edges[i];
      int edge1 = BoxInfo.edges[i + 1];
      if (axisPoints != null && edge0 == 0)
        viewer.transformPointNoClip(axisPoints[axisPt--], screens[0]);
      boolean drawTicks = (fls.tickInfos != null && (edge = tickEdges[i >> 1]) != 0);
      if (drawTicks) {
        if (atomA == null) {
          atomA = new Point3fi();
          atomB = new Point3fi();
        }
        atomA.set(vertices[edge0]);
        atomB.set(vertices[edge1]);
        float start = 0;
        if (shape instanceof Bbcage)
          switch (edge) {
          case 'x':
            start = atomA.x;
            break;
          case 'y':
            start = atomA.y;
            break;
          case 'z':
            start = atomA.z;
            break;
          }
        tickInfo = fls.tickInfos["xyz".indexOf(edge) + 1];
        if (tickInfo == null)
          tickInfo = fls.tickInfos[0];
        if (tickInfo == null)
          drawTicks = false;
        else
          tickInfo.first = start;
      }
      renderLine(screens[edge0], screens[edge1], diameter, pt0, pt1,
          drawTicks);
    }
  }
}

