

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;


import java.io.BufferedReader;


public class V3000Reader extends AtomSetCollectionReader {
    
  int headerAtomCount;
  int headerBondCount;

 public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("v3000", this);
    try {
      while (readLine() != null) {
        if (doGetModel(++modelNumber)) {
          processCtab();
          if (isLastModel(modelNumber))
            break;
        } else {
          flushLines();
        }
      }
    } catch (Exception e) {
      setError(e);
    }

  }

  void processCtab() throws Exception {
    while (readLine() != null &&
           ! line.startsWith("$$$$") &&
           ! line.startsWith("M  END")) {
      if (line.startsWith("M  V30 BEGIN ATOM")) {
        processAtomBlock();
        continue;
      }
      if (line.startsWith("M  V30 BEGIN BOND")) {
        processBondBlock();
        continue;
      }
      if (line.startsWith("M  V30 BEGIN CTAB")) {
        newAtomSet("");
      } else if (line.startsWith("M  V30 COUNTS")) {
        headerAtomCount = parseInt(line, 13);
        headerBondCount = parseInt();
      }
    }
    if (line != null && !line.startsWith("$$$$"))
      flushLines();
  }

  String processAtomBlock() throws Exception {
    for (int i = headerAtomCount; --i >= 0; ) {
      readLineWithContinuation();
      if (line == null || (! line.startsWith("M  V30 ")))
        throw new Exception("unrecognized atom");
      Atom atom = new Atom();
      String[] tokens = getTokens();
      atom.atomSerial = parseInt(tokens[2]);
      atom.elementSymbol = tokens[3];
      atom.set(parseFloat(tokens[4]), parseFloat(tokens[5]), parseFloat(tokens[6]));
      for (int j = 8; j < tokens.length; j++) {
        String token = tokens[j];
        if (token.startsWith("CHG=")) {
          int charge = parseInt(token, 4);
          atom.formalCharge = (charge > 3 ? 4 - charge : charge);
          break;
        } else if (token.startsWith("MASS=")) {
          int isotope = parseInt(token, 5);
          atom.elementNumber = (short) ((isotope << 7) + JmolAdapter
              .getElementNumber(atom.elementSymbol));
        }
      }
      atomSetCollection.addAtomWithMappedSerialNumber(atom);
    }
    readLine();
    if (line == null || ! line.startsWith("M  V30 END ATOM"))
      throw new Exception("M  V30 END ATOM not found");
    return line;
  }

  void processBondBlock() throws Exception {
    for (int i = headerBondCount; --i >= 0; ) {
      readLineWithContinuation();
      if (line == null || (! line.startsWith("M  V30 ")))
        throw new Exception("unrecognized bond");
      parseInt(line, 7); 
      int order = parseInt();
      int atomSerial1 = parseInt();
      int atomSerial2 = parseInt();
      atomSetCollection.addNewBondWithMappedSerialNumbers(atomSerial1,
                                                          atomSerial2,
                                                          order);
    }
    readLine();
    if (line == null || ! line.startsWith("M  V30 END BOND"))
      throw new Exception("M  V30 END BOND not found");
  }

  void readLineWithContinuation() throws Exception {
    readLine();
    if (line != null && line.length() > 7) {
      while (line.charAt(line.length() - 1) == '-') {
        String line2 = readLine();
        if (line2 == null || ! line.startsWith("M  V30 "))
          throw new Exception("Invalid line continuation");
        line += line2.substring(7);
      }
    }
  }
  
  void flushLines() throws Exception {
    while (readLine() != null && !line.startsWith("$$$$")) {
      
    }
  }
}
