
package org.jmol.jvxl.readers;

import java.io.BufferedReader;

abstract class PolygonFileReader extends SurfaceFileReader {

  protected int nVertices;
  protected int nTriangles;

  PolygonFileReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    vertexDataOnly = true;
  }

  void discardTempData(boolean discardAll) {
    try {
      if (br != null)
        br.close();
    } catch (Exception e) {
    }
    super.discardTempData(discardAll);
  }
     
  boolean readVolumeParameters() {
    
    return true;
  }
  
  boolean readVolumeData(boolean isMapData) {
    
    return true;
  }

  protected void readSurfaceData(boolean isMapData) throws Exception {
    getSurfaceData();
    
  }

  abstract void getSurfaceData() throws Exception;
}
