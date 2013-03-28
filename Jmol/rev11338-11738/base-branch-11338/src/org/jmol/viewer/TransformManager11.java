
package org.jmol.viewer;

import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.viewer.StateManager.Orientation;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

import org.jmol.g3d.Graphics3D;

class TransformManager11 extends TransformManager {

  private float navigationSlabOffset;
  private float zoomFactor = Float.MAX_VALUE;

  TransformManager11() {
    super();
    setNavFps(10);
  }
  
  TransformManager11(Viewer viewer) {
    super(viewer);
    setNavFps(10);
  }

  protected void setNavFps(int navFps) {
    this.navFps = navFps;
  }

  TransformManager11(Viewer viewer, int width, int height) {
    super(viewer, width, height);
    setNavFps(10);
  }

  protected void calcCameraFactors() {
    
    
    

    if (Float.isNaN(cameraDepth)) {
      cameraDepth = cameraDepthSetting;
      zoomFactor = Float.MAX_VALUE;
    }

    
    cameraDistance = cameraDepth * screenPixelCount; 

    
    
    referencePlaneOffset = cameraDistance + screenPixelCount / 2f; 

    
    
    scalePixelsPerAngstrom = (scale3D && !perspectiveDepth && !isNavigationMode ? 
        72 / scale3DAngstromsPerInch : screenPixelCount / visualRange);  

    
    modelRadiusPixels = modelRadius * scalePixelsPerAngstrom; 

    
    float offset100 = (2 * modelRadius) / visualRange * referencePlaneOffset; 

    
      
        

    if (!isNavigationMode) {
      
      
      zoomFactor = Float.MAX_VALUE;
      
      modelCenterOffset = referencePlaneOffset;
      
      if (!scale3D || perspectiveDepth)
        scalePixelsPerAngstrom *= (modelCenterOffset / offset100) * zoomPercent / 100; 

      
      
      
      modelRadiusPixels = modelRadius * scalePixelsPerAngstrom; 
      
      return;
    }
    
    if (zoomFactor == Float.MAX_VALUE) {
      
      if (zoomPercent > MAXIMUM_ZOOM_PERSPECTIVE_DEPTH)
        zoomPercent = MAXIMUM_ZOOM_PERSPECTIVE_DEPTH;
      
      modelCenterOffset = offset100 * 100 / zoomPercent;
    } else if (prevZoomSetting != zoomPercentSetting) {
      if (zoomRatio == 0) 
        modelCenterOffset = offset100 * 100 / zoomPercentSetting;
      else
        
        modelCenterOffset += (1 - zoomRatio) * referencePlaneOffset;
      navMode = NAV_MODE_ZOOMED;
    }
    prevZoomSetting = zoomPercentSetting;
    zoomFactor = modelCenterOffset / referencePlaneOffset;
    
    zoomPercent = (zoomFactor == 0 ? MAXIMUM_ZOOM_PERSPECTIVE_DEPTH
        : offset100 / modelCenterOffset * 100);
  }

  protected float getPerspectiveFactor(float z) {
    
    return (z <= 0 ? referencePlaneOffset : referencePlaneOffset / z);
  }

  protected void adjustTemporaryScreenPoint() {

    

    float z = point3fScreenTemp.z;

    
    
    
    

    if (Float.isNaN(z)) {
      if (!haveNotifiedNaN)
        Logger.debug("NaN seen in TransformPoint");
      haveNotifiedNaN = true;
      z = 1;
    } else if (z <= 0) {
      
      z = 1;
    }
    point3fScreenTemp.z = z;

    
    

    

    if (perspectiveDepth) {
      if (isNavigationMode) {
        
        point3fScreenTemp.x -= navigationShiftXY.x;
        point3fScreenTemp.y -= navigationShiftXY.y;
      }
      
      float factor = getPerspectiveFactor(z);
      point3fScreenTemp.x *= factor;
      point3fScreenTemp.y *= factor;
    }

    
    if (isNavigationMode) {
      point3fScreenTemp.x += navigationOffset.x;
      point3fScreenTemp.y += navigationOffset.y;
    } else {
      point3fScreenTemp.x += fixedRotationOffset.x;
      point3fScreenTemp.y += fixedRotationOffset.y;
    }

    if (Float.isNaN(point3fScreenTemp.x) && !haveNotifiedNaN) {
      Logger.debug("NaN found in transformPoint ");
      haveNotifiedNaN = true;
    }

    point3iScreenTemp.set((int) point3fScreenTemp.x, (int) point3fScreenTemp.y,
        (int) point3fScreenTemp.z);
  }

  

