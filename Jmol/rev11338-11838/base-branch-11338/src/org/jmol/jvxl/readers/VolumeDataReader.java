
package org.jmol.jvxl.readers;

import javax.vecmath.Point3f;
import javax.vecmath.Matrix3f;

import org.jmol.util.Logger;


class VolumeDataReader extends SurfaceReader {

  
  
  protected int dataType;
  protected boolean precalculateVoxelData;
  protected boolean allowMapData;
  protected Point3f center, point;
  protected float[] anisotropy;
  protected boolean isAnisotropic;
  protected Matrix3f eccentricityMatrix;
  protected Matrix3f eccentricityMatrixInverse;
  protected boolean isEccentric;
  protected float eccentricityScale;
  protected float eccentricityRatio;


  VolumeDataReader(SurfaceGenerator sg) {
    super(sg);
    dataType = params.dataType;
    precalculateVoxelData = true;
    allowMapData = true;    
    center = params.center;
    anisotropy = params.anisotropy;
    isAnisotropic = params.isAnisotropic;
    
      
    
    eccentricityMatrix = params.eccentricityMatrix;
    eccentricityMatrixInverse = params.eccentricityMatrixInverse;
    isEccentric = params.isEccentric;
    eccentricityScale = params.eccentricityScale;
    eccentricityRatio = params.eccentricityRatio;
  }
  
  void setup() {
    
    
    jvxlFileHeaderBuffer = new StringBuffer("volume data read from file\n\n");
    JvxlReader.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData, jvxlFileHeaderBuffer);
  }
  
  boolean readVolumeParameters() {
    setup();
    initializeVolumetricData();
    return true;
  }

  boolean readVolumeData(boolean isMapData) {
    try {
      readSurfaceData(isMapData);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  protected void readVoxelDataIndividually(boolean isMapData) throws Exception {
    if (isMapData && !allowMapData)
      return; 
    voxelData = (isMapData ? new float[nPointsX][nPointsY][nPointsZ] : null);
    volumeData.setVoxelData(voxelData);
    if (!isMapData) 
      return;
    for (int x = 0; x < nPointsX; ++x) {
      float[][] plane = new float[nPointsY][];
      voxelData[x] = plane;
      for (int y = 0; y < nPointsY; ++y) {
        float[] strip = plane[y] = new float[nPointsZ];
        for (int z = 0; z < nPointsZ; ++z) {
          strip[z] = getValue(x, y, z);
        }
      }
    }
  }
  
  protected int setVoxelRange(int index, float min, float max, float ptsPerAngstrom,
                    int gridMax) {
    if (min >= max) {
      min = -10;
      max = 10;
    }
    float range = max - min;
    float resolution = params.resolution;
    if (resolution != Float.MAX_VALUE) {
      ptsPerAngstrom = resolution;
    }
    int nGrid = (int) (range * ptsPerAngstrom) + 1;
    if (nGrid > gridMax) {
      if ((dataType & Parameters.HAS_MAXGRID) > 0) {
        if (resolution != Float.MAX_VALUE)
          Logger.info("Maximum number of voxels for index=" + index);
        nGrid = gridMax;
      } else if (resolution == Float.MAX_VALUE) {
        nGrid = gridMax;
      }
    }
    ptsPerAngstrom = (nGrid - 1) / range;
    voxelCounts[index] = nGrid;
    float d = volumeData.volumetricVectorLengths[index] = 1f / ptsPerAngstrom;

    switch (index) {
    case 0:
      volumetricVectors[0].set(d, 0, 0);
      volumetricOrigin.x = min;
      break;
    case 1:
      volumetricVectors[1].set(0, d, 0);
      volumetricOrigin.y = min;
      break;
    case 2:
      volumetricVectors[2].set(0, 0, d);
      volumetricOrigin.z = min;
      if (isEccentric)
        eccentricityMatrix.transform(volumetricOrigin);
      if (center.x != Float.MAX_VALUE)
        volumetricOrigin.add(center);
    }
    if (isEccentric)
      eccentricityMatrix.transform(volumetricVectors[index]);
    return voxelCounts[index];
  }

  protected void readSurfaceData(boolean isMapData) throws Exception {
    
    if (precalculateVoxelData) 
      generateCube();
    else
      readVoxelDataIndividually(isMapData);
  }
  
  protected void generateCube() {
    Logger.info("data type: user volumeData");
    Logger.info("voxel grid origin:" + volumetricOrigin);
    for (int i = 0; i < 3; ++i)
      Logger.info("voxel grid vector:" + volumetricVectors[i]);
    Logger.info("Read " + nPointsX + " x " + nPointsY + " x " + nPointsZ
        + " data points");
  }  
 }
