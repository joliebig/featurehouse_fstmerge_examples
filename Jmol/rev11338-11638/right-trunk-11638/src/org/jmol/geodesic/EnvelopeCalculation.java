

package org.jmol.geodesic;

import org.jmol.modelset.AtomIndexIterator;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.atomdata.AtomData;
import org.jmol.atomdata.AtomDataServer;

import java.util.BitSet;
import javax.vecmath.Point3f;





public final class EnvelopeCalculation {

  
  private short[] mads;
  private AtomData atomData = new AtomData();
  private AtomDataServer viewer;
  private int atomCount;
  
  public EnvelopeCalculation(AtomDataServer viewer, int atomCount, short[] mads) {
    this.viewer = viewer;
    this.atomCount = atomCount; 
    this.mads = mads;
    geodesicCount = Geodesic.getVertexVectorsCount();
    geodesicMap = allocateBitmap(geodesicCount);
    mapT = allocateBitmap(geodesicCount);
  }
   
  public final static float SURFACE_DISTANCE_FOR_CALCULATION = 3f;

  public final static int MAX_LEVEL = Geodesic.standardLevel;
  
  private float maxRadius = 0;
  private float scale = 1f;
  private float setRadius = Float.MAX_VALUE;
  private float addRadius = Float.MAX_VALUE;
  private boolean modelZeroBased;

  private int[][] dotsConvexMaps;
  public int[][] getDotsConvexMaps() {
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
    dotsConvexMaps = new int[max][];
  }
  
  private int geodesicCount;
  private int[] geodesicMap;
  private int[] mapT;
  private final static int[] mapNull = new int[0];
  
  private BitSet bsSurface;
  
  public BitSet getBsSurfaceClone() {
    return (bsSurface == null ? null : BitSetUtil.copy(bsSurface));
  }
  
  private boolean disregardNeighbors = false;
  private BitSet bsMySelected;
  
  public void setMads(short[] mads) {
    this.mads = mads;
  }
  
  public void setFromBits(int index, BitSet bs) {
    setAllBits(geodesicMap, geodesicCount);
    for (int iDot = geodesicCount; --iDot >= 0;)
      if (!bs.get(iDot))
        clearBit(geodesicMap, iDot);
    if (dotsConvexMaps == null)
      dotsConvexMaps = new int[atomCount][];
    int[] map = mapNull;
    int count = getMapStorageCount(geodesicMap);
    if (count > 0) {
      map = new int[count];
      System.arraycopy(geodesicMap, 0, map, 0, count);
    }
    dotsConvexMaps[index] = map;
    dotsConvexMax = Math.max(dotsConvexMax, index);
  }
  
  public float getRadius() {
    return setRadius;
  }
  
  private float radiusP, diameterP;

  public void newSet() {
    dotsConvexMax = 0;
    dotsConvexMaps = null;
    radiusP = diameterP = 0;
    mads = null;
  }
  
