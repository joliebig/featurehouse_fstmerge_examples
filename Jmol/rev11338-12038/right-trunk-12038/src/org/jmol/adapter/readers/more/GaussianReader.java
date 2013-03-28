

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Vector3f;

import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;


public class GaussianReader extends MOReader {
  
  
  private final static int STD_ORIENTATION_ATOMIC_NUMBER_OFFSET = 1;
  
  
  private String energyString = "";
  
  private String energyKey = "";
  
  
  private int calculationNumber = 1;
  
  
  private int scanPoint = -1;
  
  
  private int equivalentAtomSets = 0;
  
  

  public void readAtomSetCollection(BufferedReader reader) {
    readAtomSetCollection(reader, "gaussian");
  }
  
  private int stepNumber = 0;

  protected boolean checkLine() throws Exception {
    if (Logger.debugging)
      Logger.debug(line);
    if (line.startsWith(" Step number")) {
      equivalentAtomSets = 0;
      stepNumber++;
      
      int scanPointIndex = line.indexOf("scan point");
      if (scanPointIndex > 0) {
        scanPoint = parseInt(line, scanPointIndex + 10);
      } else {
        scanPoint = -1; 
      }
      return true;
    }
    if (line.indexOf("-- Stationary point found") > 0) {
      
      
      
      if (scanPoint >= 0)
        scanPoint++;
      return true;
    }
    if (line.indexOf("Input orientation:") >= 0
        || line.indexOf("Z-Matrix orientation:") >= 0
        || line.indexOf("Standard orientation:") >= 0) {
      if (doGetModel(++modelNumber)) {
        equivalentAtomSets++;
        if (Logger.debugging) {
          Logger.debug(" model " + modelNumber + " step " + stepNumber
              + " equivalentAtomSet " + equivalentAtomSets + " calculation "
              + calculationNumber + " scan point " + scanPoint + line);
        }
        readAtoms();
        iHaveAtoms = true;
        return false;
      }
      if (isLastModel(modelNumber) && iHaveAtoms) {
        continuing = false;
        return false;
      }
      iHaveAtoms = false;
      return true;
    }
    if (!iHaveAtoms)
      return true;
    if (line.startsWith(" Energy=")) {
      setEnergy();
      return true;
    }
    if (line.startsWith(" SCF Done:")) {
      readSCFDone();
      return true;
    }
    if (line.startsWith(" Harmonic frequencies")) {
      readFrequencies();
      return true;
    }
    if (line.startsWith(" Total atomic charges:")
        || line.startsWith(" Mulliken atomic charges:")) {
      
      
      
      readPartialCharges();
      return true;
    }
    if (line.startsWith(" Dipole moment")) {
      readDipoleMoment();
      return true;
    }
    if (line.startsWith(" Standard basis:")) {
      Logger.debug(line);
      energyUnits = "";
      calculationType = line.substring(17).trim();
      return true;
    }
    if (line.startsWith(" General basis read from cards:")) {
      Logger.debug(line);
      energyUnits = "";
      calculationType = line.substring(31).trim();
      return true;
    }
    if (line.startsWith(" AO basis set")) {
      readBasis();
      return true;
    }
    if (line.indexOf("Molecular Orbital Coefficients") >= 0) {
      if (!filterMO())
        return true;
      readGaussianMolecularOrbitals();
      if (Logger.debugging) {
        Logger.debug(orbitals.size() + " molecular orbitals read");
      }
      return true;
    }
    if (line.startsWith(" Normal termination of Gaussian")) {
      ++calculationNumber;
      equivalentAtomSets = 0;
      
      return true;
    }
    return checkNboLine();
  }
  
  
  private void readSCFDone() throws Exception {
    String tokens[] = getTokens(line, 11);
    if (tokens.length < 4)
      return;
    energyKey = tokens[0];
    energyString = tokens[2] + " " + tokens[3];
    
    atomSetCollection.setAtomSetNames(energyKey + " = " + energyString,
        equivalentAtomSets);
    
    atomSetCollection.setAtomSetProperties(energyKey, energyString,
        equivalentAtomSets);
    tokens = getTokens(readLine());
    if (tokens.length > 2) {
      atomSetCollection.setAtomSetProperties(tokens[0], tokens[2],
          equivalentAtomSets);
      if (tokens.length > 5)
        atomSetCollection.setAtomSetProperties(tokens[3], tokens[5],
            equivalentAtomSets);
      tokens = getTokens(readLine());
    }
    if (tokens.length > 2)
      atomSetCollection.setAtomSetProperties(tokens[0], tokens[2],
          equivalentAtomSets);
  }
  
  
  private void setEnergy() {
    String tokens[] = getTokens();
    energyKey = "Energy";
    energyString = tokens[1];
    atomSetCollection.setAtomSetNames("Energy = "+tokens[1], equivalentAtomSets);
  }
  
  
  
  
  
  
  
  
  
