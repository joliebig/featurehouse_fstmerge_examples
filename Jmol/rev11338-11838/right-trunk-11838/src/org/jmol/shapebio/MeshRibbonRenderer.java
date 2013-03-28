

package org.jmol.shapebio;


public class MeshRibbonRenderer extends StrandsRenderer {

  protected void renderBioShape(BioShape bioShape) {
    if (!setStrandCount())
      return;
    float offset = ((strandCount >> 1) * strandSeparation) + baseOffset;
    render2Strand(false, offset, offset);
    render1();
  }

  protected void render2Strand(boolean doFill, float offsetTop, float offsetBottom) {
    calcScreenControlPoints();
    ribbonTopScreens = calcScreens(offsetTop);
    ribbonBottomScreens = calcScreens(-offsetBottom);
    for (int i = monomerCount; --i >= 0;)
      if (bsVisible.get(i))
        renderHermiteRibbon(doFill, i, false);
    viewer.freeTempScreens(ribbonTopScreens);
    viewer.freeTempScreens(ribbonBottomScreens);
  }
}
