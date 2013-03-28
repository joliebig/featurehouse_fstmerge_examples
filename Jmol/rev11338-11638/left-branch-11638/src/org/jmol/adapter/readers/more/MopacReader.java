
package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;
import java.util.Vector;
import java.util.Hashtable;


public class MopacReader extends AtomSetCollectionReader {
    
  
  int baseAtomIndex;
  
  private boolean chargesFound = false;

 public void readAtomSetCollection(BufferedReader reader) {

    this.reader = reader;
    atomSetCollection = new AtomSetCollection("mopac");
    
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
    Vector freqs = new Vector();
    Vector vibrations = new Vector();
    String[][] data;
    int nAtoms = atomSetCollection.getLastAtomSetAtomCount();
    while (readLine() != null
        && line.indexOf("DESCRIPTION") < 0)
      if (line.indexOf("ROOT") >= 0) {
        int frequencyCount = getTokens().length - 2;
        data = new String[nAtoms * 3 + 1][];
        fillDataBlock(data);
        for (int i = 0; i < frequencyCount; ++i) {
          float freq = parseFloat(data[0][i]);
          Hashtable info = new Hashtable();
          info.put("freq", new Float(freq));
          info.put("label", "");
          freqs.addElement(info);
          baseAtomIndex = atomSetCollection.getAtomCount();
          atomSetCollection.cloneLastAtomSet();
          Atom[] atoms = atomSetCollection.getAtoms();
          atomSetCollection.setAtomSetName(freq + " cm^-1");
          atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
              "Frequencies");
          Vector vib = new Vector();
          for (int iatom = 0, dataPt = 1; iatom < nAtoms; ++iatom) {
            float dx = parseFloat(data[dataPt++][i + 1]);
            float dy = parseFloat(data[dataPt++][i + 1]);
            float dz = parseFloat(data[dataPt++][i + 1]);
            atoms[baseAtomIndex + iatom].addVibrationVector(dx, dy, dz);
            Vector vibatom = new Vector();
            vibatom.addElement(new Float(dx));
            vibatom.addElement(new Float(dy));
            vibatom.addElement(new Float(dz));
            vib.addElement(vibatom);
          }
          vibrations.addElement(vib);
        }
      }
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("VibFreqs", freqs);
    atomSetCollection
        .setAtomSetCollectionAuxiliaryInfo("vibration", vibrations);
  }
}
