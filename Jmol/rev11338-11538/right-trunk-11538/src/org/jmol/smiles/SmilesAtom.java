

package org.jmol.smiles;


public class SmilesAtom {

  private int number;
  private String symbol;
  private int atomicMass;
  private int charge;
  private int hydrogenCount;
  private int matchingAtom;
  private String chiralClass;
  private int chiralOrder;

  private SmilesBond[] bonds;
  private int bondsCount;

  private final static int INITIAL_BONDS = 4;

  
  
  public final static String DEFAULT_CHIRALITY = "";
  
  public final static String CHIRALITY_ALLENE = "AL";
  
  public final static String CHIRALITY_OCTAHEDRAL = "OH";
  
  public final static String CHIRALITY_SQUARE_PLANAR = "SP";
  
  public final static String CHIRALITY_TETRAHEDRAL = "TH";
  
  public final static String CHIRALITY_TRIGONAL_BIPYRAMIDAL = "TB";

  
  public SmilesAtom(int number) {
    this.number = number;
    this.symbol = null;
    this.atomicMass = Integer.MIN_VALUE;
    this.charge = 0;
    this.hydrogenCount = Integer.MIN_VALUE;
    this.matchingAtom = -1;
    this.chiralClass = null;
    this.chiralOrder = Integer.MIN_VALUE;
    bonds = new SmilesBond[INITIAL_BONDS];
    bondsCount = 0;
  }

  
  public void createMissingHydrogen(SmilesMolecule molecule) {
  	
  	int count = 0;
  	if (hydrogenCount == Integer.MIN_VALUE) {
      if (symbol != null) {
        if (symbol == "B") {
          count = 3;
        } else if (symbol == "Br") {
          count = 1;
        } else if (symbol == "C") {
          count = 4;
        } else if (symbol == "Cl") {
          count = 1;
        } else if (symbol == "F") {
          count = 1;
        } else if (symbol == "I") {
          count = 1;
        } else if (symbol == "N") {
          count = 3;
        } else if (symbol == "O") {
          count = 2;
        } else if (symbol == "P") {
          count = 3;
        } else if (symbol == "S") {
          count = 2;
        }
      }
      for (int i = 0; i < bondsCount; i++) {
        SmilesBond bond = bonds[i];
        switch (bond.getBondType()) {
        case SmilesBond.TYPE_SINGLE:
        case SmilesBond.TYPE_DIRECTIONAL_1:
        case SmilesBond.TYPE_DIRECTIONAL_2:
          count -= 1;
          break;
        case SmilesBond.TYPE_DOUBLE:
          count -= 2;
          break;
        case SmilesBond.TYPE_TRIPLE:
          count -= 3;
          break;
        }
      }
  	} else {
  	  count = hydrogenCount;
  	}

    
    for (int i = 0; i < count; i++) {
      SmilesAtom hydrogen = molecule.createAtom();
      molecule.createBond(this, hydrogen, SmilesBond.TYPE_SINGLE);
      hydrogen.setSymbol("H");
    }
  }

  
  public int getNumber() {
    return number;
  }

  
  public String getSymbol() {
    return symbol;
  }

  
  public void setSymbol(String symbol) {
    this.symbol = (symbol != null) ? symbol.intern() : null;
  }

  
  public int getAtomicMass() {
    return atomicMass;
  }

  
  public void setAtomicMass(int mass) {
    this.atomicMass = mass;
  }
  
  
  public int getCharge() {
    return charge;
  }

  
  public void setCharge(int charge) {
    this.charge = charge;
  }

  
  public int getMatchingAtom() {
    return matchingAtom;
  }

  
  public void setMatchingAtom(int atom) {
    this.matchingAtom = atom;
  }

  
  public String getChiralClass() {
    return chiralClass;
  }

  
  public void setChiralClass(String chiralClass) {
    this.chiralClass = (chiralClass != null) ? chiralClass.intern() : null;
  }

  
  public int getChiralOrder() {
    return chiralOrder;
  }

  
  public void setChiralOrder(int chiralOrder) {
    this.chiralOrder = chiralOrder;
  }

  
  public int getHydrogenCount() {
    return hydrogenCount;
  }

  
  public void setHydrogenCount(int count) {
    this.hydrogenCount = count;
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
  
  
  public void addBond(SmilesBond bond) {
    if (bondsCount >= bonds.length) {
      SmilesBond[] tmp = new SmilesBond[bonds.length * 2];
      System.arraycopy(bonds, 0, tmp, 0, bonds.length);
      bonds = tmp;
    }
    bonds[bondsCount] = bond;
    bondsCount++;
  }
}
