
package org.jmol.adapter.readers.simple;

import org.jmol.adapter.smarter.*;

import java.io.BufferedReader;

import javax.vecmath.Point3f;


public class AmpacReader extends AtomSetCollectionReader {

  private int atomCount;
  private int freqAtom0 = -1;
  private float[] partialCharges;
  private Point3f[] atomPositions;
  
  
  public void readAtomSetCollection(BufferedReader reader) {
    atomSetCollection = new AtomSetCollection("ampac", this);
    this.reader = reader;
    boolean iHaveAtoms = false;
    modelNumber = 0;
    try {
      while (readLine() != null) {
        if (line.indexOf("CARTESIAN COORDINATES") >= 0) {
          if (!doGetModel(++modelNumber)) {
            if (isLastModel(modelNumber) && iHaveAtoms)
              break;
            iHaveAtoms = false;
            continue;
          }
          iHaveAtoms = true;
          readCoordinates();          
        } else if (iHaveAtoms && line.indexOf("NET ATOMIC CHARGES") >= 0) {
          readPartialCharges();
        } else if (iHaveAtoms && line.indexOf("VIBRATIONAL FREQUENCIES") >= 0) {
          readFrequencies();
        }
      }
    } catch (Exception e) {
      setError(e);
    }

  }

  
  private void readCoordinates() throws Exception {

    
    boolean haveFreq = (freqAtom0 >= 0);
    if (haveFreq) {
      atomPositions = new Point3f[atomCount];
    } else {
      atomSetCollection.newAtomSet();
    }
    readLine();
    atomCount = 0;
    while (readLine() != null) {
      String[] tokens = getTokens();
      if (tokens.length < 5)
        break;
      if (haveFreq) {
        atomPositions[atomCount] = new Point3f(parseFloat(tokens[2]), parseFloat(tokens[3]), parseFloat(tokens[4]));
      } else {
        String symbol = tokens[1];
        Atom atom = atomSetCollection.addNewAtom();
        atom.elementSymbol = symbol;
        atom.set(parseFloat(tokens[2]), parseFloat(tokens[3]), parseFloat(tokens[4]));
      }
      atomCount++;
    }
    if (haveFreq)
      setPositions();
  }

  private void setPositions() {
    int maxAtom = atomSetCollection.getAtomCount();
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = freqAtom0; i <  maxAtom; i++) {
      atoms[i].set(atomPositions[i % atomCount]);
      atoms[i].partialCharge = partialCharges[i % atomCount];  
    }
  }

  private void readPartialCharges() throws Exception {

    readLine();
    partialCharges = new float[atomCount];
    String[] tokens;
    for (int i = 0; i < atomCount; i++) {
      if (readLine() == null || (tokens = getTokens()).length < 4)
        break;
      partialCharges[i] = parseFloat(tokens[2]);
    }
  }


  
  
  private void readFrequencies() throws Exception {
    while (readLine() != null && line.indexOf("FREQ  :") < 0) {
    }
    while (line != null && line.indexOf("FREQ  :") >= 0) {
      String[] frequencies = getTokens();
      while (readLine() != null && line.indexOf("IR I") < 0) {
      }
      int iAtom0 = atomSetCollection.getAtomCount();
      if (vibrationNumber == 0)
        freqAtom0 = iAtom0;
      int frequencyCount = frequencies.length - 2;
      boolean[] ignore = new boolean[frequencyCount];
      for (int i = 0; i < frequencyCount; ++i) {
        ignore[i] = !doGetVibration(++vibrationNumber);
        if (ignore[i])
          continue;
        atomSetCollection.cloneLastAtomSet();
        atomSetCollection.setAtomSetName(frequencies[i + 2] + " cm^-1");
        atomSetCollection.setAtomSetProperty("Frequency", frequencies[i + 2]
            + " cm^-1");
        atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
            "Frequencies");
      }
      fillFrequencyData(iAtom0, atomCount, ignore, false, 8, 9);
      readLine();
      readLine();
    }
  }
  
}
