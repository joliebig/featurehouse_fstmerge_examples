

package org.jmol.smiles;


public class SmilesMolecule {

  private SmilesAtom[] atoms;
  private int atomsCount;
  private SmilesBond[] bonds;
  private int bondsCount;

  private final static int INITIAL_ATOMS = 16;
  private final static int INITIAL_BONDS = 16;
  
  
  public SmilesMolecule() {
    atoms = new SmilesAtom[INITIAL_ATOMS];
    atomsCount = 0;
    bonds = new SmilesBond[INITIAL_BONDS];
    bondsCount = 0;
  }
  
  
  
  

  public SmilesAtom createAtom() {
    if (atomsCount >= atoms.length) {
      SmilesAtom[] tmp = new SmilesAtom[atoms.length * 2];
      System.arraycopy(atoms, 0, tmp, 0, atoms.length);
      atoms = tmp;
    }
    SmilesAtom atom = new SmilesAtom(atomsCount);
    atoms[atomsCount] = atom;
    atomsCount++;
    return atom;
  }

  public int getAtomsCount() {
    return atomsCount;
  }

  public SmilesAtom getAtom(int number) {
    if ((number >= 0) && (number < atomsCount)) {
      return atoms[number];
    }
    return null;
  }
  
  
  
  

  public SmilesBond createBond(
      SmilesAtom atom1,
      SmilesAtom atom2,
      int bondType) {
    if (bondsCount >= bonds.length) {
      SmilesBond[] tmp = new SmilesBond[bonds.length * 2];
      System.arraycopy(bonds, 0, tmp, 0, bonds.length);
      bonds = tmp;
    }
    SmilesBond bond = new SmilesBond(atom1, atom2, bondType);
    bonds[bondsCount] = bond;
    bondsCount++;
    if (atom1 != null) {
      atom1.addBond(bond);
    }
    if (atom2 != null) {
      atom2.addBond(bond);
    }
    return bond;
  }

  public int getBondsCount() {
    return bondsCount;
  }

  public SmilesBond getBond(int number) {
    if ((number >= 0) && (number < bondsCount)) {
      return bonds[number];
    }
    return null;
  }
}
