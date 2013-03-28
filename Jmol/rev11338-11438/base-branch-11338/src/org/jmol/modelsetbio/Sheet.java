
package org.jmol.modelsetbio;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.viewer.JmolConstants;

public class Sheet extends ProteinStructure {

  AlphaPolymer alphaPolymer;

  Sheet(AlphaPolymer alphaPolymer, int monomerIndex, int monomerCount, int id) {
    super(alphaPolymer, JmolConstants.PROTEIN_STRUCTURE_SHEET, monomerIndex,
        monomerCount, id);
    this.alphaPolymer = alphaPolymer;
  }

  public void calcAxis() {
    if (axisA != null)
      return;
    if (monomerCount == 2) {
      axisA = alphaPolymer.getLeadPoint(monomerIndexFirst);
      axisB = alphaPolymer.getLeadPoint(monomerIndexFirst + 1);
    } else {
      axisA = new Point3f();
      alphaPolymer.getLeadMidPoint(monomerIndexFirst + 1, axisA);
      axisB = new Point3f();
      alphaPolymer.getLeadMidPoint(monomerIndexFirst + monomerCount - 1, axisB);
    }

    axisUnitVector = new Vector3f();
    axisUnitVector.sub(axisB, axisA);
    axisUnitVector.normalize();

    Point3f tempA = new Point3f();
    alphaPolymer.getLeadMidPoint(monomerIndexFirst, tempA);
    if (lowerNeighborIsHelixOrSheet()) {
      
    } else {
      Graphics3D
          .projectOntoAxis(tempA, axisA, axisUnitVector, vectorProjection);
    }
    Point3f tempB = new Point3f();
    alphaPolymer.getLeadMidPoint(monomerIndexFirst + monomerCount, tempB);
    if (upperNeighborIsHelixOrSheet()) {
      
    } else {
      Graphics3D
          .projectOntoAxis(tempB, axisA, axisUnitVector, vectorProjection);
    }
    axisA = tempA;
    axisB = tempB;
  }

  Vector3f widthUnitVector;
  Vector3f heightUnitVector;

  void calcSheetUnitVectors() {
    if (!(alphaPolymer instanceof AminoPolymer))
      return;
    if (widthUnitVector == null) {
      Vector3f vectorCO = new Vector3f();
      Vector3f vectorCOSum = new Vector3f();
      AminoMonomer amino = (AminoMonomer) alphaPolymer.monomers[monomerIndexFirst];
      vectorCOSum.sub(amino.getCarbonylOxygenAtomPoint(), amino
          .getCarbonylCarbonAtomPoint());
      for (int i = monomerCount; --i > 0;) {
        amino = (AminoMonomer) alphaPolymer.monomers[i];
        vectorCO.sub(amino.getCarbonylOxygenAtomPoint(), amino
            .getCarbonylCarbonAtomPoint());
        if (vectorCOSum.angle(vectorCO) < (float) Math.PI / 2)
          vectorCOSum.add(vectorCO);
        else
          vectorCOSum.sub(vectorCO);
      }
      heightUnitVector = vectorCO; 
      heightUnitVector.cross(axisUnitVector, vectorCOSum);
      heightUnitVector.normalize();
      widthUnitVector = vectorCOSum;
      widthUnitVector.cross(axisUnitVector, heightUnitVector);
    }
  }

  public Vector3f getWidthUnitVector() {
    if (widthUnitVector == null)
      calcSheetUnitVectors();
    return widthUnitVector;
  }

  public Vector3f getHeightUnitVector() {
    if (heightUnitVector == null)
      calcSheetUnitVectors();
    return heightUnitVector;
  }
}
