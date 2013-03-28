
package org.jmol.quantum;

import javax.vecmath.Point3f;

import org.jmol.util.Logger;

import java.util.BitSet;

abstract class QuantumCalculation {

  protected boolean doDebug = false;

  protected final static float bohr_per_angstrom = 1 / 0.52918f;

  protected float[][][] voxelData;
  protected int xMin, xMax, yMin, yMax, zMin, zMax;

  protected QMAtom[] qmAtoms;
  protected int atomIndex;
  protected QMAtom thisAtom;

  
  protected float[] xBohr, yBohr, zBohr;
  protected float[] originBohr = new float[3];
  protected float[] stepBohr = new float[3];
  protected int nX, nY, nZ;
  
  
  protected float[] X, Y, Z;

  
  protected float[] X2, Y2, Z2;

  
  
  protected float rangeBohr = 10; 

  protected void initialize(int nX, int nY, int nZ) {
    
    this.nX = nX;
    this.nY = nY;
    this.nZ = nZ;
    
    
    xBohr = new float[nX];
    yBohr = new float[nY];
    zBohr = new float[nZ];

    
    X = new float[nX];
    Y = new float[nY];
    Z = new float[nZ];

    
    X2 = new float[nX];
    Y2 = new float[nY];
    Z2 = new float[nZ];
  }

  protected void setupCoordinates(float[] originXYZ, float[] stepsXYZ,
                                  BitSet bsSelected, Point3f[] atomCoordAngstroms) {

    

    for (int i = 3; --i >= 0;) {
      originBohr[i] = originXYZ[i] * bohr_per_angstrom;
      stepBohr[i] = stepsXYZ[i] * bohr_per_angstrom;
    }
    setXYZBohr(xBohr, 0, nX);
    setXYZBohr(yBohr, 1, nY);
    setXYZBohr(zBohr, 2, nZ);
    
    

    qmAtoms = new QMAtom[atomCoordAngstroms.length];
    for (int i = atomCoordAngstroms.length; --i >= 0;) {
      if (bsSelected != null && !bsSelected.get(i))
        continue;
      qmAtoms[i] = new QMAtom(atomCoordAngstroms[i], X, Y, Z, X2, Y2, Z2);
    }

    if (doDebug)
      Logger.debug("QuantumCalculation:\n origin(Bohr)= " + originBohr[0] + " "
          + originBohr[1] + " " + originBohr[2] + "\n steps(Bohr)= "
          + stepBohr[0] + " " + stepBohr[1] + " " + stepBohr[2] + "\n counts= "
          + nX + " " + nY + " " + nZ);
  }

  private void setXYZBohr(float[] bohr, int i, int n) {
    bohr[0] = originBohr[i];
    float inc = stepBohr[i];
    for (int j = 0; ++j < n;)
      bohr[j] = bohr[j - 1] + inc;
  }

  class QMAtom extends Point3f {

    
    private float[] myX, myY, myZ;

    
    private float[] myX2, myY2, myZ2;

    
    QMAtom(Point3f coordAngstroms, float[] X, float[] Y, float[] Z, 
        float[] X2, float[] Y2, float[] Z2) {
      myX = X;
      myY = Y;
      myZ = Z;
      myX2 = X2;
      myY2 = Y2;
      myZ2 = Z2;
      
      set(coordAngstroms);
      scale(bohr_per_angstrom);
    }

    protected void setXYZ(boolean setMinMax) {
      int i;
      try {
      if (setMinMax) {
        i = (int) Math.floor((x - xBohr[0] - rangeBohr) / stepBohr[0]);
        xMin = (i < 0 ? 0 : i);
        i = (int) Math.floor(1 + (x - xBohr[0] + rangeBohr) / stepBohr[0]);
        xMax = (i >= nX ? nX : i + 1);
        i = (int) Math.floor((y - yBohr[0] - rangeBohr) / stepBohr[1]);
        yMin = (i < 0 ? 0 : i);
        i = (int) Math.floor(1 + (y - yBohr[0] + rangeBohr) / stepBohr[1]);
        yMax = (i >= nY ? nY : i + 1);
        i = (int) Math.floor((z - zBohr[0] - rangeBohr) / stepBohr[2]);
        zMin = (i < 0 ? 0 : i);
        i = (int) Math.floor(1 + (z - zBohr[0] + rangeBohr) / stepBohr[2]);
        zMax = (i >= nZ ? nZ : i + 1);
      }
      for (i = xMax; --i >= xMin;) {
        myX2[i] = myX[i] = xBohr[i] - x;
        myX2[i] *= myX[i];
      }
      for (i = yMax; --i >= yMin;) {
        myY2[i] = myY[i] = yBohr[i] - y;
        myY2[i] *= myY[i];
      }
      for (i = zMax; --i >= zMin;) {
        myZ2[i] = myZ[i] = zBohr[i] - z;
        myZ2[i] *= myZ[i];
      }
      
      } catch (Exception e) {
        System.out.println("Error in QuantumCalculation setting bounds");
      }
    }
  }
}
