

package org.jmol.shapebio;

import javax.vecmath.Point3i;

public class StrandsRenderer extends BioShapeRenderer {

  protected int strandCount;
  protected float strandSeparation;
  protected float baseOffset;

  protected void renderBioShape(BioShape bioShape) {
    if (!setStrandCount())
      return;
    render1();
  }
  
  protected boolean setStrandCount() {
    if (wingVectors == null)
      return false;
    strandCount = viewer.getStrandCount(((Strands) shape).shapeID);
    strandSeparation = (strandCount <= 1) ? 0 : 1f / (strandCount - 1);
    baseOffset = ((strandCount & 1) == 0 ? strandSeparation / 2
        : strandSeparation);
    return true;
  }

  protected void render1() {
    Point3i[] screens;
    for (int i = strandCount >> 1; --i >= 0;) {
      float f = (i * strandSeparation) + baseOffset;
      screens = calcScreens(f);
      render1Strand(screens);
      viewer.freeTempScreens(screens);
      screens = calcScreens(-f);
      render1Strand(screens);
      viewer.freeTempScreens(screens);
    }
    if (strandCount % 2 == 1) {
      screens = calcScreens(0f);
      render1Strand(screens);
      viewer.freeTempScreens(screens);
    }
  }

  private void render1Strand(Point3i[] screens) {
    for (int i = bsVisible.nextSetBit(0); i >= 0; i = bsVisible.nextSetBit(i + 1))
      renderHermiteCylinder(screens, i);
  }
}
