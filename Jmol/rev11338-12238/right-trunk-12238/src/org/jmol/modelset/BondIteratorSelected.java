

package org.jmol.modelset;

import org.jmol.viewer.JmolConstants;
import java.util.BitSet;

class BondIteratorSelected implements BondIterator {

  private Bond[] bonds;
  private int bondCount;
  private int bondType;
  private int iBond;
  private BitSet bsSelected;
  private boolean bondSelectionModeOr;
  private boolean isBondBitSet;

  BondIteratorSelected(Bond[] bonds, int bondCount, int bondType,
      BitSet bsSelected, boolean bondSelectionModeOr) {
    this.bonds = bonds;
    this.bondCount = bondCount;
    this.bondType = bondType;
    this.bsSelected = bsSelected;
    this.bondSelectionModeOr = bondSelectionModeOr;
    isBondBitSet = false;
    iBond = 0;
  }

  BondIteratorSelected(Bond[] bonds, int bondCount, BitSet bsSelected) {
    this.bonds = bonds;
    this.bondCount = bondCount;
    this.bsSelected = bsSelected;
    isBondBitSet = true;
    iBond = 0;
  }

  public boolean hasNext() {
    for (; iBond < bondCount; ++iBond) {
      Bond bond = bonds[iBond];
      if (isBondBitSet) {
        if (bsSelected.get(iBond))
          return true;
        continue;
      } else if (bondType != JmolConstants.BOND_ORDER_ANY
          && (bond.order & bondType) == 0) {
        continue;
      } else if (bondType == JmolConstants.BOND_ORDER_ANY
          && (bond.order & JmolConstants.BOND_STRUT_MASK) != 0)
          continue;
      boolean isSelected1 = bsSelected.get(bond.atom1.index);
      boolean isSelected2 = bsSelected.get(bond.atom2.index);
      if ((!bondSelectionModeOr && isSelected1 && isSelected2)
          || (bondSelectionModeOr && (isSelected1 || isSelected2)))
        return true;
    }
    return false;
  }

  public int nextIndex() {
    return iBond;
  }

  public Bond next() {
    return bonds[iBond++];
  }
}
