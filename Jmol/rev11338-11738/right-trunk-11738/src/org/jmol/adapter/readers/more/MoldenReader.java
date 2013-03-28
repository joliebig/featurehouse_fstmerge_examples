package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Vector;
import java.util.NoSuchElementException;

import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;



public class MoldenReader extends MopacDataReader {
  protected float[] frequencies = null;
  protected AtomSetCollection freqAtomSet = null;
  
	public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("molden", this);
    modelNumber = 0;
    try {
      readLine();
      while (line != null) {
        if (line.indexOf("[Atoms]") >= 0 || line.indexOf("[ATOMS]") >= 0) {
          readAtoms();
          continue;
        } else if (line.indexOf("[GTO]") >= 0) {
          readGaussianBasis();
          continue;
        } else if (line.indexOf("[MO]") >= 0) {
          readMolecularOrbitals();
          continue;
        } else if (line.indexOf("[FREQ]") >= 0) {
          readFreqsAndModes();
          continue;
        }
        readLine();
      }
    } catch (Exception e) {
      setError(e);
    }

  }
  
  void readAtoms() throws Exception {
    
    
    String coordUnit = getTokens()[1];
    
    int nPrevAtom = 0, nCurAtom = 0;
   
    boolean isAU = (coordUnit.indexOf("Angs") < 0); 
    if (isAU && coordUnit.indexOf("AU") < 0) {
      throw new Exception("invalid coordinate unit " + coordUnit + " in [Atoms]"); 
    }
    
    readLine();
    while (line != null && line.indexOf('[') < 0) {    
      Atom atom = atomSetCollection.addNewAtom();
      String [] tokens = getTokens();
      atom.atomName = tokens[0];
      
      
      
      nCurAtom = parseInt(tokens[1]);
      if (nPrevAtom > 0 && nCurAtom != nPrevAtom + 1 ) { 
        throw new Exception("out of order atom in [Atoms]");
      } 
      nPrevAtom = nCurAtom;
      atom.set(parseFloat(tokens[3]), parseFloat(tokens[4]), parseFloat(tokens[5]));
      readLine();
    }
    
    if (isAU)
      for (int i = atomSetCollection.getAtomCount(); --i >= 0;)
        atomSetCollection.getAtom(i).scale(ANGSTROMS_PER_BOHR);
  }
  
  void readGaussianBasis() throws Exception {
    
    Vector sdata = new Vector();
    Vector gdata = new Vector();
    int atomIndex = 0;
    int gaussianPtr = 0;
    
    while (readLine() != null 
        && ! ((line = line.trim()).length() == 0 || line.charAt(0) == '[') ) {
      
      
      String[] tokens = getTokens();
      
      atomIndex = parseInt(tokens[0]) - 1;
      
      
      while (readLine() != null && line.trim().length() > 0) {
        
        tokens = getTokens();
        String shellLabel = tokens[0].toUpperCase();
        int nPrimitives = parseInt(tokens[1]);
        int[] slater = new int[4];
        
        slater[0] = atomIndex;
        slater[1] = JmolAdapter.getQuantumShellTagID(shellLabel);
        slater[2] = gaussianPtr;
        slater[3] = nPrimitives;
        
        for (int ip = nPrimitives; --ip >= 0;) {
          
          
          String [] primTokens = getTokens(readLine());
          int nTokens = primTokens.length;
          float orbData[] = new float[nTokens];
          
          for (int d = 0; d < nTokens; d++)
            orbData[d] = parseFloat(primTokens[d]);
          gdata.addElement(orbData);
          gaussianPtr++;
        }
        sdata.addElement(slater);
      }      
      
    }

    float [][] garray = new float[gaussianPtr][];
    for (int i = 0; i < gaussianPtr; i++)
      garray[i] = (float[]) gdata.get(i);
    moData.put("shells", sdata);
    moData.put("gaussians", garray);
    if (Logger.debugging) {
      Logger.debug(sdata.size() + " slater shells read");
      Logger.debug(garray.length + " gaussian primitives read");
    }
    atomSetCollection.setAtomSetAuxiliaryInfo("moData", moData);
  }
  
  void readMolecularOrbitals() throws Exception {
    
    
    readLine();
    if (line.equals("[5D]")) {
      
      
      
      
      Vector sdata = (Vector) moData.get("shells");
      for (int i = sdata.size(); --i >=0 ;) {
        int[] slater = (int[]) sdata.get(i);
        switch (slater[1]) {
        case JmolAdapter.SHELL_D_CARTESIAN:
          slater[1] = JmolAdapter.SHELL_D_SPHERICAL;
          break;
        case JmolAdapter.SHELL_F_CARTESIAN:
          slater[1] = JmolAdapter.SHELL_F_SPHERICAL;
          break;
        default:
          
          break;
        }
      }
      
      readLine();
    }
    
    String[] tokens = getTokens();
    while (tokens != null &&  line.indexOf('[') < 0) {
      Hashtable mo = new Hashtable();
      Vector data = new Vector();
      float energy = Float.NaN;
      float occupancy = Float.NaN;
      
      while (tokens != null && parseInt(tokens[0]) == Integer.MIN_VALUE) {
        String[] kvPair;
        if (tokens[0].startsWith("Ene")) {
          kvPair = splitKeyValue();
          energy = parseFloat(kvPair[1]);          
        } else if (tokens[0].startsWith("Occup")) {
          kvPair = splitKeyValue();
          occupancy = parseFloat(kvPair[1]);
        }  
        tokens = getTokens(readLine());
      }
      
      if (tokens == null)
        throw new Exception("error reading MOs: unexpected EOF reading coeffs");
      
      while (tokens != null && parseInt(tokens[0]) != Integer.MIN_VALUE) {
        if (tokens.length != 2)
          throw new Exception("invalid MO coefficient specification");
        
        data.addElement(tokens[1]);
        tokens = getTokens(readLine());
      }
      
      float[] coefs = new float[data.size()];
      for (int i = data.size(); --i >= 0;) {
        coefs[i] = parseFloat((String) data.get(i));
      }
      mo.put("energy", new Float(energy));
      mo.put("occupancy", new Float(occupancy));
      mo.put("coefficients", coefs);
      orbitals.addElement(mo);
      if (Logger.debugging) {
        Logger.debug(coefs.length + " coefficients in MO " + orbitals.size() );
      }
    }
    Logger.debug("read " + orbitals.size() + " MOs");
    setMOs("eV");
  }
  
  void readFreqsAndModes() throws Exception {
    String[] tokens;
    Vector frequencies = new Vector();
    while (readLine() != null && line.indexOf('[') < 0) {
      frequencies.add(getTokens()[0]);
    }
    if (line.indexOf("[FR-COORD]") < 0)
      throw new Exception("error reading normal modes: [FREQ] must be followed by [FR-COORD]");
    
    final int nFreqs = frequencies.size();
    final int nAtoms = atomSetCollection.getFirstAtomSetAtomCount();
    atomSetCollection.cloneLastAtomSet();
    atomSetCollection.setAtomSetName("frequency base geometry");
    Atom[] atoms = atomSetCollection.getAtoms();
    int i0 = atomSetCollection.getLastAtomSetAtomIndex();
    for (int nAtom = 0; nAtom < nAtoms; nAtom++) {
      tokens = getTokens(readLine());
      Atom atom = atoms[nAtom + i0];
      atom.atomName = tokens[0];
      atom.set(parseFloat(tokens[1]), parseFloat(tokens[2]), parseFloat(tokens[3]));
      atom.scale(ANGSTROMS_PER_BOHR);      
    }
      
    readLine();
    if (line.indexOf("[FR-NORM-COORD]") < 0) 
      throw new Exception("error reading normal modes: [FR-COORD] must be followed by [FR-NORM-COORD]");
    
    for (int nFreq = 0; nFreq < nFreqs; nFreq++) {
      if (readLine().indexOf("Vibration") < 0)
        throw new Exception("error reading normal modes: expected vibration data");
      atomSetCollection.cloneLastAtomSet();
      boolean ignore = !doGetVibration(nFreq + 1);
      if (!ignore) {
        atomSetCollection.setAtomSetName(frequencies.get(nFreq) + " cm-1");
        i0 = atomSetCollection.getLastAtomSetAtomIndex();
      }
      for (int nAtom = 0; nAtom < nAtoms; nAtom++) {
        tokens = getTokens(readLine());
        if (!ignore)
          atomSetCollection.addVibrationVector(nAtom + i0,
              parseFloat(tokens[0]) * ANGSTROMS_PER_BOHR,
              parseFloat(tokens[1]) * ANGSTROMS_PER_BOHR,
              parseFloat(tokens[2]) * ANGSTROMS_PER_BOHR
          );
      }      
    }
    readLine();
  }

  String[] splitKeyValue() {
    return splitKeyValue("=", line);
  }
  
  String[] splitKeyValue(String sep) {
    return splitKeyValue(sep, line);
  }
  
  String[] splitKeyValue(String sep, String text) throws NoSuchElementException {
    String[] kvPair = new String[2];
    int posSep = text.indexOf(sep);
    if (posSep < 0)
      throw new NoSuchElementException("separator not found");
    kvPair[0] = text.substring(0, posSep);
    kvPair[1] = text.substring(posSep + sep.length());
    return kvPair;    
  }
}
