



package org.jmol.jvxl.data;

import java.util.Hashtable;

import javax.vecmath.Point3i;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix3f;

import org.jmol.api.VolumeDataInterface;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.XmlUtil;

public class VolumeData implements VolumeDataInterface {

  public final Point3f volumetricOrigin = new Point3f();
  public final float[] origin = new float[3];
  public final Vector3f[] volumetricVectors = new Vector3f[3];
  public final int[] voxelCounts = new int[3];
  public int nPoints;
  public float[][][] voxelData;  
  public Hashtable voxelMap; 
  public final float[] volumetricVectorLengths = new float[3];
  private float maxVectorLength;
  private float minToPlaneDistance;

  public final Vector3f[] unitVolumetricVectors = new Vector3f[3];
  private final Matrix3f volumetricMatrix = new Matrix3f();
  private final Matrix3f inverseMatrix = new Matrix3f();
  private Point4f thePlane;
  private float thePlaneNormalMag;
  private final Point3f ptXyzTemp = new Point3f();
  public String xmlData;
  
  public VolumeData() {
    volumetricVectors[0] = new Vector3f();
    volumetricVectors[1] = new Vector3f();
    volumetricVectors[2] = new Vector3f();
    unitVolumetricVectors[0] = new Vector3f();
    unitVolumetricVectors[1] = new Vector3f();
    unitVolumetricVectors[2] = new Vector3f();
  }

  public void setVolumetricOrigin(float x, float y, float z) {
    volumetricOrigin.set(x, y, z);
  }

  public float[] getOriginFloat() {
    return origin;
  }

  public float[] getVolumetricVectorLengths() {
    return volumetricVectorLengths;
  }
  
  public void setVolumetricVector(int i, float x, float y, float z) {
    volumetricVectors[i].x = x;
    volumetricVectors[i].y = y;
    volumetricVectors[i].z = z;
    setUnitVectors();
  }

  public int[] getVoxelCounts() {
    return voxelCounts;
  }
  
  public int setVoxelCounts(int nPointsX, int nPointsY, int nPointsZ) {
    voxelCounts[0] = nPointsX;
    voxelCounts[1] = nPointsY;
    voxelCounts[2] = nPointsZ;
    return nPoints = nPointsX * nPointsY * nPointsZ;
  }

  public float[][][] getVoxelData() {
    return voxelData;
  }
  
  public void setVoxelData(float[][][] voxelData) {
    this.voxelData = voxelData;
  }

  public Hashtable getVoxelMap() {
    return voxelMap;
  }
  
  public void setVoxelMap(Hashtable voxelMap) {
    this.voxelMap = voxelMap;
  }
  
  public float getVoxelValueFromMap(int x, int y, int z) {
    Float v = (voxelMap == null ? null : (Float) voxelMap.get(x+"_" + y + "_" + z));
    return (v == null ? Float.NaN : v.floatValue());
  }

  public boolean setMatrix() {
    for (int i = 0; i < 3; i++)
      volumetricMatrix.setColumn(i, volumetricVectors[i]);
    try {
      inverseMatrix.invert(volumetricMatrix);
    } catch (Exception e) {
      Logger.error("VolumeData error setting matrix -- bad unit vectors? ");
      return false;
    }
    return true;
  }

  public void transform(Vector3f v1, Vector3f v2) {
    volumetricMatrix.transform(v1, v2);
  }

