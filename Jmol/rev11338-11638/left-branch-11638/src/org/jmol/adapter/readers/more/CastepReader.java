



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

  public void readAtomSetCollection(BufferedReader br) {

    reader = br;
    atomSetCollection = new AtomSetCollection("castep");

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

    float factor = 1.0f;

    if (tokenizeCastepCell() == 0)
      return;
    if (tokens[0].equalsIgnoreCase("bohr"))
      factor = ANGSTROMS_PER_BOHR;
    if (tokens.length < 3)
      tokenizeCastepCell();
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
  }

  private void readLatticeCart() throws Exception {

    float factor = 1.0f;
    float x, y, z;
    Vector3f[] lv = new Vector3f[3];

    if (tokenizeCastepCell() == 0)
      return;
    if (tokens[0].equalsIgnoreCase("bohr"))
      factor = ANGSTROMS_PER_BOHR;
    if (tokens.length < 3)
      tokenizeCastepCell();
    for (int n = 0; n < 3; n++) {
      if (tokens.length >= 3) {
        x = parseFloat(tokens[0]) * factor;
        y = parseFloat(tokens[1]) * factor;
        z = parseFloat(tokens[2]) * factor;
        lv[n] = new Vector3f(x, y, z);
      } else {
        Logger.warn("error reading coordinates of lattice vector "
            + Integer.toString(n + 1)
            + " in %BLOCK LATTICE_CART in CASTEP .cell file");
        return;
      }
      if (tokenizeCastepCell() == 0)
        return;
    }

    a = lv[0].length();
    b = lv[1].length();
    c = lv[2].length();
    alpha = (float) Math.toDegrees(lv[1].angle(lv[2]));
    beta = (float) Math.toDegrees(lv[2].angle(lv[0]));
    gamma = (float) Math.toDegrees(lv[0].angle(lv[1]));
  }

  private void readPositionsFrac() throws Exception {

    if (tokenizeCastepCell() == 0)
      return;
    readAtomData(1.0f);
  }

  private void readPositionsAbs() throws Exception {

    if (tokenizeCastepCell() == 0)
      return;
    if (tokens[0].equalsIgnoreCase("bohr")) {
      tokenizeCastepCell();
      readAtomData(ANGSTROMS_PER_BOHR);
    } else if (tokens[0].equalsIgnoreCase("ang")){
      tokenizeCastepCell();
      readAtomData(1.0f);
    } else {
      readAtomData(1.0f);
    }
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
