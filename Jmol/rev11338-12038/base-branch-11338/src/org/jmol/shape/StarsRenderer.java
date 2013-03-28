

package org.jmol.shape;

import org.jmol.modelset.Atom;

public class StarsRenderer extends ShapeRenderer {

  protected void render() {
    Stars stars = (Stars) shape;
    if (stars.mads == null)
      return;
    Atom[] atoms = modelSet.atoms;
    for (int i = modelSet.getAtomCount(); --i >= 0;) {
      Atom atom = atoms[i];
      if (!atom.isVisible(myVisibilityFlag))
        continue;
      colix = Shape.getColix(stars.colixes, i, atom);
      if (!g3d.setColix(colix))
        continue;
      render1(atom, stars.mads[i]);
    }
  }

  void render1(Atom atom, short mad) {
    int x = atom.screenX;
    int y = atom.screenY;
    int z = atom.screenZ;
    int d = viewer.scaleToScreen(z, mad);
    d -= (d & 1) ^ 1; 
    int r = d / 2;
    g3d.drawLine(x - r, y, z, x - r + d, y, z);
    g3d.drawLine(x, y - r, z, x, y - r + d, z);
    if (g3d.isCartesianExport())
      g3d.drawLine(x, y, z - r, x, y, z - r + d);
  }

}
