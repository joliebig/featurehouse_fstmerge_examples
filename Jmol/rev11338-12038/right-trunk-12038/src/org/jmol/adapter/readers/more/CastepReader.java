



package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Logger;

import javax.vecmath.Vector3f;

import java.io.BufferedReader;



public class CastepReader extends AtomSetCollectionReader {

  private String[] tokens;

  private float a = 0.0f;
  private float b = 0.0f;
  private float c = 0.0f;
  private float alpha = 0.0f;
  private float beta = 0.0f;
  private float gamma = 0.0f;
  private Vector3f[] abc = new Vector3f[3];

  public void readAtomSetCollection(BufferedReader br) {

    reader = br;
    atomSetCollection = new AtomSetCollection("castep", this);

    boolean iHaveFractionalCoordinates = false;

    try {

      while (tokenizeCastepCell() > 0) {


        if ((tokens.length >= 2) && (tokens[0].equalsIgnoreCase("%BLOCK"))) {

          

          
          if (tokens[1].equalsIgnoreCase("LATTICE_ABC")) {
            readLatticeAbc();
          }
          
          if (tokens[1].equalsIgnoreCase("LATTICE_CART")) {
            readLatticeCart();
          }

          
          
          if (tokens[1].equalsIgnoreCase("POSITIONS_FRAC")) {
            readPositionsFrac();
            iHaveFractionalCoordinates = true;
          }
          
          if (tokens[1].equalsIgnoreCase("POSITIONS_ABS")) {
            readPositionsAbs();
            iHaveFractionalCoordinates = false;
          }
        }
      }

      doApplySymmetry = true;
      setFractionalCoordinates(iHaveFractionalCoordinates);
      
      setUnitCell(a, b, c, alpha, beta, gamma);
      
      float[] lv = new float[3];
      for (int n = 0; n < 3; n++) {
        abc[n].get(lv);
        addPrimitiveLatticeVector(n, lv);
      }

      int nAtoms = atomSetCollection.getAtomCount();
      
      for (int n = 0; n < nAtoms; n++) {
        Atom atom = atomSetCollection.getAtom(n);
        setAtomCoord(atom);
      }
      applySymmetryAndSetTrajectory();

    } catch (Exception e) {
      setError(e);
    }
  }

  private void readLatticeAbc() throws Exception {
    if (tokenizeCastepCell() == 0)
      return;
    float factor = readLengthUnit();
    if (tokens.length >= 3) {
      a = parseFloat(tokens[0]) * factor;
      b = parseFloat(tokens[1]) * factor;
      c = parseFloat(tokens[2]) * factor;
    } else {
      Logger
          .warn("error reading a,b,c in %BLOCK LATTICE_ABC in CASTEP .cell file");
      return;
    }

    if (tokenizeCastepCell() == 0)
      return;
    if (tokens.length >= 3) {
      alpha = parseFloat(tokens[0]);
      beta = parseFloat(tokens[1]);
      gamma = parseFloat(tokens[2]);
    } else {
      Logger
          .warn("error reading alpha,beta,gamma in %BLOCK LATTICE_ABC in CASTEP .cell file");
    }

    
    for (int n = 0; n < 3; n++) {
      abc[n] = new Vector3f(Float.NaN, Float.NaN, Float.NaN);
    }
  }

  private void readLatticeCart() throws Exception {
    if (tokenizeCastepCell() == 0)
      return;
    float factor = readLengthUnit();
    float x, y, z;
    for (int n = 0; n < 3; n++) {
      if (tokens.length >= 3) {
        x = parseFloat(tokens[0]) * factor;
        y = parseFloat(tokens[1]) * factor;
        z = parseFloat(tokens[2]) * factor;
        abc[n] = new Vector3f(x, y, z);
      } else {
        Logger.warn("error reading coordinates of lattice vector "
            + Integer.toString(n + 1)
            + " in %BLOCK LATTICE_CART in CASTEP .cell file");
        return;
      }
      if (tokenizeCastepCell() == 0)
        return;
    }
    a = abc[0].length();
    b = abc[1].length();
    c = abc[2].length();
    alpha = (float) Math.toDegrees(abc[1].angle(abc[2]));
    beta = (float) Math.toDegrees(abc[2].angle(abc[0]));
    gamma = (float) Math.toDegrees(abc[0].angle(abc[1]));
  }

  private void readPositionsFrac() throws Exception {
    if (tokenizeCastepCell() == 0)
      return;
    readAtomData(1.0f);
  }

  private void readPositionsAbs() throws Exception {
    if (tokenizeCastepCell() == 0)
      return;
    float factor = readLengthUnit();
    readAtomData(factor);
  }

  
  private final static String[] lengthUnitIds = {
    "bohr", "m", "cm", "nm", "ang", "a0" };

  private final static float[] lengthUnitFactors = {
    ANGSTROMS_PER_BOHR, 1E10f, 1E8f, 1E1f, 1.0f, ANGSTROMS_PER_BOHR };

  private final static int lengthUnits = lengthUnitIds.length;

  private float readLengthUnit() throws Exception {

    float factor = 1.0f;
    for (int i=0; i<lengthUnits; i++) {
      if (tokens[0].equalsIgnoreCase(lengthUnitIds[i])) {
        factor = lengthUnitFactors[i];
        tokenizeCastepCell();
      }
    }
    return factor;
  }

  private void readAtomData(float factor) throws Exception {
    float x, y, z;
    do {
      if (tokens[0].equalsIgnoreCase("%ENDBLOCK"))
        break;
      if (tokens.length >= 4) {
        Atom atom = atomSetCollection.addNewAtom();
        x = parseFloat(tokens[1]) * factor;
        y = parseFloat(tokens[2]) * factor;
        z = parseFloat(tokens[3]) * factor;
        atom.set(x, y, z);
        atom.elementSymbol = tokens[0];
      } else {
        Logger.warn("cannot read line with CASTEP atom data: " + line);
      }
    } while (tokenizeCastepCell() > 0);
  }

  private int tokenizeCastepCell() throws Exception {
    while (true) {
      if (readLine() == null)
        return 0;
      if (line.trim().length() == 0)
        continue;
      tokens = getTokens();
      if (line.startsWith("#") || line.startsWith("!") || tokens[0].equals("#")
          || tokens[0].equals("!"))
        continue;
      break;
    }
    return tokens.length;
  }
}