  public void setPlaneParameters(Point4f plane) {
    thePlane = plane;
    thePlaneNormalMag = (float) Math.sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z);
  }

  public float calcVoxelPlaneDistance(int x, int y, int z) {
    voxelPtToXYZ(x, y, z, ptXyzTemp);
    return (thePlane.x * ptXyzTemp.x + thePlane.y * ptXyzTemp.y + thePlane.z
        * ptXyzTemp.z + thePlane.w)
        / thePlaneNormalMag;
  }

  Point4f mappingPlane;
  public float getToPlaneParameter(Point4f plane) {
    mappingPlane = plane;
    return (float) (Math.sqrt(plane.x * plane.x
        + plane.y * plane.y + plane.z * plane.z) * minToPlaneDistance);
  }
  
  public boolean isNearPlane(int x, int y, int z, Point4f plane, float toPlaneParameter) {
    voxelPtToXYZ(x, y, z, ptXyzTemp);
    return ((thePlane.x * ptXyzTemp.x + thePlane.y * ptXyzTemp.y
        + thePlane.z * ptXyzTemp.z + thePlane.w) < toPlaneParameter);
  }

  public float distancePointToPlane(Point3f pt) {
    return (thePlane.x * pt.x + thePlane.y * pt.y + thePlane.z * pt.z + thePlane.w)
        / thePlaneNormalMag;
  }

  public void voxelPtToXYZ(int x, int y, int z, Point3f pt) {
    pt.scaleAdd(x, volumetricVectors[0], volumetricOrigin);
    pt.scaleAdd(y, volumetricVectors[1], pt);
    pt.scaleAdd(z, volumetricVectors[2], pt);
  }

  public boolean setUnitVectors() {
    maxVectorLength = 0;
    for (int i = 0; i < 3; i++) {
      float d = volumetricVectorLengths[i] = volumetricVectors[i].length();
      if (d == 0)
        return false;
      if (d > maxVectorLength)
        maxVectorLength = d;
      unitVolumetricVectors[i].normalize(volumetricVectors[i]);
    }
    minToPlaneDistance = maxVectorLength * 2;
    origin[0] = volumetricOrigin.x;
    origin[1] = volumetricOrigin.y;
    origin[2] = volumetricOrigin.z;
    return setMatrix();
  }

  public void xyzToVoxelPt(float x, float y, float z, Point3i pt3i) {
    ptXyzTemp.set(x, y, z);
    ptXyzTemp.sub(volumetricOrigin);
    inverseMatrix.transform(ptXyzTemp);
    pt3i.set((int) ptXyzTemp.x, (int) ptXyzTemp.y, (int) ptXyzTemp.z);
  }

  public float lookupInterpolatedVoxelValue(Point3f point) {
    ptXyzTemp.sub(point, volumetricOrigin);
    inverseMatrix.transform(ptXyzTemp);
    return getInterpolatedVoxelValue(ptXyzTemp);
  }

  private float getInterpolatedVoxelValue(Point3f pt) {
    int iMax;
    int xDown = indexDown(pt.x, iMax = voxelCounts[0] - 1);
    int xUp = xDown + (pt.x < 0 || xDown == iMax ? 0 : 1);
    int yDown = indexDown(pt.y, iMax = voxelCounts[1] - 1);
    int yUp = yDown + (pt.y < 0 || yDown == iMax ? 0 : 1);
    int zDown = indexDown(pt.z, iMax = voxelCounts[2] - 1);
    int zUp = zDown + (pt.z < 0 || zDown == iMax ? 0 : 1);
    float v1 = getFractional2DValue(pt.x - xDown, pt.y - yDown,
        getVoxelValue(xDown, yDown, zDown), getVoxelValue(xUp, yDown, zDown),
        getVoxelValue(xDown, yUp, zDown), getVoxelValue(xUp, yUp, zDown));
    float v2 = getFractional2DValue(pt.x - xDown, pt.y - yDown,
        getVoxelValue(xDown, yDown, zUp), getVoxelValue(xUp, yDown, zUp),
        getVoxelValue(xDown, yUp, zUp), getVoxelValue(xUp, yUp, zUp));
    return v1 + (pt.z - zDown) * (v2 - v1);
  }

  public float getVoxelValue(int x, int y, int z) {
    if (voxelMap == null)
      return voxelData[x][y][z];
    Float f = (Float) voxelMap.get(x + "_" + y + "_" + z);
    return (f == null ? Float.NaN : f.floatValue());
  }

  public static float getFractional2DValue(float fx, float fy, float x11,
                                           float x12, float x21, float x22) {
    float v1 = x11 + fx * (x12 - x11);
    float v2 = x21 + fx * (x22 - x21);
    return v1 + fy * (v2 - v1);
  }

  private static int indexDown(float value, int iMax) {
    if (value < 0)
      return 0;
    int floor = (int) value;
    return (floor > iMax ? iMax : floor);
  }

  void offsetCenter(Point3f center) {
    Point3f pt = new Point3f();
    pt.scaleAdd((voxelCounts[0] - 1) / 2f, volumetricVectors[0], pt);
    pt.scaleAdd((voxelCounts[1] - 1) / 2f, volumetricVectors[1], pt);
    pt.scaleAdd((voxelCounts[2] - 1) / 2f, volumetricVectors[2], pt);
    volumetricOrigin.sub(center, pt);
  }

  public void setDataDistanceToPlane(Point4f plane) {
    setPlaneParameters(plane);
    int nx = voxelCounts[0];
    int ny = voxelCounts[1];
    int nz = voxelCounts[2];
    voxelData = new float[nx][ny][nz];
    for (int x = 0; x < nx; x++)
      for (int y = 0; y < ny; y++)
        for (int z = 0; z < nz; z++)
          voxelData[x][y][z] = calcVoxelPlaneDistance(x, y, z);
  }

  public void filterData(boolean isSquared, float invertCutoff) {
    boolean doInvert = (!Float.isNaN(invertCutoff));
    int nx = voxelCounts[0];
    int ny = voxelCounts[1];
    int nz = voxelCounts[2];
    if (isSquared) 
      for (int x = 0; x < nx; x++)
        for (int y = 0; y < ny; y++)
          for (int z = 0; z < nz; z++)
            voxelData[x][y][z] = voxelData[x][y][z] * voxelData[x][y][z];
    if (doInvert) 
      for (int x = 0; x < nx; x++)
        for (int y = 0; y < ny; y++)
          for (int z = 0; z < nz; z++)
            voxelData[x][y][z] = invertCutoff - voxelData[x][y][z];
  }

  public void capData(Point4f plane, float cutoff) {
    if (voxelData == null)
      return;
    int nx = voxelCounts[0];
    int ny = voxelCounts[1];
    int nz = voxelCounts[2];
    Vector3f normal = new Vector3f(plane.x, plane.y, plane.z);
    normal.normalize();
    float f = 1f;
    for (int x = 0; x < nx; x++)
      for (int y = 0; y < ny; y++)
        for (int z = 0; z < nz; z++) {
          float value = voxelData[x][y][z] - cutoff;
          voxelPtToXYZ(x, y, z, ptXyzTemp);
          float d = (ptXyzTemp.x * normal.x + ptXyzTemp.y * normal.y + ptXyzTemp.z * normal.z + plane.w - cutoff) / f;
          if (d >= 0 || d > value)
            voxelData[x][y][z] = d;
        }
  }

  public String setVolumetricXml() {
    StringBuffer sb = new StringBuffer();
    if (voxelCounts[0] == 0) {
      XmlUtil.appendTag(sb, "jvxlVolumeData", null);
    } else {   
      XmlUtil.openTag(sb, "jvxlVolumeData", new String[] {
          "origin", Escape.escape(volumetricOrigin) });
      for (int i = 0; i < 3; i++) 
        XmlUtil.appendTag(sb, "jvxlVolumeVector", new String[] {
            "type", "" + i,
            "count", "" + voxelCounts[i],
            "vector", Escape.escape(volumetricVectors[i]) } );
      XmlUtil.closeTag(sb, "jvxlVolumeData");
    }
    return xmlData = sb.toString();
  }

  
  public void setVoxelMapValue(int x, int y, int z, float v) {
    if (voxelMap == null)
      return;
    voxelMap.put(x+"_" + y + "_" + z, new Float(v));    
  }

}
