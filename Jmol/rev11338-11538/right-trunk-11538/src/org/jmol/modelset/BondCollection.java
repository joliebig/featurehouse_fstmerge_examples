

package org.jmol.modelset;

import java.util.BitSet;

import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;
import org.jmol.script.Token;

abstract public class BondCollection extends AtomCollection {

  protected void releaseModelSet() {
    bonds = null;
    freeBonds = null;
    super.releaseModelSet();
  }

  protected void merge(ModelSet modelSet) {
    
    super.merge(modelSet);
  }

  protected Bond[] bonds;
  protected int bondCount;
  
  public Bond[] getBonds() {
    return bonds;
  }

  public Bond getBondAt(int bondIndex) {
    return bonds[bondIndex];
  }

  public int getBondCount() {
    return bondCount;
  }
  
  public BondIterator getBondIterator(short bondType, BitSet bsSelected) {
    
    return new BondIteratorSelected(bonds, bondCount, bondType, bsSelected, 
        viewer.getBondSelectionModeOr());
  }

  public BondIterator getBondIterator(BitSet bsSelected) {
    
    return new BondIteratorSelected(bonds, bondCount, bsSelected);
  }
  
  public Atom getBondAtom1(int i) {
    return bonds[i].atom1;
  }

  public Atom getBondAtom2(int i) {
    return bonds[i].atom2;
  }

  public float getBondRadius(int i) {
    return bonds[i].getRadius();
  }

  public short getBondOrder(int i) {
    return bonds[i].getOrder();
  }

  public short getBondColix1(int i) {
    return bonds[i].getColix1();
  }

  public short getBondColix2(int i) {
    return bonds[i].getColix2();
  }
  
  public int getBondModelIndex(int i) {
    return bonds[i].atom1.atomIndex;
  }

  
  protected int getBondCountInModel(int modelIndex) {
    int n = 0;
    for (int i = bondCount; --i >= 0;)
      if (bonds[i].atom1.modelIndex == modelIndex)
        n++;
    return n;
  }

  public BitSet getBondsForSelectedAtoms(BitSet bsAtoms, boolean bondSelectionModeOr) {
    BitSet bs = new BitSet();
    for (int iBond = 0; iBond < bondCount; ++iBond) {
      Bond bond = bonds[iBond];
      boolean isSelected1 = bsAtoms.get(bond.atom1.atomIndex);
      boolean isSelected2 = bsAtoms.get(bond.atom2.atomIndex);
      if ((!bondSelectionModeOr & isSelected1 & isSelected2)
          || (bondSelectionModeOr & (isSelected1 | isSelected2)))
        bs.set(iBond);
    }
    return bs;
  }

  public Bond bondAtoms(Atom atom1, Atom atom2, short order, short mad, BitSet bsBonds) {
    
    Bond bond = getOrAddBond(atom1, atom2, order, mad, bsBonds);
    bond.order |= JmolConstants.BOND_NEW;
    return bond;
  }

  final protected static boolean showRebondTimes = true;
  private final static int bondGrowthIncrement = 250;

  protected Bond getOrAddBond(Atom atom, Atom atomOther, short order, short mad,
                            BitSet bsBonds) {
    int i;
    if (atom.isBonded(atomOther)) {
      i = atom.getBond(atomOther).index;
    } else {
      if (bondCount == bonds.length)
        bonds = (Bond[]) ArrayUtil.setLength(bonds, bondCount
            + bondGrowthIncrement);
      if (order == JmolConstants.BOND_ORDER_NULL
          || order == JmolConstants.BOND_ORDER_ANY)
        order = 1;
      i = setBond(bondCount++, bondMutually(atom, atomOther, order, mad)).index;
    }
    if (bsBonds != null)
      bsBonds.set(i);
    return bonds[i];
  }

