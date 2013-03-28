

package org.jmol.minimize;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

import org.jmol.api.MinimizerInterface;
import org.jmol.i18n.GT;
import org.jmol.minimize.forcefield.ForceField;
import org.jmol.modelset.Atom;
import org.jmol.modelset.AtomCollection;
import org.jmol.modelset.Bond;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;

import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.viewer.JmolConstants;
import org.jmol.script.Token;
import org.jmol.viewer.Viewer;

public class Minimizer implements MinimizerInterface {

  public Viewer viewer;
  public Atom[] atoms;
  public MinAtom[] minAtoms;
  public MinBond[] minBonds;
  public BitSet bsMinFixed;
  private int atomCount;
  private int bondCount;
  private int atomCountFull;
  private int[] atomMap; 
  private boolean addHydrogens;
 
  public int[][] angles;
  public int[][] torsions;
  public double[] partialCharges;
  
  private int steps = 50;
  private double crit = 1e-3;

  private static Vector atomTypes;
  private ForceField pFF;
  private String ff = "UFF";
  private BitSet bsTaint, bsSelected, bsAtoms;
  private BitSet bsFixed;
  public Vector constraints;
  
  public Minimizer() {
  }

  public void setProperty(String propertyName, Object value) {
    
    if (propertyName.equals("cancel")) {
      stopMinimization(false);
      return;
    }
    if (propertyName.equals("clear")) {
      stopMinimization(false);
      clear();
      return;
    }
    if (propertyName.equals("constraint")) {
      addConstraint((Object[]) value);
      return;
    }
    if (propertyName.equals("fixed")) {
      bsFixed = (BitSet) value;
      return;
    }
    if (propertyName.equals("stop")) {
      stopMinimization(true);
      return;
    }
    if (propertyName.equals("viewer")) {
      viewer = (Viewer) value;
      return;
    }
  }

  public Object getProperty(String propertyName, int param) {
    if (propertyName.equals("log")) {
      return (pFF == null ? "" : pFF.getLogData());
    }
    return null;
  }
  
  private Hashtable constraintMap;

  private void addConstraint(Object[] c) {
    if (c == null)
      return;
    int[] atoms = (int[]) c[0];
    int nAtoms = atoms[0];
    if (nAtoms == 0) {
      constraints = null;
      return;
    }
    if (constraints == null) {
      constraints = new Vector();
      constraintMap = new Hashtable();
    }
    if (atoms[1] > atoms[nAtoms]) {
        ArrayUtil.swap(atoms, 1, nAtoms);
        if (nAtoms == 4)
          ArrayUtil.swap(atoms, 2, 3);
    }
    String id = Escape.escape(atoms);
    Object[] c1 = (Object[]) constraintMap.get(id);
    if (c1 != null) {
      c1[2] = c[2]; 
      return;
    }
    constraintMap.put(id, c);
    constraints.addElement(c);
  }
    
  private void clear() {
    setMinimizationOn(false);
    addHydrogens = false;
    atomCount = 0;
    bondCount = 0;
    atoms = null;
    viewer = null;
    minAtoms = null;
    minBonds = null;
    angles = null;
    torsions = null;
    partialCharges = null;
    coordSaved = null;
    atomMap = null;
    bsTaint = null;
    bsAtoms = null;
    bsFixed = null;
    bsMinFixed = null;
    bsSelected = null;
    constraints = null;
    constraintMap = null;
    pFF = null;
  }
  
