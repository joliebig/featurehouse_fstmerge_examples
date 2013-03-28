

package org.jmol.adapter.readers.cifpdb;

import java.io.BufferedReader;




public class PqrReader extends PdbReader {

  public void readAtomSetCollection(BufferedReader reader) {
    fileType = "Pqr";
    super.readAtomSetCollection(reader);
  }
  

  protected int readOccupancy() {
    return 100;
  }

  protected float readBFactor() {
    return Float.MAX_VALUE; 
  }
  
  String[] tokens;
  protected float readPartialCharge() {
    tokens = getTokens();
    return parseFloat(tokens[tokens.length - 2]);
  }
  
  protected float readRadius() {
    return parseFloat(tokens[tokens.length - 1]);
  }
  

}

