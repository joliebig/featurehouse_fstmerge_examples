

package org.jmol.shapebio;


public class TraceRenderer extends BioShapeRenderer {

  protected void renderBioShape(BioShape bioShape) {
    calcScreenControlPoints();
    for (int i = monomerCount; --i >= 0;)
      if (bsVisible.get(i))
        renderHermiteConic(i, false);
  }

}

