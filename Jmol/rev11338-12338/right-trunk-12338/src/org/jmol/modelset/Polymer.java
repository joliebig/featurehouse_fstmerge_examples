
package org.jmol.modelset;

import java.util.BitSet;
import java.util.Vector;
import java.util.Hashtable;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;


import org.jmol.viewer.Viewer;

abstract public class Polymer {

  
  
  
  
  
  protected Point3f[] leadMidpoints;
  protected Point3f[] leadPoints;
  protected Point3f[] sheetPoints;
  
  protected Vector3f[] wingVectors;

  protected int[] leadAtomIndices;

  protected int type;
  public int bioPolymerIndexInModel;
  
  protected final static int TYPE_OTHER = 0; 
  protected final static int TYPE_AMINO = 1;
  protected final static int TYPE_NUCLEIC = 2;
  protected final static int TYPE_CARBOHYDRATE = 3;
  

  public int getType() {
    return type;
  }
  
  protected Polymer() {
  }

  public int getPolymerPointsAndVectors(int last, BitSet bs, Vector vList,
                                        boolean isTraceAlpha,
                                        float sheetSmoothing) {
    return 0;
  }

  public void addSecondaryStructure(byte type, 
                                    String structureID, int serialID, int strandCount,
                                    char startChainID,
                                    int startSeqcode, char endChainID,
                                    int endSeqcode) {
  }

  public void freeze() {  
  }
  
  public void calculateStructures() {
  }

  public void clearStructures() {
  }

  public String getSequence() {
    return "";
  }

  public Hashtable getPolymerInfo(BitSet bs) {
    return null;
  }

  public void setConformation(BitSet bsConformation, int nAltLocs) {
  }

  public void calcHydrogenBonds(Polymer polymer, BitSet bsA, BitSet bsB) {
    
  }
  
  public void calcSelectedMonomersCount(BitSet bsSelected) {
  }

  public void getPolymerSequenceAtoms(int iModel, int iPolymer, int group1,
                                      int nGroups, BitSet bsInclude,
                                      BitSet bsResult) {
  }

  public Point3f[] getLeadMidpoints() {
    return null;
  }
  
  public void recalculateLeadMidpointsAndWingVectors() {  
  }
  
  public void getPdbData(Viewer viewer, char ctype, char qtype, int mStep, int derivType, 
              boolean isDraw, BitSet bsAtoms, StringBuffer pdbATOM, 
              StringBuffer pdbCONECT, BitSet bsSelected, boolean addHeader, 
              BitSet bsWritten) {
    return;
  }

  public Vector calculateStruts(ModelSet modelSet, Atom[] atoms, BitSet bs1, BitSet bs2, Vector vCA, float thresh, int delta, boolean allowMultiple) {
    return null;
  }

}
