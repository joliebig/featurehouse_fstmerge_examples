
package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Escape;


import java.io.BufferedReader;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Hashtable;

import java.util.Vector;


abstract class MopacDataReader extends AtomSetCollectionReader {

  protected final static float MIN_COEF = 0.0001f;  
  
  protected final Hashtable moData = new Hashtable();
  protected int nOrbitals = 0;
  protected final Vector intinfo = new Vector();
  protected final Vector floatinfo = new Vector();
  protected final Vector orbitals = new Vector();
  
  public abstract void readAtomSetCollection(BufferedReader reader);
  
  protected final static int [] dValues = new int[] { 
    
    0, -2, 0, 
    1,  0, 1, 
   -2,  0, 0, 
    0,  1, 1, 
    1,  1, 0, 
  };

  protected final void addSlater(int iatom, int a, int b, int c, int d, 
                        float zeta, float coef) {
    
    
    intinfo.addElement(new int[] {iatom, a, b, c, d});
    floatinfo.addElement(new float[] {zeta, coef});
  }
  
  protected BitSet bsBases;
  protected int nBases;
  
  protected final void setSlaters() {
    int ndata = intinfo.size();
    if (bsBases == null)
      nBases = ndata;
    int[][] iarray = new int[nBases][];
    for (int i = 0, pt = 0; i < ndata; i++)
      if (bsBases == null || bsBases.get(i)) {
        
        iarray[pt++] = (int[]) intinfo.get(i);
      }
    float[][] farray = new float[nBases][];
    for (int i = 0, pt = 0; i < ndata; i++)
      if (bsBases == null || bsBases.get(i))
        farray[pt++] = (float[]) floatinfo.get(i);
    moData.put("slaterInfo", iarray);
    moData.put("slaterData", farray);
    atomSetCollection.setAtomSetAuxiliaryInfo("moData", moData);
  }
  
  protected final void setMOs(String units) {
    moData.put("mos", orbitals);
    moData.put("energyUnits", units);
    setMOData(moData);
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

}
