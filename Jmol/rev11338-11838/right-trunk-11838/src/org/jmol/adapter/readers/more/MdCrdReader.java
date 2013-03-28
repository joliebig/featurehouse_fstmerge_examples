

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Logger;


import java.io.BufferedReader;
import java.util.Vector;

import javax.vecmath.Point3f;



public class MdCrdReader extends AtomSetCollectionReader {
    
 private Vector trajectorySteps;
 public void readAtomSetCollection(BufferedReader br) {
    reader = br;
    atomSetCollection = new AtomSetCollection("mdcrd", this);
    
    atomSetCollection.addAtom(new Atom()); 
    trajectorySteps = (Vector) htParams.get("trajectorySteps");
    if (trajectorySteps == null)
      htParams.put("trajectorySteps", trajectorySteps = new Vector());
    try {
      readLine(); 
      readCoordinates();
      Logger.info("Total number of trajectory steps=" + trajectorySteps.size());
    } catch (Exception e) {
      setError(e);
    }
  }

  void readCoordinates() throws Exception {
    line = null;
    int atomCount = (bsFilter == null ? templateAtomCount : ((Integer) htParams
        .get("filteredAtomCount")).intValue());
    boolean isPeriodic = htParams.containsKey("isPeriodic");
    int floatCount = templateAtomCount * 3 + (isPeriodic ? 3 : 0);
    while (true)
      if (doGetModel(++modelNumber)) {
        Point3f[] trajectoryStep = new Point3f[atomCount];
        if (!getTrajectoryStep(trajectoryStep, isPeriodic))
          return;
        trajectorySteps.add(trajectoryStep);
        if (isLastModel(modelNumber))
          return;
      } else {
        if (!skipFloats(floatCount))
          return;
      }
  }

  private int ptFloat = 0;
  private int lenLine = 0;
  private float getFloat() throws Exception {
    while (line == null || ptFloat >= lenLine) {
      if (readLine() == null)
        return Float.NaN;
      ptFloat = 0;
      lenLine = line.length();
    }
    ptFloat += 8;
    return parseFloat(line.substring(ptFloat - 8, ptFloat));
  }
  
  private Point3f getPoint() throws Exception {
    float x = getFloat();
    float y = getFloat();
    float z = getFloat();
    return (Float.isNaN(z) ? null : new Point3f(x, y, z));
  }

  private boolean getTrajectoryStep(Point3f[] trajectoryStep, boolean isPeriodic)
      throws Exception {
    int atomCount = trajectoryStep.length;
    int n = -1;
    for (int i = 0; i < templateAtomCount; i++) {
      Point3f pt = getPoint();
      if (pt == null)
        return false;
      if (bsFilter == null || bsFilter.get(i)) {
        if (++n == atomCount)
          return false;
        trajectoryStep[n] = pt;
      }
    }
    if (isPeriodic)
      getPoint(); 
    return (line != null);
  }

  private boolean skipFloats(int n) throws Exception {
    int i = 0;
    
    while (i < n && readLine() != null)
      i += getTokens().length;
    return (line != null);
  }
}
