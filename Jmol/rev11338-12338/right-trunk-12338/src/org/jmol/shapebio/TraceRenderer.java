

package org.jmol.shapebio;


public class TraceRenderer extends BioShapeRenderer {

  protected void renderBioShape(BioShape bioShape) {
    calcScreenControlPoints();
    for (int i = bsVisible.nextSetBit(0); i >= 0; i = bsVisible.nextSetBit(i + 1))
      renderHermiteConic(i, false);
  }

}

