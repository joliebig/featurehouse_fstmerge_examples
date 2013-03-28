
package org.jmol.viewer;

import org.jmol.util.Logger;

class TransformManager10 extends TransformManager {


  TransformManager10(Viewer viewer) {
    super(viewer);
    perspectiveModel = 10;
  }

  TransformManager10(Viewer viewer, int width, int height) {
    super(viewer, width, height);
    perspectiveModel = 10;
  }

  
  
  

  protected void calcCameraFactors() {
    
    
    
    
    if (Float.isNaN(cameraDepth)) {
      cameraDepth = cameraDepthSetting;
    }

    
    cameraDistance = cameraDepth * screenPixelCount; 
    
    
    
    
    cameraScaleFactor = 1.02f + 0.5f / cameraDepth; 

    
    
    scalePixelsPerAngstrom = 
      (scale3D && !perspectiveDepth ? 72 / scale3DAngstromsPerInch : scaleDefaultPixelsPerAngstrom * zoomPercent / 100
        * cameraScaleFactor); 

    
    modelRadiusPixels = modelRadius * scalePixelsPerAngstrom;

    
    
    modelCenterOffset = cameraDistance + modelRadiusPixels; 

    
    
    
    referencePlaneOffset = cameraDistance; 
    
    

  }
  
  protected float getPerspectiveFactor(float z) {
    
    
    
    
    
    float factor = (z <= 0 ? referencePlaneOffset : referencePlaneOffset / z);
    if (zoomPercent >= MAXIMUM_ZOOM_PERSPECTIVE_DEPTH)
      factor += (zoomPercent - MAXIMUM_ZOOM_PERSPECTIVE_DEPTH)
          / (MAXIMUM_ZOOM_PERCENTAGE - MAXIMUM_ZOOM_PERSPECTIVE_DEPTH)
          * (1 - factor);
    return factor;
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
      float factor = getPerspectiveFactor(z);
      point3fScreenTemp.x *= factor;
      point3fScreenTemp.y *= factor;
    }

    

    point3fScreenTemp.x += fixedRotationOffset.x;
    point3fScreenTemp.y += fixedRotationOffset.y;

    if (Float.isNaN(point3fScreenTemp.x) && !haveNotifiedNaN) {
      Logger.debug("NaN found in transformPoint ");
      haveNotifiedNaN = true;
    }
    point3iScreenTemp.set((int) point3fScreenTemp.x, (int) point3fScreenTemp.y,
        (int) point3fScreenTemp.z);
  }
  
  

   
}
