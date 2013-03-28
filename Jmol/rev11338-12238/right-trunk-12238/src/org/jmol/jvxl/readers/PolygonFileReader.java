
package org.jmol.jvxl.readers;

import java.io.BufferedReader;
import java.util.Date;



abstract class PolygonFileReader extends SurfaceFileReader {

  protected int nVertices;
  protected int nTriangles;

  PolygonFileReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    jvxlFileHeaderBuffer = new StringBuffer();
    jvxlFileHeaderBuffer.append("#created ").append(new Date()).append("\n");
    vertexDataOnly = true;
  }

  protected boolean readVolumeParameters() {
    
    return true;
  }
  
  protected boolean readVolumeData(boolean isMapData) {
    
    return true;
  }

  protected void readSurfaceData(boolean isMapData) throws Exception {
    getSurfaceData();
    
  }

  abstract void getSurfaceData() throws Exception;
  
}
