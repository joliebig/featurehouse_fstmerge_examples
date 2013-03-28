
package org.jmol.shape;

import java.awt.FontMetrics;

import org.jmol.api.SymmetryInterface;
import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.StateManager;

import javax.vecmath.Point3f;

public class AxesRenderer extends FontLineShapeRenderer {

  private final static String[] axisLabels = { "+X", "+Y", "+Z", null, null, null, 
                                  "a", "b", "c", 
                                  "X", "Y", "Z", null, null, null,
                                  "X", null, "Z", null, "(Y)", null};

  private final Point3f[] axisScreens = new Point3f[6];
  {
    for (int i = 6; --i >= 0; )
      axisScreens[i] = new Point3f();
  }
  private final Point3f originScreen = new Point3f();
  
  private short[] colixes = new short[3];

  protected void render() {
    Axes axes = (Axes) shape;
    int mad = viewer.getObjectMad(StateManager.OBJ_AXIS1);
    if (mad == 0 || !g3d.checkTranslucent(false))
      return;
    int axesMode = viewer.getAxesMode();
    imageFontScaling = viewer.getImageFontScaling();
    if (viewer.areAxesTainted()) {
      Font3D f = axes.font3d;
      axes.initShape();
      if (f != null)
        axes.font3d = f;
    }
    int nPoints = 6;
    int labelPtr = 0;
    SymmetryInterface[] cellInfos = modelSet.getCellInfos();
    boolean isXY = (axes.axisXY.z != 0);
    int modelIndex = viewer.getCurrentModelIndex();
    if (viewer.isJmolDataFrame(modelIndex))
      return;
    if (axesMode == JmolConstants.AXES_MODE_UNITCELL
        && cellInfos != null) {
      if (modelIndex < 0 || !cellInfos[modelIndex].haveUnitCell())
        return;
      nPoints = 3;
      labelPtr = 6;
    } else if (isXY) {
      nPoints = 3;
      labelPtr = 9;
    } else if (axesMode == JmolConstants.AXES_MODE_BOUNDBOX) {
      nPoints = 6;
      labelPtr = (viewer.getAxesOrientationRasmol() ? 15 : 9);
    }    
    boolean isDataFrame = viewer.isJmolDataFrame();

    int aFactor = (g3d.isAntialiased() ? 2 : 1);
    int slab = g3d.getSlab();
    int widthPixels = (mad < 20 ? mad * aFactor : Integer.MIN_VALUE);
    if (isXY) {
      if (widthPixels < 0)
        widthPixels = (int) (mad > 500 ? 5 : mad / 100f);
      g3d.setSlab(0);
      pt0.set(viewer.transformPoint(axes.axisXY));
      originScreen.set(pt0.x, pt0.y, pt0.z);
      float zoomDimension = viewer.getScreenDim();
      float scaleFactor = zoomDimension / 10f * axes.scale;
      for (int i = 0; i < 3; i++) { 
        viewer.rotatePoint(axes.getAxisPoint(i, false), axisScreens[i]);
        axisScreens[i].z *= -1;
        axisScreens[i].scaleAdd(scaleFactor, axisScreens[i], originScreen);
      }
      
    } else {
      viewer.transformPointNoClip(axes.getOriginPoint(isDataFrame), originScreen);
      if (widthPixels == Integer.MIN_VALUE)
        widthPixels = viewer.scaleToScreen((int)originScreen.z, mad);
      for (int i = nPoints; --i >= 0;)
        viewer.transformPointNoClip(axes.getAxisPoint(i, isDataFrame), axisScreens[i]);
    }
    

    float xCenter = originScreen.x;
    float yCenter = originScreen.y;
    colixes[0] = viewer.getObjectColix(StateManager.OBJ_AXIS1);
    colixes[1] = viewer.getObjectColix(StateManager.OBJ_AXIS2);
    colixes[2] = viewer.getObjectColix(StateManager.OBJ_AXIS3);
    Font3D font = g3d.getFont3DScaled(axes.font3d, imageFontScaling);
    for (int i = nPoints; --i >= 0;) {
      colix = colixes[i % 3];
      g3d.setColix(colix);
      String label = axisLabels[i + labelPtr];
      if (label != null)
        renderLabel(label, font, axisScreens[i].x,
            axisScreens[i].y, axisScreens[i].z, xCenter, yCenter);
      renderLine(originScreen, axisScreens[i], widthPixels, Graphics3D.ENDCAPS_FLAT, pt0, pt1);
    }
    if (nPoints == 3 && !isXY) { 
      colix = viewer.getColixBackgroundContrast();
      g3d.setColix(colix);
      renderLabel("0", font, originScreen.x, originScreen.y, originScreen.z, xCenter, yCenter);
    }
    if (isXY)
      g3d.setSlab(slab);
  }
  
  private void renderLabel(String str, Font3D font3d, float x, float y, float z, float xCenter, float yCenter) {
    FontMetrics fontMetrics = font3d.fontMetrics;
    int strAscent = fontMetrics.getAscent();
    int strWidth = fontMetrics.stringWidth(str);
    float dx = x - xCenter;
    float dy = y - yCenter;
    if ((dx != 0 || dy != 0)) {
      float dist = (float) Math.sqrt(dx * dx + dy * dy);
      dx = (strWidth * 0.75f * dx / dist);
      dy = (strAscent * 0.75f * dy / dist);
      x += dx;
      y += dy;
    }
    float xStrBaseline = x - strWidth / 2;
    float yStrBaseline = y + strAscent / 2;
    g3d.drawString(str, font3d, (int) xStrBaseline, (int) yStrBaseline, (int) z, (int) z);
  }
}
