
package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.util.TextFormat;

import java.io.BufferedReader;



public class Wien2kReader extends AtomSetCollectionReader {

  private boolean isrhombohedral;
  private char latticeCode;
  private boolean doSymmetry = true;
  
  public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("wien2k");
    doSymmetry = !spaceGroup.equals("none");
    try {
      setFractionalCoordinates(true);
      atomSetCollection.setCollectionName(readLine());
      readUnitCell();
      readAtoms();
      readSymmetry();
      applySymmetryAndSetTrajectory();
      readEmbeddedScript();
    } catch (Exception e) {
      setError(e);
    }

  }

  
  private void readUnitCell() throws Exception {    
    readLine();
    isrhombohedral = ((latticeCode = line.charAt(0)) == 'R');
    
    if (line.startsWith("CYZ"))
      latticeCode = 'A'; 
    else if (line.startsWith("CXZ"))
      latticeCode = 'B'; 
    else if (line.startsWith("B"))
      latticeCode = 'I'; 
    if (latticeCode != 'R' && latticeCode != 'H')
      atomSetCollection.setLatticeParameter(latticeCode);
    if (line.length() > 32) {
      String name = line.substring(32).trim();
      if (name.indexOf(" ") >= 0)
        name = name.substring(name.indexOf(" ") + 1);
      if (name.indexOf("_") >= 0)
        name = name.substring(name.indexOf("_") + 1);
      setSpaceGroupName(name);
    }
    float factor = (readLine().toLowerCase().indexOf("ang") >= 0 ? 1f : ANGSTROMS_PER_BOHR);
    readLine();
    float a = parseFloat(line.substring(0,10)) * factor;
    float b = parseFloat(line.substring(10,20)) * factor;
    float c = parseFloat(line.substring(20,30)) * factor;
    int l = line.length();
    float alpha = (l >= 40 ? parseFloat(line.substring(30,40)) : 0);
    float beta = (l >= 50 ? parseFloat(line.substring(40,50)) : 0);
    float gamma = (l >= 60 ? parseFloat(line.substring(50,60)) : 0);
    if (isrhombohedral) {
      float ar = (float) Math.sqrt(a * a /3 + c * c / 9) ;
      alpha = beta = gamma = (float) (Math.acos( (2*c * c  - 3 * a * a) 
          / (2 * c * c + 6 * a * a)) * 180f / Math.PI);
      a = b = c = ar;
    }
    if (Float.isNaN(alpha) || alpha == 0)
      alpha = 90;
    if (Float.isNaN(beta) || beta == 0)
      beta = 90;
    if (Float.isNaN(gamma) || gamma == 0)
      gamma = 90; 
    setUnitCell(a, b, c, alpha, beta, gamma);  
  }
 
  private void readAtoms() throws Exception {

    
    
    readLine();
    while (line != null && (line.indexOf("ATOM") == 0 || !doSymmetry && line.indexOf(":") == 8)) {
      int thisAtom = atomSetCollection.getAtomCount();
      addAtom();
      if (readLine().indexOf("MULT=") == 10)
        for (int i = parseInt(line.substring(15,18)); --i >= 0; ) { 
          readLine();
          if (!doSymmetry)
            addAtom();
        }
      
      
      String atomName = line.substring(0, 10);
      String sym = atomName.substring(0,2).trim();
      if (sym.length() == 2 && Character.isDigit(sym.charAt(1)))
        sym = sym.substring(0, 1);
      atomName = TextFormat.simpleReplace(atomName, " ", "");
      int n = 0;
      for (int i = atomSetCollection.getAtomCount(); --i >= thisAtom; ) {
        Atom atom = atomSetCollection.getAtom(i);
        atom.elementSymbol = sym;
        atom.atomName = atomName + "_" + (n++);
      }
      while (readLine() != null && line.indexOf("ATOM") < 0 && line.indexOf("SYMMETRY") < 0) {
      }      
    }
    
  }

  private void addAtom() {
    float a = parseFloat(line.substring(12,22));
    float b = parseFloat(line.substring(25,35));
    float c = parseFloat(line.substring(38,48));
    if (false && isrhombohedral) {
      float ar = a;
      float br = b;
      float cr = c;
      a = ar * 2 / 3 - br * 1 / 3 - cr * 1 / 3;
      b = ar * 1 / 3 + br * 1 / 3 - cr * 2 / 3;
      c = ar * 1 / 3 + br * 1 / 3 + cr * 1 / 3;        
    }
    Atom atom = atomSetCollection.addNewAtom();
    setAtomCoord(atom, a, b, c);
  }
  
  private void readSymmetry() throws Exception {
    if (line.indexOf("SYMMETRY") < 0)
      return;
    int n = parseInt(line.substring(0, 4).trim());
    for (int i = n; --i >= 0;) {
      String xyz = getJones() + "," + getJones() + "," + getJones();
      if (doSymmetry)
        setSymmetryOperator(xyz);
      readLine();
    }   
  }
  
  private final String cxyz = " x y z";
  private String getJones() throws Exception {
    readLine();
    String xyz = "";
    
    float trans = parseFloat(line.substring(6));
    for (int i = 0; i < 6; i++) {
      if (line.charAt(i) == '-')
        xyz += "-";
      if (line.charAt(++i) == '1') {
        xyz += cxyz.charAt(i);
        if (trans > 0)
          xyz += "+";
        if (trans != 0)
          xyz += trans;
      }
    }
    return xyz;
  }
  
  private void readEmbeddedScript() throws Exception {
    while (line != null) {
      checkLineForScript();
      readLine();
    }
  }
}
