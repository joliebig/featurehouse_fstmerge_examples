
package org.openscience.jvxl.simplewriter;

import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.jvxl.calc.MarchingCubes;
import org.jmol.jvxl.data.JvxlData;
import org.jmol.jvxl.data.VolumeData;
import org.jmol.jvxl.readers.Parameters;



public class SimpleMarchingCubes extends MarchingCubes {

  

  private boolean doCalcArea;
  private boolean doSaveSurfacePoints;
  private float calculatedArea = Float.NaN;
  private float calculatedVolume = Float.NaN;
  private Vector surfacePoints;  
  private VoxelDataCreator vdc;


  public SimpleMarchingCubes(VoxelDataCreator vdc, VolumeData volumeData,
      Parameters params, JvxlData jvxlData, 
      Vector surfacePointsReturn, float[] areaVolumeReturn) {

    
    
    
    
    
    
    
    

    
    this.vdc = vdc;
    mode = (vdc == null ? MODE_CUBE : MODE_GETXYZ);
    setParameters(volumeData, params);
    doCalcArea = (areaVolumeReturn != null);
    surfacePoints = surfacePointsReturn;
    if (surfacePoints == null && doCalcArea)
      surfacePoints = new Vector();
    doSaveSurfacePoints = (surfacePoints != null);
    jvxlData.jvxlEdgeData = getEdgeData();
    jvxlData.nPointsX = volumeData.voxelCounts[0];
    jvxlData.nPointsY = volumeData.voxelCounts[1];
    jvxlData.nPointsZ = volumeData.voxelCounts[2];
    jvxlData.setSurfaceInfoFromBitSet(bsVoxels, null);
    if (doCalcArea) {
      areaVolumeReturn[0] = calculatedArea;
      areaVolumeReturn[1] = calculatedVolume;
    }    
  }

  protected float getValue(int i, int x, int y, int z, int pt, float[] tempValues) {
    if (bsValues.get(pt))
      return tempValues[pt % yzCount];
    bsValues.set(pt);
    float value = vdc.getValue(x, y, z);
    tempValues[pt % yzCount] = value;
    if (isInside(value, cutoff, isCutoffAbsolute))
      bsVoxels.set(pt);
    return value;
  }

  protected int newVertex(Point3f pointA, Vector3f edgeVector, float f) {
    
    

    if (doSaveSurfacePoints) {
      Point3f pt = new Point3f();
      pt.scaleAdd(f, edgeVector, pointA);
      surfacePoints.addElement(pt);
    }
    return edgeCount++;
  }
  
  protected void processTriangles(int insideMask) {
    if (doCalcArea)
      super.processTriangles(insideMask);
  }

  private Vector3f vTemp = new Vector3f();
  private Vector3f vAC = new Vector3f();
  private Vector3f vAB = new Vector3f();

  protected void addTriangle(int ia, int ib, int ic, int edgeType) {
    
    
    
    
   
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

}
