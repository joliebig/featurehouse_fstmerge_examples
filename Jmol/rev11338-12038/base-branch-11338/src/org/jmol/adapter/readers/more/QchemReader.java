

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;



public class QchemReader extends MOReader {
 

  private int calculationNumber = 1;

  MOInfo[] alphas = null;
  MOInfo[] betas = null;
  int nShell = 0;          
  int nBasis = 0;          

  
  public void readAtomSetCollection(BufferedReader reader) {
    readAtomSetCollection(reader, "qchem");
  }

  
  protected boolean checkLine() throws Exception {
    if (line.indexOf("Standard Nuclear Orientation") >= 0) {
      readAtoms();
      moData = null; 
      return true;
    }
    if (line.indexOf("Requested basis set is") >= 0) {
      readCalculationType();
      return true;
    }
    if (line.indexOf("VIBRATIONAL FREQUENCIES") >= 0) {
      readFrequencies();
      return true;
    }
    if (line.indexOf("Mulliken Net Atomic Charges") >= 0) {
      readPartialCharges();
      return true;
    }
    if (line.startsWith("Job ")) {
      calculationNumber++;
      moData = null; 
      return true;
    }
    if (line.indexOf("Basis set in general basis input format") >= 0) {
      if (moData == null) {
        
        readBasis();
      }
      return true;
    }
    if (moData == null)
      return true;
    if (line.indexOf("Orbital Energies (a.u.) and Symmetries") >= 0) {
      if (filterMO())
        readESym(true);
      return true;
    }
    if (line.indexOf("Orbital Energies (a.u.)") >= 0) {
      if (filterMO())
        readESym(false);
      return true;
    }
    if (line.indexOf("MOLECULAR ORBITAL COEFFICIENTS") >= 0) {
      if (filterMO())
        readQchemMolecularOrbitals();
      return true;
    }
    return checkNboLine();
  }

  private void readCalculationType() {
    calculationType = line.substring(line.indexOf("set is") + 6).trim();
  }




