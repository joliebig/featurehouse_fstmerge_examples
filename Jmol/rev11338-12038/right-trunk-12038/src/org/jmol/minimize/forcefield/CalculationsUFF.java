

package org.jmol.minimize.forcefield;

import java.util.Vector;

import org.jmol.minimize.MinAtom;
import org.jmol.minimize.MinBond;
import org.jmol.minimize.Util;
import org.jmol.util.TextFormat;



class CalculationsUFF extends Calculations {

  public final static int PAR_R = 0;       
  public final static int PAR_THETA = 1;   
  public final static int PAR_X = 2;       
  public final static int PAR_D = 3;       
  public final static int PAR_ZETA = 4;    
  public final static int PAR_Z = 5;       
  public final static int PAR_V = 6;       
  public final static int PAR_U = 7;       
  public final static int PAR_XI = 8;      
  public final static int PAR_HARD = 9;    
  public final static int PAR_RADIUS = 10; 

  DistanceCalc bondCalc;
  AngleCalc angleCalc;
  TorsionCalc torsionCalc;
  OOPCalc oopCalc;
  VDWCalc vdwCalc;
  ESCalc esCalc;
    
  CalculationsUFF(ForceField ff, MinAtom[] minAtoms, MinBond[] minBonds, 
      int[][] angles, int[][] torsions, double[] partialCharges,
      Vector constraints) {
    super(ff, minAtoms, minBonds, angles, torsions, partialCharges, constraints);    
    bondCalc = new DistanceCalc();
    angleCalc = new AngleCalc();
    torsionCalc = new TorsionCalc();
    oopCalc = new OOPCalc();
    vdwCalc = new VDWCalc();
    esCalc = new ESCalc();
  }
  
  String getUnit() {
    return "kJ/mol"; 
  }

  boolean setupCalculations() {

    Vector calc;

    DistanceCalc distanceCalc = new DistanceCalc();
    calc = calculations[CALC_DISTANCE] = new Vector();
    for (int i = 0; i < bondCount; i++) {
      MinBond bond = bonds[i];
      double bondOrder = bond.atomIndexes[2];
      if (bond.isAromatic)
        bondOrder = 1.5;
      if (bond.isAmide)
        bondOrder = 1.41;  
      distanceCalc.setData(calc, bond.atomIndexes[0], bond.atomIndexes[1], bondOrder);
    }

    calc = calculations[CALC_ANGLE] = new Vector();
    AngleCalc angleCalc = new AngleCalc();
    for (int i = angles.length; --i >= 0;)
      angleCalc.setData(calc, i);

    calc = calculations[CALC_TORSION] = new Vector();
    TorsionCalc torsionCalc = new TorsionCalc();
    for (int i = torsions.length; --i >= 0;)
      torsionCalc.setData(calc, i);

    calc = calculations[CALC_OOP] = new Vector();
    
    OOPCalc oopCalc = new OOPCalc();
    int elemNo;
    for (int i = 0; i < atomCount; i++) {
      MinAtom a = atoms[i];
      if (a.nBonds == 3 && isInvertible(elemNo = a.atom.getElementNumber()))
        oopCalc.setData(calc, i, elemNo);
    }

    pairSearch(calculations[CALC_VDW] = new Vector(), new VDWCalc());

    return true;
  }

  private boolean isInvertible(int n) {
    switch (n) {
    case 6: 
    case 7: 
    case 8: 
    case 15: 
    case 33: 
    case 51: 
    case 83: 
      return true;
    default: 
      return false;
    }
  }

  private void pairSearch(Vector calc, PairCalc type) {
     for (int i = 0; i < atomCount - 1; i++) { 
      MinAtom atomA = atoms[i];
      int[] atomList1 = atomA.getBondedAtomIndexes();
      B: for (int j = i + 1; j < atomCount; j++) { 
        MinAtom atomB = atoms[j];
         for (int k = atomList1.length; --k >= 0;) { 
          MinAtom nbrA = atoms[atomList1[k]];
          if (nbrA == atomB)
            continue B; 
          if (nbrA.nBonds == 1)
            continue;
          int[] atomList2 = nbrA.getBondedAtomIndexes(); 
           for (int l = atomList2.length; --l >= 0;) {
            MinAtom nbrAA = atoms[atomList2[l]];
            if (nbrAA == atomB)
              continue B; 
            
            
            
            
            
          }
        }
        type.setData(calc, i, j);
      }
    }
  }

