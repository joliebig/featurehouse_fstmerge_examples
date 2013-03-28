
package org.jmol.modelsetbio;

import org.jmol.modelset.Atom;
import org.jmol.modelset.Chain;

public class CarbohydrateMonomer extends Monomer {

  final static byte[] alphaOffsets = { 0 };

  static Monomer
    validateAndAllocate(Chain chain, String group3, int seqcode,
                        int firstIndex, int lastIndex,
                        int[] specialAtomIndexes, Atom[] atoms) {
    return new CarbohydrateMonomer(chain, group3, seqcode,
                            firstIndex, lastIndex, alphaOffsets);
  }
  
  

  private CarbohydrateMonomer(Chain chain, String group3, int seqcode,
               int firstAtomIndex, int lastAtomIndex,
               byte[] offsets) {
    super(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, offsets);
  }

  public boolean isCarbohydrate() { return true; }

  boolean isConnectedAfter(Monomer possiblyPreviousMonomer) {
    return true;
  }

}
