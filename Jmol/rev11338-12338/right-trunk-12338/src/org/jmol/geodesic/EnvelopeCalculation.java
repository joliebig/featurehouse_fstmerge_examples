

package org.jmol.geodesic;

import org.jmol.modelset.AtomIndexIterator;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.FastBitSet;
import org.jmol.util.SlowBitSet;

import org.jmol.atomdata.AtomData;
import org.jmol.atomdata.AtomDataServer;
import org.jmol.atomdata.RadiusData;

import java.util.BitSet;
import javax.vecmath.Point3f;





public final class EnvelopeCalculation {

  
  private FastBitSet geodesicMap;
  private FastBitSet mapT;

  
  private short[] mads;
  private AtomData atomData = new AtomData();
  private AtomDataServer viewer;
  private int atomCount;
  
  public EnvelopeCalculation(AtomDataServer viewer, int atomCount, short[] mads, boolean asJavaBitSet) {
    this.viewer = viewer;
    this.atomCount = atomCount; 
    this.mads = mads;
    geodesicCount = Geodesic.getVertexVectorsCount();
    
    if (asJavaBitSet) {
      geodesicMap = SlowBitSet.allocateBitmap(geodesicCount);
      mapT = SlowBitSet.allocateBitmap(geodesicCount);      
    } else {
      geodesicMap = FastBitSet.allocateBitmap(geodesicCount);
      mapT = FastBitSet.allocateBitmap(geodesicCount);
    }

}
   
  public final static float SURFACE_DISTANCE_FOR_CALCULATION = 3f;

  public final static int MAX_LEVEL = Geodesic.standardLevel;
  
  private float maxRadius = 0;
  private boolean modelZeroBased;

  private FastBitSet[] dotsConvexMaps;
  public FastBitSet[] getDotsConvexMaps() {
    return dotsConvexMaps;
  }
  
  private int dotsConvexMax; 
  
  public int getDotsConvexMax() {
    return dotsConvexMax;
  }
  
  public void allocDotsConvexMaps(int max) {
    if (dotsConvexMax >= max)
      return;
    dotsConvexMax = max;
    dotsConvexMaps = new FastBitSet[max];
  }
  
  private int geodesicCount;
  private BitSet bsSurface;
  
  public BitSet getBsSurfaceClone() {
    return BitSetUtil.copy(bsSurface);
  }
  
  private boolean disregardNeighbors = false;
  private BitSet bsMySelected;
  
  public void setMads(short[] mads) {
    this.mads = mads;
  }
  
  public void setFromBits(int index, BitSet bs) {
    geodesicMap.set(0, geodesicCount);
    for (int iDot = geodesicCount; --iDot >= 0;)
      if (!bs.get(iDot))
        geodesicMap.clear(iDot);
    if (dotsConvexMaps == null)
      dotsConvexMaps = new FastBitSet[atomCount];
    FastBitSet map;
    if (geodesicMap.isEmpty())
      map = FastBitSet.getEmptySet();
    else
      map = new FastBitSet(geodesicMap);
    dotsConvexMaps[index] = map;
    dotsConvexMax = Math.max(dotsConvexMax, index);
  }
  
  private float radiusP, diameterP;

