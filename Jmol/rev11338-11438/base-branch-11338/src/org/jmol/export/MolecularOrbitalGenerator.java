

package org.jmol.export;

import org.jmol.shapesurface.MolecularOrbitalRenderer;

public class MolecularOrbitalGenerator extends MolecularOrbitalRenderer {

  protected void renderExport() {
    ((Export3D)g3d).getExporter().renderIsosurface(imesh.vertices, imesh.colix, 
        imesh.isColorSolid ? null : imesh.vertexColixes,
        imesh.getVertexNormals(), imesh.polygonIndexes, bsFaces, 
        imesh.vertexCount, 3, null, imesh.polygonCount);
  }
}
