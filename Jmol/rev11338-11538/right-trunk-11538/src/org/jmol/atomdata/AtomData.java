




package org.jmol.atomdata;

import java.util.BitSet;

import javax.vecmath.Point3f;

public class AtomData {
  public AtomData() {    
  }
 
  final static public int MODE_FILL_COORDS = 1;
  final static public int MODE_FILL_COORDS_AND_RADII = 2;
  final static public int MODE_GET_ATTACHED_HYDROGENS = 3;
 
  public String programInfo;
  public String fileName;
  public String modelName;
  public int modelIndex;
  
  public BitSet bsSelected;
  public BitSet bsIgnored;
  
  public boolean useIonic;
  
  
  
  public int firstAtomIndex;
  public int firstModelIndex; 
  public int lastModelIndex; 

  
  
  
  public float hAtomRadius;
  
  public Point3f[] atomXyz;
  public float[] atomRadius;
  public int[] atomicNumber;
  public Point3f[][] hAtoms;
  public int atomCount;
  public int hydrogenAtomCount;
  public int adpMode;
}