  final private static int NAV_MODE_IGNORE = -2;
  final private static int NAV_MODE_ZOOMED = -1;
  final private static int NAV_MODE_NONE = 0;
  final private static int NAV_MODE_RESET = 1;
  final private static int NAV_MODE_NEWXY = 2;
  final private static int NAV_MODE_NEWXYZ = 3;
  final private static int NAV_MODE_NEWZ = 4;

  private int navMode = NAV_MODE_RESET;


  void setScreenParameters(int screenWidth, int screenHeight,
                           boolean useZoomLarge, boolean antialias,
                           boolean resetSlab, boolean resetZoom) {
    Point3f pt = (isNavigationMode ? new Point3f(navigationCenter) : null);
    Point3f ptoff = new Point3f(navigationOffset);
    ptoff.x = ptoff.x / width;
    ptoff.y = ptoff.y / height;
    super.setScreenParameters(screenWidth, screenHeight, useZoomLarge, 
        antialias, resetSlab, resetZoom);
    if (pt != null) {
      navigationCenter.set(pt);
      navTranslatePercent(-1, ptoff.x * width, ptoff.y * height);
      navigate(0, pt);
    }
  }

  float navigationDepth;

  
  protected void calcNavigationPoint() {
    
    calcNavigationDepthPercent();
    if (!navigating && navMode != NAV_MODE_RESET) {
      
      if (navigationDepth < 100 && navigationDepth > 0
          && !Float.isNaN(previousX)
          && previousX == fixedTranslation.x 
             && previousY == fixedTranslation.y 
             && navMode != NAV_MODE_ZOOMED)
        navMode = NAV_MODE_NEWXYZ;
      else
        navMode = NAV_MODE_NONE;
    }
    switch (navMode) {
    case NAV_MODE_RESET:
      
      navigationOffset.set(width / 2f, getNavPtHeight(), referencePlaneOffset);
      zoomFactor = Float.MAX_VALUE;
      calcCameraFactors();
      calcTransformMatrix();
      newNavigationCenter();
      break;
    case NAV_MODE_NONE:
    case NAV_MODE_ZOOMED:
      
      fixedRotationOffset.set(fixedTranslation);
      newNavigationCenter();
      break;
    case NAV_MODE_NEWXY:
      
      newNavigationCenter();
      break;
    case NAV_MODE_IGNORE:
    case NAV_MODE_NEWXYZ:
      
      
      
      matrixTransform.transform(navigationCenter, pointT);
      float z = pointT.z;
      matrixTransform.transform(fixedRotationCenter, pointT);
      modelCenterOffset = referencePlaneOffset + (pointT.z - z);
      calcCameraFactors();
      calcTransformMatrix();
      break;
    case NAV_MODE_NEWZ:
      
      navigationOffset.z = referencePlaneOffset;
      
      unTransformPoint(navigationOffset, navigationCenter);
      break;
    }
    matrixTransform.transform(navigationCenter, navigationShiftXY);
    if (viewer.getNavigationPeriodic()) {
      
      
      viewer.toUnitCell(navigationCenter, null);
      if (pointT.distance(navigationCenter) > 0.01) {
        matrixTransform.transform(navigationCenter, pointT);
        float dz = navigationShiftXY.z - pointT.z;
        
        modelCenterOffset += dz;
        calcCameraFactors();
        calcTransformMatrix();
        matrixTransform.transform(navigationCenter, navigationShiftXY);
      }
    }
    transformPoint(fixedRotationCenter, fixedTranslation);
    fixedRotationOffset.set(fixedTranslation);
    previousX = fixedTranslation.x;
    previousY = fixedTranslation.y;
    transformPoint(navigationCenter, navigationOffset);
    navigationOffset.z = referencePlaneOffset;
    navMode = NAV_MODE_NONE;
    calcNavigationSlabAndDepth();
  }

  private float getNavPtHeight() {
    boolean navigateSurface = viewer.getNavigateSurface();
    return height / (navigateSurface ? 1f : 2f);
  }