  private Bond getOrAddHBond(Atom atom, Atom atomOther, short order, short mad,
                            BitSet bsBonds, float energy) {
    int i;
    if (atom.isBonded(atomOther)) {
      i = atom.getBond(atomOther).index;
    } else {
      if (bondCount == bonds.length)
        bonds = (Bond[]) ArrayUtil.setLength(bonds, bondCount
            + bondGrowthIncrement);
      if (order == JmolConstants.BOND_ORDER_NULL
          || order == JmolConstants.BOND_ORDER_ANY)
        order = 1;
      i = setBond(bondCount++, hBondMutually(atom, atomOther, order, mad, energy)).index;
    }
    if (bsBonds != null)
      bsBonds.set(i);
    return bonds[i];
  }

  protected Bond setBond(int index, Bond bond) {
    return bonds[bond.index = index] = bond;
  }

  protected Bond hBondMutually(Atom atom, Atom atomOther, short order, short mad, float energy) {
    Bond bond = new HBond(atom, atomOther, order, mad, (short) 0, energy);
    addBondToAtom(atom, bond);
    addBondToAtom(atomOther, bond);
    return bond;
  }

  protected Bond bondMutually(Atom atom, Atom atomOther, short order, short mad) {
    Bond bond = new Bond(atom, atomOther, order, mad, (short) 0);
    addBondToAtom(atom, bond);
    addBondToAtom(atomOther, bond);
    return bond;
  }

  private void addBondToAtom(Atom atom, Bond bond) {
    if (atom.bonds == null) {
      atom.bonds = new Bond[1];
      atom.bonds[0] = bond;
    } else {
      atom.bonds = addToBonds(bond, atom.bonds);
    }
  }

  protected final static int MAX_BONDS_LENGTH_TO_CACHE = 5;
  protected final static int MAX_NUM_TO_CACHE = 200;
  protected int[] numCached = new int[MAX_BONDS_LENGTH_TO_CACHE];
  protected Bond[][][] freeBonds = new Bond[MAX_BONDS_LENGTH_TO_CACHE][][];
  {
    for (int i = MAX_BONDS_LENGTH_TO_CACHE; --i > 0;)
      
      freeBonds[i] = new Bond[MAX_NUM_TO_CACHE][];
  }

  private Bond[] addToBonds(Bond newBond, Bond[] oldBonds) {
    Bond[] newBonds;
    if (oldBonds == null) {
      if (numCached[1] > 0)
        newBonds = freeBonds[1][--numCached[1]];
      else
        newBonds = new Bond[1];
      newBonds[0] = newBond;
    } else {
      int oldLength = oldBonds.length;
      int newLength = oldLength + 1;
      if (newLength < MAX_BONDS_LENGTH_TO_CACHE && numCached[newLength] > 0)
        newBonds = freeBonds[newLength][--numCached[newLength]];
      else
        newBonds = new Bond[newLength];
      newBonds[oldLength] = newBond;
      for (int i = oldLength; --i >= 0;)
        newBonds[i] = oldBonds[i];
      if (oldLength < MAX_BONDS_LENGTH_TO_CACHE
          && numCached[oldLength] < MAX_NUM_TO_CACHE)
        freeBonds[oldLength][numCached[oldLength]++] = oldBonds;
    }
    return newBonds;
  }

  
  
  protected BitSet bsPseudoHBonds;

  
  void addHydrogenBond(Atom atom1, Atom atom2, short order, BitSet bsA,
                       BitSet bsB, float energy) {
    if (atom1 == null || atom2 == null)
      return;
    boolean atom1InSetA = bsA == null || bsA.get(atom1.atomIndex);
    boolean atom1InSetB = bsB == null || bsB.get(atom1.atomIndex);
    boolean atom2InSetA = bsA == null || bsA.get(atom2.atomIndex);
    boolean atom2InSetB = bsB == null || bsB.get(atom2.atomIndex);
    if (atom1InSetA && atom2InSetB || atom1InSetB && atom2InSetA)
      getOrAddHBond(atom1, atom2, order, (short) 1, bsPseudoHBonds, energy);
  }
 
  protected short getBondOrder(Atom atomA, float bondingRadiusA, Atom atomB,
                             float bondingRadiusB, float distance2,
                             float minBondDistance2, float bondTolerance) {
    if (bondingRadiusA == 0 || bondingRadiusB == 0 || distance2 < minBondDistance2)
      return 0;
    float maxAcceptable = bondingRadiusA + bondingRadiusB + bondTolerance;
    float maxAcceptable2 = maxAcceptable * maxAcceptable;
    return (distance2 > maxAcceptable2 ? (short) 0 : (short) 1);
  }

