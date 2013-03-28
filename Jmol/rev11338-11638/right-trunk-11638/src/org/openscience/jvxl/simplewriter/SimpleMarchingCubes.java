
package org.openscience.jvxl.simplewriter;

import java.util.BitSet;
import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.data.JvxlData;
import org.jmol.jvxl.data.VolumeData;



public class SimpleMarchingCubes {

  

  private VolumeData volumeData;
  private float cutoff;
  private boolean isCutoffAbsolute;
  private boolean isXLowToHigh;
  private boolean doCalcArea;
  private boolean doSaveSurfacePoints;
  private float calculatedArea = Float.NaN;
  private float calculatedVolume = Float.NaN;
  private Vector surfacePoints;
    
  private StringBuffer fractionData = new StringBuffer();

  private int cubeCountX, cubeCountY, cubeCountZ;
  private int nY, nZ;
  private int yzCount;

  private BitSet bsVoxels = new BitSet();

  private int mode;
  private final static int MODE_CUBE = 1;
  private final static int MODE_BITSET = 2;
  private final static int MODE_GETXYZ = 3;

  private VoxelDataCreator vdc;
  
  public SimpleMarchingCubes(VoxelDataCreator vdc, VolumeData volumeData,
      JvxlData jvxlData, Vector surfacePointsReturn, float[] areaVolumeReturn) {

    
    
    
    
    
    
    
    

    this.vdc = vdc;
    this.volumeData = volumeData;
    cutoff = jvxlData.cutoff;
    isCutoffAbsolute = jvxlData.isCutoffAbsolute;
    isXLowToHigh = jvxlData.isXLowToHigh;
    doCalcArea = (areaVolumeReturn != null);
    surfacePoints = surfacePointsReturn;
    if (surfacePoints == null && doCalcArea)
      surfacePoints = new Vector();
    doSaveSurfacePoints = (surfacePoints != null);

    if (vdc == null) {
      mode = MODE_CUBE;
    } else {
      mode = MODE_GETXYZ;
    }

    cubeCountX = volumeData.voxelCounts[0] - 1;
    cubeCountY = (nY = volumeData.voxelCounts[1]) - 1;
    cubeCountZ = (nZ = volumeData.voxelCounts[2]) - 1;
    yzCount = nY * nZ;
    edgeVertexPointers = (isXLowToHigh ? edgeVertexPointersLowToHigh
        : edgeVertexPointersHighToLow);
    edgeVertexPlanes = (isXLowToHigh ? edgeVertexPlanesLowToHigh
        : edgeVertexPlanesHighToLow);
    isoPointIndexPlanes = new int[2][yzCount][3];
    xyPlanes = (mode == MODE_GETXYZ ? new float[2][yzCount] : null);
    setLinearOffsets();
    calcVoxelVertexVectors();
    jvxlData.jvxlEdgeData = getEdgeData();
    jvxlData.nPointsX = volumeData.voxelCounts[0];
    jvxlData.nPointsY = volumeData.voxelCounts[1];
    jvxlData.nPointsZ = volumeData.voxelCounts[2];
    jvxlData.setSurfaceInfoFromBitSet(bsVoxels, null);
    jvxlData.updateInfoLines();
    if (doCalcArea) {
      areaVolumeReturn[0] = calculatedArea;
      areaVolumeReturn[1] = calculatedVolume;
    }    
  }

  private final float[] vertexValues = new float[8];
  private final Point3i[] vertexPoints = new Point3i[8];
  {
    for (int i = 8; --i >= 0;)
      vertexPoints[i] = new Point3i();
  }

  int edgeCount;

  

  private final Vector3f[] voxelVertexVectors = new Vector3f[8];
  private final Vector3f[] edgeVectors = new Vector3f[12];
  {
    for (int i = 12; --i >= 0;)
      edgeVectors[i] = new Vector3f();
    for (int i = 8; --i >= 0;)
      vertexPoints[i] = new Point3i();
  }

  private void calcVoxelVertexVectors() {
    
    volumeData.setMatrix();
    for (int i = 8; --i >= 0;)
      volumeData.transform(cubeVertexVectors[i],
          voxelVertexVectors[i] = new Vector3f());
    for (int i = 12; --i >= 0;)
      edgeVectors[i].sub(voxelVertexVectors[edgeVertexes[i + i + 1]],
          voxelVertexVectors[edgeVertexes[i + i]]);
  }

  private final static Vector3f[] cubeVertexVectors = { 
    new Vector3f(0, 0, 0),
    new Vector3f(1, 0, 0), 
    new Vector3f(1, 0, 1), 
    new Vector3f(0, 0, 1),
    new Vector3f(0, 1, 0), 
    new Vector3f(1, 1, 0), 
    new Vector3f(1, 1, 1),
    new Vector3f(0, 1, 1) };
  
  
  
  
  private static int[] xyPlanePts = new int[] { 
      0, 1, 1, 0, 
      0, 1, 1, 0 
  };
  
  private final int[] edgePointIndexes = new int[12];
  private int[][][] isoPointIndexPlanes;
  private float[][] xyPlanes;

  private int[][] resetIndexPlane(int[][] plane) {
    for (int i = 0; i < yzCount; i++)
      for (int j = 0; j < 3; j++)
        plane[i][j] = -1;
    return plane;
  }
  
