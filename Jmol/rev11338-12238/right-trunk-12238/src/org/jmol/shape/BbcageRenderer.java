
package org.jmol.shape;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.BoxInfo;
import org.jmol.viewer.StateManager;

public class BbcageRenderer extends CageRenderer {

  protected void setEdges() {
    tickEdges = BoxInfo.bbcageTickEdges; 
  }
  
  protected void render() {
    Bbcage bbox = (Bbcage) shape;
    if (!bbox.isVisible 
        || exportType == Graphics3D.EXPORT_NOT && !g3d.checkTranslucent(false)
        || viewer.isJmolDataFrame())
      return;
    colix = viewer.getObjectColix(StateManager.OBJ_BOUNDBOX);
    render(bbox.mad, modelSet.getBboxVertices(), null, 0);
  }

}