  private boolean haveWarned = false;

  protected boolean checkValencesAndBond(Atom atomA, Atom atomB, short order, short mad,
                            BitSet bsBonds) {
    if (atomA.getCurrentBondCount() > JmolConstants.MAXIMUM_AUTO_BOND_COUNT
        || atomB.getCurrentBondCount() > JmolConstants.MAXIMUM_AUTO_BOND_COUNT) {
      if (!haveWarned)
        Logger.warn("maximum auto bond count reached");
      haveWarned = true;
      return false;
    }
    int formalChargeA = atomA.getFormalCharge();
    if (formalChargeA != 0) {
      int formalChargeB = atomB.getFormalCharge();
      if ((formalChargeA < 0 && formalChargeB < 0)
          || (formalChargeA > 0 && formalChargeB > 0))
        return false;
    }
    if (atomA.alternateLocationID != atomB.alternateLocationID
        && atomA.alternateLocationID != '\0' && atomB.alternateLocationID != '\0')
      return false;
    getOrAddBond(atomA, atomB, order, mad, bsBonds);
    return true;
  }

  protected void deleteAllBonds() {
    viewer.setShapeProperty(JmolConstants.SHAPE_STICKS, "reset", null);
    for (int i = bondCount; --i >= 0;) {
      bonds[i].deleteAtomReferences();
      bonds[i] = null;
    }
    bondCount = 0;
  }

  protected short defaultCovalentMad;

  
  protected short getDefaultMadFromOrder(short order) {
    return (short) ((order & JmolConstants.BOND_HYDROGEN_MASK) > 0 ? 1
        : defaultCovalentMad);
  }

  protected int[] deleteConnections(float minDistance, float maxDistance, short order,
                        BitSet bsA, BitSet bsB, boolean isBonds, 
                        boolean matchNull, 
                        float minDistanceSquared, float maxDistanceSquared) {
    BitSet bsDelete = new BitSet();
    int nDeleted = 0;
    int newOrder = order |= JmolConstants.BOND_NEW;
    if (!matchNull && (order & JmolConstants.BOND_HYDROGEN_MASK) != 0)
      order = JmolConstants.BOND_HYDROGEN_MASK;
    for (int i = bondCount; --i >= 0;) {
      Bond bond = bonds[i];
      Atom atom1 = bond.atom1;
      Atom atom2 = bond.atom2;
      if (!isBonds
          && (bsA.get(atom1.atomIndex) && bsB.get(atom2.atomIndex) || bsA
              .get(atom2.atomIndex)
              && bsB.get(atom1.atomIndex)) || isBonds && bsA.get(i)) {
        if (bond.atom1.isBonded(bond.atom2)) {
          float distanceSquared = atom1.distanceSquared(atom2);
          if (distanceSquared >= minDistanceSquared
              && distanceSquared <= maxDistanceSquared
              && (matchNull || newOrder == 
                (bond.order & ~JmolConstants.BOND_SULFUR_MASK | JmolConstants.BOND_NEW)
                || (order & bond.order & JmolConstants.BOND_HYDROGEN_MASK) != 0)) {
              bsDelete.set(i);
              nDeleted++;
            }
        }
      }
    }
    if (nDeleted > 0) 
      deleteBonds(bsDelete);
    return new int[] {0, nDeleted};
  }

  protected void deleteBonds(BitSet bs) {
    int iDst = 0;
    for (int iSrc = 0; iSrc < bondCount; ++iSrc) {
      Bond bond = bonds[iSrc];
      if (!bs.get(iSrc))
        setBond(iDst++, bond);
      else
        bond.deleteAtomReferences();
    }
    for (int i = bondCount; --i >= iDst;)
      bonds[i] = null;
    bondCount = iDst;
    BitSet[] sets = (BitSet[]) viewer.getShapeProperty(
        JmolConstants.SHAPE_STICKS, "sets");
    for (int i = 0; i < sets.length; i++)
      BitSetUtil.deleteBits(sets[i], bs);
    BitSetUtil.deleteBits(bsPseudoHBonds, bs);
    BitSetUtil.deleteBits(bsAromatic, bs);
  }


  