  void readAtoms() throws Exception {
    atomSetCollection.newAtomSet();
    
    discardLines(2);
    String[] tokens;
    while (readLine() != null && !line.startsWith(" --")) {
      tokens = getTokens();
      if (tokens.length < 5)
        continue;
      String symbol = tokens[1];
      if (JmolAdapter.getElementNumber(symbol) < 1)
        continue;
      
      float x = parseFloat(tokens[2]);
      float y = parseFloat(tokens[3]);
      float z = parseFloat(tokens[4]);
      if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
        continue;
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = symbol;
      atom.set(x, y, z);
      atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
          "Calculation "+calculationNumber);
   }
  }
  
  
  private void readFrequencies() throws Exception, IOException {
    String[] tokens; String[] frequencies;
    
    
    frequencies = getTokens(discardLinesUntilStartsWith(" Frequency:"));
   
    
    
    while (true)
    {
      int frequencyCount = frequencies.length;
      
      for (int i = 1; i < frequencyCount; ++i) {
        atomSetCollection.cloneLastAtomSet();
        atomSetCollection.setAtomSetName(frequencies[i]+" cm**-1");
        
        atomSetCollection.setAtomSetProperty("Frequency",
            frequencies[i]+" cm**-1");
        atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
            "Calculation " + calculationNumber+
            SmarterJmolAdapter.PATH_SEPARATOR+"Frequencies");
      }
      
      int atomCount = atomSetCollection.getLastAtomSetAtomCount();
      int firstModelAtom =
        atomSetCollection.getAtomCount() - frequencyCount * atomCount;
      
      
      discardLinesUntilStartsWith("               X");
      
      
      float x, y, z;
      Atom[] atoms = atomSetCollection.getAtoms();
      for (int i = 0; i < atomCount; ++i) {
        tokens = getTokens(readLine());
        for (int j = 1, offset=1; j < frequencyCount; ++j) {
          int atomOffset = firstModelAtom+j*atomCount + i ;
          Atom atom = atoms[atomOffset];
          x = parseFloat(tokens[offset++]);
          y = parseFloat(tokens[offset++]);
          z = parseFloat(tokens[offset++]);
          atom.addVibrationVector(x, y, z);
        }
      }
      
      while ((line= readLine()) != null && line.length() > 0) { }
      
      line=readLine();
      if (line.indexOf("STANDARD")>=0) {
        break;  
      } else if (line.indexOf(" Frequency:") == -1) {
        frequencies = getTokens(discardLinesUntilStartsWith(" Frequency:"));
      } else {
        frequencies = getTokens(line);
      }
    }
  }

  void readPartialCharges() throws Exception {
    discardLines(3);
    Atom[] atoms = atomSetCollection.getAtoms();
    int atomCount = atomSetCollection.getLastAtomSetAtomCount();
    for (int i = 0; i < atomCount && readLine() != null; ++i)
      atoms[i].partialCharge = parseFloat(getTokens()[2]);
  }




  private void readBasis() throws Exception {
    
    moData = new Hashtable();
    int atomCount = 0;
    int shellCount = 0;
    int gaussianCount = 0;
    
    Vector sdata = new Vector();
    Vector gdata = new Vector();
    String[] tokens;

    discardLinesUntilStartsWith("$basis");
    readLine(); 
    while (readLine() != null) {  
      if (line.startsWith("****")) {
        atomCount++;           
        if (readLine() != null && line.startsWith("$end")) break;
        continue; 
      }
      shellCount++;
      int[] slater = new int[4];
      tokens = getTokens(line);
      slater[0] = atomCount;
      slater[1] = JmolAdapter.getQuantumShellTagID(tokens[0]); 
      slater[2] = gaussianCount;
      int nGaussians = parseInt(tokens[1]);
      slater[3] = nGaussians;
      sdata.addElement(slater);
      gaussianCount += nGaussians;
      for (int i = 0; i < nGaussians; i++)
        gdata.addElement(getTokens(readLine()));     
    }
    
    float[][] garray = new float[gaussianCount][];
    for (int i = 0; i < gaussianCount; i++) {
      tokens = (String[]) gdata.get(i);
      garray[i] = new float[tokens.length];
      for (int j = 0; j < tokens.length; j++)
        garray[i][j] = parseFloat(tokens[j]);
    }
    moData.put("shells", sdata);
    moData.put("gaussians", garray);
    if (Logger.debugging) {
      Logger.debug(shellCount + " slater shells read");
      Logger.debug(gaussianCount + " gaussian primitives read");
    }
    discardLinesUntilStartsWith(" There are");
    tokens = getTokens(line);
    nShell = parseInt(tokens[2]);
    nBasis = parseInt(tokens[5]);
    moData.put("calculationType", calculationType);
  }


  
  protected void readESym(boolean haveSym) throws Exception {
    String[] tokens, spin = {"A","B"};
    alphas = new MOInfo[nBasis];
    betas = new MOInfo[nBasis];
    MOInfo[] moInfos;
    int ne=0;  
    boolean readBetas = false;

    discardLinesUntilStartsWith(" Alpha");
    tokens = getTokens(line); 
    moInfos = alphas;
    for (int e = 0; e < 2; e++) { 
      int nMO = 0;
      while (readLine() != null) { 
        if (line.startsWith(" -- ")) {
          ne = 0;
          if (line.indexOf("Vacant") < 0) {
            if (line.indexOf("Occupied") > 0) ne = 1;
          }
          readLine();
        }
        if (line.startsWith(" -------")) {
          e = 2; 
          break; 
        }
        int nOrbs = getTokens(line).length;
        if (nOrbs == 0 || line.startsWith(" Warning")) { 
          discardLinesUntilStartsWith(" Beta"); 
          readBetas = true;
          moInfos = betas;
          break;
        }
        if (haveSym) tokens = getTokens(readLine());
        for (int i=0, j=0; i < nOrbs; i++, j+=2) {
          MOInfo info = new MOInfo();
          info.ne = ne;
          info.label = spin[e];
          if (haveSym) info.symmetry = tokens[j]+tokens[j+1];
          moInfos[nMO] = info;
          nMO++;
        }
      }
    }
    if (!readBetas) betas=alphas; 
  }



  private void readQchemMolecularOrbitals() throws Exception {
    
    
    int nOrbitalsPerShell[] = {1,3,4,6,5,10,7};
    
   int[][] reorder = {
        {0},
        {0, 1, 2},
        {0, 1, 2, 3},
        {0, 3, 1, 4, 5, 2},
        {4, 2, 0, 1, 3},
        {0, 4, 3, 1, 5, 9, 8, 6, 7, 2},
        {6, 4, 2, 0, 1, 3, 5}
    };
    float[] reordered = new float[10];
    int nMOs;  
    
    Vector orbitals = new Vector();
    String[] aoLabels = new String[nBasis];
    String orbitalType = getTokens(line)[0]; 
    nMOs = readMOs(orbitalType.equals("RESTRICTED"), aoLabels, orbitals, alphas);
    if (orbitalType.equals("ALPHA")) { 
      discardLinesUntilContains("BETA");
      nMOs += readMOs(false, aoLabels, orbitals, betas);
    }
    
    int iAO = 0; 
    Vector sdata = (Vector) moData.get("shells");
    
    float[][] mocoef = new float[nMOs][];
    for (int i = 0; i < nMOs; i++) { 
      Hashtable orb = (Hashtable) orbitals.get(i);
      mocoef[i] = (float[]) orb.get("coefficients");
    }    
    for (int i = 0; i < nShell; i++) {
      int[] slater = (int[]) sdata.get(i);
      if (getTokens(aoLabels[iAO]).length > 1 )  
        slater[1] += slater[1] % 2; 
      int nOrbs = nOrbitalsPerShell[slater[1]];
      
      if (slater[1] >= JmolAdapter.SHELL_D_CARTESIAN) {
        for (int j=0; j< nMOs; j++) {
          int[] order = reorder[slater[1]];
          for (int k=0, l=iAO; k < nOrbs; k++, l++)
            reordered[order[k]] = mocoef[j][l]; 
          for (int k=0, l=iAO; k < nOrbs; k++, l++)
            mocoef[j][l] = reordered[k];        
        }
      }
      iAO += nOrbs;
    }   
    moData.put("mos", orbitals);
    moData.put("energyUnits", "au");
    setMOData(moData);
  }

  private int readMOs(boolean restricted, String[] aoLabels,
                      Vector orbitals, MOInfo[] moInfos) throws Exception {
    Hashtable[] mos = new Hashtable[6];  
    float[][] mocoef = new float[6][];   
    int[] moid = new int[6];             
    String[] tokens, energy;
    int nMOs = 0;
    
    while (readLine().length() > 2) {
      tokens = getTokens(line);
      int nMO = tokens.length;    
      energy = getTokens(readLine().substring(13));
      for (int i = 0; i < nMO; i++) {
        moid[i] = parseInt(tokens[i])-1;
        mocoef[i] = new float[nBasis];
        mos[i] = new Hashtable();
      }
      for (int i = 0; i < nBasis; i++) {
        tokens = getTokens(readLine());
        aoLabels[i] = line.substring(12, 17); 
        for (int j = tokens.length-nMO, k=0; k < nMO; j++, k++)
          mocoef[k][i] = parseFloat(tokens[j]);
      }
      
      for (int i = 0; i < nMO; i++ ) {
        MOInfo moInfo = moInfos[moid[i]];
        mos[i].put("energy", new Float(energy[i]));
        mos[i].put("coefficients",mocoef[i]);
        String label = moInfo.label;
        int ne = moInfo.ne;
        if (restricted) ne = alphas[moid[i]].ne + betas[moid[i]].ne;
        mos[i].put("occupancy", new Float(ne));
        if (ne == 2) label = "AB";
        if (ne == 0) {
          if (restricted) label = "V";
          else label = "V"+label; 
        }
        mos[i].put("symmetry", moInfo.symmetry+" "+label +"("+(moid[i]+1)+")");
        orbitals.addElement(mos[i]);
      }
      nMOs += nMO;
    }
    return nMOs;
  }
  
  
  
  protected class MOInfo {
    int ne = 0;      
    String label = "???";
    String symmetry = "???";
  }
}
