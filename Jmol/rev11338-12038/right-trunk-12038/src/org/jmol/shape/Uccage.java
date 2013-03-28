
package org.jmol.shape;

import org.jmol.viewer.JmolConstants;

public class Uccage extends FontLineShape {

  public String getShapeState() {
    if (modelSet.getCellInfos() == null)
      return "";
    return super.getShapeState();
  }

  public void initShape() {
    super.initShape();
    font3d = g3d.getFont3D(JmolConstants.AXES_DEFAULT_FONTSIZE);
    myType = "unitcell";
  }
}
