
package org.jmol.modelset;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Properties;

import org.jmol.util.BitSetUtil;
import org.jmol.viewer.StateManager.Orientation;

public final class Model {

  
  
  ModelSet modelSet;
 
  
  public ModelSet getModelSet() {
    return modelSet;
  }

  int modelIndex;   
  int fileIndex;   

  boolean isPDB;
  boolean isTrajectory;
  int trajectoryBaseIndex;
  int selectedTrajectory = -1;
  boolean hasCalculatedHBonds;
  
  Hashtable dataFrames;
  int dataSourceFrame = -1;
  String jmolData; 
  String jmolFrameType;

  
  int firstAtomIndex;  
  int atomCount = 0; 
  final BitSet bsAtoms = new BitSet();

  
  
  private int bondCount = -1;

  public void resetBoundCount() {
    bondCount = -1;    
  }
    
  int getBondCount() {
    if (bondCount >= 0)
      return bondCount;
    Bond[] bonds = modelSet.getBonds();
    bondCount = 0;
    for (int i = modelSet.getBondCount(); --i >= 0;)
      if (bonds[i].atom1.modelIndex == modelIndex)
        bondCount++;
    return bondCount;
  }
  

  int firstMolecule;
  int moleculeCount;
  
  int nAltLocs;
  int nInsertions;
  
  int groupCount = -1;

  int chainCount = 0;
  Chain[] chains = new Chain[8];

  int bioPolymerCount = 0;
  Polymer[] bioPolymers = new Polymer[8];

  int biosymmetryCount;

  Hashtable auxiliaryInfo;
  Properties properties;
  float defaultRotationRadius;

  Orientation orientation;

  Model(ModelSet modelSet, int modelIndex, int trajectoryBaseIndex, 
      String jmolData, Properties properties, Hashtable auxiliaryInfo) {
    this.modelSet = modelSet;
    dataSourceFrame = this.modelIndex = modelIndex;
    this.jmolData = jmolData;
    isTrajectory = (trajectoryBaseIndex >= 0);
    this.trajectoryBaseIndex = (isTrajectory ? trajectoryBaseIndex : modelIndex);
    if (auxiliaryInfo == null)
      auxiliaryInfo = new Hashtable();
    this.auxiliaryInfo = auxiliaryInfo;
    if (auxiliaryInfo.containsKey("biosymmetryCount"))
      biosymmetryCount = ((Integer)auxiliaryInfo.get("biosymmetryCount")).intValue();
    this.properties = properties;
    if (jmolData == null) {
      jmolFrameType = "modelSet";
    } else {
      auxiliaryInfo.put("jmolData", jmolData);
      auxiliaryInfo.put("title", jmolData);
      jmolFrameType = (jmolData.indexOf("ramachandran") >= 0 ? "ramachandran"
          : jmolData.indexOf("quaternion") >= 0 ? "quaternion" 
          : "data");
    }
  }

  void setNAltLocs(int nAltLocs) {
    this.nAltLocs = nAltLocs;  
  }
  
  void setNInsertions(int nInsertions) {
    this.nInsertions = nInsertions;  
  }
  
  void addSecondaryStructure(byte type, 
                             String structureID, int serialID, int strandCount,
                             char startChainID, int startSeqcode,
                             char endChainID, int endSeqcode) {
    for (int i = bioPolymerCount; --i >= 0; ) {
      Polymer polymer = bioPolymers[i];
      polymer.addSecondaryStructure(type, structureID, serialID, strandCount, startChainID, startSeqcode,
                                    endChainID, endSeqcode);
    }
  }

  boolean structureTainted;
  void calculateStructures() {
    structureTainted = modelSet.proteinStructureTainted = true;
    for (int i = bioPolymerCount; --i >= 0; ) {
      bioPolymers[i].clearStructures();
      bioPolymers[i].calculateStructures();
    }
  }

  public boolean isStructureTainted() {
    return structureTainted;
  }
  
  void setConformation(BitSet bsConformation) {
    for (int i = bioPolymerCount; --i >= 0; )
      bioPolymers[i].setConformation(bsConformation, nAltLocs);
  }

