

package org.jmol.minimize.forcefield;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;




import org.jmol.minimize.MinAtom;
import org.jmol.minimize.MinBond;
import org.jmol.minimize.Minimizer;
import org.jmol.minimize.Util;
import org.jmol.util.Logger;
import org.jmol.util.TextFormat;
import org.jmol.viewer.Viewer;

abstract public class ForceField {

  
  
  
  final static int ENERGY = (1 << 0); 
  final static int EBOND = (1 << 1); 
  final static int EANGLE = (1 << 2); 
  final static int ESTRBND = (1 << 3); 
  final static int ETORSION = (1 << 4); 
  final static int EOOP = (1 << 5); 
  final static int EVDW = (1 << 6); 
  final static int EELECTROSTATIC = (1 << 7); 

  Calculations calc;
  
  private String getUnits() {
    return calc.getUnit();
  }

  public abstract Vector getAtomTypes();

  protected abstract Hashtable getFFParameters();

  private double criterion, e0, dE; 
  private int currentStep, stepMax;
  private double[][] coordSaved;  

  int atomCount; 
  int bondCount;

  Viewer viewer;
  MinAtom[] atoms;
  MinBond[] bonds;
  BitSet bsFixed;
  
  public ForceField() {}
  
  public void setModel(Minimizer m) {
  
    this.viewer = m.viewer;
    this.atoms = m.minAtoms;
    this.bonds = m.minBonds;
    this.bsFixed = m.bsMinFixed;
    atomCount = atoms.length;
    bondCount = bonds.length;
  }
  
  public void setConstraints(Minimizer m) {
    this.bsFixed = m.bsMinFixed;
    calc.setConstraints(m.constraints);
  }
    
  public boolean setup() {
    if (calc.haveParams())
      return true;
    Hashtable temp = getFFParameters();
    if (temp == null)
      return false;
    calc.setParams(temp);
    return calc.setupCalculations();
  }

  
  
  
  
  

  
  
  public void steepestDescentInitialize(int stepMax, double criterion) {
    this.stepMax = stepMax;
    this.criterion = criterion; 
    currentStep = 0;
    clearForces();
    calc.setLoggingEnabled(true);
    calc.setLoggingEnabled(stepMax == 0 || Logger.isActiveLevel(Logger.LEVEL_DEBUGHIGH));
    String s = calc.getDebugHeader(-1) + "Jmol Minimization Version " + Viewer.getJmolVersion() + "\n";
    calc.appendLogData(s);
    Logger.info(s);
    if (calc.loggingEnabled)
      calc.appendLogData(calc.getAtomList("S T E E P E S T   D E S C E N T"));
    dE = 0;
    calc.setPreliminary(stepMax > 0);
    e0 = energyFull(false, false);
    s = TextFormat.sprintf(" Initial E = %10.3f " + calc.getUnit() + " criterion = %8.6f max steps = " + stepMax, 
        new Object[] { new Float(e0), new Float(criterion) });
    viewer.showString(s, false);
    calc.appendLogData(s);
  }

  private void clearForces() {
    for (int i = 0; i < atomCount; i++)
      atoms[i].force[0] = atoms[i].force[1] = atoms[i].force[2] = 0; 
  }
  
  
  public boolean steepestDescentTakeNSteps(int n) {
    if (stepMax == 0)
      return false;
    boolean isPreliminary = true;
    for (int iStep = 1; iStep <= n; iStep++) {
      currentStep++;
      calc.setSilent(true);
      for (int i = 0; i < atomCount; i++)
        if (bsFixed == null || !bsFixed.get(i))
          setForcesUsingNumericalDerivative(atoms[i], ENERGY);
      linearSearch();
      calc.setSilent(false);

      if (calc.loggingEnabled)
        calc.appendLogData(calc.getAtomList("S T E P    " + currentStep));

      double e1 = energyFull(false, false);
      dE = e1 - e0;
      boolean done = Util.isNear(e1, e0, criterion);

      if (done || currentStep % 10 == 0 || stepMax <= currentStep) {
        String s = TextFormat.sprintf(" Step %-4d E = %10.6f    dE = %8.6f",
            new Object[] { new float[] { (float) e1, (float) (dE), (float) criterion },
            new Integer(currentStep) });
        viewer.showString(s, false);
        calc.appendLogData(s);
      }
      e0 = e1;
      if (done || stepMax <= currentStep) {
        if (calc.loggingEnabled)
          calc.appendLogData(calc.getAtomList("F I N A L  G E O M E T R Y"));
        if (done) {
          String s = TextFormat.formatString(
              "\n   STEEPEST DESCENT HAS CONVERGED: E = %8.5f " + getUnits() + " after " + currentStep + " steps", "f",
              (float) e1);
          calc.appendLogData(s);
          viewer.scriptEcho(s);

          Logger.info(s);
        }
        return false;
      }
      if (isPreliminary && getNormalizedDE() >= 2) {
        calc.setPreliminary(isPreliminary = false);
        e0 = energyFull(false, false);
      }
    }
    return true; 
  }