  protected void calcNavigationSlabAndDepth() {
    slabValue = 0;
    depthValue = Integer.MAX_VALUE;
    if (!slabEnabled)
      return;
    slabValue = (isNavigationMode ? 10 : (int) (referencePlaneOffset - navigationSlabOffset));
    depthValue = zValueFromPercent(depthPercentSetting);

    viewer.getGlobalSettings().setParameterValue("navigationDepth",getNavigationDepthPercent());
    viewer.getGlobalSettings().setParameterValue("navigationSlab",getNavigationSlabOffsetPercent());

    if (Logger.debugging)
      Logger.debug("\n" + "\nperspectiveScale: " + referencePlaneOffset
          + " screenPixelCount: " + screenPixelCount 
          + "\nmodelTrailingEdge: "
          + (modelCenterOffset + modelRadiusPixels) + " depthValue: "
          + depthValue + "\nmodelCenterOffset: " + modelCenterOffset
          + " modelRadiusPixels: " + modelRadiusPixels + "\nmodelLeadingEdge: "
          + (modelCenterOffset - modelRadiusPixels) + " slabValue: "
          + slabValue + "\nzoom: " + zoomPercent + " navDepth: "
          + ((int) (100 * getNavigationDepthPercent()) / 100f) + " visualRange: " + visualRange
          + "\nnavX/Y/Z/modelCenterOffset: " + navigationOffset.x + "/"
          + navigationOffset.y + "/" + navigationOffset.z + "/"
          + modelCenterOffset + " navCenter:" + navigationCenter);
  }

  
  private void newNavigationCenter() {
    
    
 
      
    
    
    isNavigationMode = false;
    
    transformPoint(fixedRotationCenter, pointT);
    pointT.x -= navigationOffset.x;
    pointT.y -= navigationOffset.y;
    
    float f = -getPerspectiveFactor(pointT.z);
    pointT.x /= f;
    pointT.y /= f;
    pointT.z = referencePlaneOffset;
    
    
    matrixUnTransform(pointT, navigationCenter);
    isNavigationMode = true;
  }

  boolean canNavigate() {
    return true;
  }

  private int nHits;
  private int multiplier = 1;

  
  protected void resetNavigationPoint(boolean doResetSlab) {

    

    if (zoomPercent < 5 && !isNavigationMode) {
      isNavigationMode = perspectiveDepth = true;
      return;
    }
    if (isNavigationMode) {
      navMode = NAV_MODE_RESET;
      slabPercentSetting = 0;
      perspectiveDepth = true;
    } else if (doResetSlab) {
      slabPercentSetting = 100;
    }
    if (doResetSlab)
      slabEnabled = isNavigationMode;
    
    zoomFactor = Float.MAX_VALUE;
    zoomPercentSetting = zoomPercent;
  }

  protected void setNavigationOffsetRelative(boolean navigatingSurface) {
    if (navigatingSurface) {
      navigateSurface(Integer.MAX_VALUE);
      return;
    }
    if (navigationDepth < 0 && navZ > 0 
        || navigationDepth > 100 && navZ < 0) {
      navZ = 0;
    }
    rotateXRadians(radiansPerDegree * -.02f * navY, null);
    rotateYRadians(radiansPerDegree * .02f * navX, null);    
    Point3f pt = getNavigationCenter();
    Point3f pts = new Point3f();
    transformPoint(pt, pts);
    pts.z += navZ;
    unTransformPoint(pts, pt);
    navigate(0, pt);
   }

