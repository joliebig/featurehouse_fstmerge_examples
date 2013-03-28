

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

import java.io.BufferedReader;



public class GromacsReader extends AtomSetCollectionReader {
  protected String fileType = "gromacs";  

  public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("xyz");
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("isPDB", Boolean.TRUE);
    try {
      readAtomSetName();
      int modelAtomCount = readAtomCount();
      readAtoms(modelAtomCount);
      applySymmetryAndSetTrajectory();
    } catch (Exception e) {
      setError(e);
    }
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
    atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.TRUE);
  }

  
  private void readAtoms(int modelAtomCount) throws Exception {
    for (int i = 0; i < modelAtomCount; ++i) {
      readLine();
      if (line.length() < 68) {
        Logger.warn("line cannot be read for GROMACS atom data: " + line);
        continue;
      }
      Atom atom = new Atom();
      atom.sequenceNumber = parseInt(line, 0, 5);
      atom.group3 = parseToken(line, 5, 9).trim();  
      atom.atomName = line.substring(11, 15).trim();
      atom.atomSerial = parseInt(line, 15, 20);
      atom.x = parseFloat(line, 20, 28) * 10;
      atom.y = parseFloat(line, 28, 36) * 10;
      atom.z = parseFloat(line, 36, 44) * 10;
      if (Float.isNaN(atom.x) || Float.isNaN(atom.y) || Float.isNaN(atom.z)) {
        Logger.warn("line cannot be read for GROMACS atom data: " + line);
        atom.set(0, 0, 0);
      }
      setAtomCoord(atom);
      atom.elementSymbol = deduceElementSymbol(atom.group3, atom.atomName);
      if (filter != null)
        if (!filterAtom(atom))
          continue;
      atom.isHetero = false;
      atomSetCollection.addAtom(atom);
      float vx = parseFloat(line, 44, 52) * 10;
      float vy = parseFloat(line, 52, 60) * 10;
      float vz = parseFloat(line, 60, 68) * 10;
        if (Float.isNaN(vx) || Float.isNaN(vy) || Float.isNaN(vz))
          continue;
        atom.vectorX = vx;
        atom.vectorY = vy;
        atom.vectorZ = vz;
    }
  }

  String deduceElementSymbol(String group3, String atomName) {
    
    if (atomName.length() <= 2 && group3.equals(atomName))
      return atomName;
    char ch1 = (atomName.length() == 4 ? atomName.charAt(0) : '\0');
    char ch2 = atomName.charAt(atomName.length() == 4 ? 1 : 0);
    boolean isHetero = JmolConstants.isHetero(group3);
    if (Atom.isValidElementSymbolNoCaseSecondChar(ch1, ch2))
      return (isHetero || ch1 != 'H' ? "" + ch1 + ch2 : "H");
    if (Atom.isValidElementSymbol(ch2))
      return "" + ch2;
    if (Atom.isValidElementSymbol(ch1))
      return "" + ch1;
    return "Xx";
  }

}

