
package org.jmol.jvxl.readers;

import java.util.BitSet;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;

import org.jmol.util.Logger;

import org.jmol.jvxl.data.MeshData;
import org.jmol.modelset.AtomIndexIterator;

class IsoSolventReader extends AtomDataReader {

  IsoSolventReader(SurfaceGenerator sg) {
    super(sg);
  }

  

  
  
  

  private float cavityRadius;
  private float envelopeRadius;

  private boolean doCalculateTroughs;
  private boolean isCavity, isPocket;
  private float solventRadius;
  private boolean isProperty;
  private boolean doSmoothProperty;
  
  protected void setup() {
    super.setup();
    cavityRadius = params.cavityRadius;
    envelopeRadius = params.envelopeRadius;
    solventRadius = params.solventRadius;
    point = params.point;

    isCavity = (params.isCavity && meshDataServer != null); 
    isPocket = (params.pocket != null && meshDataServer != null);

    isProperty = (dataType == Parameters.SURFACE_PROPERTY);
    doSmoothProperty = isProperty && params.propertySmoothing;

    doCalculateTroughs = (atomDataServer != null && !isCavity 
        && solventRadius > 0 && (dataType == Parameters.SURFACE_SOLVENT || dataType == Parameters.SURFACE_MOLECULAR));
    doUseIterator = doCalculateTroughs;
    modelIndex = params.modelIndex;
    getAtoms(Float.NaN, false, true);
    if (isCavity || isPocket)
      meshData.dots = meshDataServer.calculateGeodesicSurface(bsMySelected,
          envelopeRadius);

    setHeader("solvent/molecular surface", params.calculationType);
    setRangesAndAddAtoms(params.solvent_ptsPerAngstrom, params.solvent_gridMax,
        params.thePlane != null ? Integer.MAX_VALUE : Math.min(firstNearbyAtom,
            100));
  }

  

  public void selectPocket(boolean doExclude) {
    if (meshDataServer == null)
      return; 
    meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES, null);
    
    Point3f[] v = meshData.vertices;
    int nVertices = meshData.vertexCount;
    float[] vv = meshData.vertexValues;
    Point3f[] dots = meshData.dots;
    int nDots = dots.length;
    for (int i = 0; i < nVertices; i++) {
      for (int j = 0; j < nDots; j++) {
        if (dots[j].distance(v[i]) < envelopeRadius) {
          vv[i] = Float.NaN;
          continue;
        }
      }
    }
    meshData.getSurfaceSet();
    int nSets = meshData.nSets;
    BitSet pocketSet = new BitSet(nSets);
    BitSet ss;
    for (int i = 0; i < nSets; i++)
      if ((ss = meshData.surfaceSet[i]) != null)
        for (int j = ss.length(); --j >= 0;)
          if (ss.get(j) && Float.isNaN(meshData.vertexValues[j])) {
            pocketSet.set(i);
            
            break;
          }
    
    
    