  boolean setupElectrostatics() {

    
    
    

    if (partialCharges == null)
      return true;

    pairSearch(calculations[CALC_ES] = new Vector(), new ESCalc());
    return true;
  }

  static double calculateR0(double ri, double rj, double chiI, double chiJ,
                            double bondorder) {
    
    
    double rbo = -0.1332 * (ri + rj) * Math.log(bondorder);
    
    
    double dchi = Math.sqrt(chiI) - Math.sqrt(chiJ);
    double ren = ri * rj * dchi * dchi / (chiI * ri + chiJ * rj);
    
    
    
    return (ri + rj + rbo - ren);
  }

  double compute(int iType, Object[] dataIn) {

    switch (iType) {
    case CALC_DISTANCE:
      return bondCalc.compute(dataIn);
    case CALC_ANGLE:
      return angleCalc.compute(dataIn);
    case CALC_TORSION:
      return torsionCalc.compute(dataIn);
    case CALC_OOP:
      return oopCalc.compute(dataIn);
    case CALC_VDW:
      return vdwCalc.compute(dataIn);
    case CALC_ES:
      return esCalc.compute(dataIn);
    }
    return 0.0;
  }

  class DistanceCalc extends Calculation {

    double r0, kb;

    void setData(Vector calc, int ia, int ib, double bondOrder) {
      parA = getParameter(atoms[ia].type, ffParams);
      parB = getParameter(atoms[ib].type, ffParams);
      r0 = calculateR0(parA.dVal[PAR_R], parB.dVal[PAR_R], parA.dVal[PAR_XI],
          parB.dVal[PAR_XI], bondOrder);

      
      

      kb = KCAL332 * parA.dVal[PAR_Z] * parB.dVal[PAR_Z] / (r0 * r0 * r0);
      calc.addElement(new Object[] { new int[] { ia, ib },
          new double[] { r0, kb, bondOrder } });
    }

    double compute(Object[] dataIn) {
      getPointers(dataIn);
      r0 = dData[0];
      kb = dData[1];
      ia = iData[0];
      ib = iData[1];
      
      if (gradients) {
        da.set(atoms[ia].coord);
        db.set(atoms[ib].coord);
        rab = Util.restorativeForceAndDistance(da, db, dc);
      } else {
        rab = Math.sqrt(Util.distance2(atoms[ia].coord, atoms[ib].coord));
      }

      
      
      delta = rab - r0;     
      energy = kb * delta * delta; 

      if (gradients) {
        dE = 2.0 * kb * delta;
        addForce(da, ia, dE);
        addForce(db, ib, dE);
      }
      
      if (logging)
        appendLogData(getDebugLine(CALC_DISTANCE, this));
      
      return energy;
    }
  }

  
  final static double KCAL644 = 644.12 * KCAL_TO_KJ;
  
  class AngleCalc extends Calculation {
  
    void setData(Vector calc, int i) {
      int[] angle = (int[]) angles[i];
      a = atoms[ia = angle[0]];
      b = atoms[ib = angle[1]];
      c = atoms[ic = angle[2]];
      boolean isHXH = (a.type.equals("H_") && c.type.equals("H_"));

      parA = getParameter(a.type, ffParams);
      parB = getParameter(b.type, ffParams);
      parC = getParameter(c.type, ffParams);

      int coordination = parB.iVal[0]; 

      double zi = parA.dVal[PAR_Z];
      double zk = parC.dVal[PAR_Z];
      double theta0 = parB.dVal[PAR_THETA];
      double cosT0 = Math.cos(theta0);
      double sinT0 = Math.sin(theta0);
      double c0, c1, c2;
      switch (coordination) {
      case 1:
      case 2:
      case 4:
      case 6:
        c0 = c1 = c2 = 0;
        break;
      default:  
        c2 = 1.0 / (4.0 * sinT0 * sinT0);
        c1 = -4.0 * c2 * cosT0;
        c0 = c2 * (2.0 * cosT0 * cosT0 + 1.0);
      }

      
      MinBond bond = a.getBondTo(ib);
      double bondorder = bond.atomIndexes[2];
      if (bond.isAromatic)
        bondorder = 1.5;
      if (bond.isAmide)
        bondorder = 1.41;
      rab = calculateR0(parA.dVal[PAR_R], parB.dVal[PAR_R], parA.dVal[PAR_XI], parB.dVal[PAR_XI], bondorder);

      bond = c.getBondTo(ib);
      bondorder = bond.atomIndexes[2];
      if (bond.isAromatic)
        bondorder = 1.5;
      if (bond.isAmide)
        bondorder = 1.41;
      double rbc = calculateR0(parB.dVal[PAR_R], parC.dVal[PAR_R], 
          parB.dVal[PAR_XI], parC.dVal[PAR_XI], bondorder);
      double rac = Math.sqrt(rab * rab + rbc * rbc - 2.0 * rab * rbc * cosT0);

      
      
      double ka = (KCAL644) * (zi * zk / (Math.pow(rac, 5.0)))
          * (3.0 * rab * rbc * (1.0 - cosT0 * cosT0) - rac * rac * cosT0);
      calc.addElement(new Object[] {
          new int[] { ia, ib, ic, coordination },
          new double[] { ka, c0 - c2, c1, 2 * c2, theta0 * RAD_TO_DEG, (isHXH ? ka * 10 : ka) } });
    }

