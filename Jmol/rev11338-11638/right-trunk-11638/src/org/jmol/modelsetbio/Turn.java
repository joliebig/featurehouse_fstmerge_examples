
package org.jmol.modelsetbio;

import org.jmol.viewer.JmolConstants;

class Turn extends ProteinStructure {

  Turn(AlphaPolymer apolymer, int monomerIndex, int monomerCount, int id) {
    super(apolymer, JmolConstants.PROTEIN_STRUCTURE_TURN,
          monomerIndex, monomerCount, id);
  }
}

