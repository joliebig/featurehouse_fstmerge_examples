
package org.jmol.adapter.readers.quantum;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;
import java.util.Hashtable;



public class MopacGraphfReader extends MopacReader {
    
  int atomCount;
  
 public void readAtomSetCollection(BufferedReader reader) {

    this.reader = reader;
    atomSetCollection = new AtomSetCollection("mopacGraphf", this);
    
    try {
      readAtoms();
      readSlaterBasis();
      readMOs(false);
      if (readKeywords())
        readMOs(true);
    } catch (Exception e) {
      setError(e);
    }

  }
    
  void readAtoms() throws Exception {
    atomSetCollection.newAtomSet();
    atomCount = parseInt(readLine());
    atomicNumbers = new int[atomCount];
    for (int i = 0; i < atomCount; i++) {
      readLine();
      atomicNumbers[i] = parseInt(line.substring(0, 4));
      Atom atom = atomSetCollection.addNewAtom();
      atom.x = parseFloat(line.substring(4, 17));
      atom.y = parseFloat(line.substring(17, 29));
      atom.z = parseFloat(line.substring(29, 41));
      if (line.length() > 41)
        atom.partialCharge = parseFloat(line.substring(41));
      atom.elementSymbol = AtomSetCollectionReader.getElementSymbol(atomicNumbers[i]);
      
    }
  }
  
  

  void readSlaterBasis() throws Exception {
    
    nOrbitals = 0;
    float[] values = new float[3];
    for (int iAtom = 0; iAtom < atomCount; iAtom++) {
      getTokensFloat(readLine(), values, 3);
      int atomicNumber = atomicNumbers[iAtom];
      float zeta;
      if ((zeta = values[0]) != 0) {
        createSphericalSlaterByType(iAtom, atomicNumber, "S", zeta, 1);
      }
      if ((zeta = values[1]) != 0) {
        createSphericalSlaterByType(iAtom, atomicNumber, "Px", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Py", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Pz", zeta, 1);
      }
      if ((zeta = values[2]) != 0) {
        createSphericalSlaterByType(iAtom, atomicNumber, "Dx2-y2", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Dxz", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Dz2", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Dyz", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Dxy", zeta, 1);
      }
    }
    nOrbitals = slaters.size();
    setSlaters(true, false);
  }

  float[][] invMatrix;
  
  void readMOs(boolean isBeta) throws Exception {

    

    

    float[][] list = new float[nOrbitals][nOrbitals];
    for (int iMo = 0; iMo < nOrbitals; iMo++) {
      int n = -1;
      for (int i = 0; i < nOrbitals; i++) {
        if ((n = (n + 1) % 5) == 0)
          readLine();
        list[iMo][i] = parseFloat(line.substring(n * 15, (n + 1) * 15));
      }
    }
    if (!isBeta) {
      
      invMatrix = new float[nOrbitals][nOrbitals];
      for (int iMo = 0; iMo < nOrbitals; iMo++) {
        int n = -1;
        for (int i = 0; i < iMo + 1; i++) {
          if ((n = (n + 1) % 5) == 0)
            readLine();
          invMatrix[iMo][i] = invMatrix[i][iMo] = parseFloat(line.substring(
              n * 15, (n + 1) * 15));
        }
      }
    }
    float[][] list2 = new float[nOrbitals][nOrbitals];
    for (int i = 0; i < nOrbitals; i++)
      for (int j = 0; j < nOrbitals; j++) {
        for (int k = 0; k < nOrbitals; k++)
          list2[i][j] += (list[i][k] * invMatrix[k][j]);
        if (Math.abs(list2[i][j]) < MIN_COEF)
          list2[i][j] = 0;
      }
    

    
    float[] values = new float[2];
    for (int iMo = 0; iMo < nOrbitals; iMo++) {
      Hashtable mo = new Hashtable();
      if (readLine() != null) {
        getTokensFloat(line, values, 2);
        mo.put("energy", new Float(values[0]));
        mo.put("occupancy", new Float(values[1]));
      }
      mo.put("coefficients", list2[iMo]);
      if (isBeta)
        mo.put("type", "beta");
      orbitals.addElement(mo);
    }
    setMOs("eV");
  }
  
  private boolean readKeywords() throws Exception {
    if (readLine() == null || line.indexOf(" Keywords:") < 0)
      return false;
    moData.put("calculationType", calculationType = line.substring(11).trim());
    boolean isUHF = (line.indexOf("UHF") >= 0);
    if (isUHF)
      for (int i = orbitals.size(); --i >= 0;)
        ((Hashtable)orbitals.get(i)).put("type", "alpha");
    return isUHF;
  }
}