  synchronized void navigate(int keyCode, int modifiers) {
    if (!isNavigationMode)
      return;
    if (keyCode == 0) {
      nHits = 0;
      multiplier = 1;
      if (!navigating)
        return;
      navigating = false;
      return;
    }
    nHits++;
    if (nHits % 10 == 0)
      multiplier *= (multiplier == 4 ? 1 : 2);
    boolean navigateSurface = viewer.getNavigateSurface();
    boolean isShiftKey = ((modifiers & InputEvent.SHIFT_MASK) > 0);
    boolean isAltKey = ((modifiers & InputEvent.ALT_MASK) > 0);
    boolean isCtrlKey = ((modifiers & InputEvent.CTRL_MASK) > 0);
    float speed = viewer.getNavigationSpeed() * (isCtrlKey ? 10 : 1);
    switch (keyCode) {
    case KeyEvent.VK_PERIOD:
      navX = navY = navZ = 0;
      setNavOn(false);
      homePosition();
      return;
    case KeyEvent.VK_SPACE:
      if (!navOn)
        return;
      navX = navY = navZ = 0;
      return;
    case KeyEvent.VK_UP:
      if (navOn) {
        if (isAltKey) {
          navY += multiplier;
        } else {
          navZ += multiplier;
        }
        break;
      }
      if (navigateSurface) {
        navigateSurface(Integer.MAX_VALUE);
        break;
      }
      if (isShiftKey) {
        navigationOffset.y -= 2 * multiplier;
        navMode = NAV_MODE_NEWXY;
        break;
      }
      if (isAltKey) {
        rotateXRadians(radiansPerDegree * -.2f * multiplier, null);
        navMode = NAV_MODE_NEWXYZ;
        break;
      }
      modelCenterOffset -= speed * (viewer.getNavigationPeriodic() ? 1 : multiplier);
      navMode = NAV_MODE_NEWZ;
      break;
    case KeyEvent.VK_DOWN:
      if (navOn) {
        if (isAltKey) {
          navY -= multiplier;
        } else {
          navZ -= multiplier;
        }
        break;
      }
      if (navigateSurface) {
        navigateSurface(-2 * multiplier);
        break;
      }
      if (isShiftKey) {
        navigationOffset.y += 2 * multiplier;
        navMode = NAV_MODE_NEWXY;
        break;
      }
      if (isAltKey) {
        rotateXRadians(radiansPerDegree * .2f * multiplier, null);
        navMode = NAV_MODE_NEWXYZ;
        break;
      }
      modelCenterOffset += speed * (viewer.getNavigationPeriodic() ? 1 : multiplier);
      navMode = NAV_MODE_NEWZ;
      break;
    case KeyEvent.VK_LEFT:
      if (navOn) {
        navX -= multiplier;
        break;
      }
      if (navigateSurface) {
        break;
      }
      if (isShiftKey) {
        navigationOffset.x -= 2 * multiplier;
        navMode = NAV_MODE_NEWXY;
        break;
      }
      rotateYRadians(radiansPerDegree * 3 * -.2f * multiplier, null);
      navMode = NAV_MODE_NEWXYZ;
      break;
    case KeyEvent.VK_RIGHT:
      if (navOn) {
        navX += multiplier;
        break;
      }
      if (navigateSurface) {
        break;
      }
      if (isShiftKey) {
        navigationOffset.x += 2 * multiplier;
        navMode = NAV_MODE_NEWXY;
        break;
      }
      rotateYRadians(radiansPerDegree * 3 * .2f * multiplier, null);
      navMode = NAV_MODE_NEWXYZ;
      break;
    default:
      navigating = false;
      navMode = NAV_MODE_NONE;
      return;
    }
    navigating = true;
    finalizeTransformParameters();
  }

  private void navigateSurface(int dz) {
    if (viewer.isRepaintPending())
      return;
    viewer.setShapeProperty(JmolConstants.SHAPE_ISOSURFACE, "navigate", new Integer(dz == Integer.MAX_VALUE ? 2 * multiplier : dz));
    viewer.requestRepaintAndWait();
  }

  void navigate(float seconds, Point3f pt) {
    if (seconds > 0) {
      navigateTo(seconds, null, Float.NaN, pt, Float.NaN, Float.NaN, Float.NaN);
      return;
    }
    navigationCenter.set(pt);
    navMode = NAV_MODE_NEWXYZ;
    navigating = true;
    finalizeTransformParameters();
    navigating = false;
  }

  void navigate(float seconds, Vector3f rotAxis, float degrees) {
    if (degrees == 0)
      return;
    if (seconds > 0) {
      navigateTo(seconds, rotAxis, degrees, null, Float.NaN, Float.NaN,
          Float.NaN);
      return;
    }
    rotateAxisAngle(rotAxis, degrees / degreesPerRadian);
    navMode = NAV_MODE_NEWXYZ;
    navigating = true;
    finalizeTransformParameters();
    navigating = false;
  }

  void setNavigationDepthPercent(float timeSec, float percent) {
    if (timeSec > 0) {
      navigateTo(timeSec, null, Float.NaN, null, percent, Float.NaN, Float.NaN);
      return;
    }    
    setNavigationDepthPercent(percent);
  }

  void navTranslate(float seconds, Point3f pt) {
    transformPoint(pt, pointT);
    if (seconds > 0) {
      navigateTo(seconds, null, Float.NaN, null, Float.NaN, pointT.x, pointT.y);
      return;
    }
    navTranslatePercent(-1, pointT.x, pointT.y);
  }

