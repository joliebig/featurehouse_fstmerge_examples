
package org.jmol.util;

import javax.vecmath.*;

public class TempArray {

  public TempArray() {
  }

  
  public void clear() {
    clearTempPoints();
    clearTempScreens();
    
  }
  
  private static int findBestFit(int size, int[] lengths) {
    int iFit = -1;
    int fitLength = Integer.MAX_VALUE;

    for (int i = lengths.length; --i >= 0;) {
      int freeLength = lengths[i];
      if (freeLength >= size && freeLength < fitLength) {
        fitLength = freeLength;
        iFit = i;
      }
    }
    if (iFit >= 0)
      lengths[iFit] = 0;
    return iFit;
  }

  private static int findShorter(int size, int [] lengths) {
    for (int i = lengths.length; --i >= 0;)
      if (lengths[i] == 0) {
        lengths[i] = size;
        return i;
      }
    int iShortest = 0;
    int shortest = lengths[0];
    for (int i = lengths.length; --i > 0;)
      if (lengths[i] < shortest) {
        shortest = lengths[i];
        iShortest = i;
      }
    if (shortest < size) {
      lengths[iShortest] = size;
      return iShortest;
    }
    return -1;
  }

  
  
  
  private final static int freePointsSize = 6;
  private final int[] lengthsFreePoints = new int[freePointsSize];
  private final Point3f[][] freePoints = new Point3f[freePointsSize][];

  private void clearTempPoints() {
    for (int i = 0; i < freePointsSize; i++) {
      lengthsFreePoints[i] = 0;
      freePoints[i] = null;
    }
  }
  
  public Point3f[] allocTempPoints(int size) {
    Point3f[] tempPoints;
    int iFit = findBestFit(size, lengthsFreePoints);
    if (iFit > 0) {
      tempPoints = freePoints[iFit];
    } else {
      tempPoints = new Point3f[size];
      for (int i = size; --i >= 0;)
        tempPoints[i] = new Point3f();
    }
    return tempPoints;
  }

  public void freeTempPoints(Point3f[] tempPoints) {
    for (int i = 0; i < freePoints.length; i++)
      if (freePoints[i] == tempPoints) {
        lengthsFreePoints[i] = tempPoints.length;
        return;
      }
    int iFree = findShorter(tempPoints.length, lengthsFreePoints);
    if (iFree >= 0)
      freePoints[iFree] = tempPoints;
  }

  
  
  
  private final static int freeScreensSize = 6;
  private final int[] lengthsFreeScreens = new int[freeScreensSize];
  private final Point3i[][] freeScreens = new Point3i[freeScreensSize][];

  private void clearTempScreens() {
    for (int i = 0; i < freeScreensSize; i++) {
      lengthsFreeScreens[i] = 0;
      freeScreens[i] = null;
    }
  }
  
  public Point3i[] allocTempScreens(int size) {
    Point3i[] tempScreens;
    int iFit = findBestFit(size, lengthsFreeScreens);
    if (iFit > 0) {
      tempScreens = freeScreens[iFit];
    } else {
      tempScreens = new Point3i[size];
      for (int i = size; --i >= 0;)
        tempScreens[i] = new Point3i();
    }
    return tempScreens;
  }

  public void freeTempScreens(Point3i[] tempScreens) {
    for (int i = 0; i < freeScreens.length; i++)
      if (freeScreens[i] == tempScreens) {
        lengthsFreeScreens[i] = tempScreens.length;
        return;
      }
    int iFree = findShorter(tempScreens.length, lengthsFreeScreens);
    if (iFree >= 0)
      freeScreens[iFree] = tempScreens;
  }

  
  
  
  
  
  
  
  private final static int freeBytesSize = 2;
  private final int[] lengthsFreeBytes = new int[freeBytesSize];
  private final byte[][] freeBytes = new byte[freeBytesSize][];

  public byte[] allocTempBytes(int size) {
    byte[] tempBytes;
    int iFit = findBestFit(size, lengthsFreeBytes);
    if (iFit > 0) {
      tempBytes = freeBytes[iFit];
    } else {
      tempBytes = new byte[size];
    }
    return tempBytes;
  }

  public void freeTempBytes(byte[] tempBytes) {
    for (int i = 0; i < freeBytes.length; i++)
      if (freeBytes[i] == tempBytes) {
        lengthsFreeBytes[i] = tempBytes.length;
        return;
      }
    int iFree = findShorter(tempBytes.length, lengthsFreeBytes);
    if (iFree >= 0)
      freeBytes[iFree] = tempBytes;
  }
}
