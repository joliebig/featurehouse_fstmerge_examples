
package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Parser;
import java.io.BufferedReader;


public class MopacReader extends AtomSetCollectionReader {
    
  
  int baseAtomIndex;
  
  private boolean chargesFound = false;

 public void readAtomSetCollection(BufferedReader reader) {

    this.reader = reader;
    atomSetCollection = new AtomSetCollection("mopac", this);
    
    try {
      while (readLine() != null && !line.startsWith(" ---")) {
        if (line.indexOf("MOLECULAR POINT GROUP") >= 0) {
          
        } else if (line.trim().equals("CARTESIAN COORDINATES")) {
          processCoordinates();
          atomSetCollection.setAtomSetName("Input Structure");
        }
      }

      while (readLine() != null) {
        if (line.indexOf("TOTAL ENERGY") >= 0)
          processTotalEnergy();
        else if (line.indexOf("ATOMIC CHARGES") >= 0)
          processAtomicCharges();
        else if (line.trim().equals("CARTESIAN COORDINATES"))
          processCoordinates();
        else if (line.indexOf("ORIENTATION OF MOLECULE IN FORCE") >= 0) {
          processCoordinates();
          atomSetCollection.setAtomSetName("Orientation in Force Field");
        } else if (line.indexOf("NORMAL COORDINATE ANALYSIS") >= 0)
          readFrequencies();
      }
    } catch (Exception e) {
      setError(e);
    }

  }
    
  void processTotalEnergy() {
    
  }

  
void processAtomicCharges() throws Exception {
    discardLines(2);
    atomSetCollection.newAtomSet(); 
    baseAtomIndex = atomSetCollection.getAtomCount();
    int expectedAtomNumber = 0;
    while (readLine() != null) {
      int atomNumber = parseInt(line);
      if (atomNumber == Integer.MIN_VALUE) 
        break;
      ++expectedAtomNumber;
      if (atomNumber != expectedAtomNumber)
        throw new Exception("unexpected atom number in atomic charges");
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = parseToken();
      atom.partialCharge = parseFloat();
    }
    chargesFound = true;
  }
    
  
  void processCoordinates() throws Exception {
    discardLines(3);
    int expectedAtomNumber = 0;
    if (!chargesFound) {
      atomSetCollection.newAtomSet();
      baseAtomIndex = atomSetCollection.getAtomCount();
    } else {
      chargesFound = false;
    }
    Atom[] atoms = atomSetCollection.getAtoms();
    while (readLine() != null) {
      int atomNumber = parseInt(line);
      if (atomNumber == Integer.MIN_VALUE) 
        break;
      ++expectedAtomNumber;
      if (atomNumber != expectedAtomNumber)
        throw new Exception("unexpected atom number in coordinates");
      String elementSymbol = parseToken();

      Atom atom = atoms[baseAtomIndex + atomNumber - 1];
      if (atom == null) {
          atom = atomSetCollection.addNewAtom(); 
      }
      atom.atomSerial = atomNumber;
      atom.x = parseFloat();
      atom.y = parseFloat();
      atom.z = parseFloat();
      int atno = parseInt(elementSymbol); 
      if (atno != Integer.MIN_VALUE)
        elementSymbol = getElementSymbol(atno);
      atom.elementSymbol = elementSymbol;
    }
  }
  
  private void readFrequencies() throws Exception {
    while (readLine() != null
        && line.indexOf("DESCRIPTION") < 0)
      if (line.toUpperCase().indexOf("ROOT") >= 0) {
        int frequencyCount = getTokens().length - 2;
        discardLinesUntilNonBlank();
        String[] fdata = getTokens();
        String[] ldata = null;
        if (Float.isNaN(Parser.parseFloatStrict(fdata[0]))) {
          ldata = fdata;
          discardLinesUntilNonBlank();
          fdata = getTokens();
        }
        int iAtom0 = atomSetCollection.getAtomCount();
        int atomCount = atomSetCollection.getLastAtomSetAtomCount();
        boolean[] ignore = new boolean[frequencyCount];
        for (int i = 0; i < frequencyCount; ++i) {
          ignore[i] = !doGetVibration(++vibrationNumber);
          if (ignore[i])
            continue;  
          atomSetCollection.cloneLastAtomSet();
          atomSetCollection.setAtomSetName(fdata[i] + " cm^-1"
              + (ldata == null ? "" : " " + ldata[i]));
          atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
              "Frequencies");
        }
        fillFrequencyData(iAtom0, atomCount, ignore, false, 0, 0);
      }
  }
}
