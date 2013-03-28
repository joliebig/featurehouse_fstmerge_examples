

package org.jmol.smiles;

import java.util.BitSet;

import org.jmol.api.SmilesMatcherInterface;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Bond;
import org.jmol.modelset.ModelSet;
import org.jmol.viewer.JmolConstants;


public class PatternMatcher implements SmilesMatcherInterface {

  private int atomCount;
  private ModelSet modelSet;

  
  public PatternMatcher() {
  }
  public void setModelSet(ModelSet modelSet) {
    this.modelSet = modelSet;
    atomCount = (modelSet == null ? 0 : modelSet.getAtomCount());     
  }
  
  public BitSet getSubstructureSet(String smiles) throws Exception {
    SmilesParser parser = new SmilesParser();
    SmilesMolecule pattern = parser.parseSmiles(smiles);
    return getSubstructureSet(pattern);
  }

  
  public BitSet getSubstructureSet(SmilesMolecule pattern) {
    BitSet bsSubstructure = new BitSet();
    searchMatch(bsSubstructure, pattern, 0);
    return bsSubstructure;
  }

  
  private void searchMatch(BitSet bs, SmilesMolecule pattern, int atomNum) {
    
    SmilesAtom patternAtom = pattern.getAtom(atomNum);
    for (int i = 0; i < patternAtom.getBondsCount(); i++) {
      SmilesBond patternBond = patternAtom.getBond(i);
      if (patternBond.getAtom2() == patternAtom) {
        int matchingAtom = patternBond.getAtom1().getMatchingAtom();
        Atom atom = modelSet.getAtomAt(matchingAtom);
        Bond[] bonds = atom.getBonds();
        if (bonds != null) {
          for (int j = 0; j < bonds.length; j++) {
            if (bonds[j].getAtomIndex1() == matchingAtom) {
              searchMatch(bs, pattern, patternAtom, atomNum, bonds[j].getAtomIndex2());
            }
            if (bonds[j].getAtomIndex2() == matchingAtom) {
              searchMatch(bs, pattern, patternAtom, atomNum, bonds[j].getAtomIndex1());
            }
          }
        }
        return;
      }
    }
    for (int i = 0; i < atomCount; i++) {
      searchMatch(bs, pattern, patternAtom, atomNum, i);
    }
    
  }
  
  
  private void searchMatch(BitSet bs, SmilesMolecule pattern, SmilesAtom patternAtom, int atomNum, int i) {
    
    for (int j = 0; j < atomNum; j++) {
      SmilesAtom previousAtom = pattern.getAtom(j);
      if (previousAtom.getMatchingAtom() == i) {
        return;
      }
    }
    
    Atom atom = modelSet.getAtomAt(i);

    
    String targetSym = patternAtom.getSymbol();
    int n = atom.getElementNumber();
    if (targetSym != "*" && targetSym != JmolConstants.elementSymbolFromNumber(n))
      return;
    
    int targetMass = patternAtom.getAtomicMass();
    if (targetMass > 0) {
      
      
      int isotopeMass = atom.getIsotopeNumber();
      if (isotopeMass != targetMass)
          return;
    }
    
    if (patternAtom.getCharge() != atom.getFormalCharge())
      return;

    
    for (int j = 0; j < patternAtom.getBondsCount(); j++) {
      SmilesBond patternBond = patternAtom.getBond(j);
      
      if (patternBond.getAtom2() == patternAtom) {
        int matchingAtom = patternBond.getAtom1().getMatchingAtom();
        Bond[] bonds = atom.getBonds();
        boolean bondFound = false;
        for (int k = 0; k < bonds.length; k++) {
          if ((bonds[k].getAtomIndex1() == matchingAtom) ||
              (bonds[k].getAtomIndex2() == matchingAtom)) {
            switch (patternBond.getBondType()) {
            case SmilesBond.TYPE_AROMATIC:
              if ((bonds[k].getOrder() & JmolConstants.BOND_AROMATIC_MASK) != 0) {
                bondFound = true;
              }
              break;
            case SmilesBond.TYPE_DOUBLE:
              if ((bonds[k].getOrder() & JmolConstants.BOND_COVALENT_DOUBLE) != 0) {
                bondFound = true;
              }
              break;
            case SmilesBond.TYPE_SINGLE:
            case SmilesBond.TYPE_DIRECTIONAL_1:
            case SmilesBond.TYPE_DIRECTIONAL_2:
              if ((bonds[k].getOrder() & JmolConstants.BOND_COVALENT_SINGLE) != 0) {
                bondFound = true;
              }
              break;
            case SmilesBond.TYPE_TRIPLE:
              if ((bonds[k].getOrder() & JmolConstants.BOND_COVALENT_TRIPLE) != 0) {
                bondFound = true;
              }
              break;
            case SmilesBond.TYPE_UNKOWN:
              bondFound = true;
              break;
            }
          }
        }
        if (!bondFound)
          return;
      }
    }

    
      patternAtom.setMatchingAtom(i);
      if (atomNum + 1 < pattern.getAtomsCount()) {
        searchMatch(bs, pattern, atomNum + 1);
      } else {
        for (int k = 0; k < pattern.getAtomsCount(); k++) {
          SmilesAtom matching = pattern.getAtom(k);
          bs.set(matching.getMatchingAtom());
        }
      }
      patternAtom.setMatchingAtom(-1);
  }
}
