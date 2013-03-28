

package org.jmol.shape;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.viewer.JmolConstants;

import java.util.BitSet;

public class HalosRenderer extends ShapeRenderer {

  boolean isAntialiased;
  protected void render() {
    Halos halos = (Halos) shape;
    boolean selectDisplayTrue = viewer.getSelectionHaloEnabled();
    boolean showHiddenSelections = (selectDisplayTrue && viewer
        .getShowHiddenSelectionHalos());
    if (halos.mads == null && !selectDisplayTrue)
      return;
    isAntialiased = g3d.isAntialiased();
    Atom[] atoms = modelSet.atoms;
    BitSet bsSelected = (selectDisplayTrue ? viewer.getSelectionSet() : null);
    for (int i = modelSet.getAtomCount(); --i >= 0;) {
      Atom atom = atoms[i];
      if ((atom.getShapeVisibilityFlags() & JmolConstants.ATOM_IN_FRAME) == 0)
        continue;
      short mad = (halos.mads == null ? 0 : halos.mads[i]);
      short colix = (halos.colixes == null || i >= halos.colixes.length ? Graphics3D.INHERIT_ALL
          : halos.colixes[i]);
      boolean isHidden = modelSet.isAtomHidden(i);
      if (selectDisplayTrue && bsSelected.get(i)) {
        if (isHidden && !showHiddenSelections)
          continue;
        if (mad == 0)
          mad = -1; 
        if (colix == Graphics3D.INHERIT_ALL)
          colix = halos.colixSelection;
        if (colix == Graphics3D.USE_PALETTE)
          colix = Graphics3D.GOLD;
        else if (colix == Graphics3D.INHERIT_ALL)
          colix = Graphics3D.getColixInherited(colix, atom.getColix());
      } else if (isHidden) {
        continue;
      } else {
        colix = Graphics3D.getColixInherited(colix, atom.getColix());
      }
      if (mad == 0)
        continue;
      render1(atom, mad, colix);
    }
  }

  void render1(Atom atom, short mad, short colix) {
    int z = atom.screenZ;
    int diameter = mad;
    if (diameter < 0) { 
      diameter = atom.screenDiameter;
      if (diameter == 0) {
        float ellipsemax = atom.getADPMinMax(true);
        if (ellipsemax > 0)
          diameter = viewer.scaleToScreen(z, (int) (ellipsemax * 2000));
        if (diameter == 0) {
          diameter = viewer.scaleToScreen(z, 500);
        }
      }
    } else {
      diameter = viewer.scaleToScreen(z, mad);
    }
    float d = diameter;
    if (isAntialiased)
      d /= 2;
    float haloDiameter = (d / 4);
    if (haloDiameter < 4)
      haloDiameter = 4;
    if (haloDiameter > 10)
      haloDiameter = 10;
    haloDiameter = d + 2 * haloDiameter;
    if (isAntialiased)
      haloDiameter *= 2;
    int haloWidth = (int) haloDiameter;
    if (haloWidth <= 0)
      return;
    g3d.fillScreenedCircle(colix, haloWidth, atom.screenX,
        atom.screenY, atom.screenZ);
  }  
}