  public boolean minimize(int steps, double crit, BitSet bsSelected, boolean addHydrogen) {
    addHydrogens = addHydrogen;
    Object val;
    if (steps == Integer.MAX_VALUE) {
      val = viewer.getParameter("minimizationSteps");
      if (val != null && val instanceof Integer)
        steps = ((Integer) val).intValue();
    }
    this.steps = steps;

    if (crit <= 0) {
      val = viewer.getParameter("minimizationCriterion");
      if (val != null && val instanceof Float)
        crit = ((Float) val).floatValue();
    }
    this.crit = Math.max(crit, 0.0001);

    if (minimizationOn)
      return false;

    Logger.info("minimize: initializing (steps = " + steps + " criterion = "
        + crit + ") ...");

    getForceField();
    if (pFF == null) {
      Logger.error(GT._("Could not get class for force field {0}", ff));
      return false;
    }
    if (atoms == null) {
      atomCountFull = viewer.getAtomCount();
      atoms = viewer.getModelSet().getAtoms();
    }
    bsAtoms = BitSetUtil.copy(bsSelected);
    atomCount = BitSetUtil.cardinalityOf(bsAtoms);
    if (atomCount == 0) {
      Logger.error(GT._("No atoms selected -- nothing to do!"));
      return false;
    }

    if (!BitSetUtil.areEqual(bsSelected, this.bsSelected)
        && !setupMinimization()) {
      clear();
      return false;
    }
    setAtomPositions();
    this.bsSelected = bsSelected;

    if (constraints != null) {
      for (int i = constraints.size(); --i >= 0;) {
        Object[] constraint = (Object[]) constraints.elementAt(i);
        int[] aList = (int[]) constraint[0];
        int[] minList = (int[]) constraint[1];
        int nAtoms = aList[0] = Math.abs(aList[0]);
        for (int j = 1; j <= nAtoms; j++) {
          if (steps <= 0 || !bsAtoms.get(aList[j])) {
            aList[0] = -nAtoms; 
            break;
          }
          minList[j - 1] = atomMap[aList[j]];
        }
      }
    }

    pFF.setConstraints(this);

    

    if (steps > 0 && !addHydrogens && !viewer.useMinimizationThread())
      minimizeWithoutThread();
    else if (steps > 0)
      setMinimizationOn(true);
    else
      getEnergyOnly();
    return true;
  }

  private boolean setupMinimization() {

    

    atomMap = new int[atomCountFull];
    minAtoms = new MinAtom[atomCount];
    int elemnoMax = 0;
    BitSet bsElements = new BitSet();
    for (int i = bsAtoms.nextSetBit(0), pt = 0; i >= 0; i = bsAtoms
        .nextSetBit(i + 1), pt++) {
      Atom atom = atoms[i];
      atomMap[i] = pt;
      int atomicNo = atoms[i].getElementNumber();
      elemnoMax = Math.max(elemnoMax, atomicNo);
      bsElements.set(atomicNo);
      minAtoms[pt] = new MinAtom(pt, atom, new double[] { atom.x, atom.y,
          atom.z }, null);
    }

    Logger.info(GT._("{0} atoms will be minimized.", "" + atomCount));
    Logger.info("minimize: creating bonds...");

    
    Vector bondInfo = new Vector();
    bondCount = 0;
    for (int i = bsAtoms.nextSetBit(0); i >= 0; i = bsAtoms.nextSetBit(i + 1)) {
      Bond[] bonds = atoms[i].getBonds();
      if (bonds != null)
        for (int j = 0; j < bonds.length; j++) {
          int i2 = bonds[j].getOtherAtom(atoms[i]).getIndex();
          if (i2 > i && bsAtoms.get(i2)) {
            int bondOrder = bonds[j].getOrder();
            switch (bondOrder) {
            case 1:
            case 2:
            case 3:
              break;
            case JmolConstants.BOND_AROMATIC:
              bondOrder = 5;
              break;
            default:
              bondOrder = 1;
            }
            bondCount++;
            bondInfo
                .addElement(new int[] { atomMap[i], atomMap[i2], bondOrder });
          }
        }
    }
    int[] atomIndexes;

    minBonds = new MinBond[bondCount];
    for (int i = 0; i < bondCount; i++) {
      MinBond bond = minBonds[i] = new MinBond(atomIndexes = (int[]) bondInfo
          .elementAt(i), false, false);
      int atom1 = atomIndexes[0];
      int atom2 = atomIndexes[1];
      minAtoms[atom1].bonds.addElement(bond);
      minAtoms[atom2].bonds.addElement(bond);
      minAtoms[atom1].nBonds++;
      minAtoms[atom2].nBonds++;
    }

    for (int i = 0; i < atomCount; i++)
      atomIndexes = minAtoms[i].getBondedAtomIndexes();

    

    Logger.info("minimize: setting atom types...");

    if (atomTypes == null)
      atomTypes = getAtomTypes();
    if (atomTypes == null)
      return false;
    int nElements = atomTypes.size();
    bsElements.clear(0);
    for (int i = 0; i < nElements; i++) {
      String[] data = ((String[]) atomTypes.get(i));
      String smarts = data[0];
      if (smarts == null)
        continue;
      BitSet search = getSearch(smarts, elemnoMax, bsElements);
      
      
      
      if (bsElements.get(0))
        bsElements.clear(0);
      else if (search == null)
        break;
      else
        for (int j = 0, pt = 0; j < atomCountFull; j++)
          if (bsAtoms.get(j)) {
            if (search.get(j)) {
              minAtoms[pt].type = data[1];
              
            }
            pt++;
          }
    }

    

    Logger.info("minimize: getting angles...");
    getAngles();
    Logger.info("minimize: getting torsions...");
    getTorsions();

    pFF.setModel(this);

    if (!pFF.setup()) {
      Logger.error(GT._("could not setup force field {0}", ff));
      return false;
    }

    if (steps > 0) {
      bsTaint = BitSetUtil.copy(bsAtoms);
      BitSetUtil.andNot(bsTaint, bsFixed);
      viewer.setTaintedAtoms(bsTaint, AtomCollection.TAINT_COORD);
    }
    return true;

  }
  
