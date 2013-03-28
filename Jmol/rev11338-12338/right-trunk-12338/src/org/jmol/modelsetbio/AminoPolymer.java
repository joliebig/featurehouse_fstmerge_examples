
package org.jmol.modelsetbio;

import org.jmol.modelset.Atom;
import org.jmol.modelset.Polymer;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.viewer.JmolConstants;

import java.util.BitSet;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class AminoPolymer extends AlphaPolymer {

  
  

  AminoPolymer(Monomer[] monomers) {
    super(monomers);
    type = TYPE_AMINO;
  }

  private boolean hasOAtoms;
  boolean hasWingPoints() { return hasOAtoms; }

  

  public void calcHydrogenBonds(Polymer polymer, BitSet bsA, BitSet bsB) {
    
    
    calcProteinMainchainHydrogenBonds((AminoPolymer) polymer, bsA, bsB);
    

  }

  

  private void calcProteinMainchainHydrogenBonds(AminoPolymer polymer, BitSet bsA, BitSet bsB) {
    Point3f pt = new Point3f();
    Vector3f vNH = new Vector3f();
    boolean intraChain = (polymer == null);
    if (intraChain)
      polymer = this;
    AminoMonomer source;
    for (int i = 1; i < polymer.monomerCount; ++i) { 
      
      if ((source = ((AminoMonomer)polymer.monomers[i])).getNHPoint(pt, vNH))
        bondAminoHydrogen(source, (intraChain ? i : -100), pt, bsA, bsB);
    }
  }

  private final static float maxHbondAlphaDistance = 9;
  private final static float maxHbondAlphaDistance2 =
    maxHbondAlphaDistance * maxHbondAlphaDistance;
  private final static float minimumHbondDistance2 = 0.5f; 
  private final static double QConst = -332 * 0.42 * 0.2 * 1000;  

  private void bondAminoHydrogen(AminoMonomer source, int indexDonor, Point3f hydrogenPoint,
                         BitSet bsA, BitSet bsB) {
    Point3f sourceAlphaPoint = source.getLeadAtomPoint();
    Point3f sourceNitrogenPoint = source.getNitrogenAtomPoint();
    int energyMin1 = 0;
    int energyMin2 = 0;
    int indexMin1 = -1;
    int indexMin2 = -1;
    for (int i = monomerCount; --i >= 0; ) {
      if ((i == indexDonor || (i+1) == indexDonor) || (i-1) == indexDonor)
        continue;
      AminoMonomer target = (AminoMonomer)monomers[i];
      Point3f targetAlphaPoint = target.getLeadAtomPoint();
      float dist2 = sourceAlphaPoint.distanceSquared(targetAlphaPoint);
      if (dist2 > maxHbondAlphaDistance2)
        continue;
      int energy = calcHbondEnergy(source.getNitrogenAtom(), sourceNitrogenPoint, hydrogenPoint, target);
      if (energy < energyMin1) {
        energyMin2 = energyMin1;
        indexMin2 = indexMin1;
        energyMin1 = energy;
        indexMin1 = i;
      } else if (energy < energyMin2) {
        energyMin2 = energy;
        indexMin2 = i;
      }
    }
    if (indexMin1 >= 0) {
      
      
      
      createResidueHydrogenBond(source, indexDonor, indexMin1, bsA, bsB, -energyMin1/1000f);
      if (indexMin2 >= 0) {
        createResidueHydrogenBond(source, indexDonor, indexMin2, bsA, bsB, -energyMin2/1000f);
        
        
      }
    }
  }

  
  private int calcHbondEnergy(Atom nitrogen, Point3f nitrogenPoint,
                      Point3f hydrogenPoint, AminoMonomer target) {
    Point3f targetOxygenPoint = target.getCarbonylOxygenAtomPoint();

    
    if (targetOxygenPoint == null)
      return 0;
    float distON2 = targetOxygenPoint.distanceSquared(nitrogenPoint);
    if (distON2 < minimumHbondDistance2)
      return 0;

    float distOH2 = targetOxygenPoint.distanceSquared(hydrogenPoint);
    if (distOH2 < minimumHbondDistance2)
      return 0;

    Point3f targetCarbonPoint = target.getCarbonylCarbonAtomPoint();
    float distCH2 = targetCarbonPoint.distanceSquared(hydrogenPoint);
    if (distCH2 < minimumHbondDistance2)
      return 0;

    float distCN2 = targetCarbonPoint.distanceSquared(nitrogenPoint);
    if (distCN2 < minimumHbondDistance2)
      return 0;
    
    
    
    double distOH = Math.sqrt(distOH2);
    double distCH = Math.sqrt(distCH2);
    double distCN = Math.sqrt(distCN2);
    double distON = Math.sqrt(distON2);

    int energy = (int) ((QConst / distOH - QConst / distCH + QConst / distCN - QConst
        / distON));

    boolean isHbond = (distCN2 > distCH2 && distOH <= 3.0f && energy <= -500);

    return (!isHbond ? 0 : energy < -9900 ? -9900 : energy);
  }

  private void createResidueHydrogenBond(AminoMonomer donor, int indexAminoGroup, int indexCarbonylGroup,
                                 BitSet bsA, BitSet bsB, float energy) {
    short order;
    int aminoBackboneHbondOffset = indexAminoGroup - indexCarbonylGroup;

    switch (aminoBackboneHbondOffset) {
    case 2:
      order = JmolConstants.BOND_H_PLUS_2;
      break;
    case 3:
      order = JmolConstants.BOND_H_PLUS_3;
      break;
    case 4:
      order = JmolConstants.BOND_H_PLUS_4;
      break;
    case 5:
      order = JmolConstants.BOND_H_PLUS_5;
      break;
    case -3:
      order = JmolConstants.BOND_H_MINUS_3;
      break;
    case -4:
      order = JmolConstants.BOND_H_MINUS_4;
      break;
    default:
      order = JmolConstants.BOND_H_CALC;
    }
    Atom nitrogen = donor.getNitrogenAtom();
    AminoMonomer recipient = (AminoMonomer)monomers[indexCarbonylGroup];
    Atom oxygen = recipient.getCarbonylOxygenAtom();
    model.addHydrogenBond(nitrogen, oxygen, order, bsA, bsB, energy);
  }

  

  

  public void calculateStructures() {
    
    
    char[] structureTags = new char[monomerCount];
    for (int i = 0; i < monomerCount - 1; ++i) {
      AminoMonomer leadingResidue = (AminoMonomer) monomers[i];
      AminoMonomer trailingResidue = (AminoMonomer) monomers[i + 1];
      float phi = trailingResidue.getPhi();
      float psi = leadingResidue.getPsi();
      if (isHelix(psi, phi)) {
        
        
        

        structureTags[i] = (phi < 0 && psi < 25 ? '4' : '3');
      } else if (isSheet(psi, phi)) {
        structureTags[i] = 's';
      } else if (isTurn(psi, phi)) {
        structureTags[i] = 't';
      } else {
        structureTags[i] = 'n';
      }

      if (Logger.debugging)
        Logger.debug((0+this.monomers[0].getChainID()) + " aminopolymer:" + i
            + " " + trailingResidue.getPhi() + "," + leadingResidue.getPsi() + " " + structureTags[i]);
    }

    
    for (int start = 0; start < monomerCount; ++start) {
      if (structureTags[start] == '4') {
        int end;
        for (end = start + 1; end < monomerCount && structureTags[end] == '4'; ++end) {
        }
        end--;
        if (end >= start + 3) {
          addSecondaryStructure(JmolConstants.PROTEIN_STRUCTURE_HELIX, null, 0, 0, start,
              end);
        }
        start = end;
      }
    }

    for (int start = 0; start < monomerCount; ++start) {
      if (structureTags[start] == '3') {
        int end;
        for (end = start + 1; end < monomerCount && structureTags[end] == '3'; ++end) {
        }
        end--;
        if (end >= start + 3) {
          addSecondaryStructure(JmolConstants.PROTEIN_STRUCTURE_HELIX, null, 0, 0, start,
              end);
        }
        start = end;
      }
    }

    
    for (int start = 0; start < monomerCount; ++start) {
      if (structureTags[start] == 's') {
        int end;
        for (end = start + 1; end < monomerCount && structureTags[end] == 's'; ++end) {
        }
        end--;
        if (end >= start + 2) {
          addSecondaryStructure(JmolConstants.PROTEIN_STRUCTURE_SHEET, null, 0, 0, start,
              end);
        }
        start = end;
      }
    }

    
    for (int start = 0; start < monomerCount; ++start) {
      if (structureTags[start] == 't') {
        int end;
        for (end = start + 1; end < monomerCount && structureTags[end] == 't'; ++end) {
        }
        end--;
        if (end >= start + 2) {
          addSecondaryStructure(JmolConstants.PROTEIN_STRUCTURE_TURN, null, 0, 0, start,
              end);
        }
        start = end;
      }
    }
  }

  protected void resetHydrogenPoints() {
    ProteinStructure ps;
    ProteinStructure psLast = null;
    for (int i = 0; i < monomerCount; i++) {
      if ((ps = getProteinStructure(i)) != null && ps != psLast)
        (psLast = ps).resetAxes();
      ((AminoMonomer) monomers[i]).resetHydrogenPoint();
    }
  }

  private boolean checkWingAtoms() {
    for (int i = 0; i < monomerCount; ++i)
      if (!((AminoMonomer) monomers[i]).hasOAtom())
        return false;
    return true;
  }
  
  public void freeze() {
    hasOAtoms = checkWingAtoms();
    calcPhiPsiAngles();
  }
  
  protected boolean calcPhiPsiAngles() {
    for (int i = 0; i < monomerCount - 1; ++i)
      calcPhiPsiAngles((AminoMonomer) monomers[i], (AminoMonomer) monomers[i + 1]);
    return true;
  }
  
  private void calcPhiPsiAngles(AminoMonomer residue1,
                        AminoMonomer residue2) {
    
    
    Point3f nitrogen1 = residue1.getNitrogenAtomPoint();
    Point3f alphacarbon1 = residue1.getLeadAtomPoint();
    Point3f carbon1 = residue1.getCarbonylCarbonAtomPoint();
    Point3f nitrogen2 = residue2.getNitrogenAtomPoint();
    Point3f alphacarbon2 = residue2.getLeadAtomPoint();
    Point3f carbon2 = residue2.getCarbonylCarbonAtomPoint();

    residue2.setPhi(Measure.computeTorsion(carbon1, nitrogen2,
                                            alphacarbon2, carbon2, true));
    residue1.setPsi(Measure.computeTorsion(nitrogen1, alphacarbon1,
      carbon1, nitrogen2, true));
    
    
    residue1.setOmega(Measure.computeTorsion(alphacarbon1,
	        carbon1, nitrogen2, alphacarbon2, true));
  }
  
  protected float calculateRamachandranHelixAngle(int m, char qtype) {
    float psiLast = (m == 0 ? Float.NaN : monomers[m - 1].getPsi());
    float psi = monomers[m].getPsi();
    float phi = monomers[m].getPhi();
    float phiNext = (m == monomerCount - 1 ? Float.NaN
        : monomers[m + 1].getPhi());
    float psiNext = (m == monomerCount - 1 ? Float.NaN
        : monomers[m + 1].getPsi());
    switch (qtype) {
    default:
    case 'p':
    case 'r':
    case 'P':
       
      float dPhi = (float) ((phiNext - phi) / 2 * Math.PI / 180);
      float dPsi = (float) ((psiNext - psi) / 2 * Math.PI / 180);
      return (float) (180 / Math.PI * 2 * Math.acos(Math.cos(dPsi) * Math.cos(dPhi) - Math.cos(70*Math.PI/180)* Math.sin(dPsi) * Math.sin(dPhi)));
    case 'c':
    case 'C':
      
      return  (psi - psiLast + phiNext - phi);
    }
  }
  
  
  private static boolean isHelix(float psi, float phi) {
    return (phi >= -160) && (phi <= 0) && (psi >= -100) && (psi <= 45);
  }

  private static boolean isSheet(float psi, float phi) {
    return
      ( (phi >= -180) && (phi <= -10) && (psi >= 70) && (psi <= 180) ) || 
      ( (phi >= -180) && (phi <= -45) && (psi >= -180) && (psi <= -130) ) ||
      ( (phi >= 140) && (phi <= 180) && (psi >= 90) && (psi <= 180) );
  }

  private static boolean isTurn(float psi, float phi) {
    return (phi >= 30) && (phi <= 90) && (psi >= -15) && (psi <= 95);
  }


  

}