    double compute(Object[] dataIn) {
      
      getPointers(dataIn);
      ia = iData[0];
      ib = iData[1];
      ic = iData[2];
      int coordination = iData[3];
      double ka = (isPreliminary ? dData[4] : dData[0]);
      double a0 = dData[1];
      double a1 = dData[2];
      double a2 = dData[3];
      
      if (gradients) {
        da.set(atoms[ia].coord);
        db.set(atoms[ib].coord);
        dc.set(atoms[ic].coord);
        theta = Util.restorativeForceAndAngleRadians(da, db, dc);
      } else {
        theta = Util.getAngleRadiansABC(atoms[ia].coord, atoms[ib].coord, atoms[ic].coord);
      }

      if (!Util.isFinite(theta))
        theta = 0.0; 

      
      if ((coordination == 4 || coordination == 6) && 
          (theta > 2.35619 || theta < 0.785398)) 
        coordination = 1;
      double cosT = Math.cos(theta);
      double sinT = Math.sin(theta);
      switch (coordination) {
      case 0: 
      case 1: 
        energy = ka * (1.0 + cosT) * (1.0 + cosT) / 4.0;
        break;
      case 2: 
         
        energy = ka * (1.0  + (4.0 * cosT) * (1.0 + cosT)) / 9.0;
        break;
      case 4: 
      case 6: 
        energy = ka * cosT * cosT;
        break;
      default:
        
        energy = ka * (a0 + a1 * cosT + a2 * cosT * cosT);
      }

      if (gradients) {
        
        switch (coordination) {
        case 0: 
        case 1:
          dE = -0.5 * ka * sinT * (1 + cosT);
          break;
        case 2:
          dE = -4.0 * sinT * ka * (1.0 - 2.0 * cosT)/9.0;
          break;
        case 4:
        case 6:
          dE = -ka * sinT * cosT;
          break;
        default:
          dE = -ka * (a1 * sinT - 2.0 * a2 * cosT * sinT);
        }
        addForce(da, ia, dE);
        addForce(db, ib, dE);
        addForce(dc, ic, dE);
      }
      
      if (logging)
        appendLogData(getDebugLine(CALC_ANGLE, this));
      
      return energy;
    }
  }

  class TorsionCalc extends Calculation {

