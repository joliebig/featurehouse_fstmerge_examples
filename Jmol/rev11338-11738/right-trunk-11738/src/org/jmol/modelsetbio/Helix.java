
package org.jmol.modelsetbio;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.util.Measure;
import org.jmol.viewer.JmolConstants;

public class Helix extends ProteinStructure {

  Helix(AlphaPolymer apolymer, int monomerIndex, int monomerCount, int id) {
    super(apolymer, JmolConstants.PROTEIN_STRUCTURE_HELIX,
          monomerIndex, monomerCount, id);
  }

  public void calcAxis() {
    if (axisA != null)
      return;
    Point3f[] points = new Point3f[monomerCount + 1];
    for (int i = 0; i <= monomerCount; i++) {
      points[i] = new Point3f();
      apolymer.getLeadMidPoint(monomerIndexFirst + i, points[i]);
    }
    axisA = new Point3f();
    axisUnitVector = new Vector3f();
    Measure.calcBestAxisThroughPoints(points, axisA, axisUnitVector, vectorProjection, 4);
    axisB = new Point3f(points[monomerCount]);
    Measure.projectOntoAxis(axisB, axisA, axisUnitVector, vectorProjection);
  }

  
  
}