  private double getEnergy(int terms, boolean gradients) {
    if ((terms & ENERGY) != 0)
      return energyFull(gradients, true);
    double e = 0.0;
    if ((terms & EBOND) != 0)
      e += energyBond(gradients);
    if ((terms & EANGLE) != 0)
      e += energyAngle(gradients);
    if ((terms & ESTRBND) != 0)
      e += energyStrBnd(gradients);
    if ((terms & ETORSION) != 0)
     e += energyTorsion(gradients);
    if ((terms & EOOP) != 0)
      e += energyOOP(gradients);
    if ((terms & EVDW) != 0)
      e += energyVDW(gradients);
    if ((terms & EELECTROSTATIC) != 0)
      e += energyES(gradients);
    return e;
  }

  
  
  
  
  
  private void setForcesUsingNumericalDerivative(MinAtom atom, int terms) {
    double delta = 1.0e-5;
    atom.force[0] = -getDE(atom, terms, 0, delta);
    atom.force[1] = -getDE(atom, terms, 1, delta);
    atom.force[2] = -getDE(atom, terms, 2, delta);
    
      
    return;
  }

  private double getDE(MinAtom atom, int terms, int i, double delta) {
    
    atom.coord[i] += delta;
    double e = getEnergy(terms, false);
    atom.coord[i] -= delta;
    
      
    return (e - e0) / delta;
  }

  
  public double energyFull(boolean gradients, boolean isSilent) {
    double energy;

    if (gradients)
      clearForces();

    energy = energyBond(gradients) +
        energyAngle(gradients)
       + energyTorsion(gradients)
       + energyOOP(gradients)
       + energyVDW(gradients)
       + energyES(gradients);

    if (!isSilent && calc.loggingEnabled)      
      calc.appendLogData(TextFormat.sprintf("\nTOTAL ENERGY = %8.3f %s\n", 
          new Object[] {new Float(energy), getUnits() }));
    return energy;
  }

  double energyStrBnd(boolean gradients) {
    return 0.0f;
  }

  double energyBond(boolean gradients) {
    return calc.energyBond(gradients); 
  }
  
  double energyAngle(boolean gradients) {
    return calc.energyAngle(gradients); 
  }

  double energyTorsion(boolean gradients) {
    return calc.energyTorsion(gradients); 
  }

  double energyOOP(boolean gradients) {
    return calc.energyOOP(gradients); 
  }

  double energyVDW(boolean gradients) {
    return calc.energyVDW(gradients);
  }

  double energyES(boolean gradients) {
    return calc.energyES(gradients);
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  

  private void linearSearch() {

    double alpha = 0.0; 
    double step = 0.23;
    double trustRadius = 0.3; 
    double trustRadius2 = trustRadius * trustRadius;

    double e1 = energyFull(false, true);

    for (int iStep = 0; iStep < 10; iStep++) {
      saveCoordinates();
      for (int i = 0; i < atomCount; ++i)
        if (bsFixed == null || !bsFixed.get(i)) {
          double[] force = atoms[i].force;
          double[] coord = atoms[i].coord;
          double f2 = (force[0] * force[0] + force[1] * force[1] + force[2]
              * force[2]);
          if (f2 > trustRadius2 / step / step) {
            f2 = trustRadius / Math.sqrt(f2) / step;
            
            
            
            
            force[0] *= f2;
            force[1] *= f2;
            force[2] *= f2;
          }
          for (int j = 0; j < 3; ++j) {
            if (Util.isFinite(force[j])) {
              double tempStep = force[j] * step;
              if (tempStep > trustRadius)
                coord[j] += trustRadius;
              else if (tempStep < -trustRadius)
                coord[j] -= trustRadius;
              else
                coord[j] += tempStep;
            }
          }
        }

      double e2 = energyFull(false, true);

      
      
      if (Util.isNear(e2, e1, 1.0e-3))
        break;
      if (e2 > e1) {
        step *= 0.1;
        restoreCoordinates();
      } else if (e2 < e1) {
        e1 = e2;
        alpha += step;
        step *= 2.15;
        if (step > 1.0)
          step = 1.0;
      }
    }
    
  }

  private void saveCoordinates() {
    if (coordSaved == null)
      coordSaved = new double[atomCount][3];
    for (int i = 0; i < atomCount; i++) 
      for (int j = 0; j < 3; j++)
        coordSaved[i][j] = atoms[i].coord[j];
  }
  
  private void restoreCoordinates() {
    for (int i = 0; i < atomCount; i++) 
      for (int j = 0; j < 3; j++)
        atoms[i].coord[j] = coordSaved[i][j];
  }
  
  public boolean detectExplosion() {
    for (int i = 0; i < atomCount; i++) {
      MinAtom atom = atoms[i];
      for (int j = 0; j < 3; j++)
        if (!Util.isFinite(atom.coord[j]))
          return true;
    }
    for (int i = 0; i < bondCount; i++) {
      MinBond bond = bonds[i];
      if (Util.distance2(atoms[bond.atomIndexes[0]].coord,
          atoms[bond.atomIndexes[1]].coord) > 900.0)
        return true;
    }
    return false;
  }

  public int getCurrentStep() {
    return currentStep;
  }

  public double getEnergy() {
    return e0;
  }
  
  public String getAtomList(String title) {
    return calc.getAtomList(title);
  }

  public double getEnergyDiff() {
    return dE;
  }

  public String getLogData() {
    return calc.getLogData();
  }
  
  double getNormalizedDE() {
    return Math.abs(dE/criterion);
  }

}