   void setData(Vector calc, int i) {
      int[] t = torsions[i];
      double cosNPhi0 = -1; 
      int n = 0;
      double V = 0;
      a = atoms[ia = t[0]];
      b = atoms[ib = t[1]];
      c = atoms[ic = t[2]];
      d = atoms[id = t[3]];
      MinBond bc = c.getBondTo(ib);
      double bondOrder = bc.atomIndexes[2];
      if (bc.isAromatic)
        bondOrder = 1.5;
      if (bc.isAmide)
        bondOrder = 1.41;

      parB = getParameter(b.type, ffParams);
      parC = getParameter(c.type, ffParams);

      switch (parB.iVal[0] * parC.iVal[0]) {
      case 9: 
        
        n = 3; 
        double vi = parB.dVal[PAR_V];
        double vj = parC.dVal[PAR_V];

        
        double viNew = 0;
        switch (b.atom.getElementNumber()) {
        case 8:
          viNew = 2.0;
          break;
        case 16:
        case 34:
        case 52:
        case 84:
          viNew = 6.8;
        }
        if (viNew != 0)
          switch (c.atom.getElementNumber()) {
          case 8:
            
            vi = viNew;
            vj = 2.0;
            n = 2; 
            break;
          case 16:
          case 34:
          case 52:
          case 84:
            
            vi = viNew;
            vj = 6.8;
            n = 2; 
          }
        V = 0.5 * KCAL_TO_KJ * Math.sqrt(vi * vj);
        break;
      case 4: 
        
        cosNPhi0 = 1; 
        n = 2; 
        V = 0.5 * KCAL_TO_KJ * 5.0
            * Math.sqrt(parB.dVal[PAR_U] * parC.dVal[PAR_U])
            * (1.0 + 4.18 * Math.log(bondOrder));
        break;
      case 6: 
        
        cosNPhi0 = 1;  
        n = 6; 
        
        
        boolean sp3C = (parC.iVal[0] == 3); 
        switch ((sp3C ? c : b).atom.getElementNumber()) {
        case 8:
        case 16:
        case 34:
        case 52:
        case 84:
          switch ((sp3C ? b : c).atom.getElementNumber()) {
          case 8:
          case 16:
          case 34:
          case 52:
          case 84:
            break;
          default:
            n = 2;
            cosNPhi0 = -1; 
          }
          break;
        }
        V = 0.5 * KCAL_TO_KJ;
      }

      if (Util.isNearZero(V)) 
        return;

      calc.addElement(new Object[] { new int[] { ia, ib, ic, id, n },
          new double[] { V, cosNPhi0 } });
    }

    
    double compute(Object[] dataIn) {
       
      getPointers(dataIn);
      
      double V = dData[0];
      double cosNPhi0 = dData[1];
      ia = iData[0];
      ib = iData[1];
      ic = iData[2];
      id = iData[3];
      int n = iData[4];
      
      if (gradients) {
        da.set(atoms[ia].coord);
        db.set(atoms[ib].coord);
        dc.set(atoms[ic].coord);
        dd.set(atoms[id].coord);
        theta = Util.restorativeForceAndTorsionAngleRadians(da, db, dc, dd);
        if (!Util.isFinite(theta))
          theta = 0.001 * DEG_TO_RAD;
      } else {
        theta = Util.getTorsionAngleRadians(atoms[ia].coord, atoms[ib].coord, 
            atoms[ic].coord, atoms[id].coord, v1, v2, v3);
      }

      energy = V * (1.0 - cosNPhi0 * Math.cos(theta * n));

      if (gradients) {
        dE = V * n * cosNPhi0 * Math.sin(n * theta);
        addForce(da, ia, dE);
        addForce(db, ib, dE);
        addForce(dc, ic, dE);
        addForce(dd, id, dE);
      }
      
      if (logging)
        appendLogData(getDebugLine(CALC_TORSION, this));
      
      return energy;
    }
  }

  final static double KCAL6 = 6.0 * KCAL_TO_KJ;
  final static double KCAL22 = 22.0 * KCAL_TO_KJ;
  final static double KCAL44 = 44.0 * KCAL_TO_KJ;
  
  class OOPCalc extends Calculation {

    void setData(Vector calc, int ib, int elemNo) {

      
      

      

      
      b = atoms[ib];
      int[] atomList = b.getBondedAtomIndexes();
      a = atoms[ia = atomList[0]];
      c = atoms[ic = atomList[1]];
      d = atoms[id = atomList[2]];

      double a0 = 1.0;
      double a1 = -1.0;
      double a2 = 0.0;
      double koop = KCAL6;
      switch (elemNo) {
      case 6: 
        if ((a.type + c.type + d.type).indexOf("O_2") == 0) {
          koop += KCAL44;
          break;
        }        break;
      case 7:
      case 8:
        break;
      default:
        koop = KCAL22;
        double phi = DEG_TO_RAD;
        switch (elemNo) {
        case 15: 
          phi *= 84.4339;
          break;
        case 33: 
          phi *= 86.9735;
          break;
        case 51: 
          phi *= 87.7047;
          break;
        case 83: 
          phi *= 90.0;
          break;
        }
        double cosPhi = Math.cos(phi);
        a0 = cosPhi * cosPhi;
        a1 = -2.0 * cosPhi;
        a2 = 1.0;
        
        
        
        
        
        
      }

      koop /= 3.0;

      
      calc.addElement(new Object[] { new int[] { ia, ib, ic, id },
          new double[] { koop, a0, a1, a2, koop * 10 } });

      
      calc.addElement(new Object[] { new int[] { ic, ib, id, ia },
          new double[] { koop, a0, a1, a2, koop * 10 } });

      
      calc.addElement(new Object[] { new int[] { id, ib, ia, ic },
          new double[] { koop, a0, a1, a2, koop * 10 } });
    }