    for (int i = 0; i < nSets; i++)
      if (meshData.surfaceSet[i] != null) {
        if (pocketSet.get(i) == doExclude)
          meshData.invalidateSurfaceSet(i);
      }
    updateSurfaceData();
    if (!doExclude)
      meshData.surfaceSet = null;
    meshDataServer.fillMeshData(meshData, MeshData.MODE_PUT_SETS, null);
    meshData = new MeshData();
  }
  
  
  
  
  
  protected void generateCube() {
    

    volumeData.voxelData = voxelData = new float[nPointsX][nPointsY][nPointsZ];
    if (isCavity && params.theProperty != null) {
      
      return;
    }
    generateSolventCube(true);
    if (isCavity && dataType != Parameters.SURFACE_NOMAP
        && dataType != Parameters.SURFACE_PROPERTY) {
      generateSolventCavity();
      generateSolventCube(false);
    }
    if (params.doCapIsosurface) {
      Logger.info("capping isosurface using " + params.cappingPlane);
      volumeData.capData(params.cappingPlane, params.cutoff);
    }
  }

 private void generateSolventCavity() {
    
    
    
    
    BitSet bs = new BitSet(nPointsX * nPointsY * nPointsZ);
    int i = 0;
    int nDots = meshData.dots.length;
    int n = 0;
    

    
    
    
    
    float d;
    float r2 = envelopeRadius;

    for (int x = 0; x < nPointsX; ++x)
      for (int y = 0; y < nPointsY; ++y) {
        out: for (int z = 0; z < nPointsZ; ++z, ++i)
          if ((d = voxelData[x][y][z]) < Float.MAX_VALUE && d >= cavityRadius) {
            volumeData.voxelPtToXYZ(x, y, z, ptXyzTemp);
            for (int j = 0; j < nDots; j++) {
              if (meshData.dots[j].distance(ptXyzTemp) < r2)
                continue out;
            }
            bs.set(i);
            n++;
          }
      }
    Logger.info("cavities include " + n + " voxel points");
    atomRadius = new float[n];
    atomXyz = new Point3f[n];
    for (int x = 0, ipt = 0, apt = 0; x < nPointsX; ++x)
      for (int y = 0; y < nPointsY; ++y)
        for (int z = 0; z < nPointsZ; ++z)
          if (bs.get(ipt++)) {
            volumeData.voxelPtToXYZ(x, y, z, (atomXyz[apt] = new Point3f()));
            atomRadius[apt++] = voxelData[x][y][z];
          }
    myAtomCount = firstNearbyAtom = n;
  }

  final Point3f ptXyzTemp = new Point3f();

  void generateSolventCube(boolean isFirstPass) {
    float distance = params.distance;
    float rA, rB;
    Point3f ptA;
    Point3f ptY0 = new Point3f(), ptZ0 = new Point3f();
    Point3i pt0 = new Point3i(), pt1 = new Point3i();
    float value = (doSmoothProperty ? Float.NaN : Float.MAX_VALUE);
    if (Logger.debugging)
      Logger.startTimer();
    for (int x = 0; x < nPointsX; ++x)
      for (int y = 0; y < nPointsY; ++y)
        for (int z = 0; z < nPointsZ; ++z)
          voxelData[x][y][z] = value;
    if (dataType == Parameters.SURFACE_NOMAP)
      return;
    int atomCount = myAtomCount;
    float property[][][] = null;
    if (isProperty) {
      atomCount = firstNearbyAtom;
      property = new float[nPointsX][nPointsY][nPointsZ];
      value = (doSmoothProperty ? 0 : Float.NaN);
      for (int x = 0; x < nPointsX; ++x)
        for (int y = 0; y < nPointsY; ++y)
          for (int z = 0; z < nPointsZ; ++z)
            property[x][y][z] = value;
    }
    float maxRadius = 0;
    float r0 = (isFirstPass && isCavity ? cavityRadius : 0);
    boolean isWithin = (isFirstPass && distance != Float.MAX_VALUE && point != null);
    for (int iAtom = 0; iAtom < atomCount; iAtom++) {
      ptA = atomXyz[iAtom];
      rA = atomRadius[iAtom];
      if (rA > maxRadius)
        maxRadius = rA;
      if (isWithin && ptA.distance(point) > distance + rA + 0.5)
        continue;
      boolean isNearby = (iAtom >= firstNearbyAtom);
      setGridLimitsForAtom(ptA, rA + r0, pt0, pt1);
      volumeData.voxelPtToXYZ(pt0.x, pt0.y, pt0.z, ptXyzTemp);
      for (int i = pt0.x; i < pt1.x; i++) {
        ptY0.set(ptXyzTemp);
        for (int j = pt0.y; j < pt1.y; j++) {
          ptZ0.set(ptXyzTemp);
          for (int k = pt0.z; k < pt1.z; k++) {
            float v = ptXyzTemp.distance(ptA) - rA;
            if (doSmoothProperty) {
              v = 1 / (v + rA);
              v *= v;
              v *= v;
              if (Float.isNaN(voxelData[i][j][k]))
                voxelData[i][j][k] = 0;
              if (!Float.isNaN(atomProp[iAtom]))
                property[i][j][k] += atomProp[iAtom] * v;
              voxelData[i][j][k] += v;
            } else if (v < voxelData[i][j][k]) {
              voxelData[i][j][k] = (isNearby || isWithin
                  && ptXyzTemp.distance(point) > distance ? Float.NaN : v);
              if (isProperty)
                property[i][j][k] = atomProp[iAtom];
            }
            ptXyzTemp.add(volumetricVectors[2]);
          }
          ptXyzTemp.set(ptZ0);
          ptXyzTemp.add(volumetricVectors[1]);
        }
        ptXyzTemp.set(ptY0);
        ptXyzTemp.add(volumetricVectors[0]);
      }
    }
    if (isCavity && isFirstPass)
      return;
    if (doCalculateTroughs) {
      Point3i ptA0 = new Point3i();
      Point3i ptB0 = new Point3i();
      Point3i ptA1 = new Point3i();
      Point3i ptB1 = new Point3i();
      for (int iAtom = 0; iAtom < firstNearbyAtom - 1; iAtom++)
        if (atomNo[iAtom] > 0) {
          ptA = atomXyz[iAtom];
          rA = atomRadius[iAtom] + solventRadius;
          int iatomA = atomIndex[iAtom];
          if (isWithin && ptA.distance(point) > distance + rA + 0.5)
            continue;
          setGridLimitsForAtom(ptA, rA - solventRadius, ptA0, ptA1);
          AtomIndexIterator iter = atomDataServer.getWithinAtomSetIterator(
              iatomA, rA + solventRadius + maxRadius, bsMySelected, true, true); 
          while (iter.hasNext()) {
            int iatomB = iter.next();
            Point3f ptB = atomXyz[myIndex[iatomB]];
            rB = atomData.atomRadius[iatomB] + solventRadius;
            if (isWithin && ptB.distance(point) > distance + rB + 0.5)
              continue;
            if (params.thePlane != null
                && Math.abs(volumeData.distancePointToPlane(ptB)) > 2 * rB)
              continue;

            float dAB = ptA.distance(ptB);
            if (dAB >= rA + rB)
              continue;
            
            setGridLimitsForAtom(ptB, rB - solventRadius, ptB0, ptB1);
            pt0.x = Math.min(ptA0.x, ptB0.x);
            pt0.y = Math.min(ptA0.y, ptB0.y);
            pt0.z = Math.min(ptA0.z, ptB0.z);
            pt1.x = Math.max(ptA1.x, ptB1.x);
            pt1.y = Math.max(ptA1.y, ptB1.y);
            pt1.z = Math.max(ptA1.z, ptB1.z);
            volumeData.voxelPtToXYZ(pt0.x, pt0.y, pt0.z, ptXyzTemp);
            for (int i = pt0.x; i < pt1.x; i++) {
              ptY0.set(ptXyzTemp);
              for (int j = pt0.y; j < pt1.y; j++) {
                ptZ0.set(ptXyzTemp);
                for (int k = pt0.z; k < pt1.z; k++) {
                  float dVS = checkSpecialVoxel(ptA, rA, ptB, rB, dAB,
                      ptXyzTemp);
                  if (!Float.isNaN(dVS)) {
                    float v = solventRadius - dVS;
                    if (v < voxelData[i][j][k]) {
                      voxelData[i][j][k] = (isWithin
                          && ptXyzTemp.distance(point) > distance ? Float.NaN
                          : v);
                    }
                  }
                  ptXyzTemp.add(volumetricVectors[2]);
                }
                ptXyzTemp.set(ptZ0);
                ptXyzTemp.add(volumetricVectors[1]);
              }
              ptXyzTemp.set(ptY0);
              ptXyzTemp.add(volumetricVectors[0]);
            }
          }
        }
    }
    if (doSmoothProperty) {
      for (int x = 0; x < nPointsX; ++x)
        for (int y = 0; y < nPointsY; ++y)
          for (int z = 0; z < nPointsZ; ++z)
            if (!Float.isNaN(voxelData[x][y][z]))
                voxelData[x][y][z] = property[x][y][z] / voxelData[x][y][z];
      return;
    } else if (isProperty) {
      volumeData.voxelData = property;
      setVolumeData(volumeData);
      initializeVolumetricData();
    }
    if (params.thePlane == null) {
      for (int x = 0; x < nPointsX; ++x)
        for (int y = 0; y < nPointsY; ++y)
          for (int z = 0; z < nPointsZ; ++z)
            if (voxelData[x][y][z] == Float.MAX_VALUE)
              voxelData[x][y][z] = Float.NaN;
    } else { 
      value = 0.001f;
      for (int x = 0; x < nPointsX; ++x)
        for (int y = 0; y < nPointsY; ++y)
          for (int z = 0; z < nPointsZ; ++z)
            if (voxelData[x][y][z] < value) {
              
            } else {
              voxelData[x][y][z] = value;
            }
    }
    if (Logger.debugging)
      Logger.checkTimer("solvent surface time");
  }

  void setGridLimitsForAtom(Point3f ptA, float rA, Point3i pt0, Point3i pt1) {
    int n = (isProperty ? 4 : 1);
    volumeData.xyzToVoxelPt(ptA.x - rA, ptA.y - rA, ptA.z - rA, pt0);
    pt0.x -= n;
    pt0.y -= n;
    pt0.z -= n;
    if (pt0.x < 0)
      pt0.x = 0;
    if (pt0.y < 0)
      pt0.y = 0;
    if (pt0.z < 0)
      pt0.z = 0;
    volumeData.xyzToVoxelPt(ptA.x + rA, ptA.y + rA, ptA.z + rA, pt1);
    pt1.x += n + 1;
    pt1.y += n + 1;
    pt1.z += n + 1;
    if (pt1.x >= nPointsX)
      pt1.x = nPointsX;
    if (pt1.y >= nPointsY)
      pt1.y = nPointsY;
    if (pt1.z >= nPointsZ)
      pt1.z = nPointsZ;
  }

  final Point3f ptS = new Point3f();

  float checkSpecialVoxel(Point3f ptA, float rAS, Point3f ptB, float rBS,
                          float dAB, Point3f ptV) {
    
    float dAV = ptA.distance(ptV);
    float dBV = ptB.distance(ptV);
    float dVS = Float.NaN;
    float f = rAS / dAV;
    if (f > 1) {
      ptS.set(ptA.x + (ptV.x - ptA.x) * f, ptA.y + (ptV.y - ptA.y) * f, ptA.z
          + (ptV.z - ptA.z) * f);
      if (ptB.distance(ptS) < rBS) {
        dVS = solventDistance(ptV, ptA, ptB, rAS, rBS, dAB, dAV, dBV);
        if (!voxelIsInTrough(dVS, rAS * rAS, rBS, dAB, dAV, dBV))
          return Float.NaN;
      }
      return dVS;
    }
    f = rBS / dBV;
    if (f <= 1)
      return dVS;
    ptS.set(ptB.x + (ptV.x - ptB.x) * f, ptB.y + (ptV.y - ptB.y) * f, ptB.z
        + (ptV.z - ptB.z) * f);
    if (ptA.distance(ptS) < rAS) {
      dVS = solventDistance(ptV, ptB, ptA, rBS, rAS, dAB, dBV, dAV);
      if (!voxelIsInTrough(dVS, rAS * rAS, rBS, dAB, dAV, dBV))
        return Float.NaN;
    }
    return dVS;
  }

  boolean voxelIsInTrough(float dVS, float rAS2, float rBS, float dAB,
                          float dAV, float dBV) {
    
    float cosASBf = (rAS2 + rBS * rBS - dAB * dAB) / rBS; 
    float cosASVf = (rAS2 + dVS * dVS - dAV * dAV) / dVS; 
    return (cosASBf < cosASVf);
  }

  float solventDistance(Point3f ptV, Point3f ptA, Point3f ptB, float rAS,
                        float rBS, float dAB, float dAV, float dBV) {
    double angleVAB = Math.acos((dAV * dAV + dAB * dAB - dBV * dBV)
        / (2 * dAV * dAB));
    double angleBAS = Math.acos((dAB * dAB + rAS * rAS - rBS * rBS)
        / (2 * dAB * rAS));
    float dVS = (float) Math.sqrt(rAS * rAS + dAV * dAV - 2 * rAS * dAV
        * Math.cos(angleBAS - angleVAB));
    return dVS;
  }
}
