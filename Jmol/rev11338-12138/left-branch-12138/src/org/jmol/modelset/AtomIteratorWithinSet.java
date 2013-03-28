

package org.jmol.modelset;

import org.jmol.bspt.Bspf;
import org.jmol.bspt.CubeIterator;

import java.util.BitSet;

import javax.vecmath.Point3f;

class AtomIteratorWithinSet implements AtomIndexIterator {
  
  CubeIterator bsptIter;
  BitSet bsSelected;
  boolean isGreaterOnly;
  int atomIndex;
  int zerobase;

  
  void initialize(Bspf bspf, int bsptIndex, int atomIndex,
                  Point3f center, float distance, BitSet bsSelected,
                  boolean isGreaterOnly, int zerobase) {
    bsptIter = bspf.getCubeIterator(bsptIndex);
    bsptIter.initialize(center, distance);
    this.atomIndex = atomIndex;
    this.bsSelected = bsSelected;
    this.isGreaterOnly = isGreaterOnly;
    this.zerobase = zerobase;
  }

  int iNext;
  public boolean hasNext() {
    while (bsptIter.hasMoreElements()) {
      Atom atom = (Atom) bsptIter.nextElement();
      if ((iNext = atom.atomIndex) != atomIndex 
          && iNext > (isGreaterOnly ? atomIndex : -1)
          && (bsSelected == null || bsSelected.get(iNext)))
        return true;
    }
    iNext = -1;
    return false;
  }

  public int next() {
    return iNext - zerobase;
  }

  public float foundDistance2() {
    return bsptIter.foundDistance2();  
  }
  
  public void release() {
    bsptIter.release();
    bsptIter = null;
  }
}

