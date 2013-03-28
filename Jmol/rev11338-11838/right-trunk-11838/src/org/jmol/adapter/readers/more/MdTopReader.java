

package org.jmol.adapter.readers.more;

import java.io.BufferedReader;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;



public class MdTopReader extends FFReader {

  private int nAtoms = 0;
  private int atomCount = 0;

  public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("mdtop", this);
    try {
      setUserAtomTypes();
      readLine();
      while (line != null) {
        if (line.indexOf("%FLAG ") != 0) {
          readLine();
          continue;
        }
        line = line.substring(6).trim();
        if (line.equals("POINTERS"))
          getPointers();
        else if (line.equals("ATOM_NAME"))
          getAtomNames();
        else if (line.equals("CHARGE"))
          getCharges();
        else if (line.equals("RESIDUE_LABEL"))
          getResidueLabels();
        else if (line.equals("RESIDUE_POINTER"))
          getResiduePointers();
        else if (line.equals("AMBER_ATOM_TYPE"))
          getAtomTypes();
        else if (line.equals("MASS"))
          getMasses();
      }
      Atom[] atoms = atomSetCollection.getAtoms();
      if (filter == null) {
        nAtoms = atomCount;
      } else {
        Atom[] atoms2 = new Atom[atoms.length];
        nAtoms = 0;
        for (int i = 0; i < atomCount; i++)
          if (filterAtom(atoms[i], i))
            atoms2[nAtoms++] = atoms[i];
        atomSetCollection.discardPreviousAtoms();
        for (int i = 0; i < nAtoms; i++) {
          Atom atom = atoms2[i];
          atomSetCollection.addAtom(atom);
        }
      }
      Logger.info("Total number of atoms used=" + nAtoms);
      int j = 0;
      for (int i = 0; i < nAtoms; i++) {
        Atom atom = atoms[i];
        if (i % 100 == 0)
          j++;
        setAtomCoord(atom, (i % 100)*2, j*2, 0);
        atom.isHetero = JmolAdapter.isHetero(atom.group3);
        String atomType = atom.atomName;
        atomType = atomType.substring(atomType.indexOf('\0') + 1);
        if (!getElementSymbol(atom, atomType))
          atom.elementSymbol = deducePdbElementSymbol(atom.isHetero, atom.atomName,
              atom.group3);
      }
      atomSetCollection.setAtomSetCollectionAuxiliaryInfo("isPDB", Boolean.TRUE);
      atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.TRUE);
    } catch (Exception e) {
      setError(e);
    }
  }

  private String getDataBlock() throws Exception {
    StringBuffer sb = new StringBuffer();
    while (readLine() != null && line.indexOf("%FLAG") != 0)
      sb.append(line);
    return sb.toString();
  }

  private void getMasses() throws Exception {

  }

  private void getAtomTypes() throws Exception {
    readLine(); 
    String[] data = getTokens(getDataBlock());
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = atomCount; --i >= 0;)  
      atoms[i].atomName += '\0' + data[i];
  }

  private void getCharges() throws Exception {
    float[] data = new float[atomCount];
    readLine(); 
    getTokensFloat(getDataBlock(), data, atomCount);
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = atomCount; --i >= 0;)
      atoms[i].partialCharge = data[i];
  }

  private void getResiduePointers() throws Exception {
    readLine(); 
    String[] resPtrs = getTokens(getDataBlock());
    Logger.info("Total number of residues=" + resPtrs.length);
    int pt1 = atomCount;
    int pt2;
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = resPtrs.length; --i >= 0;) {
      int ptr = pt2 = parseInt(resPtrs[i]) - 1;
      while (ptr < pt1) {
        if (group3s != null)
          atoms[ptr].group3 = group3s[i];
        atoms[ptr++].sequenceNumber = i + 1;
      }
      pt1 = pt2;
    }
  }

  String[] group3s;
  
  private void getResidueLabels() throws Exception {
    readLine(); 
    group3s = getTokens(getDataBlock());
  }

  private void getAtomNames() throws Exception {
    readLine(); 
    Atom[] atoms = atomSetCollection.getAtoms();
    int pt = 0;
    int i = 0;
    int len = 0;
    while (pt < atomCount) {
      if (i >= len) {
        readLine();
        i = 0;
        len = line.length();
      }
      atoms[pt++].atomName = line.substring(i, i + 4).trim();
      i += 4;
    }
  }

  
  private void getPointers() throws Exception {
    readLine(); 
    String data = "";
    int pt = 0;
    while (pt++ < 3 && (line = readLine()) != null && !line.startsWith("#"))
        data += line;
    String[] tokens = getTokens(data); 
    atomCount = parseInt(tokens[0]);
    boolean isPeriodic = (tokens[27].charAt(0) != '0');
    if (isPeriodic) {
      Logger.info("Periodic type: " + tokens[27]);
      htParams.put("isPeriodic", Boolean.TRUE);
    }
    Logger.info("Total number of atoms read=" + atomCount);
    htParams.put("templateAtomCount", new Integer(atomCount));
    for (int i = 0; i < atomCount; i++) 
      atomSetCollection.addAtom(new Atom());
  }
}
