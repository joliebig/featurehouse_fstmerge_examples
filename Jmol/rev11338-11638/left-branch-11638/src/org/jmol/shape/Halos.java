

package org.jmol.shape;

import java.util.BitSet;

import org.jmol.g3d.Graphics3D;
import org.jmol.util.Logger;

public class Halos extends AtomShape {

  short colixSelection = Graphics3D.USE_PALETTE;

  void initState() {
    Logger.debug("init halos");
    translucentAllowed = false;
  }
  
 public void setProperty(String propertyName, Object value, BitSet bs) {
    if ("translucency" == propertyName)
      return;
    if ("argbSelection" == propertyName) {
      colixSelection = Graphics3D.getColix(((Integer)value).intValue());
      return;
    }
    super.setProperty(propertyName, value, bs);
  }

 public void setVisibilityFlags(BitSet bs) {
    BitSet bsSelected = (viewer.getSelectionHaloEnabled() ? viewer
        .getSelectionSet() : null);
    for (int i = atomCount; --i >= 0;) {
      boolean isVisible = bsSelected != null && bsSelected.get(i)
          || (mads != null && mads[i] != 0);
      atoms[i].setShapeVisibility(myVisibilityFlag, isVisible);
    }
  }
  
 public String getShapeState() {
    return super.getShapeState()
        + (colixSelection == Graphics3D.USE_PALETTE ? "" 
            : colixSelection == Graphics3D.INHERIT_ALL ? "  color SelectionHalos NONE;\n"
            : getColorCommand("selectionHalos", colixSelection) + ";\n");
  }
}
