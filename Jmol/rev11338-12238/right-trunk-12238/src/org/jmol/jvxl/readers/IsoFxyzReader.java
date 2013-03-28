
package org.jmol.jvxl.readers;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;

import org.jmol.jvxl.data.JvxlCoder;

class IsoFxyzReader extends VolumeDataReader {
  
  IsoFxyzReader(SurfaceGenerator sg) {
    super(sg);
    precalculateVoxelData = false;
  }

  private String functionName;
  private float[][][] data;
  
  protected void setup() {
    functionName = (String) params.functionXYinfo.get(0);
    jvxlFileHeaderBuffer = new StringBuffer();
    jvxlFileHeaderBuffer.append("functionXYZ\n").append(functionName).append("\n");
    volumetricOrigin.set((Point3f) params.functionXYinfo.get(1));
    for (int i = 0; i < 3; i++) {
      Point4f info = (Point4f) params.functionXYinfo.get(i + 2);
      voxelCounts[i] = Math.abs((int) info.x);
      volumetricVectors[i].set(info.y, info.z, info.w);      
    }
    if (isAnisotropic)
      setVolumetricAnisotropy();
    data = (float[][][]) params.functionXYinfo.get(5);
    JvxlCoder.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData, jvxlFileHeaderBuffer);
  }

  public float getValue(int x, int y, int z, int ptyz) {
    return data[x][y][z];
  }
}