  public String getEdgeData() {

    
    
    
    
    edgeCount = 0;
    calculatedArea = 0;
    calculatedVolume = 0;
    if (doSaveSurfacePoints)
      surfacePoints.clear();

    int x0, x1, xStep, ptStep, pt, ptX;
    if (isXLowToHigh) {
      x0 = 0;
      x1 = cubeCountX;
      xStep = 1;
      ptStep = yzCount;
      pt = ptX = (yzCount - 1) - nZ - 1;
      
      
    } else {
      x0 = cubeCountX - 1;
      x1 = -1;
      xStep = -1;
      ptStep = -yzCount;
      pt = ptX = (cubeCountX * yzCount - 1) - nZ - 1;
      
      
    }
    int cellIndex0 = cubeCountY * cubeCountZ - 1;
    int cellIndex = cellIndex0;
    resetIndexPlane(isoPointIndexPlanes[1]);
    for (int x = x0; x != x1; x += xStep, ptX += ptStep, pt = ptX, cellIndex = cellIndex0) {
      
      
      
      
      if (mode == MODE_GETXYZ) {
        float[] plane = xyPlanes[0];
        xyPlanes[0] = xyPlanes[1];
        xyPlanes[1] = plane;
      }
      
      
      
      int[][] indexPlane = isoPointIndexPlanes[0];
      isoPointIndexPlanes[0] = isoPointIndexPlanes[1];
      isoPointIndexPlanes[1] = resetIndexPlane(indexPlane);
      
      

      
      
      for (int y = cubeCountY; --y >= 0; pt--) {
        for (int z = cubeCountZ; --z >= 0; pt--, cellIndex--) {
          
          
          
          
          int insideMask = 0;
          for (int i = 8; --i >= 0;) {
            
            
            
            
            boolean isInside;
            Point3i offset = cubeVertexOffsets[i];
            int pti = pt + linearOffsets[i];
            switch (mode) {
            case MODE_GETXYZ:
              vertexValues[i] = getValue(i, x + offset.x, y + offset.y, z
                  + offset.z, pti, xyPlanes[xyPlanePts[i]]);
              isInside = bsVoxels.get(pti);
              break;
            case MODE_BITSET:
              isInside = bsVoxels.get(pti);
              vertexValues[i] = (isInside ? 1 : 0);
              break;
            default:
            case MODE_CUBE:
              vertexValues[i] = volumeData.voxelData[x + offset.x][y + offset.y][z
                  + offset.z];
              isInside = isInside(vertexValues[i], cutoff, isCutoffAbsolute);
              if (isInside)
                bsVoxels.set(pti);
            }
            if (isInside) {
              insideMask |= 1 << i;
            }
          }

          if (insideMask == 0) {
            continue;
          }
          if (insideMask == 0xFF) {
            continue;
          }
          
          
          if (!processOneCubical(insideMask, x, y, z, pt))
            continue;

          
          
          
          if (!doCalcArea)
            continue;
          byte[] triangles = triangleTable2[insideMask];
          for (int i = triangles.length; (i -= 4) >= 0;)
            addTriangle(triangles[i], triangles[i + 1],
                triangles[i + 2],triangles[i + 3]);
          
        }
      }
    }
    
    return fractionData.toString();
  }
  
  Vector3f vTemp = new Vector3f();
  Vector3f vAC = new Vector3f();
  Vector3f vAB = new Vector3f();

  private void addTriangle(int ia, int ib, int ic, int edgeType) {
    
    
    
    
    
    
   
    Point3f pta = (Point3f) surfacePoints.get(edgePointIndexes[ia]);
    Point3f ptb = (Point3f) surfacePoints.get(edgePointIndexes[ib]);
    Point3f ptc = (Point3f) surfacePoints.get(edgePointIndexes[ic]);
    
    vAB.sub(ptb, pta);
    vAC.sub(ptc, pta);
    vTemp.cross(vAB, vAC);
    float area = vTemp.length() / 2;
    calculatedArea += area;
    
    vAB.set(ptb);
    vAC.set(ptc);
    vTemp.cross(vAB, vAC);
    vAC.set(pta);
    calculatedVolume += vAC.dot(vTemp) / 6;
  }


  public static boolean isInside(float voxelValue, float max, boolean isAbsolute) {
    return ((max > 0 && (isAbsolute ? Math.abs(voxelValue) : voxelValue) >= max) || (max <= 0 && voxelValue <= max));
  }

  BitSet bsValues = new BitSet();

  private float getValue(int i, int x, int y, int z, int pt, float[] tempValues) {
    if (bsValues.get(pt))
      return tempValues[pt % yzCount];
    bsValues.set(pt);
    float value = vdc.getValue(x, y, z);
    tempValues[pt % yzCount] = value;
    
    if (isInside(value, cutoff, isCutoffAbsolute))
      bsVoxels.set(pt);
    return value;
  }

  private final Point3f pt0 = new Point3f();
  private final Point3f pointA = new Point3f();

  private static final int[] Pwr2 = new int[] { 1, 2, 4, 8, 16, 32, 64, 128,
      256, 512, 1024, 2048 };

  private final static int[] edgeVertexPointersLowToHigh = new int[] {
      1, 1, 2, 0, 
      5, 5, 6, 4,
      0, 1, 2, 3
  };
  
  private final static int[] edgeVertexPointersHighToLow = new int[] {
      0, 1, 3, 0, 
      4, 5, 7, 4,
      0, 1, 2, 3
  };

  private int[] edgeVertexPointers;

  private final static int[] edgeVertexPlanesLowToHigh = new int[] {
      1, 1, 1, 0, 
      1, 1, 1, 0, 
      0, 1, 1, 0
  };  

  private final static int[] edgeVertexPlanesHighToLow = new int[] {
      1, 0, 1, 1,
      1, 0, 1, 1,
      1, 0, 0, 1
  }; 
  
  private int[] edgeVertexPlanes;
  