  void navTranslatePercent(float seconds, float x, float y) {
    
    transformPoint(navigationCenter, navigationOffset);
    if (seconds >= 0) {
      if (!Float.isNaN(x))
        x = width * x / 100f
            + (Float.isNaN(y) ? navigationOffset.x : (width / 2f));
      if (!Float.isNaN(y))
        y = height * y / 100f
            + (Float.isNaN(x) ? navigationOffset.y : getNavPtHeight());
    }
    if (seconds > 0) {
      navigateTo(seconds, null, Float.NaN, null, Float.NaN, x, y);
      return;
    }
    if (!Float.isNaN(x))
      navigationOffset.x = x;
    if (!Float.isNaN(y))
      navigationOffset.y = y;
    navMode = NAV_MODE_NEWXY;
    navigating = true;
    finalizeTransformParameters();
    navigating = false;
  }

  private void navigateTo(float floatSecondsTotal, Vector3f axis,
                          float degrees, Point3f center, float depthPercent,
                          float xTrans, float yTrans) {

    Orientation o = viewer.getOrientation();
    if (!Float.isNaN(degrees) && degrees != 0)
      navigate(0, axis, degrees);
    if (center != null) {
      navigate(0, center);
    }
    if (!Float.isNaN(xTrans) || !Float.isNaN(yTrans))
      navTranslatePercent(-1, xTrans, yTrans);
    if (!Float.isNaN(depthPercent))
      setNavigationDepthPercent(depthPercent);
    Orientation o1 = viewer.getOrientation();
    o.restore(0, true);
    o1.restore(floatSecondsTotal, true);
  }

  void navigate(float seconds, Point3f[][] pathGuide) {
    navigate(seconds, pathGuide, null, null, 0, Integer.MAX_VALUE);
  }

  void navigate(float seconds, Point3f[] path, float[] theta, int indexStart,
                int indexEnd) {
    navigate(seconds, null, path, theta, indexStart, indexEnd);
  }

  private void navigate(float seconds, Point3f[][] pathGuide, Point3f[] path,
                float[] theta, int indexStart, int indexEnd) {
    if (seconds <= 0) 
      seconds = 2;
    boolean isPathGuide = (pathGuide != null);
    int nSegments = Math.min(
        (isPathGuide ? pathGuide.length : path.length) - 1, indexEnd);
    if (!isPathGuide)
      while (nSegments > 0 && path[nSegments] == null)
        nSegments--;
    nSegments -= indexStart;
    if (nSegments < 1)
      return;
    int nPer = (int) (10 * seconds); 
    int nSteps = nSegments * nPer + 1;
    Point3f[] points = new Point3f[nSteps + 2];
    Point3f[] pointGuides = new Point3f[isPathGuide ? nSteps + 2 : 0];
    int iPrev, iNext, iNext2, iNext3, pt;
    for (int i = 0; i < nSegments; i++) {
      iPrev = Math.max(i - 1, 0) + indexStart;
      pt = i + indexStart;
      iNext = Math.min(i + 1, nSegments) + indexStart;
      iNext2 = Math.min(i + 2, nSegments) + indexStart;
      iNext3 = Math.min(i + 3, nSegments) + indexStart;
      if (isPathGuide) {
        Graphics3D.getHermiteList(7, pathGuide[iPrev][0], pathGuide[pt][0],
            pathGuide[iNext][0], pathGuide[iNext2][0], pathGuide[iNext3][0],
            points, i * nPer, nPer + 1);
        Graphics3D.getHermiteList(7, pathGuide[iPrev][1], pathGuide[pt][1],
            pathGuide[iNext][1], pathGuide[iNext2][1], pathGuide[iNext3][1],
            pointGuides, i * nPer, nPer + 1);
      } else {
        Graphics3D.getHermiteList(7, path[iPrev], path[pt], path[iNext],
            path[iNext2], path[iNext3], points, i * nPer, nPer + 1);
      }
    }
    int totalSteps = nSteps;
    viewer.setInMotion(true);
    int frameTimeMillis = (int) (1000 / navFps);
    long targetTime = System.currentTimeMillis();
    for (int iStep = 0; iStep < totalSteps; ++iStep) {
      navigate(0, points[iStep]);
      if (isPathGuide) {
        alignZX(points[iStep], points[iStep + 1], pointGuides[iStep]);
      }
      targetTime += frameTimeMillis;
      if (System.currentTimeMillis() < targetTime) {
        viewer.requestRepaintAndWait();
        if (!viewer.isScriptExecuting())
          break;
        int sleepTime = (int) (targetTime - System.currentTimeMillis());
        if (sleepTime > 0) {
          try {
            Thread.sleep(sleepTime);
          } catch (InterruptedException ie) {
          }
        }
      }
    }
  }
  
