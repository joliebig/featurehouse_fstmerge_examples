

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import org.jmol.api.JmolAdapter;
import java.io.BufferedReader;


public class HinReader extends AtomSetCollectionReader {
  
 public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("hin");
    try {
      readAtoms();
    } catch (Exception e) {
      setError(e);
    }

  }
  
  int atomIndex;
  int baseAtomIndex;

  void readAtoms() throws Exception {

    while (readLine() != null ) {
      if (line.length() == 0 || line.charAt(0) == ';') 
        continue;
      if (line.startsWith("mol ")) 
        processMol();
      else if (line.startsWith("atom "))
        processAtom();
      else if (line.startsWith("endmol "))
        processEndmol();
    }
  }

  void processMol() throws Exception {
    atomSetCollection.newAtomSet();
    String molName = getMolName();
    atomSetCollection.setAtomSetName(molName);
    atomIndex = 0;
    baseAtomIndex = atomSetCollection.getAtomCount();
  }

  String getMolName() {
    parseToken(line);
    parseToken();
    return parseToken();
  }

  void processAtom() throws Exception {

    int fileAtomNumber = parseInt(line, 5);
    if (fileAtomNumber - 1 != atomIndex) {
      throw new Exception ("bad atom number sequence ... expected:" +
        (atomIndex + 1) + " found:" + fileAtomNumber);
    }

    Atom atom = atomSetCollection.addNewAtom();
    parseToken(); 
    atom.elementSymbol = parseToken();
    parseToken(); 
    parseToken(); 
    atom.partialCharge = parseFloat();
    atom.x = parseFloat();
    atom.y = parseFloat();
    atom.z = parseFloat();
    
    int bondCount = parseInt();
    for (int i = 0; i < bondCount; ++i) {
      int otherAtomNumber = parseInt();
      String bondTypeToken = parseToken();
      if (otherAtomNumber > atomIndex)
        continue;
      int bondOrder;
      switch(bondTypeToken.charAt(0)) {
      case 's': 
        bondOrder = 1;
        break;
      case 'd': 
        bondOrder = 2;
        break;
      case 't': 
        bondOrder = 3;
        break;      
      case 'a':
        bondOrder = JmolAdapter.ORDER_AROMATIC;
        break;
      default:
        throw new Exception ("unrecognized bond type:" + bondTypeToken +
          " atom #" + fileAtomNumber);
      }
      atomSetCollection.addNewBond(baseAtomIndex + atomIndex,
                       baseAtomIndex + otherAtomNumber - 1,
                       bondOrder);
    }
    ++atomIndex;
  }

  void processEndmol() {
  }
}
