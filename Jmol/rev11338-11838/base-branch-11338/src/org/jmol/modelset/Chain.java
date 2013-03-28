
package org.jmol.modelset;

import java.util.BitSet;

import org.jmol.util.BitSetUtil;

public final class Chain {

  ModelSet modelSet;
  Model model;
  char chainID;
  int groupCount;
  int selectedGroupCount;
  private boolean isDna, isRna;
  BitSet bsSelectedGroups;
  Group[] groups = new Group[16];


  

  public Chain(ModelSet modelSet, Model model, char chainID) {
    this.modelSet = modelSet;
    this.model = model;
    this.chainID = chainID;
  }

  public void setModelSet(ModelSet modelSet) {
    this.modelSet = modelSet;
  }
  
  public char getChainID() {
    return chainID;
  }
  
  public ModelSet getModelSet() {
    return modelSet;
  }
  
  public boolean isDna() { return isDna; }
  public boolean isRna() { return isRna; }

  public void setIsDna(boolean TF) {isDna = TF;}
  public void setIsRna(boolean TF) {isRna = TF;}

  public Group getGroup(int groupIndex) {
    return groups[groupIndex];
  }
  
  public int getGroupCount() {
    return groupCount;
  }

  public int getAtomCount() {
    return groups[groupCount - 1].lastAtomIndex + 1 - groups[0].firstAtomIndex;
  }
  
  public Atom getAtom(int index) {
    return modelSet.atoms[index];
  }
  
  
  public void calcSelectedGroupsCount(BitSet bsSelected) {
    selectedGroupCount = 0;
    if (bsSelectedGroups == null)
      bsSelectedGroups = new BitSet();
    BitSetUtil.clear(bsSelectedGroups);
    for (int i = 0; i < groupCount; i++) {
      if (groups[i].isSelected(bsSelected)) {
        groups[i].selectedIndex = selectedGroupCount++;
        bsSelectedGroups.set(i);
      } else {
        groups[i].selectedIndex = -1;
      }
    }
  }

  public int selectSeqcodeRange(int index0, int seqcodeA, int seqcodeB,
                                BitSet bs) {
    int seqcode, indexA, indexB, minDiff;
    boolean isInexact = false;
    for (indexA = index0; indexA < groupCount
        && groups[indexA].seqcode != seqcodeA; indexA++) {
    }
    if (indexA == groupCount) {
      
      if (index0 > 0)
        return -1;
      isInexact = true;
      minDiff = Integer.MAX_VALUE;
      for (int i = groupCount; --i >= 0;)
        if ((seqcode = groups[i].seqcode) > seqcodeA
            && (seqcode - seqcodeA) < minDiff) {
          indexA = i;
          minDiff = seqcode - seqcodeA;
        }
      if (minDiff == Integer.MAX_VALUE)
        return -1;
    }
    if (seqcodeB == Integer.MAX_VALUE) {
      indexB = groupCount - 1;
      isInexact = true;
    } else {
      for (indexB = indexA; indexB < groupCount
          && groups[indexB].seqcode != seqcodeB; indexB++) {
      }
      if (indexB == groupCount) {
        
        if (index0 > 0)
          return -1;
        isInexact = true;
        minDiff = Integer.MAX_VALUE;
        for (int i = indexA; i < groupCount; i++)
          if ((seqcode = groups[i].seqcode) < seqcodeB
              && (seqcodeB - seqcode) < minDiff) {
            indexB = i;
            minDiff = seqcodeB - seqcode;
          }
        if (minDiff == Integer.MAX_VALUE)
          return -1;
      }
    }
    for (int i = indexA; i <= indexB; ++i)
      groups[i].selectAtoms(bs);
    return (isInexact ? -1 : indexB + 1);
  }
  
  int getSelectedGroupCount() {
    return selectedGroupCount;
  }

  public final void updateOffsetsForAlternativeLocations(BitSet bsSelected,
                                                  int nAltLocInModel,
                                                  byte[] offsets,
                                                  int firstAtomIndex,
                                                  int lastAtomIndex) {

    String[] atomNames = modelSet.getAtomNames();
    for (int offsetIndex = offsets.length; --offsetIndex >= 0;) {
      int offset = offsets[offsetIndex] & 0xFF;
      if (offset == 255)
        continue;
      int iThis = firstAtomIndex + offset;
      Atom atom = getAtom(iThis);
      if (atom.getAlternateLocationID() == 0)
        continue;
      
      
      
      int nScan = lastAtomIndex - firstAtomIndex;
      for (int i = 1; i <= nScan; i++) {
        int iNew = iThis + i;
        if (iNew > lastAtomIndex)
          iNew -= nScan + 1;
        int offsetNew = iNew - firstAtomIndex;
        if (offsetNew < 0 || offsetNew > 255 || iNew == iThis
            || atomNames[iNew] != atomNames[iThis]
            || !bsSelected.get(iNew))
          continue;
        offsets[offsetIndex] = (byte) offsetNew;
        
        break;
      }
    }

  }

  public void fixIndices(int atomsDeleted) {
    for (int i = 0; i < groupCount; i++) {
      groups[i].firstAtomIndex -= atomsDeleted;
      groups[i].lastAtomIndex -= atomsDeleted;
    }
  }
}
