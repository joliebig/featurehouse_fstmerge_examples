package org.openscience.jvxl.simplewriter;

import javax.vecmath.Point3f;

public class VoxelDataCreator {

  VolumeData volumeData;
  
  VoxelDataCreator(VolumeData volumeData) {
    this.volumeData = volumeData;
  }
  
    
    
    void createVoxelData() {

      int[] counts = volumeData.getVoxelCounts();
      int nX = counts[0];
      int nY = counts[1];
      int nZ = counts[2];
      float[][][] voxelData = new float[nX][nY][nZ];
      volumeData.setVoxelData(voxelData);
      
      
      
      
      
      
      for (int x = 0; x < nX; ++x)
        for (int y = 0; y < nY; ++y)
          for (int z = 0; z < nZ; ++z) {
            voxelData[x][y][z] = getValue(x, y, z);
            
          }
    }    

    Point3f pt = new Point3f();

    public float getValue(int x, int y, int z) {
      volumeData.voxelPtToXYZ(x, y, z, pt);
      return pt.x * pt.x + pt.y * pt.y - pt.z * pt.z;  
    }


}