    double compute(Object[] dataIn) {

      getPointers(dataIn);
      double koop = (isPreliminary ? dData[4] : dData[0]);
      double a0 = dData[1];
      double a1 = dData[2];
      double a2 = dData[3];
      ia = iData[0];
      ib = iData[1];
      ic = iData[2];
      id = iData[3];

      da.set(atoms[ia].coord);
      db.set(atoms[ib].coord);
      dc.set(atoms[ic].coord);
      dd.set(atoms[id].coord);

      if (gradients) {
        theta = Util.restorativeForceAndOutOfPlaneAngleRadians(da, db, dc, dd, v1, v2, v3);
      } else {
        
          
          
        theta = Util.pointPlaneAngleRadians(da, db, dc, dd, v1, v2, v3);
      }

      if (!Util.isFinite(theta))
        theta = 0.0; 

      
      
      

      double cosTheta = Math.cos(theta);
      energy = koop * (a0 + a1 * cosTheta + a2 * cosTheta * cosTheta);

      
        

      if (gradients) {
        
        
        dE = koop
            * (a1 * Math.sin(theta) + a2 * 2.0 * Math.sin(theta) * cosTheta);
        addForce(da, ia, dE);
        addForce(db, ib, dE);
        addForce(dc, ic, dE);
        addForce(dd, id, dE);
      }

      if (logging)
        appendLogData(getDebugLine(CALC_OOP, this));

      return energy;
    }

  }

  abstract class PairCalc extends Calculation {
   
    abstract void setData(Vector calc, int ia, int ib);

  }
  
  class VDWCalc extends PairCalc {
    
    void setData(Vector calc, int ia, int ib) {
      a = atoms[ia];
      b = atoms[ib];
      
      FFParam parA = getParameter(a.type, ffParams);
      FFParam parB = getParameter(b.type, ffParams);

      double Xa = parA.dVal[PAR_X];
      double Da = parA.dVal[PAR_D];
      double Xb = parB.dVal[PAR_X];
      double Db = parB.dVal[PAR_D];

      
      
      double Dab = KCAL_TO_KJ * Math.sqrt(Da * Db);

      
      
      
      

      
      double Xab = Math.sqrt(Xa * Xb);
      calc.addElement(new Object[] {
          new int[] { ia, ib },
          new double[] { Xab, Dab } });
    }

    double compute(Object[] dataIn) {

      getPointers(dataIn);
      double Xab = dData[0];
      double Dab = dData[1];
      ia = iData[0];
      ib = iData[1];
      
      if (gradients) {
        da.set(atoms[ia].coord);
        db.set(atoms[ib].coord);
        rab = Util.restorativeForceAndDistance(da, db, dc);
      } else
        rab = Math.sqrt(Util.distance2(atoms[ia].coord, atoms[ib].coord));

      if (Util.isNearZero(rab, 1.0e-3))
        rab = 1.0e-3;

      
      
      
      
      double term = Xab / rab;
      double term6 = term * term * term;
      term6 *= term6;
      energy = Dab * term6 * (term6 - 2.0);

      if (gradients) {
        dE = Dab * 12.0 * (1.0 - term6) * term6 * term / Xab; 
        addForce(da, ia, dE);
        addForce(db, ib, dE);
      }
      
      if (logging)
        appendLogData(getDebugLine(CALC_VDW, this));
      
      return energy;
    }
  } 

  final static double KCAL332 = KCAL_TO_KJ * 332.0637;
  
  class ESCalc extends PairCalc {

    void setData(Vector calc, int ia, int ib) {
      a = atoms[ia];
      b = atoms[ib];
      double qq = KCAL332 * partialCharges[ia]
          * partialCharges[ib];
      if (qq != 0)
        calc.addElement(new Object[] {
            new int[] { ia, ib },
            new double[] { qq } });
    }