  private BitSet bsAromaticSingle;
  private BitSet bsAromaticDouble;
  protected BitSet bsAromatic = new BitSet();

  public void resetAromatic() {
    for (int i = bondCount; --i >= 0;) {
      Bond bond = bonds[i];
      if (bond.isAromatic())
        bond.setOrder(JmolConstants.BOND_AROMATIC);
    }
  }
  
  public void assignAromaticBonds() {
    assignAromaticBonds(true, null);
  }

  
  protected void assignAromaticBonds(boolean isUserCalculation, BitSet bsBonds) {
    
    
    
    if (!isUserCalculation)
      bsAromatic = new BitSet();

    

    bsAromaticSingle = new BitSet();
    bsAromaticDouble = new BitSet();
    for (int i = bondCount; --i >= 0;)
      if (bsBonds == null || bsBonds.get(i)) {
        Bond bond = bonds[i];
        if (bsAromatic.get(i))
          bond.setOrder(JmolConstants.BOND_AROMATIC);
        switch (bond.order & ~JmolConstants.BOND_NEW) {
        case JmolConstants.BOND_AROMATIC:
          bsAromatic.set(i);
          break;
        case JmolConstants.BOND_AROMATIC_SINGLE:
          bsAromaticSingle.set(i);
          break;
        case JmolConstants.BOND_AROMATIC_DOUBLE:
          bsAromaticDouble.set(i);
          break;
        }
      }
    
    Bond bond;
    for (int i = bondCount; --i >= 0;)
      if (bsBonds == null || bsBonds.get(i)) {
        bond = bonds[i];
        if (!bond.is(JmolConstants.BOND_AROMATIC)
            || bsAromaticDouble.get(i) || bsAromaticSingle.get(i))
          continue;
        if (!assignAromaticDouble(bond))
          assignAromaticSingle(bond);
      }
    
    for (int i = bondCount; --i >= 0;)
      if (bsBonds == null || bsBonds.get(i)) {
        bond = bonds[i];
        if (bsAromaticDouble.get(i)) {
          if (!bond.is(JmolConstants.BOND_AROMATIC_DOUBLE)) {
            bsAromatic.set(i);
            bond.setOrder(JmolConstants.BOND_AROMATIC_DOUBLE);
          }
        } else if (bsAromaticSingle.get(i) || bond.isAromatic()) {
          if (!bond.is(JmolConstants.BOND_AROMATIC_SINGLE)) {
            bsAromatic.set(i);
            bond.setOrder(JmolConstants.BOND_AROMATIC_SINGLE);
          }
        }
      }

    assignAromaticNandO(bsBonds);

    bsAromaticSingle = null;
    bsAromaticDouble = null;    
  }

  
  private boolean assignAromaticDouble(Bond bond) {
    int bondIndex = bond.index;
    if (bsAromaticSingle.get(bondIndex))
      return false;
    if (bsAromaticDouble.get(bondIndex))
      return true;
    bsAromaticDouble.set(bondIndex);
    if (!assignAromaticSingle(bond.atom1, bondIndex)
        || !assignAromaticSingle(bond.atom2, bondIndex)) {
      bsAromaticDouble.clear(bondIndex);
      return false;
    }
    return true;
  }
  
  
  private boolean assignAromaticSingle(Bond bond) {
    int bondIndex = bond.index;
    if (bsAromaticDouble.get(bondIndex))
      return false;
    if (bsAromaticSingle.get(bondIndex))
      return true;
    bsAromaticSingle.set(bondIndex);
    if (!assignAromaticDouble(bond.atom1) || !assignAromaticDouble(bond.atom2)) {
      bsAromaticSingle.clear(bondIndex);
      return false;
    }
    return true;
  }

  
  private boolean assignAromaticSingle(Atom atom, int notBondIndex) {
    Bond[] bonds = atom.bonds;
    if (assignAromaticSingleHetero(atom))
      return false;
    for (int i = bonds.length; --i >= 0;) {
      Bond bond = bonds[i];
      int bondIndex = bond.index;
      if (bondIndex == notBondIndex || !bond.isAromatic()
          || bsAromaticSingle.get(bondIndex))
        continue;
      if (bsAromaticDouble.get(bondIndex) || !assignAromaticSingle(bond)) {
        return false;
      }
    }
    return true;
  }
 
  
  private boolean assignAromaticDouble(Atom atom) {
    Bond[] bonds = atom.bonds;
    boolean haveDouble = assignAromaticSingleHetero(atom);
    int lastBond = -1;
    for (int i = bonds.length; --i >= 0;) {
      if (bsAromaticDouble.get(bonds[i].index))
        haveDouble = true;
      if (bonds[i].isAromatic())
        lastBond = i;
    }
    for (int i = bonds.length; --i >= 0;) {
      Bond bond = bonds[i];
      int bondIndex = bond.index;
      if (!bond.isAromatic() || bsAromaticDouble.get(bondIndex)
          || bsAromaticSingle.get(bondIndex))
        continue;
      if (!haveDouble && assignAromaticDouble(bond))
        haveDouble = true;
      else if ((haveDouble || i < lastBond) && !assignAromaticSingle(bond)) {
        return false;
      }
    }
    return haveDouble;
  } 
  
