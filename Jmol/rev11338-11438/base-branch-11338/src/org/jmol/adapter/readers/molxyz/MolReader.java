

package org.jmol.adapter.readers.molxyz;

import org.jmol.adapter.smarter.*;


import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;

import java.io.BufferedReader;


public class MolReader extends AtomSetCollectionReader {

  String header = "";
 public void readAtomSetCollection(BufferedReader reader) {
    atomSetCollection = new AtomSetCollection("mol");
    this.reader = reader;
    try {
      while (readLine() != null) {
        if (line.startsWith("$MDL")) {
          processRgHeader();
          discardLinesUntilStartsWith("$CTAB");
          processCtab();
        } else {
          if (doGetModel(++modelNumber)) {
            processMolSdHeader();
            processCtab();
            if (isLastModel(modelNumber))
              break;
          }
        }
        flushLines();
      }
    } catch (Exception e) {
      setError(e);
    }
  }
  
  void processMolSdHeader() throws Exception {
    

    String thisDataSetName = line;
    header += line + "\n";
    atomSetCollection.setCollectionName(line);
    readLine();
    if (line == null)
      return;
    header += line + "\n";
    
    readLine();
    if (line == null)
      return;
    header += line + "\n";
    checkLineForScript();
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("fileHeader", header);
    newAtomSet(thisDataSetName);
  }

  void processRgHeader() throws Exception {
    

    while (readLine() != null && !line.startsWith("$HDR")) {
    }
    if (line == null) {
      Logger.warn("$HDR not found in MDL RG file");
      return;
    }
    readLine();
    processMolSdHeader();
  }

  void processCtab() throws Exception {
    readLine();
    if (line == null)
      return;
    int atomCount = parseInt(line, 0, 3);
    int bondCount = parseInt(line, 3, 6);
    int atom0 = atomSetCollection.getAtomCount();
    readAtoms(atomCount);
    readBonds(atom0, bondCount);
    applySymmetryAndSetTrajectory();
  }

  void flushLines() throws Exception {
    while (readLine() != null && !line.startsWith("$$$$")) {
      
    }
  }

  private final static String isotopeMap0 = "H1 H2 ";
  private final static String isotopeMap1 = "D  T  ";
  void readAtoms(int atomCount) throws Exception {
    for (int i = 0; i < atomCount; ++i) {
      readLine();
      String elementSymbol = "";
      if (line.length() > 34) {
        elementSymbol = line.substring(31, 34).trim().intern();
      } else {
        
        elementSymbol = line.substring(31).trim().intern();
      }
      float x = parseFloat(line, 0, 10);
      float y = parseFloat(line, 10, 20);
      float z = parseFloat(line, 20, 30);
      int charge = 0;
      if (line.length() >= 39) {
        int code = parseInt(line, 36, 39);
        if (code >= 1 && code <= 7)
          charge = 4 - code;
        code = parseInt(line, 34, 36);
        if (code != 0 && code >= -3 && code <= 4) {
          int ptr = isotopeMap0.indexOf(elementSymbol + code);
          if (ptr >= 0)
            elementSymbol = isotopeMap1.substring(ptr, ptr + 3).trim();
          else if (elementSymbol=="C")
            elementSymbol = (12 + code) + "C";
          else if (elementSymbol=="N")
            elementSymbol = (14 + code) + "N";
        }
      }
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = elementSymbol;
      atom.formalCharge = charge;
      setAtomCoord(atom, x, y, z);
    }
  }

  void readBonds(int atom0, int bondCount) throws Exception {
    for (int i = 0; i < bondCount; ++i) {
      readLine();
      int atomIndex1 = parseInt(line, 0, 3);
      int atomIndex2 = parseInt(line, 3, 6);
      int order = parseInt(line, 6, 9);
      switch (order) {
      case 1:
      case 2:
      case 3:
        break;
      case 4:
        order = JmolAdapter.ORDER_AROMATIC;
        break;
      case 5:
        order = JmolAdapter.ORDER_PARTIAL12;
        break;
      case 6:
        order = JmolAdapter.ORDER_AROMATIC_SINGLE;
        break;
      case 7:
        order = JmolAdapter.ORDER_AROMATIC_DOUBLE;
        break;
      case 8:
        order = JmolAdapter.ORDER_PARTIAL01;
        break;
      }
      atomSetCollection
          .addBond(new Bond(atom0 + atomIndex1 - 1, atom0 + atomIndex2 - 1, order));
    }
  }
}