  private void readAtoms() throws Exception {
    atomSetCollection.newAtomSet();
    
    
    
    
    atomSetCollection.setAtomSetName(energyKey + " = " + energyString);

    String path = getTokens()[0]; 
    discardLines(4);
    String tokens[];
    while (readLine() != null &&
        !line.startsWith(" --")) {
      tokens = getTokens(); 
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementNumber =
        (byte)parseInt(tokens[STD_ORIENTATION_ATOMIC_NUMBER_OFFSET]);
      if (atom.elementNumber < 0)
        atom.elementNumber = 0; 
      int offset = tokens.length-3;
      atom.x = parseFloat(tokens[offset]);
      atom.y = parseFloat(tokens[++offset]);
      atom.z = parseFloat(tokens[++offset]);
    }
    atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
        "Calculation "+calculationNumber+
        (scanPoint>=0?(SmarterJmolAdapter.PATH_SEPARATOR+"Scan Point "+scanPoint):"")+
        SmarterJmolAdapter.PATH_SEPARATOR+path);
  }
  
  

  void readBasis() throws Exception {
    shells = new Vector();
    Vector gdata = new Vector();
    int atomCount = 0;
    gaussianCount = 0;
    shellCount = 0;
    String lastAtom = "";
    String[] tokens;
    
    boolean doSphericalD = (calculationType != null && (calculationType.indexOf("5D") > 0));
    boolean doSphericalF = (calculationType != null && (calculationType.indexOf("7F") > 0));
    while (readLine() != null && line.startsWith(" Atom")) {
      shellCount++;
      tokens = getTokens();
      int[] slater = new int[4];
      if (!tokens[1].equals(lastAtom))
        atomCount++;
      lastAtom = tokens[1];
      slater[0] = atomCount - 1;
      String oType = tokens[4];
      if (doSphericalF && oType.indexOf("F") >= 0 || doSphericalD && oType.indexOf("D") >= 0)
        slater[1] = JmolAdapter.getQuantumShellTagIDSpherical(tokens[4]);
      else
        slater[1] = JmolAdapter.getQuantumShellTagID(tokens[4]);
      
      int nGaussians = parseInt(tokens[5]);
      slater[2] = gaussianCount; 
      slater[3] = nGaussians;
      shells.addElement(slater);
      gaussianCount += nGaussians;
      for (int i = 0; i < nGaussians; i++)
        gdata.addElement(getTokens(readLine()));
    }
    if (atomCount == 0)
      atomCount = 1;
    gaussians = new float[gaussianCount][];
    for (int i = 0; i < gaussianCount; i++) {
      tokens = (String[]) gdata.get(i);
      gaussians[i] = new float[tokens.length];
      for (int j = 0; j < tokens.length; j++)
        gaussians[i][j] = parseFloat(tokens[j]);
    }
    if (Logger.debugging) {
      Logger.debug(shellCount + " slater shells read");
      Logger.debug(gaussianCount + " gaussian primitives read");
    }
  }
  
  
  void readGaussianMolecularOrbitals() throws Exception {
    if (shells == null)
      return;
    Hashtable[] mos = new Hashtable[5];
    Vector[] data = new Vector[5];
    int nThisLine = 0;
    while (readLine() != null
        && line.toUpperCase().indexOf("DENS") < 0) {
      String[] tokens;
      if (line.indexOf("                    ") == 0) {
        addMOData(nThisLine, data, mos);
        tokens = getTokens(readLine());
        nThisLine = tokens.length;
        for (int i = 0; i < nThisLine; i++) {
          mos[i] = new Hashtable();
          data[i] = new Vector();
          mos[i].put("symmetry", tokens[i]);
        }
        line = readLine().substring(21);
        tokens = getTokens();
        if (tokens.length != nThisLine)
          tokens = getStrings(line, nThisLine, 10);
        for (int i = 0; i < nThisLine; i++)
          mos[i].put("energy", new Float(tokens[i]));
        continue;
      } else if (line.length() < 21 || (line.charAt(11) != ' ' 
                                        && ! Character.isDigit(line.charAt(11)) 
                                        ) ) {
        continue;
      }
      try {
        tokens = getStrings(line.substring(21), nThisLine, 10);
        for (int i = 0; i < nThisLine; i++)
          data[i].addElement(tokens[i]);
      } catch (Exception e) {
        Logger.error("Error reading Gaussian file Molecular Orbitals at line: "
            + line);
        break;
      }
    }
    addMOData(nThisLine, data, mos);
    setMOData(false); 
  }

  
  
  
  
  private void readFrequencies() throws Exception, IOException {
    discardLinesUntilContains(":");
    if (line == null)
      throw (new Exception("No frequencies encountered"));
    while ((line= readLine()) != null && line.length() > 15) {
      
      String[] symmetries = getTokens(readLine());
      String[] frequencies = getTokens(
          discardLinesUntilStartsWith(" Frequencies"), 15);
      String[] red_masses = getTokens(
          discardLinesUntilStartsWith(" Red. masses"), 15);
      String[] frc_consts = getTokens(
          discardLinesUntilStartsWith(" Frc consts"), 15);
      String[] intensities = getTokens(
          discardLinesUntilStartsWith(" IR Inten"), 15);
      int iAtom0 = atomSetCollection.getAtomCount();
      int atomCount = atomSetCollection.getLastAtomSetAtomCount();
      int frequencyCount = frequencies.length;
      boolean[] ignore = new boolean[frequencyCount];
      for (int i = 0; i < frequencyCount; ++i) {
        ignore[i] = !doGetVibration(++vibrationNumber);
        if (ignore[i])
          continue;  
        atomSetCollection.cloneLastAtomSet();
        atomSetCollection.setAtomSetName(
            symmetries[i] + " " + frequencies[i]+" cm^-1");
        
        atomSetCollection.setAtomSetProperty(energyKey, energyString);
        atomSetCollection.setAtomSetProperty("Frequency",
            frequencies[i]+" cm^-1");
        atomSetCollection.setAtomSetProperty("Reduced Mass",
            red_masses[i]+" AMU");
        atomSetCollection.setAtomSetProperty("Force Constant",
            frc_consts[i]+" mDyne/A");
        atomSetCollection.setAtomSetProperty("IR Intensity",
            intensities[i]+" KM/Mole");
        atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
            "Calculation " + calculationNumber+
            SmarterJmolAdapter.PATH_SEPARATOR+"Frequencies");
      }
      discardLinesUntilContains(" AN ");
      fillFrequencyData(iAtom0, atomCount, ignore, true, 0, 0);
    }
  }
  
  void readDipoleMoment() throws Exception {
    
    String tokens[] = getTokens(readLine());
    if (tokens.length != 8)
      return;
    Vector3f dipole = new Vector3f(parseFloat(tokens[1]),
        parseFloat(tokens[3]), parseFloat(tokens[5]));
    Logger.info("Molecular dipole for model " + atomSetCollection.getAtomSetCount()
        + " = " + dipole);
    atomSetCollection.setAtomSetAuxiliaryInfo("dipole", dipole);
  }

  
  
  
  
  
  
  
  void readPartialCharges() throws Exception {
    discardLines(1);
    int atomCount = atomSetCollection.getAtomCount();
    int i0 = atomSetCollection.getLastAtomSetAtomIndex();
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = i0; i < atomCount; ++i) {
      
      while (atoms[i].elementNumber == 0)
        ++i;
      
      float charge = parseFloat(getTokens(readLine())[2]);
      atoms[i].partialCharge = charge;
    }
    Logger.info("Mulliken charges found for Model " + atomSetCollection.getAtomSetCount());
  }
  
}
