
package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;

import org.jmol.api.JmolAdapter;


public class GhemicalMMReader extends AtomSetCollectionReader {
    
 public void readAtomSetCollection(BufferedReader reader) {

    this.reader = reader;
    atomSetCollection = new AtomSetCollection("ghemicalMM", this);

    try {
      while (readLine() != null) {
        if (line.startsWith("!Header"))
          processHeader();
        else if (line.startsWith("!Info"))
          processInfo();
        else if (line.startsWith("!Atoms"))
          processAtoms();
        else if (line.startsWith("!Bonds"))
          processBonds();
        else if (line.startsWith("!Coord"))
          processCoord();
        else if (line.startsWith("!Charges"))
          processCharges();
        else if (line.startsWith("!End")) {
          return;
        }
      }
    } catch (Exception e) {
      setError(e);
      return;
    }
    setError(new Exception("unexpected end of file"));
  }

  void processHeader() {
  }

  void processInfo() {
  }

  void processAtoms() throws Exception {
    int atomCount = parseInt(line, 6);
    
    for (int i = 0; i < atomCount; ++i) {
      if (atomSetCollection.getAtomCount() != i)
        throw new Exception("GhemicalMMReader error #1");
      readLine();
      int atomIndex = parseInt(line);
      if (atomIndex != i)
        throw new Exception("bad atom index in !Atoms" +
                            "expected: " + i + " saw:" + atomIndex);
      int elementNumber = parseInt();
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementNumber = (byte)elementNumber;
    }
  }

  void processBonds() throws Exception {
    int bondCount = parseInt(line, 6);
    for (int i = 0; i < bondCount; ++i) {
      readLine();
      int atomIndex1 = parseInt(line);
      int atomIndex2 = parseInt();
      String orderCode = parseToken();
      int order = 0;
      
      switch(orderCode.charAt(0)) {
      case 'C': 
        order = JmolAdapter.ORDER_AROMATIC;
        break;
      case 'T':
        order = JmolAdapter.ORDER_COVALENT_TRIPLE;
        break;
      case 'D':
        order = JmolAdapter.ORDER_COVALENT_DOUBLE;
        break;
      case 'S':
      default:
        order = JmolAdapter.ORDER_COVALENT_SINGLE;
      }
      atomSetCollection.addNewBond(atomIndex1, atomIndex2, order);
    }
  }

  void processCoord() throws Exception {
    Atom[] atoms = atomSetCollection.getAtoms();
    int atomCount = atomSetCollection.getAtomCount();
    for (int i = 0; i < atomCount; ++i) {
      readLine();
      int atomIndex = parseInt(line);
      if (atomIndex != i)
        throw new Exception("bad atom index in !Coord" + "expected: " + i
            + " saw:" + atomIndex);
      atoms[i].set(parseFloat() * 10, parseFloat() * 10, parseFloat() * 10);
    }
  }

  void processCharges() throws Exception {
    Atom[] atoms = atomSetCollection.getAtoms();
    int atomCount = atomSetCollection.getAtomCount();
    for (int i = 0; i < atomCount; ++i) {
      readLine();
      int atomIndex = parseInt(line);
      if (atomIndex != i)
        throw new Exception("bad atom index in !Charges" +
                            "expected: " + i + " saw:" + atomIndex);
      atoms[i].partialCharge = parseFloat();
    }
  }
}
