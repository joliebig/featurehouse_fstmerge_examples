



package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Logger;

import javax.vecmath.Vector3f;

import java.io.BufferedReader;



public class AimsReader extends AtomSetCollectionReader {

  public void readAtomSetCollection(BufferedReader br) {
    reader = br;
    atomSetCollection = new AtomSetCollection("aims");
    

    String tokens[];
    float x,y,z;
    int nLatticeVectors = 0;
    Vector3f[] latticeVectors = new Vector3f[3];
    try {
      while (readLine() != null) {

        tokens = getTokens();

        if (tokens.length == 0) continue;

        
        if (tokens[0].equals("atom")) {
           if (tokens.length != 5) {
              Logger.warn("cannot read line with AIMS atom data: " + line);
           } else {
             Atom atom = atomSetCollection.addNewAtom();
             x = parseFloat(tokens[1]);
             y = parseFloat(tokens[2]);
             z = parseFloat(tokens[3]);
             atom.set(x,y,z);
             atom.elementSymbol = tokens[4];
          }
        }

        
        if (tokens[0].equals("lattice_vector")) { 
           if (nLatticeVectors > 2) {
              Logger.warn("more than 3 AIMS lattice vectors found with line: " + line);
           } else {
             x = parseFloat(tokens[1]);
             y = parseFloat(tokens[2]);
             z = parseFloat(tokens[3]);
             latticeVectors[nLatticeVectors] = new Vector3f(x,y,z);
           }  
           nLatticeVectors++;
        }
      }

      
      if (nLatticeVectors == 3) {

         

        doApplySymmetry = true;

         
         
         setFractionalCoordinates(false);

         
         setUnitCellFromLatticeVectors(latticeVectors);

         

         
         int nAtoms = atomSetCollection.getAtomCount();
         for (int n=0; n<nAtoms; n++) {
             Atom atom = atomSetCollection.getAtom(n);
             setAtomCoord(atom);
         }
         
         applySymmetryAndSetTrajectory();
      }

    } catch (Exception e) {
      setError(e);
    }
  }

  void setUnitCellFromLatticeVectors(Vector3f[] lv) {
    float a = lv[0].length();
    float b = lv[1].length();
    float c = lv[2].length();
    float alpha =  (float) Math.toDegrees(lv[1].angle(lv[2]));
    float beta = (float) Math.toDegrees(lv[2].angle(lv[0]));
    float gamma = (float) Math.toDegrees(lv[0].angle(lv[1]));
    setUnitCell(a,b,c,alpha,beta,gamma);
  } 

}
