

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
  
  protected float readPartialCharge() {
    return parseFloat(line, 55, 62);
  }
  
  protected float readRadius() {
    return parseFloat(line, 63, 69);
  }
  

}