  void navigateSurface(float timeSeconds, String name) {
  }


  
  void alignZX(Point3f pt0, Point3f pt1, Point3f ptVectorWing) {
    Point3f pt0s = new Point3f();
    Point3f pt1s = new Point3f();
    matrixRotate.transform(pt0, pt0s);
    matrixRotate.transform(pt1, pt1s);
    Vector3f vPath = new Vector3f(pt0s);
    vPath.sub(pt1s);
    Vector3f v = new Vector3f(0, 0, 1);
    float angle = vPath.angle(v);
    v.cross(vPath, v);
    if (angle != 0)
      navigate(0, v, angle * degreesPerRadian);
    matrixRotate.transform(pt0, pt0s);
    Point3f pt2 = new Point3f(ptVectorWing);
    pt2.add(pt0);
    Point3f pt2s = new Point3f();
    matrixRotate.transform(pt2, pt2s);
    vPath.set(pt2s);
    vPath.sub(pt0s);
    vPath.z = 0; 
    v.set(-1, 0, 0); 
    angle = vPath.angle(v);
    if (vPath.y < 0)
      angle = -angle;
    v.set(0, 0, 1);
    if (angle != 0)
      navigate(0, v, angle * degreesPerRadian);
    if (viewer.getNavigateSurface()) {
      
      v.set(1, 0, 0);
      navigate(0, v, 20);
    }
    matrixRotate.transform(pt0, pt0s);
    matrixRotate.transform(pt1, pt1s);
    matrixRotate.transform(ptVectorWing, pt2s);
  }

  Point3f getNavigationCenter() {
    return navigationCenter;
  }

  float getNavigationDepthPercent() {
    return navigationDepth;
  }
  
  void setNavigationSlabOffsetPercent(float percent) {
    viewer.getGlobalSettings().setParameterValue("navigationSlab", percent);
    calcCameraFactors(); 
    navigationSlabOffset = percent / 50 * modelRadiusPixels;
  }

  private float getNavigationSlabOffsetPercent() {
    calcCameraFactors(); 
    return 50 * navigationSlabOffset / modelRadiusPixels;
  }

  Point3f getNavigationOffset() {
    transformPoint(navigationCenter, navigationOffset);
    return navigationOffset;
  }

  private void setNavigationDepthPercent(float percent) {
    
    

    viewer.getGlobalSettings().setParameterValue("navigationDepth", percent);
    calcCameraFactors(); 
    modelCenterOffset = referencePlaneOffset - (1 - percent / 50) * modelRadiusPixels;
    calcCameraFactors(); 
    navMode = NAV_MODE_ZOOMED;
  }

  private void calcNavigationDepthPercent() {
    calcCameraFactors(); 
    navigationDepth = (modelRadiusPixels == 0 ? 50 : 
      50 * (1 + (modelCenterOffset - referencePlaneOffset) / modelRadiusPixels));
  }

  float getNavigationOffsetPercent(char XorY) {
    getNavigationOffset();
    if (width == 0 || height == 0)
      return 0;
    return (XorY == 'X' ? (navigationOffset.x - width / 2f) * 100f / width
        : (navigationOffset.y - getNavPtHeight()) * 100f / height);
  }

  protected String getNavigationText(boolean addComments) {
    getNavigationOffset();
    return (addComments ? " /* navigation center, translation, depth */ " : " ")
        + Escape.escape(navigationCenter) + " "
        + getNavigationOffsetPercent('X') + " "
        + getNavigationOffsetPercent('Y') + " " + getNavigationDepthPercent();
  }

  protected String getNavigationState() {
    if (!isNavigationMode)
      return "";
    return "# navigation state;\nnavigate 0 center "
        + Escape.escape(getNavigationCenter())
        + ";\nnavigate 0 translate " + getNavigationOffsetPercent('X') + " "
        + getNavigationOffsetPercent('Y') + ";\nset navigationDepth "
        + getNavigationDepthPercent() + ";\nset navigationSlab "
        + getNavigationSlabOffsetPercent() + ";\n\n";
  }
  
}
