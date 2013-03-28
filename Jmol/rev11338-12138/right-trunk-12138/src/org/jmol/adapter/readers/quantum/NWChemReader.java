

package org.jmol.adapter.readers.quantum;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;



public class NWChemReader extends MOReader {

  
  private int taskNumber = 1;
  
  
  private int equivalentAtomSets = 0;
  
  
  private String energyKey = "";
  
  private String energyValue = "";
  
  
  private boolean converged;
  private boolean haveEnergy;
  private boolean haveAt;
  private boolean inInput;
 
 public void readAtomSetCollection(BufferedReader reader)  {
   readAtomSetCollection(reader, "nwchem");
 }

 
  protected boolean checkLine() throws Exception {
    if (line.startsWith("          Step")) {
      init();
      return true;
    }
    if (line.startsWith("      Symmetry information")) {
      readSymmetry();
      return true;
    }
    if (line.indexOf("Total") >= 0) {
      readTotal();
      return true;
    }
    if (line.indexOf("@") >= 0) {
      readAtSign();
      return true;
    }
    if (line.startsWith("      Optimization converged")) {
      converged = true;
      return true;
    }
    if (line.indexOf("Output coordinates in angstroms") >= 0) {
      equivalentAtomSets++;
      readAtoms();
      return true;
    }
    if (line.indexOf("ENERGY GRADIENTS") >= 0) {
      equivalentAtomSets++;
      readGradients();
      return true;
    }
    if (line.indexOf("NWChem Nuclear Hessian and Frequency Analysis") >= 0) {
      readFrequencies();
      return true;
    }
    if (line.startsWith(" Task  times")) {
      init();
      taskNumber++; 
      return true;
    }
    if (line.trim().startsWith("NWChem")) {
      readNWChemLine();
      return true;
    }
    if (line.startsWith("  Mulliken analysis of the total density")) {
      
      if (equivalentAtomSets > 0)
        readPartialCharges();
      return true;
    }
    return true;
  }
  
  private void init() {
    haveEnergy = false;
    haveAt = false;
    converged = false;
    inInput = false;
    equivalentAtomSets = 0;
  }
  
  private void setEnergies(String key, String value, int nAtomSets) {
    energyKey = key;
    energyValue = value;
    atomSetCollection.setAtomSetProperties(energyKey, energyValue,
        equivalentAtomSets);
    atomSetCollection.setAtomSetNames(energyKey + " = " + energyValue,
        equivalentAtomSets);
    haveEnergy = true;
  }

  private void setEnergy(String key, String value) {
    energyKey = key;
    energyValue = value;
    atomSetCollection.setAtomSetProperty(energyKey, energyValue);
    atomSetCollection.setAtomSetName(energyKey+" = "+energyValue);
    haveEnergy = true;
  }

  
  private void readSymmetry() throws Exception {
    discardLines(2);
    if (readLine() == null)
      return;
    String tokens[] = getTokens();
    atomSetCollection.setAtomSetProperties("Symmetry group name",
        tokens[tokens.length-1], equivalentAtomSets);
  }
  
  private void readNWChemLine() {
    
    inInput = (line.indexOf("NWChem Input Module") >= 0);
  }
  
  
  private void readTotal() {
    String tokens[] = getTokens();
    try {
      if (tokens[2].startsWith("energy")) {
        
        
        if (!haveAt)
          setEnergies("E("+tokens[1]+")", tokens[tokens.length-1], equivalentAtomSets);
      }
    } catch (Exception e) {
      
    }
  }
  
  private void readAtSign() throws Exception {
    if (line.charAt(2)=='S') {
      discardLines(1); 
      if (readLine() == null)
        return;
    }
    String tokens[] = getTokens();
    if (!haveEnergy) { 
      setEnergies("E", tokens[2], equivalentAtomSets);
    } else {
      
      
      setEnergies(energyKey, energyValue, equivalentAtomSets);
    }
    atomSetCollection.setAtomSetProperties("Step", tokens[1], equivalentAtomSets);
    haveAt = true;
  }
   



  
  private void readAtoms() throws Exception {
    discardLines(3); 
    String tokens[];
    haveEnergy = false;
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
        "Task "+taskNumber+
        (inInput?SmarterJmolAdapter.PATH_SEPARATOR+"Input":
         SmarterJmolAdapter.PATH_SEPARATOR+"Geometry"));
    while (readLine() != null && line.length() > 0) {
      tokens = getTokens(); 
      if (tokens.length < 6) break; 
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = fixTag(tokens[1]);
      atom.set(parseFloat(tokens[3]), parseFloat(tokens[4]), parseFloat(tokens[5]));
    }
    
