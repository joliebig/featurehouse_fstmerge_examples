

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;

import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Vector;

import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;


public class PsiReader extends MOReader {

  

  public void readAtomSetCollection(BufferedReader reader) {
    readAtomSetCollection(reader, "psi");
  }

 
  protected boolean checkLine() throws Exception {
    if (line
        .indexOf("-Geometry after Center-of-Mass shift and reorientation (a.u.):") >= 0) {
      readAtoms(true); 
      iHaveAtoms = true;
    }
    if (line
        .indexOf("-Unique atoms in the canonical coordinate system (a.u.):") >= 0)
      readUniqueAtoms();
    if (!iHaveAtoms)
      return true;
    if (line.indexOf("New Cartesian Geometry in a.u.") >= 0) {
      readAtoms(false); 
      return true;
    }
    if (line.startsWith("  label        = ")) {
      moData.put("calculationType", calculationType = line.substring(17).trim());
      return true;
    }
    if (line.startsWith("molecular orbitals for ")) {
      moData.put("energyUnits", "");
      return true;
    }
    if (line.startsWith("  -BASIS SETS:")) {
      readBasis();
      return true;
    }
    if (line.indexOf("Molecular Orbital Coefficients") >= 0) {
      
      if (filterMO())
        readPsiMolecularOrbitals();
      return true;
    }
    if (line.indexOf("SCF total energy   =") >= 0) {
      readSCFDone();
      return true;
    }
    return checkNboLine();
  }

  
  private void readSCFDone() throws Exception {
    atomSetCollection.setAtomSetName(line);
  }

  
  

  Vector atomNames = new Vector();
  private void readAtoms(boolean isInitial) throws Exception {
    if (isInitial) {
      atomSetCollection.newAtomSet();
      atomSetCollection.setAtomSetName(""); 
      discardLinesUntilContains("----");
    }
    int atomPt = 0;
    while (readLine() != null && line.length() > 0) {
      String[] tokens = getTokens(); 
      Atom atom = (isInitial ? atomSetCollection.addNewAtom()
          : atomSetCollection.getAtom(atomPt++));
      if (isInitial)
        atomNames.addElement(tokens[0]);
      else
        atom.elementNumber = (byte) parseInt(tokens[0]);
      if (atom.elementNumber < 0)
        atom.elementNumber = 0; 
      atom.set(parseFloat(tokens[1]), parseFloat(tokens[2]), parseFloat(tokens[3]));
      atom.scale(ANGSTROMS_PER_BOHR);
    }
  }

  
  

  
  Vector shellsByUniqueAtom = new Vector();
  void readBasis() throws Exception {
    Vector gdata = new Vector();
    
    gaussianCount = 0;
    shellCount = 0;
    String[] tokens;
    int[] slater = null;
    Vector slatersByUniqueAtom = null;
    readLine();
    while (readLine() != null && line.startsWith("   -Basis set on")) {
      slatersByUniqueAtom = new Vector();
      int nGaussians = 0;
      while (readLine() != null && !line.startsWith("       )")) {
        line = line.replace('(', ' ').replace(')',' ');
        tokens = getTokens();
        int ipt = 0;
        switch (tokens.length) {
        case 3:
          if (slater != null)
            slatersByUniqueAtom.addElement(slater);
          ipt = 1;
          slater = new int[3];
          slater[0] = JmolAdapter.getQuantumShellTagID(tokens[0]);
          slater[1] = gaussianCount;
          shellCount++;
          break;
        case 2:
          break;
        }
        nGaussians++;
        gdata.addElement(new String[] { tokens[ipt], tokens[ipt + 1] });
        slater[2] = nGaussians;
      }
      if (slater != null)
        slatersByUniqueAtom.addElement(slater);
      shellsByUniqueAtom.addElement(slatersByUniqueAtom);
      gaussianCount += nGaussians;
      readLine();
    }
    float[][] garray = new float[gaussianCount][];
    for (int i = 0; i < gaussianCount; i++) {
      tokens = (String[]) gdata.get(i);
      garray[i] = new float[tokens.length];
      for (int j = 0; j < tokens.length; j++)
        garray[i][j] = parseFloat(tokens[j]);
    }
    moData.put("gaussians", garray);
    if (Logger.debugging) {
      Logger.debug(shellCount + " slater shells read");
      Logger.debug(gaussianCount + " gaussian primitives read");
    }
  }

  
  
  Hashtable uniqueAtomMap = new Hashtable();
  private void readUniqueAtoms() throws Exception {
    Vector sdata = new Vector();
    discardLinesUntilContains("----");
    int n = 0;
    while (readLine() != null && line.length() > 0) {
      String[] tokens = getTokens(); 
      uniqueAtomMap.put(tokens[0], new Integer(n++));
    }
    int atomCount = atomNames.size();
    for (int i = 0; i < atomCount; i++) {
      String atomType = (String) atomNames.elementAt(i);
      int iUnique = ((Integer)uniqueAtomMap.get(atomType)).intValue();
      Vector slaters = (Vector) shellsByUniqueAtom.elementAt(iUnique);
      if (slaters == null) {
        Logger.error("slater for atom " + i + " atomType " + atomType
            + " was not found in listing. Ignoring molecular orbitals");
        return;
      }
      for (int j = 0; j < slaters.size(); j++) {
        int[] slater = (int[]) slaters.elementAt(j);
        sdata.addElement(new int[] { i, slater[0], slater[1], slater[2] });
        
          
      }
    }
    moData.put("shells", sdata);

  }
  
  
  void readPsiMolecularOrbitals() throws Exception {
    Hashtable[] mos = new Hashtable[5];
    Vector[] data = new Vector[5];
    int nThisLine = 0;
    while (readLine() != null && line.toUpperCase().indexOf("DENS") < 0) {
      String[] tokens = getTokens();
      int ptData = (line.charAt(5) == ' ' ? 2 : 4);
      if (line.indexOf("                    ") == 0) {
        addMOData(nThisLine, data, mos);
        nThisLine = tokens.length;
        tokens = getTokens(readLine());
        for (int i = 0; i < nThisLine; i++) {
          mos[i] = new Hashtable();
          data[i] = new Vector();
          mos[i].put("symmetry", tokens[i]);
        }
        tokens = getStrings(readLine().substring(21), nThisLine, 10);
        for (int i = 0; i < nThisLine; i++)
          mos[i].put("energy", new Float(tokens[i]));
        continue;
      }
      try {
        for (int i = 0; i < nThisLine; i++)
          data[i].addElement(tokens[i + ptData]);
      } catch (Exception e) {
        Logger.error("Error reading Psi3 file molecular orbitals at line: "
            + line);
        break;
      }
    }
    addMOData(nThisLine, data, mos);
    moData.put("mos", orbitals);
    setMOData(moData);
  }

}
