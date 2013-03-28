

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;

import java.io.BufferedReader;

import org.jmol.api.JmolAdapter;



public class Mol2Reader extends FFReader {

  private int nAtoms = 0;
  private int atomCount = 0;
  private boolean isPDB = false;

  public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("mol2", this);
    try {
      setUserAtomTypes();
      setFractionalCoordinates(false);
      readLine();
      modelNumber = 0;
      while (line != null) {
        if (line.equals("@<TRIPOS>MOLECULE")) {
          if (doGetModel(++modelNumber)) {
            processMolecule();
            if (isLastModel(modelNumber))
              break;
            continue;
          }
        }
        readLine();
      }
    } catch (Exception e) {
      setError(e);
    }

  }

  private void processMolecule() throws Exception {
    

    isPDB = false;
    String thisDataSetName = readLineTrimmed();
    lastSequenceNumber = Integer.MAX_VALUE;
    chainID = 'A' - 1;
    readLine();
    line += " 0 0 0 0 0 0";
    atomCount = parseInt(line);
    int bondCount = parseInt();
    int resCount = parseInt();
    readLine();
    readLine();
    boolean iHaveCharges = (line.indexOf("NO_CHARGES") != 0);
    
    if (readLine() != null && (line.length() == 0 || line.charAt(0) != '@')) {
      
      if (readLine() != null && line.length() != 0 && line.charAt(0) != '@') {
        thisDataSetName += ": " + line.trim();
      }
    }
    newAtomSet(thisDataSetName);
    while (line != null && !line.equals("@<TRIPOS>MOLECULE")) {
      if (line.equals("@<TRIPOS>ATOM")) {
        readAtoms(atomCount, iHaveCharges);
        atomSetCollection.setAtomSetName(thisDataSetName);
      } else if (line.equals("@<TRIPOS>BOND")) {
        readBonds(bondCount);
      } else if (line.equals("@<TRIPOS>SUBSTRUCTURE")) {
        readResInfo(resCount);
      } else if (line.equals("@<TRIPOS>CRYSIN")) {
        readCrystalInfo();
      }
      readLine();
    }
    nAtoms += atomCount;
    if (isPDB) {
      atomSetCollection
          .setAtomSetCollectionAuxiliaryInfo("isPDB", Boolean.TRUE);
      atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.TRUE);
    }
    applySymmetryAndSetTrajectory();
  }

  private int lastSequenceNumber = Integer.MAX_VALUE;
  private char chainID = 'A' - 1;

  private void readAtoms(int atomCount, boolean iHaveCharges) throws Exception {
    
    
    
    for (int i = 0; i < atomCount; ++i) {
      Atom atom = atomSetCollection.addNewAtom();
      String[] tokens = getTokens(readLine());
      
      String atomType = tokens[5];
      atom.atomName = tokens[1] + '\0' + atomType;
      setAtomCoord(atom, parseFloat(tokens[2]), parseFloat(tokens[3]),
          parseFloat(tokens[4]));
      boolean deduceSymbol = !getElementSymbol(atom, atomType);
      
      
      if (tokens.length > 6) {
        atom.sequenceNumber = parseInt(tokens[6]);
        if (atom.sequenceNumber < lastSequenceNumber) {
          if (chainID == 'Z')
            chainID = 'a' - 1;
          chainID++;
        }
        lastSequenceNumber = atom.sequenceNumber;
        atom.chainID = chainID;
      }
      if (tokens.length > 7) {
        atom.group3 = tokens[7];
        atom.isHetero = JmolAdapter.isHetero(atom.group3);
        if (!isPDB && atom.group3.length() <= 3
            && JmolAdapter.lookupGroupID(atom.group3) >= 0) {
          isPDB = true;
        }
        if (isPDB && deduceSymbol)
          atom.elementSymbol = deducePdbElementSymbol(atom.isHetero, atomType,
              atom.group3);
        
      }
      if (tokens.length > 8)
        atom.partialCharge = parseFloat(tokens[8]);
    }
  }

  private void readBonds(int bondCount) throws Exception {
    
    
    for (int i = 0; i < bondCount; ++i) {
      String[] tokens = getTokens(readLine());
      int atomIndex1 = parseInt(tokens[1]);
      int atomIndex2 = parseInt(tokens[2]);
      int order = parseInt(tokens[3]);
      if (order == Integer.MIN_VALUE)
        order = (tokens[3].equals("ar") ? JmolAdapter.ORDER_AROMATIC
            : JmolAdapter.ORDER_UNSPECIFIED);
      atomSetCollection.addBond(new Bond(nAtoms + atomIndex1 - 1, nAtoms
          + atomIndex2 - 1, order));
    }
  }

  private void readResInfo(int resCount) throws Exception {
    
    for (int i = 0; i < resCount; ++i) {
      readLine();
      
    }
  }

  private void readCrystalInfo() throws Exception {
    
    readLine();
    String[] tokens = getTokens();
    if (tokens.length < 6)
      return;
    String name = "";
    for (int i = 6; i < tokens.length; i++)
      name += " " + tokens[i];
    if (name == "")
      name = " P1";
    else
      name += " *";
    name = name.substring(1);
    setSpaceGroupName(name);
    if (ignoreFileUnitCell)
      return;
    for (int i = 0; i < 6; i++)
      setUnitCellItem(i, parseFloat(tokens[i]));
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = 0; i < atomCount; ++i)
      setAtomCoord(atoms[nAtoms + i]);
  }
}