  public void newSet() {
    dotsConvexMax = 0;
    dotsConvexMaps = null;
    radiusP = diameterP = 0;
    mads = null;
  }
  
  
  public void calculate(RadiusData rd, float maxRadius, BitSet bsSelected,
                        BitSet bsIgnore, boolean disregardNeighbors,
                        boolean onlySelectedDots, boolean isSurface,
                        boolean multiModel) {
    
    

    atomData.radiusData = rd;
    if (rd.value == Float.MAX_VALUE)
      rd.value = SURFACE_DISTANCE_FOR_CALCULATION;
    atomData.modelIndex = (multiModel ? -1 : 0);
    modelZeroBased = !multiModel;

    viewer.fillAtomData(atomData,
        mads == null ? AtomData.MODE_FILL_COORDS_AND_RADII
            : AtomData.MODE_FILL_COORDS);
    atomCount = atomData.atomCount;
    if (mads != null)
      for (int i = 0; i < atomCount; i++)
        atomData.atomRadius[i] = mads[i] / 1000f;

    bsMySelected = (onlySelectedDots && bsSelected != null ? BitSetUtil
        .copy(bsSelected) : bsIgnore != null ? BitSetUtil.setAll(atomCount)
        : null);
    BitSetUtil.andNot(bsMySelected, bsIgnore);
    this.disregardNeighbors = disregardNeighbors;
    bsSurface = new BitSet();
    this.maxRadius = maxRadius;
    
    boolean isAll = (bsSelected == null);
    int i0 = (isAll ? atomCount - 1 : bsSelected.nextSetBit(0));
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : bsSelected.nextSetBit(i + 1)))
      if (bsIgnore == null || !bsIgnore.get(i)) {
        setAtomI(i);
        getNeighbors();
        calcConvexMap(isSurface);
      }
    currentPoints = null;
    setDotsConvexMax();
  }
  
  public float getRadius(int atomIndex) {
    return atomData.atomRadius[atomIndex];
  }
    
  private Point3f[] currentPoints;
  
  public Point3f[] getPoints() {
    if (dotsConvexMaps == null) {
      calculate(new RadiusData(SURFACE_DISTANCE_FOR_CALCULATION, RadiusData.TYPE_ABSOLUTE, 0),
          Float.MAX_VALUE, bsMySelected, null, false, false, false, false);
    }
    if (currentPoints != null)
      return currentPoints;
    int nPoints = 0;
    int dotCount = 42;
    for (int i = dotsConvexMax; --i >= 0;)
      nPoints += dotsConvexMaps[i].cardinality();
    Point3f[] points = new Point3f[nPoints];
    if (nPoints == 0)
      return points;
    nPoints = 0;
    for (int i = dotsConvexMax; --i >= 0;)
      if (dotsConvexMaps[i] != null) {
        int iDot = dotsConvexMaps[i].size();
        if (iDot > dotCount)
          iDot = dotCount;
        while (--iDot >= 0)
          if (dotsConvexMaps[i].get(iDot)) {
            Point3f pt = new Point3f();
            pt.scaleAdd(atomData.atomRadius[i], Geodesic.getVertexVector(iDot), atomData.atomXyz[i]);
            points[nPoints++] = pt;
          }
      }
    currentPoints = points;
    return points;
  }  
  
  
  
  private void setDotsConvexMax() {
    if (dotsConvexMaps == null)
      dotsConvexMax = 0;
    else {
      int i;
      for (i = atomCount; --i >= 0 && dotsConvexMaps[i] == null;) {
      }
      dotsConvexMax = i + 1;
    }
  }
  
  public float getAppropriateRadius(int atomIndex) {
    return (mads != null ? mads[atomIndex]/1000f : atomData.atomRadius[atomIndex]);
  }

  private int indexI;
  private Point3f centerI;
  private float radiusI;
  private float radiiIP2;
  private final Point3f pointT = new Point3f();

  private void setAtomI(int indexI) {
    this.indexI = indexI;
    centerI = atomData.atomXyz[indexI];
    radiusI = atomData.atomRadius[indexI];
    radiiIP2 = radiusI + radiusP;
    radiiIP2 *= radiiIP2;
  }
  
  private void calcConvexMap(boolean isSurface) {
    if (dotsConvexMaps == null)
      dotsConvexMaps = new FastBitSet[atomCount];
    calcConvexBits();
    FastBitSet map;
    if (geodesicMap.isEmpty())
      map = FastBitSet.getEmptySet();
    else {
      bsSurface.set(indexI);
      if (isSurface) {
        addIncompleteFaces(geodesicMap);
        addIncompleteFaces(geodesicMap);
      }
      map = new FastBitSet(geodesicMap);
    }
    dotsConvexMaps[indexI] = map;
  }
  
  private void addIncompleteFaces(FastBitSet points) {
    mapT.clear();
    short[] faces = Geodesic.getFaceVertexes(MAX_LEVEL);
    int len = faces.length;
    int maxPt = -1;
    for (int f = 0; f < len;) {
      short p1 = faces[f++];
      short p2 = faces[f++];
      short p3 = faces[f++];
      boolean ok1 = points.get(p1); 
      boolean ok2 = points.get(p2); 
      boolean ok3 = points.get(p3);
      if (! (ok1 || ok2 || ok3) || ok1 && ok2 && ok3)
        continue;
      
      
      if (!ok1) {
        mapT.set(p1);
        if (maxPt < p1)
          maxPt = p1;
      }
      if (!ok2) {
        mapT.set(p2);
        if (maxPt < p2)
          maxPt = p2;
      }
      if (!ok3) {
        mapT.set(p3);
        if (maxPt < p3)
          maxPt = p3;
      }
    }
    for (int i=0; i <= maxPt; i++) {
      if (mapT.get(i))
        points.set(i);
    }
  }

  private Point3f centerT;
  
  
  private final Point3f[] vertexTest = new Point3f[12];
  {
    for(int i = 0; i < 12; i++)
      vertexTest[i] = new Point3f();
  }

  private static int[] power4 = {1, 4, 16, 64, 256};
  
  private void calcConvexBits() {
    geodesicMap.set(0, geodesicCount);
    float combinedRadii = radiusI + radiusP;
    if (neighborCount == 0)
      return;
    int faceTest;
    int p1, p2, p3;
    short[] faces = Geodesic.getFaceVertexes(MAX_LEVEL);
    
    int p4 = power4[MAX_LEVEL - 1];
    boolean ok1, ok2, ok3;
    mapT.clear();
    for (int i = 0; i < 12; i++) {
      vertexTest[i].set(Geodesic.getVertexVector(i));
      vertexTest[i].scaleAdd(combinedRadii, centerI);      
    }    
    for (int f = 0; f < 20; f++) {
      faceTest = 0;
      p1 = faces[3 * p4 * (4 * f + 0)];
      p2 = faces[3 * p4 * (4 * f + 1)];
      p3 = faces[3 * p4 * (4 * f + 2)];
      for (int j = 0; j < neighborCount; j++) {
        float maxDist = neighborPlusProbeRadii2[j];
        centerT = neighborCenters[j];
        ok1 = vertexTest[p1].distanceSquared(centerT) >= maxDist;
        ok2 = vertexTest[p2].distanceSquared(centerT) >= maxDist;
        ok3 = vertexTest[p3].distanceSquared(centerT) >= maxDist;
        if (!ok1)
          geodesicMap.clear(p1);
        if (!ok2)
          geodesicMap.clear(p2);
        if (!ok3)
          geodesicMap.clear(p3);
        if (!ok1 && !ok2 && !ok3) {
          faceTest = -1;
          break;
        }
      }
      int kFirst = f * 12 * p4;
      int kLast = kFirst + 12 * p4;
      for (int k = kFirst; k < kLast; k++) {
        int vect = faces[k];
        if (mapT.get(vect) || !geodesicMap.get(vect))
            continue;
        switch (faceTest) {
        case -1:
          
          geodesicMap.clear(vect);
          break;
        case 0:
          
          for (int j = 0; j < neighborCount; j++) {
            float maxDist = neighborPlusProbeRadii2[j];
            centerT = neighborCenters[j];
            pointT.set(Geodesic.getVertexVector(vect));
            pointT.scaleAdd(combinedRadii, centerI);
            if (pointT.distanceSquared(centerT) < maxDist)
              geodesicMap.clear(vect);
          }
          break;
        case 1:
          
        }
        mapT.set(vect);
      }
    }
  }

  private int neighborCount;
  private int[] neighborIndices = new int[16];
  private Point3f[] neighborCenters = new Point3f[16];
  private float[] neighborPlusProbeRadii2 = new float[16];
  private float[] neighborRadii2 = new float[16];
  
  private void getNeighbors() {
    neighborCount = 0;
    if (disregardNeighbors)
      return;
    AtomIndexIterator iter = viewer.getWithinAtomSetIterator(indexI, radiusI + diameterP
        + maxRadius, bsMySelected, false, modelZeroBased); 

    while (iter.hasNext()) {
      int indexN = iter.next();
      float neighborRadius = atomData.atomRadius[indexN];
      if (centerI.distance(atomData.atomXyz[indexN]) > radiusI + radiusP + radiusP
          + neighborRadius)
        continue;
      if (neighborCount == neighborIndices.length) {
        neighborIndices = ArrayUtil.doubleLength(neighborIndices);
        neighborCenters = (Point3f[]) ArrayUtil.doubleLength(neighborCenters);
        neighborPlusProbeRadii2 = ArrayUtil
            .doubleLength(neighborPlusProbeRadii2);
        neighborRadii2 = ArrayUtil.doubleLength(neighborRadii2);
      }
      neighborCenters[neighborCount] = atomData.atomXyz[indexN];
      neighborIndices[neighborCount] = indexN;
      float neighborPlusProbeRadii = neighborRadius + radiusP;
      neighborPlusProbeRadii2[neighborCount] = neighborPlusProbeRadii
          * neighborPlusProbeRadii;
      neighborRadii2[neighborCount] = neighborRadius * neighborRadius;
      ++neighborCount;
    }
  }
  
  public void deleteAtoms(int firstAtomDeleted, int nAtomsDeleted, BitSet bsAtoms) {
    dotsConvexMaps = (FastBitSet[]) ArrayUtil.deleteElements(dotsConvexMaps, firstAtomDeleted, nAtomsDeleted);
    dotsConvexMax = dotsConvexMaps.length;
    if (mads != null)
      mads = (short[]) ArrayUtil.deleteElements(mads, firstAtomDeleted, nAtomsDeleted);
    atomData.atomRadius = (float[]) ArrayUtil.deleteElements(atomData.atomRadius, firstAtomDeleted, nAtomsDeleted);
    atomData.atomXyz = (Point3f[]) ArrayUtil.deleteElements(atomData.atomXyz, firstAtomDeleted, nAtomsDeleted);
    atomData.atomCount -= nAtomsDeleted;
    atomCount = atomData.atomCount;
    
  }  
}
