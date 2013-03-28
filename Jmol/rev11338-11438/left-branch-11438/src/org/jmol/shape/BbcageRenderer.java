
package org.jmol.shape;

import org.jmol.viewer.StateManager;
import javax.vecmath.Point3f;

public class BbcageRenderer extends FontLineShapeRenderer {

  final Point3f[] screens = new Point3f[8];
  {
    for (int i = 8; --i >= 0; )
      screens[i] = new Point3f();
  }

  protected void render() {
    Bbcage bbox = (Bbcage) shape;
    if (!bbox.isVisible 
        || !isGenerator && !g3d.checkTranslucent(false)
        || viewer.isJmolDataFrame())
      return;
    colix = viewer.getObjectColix(StateManager.OBJ_BOUNDBOX);
    render(bbox.mad, modelSet.getBboxVertices(), screens, null, 0);
  }
  
}
