

package org.jmol.export;

import org.jmol.shapesurface.IsosurfaceRenderer;

public class IsosurfaceGenerator extends IsosurfaceRenderer {

  protected void renderExport() {
    ((Export3D)g3d).getExporter().renderIsosurface(imesh.vertices, imesh.colix, 
        imesh.isColorSolid ? null : imesh.vertexColixes,
        imesh.getVertexNormals(), imesh.polygonIndexes, bsFaces, imesh.vertexCount, 3, 
        imesh.isColorSolid ? imesh.polygonColixes : null, imesh.polygonCount);
  }
}
