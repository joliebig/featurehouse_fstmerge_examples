
package org.jmol.modelsetbio;

import org.jmol.modelset.Atom;
import org.jmol.modelset.Chain;
import org.jmol.viewer.JmolConstants;

public class PhosphorusMonomer extends Monomer {

  private final static byte[] phosphorusOffsets = { 0 };

  private static float MAX_ADJACENT_PHOSPHORUS_DISTANCE = 8.0f;
 
  protected boolean isPurine;
  protected boolean isPyrimidine;

  static Monomer
    validateAndAllocate(Chain chain, String group3, int seqcode,
                        int firstIndex, int lastIndex,
                        int[] specialAtomIndexes, Atom[] atoms) {
    
    if (firstIndex != lastIndex ||
        specialAtomIndexes[JmolConstants.ATOMID_NUCLEIC_PHOSPHORUS]
        != firstIndex)
      return null;
    return new PhosphorusMonomer(chain, group3, seqcode,
                            firstIndex, lastIndex, phosphorusOffsets);
  }
  
  

  protected PhosphorusMonomer(Chain chain, String group3, int seqcode,
               int firstAtomIndex, int lastAtomIndex,
               byte[] offsets) {
    super(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, offsets);
    if (group3.indexOf('T') >= 0)
      chain.setIsDna(true);
    if (group3.indexOf('U') + group3.indexOf('I') > -2)
        chain.setIsRna(true);
    isPurine = (group3.indexOf('A') + group3.indexOf('G') + group3.indexOf('I') > -3);
    isPyrimidine = (group3.indexOf('T') + group3.indexOf('C') + group3.indexOf('U') > -3);
  }

  boolean isPhosphorusMonomer() { return true; }

  public boolean isDna() { return chain.isDna(); }

  public boolean isRna() { return chain.isRna(); }

  public boolean isPurine() { return isPurine; }
  public boolean isPyrimidine() { return isPyrimidine; }

  public Object getStructure() { return chain; }

  public byte getProteinStructureType() {
    return JmolConstants.PROTEIN_STRUCTURE_NONE;
  }

  boolean isConnectedAfter(Monomer possiblyPreviousMonomer) {
    if (possiblyPreviousMonomer == null)
      return true;
    if (! (possiblyPreviousMonomer instanceof PhosphorusMonomer))
      return false;
    
    
    float distance =
      getLeadAtomPoint().distance(possiblyPreviousMonomer.getLeadAtomPoint());
    return distance <= MAX_ADJACENT_PHOSPHORUS_DISTANCE;
  }
}
