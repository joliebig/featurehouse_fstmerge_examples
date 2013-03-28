

package org.jmol.modelsetbio;

import java.util.BitSet;

import org.jmol.modelset.Atom;
import org.jmol.modelset.Chain;
import org.jmol.modelset.Group;
import org.jmol.modelset.Polymer;
import org.jmol.viewer.JmolConstants;
import org.jmol.api.JmolBioResolver;

public final class Resolver implements JmolBioResolver {

  public Group distinguishAndPropagateGroup(Chain chain, String group3,
                                            int seqcode, int firstAtomIndex,
                                            int maxAtomIndex, int modelIndex,
                                            int[] specialAtomIndexes,
                                            byte[] specialAtomIDs, Atom[] atoms) {
    

    int lastAtomIndex = maxAtomIndex - 1;

    int distinguishingBits = 0;

    
    for (int i = JmolConstants.ATOMID_MAX; --i >= 0;)
      specialAtomIndexes[i] = Integer.MIN_VALUE;

    if (specialAtomIDs != null) {
      
      for (int i = maxAtomIndex; --i >= firstAtomIndex;) {
        int specialAtomID = specialAtomIDs[i];
        if (specialAtomID <= 0)
          continue;
        if (specialAtomID < JmolConstants.ATOMID_DISTINGUISHING_ATOM_MAX) {
          
          distinguishingBits |= (1 << specialAtomID);
        }
        specialAtomIndexes[specialAtomID] = i;
      }
    }

    if (lastAtomIndex < firstAtomIndex)
      throw new NullPointerException();

    if ((distinguishingBits & JmolConstants.ATOMID_PROTEIN_MASK) == JmolConstants.ATOMID_PROTEIN_MASK)
      return AminoMonomer.validateAndAllocate(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, specialAtomIndexes, atoms);
    if (distinguishingBits == JmolConstants.ATOMID_ALPHA_ONLY_MASK)
      return AlphaMonomer.validateAndAllocate(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, specialAtomIndexes, atoms);
    if (((distinguishingBits & JmolConstants.ATOMID_NUCLEIC_MASK) == JmolConstants.ATOMID_NUCLEIC_MASK))
      return NucleicMonomer.validateAndAllocate(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, specialAtomIndexes, atoms);
    if (distinguishingBits == JmolConstants.ATOMID_PHOSPHORUS_ONLY_MASK)
      return PhosphorusMonomer.validateAndAllocate(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, specialAtomIndexes, atoms);
    if (JmolConstants.checkCarbohydrate(group3))
      return CarbohydrateMonomer.validateAndAllocate(chain, group3, seqcode,
          firstAtomIndex, lastAtomIndex, specialAtomIndexes, atoms);
    return null;
  }   
  
  public Polymer buildBioPolymer(Group group, Group[] groups, int i, 
                                 boolean checkPolymerConnections) {
    return (group instanceof Monomer && ((Monomer) group).getBioPolymer() == null ?
      BioPolymer.allocateBioPolymer(groups, i, checkPolymerConnections) : null);
  }
  
  public void clearBioPolymers(Group[] groups, int groupCount,
                               BitSet alreadyDefined) {
    for (int i = 0; i < groupCount; ++i) {
      Group group = groups[i];
      if (group instanceof Monomer) {
        Monomer monomer = (Monomer) group;
        if (monomer.getBioPolymer() != null
            && (alreadyDefined == null || !alreadyDefined.get(monomer.getModelIndex())))
          monomer.setBioPolymer(null, -1);
      }
    }

  }
}

