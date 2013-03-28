

package org.jmol.smiles;

import org.jmol.util.Logger;

import junit.framework.TestCase;

public class TestSmilesParser extends TestCase {

  public TestSmilesParser(String arg0) {
    super(arg0);
  }

  
  public void testChapter1_01() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomH = molecule.createAtom();
    atomH.setCharge(1);
    atomH.setSymbol("H");
    checkMolecule("[H+]", molecule);
  }
  public void testChapter1_02() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC = molecule.createAtom();
    atomC.setSymbol("C");
    addHydrogen(molecule, atomC);
    addHydrogen(molecule, atomC);
    addHydrogen(molecule, atomC);
    addHydrogen(molecule, atomC);
    checkMolecule("C", molecule);
  }
  public void testChapter1_03() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO = molecule.createAtom();
    atomO.setSymbol("O");
    addHydrogen(molecule, atomO);
    addHydrogen(molecule, atomO);
    checkMolecule("O", molecule);
  }
  public void testChapter1_04() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO = molecule.createAtom();
    atomO.setCharge(1);
    atomO.setSymbol("O");
    addHydrogen(molecule, atomO);
    addHydrogen(molecule, atomO);
    addHydrogen(molecule, atomO);
    checkMolecule("[OH3+]", molecule);
  }
  public void testChapter1_05() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomH1 = molecule.createAtom();
    atomH1.setAtomicMass(2);
    atomH1.setSymbol("H");
    SmilesAtom atomO = molecule.createAtom();
    atomO.setSymbol("O");
    molecule.createBond(atomH1, atomO, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomH2 = molecule.createAtom();
    atomH2.setAtomicMass(2);
    atomH2.setSymbol("H");
    molecule.createBond(atomO, atomH2, SmilesBond.TYPE_SINGLE);
    checkMolecule("[2H]O[2H]", molecule);
  }
  public void testChapter1_06() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomAu = molecule.createAtom();
    atomAu.setSymbol("Au");
    checkMolecule("[Au]", molecule);
  }
  public void testChapter1_07() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomO = molecule.createAtom();
    atomO.setSymbol("O");
    molecule.createBond(atomC2, atomO, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomO);
    checkMolecule("CCO", molecule);
  }
  public void testChapter1_08() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO1 = molecule.createAtom();
    atomO1.setSymbol("O");
    SmilesAtom atomC = molecule.createAtom();
    atomC.setSymbol("C");
    molecule.createBond(atomO1, atomC, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO2 = molecule.createAtom();
    atomO2.setSymbol("O");
    molecule.createBond(atomC, atomO2, SmilesBond.TYPE_DOUBLE);
    checkMolecule("O=C=O", molecule);
  }
  public void testChapter1_09() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC = molecule.createAtom();
    atomC.setSymbol("C");
    SmilesAtom atomN = molecule.createAtom();
    atomN.setSymbol("N");
    molecule.createBond(atomC, atomN, SmilesBond.TYPE_TRIPLE);
    addHydrogen(molecule, atomC);
    checkMolecule("C#N", molecule);
  }
  public void testChapter1_10() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomO1 = molecule.createAtom();
    atomO1.setSymbol("O");
    molecule.createBond(atomC2, atomO1, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO2 = molecule.createAtom();
    atomO2.setSymbol("O");
    molecule.createBond(atomC2, atomO2, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomO2);
    checkMolecule("CC(=O)O", molecule);
  }
  public void testChapter1_11() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomC1, null, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC6 = molecule.createAtom();
    atomC6.setSymbol("C");
    molecule.createBond(atomC5, atomC6, SmilesBond.TYPE_SINGLE);
    atomC1.getBond(0).setAtom2(atomC6);
    atomC6.addBond(atomC1.getBond(0));
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC6);
    addHydrogen(molecule, atomC6);
    checkMolecule("C1CCCCC1", molecule);
  }
  public void testChapter1_12() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomC1, null, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    molecule.createBond(atomC3, null, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC6 = molecule.createAtom();
    atomC6.setSymbol("C");
    molecule.createBond(atomC5, atomC6, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC7 = molecule.createAtom();
    atomC7.setSymbol("C");
    molecule.createBond(atomC6, atomC7, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC8 = molecule.createAtom();
    atomC8.setSymbol("C");
    molecule.createBond(atomC7, atomC8, SmilesBond.TYPE_SINGLE);
    atomC3.getBond(1).setAtom2(atomC8);
    atomC8.addBond(atomC3.getBond(1));
    SmilesAtom atomC9 = molecule.createAtom();
    atomC9.setSymbol("C");
    molecule.createBond(atomC8, atomC9, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC0 = molecule.createAtom();
    atomC0.setSymbol("C");
    molecule.createBond(atomC9, atomC0, SmilesBond.TYPE_SINGLE);
    atomC1.getBond(0).setAtom2(atomC0);
    atomC0.addBond(atomC1.getBond(0));
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC6);
    addHydrogen(molecule, atomC6);
    addHydrogen(molecule, atomC7);
    addHydrogen(molecule, atomC7);
    addHydrogen(molecule, atomC8);
    addHydrogen(molecule, atomC9);
    addHydrogen(molecule, atomC9);
    addHydrogen(molecule, atomC0);
    addHydrogen(molecule, atomC0);
    checkMolecule("C1CC2CCCCC2CC1", molecule);
  }
  public void testChapter1_13() {    
    
  }
  public void testChapter1_14() {    
    
  }
  public void testChapter1_15() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DIRECTIONAL_1);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_DIRECTIONAL_1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    checkMolecule("C/C=C/C", molecule);
  }
  public void testChapter1_16() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomN = molecule.createAtom();
    atomN.setSymbol("N");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setChiralClass("");
    atomC1.setChiralOrder(2);
    atomC1.setSymbol("C");
    molecule.createBond(atomN, atomC1, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC1, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomO1 = molecule.createAtom();
    atomO1.setSymbol("O");
    molecule.createBond(atomC3, atomO1, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO2 = molecule.createAtom();
    atomO2.setSymbol("O");
    molecule.createBond(atomC3, atomO2, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomN);
    addHydrogen(molecule, atomN);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomO2);
    checkMolecule("N[C@@H](C)C(=O)O", molecule);
  }
  public void testChapter1_17() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO1 = molecule.createAtom();
    atomO1.setSymbol("O");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setChiralClass("");
    atomC1.setChiralOrder(1);
    atomC1.setSymbol("C");
    molecule.createBond(atomO1, atomC1, SmilesBond.TYPE_SINGLE);
    molecule.createBond(atomC1, null, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC6 = molecule.createAtom();
    atomC6.setChiralClass("");
    atomC6.setChiralOrder(1);
    atomC6.setSymbol("C");
    molecule.createBond(atomC5, atomC6, SmilesBond.TYPE_SINGLE);
    atomC1.getBond(1).setAtom2(atomC6);
    atomC6.addBond(atomC1.getBond(1));
    SmilesAtom atomO2 = molecule.createAtom();
    atomO2.setSymbol("O");
    molecule.createBond(atomC6, atomO2, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomO1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC6);
    addHydrogen(molecule, atomO2);
    checkMolecule("O[C@H]1CCCC[C@H]1O", molecule);
  }
  
  
  public void testChapter2_01() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomS = molecule.createAtom();
    atomS.setSymbol("S");
    checkMolecule("[S]", molecule);
  }
  public void testChapter2_02() {    
    testChapter1_06();
  }
  public void testChapter2_03() {    
    testChapter1_02();
  }
  public void testChapter2_04() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomP = molecule.createAtom();
    atomP.setSymbol("P");
    addHydrogen(molecule, atomP);
    addHydrogen(molecule, atomP);
    addHydrogen(molecule, atomP);
    checkMolecule("P", molecule);
  }
  public void testChapter2_05() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomS = molecule.createAtom();
    atomS.setSymbol("S");
    addHydrogen(molecule, atomS);
    addHydrogen(molecule, atomS);
    checkMolecule("S", molecule);
  }
  public void testChapter2_06() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomCl = molecule.createAtom();
    atomCl.setSymbol("Cl");
    addHydrogen(molecule, atomCl);
    checkMolecule("Cl", molecule);
  }
  public void testChapter2_07() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO = molecule.createAtom();
    atomO.setCharge(-1);
    atomO.setSymbol("O");
    addHydrogen(molecule, atomO);
    checkMolecule("[OH-]", molecule);
  }
  public void testChapter2_08() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO = molecule.createAtom();
    atomO.setCharge(-1);
    atomO.setSymbol("O");
    addHydrogen(molecule, atomO);
    checkMolecule("[OH-1]", molecule);
  }
  public void testChapter2_09() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomFe = molecule.createAtom();
    atomFe.setCharge(2);
    atomFe.setSymbol("Fe");
    checkMolecule("[Fe+2]", molecule);
  }
  public void testChapter2_10() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomFe = molecule.createAtom();
    atomFe.setCharge(2);
    atomFe.setSymbol("Fe");
    checkMolecule("[Fe++]", molecule);
  }
  public void testChapter2_11() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomU = molecule.createAtom();
    atomU.setAtomicMass(235);
    atomU.setSymbol("U");
    checkMolecule("[235U]", molecule);
  }
  public void testChapter2_12() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atom = molecule.createAtom();
    atom.setCharge(2);
    atom.setSymbol("*");
    checkMolecule("[*+2]", molecule);
  }
  
  
  public void testChapter3_01() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    checkMolecule("CC", molecule);
  }
  public void testChapter3_02() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    checkMolecule("C-C", molecule);
  }
  public void testChapter3_03() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    checkMolecule("[CH3]-[CH3]", molecule);
  }
  public void testChapter3_04() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC = molecule.createAtom();
    atomC.setSymbol("C");
    SmilesAtom atomO = molecule.createAtom();
    atomO.setSymbol("O");
    molecule.createBond(atomC, atomO, SmilesBond.TYPE_DOUBLE);
    addHydrogen(molecule, atomC);
    addHydrogen(molecule, atomC);
    checkMolecule("C=O", molecule);
  }
  public void testChapter3_05() {    
    testChapter1_09();
  }
  public void testChapter3_06() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    checkMolecule("C=C", molecule);
  }
  public void testChapter3_07() {    
    
  }
  public void testChapter3_08() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_DOUBLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    checkMolecule("C=CC=C", molecule);
  }
  public void testChapter3_09() {    
    
  }
  
  
  public void testChapter4_01() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC2, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomO1 = molecule.createAtom();
    atomO1.setSymbol("O");
    molecule.createBond(atomC4, atomO1, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO2 = molecule.createAtom();
    atomO2.setSymbol("O");
    molecule.createBond(atomC4, atomO2, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomO2);
    checkMolecule("CC(C)C(=O)O", molecule);
  }
  public void testChapter4_02() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomF1 = molecule.createAtom();
    atomF1.setSymbol("F");
    SmilesAtom atomC = molecule.createAtom();
    atomC.setSymbol("C");
    molecule.createBond(atomF1, atomC, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomF2 = molecule.createAtom();
    atomF2.setSymbol("F");
    molecule.createBond(atomC, atomF2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomF3 = molecule.createAtom();
    atomF3.setSymbol("F");
    molecule.createBond(atomC, atomF3, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC);
    checkMolecule("FC(F)F", molecule);
  }
  public void testChapter4_03() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC = molecule.createAtom();
    atomC.setSymbol("C");
    SmilesAtom atomF1 = molecule.createAtom();
    atomF1.setSymbol("F");
    molecule.createBond(atomC, atomF1, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomF2 = molecule.createAtom();
    atomF2.setSymbol("F");
    molecule.createBond(atomC, atomF2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomF3 = molecule.createAtom();
    atomF3.setSymbol("F");
    molecule.createBond(atomC, atomF3, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC);
    checkMolecule("C(F)(F)F", molecule);
  }
  public void testChapter4_04() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO1 = molecule.createAtom();
    atomO1.setSymbol("O");
    SmilesAtom atomCl = molecule.createAtom();
    atomCl.setSymbol("Cl");
    molecule.createBond(atomO1, atomCl, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO2 = molecule.createAtom();
    atomO2.setSymbol("O");
    molecule.createBond(atomCl, atomO2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO3 = molecule.createAtom();
    atomO3.setSymbol("O");
    molecule.createBond(atomCl, atomO3, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO4 = molecule.createAtom();
    atomO4.setCharge(-1);
    atomO4.setSymbol("O");
    molecule.createBond(atomCl, atomO4, SmilesBond.TYPE_SINGLE);
    checkMolecule("O=Cl(=O)(=O)[O-]", molecule);
  }
  public void testChapter4_05() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomCl = molecule.createAtom();
    atomCl.setSymbol("Cl");
    SmilesAtom atomO1 = molecule.createAtom();
    atomO1.setSymbol("O");
    molecule.createBond(atomCl, atomO1, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO2 = molecule.createAtom();
    atomO2.setSymbol("O");
    molecule.createBond(atomCl, atomO2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO3 = molecule.createAtom();
    atomO3.setSymbol("O");
    molecule.createBond(atomCl, atomO3, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO4 = molecule.createAtom();
    atomO4.setCharge(-1);
    atomO4.setSymbol("O");
    molecule.createBond(atomCl, atomO4, SmilesBond.TYPE_SINGLE);
    checkMolecule("Cl(=O)(=O)(=O)[O-]", molecule);
  }
  public void testChapter4_06() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomO1 = molecule.createAtom();
    atomO1.setSymbol("O");
    molecule.createBond(atomC5, atomO1, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO2 = molecule.createAtom();
    atomO2.setSymbol("O");
    molecule.createBond(atomC5, atomO2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC6 = molecule.createAtom();
    atomC6.setSymbol("C");
    molecule.createBond(atomC4, atomC6, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC7 = molecule.createAtom();
    atomC7.setSymbol("C");
    molecule.createBond(atomC6, atomC7, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC8 = molecule.createAtom();
    atomC8.setSymbol("C");
    molecule.createBond(atomC7, atomC8, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomO2);
    addHydrogen(molecule, atomC6);
    addHydrogen(molecule, atomC6);
    addHydrogen(molecule, atomC7);
    addHydrogen(molecule, atomC7);
    addHydrogen(molecule, atomC8);
    addHydrogen(molecule, atomC8);
    addHydrogen(molecule, atomC8);
    checkMolecule("CCCC(C(=O)O)CCC", molecule);
  }
  
  
  public void testChapter5_01() {    
    testChapter1_11();
  }
  public void testChapter5_02() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomC1, null, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC6 = molecule.createAtom();
    atomC6.setSymbol("C");
    molecule.createBond(atomC5, atomC6, SmilesBond.TYPE_SINGLE);
    atomC1.getBond(0).setAtom2(atomC6);
    atomC6.addBond(atomC1.getBond(0));
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC6);
    addHydrogen(molecule, atomC6);
    checkMolecule("C1=CCCCC1", molecule);
  }
  public void testChapter5_03() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomC1, null, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC6 = molecule.createAtom();
    atomC6.setSymbol("C");
    molecule.createBond(atomC5, atomC6, SmilesBond.TYPE_SINGLE);
    atomC1.getBond(0).setAtom2(atomC6);
    atomC6.addBond(atomC1.getBond(0));
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC6);
    checkMolecule("C=1CCCCC1", molecule);
  }
  public void testChapter5_04() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomC1, null, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC6 = molecule.createAtom();
    atomC6.setSymbol("C");
    molecule.createBond(atomC5, atomC6, SmilesBond.TYPE_SINGLE);
    atomC1.getBond(0).setAtom2(atomC6);
    atomC6.addBond(atomC1.getBond(0));
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC6);
    checkMolecule("C1CCCCC=1", molecule);
  }
  public void testChapter5_05() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomC1, null, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC6 = molecule.createAtom();
    atomC6.setSymbol("C");
    molecule.createBond(atomC5, atomC6, SmilesBond.TYPE_SINGLE);
    atomC1.getBond(0).setAtom2(atomC6);
    atomC6.addBond(atomC1.getBond(0));
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC6);
    checkMolecule("C=1CCCCC=1", molecule);
  }
  public void testChapter5_06() {    
    
  }
  public void testChapter5_07() {    
    
  }
  public void testChapter5_08() {    
    
  }
  public void testChapter5_09() {    
    
  }
  
  
  public void testChapter6_01() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomNa = molecule.createAtom();
    atomNa.setCharge(1);
    atomNa.setSymbol("Na");
    SmilesAtom atomCl = molecule.createAtom();
    atomCl.setCharge(-1);
    atomCl.setSymbol("Cl");
    checkMolecule("[Na+].[Cl-]", molecule);
  }
  public void testChapter6_02() {    
    
  }
  public void testChapter6_03() {    
    
  }
  public void testChapter6_04() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    SmilesAtom atomO = molecule.createAtom();
    atomO.setSymbol("O");
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    molecule.createBond(atomO, atomC2, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomO);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    checkMolecule("C1.O2.C12", molecule);
  }
  public void testChapter6_05() {    
    testChapter1_07();
  }
  
  
  public void testChapter7_01() {    
    testChapter1_02();
  }
  public void testChapter7_02() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC = molecule.createAtom();
    atomC.setSymbol("C");
    checkMolecule("[C]", molecule);
  }
  public void testChapter7_03() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC = molecule.createAtom();
    atomC.setAtomicMass(12);
    atomC.setSymbol("C");
    checkMolecule("[12C]", molecule);
  }
  public void testChapter7_04() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC = molecule.createAtom();
    atomC.setAtomicMass(13);
    atomC.setSymbol("C");
    checkMolecule("[13C]", molecule);
  }
  public void testChapter7_05() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC = molecule.createAtom();
    atomC.setAtomicMass(13);
    atomC.setSymbol("C");
    addHydrogen(molecule, atomC);
    addHydrogen(molecule, atomC);
    addHydrogen(molecule, atomC);
    addHydrogen(molecule, atomC);
    checkMolecule("[13CH4]", molecule);
  }
  public void testChapter7_06() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomF1 = molecule.createAtom();
    atomF1.setSymbol("F");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomF1, atomC1, SmilesBond.TYPE_DIRECTIONAL_1);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomF2 = molecule.createAtom();
    atomF2.setSymbol("F");
    molecule.createBond(atomC2, atomF2, SmilesBond.TYPE_DIRECTIONAL_1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    checkMolecule("F/C=C/F", molecule);
  }
  public void testChapter7_07() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomF1 = molecule.createAtom();
    atomF1.setSymbol("F");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomF1, atomC1, SmilesBond.TYPE_DIRECTIONAL_2);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomF2 = molecule.createAtom();
    atomF2.setSymbol("F");
    molecule.createBond(atomC2, atomF2, SmilesBond.TYPE_DIRECTIONAL_2);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    checkMolecule("F\\C=C\\F", molecule);
  }
  public void testChapter7_08() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomF1 = molecule.createAtom();
    atomF1.setSymbol("F");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomF1, atomC1, SmilesBond.TYPE_DIRECTIONAL_1);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomF2 = molecule.createAtom();
    atomF2.setSymbol("F");
    molecule.createBond(atomC2, atomF2, SmilesBond.TYPE_DIRECTIONAL_2);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    checkMolecule("F/C=C\\F", molecule);
  }
  public void testChapter7_09() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomF1 = molecule.createAtom();
    atomF1.setSymbol("F");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomF1, atomC1, SmilesBond.TYPE_DIRECTIONAL_2);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomF2 = molecule.createAtom();
    atomF2.setSymbol("F");
    molecule.createBond(atomC2, atomF2, SmilesBond.TYPE_DIRECTIONAL_1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    checkMolecule("F\\C=C/F", molecule);
  }
  public void testChapter7_10() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomF = molecule.createAtom();
    atomF.setSymbol("F");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomF, atomC1, SmilesBond.TYPE_DIRECTIONAL_1);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_DIRECTIONAL_1);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_DIRECTIONAL_1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    checkMolecule("F/C=C/C=C/C", molecule);
  }
  public void testChapter7_11() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomF = molecule.createAtom();
    atomF.setSymbol("F");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomF, atomC1, SmilesBond.TYPE_DIRECTIONAL_1);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_DIRECTIONAL_1);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    checkMolecule("F/C=C/C=CC", molecule);
  }
  public void testChapter7_12() {    
    testChapter1_16();
  }
  public void testChapter7_13() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomN = molecule.createAtom();
    atomN.setSymbol("N");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setChiralClass("");
    atomC1.setChiralOrder(1);
    atomC1.setSymbol("C");
    molecule.createBond(atomN, atomC1, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC1, atomC3, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomO1 = molecule.createAtom();
    atomO1.setSymbol("O");
    molecule.createBond(atomC3, atomO1, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomO2 = molecule.createAtom();
    atomO2.setSymbol("O");
    molecule.createBond(atomC3, atomO2, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomN);
    addHydrogen(molecule, atomN);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomO2);
    checkMolecule("N[C@H](C)C(=O)O", molecule);
  }
  public void testChapter7_14() {    
    testChapter1_17();
  }
  public void testChapter7_15() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomC1, null, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setChiralClass("");
    atomC3.setChiralOrder(1);
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_SINGLE);
    molecule.createBond(atomC3, null, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC5 = molecule.createAtom();
    atomC5.setSymbol("C");
    molecule.createBond(atomC4, atomC5, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC6 = molecule.createAtom();
    atomC6.setSymbol("C");
    molecule.createBond(atomC5, atomC6, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC7 = molecule.createAtom();
    atomC7.setSymbol("C");
    molecule.createBond(atomC6, atomC7, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC8 = molecule.createAtom();
    atomC8.setChiralClass("");
    atomC8.setChiralOrder(1);
    atomC8.setSymbol("C");
    molecule.createBond(atomC7, atomC8, SmilesBond.TYPE_SINGLE);
    atomC3.getBond(1).setAtom2(atomC8);
    atomC8.addBond(atomC3.getBond(1));
    SmilesAtom atomC9 = molecule.createAtom();
    atomC9.setSymbol("C");
    molecule.createBond(atomC8, atomC9, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC0 = molecule.createAtom();
    atomC0.setSymbol("C");
    molecule.createBond(atomC9, atomC0, SmilesBond.TYPE_SINGLE);
    atomC1.getBond(0).setAtom2(atomC0);
    atomC0.addBond(atomC1.getBond(0));
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC1);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC2);
    addHydrogen(molecule, atomC3);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC5);
    addHydrogen(molecule, atomC6);
    addHydrogen(molecule, atomC6);
    addHydrogen(molecule, atomC7);
    addHydrogen(molecule, atomC7);
    addHydrogen(molecule, atomC8);
    addHydrogen(molecule, atomC9);
    addHydrogen(molecule, atomC9);
    addHydrogen(molecule, atomC0);
    addHydrogen(molecule, atomC0);
    checkMolecule("C1C[C@H]2CCCC[C@H]2CC1", molecule);
  }
  public void testChapter7_16() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO = molecule.createAtom();
    atomO.setSymbol("O");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomO, atomC1, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomCl = molecule.createAtom();
    atomCl.setSymbol("Cl");
    molecule.createBond(atomC1, atomCl, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setChiralClass("");
    atomC2.setChiralOrder(1);
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomF = molecule.createAtom();
    atomF.setSymbol("F");
    molecule.createBond(atomC3, atomF, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomO);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    checkMolecule("OC(Cl)=[C@]=C(C)F", molecule);
  }
  public void testChapter7_17() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO = molecule.createAtom();
    atomO.setSymbol("O");
    SmilesAtom atomC1 = molecule.createAtom();
    atomC1.setSymbol("C");
    molecule.createBond(atomO, atomC1, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomCl = molecule.createAtom();
    atomCl.setSymbol("Cl");
    molecule.createBond(atomC1, atomCl, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomC2 = molecule.createAtom();
    atomC2.setChiralClass("AL");
    atomC2.setChiralOrder(1);
    atomC2.setSymbol("C");
    molecule.createBond(atomC1, atomC2, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC3 = molecule.createAtom();
    atomC3.setSymbol("C");
    molecule.createBond(atomC2, atomC3, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomC4 = molecule.createAtom();
    atomC4.setSymbol("C");
    molecule.createBond(atomC3, atomC4, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomF = molecule.createAtom();
    atomF.setSymbol("F");
    molecule.createBond(atomC3, atomF, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomO);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    addHydrogen(molecule, atomC4);
    checkMolecule("OC(Cl)=[C@AL1]=C(C)F", molecule);
  }
  public void testChapter7_18() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomF = molecule.createAtom();
    atomF.setSymbol("F");
    SmilesAtom atomPo = molecule.createAtom();
    atomPo.setChiralClass("SP");
    atomPo.setChiralOrder(1);
    atomPo.setSymbol("Po");
    molecule.createBond(atomF, atomPo, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomCl = molecule.createAtom();
    atomCl.setSymbol("Cl");
    molecule.createBond(atomPo, atomCl, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomBr = molecule.createAtom();
    atomBr.setSymbol("Br");
    molecule.createBond(atomPo, atomBr, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomI = molecule.createAtom();
    atomI.setSymbol("I");
    molecule.createBond(atomPo, atomI, SmilesBond.TYPE_SINGLE);
    checkMolecule("F[Po@SP1](Cl)(Br)I", molecule);
  }
  public void testChapter7_19() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO = molecule.createAtom();
    atomO.setSymbol("O");
    SmilesAtom atomC = molecule.createAtom();
    atomC.setSymbol("C");
    molecule.createBond(atomO, atomC, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomAs = molecule.createAtom();
    atomAs.setChiralClass("");
    atomAs.setChiralOrder(1);
    atomAs.setSymbol("As");
    molecule.createBond(atomC, atomAs, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomF = molecule.createAtom();
    atomF.setSymbol("F");
    molecule.createBond(atomAs, atomF, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomCl = molecule.createAtom();
    atomCl.setSymbol("Cl");
    molecule.createBond(atomAs, atomCl, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomBr = molecule.createAtom();
    atomBr.setSymbol("Br");
    molecule.createBond(atomAs, atomBr, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomS = molecule.createAtom();
    atomS.setSymbol("S");
    molecule.createBond(atomAs, atomS, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC);
    addHydrogen(molecule, atomS);
    checkMolecule("O=C[As@](F)(Cl)(Br)S", molecule);
  }
  public void testChapter7_20() {    
    SmilesMolecule molecule = new SmilesMolecule();
    SmilesAtom atomO = molecule.createAtom();
    atomO.setSymbol("O");
    SmilesAtom atomC = molecule.createAtom();
    atomC.setSymbol("C");
    molecule.createBond(atomO, atomC, SmilesBond.TYPE_DOUBLE);
    SmilesAtom atomCo = molecule.createAtom();
    atomCo.setChiralClass("");
    atomCo.setChiralOrder(1);
    atomCo.setSymbol("Co");
    molecule.createBond(atomC, atomCo, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomF = molecule.createAtom();
    atomF.setSymbol("F");
    molecule.createBond(atomCo, atomF, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomCl = molecule.createAtom();
    atomCl.setSymbol("Cl");
    molecule.createBond(atomCo, atomCl, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomBr = molecule.createAtom();
    atomBr.setSymbol("Br");
    molecule.createBond(atomCo, atomBr, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomI = molecule.createAtom();
    atomI.setSymbol("I");
    molecule.createBond(atomCo, atomI, SmilesBond.TYPE_SINGLE);
    SmilesAtom atomS = molecule.createAtom();
    atomS.setSymbol("S");
    molecule.createBond(atomCo, atomS, SmilesBond.TYPE_SINGLE);
    addHydrogen(molecule, atomC);
    addHydrogen(molecule, atomS);
    checkMolecule("O=C[Co@](F)(Cl)(Br)(I)S", molecule);
  }
  
  
  private static void checkMolecule(String smiles, SmilesMolecule expected) {
    try {
      SmilesParser parser = new SmilesParser();
      SmilesMolecule molecule = parser.parseSmiles(smiles);
      assertTrue(areMoleculeEquals(molecule, expected));
    } catch (InvalidSmilesException e) {
      fail("InvalidSmilesException: " + e.getMessage());
    }
  }
  
  
  private void addHydrogen(SmilesMolecule molecule, SmilesAtom bonded) {
    SmilesAtom atomH = molecule.createAtom();
    atomH.setSymbol("H");
    if (bonded != null) {
      molecule.createBond(bonded, atomH, SmilesBond.TYPE_SINGLE);
    }
  }
  
  
  private static boolean areMoleculeEquals(
          SmilesMolecule molecule1,
          SmilesMolecule molecule2) {
    if ((molecule1 == null) || (molecule2 == null)) {
      Logger.error("Molecule null");
      return false;
    }
    if (molecule1.getAtomsCount() != molecule2.getAtomsCount()) {
      Logger.error(
          "Atoms count (" +
          molecule1.getAtomsCount() + "," +
          molecule2.getAtomsCount() + ")");
      return false;
    }
    for (int i = 0; i < molecule1.getAtomsCount(); i++) {
      SmilesAtom atom1 = molecule1.getAtom(i);
      SmilesAtom atom2 = molecule2.getAtom(i);
      if ((atom1 == null) || (atom2 == null)) {
        Logger.error("Atom " + i + " null");
        return false;
      }
      if (atom1.getAtomicMass() != atom2.getAtomicMass()) {
        Logger.error(
            "Atom " + i + " atomic mass (" +
            atom1.getAtomicMass() + "," +
            atom2.getAtomicMass() + ")");
        return false;
      }
      if (atom1.getBondsCount() != atom2.getBondsCount()) {
        Logger.error(
            "Atom " + i + " bonds count (" +
            atom1.getBondsCount() + "," +
            atom2.getBondsCount() + ")");
        return false;
      }
      for (int j = 0; j < atom1.getBondsCount(); j++) {
        SmilesBond bond1 = atom1.getBond(j);
        SmilesBond bond2 = atom2.getBond(j);
        if ((bond1 == null) || (bond2 == null)) {
          Logger.error(
              "Atom " + i + ", bond " + j + " null (" +
              bond1 + "," + bond2 + ")");
          return false;
        }
        if (bond1.getBondType() != bond2.getBondType()) {
          Logger.error(
              "Atom " + i + ", bond " + j + " bond type (" +
              bond1.getBondType() + "," +
              bond2.getBondType() + ")");
          return false;
        }
        if (bond1.getAtom1().getNumber() != bond2.getAtom1().getNumber()) {
          Logger.error(
              "Atom " + i + ", bond " + j + " atom1 number (" +
              bond1.getAtom1().getNumber() + "," +
              bond2.getAtom1().getNumber() + ")");
          return false;
        }
        if (bond1.getAtom2().getNumber() != bond2.getAtom2().getNumber()) {
          Logger.error(
              "Atom " + i + ", bond " + j + " atom2 number (" +
              bond1.getAtom2().getNumber() + "," +
              bond2.getAtom2().getNumber() + ")");
          return false;
        }
      }
      if (atom1.getCharge() != atom2.getCharge()) {
        Logger.error(
            "Atom " + i + " charge (" +
            atom1.getCharge() + "," +
            atom2.getCharge() + ")");
        return false;
      }
      if (atom1.getChiralClass() == null) {
        if (atom2.getChiralClass() != null) {
          Logger.error(
              "Atom " + i + " chiral class (" +
              atom1.getChiralClass() + "," +
              atom2.getChiralClass() + ")");
          return false;
        }
      } else if (!atom1.getChiralClass().equals(atom2.getChiralClass())) {
        Logger.error(
            "Atom " + i + " chiral class (" +
            atom1.getChiralClass() + "," +
            atom2.getChiralClass() + ")");
        return false;
      }
      if (atom1.getChiralOrder() != atom2.getChiralOrder()) {
        Logger.error(
            "Atom " + i + " chiral order (" +
            atom1.getChiralOrder() + "," +
            atom2.getChiralOrder() + ")");
        return false;
      }
      if (!atom1.getSymbol().equals(atom2.getSymbol())) {
        Logger.error(
            "Atom " + i + " symbol (" +
            atom1.getSymbol() + "," +
            atom2.getSymbol() + ")");
        return false;
      }
    }
    return true;
  }
  
}
