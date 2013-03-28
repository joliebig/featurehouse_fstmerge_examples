
package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;

import java.io.BufferedReader;


public class AdfReader extends AtomSetCollectionReader {

  

  String energy = null;

  
  public void readAtomSetCollection(BufferedReader reader) {
    atomSetCollection = new AtomSetCollection("adf");
    this.reader = reader;
    boolean iHaveAtoms = false;
    modelNumber = 0;
    try {
      while (readLine() != null) {
        if (line.indexOf("Coordinates (Cartesian)") >= 0
            || line.indexOf("G E O M E T R Y  ***  3D  Molecule  ***") >= 0) {
          if (!doGetModel(++modelNumber)) {
            if (isLastModel(modelNumber) && iHaveAtoms)
              break;
            iHaveAtoms = false;
            continue;
          }
          iHaveAtoms = true;
          readCoordinates();          
        } else if (iHaveAtoms && line.indexOf("Energy:") >= 0) {
          String[] tokens = getTokens();
          energy = tokens[1];
        } else if (iHaveAtoms && line.indexOf("Vibrations") >= 0) {
          readFrequencies();
        }
      }
    } catch (Exception e) {
      setError(e);
    }

  }

  
  private void readCoordinates() throws Exception {

    
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetName("" + energy); 
    discardLinesUntilContains("----");
    while (readLine() != null && !line.startsWith(" -----")) {
      String[] tokens = getTokens();
      if (tokens.length < 5)
        break;
      String symbol = tokens[1];
      if (JmolAdapter.getElementNumber(symbol) < 1)
        continue;
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = symbol;
      atom.set(parseFloat(tokens[2]), parseFloat(tokens[3]), parseFloat(tokens[4]));
      if (tokens.length > 8)
        atom.scale(ANGSTROMS_PER_BOHR);
    }
  }

  
  
  private void readFrequencies() throws Exception {
    String[] tokens;
    String[] frequencies;
    readLine();
    int atomCount = atomSetCollection.getLastAtomSetAtomCount();
    while (readLine() != null) {
      while (readLine() != null && line.indexOf(".") < 0
          && line.indexOf("====") < 0) {
      }
      if (line == null || line.indexOf(".") < 0)
        return;
      frequencies = getTokens();
      readLine(); 
      int frequencyCount = frequencies.length;
      int firstModelAtom = atomSetCollection.getAtomCount();
      for (int i = 0; i < frequencyCount; ++i) {
        atomSetCollection.cloneLastAtomSet();
        atomSetCollection.setAtomSetName(frequencies[i] + " cm**-1");
        atomSetCollection.setAtomSetProperty("Frequency", frequencies[i]
            + " cm**-1");
        atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
            "Frequencies");
      }
      int atomPt = 0;
      while (readLine() != null && line.indexOf(".") >= 0) {
        tokens = getTokens();
        String symbol = tokens[0].substring(tokens[0].indexOf(".") + 1);
        if (JmolAdapter.getElementNumber(symbol) < 1)
          continue;
        float x, y, z;
        int offset = 1;
        for (int j = 0; j < frequencyCount; ++j) {
          int atomOffset = firstModelAtom + j * atomCount + atomPt;
          Atom atom = atomSetCollection.getAtom(atomOffset);
          x = parseFloat(tokens[offset++]);
          y = parseFloat(tokens[offset++]);
          z = parseFloat(tokens[offset++]);
          atom.addVibrationVector(x, y, z);
        }
        atomPt++;
      }
    }
  }
}