  private boolean processOneCubical(int insideMask, int x, int y, int z, int pt) {

    
    

    int edgeMask = insideMaskTable[insideMask];
    
    boolean isNaN = false;
    for (int iEdge = 12; --iEdge >= 0;) {
      
      
      
      int xEdge = Pwr2[iEdge];
      if ((edgeMask & xEdge) == 0)
        continue;
      
      
      
      
      
      
      int iPlane = edgeVertexPlanes[iEdge];
      int iPt = (pt + linearOffsets[edgeVertexPointers[iEdge]]) % yzCount;
      int iType = edgeTypeTable[iEdge];
      int index = edgePointIndexes[iEdge] = isoPointIndexPlanes[iPlane][iPt][iType];
      
      if (index >= 0)
        continue; 
      
      
      
      
      
      int vertexA = edgeVertexes[iEdge << 1];
      int vertexB = edgeVertexes[(iEdge << 1) + 1];
      
      
      
      
      float valueA = vertexValues[vertexA];
      float valueB = vertexValues[vertexB];
      
      
      
      if (Float.isNaN(valueA) || Float.isNaN(valueB))
        isNaN = true;
      
      
      
      
      
      
      
      
      
      if (doSaveSurfacePoints) {
        volumeData.voxelPtToXYZ(x, y, z, pt0);
        pointA.add(pt0, voxelVertexVectors[vertexA]);
      }
      float f = (cutoff - valueA) / (valueB - valueA);
      edgePointIndexes[iEdge] = isoPointIndexPlanes[iPlane][iPt][iType] = 
        newVertex(pointA, edgeVectors[iEdge], f);
      
      
      fractionData.append(JvxlCoder.jvxlFractionAsCharacter(f));
      
    }
    return !isNaN;
  }

  private int newVertex(Point3f pointA, Vector3f edgeVector, float f) {
    
    

    if (doSaveSurfacePoints) {
      Point3f pt = new Point3f();
      pt.scaleAdd(f, edgeVector, pointA);
      surfacePoints.addElement(pt);
    }
    return edgeCount++;
  }
  
  private final int[] linearOffsets = new int[8]; 

  
  
  private void setLinearOffsets() {
    linearOffsets[0] = 0;
    linearOffsets[1] = yzCount;
    linearOffsets[2] = yzCount + 1;
    linearOffsets[3] = 1;
    linearOffsets[4] = nZ;
    linearOffsets[5] = yzCount + nZ;
    linearOffsets[6] = yzCount + nZ + 1;
    linearOffsets[7] = nZ + 1;
  }

  public int getLinearOffset(int x, int y, int z, int offset) {
    return x * yzCount + y * nZ + z + linearOffsets[offset];
  }

  private final static Point3i[] cubeVertexOffsets = { 
    new Point3i(0, 0, 0), 
    new Point3i(1, 0, 0), 
    new Point3i(1, 0, 1), 
    new Point3i(0, 0, 1), 
    new Point3i(0, 1, 0), 
    new Point3i(1, 1, 0), 
    new Point3i(1, 1, 1), 
    new Point3i(0, 1, 1)  
  };


  

   private final static int edgeTypeTable[] = { 
     0, 2, 0, 2, 
     0, 2, 0, 2, 
     1, 1, 1, 1 };
  

  private final static byte edgeVertexes[] = { 
    0, 1, 1, 2, 2, 3, 3, 0, 4, 5,
  
    5, 6, 6, 7, 7, 4, 0, 4, 1, 5, 2, 6, 3, 7 };
  

  private final static short insideMaskTable[] = { 0x0000, 0x0109, 0x0203,
      0x030A, 0x0406, 0x050F, 0x0605, 0x070C, 0x080C, 0x0905, 0x0A0F, 0x0B06,
      0x0C0A, 0x0D03, 0x0E09, 0x0F00, 0x0190, 0x0099, 0x0393, 0x029A, 0x0596,
      0x049F, 0x0795, 0x069C, 0x099C, 0x0895, 0x0B9F, 0x0A96, 0x0D9A, 0x0C93,
      0x0F99, 0x0E90, 0x0230, 0x0339, 0x0033, 0x013A, 0x0636, 0x073F, 0x0435,
      0x053C, 0x0A3C, 0x0B35, 0x083F, 0x0936, 0x0E3A, 0x0F33, 0x0C39, 0x0D30,
      0x03A0, 0x02A9, 0x01A3, 0x00AA, 0x07A6, 0x06AF, 0x05A5, 0x04AC, 0x0BAC,
      0x0AA5, 0x09AF, 0x08A6, 0x0FAA, 0x0EA3, 0x0DA9, 0x0CA0, 0x0460, 0x0569,
      0x0663, 0x076A, 0x0066, 0x016F, 0x0265, 0x036C, 0x0C6C, 0x0D65, 0x0E6F,
      0x0F66, 0x086A, 0x0963, 0x0A69, 0x0B60, 0x05F0, 0x04F9, 0x07F3, 0x06FA,
      0x01F6, 0x00FF, 0x03F5, 0x02FC, 0x0DFC, 0x0CF5, 0x0FFF, 0x0EF6, 0x09FA,
      0x08F3, 0x0BF9, 0x0AF0, 0x0650, 0x0759, 0x0453, 0x055A, 0x0256, 0x035F,
      0x0055, 0x015C, 0x0E5C, 0x0F55, 0x0C5F, 0x0D56, 0x0A5A, 0x0B53, 0x0859,
      0x0950, 0x07C0, 0x06C9, 0x05C3, 0x04CA, 0x03C6, 0x02CF, 0x01C5, 0x00CC,
      0x0FCC, 0x0EC5, 0x0DCF, 0x0CC6, 0x0BCA, 0x0AC3, 0x09C9, 0x08C0, 0x08C0,
      0x09C9, 0x0AC3, 0x0BCA, 0x0CC6, 0x0DCF, 0x0EC5, 0x0FCC, 0x00CC, 0x01C5,
      0x02CF, 0x03C6, 0x04CA, 0x05C3, 0x06C9, 0x07C0, 0x0950, 0x0859, 0x0B53,
      0x0A5A, 0x0D56, 0x0C5F, 0x0F55, 0x0E5C, 0x015C, 0x0055, 0x035F, 0x0256,
      0x055A, 0x0453, 0x0759, 0x0650, 0x0AF0, 0x0BF9, 0x08F3, 0x09FA, 0x0EF6,
      0x0FFF, 0x0CF5, 0x0DFC, 0x02FC, 0x03F5, 0x00FF, 0x01F6, 0x06FA, 0x07F3,
      0x04F9, 0x05F0, 0x0B60, 0x0A69, 0x0963, 0x086A, 0x0F66, 0x0E6F, 0x0D65,
      0x0C6C, 0x036C, 0x0265, 0x016F, 0x0066, 0x076A, 0x0663, 0x0569, 0x0460,
      0x0CA0, 0x0DA9, 0x0EA3, 0x0FAA, 0x08A6, 0x09AF, 0x0AA5, 0x0BAC, 0x04AC,
      0x05A5, 0x06AF, 0x07A6, 0x00AA, 0x01A3, 0x02A9, 0x03A0, 0x0D30, 0x0C39,
      0x0F33, 0x0E3A, 0x0936, 0x083F, 0x0B35, 0x0A3C, 0x053C, 0x0435, 0x073F,
      0x0636, 0x013A, 0x0033, 0x0339, 0x0230, 0x0E90, 0x0F99, 0x0C93, 0x0D9A,
      0x0A96, 0x0B9F, 0x0895, 0x099C, 0x069C, 0x0795, 0x049F, 0x0596, 0x029A,
      0x0393, 0x0099, 0x0190, 0x0F00, 0x0E09, 0x0D03, 0x0C0A, 0x0B06, 0x0A0F,
      0x0905, 0x080C, 0x070C, 0x0605, 0x050F, 0x0406, 0x030A, 0x0203, 0x0109,
      0x0000 };

  

  
  
