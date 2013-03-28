

package org.jmol.minimize.forcefield;

import org.jmol.minimize.MinAtom;

abstract class Calculation {

  double dE;
  
  FFParam parA, parB, parC;  
  MinAtom a, b, c, d;
  int ia, ib, ic, id;

  int[] iData;
  double[] dData;

  double delta, rab, theta;
  double energy;

  

  abstract double compute(Object[] dataIn);
  
  
  double getEnergy() {
    return energy;
  }

  
  void getPointers(Object[] dataIn) {
    iData = (int[])dataIn[0];
    dData = (double[])dataIn[1];
  }
}
