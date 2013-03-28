

package org.jmol.export;

import org.jmol.shapesurface.PmeshRenderer;

public class PmeshGenerator extends PmeshRenderer {

  protected void renderExport() {
    ((Export3D)g3d).getExporter().renderIsosurface(mesh.vertices, mesh.colix, null,
        mesh.getVertexNormals(), mesh.polygonIndexes, bsFaces, 
        mesh.vertexCount, 4, null, mesh.polygonCount);
  }
}
