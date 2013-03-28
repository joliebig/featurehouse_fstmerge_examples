
package org.jmol.adapter.readers.quantum;

import org.jmol.adapter.smarter.*;
import org.jmol.quantum.SlaterData;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

import java.util.Vector;


abstract class SlaterReader extends AtomSetCollectionReader {

  

  protected final Hashtable moData = new Hashtable();
  protected int nOrbitals = 0;
  protected final Vector slaters = new Vector();
  protected SlaterData[] slaterArray;
  protected final Vector orbitals = new Vector();
  
  
  protected final void addSlater(int iAtom, int a, int b, int c, int d, 
                        double zeta, float coef) {
    
    slaters.addElement(new SlaterData(iAtom, a, b, c, d, zeta, coef));
  }

  protected void addSlater(SlaterData sd, int n) {
    sd.index = n;
    slaters.addElement(sd);    
  }

  
  protected final void setSlaters(boolean doScale, boolean doSort) {
    if (slaterArray == null) {
      int nSlaters = slaters.size();
      slaterArray = new SlaterData[nSlaters];
      for (int i = 0; i < slaterArray.length; i++) 
        slaterArray[i] = (SlaterData) slaters.get(i);
    }
    if (doScale)
      for (int i = 0; i < slaterArray.length; i++) {
        SlaterData sd = slaterArray[i];
        sd.coef *= scaleSlater(sd.x, sd.y, sd.z, sd.r, sd.zeta);
        
        System.out.println ("SlaterReader " + i + ": " + sd.iAtom + " " + sd.x + " " + sd.y +  " " + sd.z + " " + sd.r + " " + sd.zeta + " " + sd.coef);

      }
    if (doSort) {
      Arrays.sort(slaterArray, new SlaterSorter());
      int[] pointers = new int[slaterArray.length];      
      for (int i = 0; i < slaterArray.length; i++)
        pointers[i] = slaterArray[i].index;
      sortOrbitalCoefficients(pointers);
    }
    moData.put("slaters", slaterArray);
    atomSetCollection.setAtomSetAuxiliaryInfo("moData", moData);
  }

  class SlaterSorter implements Comparator {
    public int compare(Object a, Object b) {
      SlaterData sd1 = (SlaterData) a;
      SlaterData sd2 = (SlaterData) b;
      return ( sd1.iAtom < sd2.iAtom ? -1 : sd1.iAtom > sd2.iAtom ? 1 : 0);
    }    
  }

  protected final void setMOs(String units) {
    moData.put("mos", orbitals);
    moData.put("energyUnits", units);
    setMOData(moData);
  }

  
  protected void sortOrbitalCoefficients(int[] pointers) {
    
    for (int i = orbitals.size(); --i >= 0; ) {
      Hashtable mo = (Hashtable) orbitals.get(i);
      float[] coefs = (float[]) mo.get("coefficients");
      float[] sorted = new float[pointers.length];
      for (int j = 0; j < pointers.length; j++) {
        int k = pointers[j];
        if (k < coefs.length)
          sorted[j] = coefs[k];
      }
      mo.put("coefficients", sorted);
    }
  }
  
  
  
  protected void sortOrbitals() {
    Object[] array = orbitals.toArray();
    Arrays.sort(array, new OrbitalSorter());
    orbitals.clear();
    for (int i = 0; i < array.length; i++)
      orbitals.add(array[i]);    
  }
  
  class OrbitalSorter implements Comparator {
    public int compare(Object a, Object b) {
      Hashtable mo1 = (Hashtable) a;
      Hashtable mo2 = (Hashtable) b;
      float e1 = ((Float) mo1.get("energy")).floatValue();
      float e2 = ((Float) mo2.get("energy")).floatValue();
      return ( e1 < e2 ? -1 : e2 < e1 ? 1 : 0);
    }    
  }

  
  
  
  protected double scaleSlater(int ex, int ey, int ez, int er, double zeta) {
    int el = ex + ey + ez;
    switch (el) {
    case 0: 
    case 1: 
      ez = -1; 
      break;
    }
    
    
    
    
    
    return getSlaterConstCartesian(el + er + 1, 
        Math.abs(zeta), el, ex, ey, ez);
  }

  
  private static double fact(double f, double zeta, int n) {
    return Math.pow(2 * zeta, n + 0.5) * Math.sqrt(f * _1_4pi / fact1[n]);
  }

  private final static double _1_4pi = 0.25 / Math.PI;

  
  private final static double[] fact1 = new double[] {
    1.0, 2.0, 24.0, 720.0, 40320.0, 362880.0, 87178291200.0 };
 
  
  
  private final static double[] dfact2 = new double[] { 1, 1, 3, 15, 105 };

  
  protected final static double getSlaterConstCartesian(int n, double zeta,
                                                       int el, int ex, int ey,
                                                       int ez) {
    return fact(ez < 0 ? dfact2[el + 1] 
        : dfact2[el + 1] / dfact2[ex] / dfact2[ey] / dfact2[ez], zeta, n);
  }

  
  protected final static double getSlaterConstDSpherical(int n, double zeta, 
                                                      int ex, int ey) {
    return fact(15 / (ex < 0 ? 12 : ey < 0 ? 4 : 1), zeta, n);
  }
  
}