    if (converged) {
      setEnergy(energyKey, energyValue);
      atomSetCollection.setAtomSetProperty("Step", "converged");
    } else if (inInput) {
      atomSetCollection.setAtomSetName("Input");
    }
  }
  





  
  private void readGradients() throws Exception {
    discardLines(3); 
    String tokens[];
    atomSetCollection.newAtomSet();
    if (equivalentAtomSets > 1)
      atomSetCollection.cloneLastAtomSetProperties();
    atomSetCollection.setAtomSetProperty("vector","gradient");
    atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
        "Task "+taskNumber+
        SmarterJmolAdapter.PATH_SEPARATOR+"Gradients");
   while (readLine() != null && line.length() > 0) {
      tokens = getTokens(); 
      if (tokens.length < 8) break; 
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = fixTag(tokens[1]);
      atom.set(parseFloat(tokens[2]), parseFloat(tokens[3]), parseFloat(tokens[4]));
      atom.scale(ANGSTROMS_PER_BOHR);
      
      
      
      atomSetCollection.addVibrationVector(atom.atomIndex,
          -parseFloat(tokens[5]),
          -parseFloat(tokens[6]),
          -parseFloat(tokens[7])
      );
    }
 }

  
  
  
  
  
  
  

  
  private void readFrequencies() throws Exception {
    int firstFrequencyAtomSetIndex = atomSetCollection.getAtomSetCount();
    String path = "Task " + taskNumber + SmarterJmolAdapter.PATH_SEPARATOR
        + "Frequencies";

    
    discardLinesUntilContains("Atom information");
    discardLines(2);
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY, path);
    String tokens[];
    while (readLine() != null && line.indexOf("---") < 0) {
      tokens = getTokens();
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = fixTag(tokens[0]);
      atom.set(parseFloat(tokens[2]), parseFloat(tokens[3]), parseFloat(tokens[4]));
      atom.scale(ANGSTROMS_PER_BOHR);
    }

    discardLinesUntilContains("(Projected Frequencies expressed in cm-1)");
    discardLines(3); 

    boolean firstTime = true;
    while (readLine() != null && line.indexOf("P.Frequency") >= 0) {
      tokens = getTokens(line, 12);
      int frequencyCount = tokens.length;
      int iAtom0 = atomSetCollection.getAtomCount();
      int atomCount = atomSetCollection.getLastAtomSetAtomCount();
      if (firstTime)
        iAtom0 -= atomCount;
      boolean[] ignore = new boolean[frequencyCount];
      

      
      for (int i = 0; i < frequencyCount; ++i) {
        ignore[i] = !doGetVibration(++vibrationNumber);
        if (ignore[i])
          continue;
        if (!firstTime || i > 0) { 
          atomSetCollection.cloneLastAtomSet();
          atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY, path);
        }
        String frequencyString = tokens[i] + " cm^-1";
        atomSetCollection.setAtomSetName(frequencyString);
        atomSetCollection.setAtomSetProperty("Frequency", frequencyString);
      }
      firstTime = false;
      discardLines(1);
      fillFrequencyData(iAtom0, atomCount, ignore, false, 0, 0);
      discardLines(3);
    }

    
    
    
    try {
      discardLinesUntilContains("Projected Infra Red Intensities");
      discardLines(2);
      for (int i = vibrationNumber, idx = firstFrequencyAtomSetIndex; --i >= 0;) {
        if (readLine() == null)
          return;
        if (!doGetVibration(i + 1))
            continue;
        tokens = getTokens();
        String frequencyString = tokens[1] + " cm^-1";
        atomSetCollection.setAtomSetName(frequencyString, idx);
        atomSetCollection.setAtomSetProperty("Frequency", frequencyString, idx);
        atomSetCollection.setAtomSetProperty("IR Intensity", tokens[5]
            + " KM/mol", idx);
        idx++;
      }
    } catch (Exception e) {
      
    }
  }
  
  
  void readPartialCharges() throws Exception {
    String tokens[];
    discardLines(4);
    int atomCount = atomSetCollection.getAtomCount();
    int i0 = atomSetCollection.getLastAtomSetAtomIndex();
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = i0; i < atomCount; ++i) {
      
      while (atoms[i].elementNumber == 0)
        ++i;
      
      if (readLine() == null)
        return;
      tokens = getTokens();
      atoms[i].partialCharge = parseInt(tokens[2]) - parseFloat(tokens[3]);
    }
  }

  
  private String fixTag(String tag) {
    
    if (tag.equalsIgnoreCase("bq"))
      return "X";
    if (tag.toLowerCase().startsWith("bq"))
      tag = tag.substring(2) + "-Bq";
    return "" + Character.toUpperCase(tag.charAt(0))
        + (tag.length() == 1 ? "" : "" + Character.toLowerCase(tag.charAt(1)));
  }
}
