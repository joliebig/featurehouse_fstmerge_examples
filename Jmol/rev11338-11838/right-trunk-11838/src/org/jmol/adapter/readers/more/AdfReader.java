
package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;

import java.io.BufferedReader;


public class AdfReader extends AtomSetCollectionReader {

  

  String energy = null;
  int nXX = 0;

  
  public void readAtomSetCollection(BufferedReader reader) {
    atomSetCollection = new AtomSetCollection("adf", this);
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
    nXX = 0;
    while (readLine() != null && !line.startsWith(" -----")) {
      String[] tokens = getTokens();
      if (tokens.length < 5)
        break;
      String symbol = tokens[1];
      if (JmolAdapter.getElementNumber(symbol) < 1) {
        nXX++;
        continue;
      }
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = symbol;
      atom.set(parseFloat(tokens[2]), parseFloat(tokens[3]), parseFloat(tokens[4]));
      if (tokens.length > 8)
        atom.scale(ANGSTROMS_PER_BOHR);
    }
  }

  
  
  private void readFrequencies() throws Exception {
    readLine();
    while (readLine() != null) {
      while (readLine() != null && line.indexOf(".") < 0
          && line.indexOf("====") < 0) {
      }
      if (line == null || line.indexOf(".") < 0)
        return;
      String[] frequencies = getTokens();
      readLine(); 
      int iAtom0 = atomSetCollection.getAtomCount();
      int atomCount = atomSetCollection.getLastAtomSetAtomCount();
      int frequencyCount = frequencies.length;
      boolean[] ignore = new boolean[frequencyCount];
      for (int i = 0; i < frequencyCount; ++i) {
        ignore[i] = !doGetVibration(++vibrationNumber);
        atomSetCollection.cloneLastAtomSet();
        if (ignore[i])
          continue;
        atomSetCollection.setAtomSetName(frequencies[i] + " cm^-1");
        atomSetCollection.setAtomSetProperty("Frequency", frequencies[i]
            + " cm^-1");
        atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
            "Frequencies");
      }
      discardLines(nXX);
      fillFrequencyData(iAtom0, atomCount, ignore, true, 0, 0);
    }
  }
}