  private boolean assignAromaticSingleHetero(Atom atom) {
    
    int n = atom.getElementNumber();
    switch (n) {
    case 6: 
    case 7: 
    case 8: 
    case 16: 
      break;
    default:
      return true;
    }
    int nAtoms = atom.getValence();
    switch (n) {
    case 6: 
      return (nAtoms == 4);
    case 7: 
    case 8: 
      return (nAtoms == 10 - n && atom.getFormalCharge() < 1);
    case 16: 
      return (nAtoms == 18 - n && atom.getFormalCharge() < 1);
    }
    return false;
  }
  
  private void assignAromaticNandO(BitSet bsSelected) {
    Bond bond;
    for (int i = bondCount; --i >= 0;)
      if (bsSelected == null || bsSelected.get(i)) {
        bond = bonds[i];
        if (!bond.is(JmolConstants.BOND_AROMATIC_SINGLE))
          continue;
        Atom atom1;
        Atom atom2 = bond.atom2;
        int n1;
        int n2 = atom2.getElementNumber();
        if (n2 == 7 || n2 == 8) {
          n1 = n2;
          atom1 = atom2;
          atom2 = bond.atom1;
          n2 = atom2.getElementNumber();
        } else {
          atom1 = bond.atom1;
          n1 = atom1.getElementNumber();
        }
        if (n1 != 7 && n1 != 8)
          continue;
        int valence = atom1.getValence();
        int bondorder = atom1.getCovalentBondCount();
        int charge = atom1.getFormalCharge();
        switch (n1) {
        case 7:
          
          
          if (valence == 3 && bondorder == 3 && charge < 1 && n2 == 6
              && atom2.getValence() == 3)
            bond.setOrder(JmolConstants.BOND_AROMATIC_DOUBLE);
          break;
        case 8:
          
          if (valence == 1 && charge == 0 && (n2 == 14 || n2 == 16))
            bond.setOrder(JmolConstants.BOND_AROMATIC_DOUBLE);
          break;
        }
      }
  }

  protected BitSet getAtomBits(int tokType, Object specInfo) {
    BitSet bs;
    switch (tokType) {
    case Token.isaromatic:
      bs = new BitSet();
      for (int i = bondCount; --i >= 0;)
        if (bonds[i].isAromatic()) {
          bs.set(bonds[i].atom1.atomIndex);
          bs.set(bonds[i].atom2.atomIndex);
        }
      return bs;
    case Token.bonds:
      bs = new BitSet();
      BitSet bsBonds = (BitSet) specInfo;
      for (int i = bondCount; --i >= 0;) {
        if (!bsBonds.get(i))
          continue;
        bs.set(bonds[i].atom1.atomIndex);
        bs.set(bonds[i].atom2.atomIndex);
      }
      return bs;
    }
    return super.getAtomBits(tokType, specInfo);
  }
}

