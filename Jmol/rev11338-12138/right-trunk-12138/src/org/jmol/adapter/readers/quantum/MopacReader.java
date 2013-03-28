
package org.jmol.adapter.readers.quantum;


abstract class MopacReader extends SlaterReader {

  protected final static float MIN_COEF = 0.0001f;  
  protected int[] atomicNumbers;
  
  final private static int [] sphericalDValues = new int[] { 
    
    0, -2, 0, 
    1,  0, 1, 
   -2,  0, 0, 
    0,  1, 1, 
    1,  1, 0, 
  };

  
  protected void createSphericalSlaterByType(int iAtom, int atomicNumber,
                                             String type, float zeta, float coef) {
    int pt = "S Px Py Pz  Dx2-y2Dxz Dz2 Dyz Dxy".indexOf(type);
           
    switch (pt) {
    case 0: 
      addSlater(iAtom, 0, 0, 0, getNPQs(atomicNumber) - 1, zeta, coef);
      return;
    case 2: 
    case 5: 
    case 8: 
      addSlater(iAtom, pt == 2 ? 1 : 0, pt == 5 ? 1 : 0, pt == 8 ? 1 : 0,
          getNPQp(atomicNumber) - 2, zeta, coef);
      return;
    }
    pt = (pt >> 2) * 3 - 9;   
    addSlater(iAtom, sphericalDValues[pt++], sphericalDValues[pt++],
        sphericalDValues[pt++], getNPQd(atomicNumber) - 3, zeta,
        coef);
  }  

  
  protected double scaleSlater(int ex, int ey, int ez, int er, double zeta) {
    if (ex >= 0 && ey >= 0) {
      
      return super.scaleSlater(ex, ey, ez, er, zeta);
    }
    int el = Math.abs(ex + ey + ez);
    if (el == 3) {
      return 0; 
    }

    
    
    
    
    

    return getSlaterConstDSpherical(el + er + 1, Math.abs(zeta), ex, ey);
  }

  

  

  

  
  
  
  
  
  
  

  private final static int[] principalQuantumNumber = new int[] { 0, 
      1, 1, 
      2, 2, 2, 2, 2, 2, 2, 2, 
      3, 3, 3, 3, 3, 3, 3, 3, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
      6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
      6, 6, 6, 6, 6, 6, 6, 6, 
  };

  private final static int[] npqd = new int[] { 0,
    0, 3, 
    0, 0, 0, 0, 0, 0, 0, 3, 
    3, 3, 3, 3, 3, 3, 3, 4, 
    3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 
    4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 
    5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
    5, 6, 6, 6, 6, 6, 6, 7, 
  };

  private final static int getNPQ(int atomicNumber) {
    return (atomicNumber < principalQuantumNumber.length ? 
        principalQuantumNumber[atomicNumber] : 0);
  }

  
  private final static int getNPQs(int atomicNumber) {
    int n = getNPQ(atomicNumber);
    switch (atomicNumber) {
    case 10:
    case 18:
    case 36:
    case 54:
    case 86:
      return n + 1;
    default:
      return n;        
    }
  }

  
  private final static int getNPQp(int atomicNumber) {
    int n = getNPQ(atomicNumber);
    switch (atomicNumber) {
    case 2:
      return n + 1;
    default:
      return n;        
    }
  }

  
  private final static int getNPQd(int atomicNumber) {
    return (atomicNumber < npqd.length ? npqd[atomicNumber] : 0);
  }

}
