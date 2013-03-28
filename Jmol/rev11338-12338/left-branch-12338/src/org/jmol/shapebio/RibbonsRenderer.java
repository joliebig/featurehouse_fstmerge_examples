

package org.jmol.shapebio;


public class RibbonsRenderer extends MeshRibbonRenderer {

  protected void renderBioShape(BioShape bioShape) {
    if (wingVectors == null)
      return;
    render2Strand(true, isNucleic ? 1f : 0.5f, isNucleic ? 0f : 0.5f);
  }
}
