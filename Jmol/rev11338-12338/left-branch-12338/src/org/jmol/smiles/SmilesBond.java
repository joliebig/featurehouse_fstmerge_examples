

package org.jmol.smiles;


public class SmilesBond {

  
  public final static int TYPE_UNKOWN = -1;
  public final static int TYPE_NONE = 0;
  public final static int TYPE_SINGLE = 1;
  public final static int TYPE_DOUBLE = 2;
  public final static int TYPE_TRIPLE = 3;
  public final static int TYPE_AROMATIC = 4;
  public final static int TYPE_DIRECTIONAL_1 = 5;
  public final static int TYPE_DIRECTIONAL_2 = 6;

  
  public final static char CODE_NONE = '.';
  public final static char CODE_SINGLE = '-';
  public final static char CODE_DOUBLE = '=';
  public final static char CODE_TRIPLE = '#';
  public final static char CODE_AROMATIC = ':';
  public final static char CODE_DIRECTIONAL_1 = '/';
  public final static char CODE_DIRECTIONAL_2 = '\\';

  private SmilesAtom atom1;
  private SmilesAtom atom2;
  private int bondType;
  
  
  public SmilesBond(SmilesAtom atom1, SmilesAtom atom2, int bondType) {
    this.atom1 = atom1;
    this.atom2 = atom2;
    this.bondType = bondType;
  }

  
  public static int getBondTypeFromCode(char code) {
    switch (code) {
    case CODE_NONE:
      return TYPE_NONE;
    case CODE_SINGLE:
      return TYPE_SINGLE;
    case CODE_DOUBLE:
      return TYPE_DOUBLE;
    case CODE_TRIPLE:
      return TYPE_TRIPLE;
    case CODE_AROMATIC:
      return TYPE_AROMATIC;
    case CODE_DIRECTIONAL_1:
      return TYPE_DIRECTIONAL_1;
    case CODE_DIRECTIONAL_2:
      return TYPE_DIRECTIONAL_2;
    }
    return TYPE_UNKOWN;
  }

  public SmilesAtom getAtom1() {
    return atom1;
  }

  public void setAtom1(SmilesAtom atom) {
    this.atom1 = atom;
  }

  public SmilesAtom getAtom2() {
    return atom2;
  }

  public void setAtom2(SmilesAtom atom) {
    this.atom2 = atom;
  }

  public int getBondType() {
    return bondType;
  }

  public void setBondType(int bondType) {
    this.bondType = bondType;
  }
}
