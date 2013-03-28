
package org.jmol.bspt;

import java.util.BitSet;

import javax.vecmath.Point3f;



public final class Bspf {

  int dimMax;
  Bspt bspts[];
  
  CubeIterator[] cubeIterators;
  
  public Bspf(int dimMax) {
    this.dimMax = dimMax;
    bspts = new Bspt[0];
    cubeIterators = new CubeIterator[0];
  }

  public int getBsptCount() {
    return bspts.length;
  }
  
  public void clearBspt(int bsptIndex) {
    bspts[bsptIndex] = null;
  }
  
  public boolean isInitialized(int bsptIndex) {
    return bspts.length > bsptIndex && bspts[bsptIndex] != null;
  }
  
  public void addTuple(int bsptIndex, Point3f tuple) {
    if (bsptIndex >= bspts.length) {
      Bspt[] t = new Bspt[bsptIndex + 1];
      System.arraycopy(bspts, 0, t, 0, bspts.length);
      bspts = t;
    }
    Bspt bspt = bspts[bsptIndex];
    if (bspt == null)
      bspt = bspts[bsptIndex] = new Bspt(dimMax);
    bspt.addTuple(tuple);
  }

  public void stats() {
    for (int i = 0; i < bspts.length; ++i)
      if (bspts[i] != null)
        bspts[i].stats();
  }

  
  
  public CubeIterator getCubeIterator(int bsptIndex) {
    if (bsptIndex >= cubeIterators.length) {
      CubeIterator[] t = new CubeIterator[bsptIndex + 1];
      System.arraycopy(cubeIterators, 0, t, 0, cubeIterators.length);
      cubeIterators = t;
    }
    if (cubeIterators[bsptIndex] == null &&
        bspts[bsptIndex] != null)
      cubeIterators[bsptIndex] = bspts[bsptIndex].allocateCubeIterator();
    return cubeIterators[bsptIndex];
  }

  public void initialize(int modelIndex, Point3f[] atoms, BitSet modelAtomBitSet) {
    for (int i = atoms.length; --i >= 0;)
      if (modelAtomBitSet.get(i))
        addTuple(modelIndex, atoms[i]);
  }

}
