
package org.jmol.jvxl.readers;

import java.io.BufferedReader;
import java.util.BitSet;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.util.Logger;
import org.jmol.util.Parser;

abstract class VolumeFileReader extends SurfaceFileReader {

  protected boolean endOfData;
  protected boolean negativeAtomCount;
  protected int atomCount;
  private int nSurfaces;
  protected boolean isAngstroms;
  protected boolean canDownsample;
  private int[] downsampleRemainders;
  protected float dataMin, dataMax, dataMean;
 
  VolumeFileReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    canDownsample = isProgressive = isXLowToHigh = true;
    jvxlData.wasCubic = true;
    dataMin = Float.MAX_VALUE;
    dataMax = -Float.MAX_VALUE;
    dataMin = 0;
    boundingBox = params.boundingBox;
  }

  protected float recordData(float value) {
     if (value < dataMin)
       dataMin = value;
     if (value > dataMax)
       dataMax = value;
     dataMean += value;
     return value;
  }
  
  protected void closeReader() {
    super.closeReader();
    int n = nPointsX * nPointsY * nPointsZ;
    if (n == 0)
      return;
    dataMean /= n;
    if (dataMax != -Float.MAX_VALUE)
      Logger.info("VolumeFileReader closing file: data min/max/mean = " + dataMin + ", " + dataMax + ", " + dataMean);
  }
  
  boolean readVolumeParameters() {
    endOfData = false;
    nSurfaces = readVolumetricHeader();
    if (nSurfaces == 0)
      return false;
    if (nSurfaces < params.fileIndex) {
      Logger.warn("not enough surfaces in file -- resetting params.fileIndex to "
          + nSurfaces);
      params.fileIndex = nSurfaces;
    }
    return true;
  }
  
  boolean readVolumeData(boolean isMapData) {
    if (!gotoAndReadVoxelData(isMapData))
      return false;
    if (!vertexDataOnly)
      Logger.info("JVXL read: " + nPointsX + " x " + nPointsY + " x " + nPointsZ
          + " data points");
    return true;
  }

  protected int readVolumetricHeader() {
    try {
      readTitleLines();
      Logger.info(jvxlFileHeaderBuffer.toString());
      readAtomCountAndOrigin();
      if (atomCount == Integer.MIN_VALUE)
        return 0;
      Logger.info("voxel grid origin:" + volumetricOrigin);
      int downsampleFactor = params.downsampleFactor;
      boolean downsampling = (canDownsample && downsampleFactor > 0);
      for (int i = 0; i < 3; ++i)
        readVoxelVector(i);
      if (downsampling) {
        downsampleRemainders = new int[3];
        Logger.info("downsample factor = " + downsampleFactor);
        for (int i = 0; i < 3; ++i) {
          int n = voxelCounts[i];
          downsampleRemainders[i] = n % downsampleFactor;
          voxelCounts[i] /= downsampleFactor;
          volumetricVectors[i].scale(downsampleFactor);
          Logger.info("downsampling axis " + (i + 1) + " from " + n + " to "
              + voxelCounts[i]);
        }
      }
      for (int i = 0; i < 3; ++i) {
        line = voxelCounts[i] + " " + volumetricVectors[i].x + " "
            + volumetricVectors[i].y + " " + volumetricVectors[i].z;
        jvxlFileHeaderBuffer.append(line).append('\n');
        Logger.info("voxel grid count/vector:" + line);
        if (!isAngstroms)
          volumetricVectors[i].scale(ANGSTROMS_PER_BOHR);
      }
      if (isAnisotropic)
        setVolumetricAnisotropy();
      volumeData.setVolumetricXml();
      for (int i = 0; i < atomCount; ++i)
        jvxlFileHeaderBuffer.append(br.readLine() + "\n");
      return readExtraLine();
    } catch (Exception e) {
      Logger.error(e.toString());
      return 0;
    }
  }
  
  protected void readTitleLines() throws Exception {
    
  }
  
  protected String skipComments(boolean allowBlankLines) throws Exception {
    StringBuffer sb = new StringBuffer();
    while ((line = br.readLine()) != null && 
        (allowBlankLines && line.length() == 0 || line.indexOf("#") == 0))
      sb.append(line).append('\n');
    return sb.toString();
  }
  
  protected void readAtomCountAndOrigin() throws Exception {
    
  }

  protected void readVoxelVector(int voxelVectorIndex) throws Exception {    
    line = br.readLine();
    Vector3f voxelVector = volumetricVectors[voxelVectorIndex];
    if ((voxelCounts[voxelVectorIndex] = parseInt(line)) == Integer.MIN_VALUE) 
      next[0] = line.indexOf(" ");
    voxelVector.set(parseFloat(), parseFloat(), parseFloat());
    if (isAnisotropic)
      setVectorAnisotropy(voxelVector);
  }

  protected int readExtraLine() throws Exception {
    if (!negativeAtomCount)
      return 1;
    line = br.readLine();
    Logger.info("Reading extra CUBE information line: " + line);
    return parseInt(line);
  }

  private int downsampleFactor;
  private int nSkipX, nSkipY, nSkipZ;
  
  protected void readSurfaceData(boolean isMapData) throws Exception {
    
    

    next[0] = 0;
    downsampleFactor = params.downsampleFactor;
    nSkipX = 0;
    nSkipY = 0;
    nSkipZ = 0;
    if (canDownsample && downsampleFactor > 0) {
      nSkipX = downsampleFactor - 1;
      nSkipY = downsampleRemainders[2]
          + (downsampleFactor - 1)
          * (nSkipZ = (nPointsZ * downsampleFactor + downsampleRemainders[2]));
      nSkipZ = downsampleRemainders[1] * nSkipZ + (downsampleFactor - 1)
          * nSkipZ * (nPointsY * downsampleFactor + downsampleRemainders[1]);
      
    }

    if (params.thePlane != null) {
      params.cutoff = 0f;
    } else if (isJvxl) {
      params.cutoff = (params.isBicolorMap || params.colorBySign ? 0.01f : 0.5f);
    }
    nDataPoints = 0;
    line = "";
    jvxlNSurfaceInts = 0;
    if (isProgressive && !isMapData || isJvxl) {
      nDataPoints = volumeData.setVoxelCounts(nPointsX, nPointsY, nPointsZ);
      voxelData = null;
      if (isJvxl)
        jvxlVoxelBitSet = getVoxelBitSet(nDataPoints);
    } else {
      voxelData = new float[nPointsX][][];
      
      
      

      for (int x = 0; x < nPointsX; ++x) {
        float[][] plane = new float[nPointsY][];
        voxelData[x] = plane;
        for (int y = 0; y < nPointsY; ++y) {
          float[] strip = new float[nPointsZ];
          plane[y] = strip;
          for (int z = 0; z < nPointsZ; ++z) {
            strip[z] = recordData(getNextVoxelValue());
            if (nSkipX != 0)
              skipVoxels(nSkipX);
          }
          if (nSkipY != 0)
            skipVoxels(nSkipY);
        }
        if (nSkipZ != 0)
          skipVoxels(nSkipZ);
      }
      
    }
    volumeData.setVoxelData(voxelData);
  }

  
  
  
  
  
  
  private float[][] yzPlanes;
  private int yzCount;
  public void getPlane(int x) {
    float[] plane;
    if (yzCount == 0) {
      Logger.info("VolumeFileReader reading data progressively");
      yzPlanes = new float[2][];
      yzCount = nPointsY * nPointsZ;
      yzPlanes[0] = new float[yzCount];
      yzPlanes[1] = new float[yzCount];
    }
    plane = yzPlanes[x % 2];
    try {
      for (int y = 0, ptyz = 0; y < nPointsY; ++y) {
        for (int z = 0; z < nPointsZ; ++z) {
          plane[ptyz++] = recordData(getNextVoxelValue());
          if (nSkipX != 0)
            skipVoxels(nSkipX);
        }
        if (nSkipY != 0)
          skipVoxels(nSkipY);
      }
      if (nSkipZ != 0)
        skipVoxels(nSkipZ);
    } catch (Exception e) {
      
    }
  }
  
  protected Point3f[] boundingBox;
  
  public float getValue(int x, int y, int z, int ptyz) {
    if (boundingBox != null) {
      volumeData.voxelPtToXYZ(x, y, z, ptTemp);
      if (ptTemp.x < boundingBox[0].x || ptTemp.x > boundingBox[1].x
          || ptTemp.y < boundingBox[0].y || ptTemp.y > boundingBox[1].y
          || ptTemp.z < boundingBox[0].z || ptTemp.z > boundingBox[1].z
      )
        return Float.NaN;
    }
    if (yzPlanes == null)
      return super.getValue(x, y, z, ptyz);
    return yzPlanes[x % 2][ptyz];
  }
  
  private void skipVoxels(int n) throws Exception {
    
    for (int i = n; --i >= 0;)
      getNextVoxelValue();
  }
  
  protected BitSet getVoxelBitSet(int nPoints) throws Exception {
    
    return null;  
  }
  
  protected float getNextVoxelValue() throws Exception {
    float voxelValue = 0;
    if (nSurfaces > 1 && !params.blockCubeData) {
      for (int i = 1; i < params.fileIndex; i++)
        nextVoxel();
      voxelValue = nextVoxel();
      for (int i = params.fileIndex; i < nSurfaces; i++)
        nextVoxel();
    } else {
      voxelValue = nextVoxel();
    }
    return voxelValue;
  }

  protected float nextVoxel() throws Exception {
    float voxelValue = parseFloat();
    if (Float.isNaN(voxelValue)) {
      while ((line = br.readLine()) != null
          && Float.isNaN(voxelValue = parseFloat(line))) {
      }
      if (line == null) {
        if (!endOfData)
          Logger.warn("end of file reading cube voxel data? nBytes=" + nBytes
              + " nDataPoints=" + nDataPoints + " (line):" + line);
        endOfData = true;
        line = "0 0 0 0 0 0 0 0 0 0";
      }
      nBytes += line.length() + 1;
    }
    return voxelValue;
  }

  protected void gotoData(int n, int nPoints) throws Exception {
    if (!params.blockCubeData)
      return;
    if (n > 0)
      Logger.info("skipping " + n + " data sets, " + nPoints + " points each");
    for (int i = 0; i < n; i++)
      skipData(nPoints);
  }

  protected void skipData(int nPoints) throws Exception {
    int iV = 0;
    while (iV < nPoints) {
      line = br.readLine();
      iV += countData(line);
    }
  }

  private int countData(String str) {
    int count = 0;
    int ich = 0;
    int ichMax = str.length();
    char ch;
    while (ich < ichMax) {
      while (ich < ichMax && ((ch = str.charAt(ich)) == ' ' || ch == '\t'))
        ++ich;
      if (ich < ichMax)
        ++count;
      while (ich < ichMax && ((ch = str.charAt(ich)) != ' ' && ch != '\t'))
        ++ich;
    }
    return count;
  }

  
  protected static boolean checkAtomLine(boolean isXLowToHigh,
                                         boolean isAngstroms,
                                         String strAtomCount, String atomLine,
                                         StringBuffer bs) {
    if (atomLine.indexOf("ANGSTROMS") >= 0)
      isAngstroms = true;
    int atomCount = (strAtomCount == null ? Integer.MAX_VALUE : Parser
        .parseInt(strAtomCount));
    switch (atomCount) {
    case Integer.MIN_VALUE:
      atomCount = 0;
      atomLine = " " + atomLine.substring(atomLine.indexOf(" ") + 1);
      break;
    case Integer.MAX_VALUE:
      atomCount = Integer.MIN_VALUE;
      break;
    default:
      String s = "" + atomCount;
      atomLine = atomLine.substring(atomLine.indexOf(s) + s.length());
    }
    if (isAngstroms) {
      if (atomLine.indexOf("ANGSTROM") < 0)
        atomLine += " ANGSTROMS";
    } else {
      if (atomLine.indexOf("BOHR") < 0)
        atomLine += " BOHR";
    }
    atomLine = (atomCount == Integer.MIN_VALUE ? ""
        : (isXLowToHigh ? "+" : "-") + Math.abs(atomCount))
        + atomLine + "\n";
    bs.append(atomLine);
    return isAngstroms;
  }
  
}

