
package org.jmol.jvxl.readers;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;

class IsoFxyReader extends VolumeDataReader {
  
  IsoFxyReader(SurfaceGenerator sg) {
    super(sg);
    precalculateVoxelData = false;
  }

  private String functionName;
  private float[][] data;
  
  private boolean isPlanarMapping;
  
  protected void setup() {
    
    isPlanarMapping = (params.thePlane != null || params.state == Parameters.STATE_DATA_COLORED);
    functionName = (String) params.functionXYinfo.get(0);
    jvxlFileHeaderBuffer = new StringBuffer();
    jvxlFileHeaderBuffer.append("functionXY\n").append(functionName).append("\n");
    volumetricOrigin.set((Point3f) params.functionXYinfo.get(1));
    
      
    for (int i = 0; i < 3; i++) {
      Point4f info = (Point4f) params.functionXYinfo.get(i + 2);
      voxelCounts[i] = Math.abs((int) info.x);
      volumetricVectors[i].set(info.y, info.z, info.w);      
      
        
    }
    data = (float[][]) params.functionXYinfo.get(5);
    JvxlReader.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData, jvxlFileHeaderBuffer);
  }

  public float getValue(int x, int y, int z) {
    
    return (isPlanarMapping ? data[x][y] : data[x][y] - (volumeData.origin[2] + z * volumeData.volumetricVectors[2].z));
  }
}
