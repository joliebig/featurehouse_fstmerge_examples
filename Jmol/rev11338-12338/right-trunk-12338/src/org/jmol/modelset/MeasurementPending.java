
package org.jmol.modelset;

import org.jmol.util.Point3fi;

public class MeasurementPending extends Measurement {

  
  private boolean haveTarget = false;
  private boolean haveModified = false;
  
  public boolean haveTarget() {
    return haveTarget;
  }
  
  public boolean haveModified() {
    return haveModified;
  }
  
  private int numSet = 0;
  
  public int getNumSet() {
    return numSet;
  }

  public MeasurementPending(ModelSet modelSet) {
    super(modelSet, null, Float.NaN, (short) 0, null, 0);
  }

  private boolean checkPoint(Point3fi ptClicked) {
    for (int i = 1; i <= numSet; i++)
      if (countPlusIndices[i] == -1 - i
          && pts[i - 1].distance(ptClicked) < 0.01)
        return false;
    return true;
  }
  
  public int getIndexOf(int atomIndex) {
    for (int i = 1; i <= numSet; i++)
      if (countPlusIndices[i] == atomIndex)
        return i;
    return 0;
  }

  private int lastIndex = -1;
  
  public int addPoint(int atomIndex, Point3fi ptClicked, boolean doSet) {
    haveModified = (atomIndex != lastIndex);
    lastIndex = atomIndex;
    if (ptClicked == null) {
      if (getIndexOf(atomIndex) > 0) {
        if (doSet)
          numSet = count;
        return count;
      }
      haveTarget = (atomIndex >= 0);
      if (!haveTarget)
        return count = numSet;
      count = numSet + 1;
      countPlusIndices[count] = atomIndex;
    } else {
      if (!checkPoint(ptClicked)) {
        if (doSet)
          numSet = count;
        return count;
      }
      int pt = numSet;
      haveModified = haveTarget = true;
      count = numSet + 1;
      pts[pt] = ptClicked;
      countPlusIndices[count] = -2 - pt;
    }
    countPlusIndices[0] = count;
    if (doSet)
      numSet = count;
    value = getMeasurement();
    formatMeasurement(null);
    return count;
  }
}