  public void calculate(float addRadius, float setRadius, float scale,
                 float maxRadius, BitSet bsSelected, BitSet bsIgnore,
                 boolean useVanderwaalsRadius,
                 boolean disregardNeighbors, boolean onlySelectedDots,
                 boolean isSurface, boolean multiModel) {
    this.addRadius = (addRadius == Float.MAX_VALUE ? 0 : addRadius);
    this.setRadius = (setRadius == Float.MAX_VALUE && !useVanderwaalsRadius ? SURFACE_DISTANCE_FOR_CALCULATION
        : setRadius);
    this.scale = scale;
    atomData.useIonic = !useVanderwaalsRadius;
    atomData.adpMode = (setRadius == Short.MAX_VALUE ? 1 : setRadius == Short.MIN_VALUE ? -1 : 0);
    atomData.modelIndex = (multiModel ? -1 : 0);
    modelZeroBased = !multiModel;
    
    viewer.fillAtomData(atomData, AtomData.MODE_FILL_COORDS_AND_RADII);
    atomCount = atomData.atomCount;
    setRadii(useVanderwaalsRadius);    
    bsMySelected = (onlySelectedDots && bsSelected != null ? BitSetUtil.copy(bsSelected)
        : bsIgnore != null ? BitSetUtil.setAll(atomCount) : null);
    BitSetUtil.andNot(bsMySelected, bsIgnore);
    this.disregardNeighbors = disregardNeighbors;
    bsSurface = new BitSet();
    this.maxRadius = (maxRadius == Float.MAX_VALUE ? setRadius : maxRadius);
    
    for (int i = atomCount; --i >= 0;)
      if ((bsSelected == null || bsSelected.get(i))
          && (bsIgnore == null || !bsIgnore.get(i))) {
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
  
  private void setRadii(boolean useVanderwaalsRadius) {
    for (int i = 0; i < atomCount; i++) {
      atomData.atomRadius[i] = (mads != null ? mads[i] / 1000f
          : addRadius
          + (setRadius != Float.MAX_VALUE 
              && setRadius != Short.MAX_VALUE && setRadius != Short.MIN_VALUE
              ? setRadius : atomData.atomRadius[i]
              * scale));
    }
  }
  
  private Point3f[] currentPoints;
  
  public Point3f[] getPoints() {
    if (dotsConvexMaps == null) {
      calculate(Float.MAX_VALUE, SURFACE_DISTANCE_FOR_CALCULATION, 1f,
          Float.MAX_VALUE, bsMySelected, null, false, false, false, false, false);
    }
    if (currentPoints != null)
      return currentPoints;
    int nPoints = 0;
    int dotCount = 42;
    for (int i = dotsConvexMax; --i >= 0;)
      nPoints += getPointCount(dotsConvexMaps[i], dotCount);
    Point3f[] points = new Point3f[nPoints];
    if (nPoints == 0)
      return points;
    nPoints = 0;
    for (int i = dotsConvexMax; --i >= 0;)
      if (dotsConvexMaps[i] != null) {
        int iDot = dotsConvexMaps[i].length << 5;
        if (iDot > dotCount)
          iDot = dotCount;
        while (--iDot >= 0)
          if (getBit(dotsConvexMaps[i], iDot)) {
            Point3f pt = new Point3f();
            pt.scaleAdd(atomData.atomRadius[i], Geodesic.getVertexVector(iDot), atomData.atomXyz[i]);
            points[nPoints++] = pt;
          }
      }
    currentPoints = points;
    return points;
  }  
  
  public final static boolean getBit(int[] bitmap, int i) {
    return (bitmap[(i >> 5)] << (i & 31)) < 0;
  }

  
  
  
  
  private int getPointCount(int[] visibilityMap, int dotCount) {
    if (visibilityMap == null)
      return 0;
    int iDot = visibilityMap.length << 5;
    if (iDot > dotCount)
      iDot = dotCount;
    int n = 0;
    n = 0;
    while (--iDot >= 0)
      if (getBit(visibilityMap, iDot))
        n++;
    return n;
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
      dotsConvexMaps = new int[atomCount][];
    calcConvexBits();
    int[] map = mapNull;    
    int count = getMapStorageCount(geodesicMap);
    if (count > 0) {
      bsSurface.set(indexI);
      if (isSurface) {
        addIncompleteFaces(geodesicMap);
        addIncompleteFaces(geodesicMap);
      }
      count = getMapStorageCount(geodesicMap);
      map = new int[count];
      System.arraycopy(geodesicMap, 0, map, 0, count);
    }
    dotsConvexMaps[indexI] = map;
  }
  
  private int getMapStorageCount(int[] map) {
    int indexLast;
    for (indexLast = map.length; --indexLast >= 0
        && map[indexLast] == 0;) {
    }
    return indexLast + 1;
  }

  private void addIncompleteFaces(int[] points) {
    clearBitmap(mapT);
    short[] faces = Geodesic.getFaceVertexes(MAX_LEVEL);
    int len = faces.length;
    int maxPt = -1;
    for (int f = 0; f < len;) {
      short p1 = faces[f++];
      short p2 = faces[f++];
      short p3 = faces[f++];
      boolean ok1 = getBit(points, p1); 
      boolean ok2 = getBit(points, p2); 
      boolean ok3 = getBit(points, p3);
      if (! (ok1 || ok2 || ok3) || ok1 && ok2 && ok3)
        continue;
      
      
      if (!ok1) {
        setBit(mapT, p1);
        if (maxPt < p1)
          maxPt = p1;
      }
      if (!ok2) {
        setBit(mapT, p2);
        if (maxPt < p2)
          maxPt = p2;
      }
      if (!ok3) {
        setBit(mapT, p3);
        if (maxPt < p3)
          maxPt = p3;
      }
    }
    for (int i=0; i <= maxPt; i++) {
      if (getBit(mapT, i))
        setBit(points, i);
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
    setAllBits(geodesicMap, geodesicCount);
    float combinedRadii = radiusI + radiusP;
    if (neighborCount == 0)
      return;
    int faceTest;
    int p1, p2, p3;
    short[] faces = Geodesic.getFaceVertexes(MAX_LEVEL);
    
    int p4 = power4[MAX_LEVEL - 1];
    boolean ok1, ok2, ok3;
    clearBitmap(mapT);
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
          clearBit(geodesicMap, p1);
        if (!ok2)
          clearBit(geodesicMap, p2);
        if (!ok3)
          clearBit(geodesicMap, p3);
        if (!ok1 && !ok2 && !ok3) {
          faceTest = -1;
          break;
        }
      }
      int kFirst = f * 12 * p4;
      int kLast = kFirst + 12 * p4;
      for (int k = kFirst; k < kLast; k++) {
        int vect = faces[k];
        if (getBit(mapT, vect) || ! getBit(geodesicMap, vect))
            continue;
        switch (faceTest) {
        case -1:
          
          clearBit(geodesicMap, vect);
          break;
        case 0:
          
          for (int j = 0; j < neighborCount; j++) {
            float maxDist = neighborPlusProbeRadii2[j];
            centerT = neighborCenters[j];
            pointT.set(Geodesic.getVertexVector(vect));
            pointT.scaleAdd(combinedRadii, centerI);
            if (pointT.distanceSquared(centerT) < maxDist)
              clearBit(geodesicMap, vect);
          }
          break;
        case 1:
          
        }
        setBit(mapT, vect);
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
  
  private final static int[] allocateBitmap(int count) {
    return new int[(count + 31) >> 5];
  }

  private final static void setBit(int[] bitmap, int i) {
    bitmap[(i >> 5)] |= 1 << (~i & 31);
  }

  private final static void clearBit(int[] bitmap, int i) {
    bitmap[(i >> 5)] &= ~(1 << (~i & 31));
  }

  private final static void setAllBits(int[] bitmap, int count) {
    int i = count >> 5;
    if ((count & 31) != 0)
      bitmap[i] = 0x80000000 >> (count - 1);
    while (--i >= 0)
      bitmap[i] = -1;
  }
  
  private final static void clearBitmap(int[] bitmap) {
    for (int i = bitmap.length; --i >= 0; )
      bitmap[i] = 0;
  }

  public void deleteAtoms(int firstAtomDeleted, int nAtomsDeleted, BitSet bsAtoms) {
    dotsConvexMaps = (int[][]) ArrayUtil.deleteElements(dotsConvexMaps, firstAtomDeleted, nAtomsDeleted);
    dotsConvexMax = dotsConvexMaps.length;
    if (mads != null)
      mads = (short[]) ArrayUtil.deleteElements(mads, firstAtomDeleted, nAtomsDeleted);
    atomData.atomRadius = (float[]) ArrayUtil.deleteElements(atomData.atomRadius, firstAtomDeleted, nAtomsDeleted);
    atomData.atomXyz = (Point3f[]) ArrayUtil.deleteElements(atomData.atomXyz, firstAtomDeleted, nAtomsDeleted);
    atomData.atomCount -= nAtomsDeleted;
    atomCount = atomData.atomCount;
    
  }  
}
