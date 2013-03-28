

package org.jmol.adapter.readers.molxyz;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;

import java.io.BufferedReader;

import org.jmol.util.Logger;



public class XyzReader extends AtomSetCollectionReader {

  public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("xyz", this);
    try {
      int modelAtomCount;
      while ((modelAtomCount = readAtomCount()) > 0) {
        
        vibrationNumber = ++modelNumber;
        if (desiredVibrationNumber <= 0 ? doGetModel(modelNumber) : doGetVibration(vibrationNumber)) {
          readAtomSetName();
          readAtoms(modelAtomCount);
          applySymmetryAndSetTrajectory();
          if (isLastModel(modelNumber))
            break;
        } else {
          skipAtomSet(modelAtomCount);
        }
      }
    } catch (Exception e) {
      setError(e);
    }
  }

  private void skipAtomSet(int modelAtomCount) throws Exception {
    readLine(); 
    for (int i = modelAtomCount; --i >= 0;)
      readLine(); 
  }

  private int readAtomCount() throws Exception {
    readLine();
    if (line != null) {
      int atomCount = parseInt(line);
      if (atomCount > 0)
        return atomCount;
    }
    return 0;
  }

  private void readAtomSetName() throws Exception {
    readLineTrimmed();
    checkLineForScript();
    
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetName(line);
  }

  private void readAtoms(int modelAtomCount) throws Exception {
    for (int i = 0; i < modelAtomCount; ++i) {
      readLine();
      String[] tokens = getTokens();
      if (tokens.length < 4) {
        Logger.warn("line cannot be read for XYZ atom data: " + line);
        continue;
      }
      Atom atom = atomSetCollection.addNewAtom();
      String str = tokens[0];
      int isotope = parseInt(str);
      
      if (isotope == Integer.MIN_VALUE) {
        atom.elementSymbol = str;
      } else {
        str = str.substring(("" + isotope).length());
        atom.elementNumber = (short) ((isotope << 7) + JmolAdapter
            .getElementNumber(str));
        atomSetCollection.setFileTypeName("xyzi");
      }
      atom.x = parseFloat(tokens[1]);
      atom.y = parseFloat(tokens[2]);
      atom.z = parseFloat(tokens[3]);
      if (Float.isNaN(atom.x) || Float.isNaN(atom.y) || Float.isNaN(atom.z)) {
        Logger.warn("line cannot be read for XYZ atom data: " + line);
        atom.set(0, 0, 0);
      }
      int vpt = 4;
      setAtomCoord(atom);
      switch (tokens.length) {
      case 4:
      case 6:
        continue;
      case 5:
      case 8:
      case 9:
        
        
        if ((str = tokens[4]).indexOf(".") >= 0) {
          atom.partialCharge = parseFloat(str);
        } else {
          int charge = parseInt(str);
          if (charge != Integer.MIN_VALUE)
            atom.formalCharge = charge;
        }        
        if (tokens.length == 5)
          continue;
        if (tokens.length == 9)
          atom.atomSerial = parseInt(tokens[8]);
        vpt++;
        
      default:
         
         
        float vx = parseFloat(tokens[vpt++]);
        float vy = parseFloat(tokens[vpt++]);
        float vz = parseFloat(tokens[vpt++]);
        if (Float.isNaN(vx) || Float.isNaN(vy) || Float.isNaN(vz))
          continue;
        atomSetCollection.addVibrationVector(atom.atomIndex, vx, vy, vz);
      }
    }
  }
}
