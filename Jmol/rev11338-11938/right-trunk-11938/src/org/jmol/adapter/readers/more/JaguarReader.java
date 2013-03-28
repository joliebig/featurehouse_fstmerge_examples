

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Logger;

import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Vector;


public class JaguarReader extends MOReader {

  int moCount = 0;
  float lumoEnergy = Float.MAX_VALUE;

  public void readAtomSetCollection(BufferedReader reader)  {
    readAtomSetCollection(reader, "jaguar");
  }

  
  protected boolean checkLine() throws Exception {
    if (line.startsWith(" Input geometry:")
        || line.startsWith(" Symmetrized geometry:")
        || line.startsWith("  final geometry:")) {
      readAtoms();
      return true;
    }
    if (line.startsWith("  Atomic charges from electrostatic potential:")) {
      readCharges();
      return true;
    }
    if (line.startsWith("  number of basis functions....")) {
      moCount = parseInt(line.substring(32).trim());
      return true;
    }
    if (line.startsWith("  basis set:")) {
      moData.put("energyUnits", "");
      moData.put("calculationType", calculationType = line.substring(13).trim());
      return true;
    }
    if (line.indexOf("Shell information") >= 0) {
      readBasis();
      return true;
    }
    if (line.indexOf("Normalized coefficients") >= 0) {
      readBasisNormalized();
      return true;
    }
    if (line.startsWith(" LUMO energy:")) {
      lumoEnergy = parseFloat(line.substring(13));
      return true;
    }
    if (line.indexOf("final wvfn") >= 0) {
      readJaguarMolecularOrbitals();
      return true;
    }
    if (line.startsWith("  harmonic frequencies in")) {
      readFrequencies();
      continuing = false;
      return false;
    }
    return checkNboLine();
  }

