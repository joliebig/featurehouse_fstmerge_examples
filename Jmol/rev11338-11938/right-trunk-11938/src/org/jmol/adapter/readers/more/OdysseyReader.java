

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;

import java.io.BufferedReader;



public class OdysseyReader extends SpartanInputReader {
  
  public void readAtomSetCollection(BufferedReader reader) {
    modelName = "Odyssey file";
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("odyssey)", this);
    readInputRecords();
  }
  
}
