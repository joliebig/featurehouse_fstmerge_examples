

package org.jmol.minimize.forcefield;

import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Vector3d;

import org.jmol.minimize.MinAtom;
import org.jmol.minimize.MinBond;
import org.jmol.minimize.Util;

abstract class Calculations {

  final public static double RAD_TO_DEG = (180.0 / Math.PI);
  final public static double DEG_TO_RAD = (Math.PI / 180.0);

  final static double KCAL_TO_KJ = 4.1868;

  final static int CALC_DISTANCE = 0; 
  final static int CALC_ANGLE = 1;    
  final static int CALC_TORSION = 2;
  final static int CALC_OOP = 3;
  final static int CALC_VDW = 4;
  final static int CALC_ES = 5;
  final static int CALC_MAX = 6;

  ForceField ff;
  Vector[] calculations = new Vector[CALC_MAX];
  public Hashtable ffParams;
  
  int atomCount;
  int bondCount;
  MinAtom[] atoms;
  MinBond[] bonds;
  int[][] angles;
  int[][] torsions;
  double[] partialCharges;
  boolean havePartialCharges;
  Vector constraints;
  boolean isPreliminary;

  public void setConstraints(Vector constraints) {
    this.constraints = constraints;
  }

  Calculations(ForceField ff, MinAtom[] minAtoms, MinBond[] minBonds, 
      int[][] angles, int[][] torsions, double[] partialCharges, 
      Vector constraints) {
    this.ff = ff;
    atoms = minAtoms;
    bonds = minBonds;
    this.angles = angles;
    this.torsions = torsions;
    this.constraints = constraints;
    atomCount = atoms.length;
    bondCount = bonds.length;
    if (partialCharges != null && partialCharges.length == atomCount)
      for (int i = atomCount; --i >= 0;)
        if (partialCharges[i] != 0) {
          havePartialCharges = true;
          break;
        }
    if (!havePartialCharges)
      partialCharges = null;
    this.partialCharges = partialCharges;
  }

  boolean haveParams() {
    return (ffParams != null);
  }

  void setParams(Hashtable temp) {
    ffParams = temp;
  }

  static FFParam getParameter(String a, Hashtable ffParams) {
    return (FFParam) ffParams.get(a);
  }

  abstract boolean setupCalculations();

  abstract String getAtomList(String title);

  abstract boolean setupElectrostatics();

  abstract String getDebugHeader(int iType);

  abstract String getDebugFooter(int iType, double energy);

  abstract String getUnit();

  abstract double compute(int iType, Object[] dataIn);

  void addForce(Vector3d v, int i, double dE) {
    atoms[i].force[0] += v.x * dE;
    atoms[i].force[1] += v.y * dE;
    atoms[i].force[2] += v.z * dE;
  }

  boolean gradients;

  boolean silent;
  
  public void setSilent(boolean TF) {
    silent = TF;
  }
  
  StringBuffer logData = new StringBuffer();
  public String getLogData() {
    return logData.toString();
  }

  void appendLogData(String s) {
    logData.append(s).append("\n");
  }
  
  boolean logging;
  boolean loggingEnabled;
  
  void setLoggingEnabled(boolean TF) {
    loggingEnabled = TF;
    if (loggingEnabled)
      logData = new StringBuffer();
  }

  void setPreliminary(boolean TF) {
    isPreliminary = TF;
  }
  
  private double calc(int iType, boolean gradients) {
    logging = loggingEnabled && !silent;
    this.gradients = gradients;
    Vector calc = calculations[iType];
    int nCalc;
    double energy = 0;
    if (calc == null || (nCalc = calc.size()) == 0)
      return 0;
    if (logging)
      appendLogData(getDebugHeader(iType));
    for (int ii = 0; ii < nCalc; ii++)
      energy += compute(iType, (Object[]) calculations[iType]
          .get(ii));
    if (logging)
      appendLogData(getDebugFooter(iType, energy));
    if (constraints != null && iType <= CALC_TORSION)
      energy += constraintEnergy(iType);
    return energy;
  }

