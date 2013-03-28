
package org.jmol.shape;

import java.util.BitSet;

import org.jmol.viewer.StateManager;

public class Bbcage extends FontLineShape {

  
  final static byte edges[] = {
      0,1, 0,2, 0,4, 1,3, 
      1,5, 2,3, 2,6, 3,7, 
      4,5, 4,6, 5,7, 6,7
      };

  public void initShape() {
    super.initShape();
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
    for (int i = viewer.getModelCount(); --i >= 0; )
      if (bs.get(i) && bboxModels.get(i))
        return;
    isVisible = false;
  }
  
}
