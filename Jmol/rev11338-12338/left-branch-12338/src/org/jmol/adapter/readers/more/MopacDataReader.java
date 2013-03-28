
package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;
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

  protected final void addSlater(int i0, int i1, int i2, int i3, int i4, 
                        float zeta, float coef) {
    
    intinfo.addElement(new int[] {i0, i1, i2, i3, i4});
    floatinfo.addElement(new float[] {zeta, coef});
  }
  
  protected final void setSlaters() {
    int ndata = intinfo.size();
    int[][] iarray = new int[ndata][];
    for (int i = 0; i < ndata; i++)
      iarray[i] = (int[]) intinfo.get(i);
    float[][] farray = new float[ndata][];
    for (int i = 0; i < ndata; i++)
      farray[i] = (float[]) floatinfo.get(i);
    moData.put("slaterInfo", iarray);
    moData.put("slaterData", farray);
    atomSetCollection.setAtomSetAuxiliaryInfo("moData", moData);
  }
  
  protected final void setMOs(String units) {
    moData.put("mos", orbitals);
    moData.put("energyUnits", units);
    setMOData(moData);
  }
}
