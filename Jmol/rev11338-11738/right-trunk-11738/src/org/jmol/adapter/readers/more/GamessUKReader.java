

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;

import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Vector;

public class GamessUKReader extends GamessReader {

  public void readAtomSetCollection(BufferedReader reader) {
    readAtomSetCollection(reader, "gamessUK");
  }

  
  protected boolean checkLine() throws Exception {
    if (line.contains("BASIS OPTIONS")){
      readBasisInfo();
      return true;
    }    
    if (line.contains("$CONTRL OPTIONS")){
      readControlInfo();
      return true;
    }
    if (line.indexOf("contracted primitive functions") >= 0) {
      readGaussianBasis(
          "======================================================", "======");
      return false;
    }
    if (line.indexOf("molecular geometry") >= 0) {
      if (doGetModel(++modelNumber)) {
        atomNames = new Vector();
        readAtomsInBohrCoordinates();
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
    if (line.indexOf("FREQUENCY_INFO_WOULD_BE_HERE") >= 0) {
      
      return true;
    } 
    if (line.indexOf("SYMMETRY ASSIGNMENT") >= 0) {
      readOrbitalSymmetryAndOccupancy();
      return false;
    } 
    if (line.indexOf("- ALPHA SET -") >= 0)
      alphaBeta = "alpha";
    else if (line.indexOf("- BETA SET -") >= 0)
      alphaBeta = "beta";
    else if (line.indexOf("eigenvectors") >= 0) {
      readMolecularOrbitals(HEADER_GAMESS_UK_MO);
      setOrbitalSymmetryAndOccupancy();
      return false;
    } 
    return checkNboLine();
  }

  protected void readAtomsInBohrCoordinates() throws Exception {
    

    discardLinesUntilContains("*****");
    discardLinesUntilContains("atom");
    discardLinesUntilContains("*****");
    atomSetCollection.newAtomSet();
    while (readLine() != null
        && line.indexOf("*****") < 0) {
      if (line.charAt(14) == ' ')
        continue;
      String[] tokens = getTokens();
      String atomName = tokens[1];
      int atomicNumber = (int) parseFloat(tokens[2]);
      float x = parseFloat(tokens[3]);
      float y = parseFloat(tokens[4]);
      float z = parseFloat(tokens[5]);
      if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
        break;
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = atomName;
      atom.set(x, y, z);
      atom.scale(ANGSTROMS_PER_BOHR);
      atom.elementSymbol = AtomSetCollectionReader.getElementSymbol(atomicNumber);
      atomNames.addElement(atomName);
    }
  }

  

  protected String fixShellTag(String tag) {
    
    return tag.substring(1).toUpperCase();
  }


  
  private Vector symmetries;
  private Vector occupancies;
   
   private void readOrbitalSymmetryAndOccupancy() throws Exception {
     discardLines(4);
     symmetries = new Vector();
     occupancies = new Vector();
     while (readLine() != null && line.indexOf("====") < 0) {
       String[] tokens = getTokens(line.substring(20));
       symmetries.addElement(tokens[0] + " " + tokens[1]);
       occupancies.addElement(new Float(parseFloat(tokens[5])));
     }
   }

   private void setOrbitalSymmetryAndOccupancy() {
     
     if (symmetries.size() < orbitals.size())
       return;
     for (int i = orbitals.size(); --i >= 0; ) {
       Hashtable mo = (Hashtable)orbitals.elementAt(i);
       mo.put("symmetry", symmetries.elementAt(i));
       mo.put("occupancy", occupancies.elementAt(i));
     }
   }

  


}