  double energyStrBnd(boolean gradients) {
    return 0.0f;
  }

  double energyBond(boolean gradients) {
    return calc(CALC_DISTANCE, gradients);
  }

  double energyAngle(boolean gradients) {
    return calc(CALC_ANGLE, gradients);
  }

  double energyTorsion(boolean gradients) {
    return calc(CALC_TORSION, gradients);
  }

  double energyOOP(boolean gradients) {
    return calc(CALC_OOP, gradients);
  }

  double energyVDW(boolean gradients) {
    return calc(CALC_VDW, gradients);
  }

  double energyES(boolean gradients) {
    return calc(CALC_ES, gradients);
  }
  
  final Vector3d da = new Vector3d();
  final Vector3d db = new Vector3d();
  final Vector3d dc = new Vector3d();
  final Vector3d dd = new Vector3d();
  int ia, ib, ic, id;

  final Vector3d v1 = new Vector3d();
  final Vector3d v2 = new Vector3d();
  final Vector3d v3 = new Vector3d();
  
  private final static double PI_OVER_2 = Math.PI / 2;
  private final static double TWO_PI = Math.PI * 2;
  
  private double constraintEnergy(int iType) {

    double value = 0;
    double k = 0;
    double energy = 0;

    for (int i = constraints.size(); --i >= 0; ) {
      Object[] c = (Object[])constraints.elementAt(i);
      int nAtoms = ((int[]) c[0])[0];
      if (nAtoms != iType + 2)
        continue;
      int[] minList = (int[]) c[1];
      double targetValue = ((Float)c[2]).doubleValue();

      switch (iType) {
      case CALC_TORSION:
        id = minList[3];
        if (gradients)
          dd.set(atoms[id].coord);
        
      case CALC_ANGLE:
        ic = minList[2];
        if (gradients)
          dc.set(atoms[ic].coord);
        
      case CALC_DISTANCE:
        ib = minList[1];
        ia = minList[0];
        if (gradients) {
          db.set(atoms[ib].coord);
          da.set(atoms[ia].coord);
        }
      }

      k = 10000.0;

      switch (iType) {
      case CALC_TORSION:
        targetValue *= DEG_TO_RAD;
        value = (gradients ? Util.restorativeForceAndTorsionAngleRadians(da, db, dc, dd)
            : Util.getTorsionAngleRadians(atoms[ia].coord, 
              atoms[ib].coord, atoms[ic].coord, atoms[id].coord, v1, v2, v3));
        if (value < 0 && targetValue >= PI_OVER_2)
          value += TWO_PI; 
        else if (value > 0 && targetValue <= -PI_OVER_2)
          targetValue += TWO_PI;
       break;
      case CALC_ANGLE:
        targetValue *= DEG_TO_RAD;
        value = (gradients ? Util.restorativeForceAndAngleRadians(da, db, dc)
            : Util.getAngleRadiansABC(atoms[ia].coord, atoms[ib].coord,
              atoms[ic].coord));
        break;
      case CALC_DISTANCE:
        value = (gradients ? Util.restorativeForceAndDistance(da, db, dc)
            : Math.sqrt(Util.distance2(atoms[ia].coord, atoms[ib].coord)));
        break;
      }
      energy += constrainQuadratic(value, targetValue, k, iType);
    }
    return energy;
  }

  private double constrainQuadratic(double value, double targetValue, double k, int iType) {

    if (!Util.isFinite(value))
      return 0;

    double delta = value - targetValue;

    if (gradients) {
      double dE = 2.0 * k * delta;
      switch(iType) {
      case CALC_TORSION:
        addForce(dd, id, dE);
        
      case CALC_ANGLE:
        addForce(dc, ic, dE);
        
      case CALC_DISTANCE:
        addForce(db, ib, dE);
        addForce(da, ia, dE);
      }
    }
    return k * delta * delta;
  }

}
