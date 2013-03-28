

package org.jmol.shapebio;

import java.util.BitSet;

import org.jmol.modelset.Atom;

public class Backbone extends BioShapeCollection {

  BitSet bsSelected;
  
  public void initShape() {
    super.initShape();
    madOn = 1;
    madHelixSheet = 1500;
    madTurnRandom = 500;
    madDnaRna = 2000;
    isActive = true;
  }
  
  public void setProperty(String propertyName, Object value, BitSet bsSelected) {
    if ("bitset" == propertyName) {
      this.bsSelected = (BitSet) value;
      return;
    }
    super.setProperty(propertyName, value, bsSelected);
  }
  
  public void setSize(int size, BitSet bsSelected) {
    short mad = (short) size;
    initialize();
    boolean useThisBsSelected = (this.bsSelected != null);
    if (useThisBsSelected)
      bsSelected = this.bsSelected;
    for (int iShape = bioShapes.length; --iShape >= 0;) {
      BioShape bioShape = bioShapes[iShape];
      if (bioShape.monomerCount ==0)
        continue;
      boolean bondSelectionModeOr = viewer.getBondSelectionModeOr();
      int[] atomIndices = bioShape.bioPolymer.getLeadAtomIndices();
      
      
      
      boolean isVisible = (mad != 0);
      if (bioShape.bsSizeSet == null)
        bioShape.bsSizeSet = new BitSet();
      bioShape.isActive = true;
      for (int i = bioShape.monomerCount - 1; --i >= 0;) {
        int index1 = atomIndices[i];
        int index2 = atomIndices[i + 1];
        boolean isAtom1 = bsSelected.get(index1);
        boolean isAtom2 = bsSelected.get(index2);
        if (isAtom1 && isAtom2 
            || useThisBsSelected && isAtom1 
            || bondSelectionModeOr && (isAtom1 || isAtom2)) {
          bioShape.monomers[i].setShapeVisibility(myVisibilityFlag, isVisible);
          Atom atomA = modelSet.getAtomAt(index1);
          Atom atomB = modelSet.getAtomAt(index2);
          boolean wasVisible = (bioShape.mads[i] != 0); 
          if (wasVisible != isVisible) {
            atomA.addDisplayedBackbone(myVisibilityFlag, isVisible);
            atomB.addDisplayedBackbone(myVisibilityFlag, isVisible);
          }
          bioShape.mads[i] = mad;
          bioShape.bsSizeSet.set(i, isVisible);
          bioShape.bsSizeDefault.set(i, mad == -1);
        }
      }
    }
    if (useThisBsSelected) 
      this.bsSelected = null;
  }
  
  public void setModelClickability() {
    if (bioShapes == null)
      return;
    for (int iShape = bioShapes.length; --iShape >= 0; ) {
      BioShape bioShape = bioShapes[iShape];
      int[] atomIndices = bioShape.bioPolymer.getLeadAtomIndices();
      for (int i = bioShape.monomerCount; --i >= 0; ) {
        Atom atom = modelSet.getAtomAt(atomIndices[i]);
        if (atom.getNBackbonesDisplayed() > 0 && !modelSet.isAtomHidden(i))
          atom.setClickable(myVisibilityFlag);
      }
    }
  }  
}
