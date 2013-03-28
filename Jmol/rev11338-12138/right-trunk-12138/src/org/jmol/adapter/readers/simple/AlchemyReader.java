

package org.jmol.adapter.readers.simple;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;

import java.io.BufferedReader;



public class AlchemyReader extends AtomSetCollectionReader {

  int atomCount;
  int bondCount;

  public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("Alchemy", this);
    try {
      atomSetCollection.newAtomSet();
      String[] tokens = getTokens(readLine());
      atomCount = parseInt(tokens[0]);
      bondCount = parseInt(tokens[2]);
      readAtoms();
      readBonds();
    } catch (Exception e) {
      setError(e);
    }

  }

  
  private void readAtoms() throws Exception {
    for (int i = atomCount; --i >= 0;) {
      String[] tokens = getTokens(readLine());
      Atom atom = new Atom();
      atom.atomSerial = parseInt(tokens[0]);
      String name = atom.atomName = tokens[1];
      atom.elementSymbol = name.substring(0, 1);
      char c1 = name.charAt(0);
      char c2 = ' ';
      
      
      int nChar = (name.length() == 2
          && (Atom.isValidElementSymbol(c1, 
              c2 = Character.toLowerCase(name.charAt(1)))
              || name.equals("Du"))
           ? 2 : 1);
      atom.elementSymbol = (nChar == 1 ? "" + c1 : "" + c1 + c2);
      setAtomCoord(atom, parseFloat(tokens[2]), parseFloat(tokens[3]),
          parseFloat(tokens[4]));
      atom.partialCharge = (tokens.length >= 6 ? parseFloat(tokens[5]) : 0);
      atomSetCollection.addAtomWithMappedSerialNumber(atom);
    }
  }

  private void readBonds() throws Exception {
    for (int i = bondCount; --i >= 0;) {
      String[] tokens = getTokens(readLine());
      int atomSerial1 = parseInt(tokens[1]);
      int atomSerial2 = parseInt(tokens[2]);
      String sOrder = (tokens.length < 4 ? "1" : tokens[3].toUpperCase());
      int order = 0;
      switch (sOrder.charAt(0)) {
      default:
      case '1':
      case 'S':
        order = JmolAdapter.ORDER_COVALENT_SINGLE;
        break;
      case '2':
      case 'D':
        order = JmolAdapter.ORDER_COVALENT_DOUBLE;
        break;
      case '3':
      case 'T':
        order = JmolAdapter.ORDER_COVALENT_TRIPLE;
        break;
      case 'A':
        order = JmolAdapter.ORDER_AROMATIC;
        break;
      case 'H':
        order = JmolAdapter.ORDER_HBOND;
        break;
      }
      atomSetCollection.addNewBondWithMappedSerialNumbers(atomSerial1,
          atomSerial2, order);
    }
  }
}
