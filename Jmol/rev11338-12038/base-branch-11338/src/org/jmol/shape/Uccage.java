
package org.jmol.shape;

public class Uccage extends FontLineShape {

  public String getShapeState() {
    if (modelSet.getCellInfos() == null)
      return "";
    return super.getShapeState();
  }

  public void initShape() {
    super.initShape();
    myType = "unitcell";
  }
}