  public Chain[] getChains() {
    return chains;
  }

  
  public int getChainCount(boolean countWater) {
    if (chainCount > 1 && !countWater)
      for (int i = 0; i < chainCount; i++)
        if (chains[i].chainID == '\0')
          return chainCount - 1;
    return chainCount;
  }

  public int getGroupCount(boolean isHetero) {
    int n = 0;
    for (int i = chainCount; --i >= 0;)
      for (int j = chains[i].groupCount; --j >= 0;)
        if (chains[i].groups[j].isHetero() == isHetero)
          n++;
    return n;
  }
  
  public int getBioPolymerCount() {
    return bioPolymerCount;
  }

  void calcSelectedGroupsCount(BitSet bsSelected) {
    for (int i = chainCount; --i >= 0; )
      chains[i].calcSelectedGroupsCount(bsSelected);
  }

  void calcSelectedMonomersCount(BitSet bsSelected) {
    for (int i = bioPolymerCount; --i >= 0; )
      bioPolymers[i].calcSelectedMonomersCount(bsSelected);
  }

  void selectSeqcodeRange(int seqcodeA, int seqcodeB, char chainID, BitSet bs,
                          boolean caseSensitive) {
    for (int i = chainCount; --i >= 0;)
      if (chainID == '\t' || chainID == chains[i].chainID || !caseSensitive
          && chainID == Character.toUpperCase(chains[i].chainID))
        for (int index = 0; index >= 0;)
          index = chains[i].selectSeqcodeRange(index, seqcodeA, seqcodeB, bs);
  }

  int getGroupCount() {
    if (groupCount < 0) {
      groupCount = 0;
      for (int i = chainCount; --i >= 0;)
        groupCount += chains[i].getGroupCount();
    }
    return groupCount;
  }

  Chain getChain(char chainID) {
    for (int i = chainCount; --i >= 0; ) {
      Chain chain = chains[i];
      if (chain.getChainID() == chainID)
        return chain;
    }
    return null;
  }

  Chain getChain(int i) {
    return (i < chainCount ? chains[i] : null);
  }

  public Polymer getBioPolymer(int polymerIndex) {
    return bioPolymers[polymerIndex];
  }

  void calcHydrogenBonds(BitSet bsA, BitSet bsB) {
    for (int i = bioPolymerCount; --i >= 0;) {
      int type = bioPolymers[i].getType();
      if (type != Polymer.TYPE_AMINO && type != Polymer.TYPE_NUCLEIC)
        continue;
      if (type == Polymer.TYPE_AMINO)
        bioPolymers[i].calcHydrogenBonds(null, bsA, bsB);
      for (int j = bioPolymerCount; --j >= 0;)
        if (i != j && bioPolymers[i] != null
            && type == bioPolymers[j].getType())
          bioPolymers[j].calcHydrogenBonds(bioPolymers[i], bsA, bsB);
    }
  }
  
  public boolean isAtomHidden(int index) {
    return modelSet.isAtomHidden(index);
  }
  
  public void addHydrogenBond(Atom atom1, Atom atom2, short order, BitSet bsA, BitSet bsB, float energy) {
    hasCalculatedHBonds = true;
    modelSet.addHydrogenBond(atom1, atom2, order, bsA, bsB, energy);
    
  }

  public int getModelIndex() {
    return modelIndex;
  }

  void fixIndices(int modelIndex, int nAtomsDeleted, BitSet bsDeleted) {
    if (dataSourceFrame > modelIndex)
      dataSourceFrame--;
    if (trajectoryBaseIndex > modelIndex)
      trajectoryBaseIndex--;
    firstAtomIndex -= nAtomsDeleted;
    for (int i = 0; i < chainCount; i++)
      chains[i].fixIndices(nAtomsDeleted);
    for (int i = 0; i < bioPolymerCount; i++)
      bioPolymers[i].recalculateLeadMidpointsAndWingVectors();
    BitSetUtil.deleteBits(bsAtoms, bsDeleted);
  }
}