  private void setAtomPositions() {
    for (int i = 0; i < atomCount; i++)
      minAtoms[i].set();
    bsMinFixed = null;
    if (bsFixed != null) {
      bsMinFixed = new BitSet();
      for (int i = bsAtoms.nextSetBit(0), pt = 0; i >= 0; i = bsAtoms
          .nextSetBit(i + 1), pt++)
        if (bsFixed.get(i))
          bsMinFixed.set(pt);
    }
  }
  
  
  
  final static int TOKEN_ELEMENT_ONLY = 0;
  final static int TOKEN_ELEMENT_CHARGED = 1;
  final static int TOKEN_ELEMENT_CONNECTED = 2;
  final static int TOKEN_ELEMENT_AROMATIC = 3;
  final static int TOKEN_ELEMENT_SP = 4;
  final static int TOKEN_ELEMENT_SP2 = 5;
  
  
  final static int PT_ELEMENT = 2;
  final static int PT_CHARGE = 5;
  final static int PT_CONNECT = 6;
  
  final static Token[][] tokenTypes = new Token[][] {
           new Token[]{
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0), 
       Token.tokenExpressionEnd},
           new Token[]{
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0), 
       Token.tokenAnd, 
       new Token(Token.opEQ, Token.formalcharge),
       Token.intToken(0), 
       Token.tokenExpressionEnd},
           new Token[]{
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0)  ,  
       Token.tokenAnd, 
       new Token(Token.connected, "connected"),
       Token.tokenLeftParen,
       Token.intToken(0),   
       Token.tokenRightParen,
       Token.tokenExpressionEnd},
           new Token[]{
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0), 
       Token.tokenAnd, 
       new Token(Token.isaromatic, "isaromatic"),
       Token.tokenExpressionEnd},
           new Token[]{ 
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0)  ,  
       Token.tokenAnd, 
       Token.tokenLeftParen,
       new Token(Token.connected, "connected"),
       Token.tokenLeftParen,
       Token.intToken(1),
       Token.tokenComma,
       new Token(Token.string, "triple"),
       Token.tokenRightParen,
       Token.tokenOr,
       new Token(Token.connected, "connected"),
       Token.tokenLeftParen,
       Token.intToken(2),
       Token.tokenComma,
       new Token(Token.string, "double"),
       Token.tokenRightParen,
       Token.tokenRightParen,
       Token.tokenExpressionEnd},
           new Token[]{  
       Token.tokenExpressionBegin,
       new Token(Token.opEQ, Token.elemno), 
       Token.intToken(0)  ,  
       Token.tokenAnd, 
       new Token(Token.connected, "connected"),
       Token.tokenLeftParen,
       Token.intToken(1),
       Token.tokenComma,
       new Token(Token.string, "double"),
       Token.tokenRightParen,
       Token.tokenExpressionEnd},
  };
  
  
  private BitSet getSearch(String smarts, int elemnoMax, BitSet bsElements) {
    

    Token[] search = null;

    int len = smarts.length();
    search = tokenTypes[TOKEN_ELEMENT_ONLY];
    int n = smarts.charAt(len - 2) - '0';
    int elemNo = 0;
    if (n >= 10)
      n = 0;
    if (smarts.charAt(1) == '#') {
      elemNo = Parser.parseInt(smarts.substring(2, len - 1));
    } else {
      String s = smarts.substring(1, (n > 0 ? len - 3 : len - 1));
      if (s.equals(s.toLowerCase())) {
        s = s.toUpperCase();
        search = tokenTypes[TOKEN_ELEMENT_AROMATIC];
      }
      elemNo = JmolConstants.elementNumberFromSymbol(s);
    }
    if (elemNo > elemnoMax)
      return null;
    if (!bsElements.get(elemNo)) {
      bsElements.set(0);
      return null;
    }
    switch (smarts.charAt(len - 3)) {
    case 'D':
      search = tokenTypes[TOKEN_ELEMENT_CONNECTED];
      search[PT_CONNECT].intValue = n;
      break;
    case '^':
      search = tokenTypes[TOKEN_ELEMENT_SP + (n - 1)];
      break;
    case '+':
      search = tokenTypes[TOKEN_ELEMENT_CHARGED];
      search[PT_CHARGE].intValue = n;
      break;
    }
    search[PT_ELEMENT].intValue = elemNo;
    Object v = viewer.evaluateExpression(search);
    
    return (v instanceof BitSet ? (BitSet) v : null);
  }
  
  public void getAngles() {

    Vector vAngles = new Vector();

    for (int ib = 0; ib < atomCount; ib++) {
      MinAtom atomB = minAtoms[ib];
      int n = atomB.nBonds;
      if (n < 2)
        continue;
      
      int[] atomList = atomB.getBondedAtomIndexes();
      for (int ia = 0; ia < n - 1; ia++)
        for (int ic = ia + 1; ic < n; ic++) {
          
          vAngles.addElement(new int[] { atomList[ia], ib, atomList[ic] });
        }
    }
    
    angles = new int[vAngles.size()][];
    for (int i = vAngles.size(); --i >= 0; )
      angles[i] = (int[]) vAngles.elementAt(i);
    Logger.info(angles.length + " angles");
  }

  public void getTorsions() {

    Vector vTorsions = new Vector();

    
    
    
    for (int i = angles.length; --i >= 0;) {
      int[] angle = angles[i];
      int ia = angle[0];
      int ib = angle[1];
      int ic = angle[2];
      if (ic > ib && minAtoms[ic].nBonds != 1) {
        int[] atomList = minAtoms[ic].getBondedAtomIndexes();
        for (int j = 0; j < atomList.length; j++) {
          int id = atomList[j];
          if (id != ia && id != ib) {
            vTorsions.addElement(new int[] { ia, ib, ic, id });

          }
        }
      }
      if (ia > ib && minAtoms[ia].nBonds != 1) {
        int[] atomList = minAtoms[ia].getBondedAtomIndexes();
        for (int j = 0; j < atomList.length; j++) {
          int id = atomList[j];
          if (id != ic && id != ib) {
            vTorsions.addElement(new int[] { ic, ib, ia, id });
          }
        }
      }
      
    }

    torsions = new int[vTorsions.size()][];
    for (int i = vTorsions.size(); --i >= 0;)
      torsions[i] = (int[]) vTorsions.elementAt(i);

    Logger.info(torsions.length + " torsions");

  }

  
  
  
  
  
 
  public ForceField getForceField() {
    if (pFF == null) {
      try {
        String className = getClass().getName();
        className = className.substring(0, className.lastIndexOf(".")) 
        + ".forcefield.ForceField" + ff;
        Logger.info( "minimize: using " + className);
        pFF = (ForceField) Class.forName(className).newInstance();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
    System.out.println("minimize: forcefield = " + pFF);
    return pFF;
  }
  
  public Vector getAtomTypes() {
    getForceField();
    return (pFF == null ? null : pFF.getAtomTypes());
  }
  
  

  boolean minimizationOn;

  private MinimizationThread minimizationThread;

  private void setMinimizationOn(boolean minimizationOn) {
    
    this.minimizationOn = minimizationOn;
    if (!minimizationOn) {
      if (minimizationThread != null) {
        
        minimizationThread = null;
      }
      return;
    }
    if (minimizationThread == null) {
      minimizationThread = new MinimizationThread();
      minimizationThread.start();
    }
  }

  private void getEnergyOnly() {
    if (pFF == null || viewer == null)
      return;
    pFF.steepestDescentInitialize(steps, crit);      
    viewer.setFloatProperty("_minimizationEnergyDiff", 0);
    viewer.setFloatProperty("_minimizationEnergy", (float) pFF.getEnergy());
    viewer.setStringProperty("_minimizationStatus", "calculate");
    viewer.notifyMinimizationStatus();
  }
  
  public boolean startMinimization() {
    try {
      Logger.info("minimizer: startMinimization");
      viewer.setIntProperty("_minimizationStep", 0);
      viewer.setStringProperty("_minimizationStatus", "starting");
      viewer.setFloatProperty("_minimizationEnergy", 0);
      viewer.setFloatProperty("_minimizationEnergyDiff", 0);
      viewer.notifyMinimizationStatus();
      viewer.saveCoordinates("minimize", bsTaint);
      pFF.steepestDescentInitialize(steps, crit);
      viewer.setFloatProperty("_minimizationEnergy", (float) pFF.getEnergy());
      saveCoordinates();
    } catch (Exception e) {
      System.out.println("minimization error viwer=" + viewer + " pFF = " + pFF);
      return false;
    }
    minimizationOn = true;
    return true;
  }

  boolean stepMinimization() {
    if (!minimizationOn)
      return false;
    boolean doRefresh = viewer.getBooleanProperty("minimizationRefresh");
    viewer.setStringProperty("_minimizationStatus", "running");
    boolean going = pFF.steepestDescentTakeNSteps(1);
    int currentStep = pFF.getCurrentStep();
    viewer.setIntProperty("_minimizationStep", currentStep);
    viewer.setFloatProperty("_minimizationEnergy", (float) pFF.getEnergy());
    viewer.setFloatProperty("_minimizationEnergyDiff", (float) pFF.getEnergyDiff());
    viewer.notifyMinimizationStatus();
    if (doRefresh) {
      updateAtomXYZ();
      viewer.refresh(3, "minimization step " + currentStep);
    }
    return going;
  }

  void endMinimization() {
    updateAtomXYZ();
    setMinimizationOn(false);
    boolean failed = pFF.detectExplosion();
    if (failed)
      restoreCoordinates();
    viewer.setIntProperty("_minimizationStep", pFF.getCurrentStep());
    viewer.setFloatProperty("_minimizationEnergy", (float) pFF.getEnergy());
    viewer.setStringProperty("_minimizationStatus", (failed ? "failed" : "done"));
    viewer.notifyMinimizationStatus();
    viewer.refresh(3, "Minimizer:done" + (failed ? " EXPLODED" : "OK"));
    Logger.info("minimizer: endMinimization");
    if (addHydrogens)
      viewer.addHydrogens(bsSelected);
}

  double[][] coordSaved;
  
  private void saveCoordinates() {
    if (coordSaved == null)
      coordSaved = new double[atomCount][3];
    for (int i = 0; i < atomCount; i++) 
      for (int j = 0; j < 3; j++)
        coordSaved[i][j] = minAtoms[i].coord[j];
  }
  
  private void restoreCoordinates() {
    if (coordSaved == null)
      return;
    for (int i = 0; i < atomCount; i++) 
      for (int j = 0; j < 3; j++)
        minAtoms[i].coord[j] = coordSaved[i][j];
    updateAtomXYZ();
  }

  private void stopMinimization(boolean coordAreOK) {
    if (!minimizationOn)
      return;
    setMinimizationOn(false);
    if (coordAreOK)
      endMinimization();
    else
      restoreCoordinates();
  }
  
  void updateAtomXYZ() {
    if (steps <= 0)
      return;
    for (int i = 0; i < atomCount; i++) {
      MinAtom minAtom = minAtoms[i];
      Atom atom = minAtom.atom;
      atom.x = (float) minAtom.coord[0];
      atom.y = (float) minAtom.coord[1];
      atom.z = (float) minAtom.coord[2];
    }
    viewer.refreshMeasures();
  }

  private void minimizeWithoutThread() {
    
    if (!startMinimization())
      return;
    while (stepMinimization()) {
    }
    endMinimization();
  }
  
  class MinimizationThread extends Thread implements Runnable {
    
    MinimizationThread() {
      this.setName("MinimizationThread");
    }
    
    public void run() {
      long startTime = System.currentTimeMillis();
      long lastRepaintTime = startTime;
      
      
      if (!startMinimization())
          return;
      try {
        do {
          long currentTime = System.currentTimeMillis();
          int elapsed = (int) (currentTime - lastRepaintTime);
          int sleepTime = 33 - elapsed;
          if (sleepTime > 0)
            Thread.sleep(sleepTime);
          lastRepaintTime = currentTime = System.currentTimeMillis();
          if (!stepMinimization())
            endMinimization();            
          elapsed = (int) (currentTime - startTime);
        } while (minimizationOn && !isInterrupted());
      } catch (Exception e) {
        if (minimizationOn)
          System.out.println(" minimization thread interrupted");
      }
    }
  }
}
