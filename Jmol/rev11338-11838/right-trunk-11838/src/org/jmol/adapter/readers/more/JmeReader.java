

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import org.jmol.api.JmolAdapter;

import java.io.BufferedReader;
import java.util.StringTokenizer;

public class JmeReader extends AtomSetCollectionReader {


  StringTokenizer tokenizer;
  
 public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("jme", this);

    try {
      readLine();
      tokenizer = new StringTokenizer(line, "\t ");
      int atomCount = parseInt(tokenizer.nextToken());
      int bondCount = parseInt(tokenizer.nextToken());
      atomSetCollection.setCollectionName("JME");
      readAtoms(atomCount);
      readBonds(bondCount);
    } catch (Exception e) {
      setError(e);
    }

  }
    
  void readAtoms(int atomCount) throws Exception {
    for (int i = 0; i < atomCount; ++i) {
      String strAtom = tokenizer.nextToken();
      
      int indexColon = strAtom.indexOf(':');
      String elementSymbol = (indexColon > 0
                              ? strAtom.substring(0, indexColon)
                              : strAtom).intern();
      float x = parseFloat(tokenizer.nextToken());
      float y = parseFloat(tokenizer.nextToken());
      float z = 0;
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = elementSymbol;
      atom.set(x, y, z);
    }
  }

  void readBonds(int bondCount) throws Exception {
    for (int i = 0; i < bondCount; ++i) {
      int atomIndex1 = parseInt(tokenizer.nextToken());
      int atomIndex2 = parseInt(tokenizer.nextToken());
      int order = parseInt(tokenizer.nextToken());
      
      if (order < 1) {
        
        order = ((order == -1)
                 ? JmolAdapter.ORDER_STEREO_NEAR
                 : JmolAdapter.ORDER_STEREO_FAR);
      }
      atomSetCollection.addBond(new Bond(atomIndex1-1, atomIndex2-1, order));
    }
  }
}
