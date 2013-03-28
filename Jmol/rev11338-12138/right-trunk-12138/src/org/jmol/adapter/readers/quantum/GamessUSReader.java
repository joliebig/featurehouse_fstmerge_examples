




package org.jmol.adapter.readers.quantum;

import java.io.BufferedReader;
import java.util.Vector;

import javax.vecmath.Vector3f;

import org.jmol.adapter.smarter.Atom;
import org.jmol.util.Logger;

public class GamessUSReader extends GamessReader {

  public void readAtomSetCollection(BufferedReader reader) {
    readAtomSetCollection(reader, "gamess");
  }

  

  
  protected boolean checkLine() throws Exception {
    boolean isBohr;
    if (line.contains("BASIS OPTIONS")){
      readBasisInfo();
      return true;
    }    
    if (line.contains("$CONTRL OPTIONS")){
      readControlInfo();
      return true;
    }
    if (line.indexOf("ATOMIC BASIS SET") >= 0) {
      readGaussianBasis("SHELL TYPE", "TOTAL");
      return false;
    }
    if ((isBohr = line.indexOf("COORDINATES (BOHR)") >= 0)
        || line.indexOf("COORDINATES OF ALL ATOMS ARE (ANGS)") >= 0) {
      if (doGetModel(++modelNumber)) {
        atomNames = new Vector();
        if (isBohr)
          readAtomsInBohrCoordinates();
        else
          readAtomsInAngstromCoordinates();
        iHaveAtoms = true;
        return true;
      }
      if (isLastModel(modelNumber) && iHaveAtoms) {
        continuing = false;
        return false;
      }
      iHaveAtoms = false;
    }
    if (!iHaveAtoms)
      return true;
    if (line.indexOf("FREQUENCIES IN CM") >= 0) {
      readFrequencies();
      return true;
    }
    if (line.indexOf("SUMMARY OF THE EFFECTIVE FRAGMENT") >= 0) {
      
      
      
      readEFPInBohrCoordinates();
      return false;
    }
    if (line.indexOf("  TOTAL MULLIKEN AND LOWDIN ATOMIC POPULATIONS") >= 0) {
      readPartialCharges();
      return false;
    }
    if (line.indexOf("ELECTROSTATIC MOMENTS")>=0){
      readDipoleMoment();
      return true;
    }
    if (line.indexOf("- ALPHA SET -") >= 0)
      alphaBeta = "alpha";
    else if (line.indexOf("- BETA SET -") >= 0)
      alphaBeta = "beta";
    else if  (line.indexOf("  EIGENVECTORS") >= 0
        || line.indexOf("  INITIAL GUESS ORBITALS") >= 0
        || line.indexOf("  MCSCF OPTIMIZED ORBITALS") >= 0
        || line.indexOf("  MCSCF NATURAL ORBITALS") >= 0
        || line.indexOf("  MOLECULAR ORBITALS") >= 0
        && line
            .indexOf("  MOLECULAR ORBITALS LOCALIZED BY THE POPULATION METHOD") < 0) {
      if (!filterMO())
        return true;
      
      readMolecularOrbitals(HEADER_GAMESS_ORIGINAL);
      return false;
    }
    if (line.indexOf("EDMISTON-RUEDENBERG ENERGY LOCALIZED ORBITALS") >= 0
        || line.indexOf("  THE PIPEK-MEZEY POPULATION LOCALIZED ORBITALS ARE") >= 0) {
      if (!filterMO())
        return true;
      readMolecularOrbitals(HEADER_NONE);
      return false;
    }
    if (line.indexOf("  NATURAL ORBITALS IN ATOMIC ORBITAL BASIS") >= 0) {
      
      

      
      if (!filterMO())
        return true;
      readMolecularOrbitals(HEADER_GAMESS_OCCUPANCIES);
      return false;
    }
    return checkNboLine();
  }
  
  protected void readMolecularOrbitals(int headerType) throws Exception {
    setCalculationType();
    super.readMolecularOrbitals(headerType);
  }
  
  

