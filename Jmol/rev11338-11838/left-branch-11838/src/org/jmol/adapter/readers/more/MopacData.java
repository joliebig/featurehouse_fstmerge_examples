
package org.jmol.adapter.readers.more;



public class MopacData {

  

  private final static boolean isNoble(int atomicNumber) {
    switch (atomicNumber) {
    case 2:
    case 10:
    case 18:
    case 36:
    case 54:
    case 86:
      return true;
    default:
      return false;
    }
  }

  
  
  
  
  
  
  

  private final static int[] principalQuantumNumber = new int[] { 0, 1, 1, 
      2, 2, 2, 2, 2, 2, 2, 2, 
      3, 3, 3, 3, 3, 3, 3, 3, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 
      6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
      6, 6, 6, 6, 6, 6, 6, 6, 
  };

  private final static int getNPQ(int atomicNumber) {
    return (atomicNumber < principalQuantumNumber.length ? principalQuantumNumber[atomicNumber]
        : 0);
  }

  public final static int getNPQs(int atomicNumber) {
    return getNPQ(atomicNumber)
        + (atomicNumber > 2 && isNoble(atomicNumber) ? 1 : 0);
  }

  public final static int getNPQp(int atomicNumber) {
    return getNPQ(atomicNumber) + (atomicNumber == 2 ? 1 : 0);
  }

  private final static int[] pnqD = new int[] { 0, 
      0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 
      3, 3, 3, 3, 3, 3, 3, 4, 
      3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 
      4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 
      5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
      5, 6, 6, 6, 6, 6, 6, 7, 
  };

  public final static int getNPQd(int atomicNumber) {
    return (atomicNumber < pnqD.length ? pnqD[atomicNumber] : 0);
  }

  private final static float[] fact = new float[20];
  static {
    fact[0] = 1;
    for (int n = 1; n < fact.length; n++)
      fact[n] = fact[n - 1] * n;
  }

  private final static float fourPi = (float) (4 * Math.PI);

  public final static float getMopacConstS(int atomicNumber, float zeta) {
    int n = getNPQs(atomicNumber);
    return (float) (Math.pow(2 * zeta, n + 0.5) * Math.sqrt(1 / fourPi
        / fact[2 * n]));
  }

  public final static float getMopacConstP(int atomicNumber, float zeta) {
    int n = getNPQp(atomicNumber);
    return (float) (Math.pow(2 * zeta, n + 0.5) * Math.sqrt(3 / fourPi
        / fact[2 * n]));
  }

  private final static float[] factorDs = new float[] { 0.5f, 1f,
      (float) (0.5 / Math.sqrt(3)), 1f, 1f };

  

  public static float getFactorD(int n) {
    return factorDs[n];
  }

  public final static float getMopacConstD(int atomicNumber, float zeta) {
    int n = getNPQd(atomicNumber);
    return (float) (Math.pow(2 * zeta, n + 0.5) * Math.sqrt(15 / fourPi
        / fact[2 * n]));
  }
}