    double compute(Object[] dataIn) {
      
      getPointers(dataIn);
      double qq = dData[0];      
      ia = iData[0];
      ib = iData[1];
      
      if (gradients) {
        da.set(atoms[ia].coord);
        db.set(atoms[ib].coord);
        rab = Util.restorativeForceAndDistance(da, db, dc);
      } else
        rab = Math.sqrt(Util.distance2(atoms[ia].coord, atoms[ib].coord));

      if (Util.isNearZero(rab, 1.0e-3))
        rab = 1.0e-3;

      energy = qq / rab;

      if (gradients) {
        dE = -qq / (rab * rab);
        addForce(da, ia, dE);
        addForce(db, ib, dE);
      }
      
      if (logging)
        appendLogData(getDebugLine(CALC_ES, this));
      
      return energy;
    }
  }

  
  
  
  String getAtomList(String title) {
    String trailer =
          "----------------------------------------"
          + "-------------------------------------------------------\n";  
    StringBuffer sb = new StringBuffer();
    sb.append("\n" + title + "\n\n"
        + " ATOM    X        Y        Z    TYPE     GRADX    GRADY    GRADZ  "
        + "---------BONDED ATOMS--------\n"
        + trailer);
    for (int i = 0; i < atomCount; i++) {
      MinAtom atom = atoms[i];
      int[] others = atom.getBondedAtomIndexes();
      int[] iVal = new int[others.length + 1];
      iVal[0] = atom.atom.getAtomNumber();
      String s = "   ";
      for (int j = 0; j < others.length; j++) {
        s += " %3d";
        iVal[j + 1] = atoms[others[j]].atom.getAtomNumber();
      }
      sb.append(TextFormat.sprintf("%3d %8.3f %8.3f %8.3f  %-5s %8.3f %8.3f %8.3f" + s + "\n", 
          new Object[] { atom.type,
          new float[] { (float) atom.coord[0], (float) atom.coord[1],
            (float) atom.coord[2], (float) atom.force[0], (float) atom.force[1],
            (float) atom.force[2] }, iVal}));
    }
    sb.append(trailer + "\n\n");
    return sb.toString();
  }

  String getDebugHeader(int iType) {
    switch (iType){
    case -1:
      return  "Universal Force Field -- " +
          "Rappe, A. K., et. al.; J. Am. Chem. Soc. (1992) 114(25) p. 10024-10035\n";
    case CALC_DISTANCE:
      return
           "\nB O N D   S T R E T C H I N G (" + bondCount + " bonds)\n\n"
          +"  ATOMS  ATOM TYPES   BOND    BOND       IDEAL      FORCE\n"
          +"  I   J   I     J     TYPE   LENGTH     LENGTH    CONSTANT      DELTA     ENERGY\n"
          +"--------------------------------------------------------------------------------";
    case CALC_ANGLE:
      return 
           "\nA N G L E   B E N D I N G (" + angles.length + " angles)\n\n"
          +"    ATOMS      ATOM TYPES        VALENCE    IDEAL        FORCE\n"
          +"  I   J   K   I     J     K       ANGLE     ANGLE      CONSTANT     ENERGY\n"
          +"--------------------------------------------------------------------------";
    case CALC_TORSION:
      return 
           "\nT O R S I O N A L (" + torsions.length + " torsions)\n\n"
          +"      ATOMS           ATOM TYPES             FORCE      TORSION\n"
          +"  I   J   K   L   I     J     K     L       CONSTANT     ANGLE        ENERGY\n"
          +"----------------------------------------------------------------------------";
    case CALC_OOP:
      return 
           "\nO U T - O F - P L A N E   B E N D I N G\n\n"
          +"      ATOMS           ATOM TYPES             OOP        FORCE \n"
          +"  I   J   K   L   I     J     K     L       ANGLE     CONSTANT      ENERGY\n"
          +"--------------------------------------------------------------------------";
    case CALC_VDW:
      return 
           "\nV A N   D E R   W A A L S\n\n"
          +"  ATOMS  ATOM TYPES\n"
          +"  I   J   I     J      Rij       kij     ENERGY\n"
          +"-----------------------------------------------";
    case CALC_ES:
      return 
          "\nE L E C T R O S T A T I C   I N T E R A C T I O N S\n\n"
          +"  ATOMS  ATOM TYPES            QiQj\n"
          +"  I   J   I     J      Rij    *332.17    ENERGY\n"
          +"-----------------------------------------------";
    }
    return "";
  }