  protected void readEFPInBohrCoordinates() throws Exception {
    
    

    int atomCountInFirstModel = atomSetCollection.getAtomCount();
    
    discardLinesUntilContains("MULTIPOLE COORDINATES");

    readLine(); 
    readLine(); 
    
    
    while (readLine() != null && line.length() >= 72) {
      String atomName = line.substring(1, 2);
      
      
      
      if (atomName.charAt(0) == 'Z')
        atomName = line.substring(2, 3);
      else if (parseFloat(line, 67, 73) == 0)
        continue;
      float x = parseFloat(line, 8, 25);
      float y = parseFloat(line, 25, 40);
      float z = parseFloat(line, 40, 56);
      if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
        break;
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = atomName + (++atomCountInFirstModel);
      atom.set(x, y, z);
      atom.scale(ANGSTROMS_PER_BOHR);
      atomNames.addElement(atomName);
    }
  }
  
  protected void readAtomsInBohrCoordinates() throws Exception {
    

    readLine(); 
    String atomName;
    atomSetCollection.newAtomSet();
    int n = 0;
    while (readLine() != null
        && (atomName = parseToken(line, 1, 6)) != null) {
      float x = parseFloat(line, 17, 37);
      float y = parseFloat(line, 37, 57);
      float z = parseFloat(line, 57, 77);
      if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
        break;
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = atomName + (++n);
      atom.set(x, y, z);
      atom.scale(ANGSTROMS_PER_BOHR);
      atomNames.addElement(atomName);
    }
  }

  private void readAtomsInAngstromCoordinates() throws Exception {
    readLine(); 
    readLine(); 
    String atomName;
    atomSetCollection.newAtomSet();

    int n = 0;
    while (readLine() != null
        && (atomName = parseToken(line, 1, 6)) != null) {
      float x = parseFloat(line, 16, 31);
      float y = parseFloat(line, 31, 46);
      float z = parseFloat(line, 46, 61);
      if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
        break;
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = atomName + (++n);
      atom.set(x, y, z);
      atomNames.addElement(atomName);
    }
    
    
    
    
    if (line.indexOf("COORDINATES OF FRAGMENT MULTIPOLE CENTERS (ANGS)") >= 0) {
         readLine(); 
        readLine(); 
        readLine(); 
        
        
        while (readLine() != null
        && (atomName = parseToken(line, 1, 2)) != null) {
              if (parseToken(line,1,2).equals("Z")) 
                    atomName = parseToken(line, 2, 3);
              else if (parseToken(line,1,9).equals("FRAGNAME"))
                  continue;
              else
                    atomName = parseToken(line, 1, 2); 
              float x = parseFloat(line, 16, 31);
              float y = parseFloat(line, 31, 46);
              float z = parseFloat(line, 46, 61);
              if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
                    break;
              Atom atom = atomSetCollection.addNewAtom();
              atom.atomName = atomName + (++n);
              atom.set(x, y, z);
              atomNames.addElement(atomName);
        } 
          
    }
  }
  
  
  protected String fixShellTag(String tag) {
    return tag;
  }


  

  
  void readPartialCharges() throws Exception {
    String tokens[]=null;
    String searchstr = (havePartialChargeFilter
        && filter.toUpperCase().indexOf("CHARGE=LOW") >= 0 ? "LOW.POP."
            : "MULL.POP.");
    while (readLine() != null && ("".equals(line.trim())||line.contains("ATOM"))) {
      tokens = getTokens();      
    }
    int poploc = 0;
    for (; ++poploc < tokens.length; )
      if (searchstr.equals(tokens[poploc]))
        break;
    if (++poploc >= tokens.length || !"CHARGE".equals(tokens[poploc++]))
      return; 
    Atom[] atoms = atomSetCollection.getAtoms();
    int startAtom = atomSetCollection.getLastAtomSetAtomIndex();
    int endAtom = atomSetCollection.getAtomCount();
    for (int i = startAtom; i < endAtom && readLine() != null; ++i)
      atoms[i].partialCharge = parseFloat(getTokens(prevline)[poploc]);
  }
 
  void readDipoleMoment() throws Exception {
    String tokens[] = null;
    readLine();
    while (line != null && ("".equals(line.trim()) || !line.contains("DX"))) {
      readLine();
    }
    tokens = getTokens(line);
    if (tokens.length != 5)
      return;
    if ("DX".equals(tokens[0]) && "DY".equals(tokens[1])
        && "DZ".equals(tokens[2])) {
      tokens = getTokens(readLine());
      Vector3f dipole = new Vector3f(parseFloat(tokens[0]),
          parseFloat(tokens[1]), parseFloat(tokens[2]));
      Logger.info("Molecular dipole for model "
          + atomSetCollection.getAtomSetCount() + " = " + dipole);
      atomSetCollection.setAtomSetAuxiliaryInfo("dipole", dipole);
    }
  }
}
