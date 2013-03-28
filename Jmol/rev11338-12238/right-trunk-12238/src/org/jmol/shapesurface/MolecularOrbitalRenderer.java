
package org.jmol.shapesurface;

import java.text.NumberFormat;

import org.jmol.g3d.Graphics3D;



public class MolecularOrbitalRenderer extends IsosurfaceRenderer {

  private NumberFormat nf;

  protected void render() {
    imageFontScaling = viewer.getImageFontScaling();
    MolecularOrbital mo = (MolecularOrbital) shape;
    int modelIndex = viewer.getCurrentModelIndex();
    for (int i = mo.meshCount; --i >= 0;)
      if (render1(imesh = (IsosurfaceMesh) mo.meshes[i]) && modelIndex >= 0)
        renderInfo();
  }

  private void renderInfo() {
    if (mesh.title == null || exportType != Graphics3D.EXPORT_NOT 
        || !g3d.setColix(viewer.getColixBackgroundContrast()))
      return;
    if (nf == null)
      nf = NumberFormat.getInstance();
    if (nf != null) {
      nf.setMaximumFractionDigits(3);
      nf.setMinimumFractionDigits(3);
    }
    byte fid = g3d.getFontFid("Monospaced", 14 * imageFontScaling);
    g3d.setFont(fid);
    int lineheight = (int) (15 * imageFontScaling);
    int x = (int) (5 * imageFontScaling);
    int y = lineheight;

    for (int i = 0; i < mesh.title.length; i++)
      if (mesh.title[i].length() > 0) {
        g3d.drawStringNoSlab(mesh.title[i], null, x, y, 0);
        y += lineheight;
      }
  }

  
}
