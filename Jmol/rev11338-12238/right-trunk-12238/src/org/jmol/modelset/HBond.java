

package org.jmol.modelset;

import org.jmol.util.Logger;

public class HBond extends Bond {

  private float energy;
  private byte paletteID;
  
  HBond(Atom atom1, Atom atom2, int order, short mad, short colix, float energy) {
    super(atom1, atom2, order, mad, colix);
    if (Logger.debugging)
       Logger.debug("HBond energy = " + energy + " for #" + atom1.getIndex() 
           + " " + atom1.getInfoXYZ(false) + ", #" + atom2.getIndex() + " " + atom2.getInfoXYZ(false));
    this.energy = energy;
  }
  
  public float getEnergy() {
    return energy;
  }
  
  public byte getPaletteId() {
    return paletteID;
  }
  
  public void setPaletteID(byte paletteID) {
    this.paletteID = paletteID;
  }

}
