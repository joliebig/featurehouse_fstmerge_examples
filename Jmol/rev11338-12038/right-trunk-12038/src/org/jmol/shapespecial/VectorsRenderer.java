

package org.jmol.shapespecial;

import org.jmol.g3d.*;
import org.jmol.modelset.Atom;
import org.jmol.shape.Shape;
import org.jmol.shape.ShapeRenderer;

import javax.vecmath.*;

public class VectorsRenderer extends ShapeRenderer {

  protected void render() {
    Vectors vectors = (Vectors) shape;
    if (!vectors.isActive)
      return;
    short[] mads = vectors.mads;
    if (mads == null)
      return;
    Atom[] atoms = vectors.atoms;
    short[] colixes = vectors.colixes;
    for (int i = modelSet.getAtomCount(); --i >= 0;) {
      Atom atom = atoms[i];
      if (!atom.isVisible(myVisibilityFlag))
        continue;
      Vector3f vibrationVector = viewer.getVibrationVector(i);
      if (vibrationVector == null)
        continue;
      vectorScale = viewer.getVectorScale();
      if (transform(mads[i], atom, vibrationVector)
          && g3d.setColix(Shape.getColix(colixes, i, atom)))
        renderVector(atom);
    }
  }

  final Point3f pointVectorEnd = new Point3f();
  final Point3f pointArrowHead = new Point3f();
  final Point3i screenVectorEnd = new Point3i();
  final Point3i screenArrowHead = new Point3i();
  final Vector3f headOffsetVector = new Vector3f();
  int diameter;
  
  int headWidthPixels;
  float vectorScale;
  float headScale;
  boolean doShaft;
  final static float arrowHeadOffset = -0.2f;


  boolean transform(short mad, Atom atom, Vector3f vibrationVector) {
    float len = vibrationVector.length();
    
    if (Math.abs(len * vectorScale) < 0.01)
      return false;
    headScale = arrowHeadOffset;
    if (vectorScale < 0)
      headScale = -headScale;
    doShaft = (0.1 + Math.abs(headScale/len) < Math.abs(vectorScale));
    headOffsetVector.set(vibrationVector);
    headOffsetVector.scale(headScale / len);
    pointVectorEnd.scaleAdd(vectorScale, vibrationVector, atom);
    pointArrowHead.set(pointVectorEnd);
    pointArrowHead.add(headOffsetVector);
    screenArrowHead.set(viewer.transformPoint(pointArrowHead, vibrationVector));
    screenVectorEnd.set(viewer.transformPoint(pointVectorEnd, vibrationVector));
    diameter = (mad < 1 ? 1 : mad <= 20 ? mad : viewer.scaleToScreen(screenVectorEnd.z, mad));
    headWidthPixels = (int)(diameter * 2.0f);
    if (headWidthPixels < diameter + 2)
      headWidthPixels = diameter + 2;
    if (isExport)
      diameter = (mad < 1 ? 1 : mad); 
    return true;
  }
  
  void renderVector(Atom atom) {
    if (doShaft)
      g3d.fillCylinder(Graphics3D.ENDCAPS_OPEN, diameter, atom.screenX,
          atom.screenY, atom.screenZ, screenArrowHead.x, screenArrowHead.y,
          screenArrowHead.z);
    g3d.fillCone(Graphics3D.ENDCAPS_FLAT, headWidthPixels, screenArrowHead,
        screenVectorEnd);
  }
}
