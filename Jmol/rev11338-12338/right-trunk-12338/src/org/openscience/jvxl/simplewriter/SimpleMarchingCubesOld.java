
package org.openscience.jvxl.simplewriter;

import java.util.BitSet;

import javax.vecmath.Point3i;

import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.data.VolumeData;
import org.jmol.util.Logger;

public class SimpleMarchingCubesOld {

  

  private VolumeData volumeData;
  private float cutoff;
  private boolean isCutoffAbsolute;
  private boolean isXLowToHigh;
  private StringBuffer fractionData = new StringBuffer();

  private int cubeCountX, cubeCountY, cubeCountZ;
  private int nY, nZ;

  private BitSet bsVoxels = new BitSet();

  public BitSet getBsVoxels() {
    return bsVoxels;
  }
  
  private int mode;
  private final static int MODE_CUBE = 1;
  private final static int MODE_BITSET = 2;
  private final static int MODE_GETXYZ = 3;

  private VoxelDataCreator vdc;
  
  public SimpleMarchingCubesOld(VoxelDataCreator vdc, VolumeData volumeData, float cutoff,
      boolean isCutoffAbsolute ,   boolean isXLowToHigh) {
    
    
    
    
    
    
    
    this.vdc = vdc;
    this.volumeData = volumeData;
    this.cutoff = cutoff;
    this.isCutoffAbsolute = isCutoffAbsolute;
    this.isXLowToHigh = isXLowToHigh;
    
    if (vdc == null) {
      mode = MODE_CUBE;
    } else {
      mode = MODE_GETXYZ;
    }

    cubeCountX = volumeData.voxelCounts[0] - 1;
    cubeCountY = (nY = volumeData.voxelCounts[1]) - 1;
    cubeCountZ = (nZ = volumeData.voxelCounts[2]) - 1;
    yzCount = nY * nZ;
    setLinearOffsets();
  }

  private final float[] vertexValues = new float[8];
  private final Point3i[] vertexPoints = new Point3i[8];
  {
    for (int i = 8; --i >= 0;)
      vertexPoints[i] = new Point3i();
  }

  int edgeCount;

  
  
  private static int[] xyPlanePts = new int[] { 0, 1, 1, 0, 0, 1, 1, 0 };

  public String getEdgeData() {

    Logger.startTimer();
    
    
    
    
    
    
    int[][] isoPointIndexes = new int[cubeCountY * cubeCountZ][12];

    float[][] xyPlanes = (mode == MODE_GETXYZ ? new float[2][yzCount] : null);

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
    for (int x = x0; x != x1; x += xStep, ptX += ptStep, pt = ptX, cellIndex = cellIndex0) {
      if (mode == MODE_GETXYZ) {
        float[] plane = xyPlanes[0];
        xyPlanes[0] = xyPlanes[1];
        xyPlanes[1] = plane;
      }
      for (int y = cubeCountY; --y >= 0; pt--) {
        for (int z = cubeCountZ; --z >= 0; pt--, cellIndex--) {

          
          
          
          int[] voxelPointIndexes = propagateNeighborPointIndexes(x, y, z, pt,
              isoPointIndexes, cellIndex);
          
          
          
          
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
          
          
          processOneCubical(insideMask, voxelPointIndexes, x, y, z, pt);
        }
      }
    }
    Logger.checkTimer("old getEdgeData");
    return fractionData.toString();
  }
  
  public static boolean isInside(float voxelValue, float max, boolean isAbsolute) {
    return ((max > 0 && (isAbsolute ? Math.abs(voxelValue) : voxelValue) >= max) || (max <= 0 && voxelValue <= max));
  }

  BitSet bsValues = new BitSet();

  private float getValue(int i, int x, int y, int z, int pt, float[] tempValues) {
    
      
    bsValues.set(pt);
    float value = vdc.getValue(x, y, z);
    tempValues[pt % yzCount] = value;
    
    if (isInside(value, cutoff, isCutoffAbsolute))
      bsVoxels.set(pt);
    return value;
  }

