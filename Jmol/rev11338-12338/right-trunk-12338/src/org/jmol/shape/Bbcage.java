
package org.jmol.shape;

import java.util.BitSet;

import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.StateManager;

public class Bbcage extends FontLineShape {

  public void initShape() {
    super.initShape();
    font3d = g3d.getFont3D(JmolConstants.AXES_DEFAULT_FONTSIZE);
    myType = "boundBox";
  }

  boolean isVisible;
  int mad;
  
  public void setVisibilityFlags(BitSet bs) {
    isVisible = ((mad = viewer.getObjectMad(StateManager.OBJ_BOUNDBOX)) != 0);
    if (!isVisible)
      return;
    BitSet bboxModels = viewer.getBoundBoxModels();
    if (bboxModels == null)
      return;
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
      if (bboxModels.get(i))
        return;
    isVisible = false;
  }
  
}
