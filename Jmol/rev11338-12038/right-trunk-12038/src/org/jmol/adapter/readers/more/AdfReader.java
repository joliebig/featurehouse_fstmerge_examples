
package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;

import org.jmol.util.Escape;
import org.jmol.util.Logger;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;


public class AdfReader extends MopacDataReader {

  

  private Hashtable htSymmetries;
  private SlaterData[] slaters;
  private Vector vSymmetries;
  private String energy = null;
  private int nXX = 0;

  
  public void readAtomSetCollection(BufferedReader reader) {
    atomSetCollection = new AtomSetCollection("adf", this);
    this.reader = reader;
    boolean iHaveAtoms = false;
    modelNumber = 0;
    String symLine = null;
    try {
      while (readLine() != null) {
        if (line.indexOf("Irreducible Representations, including subspecies") >= 0) {
          readSymmetries();
          continue;
        }
        if (line.indexOf("S F O s  ***  (Symmetrized Fragment Orbitals)  ***") >= 0) {
          readSlaterBasis(); 
          continue;
        }
        if (line.indexOf(" Coordinates (Cartesian, in Input Orientation)") >= 0
            || line.indexOf("G E O M E T R Y  ***  3D  Molecule  ***") >= 0) {
          if (!doGetModel(++modelNumber)) {
            if (isLastModel(modelNumber) && iHaveAtoms)
              break;
            iHaveAtoms = false;
            continue;
          }
          iHaveAtoms = true;
          readCoordinates();
          continue;
        }
        if (!iHaveAtoms)
          continue;
        if (line.indexOf("Energy:") >= 0) {
          String[] tokens = getTokens(line.substring(line.indexOf("Energy:")));
          energy = tokens[1];
          continue;
        }
        if (line.indexOf("Vibrations") >= 0) {
          readFrequencies();
          continue;
        }
        if (line.indexOf(" === ") >= 0) {
          symLine = line;
          continue;
        }
        if (line.indexOf(" ======  Eigenvectors (rows) in BAS representation") >= 0) {
          readMolecularOrbitals(getTokens(symLine)[1]);
          continue;
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
        if (ignore[i])
          continue;
        atomSetCollection.cloneLastAtomSet();
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
  
  private void readSymmetries() throws Exception {
    
    vSymmetries = new Vector();
    htSymmetries = new Hashtable();
    readLine();
    int index = 0;
    while (readLine() != null && line.length() > 1) {
      String sym = line.trim();
      SymmetryData sd = new SymmetryData(index++, sym);
      htSymmetries.put(sym, sd);
      vSymmetries.add(sd);
    }
  }

  class SymmetryData {
    int index;
    String sym;
    int nSFO;
    int nBF;
    float[][] coefs;
    Hashtable[] mos;
    int[] basisFunctions;
    public SymmetryData(int index, String sym) {
      this.index = index;
      this.sym = sym;
    }
    
  }
  
  private void readSlaterBasis() throws Exception {
    if (vSymmetries == null)
      return;
    int nBF = 0;
    for (int i = 0; i < vSymmetries.size(); i++) {
      SymmetryData sd = (SymmetryData) vSymmetries.get(i);
      discardLinesUntilContains("=== " + sd.sym + " ===");
      if (line == null)
        return;
    
      sd.nSFO = parseInt(readLine().substring(15)); 
      sd.nBF = parseInt(readLine().substring(75));
      String funcList = "";
      while (readLine() != null && line.length() > 1)
        funcList += line;
      String[] tokens = getTokens(funcList);
      if (tokens.length != sd.nBF)
        return;
      sd.basisFunctions = new int[tokens.length];
      for (int j = tokens.length; --j >= 0; ) {
        int n = parseInt(tokens[j]);
        if (n > nBF)
          nBF = n;
        sd.basisFunctions[j] = n - 1;
      }
    }
    slaters = new SlaterData[nBF];
        
    discardLinesUntilContains("(power of)");
    discardLines(2);
    while (readLine() != null && line.indexOf("Total") < 0) {
      String[] tokens = getTokens();
      int nAtoms = tokens.length - 1;
      int[] atomList = new int[nAtoms];
      for (int i = 1; i <= nAtoms; i++)
        atomList[i - 1] = parseInt(tokens[i]) - 1;
      readLine();
      while (readLine() != null && line.length() >= 10) {
        tokens = getTokens();
        boolean isCore = tokens[0].equals("Core");
        int pt = (isCore ? 1 : 0);
        int x = parseInt(tokens[pt++]);
        int y = parseInt(tokens[pt++]);
        int z = parseInt(tokens[pt++]);
        int r = parseInt(tokens[pt++]);
        float zeta = parseFloat(tokens[pt++]);
        for (int i = 0; i < nAtoms; i++) {
          int ptBF = parseInt(tokens[pt++]) - 1;
          slaters[ptBF] = new SlaterData(atomList[i], x, y, z, r, zeta, isCore);
        }
      }
    }
  }

  private class SlaterData {
    boolean isCore;
    int iAtom;
    int x;
    int y;
    int z;
    int r;
    float alpha;
    public int pt;
    public SlaterData(int iAtom, int x, int y, int z, int r, float alpha, boolean isCore) {
      this.iAtom = iAtom;
      this.x = x;
      this.y = y;
      this.z = z;
      this.r = r;
      this.alpha = alpha;
      this.isCore = isCore;      
    }
  }
  
  private void readMolecularOrbitals(String sym) throws Exception {
    
    SymmetryData sd = (SymmetryData) htSymmetries.get(sym);
    if (sd == null)
      return;
    int ptSym = sd.index;
    boolean isLast = (ptSym == vSymmetries.size() - 1);
    int n = 0;
    int nBF = slaters.length;
    sd.coefs = new float[sd.nSFO][nBF];
    while (n < sd.nBF) {
      readLine();
      int nLine = getTokens(readLine()).length;
      readLine();
      sd.mos = new Hashtable[sd.nSFO];
      String[][] data = new String[sd.nSFO][];
      fillDataBlock(data);
      for (int j = 1; j < nLine; j++) {
        int pt = sd.basisFunctions[n++];
        for (int i = 0; i < sd.nSFO; i++)
          sd.coefs[i][pt] = parseFloat(data[i][j]);
      }
    }
    for (int i = 0; i < sd.nSFO; i++) {
      Hashtable mo = new Hashtable();
      mo.put("coefficients", sd.coefs[i]);
      
      mo.put("id", sym + " " + (i + 1));
      sd.mos[i] = mo;
    }
    if (!isLast)
      return;
   
    discardLinesUntilContains("Orbital Energies, all Irreps");
    discardLines(4);
    while (readLine() != null && line.length() > 10) {
      String[] tokens = getTokens();
      sd = (SymmetryData) htSymmetries.get(tokens[0]);
      int moPt = parseInt(tokens[1]) - 1;
      Hashtable mo = sd.mos[moPt];
      mo.put("occupancy", new Float(parseFloat(tokens[2])));
      mo.put("energy", new Float(parseFloat(tokens[4]))); 
      mo.put("symmetry", sd.sym + "_" + (sd.index + 1));
      orbitals.add(mo);
    }
    int[] pointers = new int[nBF];
    for (int i = 0; i < nBF; i++)
      slaters[i].pt = i;
    Arrays.sort(slaters, new SlaterSorter());
    int iAtom0 = atomSetCollection.getLastAtomSetAtomIndex();
    for (int i = 0; i < nBF; i++) {
      SlaterData sld = slaters[i];
      addSlater(iAtom0 + sld.iAtom, sld.x, sld.y, sld.z, sld.r, sld.alpha, 1);
      pointers[i] = slaters[i].pt;
    }
    setSlaters();
    sortOrbitalCoefficients(pointers);
    sortOrbitals();
    setMOs("eV");
  }
  
  class SlaterSorter implements Comparator {

    public int compare(Object arg0, Object arg1) {
      SlaterData s0 = (SlaterData) arg0;
      SlaterData s1 = (SlaterData) arg1;
      return (s0.iAtom < s1.iAtom ? -1 : s0.iAtom > s1.iAtom ? 1 : 0);
    }
    
  }
}
