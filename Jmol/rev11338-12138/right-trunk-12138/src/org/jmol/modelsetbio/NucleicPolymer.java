
package org.jmol.modelsetbio;

import java.util.BitSet;

import org.jmol.modelset.Atom;
import org.jmol.modelset.Polymer;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;

public class NucleicPolymer extends BioPolymer {

  NucleicPolymer(Monomer[] monomers) {
    super(monomers);
    type = TYPE_NUCLEIC;
  }

  Atom getNucleicPhosphorusAtom(int monomerIndex) {
    return monomers[monomerIndex].getLeadAtom();
  }

  boolean hasWingPoints() { return true; }

  public void calcHydrogenBonds(Polymer polymer, BitSet bsA, BitSet bsB) {
    lookForHbonds((NucleicPolymer)polymer, bsA, bsB);
  }

  private final static short HBOND_MASK = JmolConstants.BOND_H_NUCLEOTIDE;
  
  void lookForHbonds(NucleicPolymer other, BitSet bsA, BitSet bsB) {
    
    for (int i = monomerCount; --i >= 0; ) {
      NucleicMonomer myNucleotide = (NucleicMonomer)monomers[i];
      if (! myNucleotide.isPurine())
        continue;
      Atom myN1 = myNucleotide.getN1();
      Atom bestN3 = null;
      float minDist2 = 25;
      NucleicMonomer bestNucleotide = null;
      for (int j = other.monomerCount; --j >= 0; ) {
        NucleicMonomer otherNucleotide = (NucleicMonomer)other.monomers[j];
        if (! otherNucleotide.isPyrimidine())
          continue;
        Atom otherN3 = otherNucleotide.getN3();
        float dist2 = myN1.distanceSquared(otherN3);
        if (dist2 < minDist2) {
          bestNucleotide = otherNucleotide;
          bestN3 = otherN3;
          minDist2 = dist2;
        }
      }
      if (bestN3 != null) {
        model.addHydrogenBond(myN1, bestN3,  HBOND_MASK, bsA, bsB, 0);
        if (myNucleotide.isGuanine()) {
          model.addHydrogenBond(myNucleotide.getN2(),
                             bestNucleotide.getO2(), HBOND_MASK, bsA, bsB, 0);
          model.addHydrogenBond(myNucleotide.getO6(),
                             bestNucleotide.getN4(), HBOND_MASK, bsA, bsB, 0);
        } else {
          model.addHydrogenBond(myNucleotide.getN6(),
                             bestNucleotide.getO4(), HBOND_MASK, bsA, bsB, 0);
        }
      }
    }
  }

  public void getPdbData(Viewer viewer, char ctype, char qtype, int mStep, int derivType,
                         boolean isDraw, BitSet bsAtoms, 
                         StringBuffer pdbATOM, StringBuffer pdbCONECT, 
                         BitSet bsSelected, boolean addHeader, BitSet bsWritten) {
    getPdbData(viewer, this, ctype, qtype, mStep, derivType, isDraw, bsAtoms, pdbATOM, 
        pdbCONECT, bsSelected, addHeader, bsWritten);
  }   
}
