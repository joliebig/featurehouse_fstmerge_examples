
package org.jmol.shapespecial;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.shape.ShapeRenderer;

public class PolyhedraRenderer extends ShapeRenderer {

  private int drawEdges;

  protected void render() {
    Polyhedra polyhedra = (Polyhedra) shape;
    Polyhedra.Polyhedron[] polyhedrons = polyhedra.polyhedrons;
    drawEdges = polyhedra.drawEdges;
    short[] colixes = polyhedra.colixes;
    for (int i = polyhedra.polyhedronCount; --i >= 0;) {
      int iAtom = polyhedrons[i].centralAtom.getAtomIndex();
      short colix = (colixes == null || iAtom >= colixes.length ? 
          Graphics3D.INHERIT_ALL : polyhedra.colixes[iAtom]);
      render1(polyhedrons[i], colix);
    }
  }

  private void render1(Polyhedra.Polyhedron p, short colix) {
    if (p.visibilityFlags == 0)
      return;
    colix = Graphics3D.getColixInherited(colix, p.centralAtom.getColix());
    Atom[] vertices = p.vertices;
    byte[] planes;

    planes = p.planes;
    for (int i = vertices.length; --i >= 0;) {
      if (vertices[i].isSimple())
        vertices[i].transform(viewer);
    }

    boolean isAll = (drawEdges == Polyhedra.EDGES_ALL);
    boolean isFrontOnly = (drawEdges == Polyhedra.EDGES_FRONT);

    
    if (g3d.setColix(colix))
      for (int i = 0, j = 0; j < planes.length;)
        fillFace(p.normixes[i++], vertices[planes[j++]], vertices[planes[j++]],
            vertices[planes[j++]]);
    if (!g3d.setColix(Graphics3D.getColixTranslucent(colix, false, 0)))
      return;
    for (int i = 0, j = 0; j < planes.length;)
      drawFace(p.normixes[i++], vertices[planes[j++]],
          vertices[planes[j++]], vertices[planes[j++]], isAll, isFrontOnly);
  }

  private void drawFace(short normix, Atom atomA, Atom atomB, Atom atomC,
                boolean isAll, boolean isFrontOnly) {
    if (isAll || isFrontOnly && g3d.isDirectedTowardsCamera(normix))
      g3d.drawCylinderTriangle(atomA.screenX, atomA.screenY, atomA.screenZ,
          atomB.screenX, atomB.screenY, atomB.screenZ, atomC.screenX,
          atomC.screenY, atomC.screenZ, 3);
  }

  private void fillFace(short normix,
                  Atom atomA, Atom atomB, Atom atomC) {
    g3d.fillTriangle(normix,
                     atomA.screenX, atomA.screenY, atomA.screenZ,
                     atomB.screenX, atomB.screenY, atomB.screenZ,
                     atomC.screenX, atomC.screenY, atomC.screenZ);
  }
}