  private final static byte[][] triangleTable2 = { null, { 0, 8, 3, 7 },
      { 0, 1, 9, 7 }, { 1, 8, 3, 6, 9, 8, 1, 5 }, { 1, 2, 10, 7 },
      { 0, 8, 3, 7, 1, 2, 10, 7 }, { 9, 2, 10, 6, 0, 2, 9, 5 },
      { 2, 8, 3, 6, 2, 10, 8, 1, 10, 9, 8, 3 }, { 3, 11, 2, 7 },
      { 0, 11, 2, 6, 8, 11, 0, 5 }, { 1, 9, 0, 7, 2, 3, 11, 7 },
      { 1, 11, 2, 6, 1, 9, 11, 1, 9, 8, 11, 3 }, { 3, 10, 1, 6, 11, 10, 3, 5 },
      { 0, 10, 1, 6, 0, 8, 10, 1, 8, 11, 10, 3 },
      { 3, 9, 0, 6, 3, 11, 9, 1, 11, 10, 9, 3 }, { 9, 8, 10, 5, 10, 8, 11, 6 },
      { 4, 7, 8, 7 }, { 4, 3, 0, 6, 7, 3, 4, 5 }, { 0, 1, 9, 7, 8, 4, 7, 7 },
      { 4, 1, 9, 6, 4, 7, 1, 1, 7, 3, 1, 3 }, { 1, 2, 10, 7, 8, 4, 7, 7 },
      { 3, 4, 7, 6, 3, 0, 4, 3, 1, 2, 10, 7 },
      { 9, 2, 10, 6, 9, 0, 2, 3, 8, 4, 7, 7 },
      { 2, 10, 9, 3, 2, 9, 7, 0, 2, 7, 3, 6, 7, 9, 4, 6 },
      { 8, 4, 7, 7, 3, 11, 2, 7 }, { 11, 4, 7, 6, 11, 2, 4, 1, 2, 0, 4, 3 },
      { 9, 0, 1, 7, 8, 4, 7, 7, 2, 3, 11, 7 },
      { 4, 7, 11, 3, 9, 4, 11, 1, 9, 11, 2, 2, 9, 2, 1, 6 },
      { 3, 10, 1, 6, 3, 11, 10, 3, 7, 8, 4, 7 },
      { 1, 11, 10, 6, 1, 4, 11, 0, 1, 0, 4, 3, 7, 11, 4, 5 },
      { 4, 7, 8, 7, 9, 0, 11, 1, 9, 11, 10, 6, 11, 0, 3, 6 },
      { 4, 7, 11, 3, 4, 11, 9, 4, 9, 11, 10, 6 }, { 9, 5, 4, 7 },
      { 9, 5, 4, 7, 0, 8, 3, 7 }, { 0, 5, 4, 6, 1, 5, 0, 5 },
      { 8, 5, 4, 6, 8, 3, 5, 1, 3, 1, 5, 3 }, { 1, 2, 10, 7, 9, 5, 4, 7 },
      { 3, 0, 8, 7, 1, 2, 10, 7, 4, 9, 5, 7 },
      { 5, 2, 10, 6, 5, 4, 2, 1, 4, 0, 2, 3 },
      { 2, 10, 5, 3, 3, 2, 5, 1, 3, 5, 4, 2, 3, 4, 8, 6 },
      { 9, 5, 4, 7, 2, 3, 11, 7 }, { 0, 11, 2, 6, 0, 8, 11, 3, 4, 9, 5, 7 },
      { 0, 5, 4, 6, 0, 1, 5, 3, 2, 3, 11, 7 },
      { 2, 1, 5, 3, 2, 5, 8, 0, 2, 8, 11, 6, 4, 8, 5, 5 },
      { 10, 3, 11, 6, 10, 1, 3, 3, 9, 5, 4, 7 },
      { 4, 9, 5, 7, 0, 8, 1, 5, 8, 10, 1, 2, 8, 11, 10, 3 },
      { 5, 4, 0, 3, 5, 0, 11, 0, 5, 11, 10, 6, 11, 0, 3, 6 },
      { 5, 4, 8, 3, 5, 8, 10, 4, 10, 8, 11, 6 }, { 9, 7, 8, 6, 5, 7, 9, 5 },
      { 9, 3, 0, 6, 9, 5, 3, 1, 5, 7, 3, 3 },
      { 0, 7, 8, 6, 0, 1, 7, 1, 1, 5, 7, 3 }, { 1, 5, 3, 5, 3, 5, 7, 6 },
      { 9, 7, 8, 6, 9, 5, 7, 3, 10, 1, 2, 7 },
      { 10, 1, 2, 7, 9, 5, 0, 5, 5, 3, 0, 2, 5, 7, 3, 3 },
      { 8, 0, 2, 3, 8, 2, 5, 0, 8, 5, 7, 6, 10, 5, 2, 5 },
      { 2, 10, 5, 3, 2, 5, 3, 4, 3, 5, 7, 6 },
      { 7, 9, 5, 6, 7, 8, 9, 3, 3, 11, 2, 7 },
      { 9, 5, 7, 3, 9, 7, 2, 0, 9, 2, 0, 6, 2, 7, 11, 6 },
      { 2, 3, 11, 7, 0, 1, 8, 5, 1, 7, 8, 2, 1, 5, 7, 3 },
      { 11, 2, 1, 3, 11, 1, 7, 4, 7, 1, 5, 6 },
      { 9, 5, 8, 5, 8, 5, 7, 6, 10, 1, 3, 3, 10, 3, 11, 6 },
      { 5, 7, 0, 1, 5, 0, 9, 6, 7, 11, 0, 1, 1, 0, 10, 5, 11, 10, 0, 1 },
      { 11, 10, 0, 1, 11, 0, 3, 6, 10, 5, 0, 1, 8, 0, 7, 5, 5, 7, 0, 1 },
      { 11, 10, 5, 3, 7, 11, 5, 5 }, { 10, 6, 5, 7 },
      { 0, 8, 3, 7, 5, 10, 6, 7 }, { 9, 0, 1, 7, 5, 10, 6, 7 },
      { 1, 8, 3, 6, 1, 9, 8, 3, 5, 10, 6, 7 }, { 1, 6, 5, 6, 2, 6, 1, 5 },
      { 1, 6, 5, 6, 1, 2, 6, 3, 3, 0, 8, 7 },
      { 9, 6, 5, 6, 9, 0, 6, 1, 0, 2, 6, 3 },
      { 5, 9, 8, 3, 5, 8, 2, 0, 5, 2, 6, 6, 3, 2, 8, 5 },
      { 2, 3, 11, 7, 10, 6, 5, 7 }, { 11, 0, 8, 6, 11, 2, 0, 3, 10, 6, 5, 7 },
      { 0, 1, 9, 7, 2, 3, 11, 7, 5, 10, 6, 7 },
      { 5, 10, 6, 7, 1, 9, 2, 5, 9, 11, 2, 2, 9, 8, 11, 3 },
      { 6, 3, 11, 6, 6, 5, 3, 1, 5, 1, 3, 3 },
      { 0, 8, 11, 3, 0, 11, 5, 0, 0, 5, 1, 6, 5, 11, 6, 6 },
      { 3, 11, 6, 3, 0, 3, 6, 1, 0, 6, 5, 2, 0, 5, 9, 6 },
      { 6, 5, 9, 3, 6, 9, 11, 4, 11, 9, 8, 6 }, { 5, 10, 6, 7, 4, 7, 8, 7 },
      { 4, 3, 0, 6, 4, 7, 3, 3, 6, 5, 10, 7 },
      { 1, 9, 0, 7, 5, 10, 6, 7, 8, 4, 7, 7 },
      { 10, 6, 5, 7, 1, 9, 7, 1, 1, 7, 3, 6, 7, 9, 4, 6 },
      { 6, 1, 2, 6, 6, 5, 1, 3, 4, 7, 8, 7 },
      { 1, 2, 5, 5, 5, 2, 6, 6, 3, 0, 4, 3, 3, 4, 7, 6 },
      { 8, 4, 7, 7, 9, 0, 5, 5, 0, 6, 5, 2, 0, 2, 6, 3 },
      { 7, 3, 9, 1, 7, 9, 4, 6, 3, 2, 9, 1, 5, 9, 6, 5, 2, 6, 9, 1 },
      { 3, 11, 2, 7, 7, 8, 4, 7, 10, 6, 5, 7 },
      { 5, 10, 6, 7, 4, 7, 2, 1, 4, 2, 0, 6, 2, 7, 11, 6 },
      { 0, 1, 9, 7, 4, 7, 8, 7, 2, 3, 11, 7, 5, 10, 6, 7 },
      { 9, 2, 1, 6, 9, 11, 2, 2, 9, 4, 11, 1, 7, 11, 4, 5, 5, 10, 6, 7 },
      { 8, 4, 7, 7, 3, 11, 5, 1, 3, 5, 1, 6, 5, 11, 6, 6 },
      { 5, 1, 11, 1, 5, 11, 6, 6, 1, 0, 11, 1, 7, 11, 4, 5, 0, 4, 11, 1 },
      { 0, 5, 9, 6, 0, 6, 5, 2, 0, 3, 6, 1, 11, 6, 3, 5, 8, 4, 7, 7 },
      { 6, 5, 9, 3, 6, 9, 11, 4, 4, 7, 9, 5, 7, 11, 9, 1 },
      { 10, 4, 9, 6, 6, 4, 10, 5 }, { 4, 10, 6, 6, 4, 9, 10, 3, 0, 8, 3, 7 },
      { 10, 0, 1, 6, 10, 6, 0, 1, 6, 4, 0, 3 },
      { 8, 3, 1, 3, 8, 1, 6, 0, 8, 6, 4, 6, 6, 1, 10, 6 },
      { 1, 4, 9, 6, 1, 2, 4, 1, 2, 6, 4, 3 },
      { 3, 0, 8, 7, 1, 2, 9, 5, 2, 4, 9, 2, 2, 6, 4, 3 },
      { 0, 2, 4, 5, 4, 2, 6, 6 }, { 8, 3, 2, 3, 8, 2, 4, 4, 4, 2, 6, 6 },
      { 10, 4, 9, 6, 10, 6, 4, 3, 11, 2, 3, 7 },
      { 0, 8, 2, 5, 2, 8, 11, 6, 4, 9, 10, 3, 4, 10, 6, 6 },
      { 3, 11, 2, 7, 0, 1, 6, 1, 0, 6, 4, 6, 6, 1, 10, 6 },
      { 6, 4, 1, 1, 6, 1, 10, 6, 4, 8, 1, 1, 2, 1, 11, 5, 8, 11, 1, 1 },
      { 9, 6, 4, 6, 9, 3, 6, 0, 9, 1, 3, 3, 11, 6, 3, 5 },
      { 8, 11, 1, 1, 8, 1, 0, 6, 11, 6, 1, 1, 9, 1, 4, 5, 6, 4, 1, 1 },
      { 3, 11, 6, 3, 3, 6, 0, 4, 0, 6, 4, 6 }, { 6, 4, 8, 3, 11, 6, 8, 5 },
      { 7, 10, 6, 6, 7, 8, 10, 1, 8, 9, 10, 3 },
      { 0, 7, 3, 6, 0, 10, 7, 0, 0, 9, 10, 3, 6, 7, 10, 5 },
      { 10, 6, 7, 3, 1, 10, 7, 1, 1, 7, 8, 2, 1, 8, 0, 6 },
      { 10, 6, 7, 3, 10, 7, 1, 4, 1, 7, 3, 6 },
      { 1, 2, 6, 3, 1, 6, 8, 0, 1, 8, 9, 6, 8, 6, 7, 6 },
      { 2, 6, 9, 1, 2, 9, 1, 6, 6, 7, 9, 1, 0, 9, 3, 5, 7, 3, 9, 1 },
      { 7, 8, 0, 3, 7, 0, 6, 4, 6, 0, 2, 6 }, { 7, 3, 2, 3, 6, 7, 2, 5 },
      { 2, 3, 11, 7, 10, 6, 8, 1, 10, 8, 9, 6, 8, 6, 7, 6 },
      { 2, 0, 7, 1, 2, 7, 11, 6, 0, 9, 7, 1, 6, 7, 10, 5, 9, 10, 7, 1 },
      { 1, 8, 0, 6, 1, 7, 8, 2, 1, 10, 7, 1, 6, 7, 10, 5, 2, 3, 11, 7 },
      { 11, 2, 1, 3, 11, 1, 7, 4, 10, 6, 1, 5, 6, 7, 1, 1 },
      { 8, 9, 6, 1, 8, 6, 7, 6, 9, 1, 6, 1, 11, 6, 3, 5, 1, 3, 6, 1 },
      { 0, 9, 1, 7, 11, 6, 7, 7 },
      { 7, 8, 0, 3, 7, 0, 6, 4, 3, 11, 0, 5, 11, 6, 0, 1 }, { 7, 11, 6, 7 },
      { 7, 6, 11, 7 }, { 3, 0, 8, 7, 11, 7, 6, 7 },
      { 0, 1, 9, 7, 11, 7, 6, 7 }, { 8, 1, 9, 6, 8, 3, 1, 3, 11, 7, 6, 7 },
      { 10, 1, 2, 7, 6, 11, 7, 7 }, { 1, 2, 10, 7, 3, 0, 8, 7, 6, 11, 7, 7 },
      { 2, 9, 0, 6, 2, 10, 9, 3, 6, 11, 7, 7 },
      { 6, 11, 7, 7, 2, 10, 3, 5, 10, 8, 3, 2, 10, 9, 8, 3 },
      { 7, 2, 3, 6, 6, 2, 7, 5 }, { 7, 0, 8, 6, 7, 6, 0, 1, 6, 2, 0, 3 },
      { 2, 7, 6, 6, 2, 3, 7, 3, 0, 1, 9, 7 },
      { 1, 6, 2, 6, 1, 8, 6, 0, 1, 9, 8, 3, 8, 7, 6, 3 },
      { 10, 7, 6, 6, 10, 1, 7, 1, 1, 3, 7, 3 },
      { 10, 7, 6, 6, 1, 7, 10, 4, 1, 8, 7, 2, 1, 0, 8, 3 },
      { 0, 3, 7, 3, 0, 7, 10, 0, 0, 10, 9, 6, 6, 10, 7, 5 },
      { 7, 6, 10, 3, 7, 10, 8, 4, 8, 10, 9, 6 }, { 6, 8, 4, 6, 11, 8, 6, 5 },
      { 3, 6, 11, 6, 3, 0, 6, 1, 0, 4, 6, 3 },
      { 8, 6, 11, 6, 8, 4, 6, 3, 9, 0, 1, 7 },
      { 9, 4, 6, 3, 9, 6, 3, 0, 9, 3, 1, 6, 11, 3, 6, 5 },
      { 6, 8, 4, 6, 6, 11, 8, 3, 2, 10, 1, 7 },
      { 1, 2, 10, 7, 3, 0, 11, 5, 0, 6, 11, 2, 0, 4, 6, 3 },
      { 4, 11, 8, 6, 4, 6, 11, 3, 0, 2, 9, 5, 2, 10, 9, 3 },
      { 10, 9, 3, 1, 10, 3, 2, 6, 9, 4, 3, 1, 11, 3, 6, 5, 4, 6, 3, 1 },
      { 8, 2, 3, 6, 8, 4, 2, 1, 4, 6, 2, 3 }, { 0, 4, 2, 5, 4, 6, 2, 3 },
      { 1, 9, 0, 7, 2, 3, 4, 1, 2, 4, 6, 6, 4, 3, 8, 6 },
      { 1, 9, 4, 3, 1, 4, 2, 4, 2, 4, 6, 6 },
      { 8, 1, 3, 6, 8, 6, 1, 0, 8, 4, 6, 3, 6, 10, 1, 3 },
      { 10, 1, 0, 3, 10, 0, 6, 4, 6, 0, 4, 6 },
      { 4, 6, 3, 1, 4, 3, 8, 6, 6, 10, 3, 1, 0, 3, 9, 5, 10, 9, 3, 1 },
      { 10, 9, 4, 3, 6, 10, 4, 5 }, { 4, 9, 5, 7, 7, 6, 11, 7 },
      { 0, 8, 3, 7, 4, 9, 5, 7, 11, 7, 6, 7 },
      { 5, 0, 1, 6, 5, 4, 0, 3, 7, 6, 11, 7 },
      { 11, 7, 6, 7, 8, 3, 4, 5, 3, 5, 4, 2, 3, 1, 5, 3 },
      { 9, 5, 4, 7, 10, 1, 2, 7, 7, 6, 11, 7 },
      { 6, 11, 7, 7, 1, 2, 10, 7, 0, 8, 3, 7, 4, 9, 5, 7 },
      { 7, 6, 11, 7, 5, 4, 10, 5, 4, 2, 10, 2, 4, 0, 2, 3 },
      { 3, 4, 8, 6, 3, 5, 4, 2, 3, 2, 5, 1, 10, 5, 2, 5, 11, 7, 6, 7 },
      { 7, 2, 3, 6, 7, 6, 2, 3, 5, 4, 9, 7 },
      { 9, 5, 4, 7, 0, 8, 6, 1, 0, 6, 2, 6, 6, 8, 7, 6 },
      { 3, 6, 2, 6, 3, 7, 6, 3, 1, 5, 0, 5, 5, 4, 0, 3 },
      { 6, 2, 8, 1, 6, 8, 7, 6, 2, 1, 8, 1, 4, 8, 5, 5, 1, 5, 8, 1 },
      { 9, 5, 4, 7, 10, 1, 6, 5, 1, 7, 6, 2, 1, 3, 7, 3 },
      { 1, 6, 10, 6, 1, 7, 6, 2, 1, 0, 7, 1, 8, 7, 0, 5, 9, 5, 4, 7 },
      { 4, 0, 10, 1, 4, 10, 5, 6, 0, 3, 10, 1, 6, 10, 7, 5, 3, 7, 10, 1 },
      { 7, 6, 10, 3, 7, 10, 8, 4, 5, 4, 10, 5, 4, 8, 10, 1 },
      { 6, 9, 5, 6, 6, 11, 9, 1, 11, 8, 9, 3 },
      { 3, 6, 11, 6, 0, 6, 3, 4, 0, 5, 6, 2, 0, 9, 5, 3 },
      { 0, 11, 8, 6, 0, 5, 11, 0, 0, 1, 5, 3, 5, 6, 11, 3 },
      { 6, 11, 3, 3, 6, 3, 5, 4, 5, 3, 1, 6 },
      { 1, 2, 10, 7, 9, 5, 11, 1, 9, 11, 8, 6, 11, 5, 6, 6 },
      { 0, 11, 3, 6, 0, 6, 11, 2, 0, 9, 6, 1, 5, 6, 9, 5, 1, 2, 10, 7 },
      { 11, 8, 5, 1, 11, 5, 6, 6, 8, 0, 5, 1, 10, 5, 2, 5, 0, 2, 5, 1 },
      { 6, 11, 3, 3, 6, 3, 5, 4, 2, 10, 3, 5, 10, 5, 3, 1 },
      { 5, 8, 9, 6, 5, 2, 8, 0, 5, 6, 2, 3, 3, 8, 2, 5 },
      { 9, 5, 6, 3, 9, 6, 0, 4, 0, 6, 2, 6 },
      { 1, 5, 8, 1, 1, 8, 0, 6, 5, 6, 8, 1, 3, 8, 2, 5, 6, 2, 8, 1 },
      { 1, 5, 6, 3, 2, 1, 6, 5 },
      { 1, 3, 6, 1, 1, 6, 10, 6, 3, 8, 6, 1, 5, 6, 9, 5, 8, 9, 6, 1 },
      { 10, 1, 0, 3, 10, 0, 6, 4, 9, 5, 0, 5, 5, 6, 0, 1 },
      { 0, 3, 8, 7, 5, 6, 10, 7 }, { 10, 5, 6, 7 },
      { 11, 5, 10, 6, 7, 5, 11, 5 }, { 11, 5, 10, 6, 11, 7, 5, 3, 8, 3, 0, 7 },
      { 5, 11, 7, 6, 5, 10, 11, 3, 1, 9, 0, 7 },
      { 10, 7, 5, 6, 10, 11, 7, 3, 9, 8, 1, 5, 8, 3, 1, 3 },
      { 11, 1, 2, 6, 11, 7, 1, 1, 7, 5, 1, 3 },
      { 0, 8, 3, 7, 1, 2, 7, 1, 1, 7, 5, 6, 7, 2, 11, 6 },
      { 9, 7, 5, 6, 9, 2, 7, 0, 9, 0, 2, 3, 2, 11, 7, 3 },
      { 7, 5, 2, 1, 7, 2, 11, 6, 5, 9, 2, 1, 3, 2, 8, 5, 9, 8, 2, 1 },
      { 2, 5, 10, 6, 2, 3, 5, 1, 3, 7, 5, 3 },
      { 8, 2, 0, 6, 8, 5, 2, 0, 8, 7, 5, 3, 10, 2, 5, 5 },
      { 9, 0, 1, 7, 5, 10, 3, 1, 5, 3, 7, 6, 3, 10, 2, 6 },
      { 9, 8, 2, 1, 9, 2, 1, 6, 8, 7, 2, 1, 10, 2, 5, 5, 7, 5, 2, 1 },
      { 1, 3, 5, 5, 3, 7, 5, 3 }, { 0, 8, 7, 3, 0, 7, 1, 4, 1, 7, 5, 6 },
      { 9, 0, 3, 3, 9, 3, 5, 4, 5, 3, 7, 6 }, { 9, 8, 7, 3, 5, 9, 7, 5 },
      { 5, 8, 4, 6, 5, 10, 8, 1, 10, 11, 8, 3 },
      { 5, 0, 4, 6, 5, 11, 0, 0, 5, 10, 11, 3, 11, 3, 0, 3 },
      { 0, 1, 9, 7, 8, 4, 10, 1, 8, 10, 11, 6, 10, 4, 5, 6 },
      { 10, 11, 4, 1, 10, 4, 5, 6, 11, 3, 4, 1, 9, 4, 1, 5, 3, 1, 4, 1 },
      { 2, 5, 1, 6, 2, 8, 5, 0, 2, 11, 8, 3, 4, 5, 8, 5 },
      { 0, 4, 11, 1, 0, 11, 3, 6, 4, 5, 11, 1, 2, 11, 1, 5, 5, 1, 11, 1 },
      { 0, 2, 5, 1, 0, 5, 9, 6, 2, 11, 5, 1, 4, 5, 8, 5, 11, 8, 5, 1 },
      { 9, 4, 5, 7, 2, 11, 3, 7 },
      { 2, 5, 10, 6, 3, 5, 2, 4, 3, 4, 5, 2, 3, 8, 4, 3 },
      { 5, 10, 2, 3, 5, 2, 4, 4, 4, 2, 0, 6 },
      { 3, 10, 2, 6, 3, 5, 10, 2, 3, 8, 5, 1, 4, 5, 8, 5, 0, 1, 9, 7 },
      { 5, 10, 2, 3, 5, 2, 4, 4, 1, 9, 2, 5, 9, 4, 2, 1 },
      { 8, 4, 5, 3, 8, 5, 3, 4, 3, 5, 1, 6 }, { 0, 4, 5, 3, 1, 0, 5, 5 },
      { 8, 4, 5, 3, 8, 5, 3, 4, 9, 0, 5, 5, 0, 3, 5, 1 }, { 9, 4, 5, 7 },
      { 4, 11, 7, 6, 4, 9, 11, 1, 9, 10, 11, 3 },
      { 0, 8, 3, 7, 4, 9, 7, 5, 9, 11, 7, 2, 9, 10, 11, 3 },
      { 1, 10, 11, 3, 1, 11, 4, 0, 1, 4, 0, 6, 7, 4, 11, 5 },
      { 3, 1, 4, 1, 3, 4, 8, 6, 1, 10, 4, 1, 7, 4, 11, 5, 10, 11, 4, 1 },
      { 4, 11, 7, 6, 9, 11, 4, 4, 9, 2, 11, 2, 9, 1, 2, 3 },
      { 9, 7, 4, 6, 9, 11, 7, 2, 9, 1, 11, 1, 2, 11, 1, 5, 0, 8, 3, 7 },
      { 11, 7, 4, 3, 11, 4, 2, 4, 2, 4, 0, 6 },
      { 11, 7, 4, 3, 11, 4, 2, 4, 8, 3, 4, 5, 3, 2, 4, 1 },
      { 2, 9, 10, 6, 2, 7, 9, 0, 2, 3, 7, 3, 7, 4, 9, 3 },
      { 9, 10, 7, 1, 9, 7, 4, 6, 10, 2, 7, 1, 8, 7, 0, 5, 2, 0, 7, 1 },
      { 3, 7, 10, 1, 3, 10, 2, 6, 7, 4, 10, 1, 1, 10, 0, 5, 4, 0, 10, 1 },
      { 1, 10, 2, 7, 8, 7, 4, 7 }, { 4, 9, 1, 3, 4, 1, 7, 4, 7, 1, 3, 6 },
      { 4, 9, 1, 3, 4, 1, 7, 4, 0, 8, 1, 5, 8, 7, 1, 1 },
      { 4, 0, 3, 3, 7, 4, 3, 5 }, { 4, 8, 7, 7 },
      { 9, 10, 8, 5, 10, 11, 8, 3 }, { 3, 0, 9, 3, 3, 9, 11, 4, 11, 9, 10, 6 },
      { 0, 1, 10, 3, 0, 10, 8, 4, 8, 10, 11, 6 },
      { 3, 1, 10, 3, 11, 3, 10, 5 }, { 1, 2, 11, 3, 1, 11, 9, 4, 9, 11, 8, 6 },
      { 3, 0, 9, 3, 3, 9, 11, 4, 1, 2, 9, 5, 2, 11, 9, 1 },
      { 0, 2, 11, 3, 8, 0, 11, 5 }, { 3, 2, 11, 7 },
      { 2, 3, 8, 3, 2, 8, 10, 4, 10, 8, 9, 6 }, { 9, 10, 2, 3, 0, 9, 2, 5 },
      { 2, 3, 8, 3, 2, 8, 10, 4, 0, 1, 8, 5, 1, 10, 8, 1 }, { 1, 10, 2, 7 },
      { 1, 3, 8, 3, 9, 1, 8, 5 }, { 0, 9, 1, 7 }, { 0, 3, 8, 7 }, null };

}