  private final int[] nullNeighbor = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
      -1, -1 };

  private int[] propagateNeighborPointIndexes(int x, int y, int z, int pt,
                                              int[][] isoPointIndexes,
                                              int cellIndex) {
    

    

    
    int[] voxelPointIndexes = isoPointIndexes[cellIndex];

    boolean noYNeighbor = (y == cubeCountY - 1);
    int[] yNeighbor = noYNeighbor ? nullNeighbor 
        : isoPointIndexes[cellIndex + cubeCountZ];
    boolean noZNeighbor = (z == cubeCountZ - 1);
    int[] zNeighbor = noZNeighbor ? nullNeighbor
        : isoPointIndexes[cellIndex + 1];
    voxelPointIndexes[0] = -1;
    voxelPointIndexes[2] = zNeighbor[0];
    voxelPointIndexes[4] = yNeighbor[0];
    voxelPointIndexes[6] = (noYNeighbor ? zNeighbor[4] : yNeighbor[2]);

    if (isXLowToHigh) {
      
      if (x == 0) {
        voxelPointIndexes[3] = -1;
        voxelPointIndexes[8] = -1;
        voxelPointIndexes[7] = yNeighbor[3];
        voxelPointIndexes[11] = zNeighbor[8];
      } else {
        voxelPointIndexes[3] = voxelPointIndexes[1];
        voxelPointIndexes[7] = voxelPointIndexes[5];
        voxelPointIndexes[8] = voxelPointIndexes[9];
        voxelPointIndexes[11] = voxelPointIndexes[10];
      }
      voxelPointIndexes[1] = -1;
      voxelPointIndexes[5] = yNeighbor[1];
      voxelPointIndexes[9] = -1;
      voxelPointIndexes[10] = zNeighbor[9];
    } else {
      
      if (x == cubeCountX - 1) {
        voxelPointIndexes[1] = -1;
        voxelPointIndexes[5] = yNeighbor[1];
        voxelPointIndexes[9] = -1;
        voxelPointIndexes[10] = zNeighbor[9];
      } else {
        voxelPointIndexes[1] = voxelPointIndexes[3];
        voxelPointIndexes[5] = voxelPointIndexes[7];
        voxelPointIndexes[9] = voxelPointIndexes[8];
        voxelPointIndexes[10] = voxelPointIndexes[11];
      }
      voxelPointIndexes[3] = -1;
      voxelPointIndexes[7] = yNeighbor[3];
      voxelPointIndexes[8] = -1;
      voxelPointIndexes[11] = zNeighbor[8];
    }

    return voxelPointIndexes;
  }
  
  private static final int[] Pwr2 = new int[] { 1, 2, 4, 8, 16, 32, 64, 128,
    256, 512, 1024, 2048 };

  private boolean processOneCubical(int insideMask, int[] voxelPointIndexes,
                                    int x, int y, int z, int pt) {
    
    
    
    
    int edgeMask = insideMaskTable[insideMask];
    
    boolean isNaN = false;
    for (int iEdge = 12; --iEdge >= 0;) {
      
      
      
      if ((edgeMask & Pwr2[iEdge]) == 0)
        continue;
      
      
      
      
      
      if (voxelPointIndexes[iEdge] >= 0)
        continue; 
      
      
      
      
      
      int vertexA = edgeVertexes[iEdge << 1];
      int vertexB = edgeVertexes[(iEdge << 1) + 1];
      
      
      
      
      float valueA = vertexValues[vertexA];
      float valueB = vertexValues[vertexB];
      
      
      
      if (Float.isNaN(valueA) || Float.isNaN(valueB))
        isNaN = true;
      
      
      
      
      
      
      
      
      
      voxelPointIndexes[iEdge] = edgeCount++;
      
      fractionData.append(JvxlCoder.jvxlFractionAsCharacter((cutoff - valueA) / (valueB - valueA)));
    }
    return !isNaN;
  }

  final static Point3i[] cubeVertexOffsets = { new Point3i(0, 0, 0), 
    new Point3i(1, 0, 0), 
    new Point3i(1, 0, 1), 
    new Point3i(0, 0, 1), 
    new Point3i(0, 1, 0), 
    new Point3i(1, 1, 0), 
    new Point3i(1, 1, 1), 
    new Point3i(0, 1, 1) 
};

private final int[] linearOffsets = new int[8];
int yzCount;


void setLinearOffsets() {
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

}
