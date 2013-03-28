

package org.jmol.modelset;

import javax.vecmath.Point3f;

import org.jmol.bspt.Bspf;
import org.jmol.bspt.CubeIterator;

public class AtomIteratorWithinModel implements AtomIndexIterator {

  CubeIterator bsptIter;

  

  void initialize(Bspf bspf, int bsptIndex, Point3f center, float radius) {
    bsptIter = bspf.getCubeIterator(bsptIndex);
    bsptIter.initialize(center, radius);
  }

  public boolean hasNext() {
    return bsptIter.hasMoreElements();
  }

  public int next() {
    return ((Atom) bsptIter.nextElement()).index;
  }

  public float foundDistance2() {
    return bsptIter.foundDistance2();
  }
  
  public void release() {
    bsptIter.release();
    bsptIter = null;
  }
}