  String getDebugLine(int iType, Calculation c) {
    switch (iType) {
    case CALC_DISTANCE:
      return TextFormat.sprintf(
          "%3d %3d  %-5s %-5s  %4.2f%8.3f   %8.3f     %8.3f   %8.3f   %8.3f",
          new Object[] { atoms[c.ia].type, atoms[c.ib].type, 
          new float[] { (float)c.dData[2], (float)c.rab, 
              (float)c.dData[0], (float)c.dData[1], 
              (float)c.delta, (float)c.energy },
          new int[] { atoms[c.ia].atom.getAtomNumber(), atoms[c.ib].atom.getAtomNumber() }});
    case CALC_ANGLE:
      return TextFormat.sprintf(
          "%3d %3d %3d  %-5s %-5s %-5s  %8.3f  %8.3f     %8.3f   %8.3f", 
          new Object[] { atoms[c.ia].type, atoms[c.ib].type, 
              atoms[c.ic].type,
          new float[] { (float)(c.theta * RAD_TO_DEG), (float)c.dData[4] , 
              (float)c.dData[0], (float) c.energy },
          new int[] { atoms[c.ia].atom.getAtomNumber(), atoms[c.ib].atom.getAtomNumber(),
              atoms[c.ic].atom.getAtomNumber()} });
      case CALC_TORSION:
      return TextFormat.sprintf(
          "%3d %3d %3d %3d  %-5s %-5s %-5s %-5s  %8.3f     %8.3f     %8.3f", 
          new Object[] { atoms[c.ia].type, atoms[c.ib].type, 
              atoms[c.ic].type, atoms[c.id].type,
          new float[] { (float) c.dData[0], 
              (float) (c.theta * RAD_TO_DEG), (float) c.energy },
          new int[] { atoms[c.ia].atom.getAtomNumber(), atoms[c.ib].atom.getAtomNumber(),
              atoms[c.ic].atom.getAtomNumber(), atoms[c.id].atom.getAtomNumber() } });
    case CALC_OOP:
      return TextFormat.sprintf("" +
          "%3d %3d %3d %3d  %-5s %-5s %-5s %-5s  %8.3f   %8.3f     %8.3f",
          new Object[] { atoms[c.ia].type, atoms[c.ib].type, 
              atoms[c.ic].type, atoms[c.id].type,
          new float[] { (float)(c.theta * RAD_TO_DEG), 
              (float)c.dData[0], (float) c.energy },
          new int[] { atoms[c.ia].atom.getAtomNumber(), atoms[c.ib].atom.getAtomNumber(),
              atoms[c.ic].atom.getAtomNumber(), atoms[c.id].atom.getAtomNumber() } });
    case CALC_VDW:
      return TextFormat.sprintf("%3d %3d  %-5s %-5s %6.3f  %8.3f  %8.3f", 
          new Object[] { atoms[c.iData[0]].type, atoms[c.iData[1]].type,
          new float[] { (float)c.rab, (float)c.dData[0], (float)c.energy},
          new int[] { atoms[c.ia].atom.getAtomNumber(), atoms[c.ib].atom.getAtomNumber() } });
    case CALC_ES:
      return TextFormat.sprintf("%3d %3d  %-5s %-5s %6.3f  %8.3f  %8.3f", 
          new Object[] { atoms[c.iData[0]].type, atoms[c.iData[1]].type,
          new float[] { (float)c.rab, (float)c.dData[0], (float)c.energy },
          new int[] { atoms[c.ia].atom.getAtomNumber(), atoms[c.ib].atom.getAtomNumber() } });
    }
    return "";
  }

  String getDebugFooter(int iType, double energy) {
    String s = "";
    switch (iType){
    case CALC_DISTANCE:
      s = "BOND STRETCHING";
      break;
    case CALC_ANGLE:
      s = "ANGLE BENDING";
      break;
    case CALC_TORSION:
      s = "TORSIONAL";
      break;
    case CALC_OOP:
      s = "OUT-OF-PLANE BENDING";
      break;
    case CALC_VDW:
      s = "VAN DER WAALS";
      break;
    case CALC_ES:
      s = "ELECTROSTATIC ENERGY";
      break;
    }
    return TextFormat.sprintf("\n     TOTAL %s ENERGY = %8.3f %s\n", 
        new Object[] { s, getUnit(), new Float(energy) });
  }

}

