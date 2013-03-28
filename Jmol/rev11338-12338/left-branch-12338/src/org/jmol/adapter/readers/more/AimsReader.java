



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
    String symbol;
    int order;        
    float charge;     
    int nLatticeVectors = 0;
    Vector3f[] latticeVectors = new Vector3f[3];
    try {
      while (readLine() != null) {
        tokens = getTokens();
        if (tokens.length == 0) continue;
        
        if (tokens[0].equals("atom")) {
          if (tokens.length < 5) {
            Logger.warn("cannot read line with FHI-aims atom data: " + line);
          } else {
            x = parseFloat(tokens[1]);
            y = parseFloat(tokens[2]);
            z = parseFloat(tokens[3]);
            symbol = tokens[4];
            Atom atom = atomSetCollection.addNewAtom();
            atom.set(x, y, z);
            atom.elementSymbol = symbol;
          }
        }

        
        if (tokens[0].equals("multipole")) {
          if (tokens.length < 6) {
            Logger.warn("cannot read line with FHI-aims atom data: " + line);
          } else {
            x = parseFloat(tokens[1]);
            y = parseFloat(tokens[2]);
            z = parseFloat(tokens[3]);
            order = parseInt(tokens[4]);
            charge = parseFloat(tokens[5]);
            if (order > 0) {
              Logger
                  .warn("multipole line ignored since only monopoles are currently supported: "
                      + line);
              continue;
            }
            Atom atom = atomSetCollection.addNewAtom();
            atom.set(x, y, z);
            atom.partialCharge = charge;
            atom.formalCharge = Math.round(charge);
          }
        }

        
        if (tokens[0].equals("lattice_vector")) {
          if (tokens.length < 4) {
            Logger.warn("cannot read line with FHI-aims lattice vector: "
                + line);
          } else if (nLatticeVectors > 2) {
            Logger
                .warn("more than 3 FHI-aims lattice vectors found with line: "
                    + line);
          } else {
            x = parseFloat(tokens[1]);
            y = parseFloat(tokens[2]);
            z = parseFloat(tokens[3]);
            latticeVectors[nLatticeVectors] = new Vector3f(x, y, z);
          }
          nLatticeVectors++;
        }
      }

      

      if (nLatticeVectors == 3) {

        

        doApplySymmetry = true;
        setFractionalCoordinates(false);
        setUnitCellFromLatticeVectors(latticeVectors);

        

        
        int nAtoms = atomSetCollection.getAtomCount();
        for (int n = 0; n < nAtoms; n++) {
          Atom atom = atomSetCollection.getAtom(n);
          setAtomCoord(atom);
        }
        
        applySymmetryAndSetTrajectory();
      }
    } catch (Exception e) {
      setError(e);
    }
  }

  private void setUnitCellFromLatticeVectors(Vector3f[] abc) {
    float a = abc[0].length();
    float b = abc[1].length();
    float c = abc[2].length();
    float alpha = (float) Math.toDegrees(abc[1].angle(abc[2]));
    float beta = (float) Math.toDegrees(abc[2].angle(abc[0]));
    float gamma = (float) Math.toDegrees(abc[0].angle(abc[1]));
    
    setUnitCell(a, b, c, alpha, beta, gamma);
    
    float[] lv = new float[3];
    for (int n = 0; n < 3; n++) {
      abc[n].get(lv);
      addPrimitiveLatticeVector(n, lv);
    }
  }
}
