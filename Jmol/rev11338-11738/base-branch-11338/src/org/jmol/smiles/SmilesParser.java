

package org.jmol.smiles;


public class SmilesParser {

  private SmilesBond[] ringBonds;
  
  
  public SmilesParser() {
    ringBonds = null;
  }

  
  public SmilesMolecule parseSmiles(String smiles) throws InvalidSmilesException {
    if (smiles == null) {
      throw new InvalidSmilesException("SMILES expressions must not be null");
    }

    
    SmilesMolecule molecule = new SmilesMolecule();
    parseSmiles(molecule, smiles, null);
    
    
    for (int i = 0; i< molecule.getAtomsCount(); i++) {
      SmilesAtom atom = molecule.getAtom(i);
      atom.createMissingHydrogen(molecule);
    }

    
    if (ringBonds != null) {
      for (int i = 0; i < ringBonds.length; i++) {
        if (ringBonds[i] != null) {
          throw new InvalidSmilesException("Open ring");
        }
      }
    }

    return molecule;
  }

  
  private void parseSmiles(
      SmilesMolecule molecule,
      String         smiles,
      SmilesAtom     currentAtom) throws InvalidSmilesException {
    if ((smiles == null) || (smiles.length() == 0)) {
      return;
    }

    
    int index = 0;
    char firstChar = smiles.charAt(index);
    if (firstChar == '(') {
      index++;
      int currentIndex = index;
      int parenthesisCount = 1;
      while ((currentIndex < smiles.length()) &&
             (parenthesisCount > 0)) {
        switch (smiles.charAt(currentIndex)) {
        case '(':
          parenthesisCount++;
          break;
        case ')':
          parenthesisCount--;
          break;
        }
        currentIndex++;
      }
      if (parenthesisCount != 0) {
        throw new InvalidSmilesException("Unbalanced parenthesis");
      }
      String subSmiles = smiles.substring(index, currentIndex - 1);
      parseSmiles(molecule, subSmiles, currentAtom);
      index = currentIndex;
      if (index >= smiles.length()) {
          throw new InvalidSmilesException("Pattern must not end with ')'");
      }
    }

    
    firstChar = smiles.charAt(index);
    int bondType = SmilesBond.getBondTypeFromCode(firstChar);
    if (bondType != SmilesBond.TYPE_UNKOWN) {
      if (currentAtom == null) {
        throw new InvalidSmilesException("Bond without a previous atom");
      }
      index++;
    }

    
    firstChar = smiles.charAt(index);
    if ((firstChar >= '0') && (firstChar <= '9')) {
      
      String subSmiles = smiles.substring(index, index + 1);
      parseRing(molecule, subSmiles, currentAtom, bondType);
      index++;
    } else if (firstChar == '%') {
      
      index++;
      if ((smiles.charAt(index) < 0) || (smiles.charAt(index) > 9)) {
        throw new InvalidSmilesException("Ring number must follow the % sign");
      }
      int currentIndex = index;
      while ((currentIndex < smiles.length()) &&
             (smiles.charAt(currentIndex) >= '0') &&
             (smiles.charAt(currentIndex) <= '9')) {
        currentIndex++;
      }
      String subSmiles = smiles.substring(index, currentIndex);
      parseRing(molecule, subSmiles, currentAtom, bondType);
      index = currentIndex;
    } else if (firstChar == '[') {
      
      index++;
      int currentIndex = index;
      while ((currentIndex < smiles.length()) &&
             (smiles.charAt(currentIndex) != ']')) {
        currentIndex++;
      }
      if (currentIndex >= smiles.length()) {
        throw new InvalidSmilesException("Unmatched [");
      }
      String subSmiles = smiles.substring(index, currentIndex);
      currentAtom = parseAtom(molecule, subSmiles, currentAtom, bondType, true);
      index = currentIndex + 1;
    } else if (((firstChar >= 'a') && (firstChar <= 'z')) ||
               ((firstChar >= 'A') && (firstChar <= 'Z')) ||
			   (firstChar == '*')) {
      
      int size = 1;
      if (index + 1 < smiles.length()) {
        char secondChar = smiles.charAt(index + 1);
        if ((firstChar >= 'A') && (firstChar <= 'Z') &&
            (secondChar >= 'a') && (secondChar <= 'z')) {
          size = 2;
        }
      }
      String subSmiles = smiles.substring(index, index + size);
      currentAtom = parseAtom(molecule, subSmiles, currentAtom, bondType, false);
      index += size;
    }

    
    if (index == 0) {
      throw new InvalidSmilesException("Unexpected character: " + smiles.charAt(0));
    }
    if (index < smiles.length()) {
      String subSmiles = smiles.substring(index);
      parseSmiles(molecule, subSmiles, currentAtom);
    }
  }

  
  private SmilesAtom parseAtom(
        SmilesMolecule molecule,
        String         smiles,
        SmilesAtom     currentAtom,
        int            bondType,
		boolean        complete) throws InvalidSmilesException {
    if ((smiles == null) || (smiles.length() == 0)) {
      throw new InvalidSmilesException("Empty atom definition");
    }

    
  	int index = 0;
  	char firstChar = smiles.charAt(index);
  	int atomicMass = Integer.MIN_VALUE;
  	if ((firstChar >= '0') && (firstChar <= '9')) {
  	  int currentIndex = index;
  	  while ((currentIndex < smiles.length()) &&
             (smiles.charAt(currentIndex) >= '0') &&
             (smiles.charAt(currentIndex) <= '9')) {
  	    currentIndex++;
  	  }
  	  String sub = smiles.substring(index, currentIndex);
  	  try {
  	    atomicMass = Integer.parseInt(sub);
  	  } catch (NumberFormatException e) {
  	    throw new InvalidSmilesException("Non numeric atomic mass");
  	  }
  	  index = currentIndex;
  	}

  	
  	if (index >= smiles.length()) {
  	  throw new InvalidSmilesException("Missing atom symbol");
  	}
  	firstChar = smiles.charAt(index);
  	if (((firstChar < 'a') || (firstChar > 'z')) &&
        ((firstChar < 'A') || (firstChar > 'Z')) &&
        (firstChar != '*')) {
  	  throw new InvalidSmilesException("Unexpected atom symbol");
  	}
    int size = 1;
    if (index + 1 < smiles.length()) {
      char secondChar = smiles.charAt(index + 1);
      if ((firstChar >= 'A') && (firstChar <= 'Z') &&
          (secondChar >= 'a') && (secondChar <= 'z')) {
        size = 2;
      }
    }
    String atomSymbol = smiles.substring(index, index + size);
    index += size;

    
    String chiralClass = null;
    int chiralOrder = Integer.MIN_VALUE;
    if (index < smiles.length()) {
      firstChar = smiles.charAt(index);
      if (firstChar == '@') {
        index++;
        if (index < smiles.length()) {
          firstChar = smiles.charAt(index);
          if (firstChar == '@') {
            index++;
            chiralClass = SmilesAtom.DEFAULT_CHIRALITY;
            chiralOrder = 2;
          } else if ((firstChar >= 'A') && (firstChar <= 'Z') && (firstChar != 'H')) {
            if (index + 1 < smiles.length()) {
              char secondChar = smiles.charAt(index);
              if ((secondChar >= 'A') && (secondChar <= 'Z')) {
                chiralClass = smiles.substring(index, index + 2);
                index += 2;
                int currentIndex = index;
                while ((currentIndex < smiles.length()) &&
                       (smiles.charAt(currentIndex) >= '0') &&
                       (smiles.charAt(currentIndex) <= '9')) {
                  currentIndex++;
                }
                if (currentIndex > index) {
                  String sub = smiles.substring(index, currentIndex);
                  try {
                    chiralOrder = Integer.parseInt(sub);
                  } catch (NumberFormatException e) {
                    throw new InvalidSmilesException("Non numeric chiral order");
                  }
                } else {
                  chiralOrder = 1;
                }
                index = currentIndex;
              }
            }
          } else {
            chiralClass = SmilesAtom.DEFAULT_CHIRALITY;
            chiralOrder = 1;
          }
        } else {
          chiralClass = SmilesAtom.DEFAULT_CHIRALITY;
          chiralOrder = 1;
        }
      }
    }

    
    int hydrogenCount = Integer.MIN_VALUE;
    if (index < smiles.length()) {
      firstChar = smiles.charAt(index);
      if (firstChar == 'H') {
        index++;
        int currentIndex = index;
        while ((currentIndex < smiles.length()) &&
               (smiles.charAt(currentIndex) >= '0') &&
               (smiles.charAt(currentIndex) <= '9')) {
          currentIndex++;
        }
        if (currentIndex > index) {
          String sub = smiles.substring(index, currentIndex);
          try {
            hydrogenCount = Integer.parseInt(sub);
          } catch (NumberFormatException e) {
            throw new InvalidSmilesException("Non numeric hydrogen count");
          }
        } else {
          hydrogenCount = 1;
        }
        index = currentIndex;
      }
    }
    if ((hydrogenCount == Integer.MIN_VALUE) && (complete)) {
      hydrogenCount = 0;
    }

    
    int charge = 0;
    if (index < smiles.length()) {
      firstChar = smiles.charAt(index);
      if ((firstChar == '+') || (firstChar == '-')) {
        int count = 1;
        index++;
        if (index < smiles.length()) {
          char nextChar = smiles.charAt(index);
          if ((nextChar >= '0') && (nextChar <= '9')) {
            int currentIndex = index;
            while ((currentIndex < smiles.length()) &&
                   (smiles.charAt(currentIndex) >= '0') &&
                   (smiles.charAt(currentIndex) <= '9')) {
              currentIndex++;
            }
            String sub = smiles.substring(index, currentIndex);
            try {
              count = Integer.parseInt(sub);
            } catch (NumberFormatException e) {
              throw new InvalidSmilesException("Non numeric charge");
            }
            index = currentIndex;
          } else {
            int currentIndex = index;
            while ((currentIndex < smiles.length()) &&
                   (smiles.charAt(currentIndex) == firstChar)) {
              currentIndex++;
              count++;
            }
            index = currentIndex;
          }
        }
        if (firstChar == '+') {
          charge = count;
        } else {
          charge = -count;
        }
      }
    }

    
    if (index < smiles.length()) {
      throw new InvalidSmilesException("Unexpected characters after atom definition: " + smiles.substring(index));
    }

    
    if (bondType == SmilesBond.TYPE_UNKOWN) {
      bondType = SmilesBond.TYPE_SINGLE;
    }
    SmilesAtom newAtom = molecule.createAtom();
    if ((currentAtom != null) && (bondType != SmilesBond.TYPE_NONE)) {
      molecule.createBond(currentAtom, newAtom, bondType);
    }
    newAtom.setSymbol(atomSymbol);
    newAtom.setAtomicMass(atomicMass);
    newAtom.setCharge(charge);
    newAtom.setChiralClass(chiralClass);
    newAtom.setChiralOrder(chiralOrder);
    newAtom.setHydrogenCount(hydrogenCount);
    return newAtom;
  }

  
  private void parseRing(
        SmilesMolecule molecule,
        String         smiles,
        SmilesAtom     currentAtom,
        int            bondType) throws InvalidSmilesException {
  	
    int ringNum = 0;
    try {
      ringNum = Integer.parseInt(smiles);
    } catch (NumberFormatException e) {
      throw new InvalidSmilesException("Non numeric ring identifier");
    }
    
    
    if (ringBonds == null) {
      ringBonds = new SmilesBond[10];
      for (int i = 0; i < ringBonds.length; i++) {
        ringBonds[i] = null;
      }
    }
    if (ringNum >= ringBonds.length) {
      SmilesBond[] tmp = new SmilesBond[ringNum + 1];
      for (int i = 0; i < ringBonds.length; i++) {
        tmp[i] = ringBonds[i];
      }
      for (int i = ringBonds.length; i < tmp.length; i++) {
        tmp[i] = null;
      }
    }
    
    
    if (ringBonds[ringNum] == null) {
      ringBonds[ringNum] = molecule.createBond(currentAtom, null, bondType);
    } else {
      if (bondType == SmilesBond.TYPE_UNKOWN) {
        bondType = ringBonds[ringNum].getBondType();
        if (bondType == SmilesBond.TYPE_UNKOWN) {
          bondType = SmilesBond.TYPE_SINGLE;
        }
      } else {
        if ((ringBonds[ringNum].getBondType() != SmilesBond.TYPE_UNKOWN) &&
            (ringBonds[ringNum].getBondType() != bondType)) {
          throw new InvalidSmilesException("Incoherent bond type for ring");
        }
      }
      ringBonds[ringNum].setBondType(bondType);
      ringBonds[ringNum].setAtom2(currentAtom);
      currentAtom.addBond(ringBonds[ringNum]);
      ringBonds[ringNum] = null;
    }
  }

}
