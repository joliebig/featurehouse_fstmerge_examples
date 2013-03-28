

package org.jmol.shapebio;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;

public class BackboneRenderer extends BioShapeRenderer {

  protected void renderBioShape(BioShape bioShape) {
    for (int i = monomerCount - 1; --i >= 0;) {
      if (!bsVisible.get(i))
        continue;
      Atom atomA = modelSet.getAtomAt(leadAtomIndices[i]);
      Atom atomB = modelSet.getAtomAt(leadAtomIndices[i + 1]);
      if (atomA.getNBackbonesDisplayed() == 0 || atomB.getNBackbonesDisplayed() == 0
          || modelSet.isAtomHidden(atomB.getAtomIndex()))
        continue;
      int xA = atomA.screenX, yA = atomA.screenY, zA = atomA
          .screenZ;
      int xB = atomB.screenX, yB = atomB.screenY, zB = atomB
          .screenZ;
      short colixA = Graphics3D.getColixInherited(colixes[i], atomA.getColix());
      short colixB = Graphics3D.getColixInherited(colixes[i + 1], atomB.getColix());
      mad = mads[i];
      if (mad < 0) {
        g3d.drawLine(colixA, colixB, xA, yA, zA, xB, yB, zB);
      } else {
        int width = (isGenerator ? mad : viewer.scaleToScreen((zA + zB) / 2, mad));
        g3d.fillCylinder(colixA, colixB, Graphics3D.ENDCAPS_SPHERICAL, width,
            xA, yA, zA, xB, yB, zB);
      }
    }
  }  
}