  private void readAtoms() throws Exception {
    
    atomSetCollection.discardPreviousAtoms();
    
    discardLines(2);
    int atomCount = 0;
    while (readLine() != null && line.length() >= 60 && line.charAt(2) != ' ') {
      String[] tokens = getTokens();
      String atomName = tokens[0];
      float x = parseFloat(tokens[1]);
      float y = parseFloat(tokens[2]);
      float z = parseFloat(tokens[3]);
      if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z)
          || atomName.length() < 2)
        return;
      String elementSymbol;
      char ch2 = atomName.charAt(1);
      if (ch2 >= 'a' && ch2 <= 'z')
        elementSymbol = atomName.substring(0, 2);
      else
        elementSymbol = atomName.substring(0, 1);
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = elementSymbol;
      atom.atomName = atomName;
      atom.set(x, y, z);
      atomCount++;
    }
  }

  
  private void readCharges() throws Exception {
   int iAtom = 0;
    while (readLine() != null && line.indexOf("sum") < 0) {
      if (line.indexOf("Charge") < 0)
        continue;
      String[] tokens = getTokens();
      for (int i = 1; i < tokens.length; i++)
        atomSetCollection.getAtom(iAtom++).partialCharge = parseFloat(tokens[i]);
    }
  }

  
  
  void readBasis() throws Exception {
    String lastAtom = "";
    int iAtom = -1;
    int[][] sdata = new int[moCount][4];
    Vector[] sgdata = new Vector[moCount];
    String[] tokens;
    gaussianCount = 0;

    

    discardLinesUntilContains("--------");
    while (readLine() != null && (tokens = getTokens()).length == 9) {
      int jCont = parseInt(tokens[2]);
      if (jCont > 0) {
        if (!tokens[0].equals(lastAtom))
          iAtom++;
        lastAtom = tokens[0];
        int iFunc = parseInt(tokens[5]);
        int iType = parseInt(tokens[4]);
        if (iType <= 2)
          iType--; 
        if (sgdata[iFunc] == null) {
          sdata[iFunc][0] = iAtom;
          sdata[iFunc][1] = iType;
          sdata[iFunc][2] = 0; 
          sdata[iFunc][3] = 0; 
          sgdata[iFunc] = new Vector();
        }
        float factor = 1;
        
        sgdata[iFunc].addElement(new float[] { parseFloat(tokens[6]),
            parseFloat(tokens[8]) * factor });
        gaussianCount += jCont;
        for (int i = jCont - 1; --i >= 0;) {
          tokens = getTokens(readLine());
          sgdata[iFunc].addElement(new float[] { parseFloat(tokens[6]),
              parseFloat(tokens[8]) * factor });
        }
      }
    }
    float[][] garray = new float[gaussianCount][];
    Vector sarray = new Vector();
    gaussianCount = 0;
    for (int i = 0; i < moCount; i++)
      if (sgdata[i] != null) {
        int n = sgdata[i].size();
        sdata[i][2] = gaussianCount;
        sdata[i][3] = n;
        for (int j = 0; j < n; j++)
          garray[gaussianCount++] = (float[]) sgdata[i].get(j);
        sarray.addElement(sdata[i]);
      }
    moData.put("shells", sarray);
    moData.put("gaussians", garray);
    if (Logger.debugging) {
      Logger.debug(sarray.size() + " slater shells read");
      Logger.debug(gaussianCount + " gaussian primitives read");
    }
  }
  
  

  void readBasisNormalized() throws Exception {
    
    

  }

  

  private void readJaguarMolecularOrbitals() throws Exception {
    String[][] dataBlock = new String[moCount][];
    readLine();
    readLine();
    readLine();
    int nMo = 0;
    while (line != null) {
      readLine();
      readLine();
      readLine();
      if (line == null || line.indexOf("eigenvalues-") < 0)
        break;
      String[] eigenValues = getTokens();
      int n = eigenValues.length - 1;
      fillDataBlock(dataBlock);
      for (int iOrb = 0; iOrb < n; iOrb++) {
        float[] coefs = new float[moCount];
        Hashtable mo = new Hashtable();
        float energy = parseFloat(eigenValues[iOrb + 1]);
        mo.put("energy", new Float(energy));
        if (Math.abs(energy - lumoEnergy) < 0.0001) {
          moData.put("HOMO", new Integer(nMo));
          lumoEnergy = Float.MAX_VALUE;
        }
        nMo++;
        for (int i = 0; i < moCount; i++)
          coefs[i] = parseFloat((String) dataBlock[i][iOrb + 3]);
        mo.put("coefficients", coefs);
        orbitals.addElement(mo);
      }
    }
    moData.put("mos", orbitals);
    setMOData(moData);
  }

  

  private void readFrequencies() throws Exception {
    int atomCount = atomSetCollection.getLastAtomSetAtomCount();
    int iAtom0 = atomSetCollection.getAtomCount() - atomCount;
    discardLinesUntilStartsWith("  frequencies ");
    while (line != null && line.startsWith("  frequencies ")) {
      String[] frequencies = getTokens();
      int frequencyCount = frequencies.length - 1;
      boolean[] ignore = new boolean[frequencyCount];
      
      String[] symmetries = null;
      String[] intensities = null;
      while (line != null 
          && !line.startsWith("  intensities ") 
          && !line.startsWith("  force ")) {
        readLine();
        if (line.indexOf("symmetries") >= 0)
          symmetries = getTokens();
      }
      if (line.startsWith("  intensities"))
        intensities = getTokens();
      for (int i = 0; i < frequencyCount; i++) {
        ignore[i] = !doGetVibration(++vibrationNumber);
        if (ignore[i]) 
          continue;
        atomSetCollection.cloneFirstAtomSet();
        atomSetCollection.setAtomSetName(frequencies[i + 1] + " cm-1"
            + (symmetries == null ? "" : " (" + symmetries[i + 1] + ")"));
        atomSetCollection.setAtomSetProperty("Frequency", frequencies[i + 1]
            + " cm-1");
        if (intensities != null)
          atomSetCollection.setAtomSetProperty("IR Intensity",
              intensities[i + 1] + " km/mol");
      }
      fillFrequencyData(iAtom0, atomCount, ignore, false, 0, 0);
      readLine();
      readLine();
    }
  }
}
