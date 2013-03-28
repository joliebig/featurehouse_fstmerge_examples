

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;

import org.jmol.util.Logger;



abstract class SpartanInputReader extends AtomSetCollectionReader {

  protected String modelName;
  protected int atomCount;
  protected int bondCount;
  protected String bondData = "";
  

  protected void readInputRecords() {
    int atomCount0 = atomCount;
    try {
      readInputHeader();
      while (readLine() != null) {
        String[] tokens = getTokens();
        
        if (tokens.length == 2 && parseInt(tokens[0]) != Integer.MIN_VALUE && parseInt(tokens[1]) >= 0)
          break;
      }
      if (line == null)
        return;
      readInputAtoms();
      discardLinesUntilContains("ATOMLABELS");
      if (line != null)
        readAtomNames();
      discardLinesUntilContains("HESSIAN");
      if (line != null)
        readBonds(atomCount0);
      while (line != null && line.indexOf("END ") < 0 && line.indexOf("MOLSTATE") < 0)
        readLine();
      if (line != null && line.indexOf("MOLSTATE") >= 0)
        readTransform();
      if (atomSetCollection.getAtomCount() > 0)
        atomSetCollection.setAtomSetName(modelName);
    } catch (Exception e) {
      setError(e);
    }
  }
  
  private void readTransform() throws Exception {
    readLine();
    String[] tokens = getTokens(readLine() + " " + readLine());
    
    
    
    
    
    setTransform(
        parseFloat(tokens[0]), parseFloat(tokens[1]), parseFloat(tokens[2]),
        parseFloat(tokens[4]), parseFloat(tokens[5]), parseFloat(tokens[6]),
        parseFloat(tokens[8]), parseFloat(tokens[9]), parseFloat(tokens[10])
    );
  }
  
  private void readInputHeader() throws Exception {
    while (readLine() != null
        && !line.startsWith(" ")) {}
    readLine();
    modelName = line + ";";
    modelName = modelName.substring(0, modelName.indexOf(";")).trim();
  }
  
  int modelAtomCount;
  int atomCount0;
  
  private void readInputAtoms() throws Exception {
    modelAtomCount = 0;
    atomCount0 = atomCount;
    while (readLine() != null
        && !line.startsWith("ENDCART")) {
      String[] tokens = getTokens();
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = getElementSymbol(parseInt(tokens[0]));
      atom.set(parseFloat(tokens[1]), parseFloat(tokens[2]), parseFloat(tokens[3]));
      modelAtomCount++;
    }
    atomCount = atomSetCollection.getAtomCount();
    if (Logger.debugging)
      Logger.debug(atomCount + " atoms read");
  }

  private void readAtomNames() throws Exception {
    int atom0 = atomCount - modelAtomCount;
    
    
    for (int i = 0; i < modelAtomCount; i++) {
      line = readLine().trim();
      String name = line.substring(1, line.length() - 1);
      atomSetCollection.getAtom(atom0 + i).atomName = name;
    }
  }
  
  private void readBonds(int atomCount0) throws Exception {
    int nAtoms = modelAtomCount;
    
    bondData = ""; 
    while (readLine() != null && !line.startsWith("ENDHESS")) {
      String[] tokens = getTokens();
      bondData += line + " ";
      if (nAtoms == 0) {
        int sourceIndex = parseInt(tokens[0]) - 1 + atomCount0;
        int targetIndex = parseInt(tokens[1]) - 1 + atomCount0;
        int bondOrder = parseInt(tokens[2]);
        if (bondOrder > 0) {
          atomSetCollection.addBond(new Bond(sourceIndex, targetIndex,
              bondOrder < 4 ? bondOrder : bondOrder == 5 ? JmolAdapter.ORDER_AROMATIC : 1));
        }
      } else {
        nAtoms -= tokens.length;
      }
    }
    bondCount = atomSetCollection.getBondCount();
    if (Logger.debugging)
      Logger.debug(bondCount + " bonds read");
  }
}
