

package org.jmol.adapter.readers.more;

import org.jmol.adapter.readers.cifpdb.PdbReader;



public class PqrReader extends PdbReader {
  protected String fileType = "Pqr"; 
  
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

